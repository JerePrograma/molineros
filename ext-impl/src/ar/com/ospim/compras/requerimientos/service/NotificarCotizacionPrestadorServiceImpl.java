package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NotificarCotizacionPrestadorServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(NotificarCotizacionPrestadorServiceImpl.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final String ESTADO_PROCESANDO = "PROCESANDO";
    private static final String ESTADO_ENVIADO = "ENVIADO";
    private static final String ESTADO_ERROR = "ERROR";
    private static final String ESTADO_EMAIL_INVALIDO = "EMAIL_INVALIDO";

    private static final String SQL_LISTAR_CANDIDATOS =
            "SELECT id_prestador, descripcion, cuit, email, " +
                    "id_tipo_prestador, tipo_prestador " +
                    "FROM compras.listar_prestadores_cotizacion_requerimiento(?)";

    private static final String SQL_REGISTRAR_COTIZACION =
            "{ ? = call compras.registrar_cotizacion_prestador(?,?,?) }";

    private static final String SQL_MARCAR_PROCESANDO =
            "UPDATE compras.requerimiento_cotizacion_prestador " +
                    "SET estado_envio = ?, " +
                    "    intentos = intentos + 1, " +
                    "    ultimo_intento_fecha = now(), " +
                    "    ultimo_error = NULL " +
                    "WHERE id_requerimiento = ? " +
                    "  AND id_prestador = ? " +
                    "  AND estado_envio = 'PENDIENTE'";

    private static final String SQL_MARCAR_RESULTADO =
            "UPDATE compras.requerimiento_cotizacion_prestador " +
                    "SET estado_envio = ?, " +
                    "    enviado_fecha = CASE WHEN ? = 'ENVIADO' THEN now() ELSE NULL END, " +
                    "    ultimo_error = ? " +
                    "WHERE id_requerimiento = ? " +
                    "  AND id_prestador = ? " +
                    "  AND estado_envio = 'PROCESANDO'";

    private final CotizacionPrestadorMailHelper mailHelper =
            new CotizacionPrestadorMailHelper();

    public NotificacionCotizacionResultado notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        validarParametros(idRequerimientoCompra);

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        validarRequerimiento(requerimiento);

        List<PrestadorCotizacion> candidatos =
                listarPrestadoresCandidatos(idRequerimientoCompra);

        NotificacionCotizacionResultado resultado =
                new NotificacionCotizacionResultado();

        resultado.setTotalCandidatos(candidatos.size());

        if (candidatos.isEmpty()) {
            if (_log.isInfoEnabled()) {
                _log.info(
                        "No hay prestadores candidatos para cotizar requerimiento "
                                + idRequerimientoCompra
                );
            }

            return resultado;
        }

        for (int i = 0; i < candidatos.size(); i++) {
            PrestadorCotizacion prestador = candidatos.get(i);

            procesarPrestador(
                    requerimiento,
                    prestador,
                    usuario,
                    companyId,
                    resultado
            );
        }

        return resultado;
    }

    private void procesarPrestador(RequerimientoCompra requerimiento,
                                   PrestadorCotizacion prestador,
                                   String usuario,
                                   long companyId,
                                   NotificacionCotizacionResultado resultado) {

        if (prestador == null) {
            resultado.incrementarErrores();
            return;
        }

        int idRequerimiento = requerimiento.getIdRequerimientoCompra();
        int idPrestador = prestador.getIdPrestador();
        String email = trimToNull(prestador.getEmail());

        boolean registrado;

        try {
            registrado = registrarCotizacionPrestador(
                    idRequerimiento,
                    idPrestador,
                    usuario
            );
        } catch (Exception e) {
            _log.error(
                    "No se pudo reservar el envio de cotizacion. idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        if (!registrado) {
            resultado.incrementarOmitidos();
            return;
        }

        try {
            if (!marcarProcesando(idRequerimiento, idPrestador)) {
                _log.warn(
                        "No se pudo tomar la cotizacion pendiente para procesar. idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                resultado.incrementarOmitidos();
                return;
            }
        } catch (Exception e) {
            _log.error(
                    "No se pudo marcar el envio de cotizacion como procesando. idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        if (!esEmailValido(email)) {
            String errorEmail = "Email vacio o con formato invalido: "
                    + (email != null ? email : "<vacio>");

            _log.warn(
                    "Prestador con email invalido para cotizacion. idPrestador="
                            + idPrestador
                            + ", email="
                            + email
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            finalizarConControl(
                    idRequerimiento,
                    idPrestador,
                    ESTADO_EMAIL_INVALIDO,
                    errorEmail
            );

            resultado.incrementarErrores();
            return;
        }

        try {
            String asunto = construirAsunto(requerimiento);
            String cuerpo = construirCuerpo(requerimiento, prestador);

            mailHelper.enviar(
                    companyId,
                    email,
                    asunto,
                    cuerpo
            );

        } catch (Exception e) {
            String detalleError = construirDetalleError(e);

            finalizarConControl(
                    idRequerimiento,
                    idPrestador,
                    ESTADO_ERROR,
                    detalleError
            );

            _log.error(
                    "Fallo el envio de cotizacion. idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        try {
            if (!marcarResultado(
                    idRequerimiento,
                    idPrestador,
                    ESTADO_ENVIADO,
                    null
            )) {
                _log.error(
                        "El correo fue entregado al servicio de mail, pero no se pudo "
                                + "persistir el estado ENVIADO. idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                resultado.incrementarErrores();
                return;
            }

            resultado.incrementarEnviados();

        } catch (Exception e) {
            /*
             * No marcar ERROR: el mensaje ya fue aceptado por el servicio de mail.
             * Se conserva PROCESANDO para que la inconsistencia sea visible y no se
             * afirme falsamente que el envio fallo.
             */
            _log.error(
                    "El correo fue entregado al servicio de mail, pero fallo la persistencia "
                            + "del estado ENVIADO. idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
        }
    }

    private List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<PrestadorCotizacion> candidatos =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_CANDIDATOS);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();

            while (rs.next()) {
                candidatos.add(mapPrestadorCotizacion(rs));
            }

            return candidatos;

        } catch (Exception e) {
            _log.error("Error listando prestadores candidatos a cotizacion", e);
            throw e;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private boolean registrarCotizacionPrestador(int idRequerimientoCompra,
                                                 int idPrestador,
                                                 String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_REGISTRAR_COTIZACION);

            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setInt(3, idPrestador);
            stmt.setString(4, emptyToNull(usuario));

            stmt.execute();

            return stmt.getBoolean(1);

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private boolean marcarProcesando(int idRequerimiento,
                                     int idPrestador) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_MARCAR_PROCESANDO);

            stmt.setString(1, ESTADO_PROCESANDO);
            stmt.setInt(2, idRequerimiento);
            stmt.setInt(3, idPrestador);

            return stmt.executeUpdate() == 1;

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private boolean marcarResultado(int idRequerimiento,
                                    int idPrestador,
                                    String estado,
                                    String error) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_MARCAR_RESULTADO);

            stmt.setString(1, estado);
            stmt.setString(2, estado);
            stmt.setString(3, truncar(error, 4000));
            stmt.setInt(4, idRequerimiento);
            stmt.setInt(5, idPrestador);

            return stmt.executeUpdate() == 1;

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void finalizarConControl(int idRequerimiento,
                                     int idPrestador,
                                     String estado,
                                     String error) {

        try {
            if (!marcarResultado(
                    idRequerimiento,
                    idPrestador,
                    estado,
                    error
            )) {
                _log.error(
                        "No se pudo persistir el estado final de cotizacion. estado="
                                + estado
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );
            }
        } catch (Exception persistenciaError) {
            _log.error(
                    "Error persistiendo el estado final de cotizacion. estado="
                            + estado
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    persistenciaError
            );
        }
    }

    private String construirAsunto(RequerimientoCompra requerimiento) {
        return "Solicitud de cotizacion - Requerimiento #"
                + requerimiento.getIdRequerimientoCompra();
    }

    private String construirCuerpo(RequerimientoCompra requerimiento,
                                   PrestadorCotizacion prestador) {

        StringBuilder sb = new StringBuilder();

        sb.append("Estimado prestador");

        if (!WebKeysCompras.isEmpty(prestador.getDescripcion())) {
            sb.append(" ").append(prestador.getDescripcionVisible());
        }

        sb.append(",\n\n");

        sb.append("OSPIM solicita cotizacion para el siguiente requerimiento de compra.\n\n");

        sb.append("Requerimiento: #")
                .append(requerimiento.getIdRequerimientoCompra())
                .append("\n");

        sb.append("Sector: ")
                .append(requerimiento.getSectorDescripcionVisible())
                .append("\n");

        if (!WebKeysCompras.isEmpty(requerimiento.getAltaFechaAsString())) {
            sb.append("Fecha: ")
                    .append(requerimiento.getAltaFechaAsString())
                    .append("\n");
        }

        if (!WebKeysCompras.isEmpty(requerimiento.getAltaUsr())) {
            sb.append("Usuario solicitante: ")
                    .append(requerimiento.getAltaUsr())
                    .append("\n");
        }

        if (!WebKeysCompras.isEmpty(requerimiento.getObservaciones())) {
            sb.append("\nDetalle / observaciones:\n")
                    .append(requerimiento.getObservacionesVisible())
                    .append("\n");
        }

        appendDetalles(sb, requerimiento);

        sb.append("\nPor favor responder este correo informando disponibilidad, plazo e importe de cotizacion.\n\n");
        sb.append("Este mensaje fue generado automaticamente por el sistema de Compras de OSPIM.\n");

        return sb.toString();
    }

    private void appendDetalles(StringBuilder sb,
                                RequerimientoCompra requerimiento) {

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        sb.append("\nItems:\n");

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle d = detalles.get(i);

            sb.append("- ");

            if (!WebKeysCompras.isEmpty(d.getArticulo())) {
                sb.append(d.getArticuloVisible());
            } else {
                sb.append("Item sin descripcion");
            }

            sb.append(" | Cantidad: ")
                    .append(d.getCantidadString());

            if (!WebKeysCompras.isEmpty(d.getObservaciones())) {
                sb.append(" | Obs: ")
                        .append(d.getObservacionesVisible());
            }

            sb.append("\n");
        }
    }

    private PrestadorCotizacion mapPrestadorCotizacion(ResultSet rs)
            throws Exception {

        PrestadorCotizacion p = new PrestadorCotizacion();

        p.setIdPrestador(rs.getInt("id_prestador"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCuit(rs.getString("cuit"));
        p.setEmail(rs.getString("email"));
        p.setIdTipoPrestador(rs.getInt("id_tipo_prestador"));
        p.setTipoPrestador(rs.getString("tipo_prestador"));

        return p;
    }

    private void validarParametros(int idRequerimientoCompra) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }
    }

    private void validarRequerimiento(RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {
            throw new Exception("No se encontro el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {

            throw new Exception(
                    "El requerimiento no tiene sector informado."
            );
        }
    }

    private boolean esEmailValido(String email) {
        return email != null
                && email.trim().length() > 0
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private String construirDetalleError(Exception e) {
        if (e == null) {
            return "Error no informado.";
        }

        String mensaje = e.getMessage();

        if (mensaje == null || mensaje.trim().length() == 0) {
            mensaje = e.getClass().getName();
        } else {
            mensaje = e.getClass().getName() + ": " + mensaje.trim();
        }

        return truncar(mensaje, 4000);
    }

    private String emptyToNull(String value) {
        return trimToNull(value);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }

    private String truncar(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}

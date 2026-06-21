package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NotificarCotizacionPrestadorServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    NotificarCotizacionPrestadorServiceImpl.class
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
            );

    private static final Pattern SENSITIVE_DETAIL_PATTERN =
            Pattern.compile(
                    "(?i)(password|passwd|pwd|token|secret|api[_-]?key|authorization)\\s*[:=]\\s*\\S+"
            );

    private static final String SQL_LISTAR_CANDIDATOS =
            "SELECT id_prestador, descripcion, cuit, email, " +
                    "id_tipo_prestador, tipo_prestador " +
                    "FROM compras." +
                    "listar_prestadores_cotizacion_requerimiento(?)";

    private static final String SQL_REGISTRAR_COTIZACION =
            "SELECT compras.registrar_cotizacion_prestador(?, ?, ?)";

    private static final String SQL_EMAIL_RESERVADO =
            "SELECT email_destino " +
                    "FROM compras.requerimiento_cotizacion_prestador " +
                    "WHERE id_requerimiento = ? " +
                    "  AND id_prestador = ? " +
                    "  AND estado_envio = 'PROCESANDO'";

    private static final String SQL_FINALIZAR_COTIZACION =
            "SELECT compras.finalizar_cotizacion_prestador(?, ?, ?, ?)";

    private final CotizacionPrestadorMailHelper mailHelper =
            new CotizacionPrestadorMailHelper();

    public NotificacionCotizacionResultado notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        validarParametros(
                idRequerimientoCompra,
                companyId
        );

        RequerimientoCompra requerimiento =
                getRequerimientoCompra(
                        idRequerimientoCompra
                );

        validarRequerimiento(requerimiento);

        List<PrestadorCotizacion> candidatos =
                listarPrestadoresCandidatos(
                        idRequerimientoCompra
                );

        NotificacionCotizacionResultado resultado =
                new NotificacionCotizacionResultado();

        resultado.setTotalCandidatos(
                candidatos.size()
        );

        if (candidatos.isEmpty()) {
            if (_log.isInfoEnabled()) {
                _log.info(
                        "No hay prestadores pendientes de notificación. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                );
            }

            return resultado;
        }

        for (int i = 0; i < candidatos.size(); i++) {
            procesarPrestador(
                    requerimiento,
                    candidatos.get(i),
                    usuario,
                    companyId,
                    resultado
            );
        }

        return resultado;
    }

    private void procesarPrestador(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador,
            String usuario,
            long companyId,
            NotificacionCotizacionResultado resultado) {

        if (prestador == null) {
            resultado.incrementarErrores();
            return;
        }

        int idRequerimiento =
                requerimiento.getIdRequerimientoCompra();

        int idPrestador =
                prestador.getIdPrestador();

        boolean reservado;

        try {
            reservado =
                    registrarCotizacionPrestador(
                            idRequerimiento,
                            idPrestador,
                            usuario
                    );

        } catch (Exception e) {
            _log.error(
                    "No se pudo reservar el envío de cotización. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        if (!reservado) {
            if (_log.isDebugEnabled()) {
                _log.debug(
                        "No se obtuvo reserva para envío de cotización. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );
            }

            resultado.incrementarOmitidos();
            return;
        }

        String email;

        try {
            email =
                    normalizarEmail(
                            leerEmailReservado(
                                    idRequerimiento,
                                    idPrestador
                            )
                    );

        } catch (Exception e) {
            finalizarConControl(
                    idRequerimiento,
                    idPrestador,
                    WebKeysCompras.ENVIO_ERROR,
                    construirDetalleError(e)
            );

            _log.error(
                    "No se pudo leer el email reservado de cotización. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        if (!esEmailValido(email)) {

            String errorEmail =
                    "Email destino inválido.";

            _log.warn(
                    "Prestador con email inválido. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            finalizarConControl(
                    idRequerimiento,
                    idPrestador,
                    WebKeysCompras.ENVIO_EMAIL_INVALIDO,
                    errorEmail
            );

            resultado.incrementarEmailsInvalidos();
            return;
        }

        try {
            String asunto =
                    construirAsunto(requerimiento);

            String cuerpo =
                    construirCuerpo(
                            requerimiento,
                            prestador
                    );

            enviarMail(
                    companyId,
                    email,
                    asunto,
                    cuerpo
            );

        } catch (Exception e) {
            String detalleError =
                    construirDetalleError(e);

            finalizarConControl(
                    idRequerimiento,
                    idPrestador,
                    WebKeysCompras.ENVIO_ERROR,
                    detalleError
            );

            _log.error(
                    "Falló el envío de cotización. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
            return;
        }

        try {
            if (!finalizarCotizacionPrestador(
                    idRequerimiento,
                    idPrestador,
                    WebKeysCompras.ENVIO_ENVIADO,
                    null
            )) {
                _log.error(
                        "El correo fue aceptado por el servicio de mail, "
                                + "pero no se pudo persistir ENVIADO. "
                                + "Se conserva PROCESANDO. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                resultado.incrementarErrores();
                return;
            }

            resultado.incrementarEnviados();

        } catch (Exception e) {
            _log.error(
                    "El correo fue aceptado por el servicio de mail, "
                            + "pero falló la persistencia de ENVIADO. "
                            + "Se conserva PROCESANDO y no se marca ERROR. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            resultado.incrementarErrores();
        }
    }

    protected RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(
                        idRequerimientoCompra
                );
    }

    protected List<PrestadorCotizacion>
    listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<PrestadorCotizacion> candidatos =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = obtenerConexion();

            stmt = con.prepareStatement(
                    SQL_LISTAR_CANDIDATOS
            );

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            rs = stmt.executeQuery();

            while (rs.next()) {
                candidatos.add(
                        mapPrestadorCotizacion(rs)
                );
            }

            return candidatos;

        } catch (Exception e) {
            _log.error(
                    "Error listando prestadores candidatos. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            throw e;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected boolean registrarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = obtenerConexion();

            stmt = con.prepareStatement(
                    SQL_REGISTRAR_COTIZACION
            );

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            stmt.setString(
                    3,
                    normalizarUsuario(usuario)
            );

            rs = stmt.executeQuery();

            return rs.next()
                    && rs.getBoolean(1);

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected String leerEmailReservado(
            int idRequerimiento,
            int idPrestador) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = obtenerConexion();

            stmt = con.prepareStatement(
                    SQL_EMAIL_RESERVADO
            );

            stmt.setInt(
                    1,
                    idRequerimiento
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            return rs.getString("email_destino");

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected boolean finalizarCotizacionPrestador(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = obtenerConexion();

            stmt = con.prepareStatement(
                    SQL_FINALIZAR_COTIZACION
            );

            stmt.setInt(
                    1,
                    idRequerimiento
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            stmt.setString(
                    3,
                    estado
            );

            stmt.setString(
                    4,
                    truncar(error, 4000)
            );

            rs = stmt.executeQuery();

            return rs.next()
                    && rs.getBoolean(1);

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected void enviarMail(
            long companyId,
            String email,
            String asunto,
            String cuerpo) throws Exception {

        mailHelper.enviar(
                companyId,
                email,
                asunto,
                cuerpo
        );
    }

    private void finalizarConControl(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error) {

        try {
            if (!finalizarCotizacionPrestador(
                    idRequerimiento,
                    idPrestador,
                    estado,
                    error
            )) {
                _log.error(
                        "No se pudo persistir el estado final "
                                + "de cotización. estado="
                                + estado
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );
            }

        } catch (Exception persistenciaError) {
            _log.error(
                    "Error persistiendo el estado final "
                            + "de cotización. estado="
                            + estado
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    persistenciaError
            );
        }
    }

    private String construirAsunto(
            RequerimientoCompra requerimiento) {

        return "Solicitud de cotización - Requerimiento #"
                + requerimiento
                .getIdRequerimientoCompra();
    }

    private String construirCuerpo(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador) {

        StringBuilder sb = new StringBuilder();

        sb.append("Estimado prestador");

        if (!WebKeysCompras.isEmpty(
                prestador.getDescripcion()
        )) {
            sb.append(" ");
            sb.append(
                    prestador.getDescripcionVisible()
            );
        }

        sb.append(",\n\n");

        sb.append(
                "OSPIM solicita cotización para el "
                        + "siguiente requerimiento de compra."
        );

        sb.append("\n\n");

        sb.append("Requerimiento: #");
        sb.append(
                requerimiento
                        .getIdRequerimientoCompra()
        );
        sb.append("\n");

        sb.append("Sector: ");
        sb.append(
                requerimiento
                        .getSectorDescripcionVisible()
        );
        sb.append("\n");

        if (!WebKeysCompras.isEmpty(
                requerimiento.getAltaFechaAsString()
        )) {
            sb.append("Fecha: ");
            sb.append(
                    requerimiento
                            .getAltaFechaAsString()
            );
            sb.append("\n");
        }

        if (!WebKeysCompras.isEmpty(
                requerimiento.getAltaUsr()
        )) {
            sb.append("Usuario solicitante: ");
            sb.append(
                    requerimiento.getAltaUsr()
            );
            sb.append("\n");
        }

        if (!WebKeysCompras.isEmpty(
                requerimiento.getObservaciones()
        )) {
            sb.append(
                    "\nDetalle / observaciones:\n"
            );

            sb.append(
                    requerimiento
                            .getObservacionesVisible()
            );

            sb.append("\n");
        }

        appendDetalles(
                sb,
                requerimiento
        );

        sb.append(
                "\nPor favor responder este correo "
                        + "informando disponibilidad, plazo "
                        + "e importe de cotización.\n\n"
        );

        sb.append(
                "Este mensaje fue generado automáticamente "
                        + "por el sistema de Compras de OSPIM.\n"
        );

        return sb.toString();
    }

    private void appendDetalles(
            StringBuilder sb,
            RequerimientoCompra requerimiento) {

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        sb.append("\nItems:\n");

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            sb.append("- ");

            if (!WebKeysCompras.isEmpty(
                    detalle.getArticulo()
            )) {
                sb.append(
                        detalle.getArticuloVisible()
                );
            } else {
                sb.append("Ítem sin descripción");
            }

            sb.append(" | Cantidad: ");
            sb.append(
                    detalle.getCantidadString()
            );

            if (!WebKeysCompras.isEmpty(
                    detalle.getObservaciones()
            )) {
                sb.append(" | Obs: ");
                sb.append(
                        detalle.getObservacionesVisible()
                );
            }

            sb.append("\n");
        }
    }

    private PrestadorCotizacion mapPrestadorCotizacion(
            ResultSet rs) throws Exception {

        PrestadorCotizacion prestador =
                new PrestadorCotizacion();

        prestador.setIdPrestador(
                rs.getInt("id_prestador")
        );

        prestador.setDescripcion(
                rs.getString("descripcion")
        );

        prestador.setCuit(
                rs.getString("cuit")
        );

        prestador.setEmail(
                rs.getString("email")
        );

        prestador.setIdTipoPrestador(
                rs.getInt("id_tipo_prestador")
        );

        prestador.setTipoPrestador(
                rs.getString("tipo_prestador")
        );

        return prestador;
    }

    private void validarParametros(
            int idRequerimientoCompra,
            long companyId) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (companyId <= 0) {
            throw new Exception(
                    "No se pudo determinar la empresa del portal."
            );
        }
    }

    private void validarRequerimiento(
            RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontró el requerimiento de compra."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento
                .getIdSector()
                .intValue() <= 0) {

            throw new Exception(
                    "El requerimiento no tiene sector informado."
            );
        }

        if (requerimiento.getEstado()
                != WebKeysCompras.ESTADO_PENDIENTE
                && requerimiento.getEstado()
                != WebKeysCompras.ESTADO_A_COTIZAR) {

            throw new Exception(
                    "El requerimiento no se encuentra "
                            + "en estado PENDIENTE o A COTIZAR."
            );
        }
    }

    private Connection obtenerConexion()
            throws Exception {

        Connection con =
                ConnectionHelper.getConnection();

        if (con == null) {
            throw new Exception(
                    "No se pudo obtener conexión "
                            + "a la base de datos."
            );
        }

        return con;
    }

    private boolean esEmailValido(String email) {
        return email != null
                && email.length() > 0
                && EMAIL_PATTERN
                .matcher(email)
                .matches();
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }

        String resultado = email.trim();

        return resultado.length() > 0
                ? resultado
                : null;
    }

    private String construirDetalleError(Exception e) {
        if (e == null) {
            return "Error no informado.";
        }

        String mensaje = e.getMessage();

        if (mensaje == null || mensaje.trim().length() == 0) {
            mensaje = e.getClass().getName();
        } else {
            mensaje = e.getClass().getName()
                    + ": "
                    + mensaje.trim();
        }

        mensaje =
                SENSITIVE_DETAIL_PATTERN
                        .matcher(mensaje)
                        .replaceAll("$1=<omitido>");

        mensaje =
                mensaje
                        .replace('\r', ' ')
                        .replace('\n', ' ')
                        .replace('\t', ' ');

        return truncar(mensaje, 4000);
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

    private String normalizarUsuario(String usuario) {
        if (usuario == null
                || usuario.trim().length() == 0) {

            return "sistema";
        }

        return usuario.trim();
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception e) {
            _log.debug(
                    "No se pudo cerrar ResultSet.",
                    e
            );
        }
    }
}

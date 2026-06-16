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
            LogFactoryUtil.getLog(
                    NotificarCotizacionPrestadorServiceImpl.class
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
            );

    private static final String SQL_LISTAR_CANDIDATOS =
            "SELECT id_prestador, descripcion, cuit, email, " +
                    "id_tipo_prestador, tipo_prestador " +
                    "FROM compras." +
                    "listar_prestadores_cotizacion_requerimiento(?)";

    private static final String SQL_REGISTRAR_COTIZACION =
            "{ ? = call " +
                    "compras.registrar_cotizacion_prestador(?,?,?) }";

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
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
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
                        "No hay prestadores pendientes de notificacion. "
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

        String email = normalizarEmail(
                prestador.getEmail()
        );

        if (!esEmailValido(email)) {
            _log.warn(
                    "Prestador con email invalido. "
                            + "idPrestador="
                            + prestador.getIdPrestador()
                            + ", idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra()
                            + ", email="
                            + email
            );

            resultado.incrementarErrores();
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

            /*
             * Primero se envia el correo.
             *
             * La tabla de auditoria solamente representa
             * notificaciones efectivamente enviadas.
             */
            mailHelper.enviar(
                    companyId,
                    email,
                    asunto,
                    cuerpo
            );

            boolean registrado =
                    registrarCotizacionPrestador(
                            requerimiento
                                    .getIdRequerimientoCompra(),
                            prestador.getIdPrestador(),
                            usuario
                    );

            if (!registrado) {
                _log.error(
                        "El correo fue enviado pero no se pudo "
                                + "insertar la auditoria. "
                                + "idPrestador="
                                + prestador.getIdPrestador()
                                + ", idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra()
                );

                resultado.incrementarErrores();
                return;
            }

            resultado.incrementarEnviados();

        } catch (Exception e) {
            _log.error(
                    "Error notificando cotizacion. "
                            + "idPrestador="
                            + prestador.getIdPrestador()
                            + ", idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra(),
                    e
            );

            resultado.incrementarErrores();
        }
    }

    private List<PrestadorCotizacion>
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

    private boolean registrarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = obtenerConexion();

            stmt = con.prepareCall(
                    SQL_REGISTRAR_COTIZACION
            );

            stmt.registerOutParameter(
                    1,
                    Types.BOOLEAN
            );

            stmt.setInt(
                    2,
                    idRequerimientoCompra
            );

            stmt.setInt(
                    3,
                    idPrestador
            );

            stmt.setString(
                    4,
                    normalizarUsuario(usuario)
            );

            stmt.execute();

            return stmt.getBoolean(1);

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private String construirAsunto(
            RequerimientoCompra requerimiento) {

        return "Solicitud de cotizacion - Requerimiento #"
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
                "OSPIM solicita cotizacion para el "
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
                        + "e importe de cotizacion.\n\n"
        );

        sb.append(
                "Este mensaje fue generado automaticamente "
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
                sb.append("Item sin descripcion");
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
                    "No se encontro el requerimiento de compra."
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
                != WebKeysCompras.ESTADO_COTIZACIONES) {

            throw new Exception(
                    "El requerimiento no se encuentra "
                            + "en estado Cotizaciones."
            );
        }
    }

    private Connection obtenerConexion()
            throws Exception {

        Connection con =
                ConnectionHelper.getConnection();

        if (con == null) {
            throw new Exception(
                    "No se pudo obtener conexion "
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
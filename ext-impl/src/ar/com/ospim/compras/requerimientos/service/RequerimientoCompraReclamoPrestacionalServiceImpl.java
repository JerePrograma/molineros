package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RequerimientoCompraReclamoPrestacionalServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    RequerimientoCompraReclamoPrestacionalServiceImpl.class
            );

    private static final String SQL_GET_RELACION =
            "SELECT * "
                    + "FROM compras.get_requerimiento_reclamo_prestacional(?)";

    private static final String SQL_RESERVAR =
            "SELECT compras.reservar_reclamo_prestacional(?,?,?)";

    private static final String SQL_FINALIZAR =
            "SELECT compras.finalizar_reclamo_prestacional(?,?,?,?)";

    private static final String SQL_LIBERAR =
            "SELECT compras.liberar_reserva_reclamo_prestacional(?,?,?)";

    private static final String SQL_MARCAR_ERROR =
            "SELECT compras.marcar_error_reclamo_prestacional(?,?,?,?,?)";

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_RELACION);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? mapRelacion(rs) : null;
        } catch (Exception e) {
            _log.error(
                    "No se pudo consultar la relación entre el requerimiento "
                            + "de compra y el Reclamo Prestacional. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void reservarCreacion(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        ejecutarBoolean(
                SQL_RESERVAR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setString(3, normalizarUsuario(usuario));
                    }
                },
                "No se pudo reservar la creación del Reclamo Prestacional."
        );
    }

    public void finalizarCreacion(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final int idReclamoPrestacional,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        ejecutarBoolean(
                SQL_FINALIZAR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setInt(3, idReclamoPrestacional);
                        stmt.setString(4, normalizarUsuario(usuario));
                    }
                },
                "No se pudo finalizar la relación con el Reclamo Prestacional."
        );
    }

    public boolean liberarReserva(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        return ejecutarBoolean(
                SQL_LIBERAR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setString(3, normalizarUsuario(usuario));
                    }
                },
                "No se pudo liberar la reserva del Reclamo Prestacional."
        );
    }

    public boolean marcarErrorPosteriorAlInsert(
            final int idRequerimientoCompra,
            final String tokenReserva,
            final int idReclamoPrestacional,
            final String error,
            final String usuario) throws Exception {

        validarIdRequerimiento(idRequerimientoCompra);
        validarToken(tokenReserva);

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional creado."
            );
        }

        return ejecutarBoolean(
                SQL_MARCAR_ERROR,
                new Parametrizador() {
                    public void parametrizar(PreparedStatement stmt)
                            throws Exception {

                        stmt.setInt(1, idRequerimientoCompra);
                        stmt.setString(2, tokenReserva);
                        stmt.setInt(3, idReclamoPrestacional);
                        stmt.setString(4, limitarError(error));
                        stmt.setString(5, normalizarUsuario(usuario));
                    }
                },
                "No se pudo registrar el error de vinculación "
                        + "del Reclamo Prestacional."
        );
    }

    protected boolean ejecutarBoolean(
            String sql,
            Parametrizador parametrizador,
            String mensajeError) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(sql);
            parametrizador.parametrizar(stmt);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(mensajeError);
            }

            boolean resultado = rs.getBoolean(1);

            if (!resultado) {
                throw new Exception(mensajeError);
            }

            return true;
        } catch (Exception e) {
            _log.error(mensajeError, e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private RequerimientoCompraReclamoPrestacional mapRelacion(ResultSet rs)
            throws Exception {

        RequerimientoCompraReclamoPrestacional relacion =
                new RequerimientoCompraReclamoPrestacional();

        relacion.setIdRequerimientoCompra(
                rs.getInt("id_requerimiento")
        );

        int idReclamo = rs.getInt("id_reclamo_prestacional");
        relacion.setIdReclamoPrestacional(
                rs.wasNull() ? null : Integer.valueOf(idReclamo)
        );

        relacion.setEstado(rs.getString("estado"));
        relacion.setTokenReserva(rs.getString("token_reserva"));
        relacion.setReservaFecha(rs.getTimestamp("reserva_fecha"));
        relacion.setUltimoError(rs.getString("ultimo_error"));
        relacion.setAltaFecha(rs.getTimestamp("alta_fecha"));
        relacion.setAltaUsr(rs.getString("alta_usr"));
        relacion.setModiFecha(rs.getTimestamp("modi_fecha"));
        relacion.setModiUsr(rs.getString("modi_usr"));

        return relacion;
    }

    private void validarIdRequerimiento(int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }
    }

    private void validarToken(String tokenReserva) throws Exception {
        if (WebKeysCompras.isEmpty(tokenReserva)) {
            throw new Exception(
                    "No se pudo validar el contexto de creación "
                            + "del Reclamo Prestacional."
            );
        }
    }

    private String normalizarUsuario(String usuario) {
        return WebKeysCompras.isEmpty(usuario)
                ? "sistema"
                : usuario.trim();
    }

    private String limitarError(String error) {
        String value = WebKeysCompras.trimToNull(error);

        if (value == null) {
            return "Error de vinculación no especificado.";
        }

        return value.length() <= 2000
                ? value
                : value.substring(0, 2000);
    }

    private void closeQuietly(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }

    protected interface Parametrizador {
        void parametrizar(PreparedStatement stmt) throws Exception;
    }
}

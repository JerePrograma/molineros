package ar.com.ospim.compras.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EditarRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);

    public int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?,?,?,?) }";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(
                    stmt,
                    2,
                    requerimiento.getIdRequerimientoCompra() > 0
                            ? Integer.valueOf(requerimiento.getIdRequerimientoCompra())
                            : null
            );

            setNullableInteger(
                    stmt,
                    3,
                    requerimiento.getNumero() > 0
                            ? Integer.valueOf(requerimiento.getNumero())
                            : null
            );

            stmt.setInt(
                    4,
                    requerimiento.getIdEstado() > 0
                            ? requerimiento.getIdEstado()
                            : WebKeysCompras.ESTADO_BORRADOR
            );

            setNullableInteger(stmt, 5, requerimiento.getIdSector());
            stmt.setBoolean(6, requerimiento.isRequiereAfiliado());
            setNullableDate(stmt, 7, requerimiento.getFechaSolicitud());
            stmt.setString(8, emptyToNull(requerimiento.getSolicitanteUsr()));
            stmt.setString(9, emptyToNull(requerimiento.getSolicitanteNombre()));
            stmt.setString(10, emptyToNull(requerimiento.getAfiliadoCuilTitular()));
            setNullableInteger(stmt, 11, requerimiento.getAfiliadoInte());
            stmt.setString(12, emptyToNull(requerimiento.getDescripcion()));
            stmt.setString(13, emptyToNull(requerimiento.getObservaciones()));
            stmt.setString(14, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{ ? = call compras.guardar_requerimiento_detalle(?,?,?,?,?,?,?,?,?,?,?) }";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(
                    stmt,
                    2,
                    detalle.getIdRequerimientoDetalle() > 0
                            ? Integer.valueOf(detalle.getIdRequerimientoDetalle())
                            : null
            );

            stmt.setInt(3, detalle.getIdRequerimientoCompra());

            setNullableInteger(
                    stmt,
                    4,
                    detalle.getRenglon() > 0
                            ? Integer.valueOf(detalle.getRenglon())
                            : null
            );

            stmt.setString(5, emptyToNull(detalle.getTipoArticulo()));
            stmt.setString(6, emptyToNull(detalle.getArticulo()));
            stmt.setBigDecimal(7, detalle.getCantidad());
            stmt.setString(8, emptyToNull(detalle.getUnidadMedida()));
            stmt.setBigDecimal(9, detalle.getPrecioUnitarioEstimado());
            stmt.setBigDecimal(10, detalle.getPrecioTotalEstimado());
            stmt.setString(11, emptyToNull(detalle.getObservaciones()));
            stmt.setString(12, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarDetalle(int idRequerimientoDetalle, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{call compras.borrar_requerimiento_detalle(?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoDetalle);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{call compras.borrar_requerimiento(?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void cambiarEstado(int idRequerimientoCompra, int idEstadoNuevo, String usuario) throws Exception {
        if (!WebKeysCompras.esEstadoValido(idEstadoNuevo)) {
            throw new Exception("Estado de requerimiento invalido.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{call compras.cambiar_estado_requerimiento(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableDate(CallableStatement stmt, int index, java.util.Date value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.DATE);
        } else {
            stmt.setDate(index, new java.sql.Date(value.getTime()));
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }
}

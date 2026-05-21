package ar.com.ospim.compras.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraAdjunto;
import ar.com.ospim.compras.beans.RequerimientoCompraItem;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EditarRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);

    public int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{ ? = call guardar_requerimiento_compra(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, requerimiento.getIdRequerimientoCompra() > 0 ? Integer.valueOf(requerimiento.getIdRequerimientoCompra()) : null);
            setNullableInteger(stmt, 3, requerimiento.getNumero() > 0 ? Integer.valueOf(requerimiento.getNumero()) : null);
            setNullableInteger(stmt, 4, requerimiento.getSectorId());
            stmt.setString(5, emptyToNull(requerimiento.getSectorDescripcion()));
            stmt.setString(6, emptyToNull(requerimiento.getSolicitanteUsr()));
            stmt.setString(7, emptyToNull(requerimiento.getEntidad()));
            stmt.setInt(8, requerimiento.getPrioridad());

            setNullableDate(stmt, 9, requerimiento.getFechaSolicitud());
            setNullableDate(stmt, 10, requerimiento.getFechaNecesidad());
            setNullableDate(stmt, 11, requerimiento.getFechaPedidoCotizacion());

            stmt.setString(12, emptyToNull(requerimiento.getDetalleRequerimiento()));
            stmt.setString(13, emptyToNull(requerimiento.getMotivo()));
            stmt.setString(14, requerimiento.getObservaciones());
            stmt.setBigDecimal(15, requerimiento.getImporteEstimadoTotal());

            Integer ordenCompra = requerimiento.getOrdenCompraNumero();
            if ((ordenCompra == null || ordenCompra.intValue() <= 0) && requerimiento.getIdOrdenCompra() != null) {
                ordenCompra = requerimiento.getIdOrdenCompra();
            }

            setNullableInteger(stmt, 16, ordenCompra);
            stmt.setString(17, emptyToNull(requerimiento.getAfiliado()));
            stmt.setString(18, emptyToNull(requerimiento.getDni()));
            setNullableInteger(stmt, 19, requerimiento.getRpNumero());
            stmt.setString(20, emptyToNull(requerimiento.getRpObservacion()));
            stmt.setString(21, emptyToNull(requerimiento.getPedidosPresupuestos()));
            stmt.setString(22, requerimiento.getComparativa());
            stmt.setString(23, requerimiento.getCargoOspim());
            stmt.setString(24, requerimiento.getCargoEnsalud());
            setNullableBoolean(stmt, 25, requerimiento.getRecupero());
            setNullableBoolean(stmt, 26, requerimiento.getCotizado());
            stmt.setString(27, emptyToNull(requerimiento.getLocalidad()));
            stmt.setString(28, emptyToNull(requerimiento.getProvincia()));
            stmt.setString(29, usuario);

            stmt.execute();
            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int guardarItem(RequerimientoCompraItem item, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{ ? = call guardar_requerimiento_compra_item(?,?,?,?,?,?,?) }";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, item.getIdItem() > 0 ? Integer.valueOf(item.getIdItem()) : null);
            stmt.setInt(3, item.getIdRequerimientoCompra());
            stmt.setString(4, item.getDescripcion());
            stmt.setBigDecimal(5, item.getCantidad());
            stmt.setString(6, item.getUnidadMedida());
            stmt.setBigDecimal(7, item.getImporteEstimado());
            stmt.setString(8, item.getObservaciones());

            stmt.execute();
            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarItem(int idItem, String usuario) throws Exception {
        executeBaja("{call borrar_requerimiento_compra_item(?,?)}", idItem, usuario);
    }

    public void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        executeBaja("{call borrar_requerimiento_compra(?,?)}", idRequerimientoCompra, usuario);
    }

    public int guardarAdjunto(RequerimientoCompraAdjunto adjunto, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{ ? = call guardar_requerimiento_compra_adjunto(?,?,?,?,?) }";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            stmt.setInt(2, adjunto.getIdRequerimientoCompra());
            if (adjunto.getFileEntryId() == null) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, adjunto.getFileEntryId().longValue());
            }
            stmt.setString(4, adjunto.getNombreArchivo());
            stmt.setString(5, adjunto.getTipoArchivo());
            stmt.setString(6, usuario);

            stmt.execute();
            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarAdjunto(int idAdjunto, String usuario) throws Exception {
        executeBaja("{call borrar_requerimiento_compra_adjunto(?,?)}", idAdjunto, usuario);
    }

    private void executeBaja(String sql, int id, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, id);
            stmt.setString(2, usuario);
            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null || value.intValue() <= 0) {
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

    private void setNullableBoolean(CallableStatement stmt, int index, Boolean value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.BOOLEAN);
        } else {
            stmt.setBoolean(index, value.booleanValue());
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }
}

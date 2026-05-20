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
            String sql = "{ ? = call guardar_requerimiento_compra(?,?,?,?,?,?,?,?,?,?,?) }";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, requerimiento.getIdRequerimientoCompra() > 0 ? Integer.valueOf(requerimiento.getIdRequerimientoCompra()) : null);
            setNullableInteger(stmt, 3, requerimiento.getSectorId());
            stmt.setString(4, requerimiento.getSolicitanteUsr());
            stmt.setString(5, requerimiento.getEntidad());
            stmt.setInt(6, requerimiento.getPrioridad());
            stmt.setDate(7, requerimiento.getFechaNecesidad() == null ? null : new java.sql.Date(requerimiento.getFechaNecesidad().getTime()));
            stmt.setString(8, requerimiento.getMotivo());
            stmt.setString(9, requerimiento.getObservaciones());
            stmt.setBigDecimal(10, requerimiento.getImporteEstimadoTotal());
            setNullableInteger(stmt, 11, requerimiento.getIdOrdenCompra());
            stmt.setString(12, usuario);

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
}

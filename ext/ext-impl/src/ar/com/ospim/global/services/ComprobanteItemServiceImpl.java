package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ComprobanteItemServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ComprobanteItemServiceImpl.class);

	private static ComprobanteItemServiceImpl instance = null;

	public static ComprobanteItemServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteItemServiceImpl();
		}
		return instance;
	}
	
	public int save(ComprobanteItem comp, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_comprobante_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, comp.getItem());
			stmt.setBigDecimal(8, comp.getSaldo());
			stmt.setBigDecimal(9, comp.getPorcentaje());
			stmt.setBigDecimal(10, comp.getValor());
			stmt.setBigDecimal(11, comp.getIvains());			
			stmt.setBigDecimal(12, comp.getIvanins());
			stmt.setBigDecimal(13, comp.getIvaexen());
			stmt.setString(14, comp.getObservaciones());
			stmt.setString(15, user);
			stmt.setInt(16, comp.getMotivo());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public int update(ComprobanteItem comp, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_comprobante_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, comp.getItem());
			stmt.setBigDecimal(8, comp.getSaldo());
			stmt.setBigDecimal(9, comp.getPorcentaje());
			stmt.setBigDecimal(10, comp.getValor());
			stmt.setBigDecimal(11, comp.getIvains());			
			stmt.setBigDecimal(12, comp.getIvanins());
			stmt.setBigDecimal(13, comp.getIvaexen());
			stmt.setString(14, comp.getObservaciones());
			stmt.setString(15, user);
			stmt.setInt(16, comp.getMotivo());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public int actualizarItemsLiquidacion(int id_liquidacion, ComprobanteItem comp, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_comprobantes_liquidacion_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, comp.getItem());
			stmt.setBigDecimal(8, comp.getSaldo());
			stmt.setBigDecimal(9, comp.getPorcentaje());
			stmt.setBigDecimal(10, comp.getValor());
			stmt.setBigDecimal(11, comp.getIvains());			
			stmt.setBigDecimal(12, comp.getIvanins());
			stmt.setBigDecimal(13, comp.getIvaexen());
			stmt.setString(14, comp.getObservaciones());
			stmt.setString(15, user);
			stmt.setInt(16, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public List<Comprobante> getComprobantesLikeNro(Comprobante comp)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = null;
		try {
			String sql = "{call buscar_comprobantes_like_nro(?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			comps = new ArrayList<Comprobante>();
			while (rs.next()) {
				comps.add(Comprobante.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}
	
	
	
	public int deleteLiquidacionReclamo(int idliquidacion , int  idReclamoPrestacional  , int  idPrestacionReclamo ) throws SystemException {
	
		Connection con = null;
		CallableStatement stmt = null;
		try {
			
			String sql = "{call borrar_liquidacion_reclamo_item(?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idliquidacion );
			stmt.setInt(2, idReclamoPrestacional  );
			stmt.setInt(3, idPrestacionReclamo );
			stmt.executeQuery();
		} catch (SQLException e) {
			_log.error("borrar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}



	public int delete(ComprobanteItem comp, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {			 
			String sql = "{call borrar_comprobante_item(?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, comp.getItem());			
			stmt.setString(8, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("borrar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
}

package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;
import ar.com.ospim.tesoreria.CuentaDuplicadaException;
import ar.com.ospim.tesoreria.service.ContabilidadServiceImpl;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CuentaServiceImpl {
	private static Log logger = LogFactoryUtil.getLog(CuentaServiceImpl.class);

	public void update(PlanCuentas pCuenta, User user, int entidad)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad==WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_cuenta_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad==WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_cuenta_uoma(?, ?, ?, ?, ?, ?, ?, ?,?)}";
			} else {
				sql = "{call actualizar_cuenta(?, ?, ?, ?, ?, ?, ?, ?,?)}";
			}

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, pCuenta.getId());
			stmt.setString(2, pCuenta.getCuenta());
			stmt.setString(3, pCuenta.getNumero());
			stmt.setBoolean(4, pCuenta.isImputable());
			stmt.setBoolean(5, pCuenta.getAjustaInflacion());
			stmt.setString(6, pCuenta.getTipo());
			stmt.setDate(7, new java.sql.Date(pCuenta.getValidoDesde().getTime()));
			stmt.setDate(8, new java.sql.Date(pCuenta.getValidoHasta().getTime()));
			stmt.setString(9, user.getScreenName());

			stmt.executeUpdate();
			
		} catch (Exception e) {
			logger.error("Error al actualizar cuenta", e);
			if (e.getMessage().contains("u_plan_cuentas")) {
				throw new CuentaDuplicadaException();
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void guardar(PlanCuentas pCuenta, User user, int entidad)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad==WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_cuenta_amtima(?, ?, ?, ?, ?, ?, ?,?)}";
			} else if (entidad==WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_cuenta_uoma(?, ?, ?, ?, ?, ?, ?,?)}";
			} else {
				sql = "{call insertar_cuenta(?, ?, ?, ?, ?, ?, ?,?)}";
			}

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, pCuenta.getCuenta());
			stmt.setString(2, pCuenta.getNumero());
			stmt.setBoolean(3, pCuenta.isImputable());
			stmt.setString(4, pCuenta.getTipo());
			stmt.setDate(5, new java.sql.Date(pCuenta.getValidoDesde()
					.getTime()));
			stmt.setDate(6, new java.sql.Date(pCuenta.getValidoHasta()
					.getTime()));
			stmt.setString(7, user.getScreenName());
			stmt.setBoolean(8, pCuenta.getAjustaInflacion());
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				pCuenta.setId(executeQuery.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al guardar cuenta", e);
			if (e.getMessage().contains("u_plan_cuentas")) {
				throw new CuentaDuplicadaException();
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void eliminar(PlanCuentas pCuenta, Date desde, Date hasta,
			User user, int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad==WebKeysGlobal.AMTIMA) {
//				sql = "{call eliminar_cuenta_amtima(?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_cuenta_uoma(?, ?, ?, ?)}";
			}else{
				sql = "{call eliminar_cuenta(?, ?, ?, ?)}";
			}

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, pCuenta.getId());
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			stmt.setString(4, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al eliminar cuenta", e);
			if (e.getMessage().contains("viola la llave foránea")) {
				throw new ConceptoUtilizadoException();
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void eliminarPlanCuenta(PlanCuentas pCuenta, User user, int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad==WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_cuenta_amtima(?, ?)}";
//			}else if(entidad==WebKeysGlobal.UOMA) {
//				sql = "{call uoma.eliminar_cuenta_uoma(?, ?, ?, ?)}";
//			}else{
//				sql = "{call eliminar_cuenta(?, ?, ?, ?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, pCuenta.getId());
			stmt.setString(2, user.getScreenName());
			stmt.executeUpdate();
			
		} catch (Exception e) {
			logger.error("Error al eliminar plan cuenta", e);
			if (e.getMessage().contains("viola la llave foránea")) {
				throw new ConceptoUtilizadoException();
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}
	public boolean estaUtilizado(PlanCuentas pCuenta, Date desde, Date hasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call verificar_cuenta_utilizada_amtima(?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.verificar_cuenta_utilizada_uoma(?, ?, ?)}";
			}else{
				sql = "{call verificar_cuenta_utilizada(?, ?, ?)}";
			}

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, pCuenta.getId());
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getBoolean(1);
			}
		} catch (Exception e) {
			logger.error("Error al verificar_cuenta_utilizada", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return true;
	}

	public static List<PlanCuentas> getPlanCuentas(int idCuenta, int entidad) {
		
		Connection con = null;
		List<PlanCuentas> lista = null;
		CallableStatement stmt = null;
		
		try {
			String sql="{call buscar_plan_cuentas_amtima_por_id(?)}";
//			if(entidad==WebKeysGlobal.OSPIM){
//				sql = "{call trae_plan_cuentas_amtima(?)}";
//			}if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call uoma.trae_plan_cuentas_uoma(?)}";
//			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCuenta);
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanCuentas>();
			
			while (rs.next()) {
				PlanCuentas pc = PlanCuentas.getMapping(rs);
				lista.add(pc);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
    public static List<PlanCuentas> getPlanCuentas(int idCuenta, Date desde,int entidad) {
		
		Connection con = null;
		List<PlanCuentas> lista = null;
		CallableStatement stmt = null;
		
		try {
			String sql="{call buscar_plan_cuentas_amtima_por_id(?,?)}";
//			if(entidad==WebKeysGlobal.OSPIM){
//				sql = "{call trae_plan_cuentas_amtima(?)}";
//			}if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call uoma.trae_plan_cuentas_uoma(?)}";
//			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCuenta);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanCuentas>();
			
			while (rs.next()) {
				PlanCuentas pc = PlanCuentas.getMapping(rs);
				lista.add(pc);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
    
    public static PlanCuentas getCuentaById(int idCuenta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_por_id_amtima(?)}";
//			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call uoma.trae_plan_cuentas_por_id_uoma(?, ?)}";
//			}else{
//				sql = "{call trae_plan_cuentas_por_id(?, ?)}";	
			}			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCuenta);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PlanCuentas.getMapping(rs);
			}
		} catch (SQLException e) {
			logger.error("Error al traer plan cuenta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}
    
	public static PlanCuentas getCuentaById(int idCuenta,Date desde, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_por_id_amtima(?,?)}";
//			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call uoma.trae_plan_cuentas_por_id_uoma(?, ?)}";
//			}else{
//				sql = "{call trae_plan_cuentas_por_id(?, ?)}";	
			}			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCuenta);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PlanCuentas.getMapping(rs);
			}
		} catch (SQLException e) {
			logger.error("Error al traer plan cuenta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}
	
	public static PlanCuentas getCuentaByNroCuenta(String idCuenta, Date desde,Date hasta,int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_por_nro_cuenta_amtima(?,?,?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_plan_cuentas_por_nro_cuenta(?,?,?)}";
			}else{
				sql = "{call trae_plan_cuentas_por_nro_cuenta(?,?,?)}";	
			}			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, idCuenta);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PlanCuentas.getMapping(rs);
			}
		} catch (SQLException e) {
			logger.error("Error al traer plan cuenta por nro cuenta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}
	
}

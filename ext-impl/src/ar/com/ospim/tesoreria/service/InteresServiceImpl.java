package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysInteres;
import ar.com.ospim.tesoreria.beans.interes.Interes;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class InteresServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(InteresServiceImpl.class);

	private static InteresServiceImpl instance = null;

	public static InteresServiceImpl getInstance() {
		if (null == instance) {
			instance = new InteresServiceImpl();
		}
		return instance;
	}

	private int doInsert(Interes interes, int entidad, Connection con) throws SystemException, SQLException {

		int retVal = 0;
		CallableStatement stmt = null;
		
		try {			
			// conn portalmolineros
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call public.inserta_interes_ospim(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				//sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				//sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
						
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, interes.getFechaInicio());
			stmt.setString(2, interes.getFechaFin());
			stmt.setDouble(3,interes.getInteresDia());			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("insert retVal " + retVal);
			
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Interes Afip", e);
			throw new SystemException(e);
		} 
		return retVal;
	}
	
	public boolean add(Interes interes, int entidad,Connection connectionParameter) throws SystemException, SQLException {
		int retVal = 0;
		Connection con = null;
		Connection conPortalEmp = null;

		//Abre las conexiones
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		conPortalEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();
		
		try {
			
			// conexion portalmolineros
			if (doInsert(interes, entidad, con) > 0) {
				// conexion portalempleadores
				retVal = doInsert(interes, entidad, conPortalEmp);				
			}		
			
		} catch (SQLException e) {
			_log.error("Error al insertar Interes Afip", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(null, con);				
			}
			ConnectionHelper.cerrar(null, conPortalEmp);
		}
		return (retVal == 1) ? true : false;
	}

	private int doUpdate(Interes interes, 
			String origFechaDesde, String origFechaHasta, 
			int entidad,Connection con) throws SystemException, SQLException { 
		
		int retVal = 0;
		CallableStatement stmt = null;
		
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call public.update_interes_ospim(?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				// sql = "{call cajachica.update_caja_chica_uoma(?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				// sql = "{call cajachica.update_caja_chica_amtima(?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			// Fechas para Set
			stmt.setString(1, interes.getFechaInicio());
			stmt.setString(2, interes.getFechaFin());
			// Fechas para Where			
			stmt.setString(3, origFechaDesde);
			stmt.setString(4, origFechaHasta);
			stmt.setDouble(5,interes.getInteresDia());			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("update retVal " + retVal);
			
			}
			
		} catch (SQLException e) {
			_log.error("Error al modificar Interes Afip", e);
			throw new SystemException(e);
		}
		return retVal;
		
	}
	
	public boolean update(Interes interes, 
			String origFechaDesde, String origFechaHasta, 
			int entidad,Connection connectionParameter) throws SystemException, SQLException {
		int retVal = 0;
		Connection con = null;
		Connection conPortalEmp = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		// conexion portalempleadores
		conPortalEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();
										
		try {
			
			// conexion portalmolineros
			if (doUpdate(interes, origFechaDesde, origFechaHasta, entidad, con) > 0) {
				retVal = doUpdate(interes, origFechaDesde, origFechaHasta, entidad, conPortalEmp);
			}
			
			
		} catch (SQLException e) {
			_log.error("Error al modificar Interes Afip", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(null, con);
			}
			ConnectionHelper.cerrar(null, conPortalEmp);
		}
		return (retVal == 1) ? true : false;
	}

	private int doDelete(Interes interes, int entidad,Connection con) throws SystemException, SQLException {
		int retVal = 0;
		CallableStatement stmt = null;
		
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call public.eliminar_interes_ospim(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				//sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				//sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, interes.getFechaInicio());
			stmt.setString(2, interes.getFechaFin());
			stmt.setDouble(3,interes.getInteresDia());			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("eliminar retVal " + retVal);
			
			}
			
		} catch (SQLException e) {
			_log.error("Error al eliminar Interes Afip", e);
			throw new SystemException(e);
		}
		return retVal;		
	}
	
	public boolean delete(Interes interes, int entidad,Connection connectionParameter) throws SystemException, SQLException {
		int retVal = 0;
		Connection con = null;
		Connection conPortalEmp = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		// conexion portalempleadores
		conPortalEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();

		try {
			
			if (doDelete(interes, entidad, con) > 0) {
				retVal = doDelete(interes, entidad, conPortalEmp);
			}
			
		} catch (SQLException e) {
			_log.error("Error al eliminar Interes Afip", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(null, con);
			}
			ConnectionHelper.cerrar(null, conPortalEmp);
		}
		return (retVal == 1) ? true : false;
	}
	
	public List<Interes> list(String fechaInicio, String fechaFin, Double interesDia, Connection connectionParameter)
			throws SystemException {
		return list(fechaInicio, fechaFin, interesDia,connectionParameter);
	}	
	
	public List<Interes> list(String fechaInicio, String fechaFin, Double interesDia, int entidad, Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Interes> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call public.trae_interes_ospim(?,?,?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				//sql = "{call cajachica.trae_cajas_chicas_uoma(?,?,?,?)}";
			}
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaInicio && fechaInicio.toString().trim().length() > 0) {
				stmt.setString(1, fechaInicio);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != fechaFin && fechaFin.toString().trim().length() > 0) {
				stmt.setString(2, fechaFin);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			// Limit cantidad de filas. No implementado aun.
			stmt.setNull(3, Types.NUMERIC);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Interes>();
			while (rs.next()) {
				Interes regInteres = Interes.getMapping(rs);
				// REview!
				// regInteres.setEntidad(entidad);
				list.add(regInteres);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Interes Afip", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
			
		}
		return list;
	}
	
}

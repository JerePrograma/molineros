package ar.com.uoma.cuentacorrienteempresa.services;

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

import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.SaldoInicial;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class SaldoInicialServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(SaldoInicialServiceImpl.class);

	private static SaldoInicialServiceImpl instance = null;

	public static SaldoInicialServiceImpl getInstance() {
		if (null == instance) {
			instance = new SaldoInicialServiceImpl();
		}
		return instance;
	}

	private int doInsert(SaldoInicial saldo, Connection con) throws SystemException, SQLException {

		int retVal = 0;
		CallableStatement stmt = null;
		
		try {			
			// conn portalmolineros
			String sql ="";
			sql = "{call public.inserta_saldo_inicial(?,?,?,?,?)}";	
						
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, saldo.getCuit());
			stmt.setString(2, saldo.getSucursal());
			stmt.setLong(3,saldo.getMonto().longValue());
			stmt.setString(4, saldo.getPeriodo_STR());					
			stmt.setInt(5, saldo.getTipoBoleta());
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("insert retVal " + retVal);
			
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Saldo Inicial", e);
			throw new SystemException(e);
		} 
		return retVal;
	}
	
	public boolean add(SaldoInicial saldo, Connection connectionParameter) throws SystemException, SQLException {
		int retVal = 0;
		Connection conPortalEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();
		
		try {
			// conexion portalempleadores
			retVal = doInsert(saldo, conPortalEmp);				
		} catch (SQLException e) {
			_log.error("Error al insertar Saldo Inicial", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(null, conPortalEmp);
		}
		return (retVal == 1) ? true : false;
	}

	private int doUpdate(SaldoInicial saldo, Connection con) throws SystemException, SQLException { 
		
		int retVal = 0;
		CallableStatement stmt = null;
		
		try {
			
			String sql ="";
			sql = "{call public.update_saldo_inicial(?,?,?,?)}";	
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, saldo.getId());
			stmt.setString(2, saldo.getPeriodo_STR());
			stmt.setLong(3,saldo.getMonto().longValue());
			stmt.setInt(4, saldo.getTipoBoleta());			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("update retVal " + retVal);
			
			}
			
		} catch (SQLException e) {
			_log.error("Error al modificar Saldo Inicial", e);
			throw new SystemException(e);
		}
		return retVal;
	}
	
	public boolean update(SaldoInicial saldo, Connection conn) throws SystemException, SQLException {
		int retVal = 0;
										
		try {
			// conexion portalmolineros
			retVal = doUpdate(saldo, conn);
			
		} catch (SQLException e) {
			_log.error("Error al modificar Saldo Inicial", e);
			throw new SystemException(e);
		} finally {
		}
		return (retVal == 1) ? true : false;
	}

	private int doDelete(int id,Connection con) throws SystemException, SQLException {
		int retVal = 0;
		CallableStatement stmt = null;
		
		try {
			
			String sql ="";
			sql = "{call public.eliminar_saldo_inicial(?)}";	
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				retVal = rs.getInt(1);
				_log.debug("eliminar retVal " + retVal);
			
			}
			
		} catch (SQLException e) {
			_log.error("Error al eliminar Saldo Inicial", e);
			throw new SystemException(e);
		}
		return retVal;		
	}
	
	public boolean delete(int id, Connection connectionParameter) throws SystemException, SQLException {
		int retVal = 0;
		Connection conPortalEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();

		try {
			retVal = doDelete(id, conPortalEmp);
		} catch (SQLException e) {
			_log.error("Error al eliminar Saldo Inicial", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(null, conPortalEmp);
		}
		return (retVal == 1) ? true : false;
	}
	
	public List<SaldoInicial> list(Integer Id, String cuit, String suc, Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SaldoInicial> list = null;
		try {
			String sql = "";
			sql = "{call trae_saldo_inicial(?,?,?)}";
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();			
			
			stmt = con.prepareCall(sql.toString());
			
			if (null != cuit && cuit.trim().length() > 0) {
				stmt.setString(1, cuit);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if (null != suc && suc.trim().length() > 0) {
				stmt.setString(2, suc);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			if (null != Id && Id.toString().trim().length() > 0) {
				stmt.setInt(3, Id);
			} else {
				stmt.setNull(3, Types.NUMERIC);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SaldoInicial>();
			while (rs.next()) {
				SaldoInicial reg = SaldoInicial.getMapping(rs);
				list.add(reg);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Saldo Inicial", e);
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

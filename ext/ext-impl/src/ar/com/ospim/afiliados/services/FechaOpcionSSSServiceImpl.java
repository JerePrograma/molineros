package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.beans.FechaPresentacionSSS;
import ar.com.ospim.util.ConnectionHelper;

public class FechaOpcionSSSServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(FechaOpcionSSSServiceImpl.class);

	public List<FechaPresentacionSSS> traerFechasPresentacionSSS() throws Exception {
		
		List<FechaPresentacionSSS> fechaSSS = new ArrayList<FechaPresentacionSSS>();
		FechaPresentacionSSS p = null;
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			con = ConnectionHelper.getConnection();
	
			String sql = "{call public.trae_fecha_presentacion_super()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
				
			while (rs.next()) { 
				p = FechaPresentacionSSS.getMapping(rs);
				
				fechaSSS.add(p);
			}
			
		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return fechaSSS;
	}
	
	
	
	
	public void insetarProximaFechaOpcionSSS(Date fechaPress, String usr) throws  SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call public.insertar_proxima_fecha_opcion_sss(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1,   new java.sql.Date(fechaPress.getTime()));
			stmt.setString(2, usr);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
				throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
	}
	
	
	
	public Date obtenerUltimaFechaOpcionCargada() throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		Date result = null;
		try {
			con = ConnectionHelper.getConnection();
			
			
			String sql = "{? = call public.obtener_ultima_fecha_opcion_sss_cargada()}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.DATE);
		
			stmt.executeUpdate();
			
			result = stmt.getDate(1);
			
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	
public Date obtenerProximaFechaOpcionPresentar() throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		Date result = null;
		try {
			con = ConnectionHelper.getConnection();
			
			
			String sql = "{? = call public.obtener_fecha_proxima_press_sss()}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.DATE);
		
			stmt.executeUpdate();
			
			result = stmt.getDate(1);
			
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}


	public Date obtenerUltimaFechaPresentadaSSS() throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		Date result = null;
		try {
			con = ConnectionHelper.getConnection();
			
			
			String sql = "{? = call public.obtener_ultima_fecha_presentada_super()}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.DATE);
		
			stmt.executeUpdate();
			
			result = stmt.getDate(1);
			
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	
}

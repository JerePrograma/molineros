package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiObservacion;
import ar.com.ospim.util.ConnectionHelper;

/**
 * @author SVA
 * 
 */
public class AfiObservacionServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(AfiObservacionServiceImpl.class);

	public List<AfiObservacion> getObservaciones(String cuilTitular, int inte) 
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<AfiObservacion> observaciones = new ArrayList<AfiObservacion>();
		AfiObservacion ao = null;
		
		try {
			con = ConnectionHelper.getConnection();
			
			_log.debug("cuil titular: " + cuilTitular + " inte: " +inte);
			
			String sql = "{call public.buscar_afi_observaciones(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ao = AfiObservacion.getMapping(rs, "obs_");
				
				observaciones.add(ao);
		
			}
		}catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);	
		}

		return observaciones;
	}
	
	public AfiObservacion getObservacion(int idObs) 
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		AfiObservacion ao = null;
		
		try {
			con = ConnectionHelper.getConnection();
			
			_log.debug("id: " + idObs);
			
			String sql = "{call public.buscar_afi_observacion(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idObs);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ao = AfiObservacion.getMapping(rs, "obs_");		
			}
		}catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);	
		}

		return ao;
	}
	
	public int insertarObservaciones(AfiObservacion ao, String user) 
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();
			
			_log.debug("cuil titular: " + ao.getCuilTitular() + " inte: " +ao.getInte());
			
			String sql = "{call public.inserta_afi_observacion(?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ao.getCuilTitular());
			stmt.setInt(2, ao.getInte());
			stmt.setString(3, ao.getObservacion());
			stmt.setString(4, user);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return rs.getInt(1);
			}
			
		}catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);	
		}

		return -1;
	}
}

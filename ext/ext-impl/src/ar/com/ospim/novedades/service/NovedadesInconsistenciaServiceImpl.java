package ar.com.ospim.novedades.service;

import java.sql.CallableStatement;
import java.sql.Connection;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

public class NovedadesInconsistenciaServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(NovedadesInconsistenciaServiceImpl.class);

	
	public void procesarInconsistencia(int idInconsistencia, int idProceso, String user) throws SystemException {
		
		logger.debug("incio procesarInconsistencia");
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String queryArch = "{call novedades_sss.inserta_novedad_procesar_inconsistencia(?,?,?) }";
    		
			stmt = con.prepareCall(queryArch.toString());
			
			stmt.setInt(1, idInconsistencia);
			stmt.setInt(2, idProceso);
			stmt.setString(3, user);
			
			stmt.executeQuery();
			
			
			
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	    logger.debug("Fin procesarInconsistencia");

	}
	
	

	public void bajaInconsistencia(int idInconsistencia, int idProceso, String user) throws SystemException {
		
		logger.debug("incio bajaInconsistencia");
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String queryArch = "{call novedades_sss.baja_novedad_procesar_inconsistencia(?,?,?) }";
    		
			stmt = con.prepareCall(queryArch.toString());
			
			stmt.setInt(1, idInconsistencia);
			stmt.setInt(2, idProceso);
			stmt.setString(3, user);
			
			stmt.executeQuery();
			
			
			
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	    logger.debug("Fin bajaInconsistencia");

	}
}

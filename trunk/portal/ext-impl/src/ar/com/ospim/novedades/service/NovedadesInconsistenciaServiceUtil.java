package ar.com.ospim.novedades.service;

import com.liferay.portal.SystemException;

public class NovedadesInconsistenciaServiceUtil {


	private static NovedadesInconsistenciaServiceImpl instance = null;

	public static NovedadesInconsistenciaServiceImpl getInstance() {
		if (null == instance) {
			instance = new NovedadesInconsistenciaServiceImpl();
		}
		return instance;
	}
	

	
	public static void procesarInconsistencia (int idInconsistencia, int idProceso, String user) throws SystemException{
		getInstance().procesarInconsistencia(idInconsistencia, idProceso , user) ;
	}
	
	public static void bajaInconsistencia (int idInconsistencia, int idProceso, String user) throws SystemException{
		getInstance().bajaInconsistencia(idInconsistencia, idProceso , user) ;
	}
}

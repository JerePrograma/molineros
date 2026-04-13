package ar.com.ospim.afiliados.services;

import java.util.List;

import com.liferay.portal.SystemException;

import ar.com.ospim.afiliados.beans.AfiObservacion;

/**
 * @author SVA
 *  
 */
public class AfiObservacionServiceUtil {

	private static AfiObservacionServiceImpl instance = null;

	public static AfiObservacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new AfiObservacionServiceImpl();
		}
		return instance;
	}

	public static int insertarObservaciones(AfiObservacion ao, String user)  throws SystemException {
		return getInstance().insertarObservaciones(ao,user);
	}

	public static List<AfiObservacion> getObservaciones(String cuilTitular, int inte) throws SystemException {
		
		return getInstance().getObservaciones(cuilTitular,inte);
	}
	
	public static AfiObservacion getObservacion(int idObs) throws SystemException {
		
		return getInstance().getObservacion(idObs);
	}
	 

}
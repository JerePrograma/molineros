package ar.com.ospim.webservice.service;



import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SimuladorSalarialServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(SimuladorSalarialServiceUtil.class);

	public static void registraUbicacionConsulta(Integer provincia ,Integer localidad){
		SimuladorSalarialServiceImpl.registraUbicacionConsulta(provincia, localidad);
	}

}

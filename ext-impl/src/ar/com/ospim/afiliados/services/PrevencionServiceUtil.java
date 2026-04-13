package ar.com.ospim.afiliados.services;

import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;

import ar.com.ospim.afiliados.beans.EmailHomologacionPS;
import ar.com.ospim.webservice.service.AfiliadoOpe;

/**
 * @author pconde
 * 
 */
public class PrevencionServiceUtil {

	
	private static PrevencionServiceImpl instance = null;

	public static PrevencionServiceImpl getInstance() {
		if (null == instance) {
			instance = new PrevencionServiceImpl();
		}
		return instance;
	}

	public static List<AfiliadoOpe> buscarHistoricoPrevencionAfi(String cuilTitular, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarHistoricoPrevencionAfi(cuilTitular,  fechaDesde, fechaHasta);
	}


	
	public static void procesar(int operacion, Integer  idTransaccion, boolean accion) 
			throws SystemException {

		getInstance().procesar(operacion,  idTransaccion, accion, null);
	}
	
	public static EmailHomologacionPS obtenerDatosEmailPrevencion(String cuilTitular,  int inte) 
			throws SystemException {

		return getInstance().obtenerDatosEmailPrevencion(cuilTitular,  inte);
	}


}
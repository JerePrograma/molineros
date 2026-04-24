package ar.com.ospim.global.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.beans.DestinatarioPorProceso;

/**
 * Mascara del servicio que da acceso a los datos de la aplicacion (BD).
 */
public class ProcesosCorreoServiceUtil {

	private static ProcesosCorreosServiceImpl instance = null;

	public static final int ALTA_PRE_CARGA = 1;
	public static final int CAMBIOS_DISCAPACIDAD = 2;
	public static final int CAMBIOS_LEGALES = 3;
	public static final int CIERRE_RECLAMO_PRESTACIONAL = 4;
	public static final int CIERRE_RECLAMO_PRESTACIONAL_RECHAZO = 5;
	public static final int COMPROBANTE_DUPLICADO = 6;
	
	public static ProcesosCorreosServiceImpl getInstance() {
		if (null == instance) {
			instance = new ProcesosCorreosServiceImpl();
		}
		return instance;
	}

	public static List<DestinatarioPorProceso> getDestinatariosInformadosPorProceso(int idProceso){
		return getInstance().getDestinatariosInformadosPorProceso(idProceso);
	}
	
	public static List<String> getListaCorreoDestinatariosInformadosPorProceso(int idProceso){
		
		List<String> correos = new ArrayList<String>();
		List<DestinatarioPorProceso> destinatarios = getInstance().getDestinatariosInformadosPorProceso(idProceso);
		
		for (Iterator<DestinatarioPorProceso> iterator = destinatarios.iterator(); iterator.hasNext();) {
			DestinatarioPorProceso dp = iterator.next();
			correos.add(dp.getCorreo());
		}
		
		return correos;
	}	
	
}
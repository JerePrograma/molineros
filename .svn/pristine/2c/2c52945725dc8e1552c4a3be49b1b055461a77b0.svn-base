package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class ReclamoCierrePrestadorInexistenteEmail {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
	private static ReclamoCierrePrestadorInexistenteEmail instance = null;

	public static ReclamoCierrePrestadorInexistenteEmail getInstance() {
		if (null == instance) {
			instance = new ReclamoCierrePrestadorInexistenteEmail();
		}
		return instance;
	}
	

  
  
  public void enviarEmailCierrePrestadorInexistente(PrestacionesReclamo prestacion , String nroReclamo){
		
	    _log.debug("enviarEmailCierrePrestadorInexistente  "  + nroReclamo);
  	
		List<String> emails;
		emails = new ArrayList<String>();

		String destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_ERROR_PRESTADOR_INEXISTENTE");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}

		EnviaEmailsThread.enviarMailDesatendido(asunto(prestacion, nroReclamo), mensaje(prestacion, nroReclamo), emails,0);				    	
		
	}
	
	

	private static String asunto(PrestacionesReclamo prestacion, String nroReclamo) {
		String out =  null;
																													
		out = "No existe prestador para  " +  " CUIT:  " + prestacion.getComprobanteCUIT()  ;
		return out;
																												
	}	
					
  
		
	private static String mensaje(PrestacionesReclamo prestacion, String nroReclamo) {
		String out =  null;
		
		out = "Prestador CUIT  : " + prestacion.getComprobanteCUIT()  
			  + "  no se encuentra cargado para poder liquidar reclamo prestacional nro "  + nroReclamo;
		return out;
	}	


	
	

	
	

}

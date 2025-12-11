package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.liferay.portal.SystemException;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class AutorizacionPrestacionalEmail {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
	private static AutorizacionPrestacionalEmail instance = null;

	public static AutorizacionPrestacionalEmail getInstance() {
		if (null == instance) {
			instance = new AutorizacionPrestacionalEmail();
		}
		return instance;
	}
	
	
	
	public void enviarEmailSeccional(int IdPreautorizacion , Integer nroAutorizacion, String nroDoc , int idtratamiento, boolean discapacidad){
		
		PreAutorizacion preautorizacion=null;
		Seccional seccional = null;
		String from = null;

    	try {
			preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(IdPreautorizacion);
			seccional =SeccionalServiceUtil.buscarSeccionalById(preautorizacion.getSeccionalAltaUsr() );
			if(seccional!=null && seccional.getId() > 0){
     			from = SeccionalServiceUtil.buscarContactosSeccionalEmail(seccional.getId()).get(0).getContacto(); 
     		}
    	} catch (SystemException e) {
    		_log.debug(e);
		} catch (Exception e) {
			_log.debug(e);
		}
//from="dsulfaro@uoma.org.ar";
    	
    	if(StringUtils.checkEmpty(from)){
 			from = "autorizaciones@ospim.org.ar";
 		}
    	
		List<String> emails;
		emails = new ArrayList<String>();
		 // TODO modificar  
		emails.add(from);
		
		
		
		ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
		PdfServlet pdfServlet=new PdfServlet();
		HashMap<String, String> hm = new HashMap<String, String>();
	
		hm.put("SUBREPORT_DIR", "jasper/");
		hm.put("id_ini", String.valueOf(idtratamiento));		
		byte[] pdfAutorizacion=pdfServlet.crearPdfComoAdjunto(PdfServlet.AUTORIZACION_TRATAMIENTO_JASPER, hm, PdfServlet.AUTORIZACION_TRATAMIENTO_ODT_FILENAME);
		pdfs.add(pdfAutorizacion);
			
		if(null!=pdfAutorizacion && pdfAutorizacion.length>902){
			pdfs.add(pdfAutorizacion);
		}
    	
		if(discapacidad){
			EnviaEmailsThread.enviarMailDesatendido(asuntoSeccionalDiscapacidad(nroAutorizacion, nroDoc), mensajeSeccionalDiscapacidad(), emails, pdfs);				    	

		}else{
			EnviaEmailsThread.enviarMailDesatendido(asuntoSeccional(nroAutorizacion, nroDoc), mensajeSeccional(), emails, pdfs);				    	

		}		
	}
	
	
	
	
  public void enviarEmailPrestador(int idPrest , Integer nroAutorizacion, String nroDoc, int idtratamiento, boolean discapacidad ){
		
		List<ContactoElectronicoPrestador> contactosE  =  null;

    	try {
			contactosE = PrestadorServiceUtil.getInstance().getContactosElectronicos(idPrest, 0);
		
			
    	} catch (Exception e) {
			_log.debug(e);
		}
    	
		List<String> emails;
		emails = new ArrayList<String>();
		 // TODO modificar  
		
		
		//emails.add("acomas@ospim.org.ar");
		
		ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
		PdfServlet pdfServlet=new PdfServlet();
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("SUBREPORT_DIR", "jasper/");
		hm.put("id_ini", String.valueOf(idtratamiento));		
		byte[] pdfAutorizacion=pdfServlet.crearPdfComoAdjunto(PdfServlet.AUTORIZACION_TRATAMIENTO_JASPER, hm, PdfServlet.AUTORIZACION_TRATAMIENTO_ODT_FILENAME);
		pdfs.add(pdfAutorizacion);
			
		if(null!=pdfAutorizacion && pdfAutorizacion.length>902){
			pdfs.add(pdfAutorizacion);
		}
		
  		for (ContactoElectronicoPrestador contacto : contactosE) {
			if (!contacto.getContacto().isEmpty()){
				emails.add(contacto.getContacto());				
			}
		}
		if (emails.size() == 0){
			emails.add("autorizaciones@ospim.org.ar");
			EnviaEmailsThread.enviarMailDesatendido("ERROR " + asuntoPrestador(nroAutorizacion, nroDoc, discapacidad), mensajePrestador(), emails, 1);
		}else{
			List<String> em = new ArrayList<String>();
			for(String e:emails) {
			   em.clear();
//e="acomas@ospim.org.ar";
			   em.add(e);
			   //EnviaEmailsThread.enviarMailDesatendido(asuntoPrestador(nroAutorizacion, nroDoc), mensajePrestador(), em, 1);
			   EnviaEmailsThread.enviarMailDesatendido(asuntoPrestador(nroAutorizacion, nroDoc, discapacidad), mensajePrestador(), em, pdfs);
			}   
		}
    	
		
	}
  
  
  public void enviarEmailAfilaido(String error , int idtratamiento, Integer nroAutorizacion, String nroDoc, String emailAfiliado){
		

  	
		List<String> emails;
		emails = new ArrayList<String>();
//emailAfiliado="acomas@ospim.org.ar";
		emails.add(emailAfiliado);
	
		ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
		PdfServlet pdfServlet=new PdfServlet();
		HashMap<String, String> hm = new HashMap<String, String>();
	
		hm.put("SUBREPORT_DIR", "jasper/");
		hm.put("id_ini", String.valueOf(idtratamiento));		
		byte[] pdfAutorizacion=pdfServlet.crearPdfComoAdjunto(PdfServlet.AUTORIZACION_TRATAMIENTO_JASPER, hm, PdfServlet.AUTORIZACION_TRATAMIENTO_ODT_FILENAME);
		pdfs.add(pdfAutorizacion);
			
		if(null!=pdfAutorizacion && pdfAutorizacion.length>902){
			pdfs.add(pdfAutorizacion);
		}
		
  	
		EnviaEmailsThread.enviarMailDesatendido(error + asuntoAfilaido(nroAutorizacion, nroDoc), mensajeAfiliado(), emails, pdfs);				    	
		
	}
	
	

	private static String asuntoAfilaido(Integer nroAutorizacion , String nroDoc) {
		String out =  null;
		
		out = "AUTORIZACION OSPIM PRESTACIONES " +  " NRO  " + nroAutorizacion + " DNI "  + nroDoc  ;
		return out;
	}	

  
		
	private static String asuntoPrestador(Integer nroAutorizacion , String nroDoc, boolean discapacidad) {
		String out =  null;
		
		if(discapacidad){
			out = "AUTORIZACION OSPIM PRESTACIONES DISCAPACIDAD " +  " NRO  " + nroAutorizacion + " DNI "  + nroDoc  ;

		}else{
			out = "AUTORIZACION OSPIM PRESTACIONES " +  " NRO  " + nroAutorizacion + " DNI "  + nroDoc  ;
		}
		return out;
	}	


	
	private static String asuntoSeccional(Integer nroAutorizacion , String nroDoc) {
		String out =  null;
		
		out = "AUTORIZACION TRATAMIENTO " +  DateUtils.getYear(new Date()) + " NRO  " + nroAutorizacion + " DNI "  + nroDoc  ;
		return out;
	}
	
	private static String asuntoSeccionalDiscapacidad(Integer nroAutorizacion , String nroDoc) {
		String out =  null;
		
		out = "AUTORIZACION TRATAMIENTO DISCAPACIDAD " +  DateUtils.getYear(new Date()) + " NRO  " + nroAutorizacion + " DNI "  + nroDoc  ;
		return out;
	}
	
	
	
	private static String mensajeAfiliado () {
		String out =  null;
		
		String email =  "mailto:cab@ospim.org.ar";
		
		out = "Estimado beneficiario, \r\n\n"  
				+ "Se adjunta la autorización correspondiente a la prestación que recibirá el \n "
				+ "beneficiario del asunto.   \n\n"
    			
			   + "Muchas Gracias \n\n"
			   + "Saludos.- \n";
		return out;
	}
	
	
	
	
	private static String mensajePrestador () {
		
		String out =  null;
		
		String email =  "mailto:integracion@ospim.org.ar";
		
		out = "Estimado prestador, \r\n\n"  
				+ "Se adjunta la autorización correspondiente a la prestación que brindará al beneficiario del asunto.   \n\n"
    		    + "Las facturas deben cargarse en el Portal de Proveedores entre el día 1 y 5 del mes posterior al de la prestación. \n"
    		    + "El acceso al Portal Proveedores lo encuentra en http://www.ospim.org.ar/ \n\n"
			   				
				
				+ "Saludos Cordiales.- \n";
		return out;
		
	}
	
	
	private static String mensajeSeccional () {
		String out =  null;
		out = "Estimado beneficiario, \r\n\n"  
				+ "Se adjunta la autorización correspondiente a la prestación que recibirá el \n "
				+ "beneficiario del asunto.   \n\n"
    			
			   + "Muchas Gracias \n\n"
			   + "Saludos.- \n";
		return out;
	}
	
	private static String mensajeSeccionalDiscapacidad () {
		String out =  null;
		out = "Estimados Compañeros, \r\n\n"  
				+ "Adjuntamos autorización del beneficiario del asunto.   \n\n"
    			   + "Por favor, les solicitamos que entreguen dicha autorización a la familia del afiliado y enviar     \n"
			   + "el legajo original de discapacidad a la sede central de OSPIM dirigido al   \"  SECTOR DISCAPACIDAD. \" " + "\n"
				
				+ "\n"
				+ "Muchas gracias. \n\n"
				+ "Saludos cordiales. \n";
		return out;
	}
	
	

}

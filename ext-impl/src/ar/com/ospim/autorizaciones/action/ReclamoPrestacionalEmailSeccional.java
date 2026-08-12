package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class ReclamoPrestacionalEmailSeccional {
	private Logger _log = Logger.getLogger(this.getClass());

	
	private static ReclamoPrestacionalEmailSeccional instance = null;

	public static ReclamoPrestacionalEmailSeccional getInstance() {
		if (null == instance) {
			instance = new ReclamoPrestacionalEmailSeccional();
		}
		return instance;
	}
	
		
	
  public void enviarEmailOspim(ReclamoPrestacional reclamoPrestacional ){
		
    	
		Seccional seccional = null;
		String seccionalDesc = null;
		String sector = null;
		try {
			seccional = SeccionalServiceUtil.buscarSeccionalById(reclamoPrestacional.getIdSeccional());
			seccionalDesc = seccional.getDescripcion();
		} catch (Exception e){
			seccionalDesc = "N/A";//Esto no deberia pasar siempre tiene que venir una seccional 
		}
	  	
		List<String> emails;
		emails = new ArrayList<String>();

		
		String to="";
		
		if("FARMACIA".equals(reclamoPrestacional.getSector())){
			to=TraeListasServiceUtil.getSystemConfig("AVISO_SECCIONAL_EMAIL_FARMACIA");
			sector = "Farmacia";
		}else if ("PRESTACIONES MEDICAS".equals(reclamoPrestacional.getSector())){
			to=TraeListasServiceUtil.getSystemConfig("AVISO_SECCIONAL_EMAIL_PRESTACIONES_MEDICAS");
			sector = "Prestaciones Medica";
		}else if("DISCAPACIDAD".equals(reclamoPrestacional.getSector())){
			to=TraeListasServiceUtil.getSystemConfig("AVISO_SECCIONAL_EMAIL_DISCAPACIDAD");
			sector = "Discapacidad";
		}else if ("ODONTOLOGIA".equals(reclamoPrestacional.getSector())){
			to=TraeListasServiceUtil.getSystemConfig("AVISO_SECCIONAL_EMAIL_ODONTOLOGIA");
			sector = "Odontologia";
		}else{
			_log.debug("Error en Sector ReclamoPrestacionalEmailSeccional ");
		}
		
		String[] vTo = to.split(";");
		
		for(int i=0;i<vTo.length;i++){
			emails.clear(); // DS -20230307 agregado para subsanar error envio de mail gmail
			emails.add(vTo[i]);	
			EnviaEmailsThread.enviarMailDesatendido(asuntoOspim(reclamoPrestacional, sector), mensajeOspim(reclamoPrestacional, seccionalDesc), emails, 1);
		}
// DS - 20030307	
//		EnviaEmailsThread.enviarMailDesatendido(asuntoOspim(reclamoPrestacional, sector), mensajeOspim(reclamoPrestacional, seccionalDesc), emails, 1);				    				
			
   }
  

  public void enviarEmailSeccional(ReclamoPrestacional reclamoPrestacional ){
		
		String to="";
	  	String nroReclamo = String.valueOf(reclamoPrestacional.getId_reclamo());
		List<ContactoElectronico> contactose; 

	  	int id = reclamoPrestacional.getSeccional().getId();		
		contactose=SeccionalServiceUtil.buscarContactosSeccionalEmail(id );
	  	
		for (ContactoElectronico contactoElectronico : contactose) {
			to = contactoElectronico.getContacto();
		}
		
		List<String> emails;
		emails = new ArrayList<String>();

		//emails.add("pconde@ospim.org.ar");
		emails.add(to);	
	
		EnviaEmailsThread.enviarMailDesatendido(asuntoSeccional(nroReclamo), mensajeSeccional(nroReclamo), emails, 1);				    				
			
   }
  
  public void enviarEmailSeccionalObservado(ReclamoPrestacional reclamoPrestacional, String observacionIngresada ){
		
		String to="";
	  	String nroReclamo = String.valueOf(reclamoPrestacional.getId_reclamo());
	  	String nombreAfiliado = reclamoPrestacional.getAfiliado().getApellidoNombre();
		List<ContactoElectronico> contactose; 

	  	int id = reclamoPrestacional.getSeccional().getId();		
		contactose=SeccionalServiceUtil.buscarContactosSeccionalEmail(id );
	  	
		for (ContactoElectronico contactoElectronico : contactose) {
			to = contactoElectronico.getContacto();
		}
		
		List<String> emails;
		emails = new ArrayList<String>();

		//emails.add("acomas@ospim.org.ar");
		emails.add(to);	
	
		EnviaEmailsThread.enviarMailDesatendido(asuntoSeccionalObservado(nroReclamo, nombreAfiliado), mensajeSeccionalObservado(reclamoPrestacional, observacionIngresada), emails, 1);				    				
			
 }
		
	private static String asuntoOspim(ReclamoPrestacional reclamo, String sector) {
		String out =  null;
		
		out = "Precarga reintegro  " +  " nro  " + reclamo.getId_reclamo() + " Sector " + sector;
		return out;
	}	
	
	private static String asuntoSeccional(String nroReclamo) {
		String out =  null;
		
		out = "Procesar reintegro  " +  " nro  " + nroReclamo   ;
		return out;
	}	

	private static String asuntoSeccionalObservado(String nroReclamo, String nombreAfiliado) {
		String out =  null;
		
		out = "Reclamo Prestacional Nro: " + nroReclamo + " Afiliado: " + nombreAfiliado + " (Observado)";
		return out;
	}	

	private static String mensajeOspim (ReclamoPrestacional reclamo,  String seccioanalDesc) {
		String out =  null;
		 int cant = 0;
		 List<PrestacionesReclamo> prestaciones = reclamo.getPrestaciones();
		 for (PrestacionesReclamo prestacionesReclamo : prestaciones) {
			 if (prestacionesReclamo.getEstado() == null || !prestacionesReclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)){
					cant = cant +1;
			  }

			 
		 }
			
		out = "Se informa que se cargó el siguiente reintegro con el nro: " + reclamo.getNroReclamo() + "\n\n" 
				+ "Seccional de carga: " +  seccioanalDesc + " \n"
				+ "Afiliado: " +  reclamo.getAfiliado().getApellido() + ",  " + reclamo.getAfiliado().getNombre() + " \n"
				+ "Documento: "  + reclamo.getAfiliado().getDocumento_tipo() + "  " + reclamo.getAfiliado().getDocu_numero() + "  \n\n"
				+ "Cantidad de prestaciones: " +  cant  + " \n ";
    					
		return out;
	}
	
	private static String mensajeSeccional (String nroReclamo) {
		String out =  null;
				
		out =  "Se informa que se está procesando el siguiente reintegro con el nro: " + nroReclamo + "\n\n";
    	      					
		return out;
	}

	private static String mensajeSeccionalObservado (ReclamoPrestacional reclamo, String observacionIngresada) {
		String out =  null;
				
		out = "Se informa que se observó el Reclamo Nro: " + reclamo.getNroReclamo() + "\n\n" 
				+ "Afiliado: " +  reclamo.getAfiliado().getApellido() + ",  " + reclamo.getAfiliado().getNombre() + " \n"
				+ "Documento: "  + reclamo.getAfiliado().getDocumento_tipo() + "  " + reclamo.getAfiliado().getDocu_numero() + "  \n"
				+ "CUIL Titular: " + reclamo.getCuit_titular() + "\n "
				+ "CUIL " + reclamo.getAfiliado().getCuil() + "\n\n "
				+ "Observacion: " +  observacionIngresada + " \n ";
    					
		return out;
	}

}

package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.mail.MailUtils;

public class AvisoVencimientoUrgente extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4933974435595386180L;
	
	private static Log logger = LogFactoryUtil.getLog(AvisoVencimientoUrgente.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		List<ContactoCRM> alertasCRM = null; 
		
		String usuNotif = null, sectorNotif = null, bodyDerivado = null, bodyResponsables=null, usuarioFullName = null;

		ArrayList<String> emailsNotificaAlerta = new ArrayList<String>();

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		try {
			alertasCRM = ReportesServiceUtil.getContactosCRMparaAlertasUrgente(ra);
			
			for (Iterator<ContactoCRM> iterator = alertasCRM.iterator(); iterator.hasNext();) {
				ContactoCRM contacto = iterator.next();
				
				User userLiferay = null;

				Calendar c = Calendar.getInstance();
				c.setTime(contacto.getAltaFecha());
				
				TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
				Calendar hoy = Calendar.getInstance();
				hoy.setTimeZone(tz);
				
				long duracionAbiertoLong = (hoy.getTimeInMillis() - c.getTimeInMillis());
				
				int duracionAbierto = (int) (duracionAbiertoLong / (24 * 60 * 60 * 1000));
				
				if(contacto.getEstado().equals(ContactoCRM.ESTADOS.CERRADO)){
					// nada porque se soluciono antes del alerta
				}else{
					// revalidar quien esta derivado y buscar a quienes notificar...
					usuNotif = contacto.getDerivacion().getUsuario();
					sectorNotif = contacto.getDerivacion().getGrupo() ;
					
					if(usuNotif.equalsIgnoreCase("TODOS")){
						usuarioFullName = "Todos los usuarios del sector " + sectorNotif + " tienen ";
					}else{	
						
						long idUser =  UserLocalServiceUtil.getUserIdByScreenName(Long.parseLong("10112"), usuNotif);  // companyId =10112, para todos es la misma.

						userLiferay = UserLocalServiceUtil.getUserById(idUser);
						usuarioFullName = "El usuario "+ userLiferay.getFullName() + " tiene ";
					}
					

					emailsNotificaAlerta = this.armarListaDestinatariosAlerta(usuNotif, sectorNotif, false);
					
					bodyDerivado = "Ud. tiene el contacto n° "+ contacto.getIdContacto() +" pendiente de respuesta, " +
							"y su resolución debe ser inmediata. Ya transcurrieron " + duracionAbierto + " días desde la llamada del beneficiario." ;
					
					rac = ReportesServiceUtil.getConfiguracion();
					
					List<String>lem=new ArrayList<String>();
					
					for(String em:emailsNotificaAlerta) {
				 	  lem.clear();
					  lem.add(em);
					  MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), 
							rac.getPass(), 
							lem, 
							"URGENTE! - Alerta Vencimiento de CRM",
							bodyDerivado, 
							1);
					}
					bodyResponsables = usuarioFullName + "asignado el contacto n° "+ contacto.getIdContacto() +
							" y esta pendiente de respuesta, " +
							"y su resolución debe ser inmediata. Ya transcurrieron " + duracionAbierto + " días desde la llamada del beneficiario.";
					
					emailsNotificaAlerta.clear();
					emailsNotificaAlerta = this.armarListaDestinatariosAlerta(usuNotif, sectorNotif, true);
					
					
					for(String em:emailsNotificaAlerta) {
					 	  lem.clear();
						  lem.add(em);
					      MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), 
							rac.getPass(), 
							lem, 
							"URGENTE! - Alerta Vencimiento de CRM",
							bodyResponsables,
							1);
					}      
				}
			}

			ra.setUltimaEjecucion(new Date());
	
			ReportesServiceUtil.reporteEjecutado(ra);

			logger.debug("Fin de Envío de alertas de vencimiento CRM urgente");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (PortalException e) {
			logger.error(e);	
		} catch (SystemException e) {
			logger.error(e);
		}
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<String> armarListaDestinatariosAlerta(String usuNotif, String sectorNotif, boolean soloResponsables){

		DerivacionNotificacion dn = null;
		List<DerivacionNotificacion> destinatarios = new ArrayList<DerivacionNotificacion>();
		
		ArrayList<String> emailsNotificaAlerta = new ArrayList<String>();
		
		try {
			if(usuNotif!=null && usuNotif.equalsIgnoreCase("TODOS")){ // derivacion todo el sector
				destinatarios = (ArrayList<DerivacionNotificacion>) CrmServiceUtil.getNotificacionDerivacionSector(sectorNotif);
			}else{   // un solo usuario derivado
				dn = CrmServiceUtil.getNotificacionDerivacion(usuNotif);
				if(dn != null){
					destinatarios.add(dn);
				}
			}
		} catch (SystemException e) {
//			nada
		}
//		de la lista de el/los usuario/s el primer elemento trae a los responsables	
		for (Iterator<DerivacionNotificacion> iterator = destinatarios.iterator(); iterator.hasNext();) {
			DerivacionNotificacion derivNotif = iterator.next();
			
			if(soloResponsables){
				
				String[] auxEmails = derivNotif.getResponsableEmail().split(";");
				
				for (int i = 0; i < auxEmails.length; i++) {
					
					emailsNotificaAlerta.add(auxEmails[i]);
					
				}
				break;
			}else{
				emailsNotificaAlerta.add(derivNotif.getDerivacionEmail());
			}
		}

		if(emailsNotificaAlerta==null || emailsNotificaAlerta.isEmpty()){
			emailsNotificaAlerta.add("info@ospim.org.ar");
		}
		
		return emailsNotificaAlerta;
	}

}

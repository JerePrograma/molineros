package ar.com.ospim.autorizaciones.reportes.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.StringUtils;

@SuppressWarnings("unused")
public class CUDAvisoVencimiento {
	private static Date fechaOrigen;
	private static Integer diasAlVencimiento;
	
	private static Log _log = LogFactoryUtil
			.getLog(CUDAvisoVencimiento.class);

	

	public Date getFechaOrigen() {
		return fechaOrigen;
	}



	public void setFechaOrigen(Date fechaOrigen) {
		this.fechaOrigen = fechaOrigen;
	}



	public Integer getDiasAlVencimiento() {
		return diasAlVencimiento;
	}



	public void setDiasAlVencimiento(Integer diasAlVencimiento) {
		this.diasAlVencimiento = diasAlVencimiento;
	}



	public static Integer generaAvisoVencimiento() throws SystemException {
		Integer ret=0;
		String subject ="Aviso vencimiento CUD";
		ArrayList<String> emails = new ArrayList<String>();
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = ReportesServiceUtil.getConfiguracion();
		List<AfiDocumentacion> lista =AutorizacionesServiceUtil.getListaVencimientosCUD(fechaOrigen, diasAlVencimiento);
		Map<Integer, List<AfiDocumentacion>> mSeccionales = new HashMap<Integer, List<AfiDocumentacion>>();
		for (AfiDocumentacion d:lista) {
			if(d.getAfiliado().getEmail()!=null) {
				if(d.getAfiliado().getSeccional()!=null && d.getAfiliado().getSeccional().getDescripcion()!=null) {
				    List<AfiDocumentacion> lad =(List<AfiDocumentacion>) mSeccionales.get(d.getAfiliado().getSeccional().getId());
				    if(lad == null) {
				    	lad = new ArrayList<AfiDocumentacion>();
				    }
				    lad.add(d);
				    mSeccionales.put(d.getAfiliado().getSeccional().getId(), lad);
				}    		
String to="dsulfaro@uoma.org.ar"; //d.getAfiliado().getEmail();
				emails.clear();
				emails.add(to);
				MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(), emails, subject, "body",1);
				ret++;
			}
		}
		

		//Seccionales
		String to="";
		for (Integer key : mSeccionales.keySet()) {
			List<AfiDocumentacion> preAuts = (List<AfiDocumentacion>) mSeccionales.get(key);
	
            if(key==0){
               to=WebKeysAutorizaciones.EMAIL_AUTORIZACIONES;
            }else {
                List<ContactoElectronico> lc =  SeccionalServiceUtil.getInstance().buscarContactosSeccionalEmail(key);
			    to="";
			    for(ContactoElectronico ce:lc){
				  if(ce.getTipo().equals(ContactoElectronico.Tipo.EMAIL)){
					to=ce.getContacto();
					break;
				  }
			    }
            }
            
			if(StringUtils.checkEmpty(to)){
				to=WebKeysAutorizaciones.EMAIL_SISTEMAS;
			}
			
to="dsulfaro@uoma.org.ar";
			if(preAuts.size()>0 && to.length()>0){
			  String body="Vencimiento CUD Afiliados ";
			  
			  body +="<table border='1'>";
			  for(AfiDocumentacion ad :preAuts) {
                 body +="<tr><td>" + ad.getAfiliado().getApellidoNombre() + " " + ad.getAfiliado().getDocu_numero() +"</td></tr>";				  
			  }
			  body +="</table>";
			  emails.clear();
			  emails.add(to);
			  MailUtils.enviarMailGmailSinAdjHTML(rac.getMailFrom(), rac.getPass(), emails, subject, body,1);
			}
			
		}
		
		return ret;
	}
		
}



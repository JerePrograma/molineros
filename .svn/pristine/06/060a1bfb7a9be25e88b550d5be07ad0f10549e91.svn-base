package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Seccional;

public class TipoFiltroTitular extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
	
		Logger _log = Logger.getLogger(this.getClass());
		
		String idSeccional = req.getParameter("id_seccional");
		
		Seccional seccional = null;
		try{
			seccional = SeccionalServiceUtil.buscarSeccionalById(Integer.parseInt(idSeccional));
		}catch (Exception e) {
			seccional = null;
			_log.debug("Error al traer Seccioanl");
		}
		

		String json = "{\"listaFiltrada\": ["; 
		

	
		
		if (seccional != null  && seccional.isPagoSeccional()){
			for(int i = 0; i < WebKeysAutorizaciones.FILTRO_PAGO.length; i++ ) {
				 json +=  "\""+ "<option value='" + WebKeysAutorizaciones.FILTRO_PAGO[i][0]+"'>  "+WebKeysAutorizaciones.FILTRO_PAGO[i][1]  +
	            			"</option>"+"\""+",";     
			}
		}else{
			for(int i = 0; i < WebKeysAutorizaciones.FILTRO_PAGO.length; i++ ) {
				 if 	(!"2".equals(WebKeysAutorizaciones.FILTRO_PAGO[i][0])){
		             json +=  "\""+ "<option value='" + WebKeysAutorizaciones.FILTRO_PAGO[i][0]+"'>  "+WebKeysAutorizaciones.FILTRO_PAGO[i][1]  +
		         			"</option>"+"\""+",";
		        }
	           
			}
		}
			
		
		
        
        int count = json.length();
		String pago = json.substring(0, count-1);
		pago += "]}";
		return pago;
		
	
	}
}
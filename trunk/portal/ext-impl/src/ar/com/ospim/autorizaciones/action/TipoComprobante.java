package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class TipoComprobante extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String tipoDoc = req.getParameter("tipo_doc");
		

		String json = "{\"listaFiltrada\": ["; 
		

		if ("0".equals(tipoDoc)){
			for(int i = 0; i < WebKeysAutorizaciones.TIPO_COMPROBANTE.length; i++ ) {
	            if 	(!"NOTA AUTORIZACION PAGO".equals(WebKeysAutorizaciones.TIPO_COMPROBANTE[i][0])){
	                json +=  "\""+ "<option value='" + WebKeysAutorizaciones.TIPO_COMPROBANTE[i][0]+"'>  "+WebKeysAutorizaciones.TIPO_COMPROBANTE[i][1]  +
	            			"</option>"+"\""+",";
	           }
			}
		}else{
			for(int i = 0; i < WebKeysAutorizaciones.TIPO_COMPROBANTE.length; i++ ) {
	             json +=  "\""+ "<option value='" + WebKeysAutorizaciones.TIPO_COMPROBANTE[i][0]+"'>  "+WebKeysAutorizaciones.TIPO_COMPROBANTE[i][1]  +
	            			"</option>"+"\""+",";
	           
			}
		}
			
		
		
        
        int count = json.length();
		String comprobante = json.substring(0, count-1);
		comprobante += "]}";
		return comprobante;
		
	
	}
}
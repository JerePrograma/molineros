package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class TipoFiltroLetraComprobante extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
			
		String tipoPedido = req.getParameter("tipo_pedido");
		
	
		String json = "{\"listaFiltrada\": ["; 
		

	
		
		if (tipoPedido != null  &&  "REINTEGRO".equalsIgnoreCase(tipoPedido)){
			for(int i = 0; i < WebKeysAutorizaciones.LETRA_COMPROBANTE.length; i++ ) {
				 json +=  "\""+ "<option value='" + WebKeysAutorizaciones.LETRA_COMPROBANTE[i][0]+"'>  "+WebKeysAutorizaciones.LETRA_COMPROBANTE[i][1]  +
	            			"</option>"+"\""+",";     
			}
		}else{
			for(int i = 0; i < WebKeysAutorizaciones.LETRA_COMPROBANTE.length; i++ ) {
				if 	(!"A".equalsIgnoreCase(WebKeysAutorizaciones.LETRA_COMPROBANTE[i][0])){
		            json +=  "\""+ "<option value='" + WebKeysAutorizaciones.LETRA_COMPROBANTE[i][0]+"'>  "+WebKeysAutorizaciones.LETRA_COMPROBANTE[i][1]  +
		         			"</option>"+"\""+",";
		        }
			}
		}
			
		
		
        
        int count = json.length();
		String comprobante = json.substring(0, count-1);
		comprobante += "]}";
		return comprobante;
		
	
	}
}
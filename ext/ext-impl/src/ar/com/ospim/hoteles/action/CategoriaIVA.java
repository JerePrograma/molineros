package ar.com.ospim.hoteles.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.uoma.WebKeysUOMA;

public class CategoriaIVA extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String tipoCliente = req.getParameter("tipo_cliente");
		

		String json = "{\"listaFiltrada\": ["; 
		

		if ("0".equals(tipoCliente)){
			for(int i = 0; i < WebKeysUOMA.CATEGORIAS_IVA.length; i++ ) {
	            if 	("CS".equals(WebKeysUOMA.CATEGORIAS_IVA[i][0])){
	                json +=  "\""+ "<option value='" + WebKeysUOMA.CATEGORIAS_IVA[i][0]+"'>  "+WebKeysUOMA.CATEGORIAS_IVA[i][1]  +
	            			"</option>"+"\""+",";
	            }
			}
		}else{
			for(int i = 0; i < WebKeysUOMA.CATEGORIAS_IVA.length; i++ ) {
	            if 	("1".equals(tipoCliente) && "CS".equals(WebKeysUOMA.CATEGORIAS_IVA[i][0])){
	            	//No cargo elementos
	            }else{
	            	json +=  "\""+ "<option value='" + WebKeysUOMA.CATEGORIAS_IVA[i][0]+"'>  "+WebKeysUOMA.CATEGORIAS_IVA[i][1]  +
	            			"</option>"+"\""+",";
	            }
			}	
		}
			
		
		
        
        int count = json.length();
		String iva = json.substring(0, count-1);
		iva += "]}";
		return iva;
		
	
	}
}
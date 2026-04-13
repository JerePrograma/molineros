package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class IdProvinciaLocalidadAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String idProvincia = req.getParameter("idProvincia");
		
		Map<Integer,ArrayList<Localidad>> map = (Map<Integer,ArrayList<Localidad>>)req.getSession()
				.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA);
		List<Localidad> lista =map.get(Integer.parseInt(idProvincia));
		String json = "{\"listaFiltrada\": ["; 
		
		json+=  "\""+"<option selected value='0'>Seleccione una localidad</option>" +"\"" + ",";
		
        for(int i = 0; i < lista.size(); i++) {
        	json +=  "\""+ "<option value='" +lista.get(i).getId()+"'> "+lista.get(i).getDescripcion() +
        			"</option>"+"\""+",";
		}	
        
        int count = json.length();
		String localidades = json.substring(0, count-1);
		localidades += "]}";
		return localidades;
		
		/*
		String json = "{\"listaFiltrada\": ["; 
        for(int i = 0; i < lista.size(); i++) {
				json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
		}	
		int count = json.length();
		String localidades = json.substring(0, count-1);
		localidades += "]}";
		return localidades;
		*/
		
		
/*		
		List<Localidad> lista = (List<Localidad>) req.getSession()
				.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION);
		int iProvinciaInt = Integer.parseInt(idProvincia);
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			int idProv = lista.get(i).getId_provincia();
			if(idProv == iProvinciaInt) { 
				json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
			}
		}	
		int count = json.length();
		String localidades = json.substring(0, count-1);
		localidades += "]}";
		
		
		return localidades;
		
*/		
		
//		List<Localidad> lista = TraeListasServiceUtil.getLocalidadesPorProvincia(Integer.parseInt(idProvincia), null);
//
//		String json = "{\"listaFiltrada\": ["; 
//		for(int i = 0; i < lista.size(); i++) {
//				json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";
//		}	
//		int count = json.length();
//		String localidades = json.substring(0, count-1);
//		localidades += "]}";
//		return localidades;
	}
}
package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Localidad;

import com.liferay.portal.struts.JSONAction;

public class IdProvinciaLocalidadSssAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String idProvinciaSss = req.getParameter("idProvincia");

		List<Localidad> lista = (List<Localidad>) req.getSession()
				.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION);
		int iProvinciaInt = Integer.parseInt(idProvinciaSss);
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			int idProvSss = lista.get(i).getId_provinciasss();
			if(idProvSss == iProvinciaInt) { 
				json += "\""+lista.get(i).getId_localidadesss()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
			}
		}	
		int count = json.length();
		String localidades = json.substring(0, count-1);
		localidades += "]}";
		return localidades;
	}
}
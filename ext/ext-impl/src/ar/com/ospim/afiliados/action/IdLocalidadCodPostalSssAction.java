package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Localidad;

import com.liferay.portal.struts.JSONAction;

public class IdLocalidadCodPostalSssAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String idLocalidadSss = req.getParameter("idLocalidad");
		List<Localidad> lista = (List<Localidad>) req.getSession()
				.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION);
		int iLocalidadInt = Integer.parseInt(idLocalidadSss);
		String json = "{ \"codPostal\" : \"" ;
		int i = 0;
		boolean flag = false;
		while (i < lista.size() && !flag) {
			int idLoc = lista.get(i).getId_localidadesss();
			if (idLoc == iLocalidadInt) {
				json += lista.get(i).getCod_postal();
				flag = true;
			}
			i++;
		}
		json += "\"}";
		return json;
	}
}
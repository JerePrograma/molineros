package ar.com.ospim.autorizaciones.beans;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class getIntegracionDetalle extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		String id = req.getParameter("id_integracion");
		List<ReclamosPrestacionalesIntegracion> lista = (List<ReclamosPrestacionalesIntegracion>) req.getSession()
				.getAttribute(WebKeysAutorizaciones.RECLAMOS_PRESTACIONALES_INTEGRACION_EN_SESION);
		int idIntegracion = Integer.parseInt(id);
		String json = "{ \"DescripcionLarga\" : \"" ;
		int i = 0;
		boolean flag = false;
		while (i < lista.size() && !flag) {
			int idInte = lista.get(i).getId();
			if (idInte == idIntegracion) {
				json += lista.get(i).getDescripcionLarga();
				flag = true;
			}
			i++;
		}
		json += "\"}";
		return json;
	}
}
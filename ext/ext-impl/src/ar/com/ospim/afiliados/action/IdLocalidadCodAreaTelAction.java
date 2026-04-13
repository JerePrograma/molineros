package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.struts.JSONAction;

public class IdLocalidadCodAreaTelAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String idLocalidad = req.getParameter("idLocalidad");
		List<Localidad> lista = (List<Localidad>) req.getSession()
				.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION);
		int iLocalidadInt = Integer.parseInt(idLocalidad);
		String json = "{ \"codAreaTel\" : \"" ;
		int i = 0;
		boolean flag = false;
		while (i < lista.size() && !flag) {
			int idLoc = lista.get(i).getId();
			if (idLoc == iLocalidadInt) {
				if(StringUtils.checkNotEmpty(lista.get(i).getCodAreaTelefono()) && !lista.get(i).getCodAreaTelefono().equalsIgnoreCase("null")){
					json += lista.get(i).getCodAreaTelefono();
				}else{
					json += "";
				}
				flag = true;
			}
			i++;
		}
		json += "\"}";
		return json;
	}
}
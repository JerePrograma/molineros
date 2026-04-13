package ar.com.cgt.ddhh.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.beans.TiposNormasDDHH;
import ar.com.cgt.ddhh.services.TraeListasServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class IdSistemaTiposNormasDHAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String idSistema = req.getParameter("idSistema");

		ArrayList<TiposNormasDDHH> lista = (ArrayList<TiposNormasDDHH>) TraeListasServiceUtil.getTiposNormasDDHH(idSistema);
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
		}
		int count = json.length();
		String tiposNormas = json.substring(0, count-1);
		tiposNormas += "]}";
		return tiposNormas;
	}
}
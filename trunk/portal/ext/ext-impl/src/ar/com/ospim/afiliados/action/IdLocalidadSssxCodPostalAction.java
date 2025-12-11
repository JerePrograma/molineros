package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class IdLocalidadSssxCodPostalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String cpStr = req.getParameter("codigopostal");
		int iCodPos = Integer.parseInt(cpStr!=null?cpStr:"0");
		
		List<Localidad> lista = TraeListasServiceUtil.getLocalidadesPorCP(iCodPos);
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
		}	
		int count = json.length();
		String localidades = json.substring(0, count-1);
		localidades += "]}";
		return localidades;
	}
}
package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;

public class ReclamoPrestacionalProponeLoteAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int respuesta = 0;
		respuesta = ReclamosPrestacionesServiceUtil.getLoteVigenteReclamoPrestacional();
		
		return "{ \"lote\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}
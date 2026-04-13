package ar.com.ospim.afiliados.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class ValidaAfiVigenteAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean result = false;
		int respuesta = 0;
		String cuil = req.getParameter("cuil");
		String inte = req.getParameter("inte");
		
		result = EditarAfiliadoServiceUtil.estaVigenteEnOtroGrupoFliar(cuil,inte);
		
		if(result){
			respuesta=1;
		}

		return "{ \"validado\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}
package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class ValidaDuplicadoSeguimientoSURAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean result = false;
		int respuesta = 0;
		String bimestre = req.getParameter("bimestre");
		String cuil = req.getParameter("cuil");
		String inte = req.getParameter("inte");
		
		result = SeguimientoSurServiceUtil.existeSeguimientoSurPorBimestre(cuil, inte, bimestre);
		
		if(result){
			respuesta=1;
		}

		return "{ \"validado\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}
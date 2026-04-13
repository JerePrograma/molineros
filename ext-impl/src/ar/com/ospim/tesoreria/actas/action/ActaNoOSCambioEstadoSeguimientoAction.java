package ar.com.ospim.tesoreria.actas.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.service.ActaServiceUtil;

import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class ActaNoOSCambioEstadoSeguimientoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		int idEstado =Integer.parseInt(req.getParameter("id_estado"));
		int idActa = Integer.parseInt(req.getParameter("id_acta"));
		
		User user = PortalUtil.getUser(req);
		
		boolean result = ActaServiceUtil.actualizaEstadoSeguimientoActaNoOS(idActa, idEstado, user.getScreenName());
		
		String json = "{ \"result\" : \"" + String.valueOf(false) + "\"}";
		
		if(result){
			json = "{ \"result\" : \"" + String.valueOf(true) + "\"}";
		}
	
		return json;
	}
}
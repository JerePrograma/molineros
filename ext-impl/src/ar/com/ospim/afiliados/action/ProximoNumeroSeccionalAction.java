package ar.com.ospim.afiliados.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ProximoNumeroSeccionalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int provincia = ParamUtil.getInteger(req, "idProvincia");
		String tipo = ParamUtil.getString(req, "idTipo");
		Integer numero=0;	
		try {
			numero=SeccionalServiceUtil.proximoNumeroSeccional(provincia, tipo);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		if(null==numero){
			numero=0;
		}
		return "{ \"seccional\" : \""+ numero+ "\"}";
		
	}
	
}

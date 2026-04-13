package ar.com.global.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class SugerirNumeroReciboAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int entidad = ParamUtil.getInteger(req, "entidad");
		String reciboPre = ParamUtil.getString(req, "recibo_pre");
		String numero="";	
		try {
			numero=ReciboServiceUtil.getNumeroReciboSugerido(reciboPre, entidad); 
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		if(null==numero){
			numero="";
		}
		return "{ \"numero\" : \""+ numero+ "\"}";
		
	}
	
}

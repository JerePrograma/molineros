package ar.com.global.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class VerificarNumeroReciboAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int entidad = ParamUtil.getInteger(req, "entidad");
		String reciboPre = ParamUtil.getString(req, "recibo_pre");
		String numero=ParamUtil.getString(req, "recibo_numero");	
		String numeroRecibo=reciboPre+numero;
		boolean existe=false;
		try {
			List<Recibo> lista=ReciboServiceUtil.get(numeroRecibo, null, null ,null ,null ,entidad); 
			if(null!=lista && lista.size()>0){
				existe=true;
			}
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		if(null==numero){
			numero="";
		}
		return "{ \"existe\" : \""+ String.valueOf(existe)+ "\"}";
		
	}
	
}

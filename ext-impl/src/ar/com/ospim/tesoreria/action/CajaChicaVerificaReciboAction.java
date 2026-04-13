package ar.com.ospim.tesoreria.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class CajaChicaVerificaReciboAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int idCajaChica=ParamUtil.getInteger(req, "id_caja_chica");
		int idSeccional=ParamUtil.getInteger(req, "id_seccional");
		int entidad=ParamUtil.getInteger(req, "entidad");
		
		Integer resultado = 0;
		resultado = CajaChicaServiceUtil.verificaImpresionRecibo(entidad, idCajaChica, idSeccional);
		
		return "{ \"resultado\" : \"" + resultado +	"\"}";
	}

}
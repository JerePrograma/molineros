package ar.com.ospim.liquidaciones.ordenespago.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscarNroTarjetaRecargableSeccionalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		String seccional = req.getParameter("id_seccional");
		if (StringUtils.checkEmpty(seccional)) {
			seccional = "0";
		}
		String tarjeta = SeccionalServiceUtil.buscarTarjetaRecargable( Integer.parseInt(seccional));
		return "{ \"tarjeta\" : \"" + tarjeta + "\"}";
	}

}

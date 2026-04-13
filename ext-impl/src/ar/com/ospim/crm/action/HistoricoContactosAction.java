package ar.com.ospim.crm.action;

import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.services.CrmServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class HistoricoContactosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		Integer inte = ParamUtil.getInteger(renderRequest, "inte");

		int desde_dia = ParamUtil.getInteger(renderRequest, "desde_dia");
		int desde_mes = ParamUtil.getInteger(renderRequest, "desde_mes");
		int desde_anio = ParamUtil.getInteger(renderRequest, "desde_anio");
		GregorianCalendar fecha_desde = null;
		if (desde_dia != 0 && desde_anio != 0) {
			fecha_desde = new GregorianCalendar(desde_anio, desde_mes,desde_dia);
		}

		int hasta_dia = ParamUtil.getInteger(renderRequest, "hasta_dia");
		int hasta_mes = ParamUtil.getInteger(renderRequest, "hasta_mes");
		int hasta_anio = ParamUtil.getInteger(renderRequest, "hasta_anio");
		GregorianCalendar fecha_hasta = null;
		if (hasta_dia != 0 && hasta_anio != 0) {
			fecha_hasta = new GregorianCalendar(hasta_anio, hasta_mes,hasta_dia);
		}

		try {
			List<ContactoCRM> historico = null;
			
			historico = CrmServiceUtil.buscarHistoricoContactosAfi(cuil_titular, inte, fecha_desde.getTime(), fecha_hasta.getTime());
			
			renderRequest.setAttribute(WebKeysAfiliados.HISTORICO_CONTACTOS, historico);
		} catch (Exception e) {
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.historico.contacto.result.search"));
	}
}
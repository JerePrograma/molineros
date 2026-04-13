package ar.com.ospim.liquidaciones.reportes.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class BusquedaReporteOrdenesPagoAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		return mapping
				.findForward("portlet.liquidaciones.reporte.ordenes.pago.completo");
	}
}

package ar.com.ospim.liquidaciones.ordenespago.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.actions.BuscarComprobanteEmbebidoAction;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ViewOrdenesPagoAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class ViewOrdenesPagoOspimAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		Integer nro = ParamUtil.getInteger(renderRequest, "orden_pago_id");
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		session.removeAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION);
		OrdenPagoOspim op = OrdenPagoServiceUtil.getOrdenPagoOspim(nro);
		if (op.getComprobantes() != null) {
			session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
					op.getComprobantes());
			session.setAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION,
					BuscarComprobanteEmbebidoAction.sumaImportesOrden(op
							.getComprobantes()));
		}
		session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);

		return mapping
				.findForward("portlet.liquidaciones.editar_orden_pago_ospim_entry");
	}

}
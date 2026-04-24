package ar.com.ospim.liquidaciones.ordenespago.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.OpCreadaEnCanjeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReactivarOrdenPagoAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		Integer nro = ParamUtil.getInteger(renderRequest, "orden_pago_id");
		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		try {

			boolean opCreadaEnCanje = OrdenPagoServiceUtil
					.verificarOPCreadaEnCanje(nro, entidad);
			if (opCreadaEnCanje) {
				throw new OpCreadaEnCanjeException();
			}

			OrdenPagoServiceUtil.reactivar(nro, entidad);
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");

			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		if (entidad == WebKeysGlobal.UOMA) {
			return mapping.findForward("portlet.uoma.view");
		} else if (entidad == WebKeysGlobal.AMTIMA) {
			return mapping.findForward("portlet.farmacia.view");
		}else{
			return mapping.findForward("portlet.liquidaciones.view");
		}
	}

}

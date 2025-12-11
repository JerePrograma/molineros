package ar.com.ospim.liquidaciones.comprobantes.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.ComprobantesYaPagadosException;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AnularComprobanteAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		boolean borrarTotal = ParamUtil.getBoolean(renderRequest,
				"borrar_totalmente", false);
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}	
		
		Comprobante comprobanteFromRequest = EditarComprobantesAction
				.getComprobanteFromRequest(renderRequest);
		User user = PortalUtil.getUser(renderRequest);
		try {
			ComprobanteServiceUtil.anular(comprobanteFromRequest, user,
					entidad, borrarTotal);			
		} catch (ComprobantesYaPagadosException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());	 
		}catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");

			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		if (entidad==WebKeysGlobal.AMTIMA) {
			return mapping.findForward("portlet.farmacia.view");
		} else if (entidad==WebKeysGlobal.UOMA) {
			return mapping.findForward("portlet.uoma.view");
		} else {
			return mapping.findForward("portlet.liquidaciones.view");
		}
	}
}

package ar.com.uoma.recibos.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.uoma.recibos.service.ReciboNoOSServiceUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AnularReciboNoOSAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}	

		String id = renderRequest.getParameter("recibo_id");
		User user = PortalUtil.getUser(renderRequest);
		try {
			ReciboNoOSServiceUtil.anularRecibo(Integer.parseInt(id), user, entidad);
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		return mapping.findForward("portlet.estudio_isidro.view");
	}
}

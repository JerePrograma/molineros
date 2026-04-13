package ar.com.ospim.farmaciaOspim.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil;

public class CierraVademecumFarmOspimAction extends PortletAction {
	
	private static Log logger = LogFactoryUtil
			.getLog(CierraVademecumFarmOspimAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
		
		SessionErrors.clear(actionRequest);
		logger.debug("Cerrando Vademecum Farmacia...");
				
		try {
			User user = PortalUtil.getUser(actionRequest);
			int idCierreProcesado = ParamUtil.getInteger(actionRequest, "id_cierre",0); 
			FarmaciaServiceUtil.cerrarVademecum(idCierreProcesado , user);
		
					
		}  
		   catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
							
		setForward(actionRequest, "portlet.farmaciaospim.view");
		actionRequest.setAttribute("tabs1", "subir-archivo-vademecum");
	}

	
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.farmaciaospim.view"));
	}

}

package ar.com.ospim.afiliados.empleadores.action;


import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.empleadores.ImposibleBorrarEmpresaException;
import ar.com.ospim.global.services.EmpresaServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class BorrarEmpleadorAction extends EmpleadoresBaseAction {
	private static Log logger = LogFactoryUtil
			.getLog(BorrarEmpleadorAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuit = null;
		String sucu = null;

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit")
					: null;
		}
		if (null != renderRequest.getParameter("sucu")) {
			sucu = renderRequest.getParameter("sucu").trim().length() > 0 ? renderRequest
					.getParameter("sucu")
					: null;
		}
		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
		
		try {
			EmpresaServiceUtil.borrar(cuit, sucu, user);
		} catch (ImposibleBorrarEmpresaException e){
			logger.debug("No se pudo borrar empleador", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		} catch (SystemException e) {
			logger.error("No se pudo borrar empleador", e);
			return mapping.findForward("portlet.afiliados.error");
		}
		String successMessage = ParamUtil.getString(renderRequest,"successMessage");
		
		SessionMessages.add(renderRequest, "request_processed",successMessage);
		
		renderRequest.setAttribute("tabs1", "empleadores");
		
		return mapping.findForward("portlet.afiliados.view");
	}
}

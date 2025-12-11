package ar.com.ospim.estudioisidro.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarEmpresaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarEmpresaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuit = null;
		Integer lote = null;
		String razon = null;
		int ramo=0;
		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit") : null;
		}
		lote=ParamUtil.getInteger(renderRequest, "lote");
		razon=ParamUtil.getString(renderRequest, "razon");
		ramo=ParamUtil.getInteger(renderRequest, "ramo");
		
		
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		try {
			List<Empresa> empresas = EmpresaServiceUtil
					.getEmpleadoresSeguimiento(cuit,razon, lote, ramo);
			
			renderRequest.getPortletSession().removeAttribute(WebKeysEstudioIsidro.EMPRESAS_BUSCADAS, PortletSession.APPLICATION_SCOPE);
			
			renderRequest.getPortletSession().setAttribute(
					WebKeysEstudioIsidro.EMPRESAS_BUSCADAS, empresas,
					PortletSession.APPLICATION_SCOPE);

		} catch (Exception e) {
			_log.error(e);
			return mapping.findForward("portlet.estudio_isidro.error");
		}
		return mapping.findForward("portlet.estudio_isidro.empresa.result.search");

	}

}

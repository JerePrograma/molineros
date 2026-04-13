package ar.com.ospim.tesoreria.convenios.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ViewConveniosEntryAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getBancos(renderRequest);

		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		session.removeAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		String idString = renderRequest.getParameter("convenio_id");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		if(cmd.equals(Constants.UPDATE)){
			renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
		}
		Convenio convenio = ConvenioServiceUtil.getConvenio(Integer
				.parseInt(idString),0);

		if (convenio != null) {
			httpServletRequest.getSession().setAttribute(
					WebKeysTesoreria.CONVENIO_EN_EDICION, convenio);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.convenios.view_convenios_entry"));
	}
}

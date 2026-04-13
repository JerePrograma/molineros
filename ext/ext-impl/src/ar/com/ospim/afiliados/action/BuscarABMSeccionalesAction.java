package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.global.beans.Seccional;

public class BuscarABMSeccionalesAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarABMSeccionalesAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());

			int seccionalId = ParamUtil.getInteger(renderRequest,"codigo");
			int provinciaId = ParamUtil.getInteger(renderRequest,"provincia");
			String descripcion= ParamUtil.getString(renderRequest, "descripcion");
			
			
			List<Seccional> seccionales = new ArrayList<Seccional>();
			
			seccionales = SeccionalServiceUtil.buscarSeccionales(seccionalId, descripcion, provinciaId);
			
			session.removeAttribute(WebKeysAfiliados.ABM_SECCIONALES_EN_SESSION);
			session.setAttribute(WebKeysAfiliados.ABM_SECCIONALES_EN_SESSION, seccionales);
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.afiliados.seccionales.result");
	}

}
package ar.com.uoma.conveniosNoOS.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ViewConveniosNoOSEntryAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		TraeListasServiceUtil.getBancos(renderRequest);

		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		session.removeAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		String idString = renderRequest.getParameter("convenio_id");
		Convenio convenio = ConvenioNoOSServiceUtil.getConvenio(Integer
				.parseInt(idString),0, entidad);

		if (convenio != null) {
			httpServletRequest.getSession().setAttribute(
					WebKeysTesoreria.CONVENIO_EN_EDICION, convenio);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.estudio_isidro.convenios_no_os.view_convenios_entry"));
	}
}

package ar.com.cgt.ddhh.action;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.cgt.ddhh.services.OrganismoServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarAreaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarAreaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
				
		int id_area= ParamUtil.getInteger(renderRequest, "id_area");		
		
		if(id_area!=0){ //Para la edición
			Area area=OrganismoServiceUtil.getArea(id_area);
			Organismo organismo=OrganismoServiceUtil.getOrganismo(area.getId_organismo());
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,organismo);			
			portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION,area);			
			renderRequest.setAttribute("cmd", Constants.UPDATE);
			renderRequest.setAttribute("esArea",true );
			
			
		}
		return mapping.findForward("portlet.cgt_ddhh.editar_area_entry");
		

	}

	
}

package ar.com.ospim.tesoreria.reportes.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.PortletAction;

public class BusquedaReporteAportesContribucionesAction  extends PortletAction{

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {		
			return mapping.findForward(getForward(renderRequest,"portlet.tesoreria.buscar.aportes.contrib"));		
	}



}

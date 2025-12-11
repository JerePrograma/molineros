package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.services.PlanServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;



public class VerAfiAportesAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(VerAfiAportesAction.class);

	private PlanServiceUtil planService;
	
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {		
		
		setForward(actionRequest,"portlet.afiliados.ver.aportes");

	}
	
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
//		PortletSession portletSession = renderRequest.getPortletSession();
		
		String cuil_titular=ParamUtil.getString(renderRequest, "cuil_titular");	
		
		try {
			
//			List<AfiAportes> afiAportes = planService.getInstance().buscaUltimosIdsSocio(cuil_titular) ;
			List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuil_titular);

			//almaceno la lista en sesion
			renderRequest.setAttribute("IdsSocio", afiAportes); 
			
		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(renderRequest,Exception.class.getName());
		}		
		return mapping.findForward("portlet.afiliados.ver.aportes");
	}
	



}
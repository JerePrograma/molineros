package ar.com.ospim.crm.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.MotivoContacto;

/**
 * @author SVA
 * 
 */
public class ProponeCierrePredeterminadoContactoCRMAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
// redirige al render
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

	// preferi no hacer nada x el processAction...
//			System.out.println("pasando x el processAction");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		Integer idMotivo = ParamUtil.getInteger(renderRequest, "idMotivo");

//		_log.debug("buscando cierre predeterminado contacto crm : " + idMotivo);
		
		List<MotivoContacto> motivosCrm = (List<MotivoContacto>) session.getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);

		MotivoContacto mc = new MotivoContacto(idMotivo, "");
		
		String cierrePredeterminado = motivosCrm.get(motivosCrm.indexOf(mc)).getCierrePredeterminado();
		
		renderRequest.setAttribute(WebKeysCrm.CRM_CIERRE_CONTACTO_PREDET, cierrePredeterminado);		
		
		return mapping.findForward(getForward(renderRequest,"portlet.crm.contacto.cierre"));
	}

	
}

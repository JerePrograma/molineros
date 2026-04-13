package ar.com.ospim.liquidaciones.administracion.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class AdministracionPrestadoresAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {
		
			TraeListasServiceUtil.getProfesion(req);
			TraeListasServiceUtil.getEspecialidadPrestador(req);
			TraeListasServiceUtil.getSubEspecialidadPrestador(req);
			TraeListasServiceUtil.getLocalidades(req);
			TraeListasServiceUtil.getProvincias(req);
			
			// Borro las listas de matriculas y especialidades en session cuando doy de alta un nuevo prestador
//			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(req).getSession();
//			session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_SESSION);
//			session.removeAttribute(WebKeysLiquidaciones.ESPECIALIDADES_PRESTADOR_SESSION);
		
				return mapping.findForward(getForward(req,
				"portlet.liquidaciones.administracion.prestadores"));
	}
}

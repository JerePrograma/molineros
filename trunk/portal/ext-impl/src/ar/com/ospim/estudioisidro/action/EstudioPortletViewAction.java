package ar.com.ospim.estudioisidro.action;

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
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.global.services.EmpresaServiceUtil;

public class EstudioPortletViewAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EstudioPortletViewAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
//		_log.debug("Cargando listas portlet estudio view");
		
		cargarListas(session);
		
		return mapping.findForward("portlet.estudio_isidro.view");
	}
	
	private void cargarListas(HttpSession session){
		
		boolean estanPreCargadasLasListas = session.getAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION)!=null
				&& session.getAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION) !=null;
		
		if(!estanPreCargadasLasListas){
			session.setAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION, EmpresaServiceUtil.getTiposLoteEmpresa());

			session.setAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION, EmpresaServiceUtil.getEstadosEmpresa());
		}
//		no es en que momento sacarlas de memoria... pero son poquitos datos igualmete
	}

}

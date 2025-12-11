package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class BorrarRevisionReclamoAction extends PortletAction  {
	private static Log _log = LogFactoryUtil.getLog(BorrarRevisionReclamoAction.class);
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idRevision= ParamUtil.getInteger(renderRequest, "idRevision");
		RevisionesReclamo revision = new RevisionesReclamo();
		revision.setId(idRevision);   				
		_log.debug("Borrando reclamo id: " + idRevision);		
		List<RevisionesReclamo> listaRevisionesReclamo = (ArrayList<RevisionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION );
		try {
			
		
		int pos = listaRevisionesReclamo.indexOf(revision);  
//		reemplazo por el objeto de la lista
		revision= listaRevisionesReclamo.get(pos);
		if(revision.getEstado()==null){ // esta prestacion esta en BD
			revision.setEstado(RevisionesReclamo.ESTADOS.BAJA);
		}else{
			listaRevisionesReclamo.remove(pos);
		}	
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION);
		session.setAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION, listaRevisionesReclamo );
		}
		catch (Exception e) {
			_log.error("Error borrando revision ", e);	
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.autorizaciones.reclamosprestacionales.revision.reclamo"));
			                            	                            
	}
	
}

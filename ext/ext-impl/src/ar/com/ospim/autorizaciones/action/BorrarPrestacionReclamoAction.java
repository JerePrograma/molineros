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

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;


public class BorrarPrestacionReclamoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarPrestacionReclamoAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		

		
//		int idPrestacion= ParamUtil.getInteger(renderRequest, "idPrestacion");
		int idRegistro= ParamUtil.getInteger(renderRequest, "idRegistro");
		PrestacionesReclamo presta  = new PrestacionesReclamo();
		presta.setIdRegistro(idRegistro);  				
		_log.debug("Borrando prestacion id: " + idRegistro);		
		List<PrestacionesReclamo> listaPrestacionesReclamo = (ArrayList<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION );
		try {
			
		
		int pos = listaPrestacionesReclamo.indexOf(presta);  
//		reemplazo por el objeto de la lista
		presta= listaPrestacionesReclamo.get(pos);
		if(presta.getEstado()==null){ // esta prestacion esta en BD
			presta.setEstado(PrestacionesReclamo.ESTADOS.BAJA);
		}else{
			listaPrestacionesReclamo.remove(pos);
		}	
		session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
		session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION, listaPrestacionesReclamo);
		}
		catch (Exception e) {
			_log.error("Error borrando prestacion", e);	
		}
		
		if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmd)){			
			return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo_seccional");
		}else{			
			return mapping.findForward("portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo");
		}
	

	}
	
}

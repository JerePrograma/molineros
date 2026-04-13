package ar.com.ospim.seccional.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.seccional.beans.GestionSeccional;
import ar.com.ospim.seccional.beans.WebKeysSeccionales;

public class GestionesSeccionalAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(GestionesSeccionalAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
         
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		_log.debug(" Entrando carga de gestion sec y refrescar lista ");

		User user = PortalUtil.getUser(renderRequest);
		
		Integer idSeccional = ParamUtil.getInteger(renderRequest, "id_seccional");

		String observaciones = ParamUtil.getString(renderRequest, "gestion_observaciones");
		
		String fechaFinal = ParamUtil.getString(renderRequest,"fecha_final", null);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		try {
			fecha = sdf.parse(fechaFinal);
		} catch (Exception e) {
			fecha = null;
		}
		
		int idGestSec=0;
		
		GestionSeccional gs = new GestionSeccional();
		gs.setSeccional(new Seccional(idSeccional));
		gs.setObservaciones(observaciones);
		gs.setFecha(fecha);
		
		idGestSec = SeccionalServiceUtil.insertarGestionSeccional(gs, user.getScreenName());
		
//		gs.setId(idGestSec); // al pedo?
		
		renderRequest.removeAttribute(WebKeysSeccionales.GESTIONES);
		
		List<GestionSeccional> gestiones = SeccionalServiceUtil.buscarGestionesxSeccional(idSeccional);
		renderRequest.setAttribute(WebKeysSeccionales.GESTIONES, gestiones);
		
		_log.debug(" Finalizando carga de gestion sec y refrescar lista ");

		return mapping.findForward("portlet.gestion_seccional.results");
	}

}
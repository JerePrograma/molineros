package ar.com.cgt.ddhh.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.LineaTrabajo;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class SacarLineaOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarLineaOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando linea a Organismo");

		PortletSession portletSession =  renderRequest.getPortletSession();
		
		
		Boolean esArea = ParamUtil.getBoolean(renderRequest, "isArea");
		List<LineaTrabajo> list = null;

		if(esArea){
			Area area = (Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			list = area.getLineasTrabajo();
			
		}else{
			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			list = organismo.getLineasTrabajo();
		}
		if (list == null) {
			list = new ArrayList<LineaTrabajo>();
		}

		String id_linea = ParamUtil.getString(renderRequest,"id_linea");
		
		LineaTrabajo ap = new LineaTrabajo();

		try {

			ap.setDescripcion(id_linea);

			removeLineaTrabajoFromList(list, ap);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		
		renderRequest.setAttribute("esArea",esArea );

		logger.debug("Saliendo de sacar linea a organismo");
		return mapping.findForward("portlet.cgt_ddhh.agregar_linea");
	}

	private void removeLineaTrabajoFromList(List<LineaTrabajo> list, LineaTrabajo ap) {
		Iterator<LineaTrabajo> it = list.iterator();
		while (it.hasNext()) {
			LineaTrabajo aLineaEnLista = it.next();
			if (ap.getDescripcion() != null && aLineaEnLista.getDescripcion().trim().equals(ap.getDescripcion().trim())) {
				it.remove();				
			}
		}
	}
	
}

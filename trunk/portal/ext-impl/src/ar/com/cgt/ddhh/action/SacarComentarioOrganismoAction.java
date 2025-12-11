package ar.com.cgt.ddhh.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ar.com.cgt.ddhh.beans.Comentario;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class SacarComentarioOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarComentarioOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando linea a Organismo");
		Boolean esArea = ParamUtil.getBoolean(renderRequest, "isArea");
		PortletSession portletSession = renderRequest.getPortletSession();
		List<Comentario> list = null;
		
		if (esArea) {
			Area area = (Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			list = area.getComentario();
		} else {
			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			list = organismo.getComentario();			
		}
		
		if (list == null) {
			list = new ArrayList<Comentario>();
		}
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		String fecha_str = ParamUtil.getString(renderRequest, "fecha");
		String comentario = ParamUtil.getString(renderRequest, "comentario");

		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fecha_str);
		} catch (Exception e) {
			fecha = null;
		}

		Comentario ap = new Comentario();

		try {

			ap.setFecha(fecha);
			ap.setDescripcion(comentario);

			removeComentarioFromList(list, ap);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		
		renderRequest.setAttribute("esArea",esArea );
		logger.debug("Saliendo de sacar linea a organismo");
		return mapping.findForward("portlet.cgt_ddhh.agregar_comentario");
	}

	private void removeComentarioFromList(List<Comentario> list, Comentario ap) {
		Iterator<Comentario> it = list.iterator();
		while (it.hasNext()) {
			Comentario aComentarioEnLista = it.next();
			if (null != ap.getFecha()
					&& aComentarioEnLista.getFecha().equals(ap.getFecha())) {
				if (ap.getDescripcion()
						.trim()
						.toUpperCase()
						.equals(aComentarioEnLista.getDescripcion().trim()
								.toUpperCase()))
					it.remove();
			}
		}
	}

}

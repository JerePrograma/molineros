package ar.com.global.action;

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

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Contenido;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BorrarContenidoBoletinAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(BorrarContenidoBoletinAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("borrando contenido a boletin");

		PortletSession portletSession = renderRequest.getPortletSession();

		List<Contenido> contenidos = null;

		Boletin boletin = (Boletin) portletSession
				.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);

		if (boletin != null) {
			contenidos = boletin.getListaContenidos();
		}

		if (contenidos == null) {
			contenidos = new ArrayList<Contenido>();
		}

		String titulo = ParamUtil.getString(renderRequest, "titulo");
				
		try {						
			removeContenidoFromList(contenidos, titulo);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de sacar contenido a boletin");
		return mapping.findForward("portlet.global.contenido_search_result");
	}

	private void removeContenidoFromList(List<Contenido> list, String titulo) {
		Iterator<Contenido> it = list.iterator();
		while (it.hasNext()) {
			Contenido aContenidoEnLista = it.next();
			if (titulo.trim()
					.toUpperCase()
					.equals(aContenidoEnLista.getTitulo().trim()
							.toUpperCase())) {				
					it.remove();	
			}

		}
	}

}

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
import ar.com.cgt.ddhh.beans.Contacto;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class SacarContactoOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarContactoOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando linea a Organismo");

		PortletSession portletSession = renderRequest.getPortletSession();
		boolean esArea = ParamUtil.getBoolean(renderRequest, "isArea");
		List<Contacto> list = null;

		if (esArea) {
			Area area = (Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			list = area.getContactos();
		} else {
			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			list = organismo.getContactos();
		}
		if (list == null) {
			list = new ArrayList<Contacto>();
		}

		String id_cargo = ParamUtil.getString(renderRequest, "id_cargo");
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String apellido = ParamUtil.getString(renderRequest, "apellido");

		Contacto ap = new Contacto();

		try {

			ap.setCargo(id_cargo);
			ap.setNombre(nombre);
			ap.setApellido(apellido);
			removeContactoFromList(list, ap);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		renderRequest.setAttribute("esArea",esArea );
		logger.debug("Saliendo de sacar linea a organismo");
		return mapping.findForward("portlet.cgt_ddhh.agregar_contacto");
	}

	private void removeContactoFromList(List<Contacto> list, Contacto ap) {
		Iterator<Contacto> it = list.iterator();
		while (it.hasNext()) {
			Contacto aContactoEnLista = it.next();
			if (ap.getCargo() != null
					&& aContactoEnLista.getCargo().trim()
							.equals(ap.getCargo().trim())) {
				if (ap.getNombre()
						.trim()
						.toUpperCase()
						.equals(aContactoEnLista.getNombre().trim()
								.toUpperCase())) {
					if (ap.getApellido()
							.trim()
							.toUpperCase()
							.equals(aContactoEnLista.getApellido().trim()
									.toUpperCase())) {
						it.remove();
					}
				}
			}
		}
	}

}

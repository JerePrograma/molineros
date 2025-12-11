package ar.com.global.action;

import java.text.ParseException;
import java.util.ArrayList;
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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarContenidoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarContenidoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando contenido a boletín");

		PortletSession portletSession = renderRequest.getPortletSession();
		
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		
		Boletin boletin = (Boletin) portletSession.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);
		if (boletin == null) {
			boletin = new Boletin();
		}
		List<Contenido> contenidos;
		contenidos = boletin.getListaContenidos();
		
		if (contenidos == null) {
			contenidos = new ArrayList<Contenido>();
		}
		try {
			contenidos.add(getContenidos(renderRequest));
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		boletin.setListaContenidos(contenidos);
		portletSession.setAttribute(WebKeysGlobal.BOLETIN_EN_EDICION, boletin);

		return mapping.findForward("portlet.global.contenido_search_result");

	}

	private Contenido getContenidos(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Contenido contenido= new Contenido();
		String seccion=ParamUtil.getString(renderRequest, "seccion");
		String titulo = ParamUtil.getString(renderRequest, "titulo_contenido");
		String contenido_texto = ParamUtil.getString(renderRequest, "contenido");
		
		contenido.setTitulo(titulo);
		contenido.setContenido(contenido_texto);
		contenido.setSeccion(seccion);
		
		return contenido;
	}

}

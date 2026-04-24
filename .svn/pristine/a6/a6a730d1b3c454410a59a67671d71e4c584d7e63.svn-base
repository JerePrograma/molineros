package ar.com.ospim.tesoreria.actas.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.DetalleActaInspectores;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarDetalleActaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SacarDetalleActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		String accionOriginal = renderRequest.getParameter("accionOriginal");
		if (accionOriginal != null) {
			renderRequest.setAttribute("accionOriginal", accionOriginal);
		}
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		List<Acta.DetalleActaInspectores> lista = acta.getDetallesActas();
		if (lista == null) {
			lista = new ArrayList<Acta.DetalleActaInspectores>();
			acta.setDetallesActas(lista);
		}

		String id = renderRequest.getParameter("id");
		DetalleActaInspectores detalle = new DetalleActaInspectores();
		detalle.setId(Integer.parseInt(id));
		if (Integer.parseInt(id) >0) {
			detalle = lista.get(lista.indexOf(detalle));
			detalle.setBorradoLogico(true);
		} else {
			lista.remove(detalle);
		}

		acta.setCapital(acta.getCapitalFromDetalle());
		acta.setInteres(acta.getInteresFromDetalle());
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.actas.detalle.acta.search.result");
	}

}

package ar.com.ospim.tesoreria.convenios.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ActaRelacionada;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarActaRelacionadaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SacarActaRelacionadaAction.class);

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
		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		List<ActaRelacionada> lista = convenio.getActasRelacionadas();
		if (lista == null) {
			lista = new ArrayList<ActaRelacionada>();
			convenio.setActasRelacionadas(lista);
		}

		String id = renderRequest.getParameter("acta_asociada_id");
		int idInt = Integer.parseInt(id);
		Iterator<ActaRelacionada> it = lista.iterator();
		while (it.hasNext()) {
			ActaRelacionada actaR = it.next();
			if (actaR.getActaRelacionada().getId() == idInt) {
				if (actaR.getId() == 0) {
					it.remove();
				} else {
					actaR.setBorradoLogico(true);
				}
				break;
			}
		}

		renderRequest.setAttribute(WebKeysTesoreria.CONVENIOS_ACTION_EDICION,
				WebKeysTesoreria.CONVENIOS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.convenios.acta.relacionada.search.result");
	}

}

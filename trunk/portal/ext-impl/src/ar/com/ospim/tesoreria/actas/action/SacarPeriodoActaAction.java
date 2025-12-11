package ar.com.ospim.tesoreria.actas.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarPeriodoActaAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(SacarPeriodoActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
		if (peris == null) {
			peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
		}
		String todos = renderRequest.getParameter("todos");
		if (todos != null && todos.trim().equals("todos")) {
			Iterator<ActaPeriodoDeudaEmpresa> it = peris.iterator();
			while (it.hasNext()) {
				ActaPeriodoDeudaEmpresa peri = it.next();
				if (peri.getDetalle().get(0).getId() <= 0) {
					it.remove();
				} else {
					peri.setBorradoLogico(true);
				}
			}
		} else {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaStr = renderRequest.getParameter("fecha");
			fechaStr = "01-" + fechaStr;
			Date fecha = format.parse(fechaStr);
			Iterator<ActaPeriodoDeudaEmpresa> it = peris.iterator();
			while (it.hasNext()) {
				ActaPeriodoDeudaEmpresa peri = it.next();
				if (peri.getPeriodo().equals(fecha)) {
					if (peri.getDetalle().get(0).getId() <= 0) {
						it.remove();
					} else {
						peri.setBorradoLogico(true);
					}
				}
			}
		}
		acta.setPeriodos(peris);
		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		return mapping.findForward("portlet.tesoreria.actas.periodos.view");
	}

}

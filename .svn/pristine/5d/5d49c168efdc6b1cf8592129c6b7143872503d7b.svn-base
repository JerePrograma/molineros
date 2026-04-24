package ar.com.uoma.recibos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMReciboNoOSOtrosConceptosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboNoOSOtrosConceptosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
		}

		if (recibo.getOtrosConceptos() == null) {
			recibo.setOtrosConceptos(new ArrayList<ReciboOtroConcepto>());
		}

		String borrar = renderRequest.getParameter("borrar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarConcepto(renderRequest, recibo);
		} else {
			agregarConcepto(renderRequest, recibo);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		return mapping
				.findForward("portlet.estudio_isidro.recibos_no_os.otros_conceptos.result.search");
	}

	private void agregarConcepto(RenderRequest renderRequest, Recibo recibo) {
		String conceptoId = renderRequest.getParameter("concepto_id");
		String importe = renderRequest.getParameter("importe");

		ReciboOtroConcepto oc = new ReciboOtroConcepto(new Concepto(Integer
				.parseInt(conceptoId)), new BigDecimal(importe));

		int id = getMenorId(recibo.getOtrosConceptos());
		oc.setId(--id);
		recibo.getOtrosConceptos().add(oc);
	}

	private int getMenorId(List<ReciboOtroConcepto> otrosConceptos) {
		int id = -1;
		for (ReciboOtroConcepto oc : otrosConceptos) {
			if (oc.getId() < id) {
				id = oc.getId();
			}
		}
		return id;
	}

	private void borrarConcepto(RenderRequest renderRequest, Recibo recibo) {
		String id = renderRequest.getParameter("oc_id");
		recibo.getOtrosConceptos().remove(
				new ReciboOtroConcepto(Integer.parseInt(id)));

	}

}

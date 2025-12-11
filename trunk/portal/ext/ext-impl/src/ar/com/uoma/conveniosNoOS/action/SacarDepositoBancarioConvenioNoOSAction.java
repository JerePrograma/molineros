package ar.com.uoma.conveniosNoOS.action;

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
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarDepositoBancarioConvenioNoOSAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarDepositoBancarioConvenioNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando depo banc a convenio");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);

		List<ConvenioPago> list = convenio.getPagos();
		if (list == null) {
			list = new ArrayList<ConvenioPago>();
		}

		String cuotaNro = renderRequest.getParameter("cuota_nro_cta_bcria");
		ConvenioPago ap = new ConvenioPago();
		ap.setNroCuota(Integer.parseInt(cuotaNro));
		removeConvenioPagoFromList(list, ap);

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de sacar depo banc a convenio");
		return mapping.findForward("portlet.tesoreria.convenios.depositos.bcrios.view");
	}

	private void removeConvenioPagoFromList(List<ConvenioPago> list,
			ConvenioPago ap) {
		Iterator<ConvenioPago> it = list.iterator();
		while (it.hasNext()) {
			ConvenioPago aPagoEnLista = it.next();
			if (aPagoEnLista.getNroCuota() == ap.getNroCuota()) {
				if (ap.getId() != 0) {
					ap.setBorradoLogico(true);
				} else {
					it.remove();
				}
				break;
			}
		}
	}
}
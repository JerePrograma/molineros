package ar.com.uoma.conveniosNoOS.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarChequeConvenioNoOSAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarChequeConvenioNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando cheque a convenio no os");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);

		List<ConvenioPago> list = convenio.getPagos();
		if (list == null) {
			list = new ArrayList<ConvenioPago>();
		}

		String nroCheque = renderRequest.getParameter("cheque_nro");
		String idBanco = renderRequest.getParameter("id_banco");
		String idCtaBancaria = renderRequest.getParameter("idCtaBcria");
		String cuitEmisor = renderRequest.getParameter("cuitEmisor");
		String idConvPago = renderRequest.getParameter("id_convenio_pago");
		if (!StringUtils.isBlank(nroCheque) && !StringUtils.isBlank(idBanco)) {
			Banco b = new Banco(Integer.parseInt(idBanco),"");
			CuentaBancaria cb = new CuentaBancaria(Integer.parseInt(idCtaBancaria), "");
			cb.setBanco(b);
			Cheque cheque = new Cheque();
			cheque.setNumero(new BigDecimal(nroCheque));
			cheque.setCuentaBancaria(cb);
			cheque.setCuit(cuitEmisor);
			
			ConvenioPago ap = new ConvenioPago();
			ap.setCheque(cheque);
			if (!StringUtils.isBlank(idConvPago)) {
				ap.setId(Integer.parseInt(idConvPago));
			}
			removeConvenioPagoFromList(list, ap);
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de sacar cheque a convenio no os");
		return mapping.findForward("portlet.tesoreria.convenios.cheques.view");
	}

	private void removeConvenioPagoFromList(List<ConvenioPago> list,
			ConvenioPago ap) {
		Iterator<ConvenioPago> it = list.iterator();
		while (it.hasNext()) {
			ConvenioPago aPagoEnLista = it.next();
			if (aPagoEnLista.getCheque() != null
					&& aPagoEnLista.getCheque().equals(ap.getCheque())) {
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
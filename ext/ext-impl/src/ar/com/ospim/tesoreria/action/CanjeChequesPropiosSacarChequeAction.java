package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CanjeChequesPropiosSacarChequeAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
				
		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
				.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
		List<Cheque> list = canjeChequePropio.getChequesNuevos();

		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		String nro = renderRequest.getParameter("nro");

		CuentaBancaria cta = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		int indexOf = ctasBcrias.indexOf(cta);
		if (indexOf != -1) {
			cta = ctasBcrias.get(indexOf);
		}

		Cheque cheque = new Cheque(new BigDecimal(nro), cta.getBanco()
				.getId_banco());
		list.remove(cheque);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.canje.cheques.propios.cheques.result"));
	}
}

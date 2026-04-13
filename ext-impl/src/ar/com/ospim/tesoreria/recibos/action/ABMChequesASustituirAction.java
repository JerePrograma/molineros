package ar.com.ospim.tesoreria.recibos.action;

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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboCheque;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMChequesASustituirAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMChequesASustituirAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
			session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		}

		if (recibo.getChequesNoDepositados() == null) {
			recibo.setChequesNoDepositados(new ArrayList<ReciboCheque>());
		}

		String borrar = renderRequest.getParameter("borrar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarCheque(renderRequest, recibo);
		} else {
			agregarCheque(renderRequest, recibo, entidad);
		}

		return mapping
				.findForward("portlet.tesoreria.recibos.cheques_a_sustituir.result.search");
	}

	private void borrarCheque(RenderRequest renderRequest, Recibo recibo) {
		String nro = renderRequest.getParameter("cheque_nro");
		String idBanco = renderRequest.getParameter("id_banco");
		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		String cuitEmisor = renderRequest.getParameter("cuit_emisor"); 
		
		CuentaBancaria cb = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		Banco b = new Banco(Integer.parseInt(idBanco));
		cb.setBanco(b);
		Cheque ch = new Cheque();
		ch.setCuit(cuitEmisor);
		ch.setNumero(new BigDecimal(nro));
		ch.setCuentaBancaria(cb);
		
		recibo.getChequesNoDepositados().remove(new ReciboCheque(ch));

	}

	private void agregarCheque(RenderRequest renderRequest, Recibo recibo, int entidad)
			throws SystemException {
		String cuit = ParamUtil.getString(renderRequest, "cuit");

		
		List<Cheque> cheques = ChequeServiceUtil.getChequesRecibidos(cuit, entidad);
		if (cheques != null) {
			for (Cheque ch : cheques) {
				ch.setEstado(TraeListasServiceUtil
						.getEstadoChequeSustituido(renderRequest));
				ReciboCheque reciboCheque = new ReciboCheque(ch);
				if (!recibo.getChequesNoDepositados().contains(reciboCheque)) {
					recibo.getChequesNoDepositados().add(reciboCheque);
				}
			}
		}
	}

}
package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CanjeChequesPropiosAgregarChequeAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
				.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);
		List<Cheque> list = null;
		list = canjeChequePropio.getChequesNuevos();
		if (list == null) {
			list = new ArrayList<Cheque>();
			canjeChequePropio.setChequesNuevos(list);
		}

		Cheque cheque = getCheque(renderRequest, ctasBcrias);
		try {
			if (!list.contains(cheque)) {
				if (chequeExistente(cheque, entidad)) {
					throw new DuplicateNumeroChequeException(cheque);
				}
				list.add(cheque);
			}
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.canje.cheques.propios.cheques.result"));
	}

	private Cheque getCheque(RenderRequest renderRequest,
			List<CuentaBancaria> ctasBcrias) {
		String aFavorDe = StringUtils.getValueOrNull(renderRequest
				.getParameter("aFavorDe"));
		String importe = renderRequest.getParameter("importe_pago");
		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		String nro = renderRequest.getParameter("nro");

		BigDecimal importeBigD = null;
		if (StringUtils.checkNotEmpty(importe)) {
			importeBigD = new BigDecimal(importe);
		}

		CuentaBancaria cta = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		Banco b = null;
		int indexOf = ctasBcrias.indexOf(cta);
		if (indexOf != -1) {
			cta = ctasBcrias.get(indexOf);
		}
		b = new Banco(cta.getBanco().getId_banco());
		cta.setBanco(b);
		
		Cheque cheque = new Cheque();
		cheque.setNumero(new BigDecimal(nro));
		cheque.setImporte(importeBigD);
		cheque.setEstado(TraeListasServiceUtil
				.getEstadoChequeEmitido(renderRequest));
		cheque.setDebitoCredito(Cheque.Tipo.DEBITO);
		cheque.setCuentaBancaria(cta);
		cheque.setANombreDe(aFavorDe);
		cheque.setFecha(new Date());

		return cheque;
	}

	private boolean chequeExistente(Cheque cheque, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
//		List<Cheque> cheques = ChequeServiceUtil.getCheques(cheque, entidad);
		Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, WebKeysGlobal.OSPIM);
//		return cheques != null && cheques.size() > 0;
		return chequeDuplicado != null;	
	}

}

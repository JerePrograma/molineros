package ar.com.ospim.tesoreria.action;

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
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.OrdenPagoAnuladaException;
import ar.com.ospim.tesoreria.OrdenPagoInexistenteException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio.ChequeACanjear;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CanjeChequesPropiosBuscarOPAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(CanjeChequesPropiosBuscarOPAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
				.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);

		if (canjeChequePropio.getChequesViejos() == null) {
			canjeChequePropio.setChequesViejos(new ArrayList<ChequeACanjear>());
		}
		canjeChequePropio.getChequesViejos().clear();

		Integer nro = ParamUtil.getInteger(renderRequest, "nro");
		try {
			OrdenPago op=null;
			if(entidad!=WebKeysGlobal.OSPIM){
				op = OrdenPagoServiceUtil.getOrdenPago(nro, entidad);
			}else{
				op = OrdenPagoServiceUtil.getOrdenPagoOspim(nro);
			}

			if (op == null) {
				throw new OrdenPagoInexistenteException();
			} else if (op.getBaja_fecha() != null) {
				throw new OrdenPagoAnuladaException();
			}

			canjeChequePropio.getChequesViejos().addAll(
					getChequeACanjear(op.getSoloCheques(), ctasBcrias));

		} catch (OrdenPagoInexistenteException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (OrdenPagoAnuladaException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (Exception e) {
			logger.error("Error al buscar OP para canje de cheque", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.canje.cheques.propios.op.result"));
	}

	private List<ChequeACanjear> getChequeACanjear(
			List<Cheque> chequesParaReutilizar, List<CuentaBancaria> ctasBcrias) {
		List<ChequeACanjear> cac = new ArrayList<CanjeChequePropio.ChequeACanjear>();
		for (Cheque cheque : chequesParaReutilizar) {
//			int indexOf = ctasBcrias.indexOf(cheque.getCuentaBancaria());
//			cheque.setBanco(ctasBcrias.get(indexOf).getBanco());
			cac.add(new ChequeACanjear(cheque));
		}
		return cac;
	}
}

package ar.com.ospim.liquidaciones.ordenespago.action;

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
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ReutilizarChequesOrdenesPagoAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}	
		
		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		OrdenPago ordenPago = (OrdenPago) session
				.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

		if (ordenPago == null) {
			if (entidad==WebKeysGlobal.AMTIMA) {
				ordenPago = new OrdenPagoAmtima();
			} else if (entidad==WebKeysGlobal.UOMA) {
				
			} else{
				ordenPago = new OrdenPagoOspim();
			}
			session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
					ordenPago);
		}

		if (ordenPago.getFormaPago() == null) {
			ordenPago.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
		}
		List<Cheque> chequesReutilizables = null;
		
		chequesReutilizables = ChequeServiceUtil.getChequesReutilizables(entidad);
		
		for (OrdenPago.FormaPago fp : ordenPago.getFormaPago()) {
			chequesReutilizables.remove(fp.getPago());
		}

		String accion = ParamUtil.getString(renderRequest, "accion", "");
		if (accion.equals("reutilizar")) {
			int cantidad_cheques = ParamUtil.getInteger(renderRequest,
					"cantidad_cheques");
			List<Cheque> chequesReutilizar = getChequesParaReutilizar(
					renderRequest, ctasBcrias, cantidad_cheques);
			for (Cheque cheque : chequesReutilizar) {
				int indexOf = chequesReutilizables.indexOf(cheque);
				if (!ordenPago.getFormaPago().contains(
						new OrdenPago.FormaPago(cheque))) {
					ordenPago.getFormaPago().add(
							new OrdenPago.FormaPago(chequesReutilizables
									.get(indexOf), false));
				}
			}
		}

		renderRequest.setAttribute(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES,
				chequesReutilizables);

		if(!renderResponse.getNamespace().equals("_TES_1_")){
		   return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.ordenes_pago.pagos.cheques"));
		}else{
		   return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.ordenes_pago.pagos.cheques"));
		}   
	}

	private List<Cheque> getChequesParaReutilizar(RenderRequest renderRequest,
			List<CuentaBancaria> ctasBcrias, int cantidad_cheques) {
		List<Cheque> cheques = new ArrayList<Cheque>();
		String key = "utilizar_cheque_";
		for (int i = 0; i < cantidad_cheques; i++) {
			if (renderRequest.getParameter(key + i) != null) {
				String aBorrar = renderRequest.getParameter(key + i);
				String numeroCheque = aBorrar.substring(16,
						aBorrar.indexOf("_", 16));
				String idCta = aBorrar.substring(aBorrar.indexOf("_", 16) + 1,
						aBorrar.length());
				int indexOf = ctasBcrias.indexOf(new CuentaBancaria(Integer
						.valueOf(idCta)));
//				cheques.add(new Cheque(new BigDecimal(numeroCheque), ctasBcrias
//						.get(indexOf).getBanco().getId_banco()));
				
				cheques.add(new Cheque(null, new BigDecimal(numeroCheque),ctasBcrias.get(indexOf),
						ctasBcrias.get(indexOf).getBanco()));
				
			}
		}
		return cheques;
	}

}

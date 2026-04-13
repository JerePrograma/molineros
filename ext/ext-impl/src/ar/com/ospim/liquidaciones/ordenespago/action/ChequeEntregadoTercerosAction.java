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
import ar.com.ospim.global.beans.Banco;
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

public class ChequeEntregadoTercerosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		OrdenPago ordenPago = (OrdenPago) session
				.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

		if (ordenPago == null) {
			if (entidad == WebKeysGlobal.AMTIMA) {
				ordenPago = new OrdenPagoAmtima();
			} else if (entidad == WebKeysGlobal.UOMA) {

			} else {
				ordenPago = new OrdenPagoOspim();
			}
			session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
					ordenPago);
		}

		if (ordenPago.getFormaPago() == null) {
			ordenPago.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
		}
		List<Cheque> chequesCartera = null;

		chequesCartera = ChequeServiceUtil.getChequesRecibidos(entidad);

		for (OrdenPago.FormaPago fp : ordenPago.getFormaPago()) {
			chequesCartera.remove(fp.getPago());
		}

		Cheque chequeAReutilizar = getChequesCartera(renderRequest);

		int indexOf = chequesCartera.indexOf(chequeAReutilizar);
		if (!ordenPago.getFormaPago().contains(
				new OrdenPago.FormaPago(chequeAReutilizar))) {
			Cheque chequeCa=chequesCartera.get(indexOf);
			chequeCa.setCuentaBancaria(new CuentaBancaria(99));
			chequeCa.setBanco(chequeAReutilizar.getBanco());
			chequeCa.setCuit(chequeAReutilizar.getCuit());
			ordenPago.getFormaPago()
					.add(new OrdenPago.FormaPago(chequeCa));							
		}

		renderRequest.setAttribute(WebKeysLiquidaciones.CHEQUES_CARTERA,
				chequesCartera);

		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.ordenes_pago.pagos.cheques"));
	}

	/*
	 * else if
	 * (tipo_ingreso.equals(Cheque.class.getName()+Cheque.Estado.RECIBIDO)) {
	 * Cheque cheque = new Cheque(new BigDecimal(nro), cta.getBanco()
	 * .getId_banco());
	 * 
	 * cheque.setImporte(importeBigD); cheque.setEstado(TraeListasServiceUtil
	 * .getEstadoChequeEntregadoTerceros(renderRequest));
	 * cheque.setDebitoCredito(Cheque.Tipo.CREDITO); if (!list.contains(new
	 * OrdenPago.FormaPago(cheque))) { list.add(new
	 * OrdenPago.FormaPago(cheque)); } }
	 */

	private Cheque getChequesCartera(RenderRequest renderRequest)
			throws Exception {
		Cheque cheque = new Cheque();
		List<Banco> bancos = TraeListasServiceUtil.getBancos();
		String idCheque = ParamUtil.getString(renderRequest, "idCheque");
		if (null != idCheque && !idCheque.equals("")) {
			String[] partesCheque = idCheque.split("\\|");
			cheque.setNumero(new BigDecimal(partesCheque[0]));
			//cheque.setCuentaBancaria(new CuentaBancaria(99));
			int idBanco = Integer.parseInt(partesCheque[1]);
			Integer idCtaBcria= Integer.parseInt(partesCheque[2]);
			String cuit=partesCheque[3];
			cheque.setCuit(cuit);
			cheque.setCuentaBancaria(new CuentaBancaria(idCtaBcria));
			for (Banco bco : bancos) {
				if (bco.getId_banco() == idBanco) {
					cheque.setBanco(bco);
				}
			}
		}
		return cheque;
	}

}

package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Tipo;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ChequeSinChequeraException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import edu.emory.mathcs.backport.java.util.Collections;

public class SalvarUploadOrdenPagoAction extends PortletAction {

	@SuppressWarnings("unchecked")
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}


		User user = PortalUtil.getUser(actionRequest);
		List<OrdenPago> ordenes = (ArrayList<OrdenPago>) PortalUtil
				.getHttpServletRequest(actionRequest).getSession()
				.getAttribute(WebKeysLiquidaciones.ORDENES_PAGO);

		Collections.sort(ordenes, new Comparator<OrdenPagoAmtima>() {
			public int compare(OrdenPagoAmtima o1, OrdenPagoAmtima o2) {
				if (o1.getItems().isEmpty() || o2.getItems().isEmpty()) {
					return 0;
				}
				ItemOrdenPago item1 = o1.getItems().get(0);
				ItemOrdenPago item2 = o2.getItems().get(0);
				if (item1.getPeriodo().equals(item2.getPeriodo())) {
					return item1.getCodigoPrestador().compareTo(
							item2.getCodigoPrestador());
				} else {
					return item1.getPeriodo().compareTo(item2.getPeriodo());
				}

			}
		});

		try {
			if (ordenes != null) {

				for (OrdenPago opGenerica : ordenes) {
					OrdenPago op =  opGenerica;
					String nro = ParamUtil.getString(actionRequest,
							"cheque_nro_" + op.getId());
					String dcto = ParamUtil.getString(actionRequest, "dcto_"
							+ op.getId());
					String dctoDrog = ParamUtil.getString(actionRequest,
							"dcto_drog_" + op.getId());
					op.setDescuento(new BigDecimal(dcto));
					op.setDescuentoDrogueria(new BigDecimal(dctoDrog));
					if (op.getFormaPago() == null) {
						op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
					}
					if (op.getFormaPago().size() == 0) {
						op.getFormaPago().add(
								new OrdenPago.FormaPago(new Cheque()));
					}
					Cheque cheque = (Cheque) op.getFormaPago().get(0).getPago();
					cheque.setNumero(new BigDecimal(nro));
					cheque.setImporte(op.getImporteDeItemsConDescuento());
					cheque.setCuentaBancaria(new CuentaBancaria(1));
					cheque.setBanco(new Banco(1, ""));
					cheque.setFecha(new Date());
					cheque.setDebitoCredito(Tipo.DEBITO);
					cheque.setEstado(TraeListasServiceUtil
							.getEstadoChequeEmitido(actionRequest));
					op.setImporte(op.getImporteDeItemsConDescuento());

				}
				OrdenPagoServiceUtil.save(ordenes, user, entidad);
			}
		} catch (DuplicateNumeroChequeException e) {
			if (e.getCheque() != null) {
				actionRequest.setAttribute("Cheques_Duplicados", e.getCheque());
			}
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (ChequeSinChequeraException e) {
			if (e.getCheque() != null) {
				actionRequest.setAttribute("Cheques_Duplicados", e.getCheque());
			}
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.removeAttribute(WebKeysLiquidaciones.ORDENES_PAGO);
			setForward(actionRequest, "portlet.farmacia.view");

			actionRequest.setAttribute("ordenIniId", ordenes.get(0).getId());
			actionRequest.setAttribute("ordenFinId",
					ordenes.get(ordenes.size() - 1).getId());
		} else {
			int i = -1;
			for (OrdenPago op : ordenes) {
				op.setId(i);
				i--;
			}
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest,
				"portlet.farmacia.editar_uploaded_orden_pago_entry"));
	}

}

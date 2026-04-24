package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.OpConChequesCanjeadosException;
import ar.com.ospim.tesoreria.OpCreadaEnCanjeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AnularOrdenPagoAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(AnularOrdenPagoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		String accion = ParamUtil.getString(renderRequest, "accion", "");
		Integer nro = ParamUtil.getInteger(renderRequest, "orden_pago_id");
		boolean success = false;
		try {
			if (accion.equals("borrar")) {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String fechaBajaDia = ParamUtil.getString(renderRequest,
						"fechaBajaDia");
				String fechaBajaMes = ParamUtil.getString(renderRequest,
						"fechaBajaMes");
				fechaBajaMes = String
						.valueOf(Integer.valueOf(fechaBajaMes) + 1);
				String fechaBajaAnio = ParamUtil.getString(renderRequest,
						"fechaBajaAnio");
				int cantidad_cheques = ParamUtil.getInteger(renderRequest,
						"cantidad_cheques");

				Date fechaBaja = format.parse(fechaBajaDia + "-" + fechaBajaMes
						+ "-" + fechaBajaAnio);

				List<Cheque> chequesAnular = getChequesParaAnular(
						renderRequest, ctasBcrias, cantidad_cheques);

				User user = PortalUtil.getUser(renderRequest);
				boolean opCreadaEnCanje = OrdenPagoServiceUtil
						.verificarOPCreadaEnCanje(nro, entidad);
				if (opCreadaEnCanje) {
					throw new OpCreadaEnCanjeException();
				}
				if (entidad == WebKeysGlobal.OSPIM) {
					OrdenPagoServiceUtil.anularOrdenPagoOspim(nro, user,
							fechaBaja, chequesAnular);
				} else {
					OrdenPagoServiceUtil.anularOrdenPago(nro, user, fechaBaja,
							chequesAnular, entidad);
				}
				success = true;
			} else if (accion.equals("reactivar")) {

				boolean opCreadaEnCanje = OrdenPagoServiceUtil
						.verificarOPCreadaEnCanje(nro, entidad);
				if (opCreadaEnCanje) {
					throw new OpCreadaEnCanjeException();
				}

				OrdenPagoServiceUtil.reactivar(nro, entidad);
				success = true;
			}
			if (success) {
				success(renderRequest);
			}
		} catch (OpConChequesCanjeadosException e) {
			logger.error("Error al anular/reactivar OP", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
			renderRequest.setAttribute("chequesCanjeados",
					e.getChequesCanjeados());
		} catch (Exception e) {
			logger.error("Error al anular/reactivar OP", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		OrdenPago op = null;
		if (entidad == WebKeysGlobal.OSPIM) {
			op = OrdenPagoServiceUtil.getOrdenPagoOspim(nro);
		} else {
			op = OrdenPagoServiceUtil.getOrdenPago(nro, entidad);
			List<ItemOrdenPago> items = op.getItems();
			boolean esFarmacia = items != null && items.size() > 0;
			renderRequest.setAttribute(WebKeysTesoreria.IS_FARMACIA,
					String.valueOf(esFarmacia));
		}
		renderRequest.setAttribute(
				WebKeysLiquidaciones.ORDEN_PAGO_ANULACION_FORMA_PAGO,
				op.getFormaPago());
		renderRequest.setAttribute(WebKeysLiquidaciones.FECHA_BAJA_OP,
				op.getBaja_fecha());

		renderRequest.setAttribute("id_op", nro.toString());
		return mapping.findForward("portlet.liquidaciones.anular.op");
	}

	private void success(RenderRequest renderRequest) {
		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");

			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
	}

	private List<Cheque> getChequesParaAnular(RenderRequest renderRequest,
			List<CuentaBancaria> ctasBcrias, int cantidad_cheques) {
		List<Cheque> cheques = new ArrayList<Cheque>();
		String key = "anular_cheque_";
		Cheque ch = null;
		Banco b = null;
		CuentaBancaria cb =null;
		
		for (int i = 0; i < cantidad_cheques; i++) {
//			if (renderRequest.getParameter(key + i) != null) {
//				String aBorrar = renderRequest.getParameter(key + i);
//				String numeroCheque = aBorrar.substring(14,aBorrar.indexOf("_", 14));
//				String idCta = aBorrar.substring(aBorrar.indexOf("_", 14) + 1,aBorrar.length());
//				int indexOf = ctasBcrias.indexOf(new CuentaBancaria(Integer.valueOf(idCta)));
//				cheques.add(new Cheque(new BigDecimal(numeroCheque), ctasBcrias.get(indexOf).getBanco().getId_banco()));
//				
//			}
			String[] chequeDatos = new String[6];
			
			if (renderRequest.getParameter(key + i) != null) {
				chequeDatos = renderRequest.getParameter(key + i).split("_");
				
				ch = new Cheque();
				ch.setNumero(new BigDecimal(chequeDatos[2]));
				cb = new CuentaBancaria(new Integer(chequeDatos[3]));
				b = new Banco(new Integer(chequeDatos[4]));
				cb.setBanco(b);
				ch.setCuentaBancaria(cb);
				
				cheques.add(ch);
			}
		}
		return cheques;
	}
}

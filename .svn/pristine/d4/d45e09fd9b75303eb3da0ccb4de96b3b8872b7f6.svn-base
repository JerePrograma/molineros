package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;


public class EditarOrdenesPagoFormaPagoAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		int entidad=WebKeysGlobal.OSPIM;
		if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}	
		
		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		procesarMostrarBusquedaComprobantes(actionRequest);
		
		OrdenPago op = getOrdenPagoFromRequest(actionRequest, actionResponse);
		User user = PortalUtil.getUser(actionRequest);
		try {
			
			op.validar(entidad);
			
			if (cmd.equals(Constants.UPDATE)) {
				OrdenPagoServiceUtil.updateFormaPago(op, user,null,entidad);
			}
			
			session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
			actionRequest.setAttribute("orden_pago_id", op.getId().toString());
			session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			
		} catch (Exception e) {
			if(e != null && e.getMessage()!=null && e.getMessage().startsWith("Comprobante")){
				SessionErrors.add(actionRequest, "cpteError");
				actionRequest.setAttribute("msgError1",e.getMessage());
			}else{
			   SessionErrors.add(actionRequest, e.getClass().getName());
			}
			actionRequest.setAttribute(
					WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
		
	}

	@SuppressWarnings("unchecked")
	private OrdenPago getOrdenPagoFromRequest(ActionRequest actionRequest, ActionResponse actionResponse)
			throws ParseException {
		
		int entidad=WebKeysGlobal.OSPIM;
		if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}	

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		OrdenPago ordenPago = null;
		if(entidad==WebKeysGlobal.AMTIMA){
			ordenPago = (OrdenPagoAmtima) session
				.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		}else if(entidad==WebKeysGlobal.OSPIM){
			ordenPago = (OrdenPagoOspim) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		}else if(entidad==WebKeysGlobal.UOMA){
//			ordenPago = (OrdenPagoUoma) session
//					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			ordenPago = (OrdenPago) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		}

		String cuitAcreedor = actionRequest.getParameter("cuit_entidad");
		String sucuAcreedor = actionRequest.getParameter("sucursal_entidad");
		String idSeccional = actionRequest.getParameter("id_seccional");

		if (StringUtils.checkNotEmpty(idSeccional)
				&& Integer.parseInt(idSeccional) != 0) {
			ordenPago.setSeccional(new Seccional(Integer.parseInt(idSeccional),
					null, cuitAcreedor));
			sucuAcreedor = "000";
		}

		if (StringUtils.checkNotEmpty(cuitAcreedor)) {
			ordenPago
					.setAcreedor(new Empresa(cuitAcreedor, sucuAcreedor, null));
		}

		String idStr = ParamUtil
				.getString(actionRequest, "orden_pago_id", null);
		if (idStr != null && !idStr.trim().equals("")) {
			ordenPago.setId(Integer.valueOf(idStr));
		}

		String obs = actionRequest.getParameter("obs");
		ordenPago.setObservaciones(obs);

		String fechaDiaDesde = ParamUtil.getString(actionRequest,
				"fechaDiaDesde");
		String fechaMesDesde = ParamUtil.getString(actionRequest,
				"fechaMesDesde");
		fechaMesDesde = String.valueOf(Integer.valueOf(fechaMesDesde) + 1);
		String fechaAnioDesde = ParamUtil.getString(actionRequest,
				"fechaAnioDesde");

		String fechaDiaHasta = ParamUtil.getString(actionRequest,
				"fechaDiaHasta");
		String fechaMesHasta = ParamUtil.getString(actionRequest,
				"fechaMesHasta");
		fechaMesHasta = String.valueOf(Integer.valueOf(fechaMesHasta) + 1);
		String fechaAnioHasta = ParamUtil.getString(actionRequest,
				"fechaAnioHasta");

		String descuentoStr = ParamUtil.getString(actionRequest, "desc", "0");
		BigDecimal descuento = new BigDecimal(descuentoStr);
		String descuentoDrogStr = ParamUtil.getString(actionRequest,
				"desc_drog", "0");
		BigDecimal descuentoDrog = new BigDecimal(descuentoDrogStr);

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		ordenPago.setFechaDesde(format.parse(fechaDiaDesde + "-"
				+ fechaMesDesde + "-" + fechaAnioDesde));
		ordenPago.setFechaHasta(format.parse(fechaDiaHasta + "-"
				+ fechaMesHasta + "-" + fechaAnioHasta));

		ordenPago.setDescuento(descuento);
		ordenPago.setDescuentoDrogueria(descuentoDrog);

		ordenPago.setImporte(getImporteFromPagos(ordenPago));

		List<Comprobante> comprobantes = (List<Comprobante>) session
				.getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		ordenPago.setComprobantes(comprobantes);
		
		if(actionResponse.getNamespace().equals("_UOM_1_")){
//			return (OrdenPagoUoma) ordenPago;
			return (OrdenPago) ordenPago;
		}else{
			return (OrdenPagoAmtima) ordenPago;			
		}
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}

		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		if (SessionErrors.isEmpty(renderRequest)) {
			session.removeAttribute(WebKeysLiquidaciones.ORDENES_PAGO);

			String fromReinte = (String) renderRequest
					.getAttribute(WebKeysLiquidaciones.FROM_REINTEGROS);
			if (StringUtils.checkEmpty(fromReinte)) {
				session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			}

			OrdenPago op = (OrdenPago) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			if (op == null) {
				String nro = ParamUtil.getString(renderRequest,
						"orden_pago_id", null);
				if(nro==null || nro.trim().equals("") || nro.trim().equals("0")){
					nro=(String)renderRequest.getAttribute("orden_pago_id");
				}
								
				session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
				session.removeAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION);
				if (nro != null && !nro.trim().equals("") && !nro.equals("0")) {
					op = OrdenPagoServiceUtil.getOrdenPago(Integer
							.valueOf(nro), entidad);
				}
				if (op != null && op.getComprobantes() != null) {
					session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
							op.getComprobantes());
					session.setAttribute(
							WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION,
							sumaImportesOrden(op.getComprobantes()));
				}
			}
			if(op==null && renderResponse.getNamespace().equals("_UOM_1_")){
				op=new OrdenPagoUoma();
			}else if(op==null){
				op= new OrdenPagoAmtima();			
			}
			if (op != null) {
				session.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
			}
			if (op.getId() == 0) {
				renderRequest.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION,
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			}

			List<Cheque> chequesReutilizables = ChequeServiceUtil
					.getChequesReutilizables(entidad);
			if (chequesReutilizables != null && chequesReutilizables.size() > 0) {
				renderRequest.setAttribute(
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES,
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
			}
		}
		return mapping.findForward("portlet.farmacia.editar_orden_pago_forma_pago");		
	}

	private void procesarMostrarBusquedaComprobantes(ActionRequest actionRequest) {
		String key = WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES;
		String nomostrar = actionRequest.getParameter(key);
		if (nomostrar != null && nomostrar.equals(key)) {
			actionRequest.setAttribute(key, key);
		}
	}

	public BigDecimal sumaImportesOrden(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		for (Comprobante comprobante : comprobantes) {
			suma = suma.add(comprobante.getImporteComprobante());
		}
		return suma;
	}

	private BigDecimal getImporteFromPagos(OrdenPago ordenPago) {
		BigDecimal total = BigDecimal.ZERO;
		if (ordenPago != null && ordenPago.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : ordenPago.getFormaPago()) {
				if (!fp.getTipo().equals("Anticipo")) {
					total = total.add(fp.getPago().getImporte());
				}
			}
		}
		return total;
	}

}
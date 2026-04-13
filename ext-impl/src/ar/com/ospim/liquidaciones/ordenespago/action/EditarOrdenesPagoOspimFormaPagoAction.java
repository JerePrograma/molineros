package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.OrdenPagoOspimCreacionNuevoAnticipoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarOrdenesPagoOspimFormaPagoAction extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		procesarMostrarBusquedaComprobantes(actionRequest);
		OrdenPagoOspim op = getOrdenPagoFromRequest(actionRequest);
		User user = PortalUtil.getUser(actionRequest);

		try {
			op.validar(WebKeysGlobal.OSPIM);
			
			if (cmd.equals(Constants.UPDATE)) {
				OrdenPagoServiceUtil.updateFormaPago(op, user,null,WebKeysGlobal.OSPIM);
			}
			
			session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
			actionRequest.setAttribute("orden_pago_id", op.getId().toString());
		} catch (OrdenPagoOspimCreacionNuevoAnticipoException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute("nuevoAnticipo", "nuevoAnticipo");
			actionRequest.setAttribute("valorAnticipoOriginal",
					e.getImporteOriginal());
			actionRequest.setAttribute("valorAnticipoNuevo",
					e.getImporteNuevo());
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			if(op!=null && op.getAlta_usr()==null) {
				actionRequest.setAttribute("PROXIMOIDORDENPAGO",op.getId());
				op.setId(null);
				session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
				actionRequest.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION,
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			}
		}

		if (SessionErrors.isEmpty(actionRequest)) {
				String successMessage = ParamUtil.getString(actionRequest,"successMessage");
				SessionMessages.add(actionRequest, "request_processed",successMessage);
		}
	}
	
	

	private void procesarMostrarBusquedaComprobantes(ActionRequest actionRequest) {
		String key = WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES;
		String nomostrar = actionRequest.getParameter(key);
		if (nomostrar != null && nomostrar.equals(key)) {
			actionRequest.setAttribute(key, key);
		}
	}

	@SuppressWarnings("unchecked")
	private OrdenPagoOspim getOrdenPagoFromRequest(ActionRequest actionRequest)
			throws ParseException {

		OrdenPagoOspim ordenPago = (OrdenPagoOspim) PortalUtil
				.getHttpServletRequest(actionRequest).getSession()
				.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

		if (ordenPago == null) {
			ordenPago = new OrdenPagoOspim();
		}

		String idStr = ParamUtil
				.getString(actionRequest, "orden_pago_id", null);
		if (idStr != null && !idStr.trim().equals("")) {
			ordenPago.setId(Integer.valueOf(idStr));
		}
		
		Date altaFecha = null;
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = DateUtils.getCalendarGMTMenos3();
		
		String fechaHastaDia=ParamUtil.getString(actionRequest, "altaFechaDia");
		String fechaHastaMes=ParamUtil.getString(actionRequest, "altaFechaMes");
		String fechaHastaAnio=ParamUtil.getString(actionRequest, "altaFechaAnio");

		try {
			altaFecha = formatoDeFecha.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			altaFecha = cal.getTime();
		}
		
		ordenPago.setAlta_fecha(altaFecha);
		
		String fromReinte = ((String) actionRequest
				.getAttribute(WebKeysLiquidaciones.FROM_REINTEGROS))!=null?((String) actionRequest
						.getAttribute(WebKeysLiquidaciones.FROM_REINTEGROS)):ParamUtil
						.getString(actionRequest, WebKeysLiquidaciones.FROM_REINTEGROS, null);
		if(null!=fromReinte && fromReinte.equals(WebKeysLiquidaciones.FROM_REINTEGROS_FARMACIA)){
			ordenPago.setFarmacia(true);
		}
		ordenPago.setImporte(getImporteFromPagos(ordenPago));

		List<Comprobante> comprobantes = (List<Comprobante>) PortalUtil
				.getHttpServletRequest(actionRequest).getSession()
				.getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		ordenPago.setComprobantes(comprobantes);

		String obs = actionRequest.getParameter("obs");

		ordenPago.setObservaciones(obs);

		String cuitAcreedor = actionRequest.getParameter("cuit_entidad");
		String sucuAcreedor = actionRequest.getParameter("sucursal_entidad");
		String idSeccional = actionRequest.getParameter("id_seccional");
		
		ordenPago.setDestino(ParamUtil.getString(actionRequest, "destino"));
		ordenPago.setObsInterna(ParamUtil.getString(actionRequest, "observacion_interna"));
		
		ordenPago.setIdLote(ParamUtil.getInteger(actionRequest, "nro_lote"));


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

		return ordenPago;
	}

	private BigDecimal getImporteFromPagos(OrdenPagoOspim ordenPago) {
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

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest).getSession();

		if (SessionErrors.isEmpty(renderRequest)) {
			TraeListasServiceUtil.getCtasBcrias(renderRequest);
			HttpServletRequest httpReq = PortalUtil
					.getHttpServletRequest(renderRequest);

			String fromReinte = (String) renderRequest
					.getAttribute(WebKeysLiquidaciones.FROM_REINTEGROS);
			if (StringUtils.checkEmpty(fromReinte)) {
				session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			}

			OrdenPagoOspim op = (OrdenPagoOspim) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			if (op == null) {
				String nro = ParamUtil.getString(renderRequest,
						"orden_pago_id", null);
				if (nro == null || nro.trim().equals("") || nro.equals("0")) {
					nro = (String) renderRequest.getAttribute("orden_pago_id");
				}
				httpReq.getSession().removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
				httpReq.getSession().removeAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION);
				if (nro != null && !nro.trim().equals("") && !nro.equals("0")) {
					op = OrdenPagoServiceUtil.getOrdenPagoOspim(Integer.valueOf(nro));
					if (op.getComprobantes() != null) {
						session.setAttribute(
								WebKeysGlobal.COMPROBANTES_EN_SESSION,
								op.getComprobantes());
						session.setAttribute(
								WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION,
								sumaImportesOrden(op.getComprobantes()));
					}
				}
			}
			if (op == null) {
				op = new OrdenPagoOspim();
				
				try {
					int proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();
					renderRequest.setAttribute("PROXIMOIDORDENPAGO",proximoIdOP);
					
				} catch (SystemException e) {
					_log.error(e);
				}
			
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
		return mapping
				.findForward("portlet.liquidaciones.editar_orden_pago_ospim_forma_pago");
	}

	public BigDecimal sumaImportesOrden(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		for (Comprobante comprobante : comprobantes) {
			suma = suma.add(comprobante.getImporteComprobante());
		}
		return suma;
	}
}
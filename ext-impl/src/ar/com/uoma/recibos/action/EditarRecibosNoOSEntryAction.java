package ar.com.uoma.recibos.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.ReciboConceptoSinImporteException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConcepto.ConceptoPago;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.recibos.service.ReciboNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarRecibosNoOSEntryAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarRecibosNoOSEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		_log.debug("Entrando a guardar recibo");
		
		int entidad_i=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad_i=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad_i=WebKeysGlobal.UOMA;
		}else if(actionResponse.getNamespace().equals("_EST_1_")){
			entidad_i=WebKeysGlobal.ESTUDIO;
		}

		Recibo recibo = getReciboFromRequest(actionRequest);
		User user = PortalUtil.getUser(actionRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);

		try {
			distribuirChequesYFechasPagos(recibo);
			boolean debaja = false;
			String anulado = actionRequest.getParameter("anulado");
			if (anulado != null && anulado.equals("anulado")) {
				debaja = true;
			}
			String entidad = ParamUtil.getString(actionRequest, "entidad_bla");
			ReciboNoOSServiceUtil.save(recibo, debaja, user, entidad, entidad_i);
			session.removeAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			actionRequest.setAttribute("recibo_id",
					String.valueOf(recibo.getId()));
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);

		} else {
			actionRequest.setAttribute(WebKeysTesoreria.RECIBOS_ACTION_EDICION,
					WebKeysTesoreria.RECIBOS_ACTION_EDICION);
		}
		_log.debug("Saliendo de guardar recibo");
	}

	private Recibo getReciboFromRequest(ActionRequest req)
			throws ParseException {
		HttpSession session = PortalUtil.getHttpServletRequest(req)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
		}

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		// String empleador = ParamUtil.getString(req, "empleador", "");
		recibo.setEmpresa(new Empresa(cuit, sucu, null));

		String idSeccional = req.getParameter("id_seccional");
		if (StringUtils.checkNotEmpty(idSeccional)
				&& Integer.parseInt(idSeccional) != 0) {
			recibo.setSeccional(new Seccional(Integer.parseInt(idSeccional),
					null, cuit));
			recibo.getEmpresa().setSucursal("000");
		}

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaInicioDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaInicioMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaInicioAnio");
		recibo.setFecha(format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio));

		String numero = ParamUtil.getString(req, "recibo_numero");
		String obs = ParamUtil.getString(req, "obs");

		String importe = ParamUtil.getString(req, "total_conceptos");
		recibo.setImporte(new BigDecimal(importe));
		recibo.setNumero(numero);
		recibo.setObservaciones(obs);

		obtenerDatosActas(req, recibo);
		obtenerDatosConvenios(req, recibo);
		return recibo;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad_i=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad_i=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad_i=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_EST_1_")){
			entidad_i=WebKeysGlobal.ESTUDIO;
		}	
	
		if(entidad_i == WebKeysGlobal.ESTUDIO){
			String entidadStr=ParamUtil.getString(renderRequest, "entidad_rec");
			if(entidadStr.trim().equals("OSPIM")){
				entidad_i=WebKeysGlobal.OSPIM;
			}else if(entidadStr.trim().equals("UOMA")){
				entidad_i=WebKeysGlobal.UOMA;
			}else if(entidadStr.trim().equals("AMTIMA")){
				entidad_i=WebKeysGlobal.AMTIMA;
			}
			
		}

		TraeListasServiceUtil.getBancos(renderRequest);
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		TraeListasServiceUtil.getConceptoIngreso(renderRequest, entidad_i);
		
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		if (SessionErrors.isEmpty(renderRequest)) {
			session.removeAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			String id = renderRequest.getParameter("recibo_id");
			if (id == null) {
				id = (String) renderRequest.getAttribute("recibo_id");
			}
			if (StringUtils.checkNotEmpty(id)) {
				Recibo recibo = ReciboNoOSServiceUtil.get(Integer.parseInt(id), entidad_i);
				session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
				TraeListasServiceUtil.getConceptoIngreso(renderRequest,
						recibo.getFecha(), entidad_i);
			}
		} else if (session.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION) != null) {
			Recibo r = (Recibo) session
					.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			TraeListasServiceUtil.getConceptoIngreso(renderRequest,
					r.getFecha(), entidad_i);
		}

		return mapping
				.findForward("portlet.estudio_isidro.recibos.editar_recibos_no_os_entry");
	}

	private void obtenerDatosActas(ActionRequest renderRequest, Recibo recibo) {
		if (recibo.getActas() != null) {
			for (ReciboActa reciboActa : recibo.getActas()) {
				String adicionalStr = renderRequest.getParameter("acta_"
						+ reciboActa.getActa().getId());
				BigDecimal adicional = BigDecimal.ZERO;
				if (StringUtils.checkNotEmpty(adicionalStr)) {
					adicional = new BigDecimal(adicionalStr);
				}
				reciboActa.setImporteAdicional(adicional);
			}
		}
	}

	private void obtenerDatosConvenios(ActionRequest renderRequest,
			Recibo recibo) {
		if (recibo.getConvenios() != null) {
			for (ReciboConvenio reciboConvenio : recibo.getConvenios()) {
				String adicionalStr = renderRequest.getParameter("convenio_"
						+ reciboConvenio.getConvenio().getId());
				BigDecimal adicional = BigDecimal.ZERO;
				if (StringUtils.checkNotEmpty(adicionalStr)) {
					adicional = new BigDecimal(adicionalStr);
				}
				reciboConvenio.setImporteAdicional(adicional);
			}
		}
	}

	private void distribuirChequesYFechasPagos(Recibo recibo)
			throws ReciboConceptoSinImporteException {

		if (recibo.getIngresos() != null) {
			// ordeno ingresos de mas viejo a mas nuevo
			List<ReciboIngreso> ingresos = recibo.getIngresos();
			Collections.sort(ingresos, new Comparator<ReciboIngreso>() {
				public int compare(ReciboIngreso arg0, ReciboIngreso arg1) {
					return arg0.getIngreso().getFecha()
							.compareTo(arg1.getIngreso().getFecha());
				}
			});

			// ordeno conceptos para distribuirlos en los ingresos
			List<ReciboConcepto> items = new ArrayList<ReciboConcepto>();
			if (recibo.getActas() != null) {
				items.addAll(recibo.getActas());
			}
			if (recibo.getConvenios() != null) {
				items.addAll(recibo.getConvenios());
			}
			if (recibo.getChequesNoDepositados() != null) {
				items.addAll(recibo.getChequesNoDepositados());
			}
			if (recibo.getChequesRechazados() != null) {
				items.addAll(recibo.getChequesRechazados());
			}
			if (recibo.getOtrosConceptos() != null) {
				items.addAll(recibo.getOtrosConceptos());
			}

			for (ReciboConcepto rc : items) {
				if (rc.getTotalAPagarNoOS().compareTo(BigDecimal.ZERO) == 0) {
					throw new ReciboConceptoSinImporteException();
				} else if (rc.getPagos() != null) {
					rc.getPagos().clear();
				}
			}

			Collections.sort(items, new Comparator<ReciboConcepto>() {
				public int compare(ReciboConcepto arg0, ReciboConcepto arg1) {
					return arg0.getFechaAPagar().compareTo(
							arg1.getFechaAPagar());
				}
			});

			int indiceIngresos = 0;
			BigDecimal ingresoUtilizado = BigDecimal.ZERO;
			BigDecimal totalAPagarN = BigDecimal.ZERO;
			for (int indiceConceptos = 0; indiceConceptos < items.size(); indiceConceptos++) {
				ReciboConcepto concepto = items.get(indiceConceptos);
				totalAPagarN = totalAPagarN.add(concepto.getTotalAPagarNoOS());
			}

			for (int indiceConceptos = 0; indiceConceptos < items.size(); indiceConceptos++) {
				ReciboConcepto concepto = items.get(indiceConceptos);
				// BigDecimal totalAPagar = concepto.getTotalAPagarNoOS();
				if (concepto.getImporte().doubleValue() > 0) {
					while (concepto.getTotalPagado().compareTo(totalAPagarN) < 0) {
						ReciboIngreso reciboIngreso = ingresos
								.get(indiceIngresos);
						ReciboConcepto.ConceptoPago cp = new ReciboConcepto.ConceptoPago();
						cp.setIngreso(reciboIngreso);

						BigDecimal faltanteAPagar = totalAPagarN
								.subtract(concepto.getTotalPagado());
						BigDecimal totalIngresoDisponible = reciboIngreso
								.getIngreso().getImporte()
								.subtract(ingresoUtilizado);
						int comp = faltanteAPagar
								.compareTo(totalIngresoDisponible);
						if (comp < 0) { // me sobra ingreso
							cp.setImporte(faltanteAPagar);
							ingresoUtilizado = ingresoUtilizado
									.add(faltanteAPagar);
						} else if (comp == 0) {// no me sobra ingreso, alcanza
												// justo
							cp.setImporte(faltanteAPagar);
							indiceIngresos++;
							ingresoUtilizado = BigDecimal.ZERO;
						} else { // me falta ingreso
							cp.setImporte(totalIngresoDisponible);
							indiceIngresos++;
							ingresoUtilizado = BigDecimal.ZERO;
						}

						if (concepto.getPagos() == null) {
							concepto.setPagos(new ArrayList<ConceptoPago>());
						}
						concepto.getPagos().add(cp);
					}
				//Asumo que el importe está bien.
				}else{
					if(indiceIngresos>=ingresos.size()){
						indiceIngresos--;
					}
					ReciboIngreso reciboIngreso = ingresos
							.get(indiceIngresos);
					ReciboConcepto.ConceptoPago cp = new ReciboConcepto.ConceptoPago();
					cp.setIngreso(reciboIngreso);
					cp.setImporte(cp.getImporte());
					concepto.setPagos(new ArrayList<ConceptoPago>());
					concepto.getPagos().add(cp);
				}
			}
		}
	}
}

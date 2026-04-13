package ar.com.ospim.tesoreria.recibos.action;

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

import ar.com.ospim.afiliados.beans.Afiliado;
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
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.recibos.service.ReciboNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarRecibosEntryAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarRecibosEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		_log.debug("Entrando a guardar recibo");

		int entidad = WebKeysGlobal.OSPIM;

		if (actionResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (actionResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (actionResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}
		if(entidad == WebKeysGlobal.ESTUDIO){
			entidad=ParamUtil.getInteger(actionRequest, "entidad_rec");
		}

		Recibo recibo = getReciboFromRequest(actionRequest, entidad);
		User user = PortalUtil.getUser(actionRequest);
		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		String cmd = ParamUtil.getString(actionRequest, "rec_" + Constants.CMD);

		try {
			distribuirChequesYFechasPagos(recibo);
			boolean debaja = false;
			String anulado = actionRequest.getParameter("anulado");
			if (anulado != null && anulado.equals("anulado")) {
				debaja = true;
			}
			if (cmd.equals(Constants.ADD)) {
				ReciboServiceUtil.save(recibo, debaja, user, entidad);
			} else if (cmd.equals(Constants.UPDATE)) {
				ReciboServiceUtil.update(recibo, debaja, user, entidad);
			}
			session.removeAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			actionRequest.setAttribute("recibo_id",
					String.valueOf(recibo.getId()));
		} catch (Exception e) {
			_log.error(e);
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

	private Recibo getReciboFromRequest(ActionRequest req, int entidad)
			throws ParseException {
		HttpSession session = PortalUtil.getHttpServletRequest(req)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
		String origen = ParamUtil.getString(req, "origen", "");
		if (recibo == null) {
			recibo = new Recibo();
		}

		/* Sergio Valentini */
		Integer inte, nroAfi;
		String cuil_titu, nombreAf, apellidoAf;
		String cuit, sucu, idSeccional;

		String reciboDe = ParamUtil.getString(req, "entidadIngreso");

		if (null != reciboDe && reciboDe.equalsIgnoreCase("Afiliado")) {
			cuil_titu = ParamUtil.getString(req, "cuil" + origen);
			inte = ParamUtil.getInteger(req, "inte" + origen);
			nombreAf = ParamUtil.getString(req, "apellido" + origen);
			apellidoAf = ParamUtil.getString(req, "nombre" + origen);
			nroAfi = ParamUtil.getInteger(req, "numero_afi" + origen);

			Afiliado af = new Afiliado(cuil_titu, inte, nombreAf, apellidoAf);
			if (nroAfi != 0) {
				af.setId_amtima(nroAfi);
			}
			recibo.setAfiliado(af);
		} else {
			cuit = ParamUtil.getString(req, "cuit_entidad");
			sucu = ParamUtil.getString(req, "sucursal_entidad");
			// String empleador = ParamUtil.getString(req, "empleador", "");
			recibo.setEmpresa(new Empresa(cuit, sucu, null));

			idSeccional = req.getParameter("id_seccional");
			if (StringUtils.checkNotEmpty(idSeccional)
					&& Integer.parseInt(idSeccional) != 0) {
				recibo.setSeccional(new Seccional(
						Integer.parseInt(idSeccional), null, cuit));
				recibo.getEmpresa().setSucursal("000");
			}
		}

		/* fin sva */
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaInicioDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaInicioMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaInicioAnio");
		recibo.setFecha(format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio));
		String numero = null;
		String prenumero = null;
		if (entidad != WebKeysGlobal.AMTIMA) {
			prenumero = ParamUtil.getString(req, "recibo_pre", "");
			numero= ParamUtil.getString(req, "recibo_numero", "");
			if (entidad != WebKeysGlobal.OSPIM) {
				numero = prenumero
						+ numero;
			}else{
				numero= prenumero+StringUtils.leftPad(numero, 8, '0');
			}
		} else {
			numero = ParamUtil.getString(req, "recibo_numero");
		}

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

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}
		if(entidad == WebKeysGlobal.ESTUDIO){
			String entidadStr=ParamUtil.getString(renderRequest, "entidad_rec");
			if(entidadStr.trim().equals("OSPIM")){
				entidad=WebKeysGlobal.OSPIM;
			}else if(entidadStr.trim().equals("UOMA")){
				entidad=WebKeysGlobal.UOMA;
			}else if(entidadStr.trim().equals("AMTIMA")){
				entidad=WebKeysGlobal.AMTIMA;
			}
			
		}

		String origen = ParamUtil.getString(renderRequest, "origen");

		TraeListasServiceUtil.getBancos(renderRequest);
		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		if (ParamUtil.getBoolean(renderRequest, "recargarConcepto")) {
			TraeListasServiceUtil.getConceptoIngreso(renderRequest,
					ParamUtil.getString(renderRequest, "cuit"),
					ParamUtil.getString(renderRequest, "sucursal"),
					ParamUtil.getInteger(renderRequest, "id_seccional"),
					entidad);
			return mapping
					.findForward("portlet.tesoreria.recibos.actualiza_otros_conceptos");

		} else {
			TraeListasServiceUtil.getConceptoIngreso(renderRequest, entidad);
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		if (SessionErrors.isEmpty(renderRequest)) {
			session.removeAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			String id = renderRequest.getParameter("recibo_id");
			if (id == null) {
				id = (String) renderRequest.getAttribute("recibo_id");
			}
			if (StringUtils.checkNotEmpty(id)) {
				Recibo recibo = null;
				if (null != origen && origen.trim().length() > 0
						&& !origen.equals("recibosTesoreria")) {
					renderRequest.setAttribute("origen", origen);
					recibo = ReciboNoOSServiceUtil.get(Integer.parseInt(id),
							entidad);
				} else {
					recibo = ReciboServiceUtil.get(Integer.parseInt(id),
							entidad);
				}
				session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
				TraeListasServiceUtil.getConceptoIngreso(renderRequest,
						recibo.getFecha(), entidad);
			}
		} else if (session.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION) != null) {
			Recibo r = (Recibo) session
					.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
			TraeListasServiceUtil.getConceptoIngreso(renderRequest,
					r.getFecha(), entidad);
		}

		return mapping
				.findForward("portlet.tesoreria.recibos.editar_recibos_entry");
	}

	private void obtenerDatosActas(ActionRequest renderRequest, Recibo recibo) {
		if (recibo.getActas() != null) {
			for (ReciboActa reciboActa : recibo.getActas()) {
				double adicional = ParamUtil.getDouble(renderRequest, "acta_"
						+ reciboActa.getActa().getId());
				double importeTotal = ParamUtil.getDouble(renderRequest,
						"total_acta_" + reciboActa.getActa().getId());
				reciboActa.setImporteAdicional(new BigDecimal(adicional));
				reciboActa.setImportePorCheques(new BigDecimal(importeTotal));
			}
		}
	}

	private void obtenerDatosConvenios(ActionRequest renderRequest,
			Recibo recibo) {
		if (recibo.getConvenios() != null) {
			for (ReciboConvenio reciboConvenio : recibo.getConvenios()) {
				double adicional = ParamUtil.getDouble(renderRequest,
						"convenio_" + reciboConvenio.getConvenio().getId());
				double importeTotal = ParamUtil.getDouble(renderRequest,
						"total_convenio_"
								+ reciboConvenio.getConvenio().getId());

				reciboConvenio.setImporteAdicional(new BigDecimal(adicional));
				reciboConvenio
						.setImportePorCheques(new BigDecimal(importeTotal));
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

			if (recibo.getReciboPrestamos() != null) {
				items.addAll(recibo.getReciboPrestamos());
			}

			for (ReciboConcepto rc : items) {
				if (rc.getTotalAPagar().compareTo(BigDecimal.ZERO) == 0 && rc.getImporte().compareTo(BigDecimal.ZERO)==0) {
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
			for (int indiceConceptos = 0; indiceConceptos < items.size(); indiceConceptos++) {
				ReciboConcepto concepto = items.get(indiceConceptos);
				BigDecimal totalAPagar = concepto.getTotalAPagar();
				// LOS CONVENIOS PUEDEN PAGARSE PARCIALMENTE...
				if (concepto instanceof ReciboConvenio) {
					totalAPagar = ((ReciboConvenio) concepto)
							.getImportePorCheques().subtract(
									((ReciboConvenio) concepto)
											.getTotalPagado());
				}
				if (concepto instanceof ReciboActa) {
					totalAPagar = ((ReciboActa) concepto)
							.getImportePorCheques().subtract(
									((ReciboActa) concepto).getTotalPagado());
				}
				while (concepto
						.getTotalPagado()
						.setScale(2, BigDecimal.ROUND_UP)
						.compareTo(
								totalAPagar.setScale(2, BigDecimal.ROUND_DOWN)) < 0) {
					ReciboIngreso reciboIngreso = ingresos.get(indiceIngresos);
					ReciboConcepto.ConceptoPago cp = new ReciboConcepto.ConceptoPago();
					cp.setIngreso(reciboIngreso);

					BigDecimal faltanteAPagar = totalAPagar.subtract(concepto
							.getTotalPagado());
					BigDecimal totalIngresoDisponible = reciboIngreso
							.getIngreso().getImporte()
							.subtract(ingresoUtilizado);
					int comp = faltanteAPagar.compareTo(totalIngresoDisponible);
					if (comp < 0) { // me sobra ingreso
						cp.setImporte(faltanteAPagar);
						ingresoUtilizado = ingresoUtilizado.add(faltanteAPagar);
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
			}
		}
	}
}

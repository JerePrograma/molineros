package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarOrdenPagoOspimFromReintegrosFarmacia extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarOrdenPagoOspimFromReintegrosFarmacia.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		try {
			HttpSession session = PortalUtil.getHttpServletRequest(
					actionRequest).getSession();

			session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

			List<ReintegroList> reintegrosList = getReintegroList(actionRequest);

			if (reintegrosList == null || reintegrosList.isEmpty()) {
				throw new ListasReintegrosNoEncontradasException();
			}

			OrdenPagoOspim op = new OrdenPagoOspim();
			
			try {
				int proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();
				actionRequest.setAttribute("PROXIMOIDORDENPAGO",proximoIdOP);
				
			} catch (SystemException e) {
				_log.error(e);
			}
			
			session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
			actionRequest.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION,
					WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);

			op.setReintegrosList(reintegrosList);
			BigDecimal total = new BigDecimal("0");
			if (reintegrosList != null && !reintegrosList.isEmpty()) {
				for (ReintegroList reint : reintegrosList) {
					total = total.add(reint.importeTotal());
				}
				List<Seccional> seccionales = TraeListasServiceUtil
						.getSeccionales(actionRequest);
				Seccional seccional = reintegrosList.get(0).getSeccional();
				seccional = seccionales.get(seccionales.indexOf(seccional));
				if (seccional.getIdSeccional() != 9999) {
					op.setSeccional(seccional);
				}

				String cuitAcreedor = WebKeysGlobal.CUIT_OSPIM;
				if (seccional.getIdSeccional() == 9999) {
					cuitAcreedor = reintegrosList.get(0).getReintegros().get(0)
							.getAfiliado().getCuil_titular();
					User user = PortalUtil.getUser(actionRequest);
					EmpresaServiceUtil.insertarAfiliadoComoEmpresaSiNoExiste(
							cuitAcreedor, user);
				}
				op.setAcreedor(new Empresa(cuitAcreedor, "000", null));

				List<Comprobante> comprobantes = setearComprobante(
						cuitAcreedor, op, total, seccional);
				op.setObservaciones("REINTEGROS DE MEDICAMENTOS FARMACIA "
						+ seccional.getDescripcion());
				session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
						comprobantes);
			}
			op.setImporte(total);
		} catch (ListasReintegrosNoEncontradasException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (Exception e) {
			_log.error("Error al crear op de reintegros", e);
			throw e;
		}

		if (!SessionErrors.isEmpty(actionRequest)) {
			setForward(actionRequest, "portlet.liquidaciones.view");
		}
		actionRequest.setAttribute(
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
	}

	private List<Comprobante> setearComprobante(String cuitAcreedor,
			OrdenPagoOspim op, BigDecimal total, Seccional seccional)
			throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Date current = new Date();
		String nro_comp = "";
		if (seccional.getIdSeccional() != 9999) {
			nro_comp = seccional.getDescripcion() + " ";
		}
		nro_comp += DateUtils.format(current, DateUtils.PERIODO) + " -";

		Comprobante comp = new Comprobante(0, "REI", nro_comp,
				WebKeysGlobal.CUIT_OSPIM, current, current, total, " ", 0,
				null);
		comp.setAcreedorEmpresa(new Empresa(cuitAcreedor, "000", null));
		comp.setSucuComprobante(0);

		if (seccional.getIdSeccional() != 9999) {
			comp.setSeccional(seccional);
		}

		// crea concepto asociado al Comprobante reitnegro
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		Concepto concepto = new Concepto(
				ConceptoServiceUtil.getIdReintegrosFarmaciaOSPIM(current));
		concepto.setDescripcion(WebKeysGlobal.DESCRIPCION_CONCEPTO_REINTEGROS_FARMACIA); //ESTO BUSCA EL CONCEPTO DE REINTEGROS FARMACIA
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				concepto, comp.getImporteComprobante());
		conceptos.add(comprobanteConcepto);
		comp.setConceptos(conceptos);

		List<Comprobante> compsEnBase = ComprobanteServiceUtil
				.getComprobantesLikeNro(comp, true);
		int seq = 1;
		if (compsEnBase != null && !compsEnBase.isEmpty()) {
			for (Comprobante c : compsEnBase) {
				int indexOf = c.getNroComprobante().indexOf("-");
				if (indexOf != -1) {
					int indice = Integer.parseInt(c.getNroComprobante()
							.substring(indexOf + 1));
					if (indice > seq) {
						seq = indice;
					}
				}
			}
			seq++;
		}
		comp.setNroComprobante(comp.getNroComprobante() + seq);

		comprobantes.add(comp);
		op.setComprobantes(comprobantes);
		return comprobantes;
	}

	private List<ReintegroList> getReintegroList(ActionRequest actionRequest)
			throws SystemException, ParseException {

		int idSeccional = Integer.parseInt(actionRequest
				.getParameter("id_seccional"));
		String fechaDesdeDia = actionRequest.getParameter("fechaDesdeDia1");
		String fechaDesdeMes = actionRequest.getParameter("fechaDesdeMes1");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = actionRequest.getParameter("fechaDesdeAnio1");
		String fechaHastaDia = actionRequest.getParameter("fechaHastaDia2");
		String fechaHastaMes = actionRequest.getParameter("fechaHastaMes2");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = actionRequest.getParameter("fechaHastaAnio2");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-"
				+ fechaDesdeAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		return OrdenPagoServiceUtil.getReintegrosFarmaciaLists(idSeccional,
				fechaIni, fechaFin);
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		renderRequest.setAttribute(WebKeysLiquidaciones.FROM_REINTEGROS,
				WebKeysLiquidaciones.FROM_REINTEGROS_FARMACIA);
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_orden_pago_ospim_entry"));
	}

}
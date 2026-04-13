package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarEquivalenciasPrestacionesConceptosAction extends
		PortletAction {

	private String CODIGO_MEDICAMENTOS = "400000";

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {

		try {
			String ddOriginal = req.getParameter("ejercicio_desde_original");
			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");
			if (StringUtils.isBlank(dd) || StringUtils.isBlank(hta)
					&& !StringUtils.isBlank(req.getParameter("ejercicio"))) {
				String ejercicio = req.getParameter("ejercicio");
				dd = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
				hta = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
				ddOriginal = dd;
			}
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date desdeOriginal = format.parse(ddOriginal);
			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);
			req.setAttribute("ejercicio_desde_original", ddOriginal);

			int idHA = Integer.parseInt(req
					.getParameter("idHonorariosAmbulatorio"));
			int idHI = Integer.parseInt(req
					.getParameter("idHonorariosInternacion"));
			int idGA = Integer
					.parseInt(req.getParameter("idGastosAmbulatorio"));
			int idGI = Integer
					.parseInt(req.getParameter("idGastosInternacion"));

			int haId = Integer.parseInt(req
					.getParameter("honorarios_ambulatorio"));
			Concepto ha = null;
			if (haId != -1) {
				ha = new Concepto(haId);
			}
			int hiInt = Integer.parseInt(req
					.getParameter("honorarios_internacion"));
			Concepto hi = null;
			if (hiInt != -1) {
				hi = new Concepto(hiInt);
			}
			int gaInt = Integer
					.parseInt(req.getParameter("gastos_ambulatorio"));
			Concepto ga = null;
			if (gaInt != -1) {
				ga = new Concepto(gaInt);
			}
			int giInt = Integer
					.parseInt(req.getParameter("gastos_internacion"));
			Concepto gi = null;
			if (giInt != -1) {
				gi = new Concepto(giInt);
			}

			int idPrestacion = 0;
			if (StringUtils.isNotBlank(req.getParameter("id_prestacion"))) {
				idPrestacion = Integer.parseInt(req
						.getParameter("id_prestacion"));
			}
			String codigo = req.getParameter("codigo");
			String nombre_medicamento = req.getParameter("nombre_medicamento");
			String tipo_nomenclador = req.getParameter("tipo_nomenclador");

			String desc = req.getParameter("descripcion");
			if (StringUtils.isNotBlank(tipo_nomenclador)
					&& tipo_nomenclador.equals("9")) {
				desc = nombre_medicamento;
			}

			Prestacion prestacion = new Prestacion(idPrestacion, desc);
			String coefHono = req.getParameter("coefHonoarios");
			String coefGastos = req.getParameter("coefGastos");
			String importe = req.getParameter("importe");
			try {
				prestacion.setImporte(new BigDecimal(importe));
			} catch (Exception e) {

			}
			prestacion.setCodigo(codigo);
			prestacion.setMarca_rein_liq(Integer.parseInt(req
					.getParameter("marca_rein_liq")));

			PrestacionConcepto pConcepto = new PrestacionConcepto();
			if (StringUtils.isNotBlank(tipo_nomenclador)) {
				pConcepto.setIdTipoNomenclador(Integer
						.parseInt(tipo_nomenclador));
			} else {
				pConcepto.setIdTipoNomenclador(3);
			}

			try {
				pConcepto.setCoeficienteHonorarios(new BigDecimal(coefHono));
			} catch (Exception e) {
			}
			try {
				pConcepto.setCoeficienteGastos(new BigDecimal(coefGastos));
			} catch (Exception e) {
			}

			if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
					DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
				hta = format.format(DateUtils.getInfinito().getTime());
			}

			pConcepto.setValidoDesdeGastosAmbulatorio(format.parse(dd));
			pConcepto.setValidoHastaGastosAmbulatorio(format.parse(hta));
			pConcepto.setValidoDesdeGastosInternacion(format.parse(dd));
			pConcepto.setValidoHastaGastosInternacion(format.parse(hta));
			pConcepto.setValidoDesdeHonorariosAmbulatorio(format.parse(dd));
			pConcepto.setValidoHastaHonorariosAmbulatorio(format.parse(hta));
			pConcepto.setValidoDesdeHonorariosInternacion(format.parse(dd));
			pConcepto.setValidoHastaHonorariosInternacion(format.parse(hta));

			pConcepto.setPrestacion(prestacion);
			pConcepto.setHonorariosAmbulatorio(ha);
			pConcepto.setIdHonorariosAmbulatorio(idHA);
			pConcepto.setHonorariosInternacion(hi);
			pConcepto.setIdHonorariosInternacion(idHI);
			pConcepto.setGastosAmbulatorio(ga);
			pConcepto.setIdGastosAmbulatorio(idGA);
			pConcepto.setGastosInternacion(gi);
			pConcepto.setIdGastosInternacion(idGI);

			User user = PortalUtil.getUser(req);
			if (idPrestacion != 0) {
				ConceptoServiceUtil.update(pConcepto, user, desdeOriginal);
			} else {
				ConceptoServiceUtil.guardar(pConcepto, user, format.parse(dd),
						format.parse(hta));
			}
			// si no fallo piso el dde original
			req.setAttribute("ejercicio_desde_original", dd);
			req.setAttribute("id", pConcepto.getPrestacion().getId());
		} catch (Exception e) {
			SessionErrors.add(req, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(req)) {
			String successMessage = ParamUtil.getString(req, "successMessage");
			SessionMessages.add(req, "request_processed", successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		String ddOriginal = renderRequest
				.getParameter("ejercicio_desde_original");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (renderRequest.getAttribute("ejercicio_desde_original") != null) {
			ddOriginal = (String) renderRequest
					.getAttribute("ejercicio_desde_original");
		}
		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}

		if (StringUtils.isBlank(dd)) {
			dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
		}
		if (StringUtils.isBlank(hta)) {
			hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
		}
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);
		renderRequest.setAttribute("ejercicio_desde_original", ddOriginal);

		String id = renderRequest.getParameter("id");

		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}

		setearConceptosMedicamentos(renderRequest);

		PrestacionConcepto prest;
		if (id != null && !id.equals("0")) {
			prest = ConceptoServiceUtil.getPrestacionesConceptos(
					Integer.parseInt(id), format.parse(ddOriginal),
					format.parse(hta));
		} else {
			prest = new PrestacionConcepto();
			prest.setIdTipoNomenclador(3);
		}

		List<Concepto> conceptos = TraeListasServiceUtil.getConceptos(format
				.parse(dd), entidad);

		renderRequest.setAttribute("prestacionConcepto", prest);
		renderRequest.setAttribute("conceptos", conceptos);

		return mapping
				.findForward(getForward(renderRequest,
						"portlet.tesoreria.equivalencia.editar_prestaciones_conceptos"));

	}

	private void setearConceptosMedicamentos(RenderRequest req) {
		List<PrestacionConcepto> prestacionesConceptos = ConceptoServiceUtil
				.getPrestacionesConceptos(DateUtils.getDesdeEjercicioActual(),
						DateUtils.getHastaEjercicioActual());
		for (PrestacionConcepto pc : prestacionesConceptos) {
			if (pc.getPrestacion().getCodigo().equals(CODIGO_MEDICAMENTOS)) {
				req.setAttribute("medicamento_ambulatorio", pc
						.getGastosAmbulatorio().getId());
				req.setAttribute("medicamento_internacion", pc
						.getGastosInternacion().getId());
			}
		}
	}
}

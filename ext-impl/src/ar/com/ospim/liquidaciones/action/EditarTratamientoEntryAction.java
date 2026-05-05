package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.services.DocumentacionServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateTratamientoDiscapacidadIdException;
import ar.com.ospim.liquidaciones.TopeCantidadIndividualExedidoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * <a href="EditarTratamientoEntryAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */

public class EditarTratamientoEntryAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(EditarTratamientoEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.liquidaciones.tratamiento_discapacidad.result");
	}

	public String validarDocumentacionDiscapacidad(RenderRequest renderRequest,
			String cmd) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);

		String cuil = ParamUtil.getString(renderRequest, "cuil", null);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		if (cmd.equalsIgnoreCase(Constants.UPDATE)) {
			TratamientoDiscapacidad td = TratamientoDiscapacidadServiceUtil
					.getTratamientoDiscapacidad(id_tratamiento);
			cuil = td.getAfiliado().getCuil_titular();
			inte = td.getAfiliado().getInte();
		}

		ArrayList<AfiDocumentacion> docList = (ArrayList<AfiDocumentacion>) DocumentacionServiceUtil
				.buscaDocumentacionDiscapacidad(cuil, inte);

		String mensajeDocumento = "";

		if (docList == null || docList.size() == 0) {
			return "NO SE ENCUENTRA CERTIFICADO DE DISCAPACIDAD PARA DICHO AFILIADO";
		}

		AfiDocumentacion ultimoVigente = (AfiDocumentacion) Collections.max(
				docList, new Comparator<AfiDocumentacion>() {
					public int compare(AfiDocumentacion o1, AfiDocumentacion o2) {
						return o1.getFecha_baja().compareTo(o2.getFecha_baja());
					}
				});

		AfiDocumentacion primeroVigente = (AfiDocumentacion) Collections.min(
				docList, new Comparator<AfiDocumentacion>() {
					public int compare(AfiDocumentacion o1, AfiDocumentacion o2) {
						return o1.getFecha_baja().compareTo(o2.getFecha_baja());
					}
				});

		if (!((fechaDesde.after(primeroVigente.getFecha_ingre()) || fechaDesde
				.equals(primeroVigente.getFecha_ingre()))
				&& (ultimoVigente.getFecha_baja() == null
						|| fechaHasta.before(ultimoVigente.getFecha_baja()) || fechaHasta
						.equals(ultimoVigente.getFecha_baja())))) {
			mensajeDocumento = "CERTIFICADO VIGENTE DESDE "
					+ primeroVigente.getFecha_ingreAsString() + " HASTA "
					+ ultimoVigente.getFecha_bajaAsString();
		}
		return mensajeDocumento;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cmd = ParamUtil.getString(renderRequest, "accionOriginal");		
		
		if (cmd.equals(Constants.DELETE)) {
			borraTratamientoDiscapacidadEntry(renderRequest);
		}

		String mensaje = validarDocumentacionDiscapacidad(renderRequest, cmd);
		
		int id_tratamiento = 0;
		if (mensaje.length() == 0) {
			try {
				if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
					id_tratamiento = updateTratamientoEntry(renderRequest, cmd);
				} else  if (cmd.equalsIgnoreCase("estado")) {
					id_tratamiento = cambioEstadoTratamientoEntry(renderRequest);
				}
			} catch (DuplicateTratamientoDiscapacidadIdException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			} catch (Exception e) {
				_log.error(e);
				e.printStackTrace();
				SessionErrors.add(renderRequest, Exception.class.getName());
			}
		} else {
			renderRequest.setAttribute("mensajeCertificado", mensaje);
			return mapping
					.findForward("portlet.liquidaciones.tratamiento_discapacidad.error");
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
			renderRequest.setAttribute("id_tratamiento", id_tratamiento);
		}
		return mapping
				.findForward("portlet.liquidaciones.tratamiento_discapacidad.result");
	}

	private int updateTratamientoEntry(RenderRequest actionRequest, String cmd)
			throws PortalException, SystemException,
			DuplicatePrestadorIdException,
			DuplicateTratamientoDiscapacidadIdException {

		int id_tratamiento = ParamUtil.getInteger(actionRequest,
				"id_tratamiento", 0);
		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		String cuil = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0")
				.equals("") ? "0" : ParamUtil.getString(actionRequest,
				"cantidad", "0");
		String importe_total = ParamUtil.getString(actionRequest,
				"importe_total", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "importe_total", "0");
		String periodicidad = ParamUtil.getString(actionRequest,
				"periodicidad", null).equals("") ? null : ParamUtil.getString(
				actionRequest, "periodicidad", null);
		String periodo_desde = ParamUtil.getString(actionRequest,
				"periodo_desde", "") == null ? "" : ParamUtil.getString(
				actionRequest, "periodo_desde", "");
		String periodo_hasta = ParamUtil.getString(actionRequest,
				"periodo_hasta", "") == null ? "" : ParamUtil.getString(
				actionRequest, "periodo_hasta", "");

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(actionRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(actionRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(actionRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(actionRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(actionRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(actionRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

    	int id_prestador = ParamUtil.getInteger(actionRequest,"id_prestador", 0);
    	
		String cuitAcreedor = actionRequest.getParameter("cuit_entidad");
		String sucuAcreedor = actionRequest.getParameter("sucursal_entidad");
		String idSeccional = actionRequest.getParameter("id_seccional");
		
		//DS Inicio
		sucuAcreedor = "000";
		//DS Fin

/*		
		if (StringUtils.checkNotEmpty(idSeccional)
				&& Integer.parseInt(idSeccional) != 0) {
			sucuAcreedor = "000";
		}
*/
		String observaciones = ParamUtil.getString(actionRequest,
				"observaciones", null);
		boolean recupera_ape = Boolean.valueOf(ParamUtil.getString(
				actionRequest, "recupera_ape", "false"));
		int estado = ParamUtil.getInteger(actionRequest, "estado", 0);
		String documentacion = ParamUtil.getString(actionRequest,
				"documentacion", "null");

		String cantidad_viajes_mes = ParamUtil.getString(actionRequest,
				"cantidad_viajes_mes", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_viajes_mes", "0");
		String cantidad_kilometros_dia = ParamUtil.getString(actionRequest,
				"cantidad_kilometros_dia", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_kilometros_dia", "0");
		String cantidad_kilometros_mes = ParamUtil.getString(actionRequest,
				"cantidad_kilometros_mes", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "cantidad_kilometros_mes", "0");
		String importe_kilometro_unit = ParamUtil.getString(actionRequest,
				"importe_kilometro_unit", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_kilometro_unit", "0");
		String hs_espera_dia = ParamUtil.getString(actionRequest,
				"hs_espera_dia", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "hs_espera_dia", "0");
		String hs_espera_mes = ParamUtil.getString(actionRequest,
				"hs_espera_mes", "0").equals("") ? "0" : ParamUtil.getString(
				actionRequest, "hs_espera_mes", "0");
		String importe_hs_espera_unit = ParamUtil.getString(actionRequest,
				"importe_hs_espera_unit", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_hs_espera_unit", "0");

		String importe_tercerizado = ParamUtil.getString(actionRequest,
				"importe_tercerizado", "0").equals("") ? "0" : ParamUtil
				.getString(actionRequest, "importe_tercerizado", "0");
		String id_tercerizadora = ParamUtil.getString(actionRequest,
				"id_tercerizadora", "");

		String es_excepcion = ParamUtil.getString(actionRequest,
				"es_excepcion", "");
		
		User user = PortalUtil.getUser(actionRequest);
		int idTratamiento = id_tratamiento;
		if (cmd.equals(Constants.ADD)) {
			if (documentacion.equalsIgnoreCase("null")) {
				estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO;
			} else {
				estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE;
			}
			idTratamiento = TratamientoDiscapacidadServiceUtil.save(
					id_prestacion, cuil, inte, cantidad, importe_total,
					periodicidad, fechaDesde, fechaHasta, user, cuitAcreedor,
					sucuAcreedor, idSeccional, observaciones, recupera_ape,
					estado, documentacion, cantidad_viajes_mes,
					cantidad_kilometros_dia, cantidad_kilometros_mes,
					importe_kilometro_unit, hs_espera_dia, hs_espera_mes,
					importe_hs_espera_unit, importe_tercerizado,
					id_tercerizadora,id_prestador,es_excepcion);
			actionRequest.setAttribute("id_tratamiento", String
					.valueOf(idTratamiento));

		} else {
			idTratamiento = ParamUtil.getInteger(actionRequest,
					"id_tratamiento");
			TratamientoDiscapacidadServiceUtil.update(id_tratamiento,
					id_prestacion, cuil, inte, cantidad, importe_total,
					periodicidad, fechaDesde, fechaHasta, user, cuitAcreedor,
					sucuAcreedor, idSeccional, observaciones, recupera_ape,
					estado, documentacion, cantidad_viajes_mes,
					cantidad_kilometros_dia, cantidad_kilometros_mes,
					importe_kilometro_unit, hs_espera_dia, hs_espera_mes,
					importe_hs_espera_unit, importe_tercerizado,
					id_tercerizadora,id_prestador,es_excepcion);
		}
		return idTratamiento;
	}

	// POSIBLES VALIDACIONES EN UN FUTURO
	private StringBuilder getTopeCantIndivError(
			TopeCantidadIndividualExedidoException e) {
		StringBuilder error = new StringBuilder();
		error
				.append("La cantidad por reintegro no puede ser excedida. Cantidad ingresada:");
		error.append(e.getCantidad());
		error.append(" - Cantidad max.:");
		error.append(e.getTopeIndivCant());
		return error;
	}

	private StringBuilder getPrestacionHechaError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La prestación ya fue realizada al afiliado y no puede hacerse dos veces");
		return error;
	}

	private StringBuilder getFechasError() {
		StringBuilder error = new StringBuilder();
		error
				.append("La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado");
		return error;
	}

	private void putError(ActionRequest actionRequest, StringBuilder error) {
		actionRequest.setAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT, error
				.toString());
	}

	protected void borraTratamientoDiscapacidadEntry(RenderRequest renderRequest)
			throws Exception {
		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);
		User user = PortalUtil.getUser(renderRequest);
		TratamientoDiscapacidadServiceUtil.borrar(id_tratamiento, user);
	}

	protected int cambioEstadoTratamientoEntry(RenderRequest renderRequest)
			throws Exception {
		int id_tratamiento = ParamUtil.getInteger(renderRequest,
				"id_tratamiento", 0);
		int id_estado = ParamUtil.getInteger(renderRequest, "estado", 0);
		User user = PortalUtil.getUser(renderRequest);
		TratamientoDiscapacidadServiceUtil.cambiarEstadoTratamiento(
				id_tratamiento, id_estado, user.getScreenName());
		return id_tratamiento;
	}

}
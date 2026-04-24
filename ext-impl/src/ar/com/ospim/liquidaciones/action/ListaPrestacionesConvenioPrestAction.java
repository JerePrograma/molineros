package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.prestadores.exception.ProfesionEspecialidadSubEspecPrestadorException;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacional;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle.ESTADOS;
import ar.com.ospim.prestadores.services.ConvenioPrestacionalServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaPrestacionesConvenioPrestAction extends PortletAction {

	private static final Log _log = LogFactoryUtil.getLog(ListaPrestacionesConvenioPrestAction.class);
	private static final AtomicInteger TEMP_ID_SEQUENCE = new AtomicInteger(-1);
	private static final String ERROR_KEY_DETALLE_VALIDACION = "conv-prest-validaciones";
	private static final String ATTR_MSG_CONVENIO_FAIL = "msgConvenioFail";

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
			throws Exception {

		_log.info("[LISTA-CONV-PREST][RENDER][START] Inicio render ListaPrestacionesConvenioPrestAction");

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		Prestacion prest = null;

		boolean validaConvPrestDetalle = true;
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		ArrayList<ConvenioPrestacionalDetalle> detalles = null;

		_log.info("[LISTA-CONV-PREST][RENDER][PARAMS] cmd=" + cmd);
		_log.debug("[LISTA-CONV-PREST][RENDER][SESSION] sessionId=" + (session != null ? session.getId() : "null"));

		// agrega una prestacion al convenio
		if (cmd != null && cmd.equalsIgnoreCase(Constants.ADD)) {
			_log.info("[LISTA-CONV-PREST][ADD][START] Inicia flujo ADD");

			Integer idPrestacion = ParamUtil.getInteger(renderRequest, "idPrestacion");
			String codigoPrest = ParamUtil.getString(renderRequest, "codigo");

			String fechaDesdeFinal = ParamUtil.getString(renderRequest, "fechaDesde");
			String fechaHastaFinal = ParamUtil.getString(renderRequest, "fechaHasta");
			String servicio = ParamUtil.getString(renderRequest, "servicio");
			int idPlan = ParamUtil.getInteger(renderRequest, "planId");
			String planDescripcion = ParamUtil.getString(renderRequest, "planDesc");
			String tipoValorizacion = ParamUtil.getString(renderRequest, "tipoValorizacion");

			String coseguroAux = ParamUtil.getString(renderRequest, "coseguro");
			BigDecimal coseguro = new BigDecimal(StringUtils.checkNotEmpty(coseguroAux) ? coseguroAux : "0");

			String importeAux = ParamUtil.getString(renderRequest, "importe");
			String porcentajeAux = ParamUtil.getString(renderRequest, "porcentaje");
			BigDecimal importe = new BigDecimal(StringUtils.checkNotEmpty(importeAux) ? importeAux : "0");
			BigDecimal porcentaje = new BigDecimal(StringUtils.checkNotEmpty(porcentajeAux) ? porcentajeAux : "0");

			_log.debug("[LISTA-CONV-PREST][ADD][PARAMS] idPrestacion=" + idPrestacion
					+ ", codigoPrest=" + codigoPrest
					+ ", fechaDesde=" + fechaDesdeFinal
					+ ", fechaHasta=" + fechaHastaFinal
					+ ", servicio=" + servicio
					+ ", idPlan=" + idPlan
					+ ", planDescripcion=" + planDescripcion
					+ ", tipoValorizacion=" + tipoValorizacion
					+ ", coseguroAux=" + coseguroAux
					+ ", importeAux=" + importeAux
					+ ", porcentajeAux=" + porcentajeAux);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
				_log.debug("[LISTA-CONV-PREST][ADD][PARSE-FECHA] fechaDesde parseada correctamente: " + fechaDesde);
			} catch (Exception e) {
				fechaDesde = null;
				_log.error("[LISTA-CONV-PREST][ADD][PARSE-FECHA][ERROR] Error parseando fechaDesde: " + fechaDesdeFinal, e);
			}

			Date fechaHasta = null;
			try {
				if (StringUtils.checkNotEmpty(fechaHastaFinal)) {
					fechaHasta = sdf.parse(fechaHastaFinal);
					_log.debug("[LISTA-CONV-PREST][ADD][PARSE-FECHA] fechaHasta parseada correctamente: " + fechaHasta);
				}
			} catch (Exception e) {
				fechaHasta = null;
				_log.error("[LISTA-CONV-PREST][ADD][PARSE-FECHA][ERROR] Error parseando fechaHasta: " + fechaHastaFinal, e);
			}

			if (fechaDesde == null) {
				throw new IllegalArgumentException("Debe informar fecha desde");
			}
			if (idPrestacion == null || idPrestacion.intValue() <= 0) {
				throw new IllegalArgumentException("Debe informar la prestación");
			}
			if (StringUtils.checkEmpty(codigoPrest)) {
				throw new IllegalArgumentException("Debe informar el código");
			}

			String descripcionPrestacion = ConvenioPrestacionalServiceUtil.getDescripcionPrestacionPorCodigo(codigoPrest);

			prest = new Prestacion(
					idPrestacion,
					StringUtils.checkNotEmpty(descripcionPrestacion) ? descripcionPrestacion : ""
			);

			ConvenioPrestacionalDetalle convPrestDetalle = new ConvenioPrestacionalDetalle();
			convPrestDetalle.setId(generarIdTemporal());
			convPrestDetalle.setIdConvenioPrestacional(0);
			convPrestDetalle.setFechaDesde(fechaDesde);
			convPrestDetalle.setFechaHasta(fechaHasta);
			convPrestDetalle.setPrestacion(prest);
			convPrestDetalle.setCodigo(codigoPrest);
			convPrestDetalle.setIdPlan(idPlan);
			convPrestDetalle.setPlanDescripcion(planDescripcion);
			convPrestDetalle.setCoseguro(coseguro);
			convPrestDetalle.setTipoValorizacion(StringUtils.checkNotEmpty(tipoValorizacion) ? tipoValorizacion : "importe");
			convPrestDetalle.setImporte(importe);
			convPrestDetalle.setPorcentaje(porcentaje);
			convPrestDetalle.setServicio(StringUtils.checkNotEmpty(servicio) ? servicio : "0");
			convPrestDetalle.setEstado(ConvenioPrestacionalDetalle.ESTADOS.NUEVO);

			_log.debug("[LISTA-CONV-PREST][ADD][OBJ] convPrestDetalle=" + convPrestDetalle);

			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			_log.debug("[LISTA-CONV-PREST][ADD][SESSION-GET] detalles en session="
					+ (detalles != null ? detalles.size() : "null"));

			if (detalles == null) {
				detalles = new ArrayList<ConvenioPrestacionalDetalle>();
				_log.debug("[LISTA-CONV-PREST][ADD][SESSION-INIT] Se inicializa lista detalles vacía");
			}

			try {
				_log.debug("[LISTA-CONV-PREST][ADD][VALIDACION] Invocando validaDetalleConvPrest");

				validaDetalleConvPrest(detalles, convPrestDetalle);

				normalizarVigenciasMismaClave(detalles, convPrestDetalle);
				detalles.add(convPrestDetalle);
				ordenarDetalles(detalles);

				_log.debug("[LISTA-CONV-PREST][ADD][LISTA] Detalle agregado. Nuevo tamaño=" + detalles.size());

			} catch (ProfesionEspecialidadSubEspecPrestadorException e) {
				registrarErrorValidacionDetalle(
						renderRequest,
						"Ya existe un detalle cargado con la misma prestación y plan. Sólo se permite agregar una nueva versión con fecha desde futura.",
						e
				);
			}

			_log.debug("[LISTA-CONV-PREST][ADD][SESSION-SET] Reemplazando lista en session con tamaño="
					+ (detalles != null ? detalles.size() : "null"));
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);

			_log.info("[LISTA-CONV-PREST][ADD][END] Fin flujo ADD");
		}

		// copia todas las prestaciones del convenio de otro prestador en este
		if (cmd != null && cmd.equalsIgnoreCase(Constants.COPY)) {
			_log.info("[LISTA-CONV-PREST][COPY][START] Inicia flujo COPY");

			int idPrestador = ParamUtil.getInteger(renderRequest, "id_prestador");
			_log.debug("[LISTA-CONV-PREST][COPY][PARAMS] id_prestador=" + idPrestador);

			ConvenioPrestacional convenioPrest = ConvenioPrestacionalServiceUtil
					.getConvenioPrestacionalPorPrestador(idPrestador);
			_log.debug("[LISTA-CONV-PREST][COPY][SERVICE] convenioPrest obtenido=" + (convenioPrest != null));

			List<ConvenioPrestacionalDetalle> origen = convenioPrest != null
					? convenioPrest.getConvenioPrestDetalle()
					: null;

			_log.debug("[LISTA-CONV-PREST][COPY][DETALLES-ORIGEN] Cantidad detalles origen="
					+ (origen != null ? origen.size() : "null"));

			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			_log.debug("[LISTA-CONV-PREST][COPY][SESSION-GET] detalles en session="
					+ (detalles != null ? detalles.size() : "null"));

			if (detalles == null) {
				detalles = new ArrayList<ConvenioPrestacionalDetalle>();
				_log.debug("[LISTA-CONV-PREST][COPY][SESSION-INIT] No había detalles en session. Se crea nueva lista");
			}

			if (origen != null) {
				for (ConvenioPrestacionalDetalle detOrigen : origen) {
					ConvenioPrestacionalDetalle detAux = copiarDetalle(detOrigen);
					_log.debug("[LISTA-CONV-PREST][COPY][VALIDACION] Procesando detalle copiado=" + detAux);

					try {
						validaDetalleConvPrest(detalles, detAux);
						normalizarVigenciasMismaClave(detalles, detAux);
						detalles.add(detAux);
						ordenarDetalles(detalles);
					} catch (ProfesionEspecialidadSubEspecPrestadorException e) {
						registrarErrorValidacionDetalle(
								renderRequest,
								"No se pudo copiar una prestación porque entra en conflicto con una vigencia ya cargada para la misma prestación y plan.",
								e
						);
					}
				}
			}

			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);
			_log.debug("[LISTA-CONV-PREST][COPY][SESSION-SET] Lista guardada en session con tamaño="
					+ (detalles != null ? detalles.size() : "null"));

			_log.info("[LISTA-CONV-PREST][COPY][END] Fin flujo COPY");
		}

		// elimina una prestación del convenio:
		// - si es nueva, la quita de la lista en session
		// - si ya estaba persistida, la marca para borrado físico al guardar
		if (cmd != null && cmd.equalsIgnoreCase(Constants.DELETE)) {
			_log.info("[LISTA-CONV-PREST][DELETE][START] Inicia flujo DELETE");

			int idConvPrestDet = ParamUtil.getInteger(renderRequest, "id_convprest_det");
			_log.debug("[LISTA-CONV-PREST][DELETE][PARAMS] id_convprest_det=" + idConvPrestDet);

			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			_log.debug("[LISTA-CONV-PREST][DELETE][SESSION-GET] detalles en session="
					+ (detalles != null ? detalles.size() : "null"));

			if (detalles == null || detalles.isEmpty()) {
				_log.warn("[LISTA-CONV-PREST][DELETE][WARN] No hay detalles en session para eliminar");
			} else {
				ConvenioPrestacionalDetalle auxDet = new ConvenioPrestacionalDetalle();
				auxDet.setId(idConvPrestDet);

				int pos = detalles.indexOf(auxDet);
				_log.debug("[LISTA-CONV-PREST][DELETE][BUSQUEDA] Posición encontrada=" + pos);

				if (pos >= 0) {
					auxDet = detalles.get(pos);
					_log.debug("[LISTA-CONV-PREST][DELETE][DETALLE] Detalle encontrado=" + auxDet);

					if (auxDet.getEstado() == null) {
						_log.debug("[LISTA-CONV-PREST][DELETE][ACCION] Detalle persistido. Se marca BAJA para borrado físico en persistencia");
						auxDet.setEstado(ESTADOS.BAJA);
					} else {
						_log.debug("[LISTA-CONV-PREST][DELETE][ACCION] Detalle no persistido. Se elimina de la lista");
						detalles.remove(pos);
					}

					session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
					session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);
					_log.debug("[LISTA-CONV-PREST][DELETE][SESSION-SET] Lista guardada en session con tamaño="
							+ (detalles != null ? detalles.size() : "null"));
				} else {
					_log.warn("[LISTA-CONV-PREST][DELETE][WARN] No se encontró detalle con id=" + idConvPrestDet);
				}
			}

			_log.info("[LISTA-CONV-PREST][DELETE][END] Fin flujo DELETE");
		}

		_log.info("[LISTA-CONV-PREST][RENDER][END] Fin render. Forward=portlet.liquidaciones.lista_convenio_prest_detalle");
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lista_convenio_prest_detalle"));
	}

	private boolean validaDetalleConvPrest(List<ConvenioPrestacionalDetalle> detalles,
										   ConvenioPrestacionalDetalle convPrestDet)
			throws ProfesionEspecialidadSubEspecPrestadorException {

		_log.debug("[LISTA-CONV-PREST][VALIDA][START] Inicio validación detalle");
		_log.debug("[LISTA-CONV-PREST][VALIDA][INPUT] cantidadDetalles="
				+ (detalles != null ? detalles.size() : "null")
				+ ", convPrestDet=" + convPrestDet);

		if (convPrestDet == null) {
			_log.debug("[LISTA-CONV-PREST][VALIDA][END] convPrestDet null");
			return false;
		}

		if (convPrestDet.getPrestacion() == null || convPrestDet.getPrestacion().getId() <= 0) {
			_log.warn("[LISTA-CONV-PREST][VALIDA][WARN] Detalle sin prestación válida");
			throw new ProfesionEspecialidadSubEspecPrestadorException();
		}

		Date fechaNueva = truncarFecha(convPrestDet.getFechaDesde());
		Date fechaHastaNueva = truncarFecha(convPrestDet.getFechaHasta());

		if (fechaNueva == null) {
			_log.warn("[LISTA-CONV-PREST][VALIDA][WARN] Detalle sin fechaDesde válida");
			throw new ProfesionEspecialidadSubEspecPrestadorException();
		}

		if (fechaHastaNueva != null && fechaHastaNueva.before(fechaNueva)) {
			_log.warn("[LISTA-CONV-PREST][VALIDA][RECHAZO] fechaHasta < fechaDesde. nuevo=" + convPrestDet);
			throw new ProfesionEspecialidadSubEspecPrestadorException();
		}

		List<ConvenioPrestacionalDetalle> mismaClave = obtenerDetallesMismaClaveNoBaja(detalles, convPrestDet);

		if (mismaClave.isEmpty()) {
			_log.debug("[LISTA-CONV-PREST][VALIDA][END] No existe misma clave funcional. result=true");
			return true;
		}

		Date hoy = truncarFecha(new Date());

		// Si ya existe esa clave en session, sólo permito agregar nuevas versiones con fecha futura.
		if (!fechaNueva.after(hoy)) {
			_log.warn("[LISTA-CONV-PREST][VALIDA][RECHAZO] Misma clave funcional con fecha_desde <= hoy. nuevo="
					+ convPrestDet + ", hoy=" + hoy);
			throw new ProfesionEspecialidadSubEspecPrestadorException();
		}

		ordenarPorFechaDesde(mismaClave);

		for (ConvenioPrestacionalDetalle existente : mismaClave) {
			Date fechaExistente = truncarFecha(existente.getFechaDesde());

			if (fechaExistente == null) {
				continue;
			}

			_log.debug("[LISTA-CONV-PREST][VALIDA][MATCH] Misma clave funcional detectada. existente="
					+ existente + ", nuevo=" + convPrestDet);

			if (mismaFecha(fechaExistente, fechaNueva)) {
				_log.warn("[LISTA-CONV-PREST][VALIDA][RECHAZO] Ya existe detalle con misma clave y misma fechaDesde. existente="
						+ existente + ", nuevo=" + convPrestDet);
				throw new ProfesionEspecialidadSubEspecPrestadorException();
			}
		}

		// Valido la cadena resultante con el nuevo detalle incluido
		List<ConvenioPrestacionalDetalle> cadenaValidacion = new ArrayList<ConvenioPrestacionalDetalle>(mismaClave);
		cadenaValidacion.add(convPrestDet);

		ordenarPorFechaDesde(cadenaValidacion);

		for (int i = 0; i < cadenaValidacion.size() - 1; i++) {
			ConvenioPrestacionalDetalle actual = cadenaValidacion.get(i);
			ConvenioPrestacionalDetalle siguiente = cadenaValidacion.get(i + 1);

			Date desdeActual = truncarFecha(actual.getFechaDesde());
			Date desdeSiguiente = truncarFecha(siguiente.getFechaDesde());

			if (desdeActual == null || desdeSiguiente == null) {
				continue;
			}

			if (!desdeSiguiente.after(desdeActual)) {
				_log.warn("[LISTA-CONV-PREST][VALIDA][RECHAZO] Cadena temporal inválida por fechas no crecientes. actual="
						+ actual + ", siguiente=" + siguiente);
				throw new ProfesionEspecialidadSubEspecPrestadorException();
			}

			Date fechaHastaEsperada = diaAnterior(desdeSiguiente);

			if (fechaHastaEsperada.before(desdeActual)) {
				_log.warn("[LISTA-CONV-PREST][VALIDA][RECHAZO] Cadena temporal inválida. actual="
						+ actual + ", siguiente=" + siguiente
						+ ", fechaHastaEsperada=" + fechaHastaEsperada);
				throw new ProfesionEspecialidadSubEspecPrestadorException();
			}
		}

		_log.debug("[LISTA-CONV-PREST][VALIDA][END] Nueva versión válida para misma clave funcional. result=true");
		return true;
	}

	private ConvenioPrestacionalDetalle copiarDetalle(ConvenioPrestacionalDetalle origen) {
		ConvenioPrestacionalDetalle copia = new ConvenioPrestacionalDetalle();

		copia.setId(generarIdTemporal());
		copia.setIdConvenioPrestacional(0);
		copia.setFechaDesde(origen.getFechaDesde());
		copia.setFechaHasta(origen.getFechaHasta());
		copia.setPrestacion(origen.getPrestacion());
		copia.setCodigo(origen.getCodigo());
		copia.setIdPlan(origen.getIdPlan());
		copia.setPlanDescripcion(origen.getPlanDescripcion());
		copia.setCoseguro(origen.getCoseguro());
		copia.setTipoValorizacion(origen.getTipoValorizacion());
		copia.setImporte(origen.getImporte());
		copia.setPorcentaje(origen.getPorcentaje());
		copia.setServicio(origen.getServicio());
		copia.setEstado(ESTADOS.NUEVO);

		return copia;
	}

	private int generarIdTemporal() {
		return TEMP_ID_SEQUENCE.getAndDecrement();
	}

	private boolean seSuperponenFechas(Date desde1, Date hasta1, Date desde2, Date hasta2) {
		if (desde1 == null || desde2 == null) {
			return false;
		}

		Date fin1 = (hasta1 != null) ? hasta1 : new Date(Long.MAX_VALUE);
		Date fin2 = (hasta2 != null) ? hasta2 : new Date(Long.MAX_VALUE);

		return !desde1.after(fin2) && !desde2.after(fin1);
	}

	private boolean mismaClaveFuncional(ConvenioPrestacionalDetalle a, ConvenioPrestacionalDetalle b) {
		if (a == null || b == null) {
			return false;
		}

		if (a.getPrestacion() == null || b.getPrestacion() == null) {
			return false;
		}

		return a.getPrestacion().getId() == b.getPrestacion().getId()
				&& a.getIdPlan() == b.getIdPlan();
	}

	private Date truncarFecha(Date fecha) {
		if (fecha == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private void normalizarVigenciasMismaClave(List<ConvenioPrestacionalDetalle> detalles,
											   ConvenioPrestacionalDetalle nuevoDetalle) {

		if (nuevoDetalle == null) {
			return;
		}

		Date fechaNueva = truncarFecha(nuevoDetalle.getFechaDesde());
		if (fechaNueva == null) {
			return;
		}

		List<ConvenioPrestacionalDetalle> mismaClave = obtenerDetallesMismaClaveNoBaja(detalles, nuevoDetalle);
		mismaClave.add(nuevoDetalle);

		ordenarPorFechaDesde(mismaClave);

		for (int i = 0; i < mismaClave.size(); i++) {
			ConvenioPrestacionalDetalle actual = mismaClave.get(i);

			if (actual == null) {
				continue;
			}

			Date fechaDesdeActual = truncarFecha(actual.getFechaDesde());
			if (fechaDesdeActual == null) {
				continue;
			}

			if (i < mismaClave.size() - 1) {
				ConvenioPrestacionalDetalle siguiente = mismaClave.get(i + 1);
				Date fechaDesdeSiguiente = truncarFecha(siguiente != null ? siguiente.getFechaDesde() : null);

				if (fechaDesdeSiguiente != null) {
					Date nuevaFechaHasta = diaAnterior(fechaDesdeSiguiente);
					actual.setFechaHasta(nuevaFechaHasta);

					_log.debug("[LISTA-CONV-PREST][NORMALIZA] Ajustando vigencia. actual="
							+ actual + ", siguiente=" + siguiente
							+ ", nuevaFechaHasta=" + nuevaFechaHasta);
				}
			} else {
				// el último queda abierto
				actual.setFechaHasta(null);

				_log.debug("[LISTA-CONV-PREST][NORMALIZA] Última versión abierta. actual=" + actual);
			}
		}
	}

	private Date diaAnterior(Date fecha) {
		if (fecha == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return truncarFecha(cal.getTime());
	}

	private void ordenarDetalles(List<ConvenioPrestacionalDetalle> detalles) {
		if (detalles == null || detalles.isEmpty()) {
			return;
		}

		Collections.sort(detalles, new Comparator<ConvenioPrestacionalDetalle>() {
			public int compare(ConvenioPrestacionalDetalle a, ConvenioPrestacionalDetalle b) {

				int cmpPrest = compararPrestacion(a, b);
				if (cmpPrest != 0) {
					return cmpPrest;
				}

				int planA = (a != null ? a.getIdPlan() : 0);
				int planB = (b != null ? b.getIdPlan() : 0);
				if (planA != planB) {
					return planA < planB ? -1 : 1;
				}

				Date fechaA = truncarFecha(a != null ? a.getFechaDesde() : null);
				Date fechaB = truncarFecha(b != null ? b.getFechaDesde() : null);

				if (fechaA == null && fechaB == null) return 0;
				if (fechaA == null) return -1;
				if (fechaB == null) return 1;
				return fechaA.compareTo(fechaB);
			}
		});
	}

	private int compararPrestacion(ConvenioPrestacionalDetalle a, ConvenioPrestacionalDetalle b) {
		int idA = 0;
		int idB = 0;

		if (a != null && a.getPrestacion() != null) {
			idA = a.getPrestacion().getId();
		}
		if (b != null && b.getPrestacion() != null) {
			idB = b.getPrestacion().getId();
		}

		if (idA == idB) {
			return 0;
		}

		return idA < idB ? -1 : 1;
	}

	private List<ConvenioPrestacionalDetalle> obtenerDetallesMismaClaveNoBaja(
			List<ConvenioPrestacionalDetalle> detalles,
			ConvenioPrestacionalDetalle referencia) {

		List<ConvenioPrestacionalDetalle> out = new ArrayList<ConvenioPrestacionalDetalle>();

		if (detalles == null || referencia == null) {
			return out;
		}

		for (ConvenioPrestacionalDetalle det : detalles) {
			if (det == null) {
				continue;
			}

			if (det.getId() == referencia.getId()) {
				continue;
			}

			if (ESTADOS.BAJA.equals(det.getEstado()) || det.getBajaFecha() != null) {
				continue;
			}

			if (!mismaClaveFuncional(det, referencia)) {
				continue;
			}

			out.add(det);
		}

		return out;
	}

	private void ordenarPorFechaDesde(List<ConvenioPrestacionalDetalle> detalles) {
		if (detalles == null || detalles.isEmpty()) {
			return;
		}

		Collections.sort(detalles, new Comparator<ConvenioPrestacionalDetalle>() {
			public int compare(ConvenioPrestacionalDetalle a, ConvenioPrestacionalDetalle b) {
				Date fa = truncarFecha(a != null ? a.getFechaDesde() : null);
				Date fb = truncarFecha(b != null ? b.getFechaDesde() : null);

				if (fa == null && fb == null) return 0;
				if (fa == null) return -1;
				if (fb == null) return 1;
				return fa.compareTo(fb);
			}
		});
	}

	private boolean mismaFecha(Date a, Date b) {
		Date ta = truncarFecha(a);
		Date tb = truncarFecha(b);

		if (ta == null && tb == null) return true;
		if (ta == null || tb == null) return false;

		return ta.equals(tb);
	}

	private void registrarErrorValidacionDetalle(RenderRequest renderRequest, String mensaje, Exception e) {
		_log.error("[LISTA-CONV-PREST][VALIDACION][ERROR] " + mensaje, e);
		renderRequest.setAttribute(ATTR_MSG_CONVENIO_FAIL, mensaje);
		SessionErrors.add(renderRequest, ERROR_KEY_DETALLE_VALIDACION);
	}
}
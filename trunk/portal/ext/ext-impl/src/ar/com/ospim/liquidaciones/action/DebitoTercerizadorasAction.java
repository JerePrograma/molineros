package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal;
import ar.com.ospim.liquidaciones.services.BusquedaDebitosTercerizadorasServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class DebitoTercerizadorasAction extends PortletAction {

	private static final Logger _log = Logger.getLogger(DebitoTercerizadorasAction.class);

	// Session / Request keys
	private static final String SESSION_TOTALES_KEY = "BUSQUEDA_DEBITOS_TERCERIZADORAS_TOTALES";
	private static final String REQ_TIPO_MAP_KEY = "DEBITOS_TIPO_MAP";
	private static final String REQ_TIPO_SELECTED_KEY = "DEBITOS_TIPO_SELECTED";
	private static final String REQ_DETALLE_KEY = "DEBITOS_DETALLE_RESULTADOS";
	private static final String REQ_TOTALES_SESSION_KEY = "DEBITOS_TOTALES_SESSION_KEY";

	// Forward
	private static final String FWD_SEARCH_RESULT = "portlet.liquidaciones.debitos_tercerizadoras_search_result";

	// Debug controls
	private static final String PARAM_DBG = "dbg";     // ?dbg=true
	private static final String PARAM_DUMP = "dump";   // ?dump=true (dump ALL request params)
	private static final int DETAIL_PREVIEW_MAX = 10;  // log first N items only

	@Override
	public void processAction(
			ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		// No-op (por ahora)
	}

	@Override
	public ActionForward render(
			ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		final String rid = newRid();
		final long t0 = System.currentTimeMillis();

		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = httpReq.getSession();

		boolean dbg = ParamUtil.getBoolean(renderRequest, PARAM_DBG, false);
		boolean dumpAllParams = ParamUtil.getBoolean(renderRequest, PARAM_DUMP, false);

		User usuario = null;
		try {
			usuario = PortalUtil.getUser(renderRequest);
		} catch (SystemException e) {
			_log.debug(prefix(rid) + "PortalUtil.getUser SystemException: " + e.getMessage(), e);
		} catch (PortalException e) {
			_log.debug(prefix(rid) + "PortalUtil.getUser PortalException: " + e.getMessage(), e);
		}

		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);

		_log.info(prefix(rid) + "[RENDER] mapping.path=" + mapping.getPath()
				+ " mapping.type=" + mapping.getType()
				+ " cmd=" + cmd
				+ " user=" + (usuario != null ? usuario.getScreenName() : "null")
				+ " tipoDebito.param=" + safe(ParamUtil.getString(renderRequest, "tipoDebito", "LI"))
				+ " fechaDesdeMes=" + safe(ParamUtil.getString(renderRequest, "fechaDesdeMes"))
				+ " fechaDesdeAnio=" + safe(ParamUtil.getString(renderRequest, "fechaDesdeAnio")));

		if (dbg) {
			logMappingForwards(mapping, rid);
			logSessionSnapshot(session, rid, 30);
			if (dumpAllParams) {
				dumpAllRequestParams(renderRequest, rid);
			}
		}

		// Tipos (dropdown)
		Map tiposMap = parseTiposConfig(getConfigDebitosTercerizadorasTipoRaw());
		renderRequest.setAttribute(REQ_TIPO_MAP_KEY, tiposMap);

		String tipoDebito = ParamUtil.getString(renderRequest, "tipoDebito", "LI");
		if (!tiposMap.containsKey(tipoDebito)) {
			_log.warn(prefix(rid) + "tipoDebito inválido='" + tipoDebito + "'. Se fuerza a LI.");
			tipoDebito = "LI";
		}
		renderRequest.setAttribute(REQ_TIPO_SELECTED_KEY, tipoDebito);

		// para JSP
		renderRequest.setAttribute(REQ_TOTALES_SESSION_KEY, SESSION_TOTALES_KEY);

		if (!StringUtils.checkEmpty(cmd) && Constants.SEARCH.equals(cmd)) {
			doSearch(renderRequest, session, tipoDebito, rid, dbg);
		} else {
			if (dbg) {
				_log.info(prefix(rid) + "Render sin SEARCH (cmd=" + cmd + "). No se ejecuta doSearch().");
			}
		}

		ActionForward fwd = debugFindForward(mapping, FWD_SEARCH_RESULT, rid, dbg);

		long t1 = System.currentTimeMillis();
		_log.info(prefix(rid) + "[RENDER-END] elapsedMs=" + (t1 - t0));

		return fwd;
	}

	private void doSearch(RenderRequest renderRequest, HttpSession session, String tipoDebito, String rid, boolean dbg) {
		final long t0 = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Params principales (validación explícita)
		int mes0Based = ParamUtil.getInteger(renderRequest, "fechaDesdeMes", -1);
		int anio = ParamUtil.getInteger(renderRequest, "fechaDesdeAnio", -1);

		String tercerizadoras = ParamUtil.getString(renderRequest, "tipo_debitos_tercerizadoras", "");
		String proceso = ParamUtil.getString(renderRequest, "tipo_proceso", "");

		if (mes0Based < 0 || anio <= 0) {
			_log.warn(prefix(rid) + "[SEARCH] fechaDesdeMes/fechaDesdeAnio inválidos: mes0Based=" + mes0Based + " anio=" + anio);
		}
		if (tercerizadoras == null || tercerizadoras.trim().length() == 0) {
			_log.warn(prefix(rid) + "[SEARCH] tercerizadoras VACÍO. Probable causa de resultados 0 si el service espera ID.");
		}

		// Fechas
		Date fechaDesde = null;
		Date fechaHasta = null;
		Date fechaEjecucion = null;

		try {
			if (mes0Based >= 0 && anio > 0) {
				Calendar calDesde = DateUtils.getCalendarGMTMenos3();
				calDesde.set(Calendar.YEAR, anio);
				calDesde.set(Calendar.MONTH, mes0Based);
				calDesde.set(Calendar.DAY_OF_MONTH, 1);
				calDesde.set(Calendar.HOUR_OF_DAY, 0);
				calDesde.set(Calendar.MINUTE, 0);
				calDesde.set(Calendar.SECOND, 0);
				calDesde.set(Calendar.MILLISECOND, 0);

				fechaDesde = calDesde.getTime();
				fechaHasta = DateUtils.getLastDateOfMonth(fechaDesde, false);
			}
		} catch (Exception e) {
			_log.error(prefix(rid) + "[SEARCH] Error calculando fechaDesde/fechaHasta", e);
			fechaDesde = null;
			fechaHasta = null;
		}

		try {
			Calendar calEjec = DateUtils.getCalendarGMTMenos3();
			calEjec.add(Calendar.MONTH, -1);
			fechaEjecucion = calEjec.getTime();
		} catch (Exception e) {
			_log.error(prefix(rid) + "[SEARCH] Error calculando fechaEjecucion", e);
			fechaEjecucion = null;
		}

		_log.info(prefix(rid) + "[SEARCH] tipoDebito=" + tipoDebito
				+ " proceso=" + proceso
				+ " tercerizadoras=" + safe(tercerizadoras)
				+ " mes0Based=" + mes0Based
				+ " anio=" + anio
				+ " fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
				+ " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
				+ " fechaEjecucion=" + fmt(sdf, fechaEjecucion) + " (" + ms(fechaEjecucion) + ")");

		// ======= TOTALES =======
		List totales = new ArrayList();
		DebitosaTotal debitosaTotal = null;

		long tTot0 = System.currentTimeMillis();
		try {
			if (fechaHasta != null) {
				debitosaTotal = BusquedaDebitosTercerizadorasServiceUtil.getBuscarTotalesDebitos(fechaHasta, tercerizadoras);
				if (debitosaTotal != null) {
					totales.add(debitosaTotal);
				}
			} else {
				_log.warn(prefix(rid) + "[TOTALES] fechaHasta=null, no se consulta totales.");
			}
		} catch (Exception e) {
			_log.error(prefix(rid) + "[TOTALES] Error consultando totales", e);
		}
		long tTot1 = System.currentTimeMillis();

		session.setAttribute(SESSION_TOTALES_KEY, totales);

		if (debitosaTotal == null) {
			_log.warn(prefix(rid) + "[TOTALES] debitosaTotal=null (service devolvió null). elapsedMs=" + (tTot1 - tTot0));
		} else {
			boolean existe = false;
			try {
				existe = debitosaTotal.isExisteDebito();
			} catch (Exception ignore) {
				// por si isExisteDebito tira algo raro
			}

			_log.info(prefix(rid) + "[TOTALES] OK elapsedMs=" + (tTot1 - tTot0)
					+ " existeDebito=" + existe
					+ " periodo=" + safe(objToStrSafe(debitosaTotal.getPeriodo()))
					+ " idTercerizadora=" + safe(objToStrSafe(debitosaTotal.getIdTercerizadora()))
					+ " descTercerizadora=" + safe(objToStrSafe(debitosaTotal.getDescTercerizadora()))
					+ " total=" + safe(objToStrSafe(debitosaTotal.getTotal())));
		}

		// ======= DETALLE =======
		List detalle = new ArrayList();
		boolean existePersistido = (debitosaTotal != null && safeBool(debitosaTotal));

		long tDet0 = System.currentTimeMillis();
		try {
			if (fechaHasta == null) {
				_log.warn(prefix(rid) + "[DETAIL] fechaHasta=null, no se consulta detalle.");
			} else {
				if ("LI".equalsIgnoreCase(tipoDebito)) {
					detalle = existePersistido
							? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
							WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES, fechaHasta, tercerizadoras)
							: BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaLiquidacionesPendientes(
							fechaEjecucion, fechaHasta, debitosaTotal, tercerizadoras);

				} else if ("HO".equalsIgnoreCase(tipoDebito)) {
					detalle = existePersistido
							? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
							WebKeysLiquidaciones.DEBITOS_HOSPITALES, fechaHasta, tercerizadoras)
							: BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaHospitales(
							fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);

				} else if ("RE".equalsIgnoreCase(tipoDebito)) {
					detalle = existePersistido
							? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
							WebKeysLiquidaciones.DEBITOS_REINTEGROS, fechaHasta, tercerizadoras)
							: BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosReintegros(
							fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);

				} else if ("PR".equalsIgnoreCase(tipoDebito)) {
					detalle = existePersistido
							? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
							WebKeysLiquidaciones.DEBITOS_PRESTADORES, fechaHasta, tercerizadoras)
							: BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosPrestadores(
							fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
				} else {
					_log.warn(prefix(rid) + "[DETAIL] tipoDebito desconocido: " + tipoDebito);
				}
			}
		} catch (Exception e) {
			_log.error(prefix(rid) + "[DETAIL] Error consultando detalle", e);
		}
		long tDet1 = System.currentTimeMillis();

		renderRequest.setAttribute(REQ_DETALLE_KEY, detalle);

		_log.info(prefix(rid) + "[DETAIL] tipoDebito=" + tipoDebito
				+ " existePersistido=" + existePersistido
				+ " detalle.size=" + (detalle != null ? detalle.size() : -1)
				+ " elapsedMs=" + (tDet1 - tDet0));

		if (dbg) {
			previewList(detalle, rid, DETAIL_PREVIEW_MAX);
		}

		long t1 = System.currentTimeMillis();
		_log.info(prefix(rid) + "[SEARCH-END] elapsedMs=" + (t1 - t0));
	}

	// ===== config =====

	private String getConfigDebitosTercerizadorasTipoRaw() {
		// TODO: reemplazar por lectura real de system_config
		return "LI=LIQUIDACIONES;HO=HOSPITALES;RE=REINTEGROS;PR=PRESTADORES";
	}

	private Map parseTiposConfig(String raw) {
		Map out = new LinkedHashMap();
		if (raw == null) return out;

		String[] parts = raw.split(";");
		for (int i = 0; i < parts.length; i++) {
			String p = parts[i];
			if (p == null) continue;
			int idx = p.indexOf('=');
			if (idx <= 0) continue;
			String k = p.substring(0, idx).trim();
			String v = p.substring(idx + 1).trim();
			if (k.length() > 0) out.put(k, v);
		}
		return out;
	}

	// ===== forwards debug =====

	private void logMappingForwards(ActionMapping mapping, String rid) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix(rid)).append("Mapping: path=").append(mapping.getPath())
					.append(" type=").append(mapping.getType())
					.append(" actionForwardAttr=").append(mapping.getForward())
					.append("\n");

			ForwardConfig[] locals = mapping.findForwardConfigs();
			sb.append("Local forwards (").append(locals != null ? locals.length : 0).append("): ");
			if (locals != null && locals.length > 0) {
				for (int i = 0; i < locals.length; i++) {
					ForwardConfig f = locals[i];
					sb.append("[").append(f.getName()).append(" -> ").append(f.getPath()).append("] ");
				}
			} else {
				sb.append("(ninguno)");
			}
			sb.append("\n");

			ModuleConfig mc = mapping.getModuleConfig();
			ForwardConfig[] globals = (mc != null) ? mc.findForwardConfigs() : null;
			sb.append("Global forwards (").append(globals != null ? globals.length : 0).append("): ");
			if (globals != null && globals.length > 0) {
				for (int i = 0; i < globals.length; i++) {
					ForwardConfig f = globals[i];
					sb.append("[").append(f.getName()).append(" -> ").append(f.getPath()).append("] ");
				}
			} else {
				sb.append("(ninguno)");
			}

			_log.info(sb.toString());
		} catch (Exception e) {
			_log.warn(prefix(rid) + "No pude listar forwards del mapping", e);
		}
	}

	private ActionForward debugFindForward(ActionMapping mapping, String forwardName, String rid, boolean dbg) {
		if (dbg) {
			_log.info(prefix(rid) + "Buscando forward='" + forwardName + "' en mapping.path=" + mapping.getPath()
					+ " mapping.type=" + mapping.getType());
		}

		ActionForward f = mapping.findForward(forwardName);

		if (f == null) {
			_log.error(prefix(rid) + "FORWARD NO ENCONTRADO: '" + forwardName + "'");
			logMappingForwards(mapping, rid);
		} else if (dbg) {
			_log.info(prefix(rid) + "FORWARD OK: '" + forwardName + "' -> " + f.getPath());
		}
		return f;
	}

	// ===== helpers =====

	private String newRid() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	private String prefix(String rid) {
		return "[RID:" + rid + "] ";
	}

	private String fmt(SimpleDateFormat sdf, Date d) {
		return d != null ? sdf.format(d) : "null";
	}

	private String ms(Date d) {
		return d != null ? String.valueOf(d.getTime()) : "null";
	}

	private String safe(String s) {
		if (s == null) return "null";
		String t = s.trim();
		return t.length() == 0 ? "(empty)" : t;
	}

	private String objToStrSafe(Object o) {
		return (o == null) ? null : String.valueOf(o);
	}

	private boolean safeBool(DebitosaTotal dt) {
		try {
			return dt.isExisteDebito();
		} catch (Exception e) {
			return false;
		}
	}

	private void previewList(List list, String rid, int max) {
		if (list == null) {
			_log.info(prefix(rid) + "[DETAIL-PREVIEW] list=null");
			return;
		}
		int size = list.size();
		int n = Math.min(size, max);
		_log.info(prefix(rid) + "[DETAIL-PREVIEW] size=" + size + " showingFirst=" + n);

		for (int i = 0; i < n; i++) {
			Object item = list.get(i);
			_log.info(prefix(rid) + "[DETAIL-PREVIEW] #" + i + " class=" + (item != null ? item.getClass().getName() : "null")
					+ " value=" + String.valueOf(item));
		}
	}

	private void dumpAllRequestParams(RenderRequest renderRequest, String rid) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix(rid)).append("[PARAM-DUMP] ");

			Enumeration names = renderRequest.getParameterNames();
			while (names != null && names.hasMoreElements()) {
				String k = String.valueOf(names.nextElement());
				String[] vs = renderRequest.getParameterValues(k);
				sb.append(k).append("=");

				if (vs == null) {
					sb.append("null");
				} else if (vs.length == 1) {
					sb.append("'").append(vs[0]).append("'");
				} else {
					sb.append("[");
					for (int i = 0; i < vs.length; i++) {
						if (i > 0) sb.append(",");
						sb.append("'").append(vs[i]).append("'");
					}
					sb.append("]");
				}
				sb.append(" ");
			}

			_log.info(sb.toString());
		} catch (Exception e) {
			_log.warn(prefix(rid) + "No pude dumpear parámetros", e);
		}
	}

	private void logSessionSnapshot(HttpSession session, String rid, int maxKeys) {
		try {
			int c = 0;
			StringBuilder sb = new StringBuilder();
			sb.append(prefix(rid)).append("[SESSION] ");

			Enumeration names = session.getAttributeNames();
			while (names != null && names.hasMoreElements() && c < maxKeys) {
				String k = String.valueOf(names.nextElement());
				Object v = session.getAttribute(k);
				sb.append(k).append("=");
				sb.append(v != null ? v.getClass().getSimpleName() : "null");
				sb.append(" ");
				c++;
			}
			_log.info(sb.toString());
		} catch (Exception e) {
			_log.warn(prefix(rid) + "No pude snapshot de sesión", e);
		}
	}
}

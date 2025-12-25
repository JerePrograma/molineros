package ar.com.ospim.liquidaciones.action;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class DebitoTercerizadorasAction extends PortletAction {

    private static final Logger _log = Logger.getLogger(DebitoTercerizadorasAction.class);

    // Session / Request keys (existentes)
    private static final String SESSION_TOTALES_KEY = "BUSQUEDA_DEBITOS_TERCERIZADORAS_TOTALES";
    private static final String REQ_TIPO_MAP_KEY = "DEBITOS_TIPO_MAP";
    private static final String REQ_TIPO_SELECTED_KEY = "DEBITOS_TIPO_SELECTED";
    private static final String REQ_DETALLE_KEY = "DEBITOS_DETALLE_RESULTADOS";
    private static final String REQ_TOTALES_SESSION_KEY = "DEBITOS_TOTALES_SESSION_KEY";

    // NUEVO: cache por sesión
    private static final String SESSION_CACHE_MAP_KEY = "BUSQUEDA_DEBITOS_TERCERIZADORAS_CACHE_MAP";
    private static final String SESSION_ACTIVE_CACHE_KEY = "BUSQUEDA_DEBITOS_TERCERIZADORAS_ACTIVE_KEY";
    private static final int SESSION_CACHE_MAX_ENTRIES = 5;

    // NUEVO: expongo el cacheKey al JSP (para deletes robustos multi-tab)
    private static final String REQ_CACHE_KEY = "DEBITOS_CACHE_KEY";

    // Forward
    private static final String FWD_SEARCH_RESULT = "portlet.liquidaciones.debitos_tercerizadoras_search_result";

    // Debug controls
    private static final String PARAM_DBG = "dbg";     // ?dbg=true
    private static final String PARAM_DUMP = "dump";   // ?dump=true
    private static final int DETAIL_PREVIEW_MAX = 10;  // log first N items only

    // CMDs nuevos
    private static final String CMD_APPEND_SEARCH = "appendSearch";   // render: llena staging
    private static final String CMD_ANEXAR_DETALLE = "anexarDetalle"; // action: mueve staging -> main
    private static final String CMD_GUARDAR_BORRADOR = "guardarBorrador";

    // Param para identificar el "work entry" (lista principal)
    private static final String PARAM_WORK_CACHE_KEY = "workCacheKey";
    // Flag: append render reusa staging actual (sin reseed desde source)
    private static final String PARAM_APPEND_REUSE = "reuseStaging";

    // Flag en request para que el JSP hijo sepa si está en modo APPEND (staging)
    private static final String REQ_APPEND_MODE = "DEBITOS_APPEND_MODE";

    // Request keys para el staging (nuevo JSP hijo)
    private static final String REQ_ANEXAR_DETALLE_KEY = "DEBITOS_DETALLE_ANEXAR_RESULTADOS";
    private static final String REQ_ANEXAR_SOURCE_KEY  = "DEBITOS_ANEXAR_SOURCE_KEY";
    private static final String REQ_WORK_CACHE_KEY     = "DEBITOS_WORK_CACHE_KEY";

    // Opcional: forward nuevo (si todavía no lo tenés en struts-config, el render hace fallback al actual)
    private static final String FWD_APPEND_RESULT = "portlet.liquidaciones.debitos_tercerizadoras_append_result";

    // =========================
    // Cache Entry (sesión)
    // =========================
    private static class CacheEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        String cacheKey;

        int anio;
        int mes0Based;
        String tercerizadoras;

        Date fechaDesde;
        Date fechaHasta;
        Date fechaEjecucion;

        boolean existePersistido;

        List totales;
        List detalleLI;
        List detalleHO;
        List detalleRE;
        List detallePR;

        // ===== STAGING (para "agregar" / "anexar") =====
        List anexarLI;
        List anexarHO;
        List anexarRE;
        List anexarPR;

        // metadata opcional (útil para debug)
        String anexarSourceKey;
        int anexarAnio;
        int anexarMes0Based;
        String anexarTercerizadoras;

        long createdAtMs;
        long lastAccessMs;

        List safeList(List l) {
            return (l != null) ? l : new ArrayList();
        }
    }

    private static class LruMap extends LinkedHashMap implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int max;

        LruMap(int max) {
            super(16, 0.75f, true);
            this.max = max;
        }

        protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
            return size() > max;
        }
    }

    @Override
    public void processAction(
            ActionMapping mapping, ActionForm form,
            PortletConfig portletConfig, ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        final String rid = newRid();
        final long t0 = System.currentTimeMillis();

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);

        if ("deleteDetalle".equals(cmd)) {
            doDeleteDetalle(actionRequest, actionResponse, rid);

        } else if (CMD_ANEXAR_DETALLE.equals(cmd)) {
            doAnexarDetalle(actionRequest, actionResponse, rid);

        } else if (CMD_GUARDAR_BORRADOR.equals(cmd)) {
            doGuardarBorrador(actionRequest, actionResponse, rid);
            
        } else {
            // No-op: búsquedas corren por render vía AJAX (cmd=search / cmd=appendSearch)
        }

        if (_log.isInfoEnabled()) {
            _log.info(prefix(rid) + "[ACTION-END] cmd=" + cmd + " elapsedMs=" + (System.currentTimeMillis() - t0));
        }
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
        } catch (Exception e) {
            _log.debug(prefix(rid) + "PortalUtil.getUser error: " + e.getMessage(), e);
        }

        final String ns = getNs(renderRequest);

        String cmd = pS(renderRequest, ns, Constants.CMD, null);

        Map tiposMap = parseTiposConfig(getConfigDebitosTercerizadorasTipoRaw());
        renderRequest.setAttribute(REQ_TIPO_MAP_KEY, tiposMap);

        String tipoSel = pS(renderRequest, ns, "tipo_proceso", null);
        if (StringUtils.checkEmpty(tipoSel)) tipoSel = pS(renderRequest, ns, "tipoProceso", null);
        if (StringUtils.checkEmpty(tipoSel)) tipoSel = pS(renderRequest, ns, "tipoDebito", "LI");
        tipoSel = (tipoSel != null) ? tipoSel.trim().toUpperCase() : "LI";

        if (!tiposMap.containsKey(tipoSel)) {
            _log.warn(prefix(rid) + "tipoSel inválido='" + tipoSel + "'. Se fuerza a LI.");
            tipoSel = "LI";
        }

        renderRequest.setAttribute(REQ_TIPO_SELECTED_KEY, tipoSel);
        renderRequest.setAttribute(REQ_TOTALES_SESSION_KEY, SESSION_TOTALES_KEY);

        _log.info(prefix(rid) + "[RENDER] mapping.path=" + mapping.getPath()
                + " mapping.type=" + mapping.getType()
                + " cmd=" + cmd
                + " user=" + (usuario != null ? usuario.getScreenName() : "null")
                + " tipoSel=" + tipoSel
                + " fechaDesdeMes=" + safe(pS(renderRequest, ns, "fechaDesdeMes", ""))
                + " fechaDesdeAnio=" + safe(pS(renderRequest, ns, "fechaDesdeAnio", ""))
                + " tercerizadoras=" + safe(pS(renderRequest, ns, "tipo_debitos_tercerizadoras",
                pS(renderRequest, ns, "tipo_debito", ""))));

        if (dbg) {
            logMappingForwards(mapping, rid);
            logSessionSnapshot(session, rid, 50);
            if (dumpAllParams) dumpAllRequestParams(renderRequest, rid);
        }

        if (!StringUtils.checkEmpty(cmd) && Constants.SEARCH.equals(cmd)) {
            doSearch(renderRequest, session, tipoSel, rid, dbg);

        } else if (CMD_APPEND_SEARCH.equals(cmd)) {
            doAppendSearch(renderRequest, session, tipoSel, rid, dbg);

        } else if ("deleteDetalle".equals(cmd)) {
            boolean ok = hydrateFromCache(renderRequest, session, tipoSel, rid, dbg);
            if (!ok) {
                _log.warn(prefix(rid) + "[CACHE-MISS] cmd=deleteDetalle sin cache. Fallback a doSearch (puede pegar DB).");
                doSearch(renderRequest, session, tipoSel, rid, dbg);
            }

        } else {
            hydrateFromCache(renderRequest, session, tipoSel, rid, dbg);
        }

        // Siempre forward al JSP hijo “base”. El modo APPEND se comunica por request attr.
        ActionForward fwd = debugFindForward(mapping, FWD_SEARCH_RESULT, rid, dbg);

        _log.info(prefix(rid) + "[RENDER-END] elapsedMs=" + (System.currentTimeMillis() - t0));
        return fwd;
    }

    // =========================
    // SEARCH (con cache)
    // =========================
    private void doSearch(RenderRequest renderRequest, HttpSession session, String tipoSel, String rid, boolean dbg) {
        final long t0 = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        final String ns = getNs(renderRequest);

        int mes0Based = pI(renderRequest, ns, "fechaDesdeMes", -1);
        int anio = pI(renderRequest, ns, "fechaDesdeAnio", -1);

        String tercerizadoras = pS(renderRequest, ns, "tipo_debitos_tercerizadoras",
                pS(renderRequest, ns, "tipo_debito", ""));
        tercerizadoras = (tercerizadoras != null) ? tercerizadoras.trim() : "";

        String cacheKey = buildCacheKey(anio, mes0Based, tercerizadoras);

        // 1) CACHE HIT?
        CacheEntry hit = getCache(session, cacheKey);
        if (hit != null) {
            hit.lastAccessMs = System.currentTimeMillis();
            session.setAttribute(SESSION_ACTIVE_CACHE_KEY, cacheKey);

            applyEntryToRequest(hit, renderRequest, session, tipoSel);
            renderRequest.setAttribute(REQ_CACHE_KEY, cacheKey);

            // Asegurar modo normal
            renderRequest.removeAttribute(REQ_APPEND_MODE);

            _log.info(prefix(rid) + "[CACHE-HIT] key=" + cacheKey
                    + " tipoSel=" + tipoSel
                    + " LI=" + size(hit.detalleLI) + " HO=" + size(hit.detalleHO)
                    + " RE=" + size(hit.detalleRE) + " PR=" + size(hit.detallePR)
                    + " elapsedMs=" + (System.currentTimeMillis() - t0));
            return;
        }

        // 2) CACHE MISS -> DB
        if (mes0Based < 0 || anio <= 0) {
            _log.warn(prefix(rid) + "[SEARCH] fechaDesdeMes/fechaDesdeAnio inválidos: mes0Based=" + mes0Based + " anio=" + anio);
        }
        if (tercerizadoras.length() == 0) {
            _log.warn(prefix(rid) + "[SEARCH] tercerizadoras VACÍO. Probable causa de resultados 0 si el service espera ID.");
        }

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
        }

        try {
            Calendar calEjec = DateUtils.getCalendarGMTMenos3();
            calEjec.add(Calendar.MONTH, -1);
            fechaEjecucion = calEjec.getTime();
        } catch (Exception e) {
            _log.error(prefix(rid) + "[SEARCH] Error calculando fechaEjecucion", e);
        }

        _log.info(prefix(rid) + "[SEARCH-DB] key=" + cacheKey
                + " tipoSel=" + tipoSel
                + " tercerizadoras=" + safe(tercerizadoras)
                + " mes0Based=" + mes0Based
                + " anio=" + anio
                + " fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                + " fechaEjecucion=" + fmt(sdf, fechaEjecucion) + " (" + ms(fechaEjecucion) + ")");

        // ======= TOTALES =======
        List totales = new ArrayList();
        DebitosaTotal debitosaTotal = null;

        try {
            if (fechaHasta != null) {
                debitosaTotal = BusquedaDebitosTercerizadorasServiceUtil.getBuscarTotalesDebitos(fechaHasta, tercerizadoras);
                if (debitosaTotal != null) totales.add(debitosaTotal);
            } else {
                _log.warn(prefix(rid) + "[TOTALES] fechaHasta=null, no se consulta totales.");
            }
        } catch (Exception e) {
            _log.error(prefix(rid) + "[TOTALES] Error consultando totales", e);
        }

        session.setAttribute(SESSION_TOTALES_KEY, totales);

        boolean existePersistido = (debitosaTotal != null && safeBool(debitosaTotal));

        // ======= DETALLES =======
        List detalleLI = new ArrayList();
        List detalleHO = new ArrayList();
        List detalleRE = new ArrayList();
        List detallePR = new ArrayList();

        if (fechaHasta == null) {
            _log.warn(prefix(rid) + "[DETAIL] fechaHasta=null, no se consulta detalle.");
        } else {
            try {
                detalleLI = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaLiquidacionesPendientes(
                        fechaEjecucion, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[DETAIL-LI] Error", e);
            }

            try {
                detalleHO = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_HOSPITALES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaHospitales(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[DETAIL-HO] Error", e);
            }

            try {
                detalleRE = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_REINTEGROS, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosReintegros(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[DETAIL-RE] Error", e);
            }

            try {
                detallePR = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_PRESTADORES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosPrestadores(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[DETAIL-PR] Error", e);
            }
        }

        CacheEntry entry = new CacheEntry();
        entry.cacheKey = cacheKey;
        entry.anio = anio;
        entry.mes0Based = mes0Based;
        entry.tercerizadoras = tercerizadoras;
        entry.fechaDesde = fechaDesde;
        entry.fechaHasta = fechaHasta;
        entry.fechaEjecucion = fechaEjecucion;
        entry.existePersistido = existePersistido;
        entry.totales = totales;
        entry.detalleLI = (detalleLI != null) ? detalleLI : new ArrayList();
        entry.detalleHO = (detalleHO != null) ? detalleHO : new ArrayList();
        entry.detalleRE = (detalleRE != null) ? detalleRE : new ArrayList();
        entry.detallePR = (detallePR != null) ? detallePR : new ArrayList();
        entry.createdAtMs = System.currentTimeMillis();
        entry.lastAccessMs = entry.createdAtMs;

        putCache(session, cacheKey, entry);
        session.setAttribute(SESSION_ACTIVE_CACHE_KEY, cacheKey);

        applyEntryToRequest(entry, renderRequest, session, tipoSel);
        renderRequest.setAttribute(REQ_CACHE_KEY, cacheKey);

        // Asegurar modo normal
        renderRequest.removeAttribute(REQ_APPEND_MODE);

        _log.info(prefix(rid) + "[SEARCH-DB-END] key=" + cacheKey
                + " existePersistido=" + existePersistido
                + " LI=" + size(detalleLI) + " HO=" + size(detalleHO)
                + " RE=" + size(detalleRE) + " PR=" + size(detallePR)
                + " elapsedMs=" + (System.currentTimeMillis() - t0));

        if (dbg) {
            List sel = pickByTipo(tipoSel, entry.detalleLI, entry.detalleHO, entry.detalleRE, entry.detallePR);
            previewList(sel, rid, DETAIL_PREVIEW_MAX);
        }
    }

    // =========================
    // DELETE (mutar cache) + forzar RENDER result
    // =========================
    private void doDeleteDetalle(ActionRequest actionRequest, ActionResponse actionResponse, String rid) {
        HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(actionRequest);
        HttpSession session = httpReq.getSession();

        String ns = null;
        try {
            String portletId = PortalUtil.getPortletId(actionRequest);
            ns = PortalUtil.getPortletNamespace(portletId);
        } catch (Exception e) {
            // ignore
        }

        String ids = ParamUtil.getString(actionRequest, "ids", "");
        if (Validator.isNull(ids) && ns != null) {
            ids = ParamUtil.getString(actionRequest, ns + "ids", "");
        }
        ids = (ids != null) ? ids.trim() : "";

        String cacheKey = ParamUtil.getString(actionRequest, "cacheKey", "");
        if (Validator.isNull(cacheKey) && ns != null) {
            cacheKey = ParamUtil.getString(actionRequest, ns + "cacheKey", "");
        }
        if (Validator.isNull(cacheKey)) {
            cacheKey = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);
        }

        // Preservar tipo seleccionado (para que el render no “salte” a LI)
        String tipoSel = ParamUtil.getString(actionRequest, "tipo_proceso",
                ParamUtil.getString(actionRequest, "tipoDebito", null));
        if (Validator.isNull(tipoSel) && ns != null) {
            tipoSel = ParamUtil.getString(actionRequest, ns + "tipo_proceso",
                    ParamUtil.getString(actionRequest, ns + "tipoDebito", null));
        }
        tipoSel = (tipoSel != null) ? tipoSel.trim().toUpperCase() : null;

        // --- Validaciones mínimas ---
        if (Validator.isNull(cacheKey)) {
            _log.warn(prefix(rid) + "[DELETE] cacheKey=null. No se puede aplicar delete a memoria.");
            // Aun así: forzamos render a search_result para no dejar al cliente colgado.
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            return;
        }

        CacheEntry entry = getCache(session, cacheKey);
        if (entry == null) {
            _log.warn(prefix(rid) + "[DELETE] cache MISS para key=" + cacheKey + ". No se pudo borrar en memoria.");
            // Forzamos render con cacheKey (así, si el render tiene params de mes/año/tercerizadoras, podría reconstruir)
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", cacheKey);
            if (Validator.isNotNull(tipoSel)) actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        if (Validator.isNull(ids)) {
            _log.warn(prefix(rid) + "[DELETE] ids vacío. Nada para borrar. key=" + cacheKey);
            // Igual forzamos render para que rehidrate y muestre lo que haya
            session.setAttribute(SESSION_ACTIVE_CACHE_KEY, cacheKey);
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", cacheKey);
            if (Validator.isNotNull(tipoSel)) actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        // ids formato: "LI:3;LI:5;HO:1;" etc
        String[] parts = ids.split(";");
        Map toDeleteByTipo = new LinkedHashMap(); // tipo -> List<Integer>

        for (int i = 0; i < parts.length; i++) {
            String p = (parts[i] != null) ? parts[i].trim() : "";
            if (p.length() == 0) continue;

            int idx = p.indexOf(':');
            if (idx <= 0) continue;

            String tipo = p.substring(0, idx).trim().toUpperCase();
            String sIndex = p.substring(idx + 1).trim();

            try {
                Integer n = Integer.valueOf(Integer.parseInt(sIndex));
                List list = (List) toDeleteByTipo.get(tipo);
                if (list == null) {
                    list = new ArrayList();
                    toDeleteByTipo.put(tipo, list);
                }
                list.add(n);
            } catch (Exception e) {
                _log.warn(prefix(rid) + "[DELETE] No pude parsear index en '" + p + "'");
            }
        }

        int removed = 0;

        removed += removeFrom(entry.detalleLI, (List) toDeleteByTipo.get("LI"));
        removed += removeFrom(entry.detalleHO, (List) toDeleteByTipo.get("HO"));
        removed += removeFrom(entry.detalleRE, (List) toDeleteByTipo.get("RE"));
        removed += removeFrom(entry.detallePR, (List) toDeleteByTipo.get("PR"));

        entry.lastAccessMs = System.currentTimeMillis();

        // Aseguramos consistencia de “active cache”
        session.setAttribute(SESSION_ACTIVE_CACHE_KEY, cacheKey);

        _log.info(prefix(rid) + "[DELETE] key=" + cacheKey
                + " removed=" + removed
                + " sizesAfter LI=" + size(entry.detalleLI)
                + " HO=" + size(entry.detalleHO)
                + " RE=" + size(entry.detalleRE)
                + " PR=" + size(entry.detallePR));

        // ---- CLAVE: forzar el próximo render a rehidratar desde cache y forwardear al JSP de resultado ----
        actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
        actionResponse.setRenderParameter("cacheKey", cacheKey);
        if (Validator.isNotNull(tipoSel)) {
            actionResponse.setRenderParameter("tipo_proceso", tipoSel);
        }
    }

    private void doAppendSearch(RenderRequest renderRequest, HttpSession session, String tipoSel, String rid, boolean dbg) {
        final long t0 = System.currentTimeMillis();

        final String ns = getNs(renderRequest);

        // 1) workKey (lista principal)
        String workKey = pS(renderRequest, ns, PARAM_WORK_CACHE_KEY, "");
        if (Validator.isNull(workKey)) workKey = pS(renderRequest, ns, "cacheKey", "");
        if (Validator.isNull(workKey)) workKey = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);

        boolean reuseStaging = pB(renderRequest, ns, PARAM_APPEND_REUSE, false);

        CacheEntry work = null;
        if (Validator.isNotNull(workKey)) work = getCache(session, workKey);

        // fallback a ACTIVE si workKey vino vacío
        if (work == null) {
            String active = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);
            if (Validator.isNotNull(active)) {
                workKey = active;
                work = getCache(session, workKey);
            }
        }

        // === Reuse: NO reseed, solo mostrar staging actual ===
        if (reuseStaging) {
            if (work == null) {
                _log.warn(prefix(rid) + "[APPEND-REUSE] No hay workEntry. workKey=" + workKey);
                return;
            }

            work.lastAccessMs = System.currentTimeMillis();
            session.setAttribute(SESSION_ACTIVE_CACHE_KEY, workKey);

            renderRequest.setAttribute(REQ_APPEND_MODE, Boolean.TRUE);
            renderRequest.setAttribute(REQ_CACHE_KEY, workKey);
            renderRequest.setAttribute(REQ_WORK_CACHE_KEY, workKey);
            renderRequest.setAttribute(REQ_ANEXAR_SOURCE_KEY, work.anexarSourceKey);

            applyEntryToRequest(work, renderRequest, session, tipoSel);
            applyAppendEntryToRequest(work, renderRequest, tipoSel);

            _log.info(prefix(rid) + "[APPEND-REUSE] workKey=" + workKey
                    + " sourceKey=" + safe(work.anexarSourceKey)
                    + " stagingSizes LI=" + size(work.anexarLI)
                    + " HO=" + size(work.anexarHO)
                    + " RE=" + size(work.anexarRE)
                    + " PR=" + size(work.anexarPR)
                    + " elapsedMs=" + (System.currentTimeMillis() - t0));
            return;
        }

        // 2) source params (período a “agregar”)
        int mes0Based = pI(renderRequest, ns, "fechaDesdeMes", -1);
        int anio = pI(renderRequest, ns, "fechaDesdeAnio", -1);

        String tercerizadoras = pS(renderRequest, ns, "tipo_debitos_tercerizadoras",
                pS(renderRequest, ns, "tipo_debito", ""));
        tercerizadoras = (tercerizadoras != null) ? tercerizadoras.trim() : "";

        String sourceKey = buildCacheKey(anio, mes0Based, tercerizadoras);

        // 3) si no existe work, crealo como el source (degradación segura)
        if (work == null && Validator.isNotNull(sourceKey)) {
            CacheEntry created = queryDbBuildEntry(anio, mes0Based, tercerizadoras, sourceKey, tipoSel, rid, "WORK-CREATE");
            if (created != null) {
                workKey = sourceKey;
                putCache(session, workKey, created);
                work = created;
                session.setAttribute(SESSION_ACTIVE_CACHE_KEY, workKey);
            }
        }

        if (work == null) {
            _log.warn(prefix(rid) + "[APPEND] No hay workEntry. workKey=" + workKey + " sourceKey=" + sourceKey);
            return;
        }

        // 4) traer source (preferir cache)
        CacheEntry src = null;
        if (Validator.isNotNull(sourceKey)) src = getCache(session, sourceKey);

        if (src == null) {
            if (Validator.isNull(sourceKey)) {
                _log.warn(prefix(rid) + "[APPEND] sourceKey=null (anio/mes/terc inválidos). No puedo armar staging.");
                work.anexarLI = new ArrayList();
                work.anexarHO = new ArrayList();
                work.anexarRE = new ArrayList();
                work.anexarPR = new ArrayList();
            } else {
                src = queryDbBuildEntry(anio, mes0Based, tercerizadoras, sourceKey, tipoSel, rid, "APPEND-DB");
                // Importante: cachear el sourceKey para evitar pegar DB en cada “Agregar”
                if (src != null) putCache(session, sourceKey, src);
            }
        }

        // 5) set staging en work (copias mutables)
        work.anexarLI = (src != null && src.detalleLI != null) ? new ArrayList(src.detalleLI) : new ArrayList();
        work.anexarHO = (src != null && src.detalleHO != null) ? new ArrayList(src.detalleHO) : new ArrayList();
        work.anexarRE = (src != null && src.detalleRE != null) ? new ArrayList(src.detalleRE) : new ArrayList();
        work.anexarPR = (src != null && src.detallePR != null) ? new ArrayList(src.detallePR) : new ArrayList();

        work.anexarSourceKey = sourceKey;
        work.anexarAnio = anio;
        work.anexarMes0Based = mes0Based;
        work.anexarTercerizadoras = tercerizadoras;

        work.lastAccessMs = System.currentTimeMillis();
        session.setAttribute(SESSION_ACTIVE_CACHE_KEY, workKey);

        // 6) exponer para JSP
        renderRequest.setAttribute(REQ_APPEND_MODE, Boolean.TRUE);
        renderRequest.setAttribute(REQ_CACHE_KEY, workKey);
        renderRequest.setAttribute(REQ_WORK_CACHE_KEY, workKey);
        renderRequest.setAttribute(REQ_ANEXAR_SOURCE_KEY, sourceKey);

        applyEntryToRequest(work, renderRequest, session, tipoSel);
        applyAppendEntryToRequest(work, renderRequest, tipoSel);

        _log.info(prefix(rid) + "[APPEND] workKey=" + workKey
                + " sourceKey=" + sourceKey
                + " stagingSizes LI=" + size(work.anexarLI)
                + " HO=" + size(work.anexarHO)
                + " RE=" + size(work.anexarRE)
                + " PR=" + size(work.anexarPR)
                + " elapsedMs=" + (System.currentTimeMillis() - t0));
    }

    private void doAnexarDetalle(ActionRequest actionRequest, ActionResponse actionResponse, String rid) {
        HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(actionRequest);
        HttpSession session = httpReq.getSession();

        String ns = null;
        try {
            String portletId = PortalUtil.getPortletId(actionRequest);
            ns = PortalUtil.getPortletNamespace(portletId);
        } catch (Exception e) {
            // ignore
        }

        // ids (índices del STAGING)
        String ids = ParamUtil.getString(actionRequest, "ids", "");
        if (Validator.isNull(ids) && ns != null) {
            ids = ParamUtil.getString(actionRequest, ns + "ids", "");
        }
        ids = (ids != null) ? ids.trim() : "";

        // workKey (entrada principal)
        String workKey = ParamUtil.getString(actionRequest, PARAM_WORK_CACHE_KEY, "");
        if (Validator.isNull(workKey) && ns != null) {
            workKey = ParamUtil.getString(actionRequest, ns + PARAM_WORK_CACHE_KEY, "");
        }
        if (Validator.isNull(workKey)) {
            workKey = ParamUtil.getString(actionRequest, "cacheKey", "");
        }
        if (Validator.isNull(workKey) && ns != null) {
            workKey = ParamUtil.getString(actionRequest, ns + "cacheKey", "");
        }
        if (Validator.isNull(workKey)) {
            workKey = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);
        }

        // preservar tipo
        String tipoSel = ParamUtil.getString(actionRequest, "tipo_proceso",
                ParamUtil.getString(actionRequest, "tipoProceso",
                        ParamUtil.getString(actionRequest, "tipoDebito", null)));
        if (Validator.isNull(tipoSel) && ns != null) {
            tipoSel = ParamUtil.getString(actionRequest, ns + "tipo_proceso",
                    ParamUtil.getString(actionRequest, ns + "tipoProceso",
                            ParamUtil.getString(actionRequest, ns + "tipoDebito", null)));
        }
        tipoSel = (tipoSel != null) ? tipoSel.trim().toUpperCase() : null;

        if (Validator.isNull(workKey)) {
            _log.warn(prefix(rid) + "[ANEXAR] workKey=null. No puedo mover staging -> main.");
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            return;
        }

        CacheEntry entry = getCache(session, workKey);
        if (entry == null) {
            _log.warn(prefix(rid) + "[ANEXAR] cache MISS workKey=" + workKey);
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", workKey);
            if (Validator.isNotNull(tipoSel)) actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        if (Validator.isNull(ids)) {
            _log.warn(prefix(rid) + "[ANEXAR] ids vacío. Nada para mover. workKey=" + workKey);
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", workKey);
            if (Validator.isNotNull(tipoSel)) actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        // asegurar listas staging no null
        if (entry.anexarLI == null) entry.anexarLI = new ArrayList();
        if (entry.anexarHO == null) entry.anexarHO = new ArrayList();
        if (entry.anexarRE == null) entry.anexarRE = new ArrayList();
        if (entry.anexarPR == null) entry.anexarPR = new ArrayList();

        // parse ids -> tipo -> indices
        String[] parts = ids.split(";");
        Map toMoveByTipo = new LinkedHashMap(); // tipo -> List<Integer>

        for (int i = 0; i < parts.length; i++) {
            String p = (parts[i] != null) ? parts[i].trim() : "";
            if (p.length() == 0) continue;

            int idx = p.indexOf(':');
            if (idx <= 0) continue;

            String tipo = p.substring(0, idx).trim().toUpperCase();
            String sIndex = p.substring(idx + 1).trim();

            try {
                Integer n = Integer.valueOf(Integer.parseInt(sIndex));
                List list = (List) toMoveByTipo.get(tipo);
                if (list == null) {
                    list = new ArrayList();
                    toMoveByTipo.put(tipo, list);
                }
                list.add(n);
            } catch (Exception e) {
                _log.warn(prefix(rid) + "[ANEXAR] No pude parsear index en '" + p + "'");
            }
        }

        int moved = 0;
        moved += moveFromTo(entry.anexarLI, entry.detalleLI, (List) toMoveByTipo.get("LI"));
        moved += moveFromTo(entry.anexarHO, entry.detalleHO, (List) toMoveByTipo.get("HO"));
        moved += moveFromTo(entry.anexarRE, entry.detalleRE, (List) toMoveByTipo.get("RE"));
        moved += moveFromTo(entry.anexarPR, entry.detallePR, (List) toMoveByTipo.get("PR"));

        entry.lastAccessMs = System.currentTimeMillis();
        session.setAttribute(SESSION_ACTIVE_CACHE_KEY, workKey);

        _log.info(prefix(rid) + "[ANEXAR] workKey=" + workKey
                + " moved=" + moved
                + " mainSizesAfter LI=" + size(entry.detalleLI)
                + " HO=" + size(entry.detalleHO)
                + " RE=" + size(entry.detalleRE)
                + " PR=" + size(entry.detallePR)
                + " stagingSizesAfter LI=" + size(entry.anexarLI)
                + " HO=" + size(entry.anexarHO)
                + " RE=" + size(entry.anexarRE)
                + " PR=" + size(entry.anexarPR));

        // forzar render principal actualizado (el staging lo refrescarás por AJAX después)
        actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
        actionResponse.setRenderParameter("cacheKey", workKey);
        if (Validator.isNotNull(tipoSel)) actionResponse.setRenderParameter("tipo_proceso", tipoSel);
    }

    private void doGuardarBorrador(ActionRequest actionRequest, ActionResponse actionResponse, String rid) {
        HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(actionRequest);
        HttpSession session = httpReq.getSession();

        String ns = null;
        try {
            String portletId = PortalUtil.getPortletId(actionRequest);
            ns = PortalUtil.getPortletNamespace(portletId);
        } catch (Exception ignore) {}

        // workKey/cacheKey
        String workKey = ParamUtil.getString(actionRequest, PARAM_WORK_CACHE_KEY, "");
        if (Validator.isNull(workKey) && ns != null) workKey = ParamUtil.getString(actionRequest, ns + PARAM_WORK_CACHE_KEY, "");
        if (Validator.isNull(workKey)) workKey = ParamUtil.getString(actionRequest, "cacheKey", "");
        if (Validator.isNull(workKey) && ns != null) workKey = ParamUtil.getString(actionRequest, ns + "cacheKey", "");
        if (Validator.isNull(workKey)) workKey = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);

        // tipoSel (para volver al mismo tab)
        String tipoSel = ParamUtil.getString(actionRequest, "tipo_proceso",
                ParamUtil.getString(actionRequest, "tipoProceso",
                        ParamUtil.getString(actionRequest, "tipoDebito", null)));
        if (Validator.isNull(tipoSel) && ns != null) {
            tipoSel = ParamUtil.getString(actionRequest, ns + "tipo_proceso",
                    ParamUtil.getString(actionRequest, ns + "tipoProceso",
                            ParamUtil.getString(actionRequest, ns + "tipoDebito", null)));
        }
        tipoSel = (tipoSel != null) ? tipoSel.trim().toUpperCase() : "LI";

        if (Validator.isNull(workKey)) {
            _log.warn(prefix(rid) + "[GUARDAR-BORRADOR] workKey=null. No puedo guardar.");
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            return;
        }

        CacheEntry entry = getCache(session, workKey);
        if (entry == null) {
            _log.warn(prefix(rid) + "[GUARDAR-BORRADOR] cache MISS workKey=" + workKey);
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", workKey);
            actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        // === CLAVE: usar fechaHasta como "periodo" (consistente con doSearch persistido) ===
        Date periodo = entry.fechaHasta;

        // fallback si por algún motivo no está
        if (periodo == null && entry.anio > 0 && entry.mes0Based >= 0) {
            Calendar cal = DateUtils.getCalendarGMTMenos3();
            cal.set(Calendar.YEAR, entry.anio);
            cal.set(Calendar.MONTH, entry.mes0Based);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date desde = cal.getTime();
            periodo = DateUtils.getLastDateOfMonth(desde, false); // fin de mes
        }

        if (periodo == null) {
            _log.warn(prefix(rid) + "[GUARDAR-BORRADOR] periodo=null. No guardo para evitar basura.");
            actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
            actionResponse.setRenderParameter("cacheKey", workKey);
            actionResponse.setRenderParameter("tipo_proceso", tipoSel);
            return;
        }

        String tercerizadoras = (entry.tercerizadoras != null) ? entry.tercerizadoras.trim() : "";
        if (tercerizadoras.length() == 0) {
            _log.warn(prefix(rid) + "[GUARDAR-BORRADOR] tercerizadoras VACIO en entry. Guardar así suele ser inútil.");
        }

        String screenName = "system";
        try {
            User u = PortalUtil.getUser(actionRequest);
            if (u != null && Validator.isNotNull(u.getScreenName())) screenName = u.getScreenName();
        } catch (Exception e) {
            _log.debug(prefix(rid) + "[GUARDAR-BORRADOR] No pude obtener user", e);
        }

        // Listas (principal en memoria)
        List li = (entry.detalleLI != null) ? entry.detalleLI : new ArrayList();
        List ho = (entry.detalleHO != null) ? entry.detalleHO : new ArrayList();
        List re = (entry.detalleRE != null) ? entry.detalleRE : new ArrayList();
        List pr = (entry.detallePR != null) ? entry.detallePR : new ArrayList();

        // Instrumentación fuerte (si esto da 0, el problema está antes del SQL)
        _log.info(prefix(rid) + "[GUARDAR-BORRADOR] PRE sizes LI=" + li.size() + " HO=" + ho.size()
                + " RE=" + re.size() + " PR=" + pr.size()
                + " workKey=" + workKey
                + " periodo(ms)=" + periodo.getTime()
                + " anio=" + entry.anio + " mes0Based=" + entry.mes0Based
                + " tercerizadoras=" + safe(tercerizadoras)
                + " tipoSel=" + tipoSel);

        if (li.size() > 0) _log.info(prefix(rid) + "[GUARDAR-BORRADOR] LI[0] class=" + li.get(0).getClass().getName());
        if (ho.size() > 0) _log.info(prefix(rid) + "[GUARDAR-BORRADOR] HO[0] class=" + ho.get(0).getClass().getName());
        if (re.size() > 0) _log.info(prefix(rid) + "[GUARDAR-BORRADOR] RE[0] class=" + re.get(0).getClass().getName());
        if (pr.size() > 0) _log.info(prefix(rid) + "[GUARDAR-BORRADOR] PR[0] class=" + pr.get(0).getClass().getName());

        int deleted = 0;

        int insertedRowsSum = 0;   // suma de returns
        int insertedAttempts = 0;  // cuántas veces intenté insertar (independiente del return)

        int insLI = 0, insHO = 0, insRE = 0, insPR = 0;

        try {
            // 1) limpiar período (idempotencia)
            deleted = BusquedaDebitosTercerizadorasServiceUtil.borrarBorradorDebitosPorPeriodo(periodo);

            // 2) insertar snapshot (si tus grabar* devuelven 0 aunque inserten, igual ves insertedAttempts>0)
            for (Object o : li) {
                if (o instanceof ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes) {
                    insertedAttempts++;
                    int r = BusquedaDebitosTercerizadorasServiceUtil.grabarBorradorLiquidacionesPendientesDebitos(
                            (ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes) o,
                            screenName,
                            periodo
                    );
                    insertedRowsSum += r;
                    insLI += (r > 0 ? r : 1); // “optimista”: si r=0 pero no hubo excepción, cuento 1 para diagnóstico
                }
            }

            for (Object o : ho) {
                if (o instanceof ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales) {
                    insertedAttempts++;
                    int r = BusquedaDebitosTercerizadorasServiceUtil.grabarBorradorHospitalesDebitos(
                            (ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales) o,
                            screenName,
                            periodo
                    );
                    insertedRowsSum += r;
                    insHO += (r > 0 ? r : 1);
                }
            }

            for (Object o : re) {
                if (o instanceof ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros) {
                    insertedAttempts++;
                    int r = BusquedaDebitosTercerizadorasServiceUtil.grabarBorradorReintegrosDebitos(
                            (ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros) o,
                            screenName,
                            periodo
                    );
                    insertedRowsSum += r;
                    insRE += (r > 0 ? r : 1);
                }
            }

            for (Object o : pr) {
                if (o instanceof ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores) {
                    insertedAttempts++;
                    int r = BusquedaDebitosTercerizadorasServiceUtil.grabarBorradorPrestadoresDebitos(
                            (ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores) o,
                            screenName,
                            periodo
                    );
                    insertedRowsSum += r;
                    insPR += (r > 0 ? r : 1);
                }
            }

            _log.info(prefix(rid) + "[GUARDAR-BORRADOR] POST workKey=" + workKey
                    + " periodo(ms)=" + periodo.getTime()
                    + " deleted=" + deleted
                    + " insertedRowsSum=" + insertedRowsSum
                    + " insertedAttempts=" + insertedAttempts
                    + " diagCounts LI=" + insLI + " HO=" + insHO + " RE=" + insRE + " PR=" + insPR);

        } catch (Exception e) {
            _log.error(prefix(rid) + "[GUARDAR-BORRADOR] Error guardando borrador", e);
        }

        // Volver a render principal rehidratando desde cache (y preservando params clave)
        actionResponse.setRenderParameter(Constants.CMD, "deleteDetalle");
        actionResponse.setRenderParameter("cacheKey", workKey);
        actionResponse.setRenderParameter("tipo_proceso", tipoSel);

        // Estos ayudan a que no te aparezca otro render raro con 2025-12|0 por defaults
        if (entry.mes0Based >= 0) actionResponse.setRenderParameter("fechaDesdeMes", String.valueOf(entry.mes0Based));
        if (entry.anio > 0) actionResponse.setRenderParameter("fechaDesdeAnio", String.valueOf(entry.anio));
        if (Validator.isNotNull(entry.tercerizadoras)) {
            actionResponse.setRenderParameter("tipo_debitos_tercerizadoras", entry.tercerizadoras);
            actionResponse.setRenderParameter("tipo_debito", entry.tercerizadoras);
        }

        // flag para mensaje UI
        actionResponse.setRenderParameter("borradorGuardado", "1");
    }

    private int removeFrom(List target, List indices) {
        if (target == null || indices == null || indices.isEmpty()) return 0;

        // ordenar DESC para no correr índices al borrar
        Collections.sort(indices, new Comparator() {
            public int compare(Object a, Object b) {
                Integer ia = (Integer) a;
                Integer ib = (Integer) b;
                return ib.intValue() - ia.intValue();
            }
        });

        int removed = 0;
        for (int i = 0; i < indices.size(); i++) {
            Integer idx = (Integer) indices.get(i);
            int n = idx.intValue();
            if (n >= 0 && n < target.size()) {
                target.remove(n);
                removed++;
            }
        }
        return removed;
    }

    // =========================
    // Hydrate render desde cache
    // =========================
    private boolean hydrateFromCache(RenderRequest renderRequest, HttpSession session, String tipoSel, String rid, boolean dbg) {
        final String ns = getNs(renderRequest);

        int mes0Based = pI(renderRequest, ns, "fechaDesdeMes", -1);
        int anio = pI(renderRequest, ns, "fechaDesdeAnio", -1);

        String tercerizadoras = pS(renderRequest, ns, "tipo_debitos_tercerizadoras",
                pS(renderRequest, ns, "tipo_debito", ""));
        tercerizadoras = (tercerizadoras != null) ? tercerizadoras.trim() : "";

        String cacheKey = pS(renderRequest, ns, "cacheKey", "");
        if (Validator.isNull(cacheKey)) {
            cacheKey = buildCacheKey(anio, mes0Based, tercerizadoras);
        }
        if (Validator.isNull(cacheKey)) {
            cacheKey = (String) session.getAttribute(SESSION_ACTIVE_CACHE_KEY);
        }

        if (Validator.isNull(cacheKey)) return false;

        CacheEntry entry = getCache(session, cacheKey);
        if (entry == null) return false;

        entry.lastAccessMs = System.currentTimeMillis();
        session.setAttribute(SESSION_ACTIVE_CACHE_KEY, cacheKey);

        applyEntryToRequest(entry, renderRequest, session, tipoSel);
        renderRequest.setAttribute(REQ_CACHE_KEY, cacheKey);

        // Modo normal por defecto (append lo setea doAppendSearch)
        renderRequest.removeAttribute(REQ_APPEND_MODE);

        if (dbg) {
            _log.info(prefix(rid) + "[CACHE-HYDRATE] key=" + cacheKey + " tipoSel=" + tipoSel);
        }
        return true;
    }

    private void applyEntryToRequest(CacheEntry entry, RenderRequest renderRequest, HttpSession session, String tipoSel) {
        // Totales: compatibilidad con tu JSP viejo
        session.setAttribute(SESSION_TOTALES_KEY, entry.safeList(entry.totales));

        // Todas las listas
        renderRequest.setAttribute("DEBITOS_DETALLE_LI", entry.safeList(entry.detalleLI));
        renderRequest.setAttribute("DEBITOS_DETALLE_HO", entry.safeList(entry.detalleHO));
        renderRequest.setAttribute("DEBITOS_DETALLE_RE", entry.safeList(entry.detalleRE));
        renderRequest.setAttribute("DEBITOS_DETALLE_PR", entry.safeList(entry.detallePR));

        // La seleccionada
        List sel = pickByTipo(tipoSel, entry.detalleLI, entry.detalleHO, entry.detalleRE, entry.detallePR);
        renderRequest.setAttribute(REQ_DETALLE_KEY, (sel != null) ? sel : new ArrayList());
    }

    private List pickByTipo(String tipoSel, List li, List ho, List re, List pr) {
        if ("HO".equalsIgnoreCase(tipoSel)) return ho;
        if ("RE".equalsIgnoreCase(tipoSel)) return re;
        if ("PR".equalsIgnoreCase(tipoSel)) return pr;
        return li; // default LI
    }

    private String buildCacheKey(int anio, int mes0Based, String tercerizadoras) {
        if (anio <= 0 || mes0Based < 0) return null;
        String t = (tercerizadoras != null) ? tercerizadoras.trim().toUpperCase() : "";
        return anio + "-" + pad2(mes0Based + 1) + "|" + t;
    }

    private String pad2(int n) {
        return (n < 10) ? ("0" + n) : String.valueOf(n);
    }

    private CacheEntry getCache(HttpSession session, String key) {
        if (Validator.isNull(key)) return null;
        Map m = getCacheMap(session);
        return (CacheEntry) m.get(key);
    }

    private void putCache(HttpSession session, String key, CacheEntry entry) {
        if (Validator.isNull(key) || entry == null) return;
        Map m = getCacheMap(session);
        m.put(key, entry);
        session.setAttribute(SESSION_CACHE_MAP_KEY, m);
    }

    private Map getCacheMap(HttpSession session) {
        Object obj = session.getAttribute(SESSION_CACHE_MAP_KEY);
        if (obj instanceof Map) return (Map) obj;

        Map m = new LruMap(SESSION_CACHE_MAX_ENTRIES);
        session.setAttribute(SESSION_CACHE_MAP_KEY, m);
        return m;
    }

    private int size(List l) {
        return (l != null) ? l.size() : -1;
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

    private int moveFromTo(List from, List to, List indices) {
        if (from == null || to == null || indices == null || indices.isEmpty()) return 0;

        // ordenar DESC para remover sin corrimiento
        Collections.sort(indices, new Comparator() {
            public int compare(Object a, Object b) {
                Integer ia = (Integer) a;
                Integer ib = (Integer) b;
                return ib.intValue() - ia.intValue();
            }
        });

        List movedItemsInOrder = new ArrayList();
        int moved = 0;

        for (int i = 0; i < indices.size(); i++) {
            Integer idx = (Integer) indices.get(i);
            int n = idx.intValue();
            if (n >= 0 && n < from.size()) {
                Object item = from.remove(n);
                // insert al inicio para restaurar orden ascendente original
                movedItemsInOrder.add(0, item);
                moved++;
            }
        }

        if (!movedItemsInOrder.isEmpty()) {
            to.addAll(movedItemsInOrder);
        }
        return moved;
    }

    private void applyAppendEntryToRequest(CacheEntry entry, RenderRequest renderRequest, String tipoSel) {

        List li = (entry != null && entry.anexarLI != null) ? entry.anexarLI : new ArrayList();
        List ho = (entry != null && entry.anexarHO != null) ? entry.anexarHO : new ArrayList();
        List re = (entry != null && entry.anexarRE != null) ? entry.anexarRE : new ArrayList();
        List pr = (entry != null && entry.anexarPR != null) ? entry.anexarPR : new ArrayList();

        renderRequest.setAttribute("DEBITOS_ANEXAR_LI", li);
        renderRequest.setAttribute("DEBITOS_ANEXAR_HO", ho);
        renderRequest.setAttribute("DEBITOS_ANEXAR_RE", re);
        renderRequest.setAttribute("DEBITOS_ANEXAR_PR", pr);

        List sel = pickByTipo(tipoSel, li, ho, re, pr);
        renderRequest.setAttribute(REQ_ANEXAR_DETALLE_KEY, (sel != null) ? sel : new ArrayList());
    }

    private CacheEntry queryDbBuildEntry(int anio, int mes0Based, String tercerizadoras,
                                         String cacheKey, String tipoSel, String rid, String tag) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
            _log.error(prefix(rid) + "[" + tag + "] Error calculando fechaDesde/fechaHasta", e);
        }

        try {
            Calendar calEjec = DateUtils.getCalendarGMTMenos3();
            calEjec.add(Calendar.MONTH, -1);
            fechaEjecucion = calEjec.getTime();
        } catch (Exception e) {
            _log.error(prefix(rid) + "[" + tag + "] Error calculando fechaEjecucion", e);
        }

        _log.info(prefix(rid) + "[" + tag + "] key=" + cacheKey
                + " tipoSel=" + tipoSel
                + " tercerizadoras=" + safe(tercerizadoras)
                + " mes0Based=" + mes0Based
                + " anio=" + anio
                + " fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                + " fechaEjecucion=" + fmt(sdf, fechaEjecucion) + " (" + ms(fechaEjecucion) + ")");

        List totales = new ArrayList();
        DebitosaTotal debitosaTotal = null;

        try {
            if (fechaHasta != null) {
                debitosaTotal = BusquedaDebitosTercerizadorasServiceUtil.getBuscarTotalesDebitos(fechaHasta, tercerizadoras);
                if (debitosaTotal != null) totales.add(debitosaTotal);
            }
        } catch (Exception e) {
            _log.error(prefix(rid) + "[" + tag + "][TOTALES] Error consultando totales", e);
        }

        boolean existePersistido = (debitosaTotal != null && safeBool(debitosaTotal));

        List detalleLI = new ArrayList();
        List detalleHO = new ArrayList();
        List detalleRE = new ArrayList();
        List detallePR = new ArrayList();

        if (fechaHasta != null) {
            try {
                detalleLI = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaLiquidacionesPendientes(
                        fechaEjecucion, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[" + tag + "][DETAIL-LI] Error", e);
            }

            try {
                detalleHO = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_HOSPITALES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaHospitales(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[" + tag + "][DETAIL-HO] Error", e);
            }

            try {
                detalleRE = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_REINTEGROS, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosReintegros(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[" + tag + "][DETAIL-RE] Error", e);
            }

            try {
                detallePR = existePersistido
                        ? (List) BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosaGrabados(
                        WebKeysLiquidaciones.DEBITOS_PRESTADORES, fechaHasta, tercerizadoras)
                        : BusquedaDebitosTercerizadorasServiceUtil.getBusquedaDebitosPrestadores(
                        fechaDesde, fechaHasta, debitosaTotal, tercerizadoras);
            } catch (Exception e) {
                _log.error(prefix(rid) + "[" + tag + "][DETAIL-PR] Error", e);
            }
        } else {
            _log.warn(prefix(rid) + "[" + tag + "] fechaHasta=null, no se consulta detalle.");
        }

        CacheEntry entry = new CacheEntry();
        entry.cacheKey = cacheKey;
        entry.anio = anio;
        entry.mes0Based = mes0Based;
        entry.tercerizadoras = tercerizadoras;
        entry.fechaDesde = fechaDesde;
        entry.fechaHasta = fechaHasta;
        entry.fechaEjecucion = fechaEjecucion;
        entry.existePersistido = existePersistido;
        entry.totales = totales;
        entry.detalleLI = (detalleLI != null) ? detalleLI : new ArrayList();
        entry.detalleHO = (detalleHO != null) ? detalleHO : new ArrayList();
        entry.detalleRE = (detalleRE != null) ? detalleRE : new ArrayList();
        entry.detallePR = (detallePR != null) ? detallePR : new ArrayList();
        entry.createdAtMs = System.currentTimeMillis();
        entry.lastAccessMs = entry.createdAtMs;

        return entry;
    }

    private String getNs(javax.portlet.PortletRequest req) {
        try {
            String portletId = PortalUtil.getPortletId(req);
            return PortalUtil.getPortletNamespace(portletId);
        } catch (Exception e) {
            return null;
        }
    }

    private String pS(javax.portlet.PortletRequest req, String ns, String name, String def) {
        String v = ParamUtil.getString(req, name, null);
        if (Validator.isNull(v) && ns != null) {
            v = ParamUtil.getString(req, ns + name, null);
        }
        return Validator.isNull(v) ? def : v;
    }

    private int pI(javax.portlet.PortletRequest req, String ns, String name, int def) {
        final int SENTINEL = Integer.MIN_VALUE;
        int v = ParamUtil.getInteger(req, name, SENTINEL);
        if (v == SENTINEL && ns != null) {
            v = ParamUtil.getInteger(req, ns + name, SENTINEL);
        }
        return (v == SENTINEL) ? def : v;
    }

    private boolean pB(javax.portlet.PortletRequest req, String ns, String name, boolean def) {
        final String raw = pS(req, ns, name, null);
        if (Validator.isNull(raw)) return def;
        return "true".equalsIgnoreCase(raw) || "1".equals(raw) || "on".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw);
    }

}

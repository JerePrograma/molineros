<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
    Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
    fechaDesde.setTime(new Date());
    Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
    fechaHasta.setTime(new Date());

    String[] tiposStr = TraeListasServiceUtil.getSystemConfig("DEBITOS_TERCERIZADORAS_TIPO").split(";");
    List<ClaseBase> tipos = new ArrayList<ClaseBase>();
    for (int i = 0; i <= tiposStr.length - 1; i++) {
        ClaseBase c = new ClaseBase();
        String codigo = tiposStr[i].split("=")[0];
        String descripcion = tiposStr[i].split("=")[1];
        c.setId(codigo);
        c.setDescripcion(descripcion);
        tipos.add(c);
    }

    // =========================
    // Flags cierre (si el render vino desde un action con render params)
    // NO usa ParamUtil para no depender de imports.
    // =========================
    String _ns = renderResponse.getNamespace();

    String cierrePeriodoOk = request.getParameter("cierrePeriodoOk");
    if (cierrePeriodoOk == null) cierrePeriodoOk = request.getParameter(_ns + "cierrePeriodoOk");
    if (cierrePeriodoOk == null) cierrePeriodoOk = "";

    String cierrePeriodoError = request.getParameter("cierrePeriodoError");
    if (cierrePeriodoError == null) cierrePeriodoError = request.getParameter(_ns + "cierrePeriodoError");
    if (cierrePeriodoError == null) cierrePeriodoError = "";

    // Modo unificado (main)
    String busquedaModeMain = request.getParameter("busquedaMode");
    if (busquedaModeMain == null) busquedaModeMain = request.getParameter(_ns + "busquedaMode");
    if (busquedaModeMain == null || busquedaModeMain.trim().length() == 0) busquedaModeMain = "NEW";
    busquedaModeMain = busquedaModeMain.trim().toUpperCase();
    if (!"NEW".equals(busquedaModeMain)) busquedaModeMain = "NEW";
%>

<portlet:renderURL var="renderDebitosURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
</portlet:renderURL>

<%
    String renderDebitosURLStr = (String) pageContext.getAttribute("renderDebitosURL");
    if (renderDebitosURLStr == null) renderDebitosURLStr = "";
%>

<portlet:actionURL var="actionDebitosURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
</portlet:actionURL>

<portlet:renderURL var="renderArchivosURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
    <portlet:param name="cmd" value="archivosDebitos"/>
</portlet:renderURL>

<%
    String actionDebitosURLStr = (String) pageContext.getAttribute("actionDebitosURL");
    if (actionDebitosURLStr == null) actionDebitosURLStr = "";
%>

<fieldset class="block-labels">
    <legend><liferay-ui:message key="reporte-debito-tercerizadoras"/></legend>

    <!-- Flags (para flujos NO-AJAX / o para debug). No rompe nada si están vacíos -->
    <input type="hidden" id="<portlet:namespace/>busquedaEjecutada" value="0"/>
    <input type="hidden" id="<portlet:namespace/>cierrePeriodoOk" value="<%= cierrePeriodoOk %>"/>
    <input type="hidden" id="<portlet:namespace/>cierrePeriodoError" value="<%= cierrePeriodoError %>"/>

    <!-- busquedaMode del PADRE (el hijo lo lee). Main SIEMPRE NEW -->
    <input type="hidden"
           name="<portlet:namespace/>busquedaMode"
           id="<portlet:namespace/>busquedaMode"
           value="<%= busquedaModeMain %>"/>

    <!-- REFAC: Flag para modo "solo lectura" cuando el período está cerrado -->
    <input type="hidden" id="<portlet:namespace/>periodoCerrado" value="0"/>
    <input type="hidden" id="<portlet:namespace/>periodoCerradoCacheKey" value=""/>

    <!-- Banner simple -->
    <div id="<portlet:namespace/>periodoCerradoBanner"
         style="display:none; margin:8px 0; padding:6px 10px; border:1px solid #999; background:#f4f4f4; font-weight:bold;">
        Período cerrado: mostrando solo el cierre (solo lectura).
    </div>

    <!-- =========================
         BLOQUE 1: BUSCAR (MAIN)
         ========================= -->
    <style>
        .periodo-inline {
            white-space: nowrap;
        }

        .periodo-inline select,
        .periodo-inline span,
        .periodo-inline div,
        .periodo-inline img,
        .periodo-inline a {
            display: inline-block;
            vertical-align: middle;
        }

        .periodo-inline br {
            display: none;
        }
    </style>

    <table class="lfr-table" style="width:100%;">
        <tr>
            <td style="white-space:nowrap;">
                <label><liferay-ui:message key="periodo"/>:</label>
            </td>
            <td style="white-space:nowrap;">
                <div class="periodo-inline">
                    <liferay-ui:input-date
                            dayParam="fechaDesdeDia"
                            dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
                            monthParam="fechaDesdeMes"
                            monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
                            yearParam="fechaDesdeAnio"
                            yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
                            yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
                            yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
                            firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </div>
            </td>

            <td style="white-space:nowrap;">
                <label><liferay-ui:message key="cobertura-expediente-tercerizadora"/>:</label>
            </td>
            <td style="white-space:nowrap;">
                <select name="<portlet:namespace/>tipo_debitos_tercerizadoras"
                        id="<portlet:namespace/>tipo_debitos_tercerizadoras">
                    <option value="0">Seleccione</option>
                    <option value="OMI">OMINT</option>
                    <option value="MPS">MOLINEROS POR PS</option>
                    <option value="MEN">MOLINEROS POR ENSALUD</option>
                    <option value="CEM">CEMIC</option>
                    <option value="MIM">IMESA</option>
                    <option value="MON">MONOTRIBUTO</option>
                </select>
            </td>

            <td style="white-space:nowrap;">Proceso:</td>
            <td style="white-space:nowrap;">
                <select name="<portlet:namespace />tipoProceso" id="<portlet:namespace />tipoProceso">
                    <option value="0" selected="selected">Todos</option>
                    <% for (ClaseBase c : tipos) { %>
                    <option value="<%= c.getId() %>"><%= c.getDescripcion() %></option>
                    <% } %>
                </select>
            </td>

            <td style="white-space:nowrap;">
                <input id="<portlet:namespace />buscar"
                       value="<liferay-ui:message key="buscar"/>"
                       title="<liferay-ui:message key="buscar" />"
                       type="button"
                       onClick="javascript:<portlet:namespace/>buscarMovimientos();"/>

                &nbsp;

                <input id="<portlet:namespace />limpiar"
                       value="Limpiar"
                       title="Limpiar resultados"
                       type="button"
                       onClick="javascript:return <portlet:namespace/>limpiarResultados();"/>
            </td>

            <td style="width:100%;">&nbsp;</td>

            <td align="right" style="white-space:nowrap; padding-left:20px;">
                <input id="<portlet:namespace />help_periodos_trabajados"
                       value="Ver periodos trabajados"
                       title="Ver periodos trabajados"
                       type="button"
                       onclick="javascript:<portlet:namespace />verPeriodosTrabajados();"/>
            </td>
        </tr>
    </table>

    <!-- SPINNER: inmediatamente debajo de la tabla Detalle (y fuera del div que se reemplaza por AJAX) -->
    <div align="center" id="<portlet:namespace/>buscandoDetalle" style="display:none; margin:6px 0 0 0;">
        <table style="align:center;">
            <tr>
                <td><liferay-ui:message key='buscando'/></td>
                <td align="center">
                    <img alt="<liferay-ui:message key='buscando'/>"
                         src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif"/>
                </td>
            </tr>
        </table>
    </div>

    <table id="<portlet:namespace/>archivosDebitosBlock" style="display:none;">
        <tr>
            <td><label><liferay-ui:message key="archivo-debito-tercerizadora"/>:</label></td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3">
                <div id="<portlet:namespace />archivos_debitos">
                    <jsp:include page='/html/portlet/liquidaciones/reportes/archivos_debitos_terciarizadoras.jsp'/>
                </div>
            </td>
        </tr>
    </table>

    <table>
        <tr>
            <td colspan="3">
                <div id="<portlet:namespace />listado_debitos"></div>
            </td>
        </tr>
    </table>

    <hr/>

    <!-- =========================
         BLOQUE 2: AGREGAR (STAGING)
         (se oculta cuando el período está cerrado)
         ========================= -->
    <div id="<portlet:namespace/>bloque2Staging" style="display:none;">
        <table class="lfr-table">
            <tr>
                <td colspan="9"><strong>Agregar (segunda búsqueda)</strong></td>
            </tr>
            <tr>
                <td><label><liferay-ui:message key="periodo"/>:</label></td>
                <td>
                    <liferay-ui:input-date
                            dayParam="fechaDesdeDia_2"
                            dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
                            monthParam="fechaDesdeMes_2"
                            monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
                            yearParam="fechaDesdeAnio_2"
                            yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
                            yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
                            yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
                            firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </td>

                <td>
                    <input id="<portlet:namespace />agregar"
                           value="Buscar"
                           type="button"
                           onClick="javascript:<portlet:namespace/>agregarMovimientos();"/>
                </td>
            </tr>
            <tr>
                <td colspan="9">&nbsp;</td>
            </tr>
        </table>

        <table>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3">
                    <div id="<portlet:namespace />listado_debitos_anexar"></div>
                </td>
            </tr>
        </table>

        <!-- SPINNER: inmediatamente debajo de la tabla Agregar -->
        <div align="center" id="<portlet:namespace/>buscandoAgregar" style="display:none; margin:6px 0 0 0;">
            <table style="align:center;">
                <tr>
                    <td><liferay-ui:message key='buscando'/></td>
                    <td align="center">
                        <img alt="<liferay-ui:message key='buscando'/>"
                             src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif"/>
                    </td>
                </tr>
            </table>
        </div>

    </div> <!-- /bloque2Staging -->
</fieldset>

<script type="text/javascript">

    function showDetalleLoading(on) {
        var $s = jQuery("#" + nsKey("buscandoDetalle"));
        if (!$s.length) return;
        if (on) $s.show(); else $s.hide();
    }

    function showAgregarLoading(on) {
        var $s = jQuery("#" + nsKey("buscandoAgregar"));
        if (!$s.length) return;
        if (on) $s.show(); else $s.hide();
    }

    // =========================
    // Compat layer: params + namespace + aliases
    // =========================
    var NS = "<portlet:namespace/>";
    function fixUrl(u) { return (u || "").replace(/&amp;/g, "&"); }

    var RENDER_URL = fixUrl("<%= renderDebitosURLStr %>");
    var ACTION_URL = fixUrl("<%= actionDebitosURLStr %>");

    function nsKey(k) { return NS + k; }

    // Para liferay-ui:input-date (más robusto)
    function valByName(paramName) {
        return jQuery("[name='" + nsKey(paramName) + "']").val();
    }

    // Para inputs/selects con id namespaced
    function valById(idNoNs) {
        var $e = jQuery("#" + nsKey(idNoNs));
        if ($e.length) return $e.val();

        $e = jQuery("#" + idNoNs); // fallback sin namespace
        if ($e.length) return $e.val();

        $e = jQuery("[name='" + nsKey(idNoNs) + "']"); // fallback por name
        if ($e.length) return $e.val();

        return "";
    }

    // Agrega param plano + namespaced
    function put(p, k, v) {
        p[k] = v;
        p[nsKey(k)] = v;
        return p;
    }

    // Agrega varios nombres (aliases) con plano+namespaced
    function putAliases(p, keys, v) {
        for (var i = 0; i < keys.length; i++) put(p, keys[i], v);
        return p;
    }

    // Set estándar de "tipo proceso" en todos los dialectos que ya usás
    function putTipoProceso(p, tipo) {
        putAliases(p, ["tipo_proceso", "tipoProceso", "tipoDebito"], tipo || "");
        return p;
    }

    // Cache keys (por compat con tu Action/JSP)
    function putCacheKeys(p, key) {
        key = key || "";
        putAliases(p, ["cacheKey", "workCacheKey"], key);
        return p;
    }

    // Cmd (plano+namespaced)
    function putCmd(p, cmd) { return put(p, "cmd", cmd); }

    // busquedaMode (plano+namespaced)
    function putMode(p, mode) { return put(p, "busquedaMode", mode); }

    showDetalleLoading(false);showAgregarLoading(false);

    // ocultar ambos dias
    jQuery("#<portlet:namespace/>fechaDesdeDia").hide();
    jQuery("#<portlet:namespace/>fechaDesdeDia_2").hide();

    function applyYearSelectUX(yearIdNoNs) {
        var $y = jQuery("#" + nsKey(yearIdNoNs));
        if (!$y.length) return;

        // preservar selección actual
        var current = $y.val();

        // ordenar options por value numérico desc
        var opts = $y.find("option").get();
        opts.sort(function (a, b) {
            var va = parseInt((a.value || "").trim(), 10);
            var vb = parseInt((b.value || "").trim(), 10);

            if (isNaN(va) || isNaN(vb)) {
                // fallback seguro
                return ("" + (b.value || "")).localeCompare("" + (a.value || ""));
            }
            return vb - va;
        });

        $y.empty().append(opts);

        // restaurar selección
        if (current != null && current !== "") $y.val(current);

        // 10 visibles + scroll automático
        $y.prop("size", 10);

        // refuerzo visual (no siempre necesario, pero ayuda)
        $y.css({
            "overflow-y": "auto"
        });
    }

    function <portlet:namespace/>limpiarResultados() {
        // 0) apagar spinners
        try { showDetalleLoading(false); } catch (e0) {}
        try { showAgregarLoading(false); } catch (e1) {}

        // 1) flags de estado
        try { jQuery("#" + nsKey("busquedaEjecutada")).val("0"); } catch (e2) {}
        try { jQuery("#" + nsKey("periodoCerrado")).val("0"); } catch (e3) {}
        try { jQuery("#" + nsKey("periodoCerradoCacheKey")).val(""); } catch (e4) {}

        // (opcional) evitar alerts viejos si venías de no-AJAX
        try { jQuery("#" + nsKey("cierrePeriodoOk")).val(""); } catch (e5) {}
        try { jQuery("#" + nsKey("cierrePeriodoError")).val(""); } catch (e6) {}

        // 2) vaciar resultados (MAIN + STAGING)
        try { jQuery("#" + nsKey("listado_debitos")).html(""); } catch (e7) {}
        try { jQuery("#" + nsKey("listado_debitos_anexar")).html(""); } catch (e8) {}

        // 3) ocultar staging + banner
        try { jQuery("#" + nsKey("bloque2Staging")).hide(); } catch (e9) {}
        try { jQuery("#" + nsKey("periodoCerradoBanner")).hide(); } catch (e10) {}

        // 4) ocultar/limpiar bloque de archivos
        try { jQuery("#" + nsKey("archivosDebitosBlock")).hide(); } catch (e11) {}
        try { jQuery("#" + nsKey("archivos_debitos")).html(""); } catch (e12) {}

        return false; // evita cualquier comportamiento raro de submit/navegación
    }

    jQuery(document).ready(function () {
        applyYearSelectUX("fechaDesdeAnio");
        applyYearSelectUX("fechaDesdeAnio_2");

        // Main SIEMPRE NEW
        try { jQuery("#" + nsKey("busquedaMode")).val("NEW"); } catch (e0) {}

        // Estado inicial: todavía no hubo búsqueda válida
        try { jQuery("#" + nsKey("busquedaEjecutada")).val("0"); } catch (e1) {}
        try { jQuery("#" + nsKey("bloque2Staging")).hide(); } catch (e2) {}
        try { jQuery("#" + nsKey("periodoCerradoBanner")).hide(); } catch (e3) {}

        // Flags de cierre (si venís de un flujo NO-AJAX)
        try { <portlet:namespace/>_showCierreIfAny(); } catch (e4) {}

        // Aplica UI (con gating: si busquedaEjecutada!=1, no muestra staging)
        try { <portlet:namespace/>_applyPeriodoCerradoUI(); } catch (e5) {}

        // Al cambiar cualquier filtro del bloque 1 => reset + recalcular cerrado (pre-check)
        jQuery("[name='" + nsKey("fechaDesdeMes") + "'], [name='" + nsKey("fechaDesdeAnio") + "']").on("change", function () {
            <portlet:namespace/>limpiarResultados();
            try { <portlet:namespace/>_refreshPeriodoCerrado(); } catch (e) {}
        });

        jQuery("#" + nsKey("tipo_debitos_tercerizadoras")).on("change", function () {
            <portlet:namespace/>limpiarResultados();
            try { <portlet:namespace/>_refreshPeriodoCerrado(); } catch (e) {}
        });

        jQuery("#" + nsKey("tipoProceso")).on("change", function () {
            <portlet:namespace/>limpiarResultados();
            try { <portlet:namespace/>_refreshPeriodoCerrado(); } catch (e) {}
        });

    });

    // Mostrar flags de cierre (si existen en el DOM). No interfiere con AJAX.
    function <portlet:namespace/>_showCierreIfAny() {
        var ok = "";
        var err = "";

        try { ok = jQuery("#" + nsKey("cierrePeriodoOk")).val() || ""; } catch (e) {}
        try { err = jQuery("#" + nsKey("cierrePeriodoError")).val() || ""; } catch (e2) {}

        ok = ("" + ok).trim();
        err = ("" + err).trim();

        if (!ok && !err) return;

        // Limpieza (evita alert duplicado si se re-evalúa)
        try { jQuery("#" + nsKey("cierrePeriodoOk")).val(""); } catch (e3) {}
        try { jQuery("#" + nsKey("cierrePeriodoError")).val(""); } catch (e4) {}

        if (ok === "1") {
            alert("Período cerrado.");
            return;
        }

        if (err === "periodo_ya_cerrado") {
            alert("El período ya estaba cerrado. No se realizó ningún cambio.");
            return;
        }

        if (err) {
            alert("No se pudo cerrar el período. (" + err + ")");
        } else {
            alert("No se pudo cerrar el período.");
        }
    }

    // ============================================================
    // REFAC: detectar si existe cierre para el período (y opcionalmente obtener cacheKey)
    // Usa el mismo endpoint ya existente: hay_reporte_tercerizadora_grabar
    // ============================================================
    function <portlet:namespace/>_getPeriodoCerradoInfo(desde_mes, desde_anio, tipo_debitos_tercerizadoras, tipo_proceso) {

        // ok=false => "no confiable" (no usar para decisiones fuertes)
        var info = { cerrado: false, cacheKey: "", cantidad: 0, ok: false };

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/hay_reporte_tercerizadora_grabar';
        url += '&fechaDesdeMes=' + encodeURIComponent(desde_mes);
        url += '&fechaDesdeAnio=' + encodeURIComponent(desde_anio);
        url += '&tipo_debitos_tercerizadoras=' + encodeURIComponent(tipo_debitos_tercerizadoras);

        // mandamos el tipo de proceso (por compat mandamos varios nombres)
        var tp = (tipo_proceso != null) ? ("" + tipo_proceso) : "";
        url += '&tipo_proceso=' + encodeURIComponent(tp);
        url += '&tipoProceso=' + encodeURIComponent(tp);
        url += '&tipoDebito=' + encodeURIComponent(tp);

        url += '&rnd=' + (new Date().getTime());

        jQuery.ajax({
            url: url,
            async: false,
            cache: false,
            success: function (data) {
                try {
                    var obj = jQuery.parseJSON(data);

                    // Si parseó JSON, ya es "confiable"
                    info.ok = true;

                    var r = (obj && obj.resultado != null) ? ("" + obj.resultado) : "";
                    var c = (obj && obj.cerrado != null) ? ("" + obj.cerrado) : "";

                    r = (r != null) ? ("" + r).trim() : "";
                    c = (c != null) ? ("" + c).trim() : "";

                    // 1) resultado como conteo (17, 70, etc.) o como "1"
                    var rNum = parseInt(r, 10);
                    if (!isNaN(rNum) && rNum > 0) {
                        info.cerrado = true;
                        info.cantidad = rNum; // si vino "1" también cae acá
                    }

                    // 2) también aceptamos "cerrado" boolean/string
                    if (!info.cerrado && c) {
                        var cUp = c.toUpperCase();
                        if (c === "1" || cUp === "TRUE" || cUp === "T" || cUp === "YES") {
                            info.cerrado = true;
                        }
                    }

                    // 3) cacheKey (si el backend lo manda)
                    if (obj) {
                        if (obj.cacheKey) info.cacheKey = ("" + obj.cacheKey);
                        else if (obj.cache_key) info.cacheKey = ("" + obj.cache_key);
                        else if (obj.workCacheKey) info.cacheKey = ("" + obj.workCacheKey);
                        else if (obj.work_cache_key) info.cacheKey = ("" + obj.work_cache_key);
                    }

                } catch (e) {
                    // JSON roto => no confiable
                    info.ok = false;
                    info.cerrado = false;
                    info.cacheKey = "";
                    info.cantidad = 0;
                }
            },
            error: function () {
                // request fallida => no confiable
                info.ok = false;
                info.cerrado = false;
                info.cacheKey = "";
                info.cantidad = 0;
            }
        });

        info.cacheKey = (info.cacheKey != null) ? ("" + info.cacheKey).trim() : "";
        return info;
    }

    function <portlet:namespace/>_refreshPeriodoCerrado() {

        // leer selects generados por liferay-ui:input-date (robusto por name)
        var mes = valByName("fechaDesdeMes");
        var anio = valByName("fechaDesdeAnio");

        var terc = valById("tipo_debitos_tercerizadoras");
        var tipo = valById("tipoProceso");
        if (tipo == null || tipo === "") tipo = "0";

        // reset antes de recalcular (evita "pegado")
        jQuery("#" + nsKey("periodoCerrado")).val("0");
        jQuery("#" + nsKey("periodoCerradoCacheKey")).val("");

        var info = <portlet:namespace/>_getPeriodoCerradoInfo(mes, anio, terc, tipo);

        jQuery("#" + nsKey("periodoCerrado")).val(info.cerrado ? "1" : "0");
        jQuery("#" + nsKey("periodoCerradoCacheKey")).val(info.cacheKey || "");

        <portlet:namespace/>_applyPeriodoCerradoUI();
    }

    function <portlet:namespace/>_setPeriodoCerrado(flag, cacheKey) {
        var v = flag ? "1" : "0";
        try { jQuery("#" + nsKey("periodoCerrado")).val(v); } catch (e) {}
        try { jQuery("#" + nsKey("periodoCerradoCacheKey")).val(cacheKey || ""); } catch (e2) {}
        <portlet:namespace/>_applyPeriodoCerradoUI();
    }

    function <portlet:namespace/>_applyPeriodoCerradoUI() {

        // 0) Sincronizar desde el hijo (si existe) ANTES de decidir UI
        try {
            var vSrv = jQuery("#" + nsKey("periodoCerradoSrv")).val(); // hidden del hijo
            if (vSrv != null && vSrv !== "") {
                jQuery("#" + nsKey("periodoCerrado")).val(vSrv === "1" ? "1" : "0");
            }
        } catch (e0) {}

        // 0.5) Si todavía NO hubo una búsqueda válida, no mostrar ni banner ni bloque 2
        var buscado = "0";
        try { buscado = jQuery("#" + nsKey("busquedaEjecutada")).val() || "0"; } catch (eB) {}

        if (buscado !== "1") {
            try { jQuery("#" + nsKey("periodoCerradoBanner")).hide(); } catch (e1) {}
            try { jQuery("#" + nsKey("bloque2Staging")).hide(); } catch (e2) {}
            return;
        }

        // 1) Valor final (padre ya sincronizado)
        var cerrado = "0";
        try { cerrado = jQuery("#" + nsKey("periodoCerrado")).val() || "0"; } catch (e) {}

        if (cerrado === "1") {
            try { jQuery("#" + nsKey("periodoCerradoBanner")).show(); } catch (e1) {}
            try { jQuery("#" + nsKey("bloque2Staging")).hide(); } catch (e2) {}

            try {
                var $root = jQuery("#" + nsKey("listado_debitos"));

                $root.find("#" + nsKey("btnCerrarPeriodo")).hide();
                $root.find("#" + nsKey("btnGuardarBorrador")).hide();

                $root.find("input[type=button][value='Eliminar Seleccionados']").hide();
                $root.find("input[type=button][value='Marcar Todos']").hide();
                $root.find("input[type=button][value='Desmarcar Todos']").hide();

                $root.find("input[type=checkbox][name='debitos']").prop("checked", false).hide();

                $root.find("img[src*='/common/delete.png']").hide();

                try { <portlet:namespace/>_removeColumnsDetalle(); } catch (e4) {}
            } catch (e3) {}
        } else {
            try { jQuery("#" + nsKey("periodoCerradoBanner")).hide(); } catch (e4) {}
            try { jQuery("#" + nsKey("bloque2Staging")).show(); } catch (e5) {}
            try { applyYearSelectUX("fechaDesdeAnio_2"); } catch (e6) {}
        }
    }

    function <portlet:namespace/>_removeColumnsDetalle() {
        var $root = jQuery("#" + nsKey("listado_debitos"));
        if ($root.length === 0) return;

        // tabla que genera liferay-ui:search-iterator (varía según theme)
        var $tbl = $root
            .find("table.taglib-search-iterator, table.lfr-search-container, table.search-container, table")
            .filter(function () { return jQuery(this).find("th").length > 0; })
            .first();

        if ($tbl.length === 0) return;

        // primera fila que tenga TH
        var $hdr = $tbl.find("tr").filter(function () {
            return jQuery(this).find("th").length > 0;
        }).first();

        if ($hdr.length === 0) return;

        var idxSel = -1;
        var idxAcc = -1;

        $hdr.find("th").each(function (i) {
            var t = jQuery.trim(jQuery(this).text() || "");
            if (t === "Sel." || t === "Sel") idxSel = i;
            if (t === "Eliminar" || t === "Anexar") idxAcc = i;
        });

        // 1) borrar Sel.
        if (idxSel >= 0) {
            $tbl.find("tr").each(function () {
                jQuery(this).children().eq(idxSel).remove();
            });
            if (idxAcc > idxSel) idxAcc = idxAcc - 1;
        }

        // 2) borrar Eliminar/Anexar
        if (idxAcc >= 0) {
            $tbl.find("tr").each(function () {
                jQuery(this).children().eq(idxAcc).remove();
            });
        }
    }

    // ============================================================
    // BUSCAR (bloque 1) -> refresca JSP viejo
    // Si el período está cerrado: NO valida contra fuente, y fuerza modo solo lectura
    // ============================================================
    function <portlet:namespace />buscarMovimientos() {

        // Hasta que el render del hijo termine, NO consideramos válida la búsqueda
        try { jQuery("#" + nsKey("busquedaEjecutada")).val("0"); } catch (e) {}
        try { jQuery("#" + nsKey("listado_debitos_anexar")).html(""); } catch (e2) {}
        try { jQuery("#" + nsKey("bloque2Staging")).hide(); } catch (e3) {}

        // leer selects generados por liferay-ui:input-date (robusto por name)
        var desde_mes  = valByName("fechaDesdeMes");
        var desde_anio = valByName("fechaDesdeAnio");

        var tipo_debitos_tercerizadoras = valById("tipo_debitos_tercerizadoras");
        var tipo_proceso = valById("tipoProceso");

        // Unificado: SIEMPRE NEW
        try { jQuery("#" + nsKey("busquedaMode")).val("NEW"); } catch (e0) {}

        if (tipo_debitos_tercerizadoras == '0') {
            alert('Debe seleccionar una tercerizadora.');
            return false;
        }

        var mes = parseInt(desde_mes, 10) + 1;
        var periodo = desde_anio + '-' + PadLeft(mes, 2);

        // Pre-check (HINT): si falla, no bloquea; el estado final lo decide el server (hijo)
        var cerradoInfo = null;
        try { cerradoInfo = <portlet:namespace/>_getPeriodoCerradoInfo(desde_mes, desde_anio, tipo_debitos_tercerizadoras, tipo_proceso); }
        catch (ePre) { cerradoInfo = { cerrado: false, cacheKey: "", cantidad: 0, ok: false }; }

        // Pre-check: solo sirve para setear flags / optimizar validaciones.
        // NO tocar UI del block acá: se decide al final con _syncPeriodoCerradoFromChild().
        try {
            if (cerradoInfo && cerradoInfo.ok) {
                // Solo setear flags (NO aplicar UI acá)
                jQuery("#" + nsKey("periodoCerrado")).val(cerradoInfo.cerrado ? "1" : "0");
                jQuery("#" + nsKey("periodoCerradoCacheKey")).val(cerradoInfo.cacheKey || "");
            }
        } catch (eUi) {}

        showDetalleLoading(true);

        // -------- params unificados (plano + namespaced + aliases) --------
        var params = {};
        put(params, "periodo", periodo);
        put(params, "fechaDesdeDia", "01");
        put(params, "fechaDesdeMes", desde_mes);
        put(params, "fechaDesdeAnio", desde_anio);
        put(params, "rnd", "" + new Date().getTime());

        putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], tipo_debitos_tercerizadoras);
        putTipoProceso(params, tipo_proceso);

        putMode(params, "NEW");

        // señal al Action/JSP hijo para comportarse como "cerrado": SOLO si el pre-check es confiable y cerró
        if (cerradoInfo && cerradoInfo.ok && cerradoInfo.cerrado) {
            put(params, "periodoCerrado", "1"); // solo fuerza cerrado
        }

        // cacheKey: solo si viene
        if (cerradoInfo && cerradoInfo.cacheKey) {
            putCacheKeys(params, cerradoInfo.cacheKey);
        }

        putCmd(params, "search");

        try { console.log("[PRE] ok=", cerradoInfo && cerradoInfo.ok, "cerrado=", cerradoInfo && cerradoInfo.cerrado, "ck=", cerradoInfo && cerradoInfo.cacheKey); } catch(e) {}

        jQuery('#' + nsKey("listado_debitos")).load(RENDER_URL, params, function (responseText) {
            showDetalleLoading(false); showAgregarLoading(false);
            evalScriptsSafe(responseText);

            // Ahora sí: la búsqueda terminó (válida o cerrada, eso lo decide el hijo)
            try { jQuery("#" + nsKey("busquedaEjecutada")).val("1"); } catch (e0) {}

            try { <portlet:namespace/>_syncPeriodoCerradoFromChild(); } catch (e) {}
        });

        return false;
    }

    function <portlet:namespace/>_syncPeriodoCerradoFromChild() {
        var $main = jQuery("#" + nsKey("listado_debitos"));

        var v = $main.find("#" + nsKey("periodoCerradoSrv")).val();
        var wk = $main.find("#" + nsKey("workCacheKey")).val();

        v = (v != null ? (""+v).trim() : "");
        var cerrado = (v === "1" || v.toUpperCase() === "TRUE");

        if (wk) jQuery("#" + nsKey("periodoCerradoCacheKey")).val(wk);

        if (cerrado) {
            jQuery("#" + nsKey("archivosDebitosBlock")).show();
            <portlet:namespace/>_loadArchivosDebitos();
        } else {
            jQuery("#" + nsKey("archivosDebitosBlock")).hide();
            // opcional: no vaciar para debug
            // jQuery("#" + nsKey("archivos_debitos")).html("");
        }

        <portlet:namespace/>_applyPeriodoCerradoUI();
    }

    // ============================================================
    // AGREGAR (bloque 2) -> staging
    // Si el período está cerrado, NO se permite.
    // ============================================================
    function <portlet:namespace/>agregarMovimientos() {

        try {
            var be = jQuery("#" + nsKey("busquedaEjecutada")).val() || "0";
            if (be !== "1") { alert("Primero ejecutá Buscar."); return false; }
        } catch (e) {}

        try {
            var cerrado = jQuery("#" + nsKey("periodoCerrado")).val() || "0";
            if (cerrado === "1") {
                alert("El período está cerrado. No se puede anexar.");
                return false;
            }
        } catch (e00) {}

        var desde_mes = valByName("fechaDesdeMes_2");
        var desde_anio = valByName("fechaDesdeAnio_2");

        var tipo_debitos_tercerizadoras = valById("tipo_debitos_tercerizadoras");
        var tipo_proceso = valById("tipoProceso");

        if (tipo_debitos_tercerizadoras == '0') {
            alert('Debe seleccionar una tercerizadora.');
            return false;
        }

        var mes = parseInt(desde_mes, 10) + 1;
        var periodo = desde_anio + '-' + PadLeft(mes, 2);

        showAgregarLoading(true);

        // workKey del main (si existe)
        var workKey = "";
        var el = document.getElementById("<portlet:namespace/>workCacheKey");
        if (el && el.value) workKey = el.value;

        // -------- params unificados --------
        var params = {};
        put(params, "periodo", periodo);
        put(params, "fechaDesdeDia", "01");
        put(params, "fechaDesdeMes", desde_mes);
        put(params, "fechaDesdeAnio", desde_anio);

        putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], tipo_debitos_tercerizadoras);
        putTipoProceso(params, tipo_proceso);

        // STAGING forzado a LEGACY
        putMode(params, "LEGACY");

        putCacheKeys(params, workKey);
        putCmd(params, "appendSearch");

        // cache-buster
        put(params, "rnd", "" + (new Date().getTime()));

        jQuery('#' + nsKey("listado_debitos_anexar")).load(RENDER_URL, params, function (responseText) {
            showDetalleLoading(false);showAgregarLoading(false);
            evalScriptsSafe(responseText);

            try { applyYearSelectUX("fechaDesdeAnio_2"); } catch (eX) {}

            try { <portlet:namespace/>filtrarAnexar(); } catch (e) {}
        });
        return false;
    }

    function <portlet:namespace/>_collectIdsFromContainer(containerId) {
        var ids = "";
        jQuery('#' + containerId + ' input[type=checkbox][name="<portlet:namespace/>idsRow"]:checked').each(function () {
            ids += jQuery(this).val() + ';';
        });
        return ids;
    }

    function <portlet:namespace/>anexarSeleccionados() {

        try {
            var cerrado = jQuery("#" + nsKey("periodoCerrado")).val() || "0";
            if (cerrado === "1") {
                alert("El período está cerrado. No se puede anexar.");
                return false;
            }
        } catch (e00b) {}

        // workKey del main (obligatorio)
        var workKey = "";
        var el = document.getElementById("<portlet:namespace/>workCacheKey");
        if (el && el.value) workKey = el.value;
        if (!workKey) {
            alert("No hay workCacheKey (primero ejecutá Buscar).");
            return false;
        }

        var ids = <portlet:namespace/>_collectIdsFromContainer("<portlet:namespace/>listado_debitos_anexar");
        if (!ids) {
            alert("No seleccionaste nada para anexar.");
            return false;
        }

        var tipo_proceso = valById("tipoProceso");

        var busquedaModeMain = "";
        try { busquedaModeMain = jQuery("#" + nsKey("busquedaMode")).val(); } catch (e0) {}
        if (!busquedaModeMain) busquedaModeMain = "NEW";
        var busquedaModeStaging = "LEGACY";

        showDetalleLoading(true); showAgregarLoading(true);

        // 1) POST action: muta cache (staging -> main)
        var postData = {};
        putCmd(postData, "anexarDetalle");
        put(postData, "ids", ids);
        putCacheKeys(postData, workKey);
        putTipoProceso(postData, tipo_proceso);
        putMode(postData, busquedaModeMain);

        jQuery.ajax({
            url: ACTION_URL,
            type: "POST",
            data: postData,
            success: function () {

                // 2) GET render: refrescar MAIN desde cache (cmd=deleteDetalle)
                var mainParams = {};
                putCmd(mainParams, "deleteDetalle");
                putCacheKeys(mainParams, workKey);
                putTipoProceso(mainParams, tipo_proceso);
                putMode(mainParams, busquedaModeMain);
                put(mainParams, "rnd", "" + (new Date().getTime()));

                jQuery('#' + nsKey("listado_debitos")).load(
                    RENDER_URL,
                    mainParams,
                    function () {

                        // 3) GET render: refrescar STAGING sin reseed (reuseStaging=true)
                        var stParams = {};
                        putCmd(stParams, "appendSearch");
                        putCacheKeys(stParams, workKey);
                        put(stParams, "reuseStaging", "true");
                        putTipoProceso(stParams, tipo_proceso);
                        putMode(stParams, busquedaModeStaging);
                        put(stParams, "rnd", "" + (new Date().getTime()));

                        jQuery('#' + nsKey("listado_debitos_anexar")).load(
                            RENDER_URL,
                            stParams,
                            function () {
                                showDetalleLoading(false);showAgregarLoading(false);
                            }
                        );
                    }
                );
            },
            error: function (xhr) {
                showDetalleLoading(false);showAgregarLoading(false);
                alert("Error anexando. status=" + xhr.status);
            }
        });

        return false;
    }
    function PadLeft(value, length) {
        return (value.toString().length < length) ? PadLeft("0" + value, length) : value;
    }

    var ARCHIVOS_URL = fixUrl("<%= (String)pageContext.getAttribute("renderArchivosURL") %>");

    function <portlet:namespace/>_loadArchivosDebitos() {
        var params = {};
        put(params, "fechaDesdeDia", "01");
        put(params, "fechaDesdeMes", valByName("fechaDesdeMes"));
        put(params, "fechaDesdeAnio", valByName("fechaDesdeAnio"));
        putAliases(params, ["tipo_debitos_tercerizadoras","tipo_debito"], valById("tipo_debitos_tercerizadoras"));
        putTipoProceso(params, valById("tipoProceso"));

        // opcional pero útil si el backend de archivos usa work/cacheKey
        try {
            var wk = jQuery("#" + nsKey("periodoCerradoCacheKey")).val()
                || jQuery("#" + nsKey("workCacheKey")).val()
                || "";
            if (wk) putCacheKeys(params, wk);
        } catch (e0) {}

        put(params, "rnd", "" + new Date().getTime());

        jQuery("#" + nsKey("archivos_debitos")).load(ARCHIVOS_URL, params);
    }

    function evalScriptsSafe(html) {
        try {
            if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                Liferay.Util.evalScripts(html);
            }
        } catch (e) {}
    }

    function buildRangoPopupMesVigente() {
        var hoy = new Date();

        var hasta = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        var desde = new Date(hoy.getFullYear(), hoy.getMonth(), 1);

        return {
            desdeMes0: desde.getMonth(),
            desdeAnio: desde.getFullYear(),
            hastaMes0: hasta.getMonth(),
            hastaAnio: hasta.getFullYear()
        };
    }

    var popupE;
    function <portlet:namespace />verPeriodosTrabajados(){
        if (popupE == null) {
            popupE = Liferay.Popup({
                title: "Ver periodos trabajados",
                modal: true,
                width: 1080,
                onClose: function () { popupE = null; }
            });
        }

        var hoy = new Date();

        var hasta = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        var desde = new Date(hoy.getFullYear(), hoy.getMonth(), 1);

        // FORZAR defaults del popup
        var terc = "0";
        var tipo = "0";

        var url = fixUrl(
            '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>' +
            '&struts_action=/liquidaciones/debitos_tercerizadoras' +
            '&cmd=periodosTrabajados' +
            '&popupInit=1' +

            '&ptw_fechaDesdeDia=01' +
            '&ptw_fechaDesdeMes=' + encodeURIComponent(desde.getMonth()) +
            '&ptw_fechaDesdeAnio=' + encodeURIComponent(desde.getFullYear()) +
            '&ptw_fechaHastaDia=01' +
            '&ptw_fechaHastaMes=' + encodeURIComponent(hasta.getMonth()) +
            '&ptw_fechaHastaAnio=' + encodeURIComponent(hasta.getFullYear()) +
            '&ptw_incluirCerrados=1' +
            '&ptw_incluirBorradores=1' +
            '&ptw_tipo_debitos_tercerizadoras=' + encodeURIComponent(terc) +
            '&ptw_tipoProceso=' + encodeURIComponent(tipo) +

            '&ptp_fechaDesdeDia=01' +
            '&ptp_fechaDesdeMes=' + encodeURIComponent(desde.getMonth()) +
            '&ptp_fechaDesdeAnio=' + encodeURIComponent(desde.getFullYear()) +
            '&ptp_fechaHastaDia=01' +
            '&ptp_fechaHastaMes=' + encodeURIComponent(hasta.getMonth()) +
            '&ptp_fechaHastaAnio=' + encodeURIComponent(hasta.getFullYear()) +
            '&ptp_tipo_debitos_tercerizadoras=' + encodeURIComponent(terc) +
            '&ptp_tipoProceso=' + encodeURIComponent(tipo) +

            '&rnd=' + (new Date().getTime())
        );

        jQuery(popupE).load(url, function (responseText) {
            try {
                if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                    Liferay.Util.evalScripts(responseText);
                }
            } catch (e) {}
        });
    }
</script>

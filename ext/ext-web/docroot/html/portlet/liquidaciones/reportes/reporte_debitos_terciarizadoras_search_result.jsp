<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>

<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal" %>
<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores" %>
<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros" %>
<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales" %>
<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes" %>

<portlet:actionURL var="deleteDetalleURL">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
    <portlet:param name="cmd" value="deleteDetalle"/>
</portlet:actionURL>

<portlet:actionURL var="guardarBorradorURL">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
    <portlet:param name="cmd" value="guardarBorrador"/>
</portlet:actionURL>

<portlet:actionURL var="reabrirPeriodoURL">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
    <portlet:param name="cmd" value="reabrirPeriodo"/>
</portlet:actionURL>

<portlet:actionURL var="cerrarPeriodoURL">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
    <portlet:param name="cmd" value="cerrarPeriodo"/>
</portlet:actionURL>

<portlet:renderURL var="refreshListadoURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
</portlet:renderURL>

<%
    PortletURL portletURL = renderResponse.createRenderURL();

    NumberFormat format2D = new DecimalFormat("#0.00");
    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");

    // tipo seleccionado (fallback a params viejos)
    String tipoDebito = (String) request.getAttribute("DEBITOS_TIPO_SELECTED");
    if (Validator.isNull(tipoDebito)) {
        tipoDebito = ParamUtil.getString(renderRequest, "tipoDebito",
                ParamUtil.getString(renderRequest, "tipo_proceso", "LI"));
    }
    tipoDebito = (tipoDebito != null) ? tipoDebito.trim().toUpperCase() : "LI";

    // Label visible
    String tipoDebitoLabel = tipoDebito;

    // 1) Intentar resolver por config
    try {
        String cfg = TraeListasServiceUtil.getSystemConfig("DEBITOS_TERCERIZADORAS_TIPO");
        if (!Validator.isNull(cfg)) {
            String[] tiposStr = cfg.split(";");
            for (int i = 0; i < tiposStr.length; i++) {
                String[] kv = tiposStr[i].split("=");
                if (kv != null && kv.length >= 2) {
                    String cod = kv[0] != null ? kv[0].trim().toUpperCase() : "";
                    String desc = kv[1] != null ? kv[1].trim() : "";
                    if (cod.equals(tipoDebito) && !Validator.isNull(desc)) {
                        tipoDebitoLabel = desc;
                        break;
                    }
                }
            }
        }
    } catch (Exception ignore) {}

    // 2) Fallback fijo
    if (tipoDebitoLabel.equals(tipoDebito)) {
        if ("LI".equals(tipoDebito)) tipoDebitoLabel = "Liquidaciones";
        else if ("HO".equals(tipoDebito)) tipoDebitoLabel = "Hospitales";
        else if ("RE".equals(tipoDebito)) tipoDebitoLabel = "Reintegros";
        else if ("PR".equals(tipoDebito)) tipoDebitoLabel = "Prestadores";
    }

    // cacheKey para imprimir en HTML
    String cacheKey = (String) request.getAttribute("DEBITOS_CACHE_KEY");
    if (Validator.isNull(cacheKey)) {
        cacheKey = ParamUtil.getString(renderRequest, "cacheKey", "");
    }
    if (Validator.isNull(cacheKey)) cacheKey = "";

    // ===== cmd / appendMode =====
    String cmd = ParamUtil.getString(renderRequest, "cmd");
    if (Validator.isNull(cmd)) {
        cmd = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "cmd");
    }

    String mode = ParamUtil.getString(renderRequest, "mode"); // permite forzar por jsp:param
    boolean appendMode =
            "append".equalsIgnoreCase(mode)
                    || "appendSearch".equalsIgnoreCase(cmd)
                    || (request.getAttribute("DEBITOS_DETALLE_ANEXAR_RESULTADOS") != null);

    // ===== busquedaMode =====
    String busquedaModeDefault = appendMode ? "LEGACY" : "NEW";
    String busquedaMode = ParamUtil.getString(renderRequest, "busquedaMode", busquedaModeDefault);
    if (Validator.isNull(busquedaMode)) busquedaMode = busquedaModeDefault;
    busquedaMode = busquedaMode.trim().toUpperCase();
    if (!"NEW".equals(busquedaMode) && !"LEGACY".equals(busquedaMode)) busquedaMode = busquedaModeDefault;

    // ===== PERIODO CERRADO (fuente) =====
    boolean periodoCerrado = false;

    // 0) Param SOLO para forzar cerrado
    String pcParam = ParamUtil.getString(renderRequest, "periodoCerrado");
    if (Validator.isNull(pcParam)) {
        pcParam = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "periodoCerrado");
    }
    pcParam = (pcParam != null) ? pcParam.trim() : "";

    boolean pcTrue = false;
    if ("1".equals(pcParam) || "true".equalsIgnoreCase(pcParam) || "CERRADO".equalsIgnoreCase(pcParam)) {
        pcTrue = true;
    } else {
        try {
            int n = Integer.parseInt(pcParam);
            if (n > 0) pcTrue = true;
        } catch (Exception ignore) {}
    }
    if (pcTrue) periodoCerrado = true;

    // 1) Attribute del Action
    if (!periodoCerrado) {
        try {
            Object pc = request.getAttribute("PERIODO_CERRADO");
            if (pc instanceof Boolean) {
                periodoCerrado = ((Boolean) pc).booleanValue();
            } else if (pc instanceof Number) {
                periodoCerrado = ((Number) pc).intValue() == 1;
            } else if (pc != null) {
                String s = String.valueOf(pc).trim();
                periodoCerrado = "1".equals(s) || "true".equalsIgnoreCase(s);
            }
        } catch (Exception ignore) {}
    }

    // 2) Fallback por modo del Action (compat)
    if (!periodoCerrado) {
        try {
            Object mAttr = request.getAttribute("DEBITOS_BUSQUEDA_MODE");
            if (mAttr != null && "CERRADO".equalsIgnoreCase(String.valueOf(mAttr))) {
                periodoCerrado = true;
            }
        } catch (Exception ignore) {}
    }

    // ===== WORK CERRADO (para bloquear operaciones siempre) =====
    boolean workCerrado = false;

    // 1) Param workCerrado
    String wcParam = ParamUtil.getString(renderRequest, "workCerrado");
    if (Validator.isNull(wcParam)) {
        wcParam = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "workCerrado");
    }
    wcParam = (wcParam != null) ? wcParam.trim() : "";

    boolean wcTrue = false;
    if ("1".equals(wcParam) || "true".equalsIgnoreCase(wcParam) || "CERRADO".equalsIgnoreCase(wcParam)) {
        wcTrue = true;
    } else {
        try {
            int n = Integer.parseInt(wcParam);
            if (n > 0) wcTrue = true;
        } catch (Exception ignore) {}
    }
    if (wcTrue) workCerrado = true;

    // 2) Attribute del Action
    if (!workCerrado) {
        try {
            Object wc = request.getAttribute("WORK_CERRADO");
            if (wc == null) wc = request.getAttribute("DEBITOS_WORK_CERRADO");
            if (wc == null) wc = request.getAttribute("workCerrado");

            if (wc instanceof Boolean) {
                workCerrado = ((Boolean) wc).booleanValue();
            } else if (wc instanceof Number) {
                workCerrado = ((Number) wc).intValue() == 1;
            } else if (wc != null) {
                String s = String.valueOf(wc).trim();
                workCerrado = "1".equals(s) || "true".equalsIgnoreCase(s);
            }
        } catch (Exception ignore) {}
    }

    // ==========================
    // FIX: separar "cerrado" (info) de "solo lectura" (UI)
    // ==========================
    final boolean periodoCerradoSrc = periodoCerrado;              // cerrado de la fuente (lo que te interesa como estado)
    final boolean periodoCerradoUI  = (periodoCerradoSrc || workCerrado); // para banner/estado

    // Solo lectura:
    // - MAIN: si cerrado en fuente => readOnly
    // - APPEND: aunque cerrado en fuente => NO readOnly (querés anexar)
    // - WORK cerrado => siempre readOnly
    final boolean readOnlyUI = (workCerrado || (periodoCerradoSrc && !appendMode));

    // ==========================
    // FIX: selección de detalle
    // - appendSearch SIEMPRE usa staging, aunque el período esté cerrado en fuente.
    // ==========================
    List detalle = null;

    if (appendMode) {
        detalle = (List) request.getAttribute("DEBITOS_DETALLE_ANEXAR_RESULTADOS");
    } else if (periodoCerradoSrc) {
        detalle = (List) request.getAttribute("DEBITOS_DETALLE_RESULTADOS_CERRADO");
        if (detalle == null) detalle = (List) request.getAttribute("DEBITOS_DETALLE_RESULTADOS");
    } else {
        detalle = (List) request.getAttribute("DEBITOS_DETALLE_RESULTADOS");
    }

    if (detalle == null) detalle = new ArrayList();

    // -------- headers --------
    List headerNamesDetalle = new ArrayList();

    // checkbox de seleccion -> SOLO si NO es solo lectura
    if (!readOnlyUI) {
        headerNamesDetalle.add("Sel.");
    }
    headerNamesDetalle.add("Estado");

    if ("LI".equals(tipoDebito)) {
        headerNamesDetalle.add("Hospital/Autogestión");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Cargo prestadora");
        headerNamesDetalle.add("Cargo prestadora reclamo");
    } else if ("HO".equals(tipoDebito)) {
        headerNamesDetalle.add("Hospital");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Orden de pago");
        headerNamesDetalle.add("Cargo prestadora");
        headerNamesDetalle.add("Importe total");
        headerNamesDetalle.add("ID liquidación");
    } else if ("RE".equals(tipoDebito)) {
        headerNamesDetalle.add("Documento");
        headerNamesDetalle.add("Apellido");
        headerNamesDetalle.add("Nombre");
        headerNamesDetalle.add("Seccional");
        headerNamesDetalle.add("Descripción");
        headerNamesDetalle.add("Nº reintegro");
        headerNamesDetalle.add("Importe total");
        headerNamesDetalle.add("Número OP");
        headerNamesDetalle.add("Fecha OP");
        headerNamesDetalle.add("Cargo prestadora");
        headerNamesDetalle.add("Reclamo prestacional");
    } else if ("PR".equals(tipoDebito)) {
        headerNamesDetalle.add("ID liquidación");
        headerNamesDetalle.add("Prestador");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Orden de pago");
        headerNamesDetalle.add("Cargo prestadora");
        headerNamesDetalle.add("Reclamos prestacionales");
    } else {
        headerNamesDetalle.add("Detalle");
    }

    // columna acción -> SOLO si NO es solo lectura
    if (!readOnlyUI) {
        headerNamesDetalle.add(appendMode ? "Anexar" : "Eliminar");
    }

    // ===== NO PAGINADO =====
    SearchContainer scDetalle = new SearchContainer(
            renderRequest, null, null,
            "curDetalle",
            Integer.MAX_VALUE,
            portletURL, headerNamesDetalle,
            LanguageUtil.get(pageContext, "no-results-were-found")
    );

    scDetalle.setTotal(detalle.size());
    List rowsDet = scDetalle.getResultRows();

    boolean showTitulo =
            "search".equalsIgnoreCase(cmd) ||
                    "appendSearch".equalsIgnoreCase(cmd) ||
                    !Validator.isNull(cacheKey) ||
                    (detalle != null && !detalle.isEmpty());

    // ==========================
    // Alert no-results (opcional, sin bloquear búsqueda)
    // - Solo para search/appendSearch y NO para refresh post-acciones
    // ==========================
    boolean cmdSearch = "search".equalsIgnoreCase(cmd);
    boolean cmdAppend = "appendSearch".equalsIgnoreCase(cmd);

    String afterCerrar = ParamUtil.getString(renderRequest, "afterCerrar");
    if (Validator.isNull(afterCerrar)) afterCerrar = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "afterCerrar");

    String afterBorrador = ParamUtil.getString(renderRequest, "afterBorrador");
    if (Validator.isNull(afterBorrador)) afterBorrador = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "afterBorrador");

    String afterReabrir = ParamUtil.getString(renderRequest, "afterReabrir");
    if (Validator.isNull(afterReabrir)) afterReabrir = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "afterReabrir");

    boolean suppressNoResultsAlert =
            (!Validator.isNull(afterCerrar) && "1".equals(afterCerrar.trim()))
                    || (!Validator.isNull(afterBorrador) && "1".equals(afterBorrador.trim()))
                    || (!Validator.isNull(afterReabrir) && "1".equals(afterReabrir.trim()));

    for (int i = 0; i < detalle.size(); i++) {
        Object it = detalle.get(i);
        ResultRow row = new ResultRow(it, new Integer(1 + i), i);

        // key por índice
        String detalleKey = tipoDebito + ":" + i;

        // checkbox -> SOLO si NO es solo lectura
        if (!readOnlyUI) {
            String chkName = appendMode ? (renderResponse.getNamespace() + "idsRow") : "debitos";

            StringBuilder sbChk = new StringBuilder();
            sbChk.append("<input type=\"checkbox\" name=\"");
            sbChk.append(chkName);
            sbChk.append("\" value=\"");
            sbChk.append(detalleKey);
            sbChk.append("\" />");

            row.addText(sbChk.toString());
        }

        // ===== STATUS badge =====
        String badge = "";
        Boolean st = null;

        // En MAIN cerrado => badge CERRADO.
        // En APPEND cerrado => mostrar status normal (para que no tape el "pendiente/anexable").
        if (periodoCerradoUI && !appendMode) {
            badge = "<span style='background-color:#D5D8DC; font-weight:bold; font-size:10px'>CERRADO</span>";
        } else {
            try {
                if (it instanceof DebitosHospitales) {
                    st = ((DebitosHospitales) it).getStatus();
                } else if (it instanceof DebitosLiquidacionesPendientes) {
                    st = ((DebitosLiquidacionesPendientes) it).getStatus();
                } else if (it instanceof DebitosaReintegros) {
                    st = ((DebitosaReintegros) it).getStatus();
                } else if (it instanceof DebitosaPrestadores) {
                    st = ((DebitosaPrestadores) it).getStatus();
                }
            } catch (Exception ignore) {
                st = null;
            }

            if (Boolean.TRUE.equals(st)) {
                row.setClassName("row-status-no-match");
                badge = "<span style='background-color:#F1948A; font-weight:bold; font-size:10px'>AGREGADO</span>";
            } else if (Boolean.FALSE.equals(st)) {
                row.setClassName("row-status-ok");
                badge = "<span style='background-color:#ABEBC6; font-weight:bold; font-size:10px'>BORRADOR</span>";
            } else {
                row.setClassName("row-status-unknown");
                badge = "<span style='background-color:#F7DC6F; font-weight:bold; font-size:10px'>SIN ESTADO</span>";
            }
        }

        row.addText(badge);

        if ("LI".equals(tipoDebito) && it instanceof DebitosLiquidacionesPendientes) {
            DebitosLiquidacionesPendientes d = (DebitosLiquidacionesPendientes) it;

            row.addText(d.getHospitalesAutogestion() != null ? d.getHospitalesAutogestion() : "");
            row.addText(d.getFactura() != null ? d.getFactura() : "");
            row.addText(d.getMonto() != null ? format2D.format(d.getMonto()) : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getCargoPrestadoraReclamo() != null ? format2D.format(d.getCargoPrestadoraReclamo()) : "");

        } else if ("HO".equals(tipoDebito) && it instanceof DebitosHospitales) {
            DebitosHospitales d = (DebitosHospitales) it;

            if (!readOnlyUI) {
                if (Boolean.TRUE.equals(d.getStatus())) row.setClassName("row-status-no-match");
                else if (Boolean.FALSE.equals(d.getStatus())) row.setClassName("row-status-ok");
                else row.setClassName("row-status-unknown");
            }

            row.addText(d.getHospital() != null ? d.getHospital() : "");
            row.addText(d.getFactura() != null ? d.getFactura() : "");
            row.addText(d.getMonto() != null ? format2D.format(d.getMonto()) : "");
            row.addText(d.getOrdenPago() != null ? d.getOrdenPago() : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getImporteTotal() != null ? format2D.format(d.getImporteTotal()) : "");
            row.addText(String.valueOf(d.getIdLiquidacion()));

        } else if ("RE".equals(tipoDebito) && it instanceof DebitosaReintegros) {
            DebitosaReintegros d = (DebitosaReintegros) it;

            row.addText(d.getDocumento() != null ? d.getDocumento() : "");
            row.addText(d.getApellido() != null ? d.getApellido() : "");
            row.addText(d.getNombre() != null ? d.getNombre() : "");
            row.addText(d.getSeccional() != null ? d.getSeccional() : "");
            row.addText(d.getDescripcion() != null ? d.getDescripcion() : "");
            row.addText(String.valueOf(d.getNumReintegro()));
            row.addText(d.getImporteTotal() != null ? format2D.format(d.getImporteTotal()) : "");
            row.addText(d.getNumeroOP() != null ? d.getNumeroOP() : "");
            row.addText(d.getFechaOP() != null ? sdfFecha.format(d.getFechaOP()) : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getReclamoPrestacional() != null ? String.valueOf(d.getReclamoPrestacional()) : "");

        } else if ("PR".equals(tipoDebito) && it instanceof DebitosaPrestadores) {
            DebitosaPrestadores d = (DebitosaPrestadores) it;

            row.addText(String.valueOf(d.getIdLiquidacion()));
            row.addText(d.getPrestador() != null ? d.getPrestador() : "");
            row.addText(d.getFactura() != null ? d.getFactura() : "");
            row.addText(d.getMonto() != null ? format2D.format(d.getMonto()) : "");
            row.addText(d.getOrdenPago() != null ? d.getOrdenPago() : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getReclamosPrestacionales() != null ? d.getReclamosPrestacionales()
                    : (d.getReclamoPrestacional() != null ? String.valueOf(d.getReclamoPrestacional()) : ""));

        } else {
            row.addText(String.valueOf(it));
        }

        // acción -> SOLO si NO es solo lectura
        if (!readOnlyUI) {
            if (!appendMode) {
                // eliminar individual (MAIN)
                String altEliminar = LanguageUtil.get(pageContext, "eliminar");
                StringBuilder sbDel = new StringBuilder();
                sbDel.append("<img alt=\"");
                sbDel.append(altEliminar);
                sbDel.append("\" src=\"");
                sbDel.append(themeDisplay.getPathThemeImages());
                sbDel.append("/common/delete.png\" style=\"cursor:pointer;\" ");
                sbDel.append(" onClick=\"javascript:eliminarDebitosSeleccionados('");
                sbDel.append(detalleKey);
                sbDel.append("');\" />");
                row.addText(sbDel.toString());
            } else {
                // anexar individual (STAGING)
                StringBuilder sbAdd = new StringBuilder();
                sbAdd.append("<img alt=\"Anexar\" src=\"");
                sbAdd.append(themeDisplay.getPathThemeImages());
                sbAdd.append("/common/add.png\" style=\"cursor:pointer;\" ");
                sbAdd.append(" onClick=\"javascript:");
                sbAdd.append(renderResponse.getNamespace());
                sbAdd.append("anexarUno('");
                sbAdd.append(detalleKey);
                sbAdd.append("');\" />");
                row.addText(sbAdd.toString());
            }
        }

        rowsDet.add(row);
    }
%>

<% if (showTitulo) { %>

<% if (!suppressNoResultsAlert && (cmdSearch || cmdAppend) && (detalle == null || detalle.isEmpty())) { %>
<script type="text/javascript">
    alert("<%= cmdAppend ? "No hay datos disponibles para anexar." : "No hay datos para el período solicitado." %>");
</script>
<% } %>

<hr/>

<div>
    <h3 style="margin: 10px 0;"><%= appendMode ? "Agregar" : "Detalle" %> (<%= tipoDebitoLabel %>)</h3>

    <% if (periodoCerradoUI) { %>
    <div style="margin:8px 0; padding:6px; border:1px solid #ccc; font-weight:bold;">
        <% if (appendMode && !readOnlyUI && periodoCerradoSrc) { %>
        Período cerrado en fuente: mostrando pendientes anexables.
        <% } else { %>
        Período cerrado.
        <% } %>
    </div>
    <% } %>

    <div style="margin: 6px 0;">
        <% if (!appendMode) { %>

        <% if (!readOnlyUI) { %>
        <input type="button" value="Marcar todos" onclick="javascript:marcarDebitos(true);"/>
        &nbsp;
        <input type="button" value="Desmarcar todos" onclick="javascript:marcarDebitos(false);"/>
        &nbsp;
        <input type="button" value="Eliminar seleccionados" onclick="javascript:eliminarDebitosSeleccionados();"/>

        <input type="button"
               id="<portlet:namespace/>btnGuardarBorrador"
               value="Guardar borrador"
               onclick="javascript:guardarBorradorDebitos();"/>

        &nbsp;
        <input type="button"
               id="<portlet:namespace/>btnCerrarPeriodo"
               value="Cerrar período"
               onclick="return <portlet:namespace/>cerrarPeriodoDebitos();"/>

        &nbsp;
        <% } else { %>
        <%-- En main cerrado: permitir reabrir (solo si está cerrado en UI) --%>
        <% if (periodoCerradoUI) { %>
        <input type="button"
               id="<portlet:namespace/>btnReabrirPeriodo"
               value="Reabrir período"
               onclick="return <portlet:namespace/>reabrirPeriodoDebitos();"/>
        &nbsp;
        <% } %>
        <% } %>

        <input type="button" value="Exportar Excel"
               onclick="javascript:<portlet:namespace/>exportarExcelActual();"/>

        <% } else { %>

        <% if (!readOnlyUI) { %>
        <input type="button" value="Marcar todos" onclick="javascript:<portlet:namespace/>marcarAnexar(true);"/>
        &nbsp;
        <input type="button" value="Desmarcar todos" onclick="javascript:<portlet:namespace/>marcarAnexar(false);"/>
        &nbsp;
        <input type="button" value="Agregar a borrador"
               onclick="javascript:<portlet:namespace/>anexarSeleccionados();"/>
        <% } %>

        <% } %>
    </div>

    <input type="hidden" id="<portlet:namespace/>workCacheKey" value="<%= cacheKey %>"/>
    <input type="hidden" id="<portlet:namespace/>periodoCerradoSrv" value="<%= (periodoCerradoSrc ? "1" : "0") %>"/>

    <liferay-ui:search-iterator searchContainer="<%= scDetalle %>" paginate="<%= false %>"/>
    <%
        int cantMostradosDetalle = (scDetalle != null) ? scDetalle.getTotal() : 0;
        String lblDetalle = (cantMostradosDetalle == 1) ? "elemento" : "elementos";
    %>
    <div style="margin: 6px 0; font-weight: bold;">
        Cantidad de <%= lblDetalle %> mostrados: <%= cantMostradosDetalle %>
    </div>
</div>

<script type="text/javascript">
    // =========================
    // Compat layer: NS + helpers
    // =========================
    var NS = "<portlet:namespace/>";

    function nsKey(k) { return NS + k; }

    function put(p, k, v) {
        p[k] = v;
        p[nsKey(k)] = v;
        return p;
    }

    function putAliases(p, keys, v) {
        for (var i = 0; i < keys.length; i++) put(p, keys[i], v);
        return p;
    }

    function putTipoProceso(p, tipo) {
        return putAliases(p, ["tipo_proceso", "tipoProceso", "tipoDebito"], (tipo || ""));
    }

    function putCacheKeys(p, key) {
        key = key || "";
        return putAliases(p, ["cacheKey", "workCacheKey"], key);
    }

    function safeValById(idNoNs) {
        try { return jQuery("#" + nsKey(idNoNs)).val() || ""; } catch (e0) {}
        try {
            var el = document.getElementById(nsKey(idNoNs));
            return (el && el.value) ? el.value : "";
        } catch (e1) {}
        return "";
    }

    function safeName(nameNoNs) {
        try { return jQuery("[name='" + nsKey(nameNoNs) + "']").val() || ""; } catch (e0) {}
        return "";
    }

    <% if (!appendMode) { %>

    function <portlet:namespace/>cerrarPeriodoDebitos() {
        <% if (readOnlyUI) { %>
        alert("Operación no permitida en modo solo lectura.");
        return false;
        <% } %>

        if (!confirm("¿Confirma cerrar el período actual? Esto persistirá la información.")) return false;

        var cacheKey = "<%= cacheKey %>";
        if (!cacheKey) {
            try { cacheKey = jQuery("#" + nsKey("workCacheKey")).val(); } catch (e0) {}
        }
        if (!cacheKey) {
            alert("Primero ejecutá Buscar para generar el workCacheKey del período.");
            return false;
        }

        var desde_mes = safeValById("fechaDesdeMes");
        var desde_anio = safeValById("fechaDesdeAnio");
        var tercerizadora = safeValById("tipo_debitos_tercerizadoras");
        var proceso = safeValById("tipoProceso") || "<%= tipoDebito %>" || "LI";
        var modo = safeValById("busquedaMode") || "<%= busquedaMode %>" || "NEW";

        if (tercerizadora === "0" || !tercerizadora) { alert("Debe seleccionar una tercerizadora."); return false; }
        if (!desde_mes || !desde_anio) { alert("Período inválido."); return false; }

        proceso = ("" + proceso).toUpperCase();
        modo = ("" + modo).toUpperCase();
        if (modo !== "NEW" && modo !== "LEGACY") modo = "NEW";

        var btnId = nsKey("btnCerrarPeriodo");
        try {
            var b = document.getElementById(btnId);
            if (b && b._busy) return false;
            if (b) { b._busy = true; b.disabled = true; }
        } catch (e3) {}

        var data = {};
        putCacheKeys(data, cacheKey);
        put(data, "fechaDesdeMes", desde_mes);
        put(data, "fechaDesdeAnio", desde_anio);
        putAliases(data, ["tipo_debitos_tercerizadoras", "tipo_debito"], tercerizadora);
        putTipoProceso(data, proceso);
        put(data, "busquedaMode", modo);

        if (!window.jQuery || !jQuery.ajax) {
            var f = document.createElement("form");
            f.method = "POST";
            f.action = "<%= cerrarPeriodoURL %>";
            function add(k, v) {
                var i = document.createElement("input");
                i.type = "hidden"; i.name = k; i.value = v;
                f.appendChild(i);
            }
            for (var k in data) if (data.hasOwnProperty(k)) add(k, data[k]);
            document.body.appendChild(f);
            f.submit();
            return false;
        }

        jQuery.ajax({
            url: "<%= cerrarPeriodoURL %>",
            type: "POST",
            data: data,
            cache: false,
            success: function () {
                var refreshUrl = "<%= refreshListadoURL %>";

                var params = {};
                put(params, "cmd", "search");
                put(params, "fechaDesdeMes", desde_mes);
                put(params, "fechaDesdeAnio", desde_anio);
                putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], tercerizadora);
                putTipoProceso(params, proceso);
                put(params, "busquedaMode", modo);
                put(params, "afterCerrar", "1");
                put(params, "rnd", "" + (new Date().getTime()));

                jQuery.ajax({
                    url: refreshUrl,
                    type: "GET",
                    data: params,
                    cache: false,
                    success: function (html) {
                        jQuery("#" + nsKey("listado_debitos")).html(html);

                        try {
                            if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                                Liferay.Util.evalScripts(html);
                            }
                        } catch (e) {}

                        // ===== FIX: avisarle al PADRE que cambió el cerrado (y que recargue archivos) =====
                        try {
                            // por si quedó en 0 por algún reset anterior
                            jQuery("#" + nsKey("busquedaEjecutada")).val("1");
                        } catch (e0) {}

                        try {
                            // re-lee #<ns>periodoCerradoSrv del fragmento nuevo y muestra/oculta archivos
                            if (typeof <portlet:namespace/>_syncPeriodoCerradoFromChild === "function") {
                                <portlet:namespace/>_syncPeriodoCerradoFromChild();
                            }
                        } catch (e1) {}

                        alert("Período cerrado.");
                    },
                });
            },
            error: function (xhr) {
                alert("No se pudo cerrar el período. HTTP " + xhr.status);
            },
            complete: function () {
                try {
                    var b2 = document.getElementById(btnId);
                    if (b2) { b2._busy = false; b2.disabled = false; }
                } catch (e) {}
            }
        });

        return false;
    }

    function <portlet:namespace/>reabrirPeriodoDebitos() {
        <% if (!periodoCerradoUI) { %>
        alert("El período está abierto: no hay nada que reabrir.");
        return false;
        <% } %>

        if (!confirm("¿Confirma reabrir el período? Esto moverá lo grabado a borrador y habilitará edición.")) {
            return false;
        }

        var desde_mes = safeValById("fechaDesdeMes");
        var desde_anio = safeValById("fechaDesdeAnio");
        var tercerizadora = safeValById("tipo_debitos_tercerizadoras");
        var proceso = safeValById("tipoProceso") || "<%= tipoDebito %>";
        var modo = safeValById("busquedaMode") || "<%= busquedaMode %>" || "NEW";

        if (tercerizadora === "0" || !tercerizadora) { alert("Debe seleccionar una tercerizadora."); return false; }
        if (!desde_mes || !desde_anio) { alert("Período inválido."); return false; }

        proceso = ("" + proceso).toUpperCase();
        modo = ("" + modo).toUpperCase();
        if (modo !== "NEW" && modo !== "LEGACY") modo = "NEW";

        var btnId = nsKey("btnReabrirPeriodo");
        try {
            var b = document.getElementById(btnId);
            if (b && b._busy) return false;
            if (b) { b._busy = true; b.disabled = true; }
        } catch (e) {}

        var data = {};
        put(data, "fechaDesdeMes", desde_mes);
        put(data, "fechaDesdeAnio", desde_anio);
        putAliases(data, ["tipo_debitos_tercerizadoras", "tipo_debito", "idTercerizadora"], tercerizadora);
        put(data, "tipoSel", proceso);
        putTipoProceso(data, proceso);
        put(data, "busquedaMode", modo);

        if (!window.jQuery || !jQuery.ajax) {
            var f = document.createElement("form");
            f.method = "POST";
            f.action = "<%= reabrirPeriodoURL %>";
            function add(k, v) {
                var i = document.createElement("input");
                i.type = "hidden"; i.name = k; i.value = v;
                f.appendChild(i);
            }
            for (var k in data) if (data.hasOwnProperty(k)) add(k, data[k]);
            document.body.appendChild(f);
            f.submit();
            return false;
        }

        jQuery.ajax({
            url: "<%= reabrirPeriodoURL %>",
            type: "POST",
            data: data,
            cache: false,
            success: function () {
                var refreshUrl = "<%= refreshListadoURL %>";

                var params = {};
                put(params, "cmd", "search");
                put(params, "fechaDesdeMes", desde_mes);
                put(params, "fechaDesdeAnio", desde_anio);
                putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], tercerizadora);
                putTipoProceso(params, proceso);
                put(params, "busquedaMode", modo);
                put(params, "periodoCerrado", "0");
                put(params, "workCerrado", "0");
                put(params, "afterReabrir", "1");
                put(params, "rnd", "" + (new Date().getTime()));

                jQuery.ajax({
                    url: refreshUrl,
                    type: "GET",
                    data: params,
                    cache: false,
                    success: function (html) {
                        jQuery("#" + nsKey("listado_debitos")).html(html);
                        try {
                            if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                                Liferay.Util.evalScripts(html);
                            }
                        } catch (e) {}
                        try {
                            if (typeof <portlet:namespace/>_syncPeriodoCerradoFromChild === "function") {
                                <portlet:namespace/>_syncPeriodoCerradoFromChild();
                            }
                        } catch (e1) {}
                        alert("Período reabierto.");
                    },
                    error: function (xhr2) {
                        alert("Período reabierto, pero falló el refresh del listado. HTTP " + xhr2.status);
                    }
                });
            },
            error: function (xhr) {
                alert("No se pudo reabrir el período. HTTP " + xhr.status);
            },
            complete: function () {
                try {
                    var b2 = document.getElementById(btnId);
                    if (b2) { b2._busy = false; b2.disabled = false; }
                } catch (e1) {}
            }
        });

        return false;
    }

    function marcarDebitos(valor) {
        var checkboxes = document.getElementsByName('debitos');
        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].type == "checkbox") {
                checkboxes[i].checked = valor;
            }
        }
    }

    function getCheckedValuesByName(nombre) {
        var els = document.getElementsByName(nombre);
        var values = "";
        for (var i = 0; i < els.length; i++) {
            if (els[i].checked) values = values + els[i].value + ";";
        }
        return values;
    }

    function eliminarDebitosSeleccionados(detalleKey) {
        <% if (readOnlyUI) { %>
        alert("Operación no permitida en modo solo lectura.");
        return false;
        <% } %>

        var ids = "";
        if (detalleKey && detalleKey !== "") ids = detalleKey + ";";
        else ids = getCheckedValuesByName('debitos');

        if (ids === "") { alert("Debe seleccionar elementos para realizar la operación."); return false; }
        if (!confirm("¿Confirma la eliminación de los elementos seleccionados?")) return false;

        if (typeof jQuery === 'undefined' || !jQuery.ajax) {
            var url = '<%= deleteDetalleURL %>'
                + '&ids=' + encodeURIComponent(ids)
                + '&busquedaMode=' + encodeURIComponent("<%= busquedaMode %>")
                + '&tipo_proceso=' + encodeURIComponent("<%= tipoDebito %>");
            window.location.href = url;
            return false;
        }

        var cacheKey = "<%= cacheKey %>";

        var tipoActual = "";
        try {
            var $tipoSel = jQuery("#" + nsKey("tipoProceso"));
            if ($tipoSel && $tipoSel.length > 0) tipoActual = $tipoSel.val();
        } catch (e0) {}
        if (!tipoActual) tipoActual = "<%= tipoDebito %>";
        if (!tipoActual) tipoActual = "LI";
        tipoActual = ("" + tipoActual).toUpperCase();

        var modo = "";
        try { modo = jQuery("#" + nsKey("busquedaMode")).val(); } catch (e1) {}
        if (!modo) modo = "<%= busquedaMode %>";
        if (!modo) modo = "NEW";
        modo = ("" + modo).toUpperCase();
        if (modo !== "NEW" && modo !== "LEGACY") modo = "NEW";

        var data = {};
        put(data, "ids", ids);
        putCacheKeys(data, cacheKey);
        putTipoProceso(data, tipoActual);
        put(data, "busquedaMode", modo);

        jQuery.ajax({
            url: '<%= deleteDetalleURL %>',
            type: "POST",
            data: data,
            cache: false,
            success: function () {
                var refreshUrl = '<%= refreshListadoURL %>';

                var desde_mes = safeValById("fechaDesdeMes");
                var desde_anio = safeValById("fechaDesdeAnio");
                var terc = safeValById("tipo_debitos_tercerizadoras");

                var grabarEl = document.getElementById("<portlet:namespace />grabarDebitos");
                var grabar_debitos = (grabarEl && grabarEl.checked) ? "true" : "false";

                var params = {};
                put(params, "cmd", "deleteDetalle");
                putCacheKeys(params, cacheKey);
                putTipoProceso(params, tipoActual);
                put(params, "fechaDesdeMes", desde_mes);
                put(params, "fechaDesdeAnio", desde_anio);
                putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], terc);
                put(params, "grabarDebitos", grabar_debitos);
                put(params, "busquedaMode", modo);
                put(params, "rnd", "" + (new Date().getTime()));

                jQuery.ajax({
                    url: refreshUrl,
                    type: "GET",
                    data: params,
                    cache: false,
                    success: function (html) {
                        jQuery("#<portlet:namespace />listado_debitos").html(html);
                    },
                    error: function (xhr2) {
                        alert("Falló el refresh del listado. HTTP " + xhr2.status);
                    }
                });
            },
            error: function (xhr) {
                alert("No se pudo eliminar. HTTP " + xhr.status);
            }
        });

        return false;
    }

    function <portlet:namespace/>exportarExcelActual() {
        var tipoProceso = "";
        try { tipoProceso = jQuery("#" + nsKey("tipoProceso")).val(); } catch (e) {}
        if (!tipoProceso) tipoProceso = "<%= tipoDebito %>";
        tipoProceso = ("" + tipoProceso).trim().toUpperCase();

        var segmento =
            (tipoProceso === "HO") ? "HOSPITALES" :
                (tipoProceso === "LI") ? "LIQUIDACIONES_PENDIENTES" :
                    (tipoProceso === "RE") ? "REINTEGROS" :
                        (tipoProceso === "PR") ? "PRESTADORES" :
                            "TOTAL";

        return <portlet:namespace/>descargarExcel(segmento);
    }

    function <portlet:namespace/>descargarExcel(segmento) {
        var desde_mes = safeValById("fechaDesdeMes");
        var desde_anio = safeValById("fechaDesdeAnio");
        var tipo_debitos_tercerizadoras = safeValById("tipo_debitos_tercerizadoras");
        var tipo_proceso = safeValById("tipoProceso");

        var busquedaMode = safeValById("busquedaMode") || "NEW";
        busquedaMode = ("" + busquedaMode).toUpperCase();
        if (busquedaMode !== "NEW" && busquedaMode !== "LEGACY") busquedaMode = "NEW";

        var grabar_debitos = "false";

        if (tipo_debitos_tercerizadoras == '0') {
            alert('Debe seleccionar una tercerizadora.');
            return false;
        }

        var mes = parseInt(desde_mes, 10) + 1;

        function _padLeft(value, length) {
            value = "" + value;
            while (value.length < length) value = "0" + value;
            return value;
        }

        var periodo = desde_anio + '-' + _padLeft(mes, 2);
        var ctx = "<%= themeDisplay.getPathContext() %>";

        var url = ctx + '/xlsservlet/?reporte=REPORTE_DEBITO_TERCERIZADORAS'
            + '&fechaDesdeDia=01'
            + '&fechaDesdeMes=' + encodeURIComponent(desde_mes)
            + '&fechaDesdeAnio=' + encodeURIComponent(desde_anio)
            + '&periodo=' + encodeURIComponent(periodo)
            + '&tipo_debitos_tercerizadoras=' + encodeURIComponent(tipo_debitos_tercerizadoras)
            + '&tipo_debito=' + encodeURIComponent(tipo_debitos_tercerizadoras)
            + '&tipo_proceso=' + encodeURIComponent(tipo_proceso)
            + '&tipoDebito=' + encodeURIComponent(tipo_proceso)
            + '&busquedaMode=' + encodeURIComponent(busquedaMode)
            + '&grabarDebitos=' + encodeURIComponent(grabar_debitos)
            + '&segmento=' + encodeURIComponent(segmento)
            + '&rnd=' + (new Date().getTime());

        window.location.href = url;
        return false;
    }

    function guardarBorradorDebitos() {
        <% if (readOnlyUI) { %>
        alert("Operación no permitida en modo solo lectura.");
        return false;
        <% } %>

        if (!confirm("¿Confirma guardar el borrador del período actual?")) return false;

        var hasJq = (window.jQuery && window.jQuery.ajax);

        var cacheKey = "<%= cacheKey %>";
        if (!cacheKey) {
            try {
                var el = document.getElementById(nsKey("workCacheKey"));
                if (el && el.value) cacheKey = el.value;
            } catch (e0) {}
        }
        if (!cacheKey) {
            alert("Falta workCacheKey/cacheKey. Primero ejecutá Buscar para generar el workCacheKey del período.");
            return false;
        }

        var tipoActual = (safeValById("tipoProceso") || "<%= tipoDebito %>" || "LI");
        tipoActual = ("" + tipoActual).toUpperCase();

        var modo = "";
        try { modo = jQuery("#" + nsKey("busquedaMode")).val(); } catch (e1) {}
        if (!modo) modo = "<%= busquedaMode %>";
        if (!modo) modo = "NEW";
        modo = ("" + modo).toUpperCase();
        if (modo !== "NEW" && modo !== "LEGACY") modo = "NEW";

        var btn = document.getElementById(nsKey("btnGuardarBorrador"));
        if (btn) {
            if (btn._busy) return false;
            btn._busy = true;
            btn.disabled = true;
        }

        if (!hasJq) {
            var f = document.createElement("form");
            f.method = "POST";
            f.action = "<%= guardarBorradorURL %>";

            function add(k, v) {
                var i = document.createElement("input");
                i.type = "hidden";
                i.name = k;
                i.value = v;
                f.appendChild(i);
            }

            add(nsKey("cacheKey"), cacheKey);
            add("cacheKey", cacheKey);
            add(nsKey("workCacheKey"), cacheKey);
            add("workCacheKey", cacheKey);

            add(nsKey("tipo_proceso"), tipoActual);
            add("tipo_proceso", tipoActual);
            add(nsKey("tipoProceso"), tipoActual);
            add("tipoProceso", tipoActual);
            add(nsKey("tipoDebito"), tipoActual);
            add("tipoDebito", tipoActual);

            add(nsKey("grabarDebitos"), "true");
            add("grabarDebitos", "true");
            add(nsKey("segmento"), "ALL");
            add("segmento", "ALL");

            document.body.appendChild(f);
            f.submit();
            return false;
        }

        var data = {};
        putCacheKeys(data, cacheKey);
        putTipoProceso(data, tipoActual);
        put(data, "grabarDebitos", "true");
        put(data, "segmento", "ALL");

        jQuery.ajax({
            url: "<%= guardarBorradorURL %>",
            type: "POST",
            data: data,
            cache: false,
            success: function () {
                var refreshUrl = "<%= refreshListadoURL %>";

                var desde_mes = safeValById("fechaDesdeMes");
                var desde_anio = safeValById("fechaDesdeAnio");
                var terc = safeValById("tipo_debitos_tercerizadoras");

                var params = {};
                put(params, "cmd", "search");
                putCacheKeys(params, cacheKey);
                putTipoProceso(params, tipoActual);
                put(params, "fechaDesdeMes", desde_mes);
                put(params, "fechaDesdeAnio", desde_anio);
                putAliases(params, ["tipo_debitos_tercerizadoras", "tipo_debito"], terc);

                put(params, "afterBorrador", "1");
                put(params, "busquedaMode", modo);
                put(params, "periodoCerrado", "0");
                put(params, "rnd", "" + (new Date().getTime()));

                jQuery.ajax({
                    url: refreshUrl,
                    type: "GET",
                    data: params,
                    cache: false,
                    success: function (html) {
                        jQuery("#" + nsKey("listado_debitos")).html(html);
                        try {
                            if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                                Liferay.Util.evalScripts(html);
                            }
                        } catch (e) {}
                        alert("Borrador guardado.");
                    },
                    error: function (xhr2) {
                        alert("Borrador guardado, pero falló el refresh del listado. HTTP " + xhr2.status);
                    }
                });
            },
            error: function (xhr) {
                alert("No se pudo guardar borrador. HTTP " + xhr.status);
            },
            complete: function () {
                if (btn) { btn._busy = false; btn.disabled = false; }
            }
        });

        return false;
    }

    <% } else { %>

    // ===== STAGING helpers =====

    function <portlet:namespace/>marcarAnexar(valor) {
        <% if (readOnlyUI) { %>
        alert("Operación no permitida en modo solo lectura.");
        return false;
        <% } %>

        var name = "<portlet:namespace/>idsRow";
        jQuery("#<portlet:namespace/>listado_debitos_anexar input[type=checkbox][name='" + name + "']").each(function () {
            this.checked = valor;
        });
    }

    function <portlet:namespace/>anexarUno(detalleKey) {
        <% if (readOnlyUI) { %>
        alert("Operación no permitida en modo solo lectura.");
        return false;
        <% } %>

        var name = "<portlet:namespace/>idsRow";
        var $boxes = jQuery("#<portlet:namespace/>listado_debitos_anexar input[type=checkbox][name='" + name + "']");
        $boxes.each(function () {
            if (this.value === detalleKey) this.checked = true;
        });

        if (typeof <portlet:namespace/>anexarSeleccionados === "function") {
            <portlet:namespace/>anexarSeleccionados();
        } else {
            alert("Falta function <portlet:namespace/>anexarSeleccionados() en el JSP padre.");
        }

        return false;
    }

    <% } %>

</script>

<% } %>
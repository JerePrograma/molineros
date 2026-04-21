<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
    // ============================================================
    // Defaults + params (SIN ParamUtil, como tu formato)
    // ============================================================
    String _ns = renderResponse.getNamespace();

    Calendar calNow = CalendarFactoryUtil.getCalendar();
    calNow.setTime(new Date());

    // Default visual = desde mes vigente hasta mes vigente
    Calendar calHastaDef = CalendarFactoryUtil.getCalendar();
    calHastaDef.setTime(new Date());
    calHastaDef.set(Calendar.DAY_OF_MONTH, 1);
    calHastaDef.set(Calendar.HOUR_OF_DAY, 0);
    calHastaDef.set(Calendar.MINUTE, 0);
    calHastaDef.set(Calendar.SECOND, 0);
    calHastaDef.set(Calendar.MILLISECOND, 0);

    Calendar calDesdeDef = (Calendar) calHastaDef.clone();
    calDesdeDef.add(Calendar.MONTH, 0);

    int defDesdeMes0 = calDesdeDef.get(Calendar.MONTH);
    int defDesdeAnio = calDesdeDef.get(Calendar.YEAR);

    int defHastaMes0 = calHastaDef.get(Calendar.MONTH);
    int defHastaAnio = calHastaDef.get(Calendar.YEAR);

    int defMes0 = calNow.get(Calendar.MONTH);
    int defAnio = calNow.get(Calendar.YEAR);

    // ============================================================
    // LECTURA ROBUSTA - FILTROS TRABAJADOS (ptw_*)
    // ============================================================
    String ptwDesdeMes = _p(request, _ns, "ptw_fechaDesdeMes");
    String ptwDesdeAnio = _p(request, _ns, "ptw_fechaDesdeAnio");
    String ptwHastaMes = _p(request, _ns, "ptw_fechaHastaMes");
    String ptwHastaAnio = _p(request, _ns, "ptw_fechaHastaAnio");

    boolean ptwPrimerRender =
            _blank(ptwDesdeMes) &&
                    _blank(ptwDesdeAnio) &&
                    _blank(ptwHastaMes) &&
                    _blank(ptwHastaAnio);

    if (ptwPrimerRender) {
        ptwDesdeMes = String.valueOf(defDesdeMes0);
        ptwDesdeAnio = String.valueOf(defDesdeAnio);
        ptwHastaMes = String.valueOf(defHastaMes0);
        ptwHastaAnio = String.valueOf(defHastaAnio);
    } else {
        if (_blank(ptwDesdeMes))  ptwDesdeMes  = String.valueOf(defDesdeMes0);
        if (_blank(ptwDesdeAnio)) ptwDesdeAnio = String.valueOf(defDesdeAnio);
        if (_blank(ptwHastaMes))  ptwHastaMes  = String.valueOf(defHastaMes0);
        if (_blank(ptwHastaAnio)) ptwHastaAnio = String.valueOf(defHastaAnio);
    }

    int ptwDesdeMes0 = defDesdeMes0, ptwDesdeAnioInt = defDesdeAnio, ptwHastaMes0 = defHastaMes0, ptwHastaAnioInt = defHastaAnio;
    try { ptwDesdeMes0 = Integer.parseInt(ptwDesdeMes); } catch (Exception ignore) { ptwDesdeMes0 = defDesdeMes0; }
    try { ptwDesdeAnioInt = Integer.parseInt(ptwDesdeAnio); } catch (Exception ignore) { ptwDesdeAnioInt = defDesdeAnio; }
    try { ptwHastaMes0 = Integer.parseInt(ptwHastaMes); } catch (Exception ignore) { ptwHastaMes0 = defHastaMes0; }
    try { ptwHastaAnioInt = Integer.parseInt(ptwHastaAnio); } catch (Exception ignore) { ptwHastaAnioInt = defHastaAnio; }

    String ptwTerc = _pAny(request, _ns, new String[]{"ptw_tipo_debitos_tercerizadoras","ptw_tipo_debito"});
    if (_blank(ptwTerc)) ptwTerc = "0";
    ptwTerc = ptwTerc.trim().toUpperCase();

    String ptwTipoProc = _pAny(request, _ns, new String[]{"ptw_tipoProceso","ptw_tipo_proceso","ptw_tipoDebito"});
    if (_blank(ptwTipoProc)) ptwTipoProc = "0";
    ptwTipoProc = ptwTipoProc.trim().toUpperCase();

    String ptwIncC = _p(request, _ns, "ptw_incluirCerrados");
    if (_blank(ptwIncC)) ptwIncC = "1";

    String ptwIncB = _p(request, _ns, "ptw_incluirBorradores");
    if (_blank(ptwIncB)) ptwIncB = "1";

    boolean ptwIncluirCerrados = "1".equals(ptwIncC) || "true".equalsIgnoreCase(ptwIncC);
    boolean ptwIncluirBorradores = "1".equals(ptwIncB) || "true".equalsIgnoreCase(ptwIncB);

    // ============================================================
    // LECTURA ROBUSTA - FILTROS PENDIENTES (ptp_*)
    // ============================================================
    String ptpDesdeMes = _p(request, _ns, "ptp_fechaDesdeMes");
    String ptpDesdeAnio = _p(request, _ns, "ptp_fechaDesdeAnio");
    String ptpHastaMes = _p(request, _ns, "ptp_fechaHastaMes");
    String ptpHastaAnio = _p(request, _ns, "ptp_fechaHastaAnio");

    boolean ptpPrimerRender =
            _blank(ptpDesdeMes) &&
                    _blank(ptpDesdeAnio) &&
                    _blank(ptpHastaMes) &&
                    _blank(ptpHastaAnio);

    if (ptpPrimerRender) {
        ptpDesdeMes = String.valueOf(defDesdeMes0);
        ptpDesdeAnio = String.valueOf(defDesdeAnio);
        ptpHastaMes = String.valueOf(defHastaMes0);
        ptpHastaAnio = String.valueOf(defHastaAnio);
    } else {
        if (_blank(ptpDesdeMes))  ptpDesdeMes  = String.valueOf(defDesdeMes0);
        if (_blank(ptpDesdeAnio)) ptpDesdeAnio = String.valueOf(defDesdeAnio);
        if (_blank(ptpHastaMes))  ptpHastaMes  = String.valueOf(defHastaMes0);
        if (_blank(ptpHastaAnio)) ptpHastaAnio = String.valueOf(defHastaAnio);
    }

    int ptpDesdeMes0 = defDesdeMes0, ptpDesdeAnioInt = defDesdeAnio, ptpHastaMes0 = defHastaMes0, ptpHastaAnioInt = defHastaAnio;
    try { ptpDesdeMes0 = Integer.parseInt(ptpDesdeMes); } catch (Exception ignore) { ptpDesdeMes0 = defDesdeMes0; }
    try { ptpDesdeAnioInt = Integer.parseInt(ptpDesdeAnio); } catch (Exception ignore) { ptpDesdeAnioInt = defDesdeAnio; }
    try { ptpHastaMes0 = Integer.parseInt(ptpHastaMes); } catch (Exception ignore) { ptpHastaMes0 = defHastaMes0; }
    try { ptpHastaAnioInt = Integer.parseInt(ptpHastaAnio); } catch (Exception ignore) { ptpHastaAnioInt = defHastaAnio; }

    String ptpTerc = _pAny(request, _ns, new String[]{"ptp_tipo_debitos_tercerizadoras","ptp_tipo_debito"});
    if (_blank(ptpTerc)) ptpTerc = "0";
    ptpTerc = ptpTerc.trim().toUpperCase();

    String ptpTipoProc = _pAny(request, _ns, new String[]{"ptp_tipoProceso","ptp_tipo_proceso","ptp_tipoDebito"});
    if (_blank(ptpTipoProc)) ptpTipoProc = "0";
    ptpTipoProc = ptpTipoProc.trim().toUpperCase();

    // tipos (config)
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

    // resultados
    List cerrados = (List) request.getAttribute("PERIODOS_TRAB_CERRADOS");
    if (cerrados == null) cerrados = new ArrayList();

    List borradores = (List) request.getAttribute("PERIODOS_TRAB_BORRADORES");
    if (borradores == null) borradores = new ArrayList();

    List pendientes = (List) request.getAttribute("PERIODOS_TRAB_PENDIENTES");
    if (pendientes == null) pendientes = new ArrayList();

    String err = (String) request.getAttribute("PERIODOS_TRAB_ERROR");
    if (err == null) err = "";

    String errPend = (String) request.getAttribute("PERIODOS_PEND_ERROR");
    if (errPend == null) errPend = "";

    // ============================================================
    // Orden:
    // - Cerrados / Borradores: última modificación DESC
    // - Pendientes: período DESC
    // ============================================================
    java.util.Collections.sort(cerrados, new java.util.Comparator() {
        public int compare(Object o1, Object o2) {
            java.util.Date d1 = _toDate(_val(o1,
                    new String[]{"ultima_modificacion","ultimaModificacion"},
                    new String[]{"getUltimaModificacion","getUltima_modificacion"}));

            java.util.Date d2 = _toDate(_val(o2,
                    new String[]{"ultima_modificacion","ultimaModificacion"},
                    new String[]{"getUltimaModificacion","getUltima_modificacion"}));

            long t1 = (d1 != null) ? d1.getTime() : Long.MIN_VALUE;
            long t2 = (d2 != null) ? d2.getTime() : Long.MIN_VALUE;

            if (t2 > t1) return 1;
            if (t2 < t1) return -1;
            return 0;
        }
    });

    java.util.Collections.sort(borradores, new java.util.Comparator() {
        public int compare(Object o1, Object o2) {
            java.util.Date d1 = _toDate(_val(o1,
                    new String[]{"ultima_modificacion","ultimaModificacion"},
                    new String[]{"getUltimaModificacion","getUltima_modificacion"}));

            java.util.Date d2 = _toDate(_val(o2,
                    new String[]{"ultima_modificacion","ultimaModificacion"},
                    new String[]{"getUltimaModificacion","getUltima_modificacion"}));

            long t1 = (d1 != null) ? d1.getTime() : Long.MIN_VALUE;
            long t2 = (d2 != null) ? d2.getTime() : Long.MIN_VALUE;

            if (t2 > t1) return 1;
            if (t2 < t1) return -1;
            return 0;
        }
    });

    java.util.Collections.sort(pendientes, new java.util.Comparator() {
        public int compare(Object o1, Object o2) {
            java.util.Date d1 = _toDate(_val(o1,
                    new String[]{"periodo_mes","periodoMes"},
                    new String[]{"getPeriodoMes","getPeriodo_mes"}));

            java.util.Date d2 = _toDate(_val(o2,
                    new String[]{"periodo_mes","periodoMes"},
                    new String[]{"getPeriodoMes","getPeriodo_mes"}));

            long t1 = (d1 != null) ? d1.getTime() : Long.MIN_VALUE;
            long t2 = (d2 != null) ? d2.getTime() : Long.MIN_VALUE;

            if (t2 > t1) return 1;
            if (t2 < t1) return -1;
            return 0;
        }
    });
%>

<portlet:renderURL var="renderPeriodosURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/liquidaciones/debitos_tercerizadoras"/>
</portlet:renderURL>

<%
    String renderPeriodosURLStr = (String) pageContext.getAttribute("renderPeriodosURL");
    if (renderPeriodosURLStr == null) renderPeriodosURLStr = "";
%>

<%!
    // =========================
    // Param helpers (first non-blank)
    // =========================
    private boolean _blank(String s) {
        return s == null || s.trim().length() == 0;
    }
    private String _p(javax.servlet.http.HttpServletRequest req, String ns, String key) {
        String v1 = req.getParameter(key);
        String v2 = req.getParameter(ns + key);
        if (!_blank(v1)) return v1;
        if (!_blank(v2)) return v2;
        return null;
    }
    private String _pAny(javax.servlet.http.HttpServletRequest req, String ns, String[] keys) {
        if (keys == null) return null;
        for (int i=0; i<keys.length; i++) {
            String v = _p(req, ns, keys[i]);
            if (!_blank(v)) return v;
        }
        return null;
    }

    // =========================
    // Result mapping helpers
    // =========================
    private Object _mapGet(Object o, String key) {
        try {
            if (o instanceof java.util.Map) return ((java.util.Map) o).get(key);
        } catch (Exception ignore) {}
        return null;
    }
    private Object _invoke0(Object o, String m) {
        try {
            java.lang.reflect.Method mm = o.getClass().getMethod(m, new Class[0]);
            return mm.invoke(o, new Object[0]);
        } catch (Exception ignore) {}
        return null;
    }
    private Object _val(Object o, String[] keys, String[] getters) {
        if (o == null) return null;
        if (keys != null) {
            for (int i=0;i<keys.length;i++) {
                Object v = _mapGet(o, keys[i]);
                if (v != null) return v;
            }
        }
        if (getters != null) {
            for (int i=0;i<getters.length;i++) {
                Object v = _invoke0(o, getters[i]);
                if (v != null) return v;
            }
        }
        return null;
    }
    private String _s(Object v) {
        if (v == null) return "";
        String t = String.valueOf(v);
        return (t != null) ? t : "";
    }
    private java.util.Date _toDate(Object v) {
        if (v == null) return null;
        if (v instanceof java.util.Date) return (java.util.Date) v;
        if (v instanceof java.sql.Timestamp) return new java.util.Date(((java.sql.Timestamp)v).getTime());
        if (v instanceof java.sql.Date) return new java.util.Date(((java.sql.Date)v).getTime());
        return null;
    }
    private String _tipoProcesoNombre(String tipoProceso) {
        String t = (tipoProceso != null) ? tipoProceso.trim().toUpperCase() : "";
        if ("HO".equals(t)) return "HOSPITALES";
        if ("LI".equals(t)) return "LIQUIDACIONES_PENDIENTES";
        if ("RE".equals(t)) return "REINTEGROS";
        if ("PR".equals(t)) return "PRESTADORES";
        return "TOTAL";
    }
%>

<div id="<portlet:namespace/>ptwRoot">

    <fieldset class="block-labels">
        <legend>Períodos trabajados y pendientes</legend>

        <% if (err != null && err.trim().length() > 0) { %>
        <div style="margin:8px 0; padding:6px; border:1px solid #999; background:#f4f4f4; font-weight:bold;">
            <%= err %>
        </div>
        <% } %>

        <% if (errPend != null && errPend.trim().length() > 0) { %>
        <div style="margin:8px 0; padding:6px; border:1px solid #999; background:#f4f4f4; font-weight:bold;">
            <%= errPend %>
        </div>
        <% } %>

        <!-- =========================================================
             FILTROS INDEPENDIENTES - TRABAJADOS
             ========================================================= -->
        <h3 style="margin: 6px 0;">Filtros de trabajados</h3>

        <table class="lfr-table">
            <tr>
                <td><label>Desde:</label></td>
                <td>
                    <liferay-ui:input-date
                            dayParam="ptw_fechaDesdeDia"
                            dayValue="1"
                            monthParam="ptw_fechaDesdeMes"
                            monthValue="<%= ptwDesdeMes0 %>"
                            yearParam="ptw_fechaDesdeAnio"
                            yearValue="<%= ptwDesdeAnioInt %>"
                            yearRangeStart="<%= defAnio - 25 %>"
                            yearRangeEnd="<%= defAnio %>"
                            firstDayOfWeek="<%= calNow.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </td>

                <td><label>Hasta:</label></td>
                <td>
                    <liferay-ui:input-date
                            dayParam="ptw_fechaHastaDia"
                            dayValue="1"
                            monthParam="ptw_fechaHastaMes"
                            monthValue="<%= ptwHastaMes0 %>"
                            yearParam="ptw_fechaHastaAnio"
                            yearValue="<%= ptwHastaAnioInt %>"
                            yearRangeStart="<%= defAnio - 25 %>"
                            yearRangeEnd="<%= defAnio %>"
                            firstDayOfWeek="<%= calNow.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </td>

                <td><label><liferay-ui:message key="cobertura-expediente-tercerizadora"/>:</label></td>
                <td>
                    <select name="<portlet:namespace/>ptw_tipo_debitos_tercerizadoras" id="<portlet:namespace/>ptw_tipo_debitos_tercerizadoras">
                        <option value="0" <%= "0".equals(ptwTerc) ? "selected" : "" %>>Todas</option>
                        <option value="OMI" <%= "OMI".equals(ptwTerc) ? "selected" : "" %>>OMINT</option>
                        <option value="MPS" <%= "MPS".equals(ptwTerc) ? "selected" : "" %>>MOLINEROS POR PS</option>
                        <option value="MEN" <%= "MEN".equals(ptwTerc) ? "selected" : "" %>>MOLINEROS POR ENSALUD</option>
                        <option value="CEM" <%= "CEM".equals(ptwTerc) ? "selected" : "" %>>CEMIC</option>
                        <option value="MIM" <%= "MIM".equals(ptwTerc) ? "selected" : "" %>>IMESA</option>
                        <option value="MON" <%= "MON".equals(ptwTerc) ? "selected" : "" %>>MONOTRIBUTO</option>
                    </select>
                </td>

                <td>Proceso:</td>
                <td>
                    <select name="<portlet:namespace/>ptw_tipoProceso" id="<portlet:namespace/>ptw_tipoProceso">
                        <option value="0" <%= "0".equals(ptwTipoProc) ? "selected" : "" %>>Todos</option>
                        <% for (ClaseBase c : tipos) { %>
                        <%
                            String cod = (c != null && c.getId() != null) ? c.getId().trim().toUpperCase() : "";
                            String sel = cod.equals(ptwTipoProc) ? "selected" : "";
                        %>
                        <option value="<%= c.getId() %>" <%= sel %>><%= c.getDescripcion() %></option>
                        <% } %>
                    </select>
                </td>
            </tr>

            <tr>
                <td colspan="10">
                    <label>
                        <input type="checkbox"
                               id="<portlet:namespace/>ptw_incluirCerrados"
                               name="<portlet:namespace/>ptw_incluirCerrados"
                               value="1" <%= ptwIncluirCerrados ? "checked" : "" %> />
                        Incluir cerrados
                    </label>
                    &nbsp;&nbsp;
                    <label>
                        <input type="checkbox"
                               id="<portlet:namespace/>ptw_incluirBorradores"
                               name="<portlet:namespace/>ptw_incluirBorradores"
                               value="1" <%= ptwIncluirBorradores ? "checked" : "" %> />
                        Incluir borradores
                    </label>

                    &nbsp;&nbsp;

                    <input id="<portlet:namespace/>ptw_buscarPeriodos"
                           value="<liferay-ui:message key="buscar"/>"
                           title="<liferay-ui:message key="buscar" />"
                           type="button"
                           onClick="javascript:<portlet:namespace/>buscarPeriodosTrabajados();"/>
                </td>
            </tr>
        </table>

        <hr/>

        <!-- =========================================================
             RESULTADOS - TRABAJADOS
             ========================================================= -->
        <table width="100%">
            <tr>
                <td width="50%" valign="top" style="padding-right:8px;">
                    <h3 style="margin: 6px 0;">Cerrados</h3>

                    <table class="lfr-table" width="100%">
                        <tr>
                            <th>Terc.</th>
                            <th>Proceso</th>
                            <th>Período</th>
                            <th>Cantidad</th>
                            <th>Usuario</th>
                            <th>Últ. modif.</th>
                        </tr>

                        <%
                            java.text.SimpleDateFormat sdfTs = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                            if (cerrados == null || cerrados.size() == 0) {
                        %>
                        <tr>
                            <td colspan="6">Sin resultados.</td>
                        </tr>
                        <%
                        } else {
                            for (int i=0;i<cerrados.size();i++) {
                                Object it = cerrados.get(i);

                                String idT = _s(_val(it,
                                        new String[]{"id_tercerizadora","idTercerizadora"},
                                        new String[]{"getIdTercerizadora","getId_tercerizadora"})).toUpperCase();

                                String tp = _s(_val(it,
                                        new String[]{"tipo_proceso","tipoProceso"},
                                        new String[]{"getTipoProceso","getTipo_proceso"})).toUpperCase();

                                String per = _s(_val(it,
                                        new String[]{"periodo_label","periodoLabel"},
                                        new String[]{"getPeriodoLabel","getPeriodo_label"}));

                                String cant = _s(_val(it,
                                        new String[]{"cantidad_registros","cantidadRegistros"},
                                        new String[]{"getCantidadRegistros","getCantidad_registros"}));

                                String usr = _s(_val(it,
                                        new String[]{"usuario"},
                                        new String[]{"getUsuario"}));

                                java.util.Date ts = _toDate(_val(it,
                                        new String[]{"ultima_modificacion","ultimaModificacion"},
                                        new String[]{"getUltimaModificacion","getUltima_modificacion"}));

                                String tsStr = (ts != null) ? sdfTs.format(ts) : "";
                        %>
                        <tr>
                            <td><%= idT %></td>
                            <td><%= _tipoProcesoNombre(tp) %></td>
                            <td><%= per %></td>
                            <td><%= cant %></td>
                            <td><%= usr %></td>
                            <td><%= tsStr %></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                    </table>

                    <%
                        int cantMostrados = (cerrados != null) ? cerrados.size() : 0;
                        String lbl = (cantMostrados == 1) ? "elemento" : "elementos";
                    %>
                    <div style="margin: 6px 0; font-weight: bold;">
                        Cantidad de <%= lbl %> mostrados: <%= cantMostrados %>
                    </div>
                </td>

                <td width="50%" valign="top" style="padding-left:8px;">
                    <h3 style="margin: 6px 0;">Borradores</h3>

                    <table class="lfr-table" width="100%">
                        <tr>
                            <th>Terc.</th>
                            <th>Proceso</th>
                            <th>Período</th>
                            <th>Cantidad</th>
                            <th>Usuario</th>
                            <th>Últ. modif.</th>
                        </tr>

                        <%
                            java.text.SimpleDateFormat sdfTs2 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                            if (borradores == null || borradores.size() == 0) {
                        %>
                        <tr>
                            <td colspan="6">Sin resultados.</td>
                        </tr>
                        <%
                        } else {
                            for (int i=0;i<borradores.size();i++) {
                                Object it = borradores.get(i);

                                String idT = _s(_val(it,
                                        new String[]{"id_tercerizadora","idTercerizadora"},
                                        new String[]{"getIdTercerizadora","getId_tercerizadora"})).toUpperCase();

                                String tp = _s(_val(it,
                                        new String[]{"tipo_proceso","tipoProceso"},
                                        new String[]{"getTipoProceso","getTipo_proceso"})).toUpperCase();

                                String per = _s(_val(it,
                                        new String[]{"periodo_label","periodoLabel"},
                                        new String[]{"getPeriodoLabel","getPeriodo_label"}));

                                String cant = _s(_val(it,
                                        new String[]{"cantidad_registros","cantidadRegistros"},
                                        new String[]{"getCantidadRegistros","getCantidad_registros"}));

                                String usr = _s(_val(it,
                                        new String[]{"usuario"},
                                        new String[]{"getUsuario"}));

                                java.util.Date ts = _toDate(_val(it,
                                        new String[]{"ultima_modificacion","ultimaModificacion"},
                                        new String[]{"getUltimaModificacion","getUltima_modificacion"}));

                                String tsStr = (ts != null) ? sdfTs2.format(ts) : "";
                        %>
                        <tr>
                            <td><%= idT %></td>
                            <td><%= _tipoProcesoNombre(tp) %></td>
                            <td><%= per %></td>
                            <td><%= cant %></td>
                            <td><%= usr %></td>
                            <td><%= tsStr %></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                    </table>

                    <%
                        int cantMostradosB = (borradores != null) ? borradores.size() : 0;
                        String lblB = (cantMostradosB == 1) ? "elemento" : "elementos";
                    %>
                    <div style="margin: 6px 0; font-weight: bold;">
                        Cantidad de <%= lblB %> mostrados: <%= cantMostradosB %>
                    </div>
                </td>
            </tr>
        </table>

        <hr/>

        <!-- =========================================================
             FILTROS INDEPENDIENTES - PENDIENTES
             ========================================================= -->
        <h3 style="margin: 6px 0;">Filtros de pendientes</h3>

        <table class="lfr-table">
            <tr>
                <td><label>Desde:</label></td>
                <td>
                    <liferay-ui:input-date
                            dayParam="ptp_fechaDesdeDia"
                            dayValue="1"
                            monthParam="ptp_fechaDesdeMes"
                            monthValue="<%= ptpDesdeMes0 %>"
                            yearParam="ptp_fechaDesdeAnio"
                            yearValue="<%= ptpDesdeAnioInt %>"
                            yearRangeStart="<%= defAnio - 25 %>"
                            yearRangeEnd="<%= defAnio %>"
                            firstDayOfWeek="<%= calNow.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </td>

                <td><label>Hasta:</label></td>
                <td>
                    <liferay-ui:input-date
                            dayParam="ptp_fechaHastaDia"
                            dayValue="1"
                            monthParam="ptp_fechaHastaMes"
                            monthValue="<%= ptpHastaMes0 %>"
                            yearParam="ptp_fechaHastaAnio"
                            yearValue="<%= ptpHastaAnioInt %>"
                            yearRangeStart="<%= defAnio - 25 %>"
                            yearRangeEnd="<%= defAnio %>"
                            firstDayOfWeek="<%= calNow.getFirstDayOfWeek() - 1 %>"
                            disabled="<%= false %>"
                    />
                </td>

                <td><label><liferay-ui:message key="cobertura-expediente-tercerizadora"/>:</label></td>
                <td>
                    <select name="<portlet:namespace/>ptp_tipo_debitos_tercerizadoras" id="<portlet:namespace/>ptp_tipo_debitos_tercerizadoras">
                        <option value="0" <%= "0".equals(ptpTerc) ? "selected" : "" %>>Todas</option>
                        <option value="OMI" <%= "OMI".equals(ptpTerc) ? "selected" : "" %>>OMINT</option>
                        <option value="MPS" <%= "MPS".equals(ptpTerc) ? "selected" : "" %>>MOLINEROS POR PS</option>
                        <option value="MEN" <%= "MEN".equals(ptpTerc) ? "selected" : "" %>>MOLINEROS POR ENSALUD</option>
                        <option value="CEM" <%= "CEM".equals(ptpTerc) ? "selected" : "" %>>CEMIC</option>
                        <option value="MIM" <%= "MIM".equals(ptpTerc) ? "selected" : "" %>>IMESA</option>
                        <option value="MON" <%= "MON".equals(ptpTerc) ? "selected" : "" %>>MONOTRIBUTO</option>
                    </select>
                </td>

                <td>Proceso:</td>
                <td>
                    <select name="<portlet:namespace/>ptp_tipoProceso" id="<portlet:namespace/>ptp_tipoProceso">
                        <option value="0" <%= "0".equals(ptpTipoProc) ? "selected" : "" %>>Todos</option>
                        <% for (ClaseBase c : tipos) { %>
                        <%
                            String cod = (c != null && c.getId() != null) ? c.getId().trim().toUpperCase() : "";
                            String sel = cod.equals(ptpTipoProc) ? "selected" : "";
                        %>
                        <option value="<%= c.getId() %>" <%= sel %>><%= c.getDescripcion() %></option>
                        <% } %>
                    </select>
                </td>
            </tr>

            <tr>
                <td colspan="10">
                    <input id="<portlet:namespace/>ptp_buscarPendientes"
                           value="<liferay-ui:message key="buscar"/>"
                           title="<liferay-ui:message key="buscar" />"
                           type="button"
                           onClick="javascript:<portlet:namespace/>buscarPeriodosPendientes();"/>
                </td>
            </tr>
        </table>

        <br/>

        <!-- =========================================================
             RESULTADOS - PENDIENTES
             ========================================================= -->
        <h3 style="margin: 6px 0;">Pendientes</h3>

        <table class="lfr-table" width="100%">
            <tr>
                <th>Terc.</th>
                <th>Proceso</th>
                <th>Período</th>
                <th>Cantidad</th>
                <th>Usuario</th>
                <th>Últ. modif.</th>
            </tr>

            <%
                java.text.SimpleDateFormat sdfTs3 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                if (pendientes == null || pendientes.size() == 0) {
            %>
            <tr>
                <td colspan="6">Sin resultados.</td>
            </tr>
            <%
            } else {
                for (int i=0;i<pendientes.size();i++) {
                    Object it = pendientes.get(i);

                    String idT = _s(_val(it,
                            new String[]{"id_tercerizadora","idTercerizadora"},
                            new String[]{"getIdTercerizadora","getId_tercerizadora"})).toUpperCase();

                    String tp = _s(_val(it,
                            new String[]{"tipo_proceso","tipoProceso"},
                            new String[]{"getTipoProceso","getTipo_proceso"})).toUpperCase();

                    String per = _s(_val(it,
                            new String[]{"periodo_label","periodoLabel"},
                            new String[]{"getPeriodoLabel","getPeriodo_label"}));

                    String cant = _s(_val(it,
                            new String[]{"cantidad_registros","cantidadRegistros"},
                            new String[]{"getCantidadRegistros","getCantidad_registros"}));

                    String usr = _s(_val(it,
                            new String[]{"usuario"},
                            new String[]{"getUsuario"}));
                    if (_blank(usr)) usr = "-";

                    java.util.Date ts = _toDate(_val(it,
                            new String[]{"ultima_modificacion","ultimaModificacion"},
                            new String[]{"getUltimaModificacion","getUltima_modificacion"}));

                    String tsStr = (ts != null) ? sdfTs3.format(ts) : "-";
            %>
            <tr>
                <td><%= idT %></td>
                <td><%= _tipoProcesoNombre(tp) %></td>
                <td><%= per %></td>
                <td><%= cant %></td>
                <td><%= usr %></td>
                <td><%= tsStr %></td>
            </tr>
            <%
                    }
                }
            %>
        </table>

        <%
            int cantMostradosP = (pendientes != null) ? pendientes.size() : 0;
            String lblP = (cantMostradosP == 1) ? "elemento" : "elementos";
        %>
        <div style="margin: 6px 0; font-weight: bold;">
            Cantidad de <%= lblP %> mostrados: <%= cantMostradosP %>
        </div>
    </fieldset>

    <script type="text/javascript">
        (function (w, $) {

            var PTW_NS = "<portlet:namespace/>";
            function ptwNsKey(k) { return PTW_NS + k; }
            function ptwFixUrl(u) { return (u || "").replace(/&amp;/g, "&"); }

            function ptwRoot() {
                try {
                    if (typeof popupE !== "undefined" && popupE) return $(popupE);
                } catch (e) {}
                var $r = $("#" + ptwNsKey("ptwRoot"));
                return $r.length ? $r : $(document);
            }

            function ptwValByName(paramNameNoNs) {
                return ptwRoot().find("[name='" + ptwNsKey(paramNameNoNs) + "']").val();
            }

            function ptwValById(idNoNs) {
                var $root = ptwRoot();

                var $e = $root.find("#" + ptwNsKey(idNoNs));
                if ($e.length) return $e.val();

                $e = $root.find("#" + idNoNs);
                if ($e.length) return $e.val();

                $e = $root.find("[name='" + ptwNsKey(idNoNs) + "']");
                if ($e.length) return $e.val();

                return "";
            }

            function ptwBuildUrl(base, params) {
                var qs = $.param(params || {});
                if (!qs) return base;
                return base + (base.indexOf("?") >= 0 ? "&" : "?") + qs;
            }

            function ptwApplyYearSelectUX(yearIdNoNs) {
                var $root = ptwRoot();

                var $y = $root.find("#" + ptwNsKey(yearIdNoNs));
                if (!$y.length) {
                    $y = $root.find("[name='" + ptwNsKey(yearIdNoNs) + "']");
                }
                if (!$y.length) return;

                var current = $y.val();
                var opts = $y.find("option").get();

                opts.sort(function (a, b) {
                    var va = parseInt((a.value || "").trim(), 10);
                    var vb = parseInt((b.value || "").trim(), 10);

                    if (isNaN(va) || isNaN(vb)) {
                        return ("" + (b.value || "")).localeCompare("" + (a.value || ""));
                    }
                    return vb - va;
                });

                $y.empty().append(opts);

                if (current != null && current !== "") {
                    $y.val(current);
                }

                $y.prop("size", 10);
                $y.css("overflow-y", "auto");
            }

            var PERIODOS_URL = ptwFixUrl("<%= renderPeriodosURLStr %>");

            function put(p, k, v) { p[k] = v; p[ptwNsKey(k)] = v; return p; }
            function putAliases(p, keys, v) { for (var i=0;i<keys.length;i++) put(p, keys[i], v); return p; }

            function putTrabajadosParams(params) {
                put(params, "ptw_fechaDesdeDia", "01");
                put(params, "ptw_fechaDesdeMes", ptwValByName("ptw_fechaDesdeMes"));
                put(params, "ptw_fechaDesdeAnio", ptwValByName("ptw_fechaDesdeAnio"));

                put(params, "ptw_fechaHastaDia", "01");
                put(params, "ptw_fechaHastaMes", ptwValByName("ptw_fechaHastaMes"));
                put(params, "ptw_fechaHastaAnio", ptwValByName("ptw_fechaHastaAnio"));

                var terc = ptwValById("ptw_tipo_debitos_tercerizadoras");
                putAliases(params, ["ptw_tipo_debitos_tercerizadoras","ptw_tipo_debito"], terc);

                var tp = ptwValById("ptw_tipoProceso");
                putAliases(params, ["ptw_tipoProceso","ptw_tipo_proceso","ptw_tipoDebito"], tp);

                var incC = ptwRoot().find("#" + ptwNsKey("ptw_incluirCerrados")).is(":checked") ? "1" : "0";
                var incB = ptwRoot().find("#" + ptwNsKey("ptw_incluirBorradores")).is(":checked") ? "1" : "0";
                put(params, "ptw_incluirCerrados", incC);
                put(params, "ptw_incluirBorradores", incB);
            }

            function putPendientesParams(params) {
                put(params, "ptp_fechaDesdeDia", "01");
                put(params, "ptp_fechaDesdeMes", ptwValByName("ptp_fechaDesdeMes"));
                put(params, "ptp_fechaDesdeAnio", ptwValByName("ptp_fechaDesdeAnio"));

                put(params, "ptp_fechaHastaDia", "01");
                put(params, "ptp_fechaHastaMes", ptwValByName("ptp_fechaHastaMes"));
                put(params, "ptp_fechaHastaAnio", ptwValByName("ptp_fechaHastaAnio"));

                var terc = ptwValById("ptp_tipo_debitos_tercerizadoras");
                putAliases(params, ["ptp_tipo_debitos_tercerizadoras","ptp_tipo_debito"], terc);

                var tp = ptwValById("ptp_tipoProceso");
                putAliases(params, ["ptp_tipoProceso","ptp_tipo_proceso","ptp_tipoDebito"], tp);
            }

            w["<portlet:namespace/>buscarPeriodosTrabajados"] = function () {

                var params = {};
                put(params, "cmd", "periodosTrabajados");

                putTrabajadosParams(params);
                putPendientesParams(params);

                put(params, "rnd", "" + (new Date().getTime()));

                var fullUrl = ptwBuildUrl(PERIODOS_URL, params);

                try {
                    if (typeof popupE !== "undefined" && popupE) {
                        $(popupE).load(fullUrl);
                        return false;
                    }
                } catch (e) {}

                w.location.href = fullUrl;
                return false;
            };

            w["<portlet:namespace/>buscarPeriodosPendientes"] = function () {

                var params = {};
                put(params, "cmd", "periodosPendientes");

                putTrabajadosParams(params);
                putPendientesParams(params);

                put(params, "rnd", "" + (new Date().getTime()));

                var fullUrl = ptwBuildUrl(PERIODOS_URL, params);

                try {
                    if (typeof popupE !== "undefined" && popupE) {
                        $(popupE).load(fullUrl);
                        return false;
                    }
                } catch (e) {}

                w.location.href = fullUrl;
                return false;
            };

            try { ptwRoot().find("#" + ptwNsKey("ptw_fechaDesdeDia")).hide(); } catch (e0) {}
            try { ptwRoot().find("#" + ptwNsKey("ptw_fechaHastaDia")).hide(); } catch (e1) {}
            try { ptwRoot().find("#" + ptwNsKey("ptp_fechaDesdeDia")).hide(); } catch (e2) {}
            try { ptwRoot().find("#" + ptwNsKey("ptp_fechaHastaDia")).hide(); } catch (e3) {}

            try { ptwApplyYearSelectUX("ptw_fechaDesdeAnio"); } catch (e4) {}
            try { ptwApplyYearSelectUX("ptw_fechaHastaAnio"); } catch (e5) {}
            try { ptwApplyYearSelectUX("ptp_fechaDesdeAnio"); } catch (e6) {}
            try { ptwApplyYearSelectUX("ptp_fechaHastaAnio"); } catch (e7) {}

        })(window, jQuery);
    </script>
</div>
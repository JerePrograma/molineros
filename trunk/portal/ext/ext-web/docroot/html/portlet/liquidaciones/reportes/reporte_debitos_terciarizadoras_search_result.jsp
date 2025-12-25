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

    // cacheKey (Java/JSP) para poder imprimirlo en HTML (hidden / staging)
    String cacheKey = (String) request.getAttribute("DEBITOS_CACHE_KEY");
    if (Validator.isNull(cacheKey)) {
        cacheKey = ParamUtil.getString(renderRequest, "cacheKey", "");
    }
    if (Validator.isNull(cacheKey)) {
        cacheKey = "";
    }

    // ===== detectar modo (main vs staging) =====
    String cmd = ParamUtil.getString(renderRequest, "cmd");
    if (Validator.isNull(cmd)) {
        cmd = ParamUtil.getString(renderRequest, renderResponse.getNamespace() + "cmd");
    }
    String mode = ParamUtil.getString(renderRequest, "mode"); // permite forzar por jsp:param
    boolean appendMode =
            "append".equalsIgnoreCase(mode)
                    || "appendSearch".equalsIgnoreCase(cmd)
                    || (request.getAttribute("DEBITOS_DETALLE_ANEXAR_RESULTADOS") != null);

    // Detalle (main vs staging)
    List detalle = appendMode
            ? (List) request.getAttribute("DEBITOS_DETALLE_ANEXAR_RESULTADOS")
            : (List) request.getAttribute("DEBITOS_DETALLE_RESULTADOS");

    if (detalle == null) detalle = new ArrayList();

    // -------- DETALLE (dinamico por tipo) --------
    List headerNamesDetalle = new ArrayList();

    // checkbox de seleccion (al principio)
    headerNamesDetalle.add("Sel.");

    if ("LI".equals(tipoDebito)) {
        headerNamesDetalle.add("Numero");
        headerNamesDetalle.add("Hospital/Autogestion");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Cargo Prestadora");
        headerNamesDetalle.add("Cargo Prestadora Reclamo");
    } else if ("HO".equals(tipoDebito)) {
        headerNamesDetalle.add("Numero");
        headerNamesDetalle.add("Hospital");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Orden Pago");
        headerNamesDetalle.add("Cargo Prestadora");
        headerNamesDetalle.add("Importe Total");
        headerNamesDetalle.add("Id Liquidacion");
    } else if ("RE".equals(tipoDebito)) {
        headerNamesDetalle.add("Documento");
        headerNamesDetalle.add("Apellido");
        headerNamesDetalle.add("Nombre");
        headerNamesDetalle.add("Seccional");
        headerNamesDetalle.add("Descripcion");
        headerNamesDetalle.add("Num Reintegro");
        headerNamesDetalle.add("Importe Total");
        headerNamesDetalle.add("Numero OP");
        headerNamesDetalle.add("Fecha OP");
        headerNamesDetalle.add("Cargo Prestadora");
        headerNamesDetalle.add("Reclamo Prestacional");
    } else if ("PR".equals(tipoDebito)) {
        headerNamesDetalle.add("Numero");
        headerNamesDetalle.add("Id Liquidacion");
        headerNamesDetalle.add("Prestador");
        headerNamesDetalle.add("Factura");
        headerNamesDetalle.add("Monto");
        headerNamesDetalle.add("Orden Pago");
        headerNamesDetalle.add("Cargo Prestadora");
        headerNamesDetalle.add("Reclamo Prestacional");
        headerNamesDetalle.add("Reclamos Prestacionales");
    } else {
        headerNamesDetalle.add("Detalle");
    }

    // columna de accion
    headerNamesDetalle.add(appendMode ? "Anexar" : "Eliminar");

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

    for (int i = 0; i < detalle.size(); i++) {
        Object it = detalle.get(i);
        ResultRow row = new ResultRow(it, new Integer(1 + i), i);

        // Key actual (indice)
        String detalleKey = tipoDebito + ":" + i;

        // checkbox
        String chkName = appendMode ? (renderResponse.getNamespace() + "idsRow") : "debitos";

        StringBuilder sbChk = new StringBuilder();
        sbChk.append("<input type=\"checkbox\" name=\"");
        sbChk.append(chkName);
        sbChk.append("\" value=\"");
        sbChk.append(detalleKey);
        sbChk.append("\" />");
        row.addText(sbChk.toString());

        if ("LI".equals(tipoDebito) && it instanceof DebitosLiquidacionesPendientes) {
            DebitosLiquidacionesPendientes d = (DebitosLiquidacionesPendientes) it;

            row.addText(d.getNumero() != null ? String.valueOf(d.getNumero()) : "");
            row.addText(d.getHospitalesAutogestion() != null ? d.getHospitalesAutogestion() : "");
            row.addText(d.getFactura() != null ? d.getFactura() : "");
            row.addText(d.getMonto() != null ? format2D.format(d.getMonto()) : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getCargoPrestadoraReclamo() != null ? format2D.format(d.getCargoPrestadoraReclamo()) : "");

        } else if ("HO".equals(tipoDebito) && it instanceof DebitosHospitales) {
            DebitosHospitales d = (DebitosHospitales) it;

            row.addText(d.getNumero() != null ? String.valueOf(d.getNumero()) : "");
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

            row.addText(d.getNumero() != null ? String.valueOf(d.getNumero()) : "");
            row.addText(String.valueOf(d.getIdLiquidacion()));
            row.addText(d.getPrestador() != null ? d.getPrestador() : "");
            row.addText(d.getFactura() != null ? d.getFactura() : "");
            row.addText(d.getMonto() != null ? format2D.format(d.getMonto()) : "");
            row.addText(d.getOrdenPago() != null ? d.getOrdenPago() : "");
            row.addText(d.getCargoPrestadora() != null ? format2D.format(d.getCargoPrestadora()) : "");
            row.addText(d.getReclamoPrestacional() != null ? String.valueOf(d.getReclamoPrestacional()) : "");
            row.addText(d.getReclamosPrestacionales() != null ? d.getReclamosPrestacionales() : "");

        } else {
            row.addText(String.valueOf(it));
        }

        if (!appendMode) {
            // Accion eliminar individual (MAIN)
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
            // Accion anexar individual (STAGING) -> marca y llama al anexar del padre
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

        rowsDet.add(row);
    }
%>

<hr/>

<div>
    <h3 style="margin: 10px 0;"><%= appendMode ? "Agregar" : "Detalle" %> (<%= tipoDebito %>)</h3>

    <div style="margin: 6px 0;">
        <% if (!appendMode) { %>
        <input type="button" value="Marcar Todos" onclick="javascript:marcarDebitos(true);"/>
        &nbsp;
        <input type="button" value="Desmarcar Todos" onclick="javascript:marcarDebitos(false);"/>
        &nbsp;
        <input type="button" value="Eliminar Seleccionados" onclick="javascript:eliminarDebitosSeleccionados();"/>
        <% } else { %>
        <input type="button" value="Marcar Todos" onclick="javascript:<portlet:namespace/>marcarAnexar(true);"/>
        &nbsp;
        <input type="button" value="Desmarcar Todos" onclick="javascript:<portlet:namespace/>marcarAnexar(false);"/>
        &nbsp;
        <input type="button" value="Anexar Seleccionados" onclick="javascript:<portlet:namespace/>anexarSeleccionados();"/>

        <input type="button" value="Guardar borrador" onclick="javascript:guardarBorradorDebitos();"/>

        <% } %>
    </div>

    <input type="hidden" id="<portlet:namespace/>workCacheKey" value="<%= cacheKey %>" />

    <liferay-ui:search-iterator searchContainer="<%= scDetalle %>" paginate="<%= false %>"/>
</div>

<script type="text/javascript">

    <% if (!appendMode) { %>

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
            if (els[i].checked) {
                values = values + els[i].value + ";";
            }
        }
        return values;
    }

    function eliminarDebitosSeleccionados(detalleKey) {

        var ids = "";

        if (detalleKey && detalleKey !== "") {
            ids = detalleKey + ";";
        } else {
            ids = getCheckedValuesByName('debitos');
        }

        if (ids === "") {
            alert("Debe seleccionar elementos para realizar la operación");
            return false;
        }

        if (!confirm("¿Confirma la eliminación de los elementos seleccionados?")) {
            return false;
        }

        if (typeof jQuery === 'undefined' || !jQuery.ajax) {
            var url = '<%= deleteDetalleURL %>' + '&ids=' + encodeURIComponent(ids);
            window.location.href = url;
            return false;
        }

        // cacheKey seguro (evitar "null")
        var cacheKey = "<%= cacheKey %>";

        // tipo actual real del padre
        var tipoActual = "";
        try {
            var $tipoSel = jQuery("#<portlet:namespace/>tipoProceso");
            if ($tipoSel && $tipoSel.length > 0) {
                tipoActual = $tipoSel.val();
            }
        } catch (e0) {}

        if (!tipoActual) tipoActual = "<%= tipoDebito %>";
        if (!tipoActual) tipoActual = "LI";
        tipoActual = ("" + tipoActual).toUpperCase();

        var data = {};
        data["ids"] = ids;
        data["cacheKey"] = cacheKey;

        data["tipo_proceso"] = tipoActual;
        data["tipoProceso"] = tipoActual;
        data["tipoDebito"] = tipoActual;

        data["<portlet:namespace/>ids"] = ids;
        data["<portlet:namespace/>cacheKey"] = cacheKey;

        data["<portlet:namespace/>tipo_proceso"] = tipoActual;
        data["<portlet:namespace/>tipoProceso"] = tipoActual;
        data["<portlet:namespace/>tipoDebito"] = tipoActual;

        jQuery.ajax({
            url: '<%= deleteDetalleURL %>',
            type: "POST",
            data: data,
            cache: false,
            success: function () {

                var refreshUrl = '<%= refreshListadoURL %>';

                var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val() || "";
                var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val() || "";
                var terc = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras").val() || "";

                var grabarEl = document.getElementById("<portlet:namespace />grabarDebitos");
                var grabar_debitos = (grabarEl && grabarEl.checked) ? "true" : "false";

                var params = {};
                params["cmd"] = "deleteDetalle";
                params["cacheKey"] = cacheKey;

                params["tipo_proceso"] = tipoActual;
                params["tipoProceso"] = tipoActual;
                params["tipoDebito"] = tipoActual;

                params["fechaDesdeMes"] = desde_mes;
                params["fechaDesdeAnio"] = desde_anio;
                params["tipo_debitos_tercerizadoras"] = terc;
                params["tipo_debito"] = terc;
                params["grabarDebitos"] = grabar_debitos;

                params["<portlet:namespace/>cmd"] = "deleteDetalle";
                params["<portlet:namespace/>cacheKey"] = cacheKey;

                params["<portlet:namespace/>tipo_proceso"] = tipoActual;
                params["<portlet:namespace/>tipoProceso"] = tipoActual;
                params["<portlet:namespace/>tipoDebito"] = tipoActual;

                params["<portlet:namespace/>fechaDesdeMes"] = desde_mes;
                params["<portlet:namespace/>fechaDesdeAnio"] = desde_anio;
                params["<portlet:namespace/>tipo_debitos_tercerizadoras"] = terc;
                params["<portlet:namespace/>tipo_debito"] = terc;
                params["<portlet:namespace/>grabarDebitos"] = grabar_debitos;

                var rnd = "" + (new Date().getTime());
                params["rnd"] = rnd;
                params["<portlet:namespace/>rnd"] = rnd;

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

    function exportacion(periodo, idTercerizadora) {
        var test = (periodo + "").split("-");
        var anio = test[0];
        var mes = test[1];

        if (!anio || !mes) {
            alert("Periodo invalido para exportacion: " + periodo);
            return;
        }

        var periodo_aux = anio + '-' + mes;

        var url_sub = '/xlsservlet/?reporte=REPORTE_DEBITO_TERCERIZADORAS'
            + '&fechaDesdeDia=01'
            + '&fechaDesdeMes=' + (mes - 1)
            + '&fechaDesdeAnio=' + anio
            + '&grabarDebitos=false'
            + '&periodo=' + periodo_aux
            + '&tipo_debitos_tercerizadoras=' + (idTercerizadora || '')
            + '&rnd=' + Math.floor(Math.random() * 100);

        window.location.href = url_sub;
    }

    function guardarBorradorDebitos() {

        if (!confirm("¿Confirma guardar el borrador del período actual?")) {
            return false;
        }

        if (typeof jQuery === 'undefined' || !jQuery.ajax) {
            // fallback sin ajax: redirigir (pierde UX pero funciona)
            var url = '<%= guardarBorradorURL %>'
                + '&cacheKey=' + encodeURIComponent("<%= cacheKey %>")
                + '&tipo_proceso=' + encodeURIComponent("<%= tipoDebito %>");
            window.location.href = url;
            return false;
        }

        var cacheKey = "<%= cacheKey %>";

        // tipo actual real del padre
        var tipoActual = "";
        try {
            var $tipoSel = jQuery("#<portlet:namespace/>tipoProceso");
            if ($tipoSel && $tipoSel.length > 0) {
                tipoActual = $tipoSel.val();
            }
        } catch (e0) {}
        if (!tipoActual) tipoActual = "<%= tipoDebito %>";
        if (!tipoActual) tipoActual = "LI";
        tipoActual = ("" + tipoActual).toUpperCase();

        var data = {};
        data["cacheKey"] = cacheKey;
        data["workCacheKey"] = cacheKey;

        data["tipo_proceso"] = tipoActual;
        data["tipoProceso"] = tipoActual;
        data["tipoDebito"] = tipoActual;

        data["<portlet:namespace/>cacheKey"] = cacheKey;
        data["<portlet:namespace/>workCacheKey"] = cacheKey;

        data["<portlet:namespace/>tipo_proceso"] = tipoActual;
        data["<portlet:namespace/>tipoProceso"] = tipoActual;
        data["<portlet:namespace/>tipoDebito"] = tipoActual;

        jQuery.ajax({
            url: '<%= guardarBorradorURL %>',
            type: "POST",
            data: data,
            cache: false,
            success: function () {

                // refrescar el listado igual que delete (para mantener estado)
                var refreshUrl = '<%= refreshListadoURL %>';

                var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val() || "";
                var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val() || "";
                var terc = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras").val() || "";

                var grabarEl = document.getElementById("<portlet:namespace />grabarDebitos");
                var grabar_debitos = (grabarEl && grabarEl.checked) ? "true" : "false";

                var params = {};
                params["cmd"] = "deleteDetalle"; // fuerza hydrate principal
                params["cacheKey"] = cacheKey;

                params["tipo_proceso"] = tipoActual;
                params["tipoProceso"] = tipoActual;
                params["tipoDebito"] = tipoActual;

                params["fechaDesdeMes"] = desde_mes;
                params["fechaDesdeAnio"] = desde_anio;
                params["tipo_debitos_tercerizadoras"] = terc;
                params["tipo_debito"] = terc;
                params["grabarDebitos"] = grabar_debitos;

                params["<portlet:namespace/>cmd"] = "deleteDetalle";
                params["<portlet:namespace/>cacheKey"] = cacheKey;

                params["<portlet:namespace/>tipo_proceso"] = tipoActual;
                params["<portlet:namespace/>tipoProceso"] = tipoActual;
                params["<portlet:namespace/>tipoDebito"] = tipoActual;

                params["<portlet:namespace/>fechaDesdeMes"] = desde_mes;
                params["<portlet:namespace/>fechaDesdeAnio"] = desde_anio;
                params["<portlet:namespace/>tipo_debitos_tercerizadoras"] = terc;
                params["<portlet:namespace/>tipo_debito"] = terc;
                params["<portlet:namespace/>grabarDebitos"] = grabar_debitos;

                var rnd = "" + (new Date().getTime());
                params["rnd"] = rnd;
                params["<portlet:namespace/>rnd"] = rnd;

                jQuery.ajax({
                    url: refreshUrl,
                    type: "GET",
                    data: params,
                    cache: false,
                    success: function (html) {
                        jQuery("#<portlet:namespace />listado_debitos").html(html);
                        alert("Borrador guardado.");
                    },
                    error: function (xhr2) {
                        alert("Borrador guardado, pero falló el refresh. HTTP " + xhr2.status);
                    }
                });
            },
            error: function (xhr) {
                alert("No se pudo guardar borrador. HTTP " + xhr.status);
            }
        });

        return false;
    }

    <% } else { %>

    // ===== STAGING helpers (no pisar delete del main) =====

    function <portlet:namespace/>marcarAnexar(valor) {
        var name = "<portlet:namespace/>idsRow";
        jQuery("#<portlet:namespace/>listado_debitos_anexar input[type=checkbox][name='" + name + "']").each(function () {
            this.checked = valor;
        });
    }

    function <portlet:namespace/>anexarUno(detalleKey) {
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
    }

    <% } %>

</script>

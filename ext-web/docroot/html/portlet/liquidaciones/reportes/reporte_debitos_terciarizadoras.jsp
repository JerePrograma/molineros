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

<%
    String actionDebitosURLStr = (String) pageContext.getAttribute("actionDebitosURL");
    if (actionDebitosURLStr == null) actionDebitosURLStr = "";
%>

<fieldset class="block-labels">
    <legend><liferay-ui:message key="reporte-debito-tercerizadoras"/></legend>

    <!-- =========================
         BLOQUE 1: BUSCAR (TABLA VIEJA)
         ========================= -->
    <table class="lfr-table">
        <tr>
            <td><label><liferay-ui:message key="periodo"/>:</label></td>
            <td>
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
            </td>

            <td><label><liferay-ui:message key="cobertura-expediente-tercerizadora"/>:</label></td>
            <td>
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

            <td>Proceso:</td>
            <td>
                <select name="<portlet:namespace />tipoProceso" id="<portlet:namespace />tipoProceso">
                    <% for (ClaseBase c : tipos) { %>
                    <option value="<%= c.getId() %>"><%= c.getDescripcion() %>
                    </option>
                    <% } %>
                </select>
            </td>

            <td>
                <label><liferay-ui:message key="grabar-debitos-tercerizadoras"/>:</label>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <input type="checkbox"
                       name="<portlet:namespace />grabarDebitos"
                       id="<portlet:namespace />grabarDebitos"
                       value="false"/>
            </td>

            <td>
                <input id="<portlet:namespace />buscar"
                       value="<liferay-ui:message key="buscar"/>"
                       title="<liferay-ui:message key="buscar" />"
                       type="button"
                       onClick="javascript:<portlet:namespace/>buscarMovimientos();"/>
            </td>
        </tr>
        <tr>
            <td colspan="7">&nbsp;</td>
        </tr>
    </table>

    <table>
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
            <td><label>Detalle:</label></td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3">
                <div id="<portlet:namespace />listado_debitos">
                    <jsp:include
                            page='/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras_search_result.jsp'/>
                </div>
            </td>
        </tr>
    </table>

    <hr/>

    <!-- =========================
         BLOQUE 2: AGREGAR (NUEVA BUSQUEDA / STAGING)
         Mismos campos, PERO con IDs distintos (_2)
         ========================= -->
    <table class="lfr-table">
        <tr>
            <td colspan="7"><strong>Agregar (segunda búsqueda)</strong></td>
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

            <td><label><liferay-ui:message key="cobertura-expediente-tercerizadora"/>:</label></td>
            <td>
                <select name="<portlet:namespace/>tipo_debitos_tercerizadoras_2"
                        id="<portlet:namespace/>tipo_debitos_tercerizadoras_2">
                    <option value="0">Seleccione</option>
                    <option value="OMI">OMINT</option>
                    <option value="MPS">MOLINEROS POR PS</option>
                    <option value="MEN">MOLINEROS POR ENSALUD</option>
                    <option value="CEM">CEMIC</option>
                    <option value="MIM">IMESA</option>
                    <option value="MON">MONOTRIBUTO</option>
                </select>
            </td>

            <td>Proceso:</td>
            <td>
                <select name="<portlet:namespace />tipoProceso_2" id="<portlet:namespace />tipoProceso_2">
                    <% for (ClaseBase c : tipos) { %>
                    <option value="<%= c.getId() %>"><%= c.getDescripcion() %>
                    </option>
                    <% } %>
                </select>
            </td>

            <td>
                <label><liferay-ui:message key="grabar-debitos-tercerizadoras"/>:</label>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <input type="checkbox"
                       name="<portlet:namespace />grabarDebitos_2"
                       id="<portlet:namespace />grabarDebitos_2"
                       value="false"/>
            </td>

            <td>
                <input id="<portlet:namespace />agregar"
                       value="Agregar"
                       type="button"
                       onClick="javascript:<portlet:namespace/>agregarMovimientos();"/>
            </td>
        </tr>
        <tr>
            <td colspan="7">&nbsp;</td>
        </tr>
    </table>

    <table>
        <tr>
            <td><label>Agregar:</label></td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3">
                <div id="<portlet:namespace />listado_debitos_anexar">
                    <jsp:include
                            page='/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras_append_result.jsp'/>
                </div>
            </td>
        </tr>
    </table>

</fieldset>

<fieldset class="block-labels">
    <div align="center" id="<portlet:namespace />buscando">
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
    <div align="center" id="<portlet:namespace />busquedaMovimientoDiv"></div>
</fieldset>

<script type="text/javascript">
    jQuery('#<portlet:namespace />buscando').hide();

    // ocultar ambos días
    jQuery("#<portlet:namespace/>fechaDesdeDia").hide();
    jQuery("#<portlet:namespace/>fechaDesdeDia_2").hide();

    jQuery(document).ready(function () {
        valiadSiHayReporteGrabado();     // bloque 1
        valiadSiHayReporteGrabado_2();   // bloque 2
    });

    // ============================================================
    // BUSCAR (bloque 1) -> refresca JSP viejo
    // ============================================================
    function <portlet:namespace />buscarMovimientos() {

        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
        var grabar_debitos = document.getElementById("<portlet:namespace />grabarDebitos").checked ? 'true' : 'false';
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras").val();
        var tipo_proceso = jQuery("#<portlet:namespace/>tipoProceso").val();

        if (tipo_debitos_tercerizadoras == '0') {
            alert('Debe seleccionar una tercerizadoras');
            return false;
        }

        var mes = parseInt(desde_mes) + 1;
        var periodo = desde_anio + '-' + PadLeft(mes, 2);

        jQuery('#<portlet:namespace />buscando').show();

        if (valiadSiHayReporte() == false) {
            jQuery('#<portlet:namespace />buscando').hide();
            alert('No hay Datos para el periodo solicitado');
            return false;
        }

        var busquedaNom = {
            "periodo": periodo,
            "fechaDesdeDia": "01",
            "fechaDesdeMes": desde_mes,
            "fechaDesdeAnio": desde_anio,

            "tipo_debitos_tercerizadoras": tipo_debitos_tercerizadoras,
            "tipo_debito": tipo_debitos_tercerizadoras,

            "tipo_proceso": tipo_proceso,
            "tipoDebito": tipo_proceso,

            "grabarDebitos": grabar_debitos,
            "cmd": "search"
        };

        jQuery('#<portlet:namespace />listado_debitos').load("<%= renderDebitosURLStr %>", busquedaNom, function () {
            jQuery('#<portlet:namespace />buscando').hide();
        });
    }

    // ============================================================
    // AGREGAR (bloque 2) -> MISMO concepto que Buscar,
    // pero refresca JSP nuevo (staging) y permite OTRO período.
    // ============================================================
    function <portlet:namespace/>agregarMovimientos() {

        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes_2").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio_2").val();
        var grabar_debitos = document.getElementById("<portlet:namespace />grabarDebitos_2").checked ? 'true' : 'false';
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras_2").val();
        var tipo_proceso = jQuery("#<portlet:namespace/>tipoProceso_2").val();

        if (tipo_debitos_tercerizadoras == '0') {
            alert('Debe seleccionar una tercerizadoras');
            return false;
        }

        var mes = parseInt(desde_mes) + 1;
        var periodo = desde_anio + '-' + PadLeft(mes, 2);

        jQuery('#<portlet:namespace />buscando').show();

        if (valiadSiHayReporte_2() == false) {
            jQuery('#<portlet:namespace />buscando').hide();
            alert('No hay Datos para el periodo solicitado');
            return false;
        }

        // workKey del main (si existe)
        var workKey = "";
        var el = document.getElementById("<portlet:namespace/>workCacheKey");
        if (el && el.value) workKey = el.value;

        var busquedaNom = {
            "periodo": periodo,
            "fechaDesdeDia": "01",

            // OJO: mandamos los nombres que el Action realmente lee
            "fechaDesdeMes": desde_mes,
            "fechaDesdeAnio": desde_anio,

            "tipo_debitos_tercerizadoras": tipo_debitos_tercerizadoras,
            "tipo_debito": tipo_debitos_tercerizadoras,

            "tipo_proceso": tipo_proceso,
            "tipoDebito": tipo_proceso,

            "grabarDebitos": grabar_debitos,

            "workCacheKey": workKey,
            "cmd": "appendSearch"
        };

        // duplicados namespaced (por si algún filtro termina llegando namespaced)
        busquedaNom["<portlet:namespace/>cmd"] = "appendSearch";
        busquedaNom["<portlet:namespace/>workCacheKey"] = workKey;

        // cache-buster
        var rnd = "" + (new Date().getTime());
        busquedaNom["rnd"] = rnd;
        busquedaNom["<portlet:namespace/>rnd"] = rnd;

        jQuery('#<portlet:namespace />listado_debitos_anexar').load("<%= renderDebitosURLStr %>", busquedaNom, function () {
            jQuery('#<portlet:namespace />buscando').hide();
            try { <portlet:namespace/>filtrarAnexar(); } catch(e) {}
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

        // preservar tipo seleccionado (uso el del bloque 2 para staging)
        var tipo_proceso = jQuery("#<portlet:namespace/>tipoProceso_2").val();

        jQuery('#<portlet:namespace />buscando').show();

        // 1) POST action: muta cache (staging -> main)
        jQuery.ajax({
            url: "<%= actionDebitosURLStr %>",
            type: "POST",
            data: {
                "cmd": "anexarDetalle",
                "<portlet:namespace/>cmd": "anexarDetalle",

                "ids": ids,
                "<portlet:namespace/>ids": ids,

                "workCacheKey": workKey,
                "<portlet:namespace/>workCacheKey": workKey,

                // compat: algunos flujos usan cacheKey
                "cacheKey": workKey,
                "<portlet:namespace/>cacheKey": workKey,

                "tipo_proceso": tipo_proceso,
                "<portlet:namespace/>tipo_proceso": tipo_proceso
            },
            success: function () {

                // 2) GET render: refrescar MAIN desde cache (cmd=deleteDetalle)
                jQuery('#<portlet:namespace />listado_debitos').load(
                    "<%= renderDebitosURLStr %>",
                    {
                        "cmd": "deleteDetalle",
                        "<portlet:namespace/>cmd": "deleteDetalle",
                        "cacheKey": workKey,
                        "<portlet:namespace/>cacheKey": workKey,
                        "tipo_proceso": tipo_proceso,
                        "<portlet:namespace/>tipo_proceso": tipo_proceso,
                        "rnd": "" + (new Date().getTime())
                    },
                    function () {

                        // 3) GET render: refrescar STAGING sin reseed (reuseStaging=true)
                        jQuery('#<portlet:namespace />listado_debitos_anexar').load(
                            "<%= renderDebitosURLStr %>",
                            {
                                "cmd": "appendSearch",
                                "<portlet:namespace/>cmd": "appendSearch",
                                "workCacheKey": workKey,
                                "<portlet:namespace/>workCacheKey": workKey,
                                "reuseStaging": "true",
                                "<portlet:namespace/>reuseStaging": "true",
                                "tipo_proceso": tipo_proceso,
                                "<portlet:namespace/>tipo_proceso": tipo_proceso,
                                "rnd": "" + (new Date().getTime())
                            },
                            function () {
                                jQuery('#<portlet:namespace />buscando').hide();
                            }
                        );

                    }
                );

            },
            error: function (xhr) {
                jQuery('#<portlet:namespace />buscando').hide();
                alert("Error anexando. status=" + xhr.status);
            }
        });

        return false;
    }

    // ============================================================
    // VALIDACIONES BLOQUE 1
    // ============================================================
    function valiadSiHayReporte() {
        var result = false;
        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras").val();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/hay_reporte_tercerizadora';
        url += '&fechaDesdeMes=' + desde_mes;
        url += "&fechaDesdeAnio=" + desde_anio;
        url += "&tipo_debitos_tercerizadoras=" + tipo_debitos_tercerizadoras;

        jQuery.ajax({
            url: url,
            async: false,
            success: function (data) {
                var obj = jQuery.parseJSON(data);
                if (obj && obj.resultado == '1') result = true;
            }
        });
        return result;
    }

    function valiadSiHayReporteGrabado() {
        var result = false;
        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras").val();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/hay_reporte_tercerizadora_grabar';
        url += '&fechaDesdeMes=' + desde_mes;
        url += "&fechaDesdeAnio=" + desde_anio;
        url += "&tipo_debitos_tercerizadoras=" + tipo_debitos_tercerizadoras;

        jQuery.ajax({
            url: url,
            async: false,
            success: function (data) {
                var obj = jQuery.parseJSON(data);
                if (obj && obj.resultado == '1') result = true;
            }
        });

        if (result == true) {
            jQuery('#<portlet:namespace />grabarDebitos').attr('checked', true);
            jQuery('#<portlet:namespace />grabarDebitos').attr("disabled", true);
        } else {
            jQuery('#<portlet:namespace />grabarDebitos').attr('checked', false);
            jQuery('#<portlet:namespace />grabarDebitos').attr("disabled", false);
        }
    }

    // eventos bloque 1
    jQuery('#<portlet:namespace />fechaDesdeMes').change(valiadSiHayReporteGrabado);
    jQuery('#<portlet:namespace />fechaDesdeAnio').change(valiadSiHayReporteGrabado);
    jQuery('#<portlet:namespace />tipo_debitos_tercerizadoras').change(valiadSiHayReporteGrabado);

    // ============================================================
    // VALIDACIONES BLOQUE 2 (segunda búsqueda)
    // ============================================================
    function valiadSiHayReporte_2() {
        var result = false;
        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes_2").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio_2").val();
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras_2").val();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/hay_reporte_tercerizadora';
        url += '&fechaDesdeMes=' + desde_mes;
        url += "&fechaDesdeAnio=" + desde_anio;
        url += "&tipo_debitos_tercerizadoras=" + tipo_debitos_tercerizadoras;

        jQuery.ajax({
            url: url,
            async: false,
            success: function (data) {
                var obj = jQuery.parseJSON(data);
                if (obj && obj.resultado == '1') result = true;
            }
        });
        return result;
    }

    function valiadSiHayReporteGrabado_2() {
        var result = false;
        var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes_2").val();
        var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio_2").val();
        var tipo_debitos_tercerizadoras = jQuery("#<portlet:namespace/>tipo_debitos_tercerizadoras_2").val();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/hay_reporte_tercerizadora_grabar';
        url += '&fechaDesdeMes=' + desde_mes;
        url += "&fechaDesdeAnio=" + desde_anio;
        url += "&tipo_debitos_tercerizadoras=" + tipo_debitos_tercerizadoras;

        jQuery.ajax({
            url: url,
            async: false,
            success: function (data) {
                var obj = jQuery.parseJSON(data);
                if (obj && obj.resultado == '1') result = true;
            }
        });

        if (result == true) {
            jQuery('#<portlet:namespace />grabarDebitos_2').attr('checked', true);
            jQuery('#<portlet:namespace />grabarDebitos_2').attr("disabled", true);
        } else {
            jQuery('#<portlet:namespace />grabarDebitos_2').attr('checked', false);
            jQuery('#<portlet:namespace />grabarDebitos_2').attr("disabled", false);
        }
    }

    // eventos bloque 2
    jQuery('#<portlet:namespace />fechaDesdeMes_2').change(valiadSiHayReporteGrabado_2);
    jQuery('#<portlet:namespace />fechaDesdeAnio_2').change(valiadSiHayReporteGrabado_2);
    jQuery('#<portlet:namespace />tipo_debitos_tercerizadoras_2').change(valiadSiHayReporteGrabado_2);

    function PadLeft(value, length) {
        return (value.toString().length < length) ? PadLeft("0" + value, length) : value;
    }
</script>

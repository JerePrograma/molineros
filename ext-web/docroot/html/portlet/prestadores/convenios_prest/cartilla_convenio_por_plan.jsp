<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
    BusquedaCartillaConvenioFiltro filtro =
            (BusquedaCartillaConvenioFiltro) request.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);

    if (filtro == null) {
        filtro = (BusquedaCartillaConvenioFiltro) session.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);
    }

    if (filtro == null) {
        filtro = new BusquedaCartillaConvenioFiltro();
    }

    List<CartillaConvenioRow> resultados =
            (List<CartillaConvenioRow>) request.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

    if (resultados == null) {
        resultados = (List<CartillaConvenioRow>) session.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);
    }

    if (resultados == null) {
        resultados = new ArrayList<CartillaConvenioRow>();
    }

    request.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtro);
    request.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultados);

    List<Plan> planes =
            (List<Plan>) session.getAttribute(WebKeysPrestadores.PLANES_EN_SESSION);
            
    List<EspecialidadPrestador> especialidades =
            (List<EspecialidadPrestador>) session.getAttribute(WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION);

    String idPlanValue = filtro.getIdPlan() != null ? String.valueOf(filtro.getIdPlan()) : "";
    String idPrestadorValue = filtro.getIdPrestador() != null ? String.valueOf(filtro.getIdPrestador()) : "";
    String cuitPrestadorValue = filtro.getCuitPrestador() != null ? filtro.getCuitPrestador() : "";
    String prestadorDescripcionValue = filtro.getPrestadorDescripcion() != null ? filtro.getPrestadorDescripcion() : "";
    String idProvinciaValue = filtro.getIdProvincia() != null ? String.valueOf(filtro.getIdProvincia()) : "";
    String idLocalidadValue = filtro.getIdLocalidad() != null ? String.valueOf(filtro.getIdLocalidad()) : "";
    String idEspecialidadValue = filtro.getIdEspecialidad() != null ? String.valueOf(filtro.getIdEspecialidad()) : "";

    boolean hayResultadosIniciales = resultados != null && !resultados.isEmpty();
%>

<portlet:renderURL var="renderCartillaSearchURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/prestadores/cartilla_convenio_por_plan" />
    <portlet:param name="<%= Constants.CMD %>" value="search" />
</portlet:renderURL>

<portlet:actionURL var="exportCartillaXlsBaseURL">
    <portlet:param name="struts_action" value="/prestadores/cartilla_convenio_por_plan" />
    <portlet:param name="<%= Constants.CMD %>" value="exportCartillaXls" />
</portlet:actionURL>

<portlet:renderURL var="volverURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/prestadores/view" />
    <portlet:param name="tabs1" value="cartilla-convenios-prestadores" />
</portlet:renderURL>

<form action="#" method="post" name="<portlet:namespace />fm" onsubmit="return <portlet:namespace/>buscarCartilla();">
    <fieldset class="block-labels">
        <legend>Cartilla de Convenios de Prestadores</legend>

        <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px; width: 100%;">
            <tr>
                <td><label>Plan:</label></td>
                <td>
                    <select name="idPlan" id="<portlet:namespace />idPlan">
                        <option value="">-- Todos los planes --</option>
                        <%
                            if (planes != null) {
                                for (Plan p : planes) {
                        %>
                        <option value="<%= p.getId() %>"
                            <%= idPlanValue.equals(String.valueOf(p.getId())) ? "selected=\"selected\"" : "" %>>
                            <%= HtmlUtil.escape(p.getDescripcion()) %>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </td>

                <td><label>Cód. prestador:</label></td>
                <td>
                    <input type="text"
                           name="idPrestador"
                           id="<portlet:namespace />idPrestador"
                           maxlength="6"
                           size="8"
                           value="<%= HtmlUtil.escape(idPrestadorValue) %>"
                           onkeydown="return allowOnlyDigits(event);" />
                </td>
            </tr>

            <tr>
                <td><label>CUIT:</label></td>
                <td>
                    <input type="text"
                           name="cuitPrestador"
                           id="<portlet:namespace />cuitPrestador"
                           maxlength="11"
                           size="14"
                           value="<%= HtmlUtil.escape(cuitPrestadorValue) %>"
                           onkeydown="return allowOnlyDigits(event);" />
                </td>

                <td><label>Nombre prestador:</label></td>
                <td>
                    <input type="text"
                           name="prestadorDescripcion"
                           id="<portlet:namespace />prestadorDescripcion"
                           size="50"
                           value="<%= HtmlUtil.escape(prestadorDescripcionValue) %>" />
                </td>
            </tr>

            <tr>
                <td><label>Provincia:</label></td>
                <td>
                    <select name="idProvincia"
                    id="<portlet:namespace />idProvincia"
                    onchange="<portlet:namespace/>filtrarLocalidadesCartilla();">
                        <option value="">-- Todas --</option>
                        <%
                            if (provincias != null) {
                                for (Provincia p : provincias) {
                        %>
                        <option value="<%= p.getId() %>"
                            <%= idProvinciaValue.equals(String.valueOf(p.getId())) ? "selected=\"selected\"" : "" %>>
                            <%= HtmlUtil.escape(p.getDescripcion()) %>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </td>

                <td><label>Localidad:</label></td>
                <td>
                    <div class="selector-localidad-cartilla">
                        <select name="idLocalidad" id="<portlet:namespace />idLocalidad">
                            <option value="">-- Todas --</option>
                            <%
                                Integer idProvinciaFiltro = null;

                                try {
                                    if (idProvinciaValue != null && idProvinciaValue.trim().length() > 0) {
                                        idProvinciaFiltro = Integer.valueOf(idProvinciaValue);
                                    }
                                } catch (Exception e) {
                                    idProvinciaFiltro = null;
                                }

                                if (localidades != null && idProvinciaFiltro != null && idProvinciaFiltro.intValue() > 0) {
                                    java.util.LinkedHashMap<Integer, Localidad> localidadesUnicas =
                                            new java.util.LinkedHashMap<Integer, Localidad>();

                                    for (Localidad l : localidades) {
                                        if (l == null) continue;
                                        if (l.getId() <= 0) continue;
                                        if (l.getId_provincia() != idProvinciaFiltro.intValue()) continue;
                                        if (l.getDescripcion() == null || l.getDescripcion().trim().length() == 0) continue;

                                        localidadesUnicas.put(Integer.valueOf(l.getId()), l);
                                    }

                                    for (Localidad l : localidadesUnicas.values()) {
                            %>
                            <option value="<%= l.getId() %>"
                                <%= idLocalidadValue.equals(String.valueOf(l.getId())) ? "selected=\"selected\"" : "" %>>
                                <%= HtmlUtil.escape(l.getDescripcion()) %>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                </td>
            </tr>

            <tr>
                <td><label>Especialidad:</label></td>
                <td>
                    <select name="idEspecialidad" id="<portlet:namespace />idEspecialidad">
                        <option value="">-- Todas --</option>
                        <%
                            if (especialidades != null) {
                                for (EspecialidadPrestador e : especialidades) {
                        %>
                        <option value="<%= e.getIdEspecialidad() %>"
                            <%= idEspecialidadValue.equals(String.valueOf(e.getIdEspecialidad())) ? "selected=\"selected\"" : "" %>>
                            <%= HtmlUtil.escape(e.getDescripcion()) %>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </td>

                <td><label>Incluye bajas:</label></td>
                <td>
                    <input type="checkbox"
                           name="incluyeBajas"
                           id="<portlet:namespace />incluyeBajas"
                           value="true"
                           <%= filtro.isIncluyeBajas() ? "checked=\"checked\"" : "" %> />
                </td>
            </tr>

            <tr>
                <td colspan="4" align="left">
                    <input type="submit" value="Buscar" />

                    &nbsp;&nbsp;

                    <input type="button"
                           value="Limpiar"
                           onclick="return <portlet:namespace/>limpiarCartilla();" />

                    &nbsp;&nbsp;

                    <input type="button"
                           id="<portlet:namespace />btnExportarCartilla"
                           value="Exportar XLS"
                           style="<%= hayResultadosIniciales ? "" : "display:none;" %>"
                           onclick="javascript:<portlet:namespace/>exportarCartilla();" />

                    &nbsp;&nbsp;

                    <input type="button"
                           value="Volver"
                           onclick="location.href='<%= volverURL.toString() %>';" />
                </td>
            </tr>

            <tr>
                <td colspan="4">
                    &nbsp;(Si no selecciona un plan, se consultan todos los planes)
                </td>
            </tr>
        </table>
    </fieldset>

    <div align="center" id="<portlet:namespace />buscandoCartilla" style="display:none; margin:6px 0 0 0;">
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

    <fieldset class="block-labels">
        <legend>Resultados</legend>

        <div id="<portlet:namespace />cartilla_resultados">
            <%
                if (hayResultadosIniciales) {
            %>
                <jsp:include page="/html/portlet/prestadores/convenios_prest/cartilla_prestadores_por_plan_search_result.jsp" />
            <%
                } else {
            %>
                <div class="portlet-msg-info">Ejecutá una búsqueda para ver resultados.</div>
            <%
                }
            %>
        </div>
    </fieldset>
</form>

<script type="text/javascript">
    var NS = "<portlet:namespace/>";

    function nsKey(k) {
        return NS + k;
    }

    function fixUrl(u) {
        return (u || "").replace(/&amp;/g, "&");
    }

    var SEARCH_URL = fixUrl("<%= (String) pageContext.getAttribute("renderCartillaSearchURL") %>");
    var EXPORT_URL = fixUrl("<%= (String) pageContext.getAttribute("exportCartillaXlsBaseURL") %>");

    function valById(idNoNs) {
        var $e = jQuery("#" + nsKey(idNoNs));
        if ($e.length) return $e.val();

        $e = jQuery("#" + idNoNs);
        if ($e.length) return $e.val();

        $e = jQuery("[name='" + idNoNs + "']");
        if ($e.length) return $e.val();

        $e = jQuery("[name='" + nsKey(idNoNs) + "']");
        if ($e.length) return $e.val();

        return "";
    }

    function put(params, key, value) {
        params[key] = value;
        params[nsKey(key)] = value;
        return params;
    }

    function showCartillaLoading(on) {
        var $s = jQuery("#" + nsKey("buscandoCartilla"));
        if (!$s.length) return;

        if (on) $s.show();
        else $s.hide();
    }

    function evalScriptsSafe(html) {
        try {
            if (window.Liferay && Liferay.Util && typeof Liferay.Util.evalScripts === "function") {
                Liferay.Util.evalScripts(html);
            }
        } catch (e) {}
    }

    function allowOnlyDigits(evt) {
        evt = evt || window.event;

        var code = evt.which != null ? evt.which : evt.keyCode;

        if (code === 8 || code === 9 || code === 13 || code === 27 || code === 46) {
            return true;
        }

        if (code >= 35 && code <= 40) {
            return true;
        }

        if (code >= 48 && code <= 57) {
            return true;
        }

        if (code >= 96 && code <= 105) {
            return true;
        }

        return false;
    }

    function <portlet:namespace/>syncExportButton() {
        var qty = 0;
        var $qty = jQuery("#" + nsKey("cartillaResultsCount"));

        if ($qty.length) {
            qty = parseInt($qty.val(), 10);
            if (isNaN(qty)) qty = 0;
        }

        if (qty > 0) {
            jQuery("#" + nsKey("btnExportarCartilla")).show();
        } else {
            jQuery("#" + nsKey("btnExportarCartilla")).hide();
        }
    }

    function <portlet:namespace/>buscarCartilla() {
        var params = {};

        put(params, "cmd", "search");
        put(params, "idPlan", valById("idPlan"));
        put(params, "idPrestador", valById("idPrestador"));
        put(params, "cuitPrestador", valById("cuitPrestador"));
        put(params, "prestadorDescripcion", valById("prestadorDescripcion"));
        put(params, "idProvincia", valById("idProvincia"));
        put(params, "idLocalidad", valById("idLocalidad"));
        put(params, "idEspecialidad", valById("idEspecialidad"));
        put(params, "incluyeBajas", jQuery("#" + nsKey("incluyeBajas")).is(":checked") ? "true" : "false");
        put(params, "rnd", "" + new Date().getTime());

        showCartillaLoading(true);

        jQuery.ajax({
            url: SEARCH_URL,
            type: "GET",
            data: params,
            success: function(responseText) {
                jQuery("#" + nsKey("cartilla_resultados")).html(responseText);
                showCartillaLoading(false);
                evalScriptsSafe(responseText);
                <portlet:namespace/>syncExportButton();
            },
            error: function(xhr) {
                showCartillaLoading(false);
                jQuery("#" + nsKey("cartilla_resultados"))
                    .html('<div class="portlet-msg-error">Error consultando cartilla.</div>');
            }
        });

        return false;
    }

    function <portlet:namespace/>limpiarCartilla() {
        jQuery("#" + nsKey("idPlan")).val("");
        jQuery("#" + nsKey("idPrestador")).val("");
        jQuery("#" + nsKey("cuitPrestador")).val("");
        jQuery("#" + nsKey("prestadorDescripcion")).val("");
        jQuery("#" + nsKey("idProvincia")).val("");
        jQuery("#" + nsKey("idLocalidad")).val("");
        jQuery("#" + nsKey("idLocalidad")).html('<option value="">-- Todas --</option>');
        jQuery("#" + nsKey("idEspecialidad")).val("");
        jQuery("#" + nsKey("incluyeBajas")).prop("checked", false);

        jQuery("#" + nsKey("cartilla_resultados")).html('<div class="portlet-msg-info">Ejecutá una búsqueda para ver resultados.</div>');
        jQuery("#" + nsKey("btnExportarCartilla")).hide();
        showCartillaLoading(false);

        return false;
    }

    function appendUrlParam(url, key, value) {
        var sep = (url.indexOf("?") >= 0) ? "&" : "?";
        return url + sep + encodeURIComponent(key) + "=" + encodeURIComponent(value == null ? "" : value);
    }

    function appendPortletParam(url, key, value) {
        url = appendUrlParam(url, key, value);
        url = appendUrlParam(url, nsKey(key), value);
        return url;
    }

    function <portlet:namespace/>exportarCartilla() {
        var url = EXPORT_URL;

        url = appendPortletParam(url, "idPlan", valById("idPlan"));
        url = appendPortletParam(url, "idPrestador", valById("idPrestador"));
        url = appendPortletParam(url, "cuitPrestador", valById("cuitPrestador"));
        url = appendPortletParam(url, "prestadorDescripcion", valById("prestadorDescripcion"));
        url = appendPortletParam(url, "idProvincia", valById("idProvincia"));
        url = appendPortletParam(url, "idLocalidad", valById("idLocalidad"));
        url = appendPortletParam(url, "idEspecialidad", valById("idEspecialidad"));
        url = appendPortletParam(url, "incluyeBajas", jQuery("#" + nsKey("incluyeBajas")).is(":checked") ? "true" : "false");

        location.href = url;
    }

    jQuery(document).ready(function () {
        <portlet:namespace/>syncExportButton();
    });

    function <portlet:namespace/>filtrarLocalidadesCartilla() {
        var idProvincia = jQuery("#" + nsKey("idProvincia")).val();
        var $localidad = jQuery("#" + nsKey("idLocalidad"));

        $localidad.html('<option value="">-- Todas --</option>');
        $localidad.val("");

        if (!idProvincia || idProvincia === "0") {
            return false;
        }

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/prestadores/id_provincia_localidad&idProvincia='
            + encodeURIComponent(idProvincia);

        jQuery.ajax({
            url: fixUrl(url),
            type: "GET",
            async: false,
            success: function(data) {
                var obj = null;

                try {
                    obj = jQuery.parseJSON(data);
                } catch (e) {
                    obj = null;
                }

                if (obj && obj.listaFiltrada && obj.listaFiltrada.length) {
                    $localidad.html(obj.listaFiltrada.join(""));
                } else {
                    $localidad.html('<option value="">-- Todas --</option>');
                }

                var $defaultOption = $localidad.find("option[value='0']").first();
                if ($defaultOption.length) {
                    $defaultOption.val("");
                    $defaultOption.text("-- Todas --");
                }

                if ($localidad.find("option[value='']").length === 0) {
                    $localidad.prepend('<option value="">-- Todas --</option>');
                }

                $localidad.val("");
            },
            error: function() {
                $localidad.html('<option value="">-- Todas --</option>');
                $localidad.val("");
            }
        });

        return false;
    }
</script>
<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.EspecialidadPrestador" %>
<%@ page import="ar.com.ospim.prestadores.beans.BusquedaCartillaConvenioFiltro" %>
<%@ page import="ar.com.ospim.prestadores.beans.CartillaConvenioRow" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
    BusquedaCartillaConvenioFiltro filtro =
            (BusquedaCartillaConvenioFiltro) request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);

    if (filtro == null) {
        filtro = new BusquedaCartillaConvenioFiltro();
    }

    List<CartillaConvenioRow> resultados =
            (List<CartillaConvenioRow>) request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

    if (resultados == null) {
        resultados = new ArrayList<CartillaConvenioRow>();
    }

    List<Plan> planes =
            (List<Plan>) session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION);

    List<Provincia> provincias =
            (List<Provincia>) session.getAttribute(WebKeysLiquidaciones.PROVINCIAS_EN_SESSION);

    List<Localidad> localidades =
            (List<Localidad>) session.getAttribute(WebKeysLiquidaciones.LOCALIDADES_EN_SESSION);

    List<EspecialidadPrestador> especialidades =
            (List<EspecialidadPrestador>) session.getAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION);

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
                    <select name="idProvincia" id="<portlet:namespace />idProvincia">
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
                    <select name="idLocalidad" id="<portlet:namespace />idLocalidad">
                        <option value="">-- Todas --</option>
                        <%
                            if (localidades != null) {
                                for (Localidad l : localidades) {
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
                           id="<portlet:namespace/>btnExportarCartilla"
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

    <div align="center" id="<portlet:namespace/>buscandoCartilla" style="display:none; margin:6px 0 0 0;">
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

        <div id="<portlet:namespace/>cartilla_resultados">
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

        // backspace, tab, enter, escape, delete
        if (code === 8 || code === 9 || code === 13 || code === 27 || code === 46) {
            return true;
        }

        // flechas, home, end
        if ((code >= 35 && code <= 40)) {
            return true;
        }

        // números
        if (code >= 48 && code <= 57) {
            return true;
        }

        // numpad
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

        jQuery("#" + nsKey("cartilla_resultados")).load(SEARCH_URL, params, function(responseText) {
            showCartillaLoading(false);
            evalScriptsSafe(responseText);
            <portlet:namespace/>syncExportButton();
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
</script>
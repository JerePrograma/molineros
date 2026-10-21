<%--
Responsabilidad:
    Renderiza las filas del resultado de búsqueda de requerimientos.
Incluido desde:
    Forward, Action o entry point directo en: tiles-defs.xml.
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper" %>


<%
List<RequerimientoCompra> requerimientos =
        (List<RequerimientoCompra>) renderRequest.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA);

if (requerimientos == null) {
    requerimientos = new ArrayList<RequerimientoCompra>();
}

boolean mostrarIdRpListado =
        Boolean.TRUE.equals(
                renderRequest.getAttribute(
                        WebKeysCompras.MOSTRAR_ID_RP_LISTADO
                )
        );

Map<Integer, RequerimientoCompraReclamoPrestacional> relacionesRp =
        (Map<Integer, RequerimientoCompraReclamoPrestacional>)
                renderRequest.getAttribute(
                        WebKeysCompras
                                .RELACIONES_RECLAMO_PRESTACIONAL_COMPRA
                );

if (relacionesRp == null) {
    relacionesRp =
            new HashMap<Integer, RequerimientoCompraReclamoPrestacional>();
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/buscar_requerimientos");

List<String> headerNames = new ArrayList<String>();
String[] titulos = ExportarRequerimientosCompraHelper.titulosListado(mostrarIdRpListado);
for (int h = 0; h < titulos.length; h++) {
    headerNames.add(titulos[h]);
}
headerNames.add("acciones");

SearchContainer searchContainer = new SearchContainer(
        renderRequest,
        null,
        null,
        SearchContainer.DEFAULT_CUR_PARAM,
        BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA,
        portletURL,
        headerNames,
        "No se encontraron requerimientos de compras."
);

searchContainer.setTotal(requerimientos.size());
List resultRows = searchContainer.getResultRows();

for (int i = 0; i < requerimientos.size(); i++) {
    RequerimientoCompra req = requerimientos.get(i);

    PortletURL verURL = renderResponse.createRenderURL();
    verURL.setWindowState(WindowState.MAXIMIZED);
    verURL.setParameter("struts_action", "/compras/ver_requerimiento");
    verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

    String[] valores = ExportarRequerimientosCompraHelper.valoresListado(
            req, relacionesRp.get(Integer.valueOf(req.getIdRequerimientoCompra())),
            mostrarIdRpListado);

    ResultRow row = new ResultRow(req, req.getIdRequerimientoCompraString(), i);
    for (int c = 0; c < valores.length; c++) {
        row.addText(HtmlUtil.escape(valores[c]), verURL);
    }

    row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/compras/requerimientos/requerimiento_compra_acciones.jsp");

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

<%
int totalRegistros = requerimientos.isEmpty() ? 0 : requerimientos.get(0).getTotalRegistros();
int totalPaginas = totalRegistros == 0 ? 0 : 1 + (totalRegistros - 1)
        / BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA;
Integer paginaListado = (Integer) renderRequest.getAttribute(
        WebKeysCompras.PAGINA_REQUERIMIENTOS_COMPRA);
int paginaActual = paginaListado != null ? paginaListado.intValue() : 1;
if (totalPaginas > 0) {
%>
<table class="lfr-table">
    <tr>
        <td>Total filas encontradas: <%= totalRegistros %></td>
        <td><%= paginaActual %>/<%= totalPaginas %> páginas&nbsp;</td>
        <td>
            <select name="<portlet:namespace />pagina_sel" id="<portlet:namespace />pagina_sel"
                    onchange="<portlet:namespace />buscarRequerimientos(this.value);">
                <% for (int pagina = 1; pagina <= totalPaginas; pagina++) { %>
                <option value="<%= pagina %>" <% if (pagina == paginaActual) { %>selected="selected"<% } %>><%= pagina %></option>
                <% } %>
            </select>
        </td>
        <td>
            <div class="search-pages">
                <div class="page-links">
                    <c:choose>
                        <c:when test="<%= paginaActual > 1 %>">
                            <a class="first" href="javascript:void(0)" onclick="return <portlet:namespace />buscarRequerimientos(1);"><liferay-ui:message key="first" /></a>
                        </c:when>
                        <c:otherwise>
                            <span class="first"><liferay-ui:message key="first" /></span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="<%= paginaActual > 1 %>">
                            <a class="previous" href="javascript:void(0)" onclick="return <portlet:namespace />buscarRequerimientos(<%= paginaActual - 1 %>);"><liferay-ui:message key="previous" /></a>
                        </c:when>
                        <c:otherwise>
                            <span class="previous"><liferay-ui:message key="previous" /></span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="<%= paginaActual < totalPaginas %>">
                            <a class="next" href="javascript:void(0)" onclick="return <portlet:namespace />buscarRequerimientos(<%= paginaActual + 1 %>);"><liferay-ui:message key="next" /></a>
                        </c:when>
                        <c:otherwise>
                            <span class="next"><liferay-ui:message key="next" /></span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="<%= paginaActual < totalPaginas %>">
                            <a class="last" href="javascript:void(0)" onclick="return <portlet:namespace />buscarRequerimientos(<%= totalPaginas %>);"><liferay-ui:message key="last" /></a>
                        </c:when>
                        <c:otherwise>
                            <span class="last"><liferay-ui:message key="last" /></span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </td>
    </tr>
</table>
<% } %>

<%
String exportacionToken = (String) renderRequest.getAttribute(
        ExportarRequerimientosCompraHelper.TOKEN);
if (exportacionToken != null
        && renderRequest.getAttribute(WebKeysCompras.ERROR_PARA_ALERT) == null) {
%>
<input type="hidden" class="compras-exportacion-token"
       value="<%= HtmlUtil.escape(exportacionToken) %>" />
<% } %>

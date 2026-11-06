<%--
Referencia legacy:
    Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp: fieldset, lfr-table,
    etiquetas laterales, input-date y botonera.
    Liquidaciones/ordenes_pago/orden_pago_ospim_search_result.jsp:
    SearchContainer, ResultRow y enlace a la vista propia de la orden.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden" %>
<%@ page import="ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%
if (!RequerimientoCompraOrdenAction.puedeConsultar(user)) {
%>
<div class="portlet-msg-error">No posee permisos para consultar &oacute;rdenes de compra.</div>
<%
} else {
    List<RequerimientoCompraOrden> ordenes =
            (List<RequerimientoCompraOrden>) renderRequest.getAttribute("comprasOrdenesEmitidas");
    if (ordenes == null) {
        ordenes = new ArrayList<RequerimientoCompraOrden>();
    }
    List<RequerimientoCompraSector> sectores =
            (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO);
    if (sectores == null) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
    Integer paginaAttribute = (Integer) renderRequest.getAttribute("comprasOrdenesPagina");
    int paginaOrdenes = paginaAttribute == null ? 1 : paginaAttribute.intValue();
    boolean haySiguiente = Boolean.TRUE.equals(renderRequest.getAttribute("comprasOrdenesHaySiguiente"));
    String errorOrdenes = (String) renderRequest.getAttribute("comprasOrdenesError");
    String[] nombresFiltros = { "id_orden_compra", "id_sector_oc",
            "fechaAltaOCDesdeDia", "fechaAltaOCDesdeMes", "fechaAltaOCDesdeAnio",
            "fechaAltaOCHastaDia", "fechaAltaOCHastaMes", "fechaAltaOCHastaAnio" };
    String[] valoresFiltros = new String[nombresFiltros.length];
    String namespaceOrdenes = renderResponse.getNamespace();
    PortletURL buscarOrdenesURL = renderResponse.createRenderURL();
    buscarOrdenesURL.setWindowState(LiferayWindowState.MAXIMIZED);
    buscarOrdenesURL.setParameter("struts_action", "/compras/view");
    buscarOrdenesURL.setParameter("tabs1", "orden-de-compra");
    pageContext.setAttribute("buscarOrdenesURL", buscarOrdenesURL);
    PortletURL paginaOrdenesURL = renderResponse.createRenderURL();
    paginaOrdenesURL.setWindowState(LiferayWindowState.MAXIMIZED);
    paginaOrdenesURL.setParameter("struts_action", "/compras/view");
    paginaOrdenesURL.setParameter("tabs1", "orden-de-compra");
    for (int f = 0; f < nombresFiltros.length; f++) {
        String value = ParamUtil.getString(renderRequest, nombresFiltros[f], "");
        if (WebKeysCompras.isEmpty(value)) {
            value = ParamUtil.getString(renderRequest, namespaceOrdenes + nombresFiltros[f], "");
        }
        valoresFiltros[f] = value;
        paginaOrdenesURL.setParameter(nombresFiltros[f], value);
    }
    int anioActualOrdenes = Calendar.getInstance().get(Calendar.YEAR);
%>
<form action="<%= buscarOrdenesURL %>" method="get" name="<portlet:namespace />fmOrdenesCompra">
    <liferay-portlet:renderURLParams varImpl="buscarOrdenesURL" />
    <fieldset class="block-labels">
        <legend>Filtro de b&uacute;squeda de &oacute;rdenes de compra</legend>
        <table class="lfr-table" width="100%">
            <tr>
                <td><label for="<portlet:namespace />id_orden_compra">N&uacute;mero OC:</label></td>
                <td><input id="<portlet:namespace />id_orden_compra" name="<portlet:namespace />id_orden_compra" type="text" size="15" maxlength="10" value="<%= HtmlUtil.escape(valoresFiltros[0]) %>" /></td>
                <td><label for="<portlet:namespace />id_sector_oc">Sector:</label></td>
                <td>
                    <select id="<portlet:namespace />id_sector_oc" name="<portlet:namespace />id_sector_oc">
                        <option value="0">Todos</option>
                        <% for (int s = 0; s < sectores.size(); s++) {
                            RequerimientoCompraSector sector = sectores.get(s);
                            String idSector = String.valueOf(sector.getIdSector()); %>
                        <option value="<%= idSector %>" <%= idSector.equals(valoresFiltros[1]) ? "selected=\"selected\"" : "" %>><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr><td colspan="4">&nbsp;</td></tr>
            <tr>
                <td><label>Fecha Alta OC desde:</label></td>
                <td>
                    <liferay-ui:input-date dayParam="fechaAltaOCDesdeDia"
                            dayValue="<%= GetterUtil.getInteger(valoresFiltros[2], -1) %>"
                            monthParam="fechaAltaOCDesdeMes"
                            monthValue="<%= GetterUtil.getInteger(valoresFiltros[3], -1) %>"
                            yearParam="fechaAltaOCDesdeAnio"
                            yearValue="<%= GetterUtil.getInteger(valoresFiltros[4], -1) %>"
                            yearRangeStart="1900" yearRangeEnd="<%= anioActualOrdenes %>"
                            firstDayOfWeek="<%= Calendar.getInstance().getFirstDayOfWeek() - 1 %>"
                            dayNullable="true" monthNullable="true" yearNullable="true" />
                </td>
                <td><label>Fecha Alta OC hasta:</label></td>
                <td>
                    <liferay-ui:input-date dayParam="fechaAltaOCHastaDia"
                            dayValue="<%= GetterUtil.getInteger(valoresFiltros[5], -1) %>"
                            monthParam="fechaAltaOCHastaMes"
                            monthValue="<%= GetterUtil.getInteger(valoresFiltros[6], -1) %>"
                            yearParam="fechaAltaOCHastaAnio"
                            yearValue="<%= GetterUtil.getInteger(valoresFiltros[7], -1) %>"
                            yearRangeStart="1900" yearRangeEnd="<%= anioActualOrdenes %>"
                            firstDayOfWeek="<%= Calendar.getInstance().getFirstDayOfWeek() - 1 %>"
                            dayNullable="true" monthNullable="true" yearNullable="true" />
                </td>
            </tr>
            <tr><td colspan="4">&nbsp;</td></tr>
            <tr><td colspan="4"><input type="submit" value="Buscar" />&nbsp;&nbsp;<input type="button" value="Limpiar" onclick="window.location.href='<%= HtmlUtil.escape(buscarOrdenesURL.toString()) %>';" /></td></tr>
        </table>
    </fieldset>
</form>
<% if (!WebKeysCompras.isEmpty(errorOrdenes)) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape(errorOrdenes) %></div>
<% } else {
    List<String> cabecerasOrdenes = new ArrayList<String>();
    cabecerasOrdenes.add("N\u00famero OC");
    cabecerasOrdenes.add("ID Requerimiento");
    cabecerasOrdenes.add("ID Reclamo Prestacional");
    cabecerasOrdenes.add("Fecha Alta OC");
    cabecerasOrdenes.add("Sector");
    cabecerasOrdenes.add("Prestador adjudicado");
    cabecerasOrdenes.add("Referencia");
    cabecerasOrdenes.add("Acciones");
    SearchContainer contenedorOrdenes = new SearchContainer(renderRequest, null, null,
            SearchContainer.DEFAULT_CUR_PARAM,
            ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getRequerimientosPorPagina(),
            paginaOrdenesURL, cabecerasOrdenes, "No se encontraron ordenes de compra emitidas.");
    contenedorOrdenes.setTotal(ordenes.size());
    List filasOrdenes = contenedorOrdenes.getResultRows();
    for (int i = 0; i < ordenes.size(); i++) {
        RequerimientoCompraOrden orden = ordenes.get(i);
        RequerimientoCompra requerimientoOrden = orden.getRequerimiento();
        PortletURL verOrdenURL = renderResponse.createRenderURL();
        verOrdenURL.setWindowState(LiferayWindowState.MAXIMIZED);
        verOrdenURL.setParameter("struts_action", "/compras/orden_compra");
        verOrdenURL.setParameter("id_requerimiento_compra", String.valueOf(orden.getIdRequerimientoCompra()));
        verOrdenURL.setParameter("origen_ordenes", "1");
        String referenciaOrden = requerimientoOrden.getAfiliadoNombreApellidoVisible();
        if (WebKeysCompras.isEmpty(referenciaOrden)) {
            referenciaOrden = "Compra del sector";
        }
        ResultRow filaOrden = new ResultRow(orden, String.valueOf(orden.getIdOrdenCompra()), i);
        filaOrden.addText(String.valueOf(orden.getIdOrdenCompra()), verOrdenURL);
        filaOrden.addText(String.valueOf(orden.getIdRequerimientoCompra()), verOrdenURL);
        filaOrden.addText(orden.getIdReclamoPrestacional() > 0
                ? String.valueOf(orden.getIdReclamoPrestacional()) : "", verOrdenURL);
        filaOrden.addText(HtmlUtil.escape(orden.getAltaFechaAsString()), verOrdenURL);
        filaOrden.addText(HtmlUtil.escape(requerimientoOrden.getSectorDescripcionVisible()), verOrdenURL);
        filaOrden.addText(HtmlUtil.escape(orden.getProveedor() == null ? "" : orden.getProveedor()), verOrdenURL);
        filaOrden.addText(HtmlUtil.escape(referenciaOrden), verOrdenURL);
        filaOrden.addText("Ver", verOrdenURL);
        filasOrdenes.add(filaOrden);
    }
%>
<div id="<portlet:namespace />ordenesCompraListado">
    <liferay-ui:search-iterator paginate="false" searchContainer="<%= contenedorOrdenes %>" />
</div>
<style type="text/css">
#<portlet:namespace />ordenesCompraListado .taglib-search-iterator { width: 100%; table-layout: fixed; }
#<portlet:namespace />ordenesCompraListado .taglib-search-iterator td { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>
<div class="taglib-search-iterator-page-iterator-bottom">
    <% if (paginaOrdenes > 1) {
        paginaOrdenesURL.setParameter("pagina_ordenes", String.valueOf(paginaOrdenes - 1)); %>
    <a href="<%= paginaOrdenesURL %>"><liferay-ui:message key="previous" /></a>&nbsp;&nbsp;
    <% } %>
    P&aacute;gina <%= paginaOrdenes %>
    <% if (haySiguiente) {
        paginaOrdenesURL.setParameter("pagina_ordenes", String.valueOf(paginaOrdenes + 1)); %>
    &nbsp;&nbsp;<a href="<%= paginaOrdenesURL %>"><liferay-ui:message key="next" /></a>
    <% } %>
</div>
<% }
} %>

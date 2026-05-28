<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
List<RequerimientoCompra> requerimientos =
        (List<RequerimientoCompra>) renderRequest.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA);

if (requerimientos == null) {
    requerimientos =
            (List<RequerimientoCompra>) portletSession.getAttribute(
                    WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA,
                    PortletSession.PORTLET_SCOPE
            );
}

if (requerimientos == null) {
    requerimientos = new ArrayList<RequerimientoCompra>();
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/buscar_requerimientos");

List<String> headerNames = new ArrayList<String>();
headerNames.add("id");
headerNames.add("estado");
headerNames.add("sector");
headerNames.add("cuil-titular");
headerNames.add("integrante");
headerNames.add("cargo-ospim");
headerNames.add("cargo-tercerizadora");
headerNames.add("recupero");
headerNames.add("alta-fecha");
headerNames.add("acciones");

SearchContainer searchContainer = new SearchContainer(
        renderRequest,
        null,
        null,
        SearchContainer.DEFAULT_CUR_PARAM,
        Integer.MAX_VALUE,
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

    ResultRow row = new ResultRow(req, req.getIdRequerimientoCompraString(), i);

    row.addText(HtmlUtil.escape(req.getIdString()), verURL);
    row.addText(HtmlUtil.escape(req.getEstadoDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getSectorDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getAfiliadoCuilTitularVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getAfiliadoIntString()), verURL);
    row.addText(HtmlUtil.escape(req.getCargoOspimString()) + "%", verURL);
    row.addText(HtmlUtil.escape(req.getCargoTercerizadoraString()) + "%", verURL);
    row.addText(HtmlUtil.escape(req.getRecuperoDescripcion()), verURL);
    row.addText(HtmlUtil.escape(req.getAltaFechaAsString()), verURL);

    row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/compras/editar_borrar_requerimiento.jsp");

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

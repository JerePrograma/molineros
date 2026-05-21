<%@ include file="/html/portlet/compras/init.jsp" %>

<%
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<RequerimientoCompra> requerimientos = (List<RequerimientoCompra>) renderRequest.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA);
if (requerimientos == null) {
    requerimientos = (List<RequerimientoCompra>) portletSession.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA, PortletSession.PORTLET_SCOPE);
}
if (requerimientos == null) {
    requerimientos = new ArrayList<RequerimientoCompra>();
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/buscar_requerimientos");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Numero");
headerNames.add("Fecha solicitud");
headerNames.add("Afiliado");
headerNames.add("DNI");
headerNames.add("Sector");
headerNames.add("Detalle");
headerNames.add("Estado");
headerNames.add("RP");
headerNames.add("OC");
headerNames.add("Cotizado");
headerNames.add("Recupero");
headerNames.add("Localidad");
headerNames.add("Provincia");
headerNames.add("Acciones");

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

    PortletURL editarURL = renderResponse.createRenderURL();
    editarURL.setWindowState(WindowState.MAXIMIZED);
    editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
    editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

    ResultRow row = new ResultRow(req, req.getIdRequerimientoCompraString(), i);
    row.addText(req.getNumeroString(), verURL);
    row.addText(req.getFechaSolicitudAsString(), verURL);
    row.addText(req.getAfiliado() != null ? req.getAfiliado() : "", verURL);
    row.addText(req.getDniString(), verURL);
    row.addText(req.getSectorDescripcion() != null ? req.getSectorDescripcion() : "", verURL);
    row.addText(req.getDetalleRequerimiento() != null ? req.getDetalleRequerimiento() : req.getMotivoVisible(), verURL);
    row.addText(req.getEstadoDescripcion(), verURL);
    row.addText(req.getRpNumeroString(), verURL);
    row.addText(req.getOrdenCompraNumeroString(), verURL);
    row.addText(req.getCotizadoDescripcion(), verURL);
    row.addText(req.getRecuperoDescripcion(), verURL);
    row.addText(req.getLocalidad() != null ? req.getLocalidad() : "", verURL);
    row.addText(req.getProvincia() != null ? req.getProvincia() : "", verURL);

    StringBuffer acciones = new StringBuffer();
    acciones.append("<a href=\"").append(verURL.toString()).append("\">Ver</a>");
    if (puedeABM && req.isEditable()) {
        acciones.append("&nbsp;|&nbsp;<a href=\"").append(editarURL.toString()).append("\">Editar</a>");
    }
    row.addText(acciones.toString());

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

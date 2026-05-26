<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
boolean showABMButtons = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

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
headerNames.add("numero");
headerNames.add("fecha-solicitud");
headerNames.add("sector");
headerNames.add("solicitante");
headerNames.add("cuil-titular");
headerNames.add("integrante");
headerNames.add("descripcion");
headerNames.add("estado");
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

    PortletURL editarURL = renderResponse.createRenderURL();
    editarURL.setWindowState(WindowState.MAXIMIZED);
    editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
    editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

    ResultRow row = new ResultRow(req, req.getIdRequerimientoCompraString(), i);

    row.addText(HtmlUtil.escape(req.getNumeroVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getFechaSolicitudAsString()), verURL);
    row.addText(HtmlUtil.escape(req.getSectorDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getSolicitanteVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getAfiliadoCuilTitularVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getAfiliadoInteString()), verURL);
    row.addText(HtmlUtil.escape(req.getDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getEstadoDescripcionVisible()), verURL);

    StringBuffer acciones = new StringBuffer();
    acciones.append("<a href=\"").append(verURL.toString()).append("\">Ver</a>");

    if (showABMButtons && req.isEditable()) {
        acciones.append("&nbsp;|&nbsp;<a href=\"").append(editarURL.toString()).append("\">Editar</a>");
    }

    row.addText(acciones.toString());

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

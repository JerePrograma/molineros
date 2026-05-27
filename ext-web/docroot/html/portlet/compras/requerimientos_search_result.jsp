<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
boolean showABMButtons = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean showAnularButtons =
        user != null
        && (PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS) || showABMButtons);

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

String ns = renderResponse.getNamespace();

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

    PortletURL editarURL = renderResponse.createRenderURL();
    editarURL.setWindowState(WindowState.MAXIMIZED);
    editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
    editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

    PortletURL cambiarEstadoURL = renderResponse.createActionURL();
    cambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);
    cambiarEstadoURL.setParameter("struts_action", "/compras/cambiar_estado_requerimiento");

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

    StringBuffer acciones = new StringBuffer();
    acciones.append("<a href=\"").append(verURL.toString()).append("\">Ver</a>");

    if (showABMButtons && req.isEditable()) {
        acciones.append("&nbsp;|&nbsp;<a href=\"").append(editarURL.toString()).append("\">Editar</a>");
    }

    if (showABMButtons && req.puedeCotizar()) {
        String formId = ns + "cotizar_" + req.getIdString();
        acciones.append("&nbsp;|&nbsp;");
        acciones.append("<form action=\"").append(cambiarEstadoURL.toString()).append("\" method=\"post\" id=\"")
                .append(formId).append("\" style=\"display:inline;\">");
        acciones.append("<input type=\"hidden\" name=\"").append(ns).append("id_requerimiento_compra\" value=\"")
                .append(req.getIdString()).append("\" />");
        acciones.append("<input type=\"hidden\" name=\"").append(ns).append("estado_nuevo\" value=\"")
                .append(WebKeysCompras.ESTADO_COTIZADO).append("\" />");
        acciones.append("<input type=\"button\" value=\"Cotizar\" onclick=\"submitForm(document.getElementById('")
                .append(formId).append("'));\" />");
        acciones.append("</form>");
    }

    if (showAnularButtons && req.puedeAnular()) {
        String formId = ns + "anular_" + req.getIdString();
        acciones.append("&nbsp;|&nbsp;");
        acciones.append("<form action=\"").append(cambiarEstadoURL.toString()).append("\" method=\"post\" id=\"")
                .append(formId).append("\" style=\"display:inline;\">");
        acciones.append("<input type=\"hidden\" name=\"").append(ns).append("id_requerimiento_compra\" value=\"")
                .append(req.getIdString()).append("\" />");
        acciones.append("<input type=\"hidden\" name=\"").append(ns).append("estado_nuevo\" value=\"")
                .append(WebKeysCompras.ESTADO_ANULADO).append("\" />");
        acciones.append("<input type=\"button\" value=\"Anular\" onclick=\"if(confirm('Confirma anular el requerimiento?')) submitForm(document.getElementById('")
                .append(formId).append("'));\" />");
        acciones.append("</form>");
    }

    row.addText(acciones.toString());

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

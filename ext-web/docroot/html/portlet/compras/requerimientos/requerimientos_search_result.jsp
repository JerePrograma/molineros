<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>


<%!
private String normalizarDocumentoAfiliado(String value) {
    if (value == null) {
        return "";
    }

    value = value.trim();

    if (value.length() == 0) {
        return "";
    }

    return value.replaceAll("[^0-9]", "");
}
%>

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
headerNames.add("Id");
headerNames.add("estado");
headerNames.add("sector");
headerNames.add("afiliado-nombre");
headerNames.add("afiliado-dni");
headerNames.add("tercerizadora");
headerNames.add("cargo-ospim");
headerNames.add("cargo-tercerizadora");
headerNames.add("SURGE");
headerNames.add("alta-fecha");
if (mostrarIdRpListado) {
    headerNames.add("Id RP");
}
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

    String afiliadoNombreApellido = req.getAfiliadoNombreApellidoVisible();

    if (WebKeysCompras.isEmpty(afiliadoNombreApellido)) {
        afiliadoNombreApellido = req.getAfiliadoCuilTitularVisible();

        if (!WebKeysCompras.isEmpty(req.getAfiliadoIntString())) {
            afiliadoNombreApellido += " / " + req.getAfiliadoIntString();
        }
    }

    String afiliadoDocumento = req.getAfiliadoDocumentoNroVisible();

    if (WebKeysCompras.isEmpty(afiliadoDocumento)) {
        afiliadoDocumento = req.getAfiliadoDocumentoVisible();
    }

    afiliadoDocumento = normalizarDocumentoAfiliado(afiliadoDocumento);

    RequerimientoCompraReclamoPrestacional relacionRp =
            relacionesRp.get(
                    Integer.valueOf(
                            req.getIdRequerimientoCompra()
                    )
            );

    String idRpVisible = "";

    if (relacionRp != null
            && relacionRp.isVinculado()
            && relacionRp.getIdReclamoPrestacionalInt() > 0) {

        idRpVisible =
                String.valueOf(
                        relacionRp.getIdReclamoPrestacionalInt()
                );
    }

    ResultRow row = new ResultRow(req, req.getIdRequerimientoCompraString(), i);

    row.addText(HtmlUtil.escape(req.getIdString()), verURL);

    row.addText(HtmlUtil.escape(req.getEstadoDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(req.getSectorDescripcionVisible()), verURL);
    row.addText(HtmlUtil.escape(afiliadoNombreApellido), verURL);
    row.addText(HtmlUtil.escape(afiliadoDocumento), verURL);
    row.addText(HtmlUtil.escape(req.getIdTercerizadora() != null ? req.getIdTercerizadora() : ""), verURL);
    row.addText(HtmlUtil.escape(req.getCargoOspimString()) + "%", verURL);
    row.addText(HtmlUtil.escape(req.getCargoTercerizadoraString()) + "%", verURL);
    row.addText(HtmlUtil.escape(req.getSurgeDescripcion()), verURL);
    row.addText(HtmlUtil.escape(req.getAltaFechaAsString()), verURL);

    if (mostrarIdRpListado) {
        row.addText(
                HtmlUtil.escape(
                        idRpVisible
                ),
                verURL
        );
    }

    row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/compras/requerimientos/editar_borrar_requerimiento.jsp");

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

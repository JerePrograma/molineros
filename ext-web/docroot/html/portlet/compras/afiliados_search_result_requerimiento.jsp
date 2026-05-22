<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>

<%@ include file="/html/portlet/compras/init.jsp" %>

<%!
private String safe(String value) {
    return value != null ? value : "";
}

private String js(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ");
}
%>

<%
List<Afiliado> afiliadosList = (ArrayList<Afiliado>) renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);

if (afiliadosList == null || afiliadosList.size() == 0) {
    afiliadosList = (ArrayList<Afiliado>) portletSession.getAttribute(
            WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION,
            PortletSession.APPLICATION_SCOPE
    );
}

if (afiliadosList == null || afiliadosList.size() == 0) {
    afiliadosList = (ArrayList<Afiliado>) portletSession.getAttribute(
            WebKeysAfiliados.BUSQUEDA_AFILIADO,
            PortletSession.APPLICATION_SCOPE
    );
}

if (afiliadosList == null) {
    afiliadosList = new ArrayList<Afiliado>();
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/buscar_afiliados_requerimiento");

List<String> headerNames = new ArrayList<String>();
headerNames.add("CUIL");
headerNames.add("Inte");
headerNames.add("Apellido");
headerNames.add("Nombre");
headerNames.add("Parentesco");
headerNames.add("Tipo doc.");
headerNames.add("Nro. doc.");
headerNames.add("Seccional");
headerNames.add("Id OSPIM");
headerNames.add("Vigencia");
headerNames.add("Baja");
headerNames.add("Acción");

SearchContainer searchContainer = new SearchContainer(
        renderRequest,
        null,
        null,
        SearchContainer.DEFAULT_CUR_PARAM,
        Integer.MAX_VALUE,
        portletURL,
        headerNames,
        "No se encontraron afiliados."
);

searchContainer.setTotal(afiliadosList.size());

List resultRows = searchContainer.getResultRows();

for (int i = 0; i < afiliadosList.size(); i++) {
    Afiliado afiliado = afiliadosList.get(i);

    String cuilTitular = safe(afiliado.getCuil_titular());
    String inte = safe(afiliado.getInteAsString());
    String apellido = safe(afiliado.getApellido());
    String nombre = safe(afiliado.getNombre());
    String apellidoNombre = safe(afiliado.getApellidoNombre());
    String documento = safe(afiliado.getDocu_numero());

    ResultRow row = new ResultRow(afiliado, cuilTitular + "_" + inte, i);

    row.addText(HtmlUtil.escape(afiliado.getCuil_titularMasked()));
    row.addText(HtmlUtil.escape(inte));
    row.addText(HtmlUtil.escape(apellido));
    row.addText(HtmlUtil.escape(nombre));
    row.addText(HtmlUtil.escape(safe(afiliado.getParentesco())));
    row.addText(HtmlUtil.escape(safe(afiliado.getDocumento_tipo())));
    row.addText(HtmlUtil.escape(documento));

    row.addText(
            HtmlUtil.escape(
                    afiliado.getSeccional() != null && afiliado.getSeccional().getDescripcion() != null
                            ? afiliado.getSeccional().getDescripcion()
                            : "Sin especificar"
            )
    );

    row.addText(String.valueOf(afiliado.getId_ospim()));
    row.addText(HtmlUtil.escape(afiliado.getVigen_fechaAsString()));
    row.addText(HtmlUtil.escape(afiliado.getBaja_fechaAsString()));

    StringBuffer accion = new StringBuffer();

    accion.append("<a href=\"javascript:");
    accion.append("<portlet:namespace />seleccionarAfiliadoRequerimiento('");
    accion.append(js(cuilTitular));
    accion.append("','");
    accion.append(js(inte));
    accion.append("','");
    accion.append(js(apellidoNombre));
    accion.append("','");
    accion.append(js(documento));
    accion.append("');\">Seleccionar</a>");

    row.addText(accion.toString());

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
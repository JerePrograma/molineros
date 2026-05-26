<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>

<%@ include file="/html/portlet/compras/init.jsp" %>

<%!
private String safe(String value) {
    return value != null ? value : "";
}

/**
 * Escapa valores para insertarlos dentro de strings JavaScript.
 * Importante: no deja comillas crudas porque este valor termina dentro de un atributo HTML.
 */
private String js(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\x27")
            .replace("\"", "\\x22")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E")
            .replace("&", "\\x26");
}
%>

<%
List<Afiliado> afiliadosList = null;

Object afiliadosAttr = renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
if (afiliadosAttr instanceof List) {
    afiliadosList = (List<Afiliado>) afiliadosAttr;
}

if (afiliadosList == null || afiliadosList.size() == 0) {
    Object afiliadosSesion = portletSession.getAttribute(
            WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION,
            PortletSession.APPLICATION_SCOPE
    );

    if (afiliadosSesion instanceof List) {
        afiliadosList = (List<Afiliado>) afiliadosSesion;
    }
}

if (afiliadosList == null || afiliadosList.size() == 0) {
    Object busquedaSesion = portletSession.getAttribute(
            WebKeysAfiliados.BUSQUEDA_AFILIADO,
            PortletSession.APPLICATION_SCOPE
    );

    if (busquedaSesion instanceof List) {
        afiliadosList = (List<Afiliado>) busquedaSesion;
    }
}

if (afiliadosList == null) {
    afiliadosList = new ArrayList<Afiliado>();
}

String namespace = renderResponse.getNamespace();

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
    String cuilTitularMasked = safe(afiliado.getCuil_titularMasked());
    String inte = safe(afiliado.getInteAsString());
    String apellido = safe(afiliado.getApellido());
    String nombre = safe(afiliado.getNombre());
    String apellidoNombre = safe(afiliado.getApellidoNombre());
    String documento = safe(afiliado.getDocu_numero());
    String parentesco = safe(afiliado.getParentesco());
    String tipoDocumento = safe(afiliado.getDocumento_tipo());
    String vigencia = safe(afiliado.getVigen_fechaAsString());
    String baja = safe(afiliado.getBaja_fechaAsString());

    String seccionalDescripcion = "Sin especificar";
    if (afiliado.getSeccional() != null && afiliado.getSeccional().getDescripcion() != null) {
        seccionalDescripcion = afiliado.getSeccional().getDescripcion();
    }

    ResultRow row = new ResultRow(afiliado, cuilTitular + "_" + inte, i);

    row.addText(HtmlUtil.escape(cuilTitularMasked));
    row.addText(HtmlUtil.escape(inte));
    row.addText(HtmlUtil.escape(apellido));
    row.addText(HtmlUtil.escape(nombre));
    row.addText(HtmlUtil.escape(parentesco));
    row.addText(HtmlUtil.escape(tipoDocumento));
    row.addText(HtmlUtil.escape(documento));
    row.addText(HtmlUtil.escape(seccionalDescripcion));
    row.addText(String.valueOf(afiliado.getId_ospim()));
    row.addText(HtmlUtil.escape(vigencia));
    row.addText(HtmlUtil.escape(baja));

    StringBuffer accion = new StringBuffer();

    accion.append("<a href=\"javascript:void(0);\" onclick=\"");
    accion.append(namespace);
    accion.append("seleccionarAfiliadoRequerimiento('");
    accion.append(js(cuilTitular));
    accion.append("','");
    accion.append(js(inte));
    accion.append("','");
    accion.append(js(apellidoNombre));
    accion.append("','");
    accion.append(js(documento));
    accion.append("'); return false;\">Seleccionar</a>");

    row.addText(accion.toString());

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
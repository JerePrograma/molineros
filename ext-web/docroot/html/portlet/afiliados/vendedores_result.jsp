<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.liferay.portal.kernel.dao.search.*" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%
List<Map<String,Object>> resultados = (List<Map<String,Object>>) request.getAttribute("resultados");
if (resultados == null) resultados = new ArrayList<Map<String,Object>>();

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

String editIcon = themeDisplay.getPathThemeImages() + "/common/edit.png";
String deleteIcon = themeDisplay.getPathThemeImages() + "/common/delete.png";
String bajaIcon = themeDisplay.getPathThemeImages() + "/common/close.png";

%>

<form action="" method="post" name="<portlet:namespace />fmEliminar"></form>

<% if (resultados.isEmpty()) { %>
    <div class="portlet-msg-info" style="margin-top:10px;">
        No se encontraron resultados.
    </div>
<% } else { %>

<%
PortletURL portletURL = renderResponse.createRenderURL();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nombre");
headerNames.add("Apellido");
headerNames.add("DNI");
headerNames.add("Horario");
headerNames.add("Motivo");
headerNames.add("Baja fecha");
headerNames.add("Acciones");

SearchContainer searchContainer = new SearchContainer(
    renderRequest, null, null,
    SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
    portletURL, headerNames,
    LanguageUtil.get(pageContext, "no-results-were-found")
);

searchContainer.setTotal(resultados.size());
List<ResultRow> resultRows = searchContainer.getResultRows();

String fNombre = ParamUtil.getString(request, "nombre", "");
String fApellido = ParamUtil.getString(request, "apellido", "");
String fDni = ParamUtil.getString(request, "dni", "");
%>

<portlet:renderURL var="volverVendedoresURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/afiliados/view" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="autoBuscar" value="1" />
    <portlet:param name="nombre" value="<%= fNombre %>" />
    <portlet:param name="apellido" value="<%= fApellido %>" />
    <portlet:param name="dni" value="<%= fDni %>" />
</portlet:renderURL>

<%
for (int i = 0; i < resultados.size(); i++) {
    Map<String,Object> r = resultados.get(i);

    String idStr = String.valueOf(r.get("id"));
    String nombre = Validator.isNotNull(r.get("nombre")) ? String.valueOf(r.get("nombre")) : "";
    String apellido = Validator.isNotNull(r.get("apellido")) ? String.valueOf(r.get("apellido")) : "";
    String dni = Validator.isNotNull(r.get("dni")) ? String.valueOf(r.get("dni")) : "";

    String horaDesde = Validator.isNotNull(r.get("hora_desde")) ? String.valueOf(r.get("hora_desde")) : "";
    String horaHasta = Validator.isNotNull(r.get("hora_hasta")) ? String.valueOf(r.get("hora_hasta")) : "";

    if (horaDesde.length() >= 5) horaDesde = horaDesde.substring(0, 5);
    if (horaHasta.length() >= 5) horaHasta = horaHasta.substring(0, 5);

    String horario = "";
    if (Validator.isNotNull(horaDesde) && Validator.isNotNull(horaHasta)) {
        horario = horaDesde + " - " + horaHasta;
    }

    String motivo = Validator.isNotNull(r.get("motivo")) ? String.valueOf(r.get("motivo")) : "";
    String bajaFecha = "";
    Object bf = r.get("baja_fecha");
    if (bf instanceof Timestamp) {
        bajaFecha = sdf.format((Timestamp) bf);
    } else if (bf != null) {
        bajaFecha = String.valueOf(bf);
    }

    PortletURL editarURL = renderResponse.createRenderURL();
    editarURL.setWindowState(LiferayWindowState.MAXIMIZED);
    editarURL.setParameter("struts_action", "/afiliados/vendedor");
    editarURL.setParameter("tabs1", "vendedores");
    editarURL.setParameter("cmd", "editar");
    editarURL.setParameter("id", idStr);
    editarURL.setParameter("nombre", fNombre);
    editarURL.setParameter("apellido", fApellido);
    editarURL.setParameter("dni", fDni);

    PortletURL bajaURL = renderResponse.createActionURL();
    bajaURL.setParameter("struts_action", "/afiliados/vendedor");
    bajaURL.setParameter("tabs1", "vendedores");
    bajaURL.setParameter("cmd", "eliminar");
    bajaURL.setParameter("id", idStr);
    bajaURL.setParameter("redirect", volverVendedoresURL.toString());

    boolean dadoDeBaja = Validator.isNotNull(bajaFecha);

    String acciones = "";

    if (dadoDeBaja) {
        acciones =
            "<img src='" + bajaIcon + "' alt='Dado de baja' title='Dado de baja' />";
    } else {
        acciones =
            "<a href='" + editarURL.toString() + "' title='Editar'>" +
            "<img src='" + editIcon + "' alt='Editar' />" +
            "</a>&nbsp;&nbsp;" +
            "<a href='javascript:void(0);' onclick=\"" + renderResponse.getNamespace() + "confirmarBaja('" + bajaURL.toString() + "');\" title='Eliminar'>" +
            "<img src='" + deleteIcon + "' alt='Eliminar' />" +
            "</a>";
    }

    ResultRow row = new ResultRow(r, idStr, i);
    row.addText(nombre);
    row.addText(apellido);
    row.addText(dni);
    row.addText(horario);
    row.addText(motivo);
    row.addText(bajaFecha);
    row.addText(acciones);

    resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<% } %>

<script type="text/javascript">
function <portlet:namespace />confirmarBaja(url) {
    if (confirm('¿Estás seguro de eliminar?')) {
        var form = document.<portlet:namespace />fmEliminar;
        form.action = url;
        form.submit();
    }
}
</script>
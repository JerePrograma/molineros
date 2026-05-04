<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<portlet:defineObjects />


<%
String fNombre = ParamUtil.getString(
    renderRequest, "nombre",
    (String)renderRequest.getPortletSession().getAttribute("ven_nombre")
);
if (fNombre == null) fNombre = "";

String fApellido = ParamUtil.getString(
    renderRequest, "apellido",
    (String)renderRequest.getPortletSession().getAttribute("ven_apellido")
);
if (fApellido == null) fApellido = "";

String fDni = ParamUtil.getString(
    renderRequest, "dni",
    (String)renderRequest.getPortletSession().getAttribute("ven_dni")
);
if (fDni == null) fDni = "";


String autoBuscar = ParamUtil.getString(renderRequest, "autoBuscar", "");

%>

<liferay-ui:success key="vendedor_guardado_ok" message="El vendedor fue guardado correctamente." />
<liferay-ui:error key="vendedor_guardado_error" message="No se pudo guardar el vendedor." />

<liferay-ui:success key="vendedor_baja_ok" message="El vendedor fue dado de baja correctamente." />
<liferay-ui:error key="vendedor_baja_error" message="No se pudo dar de baja el vendedor." />

<liferay-ui:success key="historico_guardado_ok" message="El histórico fue guardado correctamente." />
<liferay-ui:error key="historico_guardado_error" message="No se pudo guardar el histórico." />

<liferay-ui:error
    key="vendedor_no_se_puede_baja_con_solicitudes"
    message="No se puede dar de baja el vendedor porque tiene solicitudes asignadas." />

<liferay-ui:error
    key="historico_no_permitido_con_solicitudes_en_periodo"
    message="No se puede cargar licencia o vacaciones en ese período porque el vendedor tiene una solicitud asignada." />
    
<div align="center" id="<portlet:namespace />vendedoresBuscando" style="display:none;">
    <table>
        <tr>
            <td><liferay-ui:message key="buscando" /></td>
            <td>
                <img alt="<liferay-ui:message key='buscando' />"
                     src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
            </td>
        </tr>
    </table>
</div>

<portlet:renderURL var="buscarVendedoresURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/afiliados/vendedor" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="cmd" value="buscar" />
</portlet:renderURL>

<portlet:renderURL var="nuevoVendedorURL">
    <portlet:param name="struts_action" value="/afiliados/vendedor" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="cmd" value="nuevo" />
</portlet:renderURL>

<fieldset class="block-labels">
    <legend>Vendedores</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Nombre</label></td>
            <td>
                <input type="text" id="<portlet:namespace />nombre" value="<%= fNombre %>" />
            </td>

            <td><label>Apellido</label></td>
            <td>
                <input type="text" id="<portlet:namespace />apellido" value="<%= fApellido %>" />
            </td>

            <td><label>DNI</label></td>
            <td>
                <input type="text" id="<portlet:namespace />dni" value="<%= fDni %>" />
            </td>

            <td>
                <input type="button" value="Buscar" onclick="<portlet:namespace />buscarVendedores();" />
                <input type="button" value="Nuevo" onclick="location.href='<%= nuevoVendedorURL %>';" />
            </td>
        </tr>
    </table>
</fieldset>

<br/>

<div id="<portlet:namespace />vendedoresResultados">
   <% if (renderRequest.getAttribute("resultados") != null) { %>
        <liferay-util:include page="/html/portlet/afiliados/vendedores_result.jsp" />
    <% } else { %>
        <div class="portlet-msg-info">Ingresá filtros y presioná <b>Buscar</b>.</div>
    <% } %>
</div>

<script type="text/javascript">
function <portlet:namespace />buscarVendedores() {
    var nombre = jQuery('#<portlet:namespace />nombre').val();
    var apellido = jQuery('#<portlet:namespace />apellido').val();
    var dni = jQuery('#<portlet:namespace />dni').val();

    var url = '${buscarVendedoresURL}';
    url += '&<portlet:namespace />nombre=' + encodeURIComponent(nombre || '');
    url += '&<portlet:namespace />apellido=' + encodeURIComponent(apellido || '');
    url += '&<portlet:namespace />dni=' + encodeURIComponent(dni || '');
    url += '&rnd=' + Math.floor(Math.random() * 100000);

    jQuery('#<portlet:namespace />vendedoresBuscando').show();

    jQuery('#<portlet:namespace />vendedoresResultados').load(url, function() {
        jQuery('#<portlet:namespace />vendedoresBuscando').hide();
    });
}

jQuery(function() {
    var autoBuscar = '<%= autoBuscar %>';
    if (autoBuscar === '1') {
        <portlet:namespace />buscarVendedores();
    }
});
</script>
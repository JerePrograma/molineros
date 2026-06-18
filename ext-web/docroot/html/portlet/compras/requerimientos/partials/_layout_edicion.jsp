<%@ include file="/html/portlet/compras/requerimientos/partials/_estilos.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_mensajes.jsp" %>

<% if (modoEditable) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_form_hidden.jsp" %>
<% } %>

<div id="<portlet:namespace />compras_layout"
     class="<%= !modoEditable ? "compras-modo-vista" : "" %>">

    <%@ include file="/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp" %>

    <% if (puedeEditarEstructuraPantalla) { %>
        <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_editable.jsp" %>
    <% } else { %>
        <%@ include file="/html/portlet/compras/requerimientos/partials/_afiliado_readonly.jsp" %>
    <% } %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/_observaciones.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_adjuntos.jsp" %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_botonera.jsp" %>
</div>

<%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_comunes.jsp" %>

<% if (modoEditable) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_edicion.jsp" %>
<% } %>

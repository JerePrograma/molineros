<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ include file="/html/portlet/compras/requerimientos/partials/_detalle_modelo.jsp" %>

<fieldset class="block-labels">
    <legend>Detalle del requerimiento</legend>

    <% if (puedeABMDetalle) { %>
        <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp" %>
        <br />
    <% } %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle_tabla.jsp" %>

    <%
    RequerimientoCompra req = reqDetalle;
    boolean puedeEditarEstructuraPantalla = puedeABMDetalle;
    %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/_observaciones.jsp" %>
</fieldset>

<%@ include file="/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp" %>

<% if (puedeABMDetalle || puedeCotizarDetalle) { %>
    <%@ include file="/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp" %>
<% } %>
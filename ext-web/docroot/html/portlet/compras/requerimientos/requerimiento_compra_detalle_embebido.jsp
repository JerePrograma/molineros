<%--
Responsabilidad:
    Ensambla modelo, editor, tabla y scripts del detalle reutilizable.
Incluido desde:
    requerimiento_compra_detalle_componente.jsp
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_modelo_componente.jsp" %>

<fieldset class="block-labels compras-seccion compras-seccion-detalle">
    <legend>Detalle del requerimiento</legend>

    <% if (puedeABMDetalle) { %>
        <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_editor_componente.jsp" %>
    <% } %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_tabla_componente.jsp" %>

    <%
    RequerimientoCompra req = reqDetalle;
    boolean puedeEditarEstructuraPantalla = puedeABMDetalle;
    %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_observaciones_componente.jsp" %>
</fieldset>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_scripts_base_componente.jsp" %>

<% if (puedeABMDetalle
        || puedeCotizarDetalle
        || puedeEliminarDetalle) { %>

    <%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_scripts_edicion_componente.jsp" %>

<% } %>
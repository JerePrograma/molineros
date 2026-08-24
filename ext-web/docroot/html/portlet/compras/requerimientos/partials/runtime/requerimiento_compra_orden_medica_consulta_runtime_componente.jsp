<%--
Responsabilidad:
    Recupera atributos request e incluye la consulta de Órdenes Médicas.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Consulta y estados de solo lectura.
Entradas requeridas:
    Atributos request publicados por requerimiento_compra_contexto_publicacion_componente.jsp.
Atributos de request consumidos:
    Claves compras.requerimiento.* leídas por requerimiento_compra_runtime_inicializacion_componente.jsp.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>

<%-- Requiere compras.requerimiento.req. --%>
<%
RequerimientoCompra req = (RequerimientoCompra) request.getAttribute("compras.requerimiento.req");
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_medica_consulta_componente.jsp" %>

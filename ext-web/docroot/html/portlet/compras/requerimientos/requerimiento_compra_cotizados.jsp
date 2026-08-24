<%--
Responsabilidad:
    Renderiza el listado de requerimientos cotizados.
Incluido desde:
    Forward, Action o entry point directo en: view.jsp.
Pantallas o estados de uso:
    Consulta y estados de solo lectura.
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
<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_COTIZADO)
);

request.setAttribute(
        WebKeysCompras.COTIZADOS_INCLUYE_RECLAMO_RP,
        Boolean.TRUE
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" %>

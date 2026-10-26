<%--
Responsabilidad:
    Reutiliza el listado para consultar los requerimientos en Orden de Compra.
Referencia legacy:
    requerimiento_compra_cotizados.jsp: estado forzado en el listado existente.
    Liquidaciones view.jsp: solapas que reutilizan un listado con filtro fijo.
--%>
<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_ORDEN_COMPRA)
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" %>

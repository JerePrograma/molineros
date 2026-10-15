<%--
Responsabilidad:
    Reutiliza el listado para revisar los requerimientos Para Autorizar.
Referencia legacy:
    requerimiento_compra_cotizados.jsp: estado forzado en el listado existente.
    Liquidaciones reintegros/editar_reintegro.jsp: autorizacion por rol y estado.
--%>
<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_PARA_AUTORIZAR)
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" %>

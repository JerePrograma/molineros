<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_PENDIENTE)
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>

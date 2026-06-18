<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_COTIZADO)
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>

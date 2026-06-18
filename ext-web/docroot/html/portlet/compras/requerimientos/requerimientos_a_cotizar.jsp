<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_A_COTIZAR)
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>

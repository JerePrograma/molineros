<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_A_COTIZAR)
);

request.setAttribute(
        "COMPRAS_MODO_REQUERIMIENTO",
        Boolean.FALSE
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>
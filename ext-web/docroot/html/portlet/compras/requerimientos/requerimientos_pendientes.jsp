<%
request.setAttribute(
        "COMPRAS_ESTADO_FORZADO",
        String.valueOf(WebKeysCompras.ESTADO_PENDIENTE)
);

request.setAttribute(
        "COMPRAS_MODO_REQUERIMIENTO",
        Boolean.TRUE
);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>
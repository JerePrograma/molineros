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

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>

<%
request.setAttribute("COMPRAS_ESTADO_FORZADO", String.valueOf(WebKeysCompras.ESTADO_COTIZACIONES));
request.setAttribute("COMPRAS_MODO_ORDENES", Boolean.TRUE);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>
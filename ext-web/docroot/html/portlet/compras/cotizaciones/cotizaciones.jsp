<%
request.setAttribute("COMPRAS_ESTADO_FORZADO", String.valueOf(WebKeysCompras.ESTADO_AUTORIZADO));
request.setAttribute("COMPRAS_MODO_COTIZACION", Boolean.TRUE);
%>

<%@ include file="/html/portlet/compras/requerimientos/requerimientos.jsp" %>
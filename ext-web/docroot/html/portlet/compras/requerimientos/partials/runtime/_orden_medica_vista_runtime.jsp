<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>

<%-- Requiere compras.requerimiento.req. --%>
<%
RequerimientoCompra req = (RequerimientoCompra) request.getAttribute("compras.requerimiento.req");
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/_orden_medica_vista.jsp" %>

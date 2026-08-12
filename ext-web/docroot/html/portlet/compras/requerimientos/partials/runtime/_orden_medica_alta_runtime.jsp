<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>

<%-- Requiere esNuevo y modoEditable. --%>
<%
boolean esNuevo = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.esNuevo"));
boolean modoEditable = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.modoEditable"));
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/_orden_medica_alta.jsp" %>

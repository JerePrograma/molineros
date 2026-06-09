<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
request.setAttribute("MODO_REQUERIMIENTO_COMPRA", "VISTA");
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/_modelo_requerimiento.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/_layout_vista.jsp" %>

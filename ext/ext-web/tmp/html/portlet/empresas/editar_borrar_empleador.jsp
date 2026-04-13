<%@ include file="/html/portlet/afiliados/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
EntidadPadronUnificado empresa=(EntidadPadronUnificado)row.getObject();
String cuit = empresa.getCuit();
String sucur = empresa.getSucursal();
%>

	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" />		
		<portlet:param name="cuit" value="<%= cuit %>" />
		<portlet:param name="sucu" value="<%= sucur %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />


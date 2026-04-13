<%@ include file="/html/portlet/afiliados/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Empresa empresa=(Empresa)row.getObject();
String cuit = empresa.getCuit();
String sucur = empresa.getSucursal();
%>

<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" />		
		<portlet:param name="cuit" value="<%= cuit %>" />
		<portlet:param name="sucu" value="<%= sucur %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<c:if test="<%= Validator.isNull(empresa.getBaja_fecha()) || (Validator.isNotNull(empresa.getBaja_fecha()) && empresa.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/afiliados/borrar_empleadores_entry" />		
		<portlet:param name="cuit" value="<%= cuit %>" />
		<portlet:param name="sucu" value="<%= sucur %>" />
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	</c:if>
	<c:if test="<%= Validator.isNotNull(empresa.getBaja_fecha()) && empresa.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
		<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="reactivaURL">
			<portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" />		
			<portlet:param name="cuit" value="<%= cuit %>" />
			<portlet:param name="sucu" value="<%= sucur %>" />
			<portlet:param name="reactivar" value="true" />
		</portlet:renderURL>
		<liferay-ui:icon image="add" url="<%=reactivaURL%>" />
	</c:if>
</liferay-ui:icon-menu>

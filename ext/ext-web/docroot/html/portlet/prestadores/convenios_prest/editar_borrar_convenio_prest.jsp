<%@ include file="/html/portlet/prestadores/convenios_prest/init.jsp" %>

<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_CONVENIO_PREST);

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ConvenioPrestacional convenioPrest=(ConvenioPrestacional)row.getObject();
String id_convenio_prest = String.valueOf(convenioPrest.getId());
%>
<c:if test="<%= Validator.isNull(convenioPrest.getBajaFecha())%>">
<c:if test="<%= showABMButtons %>">
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/liquidaciones/editar_convenio_prest_entry" />
		<portlet:param name="id_convenio_prest" value="<%=id_convenio_prest%>" />
		<portlet:param name="cmd" value="<%= Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
<%-- 	<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/liquidaciones/editar_convenio_prest_entry" />
		<portlet:param name="cmd" value="<%= Constants.DELETE %>" />
		<portlet:param name="id_convenio_prest" value="<%=id_convenio_prest%>" />
	</portlet:actionURL>
	<liferay-ui:icon-delete url="<%= deleteURL %>" /> --%>
		<%	
		String deleteURL="javascript:if(confirm('Est�s seguro que lo deseas dar de baja?')) { eliminarConvenioPrest('"+id_convenio_prest+"');}";		
		%>
		<liferay-ui:icon image="delete" url="<%= deleteURL %>"/>	
</liferay-ui:icon-menu>
</c:if>
</c:if>
<c:if test="<%= Validator.isNotNull(convenioPrest.getBajaFecha())%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="dado-de-baja"
	/>
</c:if>
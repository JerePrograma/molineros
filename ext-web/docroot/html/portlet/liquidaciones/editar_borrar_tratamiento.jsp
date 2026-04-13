<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<% 
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Reintegro reintegro=(Reintegro)row.getObject();
String id_reintegro = reintegro.getId_reintegroString();

%>
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_entry" />		
		<portlet:param name="id_reintegro" value="<%=id_reintegro%>" />	
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
	<c:if test="<%= showABMButtons %>">
		<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
			<portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_entry" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="numero" value="<%=id_reintegro%>" />		
		</portlet:actionURL>
		<liferay-ui:icon-delete url="<%= deleteURL %>" />
	</c:if>	
</liferay-ui:icon-menu>
<c:if test="<%= (Validator.isNotNull(reintegro.getBaja_fecha()) && reintegro.getBaja_fecha().getTime()<System.currentTimeMillis())%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="reintegro-dado-de-baja"			
	/>
</c:if>
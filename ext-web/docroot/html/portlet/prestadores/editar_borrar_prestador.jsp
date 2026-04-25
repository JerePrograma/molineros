<%@ include file="/html/portlet/prestadores/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Prestador prestador=(Prestador)row.getObject();
String id = String.valueOf(prestador.getId_prestador());
%>
<c:if test="<%= Validator.isNull(prestador.getBaja_fecha()) || (Validator.isNotNull(prestador.getBaja_fecha()) && prestador.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/liquidaciones/editar_prestadores_entry" />		
		<portlet:param name="prestador_id" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/liquidaciones/borrar_prestadores_entry" />		
		<portlet:param name="prestador_id" value="<%= id %>" />
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(prestador.getBaja_fecha()) && prestador.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="empleador-dado-de-baja"			
		/>
</c:if>

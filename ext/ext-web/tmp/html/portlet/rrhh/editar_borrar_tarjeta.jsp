<%@ include file="/html/portlet/rrhh/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
TarjetaAcceso  tarjeta  =(TarjetaAcceso)row.getObject();
String id = String.valueOf(tarjeta.getId());
String url = "javascript:imprimir('"+ id +"')";

%>
<c:if test="<%= Validator.isNull(tarjeta.getBaja_fecha()) %>"> 

<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/rrhh/editar_borrar_tarjetas_entry" />		
		<portlet:param name="id_registro_tarjeta" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/rrhh/editar_borrar_tarjetas_entry" />		
		<portlet:param name="id_registro_tarjeta" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
</liferay-ui:icon-menu>

</c:if>


<c:if test="<%= Validator.isNotNull(tarjeta.getBaja_fecha()) && tarjeta.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="equipo-dado-de-baja"			
		/>
</c:if>



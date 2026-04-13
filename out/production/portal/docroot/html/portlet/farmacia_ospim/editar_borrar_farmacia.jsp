<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Farmacia  farmacia  =(Farmacia )row.getObject();
String id = String.valueOf(farmacia.getId_farmacia() );

%>
<c:if test="<%= Validator.isNull(farmacia.getBajaFecha()  ) %>">

<liferay-ui:icon-menu align="left">


	
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_farmacia_entry" />		
		<portlet:param name="id_registro_farmacia" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_farmacia_entry" />		
		<portlet:param name="id_registro_farmacia" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	
	
</liferay-ui:icon-menu>

</c:if>


<c:if test="<%= Validator.isNotNull(farmacia.getBajaFecha() ) && farmacia.getBajaFecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="farmacia-dada-de-baja"			
		/>
</c:if>


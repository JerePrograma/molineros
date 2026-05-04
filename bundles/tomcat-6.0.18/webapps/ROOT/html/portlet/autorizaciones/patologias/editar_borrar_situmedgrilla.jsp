<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<% 
int id ;
id= 290;

//boolean  estadoCargado = situacion.getEstadoRegSitMedica().equals("CARGADO"); 
%>

<liferay-ui:icon-menu align="left">

	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_borrar_situacionmedica_entry" />		
		<portlet:param name="id_registro_sitmed" value="<%= id %>" />
		
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_borrar_situacionmedica_entry" />		
		<portlet:param name="id_registro_sitmed" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	
	
</liferay-ui:icon-menu>




<c:if test="<%= Validator.isNotNull(situacion.getBaja_fecha()) && situacion.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="situacion-dado-de-baja"			
		/>
</c:if>



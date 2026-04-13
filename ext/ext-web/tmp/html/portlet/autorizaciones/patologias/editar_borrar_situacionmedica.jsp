<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
SituacionMedica  situacion  =(SituacionMedica )row.getObject();
String id = String.valueOf(situacion.getId_String());
String cuilTitular = situacion.getAfiliado().getCuil_titular();
String inte = String.valueOf(situacion.getAfiliado().getInte() );
String url = "javascript:imprimir('"+ id +"')";


//boolean  estadoCargado = situacion.getEstadoRegSitMedica().equals("CARGADO"); 
%>
<c:if test="<%= Validator.isNull(situacion.getBaja_fecha()) %>">

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

</c:if>

<c:if test="<%= Validator.isNotNull(situacion.getBaja_fecha()) && situacion.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="medicamento-dado-de-baja"			
		/>
</c:if>



<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Vademecum   vademecum  =(Vademecum )row.getObject();
String id = String.valueOf(vademecum.getRegistro());
String idTroquel= String.valueOf(vademecum.getTroquel() );
String url = "javascript:imprimir('"+ id +"')";

%>
<c:if test="<%= Validator.isNull(vademecum.getBaja_fecha() ) %>">



<liferay-ui:icon-menu align="left">
							
    
	
	<% if ( vademecum.getOrigenDeLosDatos()==null || ( vademecum.getOrigenDeLosDatos()!=null &&  ! vademecum.getOrigenDeLosDatos().equals("SSS")) ) {%>
		<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
				<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_vademecum_entry" />		
				<portlet:param name="id_registro_vade" value="<%= id %>" />
				<portlet:param name="id_troquel_vade" value="<%= idTroquel %>" />
				<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
		</portlet:renderURL>
		<liferay-ui:icon image="edit" url="<%= editURL %>" />
		<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
				<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_vademecum_entry" />		
				<portlet:param name="id_registro_vade" value="<%= id %>" />
				<portlet:param name="id_troquel_vade" value="<%= idTroquel %>" />
				<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
		</portlet:renderURL>
		<liferay-ui:icon-delete url="<%=deleteURL%>" />
	<%} %>
</liferay-ui:icon-menu>

</c:if>

<c:if test="<%= Validator.isNotNull(vademecum.getBaja_fecha()) && vademecum.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="medicamento-dado-de-baja"			
		/>
</c:if>



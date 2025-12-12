<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
EquipoInterdisciplinario  equipo =(EquipoInterdisciplinario )row.getObject();
String id = String.valueOf(equipo.getId_registroEquipoInter());
String url = "javascript:imprimir('"+ id +"')";
boolean  estadoCargado = equipo.getEstadoRegEquipoInter().equals("CARGADO");
boolean  estadoCerrado = equipo.getEstadoRegEquipoInter().equals("CERRADO");
estadoCargado=true;
%>
<c:if test="<%= Validator.isNull(equipo.getBaja_fecha()) %>">

<liferay-ui:icon-menu align="left">

<c:if test="<%= ( Validator.isNotNull(equipo.getEstadoRegEquipoInter()) && estadoCargado     )%>">
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_borrar_equipointerdisciplinario_entry" />		
		<portlet:param name="id_registro_eq" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_borrar_equipointerdisciplinario_entry" />		
		<portlet:param name="id_registro_eq" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	
</c:if>	
<c:if test="<%= ( Validator.isNotNull(equipo.getEstadoRegEquipoInter()) && estadoCerrado     )%>">
	<liferay-ui:icon image="print" url="<%= url %>" message="Imprimir"/>
</c:if>		
	
</liferay-ui:icon-menu>

</c:if>


<c:if test="<%= Validator.isNotNull(equipo.getBaja_fecha()) && equipo.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="equipo-dado-de-baja"			
		/>
</c:if>



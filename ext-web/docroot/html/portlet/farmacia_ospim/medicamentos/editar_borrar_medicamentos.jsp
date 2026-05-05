<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Medicamento  medicamento =(Medicamento )row.getObject();
String id = String.valueOf(medicamento.getId_medicamento());
String url = "javascript:imprimir('"+ id +"')";
//boolean  estadoCargado = medicamento.getEstadoRegMedicamento().equals("CARGADO");
boolean  estadoCargado;
estadoCargado=true;
%>
<c:if test="<%= Validator.isNull(medicamento.getFecha_baja() ) %>">

<liferay-ui:icon-menu align="left">

<c:if test="<%=(  estadoCargado     )%>">
	<c:if test="<%=(  !medicamento.getManualDat()     )%>">
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_medicamentos_entry" />		
		<portlet:param name="id_registro_med" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
	</c:if>
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_medicamentos_entry" />		
		<portlet:param name="id_registro_med" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	
</c:if>	
	<%-- <liferay-ui:icon image="print" url="<%= url %>" message="Imprimir"/> --%>
	
</liferay-ui:icon-menu>

</c:if>


<c:if test="<%= Validator.isNotNull(medicamento.getFecha_baja()) && medicamento.getFecha_baja().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="medicamento-dado-de-baja"			
		/>
</c:if>



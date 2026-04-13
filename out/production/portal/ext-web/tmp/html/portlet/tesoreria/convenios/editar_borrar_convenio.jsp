<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Convenio convenio=(Convenio)row.getObject();
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}

if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio";
}
%>
<c:if test="<%= Validator.isNull(convenio.getBaja_fecha()) || (Validator.isNotNull(convenio.getBaja_fecha()) && convenio.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">
	<%if(portlet_name.equals("tesoreria")){%>
	<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/tesoreria/view_convenios_entry" />
		<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.UPDATE%>" />
		<portlet:param name="convenio_id" value="<%= String.valueOf(convenio.getId()) %>" />		
	</portlet:actionURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<%}%>

	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
	<%if(portlet_name.equals("uoma")) {%>
		<portlet:param name="struts_action" value="/estudio_isidro/borrar_convenios_entry" />
	<%}else{%>
		<portlet:param name="struts_action" value="/tesoreria/borrar_convenios_entry" />
	<%}%>
		<portlet:param name="id" value="<%= String.valueOf(convenio.getId()) %>" />		
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(convenio.getBaja_fecha()) && convenio.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="convenio-dado-de-baja"			
		/>
</c:if>

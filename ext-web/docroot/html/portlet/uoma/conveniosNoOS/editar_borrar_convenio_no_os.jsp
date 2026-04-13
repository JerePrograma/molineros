<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "estudio_isidro";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
} 
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
} 

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Convenio convenio=(Convenio)row.getObject();
//String urlBorrar="/"+portlet_name+"/borrar_convenios_no_os_entry";
String urlAnular="javascript:anularConvenio('"+String.valueOf(convenio.getId())+"')";
String urlEdit="/"+portlet_name+"/view_convenios_no_os_entry";

%>
<c:if test="<%= Validator.isNull(convenio.getBaja_fecha()) || (Validator.isNotNull(convenio.getBaja_fecha()) && convenio.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">	
	<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="<%=urlEdit%>" />
		<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.UPDATE%>" />
		<portlet:param name="convenio_id" value="<%= String.valueOf(convenio.getId()) %>" />		
	</portlet:actionURL>	
	
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
	<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=urlAnular%>" />
	
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(convenio.getBaja_fecha()) && convenio.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="convenio-dada-de-baja-click-cambiar-fecha-reactivar"	
			url="<%=urlAnular%>"		
		/>
</c:if>

<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Recibo rec=(Recibo)row.getObject();

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
String urlEdit="/"+portlet_name+"/editar_recibos_entry";
//String urlAnular="/"+portlet_name+"/anular_recibos_entry";
String urlAnular="javascript:anularRecibo('"+String.valueOf(rec.getId())+"')";
String origen=ParamUtil.getString(request,"origen");
%>
<c:if test="<%= Validator.isNull(rec.getBaja_fecha()) || (Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left" >
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="<%=urlEdit%>" />		
		<portlet:param name="recibo_id" value="<%= String.valueOf(rec.getId()) %>" />
		<portlet:param name="origen" value="<%=origen%>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=urlAnular%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="recibo-dada-de-baja-click-cambiar-fecha-reactivar"			
			url="<%=urlAnular%>"
		/>		
</c:if>

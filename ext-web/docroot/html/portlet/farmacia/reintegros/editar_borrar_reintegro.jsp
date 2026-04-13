<%@ include file="/html/portlet/farmacia/init.jsp" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
	
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_LIQ_1_")){
	portlet_name = "liquidaciones";
} 
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA);
boolean liquidado = false;

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ReintegroMedicamento reintegro=(ReintegroMedicamento)row.getObject();
String id_reintegro = reintegro.getId_reintegroString();
//if (reintegro.getBajaFechaOP() == null){
	liquidado = reintegro.getIdOP() != 0;
//}
String urlEdit="/"+portlet_name+"/editar_reintegro_farmacia_entry";
%>
<c:if test="<%= !liquidado && (Validator.isNull(reintegro.getBaja_fecha()) || (Validator.isNotNull(reintegro.getBaja_fecha()) && reintegro.getBaja_fecha().getTime()>System.currentTimeMillis()))%>">
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="<%=urlEdit%>" />		
		<portlet:param name="id_reintegro" value="<%=id_reintegro%>" />		
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
	<c:if test="<%= showABMButtons %>">
		<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
			<portlet:param name="struts_action" value="<%=urlEdit%>" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="numero" value="<%=id_reintegro%>" />		
		</portlet:actionURL>
		<liferay-ui:icon-delete url="<%= deleteURL %>" />
	</c:if>	
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%=Validator.isNotNull(reintegro.getBaja_fecha())%>">
<%-- <c:if test="<%= !liquidado && (Validator.isNotNull(reintegro.getBaja_fecha()) && reintegro.getBaja_fecha().getTime()<System.currentTimeMillis())%>"> --%>
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="reintegro-dado-de-baja"			
	/>
</c:if>
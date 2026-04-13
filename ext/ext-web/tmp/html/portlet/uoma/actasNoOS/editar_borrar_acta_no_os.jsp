<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%
String portlet_name = null;	
		
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "estudio_isidro";
} 

boolean isActa = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS)|| portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Acta acta=(Acta)row.getObject();
String deleteURL="javascript:anularActaNoOS('"+acta.getId()+"', "+acta.isActaCerrada()+")";
String urlEdit="/"+portlet_name+"/edit_actas_no_os_entry";
%>
<%if ((acta.isActaCerrada() && isActa) || !acta.isActaCerrada()) {%>
<liferay-ui:icon-menu align="left">
<c:if test="<%= Validator.isNull(acta.getBaja_fecha()) || (Validator.isNotNull(acta.getBaja_fecha()) && acta.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="<%=urlEdit%>" />		
		<portlet:param name="acta_id" value="<%= String.valueOf(acta.getId()) %>" />
		<portlet:param name="fromBusquedaDeuda" value="<%=Boolean.toString(!acta.isActaCerrada())%>"/>
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
</c:if>
 <c:choose>
 	<c:when test="<%= (Validator.isNull(acta.getBaja_fecha()) || (Validator.isNotNull(acta.getBaja_fecha()) && acta.getBaja_fecha().getTime()>System.currentTimeMillis())) && acta.isActaCerrada()%>">
		<liferay-ui:icon image="../message_boards/ban_user" message="pasar-calculo"  url="<%=deleteURL %>" />
    </c:when>
    <c:when test="<%= (Validator.isNull(acta.getBaja_fecha()) || (Validator.isNotNull(acta.getBaja_fecha()) && acta.getBaja_fecha().getTime()>System.currentTimeMillis())) && !acta.isActaCerrada()%>">
		<liferay-ui:icon image="../message_boards/ban_user" message="delete"  url="<%=deleteURL %>" />
    </c:when>
</c:choose>    		
</liferay-ui:icon-menu>
<%} %>

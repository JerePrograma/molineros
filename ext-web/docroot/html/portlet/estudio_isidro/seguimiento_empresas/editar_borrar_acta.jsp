<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 

boolean isActa = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Acta acta=(Acta)row.getObject();
String editURL=null;
String deleteURL=null;
if(!acta.getEntidad().trim().equals("OSPIM")){
	editURL="javascript:editarActaNoOSSeguimiento('"+acta.getId()+"')";
	deleteURL="javascript:anularActaNoOS('"+acta.getId()+"', "+acta.isActaCerrada()+")";
}else{
	editURL="javascript:editarActaSeguimiento('"+acta.getId()+"')";
	deleteURL="javascript:anularActa('"+acta.getId()+"', "+acta.isActaCerrada()+")";
}

%>

<%if ((acta.isActaCerrada() && isActa) || !acta.isActaCerrada()) {%>
	<liferay-ui:icon-menu align="left">
		<c:if test="<%= Validator.isNull(acta.getBaja_fecha()) || (Validator.isNotNull(acta.getBaja_fecha()) && acta.getBaja_fecha().getTime()>System.currentTimeMillis())%>">	
			<liferay-ui:icon image="edit" url="<%= editURL %>" />
		</c:if>
		<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=deleteURL %>" />
	</liferay-ui:icon-menu>
<%} %>

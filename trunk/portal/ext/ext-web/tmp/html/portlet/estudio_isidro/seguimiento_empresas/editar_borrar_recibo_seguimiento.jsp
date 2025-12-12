<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Recibo rec=(Recibo)row.getObject();
String editURL="javascript:editarRecibo('"+rec.getId()+"')";
String deleteURL="javascript:anularRecibo('"+rec.getId()+"')";
%>
<c:if test="<%= Validator.isNull(rec.getBaja_fecha()) || (Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">
	<liferay-ui:icon image="edit" url="<%= editURL %>" />	
	<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="recibo-dada-de-baja"			
		/>
</c:if>

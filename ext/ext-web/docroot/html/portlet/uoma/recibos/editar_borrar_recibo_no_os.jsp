<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Recibo rec=(Recibo)row.getObject();
%>
<c:if test="<%= Validator.isNull(rec.getBaja_fecha()) || (Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">
	
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/estudio_isidro/anular_recibos_no_os_entry" />
		<portlet:param name="recibo_id" value="<%= String.valueOf(rec.getId()) %>" />		
	</portlet:renderURL>
	<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(rec.getBaja_fecha()) && rec.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="recibo-dada-de-baja"			
		/>
</c:if>

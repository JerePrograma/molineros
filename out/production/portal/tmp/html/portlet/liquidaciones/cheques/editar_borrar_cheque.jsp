<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Cheque cheque=(Cheque)row.getObject();

StringBuilder sb= new StringBuilder();
sb.append("<a href='#' onClick=\"javascript:imprimir('");
sb.append(cheque.getNumero().toString());
sb.append("');\" >Imprimir</a>");


%>
<c:if test="<%= (Validator.isNull(cheque.getBaja_fecha()) || (Validator.isNotNull(cheque.getBaja_fecha()) && cheque.getBaja_fecha().getTime()>System.currentTimeMillis()))%>">
<%if (cheque.getDebitoCredito().equals(Cheque.Tipo.DEBITO)){ %>	<%=sb.toString() %><br/><%} %>
<liferay-ui:icon-menu>
	
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/liquidaciones/anular_cheque" />		
		<portlet:param name="cheque_nro" value="<%= cheque.getNumero().toString() %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="../message_boards/ban_user" message="anular" label="anular"  url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(cheque.getBaja_fecha()) && cheque.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon image="../message_boards/ban_user"	/>
</c:if>

<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio" %>
<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
CanjeChequePropio canje=(CanjeChequePropio)row.getObject();
String deleteURL="javascript:anularCanje('"+canje.getId() +"')";
%>
<c:if test="<%= Validator.isNull(canje.getBaja_fecha())%>">
<liferay-ui:icon-menu>
	<liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=deleteURL %>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(canje.getBaja_fecha())%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="canje-dada-de-baja"			
		/>
</c:if>
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Liquidacion liquidacion=(Liquidacion)row.getObject();
String id_liquidacion = liquidacion.getId_liquidacionString();
%>
<c:if test="<%= Validator.isNull(liquidacion.getBaja_fecha()) || (Validator.isNotNull(liquidacion.getBaja_fecha()) && liquidacion.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/liquidaciones/editar_liquidacion_entry" />		
		<portlet:param name="id_liquidacion" value="<%=id_liquidacion%>" />
		<portlet:param name="paga" value="1" />
		<portlet:param name="ajustar" value="1" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(liquidacion) && Validator.isNotNull(liquidacion.getBaja_fecha()) && liquidacion.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="liquidacion-dado-de-baja"
		/>
</c:if>
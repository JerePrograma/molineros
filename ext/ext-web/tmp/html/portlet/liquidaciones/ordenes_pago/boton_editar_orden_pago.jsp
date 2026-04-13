<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
OrdenPagoAmtima op=(OrdenPagoAmtima)row.getObject();
String deleteURL="javascript:anularOpAmtima('"+op.getId().toString()+"')";
String reactivarURL="javascript:reactivarOpAmtima('"+op.getId().toString()+"')";
boolean rolABMPagos = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP_PAGOS);
String editarFormaPagoURL="javascript:editarFormaPagoOpAmtima('"+op.getId().toString()+"')";
%>
<liferay-ui:icon-menu>
    <c:if test="<%= Validator.isNull(op.getBaja_fecha()) %>">  
	    <liferay-ui:icon image="../message_boards/ban_user" message="anular"  url="<%=deleteURL %>" />
	</c:if>    
	<c:if test="<%= Validator.isNotNull(op.getBaja_fecha()) && op.isReActivable() %>">
		<liferay-ui:icon image="../common/undo" message="reactivar-op" url="<%=reactivarURL %>"/>
	</c:if>
	<c:if test="<%= Validator.isNull(op.getBaja_fecha()) && rolABMPagos %>">
		<liferay-ui:icon image="../common/edit" message="editar-forma-pago-op" url="<%=editarFormaPagoURL %>"/>
	</c:if>
</liferay-ui:icon-menu>

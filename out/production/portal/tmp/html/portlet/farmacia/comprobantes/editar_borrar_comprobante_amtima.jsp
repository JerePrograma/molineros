<%@ include file="/html/portlet/farmacia/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Comprobante comp=(Comprobante)row.getObject();
%>
<c:if test="<%= Validator.isNull(comp.getBaja_fecha()) || (Validator.isNotNull(comp.getBaja_fecha()) && comp.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">
<!--  	<% if  (!comp.isOpExistente()) {%> -->
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/farmacia/editar_comprobante_amtima" />
		<portlet:param name="nro_comprobante" value="<%= String.valueOf(comp.getNroComprobante()) %>" />
		<portlet:param name="pto_venta" value="<%= String.valueOf(comp.getPtoVenta()) %>" />
		<portlet:param name="tipo_comprobante" value="<%= String.valueOf(comp.getTipoComprobante()) %>" />
		<portlet:param name="letra" value="<%= String.valueOf(comp.getLetraComprobante()) %>" />
		<portlet:param name="sucursal" value="<%= String.valueOf(comp.getSucuComprobante()) %>" />
		<portlet:param name="cuit_compr_emisor" value="<%= String.valueOf(comp.getCuit()) %>" />
		<portlet:param name="esAmtima" value="esAmtima" />
		<portlet:param name="permitirEdicion" value="editar" /> 
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
<!--  	<%} %> -->
	<% if  (!comp.isOpExistente() && !comp.isPagado() ) {%>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/farmacia/anular_comprobante_amtima_entry" />
		<portlet:param name="nro_comprobante" value="<%= String.valueOf(comp.getNroComprobante()) %>" />
		<portlet:param name="pto_venta" value="<%= String.valueOf(comp.getPtoVenta()) %>" />
		<portlet:param name="tipo_comprobante" value="<%= String.valueOf(comp.getTipoComprobante()) %>" />
		<portlet:param name="letra" value="<%= String.valueOf(comp.getLetraComprobante()) %>" />
		<portlet:param name="sucursal" value="<%= String.valueOf(comp.getSucuComprobante()) %>" />
		<portlet:param name="cuit_compr_emisor" value="<%= String.valueOf(comp.getCuit()) %>" />
		<portlet:param name="esAmtima" value="esAmtima" />
		<portlet:param name="borrar_totalmente" value="<%=String.valueOf(!comp.isOpExistente()) %>" />
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	<%} %>
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(comp.getBaja_fecha()) && comp.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="comprobante-dado-de-baja"			
		/>
</c:if>

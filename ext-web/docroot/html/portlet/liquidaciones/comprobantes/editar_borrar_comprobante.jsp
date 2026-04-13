<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.global.WebKeysPortal" %>
<%@ page import="java.util.Date" %>

<% 
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
Date fechaCierre = (Date)request.getAttribute(WebKeysPortal.FECHA_CIERRE_PERIODO_CONTABLE);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Comprobante comp=(Comprobante)row.getObject();

%>
<c:if test="<%= (Validator.isNull(comp.getBaja_fecha()) || (Validator.isNotNull(comp.getBaja_fecha()) && comp.getBaja_fecha().getTime()>System.currentTimeMillis()))%>">
<liferay-ui:icon-menu align="left">
 	<% if  (fechaCierre.compareTo(comp.getFechaRecepcion()) < 0) {%> 
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<%if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/editar_comprobante" />
		<%}else{%>
			<portlet:param name="struts_action" value="/liquidaciones/editar_comprobante" />
		<%}%>
		<portlet:param name="nro_comprobante" value="<%= String.valueOf(comp.getNroComprobante()) %>" />
		<portlet:param name="pto_venta" value="<%= String.valueOf(comp.getPtoVenta()) %>" />
		<portlet:param name="tipo_comprobante" value="<%= String.valueOf(comp.getTipoComprobante()) %>" />
		<portlet:param name="letra" value="<%= String.valueOf(comp.getLetraComprobante()) %>" />
		<portlet:param name="sucursal" value="<%= String.valueOf(comp.getSucuComprobante()) %>" />
 		<portlet:param name="cuit_compr_emisor" value="<%= String.valueOf(comp.getCuit()) %>" />
 		<portlet:param name="permitirEdicion" value="editar" /> 
		
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
  	<%} %> 

	<%-- <% if  ((!comp.isOpExistente()  && !comp.isPagado()  )&& fechaCierre.compareTo(comp.getFechaRecepcion()) < 0) {%> --%>
	<% if  ( !comp.isPagado() && fechaCierre.compareTo(comp.getFechaRecepcion()) < 0) {%>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<%if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/anular_comprobante_entry" />
		<%}else{%>
			<portlet:param name="struts_action" value="/liquidaciones/anular_comprobante_entry" />
		<%}%>
		
		<portlet:param name="nro_comprobante" value="<%= String.valueOf(comp.getNroComprobante()) %>" />
		<portlet:param name="pto_venta" value="<%= String.valueOf(comp.getPtoVenta()) %>" />
		<portlet:param name="tipo_comprobante" value="<%= String.valueOf(comp.getTipoComprobante()) %>" />
		<portlet:param name="letra" value="<%= String.valueOf(comp.getLetraComprobante()) %>" />
		<portlet:param name="sucursal" value="<%= String.valueOf(comp.getSucuComprobante()) %>" />
		<portlet:param name="cuit_compr_emisor" value="<%= String.valueOf(comp.getCuit()) %>" />
		<% if  (!comp.isOpExistente() && fechaCierre.compareTo(comp.getFechaRecepcion()) < 0) {%>
			<portlet:param name="borrar_totalmente" value="<%=String.valueOf(!comp.isOpExistente()) %>" />
		<% } else { %>
			<portlet:param name="borrar_totalmente" value="false" />
		<% } %>
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

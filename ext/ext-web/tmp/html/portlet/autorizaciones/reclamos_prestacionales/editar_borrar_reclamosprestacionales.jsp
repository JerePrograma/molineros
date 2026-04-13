<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ReclamoPrestacional  reclamo=(ReclamoPrestacional )row.getObject();
String id = String.valueOf(reclamo.getId_reclamo());
String url = "javascript:imprimir('"+ id +"')";	
SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
String fechaBaja = "";
if (reclamo.getBaja_fecha() != null ){
	fechaBaja = "Registro de baja al " + sdf1.format(reclamo.getBaja_fecha());
}

%>
<c:if test="<%= Validator.isNull(reclamo.getBaja_fecha()) || (Validator.isNotNull(reclamo.getBaja_fecha()) && reclamo.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">

	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" />		
		<portlet:param name="id_reclamosel" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />	
	
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" />		
		<portlet:param name="id_reclamosel" value="<%= id %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
	
	
	
	<liferay-ui:icon image="print" url="<%= url %>" message="Imprimir"/>
	
	
	
	
		
	
	
</liferay-ui:icon-menu>
</c:if>


<c:if test="<%= Validator.isNotNull(reclamo.getBaja_fecha()) && reclamo.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../common/close"
			message="<%= fechaBaja %>"			
		/>
</c:if>



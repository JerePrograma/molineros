<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ReclamoPrestacional  reclamo=(ReclamoPrestacional )row.getObject();
String id = String.valueOf(reclamo.getId_reclamo());
String url = "javascript:imprimir('"+ id +"')";
String fechaBaja = "";
SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
	
if (reclamo.getBaja_fecha() != null ){
	fechaBaja = "Registro de baja al " + sdf1.format(reclamo.getBaja_fecha());
}
%>
<c:if test="<%= Validator.isNull(reclamo.getBaja_fecha()) || (Validator.isNotNull(reclamo.getBaja_fecha()) && reclamo.getBaja_fecha().getTime()>System.currentTimeMillis())%>">
<liferay-ui:icon-menu align="left">

	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="addURL">
		<portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" />		
		<portlet:param name="casoasociado" value="<%= id %>" />
	</portlet:renderURL>			
	<liferay-ui:icon image="add" url="<%= addURL %>" message="Asociar Nuevo"/>	
		
	<liferay-ui:icon image="print" url="<%= url %>" message="Imprimir"/>
	
	
</liferay-ui:icon-menu>
</c:if>


<c:if test="<%= Validator.isNotNull(reclamo.getBaja_fecha()) && reclamo.getBaja_fecha().getTime()<System.currentTimeMillis()%>">
	<liferay-ui:icon
			image="../common/close"
			message="<%= fechaBaja %>"			
		/>
</c:if>



<%@ include file="/html/portlet/novedades/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
NovedadEmpleadorTotal nov=(NovedadEmpleadorTotal) row.getObject();
String cuil=null, inte=null, tipoNoveEmpl=null;

cuil = nov.getCuil_titular(); 
inte = String.valueOf(nov.getInte());
tipoNoveEmpl = nov.getNovedad_desc();

if(cuil != null && (tipoNoveEmpl.equalsIgnoreCase("CAMBIO DE PLAN") ||
		tipoNoveEmpl.equalsIgnoreCase("BAJA SUGERIDA")) ){ %>
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="inte" value="<%= inte %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
</liferay-ui:icon-menu>

<%} 

else if(cuil != null && tipoNoveEmpl.equalsIgnoreCase("ALTA DE PLAN y ALTA DE AFILIADO")){ %>
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="addURL">
		<portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" />		
		<portlet:param name="<%=Constants.ADD%>" value="<%= Constants.ADD %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= addURL %>" />
</liferay-ui:icon-menu>

<%} %>


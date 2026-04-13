<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ page import="ar.com.ospim.util.StringUtils" %>
<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Afiliado afiliado=(Afiliado)row.getObject();
String cuil = afiliado.getCuil_titular();
String nroFormulario = String.valueOf(afiliado.getInte()); //chanchada...

%>
<%if(afiliado.getDiscapacitado().equalsIgnoreCase("f") &&  Validator.isNull(afiliado.getBaja_fecha()) ) { %>
<liferay-ui:icon-menu>
	<%if(StringUtils.checkEmpty(afiliado.getIdAmtimaBajaFechaAsString())){ %>
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="nro_formulario" value="<%= nroFormulario %>" />
		<portlet:param name="opciones" value="true" />
		<portlet:param name="editaropcion" value="SI"/>
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<%} %>
	<%String deleteOpcionUrl = "javascript:eliminarOpcionSSS('"+cuil+"','"+nroFormulario+"')";%>
	
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="nro_formulario" value="<%= nroFormulario %>" />
		<portlet:param name="opciones" value="true" />
		<portlet:param name="editaropcion" value="<%= Constants.DELETE %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="delete" url="<%= deleteOpcionUrl %>"/>	
</liferay-ui:icon-menu>
<% } %>
<%String recuperarOpcionUrl = "javascript:recuperarOpcionSSS('"+cuil+"','"+nroFormulario+"')";%>

<c:if test="<%= Validator.isNotNull(afiliado.getBaja_fecha())%>">
	
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="recuperarURL">
		<portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="nro_formulario" value="<%= nroFormulario %>" />
		<portlet:param name="opciones" value="true" />
		<portlet:param name="editaropcion" value="<%= Constants.RESTORE %>" />
	</portlet:renderURL>
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="afiliado-dado-de-baja"
			url="<%= recuperarOpcionUrl %>" />
			
</c:if>

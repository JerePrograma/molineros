<%@ include file="/html/portlet/prestadores/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
PrestadorLugarAtencion lugarAt=(PrestadorLugarAtencion)row.getObject();
String idDom = String.valueOf(lugarAt.getId_domicilio());
String idPrest = String.valueOf(lugarAt.getId_prestador());
%>
<c:if test="<%= Validator.isNull(lugarAt.getBajaFecha())%>">
<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" />		
		<portlet:param name="prestador_id" value="<%= idPrest %>" />
		<portlet:param name="domicilio_id" value="<%= idDom %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="deleteURL">
		<portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" />		
		<portlet:param name="prestador_id" value="<%= idPrest %>" />
		<portlet:param name="domicilio_id" value="<%= idDom %>" />
		<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.DELETE %>" />
	</portlet:renderURL>
	<liferay-ui:icon-delete url="<%=deleteURL%>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(lugarAt.getBajaFecha()) || (lugarAt.getEstado()!= null && lugarAt.getEstado().equals(PrestadorLugarAtencion.ESTADOS.BAJA))%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="lugar-at-dado-de-baja"			
		/>
</c:if>

<%@ include file="/html/portlet/correspondencia/init.jsp" %>

<% 
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ItemCorrespondencia item=(ItemCorrespondencia)row.getObject();
String id_item = String.valueOf(item.getId());
String id_correspondencia = String.valueOf(item.getId_correspondencia());

String deleteURL="javascript:borraCorrespondenciaDetalle('"+id_item+"')";
String editURL="javascript:editaCorrespondenciaDetalle('"+id_item+"')"; 

%>

<liferay-ui:icon-menu>
	
	<liferay-ui:icon image="edit" url="<%= editURL %>" />

	<liferay-ui:icon-delete url="<%= deleteURL %>" />
	
</liferay-ui:icon-menu>

<c:if test="<%= Validator.isNotNull(item.getBaja_fecha())%>">
	
	<liferay-ui:icon image="../message_boards/ban_user" message="eliminada-correspondencia"	/>
	
</c:if>
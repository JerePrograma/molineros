<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@ include file="/html/portlet/correspondencia/init.jsp" %>

<% 
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ItemCorrespondencia item=(ItemCorrespondencia)row.getObject();
String id_item = String.valueOf(item.getId());
String id_correspondencia = String.valueOf(item.getId_correspondencia());
String estado_item = item.getEstado();
String tipo_registro = item.getCabecera().getTipoRegistro();

//Posibles estados item: Para salidas: ENVIADO. Para entradas: INGRESADO, RECIBIDO
//es editable y con posibilidad de dar de baja si está en estado ingresado o enviado Y NO ESTÁ EN PAQUETE

%>
<c:if test="<%= Validator.isNull(item.getBaja_fecha()) && ( !item.getEstado().equalsIgnoreCase(\"RECIBIDO\") ) %>">
								
<liferay-ui:icon-menu>
	<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/correspondencia/editar_correspondencia_entry" />
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.EDIT%>" />
		<portlet:param name="id_item_correspondencia" value="<%=id_item%>" />
		<portlet:param name="id_correspondencia" value="<%=id_correspondencia%>" />
		<portlet:param name="tipo_registro" value="<%=tipo_registro%>" />
	</portlet:actionURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	
		<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="deleteURL">
			<portlet:param name="struts_action" value="/correspondencia/editar_correspondencia_entry" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
     		<portlet:param name="id_item_correspondencia" value="<%=id_item%>" />
			<portlet:param name="id_correspondencia" value="<%=id_correspondencia%>" />
			<portlet:param name="tipo_registro" value="<%=tipo_registro%>" />
		</portlet:actionURL>
		<liferay-ui:icon-delete url="<%= deleteURL %>" />
</liferay-ui:icon-menu>
</c:if>
<c:if test="<%= Validator.isNotNull(item.getBaja_fecha())%>">
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="eliminada-correspondencia"			
	/>
</c:if>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento" %>

<% 
String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}  
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}  

boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Asiento asiento=(Asiento)row.getObject();
String deleteURL="javascript:eliminarAsiento('"+asiento.getId()+"', '"+asiento.getDescripcion()+"')";
%>

<%if (rolABM) {%>
<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/editar_asientos_entry" />			
		<%}else if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/editar_asientos_entry" />
		<%}else{%>
			<portlet:param name="struts_action" value="/tesoreria/editar_asientos_entry" />
		<%}%>
		<portlet:param name="asiento_id" value="<%= String.valueOf(asiento.getId()) %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<liferay-ui:icon image="../message_boards/ban_user" message="Eliminar"  url="<%=deleteURL %>" />
</liferay-ui:icon-menu>
<%} %>

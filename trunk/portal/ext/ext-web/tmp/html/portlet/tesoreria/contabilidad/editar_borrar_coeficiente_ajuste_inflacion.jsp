<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion" %>

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

//boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
CoeficienteAjusteInflacion coeficiente=(CoeficienteAjusteInflacion)row.getObject();
String deleteURL="javascript:eliminarCoeficiente('"+coeficiente.getEntidad()+"', '"+coeficiente.getPeriodo()+"')";
%>


<liferay-ui:icon-menu align="left">
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/coeficientes_ajuste_inflacion" />			
		<%}else if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/coeficientes_ajuste_inflacion" />
		<%}else{%>
			<portlet:param name="struts_action" value="/tesoreria/coeficientes_ajuste_inflacion" />
		<%}%>
		<portlet:param name="entidad" value="<%= String.valueOf(coeficiente.getEntidad()) %>" />
		<portlet:param name="periodo" value="<%= String.valueOf(coeficiente.getPeriodo()) %>" />
		<portlet:param name="coeficiente" value="<%= String.valueOf(coeficiente.getCoeficiente()) %>" />
		<portlet:param name="cmd" value="edit" />
		
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<liferay-ui:icon image="../message_boards/ban_user" message="Eliminar"  url="<%=deleteURL %>" />
</liferay-ui:icon-menu>


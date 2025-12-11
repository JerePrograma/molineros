<%@ include file="/html/portlet/novedades/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
NovedadTotal nov=(NovedadTotal)row.getObject();
String cuil=null, inte=null;

if(nov.getCodigo_movimiento().equalsIgnoreCase("AO")){
	%>
	<liferay-ui:icon-menu>
		<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="addURL">
			<portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" />	
<%--			<portlet:param name="opciones" value="true" />	
			<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.ADD%>"  />
 		<portlet:param name="cuil_titular" value="<%= nov.getCuil_titular() %>" /> --%>
		</portlet:actionURL>
		<liferay-ui:icon image="add" url="<%=addURL %>" />
	</liferay-ui:icon-menu>
	<%
}else if(nov.getCodigo_movimiento().equalsIgnoreCase("BO")){
	List<Afiliado>afiliados = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(String.valueOf(nov.getDocumento_numero()),nov.getDocumento_tipo()) ;
	for(Afiliado afi : afiliados){
		if(afi.getBaja_fecha() == null){
			cuil = afi.getCuil_titular();
			inte = afi.getInteAsString();
		}
	}
	
}else{

List<Afiliado>afiliados = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(String.valueOf(nov.getDocumento_numero()),nov.getDocumento_tipo()) ;
Afiliado afi = null;

	if(afiliados.size()==1){
		afi = afiliados.get(0);
		cuil = afi.getCuil_titular();
		inte = afi.getInteAsString();
	}

} %>
<% if(cuil != null){ %>
<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="inte" value="<%= inte %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
</liferay-ui:icon-menu>

<%-- <%} else { %>
	<div><table><tr><td>Analizar caso</td></tr></table> </div> --%> 
<%} %>



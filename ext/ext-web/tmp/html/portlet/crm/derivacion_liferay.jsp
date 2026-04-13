<%@ include file="/html/portlet/crm/init.jsp"%>
<%@ include file="/html/portlet/crm/modelo_liferay.jsp"%>
<% 
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
		
	List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		Organization orgLocal = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId()).get(0); 
	
	String accion = (String) request.getAttribute(Constants.CMD);

	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
%>

<%if(!esView){ %>	
<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="edificio" />:</label></td>
			<td><select name="<portlet:namespace/>edificio_destino"
				id="<portlet:namespace/>edificio_destino" <% if (esView) { %> disabled="disabled" <%}else{ %> onchange="javascript:filtrarGrupos();" onclick="javascript:filtrarGrupos();" onfocus="javascript:filtrarGrupos();"  <%} %>  >			
				<%
					for (Organization org : organizaciones) {
				%>
						<option value="<%= org.getOrganizationId() %>" 
						 	<%if(org.getOrganizationId()==orgLocal.getOrganizationId()){ %> selected="selected" <%} %> <%=org.getName() %>><%=org.getName() %> 
						</option>
				<%
					}
				%>
			</select></td>
			<td><label><liferay-ui:message key="sector" />:</label></td>
			<td><select name="<portlet:namespace/>sector_destino"
				id="<portlet:namespace/>sector_destino" <% if (esView) { %> disabled="disabled" <%} else { %> onchange="javascript:filtrarUsuarios(false,true);" onclick="javascript:filtrarUsuarios(false,true);" onfocus="javascript:filtrarGrupos(false);" <%} %>>			
				<option value="">Seleccione un sector</option>
				
			</select></td>
			<td><label><liferay-ui:message key="usuario" />:</label></td>
			<td><select name="<portlet:namespace/>usuario_destino"
				id="<portlet:namespace/>usuario_destino" <% if (esView) { %> disabled="disabled" <%} %>>						
				<option value="">Seleccione un usuario</option>
			</select></td>
		</tr>	
	</table>    
<%} %>	

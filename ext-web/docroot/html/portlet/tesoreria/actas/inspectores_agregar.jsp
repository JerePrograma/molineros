<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
	
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if (renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}
if(renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
}
 
List<Inspector> inspectores= (ArrayList<Inspector>)request.getSession().getAttribute(WebKeysTesoreria.INSPECTORES_EN_SESSION);


//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

%>
<table class="lfr-table" width="100%">
		<tr>
			<td>
			<% if (esEdicion) { %> 
				<select  name="<portlet:namespace/>inspector" id="<portlet:namespace/>inspector">
					<option value=""></option>
					<% for (Inspector ins: inspectores) { %>
					<option value="<%= ins.getId() %>"><%=ins.getNombre()%></option>
					<% } %>
				</select>&nbsp;
				&nbsp;&nbsp;&nbsp;
				
				<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarInspector();" />
				<%} %>
			</td>
		</tr>
		<tr>
			<td>
				<div align="center" id="<portlet:namespace />buscandoInspectores">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div align="center" id="<portlet:namespace />inspectores">		
					<jsp:include page='inspectores_search_result.jsp' />
				</div>
			</td>
		</tr>
</table>
<script type="text/javascript">
	function <portlet:namespace />agregarInspector(){
			jQuery('#<portlet:namespace />buscandoInspectores').show();	
			var idInspector=jQuery('#<portlet:namespace />inspector').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_inspector&inspector=' +idInspector + '&esEdicion=' + "<%=esEdicion%>";
		
			jQuery('#<portlet:namespace />inspectores').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoInspectores').hide();            															
																			   }
															   );	
			document.getElementById("<portlet:namespace/>inspector").selectedIndex = 0;
	}

	function borraInspector(idInspector){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_inspector&inspector=' +idInspector+ '&esEdicion=' + "<%=esEdicion%>"
			jQuery('#<portlet:namespace />inspectores').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoInspectores').hide();            															
																			   }
															   );
		}	
	}
	jQuery('#<portlet:namespace />buscandoInspectores').hide();
</script>
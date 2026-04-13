<%@ include file="/html/portlet/rrhh/init.jsp"%>
<%

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ABM_RRHH);
List<String> errores = (List<String>)request.getAttribute("errores");
Integer origenEdificioRRHH = (Integer) request.getAttribute("origenEdificioRRHH");

if(origenEdificioRRHH==null){
	origenEdificioRRHH=0;
}
if (errores != null && !errores.isEmpty()){
	%>
<table>
	<%
	for (String error : errores){
		%>
	<tr>
		<td><%=error%></td>
	</tr>
	<%
	}
	%>
</table>
<%
}

%>

<%-- </form>

<form action="" method="get" name="<portlet:namespace />fm2"
	enctype="multipart/form-data"> --%>

<fieldset class="block-labels"><legend>Archivo Horarios</legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
		<td><liferay-ui:message	key="importar-archivos" />:</td>
		<td align="center"><input type="file" name="importa_horarios"
			id="importa_horarios" /></td>
	</tr>	
	<tr>
		<td><liferay-ui:message	key="origen" />:</td>
		<td><select name="<portlet:namespace />origen">
				  <optgroup label="Edificio">
				    <option value="1" <%if(origenEdificioRRHH == 1){  %> selected="selected" <%} %>>SAN JUAN</option>
				    <option value="2" <%if(origenEdificioRRHH == 2){  %> selected="selected" <%} %>>MÉXICO</option>
				    <option value="3" <%if(origenEdificioRRHH == 3){  %> selected="selected" <%} %>>LOS DIQUES</option>
				  </optgroup>
			</select>	   
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">	 
		<% if (showABMButtons && origenEdificioRRHH < 3) { %>	
		<input type="submit"
			value="Subir Archivo"
			onClick="<portlet:namespace />uploadArchivos();" />	
		<% }else if (showABMButtons && origenEdificioRRHH == 3) { %>	
		<input type="submit"
			value="Subir Archivo"
			onClick="<portlet:namespace />uploadArchivosConVerificacion();" />
			
		<%} else { %> &nbsp; <%} %>			
		<input type="button"
			value="Verificar Los Diques"
			onClick="<portlet:namespace />buscarUltimasFichadasLosDiques();" />	
		</td>
	</tr>
</table>
</fieldset>
<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando'/></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaFichadasDiv">
</div>
</fieldset>
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();

		function <portlet:namespace />uploadArchivos() {	
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/rrhh/upload_archivo_horarios"/></portlet:actionURL>';			
			/*document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url); */
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}
		
		function <portlet:namespace />uploadArchivosConVerificacion() {	
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/rrhh/upload_archivo_horarios_con_verificacion"/></portlet:actionURL>';			
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}
		
		function <portlet:namespace />buscarUltimasFichadasLosDiques(){
			
			jQuery('#<portlet:namespace />buscando').show();
			var params = {"<%=Constants.CMD%>":"<%=Constants.SEARCH%>"};
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/rrhh/upload_archivo_horarios_con_verificacion" /></portlet:renderURL>';
			
			jQuery('#<portlet:namespace />busquedaFichadasDiv').load(url, params, function() {
																		jQuery('#<portlet:namespace />buscando').hide();            															
																	  }
			);
		}	

		
</script>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		String portlet_name = null;
			
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "estudio_isidro";
		}
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO)|| portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
 		boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-convenios" /></legend>
				<table class="lfr-table">
					<tr>
						<td width="60">
							<liferay-ui:message key="entidad" />
						</td>				
						<td width="60">		
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad_con">	
								<%if(!portlet_name.equals("farmacia")&&!portlet_name.equals("uoma")){%>
								<option selected value=""></option>
								<%}%>							
								<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
									<% if(portlet_name.equals("uoma") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>									
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(portlet_name.equals("farmacia") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)){%>
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(!portlet_name.equals("farmacia") && !portlet_name.equals("uoma")){%>
											<option value="<%= entidad %>"><%=entidad%></option>
									<%}%>
								<%}%>								
							</select>
						</td>
						<td><label><liferay-ui:message key="convenio" />:</label></td>
						<td><input id="<portlet:namespace />convenio" name="<portlet:namespace />convenio" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td><input id="<portlet:namespace />empresa" name="<portlet:namespace />empresa" size="50" maxlength="50" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td>
							<% if(showABMButtons && !soloVer) { %>
								<input type="button" value="<liferay-ui:message key="alta-convenio" />" onClick="<portlet:namespace />altaConvenio();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaConvenioDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var convenio=jQuery('#<portlet:namespace />convenio').val();
		var empresa=escape(jQuery('#<portlet:namespace />empresa').val());
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var entidad=jQuery('#<portlet:namespace />entidad_con').val();
		
		if(!<portlet:namespace />validarBusqueda(convenio,empresa,cuit,entidad)){
			return false;
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_convenios_no_os&convenio='+convenio+
		'&empresa='+empresa+'&cuit='+cuit+'&entidad_con='+entidad;

		jQuery('#<portlet:namespace />busquedaConvenioDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(convenio, empresa, cuit, entidad){		
		if(trim(convenio).length ==0 && trim(empresa).length==0 && trim(cuit).length==0 && trim(entidad).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaConvenio() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_convenios_no_os_entry';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
</script>

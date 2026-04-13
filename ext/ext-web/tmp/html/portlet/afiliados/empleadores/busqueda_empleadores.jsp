<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>



<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-empleadores" /></legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="sucursal" />:</label></td>
						<td><input id="<portlet:namespace />sucursal" name="<portlet:namespace />sucursal" size="5" maxlength="6" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="descripcion" />:</label></td>
						<td><input id="<portlet:namespace />descripcion" name="<portlet:namespace />descripcion" size="30" maxlength="30" type="text" value="" /></td>
						<td><label><liferay-ui:message key="seccional" />:</label></td>
						<td colspan="5"><liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp"/></td>			
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td>
							<% if(showABMButtons) { %>
								<input type="button" value="<liferay-ui:message key="alta-empleador" />" onClick="<portlet:namespace />altaEmpleador();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="17">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaEmpleadorDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var sucu=jQuery('#<portlet:namespace />sucursal').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion').val();
		var id_seccional=jQuery('#<portlet:namespace />id_seccional').val();
		
		if(!<portlet:namespace />validarBusqueda(cuit,sucu,descripcion,id_seccional)){
			return false;
		}		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_empleadores&cuit='+cuit+
		'&sucu='+sucu+'&descripcion='+encodeURI(descripcion)+'&id_seccional='+id_seccional;

		jQuery('#<portlet:namespace />busquedaEmpleadorDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(cuit,sucu,descripcion,id_seccional){		
		if(trim(cuit.length)==0 && trim(sucu.length)==0 && trim(descripcion.length)==0 && id_seccional==0){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaEmpleador() {
		jQuery('#<portlet:namespace />cuit').val("");
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
</script>

<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
String portlet_name=null;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
%>

		<fieldset class="block-labels">
				<legend>Proveedores</legend>
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
						
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td>
							<input type="button" value="<liferay-ui:message key="alta-proveedor" />" onClick="<portlet:namespace />altaProveedor();" />
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
			<div align="center" id="<portlet:namespace />busquedaProveedorDiv">						
			</div>
		</fieldset>
		
<form action="" id="<portlet:namespace />fm" name="<portlet:namespace />fm">	
	<input type="hidden" id="cuit" name="cuit" value=""/>		
	<input type="hidden" id="sucursal" name="sucursal" value=""/>
</form>
	
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var sucu=jQuery('#<portlet:namespace />sucursal').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion').val();
		
		if(!<portlet:namespace />validarBusqueda(cuit,sucu,descripcion)){
			return false;
		}		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/proveedores_editar&cuit='+cuit+
		'&sucursal='+sucu+'&descripcion='+encodeURI(descripcion)+'&cmd=filter';

		jQuery('#<portlet:namespace />busquedaProveedorDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	
	function <portlet:namespace />validarBusqueda(cuit,sucu,descripcion){		
		if(trim(cuit.length)==0 && trim(sucu.length)==0 && trim(descripcion.length)==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaProveedor() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		    params+="&accion=E";
//		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/proveedores_editar" /></portlet:renderURL>';
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/proveedores_editar';				
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	
	function editarProveedor(id_Proveedor,tipoEdicion){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&idPrv=" + id_Proveedor;
	 	params+="&accion=" + tipoEdicion;
//		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/proveedores_editar" /></portlet:renderURL>';
        var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/proveedores_editar';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}
</script>

<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
	Boolean esEdicion = ParamUtil.getBoolean(request, "edit_mode");
	String prefijo=ParamUtil.getString(request, "origen","");
	String codigo =ParamUtil.getString(request, "codigo","");
	 		
	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		
	
%>

<portlet:defineObjects/>							
				<table class="lfr-table">
					
					<tr>
						<td><label><liferay-ui:message key="codigo" />: </label></td>
						<td colspan="2"><input id="<portlet:namespace />codigoCie<%=prefijo%>" 
							name="<portlet:namespace />codigoCie<%=prefijo%>" size="7" maxlength="100" type="text" 
							value="<%=codigo!=""  ? codigo  : ""  %>"  
							<%= !esEdicion ? " readonly='readonly'" : ""  %>  
							
						</td>
						<td><label><liferay-ui:message key="descripcion" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />detalleCie<%=prefijo%>" 
											 name="<portlet:namespace />detalleCie<%=prefijo%>" 
											 size="100" maxlength="100" type="text" value=""  
											 <%= !esEdicion ? " readonly='readonly'" : ""  %> 
						</td>
						
						<td colspan="2">

							<input id="<portlet:namespace />buscarCie" value="<liferay-ui:message key="buscar"/>" 
							title="<liferay-ui:message key="buscar-afiliado" />" 
							type="button" onClick="javascript:<portlet:namespace />buscarCieCodigo<%=prefijo%>();" />
						
						&nbsp;
						
							<input id="<portlet:namespace />limpiarCampos" value="<liferay-ui:message key="limpiar-campos"/>" 
							title="<liferay-ui:message key="buscar-afiliado" />" 
							type="button" onClick="javascript:<portlet:namespace />limpiarCamposCie<%=prefijo%>();"  />
						
						
						</td>
					</tr>
				</table>
				
				
<script type="text/javascript">
	var popupCieDiez; 		
	var popupdd;
	 

	if ( <%=!esEdicion%> ) { 
		 jQuery("#<portlet:namespace />buscarCie").hide();
		 jQuery("#<portlet:namespace />limpiarCampos").hide();
	}
	
	function <portlet:namespace />buscarCieCodigo<%=prefijo%>(){
		var codigoCie =jQuery('#<portlet:namespace />codigoCie').val();
		var detalleCie=jQuery('#<portlet:namespace />detalleCie').val();
		
		if(!<portlet:namespace />validarBusquedaCie10<%=prefijo%>(codigoCie,detalleCie)){
			return false;
		}
		
		popupCieDiez = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-ciediez" />",modal:true,width:830});
		
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/cie_diez&codigoCie='+codigoCie+
	    '&detalleCie='+detalleCie+'&origen=<%=prefijo%>&popup=true';	    
       jQuery(popupCieDiez).load(url);
  	}


	
	function <portlet:namespace />validarBusquedaCie10<%=prefijo%>(codigocie10,detallecie10){			
		if(trim(codigocie10.length)==0 && trim(detallecie10.length)==0 ){
			//alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}				
	

	function <portlet:namespace />limpiarCamposCie<%=prefijo%>() {
		jQuery("#<portlet:namespace />codigoCie<%=prefijo%>").val('');
		jQuery("#<portlet:namespace />detalleCie<%=prefijo%>").val('');
		limpiaCamposBusquedaCieDiez<%=prefijo%>();
	}
	
	function seleccionaCieDiez<%=prefijo%>(codigo ,descripcion){
		seleccionaCamposCieDiez<%=prefijo%>(codigo, descripcion );
		Liferay.Popup.close(popupCieDiez);
	}
	
	jQuery(document).ready(function(){
																	
	});	


	
</script>

<%@ include file="/html/portlet/farmacia_ospim/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
	String edit_mode = ParamUtil.getString(request, "edit_mode", null);
    String inhabilitar = ParamUtil.getString(request, "inhabilitar", null);
	String prefijo=ParamUtil.getString(request, "origen","");
	String detalle =ParamUtil.getString(request, "codigoColegio","");	 		
	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
%>
<portlet:defineObjects/>							
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="descripcion" />:</label></td>
						<td colspan="2">
						<input id="<portlet:namespace />detalleColegio<%=prefijo%>" name="<portlet:namespace />detallecolegio<%=prefijo%>" size="25" maxlength="100" type="text" value=""  <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %> 
						<%= !Boolean.parseBoolean(inhabilitar) ? "" :" disabled='disabled'" %> />
						</td>
						<td colspan="2">
							<input id="<portlet:namespace />buscarColegio" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />buscarColegioDetalleCodigo<%=prefijo%>();" />
						&nbsp;
							<input id="<portlet:namespace />limpiarCampos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />limpiarCamposColegio<%=prefijo%>();"  />
						</td>
					</tr>
				</table>
<script type="text/javascript">
	var popupColegio; 		
	 if ( <%=inhabilitar%> ) { 
		 jQuery("#<portlet:namespace />buscarColegio").hide();
		 jQuery("#<portlet:namespace />limpiarCampos").hide();
	 }
	function <portlet:namespace />buscarColegioDetalleCodigo<%=prefijo%>(){
		var detalleColegio=jQuery('#<portlet:namespace />detalleColegio').val();	
		var codigoColegio =jQuery('#<portlet:namespace />codigoColegio').val();		
		if(!<portlet:namespace />validarBusquedaColegio<%=prefijo%>(detalleColegio, codigoColegio)){
			return false;
		}
		popupColegio = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-colegio" />",modal:true,width:830});
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmaciaospim/colegio_buscador&codigoColegio='+codigoColegio+
	    '&detalleColegio='+detalleColegio+'&origen=<%=prefijo%>&popup=true';	    
       jQuery(popupColegio).load(url);
  	}
	function <portlet:namespace />validarBusquedaColegio<%=prefijo%>(detalleColegio, codigoColegio){			
		if(trim(detalleColegio.length)==0 && trim(codigoColegio.length)==0 ){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}				
	function <portlet:namespace />limpiarCamposColegio<%=prefijo%>() {
		jQuery("#<portlet:namespace />detalleColegio<%=prefijo%>").val('');
		limpiaCamposBusquedaColegio<%=prefijo%>();
	}
	function seleccionaColegio<%=prefijo%>(codigo,descripcion){
		seleccionaCamposColegio<%=prefijo%>(codigo,descripcion );
		Liferay.Popup.close(popupColegio);
	}
	jQuery(document).ready(function(){								
	});	
	
</script>
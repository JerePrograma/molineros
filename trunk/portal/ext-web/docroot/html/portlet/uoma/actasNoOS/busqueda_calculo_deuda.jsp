<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.ImposibleBorrarActaException" %>

<portlet:defineObjects/>

<liferay-ui:error exception="<%= ImposibleBorrarActaException.class %>" message="imposible-borrar-acta" />
<%
		String portlet_name = null;	
		String entidad= null;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
			entidad="A.M.T.I.M.A.";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
			entidad="U.O.M.A.";
		}
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "estudio_isidro";
		}
 		boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");

	    boolean isActa = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS);
	    if (isActa) {
	    	showABMButtons = true;
	    }
%>
		<fieldset class="block-labels">
		<input type="hidden" name="fromBusquedaDeuda" id="fromBusquedaDeuda" value="true"/>
		<input type="hidden" id="busqueda" name="busqueda" value="true" />
				<legend><liferay-ui:message key="busqueda-deudas" /></legend>
				<table class="lfr-table">
					<tr>
						<td>
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">	
								<%if(!portlet_name.equals("farmacia")&&!portlet_name.equals("uoma")){%>
								<option selected value=""></option>
								<%}%>							
								<%for (String entidadStr : WebKeysGlobal.ENTIDADES_UOMA) {	%>
									<% if(portlet_name.equals("uoma") && entidadStr.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>									
											<option value="<%= entidadStr %>" selected><%=entidadStr%></option>
									<%}else if(portlet_name.equals("farmacia") && entidadStr.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)){%>
											<option value="<%= entidadStr %>" selected><%=entidadStr%></option>
									<%}else if(!portlet_name.equals("farmacia") && !portlet_name.equals("uoma") && !entidadStr.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)){%>
											<option value="<%= entidadStr %>"><%=entidadStr%></option>
									<%}%>
								<%}%>								
							</select>						
						</td>				
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
							<% if(showABMButtons) { %>
								<input type="button" value="<liferay-ui:message key="alta-deuda" />" onClick="<portlet:namespace />altaActa();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="14">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaActaDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){		
		var empresa=jQuery('#<portlet:namespace />empresa').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		
		if(!<portlet:namespace />validarBusqueda(empresa, cuit)){
			return false;
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var entidad=jQuery('#<portlet:namespace/>entidad').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_actas_no_os' +
		'&empresa='+empresa+'&cuit='+cuit+'&calculo=true&entidad='+entidad;
		 url += '&rnd=' + Math.floor(Math.random()*100);	
		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda( empresa, cuit){		
		if( trim(empresa).length==0 && trim(cuit).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaActa() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
</script>

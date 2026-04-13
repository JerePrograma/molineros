<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%
		String portlet_name = null;	
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "estudio_isidro";
		}
		
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO)|| portlet_name.equals("farmacia");
 		boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		
%>
		<fieldset class="block-labels">
		
			<legend><liferay-ui:message key="actas" /></legend>
			
			<input type="hidden" id="cuit" name="cuit" />
			<input type="hidden" id="razon" name="razon" />
			<input type="hidden" id="busqueda" name="busqueda" value="true" />						
			<div align="center" id="<portlet:namespace />busquedaEmpresa">
				<table class="lfr-table" width="100%">
					<tr>					
						<td colspan="9">
							<table width="100%">
								<tr>
									<td>
										<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
									  		<liferay-util:param name="esEditable" value='true'/>						  		
									  		<liferay-util:param name="suf_entidad" value='segui_'/>
									  		<liferay-util:param name="suf" value='segui_'/>
										</liferay-util:include>
									</td>
								</tr>
							</table>							
						</td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>					
					<tr>
						<td width="60">
							<liferay-ui:message key="nro-acta" />
						</td>													
						<td width="60">
							<input id="<portlet:namespace />acta_convenio" name="<liferay-ui:message key="acta_convenio"/>" type="text"/>
						</td>
						<td width="60">
							<liferay-ui:message key="entidad" />
						</td>				
						<td width="60">
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">	
								<%if(!portlet_name.equals("farmacia")&&!portlet_name.equals("uoma")){%>
								<option selected value=""></option>
								<%}%>							
								<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
									<% if(portlet_name.equals("uoma") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>									
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(portlet_name.equals("farmacia") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)){%>
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(!portlet_name.equals("farmacia") && !portlet_name.equals("uoma") && !entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)){%>
											<option value="<%= entidad %>"><%=entidad%></option>
									<%}%>
								<%}%>								
							</select>
						</td>
						<td width="60"><label><liferay-ui:message key="estado" />:</label></td>
						<td width="60">
							<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
								<option selected value=""></option>
								<%for (String estado : WebKeysTesoreria.ESTADO_ACTAS_NO_OS) {	%>
											<option value="<%= estado %>"><%=estado%></option>						
								<%}%>
							</select>
						</td>
						<td width="60">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td width="60">							
							<div id="<portlet:namespace />limpiar">
								<input type="button" value="<liferay-ui:message key="limpiar-campos" />" onClick="limpiarCampos();" />
							</div>							
						</td>							
						<td width="60">
							<%if(!soloVer){ %>
							<input type="button" value="<liferay-ui:message key="alta-acta" />" onClick="nuevoActaConvenio();" />
							<%} %>																
						</td>		
					</tr>					
				</table>	     
				<div align="center" id="<portlet:namespace />actaConvenioBuscando">			
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
			</div>
		</fieldset>	
		
		
			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />actaConvenioBuscando').hide();		
	
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){		
		var cuit=jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val();		
		var razon=jQuery('#<portlet:namespace />entidad<%=sufi%>').val();
		var acta=jQuery('#<portlet:namespace />acta_convenio').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var estado=jQuery('#<portlet:namespace />estado').val();
		
		jQuery('#<portlet:namespace />actaConvenioBuscando').show();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_actas_no_os&acta='+acta+
		'&empresa='+escape(razon)+'&cuit='+cuit+'&entidad='+escape(entidad)+'&estado='+escape(estado);		
		url += '&rnd=' + Math.floor(Math.random()*100);		 
		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />actaConvenioBuscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(acta, empresa, cuit){		
		if(trim(acta).length ==0 && trim(empresa).length==0 && trim(cuit).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;empresa
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaActa() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/editar_actas_no_os_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     

	
	
	
	function nuevoActaConvenio(){
		var cuit=jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val();		
		var razon=jQuery('#<portlet:namespace />entidad<%=sufi%>').val();
		var acta=jQuery('#<portlet:namespace />acta_convenio').val();
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry&cuit='+cuit;		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}	
	
	
	function <portlet:namespace />validarBusqueda(cuit){		
		if(trim(cuit).length==0 && jQuery('#<portlet:namespace />estadoBuscar').val()=='' && jQuery('#<portlet:namespace />entidad<%=sufi%>').val()==''){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}
	function cambiaCuitsegui_(){
	}
	
</script>

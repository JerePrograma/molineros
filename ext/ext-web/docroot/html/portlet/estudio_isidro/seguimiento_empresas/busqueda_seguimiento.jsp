<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		
		LlamadosEstudio llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);

		portletSession.removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
		
%>
		<fieldset class="block-labels">
		
			<legend><liferay-ui:message key="empresa" /></legend>

			<div>
				&nbsp;
			</div>			
			<div>
				&nbsp;
			</div>
			<input type="hidden" id="cuit" name="cuit" />
			<input type="hidden" id="razon" name="razon" />			
			<div align="center" id="<portlet:namespace />busquedaEmpresa">
				<table class="lfr-table">
					<tr>						
						<td >
							<table>
								<tr>
									<td>
										<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
									  		<liferay-util:param name="esEditable" value='true'/>						  		
									  		<liferay-util:param name="cuit" value='<%= llest != null && llest.getEmpresa() != null ? llest.getEmpresa().getCuit() : new String("")%>'/>
									  		<liferay-util:param name="sucu" value='000'/>
									  		<liferay-util:param name="suf_entidad" value='segui_'/>
									  		<liferay-util:param name="suf" value='segui_'/>
										</liferay-util:include>
									</td>
								</tr>
							</table>							
						</td>
						<td rowspan="2" valign="top">
							<liferay-ui:message key="nro-lote" />
						</td>													
						<td rowspan="2" valign="top">
							<input type="text"  id="<portlet:namespace />lote" name="<portlet:namespace />lote" />			
						</td>
						<td rowspan="2" valign="top">
							<liferay-ui:message key="ramo" />
						</td>													
						<td rowspan="2" valign="top">
							<select name="<portlet:namespace/>tipo" id="<portlet:namespace/>tipo">	
								<option selected value="0">TODAS</option>							
								<option value="1">Molineras</option>
								<option value="2">Desreguladas</option>
							</select>
						</td>
				   </tr>
				   <tr>				  
						<td rowspan="2" valign="top" colspan="5" align="right">					
							<table>
								<tr>
									<td>		
										<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
									</td>
									<td>
										<div id="<portlet:namespace />limpiar">
											<input type="button" value="<liferay-ui:message key="limpiar-campos" />" onClick="limpiarCampos();" />
										</div>
									</td>
									<td>															
										<div id="<portlet:namespace />altaLlamado">
											<input type="button" value="<liferay-ui:message key="nuevo-llamado" />" onClick="nuevoLlamado();" />
										</div>									
									</td>		
								</tr>
							</table>							
						</td>													
						
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</table>	     
				<div align="center" id="<portlet:namespace />empresasSeguimientoBuscando">			
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>	
				<table>
					<tr>
						<td>
							<div align="center" id="<portlet:namespace />empresasSeguimiento" style="width: 1200px;" >			
											
							</div>
						</td>
					</tr>
				</table> 	  
			</div>
		</fieldset>	
		
		
			
<script type="text/javascript">	
	
	jQuery('#<portlet:namespace />buscandoActa').hide();	
	jQuery('#<portlet:namespace />buscandoConv').hide();
	jQuery('#<portlet:namespace />buscandoCalc').hide();
	jQuery('#<portlet:namespace />buscandoLlamados').hide();
	jQuery('#<portlet:namespace />buscandoReci').hide();
	jQuery('#<portlet:namespace />altaLlamado').hide();
	jQuery('#<portlet:namespace />infoEmpresa').hide();
	jQuery('#<portlet:namespace />empresasSeguimientoBuscando').hide();		
	//jQuery('#<portlet:namespace />busquedaEmpresa').hide();
	//limpiarCampos();
	jQuery('#<portlet:namespace />sucursal_entidad<%=sufi%>').val('000');

	function cambiaCuit<%=sufi%>(){
	}
	function limpiarCampos(){
		jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val('');
		jQuery('#<portlet:namespace />sucursal_entidad<%=sufi%>').val('000');
		jQuery('#<portlet:namespace />entidad<%=sufi%>').val('');
		jQuery('#<portlet:namespace />estadoBuscar').val('0');
	}
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		if(jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val()!='' &&		   
		   jQuery('#<portlet:namespace />entidad<%=sufi%>').val()!='') {
		   /* jQuery('#<portlet:namespace />altaLlamado').show(); */
		}
			
		var cuit=jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val();
		var lote=jQuery('#<portlet:namespace />lote').val();
		var razon=jQuery('#<portlet:namespace />entidad<%=sufi%>').val();
		var ramo=jQuery('#<portlet:namespace />tipo').val();

		if(!<portlet:namespace />validarBusqueda(cuit,razon,lote)){
			return false;
		}
		jQuery('#<portlet:namespace />empresasSeguimientoBuscando').show();
		//NUEVA LISTA EMPRESAS
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_empresa&cuit='+cuit+'&lote='+lote+'&razon='+escape(razon)+'&ramo='+ramo;
		jQuery('#<portlet:namespace />empresasSeguimiento').load(url, function() {
																			jQuery('#<portlet:namespace />empresasSeguimientoBuscando').hide();								
																			/* jQuery('#<portlet:namespace />altaLlamado').show(); */
	        															  } ); 
	    
	});
	
	function buscarSeguimiento(cuit,razon,lote){		
		jQuery('#cuit').val(cuit);
		jQuery('#razon').val(razon);	
		jQuery('#razon').val(lote);
		if(!<portlet:namespace />validarBusqueda(cuit,razon,lote)){
			return false;
		}				
		//BUSCO ESTADO
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/estudio_isidro/buscar_seguimiento_molinera" /> ///estudio_isidro/buscar_seguimiento
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';		
		submitForm(document.<portlet:namespace />fm, url);
	}		
	
<%-- 	function buscarRestoPagina(cuit,razon){		
		jQuery('#cuit').val(cuit);
		jQuery('#razon').val(razon);				
		if(!<portlet:namespace />validarBusqueda(cuit)){
			return false;
		}				
		//BUSCO ESTADO
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/estudio_isidro/buscar_seguimiento" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';			
		submitForm(document.<portlet:namespace />fm, url);
	}	 --%>	
	
	
	/* function buscarEmpresaEstado(cuit, estado){		
		jQuery('#estado').val(estado);
		buscarEmpresa(cuit);
	} */
	
	
	function nuevoLlamado(){		    
		var cuit=jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val();		
		var razon=jQuery('#<portlet:namespace />entidad<%=sufi%>').val();
		buscarRestoPagina(cuit,razon);
		jQuery('#<portlet:namespace />nuevoLlamado').show();
	}	
	
	
	function <portlet:namespace />validarBusqueda(cuit,razon,lote){	

		if(trim(razon).length==0 
		&& trim(cuit).length<11
		/* && jQuery('#<portlet:namespace />estadoBuscar').val() == 0 */
		<%-- && jQuery('#<portlet:namespace />entidad<%=sufi%>').val()==''  --%>
		&& jQuery('#<portlet:namespace />tipo').val()=='0'
		&& trim(lote).length==0){			
			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}
		
	function busquedaEmpresa() {
		jQuery('#<portlet:namespace />busquedaEmpresa').show();
	}
	
	function ocultaBusquedaEmpresa() {
		//jQuery('#<portlet:namespace />busquedaEmpresa').hide();
	}
	
	function verInfoEmpresaSinParametros() {	
		var cuit_empleador=jQuery("#<portlet:namespace />cuit_entidad<%=sufi%>").val();		
		var sucu_empleador=jQuery("#<portlet:namespace />sucursal_entidad<%=sufi%>").val();
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1300});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}
	
	function verInfoEmpresa(cuit_empleador, sucu_empleador) {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1300});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}	
	
	function refrescarDatosEmpresa(cuit) {
		 if(popup!=null){
             Liferay.Popup.close(popup);
             jQuery("#<portlet:namespace />cuit_entidad<%=sufi%>").val(cuit);
             jQuery('#<portlet:namespace />buscar').trigger( "click" );
         }   
	}
	
	
</script>

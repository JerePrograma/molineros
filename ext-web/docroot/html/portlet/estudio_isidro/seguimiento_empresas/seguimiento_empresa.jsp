<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<liferay-ui:error exception="<%= DuplicateActaIdException.class %>" message="acta-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.FaltaFechaCierreActaException.class %>" message="falta-fecha-cierre" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-menor-fecha-contable" />
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		Empresa llest=((LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa();
		//Empresa llest=((LlamadosEstudio)renderRequest.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa();
		List<EstadoEmpresa> estadosEmpresas = (ArrayList<EstadoEmpresa>) request.getSession().getAttribute(WebKeysEstudioIsidro.ESTADOS_EMPRESA);
		if(estadosEmpresas==null){
			estadosEmpresas = EmpresaServiceUtil.getEstadosEmpresa();
			request.getSession().setAttribute(WebKeysEstudioIsidro.ESTADOS_EMPRESA, estadosEmpresas);
		}
		
		String portlet_name = ParamUtil.getString(request, "portlet_name");

		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "empresas";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		if(renderResponse.getNamespace().equals("_AFI_1_")){
			portlet_name = "afiliados";
		}
		if(renderResponse.getNamespace().equals("_CGT_1_")){
			portlet_name = "cgt";
		}
		if(renderResponse.getNamespace().equals("_EST_1_")){
			portlet_name = "estudio_isidro";
		}
		
		if(renderResponse.getNamespace().equals("_EMP_1_")){
			portlet_name = "empresas";
		}
		/* String estado=null!=llest && llest.getEstado()!=null?llest.getEstado():""; */
		
%>
<div id="allPage">
<div align="center" id="<portlet:namespace />nuevoLlamado">
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="nuevo-llamado" /></legend>
			<div id="<portlet:namespace />infoEmpresa">	
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/info_empresa.jsp">
				</liferay-util:include>						
			</div>					
			<table style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td>
						<label><liferay-ui:message key="fecha" />: &nbsp;</label>
						<input type="hidden" id="cuit" name="cuit" value="<%=llest.getCuit()%>"/>
					</td>
					<td>
						<liferay-ui:input-date dayParam="fechaLlamadoDia"
							dayValue="<%= fechaHoy.get(Calendar.DATE) %>"
							monthParam="fechaLlamadoMes"
							monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
							yearParam="fechaLlamadoAnio"
							yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
							yearRangeStart="<%= fechaHoy.get(Calendar.YEAR)%>"
							yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR)+1%>"
							firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"/>
					</td>
					<td align="left">
						<liferay-ui:message key="carta-documento" />:
					</td>
					<td align="left">
						<input type="text" name="carta_doc" id="carta_doc" value="<%=(llest != null && null!=llest.getCartaDoc())?llest.getCartaDoc():""%>"/></label>
					</td>
					<td align="left">
						<liferay-ui:message key="ubicacion-carpeta" />:
					</td>			
					<td align="left">
						<input type="text" name="ubicacion_carpeta" id="ubicacion_carpeta" value="<%=(llest != null && null!=llest.getUbicacionCarpeta())?llest.getUbicacionCarpeta():""%>"/></label>
					</td>					
					<td><liferay-ui:message key="estado" />: </td>					
					<td>
						<select id="estadoLlamada" name="estadoLlamada">
							<option value="ABIERTO" <%if(llest!=null && llest.getEstado().equals("ABIERTO")){%>selected<%}%>>Abierto</option>
							<option value="CERRADO" <%if(llest!=null && llest.getEstado().equals("CERRADO")){%>selected<%}%>>Cerrado</option>
						</select>
					</td>												
				</tr>
				<tr>
					<td align="left" valign="top">								
						<label><liferay-ui:message key="tipo-contacto" />:&nbsp;</label> 
					</td>		
					<td align="left" valign="top">
						<select id="tipo_contacto" name="tipo_contacto">
							<option value="PERSONAL">PERSONAL</option>
							<option value="TELEFONICO" selected>TELEFONICO</option>
						</select>
					</td>
					<td valign="top">
						<label><liferay-ui:message key="observaciones" />:&nbsp;</label>
					</td>	
					<td colspan="5" align="left" valign="top">
						<textarea id="<portlet:namespace />observaciones" name="<portlet:namespace />observaciones" cols="90" rows="5"></textarea>
					</td>
				</tr>
				<tr>	
					<td colspan="8" align="left">
						<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarLlamado();" />
					</td>					
				</tr>
			</table>
			
		</fieldset>
		</div>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="calculo-deuda" /></legend>
			<div align="center" id="<portlet:namespace />buscandoCalc">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaCalculoDiv" style="height:120px; overflow: scroll;">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/deuda_search_result.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
			</div>
			<table width="100%">
				<tr>
					<td colspan="2">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="button" value="<liferay-ui:message key="alta-deuda" />" onClick="<portlet:namespace />altaActa('<%=llest.getCuit()%>','<%=llest.isMolinera()%>');" />&nbsp;
						<input type="button" value="<liferay-ui:message key="deudas-noOS" />" onClick="<portlet:namespace />altaActaMolinera('<%=llest.getCuit()%>');" />						
					</td>					
				</tr>
			</table>
			
		</fieldset>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="contactos" /></legend>
			<div align="center" id="<portlet:namespace />buscandoLlamados">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaLlamados" style="height:240px; overflow: scroll;">		
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/llamados_search_result.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>							
			</div>
		</fieldset>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="actas" /></legend>
			<div align="center" id="<portlet:namespace />buscandoActa">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<input type="hidden" id="<portlet:namespace />cur" name="<portlet:namespace />cur"/>
			<div align="center" id="<portlet:namespace />busquedaActaDiv" style="height:200px; overflow: scroll;">	
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/actas_search_result_seguimiento.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>						
			</div>
		</fieldset>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="convenios" /></legend>
			<div align="center" id="<portlet:namespace />buscandoConv">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaConvenioDiv" style="height:120px; overflow: scroll;">		
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/convenios_search_result_seguimiento.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
			</div>
		</fieldset>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="recibos" /></legend>
			<div align="center" id="<portlet:namespace />buscandoReci">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaReciboDiv" style="height:120px; overflow: scroll;">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/recibos_search_result_seguimiento.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
			</div>
		</fieldset>
</div>
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoActa').hide();	
	jQuery('#<portlet:namespace />buscandoConv').hide();
	jQuery('#<portlet:namespace />buscandoCalc').hide();
	jQuery('#<portlet:namespace />buscandoLlamados').hide();
	jQuery('#<portlet:namespace />buscandoReci').hide();
	jQuery('#<portlet:namespace />altaLlamado').hide();	
	//jQuery('#<portlet:namespace />infoEmpresa').hide();	
	jQuery('#<portlet:namespace />empresasSeguimientoBuscando').hide();
	//jQuery('#<portlet:namespace />empresasSeguimiento').hide();	
	//jQuery('#<portlet:namespace />busquedaEmpresa').hide();
	function cambiaCuit<%=sufi%>(){
	}
	
	/* function buscarEmpresaEstado(cuit, estado){		
		jQuery('#estado').val(estado);
		buscarEmpresa(cuit);
	}	 */
	

	function <portlet:namespace />grabarLlamado() {
		var cuit=jQuery('#cuit').val();
		var llamadoDia=jQuery('#<portlet:namespace />fechaLlamadoDia').val();
		var llamadoMes=parseInt(jQuery('#<portlet:namespace />fechaLlamadoMes').val())+1;
		var llamadoAnio=jQuery('#<portlet:namespace />fechaLlamadoAnio').val();
		var fechaLlamado=llamadoDia+'/'+llamadoMes+'/'+llamadoAnio;
		var observaciones=jQuery('#<portlet:namespace />observaciones').val();
		var tipoContacto=jQuery('#tipo_contacto').val();		
		/* var estado=jQuery('#estado').val(); */
		var estado=jQuery('#<portlet:namespace />estadoEmpresa').val();
		var cartaDoc=jQuery('#carta_doc').val();
		var ubicacionCarpeta=jQuery('#ubicacion_carpeta').val();
		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/grabar_llamado&cuit='+cuit+'&observaciones='+encodeURI(observaciones)
		+'&fechaLlamado='+fechaLlamado+'&estadoEmpresa='+encodeURI(estado)+'&cartaDoc='+encodeURI(cartaDoc)+'&ubicacionCarpeta='+encodeURI(ubicacionCarpeta)+'&tipoContacto='+tipoContacto;
		 url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery('#<portlet:namespace />busquedaLlamados').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoLlamados').hide();       
        																jQuery('#<portlet:namespace />observaciones').val(''); 																            															
        															  }
        ); 
		 
	}
	
	function busquedaEmpresa() {
		jQuery('#<portlet:namespace />busquedaEmpresa').show();
	}
	
	function ocultaBusquedaEmpresa() {
		jQuery('#<portlet:namespace />busquedaEmpresa').hide();
	}
	
	function verInfoEmpresaSinParametros() {	
		var cuit_empleador=jQuery("#<portlet:namespace />cuit_entidad<%=sufi%>").val();		
		var sucu_empleador=jQuery("#<portlet:namespace />sucursal_entidad<%=sufi%>").val();
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:920});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}
	
	function verInfoEmpresa(cuit_empleador, sucu_empleador) {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1200});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}
	var popupActa;
	function <portlet:namespace />altaActa(cuit, molinera) {						
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_actas_entry&fromBusquedaDeuda=fromBusquedaDeuda&cuit='+cuit+'&molinera='+molinera;
		//document.<portlet:namespace />fm.method = 'post';
		popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:920});
		jQuery(popupActa).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}    
	
	function <portlet:namespace />altaActaMolinera(cuit) {				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/edit_actas_no_os_entry&fromBusquedaDeuda=true&cuit='+cuit;
		//document.<portlet:namespace />fm.method = 'post';
		popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:920});
		jQuery(popupActa).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}
	
	function editarRecibo(id) {
				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_recibos_entry&recibo_id='+id;
		//document.<portlet:namespace />fm.method = 'post';
		popupRecibo = Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,width:920});
		jQuery(popupRecibo).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}   
	
	function anularRecibo(id) {				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/anular_recibos_entry&recibo_id='+id;
		//document.<portlet:namespace />fm.method = 'post';
		popupRecibo = Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,width:920});
		jQuery(popupRecibo).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	} 
	
	function refrescarDatosEmpresa(cuit) {				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/refrescar_empresa';
		 if(popup!=null){
             Liferay.Popup.close(popup);
             jQuery('#<portlet:namespace />infoEmpresa').load(url);
         }   
	}
	
</script>
		
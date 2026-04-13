<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="gestiones" /></legend>
<portlet:defineObjects/>
<% 
Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
fechaHoy.setTime(new Date());

List<TipoLoteEmpresa> tiposLoteEmp = (ArrayList<TipoLoteEmpresa>) request.getSession().getAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION);
List<EstadoGestion> estadosGestion = (ArrayList<EstadoGestion>) request.getSession().getAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION);


boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO);
LlamadosEstudio llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
List<Llamado> llamados= llest.getLlamados();
String estadoLlamado = (llamados != null && llamados.size()>0 && null!=llamados.get(0).getEstado())?llamados.get(0).getEstado():"";
Integer estadoGestion = (llamados != null && llamados.size()>0 && null!=llamados.get(0).getEstadoGestion().getId())?llamados.get(0).getEstadoGestion().getId():0;
Integer loteAsignado = (llamados != null && llamados.size()>0 && null!=llamados.get(0).getLote())?llamados.get(0).getLote():null;
String tipoLoteAsignado = (llamados != null && llamados.size()>0 && null!=llamados.get(0).getLote())?llamados.get(0).getTipoLote():null;

boolean molinera=false;
if(llest!=null){
   Empresa empresa=llest.getEmpresa();
   if(empresa!=null){
	   molinera=empresa.isMolinera();
   }
}   
%>

<div id="<portlet:namespace />nuevo_llamado_div">
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">				
				<tr>
					<td colspan="2">
						<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
							<tr>
								<td>
									<label><liferay-ui:message key="fecha" />: &nbsp;</label>
									<input type="hidden" id="cuit" name="cuit" value="<%=llest.getCuit()%>"/>
									<input type="hidden" id="<portlet:namespace />llamado_id" name="<portlet:namespace />llamado_id" value=""/>
									<input type="hidden" id="<portlet:namespace />googleEvent" name="<portlet:namespace />googleEvent" value=""/>
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
								<td>
									<liferay-ui:message key="carta-documento" />: 
								</td>
								<td>	
									<input type="text" name="carta_doc" id="carta_doc" value="<%=(llamados != null && llamados.size()>0 && null!=llamados.get(0).getCartaDocumento())?llamados.get(0).getCartaDocumento():""%>"/></label>
								</td>					
								<td><liferay-ui:message key="situacion" />:</td>					
								<td>
									<select id="<portlet:namespace />estadoLlamada" name="<portlet:namespace />estadoLlamada">
										<option value="ABIERTO" <%if(estadoLlamado!=null && estadoLlamado.equals("ABIERTO")){%>selected<%}%>>Abierto</option>
										<option value="CERRADO" <%if(estadoLlamado!=null && estadoLlamado.equals("CERRADO")){%>selected<%}%>>Cerrado</option>
									</select>
								</td>
								<td>
									<liferay-ui:message key="estado" />:
								</td>													
								<td>
									<select name="<portlet:namespace />estadoGestion" id="<portlet:namespace />estadoGestion" 
										style="width: 240px; ">
										<option value='0'><liferay-ui:message key="seleccione-estado" /></option>
										<%for (EstadoGestion estado : estadosGestion) { %>
												<option value="<%= estado.getId()%>" 
														<%if(estadoGestion == estado.getId()){ %>selected="selected" <%} %> ><%=estado.getDescripcion()%></option>
								
										<% } %>
									</select>
								</td>
							</tr>
						</table>
					</td>
				</tr>			
				<tr>
					<td valign="top">
						<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
							<tr>
							<td valign="top">
								<liferay-ui:message key="nro-lote" />:
							</td>													
							<td valign="top">
							<input id="<portlet:namespace />lote"
									name="<portlet:namespace />lote" size="15" type="text"
									value="<%=loteAsignado%>" readonly="readonly" /></td>
							<td valign="top">
								<liferay-ui:message key="tipo-lote" />:
							</td>													
							<td valign="top">
							<select id="<portlet:namespace />tipoLote" name="<portlet:namespace />tipoLote" disabled="disabled" >
							       <option value="" <%=Validator.isNotNull(tipoLoteAsignado) && tipoLoteAsignado.equals("") ? "selected" : ""  %>></option>
							       <%for (TipoLoteEmpresa tle : tiposLoteEmp){ %>
									<option value="<%=tle.getTipoLote()%>" <%=Validator.isNotNull(tipoLoteAsignado) && tipoLoteAsignado.equals(tle.getTipoLote())?"selected" : "" %> ><%=tle.getDescripcionLote() %></option>								
								   <%} %>
							</select>
							</td>
							</tr>
							<tr>
						       <td colspan="4">
						           <fieldset class="block-labels"> 
				                   <legend>Agenda Google</legend>
						           <table class="lfr-table">
								      <tr>        
								           <td>
										     <label><liferay-ui:message key="fecha-agenda" />: &nbsp;</label>						
									       </td>
									       <td>
										   <liferay-ui:input-date dayParam="fechaAgendaDia"							
											dayNullable="true" 							
											monthParam="fechaAgendaMes"
											monthNullable="true"												
											yearParam="fechaAgendaAnio"
											yearNullable="true"							
											yearRangeStart="<%= fechaHoy.get(Calendar.YEAR)%>"
											yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR)+1%>"
											firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"/>-
										    <liferay-ui:input-time amPmParam="ampm" hourParam="horaAgenda"  hourNullable="true" minuteParam="minutoAgenda" minuteNullable="true" minuteInterval="10"/>	
								            </td>
								      </tr>
								    </table>
								    </fieldset>
								           	
								 </td>   
								</tr>  
						</table>
					</td>		
					<td valign="top">
						<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
							<tr>
								<td  valign="top">
									<label><liferay-ui:message key="observaciones" />: &nbsp;</label>
								</td>	
								<td colspan="3" valign="top">
					         	 	
						     		<textarea id="<portlet:namespace />observaciones_llamado" name="<portlet:namespace />observaciones_llamado" cols="80" rows="5"></textarea>
					          	</td>
					         </tr> 	
					   </table>
					</td>     	 																	
		     </tr>
		     <tr>
		     	 <td align="center" colspan="2">
					 <input type="button" value="<liferay-ui:message key="save" />" onClick="javascript:<portlet:namespace />grabarLlamado();" />
				</td>
		     </tr>
		 </table> 
</div>
	
<div id="<portlet:namespace />agregar_llamado_div">
	<a href="javascript:agregarLlamado('abrir');">Añadir gestión</a>		
</div>
<div id="<portlet:namespace />no_agregar_llamado_div">
	<a href="javascript:agregarLlamado('cerrar');">Cerrar añadir gestión</a>		
</div>
<%

			        NumberFormat formatter = new DecimalFormat("#0.00");
			        SimpleDateFormat sdf = new  SimpleDateFormat("dd/MM/yyyy HH:mm");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("fecha");
			 		headerNames.add("usuario");			 		
			 		headerNames.add("observaciones");			 		
			 		headerNames.add("estado");
			 		headerNames.add("fecha-agenda");
			 		headerNames.add("Tipo Lote");
			 		headerNames.add("Nro Lote");
			 		headerNames.add("Importe Lote");
			 		headerNames.add("delete");
			 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,100, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-llamados-were-found"));
				
					if(null!=llamados){		 	
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < llamados.size(); i++) {
					 				Llamado llamado = (Llamado) llamados.get(i);
					 				Calendar fechaCalendar=Calendar.getInstance();
					 				fechaCalendar.setTime(llamado.getFecha());
				 					ResultRow row = new ResultRow(llamado, llamado.getCuit(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
//					 				row.addText(llamado.getFechaAsString());
									row.addText(sdf.format(llamado.getFecha()));
									row.addText(llamado.getUser());
									row.addText(llamado.getObservaciones());									
									/* row.addText(llamado.getEstado()); */
									row.addText(llamado.getEstadoGestion().getDescripcion() );
									row.addText(llamado.getFechaAgendaAsString());
									row.addText(null!=llamado.getTipoLote()?llamado.getTipoLote():"");
									row.addText(null!=llamado.getLote()?llamado.getLote().toString():"");
									
									row.addText(null!=llamado.getDeudaLote() && 
											llamado.getEstadoGestion()!=null &&
													llamado.getEstadoGestion().getDescripcion()!=null &&
											"ABIERTO".equalsIgnoreCase(llamado.getEstadoGestion().getDescripcion().trim())?formatter.format(llamado.getDeudaLote()):"");
									
									StringBuilder sb=new StringBuilder();
									sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
							 		sb.append(themeDisplay.getPathThemeImages());
							 		sb.append("/common/edit.png\" onClick=\"javascript:editarLlamado('");			 			
							 		sb.append(llamado.getId());		
							 		sb.append("','");
							 		sb.append(fechaCalendar.get(Calendar.DATE));
							 		sb.append("','");
							 		sb.append(fechaCalendar.get(Calendar.MONTH));
							 		sb.append("','");
							 		sb.append(fechaCalendar.get(Calendar.YEAR));
							 		sb.append("','");
							 		sb.append(llamado.getCartaDocumento());
							 		sb.append("','");
							 		sb.append(llamado.getUbicacionCarpeta());
							 		sb.append("','");
							 		sb.append(llamado.getEstado() );
							 		
							 		sb.append("','");
							 		sb.append(llamado.getEstadoGestion().getId());
							 		
							 		
							 		sb.append("','");
							 		sb.append(llamado.getTipoContacto());
							 		sb.append("','");
							 		sb.append(llamado.getObservaciones());
							 		fechaCalendar=null;
							 		if(null!=llamado.getFechaAgenda()){
							 			fechaCalendar=Calendar.getInstance();
							 			fechaCalendar.setTime(llamado.getFechaAgenda());
							 		}
							 		sb.append("','");
							 		sb.append(null!=fechaCalendar?fechaCalendar.get(Calendar.DATE):"");
							 		sb.append("','");
							 		sb.append(null!=fechaCalendar?fechaCalendar.get(Calendar.MONTH):"");
							 		sb.append("','");
							 		sb.append(null!=fechaCalendar?fechaCalendar.get(Calendar.YEAR):"");
							 		sb.append("','");
							 		sb.append(null!=fechaCalendar?fechaCalendar.get(Calendar.HOUR_OF_DAY):"");
							 		sb.append("','");
							 		sb.append(null!=fechaCalendar?fechaCalendar.get(Calendar.MINUTE):"");
							 		
							 		sb.append("','");
							 		sb.append(null!=llamado.getTipoLote()?llamado.getTipoLote():"");
							 		sb.append("','");
							 		sb.append(null!=llamado.getLote()?llamado.getLote():"");
							 		
							 		sb.append("','");
							 		sb.append(null!=llamado.getGoogleEvent()?llamado.getGoogleEvent():"");
							 		sb.append("');\" />");
									sb.append("/");
							 		sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
							 		sb.append(themeDisplay.getPathThemeImages());
							 		sb.append("/common/delete.png\" onClick=\"javascript:borrarLlamado('");			 			
							 		sb.append(llamado.getId());							 		
							 		sb.append("');\" />");
							 		if(llamado.getUser().equalsIgnoreCase(user.getScreenName())) {
							 			row.addText(sb.toString());
									}else{
										row.addText("");
									}
									resultRows.add(row);
					 	}		
				 			
					 }
				 	
			%>

      																
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

</fieldset>

<!--if (total >1){ >
<@ include file="/html/portlet/utils/paginator/paginator.jsp" >
<} -->

<script type="text/javascript">
jQuery('#<portlet:namespace />cantidad_gestiones').html('<%=null!=llest && llest.getLlamados()!=null?llest.getLlamados().size():0%>');
jQuery('#<portlet:namespace />nuevo_llamado_div').hide();
jQuery('#<portlet:namespace />no_agregar_llamado_div').hide();

 function agregarLlamado(accion){	
	document.getElementById("<portlet:namespace />tipoLote").disabled=true;
	if(accion=="abrir"){
		jQuery('#<portlet:namespace />llamado_id').val('');
		jQuery('#<portlet:namespace />nuevo_llamado_div').show();
		jQuery('#<portlet:namespace />no_agregar_llamado_div').show();
		jQuery('#<portlet:namespace />agregar_llamado_div').hide();	
		limpiarLlamado();
		
		proponeLote();
		
		
	}else if(accion=="editar"){
		jQuery('#<portlet:namespace />nuevo_llamado_div').show();
		jQuery('#<portlet:namespace />no_agregar_llamado_div').show();
		jQuery('#<portlet:namespace />agregar_llamado_div').hide();
	}else{
		jQuery('#<portlet:namespace />nuevo_llamado_div').hide();
		jQuery('#<portlet:namespace />no_agregar_llamado_div').hide();
		jQuery('#<portlet:namespace />agregar_llamado_div').show();
	}
 }
 
 function <portlet:namespace />paginar(cur){
    var cuit=jQuery('#<portlet:namespace />cuit_entidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_llamados&cuit='+cuit;
		url += '&cur=' + cur;
		url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery('#<portlet:namespace />busquedaLlamados').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoLlamados').hide();            															
        															  }
        );
 }
 
 function <portlet:namespace />grabarLlamado() { 
	    document.getElementById("<portlet:namespace />tipoLote").disabled=false;
	 	var cuit=jQuery('#<portlet:namespace />cuit').val();
		var llamadoDia=jQuery('#<portlet:namespace />fechaLlamadoDia').val();
		var llamadoMes=parseInt(jQuery('#<portlet:namespace />fechaLlamadoMes').val())+1;
		var llamadoAnio=jQuery('#<portlet:namespace />fechaLlamadoAnio').val();
		var agendaDia=jQuery('#<portlet:namespace />fechaAgendaDia').val();
		var agendaMes=parseInt(jQuery('#<portlet:namespace />fechaAgendaMes').val())+1;
		var agendaAnio=jQuery('#<portlet:namespace />fechaAgendaAnio').val();		
		var fechaLlamado=llamadoDia+'/'+llamadoMes+'/'+llamadoAnio;
		var fechaAgenda=agendaDia+'/'+agendaMes+'/'+agendaAnio;
		var observaciones=jQuery('#<portlet:namespace />observaciones_llamado').val();		
		/* var tipoContacto=jQuery('#tipo_contacto').val(); */		
		var estadoLlamada=jQuery('#<portlet:namespace />estadoLlamada').val();	
		var estadoGestion=jQuery('#<portlet:namespace />estadoGestion').val();	 
		var cartaDoc=jQuery('#carta_doc').val();
		/* var ubicacionCarpeta=jQuery('#ubicacion_carpeta').val(); */
		var horaAgenda=jQuery('select[name="<portlet:namespace />horaAgenda"]').val();
		var minutoAgenda=jQuery('select[name="<portlet:namespace />minutoAgenda"]').val();
		var id=jQuery('#<portlet:namespace />llamado_id').val();
		var tipoLote=jQuery('#<portlet:namespace />tipoLote').val();
		var nroLote=jQuery('#<portlet:namespace />lote').val();
		var googleEvent=jQuery('#<portlet:namespace />googleEvent').val();
		
		if(!<portlet:namespace />validarGestion(estadoGestion)){
			return false;
		}
		
		var molinera='<%=molinera%>';
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/grabar_llamado';
	
		url +='&cuit='+cuit+'&observaciones='+encodeURI(observaciones)+'&fechaLlamado='+fechaLlamado+'&cartaDoc='+encodeURI(cartaDoc);
		url +='&estadoLlamada='+estadoLlamada; 
		url +='&estadoGestion='+estadoGestion;
		url +='&fechaAgenda='+fechaAgenda;
		url +='&horaAgenda='+horaAgenda;
		url +='&minutoAgenda='+minutoAgenda;
		url +='&tipoLote='+tipoLote;
		if(parseInt(nroLote) >= 0){
			url +='&nroLote='+nroLote;
 		}
		url +='&id='+id;
		url +='&googleEvent='+googleEvent;
		url += '&rnd=' + Math.floor(Math.random()*100);	
		url +='&molinera='+molinera;

		jQuery('#<portlet:namespace />busquedaLlamados').load(url, function() {
     																jQuery('#<portlet:namespace />buscandoLlamados').hide();       
     																jQuery('#<portlet:namespace />observaciones').val(''); 			
     																jQuery('#<portlet:namespace />fechaLlamadoDia').val('');
      																jQuery('#<portlet:namespace />fechaLlamadoMes').val('');
      																jQuery('#<portlet:namespace />fechaLlamadoAnio').val('');
      																jQuery('#carta_doc').val('');
      																jQuery('#ubicacion_carpeta').val('');
      																jQuery('#<portlet:namespace />estadoLlamada').val('ABIERTO');
      																jQuery('#<portlet:namespace />estadoGestion').val('0');
      																jQuery('#tipo_contacto').val('');
      																jQuery('#<portlet:namespace />observaciones_llamado').val('');		
      																jQuery('#<portlet:namespace />fechaAgendaDia').val('');
      																jQuery('#<portlet:namespace />fechaAgendaMes').val('');
      																jQuery('#<portlet:namespace />fechaAgendaAnio').val('');
      																jQuery('select[name="<portlet:namespace />horaAgenda"]').val('');  																
      																jQuery('select[name="<portlet:namespace />minutoAgenda"]').val('');
      																jQuery('#<portlet:namespace />tipoLote').val('');
      																document.getElementById("<portlet:namespace />tipoLote").disabled=true;
      																jQuery('#<portlet:namespace />lote').val('');
      																
      																if(jQuery('#<portlet:namespace />llamado_id').val()!=''){
      																	jQuery('#<portlet:namespace />llamado_id').val('');
      																	agregarLlamado('cerrar');
      																	
      																}
      																jQuery('#<portlet:namespace />fechaAgendaAnio');    
      																
      																
     															  }
     	);
		 
	}
 
 	function borrarLlamado(id) { 		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/grabar_llamado&accion=borrar&id='+id;
		url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery('#<portlet:namespace />busquedaLlamados').load(url, function() {
  																jQuery('#<portlet:namespace />buscandoLlamados').hide();       
  																jQuery('#<portlet:namespace />observaciones').val('');  														 		
  															  }
  		);
		 
	}
 	
 	function editarLlamado(id, diall, mesll, anioll, cartaDoc, ubicacion, estado,estadoGestion, tipo, observacion, diaAg, mesAg, anioAg, horaAg, minAg,tipoLote,nroLote,googleEvent) { 		
 		jQuery('#<portlet:namespace />llamado_id').val(id);
 		jQuery('#<portlet:namespace />fechaLlamadoDia').val(diall);
		jQuery('#<portlet:namespace />fechaLlamadoMes').val(mesll);
		jQuery('#<portlet:namespace />fechaLlamadoAnio').val(anioll);
		jQuery('#carta_doc').val(cartaDoc);
		jQuery('#ubicacion_carpeta').val(ubicacion);
		jQuery('#<portlet:namespace />estadoLlamada').val(estado);
		jQuery('#tipo_contacto').val(tipo);
		jQuery('#<portlet:namespace />observaciones_llamado').val(observacion);		
		jQuery('#<portlet:namespace />fechaAgendaDia').val(diaAg);
		jQuery('#<portlet:namespace />fechaAgendaMes').val(mesAg);
		jQuery('#<portlet:namespace />fechaAgendaAnio').val(anioAg);
		jQuery('select[name="<portlet:namespace />horaAgenda"]').val(horaAg);		
		jQuery('select[name="<portlet:namespace />minutoAgenda"]').val(minAg);
		
		jQuery('#<portlet:namespace />tipoLote').val(tipoLote);
		jQuery('#<portlet:namespace />lote').val(nroLote);
		jQuery('#<portlet:namespace />googleEvent').val(googleEvent);
		jQuery('#<portlet:namespace />estadoGestion').val(estadoGestion);
		agregarLlamado("editar");		 
	}
 	
 	function limpiarLlamado(){
 		
 		var today = new Date();
 		var dd = today.getDate();
 		var mm = today.getMonth(); 
 		var yyyy = today.getFullYear();
 		
 		jQuery('#<portlet:namespace />observaciones').val(''); 			
		jQuery('#<portlet:namespace />fechaLlamadoDia').val(dd);
		jQuery('#<portlet:namespace />fechaLlamadoMes').val(mm);
		jQuery('#<portlet:namespace />fechaLlamadoAnio').val(yyyy);
			jQuery('#carta_doc').val('');
			jQuery('#ubicacion_carpeta').val('');
			jQuery('#<portlet:namespace />estadoLlamada').val('ABIERTO');
			jQuery('#<portlet:namespace />estadoGestion').val('0');
			jQuery('#tipo_contacto').val('');
			jQuery('#<portlet:namespace />observaciones_llamado').val('');		
			jQuery('#<portlet:namespace />fechaAgendaDia').val('');
			jQuery('#<portlet:namespace />fechaAgendaMes').val('');
			jQuery('#<portlet:namespace />fechaAgendaAnio').val('');
			jQuery('select[name="<portlet:namespace />horaAgenda"]').val('');  																
			jQuery('select[name="<portlet:namespace />minutoAgenda"]').val('');
			document.getElementById("<portlet:namespace />tipoLote").disabled=false;
			jQuery('#<portlet:namespace />tipoLote').val('');
			document.getElementById("<portlet:namespace />tipoLote").disabled=true;
			jQuery('#<portlet:namespace />lote').val('');
 	}
 	
 	function proponeLote(){
 	  var cuit=jQuery('#<portlet:namespace />cuit').val();
 	  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/propone_lote&cuit=';
		  url += cuit;
			
      jQuery.ajax({   
	    url: url,
	    async:false,
	    success: function(data){
		var obj = jQuery.parseJSON(data);
		var lote = obj.lote;
		var tipoLote =obj.tipoLote;

		if(lote == null || lote == 'null' ){
			lote = "";
		}
		jQuery('#<portlet:namespace />tipoLote').val(tipoLote);
		jQuery('#<portlet:namespace />lote').val(lote);
	   }});

 	}
 	
 	function <portlet:namespace />validarGestion(estadoGestion){	

		if(parseInt(estadoGestion) == 0 ){			
			
			alert('<liferay-ui:message key="Seleccione un Estado de Gestión"/>');
			return false;
		}else{
			return true;
		}		
		
	}
	
</script>
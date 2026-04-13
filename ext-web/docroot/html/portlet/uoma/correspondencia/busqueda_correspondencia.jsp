<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

		List<TipoCorrespondencia> tiposCorresp= CorrespondenciaServiceImpl.buscarTipoCorrespondencia();

		//verificar los calendars
 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		 				
%>
		
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-correspondencia" /></legend>
				<table class="lfr-table">		
					<tr>
						<td><label><liferay-ui:message key="destino" />:</label></td>
						<td>
							<select name="<portlet:namespace/>destino" id="<portlet:namespace/>destino" onChange="javascript:<portlet:namespace />cambiaDestino();">																
								<option value="ENTRANTE">Entrante</option>
								<option value="SALIENTE">Saliente</option>													
							</select>
						</td>	
						<td><label><liferay-ui:message key="lugar-recepcion" />:</label></td>
						<td colspan="11">
							<select name="<portlet:namespace/>edificio" id="<portlet:namespace/>edificio">
								<option value="" default="true">Sin Seleccionar</option>																
								<option value="MEXICO">Mexico</option>
								<option value="SANJUAN">San Juan</option>													
							</select>
						</td>											 					
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>			
					<tr>	
						<td>
						 	<div id="<portlet:namespace />fechaRecepcionLabel" name="<portlet:namespace />fechaRecepcionLabel">
						 		<label><liferay-ui:message key="fecha-recepcion-desde" />:</label>
						 	</div>
						 	<div id="<portlet:namespace />fechaEnvioLabel" name="<portlet:namespace />fechaEnvioLabel">
						 		<label><liferay-ui:message key="fecha-envio-desde" />:</label>
						 	</div>
						</td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH)-1 %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR)- 3 %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>
							<div id="<portlet:namespace />fechaRecepcionLabelHasta" name="<portlet:namespace />fechaRecepcionLabelHasta">
						 		<label><liferay-ui:message key="fecha-recepcion-hasta" />:</label>
						 	</div>
						 	<div id="<portlet:namespace />fechaEnvioLabelHasta" name="<portlet:namespace />fechaEnvioLabelHasta">
						 		<label><liferay-ui:message key="fecha-envio-hasta" />:</label>
						 	</div>
						</td>
						<td colspan="9">
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>						
											
					</tr>				
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="razon-prestador" />:</label></td>
						<td>
							<input id="<portlet:namespace />razon_prestador" name="<portlet:namespace />razon_prestador" size="20" maxlength="100" type="text"/>
						</td>
						<td colspan="10">	
							<div id="<portlet:namespace />divRemitenteDir" name="<portlet:namespace />divRemitenteDir">
									<liferay-util:include page='/html/portlet/uoma/editar_domicilio.jsp'>
											<liferay-util:param name="edit_mode" value="true"  />
											<liferay-util:param name="prefijo" value="remi" />
									</liferay-util:include>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="seccional" />:</label></td>	
						<td colspan="11">
									<liferay-util:include page='/html/portlet/uoma/busqueda_seccional_incidente.jsp'>											
									</liferay-util:include>
						</td>
					</tr>	
					<!--tr>
						<td><label><liferay-ui:message key="id-correspondencia-desde" />:</label></td>
						<td>
							<input id="<portlet:namespace />idCorrespondenciaDesde" name="<portlet:namespace />idCorrespondenciaDesde" size="4" maxlength="4" type="text" value="" />
						</td>
						<td><label><liferay-ui:message key="id-correspondencia-hasta" />:</label></td>
						<td>
							<input id="<portlet:namespace />idCorrespondenciaHasta" name="<portlet:namespace />idCorrespondenciaHasta" size="4" maxlength="4" type="text" value="" />
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="tipo-correspondencia" />:</label></td>
						<td colspan="11">
							<select name="<portlet:namespace/>tipoCorr" id="<portlet:namespace/>tipoCorr" onChange="javascript:<portlet:namespace />cambiaDestino();">
								<%for(TipoCorrespondencia tipo:tiposCorresp){%>
									<option value="0">Todos</option>
									<option value="<%=tipo.getIdTipo()%>"><%=tipo.getDescripcion()%></option>									
								<%}%>												
							</select>
						</td>										 					
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="remitente" />:</label></td>
						<td>
							<input id="<portlet:namespace />remitente" name="<portlet:namespace />remitente" size="20" maxlength="100" type="text" value="" />
						</td>
						<td><label><liferay-ui:message key="destinatario" />:</label></td>
						<td colspan="9">
							<input id="<portlet:namespace />destinatario" name="<portlet:namespace />destinatario" size="20" maxlength="100" type="text" value="" />
						</td>
																 															 					
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="receptor" />:</label></td>
						<td colspan="11">
							<input id="<portlet:namespace />receptor" name="<portlet:namespace />receptor" size="20" maxlength="100" type="text" value="" />
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr-->
															
					<tr>												
						<td align="right" colspan="12">
								<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" onClick="javascript: <portlet:namespace />buscarCorrespondencia();" type="button"/>&nbsp;															
								<input type="button" value="<liferay-ui:message key="nueva-correspondencia" />" onClick="<portlet:namespace />nuevaCorrespondencia();" />																					
						</td>						
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
			<div align="center" id="<portlet:namespace />busquedaCorrespondenciaDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />fechaEnvioLabel').hide();
	jQuery('#<portlet:namespace />fechaEnvioLabelHasta').hide();
	jQuery('#<portlet:namespace />buscando').hide();		
	
	function <portlet:namespace />nuevaCorrespondencia() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/editar_correspondencia" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />buscarCorrespondencia(){
		var destino=jQuery('#<portlet:namespace/>destino').val();
		var edificio=jQuery('#<portlet:namespace/>edificio').val();
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	    var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var desde_final=diaDesde+'/'+mesDesde+'/'+anioDesde;		
		var hasta_final=diaHasta+'/'+mesHasta+'/'+anioHasta;		
		var idCorrDesde=jQuery('#<portlet:namespace />idCorrespondenciaDesde').val();
		var idCorrHasta=jQuery('#<portlet:namespace />idCorrespondenciaHasta').val();
		var tipoCorr=jQuery('#<portlet:namespace/>tipoCorr').val();
		var remitente=jQuery('#<portlet:namespace/>remitente').val();
		var destinatario=jQuery('#<portlet:namespace/>destinatario').val();
		var receptor=jQuery('#<portlet:namespace/>receptor').val();
		var razon_prestador=jQuery('#<portlet:namespace />razon_prestador').val();
		var provincia= jQuery('#<portlet:namespace />provinciaremi').val();
		var localidad= jQuery('#<portlet:namespace />localidadremi').val();
		var seccional= jQuery('#<portlet:namespace />id_seccional_r').val();
				    
		var busquedaCorr = { "destino": destino, "edificio": edificio, "desde_final": desde_final, "hasta_final": hasta_final, "id_corr_desde": idCorrDesde, 
							 "id_corr_hasta": idCorrHasta, "tipoCorr": tipoCorr, "remitente": remitente, "destinatario": destinatario, "receptor": receptor,
							 "razon_prestador": razon_prestador, "provinciaremi": provincia, "localidadremi": localidad, "id_seccional_r": seccional};
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/uoma/buscar_correspondencia" /></portlet:renderURL>';
		
        jQuery('#<portlet:namespace />busquedaCorrespondenciaDiv').load(url,busquedaCorr, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	}
	
	function <portlet:namespace />cambiaDestino(){		
		var destino=jQuery('#<portlet:namespace/>destino').val();
		if(destino=="ENTRANTE"){
			jQuery('#<portlet:namespace />fechaEnvioLabel').hide();
			jQuery('#<portlet:namespace />fechaEnvioLabelHasta').hide();
			jQuery('#<portlet:namespace />fechaRecepcionLabel').show();
			jQuery('#<portlet:namespace />fechaRecepcionLabelHasta').show();
		}else{
			jQuery('#<portlet:namespace />fechaRecepcionLabel').hide();
			jQuery('#<portlet:namespace />fechaRecepcionLabelHasta').hide();
			jQuery('#<portlet:namespace />fechaEnvioLabel').show();
			jQuery('#<portlet:namespace />fechaEnvioLabelHasta').show();
		}		
			
	}
	
	function editaCorrespondencia(id_correspondencia){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/uoma/editar_correspondencia&id_correspondencia='+id_correspondencia;		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	<% if (request.getAttribute("paquete_id")!= null){ %>
		alert('Paquete generdo con id: <%=request.getAttribute("paquete_id")%>');
	<%}%>

</script>
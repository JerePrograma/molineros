<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		
		ArrayList<TemasNormasDDHH> temasNormasDH = (ArrayList<TemasNormasDDHH>) TraeListasServiceUtil.getTemasNormasDDHH();
%>


		<fieldset class="block-labels">
		
			<legend><liferay-ui:message key="busqueda-normasddhh" /></legend>
			
			<div id="<portlet:namespace />busquedaNormasDH"> 
				<table class="lfr-table" width="100%">
					<tr>	
						<td>
							<label><liferay-ui:message key="fecha-desde" />:</label>
						</td>
						<td>
							<span id="<portlet:namespace />fechaDesde">
							<liferay-ui:input-date
							dayParam="fechaDesdeDia"
							dayValue="<%= fechaHoy.get(Calendar.DATE) %>" 
							dayNullable="<%= true %>" 
							monthParam="fechaDesdeMes"
							monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"				
							monthNullable="<%= true %>"	
							yearParam="fechaDesdeAnio"
							yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 0 %>"
							firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
							</span>
						</td>
						<td>
							<label><liferay-ui:message key="fecha-hasta" />:</label>
						</td>
						<td>
							<span id="<portlet:namespace />fechaHasta">
							<liferay-ui:input-date
							dayParam="fechaHastaDia"
							dayValue="<%= fechaHoy.get(Calendar.DATE) %>" 
							dayNullable="<%= true %>" 
							monthParam="fechaHastaMes"
							monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"					
							yearParam="fechaHastaAnio"
							yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) +0 %>"
							yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
							</span>
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="sistema" />:</label></td>
						<td>
							<select name="<portlet:namespace/>sistemaselect" id="<portlet:namespace/>sistemaselect" <%if(!showABMButtons){%>disabled<%}%> onchange="javascript:filtrarTiposNormas();" >
								<option value="Todos" selected="selected" >Todos los sistemas</option>
								<%	for (String sist : WebKeysCGT.SISTEMA) {	%>
										<option value="<%= sist %>" >
											<%=sist%>
										</option>
								<%	}	%>					
							</select>
						</td>
						<td><label><liferay-ui:message key="tipo" />:</label></td>
						<td>
							<select name="<portlet:namespace/>tipo_norma" id="<portlet:namespace/>tipo_norma" onchange="javascript:filtrarTiposNormas();">
								<option value="0" selected="selected">Todos los Tipos de Normas DDHH</option>
							</select>
						</td>	
						<td><label><liferay-ui:message key="numero" />:</label></td>
						<td>
							<input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="20" maxlength="15"  type="text" 
							value="" />
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>			
					<tr>
						<td>
							<label><liferay-ui:message key="author" />:</label>
						</td>
						<td>
							<input id="<portlet:namespace />autor" name="<portlet:namespace />autor" size="20" maxlength="25"  type="text" value=""/>
						</td>	
						<td>
							<label><liferay-ui:message key="lugar" />:</label>
						</td>
						<td><input id="<portlet:namespace />lugar" name="<portlet:namespace />lugar" size="20" maxlength="25" type="text" value="" />
						</td>
						<td>
							<label><liferay-ui:message key="tema" />:</label>
						</td>
						<td>
							<select name="<portlet:namespace/>tema_norma" id="<portlet:namespace/>tema_norma">
								<option value="Todos" selected="selected">Todos los Temas de Normas DDHH</option>
								<%	for (TemasNormasDDHH temas : temasNormasDH ) {	%>
										<option value="<%= temas.getId() %>" >
											<%=temas.getDescripcion()%>
										</option>
								<%	}	%>				
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
				</table>	
						
				<table>
				   <tr>				  
						<td colspan="6" align="center">					
							<table>
								<tr>
									<td>		
										<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
									</td>
									<td>&nbsp;&nbsp;</td>									
									<td>							
									<%if(showABMButtons){%>								
										<div id="<portlet:namespace />nuevaNorma">
											<input type="button" value="<liferay-ui:message key="nueva-norma-ddhh" />" onClick="nuevaNorma();" />
										</div>			
									<%}%>						
									</td>		
								</tr>
							</table>							
						</td>													
						
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
				</table>
				<div align="center" id="<portlet:namespace />normaDHBuscando">			
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
							<div align="center" id="<portlet:namespace />resultadoNormasDH" style="width: 1200px;" >			
								<liferay-util:include page="/html/portlet/cgt_ddhh/normasDDHH/normasddhh_seguimiento_search_result.jsp">									
								</liferay-util:include>				
							</div>
						</td>
					</tr>
				</table>  
		</fieldset>
		<input type="hidden" id="<portlet:namespace />id_norma" name="<portlet:namespace />id_norma" />
			
<script type="text/javascript">	
	
	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeDia').val("");
		jQuery('#<portlet:namespace />fechaDesdeMes').val("");
		jQuery('#<portlet:namespace />fechaDesdeAnio').val("");
		jQuery('#<portlet:namespace />fechaHastaDia').val("");
		jQuery('#<portlet:namespace />fechaHastaMes').val("");
		jQuery('#<portlet:namespace />fechaHastaAnio').val("");
	}
	
	<portlet:namespace />resetValid();
	
	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional_r").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada_r").val("1")
		}
	}
	
	<portlet:namespace />initDateFields();

	jQuery('#<portlet:namespace />normaDHBuscando').hide();

	function nuevaNorma(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_norma_ddhh_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var sistema = jQuery('#<portlet:namespace />sistemaselect').val(); 
		var id_tipo_norma = jQuery('#<portlet:namespace />tipo_norma').val();
		var id_tema_norma = jQuery('#<portlet:namespace />tema_norma').val();
		var autor = jQuery('#<portlet:namespace />autor').val();
		var lugar = jQuery('#<portlet:namespace />lugar').val();
		var nro = jQuery('#<portlet:namespace />numero').val();
		
		jQuery('#<portlet:namespace />normaDHBuscando').show();
		//NUEVA LISTA NORMAS
	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/buscar_norma_ddhh&sistema='+sistema+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&id_tema='+id_tema_norma+'&id_tipo='+id_tipo_norma+'&autor='+encodeURI(autor)+'&lugar='+encodeURI(lugar)+
		'&numero='+encodeURI(nro);	
		
		jQuery('#<portlet:namespace />resultadoNormasDH').load(url, function() {
																			jQuery('#<portlet:namespace />normaDHBuscando').hide();
	        															  } );
	});
	
	function verNormaDDHH(id_norma){		
		jQuery('#<portlet:namespace />id_norma').val(id_norma);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_norma_ddhh" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function borrarNormaDDHH(id_norma){
		if(!confirm("<liferay-ui:message key='desea-borrar-la-normaDH'/>")){
				return false;
		}else{		
			
			var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
			var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
			var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
			var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
			var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
			var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
			var sistema = jQuery('#<portlet:namespace />sistemaselect').val();
			var id_tipo_norma = jQuery('#<portlet:namespace />tipo_norma').val();
			var id_tema_norma = jQuery('#<portlet:namespace />tema_norma').val();
			var autor = jQuery('#<portlet:namespace />autor').val();
			var lugar = jQuery('#<portlet:namespace />lugar').val();
			var nro = jQuery('#<portlet:namespace />numero').val();
			
			jQuery('#<portlet:namespace />normaDHBuscando').show();
			//NUEVA LISTA NORMAS DDHH
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_norma_ddhh&sistema='+sistema+
			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
			'&id_tema='+id_tema_norma+'&id_tipo='+id_tipo_norma+'&autor='+encodeURI(autor)+'&lugar='+encodeURI(lugar)+
			'&numero='+encodeURI(nro)+'&id_normaddhh='+id_norma;
			
			jQuery('#<portlet:namespace />resultadoNormasDH').load(url, function() {
																				jQuery('#<portlet:namespace />normaDHBuscando').hide();
		        															  } );
		}	
	}
	
	function filtrarTiposNormas() {		
		var idSistema = jQuery('#<portlet:namespace />sistemaselect').val();

		if(jQuery('#<portlet:namespace />sistemaselect').val() == "Todos"){
			document.getElementById("<portlet:namespace/>tipo_norma").length = 0;		
			addElementToSelect("<portlet:namespace/>tipo_norma", "Seleccione un Tipo de Norma", "Todos");
			return true;
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/id_sistema_tipos_normas&idSistema='+idSistema;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>tipo_norma").length = 0;						
				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>tipo_norma", "Seleccione un Tipo de Norma", "Todos");
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>tipo_norma", text, value);
				}                                                                                                                                                                                                                                                            
			}
		});		
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

</script>

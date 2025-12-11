<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		
%>


		<fieldset class="block-labels">
		
			<legend><liferay-ui:message key="busqueda-organismos" /></legend>
						
			<div id="<portlet:namespace />busquedaOrganismos">
				<table class="lfr-table" width="100%">
					<tr>						
						<td>
							<liferay-ui:message key="nombre-organismo" />
						</td>													
						<td>
							<input id="<portlet:namespace />nombre_organismo" name="<portlet:namespace />nombre_organismo" size="50"  type="text"/>
						</td>
						<td>
							<liferay-ui:message key="sigla" />
						</td>													
						<td>
							<input id="<portlet:namespace />sigla" name="<portlet:namespace />sigla" size="10"  type="text"/>
						</td>
						<td>
						<td>
							<liferay-ui:message key="ambito" />
						</td>
						<td>
							<select  name="<portlet:namespace/>ambito" id="<portlet:namespace/>ambito">
								<option value="">TODOS</option>
								<%	for (String amb : WebKeysCGT.AMBITOS) {	%>
									<option value="<%= amb %>">
										<%=amb%>
									</option>
								<%	}	%>	
							</select>
						</td>
						<td>
							<liferay-ui:message key="lineas-trabajo" />
						</td>						
						<td>
							<select  name="<portlet:namespace/>id_linea" id="<portlet:namespace/>id_linea">
								<option value="">TODOS</option>
								<%	for (String amb : WebKeysCGT.LINEAS_TRABAJO) {	%>
										<option value="<%= amb %>">
											<%=amb%>
										</option>
								<%	}	%>					
							</select>													
						</td>
				   </tr>
				   <tr>
				   		<td>&nbsp;</td>
				   </tr>				   
				   <tr>				  
						<td colspan="6" align="center">					
							<table>
								<tr>
									<td>		
										<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
									</td>									
									<td>							
									<%if(showABMButtons){%>								
										<div id="<portlet:namespace />nuevoOrganismo">
											<input type="button" value="<liferay-ui:message key="nuevo-organismo" />" onClick="nuevoOrganismo();" />
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
				<div align="center" id="<portlet:namespace />organismoBuscando">			
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
							<div align="center" id="<portlet:namespace />resultadoOrganismos" style="width: 1200px;" >			
								<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/empresas_seguimiento_search_result.jsp">									
								</liferay-util:include>				
							</div>
						</td>
					</tr>
				</table> 	  
			</div>
		</fieldset>
		<input type="hidden" id="<portlet:namespace />id_organismo" name="<portlet:namespace />id_organismo" />
		<input type="hidden" id="<portlet:namespace />id_area" name="<portlet:namespace />id_area" />
		<input type="hidden" id="<portlet:namespace />itemsLista" name="<portlet:namespace />itemsLista" />
			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />organismoBuscando').hide();
	
	function limpiarCampos(){
		jQuery('#<portlet:namespace />cuit_entidad<%=sufi%>').val('');
		jQuery('#<portlet:namespace />sucursal_entidad<%=sufi%>').val('');
		jQuery('#<portlet:namespace />entidad<%=sufi%>').val('');
		jQuery('#<portlet:namespace />estadoBuscar').val('');
	}
	
	function borrarArea(id_area){
		if(!confirm("<liferay-ui:message key='desea-borrar-el-area'/>")){
				return false;
		}else{		
			var nombre_organismo=jQuery('#<portlet:namespace />nombre_organismo').val();
			var ambito=jQuery('#<portlet:namespace />ambito').val();
			var linea=jQuery('#<portlet:namespace />id_linea').val();
			var sigla=jQuery('#<portlet:namespace />sigla').val();
			jQuery('#<portlet:namespace />organismoBuscando').show();
			//NUEVA LISTA EMPRESAS
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_area&id_area='+id_area+'&nombre='+nombre_organismo+'&ambito='+ambito+'&linea='+linea+'&sigla='+sigla;		
			jQuery('#<portlet:namespace />resultadoOrganismos').load(url, function() {
																				jQuery('#<portlet:namespace />organismoBuscando').hide();
		        															  } );
		}	
	}
	
	function borrarOrganismo(id_organismo){
		if(!confirm("<liferay-ui:message key='desea-borrar-el-organismo'/>")){
				return false;
		}else{		
			var nombre_organismo=jQuery('#<portlet:namespace />nombre_organismo').val();
			var ambito=jQuery('#<portlet:namespace />ambito').val();
			var linea=jQuery('#<portlet:namespace />id_linea').val();
			var sigla=jQuery('#<portlet:namespace />sigla').val();
			jQuery('#<portlet:namespace />organismoBuscando').show();
			//NUEVA LISTA EMPRESAS
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_organismo&id_organismo='+id_organismo+'&nombre='+nombre_organismo+'&ambito='+ambito+'&linea='+linea+'&sigla='+sigla;		
			jQuery('#<portlet:namespace />resultadoOrganismos').load(url, function() {
																				jQuery('#<portlet:namespace />organismoBuscando').hide();
		        															  } );
		}	
	}
	
	function verOrganismo(id_organismo){		
		jQuery('#<portlet:namespace />id_organismo').val(id_organismo);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_organismo" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function verArea(id_area){		
		jQuery('#<portlet:namespace />id_area').val(id_area);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_area" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	jQuery('#<portlet:namespace />buscar').click(function(){
						
		var nombre_organismo=jQuery('#<portlet:namespace />nombre_organismo').val();
		var ambito=jQuery('#<portlet:namespace />ambito').val();
		var linea=jQuery('#<portlet:namespace />id_linea').val();
		var sigla=jQuery('#<portlet:namespace />sigla').val();
		
		
		jQuery('#<portlet:namespace />organismoBuscando').show();
		//NUEVA LISTA EMPRESAS
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/buscar_organismo&nombre='+encodeURI(nombre_organismo)+'&ambito='+encodeURI(ambito)+'&linea='+encodeURI(linea)+'&sigla='+encodeURI(sigla);		
		jQuery('#<portlet:namespace />resultadoOrganismos').load(url, function() {
																			jQuery('#<portlet:namespace />organismoBuscando').hide();
	        															  } );
	    
	});
	function agregarArea(id_organismo){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_area_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';
		document.<portlet:namespace />fm.<portlet:namespace />id_organismo.value = id_organismo;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	
	}
	
	function nuevoOrganismo(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_organismo_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />generarLista(tipo) {
			var items = document.getElementsByName('items');
			var itemsValue = "";
			var i = 0;			
			for (i = 0; i<items.length; i++){
				if (items[i].checked) {					
					itemsValue= itemsValue+items[i].value+";"; 
				}
			}
			alert(itemsValue);
			jQuery('#<portlet:namespace />itemsLista').val(itemsValue);
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_mailing_entry" /></portlet:actionURL>';
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'crearListaOrganismo';		
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);				
	}
</script>

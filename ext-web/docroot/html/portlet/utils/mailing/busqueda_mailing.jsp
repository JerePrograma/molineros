<%@ include file="/html/portlet/utils/mailing/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%
 		boolean showABMButtons = true;
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		
%>


		<fieldset class="block-labels">
		
			<legend><liferay-ui:message key="busqueda-mailing" /></legend>
						
			<div id="<portlet:namespace />busquedaMailing">
				<table class="lfr-table" width="100%">
					<tr>	
						<td>
							<liferay-ui:message key="id" />
						</td>													
						<td>
							<input id="<portlet:namespace />id_lista" name="<portlet:namespace />id_lista" size="10"  type="text"/>
						</td>					
						<td>
							<liferay-ui:message key="nombre-lista" />
						</td>													
						<td>
							<input id="<portlet:namespace />nombre_lista" name="<portlet:namespace />nombre_lista" size="50"  type="text"/>
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
										<div id="<portlet:namespace />nuevaLista">
											<input type="button" value="<liferay-ui:message key="nueva-lista" />" onClick="nuevoLista();" />
										</div>									
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
				<div align="center" id="<portlet:namespace />listaBuscando">			
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
							<div align="center" id="<portlet:namespace />resultadoListas" style="width: 1200px;" >			
												
							</div>
						</td>
					</tr>
				</table> 	  
			</div>
		</fieldset>
		<input type="hidden" id="<portlet:namespace />id_lista" name="<portlet:namespace />id_lista" />
		<input type="hidden" id="<portlet:namespace />id_area" name="<portlet:namespace />id_area" />

			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />listaBuscando').hide();
	
	function limpiarCampos(){
		jQuery('#<portlet:namespace />id_lista').val('');
		jQuery('#<portlet:namespace />nombre_lista').val('');		
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
			jQuery('#<portlet:namespace />resultadoListas').load(url, function() {
																				jQuery('#<portlet:namespace />organismoBuscando').hide();
		        															  } );
		}	
	}
	
	function verLista(id_lista){
		jQuery('#<portlet:namespace />id_lista').val(id_lista);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_lista_mailing" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	jQuery('#<portlet:namespace />buscar').click(function(){
						
		var id_lista=jQuery('#<portlet:namespace />id_lista').val();
		var nombre_lista=jQuery('#<portlet:namespace />nombre_lista').val();
			
		
		jQuery('#<portlet:namespace />listaBuscando').show();
		//NUEVA LISTA EMPRESAS
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/buscar_lista_mailing&nombre_lista='+encodeURI(nombre_lista)+'&id_lista='+encodeURI(id_lista);		
		jQuery('#<portlet:namespace />resultadoListas').load(url, function() {
																		
																			jQuery('#<portlet:namespace />listaBuscando').hide();
	        															  } );
	    
	});
	
	
	function nuevoLista(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_mailing_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>

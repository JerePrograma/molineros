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
							<input id="<portlet:namespace />id_boletin" name="<portlet:namespace />id_boletin" size="10"  type="text"/>
						</td>					
						<td>
							<liferay-ui:message key="nombre" />
						</td>													
						<td>
							<input id="<portlet:namespace />nombre_boletin" name="<portlet:namespace />nombre_boletin" size="50"  type="text"/>
						</td>
						<td>
							<liferay-ui:message key="subject" />
						</td>													
						<td>
							<input id="<portlet:namespace />subject" name="<portlet:namespace />subject" size="50"  type="text"/>
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
										<div id="<portlet:namespace />nuevoBoletin">
											<input type="button" value="<liferay-ui:message key="nuevo-boletin" />" onClick="nuevoBoletin();" />
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
				<div align="center" id="<portlet:namespace />boletinBuscando">			
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
							<div align="center" id="<portlet:namespace />resultadoBoletin" style="width: 1200px;" >			
												
							</div>
						</td>
					</tr>
				</table> 	  
			</div>
		</fieldset>
		<input type="hidden" id="<portlet:namespace />id_boletin" name="<portlet:namespace />id_boletin" />		

			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />boletinBuscando').hide();
	
	function limpiarCampos(){
		jQuery('#<portlet:namespace />id_boletin').val('');
		jQuery('#<portlet:namespace />nombre_boletin').val('');		
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
	
	function editarBoletin(id_boletin){
		jQuery('#<portlet:namespace />id_boletin').val(id_boletin);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_boletin" />
		</portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	jQuery('#<portlet:namespace />buscar').click(function(){
						
		var id_boletin=jQuery('#<portlet:namespace />id_boletin').val();
		var nombre_boletin=jQuery('#<portlet:namespace />nombre_boletin').val();
		var subject=jQuery('#<portlet:namespace />subject').val();
			
		
		jQuery('#<portlet:namespace />boletinBuscando').show();
		//NUEVA LISTA EMPRESAS
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/buscar_boletin&id_boletin='
		+encodeURI(id_boletin)+'&nombre_boletin='+encodeURI(nombre_boletin)+'&subject='+encodeURI(subject);		
		jQuery('#<portlet:namespace />resultadoBoletin').load(url, function() {
																		
																			jQuery('#<portlet:namespace />boletinBuscando').hide();
	        															  } );
	    
	});
	
	
	function nuevoBoletin(){		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_boletin_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>

<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

		
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysUnidadOperativa.ROL_ABM_UNIDAD_OPERATIVA);
		

		//verificar los calendars
 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		
 				
%>
		
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-unidad-operativa" /></legend>
				<table class="lfr-table">					
					<tr>	
						<td><label><liferay-ui:message key="fecha-incidente-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR)- 3 %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-incidente-hasta" />:</label></td>
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
						<!--td><label>Estado:</label></td>
						<td>
							<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
								<option value="0">Todos</option>								
								<option value="<%=WebKeysUnidadOperativa.ESTADO_ABIERTO%>">Abierto</option>
								<option value="<%=WebKeysUnidadOperativa.ESTADO_CERRADO%>">Cerrado</option>													
							</select>
						</td-->						
						<td colspan="1"><label><liferay-ui:message key="seccional-incidente" />:</label></td>					
						<td colspan="11"><jsp:include page='/html/portlet/uoma/busqueda_seccional_incidente.jsp' /></td>												 					
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">
							<fieldset class="block-labels"><legend><liferay-ui:message
								key="datos-afiliado" /></legend>
							<liferay-util:include page='/html/portlet/uoma/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
							</liferay-util:include>
							</fieldset>
						</td>
					</tr>					
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>										
					<tr>												
						<td align="right" colspan="12">
								<input type="button" 
								id="<portlet:namespace />buscar" 
								value="<liferay-ui:message key="buscar"/>" 
								title="<liferay-ui:message key="buscar" />" 
								onclick="<portlet:namespace />buscarIncidentes();" />&nbsp;
							<c:if test="<%=showABMButtons%>">								
								<input type="button" value="<liferay-ui:message key="alta-caso" />" onClick="<portlet:namespace />altaCaso();" />
							</c:if>														
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
			<div align="center" id="<portlet:namespace />busquedaIncidenteDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();		
	function <portlet:namespace />altaCaso() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/editar_incidente_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />buscarIncidentes(){
	    var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();	    
	    var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional_r').val();
		var seccional_afiliado=jQuery('#<portlet:namespace />id_seccional_afiliado').val();				
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		
		var desde_final=diaDesde+'/'+mesDesde+'/'+anioDesde;		
		var hasta_final=diaHasta+'/'+mesHasta+'/'+anioHasta;		
			
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}		
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_incidentes&cuil='+cuil+
		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&seccional='+seccional+'&nombre='+escape(nombre)+'&apellido='+escape(apellido)+
		'&entidad='+entidad+'&numero_afi='+numero_afi+'&fecha_desde='+desde_final+'&fecha_hasta='+hasta_final+'&seccional_afiliado='+seccional_afiliado+'&pagina='+offset_reg;
		
        jQuery('#<portlet:namespace />busquedaIncidenteDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	}
	
	function editaIncidente(id_incidente){		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/uoma/editar_incidente_entry&id_incidente='+id_incidente;		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>
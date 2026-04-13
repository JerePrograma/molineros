<%@ page import="ar.com.ospim.tesoreria.ReciboDerivadoException" %>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<liferay-ui:error exception="<%= ReciboDerivadoException.class %>" message="recibo-derivado-exception" />
<%
 		boolean showABMButtons = true; //PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_RECIBOS);
 		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-ingresos-estudio" /></legend>
				<table class="lfr-table">
					<tr>						
						<td colspan="4">
							<label><liferay-ui:message key="fecha-desde" />:</label>&nbsp;&nbsp;
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
							monthParam="fechaDesdeMes1"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaInicioAnio1"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>						
						<td colspan="4">
							<label><liferay-ui:message key="fecha-hasta" />:&nbsp;&nbsp;
							<liferay-ui:input-date
							dayParam="fechaHastaDia2"
							dayValue="<%= fechaPago.get(Calendar.DATE) %>" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />							
						</td>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="14">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="id-ingreso" />:</label></td>
						<td><input id="<portlet:namespace />recibo" name="<portlet:namespace />recibo" size="13" maxlength="12" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td><input id="<portlet:namespace />empresa" name="<portlet:namespace />empresa" size="50" maxlength="50" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad_bla" id="<portlet:namespace/>entidad_bla">
								<option value="">Sin Seleccionar</option>
								<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
									<option value="<%= entidad %>"><%=entidad%></option>									
								<%}%>
							</select>
						</td>
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>						
						<td>
							<% if(showABMButtons) { %>
								<input type="button" value="<liferay-ui:message key="alta-ingreso" />" onClick="<portlet:namespace />altaRecibo();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="14">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaReciboDiv">
					
			</div>
		</fieldset>
			
<script type="text/javascript">
	var recibos;
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var recibo=jQuery('#<portlet:namespace />recibo').val();
		var empresa=jQuery('#<portlet:namespace />empresa').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var entidad=jQuery('#<portlet:namespace />entidad_bla').val();
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");
		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		
		/*if(!<portlet:namespace />validarBusqueda(recibo,empresa, cuit, entidad)){
			return false;
		}*/		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_recibos_no_os&recibo='+recibo+
		'&empresa='+empresa+'&cuit='+cuit+'&entidad_bla='+entidad+'&fechaDesdeDia='+fechaDesdeDia.value
				+'&fechaDesdeMes='+fechaDesdeMes.value
				+'&fechaDesdeAnio='+fechaDesdeAnio.value
				+'&fechaHastaDia='+fechaHastaDia.value
				+'&fechaHastaMes='+fechaHastaMes.value
				+'&fechaHastaAnio='+fechaHastaAnio.value;
		 url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaReciboDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function verReporte(){
		var recibo=jQuery('#<portlet:namespace />recibo').val();
		var empresa=jQuery('#<portlet:namespace />empresa').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var entidad=jQuery('#<portlet:namespace />entidad_bla').val();
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");
		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");		
		
		/*if(!<portlet:namespace />validarBusqueda(recibo,empresa, cuit, entidad)){
			return false;
		}*/		
		jQuery('#<portlet:namespace />buscando').show();
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RECIBOS_SEGUIMIENTO'+
				'&recibo='+recibo+
				'&empresa='+empresa+
				'&cuit='+cuit+
				'&entidad_bla='+entidad+
				'&fechaDesdeDia='+fechaDesdeDia.value+
				'&fechaDesdeMes='+fechaDesdeMes.value+
				'&fechaDesdeAnio='+fechaDesdeAnio.value+
				'&fechaHastaDia='+fechaHastaDia.value+
				'&fechaHastaMes='+fechaHastaMes.value+
				'&fechaHastaAnio='+fechaHastaAnio.value+
				'&sacarRecibos='+recibos;
	}
	
	function <portlet:namespace />validarBusqueda(recibo, empresa, cuit, entidad){		
		if(trim(recibo).length ==0 && trim(empresa).length==0 && trim(cuit).length==0 && trim(entidad).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaRecibo() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_recibos_no_os_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function deseleccionarRecibo(recibo_id){			
		var splitRecibos;
		if(null!=recibos){
			splitRecibos=recibos.split("|");
		}
		
		var auxRecibos;
		var sacar=false;
		
		if(jQuery("#incluir_recibo_"+recibo_id).is(':checked')){			
			for(i=0;i<splitRecibos.length;i++){				
				if(splitRecibos[i]!=recibo_id){
					if(null!=auxRecibos){
		 				auxRecibos=auxRecibos+'|'+splitRecibos[i];
		 			}else{
		 				auxRecibos=splitRecibos[i];
		 			}
				}
			}
		}else{
			if(null!=recibos){
		 		auxRecibos=recibos+'|'+recibo_id;
		 	}else{
		 		auxRecibos=recibo_id;
		 	}
		}	 	
	 	recibos=auxRecibos;
	}     
	
</script>

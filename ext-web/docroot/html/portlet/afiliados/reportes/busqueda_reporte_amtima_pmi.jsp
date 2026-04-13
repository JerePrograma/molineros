<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<% 				
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-amtima-pmi" /></legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
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
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td>
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
						
						<td >
				           <label>Solo CONYUGES/CONCUBINAS</label>
				           <input type="checkbox" id="<portlet:namespace />solo_conyuges" name="<portlet:namespace />solo_conyuges" value=""  />
			            </td> 
						
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="obtener-en-pantalla"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaActaDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		var solo_conyuges=jQuery("#<portlet:namespace/>solo_conyuges").is(':checked');

		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/reporte_amtima_pmi'
		+'&fechaDesdeDia='+fechaDesdeDia.value
		+'&fechaDesdeMes='+fechaDesdeMes.value
		+'&fechaDesdeAnio='+fechaDesdeAnio.value
		+'&fechaHastaDia='+fechaHastaDia.value
		+'&fechaHastaMes='+fechaHastaMes.value
		+'&fechaHastaAnio='+fechaHastaAnio.value
		+'&solo_conyuges='+solo_conyuges;

		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});

	jQuery('#<portlet:namespace />reporte').click(function(){		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		var solo_conyuges=jQuery("#<portlet:namespace/>solo_conyuges").is(':checked');

//		jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/xlsservlet/?reporte=REPORTE_AMTIMA_PMI'
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&solo_conyuges='+solo_conyuges;
	});
	
	
</script>

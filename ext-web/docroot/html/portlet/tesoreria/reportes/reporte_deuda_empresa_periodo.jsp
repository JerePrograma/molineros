<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		fechaPago.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-reporte-deuda-periodo" /></legend>
				<table class="lfr-table">
					<tr>						
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="1" 
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
							dayValue="1" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
					<tr>
					<tr>
						<td><label><liferay-ui:message key="ramo-desde" />:</label></td>
						<td>
							<input id="<portlet:namespace />id_ramo" name="<portlet:namespace />id_ramo" size="3" maxlength="3" type="text" value=""/>
						</td>
						<td><label><liferay-ui:message key="ramo-hasta" />:</label></td>
						<td>
							<input id="<portlet:namespace />id_ramo_hasta" name="<portlet:namespace />id_ramo_hasta" size="3" maxlength="3" type="text" value=""/>
						</td>						
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
					<tr>
					<tr>
						<td>
							<liferay-ui:message key="agrupar-remuneracion"/>&nbsp;<input type="checkbox" id="<portlet:namespace />agrupar_remuneracion" name="<portlet:namespace />agrupar_remuneracion" value="false"/>
						</td>
						<td>
							<liferay-ui:message key="incluir-empresa-sin-deuda"/>&nbsp;<input type="checkbox" id="<portlet:namespace />incluir_sin_deuda" name="<portlet:namespace />incluir_sin_deuda" value="false"/>
						</td>
						<!-- td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="obtener-en-pantalla"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td-->
						<td colspan="2">							
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td>
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
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
		<liferay-util:include page='/html/portlet/tesoreria/reportes/reporte_deuda_empresa_periodo_agendado.jsp'></liferay-util:include>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		var agrupar_remuneracion= 	jQuery("#<portlet:namespace />agrupar_remuneracion").is(':checked');
		var sin_deuda=jQuery("#<portlet:namespace />incluir_sin_deuda").is(':checked');
		
		var ramo_desde=document.getElementById("<portlet:namespace />id_ramo");
		var ramo_hasta=document.getElementById("<portlet:namespace />id_ramo_hasta");
		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/reporte_deuda_empresa_periodo'
		+'&fechaDesdeDia='+fechaDesdeDia.value
		+'&fechaDesdeMes='+fechaDesdeMes.value
		+'&fechaDesdeAnio='+fechaDesdeAnio.value
		+'&fechaHastaDia='+fechaHastaDia.value
		+'&fechaHastaMes='+fechaHastaMes.value
		+'&fechaHastaAnio='+fechaHastaAnio.value
		+'&agrupar_remuneracion='+agrupar_remuneracion
		+'&sin_deuda='+sin_deuda
		+'&ramo_desde='+ramo_desde;
		+'&ramo_hasta='+ramo_hasta;

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
		var agrupar_remuneracion= 	jQuery("#<portlet:namespace />agrupar_remuneracion").is(':checked');
		var sin_deuda=jQuery("#<portlet:namespace />incluir_sin_deuda").is(':checked');
		var ramo_desde=document.getElementById("<portlet:namespace />id_ramo").value;
		var ramo_hasta=document.getElementById("<portlet:namespace />id_ramo_hasta").value;
		
		jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/xlsservlet/?reporte=REPORTE_DEUDA_EMPRESA_PERIODO'			
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&agrupar_remuneracion='+agrupar_remuneracion
			+'&sin_deuda='+sin_deuda
			+'&ramo_desde='+ramo_desde
			+'&ramo_hasta='+ramo_hasta;
	});
	
	
</script>

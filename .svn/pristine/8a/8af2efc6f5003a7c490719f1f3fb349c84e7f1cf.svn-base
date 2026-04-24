<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-aportes-contrib-empresas" /></legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" /></td>
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
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="obtener-en-pantalla"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
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
		
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();

		if (trim(cuit).length != 11 && trim(cuil).length != 11){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/reporte_aportes_contribuciones_empresa'
		+'&cuit='+cuit
		+'&cuil='+cuil
		+'&fechaDesdeDia='+fechaDesdeDia.value
		+'&fechaDesdeMes='+fechaDesdeMes.value
		+'&fechaDesdeAnio='+fechaDesdeAnio.value
		+'&fechaHastaDia='+fechaHastaDia.value
		+'&fechaHastaMes='+fechaHastaMes.value
		+'&fechaHastaAnio='+fechaHastaAnio.value;

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
		
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();

		if (trim(cuit).length != 11 && trim(cuil).length != 11){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/xlsservlet/?reporte=APORTES_CONTRIBUCIONES_EMP'
			+'&cuit='+cuit
			+'&cuil='+cuil
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value;
	});
	
	
</script>

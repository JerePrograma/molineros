<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%		
	//verificar los calendars
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date()); 		
	Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
	periodoDesde.setTime(new Date());
	Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
	periodoHasta.setTime(new Date());
%>

<fieldset class="block-labels"><legend><liferay-ui:message
	key="listados_padron_sss" /></legend>
<table class="lfr-table">
	<tr>	
		<td><label><liferay-ui:message key="periodo" />:</label></td>
		<td><liferay-ui:input-date dayParam="periodoDesdeDia"
			dayNullable="<%= true %>" dayValue=""
			monthAndYearParam="periodoDesdeMesAnio"
			monthValue="<%= fechaHasta.get(Calendar.MONTH)-2%>"
			monthAndYearNullable="<%= true %>"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 2 %>"
			yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) %>"
			firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
		</td>
		
		<td>
			<select id="<portlet:namespace />movimiento" name="<portlet:namespace />movimiento">
				<option value="ALTAS">ALTAS</option>
				<option value="BAJAS">BAJAS</option>
				<option value="MODIFICACIONES">MODIFICACIONES</option>				
			</select>
		</td>	
		<td>
			<label><liferay-ui:message key="registrar-informado" />:</label>
		</td>
		<td>
			<input type="checkbox" name="<portlet:namespace />registrar" id="<portlet:namespace />registrar"/>
		</td>
		<td coslpan="1"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button" /></td>
	</tr>
</table>
</fieldset>
<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
</fieldset>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();

	jQuery('#<portlet:namespace />buscar').click(function exportarExcel(){
		var tipoMovimiento=jQuery("#<portlet:namespace />movimiento").val();		
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();		
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();			
		var registrar=jQuery("#<portlet:namespace/>registrar").is(':checked');			
		window.location.href ='/txtservlet/?reporte=REPORTE_LISTADO_SS'			
			+'&periodoDesde='+periodoDesdeMesAnio
			+'&periodoHasta='+periodoHastaMesAnio
			+'&tipo='+tipoMovimiento
			+'&registrar='+registrar;			
	});  

	<portlet:namespace />hideDayFieldOfPeriodFields ();
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}

</script>
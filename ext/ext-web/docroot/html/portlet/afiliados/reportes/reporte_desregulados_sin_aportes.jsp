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
	key="desregulados-sin-aportes" /></legend>
<table class="lfr-table">
	<tr>	
		<td><label><liferay-ui:message key="periodo-ultimo-pago" />:</label></td>
		<td><liferay-ui:input-date dayParam="periodoDesdeDia"
			dayNullable="<%= true %>" dayValue=""
			monthAndYearParam="periodoDesdeMesAnio"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthAndYearNullable="<%= true %>"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 1 %>"
			yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) + 1 %>"
			firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td><label><liferay-ui:message key="periodo-vigencia" />:</label></td>
		<td><liferay-ui:input-date dayParam="periodoHastaDia"
			dayNullable="<%= true %>" dayValue=""
			monthAndYearParam="periodoHastaMesAnio"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthAndYearNullable="<%= true %>"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodoHasta.get(Calendar.YEAR) - 1 %>"
			yearRangeEnd="<%= periodoHasta.get(Calendar.YEAR) + 1 %>"
			firstDayOfWeek="<%= periodoHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td coslpan="1"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button" /></td>
		<td width="250">&nbsp;&nbsp;</td>
		<td align="left" width="200" ><b>Descripción del reporte:</br></b><i><liferay-ui:message key="descripcion-listado-morosos"/></i> </td>	
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
<div align="center" id="<portlet:namespace />busquedaDesreguladosDiv">
</div>
</fieldset>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();

	jQuery('#<portlet:namespace />buscar').click(function exportarExcel(){
		var tercerizadora=jQuery('#<portlet:namespace/>tercerizadora').val();
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();		
		window.location.href ='/xlsservlet/?reporte=REPORTE_DESREGULADOS_SIN_APORTES'			
			+'&periodoUltimoPago='+periodoDesdeMesAnio
			+'&periodoVigencia='+periodoHastaMesAnio;			
	});  

	<portlet:namespace />hideDayFieldOfPeriodFields ();
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}

</script>
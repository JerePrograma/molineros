<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
	String accion = (String)session.getAttribute(Constants.CMD);
	String cuil_titular=request.getParameter("cuil_titular");
	//verificar los calendars
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde.setTime(new Date());
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
	fechaHasta.setTime(new Date());
	Afiliado afiliado = (Afiliado)request.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	if (afiliado == null) {
		afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
	}
%>

<fieldset class="block-labels"><legend><liferay-ui:message
	key="grupo-filtro-busqueda-historico-mov" /></legend>
	<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td colspan="3"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button"
			onClick="javascript:<portlet:namespace />buscarMovimientosHistoricos();" /></td>
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
<div align="center" id="<portlet:namespace />busquedaMovimientoHistoricoDiv">
</div>
</fieldset>

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
function <portlet:namespace />buscarMovimientosHistoricos(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/historico_movimientos&cuil_titular='+<%=cuil_titular%>;
	jQuery('#<portlet:namespace />buscando').show();		
	jQuery("#<portlet:namespace/>busquedaMovimientoHistoricoDiv").load(url,{desde_dia:desde_dia, desde_mes:desde_mes, desde_anio:desde_anio, hasta_dia:hasta_dia,
		hasta_mes:hasta_mes, hasta_anio:hasta_anio}, function(){jQuery('#<portlet:namespace />buscando').hide();});	
}	
</script>
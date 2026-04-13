<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		Calendar periodoSistemaUoma = CalendarFactoryUtil.getCalendar();
 		periodoSistemaUoma.setTime(new Date());
%>

 <fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-boletas-portal-empleadores" /></legend>
				<table class="lfr-table">
				<tr>
				<td><label><liferay-ui:message key="fechas-procesos-desde" />:</label></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr> 
					<td>
						<div id="<portlet:namespace />ultimos_procesos_sistema_viejo">
							<jsp:include page='/html/portlet/tesoreria/reportes/ultimos_procesos_sistema_viejo.jsp' />  
						</div>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>	
				</tr>
					<tr>						
						<td>
							<liferay-ui:input-date
							dayParam="vigenciaArchivoDia"
							dayValue="1" 
							monthParam="vigenciaArchivoMes"
							monthValue="<%= periodoSistemaUoma.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="vigenciaArchivoAnio"
							yearValue="<%= periodoSistemaUoma.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= periodoSistemaUoma.get(Calendar.YEAR) - 8 %>"
							yearRangeEnd="<%= periodoSistemaUoma.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= periodoSistemaUoma.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
					<td><input type="button" id="<portlet:namespace />buscarA" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" /></td>
					</tr>
				</table>
	</fieldset> 
	
<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();

	function bajarListadoProcesos(fecha){
		var parceoFecha = fecha.split('-');
		new Date(parceoFecha[0], parceoFecha[1]-1, parceoFecha[2]);
		
		var vigenciaArchivoDia=parceoFecha[2];		
		var vigenciaArchivoMes=parceoFecha[1];	
		var vigenciaArchivoAnio=parceoFecha[0];		
		
		jQuery('#<portlet:namespace />buscando').show();
	    window.location.href ='/txtservlet/?reporte=REPORTE_VIEJO_SIST_UOMA'+'&vigenciaArchivoDia='+vigenciaArchivoDia+'&vigenciaArchivoMes='+vigenciaArchivoMes+'&vigenciaArchivoAnio='+vigenciaArchivoAnio; 
	}
	
	jQuery('#<portlet:namespace />buscarA').click(function(){
		
		var vigenciaArchivoDia=jQuery('#<portlet:namespace />vigenciaArchivoDia').val();		
		var vigenciaArchivoMes=jQuery('#<portlet:namespace />vigenciaArchivoMes').val();	
		var vigenciaArchivoAnio=jQuery('#<portlet:namespace />vigenciaArchivoAnio').val();		

		jQuery('#<portlet:namespace />buscando').show();
		    window.location.href ='/txtservlet/?reporte=REPORTE_VIEJO_SIST_UOMA'+'&vigenciaArchivoDia='+vigenciaArchivoDia+'&vigenciaArchivoMes='+vigenciaArchivoMes+'&vigenciaArchivoAnio='+vigenciaArchivoAnio; 
		});
	
</script>
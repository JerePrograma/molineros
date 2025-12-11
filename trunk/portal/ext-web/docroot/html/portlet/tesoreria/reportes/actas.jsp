<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-actas" /></legend>
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
							<input value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button" onclick="buscar()"/>
						</td>
						
					</tr>
				</table>	      	  
		</fieldset>	
		</fieldset>
			
<script type="text/javascript">

	function buscar(){
			var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
			var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
			var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");
		
			var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
			var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
			var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
			
			window.location.href ='/xlsservlet/?reporte=REPORTE_ACTAS'
				+'&fechaDesdeDia='+fechaDesdeDia.value
				+'&fechaDesdeMes='+fechaDesdeMes.value
				+'&fechaDesdeAnio='+fechaDesdeAnio.value
				+'&fechaHastaDia='+fechaHastaDia.value
				+'&fechaHastaMes='+fechaHastaMes.value
				+'&fechaHastaAnio='+fechaHastaAnio.value
				+'&rnd=' + Math.floor(Math.random()*100);
		};
	
	
</script>

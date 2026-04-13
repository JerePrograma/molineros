<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
//		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 
        Calendar d = CalendarFactoryUtil.getCalendar();;
        Calendar fechaDesde = CalendarFactoryUtil.getCalendar(d.get(Calendar.YEAR) ,d.get(Calendar.MONTH),1);
//		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>

		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-empresa-nuevos-afiliados-periodo" /></legend>
				<table class="lfr-table">			
					<tr>
						<td><label><liferay-ui:message key="periodo" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" 
								/>
						</td>
					
						<td>
							<input id="<portlet:namespace />excel" value="<liferay-ui:message key="excel"/>" title="<liferay-ui:message key="excel" />" type="button" onClick="javascript:<portlet:namespace />nuevosAfiliadosEmpresaExcel();"/>
						</td>
						<td colspan="5"><label id="<portlet:namespace />mensaje" style="color: red"></label></td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>		
		
<script type="text/javascript">

jQuery('#<portlet:namespace />fechaDesdeDia').hide();

function <portlet:namespace />nuevosAfiliadosEmpresaExcel(){	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
/*	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
*/	
	
	var url = '/xlsservlet/?reporte=REPORTE_NUEVOS_AFILIADOS_EMPRESAS';
        url += '&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio;
               //'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location.href =url;
}

</script>
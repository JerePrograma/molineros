<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
        String portlet_name = ParamUtil.getString(request, "portlet_name");

        if (portlet_name == null || portlet_name.trim().equals("")){
	       portlet_name = "tesoreria";
        }
        if(renderResponse.getNamespace().equals("_FAR_1_")){
	        portlet_name = "farmacia";
        }
        if(renderResponse.getNamespace().equals("_UOM_1_")){
	       portlet_name = "uoma";
        }  
        Calendar d = CalendarFactoryUtil.getCalendar();;
        Calendar fechaDesde = CalendarFactoryUtil.getCalendar(d.get(Calendar.YEAR) ,d.get(Calendar.MONTH),1);
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>

		<fieldset class="block-labels">
				<legend>EOAF</legend>
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
							<input id="<portlet:namespace />excel" value="<liferay-ui:message key="excel"/>" title="<liferay-ui:message key="excel" />" type="button" onClick="javascript:<portlet:namespace />eoafExcel();"/>
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

function <portlet:namespace />eoafExcel(){	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	
	var url = '/xlsservlet/?reporte=REPORTE_EOAF';
        url += '&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio;
        url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'	;               
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location.href =url;
}

</script>
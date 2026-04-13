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
		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-retencion-ganancias" /></legend>
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
								disabled="<%= false %>" />
						</td>
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
						</td>
					</tr>						
					<tr>
						<td colspan="7">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaMovimientoDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
jQuery("#<portlet:namespace/>fechaDesdeDia").hide();
function <portlet:namespace />buscarMovimientos(){
	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	 
	var url = '/txtservlet/?reporte=REPORTE_RETENCION_GANANCIAS' 
		+ '&fechaDesdeDia=01'
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio		
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
	url += '&rnd=' + Math.floor(Math.random()*100);

	window.location.href =url;
}	
</script>
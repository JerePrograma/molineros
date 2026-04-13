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
				<legend><liferay-ui:message key="reporte-anticipos" /></legend>
				<table class="lfr-table">			
					<tr>
						<td><label><liferay-ui:message key="fecha-emision-desde" />:</label></td>
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
						<td><label><liferay-ui:message key="fecha-emision-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) -25 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 25 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<%if(portlet_name.equals("uoma")){ %>
						<td><label><liferay-ui:message key="tomar-utilizados-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDiaUtil"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMesUtil"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnioUtil"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) -25 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 25 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<%}else{ %>
						<td>
							&nbsp;
						</td>
						<%} %>						
					</tr>						
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>
						<%if(portlet_name.equals("uoma") || portlet_name.equals("farmacia")){ %>		
							<td><liferay-ui:message key="empresa"/></td>				
							<td colspan="5">
								<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
							  		<liferay-util:param name="esEditable" value='true'/>
							  		<%if(portlet_name.equals("uoma")  || portlet_name.equals("farmacia")){%>
							  			<liferay-util:param name="soloOP" value='false'/>
							  		<%}else{%>
							  			<liferay-util:param name="soloOP" value='true'/>
							  		<%}%>
									</liferay-util:include>
							</td>
						<%}else{%>
							<td colspan="6">&nbsp;</td>
						<%}%>
							<td colspan="1">
								<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
							</td>
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
function <portlet:namespace />buscarMovimientos(){
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	
	var hasta_dia_util=jQuery("#<portlet:namespace/>fechaHastaDiaUtil").val();	
	var hasta_mes_util=jQuery("#<portlet:namespace/>fechaHastaMesUtil").val();
	var hasta_anio_util=jQuery("#<portlet:namespace/>fechaHastaAnioUtil").val();

	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	<%if(portlet_name.equals("uoma")){ %>		
		var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();		
		var sucur_entidad=jQuery("#<portlet:namespace />sucursal_entidad").val();
		var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();
		var id_incluir_mov_bcrios = document.getElementById("<portlet:namespace />incluir_movimientosbancarios");		
	<%}%>
	<%if(portlet_name.equals("farmacia")){ %>		
	    var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();		
	    var sucur_entidad=jQuery("#<portlet:namespace />sucursal_entidad").val();
	    var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();
	    var id_incluir_mov_bcrios = document.getElementById("<portlet:namespace />incluir_movimientosbancarios");		
    <%}%>
	 
	var url = '/xlsservlet/?reporte=REPORTE_ANTICIPOS_OP' 
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaDiaUtil='+hasta_dia_util
		+ '&fechaHastaMesUtil='+hasta_mes_util
		+ '&fechaHastaAnioUtil='+hasta_anio_util
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
		+ '&fechaHastaAnio=' +hasta_anio;
		<%if(!portlet_name.equals("tesoreria")){ %>
			url=url+ '&cuit='+cuit_entidad
				+ '&sucursal='+sucur_entidad
				+ '&id_seccional='+id_seccional			
		<%}%>		
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}
function cambiaCuit(){
}
function filtrarConceptosUOMA(){};
	
</script>
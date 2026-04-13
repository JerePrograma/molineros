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
 		Calendar fechaPagoHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaPagoHasta.setTime(new Date()); 
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="estado-comprobantes" /></legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="acreedor" />:</label></td>
						<td colspan="5" width="90%">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td  colspan="1"><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td colspan="2">
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 2 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 1 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>		
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<%if(!portlet_name.equals("tesoreria")){%>						
						<tr>
							<td><label><liferay-ui:message key="fecha-emision-desde" />:</label></td>
							<td>
								<liferay-ui:input-date
									dayParam="fechaEmiDesdeDia"			
									dayValue="<%=0%>"				
									monthParam="fechaEmiDesdeMes"
									monthValue="<%=-1%>"
									yearParam="fechaEmiDesdeAnio"
									yearValue="<%=-0%>"
									yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
									yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
									firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
									dayNullable="<%= true %>"
      								monthNullable="<%= true %>"
      								yearNullable="<%= true %>"
									disabled="<%= false %>" />
							</td>
							<td  colspan="1"><label><liferay-ui:message key="fecha-emision-hasta" />:</label></td>
							<td colspan="2">
								<liferay-ui:input-date
									dayParam="fechaEmiHastaDia"																					
									dayValue="<%=0%>"	
									dayNullable="<%= true %>"
									monthParam="fechaEmiHastaMes"
									monthValue="<%=-1%>"	
									monthNullable="<%= true %>"
									yearParam="fechaEmiHastaAnio"
									yearValue="<%=0%>"	
									yearNullable="<%= true %>"
									yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 2 %>"
									yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 1 %>"
									firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
							</td>
						</tr>
						<tr>
							<td colspan="6">&nbsp;</td>
						</tr>
					<%}%>
					<tr>
						<td><label>Considerar pagos hasta:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaPagoHastaDia"
								dayValue="<%= fechaPagoHasta.get(Calendar.DATE)%>"
								monthParam="fechaPagoHastaMes"
								monthValue="<%= fechaPagoHasta.get(Calendar.MONTH) %>"
								yearParam="fechaPagoHastaAnio"
								yearValue="<%= fechaPagoHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaPagoHasta.get(Calendar.YEAR) - 2 %>"
								yearRangeEnd="<%= fechaPagoHasta.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaPagoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>
							Solo comprobantes con saldo&nbsp;<input type="checkbox" name="soloConSaldo" id="soloConSaldo" value="true"/>
						</td>
						<td colspan="3">
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
						</td>
					</tr>		
					<tr>						
						<td colspan="6">
							<%if(portlet_name.equals("tesoreria")){%>
							    Incluir Proveedores&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_proveedores" id="<portlet:namespace />incluir_proveedores" value="true" checked="checked"/>&nbsp;
							    Incluir Liquidaciones&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" value="true" checked="checked"/>&nbsp;
							    Incluir Reintegros&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" value="true" checked="checked"/>&nbsp;
							<%}else{%>
								<input type="hidden" name="<portlet:namespace />incluir_proveedores" id="<portlet:namespace />incluir_proveedores" value="false"/>&nbsp;
							    <input type="hidden" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" value="false"/>&nbsp;
							    <input type="hidden" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" value="false"/>&nbsp;							
							<%}%>							    
						</td>
					</tr>						
					<tr>
						<td colspan="5">&nbsp;</td>
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
	var incluir_reintegros = document.getElementById("<portlet:namespace />incluir_reintegros");
	var incluir_liquidaciones = document.getElementById("<portlet:namespace />incluir_liquidaciones");
	var incluir_proveedores = document.getElementById("<portlet:namespace />incluir_proveedores");
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	<%if(!portlet_name.equals("tesoreria")){%>
		var emi_desde_dia=jQuery("#<portlet:namespace/>fechaEmiDesdeDia").val();	
		var emi_desde_mes=jQuery("#<portlet:namespace/>fechaEmiDesdeMes").val();
		var emi_desde_anio=jQuery("#<portlet:namespace/>fechaEmiDesdeAnio").val();
		var emi_hasta_dia=jQuery("#<portlet:namespace/>fechaEmiHastaDia").val();	
		var emi_hasta_mes=jQuery("#<portlet:namespace/>fechaEmiHastaMes").val();
		var emi_hasta_anio=jQuery("#<portlet:namespace/>fechaEmiHastaAnio").val();
	<%}%>
	var pago_hta_dia=jQuery("#<portlet:namespace/>fechaPagoHastaDia").val();	
	var pago_hta_mes=jQuery("#<portlet:namespace/>fechaPagoHastaMes").val();
	var pago_hta_anio=jQuery("#<portlet:namespace/>fechaPagoHastaAnio").val();

	
	var soloConSaldo = document.getElementById("soloConSaldo");
	
	var url = '/xlsservlet/?reporte=ESTADO_COMPROBANTES' 
		+'&incluir_reintegros=' + (incluir_reintegros.checked ? incluir_reintegros.value : 'false')
		+'&incluir_liquidaciones=' + (incluir_liquidaciones.checked ? incluir_liquidaciones.value : 'false')
		+'&incluir_proveedores=' + (incluir_proveedores.checked ? incluir_proveedores.value : 'false')
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaAnio=' +hasta_anio
		+ '&fechaPagoHastaDia=' +pago_hta_dia
		+ '&fechaPagoHastaMes=' +pago_hta_mes
		+ '&fechaPagoHastaAnio=' +pago_hta_anio
		+ '&cuit_entidad=' +cuit_entidad
		+ '&sucursal_entidad=' +sucursal_entidad
		+'&soloConSaldo=' + (soloConSaldo.checked ? soloConSaldo.value : 'false')
		+ '&id_seccional=' +id_seccional
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		<%if(!portlet_name.equals("tesoreria")){%>
			url+='&fechaEmiDesdeDia='+emi_desde_dia+'&fechaEmiDesdeMes='+emi_desde_mes+'&fechaEmiDesdeAnio='+emi_desde_anio+
				 '&fechaEmiHastaDia='+emi_hasta_dia+'&fechaEmiHastaMes='+emi_hasta_mes+'&fechaEmiHastaAnio='+emi_hasta_anio;
		<%}%>
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}
function cambiaCuit(){
}
	
</script>
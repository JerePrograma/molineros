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
				<legend><liferay-ui:message key="cuentas-corrientes" /></legend>
				<table class="lfr-table">			
					<tr>							
						<td colspan="5">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>						  		
						  			<liferay-util:param name="soloOP" value='false'/>					  		
						  				  		
							</liferay-util:include>
						</td>
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
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
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
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
						</td>
					</tr>		
					<tr>
						<td>
							Sólo acreedores con saldo&nbsp;<input type="checkbox" name="soloConSaldo" id="soloConSaldo" value="true" checked="true"/>
						</td>
						<td>
							Mostrar solo comprobantes con saldo (no tiene en cuenta el saldo inicial)&nbsp;<input type="checkbox" name="mostrarSoloComprobantesConSaldo" id="mostrarSoloComprobantesConSaldo" value="true" onclick="comprobantesConSaldoClick()"/>
						</td>
						<td colspan="2">
							<div id="fechaPagoHasta">
							<label>Evaluar pago de comprobantes a la fecha:</label>
							<liferay-ui:input-date
								dayParam="fechaPagoHastaDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								monthParam="fechaPagoHastaMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaPagoHastaAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
							</div>
						</td>
					</tr>	
					<tr>
						<td colspan="5">
							Mostrar Informacion de Comprobantes con su OP&nbsp;<input type="checkbox" name="mostrarMasInfo" id="mostrarMasInfo" value="true"/>
						</td>
					</tr>	
					<tr>
						<td colspan="5">						
						    Incluir Proveedores&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_proveedores" id="<portlet:namespace />incluir_proveedores" value="true" checked="checked"/>&nbsp;
						    <%if(!portlet_name.equals("uoma")){%>
						    	Incluir Liquidaciones&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" value="true" checked="checked"/>&nbsp;
						        Incluir Liquidaciones Farmacia&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_liquidaciones_farmacia" id="<portlet:namespace />incluir_liquidaciones_farmacia" value="true" checked="checked"/>&nbsp;
						    	Incluir Reintegros&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" value="true" checked="checked"/>&nbsp;
						    	Incluir Reintegros Farmacia&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_reintegros_farmacia" id="<portlet:namespace />incluir_reintegros_farmacia" value="true" checked="checked"/>&nbsp;
						    <%}else{%>
						    	<input type="hidden" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" value="false"/>
						        <input type="hidden" name="<portlet:namespace />incluir_liquidaciones_farmacia" id="<portlet:namespace />incluir_liquidaciones_farmacia" value="false"/>
						    	<input type="hidden" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" value="false" />
						    	<input type="hidden" name="<portlet:namespace />incluir_reintegros_farmacia" id="<portlet:namespace />incluir_reintegros_farmacia" value="false"/>
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
	var incluir_reintegros_farmacia = document.getElementById("<portlet:namespace />incluir_reintegros_farmacia");
	var incluir_liquidaciones = document.getElementById("<portlet:namespace />incluir_liquidaciones");
	var incluir_liquidaciones_farmacia = document.getElementById("<portlet:namespace />incluir_liquidaciones_farmacia");
	var incluir_proveedores = document.getElementById("<portlet:namespace />incluir_proveedores");
	
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();

	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var pago_hasta_dia=jQuery("#<portlet:namespace/>fechaPagoHastaDia").val();	
	var pago_hasta_mes=jQuery("#<portlet:namespace/>fechaPagoHastaMes").val();
	var pago_hasta_anio=jQuery("#<portlet:namespace/>fechaPagoHastaAnio").val();

	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var mostrarSoloComprobantesConSaldo = document.getElementById("mostrarSoloComprobantesConSaldo");
	var mostrarMasInfo = document.getElementById("mostrarMasInfo");
	
	var url = '/xlsservlet/?reporte=CUENTAS_CORRIENTES' 
		+'&incluir_reintegros=' + (incluir_reintegros.checked ? incluir_reintegros.value : 'false')
		+'&incluir_reintegros_farmacia=' + (incluir_reintegros_farmacia.checked ? incluir_reintegros_farmacia.value : 'false')
		+'&incluir_liquidaciones=' + (incluir_liquidaciones.checked ? incluir_liquidaciones.value : 'false')
		+'&incluir_liquidaciones_farmacia=' + (incluir_liquidaciones_farmacia.checked ? incluir_liquidaciones_farmacia.value : 'false')
		+'&incluir_proveedores=' + (incluir_proveedores.checked ? incluir_proveedores.value : 'false')
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaAnio=' +hasta_anio
		+ '&cuit_entidad=' +cuit_entidad
		+ '&sucursal_entidad=' +sucursal_entidad
		+ '&fechaPagoHastaDia=' +pago_hasta_dia
		+ '&fechaPagoHastaMes=' +pago_hasta_mes
		+ '&fechaPagoHastaAnio=' +pago_hasta_anio
		+ '&mostrarMasInfo=' + (mostrarMasInfo.checked ? mostrarMasInfo.value : 'false')
		+'&soloConSaldo=' + (soloConSaldo.checked ? soloConSaldo.value : 'false')
		+'&mostrarSoloComprobantesConSaldo=' + (mostrarSoloComprobantesConSaldo.checked ? mostrarSoloComprobantesConSaldo.value : 'false')
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'	
		+ '&id_seccional=' +id_seccional;
	url += '&rnd=' + Math.floor(Math.random()*100);


	var fechaIni = new Date(desde_anio,desde_mes,desde_dia,0,0,0);
	var x=new Date();
	x.setFullYear(2010,7,0);
	if (fechaIni<x){
		alert("Las cuentas corrientes pedidas con fecha inferior a 01/08/2010 pueden tener saldos poco confiables.");
	}
	
	window.location.href =url;
}
function cambiaCuit(){
}

function comprobantesConSaldoClick(){
	var mostrarSoloComprobantesConSaldo = document.getElementById("mostrarSoloComprobantesConSaldo");
	if (mostrarSoloComprobantesConSaldo.checked ){
		jQuery("#fechaPagoHasta").show();
	}else{
		jQuery("#fechaPagoHasta").hide();
	}
}
jQuery("#fechaPagoHasta").hide();
	
</script>
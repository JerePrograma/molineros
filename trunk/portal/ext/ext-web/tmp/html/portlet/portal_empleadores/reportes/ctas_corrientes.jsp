<%@ include file="/html/portlet/portal_empleadores/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 
 		Calendar fechaPagoHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaPagoHasta.setTime(new Date()); 
%>	
		<fieldset class="block-labels">
				<legend>Reporte DDJJ y pagos</legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="Empresa" />:</label></td>
						<td colspan="4">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='portal_empleadores'/>
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
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>		
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
					</tr>		
					<tr>
						<td colspan="3">
							Aporte Sol UOMA&nbsp;<input type="checkbox" id="APORTE_SOL_UOMA" name="APORTE_SOL_UOMA" value="true" checked="checked"/>&nbsp;
							Cuota Social UOMA&nbsp;<input type="checkbox" id="CUOTA_SOC_UOMA"  name="CUOTA_SOC_UOMA" value="true" checked="checked"/>&nbsp;
							Art. 46&nbsp;<input type="checkbox" id="ART_46"  name="ART_46" value="true" checked="checked"/>&nbsp;
							Cuota Usufructo&nbsp;<input type="checkbox" id="CUOTA_USUFRUCTO"  name="CUOTA_USUFRUCTO" value="true" checked="checked"/>&nbsp;
							Cuota AMTIMA&nbsp;<input type="checkbox" id="CUOTA_AMTIMA" name="CUOTA_AMTIMA" value="true" checked="checked"/>&nbsp;
							Boleta Blanca AMTIMA&nbsp;<input type="checkbox" id="amtima" name="amtima" value="true" checked="checked"/>&nbsp;
							Boleta Blanca OSPIM&nbsp;<input type="checkbox" id="ospim" name="ospim" value="true" checked="checked"/>&nbsp;
							Boleta Blanca UOMA&nbsp;<input type="checkbox" id="uoma" name="uoma" value="true" checked="checked"/>&nbsp;
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
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
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();

	var pago_hta_dia=jQuery("#<portlet:namespace/>fechaPagoHastaDia").val();	
	var pago_hta_mes=jQuery("#<portlet:namespace/>fechaPagoHastaMes").val();
	var pago_hta_anio=jQuery("#<portlet:namespace/>fechaPagoHastaAnio").val();

	var aporte_solidario_uoma = document.getElementById("APORTE_SOL_UOMA");
	var cuota_social_uoma = document.getElementById("CUOTA_SOC_UOMA");
	var art46 = document.getElementById("ART_46");
	var cuota_usufructo = document.getElementById("CUOTA_USUFRUCTO");
	var cuota_amtima = document.getElementById("CUOTA_AMTIMA");
	
	var uoma = document.getElementById("uoma");
	var amtima = document.getElementById("amtima");
	var ospim = document.getElementById("ospim");
	
	var url = '/xlsservlet/?reporte=CUENTA_CORRIENTE_PORTAL_EMPLEADORES' 
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
		+ '&aporte_solidario_uoma='+ (aporte_solidario_uoma.checked ? aporte_solidario_uoma.value : 'false')
		+ '&cuota_social_uoma='+ (cuota_social_uoma.checked ? cuota_social_uoma.value : 'false')
		+ '&art46='+ (art46.checked ? art46.value : 'false')
		+ '&cuota_usufructo='+ (cuota_usufructo.checked ? cuota_usufructo.value : 'false')
		+ '&cuota_amtima='+ (cuota_amtima.checked ? cuota_amtima.value : 'false')
		+ '&uoma='+ (uoma.checked ? uoma.value : 'false')
		+ '&amtima='+ (amtima.checked ? amtima.value : 'false')
		+ '&ospim='+ (ospim.checked ? ospim.value : 'false')
		+ '&id_seccional=' +id_seccional;
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}
function cambiaCuit(){
}
	
</script>
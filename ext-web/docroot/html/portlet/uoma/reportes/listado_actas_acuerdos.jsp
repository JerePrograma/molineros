<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.uoma.beans.ActasAcuerdos" %>

<portlet:defineObjects/>

<%		
		String portlet_name = ParamUtil.getString(request, "portlet_name");
			 
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="listado-actas-acuerdos" /></legend>
				<table class="lfr-table">			
					<tr>							
						<td colspan="5" width="100%">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>						  		
							</liferay-util:include>
						</td>
					</tr>
					<tr>							
						<td colspan="5" width="100%">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="tipo-reporte" />:</label></td>
						<td colspan="4">
								<select name="<portlet:namespace />tipo_reporte" id="<portlet:namespace />tipo_reporte"> 
									<option value="ACTAS">Actas</option>
									<option value="ACUERDOS">Acuerdos</option>									
								</select>
						</td>
					</tr>
					
					<tr>							
						<td colspan="5" width="100%">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<label>Evaluar saldo de actas/acuerdos a la fecha:</label>
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
						</td>
						<td colspan="2">
							<label>Tipo de saldo:</label>
							<select name="<portlet:namespace />con_saldo" id="<portlet:namespace />con_saldo"> 
								<option value="<%=ActasAcuerdos.CON_SALDO%>">Con Saldo</option>
								<option value="<%=ActasAcuerdos.SIN_SALDO%>">Sin Saldo</option>
								<option value="<%=ActasAcuerdos.TODOS%>" selected>Todos</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
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
						<td colspan="5" width="100%">
							&nbsp;
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

	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var pago_hasta_dia=jQuery("#<portlet:namespace/>fechaPagoHastaDia").val();	
	var pago_hasta_mes=jQuery("#<portlet:namespace/>fechaPagoHastaMes").val();
	var pago_hasta_anio=jQuery("#<portlet:namespace/>fechaPagoHastaAnio").val();

	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var conSaldo=jQuery("#<portlet:namespace/>con_saldo").val();	
	var tipoReporte=jQuery("#<portlet:namespace/>tipo_reporte").val();
	
	var url = '/xlsservlet/?reporte=LISTADO_ACTAS_ACUERDOS'
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaAnio=' +hasta_anio
		+ '&cuit_entidad=' +cuit_entidad
		+ '&sucursal_entidad=' +sucursal_entidad
		+ '&fechaPagoDia=' +pago_hasta_dia
		+ '&fechaPagoMes=' +pago_hasta_mes
		+ '&fechaPagoAnio=' +pago_hasta_anio
		+ '&conSaldo='+conSaldo
		+ '&tipoReporte='+tipoReporte;
	
	url += '&rnd=' + Math.floor(Math.random()*100);

	window.location.href =url;
}
function cambiaCuit(){
}

	
</script>
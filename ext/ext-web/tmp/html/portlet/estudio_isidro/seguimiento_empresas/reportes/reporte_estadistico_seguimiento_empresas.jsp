<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
		List<TipoLoteEmpresa> tiposLoteEmp = (ArrayList<TipoLoteEmpresa>) request.getSession().getAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION);

		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-seguimiento-estadistico" /></legend>
				<table class="lfr-table">			
					<tr>							
						<td colspan="8">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>						  		
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
							<liferay-ui:message key="nro-lote" />
						</td>													
						<td>
							<input type="text"  id="<portlet:namespace />lote" name="<portlet:namespace />lote" />			
						</td>
						<td>
							<liferay-ui:message key="tipo-lote" />
						</td>													
						<td>
							<select name="<portlet:namespace/>tipo" id="<portlet:namespace/>tipo">	
								<option selected value="">TODOS</option>							
								
								<%for (TipoLoteEmpresa tle : tiposLoteEmp){ %>
									<option value="<%=tle.getTipoLote()%>"><%=tle.getDescripcionLote() %></option>								
								<%} %>
								
							</select>
						</td>
					</tr>
					<tr>	
						<td colspan="8">
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" 
							onClick="javascript:<portlet:namespace />generarReporte();"/>
						</td>
					</tr>						
				</table>	      	  
		</fieldset>	
		
			
<script type="text/javascript">

function <portlet:namespace />generarReporte(){
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();

	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	 
	var nro_lote=jQuery("#<portlet:namespace/>lote").val();
	var tipo_lote=jQuery("#<portlet:namespace/>tipo").val();
	
	var url = '/xlsservlet/?reporte=REPORTE_ESTADISTICO_ESTUDIO' 
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaAnio=' +hasta_anio
		+ '&cuit_entidad=' +cuit_entidad
		+ '&sucursal_entidad=' +sucursal_entidad
		+ '&id_seccional=' +id_seccional
	    + '&nro_lote=' +nro_lote
	    + '&tipo_lote='+tipo_lote;
	url += '&rnd=' + Math.floor(Math.random()*100);

	window.location.href =url;
}
	
</script>
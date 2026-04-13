<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%	
	
	boolean showRegistrar = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.setTime(new Date());	
%>
<table class="lfr-table">
<tr>
	<td width="550">
		<fieldset class="block-labels"><legend><liferay-ui:message key="reporte-credenciales-emitidas" /></legend>
		<table class="lfr-table">	
			<tr>			
					<td>		
							
							<liferay-ui:message key="emitidas-desde-ultimo-informe" />
								&nbsp;<input type="checkbox" name="<portlet:namespace />desdeUltimo" id="<portlet:namespace />desdeUltimo" onClick="mostrarFecha();" checked/>
							<%if(showRegistrar){%>		
								&nbsp;<liferay-ui:message key="registrar-informado" />
								&nbsp;<input type="checkbox" name="<portlet:namespace />informar" id="<portlet:namespace />informar"/>			
							<%}else{%>
								<input type="hidden" name="<portlet:namespace />informar" id="<portlet:namespace />informar" value="false"/>
							<%}%>
				</td>
				<td>
					<div id="<portlet:namespace />fechaDiv" name="<portlet:namespace />fechaDiv">
								<liferay-ui:message key="fecha-desde" />&nbsp;		
								<liferay-ui:input-date
								dayParam="fechaDesdeDia1"
								dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
								monthParam="fechaDesdeMes1"
								monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
								yearParam="fechaDesdeAnio1"
								yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
								firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
								disabled="false" />&nbsp;-
								<liferay-ui:message key="fecha-hasta" />&nbsp;		
								<liferay-ui:input-date
								dayParam="fechaHastaDia1"
								dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
								monthParam="fechaHastaMes1"
								monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
								yearParam="fechaHastaAnio1"
								yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
								firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
								disabled="false" />
							</div>
					
				</td>
				<td>
					<liferay-ui:message key="ultimos-reportes-emitidos" />
					<liferay-util:include page="/html/portlet/afiliados/reportes/ultimo_reporte_creden.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
					</liferay-util:include>
				</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>	
			<tr>	
				<td align="center" colspan="3">
				<input id="<portlet:namespace />buscar"
					value="<liferay-ui:message key="buscar"/>"
					title="<liferay-ui:message key="buscar" />" type="button" />
				</td>
			</tr>
		</table>
		</fieldset>
		<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center"><img
					alt="<liferay-ui:message key='buscando'/>"
					src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
		</div>
		</fieldset>
	</td>
	<td width="200" valign="top">
		<fieldset class="block-labels"><legend><liferay-ui:message key="reporte-legajos-credenciales-emitidas" /></legend>
		<table class="lfr-table">	
			<tr>			
				<td>&nbsp;</td>
				<td>		
					<liferay-util:include page="/html/portlet/afiliados/reportes/ultimos_reportes_legajos_creden.jsp">
					</liferay-util:include>
				</td>
				<td>&nbsp;</td>		
			</tr>
		</table>		
		</fieldset>
	</td>
</tr>
</table>


<script type="text/javascript">
	jQuery('#<portlet:namespace />envioModificaciones').hide();
		
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />fechaDiv').hide();
	
	function mostrarFecha(){
		var checked=jQuery("#<portlet:namespace/>desdeUltimo").is(':checked');		
		if(checked){
			jQuery('#<portlet:namespace />fechaDiv').hide();
		}else{
			jQuery('#<portlet:namespace />fechaDiv').show();
		}	
	}

	jQuery('#<portlet:namespace />buscar').click(function exportarExcel(){				
		var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio1");
		
		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia1");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes1");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio1");
		
		var id_terc=jQuery('#<portlet:namespace />tercerizadora').val();
		var tipo=jQuery("#<portlet:namespace/>tipoInforme").val();
		var ultimo=jQuery("#<portlet:namespace />desdeUltimo").is(':checked');
		<%if(showRegistrar){%>
			var informar=jQuery("#<portlet:namespace/>informar").is(':checked');
		<%}else{%>
			var informar=jQuery("#<portlet:namespace/>informar");
		<%}%>		
		window.location.href ='/xlsservlet/?reporte=REPORTE_LISTADO_CREDEN_EMITIDAS'			
			+'&informar='+informar			
			+'&ultimo='+ultimo
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value			
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value			
			+'&fechaHastaAnio='+fechaHastaAnio.value
									
	});  

	<portlet:namespace />hideDayFieldOfPeriodFields ();
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}
	
		
	function imprimirListadoAnterior(id_reporte){
		window.location.href ='/xlsservlet/?reporte=REPORTE_LISTADO_CREDEN_EMITIDAS'			
			+'&id_reporte='+id_reporte						
			+'&tipoInforme=histo';
	}
	
	function imprimirLegajosProcesoAnterior(id_lote, periodo){
		window.location.href ='/xlsservlet/?reporte=REPORTE_LEGAJOS_PROCESADOS'			
			+'&nro_lote='+id_lote
			+'&periodo='+periodo;
	}
	
	
	
</script>
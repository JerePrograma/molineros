<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%		
	//verificar los calendars
	List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();
	boolean showRegistrar = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.setTime(new Date());	
%>

<fieldset class="block-labels"><legend><liferay-ui:message
	key="reporte-padron-tercerizadoras" /></legend>
<table class="lfr-table">	
	<tr>	
		<td width="47%">
			<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td>
						<liferay-ui:message key="tercerizadora-servicio" />
					</td>
					<td>
						<select name="<portlet:namespace/>tercerizadora" id="<portlet:namespace/>tercerizadora">
							<option value="0">Seleccione una tercerizadora</option>
							<%	for (TercerizadoraServicio terce : tercServList) { %>
									<option value="<%= terce.getId_tercerizadora()%>"><%=terce.getDescripcion()%></option>
							<%	} %>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="tipo-informe" />
					</td>
					<td>
						<select name="<portlet:namespace/>tipoInforme" id="<portlet:namespace/>tipoInforme" onChange="javascript=cambiaTipoInforme();">
							<option value="1">Padrón completo</option>
							<option value="2">Modificaciones</option>				
							<option value="3">Titulares</option>
							<option value="4">Padrón completo con valor de Cápita</option>
							<option value="5">Facturación</option>				
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2"><i>
						<div id="<portlet:namespace/>envioModificaciones">
							<liferay-ui:message key="modificaciones-desde-ultimo-padron" />
						</div></i>			
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="vigen-fecha" />
					</td>
					<td>					
						<liferay-ui:input-date
						dayParam="fechaDesdeDia1"
						dayValue="1"
						monthParam="fechaDesdeMes1"
						monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
						yearParam="fechaDesdeAnio1"
						yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 50 %>"
						yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
						firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
					</td>
				</tr>
				<%if(showRegistrar){%>
					<tr>
						<td>
							<liferay-ui:message key="registrar-informado" />
						</td>		
						<td colspan="2">
							<input type="checkbox" name="<portlet:namespace />informar" id="<portlet:namespace />informar"/>
						</td>			
					</tr>
					<tr>
					<td>
						<liferay-ui:message key="ver-ids-seccionales" />
						</td>		
						<td colspan="2">
						   <input type="checkbox"  name="<portlet:namespace />ver_codigo_ceccionales" id="<portlet:namespace />ver_codigo_ceccionales"/>
						</td>
						</tr>
				<%}else{%>
					<input type="hidden" name="<portlet:namespace />informar" id="<portlet:namespace />informar" value="false"/>
				<%}%>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>	
				<tr>	
					<td align="center" colspan="2"><input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />" type="button" />
					</td>
				</tr>						
				</table>
			</td>
			<td>
				<table>
					<tr>
						<th><liferay-ui:message key="ultimos-padrones-informados" /></th>
					</tr>
					<tr>
						<td>
							<liferay-util:include page="/html/portlet/afiliados/reportes/ultimo_padron_informado.jsp">
								<liferay-util:param name="esEditable" value='true'/>						  		
							</liferay-util:include>
						</td>
					</tr>
				</table>
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

<script type="text/javascript">
	jQuery('#<portlet:namespace />fechaDesdeDia1').hide();
	jQuery('#<portlet:namespace />envioModificaciones').hide();
		
	jQuery('#<portlet:namespace />buscando').hide();

	jQuery('#<portlet:namespace />buscar').click(function exportarExcel(){				
		var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio1");
		
		var id_terc=jQuery('#<portlet:namespace />tercerizadora').val();
		
		if(id_terc == "0"){
			alert("Debe seleccionar una tercerizadora");
			return false;
		}
		
		var tipo=jQuery("#<portlet:namespace/>tipoInforme").val();
		<%if(showRegistrar){%>
			var informar=jQuery("#<portlet:namespace/>informar").is(':checked');
		<%}else{%>
			var informar=jQuery("#<portlet:namespace/>informar");
		<%}%>	
		
		var verCodigoSeccionales =jQuery("#<portlet:namespace />ver_codigo_ceccionales").is(':checked');
		
		window.location.href ='/zipservlet/?reporte=REPORTE_TERCERIZADORA_CAPITAS'
			+'&id_terc='+id_terc
			+'&informar='+informar
			+'&tipoInforme='+tipo
			+'&verCodigoSeccionales='+verCodigoSeccionales			
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value;
									
	});  

/* 	<portlet:namespace />hideDayFieldOfPeriodFields ();
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	} */
	
	function cambiaTipoInforme(){
		var tipo=jQuery("#<portlet:namespace/>tipoInforme").val();
		
		if(tipo==2){
			jQuery('#<portlet:namespace />envioModificaciones').show();
		}else{
			jQuery('#<portlet:namespace />envioModificaciones').hide();
		}
	}
	
	function imprimirListadoAnterior(tercerizadora, tipo, fecha, fechaVigencia){
		
		var verCodigoSeccionales =jQuery("#<portlet:namespace />ver_codigo_ceccionales").is(':checked');
				
		window.location.href ='/zipservlet/?reporte=REPORTE_TERCERIZADORA_CAPITAS_HISTORICO'			
			+'&fecha='+fecha
			+'&id_terc='+escape(tercerizadora)			
			+'&tipoInforme='+tipo
			+'&verCodigoSeccionales='+verCodigoSeccionales
			+'&fechaVigencia='+fechaVigencia;
	}
	
	
</script>
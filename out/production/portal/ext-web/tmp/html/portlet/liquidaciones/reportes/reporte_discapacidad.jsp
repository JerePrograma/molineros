<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server 				
		
		boolean showABMButtons =  PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar();
 		periodoDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar();
 		periodoHasta.setTime(new Date());
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-liquidaciones" /></legend>
				<table class="lfr-table">										
					<tr>	
						<td><label>Fecha OP Desde:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label>Fecha OP Hasta:</label></td>
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoDesdeDia"
								dayNullable="<%= true %>"
								dayValue=""
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"							
								yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoHastaDia"
								dayNullable="<%= true %>" 
								dayValue=""							
								monthAndYearParam="periodoHastaMesAnio"
								monthValue="<%= periodoHasta.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= periodoHasta.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= periodoHasta.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= periodoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					
						<tr>																			
							<td><label>Codigo Prestación:</label></td>													
							<td><input id="<portlet:namespace />codPrestaci" name="<portlet:namespace />codPrestaci" size="6" maxlength="6" type="text" value="" /></td>														
							<td><label><liferay-ui:message key="estado" />:</label></td>
							<td colspan="1">
							<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">								
								<option value="100"></option>
								<option value="1">Doc. Faltante</option>
								<option value="2">Sin Tratamiento</option>
								<option value="3">Periodo Incorrecto</option>
								<option value="4">Periodo Duplicado o Excedido</option>
								<option value="0">Autorizado</option>
							</select>
							</td>
						</tr>					
					
					
					
					
						<tr>
							<td colspan="12">
		<fieldset class="block-labels"><legend> <liferay-ui:message
			key="datos-afiliado" /></legend>
		<div id="loadAfiliado"><liferay-util:include
			page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
			<liferay-util:param
				value="true"
				name="edit_mode" />
			<liferay-util:param name="cuil"
				value='' />
			<liferay-util:param name="inte"
				value='' />
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="discapacidad" />
			<liferay-util:param name="pag_reintegro" value='1' /></div>
		</liferay-util:include></fieldset>

							</td>
						</tr>
															
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					
						<tr>
							<td><label>Diagnóstico:</label></td>
							<td>
							<input id="<portlet:namespace />diagnostico"
								name="<portlet:namespace />diagnostico" size="40" maxlength="200"
								type="text"
								value="" />
							</td>
							<td><label>Cie X:</label></td>
							<td>
							<input id="<portlet:namespace />ciex"
								name="<portlet:namespace />ciex" size="40" maxlength="200"
								type="text"
								value="" />
							</td>							
						</tr>
						<tr>
						<td colspan="12">&nbsp;</td>
						</tr>
						
						<tr>						
						
						<td colspan="5"><liferay-util:include
						page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						<liferay-util:param name="esEditable"
							value='true' />
						<liferay-util:param name="cuit" value='' />
						<liferay-util:param name="sucu" value='' />
						<liferay-util:param name="razon" value='' />
						<liferay-util:param name="id_seccional" value='' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_'/>				
					</liferay-util:include></td>					
					</tr>
					
					<tr>
					<td colspan="12">&nbsp;</td>
					</tr>			
			<tr>				
				<td colspan="1">Liquidación:</label></td>
				<td><input type="checkbox" id="<portlet:namespace />liquidaciones" name="<portlet:namespace />liquidaciones" value="1" checked="checked" /></td>
				<td colspan="1">Reintegros:</label></td>														
				<td><input type="checkbox" id="<portlet:namespace />prestacional" name="<portlet:namespace />prestacional" value="1" checked="checked"/></td>
			</tr>
			<tr>
					<td colspan="12">&nbsp;</td>
					</tr>
					
					<tr>						
					<td coslpan="1">							
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
					</td>		
					</tr>
					<tr>						
					</tr>
				</table>	      	  
		</fieldset>
				
			
<script type="text/javascript">

	<portlet:namespace />hideDayFieldOfPeriodFields();

	jQuery('#<portlet:namespace />buscando').hide();
		
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();

		var codPrestaci=jQuery('#<portlet:namespace />codPrestaci').val();
		var estado=jQuery('#<portlet:namespace />estado').val();
		
		var entidad=jQuery('#<portlet:namespace />entidad').val();		 		

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
								
		var cuit=jQuery('#<portlet:namespace />cuit_entidad').val();
		var sucu=jQuery('#<portlet:namespace />sucursal_entidad').val();
					
		var diagnostico=jQuery('#<portlet:namespace />diagnostico').val();
		var ciex=jQuery('#<portlet:namespace />ciex').val();

		var boolpresta=jQuery('#<portlet:namespace />prestacional').is(':checked');
		var presta = "0";
		if (boolpresta == true) {
			presta = "1";
		}
		var liquida=jQuery('#<portlet:namespace />liquidaciones').is(':checked');
		var liquidac = "0";
		if (liquida == true) {
			liquidac = "1";
		}				


		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}		
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_DISCAPACIDAD'+'&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&codPrestaci='+codPrestaci+
		'&estado='+estado+					
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&cuit='+cuit+'&sucursal='+sucu+'&diagnostico='+diagnostico+'&ciex='+ciex+
		'&presta='+presta+'&liquidaciones='+liquidac;
			
	});
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeDia').val("");
		jQuery('#<portlet:namespace />fechaDesdeMes').val("");		
		jQuery('#<portlet:namespace />fechaDesdeAnio').val("");
		jQuery('#<portlet:namespace />fechaHastaDia').val("");
		jQuery('#<portlet:namespace />fechaHastaMes').val("");
		jQuery('#<portlet:namespace />fechaHastaAnio').val("");			
	}
	
	<portlet:namespace />initDateFields();

	function cambiaCuit(){
	}
	
</script>
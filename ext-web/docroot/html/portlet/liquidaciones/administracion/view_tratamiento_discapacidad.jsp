<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%
	int id_tratamiento = ParamUtil.getInteger(request,
			"id_tratamiento", 0);

	TratamientoDiscapacidad tratamiento = null;
	tratamiento = TratamientoDiscapacidadServiceUtil
			.getTratamientoDiscapacidad(id_tratamiento);
	List<Documento> documentosDiscapacidad = TraeListasServiceUtil
			.getDocumentosDiscapacidad();
	List<TercerizadoraServicio> tercerizadoras = TraeListasServiceUtil
			.getTercerizadorasPorConvenios();
	List<Documento> documentosFaltantes = tratamiento != null ? tratamiento
			.getDocumentosFaltantes()
			: new ArrayList<Documento>();

	String viewStr = ParamUtil.getString(request, "view", "");

	boolean esView = false;
	if (viewStr != null && viewStr.length() > 0) {
		esView = true;
	}

	Date periodoDesde = null;
	Calendar pediodoDC = CalendarFactoryUtil.getCalendar();
	periodoDesde = Validator.isNotNull(tratamiento) ? tratamiento
			.getPeriodo_desde() : null;
	if (periodoDesde == null) {
		pediodoDC.setTime(DateUtils
				.getFirstDateOfYear(new Date(), true));
	} else {
		pediodoDC.setTime(tratamiento.getPeriodo_desde());
	}

	Date periodoHasta = null;
	Calendar pediodoHC = CalendarFactoryUtil.getCalendar();
	periodoHasta = Validator.isNotNull(tratamiento) ? tratamiento
			.getPeriodo_hasta() : null;
	if (periodoHasta == null) {
		pediodoHC
				.setTime(DateUtils.getLastDateOfYear(new Date(), true));
	} else {
		pediodoHC.setTime(tratamiento.getPeriodo_hasta());
	}
	Prestador prestador = Validator.isNotNull(tratamiento) ? tratamiento
			.getPrestador()
			: null;	
	
%>
<fieldset class="block-labels"><legend>Tratamiento</legend>
<table class="lfr-table">

<tr>
		<td colspan="8"><label>Periodicidad:</label>&nbsp; &nbsp; &nbsp;
		<select name="<portlet:namespace/>periodicidad"
			id="<portlet:namespace/>periodicidad">
			<option value="Mensual"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getPeriodicidad())
					&& tratamiento.getPeriodicidad()
							.equalsIgnoreCase("Mensual") ? "selected" : ""%>>Mensual</option>			
		</select> 		
		&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		<label>Fecha Desde:</label>&nbsp; &nbsp; &nbsp;			
					
		<liferay-ui:input-date dayParam="periodoDesdeDia"
			dayValue="<%= pediodoDC.get(Calendar.DATE)%>"
			dayNullable="<%= false %>" monthParam="periodoDesdeMes"
			monthValue="<%= pediodoDC.get(Calendar.MONTH) %>"
			monthNullable="<%= false %>" yearParam="periodoDesdeAnio"
			yearValue="<%= pediodoDC.get(Calendar.YEAR) %>"
			yearNullable="<%= false %>"
			yearRangeStart="<%= pediodoDC.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= pediodoDC.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= pediodoDC.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
								
		<label>Fecha Hasta:</label>&nbsp; &nbsp; &nbsp;

		<liferay-ui:input-date dayParam="periodoHastaDia"
			dayValue="<%= pediodoHC.get(Calendar.DATE)%>"
			dayNullable="<%= false %>" monthParam="periodoHastaMes"
			monthValue="<%= pediodoHC.get(Calendar.MONTH) %>"
			monthNullable="<%= false %>" yearParam="periodoHastaAnio"
			yearValue="<%= pediodoHC.get(Calendar.YEAR) %>"
			yearNullable="<%= false %>"
			yearRangeStart="<%= pediodoHC.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= pediodoHC.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= pediodoHC.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />	
		&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;	
		<label id="<portlet:namespace/>recupera_ape_lb">Recupera por SUR:</label> &nbsp; &nbsp; &nbsp;		
		<input type="checkbox"  id="<portlet:namespace/>recupera_ape"
			name="<portlet:namespace/>recupera_ape" value="true"  <%=tratamiento != null && tratamiento.isRecupera_ape() ? "checked='checked'"
							: ""%>></input></td>
        <td align="right"><span style="font-size: 13pt; color:red; "><label ><%=tratamiento != null && tratamiento.getExcepcionContratoPrestador()!=null && tratamiento.getExcepcionContratoPrestador() ?"Excepción Contrato":""%></label></span> </td>							
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>


<td><label>Prestador:</label></td>
	<tr>
	    <td colspan="5">
		   <liferay-util:include
			page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
			<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador" />
			<liferay-util:param name="cuit_prestador"
			value='<%= Validator.isNotNull(prestador) ? prestador.getCuit() : "" %>' />
			<liferay-util:param name="nombre_prestador"
				value='<%=Validator.isNotNull(prestador) ? prestador.getDescripcion() : "" %>' />
			<liferay-util:param name="id_prestador"
				value='<%=Validator.isNotNull(prestador) ? String.valueOf(prestador.getId_prestador()) : "" %>' />
			<liferay-util:param name="solo_vigentes"
				value='true' />	
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( !esView )%>' />
			<liferay-util:param name="ext"
				value='_trat' />	
		   </liferay-util:include>
		</td>
	</tr>
	
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="prestacion" />:</label></td>
		<td colspan="3">
		<fieldset class="block-labels"><liferay-util:include
			page="/html/portlet/utils/prestaciones/busqueda_prestacion.jsp">
			<liferay-util:param name="search_url"
				value="/liquidaciones/buscar_prestacion" />
			<liferay-util:param name="id_prestacion"
				value='<%=tratamiento != null ? String.valueOf(tratamiento.getPrestacion().getId_prestacion()) : "" %>' />
			<liferay-util:param name="codigo"
				value='<%=tratamiento != null ? tratamiento.getPrestacion().getCodigo() : "" %>' />
			<liferay-util:param name="prestacion"
				value='<%=tratamiento != null ? tratamiento.getPrestacion().getDescripcion() : "" %>' />
			<liferay-util:param name="discapacidad"
				value='<%=String.valueOf( true )%>' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( !esView )%>' />
			<liferay-util:param name="suf" value='_trat'/>
		</liferay-util:include></fieldset>
		</td>
		
		
		
		<c:if test="<%=Validator.isNotNull(tratamiento)%>"> 
		<td><label><liferay-ui:message key="estado" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace />estado_" id="<portlet:namespace />estado_" disabled='disabled'>
			<option value="0"></option>
			<option value="1" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 1 ? "selected" : ""%>>En
			Curso</option>
			<option value="2" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 2 ? "selected" : ""%>>Documentación Faltante</option>			
			<option value="3"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 3 ? "selected" : ""%>>Cambio
			Prestador</option>
			<option value="4"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 4 ? "selected" : ""%>>Finalizado</option>
			<option value="5"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 5 ? "selected" : ""%>>Abandonado</option>
		</select></td>
		</c:if>
		
		
	</tr>
		
	<tr>
	  <td colspan="8">
		  <span id="<portlet:namespace />mensajeContrato" style="color: red;display:none;" >No existe contrato para esta Prestación del Prestador Solicitado </span>
	  </td>
	</tr>
		
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
		
		
	<tr>
	<td colspan="8">

	<div align="left" id="<portlet:namespace />divTransporte">
	<table>				
		<tr>
			<td colspan="1"><label>Cantidad Viajes(Mes):</label></td>
			<td colspan="7">
		
		<input id="<portlet:namespace />cantidad_viajes_mensuales"
			name="<portlet:namespace />cantidad_viajes_mensuales" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_viajes_mes() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad_viajes_mensuales'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		
		<label>Cantidad Km.(Día):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />cantidad_kilometros_dia"
			name="<portlet:namespace />cantidad_kilometros_dia" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_kilometros_dia() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad_kilometros_dia'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		
		<label>Cantidad Km.(Mes):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />cantidad_kilometros_mes"
			name="<portlet:namespace />cantidad_kilometros_mes" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_kilometros_mes() : 0%>"
			readonly="readonly" /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;

		<label>Importe Km.(Unit):</label> &nbsp;&nbsp;&nbsp; &nbsp;		
		<input id="<portlet:namespace />importe_kilometro_unit"
			name="<portlet:namespace />importe_kilometro_unit" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_kilometro_unit()) ? tratamiento
							.getImporte_kilometro_unit()
							: "0"
							: "0"%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_kilometro_unit'),event);"
			<%}%> /> 
		
			&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <label>Imp. Total Km.:</label> &nbsp;<input id="<portlet:namespace />importe_total_km"
			name="<portlet:namespace />importe_total_km" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total_km()) ? tratamiento
					.getImporte_total_km() : "" : ""%>"
			readonly="readonly" />			
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>					
		
		<tr>
		<td colspan="1">&nbsp;</td>
		<td colspan="6">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; <label>Hs. Espera(Día):</label>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		<input id="<portlet:namespace />hs_espera_dia"
			name="<portlet:namespace />hs_espera_dia" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getHs_espera_dia() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />hs_espera_dia'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		
		<label>Hs. espera(Mes):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />hs_espera_mes"
			name="<portlet:namespace />hs_espera_mes" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getHs_espera_mes() : 0%>" readonly='readonly' /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;							
		
		<label>Importe Hora(Unit):</label> &nbsp;&nbsp;&nbsp; &nbsp; &nbsp;
		<input id="<portlet:namespace />importe_hs_espera_unit"
			name="<portlet:namespace />importe_hs_espera_unit" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_hs_espera_unit()) ? tratamiento
							.getImporte_hs_espera_unit()
							: "0"
							: "0"%>"
			onchange="sumarTodoTransporte()" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_hs_espera_unit'),event);"
			<%}%> /> 		
			&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		<label><liferay-ui:message
			key="total" /> Hs:</label> &nbsp;<input id="<portlet:namespace />importe_total_hs"
			name="<portlet:namespace />importe_total_hs" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total_hs()) ? tratamiento
					.getImporte_total_hs() : "" : ""%>"
			readonly="readonly" />
		</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		</table>
	</div>

	</td>
	</tr>
	
	<tr>	    						
		<td colspan="1"><label><liferay-ui:message key="cant" />:</label></td>			
		<td colspan="7">
		
		<input id="<portlet:namespace />cantidad"
			name="<portlet:namespace />cantidad" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad() : 0%>"
			onchange="sumarTodo();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label><liferay-ui:message
			key="imp" />:</label> &nbsp;
		<input id="<portlet:namespace />importe"
			name="<portlet:namespace />importe" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total()) ? tratamiento
					.getImporte_total() : "0" : "0"%>"
			onchange="sumarTodo();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label><liferay-ui:message
			key="total" />:</label> &nbsp;<input id="<portlet:namespace />total"
			name="<portlet:namespace />total" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total()) ? tratamiento
					.getImporte_total().multiply(tratamiento.getCantidad())
					: "" : ""%>"
			readonly="readonly" />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label id="<portlet:namespace />cntTercLb">Cantidad Tercerizada:</label> &nbsp;
			<input id="<portlet:namespace />importe_tercerizado"
			name="<portlet:namespace />importe_tercerizado" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_tercerizado()) ? tratamiento
							.getImporte_tercerizado()
							: "0"
							: "0"%>"
			<%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_tercerizado'),event);"
			<%}%> />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		
			</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	
		<tr>
		<td colspan="1"><label>Observaciones:</label></td>
		<td colspan="1"><textarea rows="2" cols="50" id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <%if (esView) {%> <%="readonly='readonly'"%> <%}%>><%=tratamiento != null ? tratamiento.getObservaciones()
							: ""%></textarea>
		</td>	
		<td colspan="1"><label>Documentación faltante:</label></td>
		<td><select id="<portlet:namespace/>documentacion"
			name="<portlet:namespace/>documentacion" multiple="multiple" size="10">
			<optgroup label="Documentos">
				<%
					for (Documento dd : documentosDiscapacidad) {
				%>
				<option value="<%=dd.getId_documento()%>"
				 <%for (Documento df : documentosFaltantes) {
					if (dd.getId_documento() == df.getId_documento()) {%>
				 	selected="selected"
				 			<%break;
					}
				}%>
				 ><%=dd.getDescripcion()%></option>
				<%
					}
				%>
			</optgroup>
		</select></td>		
	</tr>
	
	<c:if test="<%=!esView%>">
	
	<tr>				
		<td colspan="8">				
		
		<input type="button"
			value="<liferay-ui:message key="save" />" id="saveTratamientoD"
			onClick="<portlet:namespace />saveTratamientoDiscapacidad(); return false;" />
		<c:if
			test="<%=Validator.isNotNull(tratamiento)%>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit"
				value="Autorización Tratamiento RTF"
				onClick="<portlet:namespace />imprimirATR();return false;" />
		</c:if>
		<c:if
			test="<%=Validator.isNotNull(tratamiento)%>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit"
				value="Autorización Tratamiento ODT"
				onClick="<portlet:namespace />imprimirATO();return false;" />
		</c:if>
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE)%>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit"
				value="Cambio Prestador"
				onClick="<portlet:namespace />cambiarEstadoTratamiento('<%=WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_CAMBIO_PRESTADOR%>>');return false;" />
		</c:if>			
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE || tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO)%>">			
			&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit"
				value="Finalizar Tratamiento"
				onClick="<portlet:namespace />cambiarEstadoTratamiento('<%=WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_FINALIZADO%>>');return false;" />
		</c:if>		
		<c:if test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE || tratamiento.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO)%>">			
			&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit"
				value="Abandono de Tratamiento"
				onClick="<portlet:namespace />cambiarEstadoTratamiento('<%=WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO%>>');return false;" />
		</c:if>
				
		<input type="hidden"
			value="<%=tratamiento != null ? tratamiento.getId_tratamiento()
						: ""%>"
			name="tratamiento_id" id="tratamiento_id" /> <input type="hidden"
			value="<%=(tratamiento == null ? Constants.ADD
						: Constants.UPDATE)%>"
			name="accionOriginal" id="accionOriginal" />
			
		 <input type="hidden" name="<portlet:namespace />es_excepcion_tratamiento" id="<portlet:namespace />es_excepcion_tratamiento" />	
		</td>
	</tr>
	</c:if>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>

	<tr>

	</tr>

	<tr>
		<td colspan="8">
	</tr>
</table>
<div align="center" id="<portlet:namespace />buscandoPrestadorTD"
	name="<portlet:namespace />buscandoPrestadorTD">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />prestadores_resultadoTD">
	<jsp:include
		page='tratamiento_discapacidad_result.jsp' />
		
</div>
<script type="text/javascript">	

	jQuery('#<portlet:namespace />buscandoPrestadorTD').hide();
	jQuery('#<portlet:namespace/>recupera_ape').hide();
	jQuery('#<portlet:namespace/>recupera_ape_lb').hide();
	jQuery('#<portlet:namespace />cntTercLb').hide();
	jQuery('#<portlet:namespace />importe_tercerizado').hide();
	try {
		sumarTodo();
	} catch (err) {}

	try {
		sumarTodoTransporte();
	} catch (err) {}

	
	function sumarTodoTransporte(){
		var cantidad_viajes_mensuales = 0;
		var cantidad_kilometros_dia = 0;
		var cantidad_kilometros_mes = 0;
		var importe_kilometro_unit = 0;		
		var importe_total_km = 0;

		var hs_espera_dia = 0;
		var hs_espera_mes = 0;
		var importe_hs_espera_unit = 0;
		var importe_total_hs = 0;

		if (document.getElementById("<portlet:namespace />cantidad_viajes_mensuales")!=null && trim(document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value) != ""){
			cantidad_viajes_mensuales = document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value;
		}
		if (document.getElementById("<portlet:namespace />cantidad_kilometros_dia") != null && trim(document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value)!= ""){
			cantidad_kilometros_dia = document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value;
		}							
		try {
			cantidad_kilometros_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(cantidad_kilometros_dia);
			document.getElementById("<portlet:namespace />cantidad_kilometros_mes").value = Math.round(cantidad_kilometros_mes * 100)/100;
		} catch (err) {}
				
		if (document.getElementById("<portlet:namespace />importe_kilometro_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_kilometro_unit").value) != ""){
			importe_kilometro_unit = document.getElementById("<portlet:namespace />importe_kilometro_unit").value;
		}
		try {
			importe_total_km = parseFloat(cantidad_kilometros_mes) * parseFloat(importe_kilometro_unit);
			document.getElementById("<portlet:namespace />importe_total_km").value = Math.round(importe_total_km * 100)/100;
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />hs_espera_dia")!=null && trim(document.getElementById("<portlet:namespace />hs_espera_dia").value) != ""){
			hs_espera_dia = document.getElementById("<portlet:namespace />hs_espera_dia").value;
		}
		try {
			hs_espera_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(hs_espera_dia);
			document.getElementById("<portlet:namespace />hs_espera_mes").value = Math.round(hs_espera_mes * 100)/100;
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />importe_hs_espera_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_hs_espera_unit").value) != ""){
			importe_hs_espera_unit = document.getElementById("<portlet:namespace />importe_hs_espera_unit").value;
		}
		try {
			importe_total_hs = parseFloat(hs_espera_mes) * parseFloat(importe_hs_espera_unit);
			document.getElementById("<portlet:namespace />importe_total_hs").value = Math.round(importe_total_hs * 100)/100;
		} catch (err) {}

	    var codigoo = jQuery("#<portlet:namespace />codigo_trat").val();
		
	    if (codigoo == '<%=WebKeysLiquidaciones.PRESTACION_TRANSPORTE%>') {
	    	var cant = 1;
			var imp = 0;
			var imp = parseFloat(importe_total_km) + parseFloat(importe_total_hs);  
			try {
				var x = parseFloat(cant) * parseFloat(imp);
				document.getElementById("<portlet:namespace />cantidad").value = cant;
				document.getElementById("<portlet:namespace />importe").value = imp;
				document.getElementById("<portlet:namespace />total").value = Math.round(x * 100)/100;				
			}
			catch (err) {}
	    }
	}

	function sumarTodo(){
		var cant = 0;
		var imp = 0;
		if (document.getElementById("<portlet:namespace />cantidad")!=null && trim(document.getElementById("<portlet:namespace />cantidad").value) != ""){
			cant = 	document.getElementById("<portlet:namespace />cantidad").value;
		}
		if (document.getElementById("<portlet:namespace />importe") != null && trim(document.getElementById("<portlet:namespace />importe").value)!= ""){
			imp = document.getElementById("<portlet:namespace />importe").value;
		}
		try {
			var x = parseFloat(cant) * parseFloat(imp);
			document.getElementById("<portlet:namespace />total").value = Math.round(x * 100)/100;
		}
		catch (err) {}
	}
	
	function <portlet:namespace />saveTratamientoDiscapacidad() {

		var id_tratamiento = document.getElementById("tratamiento_id").value;		
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
		var cuil = jQuery("#<portlet:namespace />cuil").val();
		var inte = jQuery("#<portlet:namespace />inte").val();
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe_total = jQuery("#<portlet:namespace />importe").val();
		var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

		var recupera_ape = "false";
		if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
		var observaciones = jQuery("#<portlet:namespace />observaciones").val();		
		var documentacion = jQuery("#<portlet:namespace />documentacion").val();
		var accionOriginal = document.getElementById("accionOriginal").value;

// DS		
/*		
		var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad_trat").value;
		var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad_trat").value;
*/		
// New
        var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
		var sucursal_entidad = 0;
		var id_prestador=document.getElementById("<portlet:namespace />id_prestador_trat").value;

// fin New
		
		var id_seccional = 0;//document.getElementById("id_seccional").value;

		var id_tercerizadora = '';
		var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;

		var cantidad_viajes_mensuales = 0;
		var cantidad_kilometros_dia = 0;
		var cantidad_kilometros_mes = 0;
		var importe_kilometro_unit = 0;		
		var importe_total_km = 0;

		var hs_espera_dia = 0;
		var hs_espera_mes = 0;
		var importe_hs_espera_unit = 0;
		var importe_total_hs = 0;

		if (document.getElementById("<portlet:namespace />cantidad_viajes_mensuales")!=null && trim(document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value) != ""){
			cantidad_viajes_mensuales = document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value;
		}
		if (document.getElementById("<portlet:namespace />cantidad_kilometros_dia") != null && trim(document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value)!= ""){
			cantidad_kilometros_dia = document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value;
		}							
		try {
			cantidad_kilometros_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(cantidad_kilometros_dia);			
		} catch (err) {}			
		if (document.getElementById("<portlet:namespace />importe_kilometro_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_kilometro_unit").value) != ""){
			importe_kilometro_unit = document.getElementById("<portlet:namespace />importe_kilometro_unit").value;
		}		
		if (document.getElementById("<portlet:namespace />hs_espera_dia")!=null && trim(document.getElementById("<portlet:namespace />hs_espera_dia").value) != ""){
			hs_espera_dia = document.getElementById("<portlet:namespace />hs_espera_dia").value;
		}
		try {
			hs_espera_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(hs_espera_dia);
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />importe_hs_espera_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_hs_espera_unit").value) != ""){
			importe_hs_espera_unit = document.getElementById("<portlet:namespace />importe_hs_espera_unit").value;
		}	
		
		var esExcepcion = document.getElementById("<portlet:namespace />es_excepcion_tratamiento").value;	

		if (<portlet:namespace />validarCamposTD()) {
			jQuery('#<portlet:namespace />estado').removeAttr('disabled');
			var estado = jQuery("#<portlet:namespace />estado_").val();					
			jQuery('#<portlet:namespace />buscandoPrestadorTD').show();
		
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/editar_tratamiento_entry&id_tratamiento='+id_tratamiento+
			'&id_prestacion='+id_prestacion+'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio+
			'&accionOriginal='+accionOriginal+'&periodicidad='+periodicidad+'&observaciones='+encodeURI(observaciones)+'&recupera_ape='+recupera_ape+'&estado='+estado+'&documentacion='+documentacion+
			'&cuit_entidad='+cuit_entidad+'&sucursal_entidad='+sucursal_entidad+'&id_seccional='+id_seccional+
			'&cantidad_viajes_mes='+cantidad_viajes_mensuales+'&cantidad_kilometros_dia='+cantidad_kilometros_dia+
			'&cantidad_kilometros_mes='+cantidad_kilometros_mes+'&importe_kilometro_unit='+importe_kilometro_unit+
			'&hs_espera_dia='+hs_espera_dia+'&hs_espera_mes='+hs_espera_mes+'&importe_hs_espera_unit='+importe_hs_espera_unit+
			'&id_tercerizadora='+id_tercerizadora+'&importe_tercerizado='+importe_tercerizado+'&id_prestador='+id_prestador+
			'&es_excepcion='+esExcepcion;
			jQuery('#<portlet:namespace />prestadores_resultadoTD').load(url, function() {
				jQuery('#<portlet:namespace />buscandoPrestadorTD').hide();
			});
		}
	}
	
	function <portlet:namespace />validarCamposTD() {
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe=jQuery('#<portlet:namespace />importe').val();
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();
	
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();

/* DS		
		var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad_trat").value;
		var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad_trat").value;
*/	
        var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
        var sucursal_entidad = 0;
//DS fin

		var id_seccional = 0;//document.getElementById("id_seccional").value;

		if (trim(cuit_entidad) == "") {
			alert("Prestador Obligatorio");
			jQuery("#<portlet:namespace />cuit_entidad_trat").focus();
			return false;
		}
		
		if (parseInt(periodoDesdeAnio,10) != parseInt(periodoHastaAnio,10)) {
			alert ("Las fechas deben pertenecer al mismo año lectivo");
			return false;
		}
		
		if ((parseInt(periodoDesdeAnio,10) > parseInt(periodoHastaAnio,10))
				|| (parseInt(periodoDesdeAnio,10) == parseInt(periodoHastaAnio,10) && parseInt(periodoDesdeMes,10) > parseInt(periodoHastaMes,10))
				|| (parseInt(periodoDesdeAnio,10) == parseInt(periodoHastaAnio,10) && parseInt(periodoDesdeMes,10) == parseInt(periodoHastaMes,10) && parseInt(periodoDesdeDia,10) > parseInt(periodoHastaDia,10))){
				alert("La Fecha Desde corresponde a una fecha posterior a la Fecha Hasta.");
				return false;
		}

		if(trim(id_prestacion) == ""){
			alert("<liferay-ui:message key='prestacion-obligatoria' />");
			jQuery("#<portlet:namespace />id_prestacion_trat").focus();
			return false;
		}
		if(trim(id_prestacion) != "" && jQuery("#<portlet:namespace />pres_seleccionada_trat").val()!="1"){
			alert("<liferay-ui:message key='prestacion-invalida' />");
			jQuery("#<portlet:namespace />id_prestacion_trat").focus();
			return false;
		}
		if (cantidad.length == 0 || cantidad == '0.0' || cantidad == '0') {
			alert("<liferay-ui:message key='cantidad-obligatoria' />");
			jQuery('#<portlet:namespace />cantidad').focus();
			return false;
		}
		if (importe.length == 0 || importe == '0.0' || importe == '0') {
			alert("<liferay-ui:message key='importe-obligatorio' />");
			jQuery('#<portlet:namespace />importe').focus();
			return false;
		}
		var codigoo = jQuery("#<portlet:namespace />codigo_trat").val();
	    if (codigoo == '<%=WebKeysLiquidaciones.PRESTACION_TRANSPORTE%>') {			
	    	var importe_kms = document.getElementById("<portlet:namespace />importe_total_km").value;
	    	var importe_hs = document.getElementById("<portlet:namespace />importe_total_hs").value;
			var importe_total = parseFloat (importe_kms) + parseFloat (importe_hs);	
	    	if (importe_total.length == 0 || importe_total == '0.0' || importe_total == '0') {
				alert("Debe ingresar valores para kilómetros o para horas de espera");				
				return false;
			}
	    		    	
	    }


		
		return true;
	}

	function <portlet:namespace />cambiarEstadoTratamiento(estado) {
		if(!confirm("Seguro de querer cambiar el estado del tratamiento?")){
			return false;
		}else{
			var id_tratamiento = document.getElementById("tratamiento_id").value;
			var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
			var cuil = jQuery("#<portlet:namespace />cuil").val();
			var inte = jQuery("#<portlet:namespace />inte").val();
			var cantidad=jQuery('#<portlet:namespace />cantidad').val();
			var importe_total = jQuery("#<portlet:namespace />importe").val();
			var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

			var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
			var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
			var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

			var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
			var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
			var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

			var recupera_ape = "false";
			if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
			var observaciones = jQuery("#<portlet:namespace />observaciones").val();		
			var documentacion = jQuery("#<portlet:namespace />documentacion").val();
			var accionOriginal = document.getElementById("accionOriginal").value;

//DS - Inicio			
//			var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad_trat").value;
//			var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad_trat").value;
			
			var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
			var sucursal_entidad = 0;
			
//DS - Fin			
			
			var id_seccional = 0;//document.getElementById("id_seccional").value;

			var id_tercerizadora = '';
			var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;
			
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/editar_tratamiento_entry&id_tratamiento='+
			id_tratamiento+'&estado='+estado+'&accionOriginal=estado'+
			'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio;
			
			jQuery('#<portlet:namespace />prestadores_resultadoTD').load(url, function() {
				jQuery('#<portlet:namespace />buscandoPrestadorTD').hide();
			});
			return true;
		}
		return false;		
	}

	function <portlet:namespace />imprimirAT(){
		window.location.href ='/pdfservlet/?accion=<%="autorizacionTratamiento"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_tratamientoString() : "0"%>';		
	}

	function <portlet:namespace />imprimirATO(){
		window.location.href ='/odtservlet/?accion=<%="autorizacionTratamientoOdt"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_tratamientoString() : "0"%>';		
	}

	function <portlet:namespace />imprimirATR(){
		window.location.href ='/odtservlet/?accion=<%="autorizacionTratamientoRtf"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_tratamientoString() : "0"%>';		
	}
	
	function cambiaCuit_trat(){
	}

	function <portlet:namespace />mostrarDivTransporte(){
		jQuery('#<portlet:namespace />divTransporte').show();
	}

	function <portlet:namespace />ocultarDivTransporte(){
		jQuery('#<portlet:namespace />divTransporte').hide();
	}

	<portlet:namespace />ocultarDivTransporte();
	
	
//DS - Nuevo para busqueda de importe en contrato detalle	
	jQuery("#<portlet:namespace />codigo_trat").change(function(){
       <portlet:namespace />buscarContratoPrestadorTrat('codigo');
	});
	
	jQuery('#<portlet:namespace />periodoDesdeDia').change(function(){
       <portlet:namespace />buscarContratoPrestadorTrat('periododesdedia');
	});
	
	jQuery('#<portlet:namespace />periodoDesdeMes').change(function(){
       <portlet:namespace />buscarContratoPrestadorTrat('periododesdemes');
	});
	
	jQuery('#<portlet:namespace />periodoDesdeAnio').change(function(){
       <portlet:namespace />buscarContratoPrestadorTrat('periododedanio');
	});

	jQuery('#<portlet:namespace />periodoHastaDia').change(function(){
       <portlet:namespace />buscarContratoPrestadorTrat('periodohastadia');
	});
	
	jQuery('#<portlet:namespace />periodoHastaMes').change(function(){
      <portlet:namespace />buscarContratoPrestadorTrat('periodohastames');
	});
	
	jQuery('#<portlet:namespace />periodoHastaAnio').change(function(){
      <portlet:namespace />buscarContratoPrestadorTrat('periodohastaanio');
	});		
	
//DS - Fin	
	
	function <portlet:namespace />buscarContratoPrestadorTrat(origen){
		var idPrestador=document.getElementById("<portlet:namespace />id_prestador_trat").value;
		var codigo=document.getElementById("<portlet:namespace />codigo_trat").value;
		var idPrestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
		
		var cuil = jQuery("#<portlet:namespace />cuil").val();
		var inte = jQuery("#<portlet:namespace />inte").val();
		
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();
		var id_tratamiento = document.getElementById("tratamiento_id").value;
		var r;
		var bloquea = jQuery("#<portlet:namespace />mensajeContrato").val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/busca_contrato_prestador';
		url += "&idprestador="+idPrestador;
		url += "&codigo="+codigo;
		url += "&cuil="+cuil;
		url += "&inte="+inte;
		url += "&desdedia="+periodoDesdeDia;
		url += "&desdemes="+periodoDesdeMes;
		url += "&desdeanio="+periodoDesdeAnio;
		url += "&hastadia="+periodoHastaDia;
		url += "&hastames="+periodoHastaMes;
		url += "&hastaanio="+periodoHastaAnio;
		url += "&idprestacion="+idPrestacion;

		if( codigo !="" && idPrestador!="" ){
		  jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var mensaje= obj.mensaje;
				var resultado =obj.resultado;
				r=true;
				if(mensaje!=null && mensaje !=""){
				    r = confirm(mensaje+".\nEfectua una Excepción? ");
					if (r == true) {
						jQuery("#<portlet:namespace />mensajeContrato").html('Excepcion agregada');
						jQuery("#<portlet:namespace />es_excepcion_tratamiento").val("SI");
						
					} else {
						jQuery("#<portlet:namespace />mensajeContrato").html('');
						jQuery("#<portlet:namespace />es_excepcion_tratamiento").val("NO");
					}
					
//					jQuery("#<portlet:namespace />mensajeContrato").html(mensaje);
					jQuery("#<portlet:namespace />mensajeContrato").show();
					
	            }else{
	            	
	            	jQuery("#<portlet:namespace />mensajeContrato").html("");
	            	jQuery("#<portlet:namespace />mensajeContrato").hide();
	            	jQuery("#<portlet:namespace />es_excepcion_tratamiento").val("");
	            	
	            }
	            if(resultado!=0 ){
	            	jQuery("#<portlet:namespace />importe").val(Math.round(resultado * 100) / 100);
	            	try{
	            	   sumarTodo();
	            	}catch(e){}
	            	try{
		           	   sumarTodoTransporte();
		           	}catch(e){}
	            }
	            if(!r){
	              <portlet:namespace />limpiarPrestacion();
	            }  
	            return r;
			}				                                                                                                                                                                                                                                                            
		  });
		  
          return;		  
        }	  

	}
	
	function <portlet:namespace />limpiarPrestacion(){
		
	     jQuery("#<portlet:namespace />id_prestacion_trat").val('');
	     jQuery("#<portlet:namespace />codigo_trat").val('');
	     jQuery("#<portlet:namespace />prestacion_trat").val('');
	     jQuery("#<portlet:namespace />pres_seleccionada_trat").val('')
	     jQuery("#<portlet:namespace />btnBuscarPrestacion_trat").show();
	     
	}
	
</script>
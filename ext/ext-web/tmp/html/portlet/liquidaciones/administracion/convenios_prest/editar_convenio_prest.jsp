<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_CONVENIO_PREST);
	boolean showAuditoriaButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_AUDITOR_CONVENIO_PREST);
	
	boolean esEdicion = false;
	String cmd = (String) request.getAttribute(Constants.CMD);
	/* if (prestador == null  ||
	   ( cmd!=null  && cmd.length() > 0  && !request.getAttribute(Constants.CMD).equals(Constants.VIEW)   ) ) {
		esEdicion = true;
	} */
	

/* 	String error =(String) request.getAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT); */
 
	ConvenioPrestacional convenioPrest = (ConvenioPrestacional)request.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_EN_EDICION);
	/* ArrayList<ConvenioPrestacionalDetalle> detalles = convenioPrest != null ? (convenioPrest.getContratoDetalle() != null ? (ArrayList<ConvenioPrestacionalDetalle>)convenioPrest.getContratoDetalle() : new ArrayList<ConvenioPrestacionalDetalle>()) : new ArrayList<ConvenioPrestacionalDetalle>(); */	
	List<TipoPago> tiposPago = (List<TipoPago>) request.getSession().getAttribute(WebKeysLiquidaciones.TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION);
	/* List<Plan> planList = (List<Plan>) request.getSession().getAttribute(WebKeysAfiliados.PLANES_EN_SESSION); */
	List<Plan> planList = null;
	List<PrestadorPlan> planList1 = null;
	if(convenioPrest==null){
		planList = (List<Plan>) request.getSession().getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
	}else{
		planList1 = (List<PrestadorPlan>) request.getSession().getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
	}
	List<TipoNomenclador> tipoNomencladorList =(List<TipoNomenclador>) request.getSession().getAttribute(WebKeysLiquidaciones.TIPOS_NOMENCLADORES_EN_SESSION);
	
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	Calendar fechaVig= CalendarFactoryUtil.getCalendar();
	Calendar fechaVen= CalendarFactoryUtil.getCalendar();
	if(convenioPrest!=null && convenioPrest.getVigencia()!=null){
		fechaVig.setTime(convenioPrest.getVigencia());
	}
	if(convenioPrest!=null && convenioPrest.getVencimiento()!=null){
		fechaVen.setTime(convenioPrest.getVencimiento());
	}
	
	if (convenioPrest == null  ||
	   ( cmd!=null  && cmd.length() > 0  && !request.getAttribute(Constants.CMD).equals(Constants.VIEW)   ) ) {
		esEdicion = true;
	}
%>


<form action="" method="post" name="<portlet:namespace />convenios_prest_fm">
<%-- <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" /> --%>

<input type="hidden" id="<portlet:namespace />id_convprest"
	name="<portlet:namespace />id_convprest"
	value="<%= Validator.isNotNull(convenioPrest) ? convenioPrest.getId() : "0" %>" />
<input type="hidden" id="<portlet:namespace />id_convprest_det"
	name="<portlet:namespace />id_convprest_det" value="" /> 
<input type="hidden" id="<portlet:namespace />plan_text"
	name="<portlet:namespace />plan_text" value="" />
<input type="hidden" id="<portlet:namespace />estado"
	name="<portlet:namespace />estado" value="<%= Validator.isNotNull(convenioPrest) ? convenioPrest.getEstado() : "1" %>" /> 
	
<liferay-ui:success key="insertConvenioOk"  message="<%=(String)request.getAttribute(\"msgConvenioOk\")  %>"  />
<liferay-ui:success key="updateConvenioOk"  message="<%=(String)request.getAttribute(\"msgConvenioOk\")  %>"  />
<liferay-ui:success key="updateEstadoConvPrestoOk"  message="<%=(String)request.getAttribute(\"msgConvenioOk\")  %>"  />
<liferay-ui:error key="conv-prest-duplicado" message="conv-prest-duplicado" />
<liferay-ui:error key="conv-prest-sin-items" message="conv-prest-sin-items" />
<liferay-ui:error key="conv-prest-validaciones" message="<%=(String)request.getAttribute(\"msgConvenioFail\")  %>" />

<fieldset class="block-labels"><legend><liferay-ui:message key="datos-prestador" /></legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">

	<tr>
		<td colspan="6"><liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
			<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador" />
			<liferay-util:param name="cuit_prestador"
				value='<%= Validator.isNotNull(convenioPrest)&&convenioPrest.getPrestador()!=null ? convenioPrest.getPrestador().getCuit(): "" %>' />
			<liferay-util:param name="nombre_prestador"
				value='<%=Validator.isNotNull(convenioPrest)&&convenioPrest.getPrestador()!=null ? convenioPrest.getPrestador().getDescripcion() : "" %>' />
			<liferay-util:param name="id_prestador"
				value='<%=Validator.isNotNull(convenioPrest)&&convenioPrest.getPrestador()!=null&&convenioPrest.getPrestador().getId_prestador() != 0 ? String.valueOf(convenioPrest.getPrestador().getId_prestador()) : "" %>' />
			<liferay-util:param name="esEditable" value='<%=String.valueOf( esEdicion )%>' />
			<liferay-util:param name="controlaPlan" value='<%=String.valueOf( true )%>' />
		</liferay-util:include></td>

		<td>&nbsp;</td>

		<td><label><liferay-ui:message key="estado" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace/>estado_select"
			id="<portlet:namespace/>estado_select" disabled="disabled">
			<option value="1"
				<%=Validator.isNotNull(convenioPrest) && convenioPrest.getEstado().equals(EstadosConvPrest.CARGADO) ? "selected" : ""  %>
				<%=Validator.isNull(convenioPrest) ? "selected" : ""  %>>Cargado</option>
			<option value="2"
				<%=Validator.isNotNull(convenioPrest) && convenioPrest.getEstado().equals(EstadosConvPrest.APROBADO) ? "selected" : ""  %>>Aprobado</option>
			<option value="3"
				<%=Validator.isNotNull(convenioPrest) && convenioPrest.getEstado().equals(EstadosConvPrest.RECHAZADO) ? "selected" : ""  %>>Rechazado</option>
		</select></td>
		
<!-- 		<td><a href="javascript:;" onclick="volverAConvPrestPpal();"><liferay-ui:message key="volver-inicio-convenios-pret" /></a></td>-->
	</tr>
<!-- 	<tr>
		<td colspan="8">&nbsp;</td>
	</tr> -->
	<tr>
		<td><label><liferay-ui:message key="dia-recep" />:</label></td>
		<td><select name="<portlet:namespace/>dia_recepcion"
			id="<portlet:namespace/>dia_recepcion" <% if (!esEdicion) { %>
			disabled="disabled" <%} %>>

			<%for (int i = 1 ; i <= 31 ; i++) {%>
			<option value="<%= i %>"
				<%= (convenioPrest != null && convenioPrest.getDiaRecepcion() == i) ? "selected" : ""  %>><%=i%></option>
			<% } %>
		</select></td>
		<td><label><liferay-ui:message key="cond-pago" />:</label></td>

		<td><select name="<portlet:namespace/>condicion_pago"
			id="<portlet:namespace/>condicion_pago" <% if (!esEdicion) { %>
			disabled="disabled" <%} %>>

			<option value="30"
				<%= convenioPrest != null && convenioPrest.getCondicionDePago() != null 
						? convenioPrest.getCondicionDePago().equals("30") ? "selected" : "" : ""  %>>30 d�as</option>
			<option value="60"
				<%= convenioPrest != null && convenioPrest.getCondicionDePago() != null  
						? convenioPrest.getCondicionDePago().equals("60") ? "selected" : "" : ""  %>>60 d�as</option>
		</select></td>
		<td><label><liferay-ui:message key="forma-pago" />:</label></td>
		<td><select name="<portlet:namespace/>forma_de_pago"
			id="<portlet:namespace/>forma_de_pago" <% if (!esEdicion) { %>
			disabled="disabled" <%} %>>

			<%for (TipoPago tipoPago : tiposPago) {%>
			<option value="<%= String.valueOf(tipoPago.getId()) %>"
				<%= (convenioPrest != null && convenioPrest.getTipoPago().getId()== tipoPago.getId()) ? "selected" : ""  %>><%=tipoPago.getDescripcion()%></option>
			<%
				}
			%>
		</select></td>
		<td colspan="2">&nbsp;<% if (convenioPrest != null && convenioPrest.getBajaFecha() != null){ %> <label>De Baja al <%=DateUtils.format(convenioPrest.getBajaFecha(), DateUtils.SHORT) %> </label> <%} %> </td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="vigen-fecha" />:</label></td>
		<td colspan="2"><liferay-ui:input-date
			dayParam="vigenciaDia"
			dayValue="<%= fechaVig.get(Calendar.DATE)%>"
			monthParam="vigenciaMes"
			monthValue="<%= fechaVig.get(Calendar.MONTH) %>"
			yearParam="vigenciaAnio"
			yearValue="<%= fechaVig.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaVig.get(Calendar.YEAR) - 20 %>"
			yearRangeEnd="<%= fechaVig.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= fechaVig.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<!-- <td colspan="1"></td> -->
		<td colspan="1"><label><liferay-ui:message key="fecha-vencimiento" />:</label></td>
		<% if(convenioPrest!=null && convenioPrest.getVencimiento()!=null){ %>
		<td colspan="2"><liferay-ui:input-date
			dayNullable="<%= true %>"
			dayParam="vencimientoDia"
			dayValue="<%= fechaVen.get(Calendar.DATE)%>"
			monthNullable="<%= true %>"
			monthParam="vencimientoMes"
			monthValue="<%= fechaVen.get(Calendar.MONTH) %>"
			yearNullable="<%= true %>"
			yearParam="vencimientoAnio"
			yearValue="<%= fechaVen.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaVen.get(Calendar.YEAR) - 20 %>"
			yearRangeEnd="<%= fechaVen.get(Calendar.YEAR) + 20 %>"
			firstDayOfWeek="<%= fechaVen.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<%}else{ %>
		<td colspan="2"><liferay-ui:input-date
			dayNullable="<%= true %>"
			dayParam="vencimientoDia"
			monthNullable="<%= true %>"
			monthParam="vencimientoMes"
			yearNullable="<%= true %>"
			yearParam="vencimientoAnio"
			yearRangeStart="<%= fechaVen.get(Calendar.YEAR) - 20 %>"
			yearRangeEnd="<%= fechaVen.get(Calendar.YEAR) + 20 %>"
			firstDayOfWeek="<%= fechaVen.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<%} %>		
		<% if (!!esEdicion && showABMButtons) { %>
		<td>&nbsp;</td>
			<% if (showABMButtons) { %>
				<td colspan="2"><input type="submit" value="<liferay-ui:message key="copiar-conv-pres" />"
					onClick="<portlet:namespace />copiarConvenioDePrestador();return false;" /></td>
			<%}else{ %>
				<td colspan="2">&nbsp;</td>			
			<% } %>	
		<% } %>	
	</tr>		
</table>
</fieldset>
<br/>
<fieldset class="block-labels"><legend><liferay-ui:message key="datos-prestacion" /></legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td colspan="1"><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td colspan="1"><liferay-ui:input-date
			dayParam="prestacionFechaDesdeDia"
			dayValue="<%= fechaHoy.get(Calendar.DATE)%>"
			monthParam="prestacionFechaDesdeMes"
			monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
			yearParam="prestacionFechaDesdeAnio"
			yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 25 %>"
			firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<td colspan="1"><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td colspan="1"><liferay-ui:input-date
			dayParam="prestacionFechaHastaDia"
			dayValue="<%= fechaHoy.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="prestacionFechaHastaMes"
			monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="prestacionFechaHastaAnio"
			yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 25 %>"
			firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
	</tr>
	<tr>
		<td><liferay-ui:message key="tipo-nomenclador" />:</td>
		<td colspan="2"><select name="<portlet:namespace/>tipoNomencladorfiltro" 
							id="<portlet:namespace/>tipoNomencladorfiltro"
							<% if (!esEdicion) { %> disabled="disabled" <%} %>
							<%-- onclick="javascript:<portlet:namespace/>cambiaTipoNomen()" --%>
							onchange="javascript:<portlet:namespace/>cambiaTipoNomen()" >
					<option value="0">Seleccione un nomenclador</option>
					<%	for (TipoNomenclador tnom : tipoNomencladorList) { %>
							<option value="<%= tnom.getId_tipo_nomenclador()%>"><%=tnom.getDescripcion()%></option>
					<%	} %>
				</select></td>
		<td colspan="3">&nbsp;</td>		
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="codigo-desde" />:</label></td>
		<td colspan="3"><liferay-util:include
			page="/html/portlet/utils/prestaciones/busqueda_tipo_nomenclador_prestacion.jsp">
			<liferay-util:param name="search_url"
				value="/liquidaciones/buscar_tipo_nomenclador_prestacion" />
			<!-- <liferay-util:param name="id_tipo_nomenclador" value='' /> -->
			<liferay-util:param name="codigo" value='' />
			<liferay-util:param name="prestacion" value='' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( esEdicion )%>' />
			<liferay-util:param name="suf" value='_desde' />
		</liferay-util:include></td>
	</tr>
	<tr>	
		<td colspan="1"><label><liferay-ui:message key="codigo-hasta" />:</label></td>
		<td colspan="3"><liferay-util:include
			page="/html/portlet/utils/prestaciones/busqueda_tipo_nomenclador_prestacion.jsp">
			<liferay-util:param name="search_url"
				value="/liquidaciones/buscar_tipo_nomenclador_prestacion" />
			<liferay-util:param name="codigo" value='' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( esEdicion )%>' />
			<liferay-util:param name="suf" value='_hasta' />
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="servicio" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace/>servicio"
			id="<portlet:namespace/>servicio" <% if (!esEdicion) { %> disabled="disabled" <%} %>>
			<option value="0">TODOS</option>
			<%for (String servicio : WebKeysGlobal.LISTA_SERVICIO) {%>
			<option value="<%= servicio %>"><%=servicio%></option>
			<%} %>
		</select></td>	
		<td><label><liferay-ui:message key="plan" />:</label></td>
		<td><select name="<portlet:namespace/>plan" id="<portlet:namespace/>plan" 
				<% if (!esEdicion) { %> disabled="disabled" <%} %>>
				
				<%if(planList==null){ %>
					<option value='-1'>Seleccione primero un Prestador</option>
				<%}else if(convenioPrest == null){ %>	
					<option value='0'>TODOS</option>
					<%for (Plan plan : planList) {%>
					<option value="<%= plan.getId()%>"><%=plan.getDescripcion()%></option>
					<%}	%>
				<%}else{%>
					<%for (PrestadorPlan plan : planList1) {%>
					<option value="<%= plan.getId()%>"><%=plan.getPlan().getDescripcion()%></option>
					<%}	%>
				<%} %>
			</select></td>
		<td colspan="1"><label><liferay-ui:message key="Coseguro" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />coseguro"
			name="<portlet:namespace />coseguro" size="12" maxlength="12"
			type="text" value=""
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />coseguro'),event);"
			<% if (!esEdicion) { %> readonly="readonly" <%} %> /></td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="tipo-valoriz" />:</label></td>
		<td><select name="<portlet:namespace/>tipo_valorizacion"
			id="<portlet:namespace/>tipo_valorizacion" <% if (!esEdicion) { %>
			disabled="disabled" <%} %>
			onchange="javascript:<portlet:namespace/>cambiaTipoValoriz()">
			<option value="importe">Importe</option>
			<!-- <option value="porcentaje">Porcentaje</option> provisoriamento deshabilitado pedido por AComas-->
		</select></td>
		<%-- <td colspan="1"><label><liferay-ui:message key="honorarios" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />honorarios"
			name="<portlet:namespace />honorarios" size="8" maxlength="12"
			type="text" value="" onchange="sumarTodo();"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />honorarios'),event);"
			onBlur="javascript:;" <% if (esView) { %> readonly="readonly" <%} %> /></td>

		<td colspan="1"><label><liferay-ui:message key="gastos" />:</label></td>
		<td><input id="<portlet:namespace />gastos"
			name="<portlet:namespace />gastos" size="12" maxlength="12"
			type="text" value="" onchange="sumarTodo();"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />gastos'),event);"
			onBlur="javascript:;" <% if (esView) { %> readonly="readonly" <%} %> /></td> --%>
		<td colspan="1"><label><liferay-ui:message key="importe" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />importe"
			name="<portlet:namespace />importe" size="8" maxlength="12"
			type="text" value=""
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event);"
			onBlur="javascript:;" <% if (!esEdicion) { %> readonly="readonly" <%} %> /></td>

		<td colspan="1"><label><liferay-ui:message key="porcentaje" />:</label></td>
		<td><input id="<portlet:namespace />porcentaje"
			name="<portlet:namespace />porcentaje" size="12" maxlength="12"
			type="text" value="" 
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />porcentaje'),event);"
			onBlur="javascript:;" <% if (!esEdicion) { %> readonly="readonly" <%} %> /></td>
			
		<%-- <td colspan="1"><label><liferay-ui:message key="importe-total" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />importe_total"
			name="<portlet:namespace />importe_total" size="12" maxlength="12"
			type="text" value="" onchange="sumarTodo();"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_total'),event);"
			onBlur="javascript:;" <% if (esView) { %> readonly="readonly" <%} %> /></td> --%>
	</tr>
	<tr>
		<%-- <% if (!esView && showABMButtons) { %>
		<td><input type="submit" value="Agregar"
			onClick="<portlet:namespace />agregarDetalle();return false;" /></td>
		<td>&nbsp;</td>
		<% } %> --%>
		<td><input type="button" value="<liferay-ui:message key="agregar" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
		      onClick="<portlet:namespace />agregarDetalle();" /></td>
	</tr>
</table>
</fieldset>

<table style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td colspan="1">
			<label>&nbsp;Vista detalle por:&nbsp;</label>
			<input type="radio" value="RANGO" 
				   name="<portlet:namespace/>tipoVista"  
				   <%if(request.getSession().getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE) ==null){ %> checked="checked" <% } %> 
				   onchange="javascript:<portlet:namespace />cambioVista();"/>
			<label>&nbsp;rango&nbsp;</label>	   
			<%if(convenioPrest!=null && convenioPrest.getId() > 0){ %>
			<input type="radio" value="CODIGO" 
				   name="<portlet:namespace/>tipoVista" 
				   <%if(request.getSession().getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE)!=null){ %> checked="checked" <% } %>
				   onchange="javascript:<portlet:namespace />cambioVista();" />
			<label>&nbsp;c�digo&nbsp;</label>	   
			<%} %>
			<%-- <select id="<portlet:namespace/>vistaDetalle" name="<portlet:namespace/>vistaDetalle">
				<optgroup label="Visualizaci�n del Detalle">
					<option value="RANGO" selected="selected">Por Rango</option>
					<option value="CODIGO">Por C�digo</option>
				</optgroup>
			</select> --%>
		</td>				
	</tr>
	<tr>
		<td width="100%">
		<div align="center" id="<portlet:namespace />buscandoDetalles">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center"><img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
		</div>
		<div align="center" id="<portlet:namespace />convenio_prest_detalle">
		<liferay-util:include page="/html/portlet/liquidaciones/administracion/convenios_prest/conv_prest_detalle_search_result.jsp">
			<liferay-util:param name="<%=WebKeysLiquidaciones.VIEW_CONVENIO_PREST%>" value="<%=String.valueOf(!esEdicion)%>"/>
		</liferay-util:include>
		<%-- <jsp:include page='contrato_detalle_search_result.jsp' /></div> --%>
		</td>
	</tr>
	<tr>
		<% if (showABMButtons) { %>
			<td width="100%" align="left">
			<%if((cmd!=null && cmd.equalsIgnoreCase(Constants.ADD)) || convenioPrest == null) {%>
					<input type="button" value="<liferay-ui:message key="save" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
						onClick="<portlet:namespace />saveConvPrestacEntry();return false;" />
				<%}
				if((cmd!=null && cmd.equalsIgnoreCase(Constants.UPDATE)) && convenioPrest != null) {%>
					<input type="button" value="<liferay-ui:message key="update" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
						onClick="<portlet:namespace />updateConvPrestacEntry();return false;" />
				<%} %>
		<%} %>
		<% if (showAuditoriaButtons) { %>
		<c:if test="<%= Validator.isNotNull(convenioPrest) 
						&& convenioPrest.getId() > 0
						&& convenioPrest.getEstado().equals(EstadosConvPrest.CARGADO)
						&& convenioPrest.getBajaFecha() == null/* && detalles.size() > 0 */  %>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit" value="Auditar"
				onClick="<portlet:namespace />cambiarEstadoAuditoria(2);" />
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit" value="Rechazar"
				onClick="<portlet:namespace />cambiarEstadoAuditoria(3)" />
		</c:if>
		<% } %>
		</td>
	</tr>
</table>
</form>

<script>
<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.UPDATE)){ %>
<portlet:namespace />buscarPlanesDelPrestador();
<%}%>

function <portlet:namespace />initDateFields(){
	jQuery('#<portlet:namespace />prestacionFechaHastaDia').val("");
	jQuery('#<portlet:namespace />prestacionFechaHastaMes').val("");		
	jQuery('#<portlet:namespace />prestacionFechaHastaAnio').val("");
}


<portlet:namespace />initDateFields();
<portlet:namespace/>cambiaTipoValoriz();

jQuery('#<portlet:namespace />buscandoDetalles').hide();

function <portlet:namespace />saveConvPrestacEntry(){

	if(<portlet:namespace />validarCampos()){
		 
		<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_convenio_prest_entry" /></portlet:actionURL>';
		url = url + '&<%=Constants.CMD %>='+'<%=Constants.SAVE%>'; --%>
		
		var cmd_ = '<%=Constants.SAVE%>';

		var xportletUrl = '/liquidaciones/editar_convenio_prest_entry';
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
	    '</liferay-portlet:actionURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__cmd", cmd_);

		document.<portlet:namespace />convenios_prest_fm.method = 'post';
		submitForm(document.<portlet:namespace />convenios_prest_fm, url);
	} 
}

function <portlet:namespace />updateConvPrestacEntry(){

	if(<portlet:namespace />validarCampos()){
		 
		<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_convenio_prest_entry" /></portlet:actionURL>';
		url = url + '&<%=Constants.CMD %>='+'<%=Constants.UPDATE%>'; --%>
		
		var cmd_ = '<%=Constants.UPDATE%>';

		var xportletUrl = '/liquidaciones/editar_convenio_prest_entry';
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
	    '</liferay-portlet:actionURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__cmd", cmd_);
  	    
		document.<portlet:namespace />convenios_prest_fm.method = 'post';
		submitForm(document.<portlet:namespace />convenios_prest_fm, url);
	} 
}

function <portlet:namespace />validarCampos(){
	
	var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador").value;
	
	if (trim(cuit_entidad) == "") {
		alert("Prestador Obligatorio");
		jQuery("#<portlet:namespace />cuit_prestador").focus();
		return false;
	}
	
	return true;	
}

function <portlet:namespace />validarCamposDetalle(){

	var codigo_d = jQuery("#<portlet:namespace />codigo_desde").val();
	var codigo_h = jQuery("#<portlet:namespace />codigo_hasta").val();
		
	var diaD = document.getElementById("<portlet:namespace />prestacionFechaDesdeDia").value; 
	var mesD = parseInt(document.getElementById("<portlet:namespace />prestacionFechaDesdeMes").value, 10);
	var anioD = document.getElementById("<portlet:namespace />prestacionFechaDesdeAnio").value;		
	var diaH = document.getElementById("<portlet:namespace />prestacionFechaHastaDia").value; 
	var mesH = parseInt(document.getElementById("<portlet:namespace />prestacionFechaHastaMes").value, 10);
	var anioH = document.getElementById("<portlet:namespace />prestacionFechaHastaAnio").value;

	var importe = document.getElementById("<portlet:namespace />importe").value;
	var porcentaje = document.getElementById("<portlet:namespace />porcentaje").value;

	var plan = jQuery("#<portlet:namespace />plan").val();

	if (importe == '') {
		importe = '0';
	}

	if (porcentaje == '') {
		porcentaje = '0';
	}

	if (diaH.length > 0 && mesH > 0 && anioH.length > 0){
		
		if ((parseInt(anioD) > parseInt(anioH))
			|| (parseInt(anioD) == parseInt(anioH) && parseInt(mesD,10) > parseInt(mesH,10))
			|| (parseInt(anioD) == parseInt(anioH) && parseInt(mesD,10) == parseInt(mesH,10) && parseInt(diaD,10) > parseInt(diaH,10))){
			alert("La fecha desde debe ser anterior a la fecha hasta.");
			return false;
		}
	}

	if(jQuery("#<portlet:namespace />codigo_desde").val() == ""){
		alert("C�digo Desde obligatorio");
		jQuery("#<portlet:namespace />codigo_desde").focus();
		return false;
	}
	
	if(jQuery("#<portlet:namespace />codigo_desde").val() != "" && jQuery("#<portlet:namespace />pres_seleccionada_desde").val()!="1"){
		alert("C�digo Desde inv�lido");
		jQuery("#<portlet:namespace />codigo_desde").focus();
		return false;
	}
		
	if (codigo_h == '' || codigo_h == 'undefined'){
		codigo_h = '1000000';
	}

	/* if(codigo_h.length < 6 || codigo_d.length != 6 || (codigo_h.length > 6 && codigo_h != '1000000')){
		alert('Los C�digos deben tener 6 d�gitos');
		return false;
	} */
	
	if ( (codigo_d.localeCompare(codigo_h) == 0) && (parseInt(codigo_d, 10) > parseInt(codigo_h, 10)) ){
		alert('El C�digo Hasta debe ser mayor al C�digo Desde');
		return false;
	}

	if (parseFloat(importe, 10) <= 0 && parseFloat(porcentaje, 10) <= 0) {
		alert('Ingrese Importe y/o Porcentaje');
		return false;		
	}	

	if(plan == -1){
		alert('El Prestador no tiene planes cargados, no se puede cargar prestaciones');
		return false;	
	}

	return true;
}

function <portlet:namespace />limpiarBuscadoresPrestaciones(){
	jQuery("#<portlet:namespace />prestacion_desde").val('');
	jQuery("#<portlet:namespace />codigo_desde").val('');
	jQuery("#<portlet:namespace />pres_seleccionada_desde").val('');
	jQuery("#<portlet:namespace />id_prestacion_desde").val('');
	jQuery("#<portlet:namespace />btnBuscarPrestacion_desde").show();	
	jQuery("#<portlet:namespace />prestacion_hasta").val('');
	jQuery("#<portlet:namespace />codigo_hasta").val('');
	jQuery("#<portlet:namespace />pres_seleccionada_desde").val('');
	jQuery("#<portlet:namespace />id_prestacion_hasta").val('');
	jQuery("#<portlet:namespace />btnBuscarPrestacion_hasta").show();	

}

function <portlet:namespace />limpiarDetalle(){
	document.getElementById("<portlet:namespace/>tipoNomencladorfiltro").selectedIndex = 0; 
	document.getElementById("<portlet:namespace/>tipo_valorizacion").selectedIndex = 0; 
	document.getElementById("<portlet:namespace/>servicio").selectedIndex = 0; 
	document.getElementById("<portlet:namespace/>plan").selectedIndex = 0; 
	
	jQuery("#<portlet:namespace />prestacion_desde").val('');
	jQuery("#<portlet:namespace />codigo_desde").val('');
	jQuery("#<portlet:namespace />pres_seleccionada_desde").val('');
	jQuery("#<portlet:namespace />id_prestacion_desde").val('');
	jQuery("#<portlet:namespace />btnBuscarPrestacion_desde").show();	

	jQuery("#<portlet:namespace />prestacion_hasta").val('');
	jQuery("#<portlet:namespace />codigo_hasta").val('');
	jQuery("#<portlet:namespace />pres_seleccionada_hasta").val('');
	jQuery("#<portlet:namespace />id_prestacion_hasta").val('');
	jQuery("#<portlet:namespace />btnBuscarPrestacion_hasta").show();	

	
	jQuery("#<portlet:namespace />importe").val('');
	jQuery("#<portlet:namespace />porcentaje").val('');
	jQuery("#<portlet:namespace />coseguro").val('');
	
	jQuery("#<portlet:namespace />prestacionFechaHastaDia").val('');
	jQuery("#<portlet:namespace />prestacionFechaHastaMes").val('');
	jQuery("#<portlet:namespace />prestacionFechaHastaAnio").val('');
}

function <portlet:namespace />agregarDetalle() {
	if (!<portlet:namespace />validarCamposDetalle()) {
		return false;
	}

	jQuery('#<portlet:namespace />buscandoDetalles').show();

	var codigo = jQuery("#<portlet:namespace />codigo").val();
	var idPrestador = jQuery("#<portlet:namespace />id_prestador").val();
	var prestacionDesc = jQuery("#<portlet:namespace />prestacion").val();

	var idPrest = "";
	if (jQuery("#<portlet:namespace />id_prestacion").length > 0) {
		idPrest = jQuery("#<portlet:namespace />id_prestacion").val();
	} else if (jQuery("#<portlet:namespace />idPrestacion").length > 0) {
		idPrest = jQuery("#<portlet:namespace />idPrestacion").val();
	}

	// ... resto igual ...

	var params = {
		"idPrestacion" : idPrest,
		"idPrestador" : idPrestador,
		"codigo" : codigo,
		"prestacionDesc" : prestacionDesc,
		"fechaDesde" : fechaDesdeFinal,
		"fechaHasta" : fechaHastaFinal,
		"servicio" : servicio,
		"planId" : planId,
		"planDesc" : plan_text,
		"tipoValorizacion" : tipoValoriz,
		"coseguro" : cosegu,
		"importe" : importe,
		"porcentaje" : porcentaje,
		"<%=Constants.CMD%>" : "<%=Constants.ADD%>"
	};

	jQuery('#<portlet:namespace />convenio_prest_detalle').load(url, params, function() {
		jQuery('#<portlet:namespace />buscandoDetalles').hide();
	});

	<portlet:namespace />limpiarBuscadorPrestacion();
	return false;
}

<%-- function <portlet:namespace />agregarDetalleOK() {		
			document.getElementById("<portlet:namespace />tipo_valorizacion").disabled = false;
			document.getElementById("<portlet:namespace />importe_total").disabled = false;
			document.<portlet:namespace />convenios_prest_fm.<portlet:namespace /><%= Constants.CMD %>.value = "agregaDetalle";
			var plan_text = jQuery("#<portlet:namespace />plan :selected").text();
			document.getElementById("<portlet:namespace />plan_text").value = plan_text;
			url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_contrato_entry' /></portlet:actionURL>";			
			submitForm(document.<portlet:namespace />convenios_prest_fm, url);				
			return true;	
} --%>

function borraConvPrestDetalle(id_convprest_det) {
		jQuery('#<portlet:namespace />id_convprest_det').val(id_convprest_det);
		<%-- document.<portlet:namespace />convenios_prest_fm.<portlet:namespace /><%= Constants.CMD %>.value = "borraDetalle"; --%>				
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/lista_prestaciones_convenio_prest&id_convprest_det='+id_convprest_det+'&<%=Constants.CMD%>='+'<%=Constants.DELETE%>';		
		jQuery("#<portlet:namespace />convenio_prest_detalle").load(url,function(){;					
			/* Liferay.Popup.close(popupPrestador); */	
		});
		return true;
}

function <portlet:namespace />cambiarEstadoAuditoria(estado) {
		<%-- if(estado == 2){
			document.<portlet:namespace />convenios_prest_fm.<portlet:namespace /><%= Constants.CMD %>.value = <%=Constants.APPROVE%>;
		}
		if(estado == 3){
			document.<portlet:namespace />convenios_prest_fm.<portlet:namespace /><%= Constants.CMD %>.value = <%=Constants.REJECT%>;
		} --%>
		var url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_convenio_prest_entry' /></portlet:actionURL>";			

		if(<%=convenioPrest!=null%>){
		url = url + '&id_convenio_prest='+'<%=convenioPrest!=null?convenioPrest.getId():0%>';
		}
	
		if(estado == 2){
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.APPROVE%>';
		}
		if(estado == 3){
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.REJECT%>';
		}
		
		submitForm(document.<portlet:namespace />convenios_prest_fm, url);				
		return true;
}

function inhabilitarCodigoHasta() {
	jQuery('#<portlet:namespace />codigo_hasta').val();
	document.getElementById("<portlet:namespace />codigo_hasta").disabled = true;
}

function habilitarCodigoHasta(){
	jQuery('#<portlet:namespace />codigo_hasta').val();
	document.getElementById("<portlet:namespace />codigo_hasta").disabled = false;
}

function <portlet:namespace/>cambiaTipoValoriz(){
	var tipValor = jQuery('#<portlet:namespace/>tipo_valorizacion').val();
	if(tipValor == 'importe'){
		document.getElementById("<portlet:namespace/>importe").disabled = false;	
		document.getElementById("<portlet:namespace/>porcentaje").disabled = true;
		jQuery('#<portlet:namespace/>porcentaje').val('');
	}
	if(tipValor == 'porcentaje'){
		document.getElementById("<portlet:namespace/>importe").disabled = true;	
		document.getElementById("<portlet:namespace/>porcentaje").disabled = false;
		jQuery('#<portlet:namespace/>importe').val('');
	}

}

function setearTipoValorizacionPorcentaje(){
	jQuery('#<portlet:namespace />tipo_valorizacion').val('porcentaje');	
	document.getElementById("<portlet:namespace />tipo_valorizacion").disabled = true;
	var tipo_val = jQuery("#<portlet:namespace />tipo_valorizacion").val();	
	if (tipo_val == 'porcentaje'){
		inhabilitarImporte();
	}
}

/* function inhabilitarImporte(){
	jQuery('#<portlet:namespace />importe').val('');
	document.getElementById("<portlet:namespace />importe").disabled = true;	
}

function habilitarImporte(){	
	document.getElementById("<portlet:namespace />importe").disabled = false;
	jQuery('#<portlet:namespace />importe').val('');	
}

jQuery('#<portlet:namespace />tipo_valorizacion').change(function(){
	var tipo_valorizacion = jQuery('#<portlet:namespace />tipo_valorizacion').val();	
	if (tipo_valorizacion != 'importe') {
		inhabilitarImporte();
	}
	else {
		habilitarImporte();
	}
});
 */
function cambioCodigoHasta(){
	var codigo_h = jQuery("#<portlet:namespace />codigo_hasta").val();	
	if (codigo_h.length == 0) {
		document.getElementById("<portlet:namespace />tipo_valorizacion").disabled = false;		
	}	
	pasarParametrosConveniosPrest_hasta();
}

jQuery('#<portlet:namespace />codigo_desde').change(function(){
	pasarParametrosConveniosPrest_desde();
});

function pasarParametrosConveniosPrest_desde() {
	var tipo_nomencla = jQuery("#<portlet:namespace />tipo_nomenclador_desde").val();
	var codigo_d = jQuery("#<portlet:namespace />codigo_desde").val();
	var codigo_h = jQuery("#<portlet:namespace />codigo_hasta").val();	

	if (tipo_nomencla == '3') {
		inhabilitarCodigoHasta();
	} 	
	if (tipo_nomencla == '1' || tipo_nomencla == '2' || tipo_nomencla == '4' || tipo_nomencla == '5' || tipo_nomencla == '6') {
		habilitarCodigoHasta();
	}
	/* if (codigo_d.length > 0 && codigo_h.length > 0) {
		setearTipoValorizacionPorcentaje();				
	} */
}

function pasarParametrosConveniosPrest_hasta() {		
	var codigo_d = jQuery("#<portlet:namespace />codigo_desde").val();
	var codigo_h = jQuery("#<portlet:namespace />codigo_hasta").val();
	
	/* if (codigo_d.length > 0 && codigo_h.length > 0) {
		setearTipoValorizacionPorcentaje();
	}	 */
}

var popupPrestador;

function <portlet:namespace />copiarConvenioDePrestador(){	
	popupPrestador = Liferay.Popup({title:"Copiar Convenio de Prestador",modal:true,width:800,position:[220,40]});
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_prestador_convenio_prest';
	jQuery(popupPrestador).load(url);
}

function <portlet:namespace />aceptarCopiaConvenioPrest(){
	var id_prestador = jQuery("#<portlet:namespace />id_prestador_bp").val();
	var id_prestador_convprest = jQuery("#<portlet:namespace />id_prestador").val();
	
	if (id_prestador.length == 0) {
		alert("Seleccione un prestador");
		return false;
	} 
	if (id_prestador_convprest.length == 0){
		if (id_prestador = id_prestador_convprest) {
			alert("Prestador inv�lido");
			return false;
		}
	}
	
	jQuery('#<portlet:namespace />divBusqueda_prestador_bp').show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/lista_prestaciones_convenio_prest&id_prestador='+id_prestador+'&<%=Constants.CMD%>='+'<%=Constants.COPY%>';		
	jQuery("#<portlet:namespace />convenio_prest_detalle").load(url,function(){;					
		Liferay.Popup.close(popupPrestador);	
	});
}

function <portlet:namespace />cancelarCopiaConvenioPrest(){
	Liferay.Popup.close(popupPrestador);
}

function volverAConvPrestPpal() {
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/convenios_prestacionales" /></portlet:renderURL>';
	document.<portlet:namespace />convenios_prest_fm.method = 'post';
	jQuery('#<portlet:namespace />codPrest').val('');
	jQuery('#<portlet:namespace />id_prestador').val('');
	jQuery('#<portlet:namespace />cuit_prestador').val('');
	jQuery('#<portlet:namespace />nombre_prestador').val('');
	submitForm(document.<portlet:namespace />convenios_prest_fm, url);	
}

function <portlet:namespace/>cambiaTipoNomen(){
	var idTipNom = jQuery('#<portlet:namespace/>tipoNomencladorfiltro').val();
	jQuery('#<portlet:namespace />id_tipo_nomenclador_desde').val(idTipNom);
	jQuery('#<portlet:namespace />id_tipo_nomenclador_hasta').val(idTipNom);
	
	<portlet:namespace />limpiarBuscadoresPrestaciones();
}

function addElementToSelect(id_combo, texto, valor) {
	var combo = document.getElementById(id_combo);
	var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice ser� el del nuevo elemento
	combo.options[idxElemento] = new Option();
	combo.options[idxElemento].text = texto; //Este es el texto que ver�s en la combo
	combo.options[idxElemento].value = valor; //Este es el valor que se enviar� cuando hagas un submit del formulario que lo contiene
}

function <portlet:namespace />buscarPlanesDelPrestador(){
	var idPrestador = jQuery('#<portlet:namespace />id_prestador').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/lista_planes_del_prestador&idPrestador='+idPrestador;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace/>plan").length = 0;						
			var obj = jQuery.parseJSON(data);
			/* addElementToSelect("<portlet:namespace/>plan", "TODOS", 0); */
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelect("<portlet:namespace/>plan", text, value);					
			}				                                                                                                                                                                                                                                                            
		}
	});
	
}
	
function <portlet:namespace />cambioVista(){
	jQuery('#<portlet:namespace />buscandoDetalles').show();
	
	var tipoVistaSel = jQuery("input[name='<portlet:namespace/>tipoVista']:checked").val(); 
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/cambio_vista_detalles_prestaciones_convenio_prest';
	
	var params = {"tipoVistaSelec" : tipoVistaSel};
	
	jQuery('#<portlet:namespace />convenio_prest_detalle').load(url,params, function() {
		jQuery('#<portlet:namespace />buscandoDetalles').hide();            															
	});
	
} 

</script>
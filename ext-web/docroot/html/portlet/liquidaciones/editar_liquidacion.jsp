<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.service.ContabilidadServiceUtil"%>
<%@ page import="ar.com.ospim.global.services.ComprobanteServiceUtil"%>
<%@ page import="ar.com.ospim.global.PrestacionComprobanteExistenteException"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%

String portlet_name = "liquidaciones";
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//String origen=	(String)request.getSession().getAttribute("origen");

if(renderResponse.getNamespace().equals("_COR_1_")){
	portlet_name = "correspondencia";
} else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
} else{
	portlet_name = "liquidaciones";
}



	String viewStr = (String)request.getAttribute(WebKeysLiquidaciones.VIEW_LIQUIDACION);
	
	String con_reclamo_prestacional = String.valueOf(request.getAttribute("con_reclamo_prestacional")!=null?request.getAttribute("con_reclamo_prestacional"):0);
	
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	String error =(String) request.getAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT);
	
	Liquidacion liquidacion = (Liquidacion)request.getAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION);
	
	
	
	if(liquidacion==null){
	   liquidacion=	(Liquidacion)request.getSession().getAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION);
	}
	
	String paga = (String)request.getAttribute("paga");
	
	LiquidacionPrestacion liquidacionPrestacion  = (LiquidacionPrestacion)request.getAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION);
	Comprobante comprobante = liquidacion != null ? liquidacion.getComprobante() : null;
	Comprobante.ComprobanteConcepto conceptoConvenios = null;
	if (comprobante != null) {
		conceptoConvenios = ComprobanteServiceUtil.getConceptoConveniosGlobales(comprobante, WebKeysGlobal.OSPIM);
	}
	ArrayList <LiquidacionPrestacion> liquidacionPrestaciones = (ArrayList<LiquidacionPrestacion>)request.getAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION);
	Prestador prestador = Validator.isNotNull(liquidacion) ? (liquidacion.getPrestador_lugar_atencion()!=null &&
			liquidacion.getPrestador_lugar_atencion().getPrestador()!=null?liquidacion.getPrestador_lugar_atencion().getPrestador():null ): null;
	Afiliado afiliado = Validator.isNotNull(liquidacion) && Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getAfiliado() : null;
	Prestacion prestacion = Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getPrestacion() : null;

	Date fechaLiquidacion = null;
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	fechaLiquidacion = Validator.isNotNull(liquidacion)? liquidacion.getFecha() : null;
	if (fechaLiquidacion == null) {
		fechaHoy.setTime(new Date());
	}
	else{
		fechaHoy.setTime(liquidacion.getFecha());
	}

	Date periodoLiquidacion = null;
	Calendar periodo = CalendarFactoryUtil.getCalendar();
	periodoLiquidacion = Validator.isNotNull(liquidacion)? liquidacion.getPeriodo() : null;
	if (fechaLiquidacion == null) {
		periodo.setTime(new Date());
	}
	else{
		periodo.setTime(liquidacion.getPeriodo());
	}

	//periodo prestación por defecto 
	Calendar periodoPrestacion = CalendarFactoryUtil.getCalendar();
	periodoPrestacion.setTime(new Date());

	Date emisionFechaLiquidacion = null;
	Calendar emisionFecha = CalendarFactoryUtil.getCalendar();
	emisionFechaLiquidacion = Validator.isNotNull(liquidacion) ? liquidacion.getFecha_emitido() : null;
	if (emisionFechaLiquidacion == null) {
		emisionFecha.setTime(new Date());
	}
	else{
		emisionFecha.setTime(liquidacion.getFecha_emitido());
	}

	Date reciboFechaLiquidacion = null;
	Calendar reciboFecha = CalendarFactoryUtil.getCalendar();
	reciboFechaLiquidacion = Validator.isNotNull(liquidacion) ? liquidacion.getFecha_recibido() : null;
	if (reciboFechaLiquidacion == null) {
		reciboFecha.setTime(new Date());
	}
	else{
		reciboFecha.setTime(liquidacion.getFecha_recibido());
	}

	Date vencimientoFechaLiquidacion = null;
	Calendar vencimientoFecha = CalendarFactoryUtil.getCalendar();
	vencimientoFechaLiquidacion = Validator.isNotNull(liquidacion) ? liquidacion.getFecha_vencimiento() : reciboFechaLiquidacion; 
	if (vencimientoFechaLiquidacion == null) {
		vencimientoFecha.setTime(DateUtils.anyadeMeses(new Date(), 1));
	}
	else{
		vencimientoFecha.setTime(liquidacion.getFecha_vencimiento());
	}	
	
	String cuil_titular_servicio = (String) request.getSession().getAttribute("cuil_titular_servicio");
	int inte_titular_servicio = (Integer) request.getSession().getAttribute("int_titular_servicio") != null ? (Integer) request.getSession().getAttribute("int_titular_servicio") : 0;
	String servicioS = (String) request.getSession().getAttribute("servicio");
	
	Date prestacionFechaLiquidacion = null;
	Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
	prestacionFechaLiquidacion = (Date) request.getSession().getAttribute("fecha_prestacion_servicio"); 
	if (prestacionFechaLiquidacion == null) {
		prestacionFecha.setTime(new Date());
	}
	else{
		prestacionFecha.setTime(prestacionFechaLiquidacion);
	}
	String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);
	
	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
	
	List<ComprobanteItem> debitoList = null;
	BigDecimal sumaDebitos = BigDecimal.ZERO;
	if (Validator.isNotNull(liquidacion)) {
		debitoList = DebitoServiceUtil.buscaDebitos(Integer.valueOf(liquidacion.getId_liquidacion()));
		sumaDebitos = DebitoServiceUtil.sumaImportesItems(debitoList);
	}
	List<Concepto> conceptos = (List<Concepto>) request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_LIQUIDACION);
	
	Date fecha_cierre_periodo = ContabilidadServiceUtil.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
	Calendar fcp =  CalendarFactoryUtil.getCalendar();
	fcp.setTime(fecha_cierre_periodo);
	int diaPer = fcp.get(Calendar.DATE);
	int mesPer = fcp.get(Calendar.MONTH) + 1;
	int anioPer = fcp.get(Calendar.YEAR);	
	
    String loteAbierto = (String) request.getAttribute("lote_abierto");
    
    
    boolean esLiquidadorExterno = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_LIQUIDACIONES_HOSPITALES);
%>


<form action="" method="post" name="<portlet:namespace />fm">
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" /> <input type="hidden"
		id="<portlet:namespace />motivoAltaDiscapacidad"
		name="<portlet:namespace />motivoAltaDiscapacidad" value="" /> <input
		type="hidden" id="<portlet:namespace />importe_anterior"
		name="<portlet:namespace />importe_anterior" value="0.0" /> <input
		type="hidden" id="<portlet:namespace />cantidad_anterior"
		name="<portlet:namespace />cantidad_anterior" value="0.0" />
		
	<input    type="hidden" id="<portlet:namespace />con_reclamo_prestacional"
	name="<portlet:namespace />con_reclamo_prestacional" value="<%=con_reclamo_prestacional%>" />

    <input     type="hidden"  id="<portlet:namespace />id_reclamo_prestacional"
	name="<portlet:namespace />id_reclamo_prestacional" value="" />
    
    <input  type="hidden"      id="<portlet:namespace />id_prestacion_reclamo_prestacional"
	name="<portlet:namespace />id_prestacion_reclamo_prestacional" value="" />

<input   type="hidden"  id="<portlet:namespace />importeoriginalreclamo" name="<portlet:namespace />importeoriginalreclamo" value="" />
<input   type="hidden"  id="<portlet:namespace />importeoriginalnovalidado" name="<portlet:namespace />importeoriginalnovalidado" value="" />

<input   type="hidden"  id="<portlet:namespace />cargo_ospim" name="<portlet:namespace />cargo_ospim" value="0" />

<input  type="hidden" id="<portlet:namespace />botonprestacionesreclamo"
	name="<portlet:namespace />botonprestacionesreclamo" value="" />
		
	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="encabezado-liquidacion" />
		</legend>

		<table class="lfr-table">
			<input type="hidden" name="<portlet:namespace/>entidad_liquidacion"
				id="<portlet:namespace/>entidad_liquidacion" value="O.S.P.I.M." />
			<input type="hidden" name="<portlet:namespace/>orden"
				id="<portlet:namespace/>orden" value="" />
			<input type="hidden" name="<portlet:namespace />editPrestaci"
				id="<portlet:namespace />editPrestaci" value="" />
			<input type="hidden" name="<portlet:namespace />paga"
				id="<portlet:namespace />paga" value="<%= paga %>>" />

			<tr>
				<td><label><liferay-ui:message key="date" />:</label></td>
				<td><liferay-ui:input-date dayParam="fechaDia"
						dayValue="<%= fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMes"
						monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
						yearParam="fechaAnio"
						yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
						yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
						firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
				<td><label><liferay-ui:message key="periodo" />:</label></td>
				<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
						dayNullable="<%= true %>" dayValue=""
						monthAndYearParam="periodoMesAnio"
						monthValue="<%= periodo.get(Calendar.MONTH) %>"
						monthAndYearNullable="<%= false %>"
						yearValue="<%= periodo.get(Calendar.YEAR) %>"
						yearRangeStart="<%= periodo.get(Calendar.YEAR) - 100 %>"
						yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 100 %>"
						firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
				<td colspan="2"><liferay-ui:message key="numero" />:</label>&nbsp;<input
					id="<portlet:namespace />numero" name="<portlet:namespace />numero"
					size="8" maxlength="8" type="text"
					value="<%=Validator.isNotNull(liquidacion) ? liquidacion.getId_liquidacionString() : "" %>"
					readonly='readonly' /> &nbsp;&nbsp;&nbsp; <c:if
						test="<%= Validator.isNotNull(liquidacion) && liquidacion.getIdOP() != 0 %>">
						<label>OP: <%=liquidacion.getIdOP() + " / " + liquidacion.getFechaOP().toString()%></label>
					</c:if></td>
				<td colspan="2"><label>Ocultar Detalle:</label>&nbsp; <input
					type="checkbox" id="<portlet:namespace />tercerizado_cab"
					name="<portlet:namespace />tercerizado_cab" value="1" /></td>
				<td colspan="2"><input id="<portlet:namespace />debitos_cab"
					name="<portlet:namespace />debitos_cab" size="10" maxlength="20"
					type="hidden"
					value="<%= Validator.isNotNull(liquidacion) ? liquidacion.getDebitado() : "0" %>"
					<% if (esView) { %> readonly="readonly" <%} %> /></td>
					
				<td colspan="2" >
				
				  <div align="center" id="<portlet:namespace />divOblea" style="background-color: #bfcf90">
				    <table>
				      <tr>
				       <td>&nbsp;&nbsp;</td>
				       <td><label> Carta Documento: </label></td>
				       <td>    <input id="<portlet:namespace />oblea"
					        name="<portlet:namespace />oblea" size="30" 
					        type="text" value="<%=Validator.isNotNull(liquidacion) && Validator.isNotNull(liquidacion.getComprobante()) &&
					             Validator.isNotNull(liquidacion.getComprobante().getObleaCorreo())
					           ? liquidacion.getComprobante().getObleaCorreo() : "" %>"  
					        readonly="readonly" />
					   </td>   
					   <td>&nbsp;&nbsp;</td>  
					  </tr>      
					 </table>      
				  </div>	 
				</td>		
			</tr>
			<tr>
				<td colspan="12">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="12">
					<fieldset class="block-labels">
						<liferay-util:include
							page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
							<%if(portlet_name.equals("correspondencia")){ %>  	
							<liferay-util:param name="search_url" value="/correspondencia/buscar_prestador" />
							<%}else if(portlet_name.equals("comprobantes")){ %>  	
							<liferay-util:param name="search_url" value="/comprobantes/buscar_prestador" />
							<%}else{ %>  
							<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador" />
							<%} %> 
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
							<liferay-util:param name="esLiquidadorHospital" value='<%= String.valueOf(esLiquidadorExterno) %>'/>	
						</liferay-util:include>
					</fieldset>
				</td>
			</tr>
			<tr>
				<td colspan="12">&nbsp;</td>
			</tr>
			<liferay-ui:error
				exception="<%= ComprobanteExistenteException.class %>"
				message="exception-comp-existente" />
			<liferay-ui:error
				exception="<%= PrestacionComprobanteExistenteException.class %>"
				message="exception-prestacion-comp-existente" />	
			<liferay-ui:error
				exception="<%= ComprobanteInexistenteException.class %>"
				message="exception-comp-inexistente" />
			<liferay-ui:error
				exception="<%= ComprobanteInexistenteException.class %>"
				message="empresa-inexistente-cuit" />
			<liferay-ui:error
				exception="<%= FechaMenorACierreContableException.class %>"
				message="comprobante-recepcion-menor-fecha-contable" />
				
			<tr>
				<td><label><liferay-ui:message key="comprobante" />:</label></td>
				<td colspan="5"><select
					id="<portlet:namespace/>comprobante_tipo"
					name="<portlet:namespace/>comprobante_tipo" <% if (esView) { %>
					disabled="disabled" <%} %>>
						<option value=""></option>
						<option value="FCP"
							<%=Validator.isNotNull(liquidacion) && liquidacion.getCompro_a_debitar_tipo().equals("FCP") ? "selected" : ""  %>>FCP</option>
						<option value="RCB"
							<%=Validator.isNotNull(liquidacion) && liquidacion.getCompro_a_debitar_tipo().equals("RCB") ? "selected" : ""  %>>RCB</option>
						<option value="NCR"
							<%=Validator.isNotNull(liquidacion) && liquidacion.getCompro_a_debitar_tipo().equals("NCR") ? "selected" : ""  %>>NCR</option>
				</select> &nbsp; <select name="<portlet:namespace/>comprobante_letra"
					id="<portlet:namespace/>comprobante_letra" <% if (esView) { %>
					disabled="disabled" <%} %>>
						<option value=""></option>
						<option value="B"
							<%=Validator.isNotNull(liquidacion) && liquidacion.getCompro_a_debitar_letra().equals("B") ? "selected" : ""  %>>B</option>
						<option value="C"
							<%=Validator.isNotNull(liquidacion) && liquidacion.getCompro_a_debitar_letra().equals("C") ? "selected" : ""  %>>C</option>
				</select> &nbsp; <input id="<portlet:namespace />sucu"
					name="<portlet:namespace />sucu" size="5" maxlength="4" type="text"
					value="<%=Validator.isNotNull(liquidacion) ? (liquidacion.getSucu()!=0?liquidacion.getSucu():"" ): "" %>"
					<% if (esView) { %> readonly="readonly" <%} %> />&nbsp; <input
					id="<portlet:namespace />comprobante_nro"
					name="<portlet:namespace />comprobante_nro" size="11"
					maxlength="8" type="text"
					value="<%=Validator.isNotNull(liquidacion) ? liquidacion.getCompro_a_debitar_numero() : "" %>"
					<% if (esView) { %> readonly="readonly" <%} %>
					onblur="formateaNroComprobante()" /></td>
				<td colspan="1"><label><liferay-ui:message
							key="importe-total" />:</label></td>
				<td colspan="1"><input id="<portlet:namespace />importe_total"
					name="<portlet:namespace />importe_total" size="14" maxlength="20"
					type="text"
					value="<%=Validator.isNotNull(liquidacion)  ? (liquidacion.getImporte()!=null?liquidacion.getImporte():"0" ): "0" %>"
					onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_total'),event);"
					onBlur="pierdeFocoImporteLiq();" <% if (esView) { %>
					readonly="readonly" <%} %> /></td>
				<td colspan="1"><label>Debitos:</label></td>
				<td colspan="1"><input id="<portlet:namespace />debitado"
					name="<portlet:namespace />debitado" size="14" maxlength="20"
					type="text" value="<%= sumaDebitos %>" readonly="readonly" /></td>
				<td colspan="1"><label><liferay-ui:message key="orden-compra-nro" />:</label></td>
				<td colspan="1"><input id="<portlet:namespace />nro_oc"
					name="<portlet:namespace />nro_oc" size="12" maxlength="8"
					type="text" value="<%=Validator.isNotNull(liquidacion) ? liquidacion.getIdOC() : "0" %>"  
					 <% if (esView) { %> readonly="readonly" <%} %>/></td>	
			</tr>
			<tr>
				<td colspan="12">&nbsp;</td>
			</tr>
			<tr>
				<td><label><liferay-ui:message key="observaciones" />:</label></td>
				<td colspan="9"><textarea rows="2" cols="70"
						id="<portlet:namespace />observaciones"
						name="<portlet:namespace />observaciones" <% if (esView) { %>
						<%="readonly='readonly'" %> <%}%>><%= Validator.isNotNull(liquidacion) && liquidacion.getObservaciones() != null? liquidacion.getObservaciones() : "" %></textarea>
				</td>
				
				<td></td>
				
			   	 	 
			</tr>
			<tr>
				<td colspan="12">&nbsp;</td>
			</tr>
			<tr>
				<td><label><liferay-ui:message key="fecha-emision" />:</label></td>
				<td><liferay-ui:input-date dayParam="fechaEDia"
						dayValue="<%= emisionFecha.get(Calendar.DATE)%>"
						monthParam="fechaEMes"
						monthValue="<%= emisionFecha.get(Calendar.MONTH) %>"
						yearParam="fechaEAnio"
						yearValue="<%= emisionFecha.get(Calendar.YEAR) %>"
						yearRangeStart="<%= emisionFecha.get(Calendar.YEAR) - 120 %>"
						yearRangeEnd="<%= emisionFecha.get(Calendar.YEAR) + 120 %>"
						firstDayOfWeek="<%= emisionFecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
				<td>&nbsp;</td>
				<td><label><liferay-ui:message key="fecha-recibido" />:</label></td>
				<td><span id="recep"><liferay-ui:input-date
							dayParam="fechaRDia"
							dayValue="<%= reciboFecha.get(Calendar.DATE)%>"
							monthParam="fechaRMes"
							monthValue="<%= reciboFecha.get(Calendar.MONTH) %>"
							yearParam="fechaRAnio"
							yearValue="<%= reciboFecha.get(Calendar.YEAR) %>"
							yearRangeStart="<%= reciboFecha.get(Calendar.YEAR) - 120 %>"
							yearRangeEnd="<%= reciboFecha.get(Calendar.YEAR) + 120 %>"
							firstDayOfWeek="<%= reciboFecha.getFirstDayOfWeek() - 1 %>"
							disabled="<%= esView %>" /></span></td>
				<td>&nbsp;</td>
				<td><label><liferay-ui:message key="fecha-vencimiento" />:</label></td>
				<td colspan="5"><liferay-ui:input-date dayParam="fechaVDia"
						dayValue="<%= vencimientoFecha.get(Calendar.DATE)%>"
						monthParam="fechaVMes"
						monthValue="<%= vencimientoFecha.get(Calendar.MONTH) %>"
						yearParam="fechaVAnio"
						yearValue="<%= vencimientoFecha.get(Calendar.YEAR) %>"
						yearRangeStart="<%= vencimientoFecha.get(Calendar.YEAR) - 120 %>"
						yearRangeEnd="<%= vencimientoFecha.get(Calendar.YEAR) + 120 %>"
						firstDayOfWeek="<%= vencimientoFecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
			</tr>
		</table>
		<div id="<portlet:namespace />botonesTercerizada"></div>
	</fieldset>
	<div id="<portlet:namespace />divDatosPrestacion">
		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="datos-prestacion" />
			</legend>
			<table>
				<tr>
					<td colspan="1"><label><liferay-ui:message key="date" />:</label></td>
					<td colspan="1"><liferay-ui:input-date
							dayParam="prestacionFechaDia"
							dayValue="<%= prestacionFecha.get(Calendar.DATE)%>"
							monthParam="prestacionFechaMes"
							monthValue="<%= prestacionFecha.get(Calendar.MONTH) %>"
							yearParam="prestacionFechaAnio"
							yearValue="<%= prestacionFecha.get(Calendar.YEAR) %>"
							yearRangeStart="<%= prestacionFecha.get(Calendar.YEAR) - 120 %>"
							yearRangeEnd="<%= prestacionFecha.get(Calendar.YEAR) + 120 %>"
							firstDayOfWeek="<%= prestacionFecha.getFirstDayOfWeek() - 1 %>"
							disabled="<%= esView %>" /></td>

					<td colspan="1"><label><liferay-ui:message
								key="periodo" />:</label></td>
					<td colspan="1"><liferay-ui:input-date
							dayParam="periodoPrestacionDia" dayNullable="<%= true %>"
							dayValue="" monthAndYearParam="periodoPrestacionMesAnio"
							monthValue="<%= periodoPrestacion.get(Calendar.MONTH) %>"
							monthAndYearNullable="<%= false %>"
							yearValue="<%= periodoPrestacion.get(Calendar.YEAR) %>"
							yearRangeStart="<%= periodoPrestacion.get(Calendar.YEAR) - 100 %>"
							yearRangeEnd="<%= periodoPrestacion.get(Calendar.YEAR) + 100 %>"
							firstDayOfWeek="<%= periodoPrestacion.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>

					<td colspan="1"><label><liferay-ui:message
								key="servicio" />:</label></td>
					<td colspan="1"><select name="<portlet:namespace/>servicio"
						id="<portlet:namespace/>servicio" <% if (esView) { %>
						disabled="disabled" <%} %>>
							<option value=""></option>
							<%for (String servicio : WebKeysGlobal.LISTA_SERVICIO) {%>
							<option value="<%= servicio %>"
								<%= (servicioS != null && servicio.equalsIgnoreCase(servicioS)) ? "selected" : ""  %>><%=servicio%></option>
							<%
					}
					%>
					</select></td>
					<td colspan="1">&nbsp;</td>
					<td colspan="1"><label>Cargar a Modo Estadístico:</label></td>
					<td colspan="1"><select
						name="<portlet:namespace/>descontar_capitas"
						id="<portlet:namespace/>descontar_capitas">
							<option value="0"
								<%= Validator.isNotNull(liquidacionPrestacion) && Validator.isNotNull(liquidacionPrestacion.getTercerizado()) && liquidacionPrestacion.getTercerizado().equals("0") ? "selected" : ""  %>>No</option>
							<option value="1"
								<%= Validator.isNotNull(liquidacionPrestacion) && Validator.isNotNull(liquidacionPrestacion.getTercerizado()) && liquidacionPrestacion.getTercerizado().equals("1") ? "selected" : ""  %>>Si</option>
					</select></td>

				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="12"><input type="hidden"
						id="<portlet:namespace />fprest"
						name="<portlet:namespace />fprest"
						value="<%=prestacionFechaString%>" />
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="datos-afiliado" />
							</legend>
							<liferay-util:include
								page='/html/portlet/liquidaciones/busqueda_afiliado_filtro_prevencion.jsp'>
								<liferay-util:param value="<%= String.valueOf(!esView) %>"
									name="edit_mode" />
								<liferay-util:param name="cuil"
									value='<%= Validator.isNotNull(cuil_titular_servicio) ?  cuil_titular_servicio : "" %>' />
								<liferay-util:param name="inte"
									value='<%=  String.valueOf(inte_titular_servicio) %>' />
									<liferay-util:param name="pag_reintegro_reclamo" value='1' />
								<liferay-util:param name="pag_reintegro" value='1' />
							</liferay-util:include>
						</fieldset></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				</table>
				
		
</div>
<div id="<portlet:namespace />divBuscarPorNroLote">
		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="busqueda-por-lote" />
			</legend>
			<table>
			<tr>
				 <td><label>Nro.Lote: &nbsp;</label></td>
			 	 <td><input id="<portlet:namespace />nroLote_filtro" name="<portlet:namespace />nroLote_filtro" size="10" maxlength="20" type="text" value=''
			       onKeyPress="return soloNumeros(event)" /></td> 
			       <td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			     <td>  <input type="submit"
				value="<liferay-ui:message key="buscar" />"
				onClick="<portlet:namespace />buscarPorLote();return false;" /></td>
			 </tr>
			</table>
</div>

&nbsp;
&nbsp;


<div id="<portlet:namespace />div_boton_reclamos_prestaciones">
		<input type="button" value="Ver Prestaciones del Reclamo Prestacional"
			onClick="<portlet:namespace />ver_prestaciones_reclamos();return false;" />
</div>

<div id="<portlet:namespace />div_boton_cancelar_reclamos_prestaciones">			
		<input type="button" value="Cancelar Ingreso de Prestacion del Reclamo"
			onClick="<portlet:namespace />cancelarYlimpiaCampos();<portlet:namespace />limpiarCamposMedicamento();return false;" />	
</div>	
<div id="<portlet:namespace />div_label_prestacion_reclamo">
<span  style="font-size:125%"> <b>Editando Prestacion de Reclamo de Afiliado</b></span>
</div>

<div align="center" id="<portlet:namespace />div_boton_oculta_reclamos_prestaciones">
		<input type="button" value="Oculta Prestaciones del Reclamo Prestacional" onClick="<portlet:namespace />oculta_prestaciones_reclamos();" />
		<br><br>
		<span  style="font-size:155%"><b>Listado De Prestaciones de Reclamos del Afiliado</b></span>
</div>	

<div align="center" id="<portlet:namespace />div_boton_oculta_reclamos_prestaciones_por_nro_lote">
		<input type="button" value="Oculta Prestaciones del Reclamo Prestacional" onClick="<portlet:namespace />oculta_prestaciones_reclamos_por_nro_lote();" />
		<br><br>
		<span  style="font-size:155%"><b>Listado De Prestaciones de Reclamos del Afiliado</b></span>
</div>	

<div id="<portlet:namespace />div_reclamos_prestaciones">
<fieldset class="block-labels"><legend><liferay-ui:message
	key="Prestaciones de los Reclamos del Afiliado" /></legend>

    <liferay-util:include page="/html/portlet/liquidaciones/reintegros/reintegro_prestaciones_reclamos_search_result.jsp">	</liferay-util:include>

</fieldset>
</div>

<div id="<portlet:namespace />div_reclamos_prestaciones_por_lotes"  style="height:250px;overflow:auto;">
<fieldset class="block-labels"><legend><liferay-ui:message
	key="Prestaciones de los Reclamos del Afiliado" /></legend>

    <liferay-util:include page="/html/portlet/liquidaciones/reintegros/reintegro_prestaciones_reclamos_search_result_por_lote.jsp">	</liferay-util:include>

</fieldset>
</div>
				
<div id="<portlet:namespace />div_busqueda_prestacion">				
<table class="lfr-table">
				<tr>
					<td colspan="1"><label><liferay-ui:message
								key="prestacion" />:</label></td>
					<td colspan="3" align="left">
					
							<liferay-util:include page="/html/portlet/utils/prestaciones/busqueda_prestacion.jsp">
							
							
							<%if(portlet_name.equals("correspondencia")){ %>  	
								<liferay-util:param name="search_url" value="/correspondencia/buscar_prestacion" />
							<%}else if(portlet_name.equals("comprobantes")){ %> 
							    <liferay-util:param name="search_url" value="/comprobantes/buscar_prestacion" />	
							<%}else{ %>  
								<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestacion" />
							<%} %>

							<liferay-util:param name="id_prestacion" value='<%=liquidacionPrestacion!=null && liquidacionPrestacion.getId_prestacion() != 0 ? String.valueOf(liquidacionPrestacion.getId_prestacion()) : "" %>' />
							<liferay-util:param name="codigo" value='<%=liquidacionPrestacion!=null && liquidacionPrestacion.getPrestacion() != null ? liquidacionPrestacion.getPrestacion().getCodigo() : "" %>' />
							<liferay-util:param name="prestacion" value='<%=liquidacionPrestacion!=null && liquidacionPrestacion.getPrestacion() != null ? liquidacionPrestacion.getPrestacion().getDescripcion() : "" %>' />
							<liferay-util:param name="esEditable" value='<%=String.valueOf( !esView )%>' />
						</liferay-util:include>
					</td>
					<td>&nbsp;</td>
					<td colspan="1"><label><liferay-ui:message key="cant" />:</label>
					</td>
					<td colspan="1"><input id="<portlet:namespace />cantidad"
						name="<portlet:namespace />cantidad" size="5" maxlength="8"
						type="text"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getCantidad() : "" %>"
						onchange="sumarTodo();"
						onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad'),event);"
						
			<% if (esView) { %> readonly="readonly" <%} %> /></td>
					<td colspan="1"><label><liferay-ui:message key="imp" />:</label></td>
					<td colspan="1"><input id="<portlet:namespace />importe"
						name="<portlet:namespace />importe" size="12" maxlength="11"
						type="text"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getImporte() : "" %>"
						onchange="sumarTodo();"
						onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event);"
						<% if (esView) { %> readonly="readonly" <%} %> /></td>
					<td colspan="1"><label><liferay-ui:message key="total" />:</label></td>
					<td colspan="1"><input id="<portlet:namespace />total"
						name="<portlet:namespace />total" size="12" maxlength="15"
						type="text"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getMultiplicarImporteCant() : "" %>"
						<% if (esView) { %> readonly="readonly" <%} %> /></td>
					
				</tr>
				
				<tr><td>&nbsp;</td></tr>
				<tr>
				<td colspan="1"><label><liferay-ui:message key="cargo-prestadora" />:</label></td>
					<td colspan="1"><input id="<portlet:namespace />cargo_prestadora"
						name="<portlet:namespace />cargo_prestadora" size="12" maxlength="15"
						type="text"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? (liquidacionPrestacion.getCargoPrestadora()!=null?liquidacionPrestacion.getCargoPrestadora():"0" ): "0" %>"
						<% if (esView) { %> readonly="readonly" <%} %> /></td>
						
						<td colspan="1"><label>Cargo Monotributo:</label></td>
					    <td colspan="1"><input id="<portlet:namespace />cargo_imesa"
						name="<portlet:namespace />cargo_imesa" size="12" maxlength="15"
						type="text"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? (liquidacionPrestacion.getCargoImesa()!=null?liquidacionPrestacion.getCargoImesa():"0" ): "0" %>"
						<% if (esView) { %> readonly="readonly" <%} %> /></td>
				</tr>
				
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<input id="<portlet:namespace />solicitado"
						name="<portlet:namespace />solicitado" size="5" maxlength="10"
						type="hidden"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getSolicitado() : "0" %>"
						onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />solicitado'),event);"
						<% if (esView) { %> readonly="readonly" <%} %> />
					<input id="<portlet:namespace />resultado"
						name="<portlet:namespace />resultado" size="12" maxlength="20"
						type="hidden"
						value="<%= Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getResultado() : "0" %>"
						<% if (esView) { %> readonly="readonly" <%} %> />
					<td colspan="1"><input type="hidden"
						name="<portlet:namespace/>descontar_capitas_"
						value="<%=Validator.isNotNull(liquidacionPrestacion) ? liquidacionPrestacion.getTercerizado()  : ""  %>" />
					</td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
			</table>
		</fieldset>

	</div>
	
	
	<div id="<portlet:namespace />divDatosConceptos">
		<fieldset class="block-labels">
			<legend>Conceptos</legend>
			<table>
				<tr>
					<td><liferay-ui:message key="conceptos" />:&nbsp;</td>
					<td><select id="<portlet:namespace />id_concepto"
						name="<portlet:namespace />id_concepto">
							<% 	for (Concepto concepto : conceptos) {  %>
							<option value="<%=concepto.getId()%>"><%=concepto.getDescripcion()%></option>
							<%	 } %>
					</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td><label><liferay-ui:message key="importe" />:&nbsp;</label></td>
					<td><input type="text"
						value='<%=conceptoConvenios != null ? conceptoConvenios.getImporte().toString() : "" %>'
						name="<portlet:namespace />importe_concepto"
						id="<portlet:namespace />importe_concepto"
						onkeydown="allowOnlyDigitsAndDecimals(event)"
						onchange="agregarCeros(this);" /></td>	   	
				</tr>
			</table>
		</fieldset>
	</div>
	
	
	<div id="<portlet:namespace />divDatosConceptosConCargos">
		<fieldset class="block-labels">
			<legend>Conceptos</legend>
			<table class="lfr-table">
				<tr>
					<td><liferay-ui:message key="conceptos" />:&nbsp;</td>
					<td align="center"><select id="<portlet:namespace />id_concepto"
						name="<portlet:namespace />id_concepto">
							<%
								for (Concepto concepto : conceptos) {
							%>
							<option value="<%=concepto.getId()%>"><%=concepto.getDescripcion()%></option>
							<%
								}
							%>
					</select>&nbsp;</td>
			
				
					<td colspan="1"><label><liferay-ui:message key="importe" />:&nbsp;&nbsp;</label></td>
					<td colspan="1"><input type="text"
						value='<%=conceptoConvenios != null ? conceptoConvenios.getImporte().toString() : ""%>'
						name="<portlet:namespace />importe_concepto_sin_detalle"
						id="<portlet:namespace />importe_concepto_sin_detalle"
						onkeydown="allowOnlyDigitsAndDecimals(event)"
						onchange="agregarCeros(this);" /></td>
				</tr> 
				<tr>
					<td>&nbsp;</td>
				</tr>
				 <tr align="left"> 
						
					    <td  colspan="1">&nbsp;<label><liferay-ui:message key="cargo-ospim" />:&nbsp;</label></td>
						 <td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoOspim() : "0.00" %>'
							name="<portlet:namespace />cargo_ospim_sin_detalle"
							id="<portlet:namespace />cargo_ospim_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);" /></td>	
						<td  colspan="1">&nbsp;<label><liferay-ui:message key="cargo-prestadora_terc_preven" />:&nbsp;&nbsp;</label></td>
						<td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoPS() : "0.00" %>'
							name="<portlet:namespace />cargo_prestadora_sin_detalle"
							id="<portlet:namespace />cargo_prestadora_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);" /></td>
						<td  colspan="1">&nbsp;<label><liferay-ui:message key="cargo-prestadora_terc" />:&nbsp;&nbsp;</label></td>
						<td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoEnSalud() : "0.00" %>'
							name="<portlet:namespace />cargo_prestadora_en_salud_sin_detalle"
							id="<portlet:namespace />cargo_prestadora_en_salud_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);" /></td>	
				</tr>	
				<tr align="left"> 		
						<td  colspan="1">&nbsp;<label><liferay-ui:message key="cargo-prestadora-omint" />:&nbsp;&nbsp;</label></td>
						<td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoOmint() : "0.00" %>'
							name="<portlet:namespace />cargo_omint_sin_detalle"
							id="<portlet:namespace />cargo_omint_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);"/></td>
						<td  colspan="1">&nbsp;<label>Cargo CEMIC:&nbsp;&nbsp;</label></td>	
						<td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoCemic() : "0.00" %>'
							name="<portlet:namespace />cargo_cemic_sin_detalle"
							id="<portlet:namespace />cargo_cemic_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);"/></td>		
				        <td  colspan="1">&nbsp;<label>Cargo Monotributo:&nbsp;&nbsp;</label></td>	
					    <td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoImesa() : "0.00" %>'
							name="<portlet:namespace />cargo_imesa_sin_detalle"
							id="<portlet:namespace />cargo_imesa_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);"/></td>
							
						<td  colspan="1">&nbsp;<label>Cargo CES:&nbsp;&nbsp;</label></td>	
					    <td colspan="1"><input type="text"
							value='<%= Validator.isNotNull(liquidacion) ? liquidacion.getCargoCES() : "0.00" %>'
							name="<portlet:namespace />cargo_ces_sin_detalle"
							id="<portlet:namespace />cargo_ces_sin_detalle"
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							onchange="agregarCeros(this);"/></td>	
					
					</tr>		


			</table>
		</fieldset>
	</div>
	
	

	<table>
		<tr>
			<td colspan="12">&nbsp;</td>
		</tr>
		<tr>
			<% if (!esView) { %>
			<% if (!paga.equals("1")) { %>
			<td><input type="submit"
				value="<liferay-ui:message key="save" />"
				onClick="<portlet:namespace />confirmaComprobante();return false;" /></td>
			<c:if
				test="<%=Validator.isNotNull(liquidacion) && liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO%>">
				<td>&nbsp;</td>
				<td><input type="submit"
					value="<liferay-ui:message key="cerrar" />"
					onClick="<portlet:namespace />cerrarLiquidacionEntry('<%=WebKeysLiquidaciones.LIQUIDACION_ESTADO_CERRADO%>>');return false;" /></td>
			</c:if>
			<c:if
				test="<%=Validator.isNotNull(liquidacion) && liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO%>">
				<td>&nbsp;</td>
				<td><input type="button" id="elntdeb1"
					value="<liferay-ui:message key="debitos" />"
					onClick="<portlet:namespace />debitosLiquidaciones( <%=liquidacion.getId_liquidacionString()%>, 'false');" />
				</td>
			</c:if>
			<c:if
				test="<%=Validator.isNotNull(liquidacion) && liquidacion.getEstado() > WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO%>">
				<td>&nbsp;</td>
				<td><input type="button" id="elntdeb2"
					value="<liferay-ui:message key="debitos" />"
					onClick="<portlet:namespace />debitosLiquidaciones( <%=liquidacion.getId_liquidacionString()%>, 'true');" />
				</td>
			</c:if>
			<%}  else { //botones para ajustar liquidaciones pagas %>
			<td><input type="button"			    
			    id="<portlet:namespace />ajustarLiquidacion"
		        name="<portlet:namespace />ajustarLiquidacion"		
				value="<liferay-ui:message key="Ajustar Prestaciones" />"
				onClick="<portlet:namespace />ajustarLiquidacionEntry();return false;" /></td>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<liferay-ui:message key="Cerrar Ajustes" />"
				onClick="<portlet:namespace />cerrarAjustesLiquidacionEntry();return false;" /></td>
			<%} %>
			<td>
				<div id="<portlet:namespace />div_tratamientos_discapacidad">
					<input type="button" value="Ver Tratamientos"
						onClick="<portlet:namespace />ver_tratamientos_autorizados();return false;" />
				</div>
			</td>
			<%} %>
			<c:if
				test="<%=Validator.isNotNull(liquidacion) && debitoList != null && debitoList.size() >  0 %>">
				<td>&nbsp;</td>
				<td><input type="button" value="<liferay-ui:message key="imprimir-nota-debito" />"
					onClick="<portlet:namespace />imprimirND();return false;" /></td>
			</c:if>
		</tr>
		<tr>
			<td colspan="12">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />buscandoPrestaciones">
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
				<div align="center"
					id="<portlet:namespace />liquidacion_prestaciones">
					<jsp:include page='liquidacion_prestaciones_search_result.jsp' /></div>
			</td>
		</tr>
		<tr>
			<td>
				<div align="center"
					id="<portlet:namespace />cant_prestacion_afiliado"></div>
			</td>
		</tr>


	</table>
	
	
	 <%if(liquidacion != null && liquidacion.getId_liquidacion() >0){ %>
       <table>
       <tr>
       	 <td>&nbsp;</td>
       </tr>
       <tr>
        <td> 
             <div id="<portlet:namespace />crm_auditoria">
			 	<table style="font-size: 8">
						<tr>
							<td><label><liferay-ui:message key="crm-contacto-alta-sec-usu" />:</label></td>
							<td><%=liquidacion.getAlta_usr()!=null? liquidacion.getAlta_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-alta-fec" />:</label></td>
							<td><%=liquidacion.getAlta_fecha()!=null?sdf.format(liquidacion.getAlta_fecha()):"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label>Modi.Usuario: </label></td>
							<td><%=liquidacion.getModi_usr()!=null?liquidacion.getModi_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-modi-fec" />:</label></td>
							<td><%=liquidacion.getModi_fecha()!=null?sdf.format(liquidacion.getModi_fecha()):"" %></td>
						</tr>
				</table>   
		     </div>
         </td>
         </tr>
       </table>
   <%}%>      
	
	
	

</form>

<form action="" method="post" enctype="multipart/form-data"
	id="<portlet:namespace />borrar_prest"
	name="<portlet:namespace />borrar_prest">
	<input type="hidden" name="<portlet:namespace />borrar_numero"
		id="<portlet:namespace />borrar_numero" value="" /> <input
		type="hidden" name="<portlet:namespace />borrar_orden"
		id="<portlet:namespace />borrar_orden" value="" /> <input
		type="hidden" name="<portlet:namespace />deletePrestaci" value="1" />
	<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>"
		value="<%= Constants.DELETE %>" /> <input type="hidden"
		name="<portlet:namespace />paga" id="<portlet:namespace />paga"
		value="<%= paga %>>" />
		
		
		<input type="hidden" name="<portlet:namespace />borrar_id_reclamo_prestacion"
		id="<portlet:namespace />borrar_id_reclamo_prestacion" value="" /> 
		
		<input type="hidden" name="<portlet:namespace />borrar_id_prestacion_reclamo"
		id="<portlet:namespace />borrar_id_prestacion_reclamo" value="" /> 
		
		
		
</form>

<form action="" method="post" enctype="multipart/form-data"
	id="<portlet:namespace />cambio_estado_liquidacion"
	name="<portlet:namespace />cambio_estado_liquidacion">
	<input type="hidden" name="<portlet:namespace />cambio_estado_numero"
		id="<portlet:namespace />cambio_estado_numero"
		value="<%=Validator.isNotNull(liquidacion) ? liquidacion.getId_liquidacionString() : "" %>" />
	<input type="hidden" name="<portlet:namespace />estado_futuro"
		id="<portlet:namespace />estado_futuro" value="" /> <input
		type="hidden" name="<portlet:namespace />paga"
		id="<portlet:namespace />paga" value="<%= paga %>>" /> <input
		type="hidden" name="<portlet:namespace />importe_concepto_"
		id="<portlet:namespace />importe_concepto_" value="" /> <input
		type="hidden" name="<portlet:namespace />id_concepto_"
		id="<portlet:namespace />id_concepto_" value="" />
</form>

<script>

jQuery("#<portlet:namespace />importe").blur(function(){ validaMontoOriginalReclamoImporte(); }); 
jQuery("#<portlet:namespace />cantidad").blur(function(){ validaMontoOriginalReclamoCantidad(); });
jQuery("#<portlet:namespace />importeCompro").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />sucursal_entidad").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />entidad").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />cuit_entidad").blur(function(){ validaMontoOriginalReclamo(); });

		jQuery(window).load(function () {	
			ocultarDetalle();
			
			
			var tieneObleaCorreo = '<%=  Validator.isNotNull(liquidacion) && Validator.isNotNull(liquidacion.getComprobante()) &&
                  Validator.isNotNull(liquidacion.getComprobante().getObleaCorreo())
                   ? liquidacion.getComprobante().getObleaCorreo() : "0" %>';		
	        if (tieneObleaCorreo != "0") {
		         jQuery('#<portlet:namespace />divOblea').show();
	        }else{	
		         jQuery('#<portlet:namespace />divOblea').hide();
	        }
		});


		function <portlet:namespace />hideShowDivPrestaciones () {
			jQuery('#<portlet:namespace />divDatosPrestacion').hide();
			jQuery('#<portlet:namespace />botonesTercerizada').show();
			jQuery('#<portlet:namespace />divDatosConceptos').hide();
			jQuery('#<portlet:namespace />divDatosConceptosConCargos').hide();

			
			var liquidacionTercerizada = <%=  Validator.isNotNull(liquidacion) ? liquidacion.getTercerizado(): "0" %>;			
			if (liquidacionTercerizada == "0") {
				jQuery('#<portlet:namespace />divDatosPrestacion').show();
				jQuery('#<portlet:namespace />botonesTercerizada').hide();
			}
			<c:if test="<%= Validator.isNotNull(liquidacion) && liquidacion.esTercerizado() %>">
				document.getElementById("<portlet:namespace />tercerizado_cab").checked = true;
				jQuery('#<portlet:namespace />divDatosConceptosConCargos').show();
			</c:if>
			<c:if test="<%= (liquidacionPrestaciones != null && liquidacionPrestaciones.size() > 0)
				|| (Validator.isNotNull(liquidacion) && liquidacion.getEstado() > WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO) %>">
				document.getElementById("<portlet:namespace />tercerizado_cab").disabled = true;
				jQuery('#<portlet:namespace />divDatosConceptosConCargos').hide();
			</c:if>
		}
		
		jQuery('#<portlet:namespace />tercerizado_cab').click(function(){
			ocultarDetalle();
		});
		
		
		function ocultarDetalle(){
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked == true) {
				jQuery('#<portlet:namespace />divDatosPrestacion').hide();
				jQuery('#<portlet:namespace />botonesTercerizada').show();
				jQuery('#<portlet:namespace />divDatosConceptosConCargos').show();
				jQuery('#<portlet:namespace />divDatosConceptos').hide();
				jQuery('#<portlet:namespace />div_busqueda_prestacion').hide();
				jQuery('#<portlet:namespace />divBuscarPorNroLote').hide();

			} else {
				jQuery('#<portlet:namespace />divDatosPrestacion').show();
				jQuery('#<portlet:namespace />botonesTercerizada').hide();
				jQuery('#<portlet:namespace />divDatosConceptosConCargos').hide();
				jQuery('#<portlet:namespace />divDatosConceptos').show();
				jQuery('#<portlet:namespace />div_busqueda_prestacion').show();
				jQuery('#<portlet:namespace />botonesTercerizada').hide();
				jQuery('#<portlet:namespace />divBuscarPorNroLote').show();
			}
		}
	
		
		function <portlet:namespace />hideDayFieldOfPeriodFields () {
			jQuery("#<portlet:namespace />periodoDia").hide();			
			try {
			jQuery("#<portlet:namespace />periodoPrestacionDia").hide();
			} catch (err) {}			
							
		}

		function <portlet:namespace />readOnlyEncabezado () {
			document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
			//fecha readonly
			document.getElementById("<portlet:namespace />fechaDia").disabled = true; 
			document.getElementById("<portlet:namespace />fechaMes").disabled = true;
			document.getElementById("<portlet:namespace />fechaAnio").disabled = true;
			//periodo
			document.getElementById("<portlet:namespace />periodoMesAnio").disabled = true;
			//cod prestador
			document.getElementById("<portlet:namespace />id_prestador").disabled = true;
			document.getElementById("<portlet:namespace />cuit_prestador").disabled = true;			
			document.getElementById("<portlet:namespace />nombre_prestador").disabled = true;			
			//comprobante
			document.getElementById("<portlet:namespace/>comprobante_tipo").disabled = true;
			document.getElementById("<portlet:namespace/>comprobante_letra").disabled = true;
			document.getElementById("<portlet:namespace />sucu").disabled = true;
			document.getElementById("<portlet:namespace />comprobante_nro").disabled = true;
			//importe total
			document.getElementById("<portlet:namespace />importe_total").disabled = true;
			//fecha emision
			document.getElementById("<portlet:namespace />fechaEDia").disabled = true;
			document.getElementById("<portlet:namespace />fechaEMes").disabled = true;
			document.getElementById("<portlet:namespace />fechaEAnio").disabled = true;						
			//fecha recibido
			document.getElementById("<portlet:namespace />fechaRDia").disabled = true;
			document.getElementById("<portlet:namespace />fechaRMes").disabled = true;
			document.getElementById("<portlet:namespace />fechaRAnio").disabled = true;
			//fecha vencimiento
			document.getElementById("<portlet:namespace />fechaVDia").disabled = true;
			document.getElementById("<portlet:namespace />fechaVMes").disabled = true;
			document.getElementById("<portlet:namespace />fechaVAnio").disabled = true;
			//jQuery('#<portlet:namespace />divDatosPrestacion').show();
		}
		
		<portlet:namespace />hideDayFieldOfPeriodFields ();
		<portlet:namespace />hideShowDivPrestaciones();
		<c:if test='<%= paga.equals("1") %>'>
			<portlet:namespace />readOnlyEncabezado();
		</c:if>
		
		jQuery('#<portlet:namespace />buscandoPrestaciones').hide();

		function <portlet:namespace />validarCampos() {		
			/*
			if (!validaMontoOriginalReclamo()){
				jQuery("#<portlet:namespace />importe").focus();
		    	return false;
			}
			*/
			
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked == true) {
				if (validaImporteMayorAReintegrosGlobal() == false){
					return false;	
				}
			}else{
				validaImporteMayorAReintegros();
			}
			
			
			if (trim(jQuery("#<portlet:namespace />importe_total").val()) == "") {
				jQuery("#<portlet:namespace />importe_total").val('0');
			}			
			
			if (trim(jQuery("#<portlet:namespace />importe_total").val()) == 0) {
				alert("El importe total debe ser mayor a Cero");
				jQuery('#<portlet:namespace />importe_total').focus();
				return false;
			}	
			
			var comprobante=jQuery('#<portlet:namespace />comprobante_nro').val();
			var sucu = jQuery('#<portlet:namespace />sucu').val();
			
			if (trim(sucu).length == 0){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucu').focus();
				return false;
			}
			if(trim(comprobante).length == 0){
				alert("<liferay-ui:message key='comprobante-obligatorio' />");
				jQuery('#<portlet:namespace />comprobante_nro').focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_prestador").val() == ""){
				alert("<liferay-ui:message key='profesional-obligatorio' />");
				jQuery("#<portlet:namespace />id_prestador").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_prestador").val() != "" && jQuery("#<portlet:namespace />prest_seleccionada").val()!="1"){
				alert("<liferay-ui:message key='profesional-invalido' />");
				jQuery("#<portlet:namespace />id_prestador").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />cuit_prestador").val() == ""){
				alert("<liferay-ui:message key='cuit-profesional-obligatorio' />");
				jQuery("#<portlet:namespace />cuit_prestador").focus();
				return false;
			}
			if(!validarCuil(jQuery("#<portlet:namespace />cuit_prestador").val(),"<liferay-ui:message key='cuit-profesional'/>")){
				jQuery('#<portlet:namespace />cuit_prestador').focus();
				return false;
			}
			
			var hoy = new Date();
			
			var diaE = document.getElementById("<portlet:namespace />fechaEDia").value; 
			var mesE = document.getElementById("<portlet:namespace />fechaEMes").value; 
			var anioE = document.getElementById("<portlet:namespace />fechaEAnio").value;
			
			var fechaEmision = new Date(anioE,mesE,diaE);

			if (fechaEmision>hoy){
				alert("La fecha de emisión no puede ser posterior al día de hoy");
				return false;
			}
			
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked != true){
				var cuil=jQuery('#<portlet:namespace />cuil').val();
				var inte=jQuery('#<portlet:namespace />inte').val();
				var cantidad=jQuery('#<portlet:namespace />cantidad').val();
				var importe=jQuery('#<portlet:namespace />importe').val();
				var id_prestacion=jQuery("#<portlet:namespace />id_prestacion").val();
				
				
			//si cualquiera no es vacía, mando a guardar el encabezado solamente
			<c:if test="<%= Validator.isNotNull(liquidacion) %>">
				if (cuil != '' || inte != '' || id_prestacion != '' || cantidad != '' ||  importe != '') {														
			</c:if>	
					var diaE = document.getElementById("<portlet:namespace />fechaEDia").value; 
					var mesE = document.getElementById("<portlet:namespace />fechaEMes").value; //per.substring(0,per.indexOf("_"));
					var anioE = document.getElementById("<portlet:namespace />fechaEAnio").value;//per.substring(per.indexOf("_")+1);
					
					var diaPer = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
					var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
					var anioPer = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
					
					if ((parseInt(anioPer) > parseInt(anioE))
							|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) > parseInt(mesE,10))
							|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) == parseInt(mesE,10) && parseInt(diaPer,10) > parseInt(diaE,10))){
							alert("La fecha de la prestación no puede ser mayor a la fecha de emisión.");
							return false;
					}
					if (document.getElementById("<portlet:namespace />baja_fecha").value != ""){
						var baja = document.getElementById("<portlet:namespace />baja_fecha").value;
						var diaBaja = baja.substring(0, 2);
						var mesBaja = baja.substring(baja.indexOf("/")+1,baja.indexOf("/",baja.indexOf("/")+1));				
						mesPer++;//el mes sacado del select es 0 based					
						var anioBaja = baja.substring(baja.indexOf("/",baja.indexOf("/")+1)+1);					
						if ((parseInt(anioPer) > parseInt(anioBaja))
							|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) > parseInt(mesBaja,10))
							|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) == parseInt(mesBaja,10) && parseInt(diaPer,10) > parseInt(diaBaja,10))){
							alert("La fecha de la prestación corresponde a una fecha posterior a la baja del afiliado.");
							return false;
						}
					}
					var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value;				
					if (document.getElementById("<portlet:namespace />fecha_alta_af").value != ""){
						var alta = document.getElementById("<portlet:namespace />fecha_alta_af").value;
						var diaAlta = alta.substring(0, 2);
						var mesAlta = alta.substring(alta.indexOf("/")+1,alta.indexOf("/",alta.indexOf("/")+1));				
						mesPer++;//el mes sacado del select es 0 based
						var anioAlta = alta.substring(alta.indexOf("/",alta.indexOf("/")+1)+1);
						if ((parseInt(anioPer) < parseInt(anioAlta))
							|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) < parseInt(mesAlta,10))
							|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) == parseInt(mesAlta,10) && parseInt(diaPer,10) < parseInt(diaAlta,10))){
							alert("La fecha de prestación corresponde a una fecha anterior al alta del afiliado.");
							return false;
						}
					}
					if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
						alert("El afiliado debe tener un plan vigente en la fecha de la prestación");
						return false;
					}

					if (  (parseFloat(jQuery('#<portlet:namespace />cargo_prestadora').val()) != "0" || 
							    parseFloat(jQuery('#<portlet:namespace />cargo_imesa').val()) != "0")
							&&  jQuery('#<portlet:namespace />id_tercerizadora').val() != "PRS" //Se agrego 08/09/2025 a pedido S.Linska    
							&&  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MPS" 
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MEN"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "CEU"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MIM"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MON"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MCE"
							&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "OMI") {
						alert("El  afiliado debe tener una tercerizadora MOLINEROS POR ENSALUD u OMINT para ingresarle un monto a la Tercerizadora");
						return false;
					}

					if(jQuery("#<portlet:namespace />id_prestacion").val() == ""){
						alert("<liferay-ui:message key='prestacion-obligatoria' />");
						jQuery("#<portlet:namespace />id_prestacion").focus();
						return false;
					}
					if(jQuery("#<portlet:namespace />id_prestacion").val() != "" && jQuery("#<portlet:namespace />pres_seleccionada").val()!="1"){
						alert("<liferay-ui:message key='prestacion-invalida' />");
						jQuery("#<portlet:namespace />id_seccional").focus();
						return false;
					}				
					if(trim(cantidad).length == 0){
						alert("<liferay-ui:message key='cantidad-obligatoria' />");
						jQuery('#<portlet:namespace />cantidad').focus();
						return false;
					}
					if (!isFloat(trim(cantidad))){
						alert("<liferay-ui:message key='cantidad-invalida' />");
						jQuery('#<portlet:namespace />cantidad').focus();
						return false;
					}								
					if(trim(importe).length == 0){
						alert("<liferay-ui:message key='importe-obligatorio' />");
						jQuery('#<portlet:namespace />importe').focus();
						return false;
					}
					if (!isFloat(trim(importe))){
						alert("<liferay-ui:message key='importe-invalido' />");
						jQuery('#<portlet:namespace />importe').focus();
						return false;
					}
					
					document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
					if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {				
						<portlet:namespace />validarTopesDiscapacidad();
						return false;
					}
			<c:if test="<%= Validator.isNotNull(liquidacion) %>">																					
				}
			</c:if>
			}
			document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
			return true;
		}

		function <portlet:namespace />validarTopesDiscapacidad() {
			var id_prestacion = jQuery("#<portlet:namespace />id_prestacion").val();
			var fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();
			var cantidad = jQuery('#<portlet:namespace />cantidad').val();
			var importe = jQuery('#<portlet:namespace />importe').val();
			var importe_anterior = jQuery('#<portlet:namespace />importe_anterior').val();
			var cantidad_anterior = jQuery('#<portlet:namespace />cantidad_anterior').val();
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var cuit=jQuery('#<portlet:namespace />cuit_prestador').val();
			var codPrestaci=jQuery('#<portlet:namespace />codigo').val();
			
			var periodo = jQuery('#<portlet:namespace />periodoPrestacionMesAnio').val();

			var diaPeriodoS = '01';
			var mesPeriodoS = '';
			var mesPeriodo = parseInt(periodo.substring(0, periodo.indexOf("_")))+1;
			if (mesPeriodo < 10) {
				mesPeriodoS = '0'+mesPeriodo;
			} else {
				mesPeriodoS = mesPeriodo;
			} 
			var anioPeriodoS = periodo.substring(periodo.indexOf("_")+1,periodo.length);				
				mesPeriodo--;//el mes sacado del select es 0 based	}
			var ppn = mesPeriodoS + '/' + anioPeriodoS;

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_topes_discapacidad_afiliado&cuil_titular='+cuil+
			  '&inte='+inte+'&id_prestacion='+id_prestacion+'&fecha_prestacion='+fecha_prestacion+'&cantidad='+cantidad+'&importe='+importe+'&importe_anterior='+importe_anterior+'&cantidad_anterior='+cantidad_anterior+'&cuit_entidad='+cuit+'&sucursal_entidad=000'+'&periodo='+ppn+'&codPrestaci='+codPrestaci;			
			jQuery("#<portlet:namespace />cant_prestacion_afiliado").load(url);
		}
		
		function <portlet:namespace />validarAjustar() {
			
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked != true){
				var cuil=jQuery('#<portlet:namespace />cuil').val();
				var inte=jQuery('#<portlet:namespace />inte').val();
				var cantidad=jQuery('#<portlet:namespace />cantidad').val();
				var importe=jQuery('#<portlet:namespace />importe').val();
				var id_prestacion=jQuery("#<portlet:namespace />id_prestacion").val();

				document.getElementById("<portlet:namespace />fechaEDia").disabled = false;
				document.getElementById("<portlet:namespace />fechaEMes").disabled = false;
				document.getElementById("<portlet:namespace />fechaEAnio").disabled = false;
				
				var diaE = document.getElementById("<portlet:namespace />fechaEDia").value; 
				var mesE = document.getElementById("<portlet:namespace />fechaEMes").value; //per.substring(0,per.indexOf("_"));
				var anioE = document.getElementById("<portlet:namespace />fechaEAnio").value;//per.substring(per.indexOf("_")+1);

				document.getElementById("<portlet:namespace />prestacionFechaDia").disabled = false;
				document.getElementById("<portlet:namespace />prestacionFechaMes").disabled = false;
				document.getElementById("<portlet:namespace />prestacionFechaAnio").disabled = false;
				
				var diaPer = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
				var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
				var anioPer = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);

				if ((parseInt(anioPer) > parseInt(anioE))
						|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) > parseInt(mesE,10))
						|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) == parseInt(mesE,10) && parseInt(diaPer,10) > parseInt(diaE,10))){
						alert("La fecha de la prestación no puede ser mayor a la fecha de emisión.");
						return false;
				}

				if (document.getElementById("<portlet:namespace />baja_fecha").value != ""){
					var baja = document.getElementById("<portlet:namespace />baja_fecha").value;
					var diaBaja = baja.substring(0, 2);
					var mesBaja = baja.substring(baja.indexOf("/")+1,baja.indexOf("/",baja.indexOf("/")+1));				
					mesPer++;//el mes sacado del select es 0 based					
					var anioBaja = baja.substring(baja.indexOf("/",baja.indexOf("/")+1)+1);					
					if ((parseInt(anioPer) > parseInt(anioBaja))
						|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) > parseInt(mesBaja,10))
						|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) == parseInt(mesBaja,10) && parseInt(diaPer,10) > parseInt(diaBaja,10))){
						alert("La fecha de la prestación corresponde a una fecha posterior a la baja del afiliado.");
						return false;
					}
				}
				var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value;				
				if (document.getElementById("<portlet:namespace />fecha_alta_af").value != ""){
					var alta = document.getElementById("<portlet:namespace />fecha_alta_af").value;
					var diaAlta = alta.substring(0, 2);
					var mesAlta = alta.substring(alta.indexOf("/")+1,alta.indexOf("/",alta.indexOf("/")+1));				
					mesPer++;//el mes sacado del select es 0 based					
					var anioAlta = alta.substring(alta.indexOf("/",alta.indexOf("/")+1)+1);
					if ((parseInt(anioPer) < parseInt(anioAlta))
						|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) < parseInt(mesAlta,10))
						|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) == parseInt(mesAlta,10) && parseInt(diaPer,10) < parseInt(diaAlta,10))){
						alert("La fecha de prestación corresponde a una fecha anterior al alta del afiliado.");
						return false;
					}
				}
				if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
					alert("El afiliado debe tener un plan vigente en la fecha de la prestación");
					return false;
				}
				if(jQuery("#<portlet:namespace />id_prestacion").val() == "") {
					alert("<liferay-ui:message key='prestacion-obligatoria' />");
					jQuery("#<portlet:namespace />id_prestacion").focus();
					return false;
				}
				if(jQuery("#<portlet:namespace />id_prestacion").val() != "" && jQuery("#<portlet:namespace />pres_seleccionada").val()!="1"){
					alert("<liferay-ui:message key='prestacion-invalida' />");
					jQuery("#<portlet:namespace />id_seccional").focus();
					return false;
				}				
				if(trim(cantidad).length == 0){
					alert("<liferay-ui:message key='cantidad-obligatoria' />");
					jQuery('#<portlet:namespace />cantidad').focus();
					return false;
				}
				if (!isFloat(trim(cantidad))){
					alert("<liferay-ui:message key='cantidad-invalida' />");
					jQuery('#<portlet:namespace />cantidad').focus();
					return false;
				}								
				if(trim(importe).length == 0){
					alert("<liferay-ui:message key='importe-obligatorio' />");
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				}
				if (!isFloat(trim(importe))){
					alert("<liferay-ui:message key='importe-invalido' />");
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				}
			}
			document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
			return true;
		}
		
		function <portlet:namespace />saveLiquidacionEntry() {
		   if (jQuery('#<portlet:namespace />importeoriginalnovalidado').val()!='') {
		      alert('El total ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestaci\u00f3n en el reclamo.');
			  return false; 	
		    }	
		    jQuery('#<portlet:namespace />importeoriginalnovalidado').val('');
		    if(!<portlet:namespace />validarComprobante()){
		       alert('El comprobante ingresado no coincide con el asociado originalmente a la liquidación');
			   return false;
		    }
		    
		    if (<portlet:namespace />validarCampos()) {
				<portlet:namespace />validarTopes();
			}
			return false;
		}

		//Guarda la pantalla como una liquidación, no haced caso del nombre.
		function <portlet:namespace />validarTopes() {
			<% if ( !(Validator.isNotNull(liquidacion) && liquidacion.getId_liquidacion()  != 0)  && !esView 	) { 	%>
				<portlet:namespace />habilitaControlBusquedaAfiliado(true);						
			<%}%>
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);

			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= liquidacion == null || (liquidacion != null && liquidacion.getId_liquidacion() == 0) ? Constants.ADD : Constants.UPDATE %>";
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';
			submitForm(document.<portlet:namespace />fm, url);				
			return true;
		}
		
		function <portlet:namespace />ajustarLiquidacionEntry() {
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked == false) {
				if (<portlet:namespace />validarAjustar()) {
					document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";				
					var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';
					submitForm(document.<portlet:namespace />fm, url);				
					return true;
				}
			}
			return false;
		}
		
		function borraLiquidacionPrestacion(id_liquidacion,idReclamoPrestacional,idPrestacionReclamo, orden){
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
				return false;
			}else{
				jQuery("#<portlet:namespace />borrar_numero").val(id_liquidacion);
				jQuery("#<portlet:namespace />borrar_orden").val(orden);
				
				jQuery("#<portlet:namespace />borrar_id_reclamo_prestacion").val(idReclamoPrestacional);
				jQuery("#<portlet:namespace />borrar_id_prestacion_reclamo").val(idPrestacionReclamo);
		
				
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';			
				submitForm(document.<portlet:namespace />borrar_prest, url);
				return true;
			}
			return false;
		}
		
		function <portlet:namespace />cerrarLiquidacionEntry(estado) {			
			var diaR = document.getElementById("<portlet:namespace />fechaRDia").value; 
			var mesR = document.getElementById("<portlet:namespace />fechaRMes").value; //per.substring(0,per.indexOf("_"));
			var anioR = document.getElementById("<portlet:namespace />fechaRAnio").value;//per.substring(per.indexOf("_")+1);
//            var origen = document.getElementById("<portlet:namespace />origen").value; 
			//	Validación fechas recibo vs cierre del periodo contable 
			if ((parseInt(<%=anioPer%>) > parseInt(anioR))
					|| (parseInt(<%=anioPer%>, 10) == parseInt(anioR, 10) && parseInt(<%=mesPer%>,10) > parseInt(mesR,10) + 1)
					|| (parseInt(<%=anioPer%>, 10) == parseInt(anioR, 10) && parseInt(<%=mesPer%>,10) == parseInt(mesR,10) + 1 && parseInt(<%=diaPer%>,10) > parseInt(diaR,10))){
					alert("La Fecha Recibido del comprobante no puede ser superior a la fecha de cierre del periodo contable anterior.");
					jQuery('#<portlet:namespace />fechaRMes').focus();
					return false;
			}	
			
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-close-this-liquidacion'/>")){
				return false;
			}else{
				jQuery("#<portlet:namespace />estado_futuro").val(estado);
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';
//				var params ="&origen="+origen+"&accion=cerrar";
//				url += params;
				submitForm(document.<portlet:namespace />cambio_estado_liquidacion, url);
				return true;
			}
			return false;		
		}		

		function <portlet:namespace />cerrarAjustesLiquidacionEntry() {
			if(!confirm("Está seguro de guardar los cambios")){
				return false;
			}else{
				jQuery("#<portlet:namespace />importe_concepto_").val(jQuery("#<portlet:namespace />importe_concepto").val());
				jQuery("#<portlet:namespace />id_concepto_").val(jQuery("#<portlet:namespace />id_concepto").val());
				jQuery("#<portlet:namespace />estado_futuro").val('<%= WebKeysLiquidaciones.LIQUIDACION_ESTADO_MODIFICAR_PAGA %>'); //cierre definitivo
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';
				submitForm(document.<portlet:namespace />cambio_estado_liquidacion, url);
				return true;
			}
			return false;		
		}

		var popupTratamientos;
		function <portlet:namespace />ver_tratamientos_autorizados() {
			var view = 'false';
			<c:if test="<%= viewStr != null %>">
				view = 'true';
			</c:if>
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();

			var id_prestacion = jQuery("#<portlet:namespace />id_prestacion").val();			
			var cuit=jQuery('#<portlet:namespace />cuit_prestador').val();
			
			var periodo=jQuery('#<portlet:namespace />periodoPrestacionMesAnio').val();			
			var diaPeriodoS = '01';
			var mesPeriodoS = '';
			var mesPeriodo = parseInt(periodo.substring(0, periodo.indexOf("_")))+1;
			if (mesPeriodo < 10) {
				mesPeriodoS = '0'+mesPeriodo;
			} else {
				mesPeriodoS = mesPeriodo;
			} 
			var anioPeriodoS = periodo.substring(periodo.indexOf("_")+1,periodo.length);				
				mesPeriodo--;//el mes sacado del select es 0 based	}
			var ppn = mesPeriodoS + '/' + anioPeriodoS;
			
			var codPrestaci=jQuery('#<portlet:namespace />codigo').val();
			
			if (trim(cuil).length == 0 || trim(inte).length == 0) {
				alert ("Debe seleccionar el afiliado primero");
				return false;
			}
			popupTratamientos = Liferay.Popup({title:"<liferay-ui:message key="Tratamientos autorizados" />",modal:true,width:1000});
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/ver_tratamientos_discapacidad_afiliado&cuil='+escape(cuil)+'&inte='+escape(inte)+'&view='+view+
		    '&id_prestacion='+id_prestacion+'&cuit_entidad='+cuit+'&periodo='+ppn+'&codPrestaci='+codPrestaci;
			jQuery(popupTratamientos).load(url);
		}		
		
		function editarLiquidacionPrestacion(orden, fecha_prestacion, id_prestacion, codigo, servicio, cuil, inte, cantidad,idReclamoPrestacional,idPrestacionReclamo, importe, descontar_capitas, periodo , cargo_prestadora,cargo_imesa){					
			jQuery("#<portlet:namespace />orden").val(orden);			
			var diaP = parseInt(fecha_prestacion.substring(0, 2), 10);
			var mesP = parseInt(fecha_prestacion.substring(fecha_prestacion.indexOf("/")+1,fecha_prestacion.indexOf("/",fecha_prestacion.indexOf("/")+1)), 10);				
			mesP--;//el mes sacado del select es 0 based
			var anioP = fecha_prestacion.substring(6,10);
			jQuery("#<portlet:namespace />prestacionFechaDia").val(diaP);
			jQuery("#<portlet:namespace />prestacionFechaMes").val(mesP);
			jQuery("#<portlet:namespace />prestacionFechaAnio").val(anioP);
			jQuery("#<portlet:namespace />servicio").val(servicio);
			jQuery("#<portlet:namespace />cuil").val(cuil);
			jQuery("#<portlet:namespace />inte").val(inte);
			jQuery("#<portlet:namespace />id_prestacion").val(id_prestacion);
			jQuery("#<portlet:namespace />pres_seleccionada").val("1");			
			jQuery("#<portlet:namespace />codigo").val(codigo);
			jQuery("#<portlet:namespace />cantidad").val(cantidad);			
			jQuery("#<portlet:namespace />importe").val(importe);			
			jQuery("#<portlet:namespace />importe_anterior").val(importe);			
			jQuery("#<portlet:namespace />cantidad_anterior").val(cantidad);			
			jQuery("#<portlet:namespace />descontar_capitas").val(descontar_capitas);
			sumarTodo();			
			jQuery("#<portlet:namespace />editPrestaci").val("1");
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;
			jQuery("#<portlet:namespace />fprest").val(fpn);
			
			
			
			jQuery('#<portlet:namespace />id_reclamo_prestacional').val(idReclamoPrestacional);
			jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(idPrestacionReclamo);
			
			jQuery('#<portlet:namespace />cargo_prestadora').val(cargo_prestadora);
			jQuery('#<portlet:namespace />cargo_imesa').val(cargo_imesa);

			
			
			var perlength = periodo.length;
			if (perlength > 0) {					
				var diaPeriodo = '01';
				var mesPeriodo = parseInt(periodo.substring(0, periodo.indexOf("/")+1), 10);
				var anioPeriodo = periodo.substring(periodo.indexOf("/")+1,periodo.length);				
				mesPeriodo--;//el mes sacado del select es 0 based
			} else {
				var diaPeriodo = diaP;
				var mesPeriodo = mesP;
				var anioPeriodo = anioP;									
			}
			var perio = mesPeriodo + "_" + anioPeriodo;
			jQuery("#<portlet:namespace />periodoPrestacionMesAnio").val(perio);		
			reLoadAfiliado();
		    <portlet:namespace />buscarPrestacion();
		}					
		
		function reLoadAfiliado() {
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();			
			if (cuil != "" && inte != ""){				
				<portlet:namespace />buscarAfiliados_(jQuery("#<portlet:namespace />fprest").val());		
			}
		}

		jQuery('#<portlet:namespace />prestacionFechaDia').change(function(){
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;			
			jQuery("#<portlet:namespace />fprest").val(fpn);						
			reLoadAfiliado();
		});

		jQuery('#<portlet:namespace />prestacionFechaMes').change(function(){
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;
			jQuery("#<portlet:namespace />fprest").val(fpn);
			reLoadAfiliado();
		});

		jQuery('#<portlet:namespace />prestacionFechaAnio').change(function(){	
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;
			jQuery("#<portlet:namespace />fprest").val(fpn);
			reLoadAfiliado();
		});
		
		function sumarTodo(){
			var cant = 0;
			var imp = 0;
			if (document.getElementById("<portlet:namespace />cantidad")!=null && trim(document.getElementById("<portlet:namespace />cantidad").value) != ""){
				cant = 	document.getElementById("<portlet:namespace />cantidad").value;
			}
			if (document.getElementById("<portlet:namespace />importe") != null && trim(document.getElementById("<portlet:namespace />importe").value)!= ""){
				imp = document.getElementById("<portlet:namespace />importe").value;
			}
			var x = parseFloat(cant) * parseFloat(imp);
			document.getElementById("<portlet:namespace />total").value = Math.round(x * 100)/100;					
		}
		
		sumarTodo();

		var errorJS = "<%=error != null ? error : ""%>";
		if (errorJS != ""){
			alert(errorJS);
		}

		var aFinMes = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

		var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
		var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
		var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
		mesPrest++;
		var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;			
		jQuery("#<portlet:namespace />fprest").val(fpn);

		jQuery('#<portlet:namespace />fechaRDia').change(function(){
			var diaPrest = document.getElementById("<portlet:namespace />fechaRDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />fechaRMes").value;
			var anioPrest = document.getElementById("<portlet:namespace />fechaRAnio").value;
			if (mesPrest == 11) {
				mesPrest = 0;
				anioPrest++;
			}
			else {
				mesPrest++;
			}
			if (diaPrest > aFinMes[mesPrest]) {
				diaPrest = aFinMes[mesPrest];
			}			
			document.getElementById("<portlet:namespace />fechaVDia").value = diaPrest;
			document.getElementById("<portlet:namespace />fechaVMes").value = mesPrest;
			document.getElementById("<portlet:namespace />fechaVAnio").value = anioPrest;
		});

		jQuery('#<portlet:namespace />fechaRMes').change(function(){
			var diaPrest = document.getElementById("<portlet:namespace />fechaRDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />fechaRMes").value;
			var anioPrest = document.getElementById("<portlet:namespace />fechaRAnio").value;
			if (mesPrest == 11) {
				mesPrest = 0;
				anioPrest++; 
			}
			else {
				mesPrest++;
			}
			if (diaPrest > aFinMes[mesPrest]) {
				diaPrest = aFinMes[mesPrest];
			}			
			document.getElementById("<portlet:namespace />fechaVDia").value = diaPrest;
			document.getElementById("<portlet:namespace />fechaVMes").value = mesPrest;
			document.getElementById("<portlet:namespace />fechaVAnio").value = anioPrest;					
		});

		jQuery('#<portlet:namespace />fechaRAnio').change(function(){
			var diaPrest = document.getElementById("<portlet:namespace />fechaRDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />fechaRMes").value;
			var anioPrest = document.getElementById("<portlet:namespace />fechaRAnio").value;
			if (mesPrest == 11) {
				mesPrest = 0;
				anioPrest++; 
			}
			else {
				mesPrest++;
			}
			if (diaPrest > aFinMes[mesPrest]) {
				diaPrest = aFinMes[mesPrest];
			}
			document.getElementById("<portlet:namespace />fechaVDia").value = diaPrest;
			document.getElementById("<portlet:namespace />fechaVMes").value = mesPrest;
			document.getElementById("<portlet:namespace />fechaVAnio").value = anioPrest;			
		});

		var popupdl;
		function <portlet:namespace />debitosLiquidaciones(id_liquidacion, view) {
			popupdl = Liferay.Popup({title:"<liferay-ui:message key="debitos" />",modal:true,width:1200,
				 onClose: function() {
					<portlet:namespace />saveLiquidacionEntry();
				 }});						
			var cuit_prestador = jQuery("#<portlet:namespace />cuit_prestador").val();
			var idprestador = jQuery("#<portlet:namespace />id_prestador").val();
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/debitos_liquidacion&id_liquidacion='+id_liquidacion+'&view='+view+
		    '&id_prestador='+idprestador;
			jQuery(popupdl).load(url);
		}

		function <portlet:namespace />imprimirND(){
			window.location.href ="/pdfservlet/?accion=<%="notaDebitoLiquidacion"%>&id_liquidacion=<%=liquidacion!=null && liquidacion.getId_liquidacionString() != null ? liquidacion.getId_liquidacionString() : ""%>";		
		}

		function pierdeFocoImporteLiq() {
			var idpcs = <%= WebKeysLiquidaciones.PRESTADOR_CONSOLIDAR_SALUD %> ;
			var idpcc = <%= WebKeysLiquidaciones.PRESTADOR_CONSOLIDAR_CAPITAS %>;
			var idpce = <%= WebKeysLiquidaciones.PRESTADOR_CONSOLIDAR_EMPRESAS %>;
			var idpca = <%= WebKeysLiquidaciones.PRESTADOR_CONSOLIDAR_APE %>;
			var idpcd = <%= WebKeysLiquidaciones.PRESTADOR_CONSOLIDAR_DESEMPLEADOS %>;
			var idprestador = jQuery("#<portlet:namespace />id_prestador").val();
	        //if (document.getElementById("<portlet:namespace />tercerizado_cab").checked == true && (jQuery("#<portlet:namespace />debitos_cab").val() == '0' || jQuery("#<portlet:namespace />debitos_cab").val() == '')) {
	        if ( idprestador != idpcs && idprestador != idpcc && idprestador != idpce && idprestador != idpca && idprestador != idpcd ) {
	    		jQuery("#<portlet:namespace />debitos_cab").val(jQuery("#<portlet:namespace />importe_total").val() - jQuery("#<portlet:namespace />debitado").val());
	        }//}
		}
		
		function actualizarConceptos(){
			var fechaDia  = document.getElementById("<portlet:namespace />fechaRDia");
			var fechaMes= document.getElementById("<portlet:namespace />fechaRMes");
			var fechaAnio = document.getElementById("<portlet:namespace />fechaRAnio");
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_conceptos_liquidacion_para_fecha';
			url +='&fechaDia='+fechaDia.value;
			url +='&fechaMes='+fechaMes.value;
			url +='&fechaAnio='+fechaAnio.value;
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery.ajax({
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					jQuery('#<portlet:namespace />id_concepto').find('option').remove();
					for(var i =0;i< obj.conceptos.length; i++){
						jQuery('#<portlet:namespace />id_concepto').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
					}                                                                                                                                                                                                                                                            
				}
			});
		}
					
		jQuery(document).ready(function() {
			jQuery("#recep").find("img").hide();
			jQuery('#<portlet:namespace/>fechaRMes').change(function(){
				
				actualizarConceptos();
			});
			jQuery('#<portlet:namespace/>fechaRAnio').change(function(){
				actualizarConceptos();
			});
		});

		jQuery('#<portlet:namespace />div_tratamientos_discapacidad').hide();

		var popupTratamientosD;
		function editarTratamiento(id_tratamiento) {
			popupTratamientosD = Liferay.Popup({title:"Ver Tratamiento",modal:true,position:[150,30],xy: ['center', 100],width:1120});
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/tratamiento_discapacidad&id_tratamiento='+id_tratamiento+'&view=true';
			jQuery(popupTratamientosD).load(url);
		}
		
		
        function <portlet:namespace />ver_prestaciones_reclamos() {
		    
		    var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var params = { "cuil":cuil,  "inte":inte , "reintegro":false };
			
			
			//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_prestaciones_reclamos_reintegros" /></portlet:renderURL>';
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/lista_prestaciones_reclamos_reintegros';																																				  
						
			jQuery('#<portlet:namespace />div_reclamos_prestaciones').load(url,params, function(){
												jQuery('#<portlet:namespace />buscando').hide();            															
																  });
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		    jQuery("#div_boton_cancelar_reclamos_prestaciones").hide();
		    jQuery("#<portlet:namespace />div_reclamos_prestaciones_por_lotes").hide();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_por_nro_lote").hide();
		    
			
		}
		
        function <portlet:namespace />cancelarYlimpiaCampos() {        	
        	<portlet:namespace />cancelar_prestaciones_reclamos();        	
        }
        
        function <portlet:namespace />cancelar_prestaciones_reclamos() {
			
			jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").hide();
			jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
		    jQuery('#<portlet:namespace />prestacion').val("");	
		    jQuery('#<portlet:namespace />codigo').val("");
			jQuery('#<portlet:namespace />importe').val("");
			jQuery('#<portlet:namespace />cantidad').val("");
			jQuery('#<portlet:namespace />total').val("");
			
			jQuery('#<portlet:namespace />importeoriginalreclamo').val("");
			jQuery('#<portlet:namespace />importeoriginalnovalidado').val("");
			
			jQuery('#<portlet:namespace />id_reclamo_prestacional').val("");
			jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val("");			
			// desahilita el control de busqueda de afiliados
			<portlet:namespace />habilitaControlBusquedaAfiliado(true);
			// habilita  controles de importes de la pretacion 
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);
			
			if (jQuery("#<portlet:namespace />div_reclamos_prestaciones_por_lotes").show() ){
				jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
			}
			
		}
		
		
		function <portlet:namespace />habilitaControlBusquedaAfiliado(accion){
			if (accion){
				document.getElementById("<portlet:namespace />numero_afi").disabled = "";
				document.getElementById("<portlet:namespace />cuil").disabled = "";
				document.getElementById("<portlet:namespace />inte").disabled = "";
				document.getElementById("<portlet:namespace/>tipoDoc").disabled = "";
				document.getElementById("<portlet:namespace />nroDoc").disabled = "";
				
			}else{
				document.getElementById("<portlet:namespace />numero_afi").disabled = "disabled";
				document.getElementById("<portlet:namespace />cuil").disabled = "disabled";
				document.getElementById("<portlet:namespace />inte").disabled = "disabled";
				document.getElementById("<portlet:namespace/>tipoDoc").disabled = "disabled";
				document.getElementById("<portlet:namespace />nroDoc").disabled = "disabled";
			}
		}
		
		
		function <portlet:namespace />desactivaControlesPrestacionDesdeReclamo(valor) {
	    	   if (valor) {
	    		   document.getElementById("<portlet:namespace />prestacion").disabled = "disabled";
	       		   document.getElementById("<portlet:namespace />codigo").disabled = "disabled";
	       		   //document.getElementById("<portlet:namespace />importe").disabled = "disabled";
	       		   //document.getElementById("<portlet:namespace />cantidad").disabled = "disabled";
	       		   document.getElementById("<portlet:namespace />total").disabled = "disabled";   
	    	   }
	    	   else{
	    		   document.getElementById("<portlet:namespace />prestacion").disabled = "";
	       		   document.getElementById("<portlet:namespace />codigo").disabled = "";
	       		   document.getElementById("<portlet:namespace />importe").disabled = "";
	       		   document.getElementById("<portlet:namespace />cantidad").disabled = "";
	       		   document.getElementById("<portlet:namespace />total").disabled = "";
	    	   }
	    	   
	       }
		
		
		function <portlet:namespace />oculta_prestaciones_reclamos(){
			
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
			jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
			jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
			
        }
		
		
	function <portlet:namespace />oculta_prestaciones_reclamos_por_nro_lote(){
			jQuery("#<portlet:namespace />div_reclamos_prestaciones_por_lotes").hide();
			jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_por_nro_lote").hide();		
    }
		


		function validaMontoOriginalReclamoCantidad(){
			if (!validaMontoOriginalReclamo()){		
		    	jQuery("#<portlet:namespace />importe").focus();
		    	return false;
			}
		} 
		function validaMontoOriginalReclamoImporte (){
		    if (!validaMontoOriginalReclamo()){  
		    	jQuery("#<portlet:namespace />total").focus();
			}
		} 

		function validaMontoOriginalReclamo(){
	       var valor=false;
	       if (jQuery("#<portlet:namespace />importeoriginalreclamo").val()!=''){ // prestacion de reclamo prestacional 
	    	   var importe1;
	    	   var cantidad1;	    	
	    	   var total;	    		    
	    	   importe1 =jQuery('#<portlet:namespace />importe').val() ;
	    	   cantidad1 =jQuery('#<portlet:namespace />cantidad').val() ;
	    	   valor=true;
	    	   total = cantidad1 * importe1  ; 
	    	   totalHistorico=jQuery('#<portlet:namespace />importeoriginalreclamo').val() ;
	    	   jQuery('#<portlet:namespace />importeoriginalnovalidado').val('') ;
		   	   if ( Math.round(total) >Math.round(totalHistorico)  ){
		   		   alert('El monto ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestaci\u00f3n en el reclamo.');		    		   
		   		   valor=false;
		   		   jQuery('#<portlet:namespace />importeoriginalnovalidado').val('bad') ;
		   	   }    	   
		   	  
	       }
	       return valor;
	      }
		
		function validaImporteMayorAReintegros(){
			var valor=true;
			var total = 0;
			var cargo_prestadora = 0;
			var cargo_imesa = 0;
		
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />total').val()))){
				total = parseFloat(jQuery('#<portlet:namespace />total').val());
			}
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_prestadora').val()))){
				cargo_prestadora = parseFloat(jQuery('#<portlet:namespace />cargo_prestadora').val());
			}
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_imesa').val()))){
				cargo_imesa = parseFloat(jQuery('#<portlet:namespace />cargo_imesa').val());
			}
			
			if (total < cargo_prestadora+cargo_imesa ){
				alert('El cargo prestadora no pueden superar el importe' );
				valor = false;

			}	
				
		    return valor;
		 }
		
		
		function validaImporteMayorAReintegrosGlobal(){
			var valor=true;
			var cargo_ospim_sin_detalle = 0;
			var cargo_prestadora_sin_detalle = 0;
			var cargo_omint_sin_detalle = 0;
			var cargo_prestadora_en_salud_sin_detalle = 0;
			var cargo_cemic_sin_detalle=0;
			var cargo_imesa_sin_detalle=0;
			var cargo_ces_sin_detalle=0;
			var importe = 0;
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />importe_concepto_sin_detalle').val()))){
				importe = jQuery('#<portlet:namespace />importe_concepto_sin_detalle').val();
			}
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_ospim_sin_detalle').val()))){
				cargo_ospim_sin_detalle = jQuery('#<portlet:namespace />cargo_ospim_sin_detalle').val();
			}
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_prestadora_sin_detalle').val()))){
				cargo_prestadora_sin_detalle = jQuery('#<portlet:namespace />cargo_prestadora_sin_detalle').val();
			}
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_omint_sin_detalle').val()))){
				cargo_omint_sin_detalle = jQuery('#<portlet:namespace />cargo_omint_sin_detalle').val();
			}
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_prestadora_en_salud_sin_detalle').val()))){
				cargo_prestadora_en_salud_sin_detalle = jQuery('#<portlet:namespace />cargo_prestadora_en_salud_sin_detalle').val();
			}
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_cemic_sin_detalle').val()))){
				cargo_cemic_sin_detalle = jQuery('#<portlet:namespace />cargo_cemic_sin_detalle').val();
			}
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_imesa_sin_detalle').val()))){
				cargo_imesa_sin_detalle = jQuery('#<portlet:namespace />cargo_imesa_sin_detalle').val();
			}
			
			if (IsNumeric(trim(jQuery('#<portlet:namespace />cargo_ces_sin_detalle').val()))){
				cargo_ces_sin_detalle = jQuery('#<portlet:namespace />cargo_ces_sin_detalle').val();
			}
			
			var suma = parseFloat(cargo_ospim_sin_detalle) + parseFloat(cargo_prestadora_sin_detalle) 
					   + parseFloat(cargo_omint_sin_detalle)   + parseFloat(cargo_prestadora_en_salud_sin_detalle) 
					   + parseFloat(cargo_cemic_sin_detalle) + parseFloat(cargo_imesa_sin_detalle) + parseFloat(cargo_ces_sin_detalle);
			
			if (parseFloat(suma).toFixed(2) > parseFloat(importe).toFixed(2)){
				alert('La suma de los cargos de tercerizadoras no pueden superar el importe de los conceptos o del comprobante' );
				valor = false;
			}	
			
		    return valor;
		 }

		function <portlet:namespace />buscarPorLote() {
			
			
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var nroLote_filtro=jQuery('#<portlet:namespace />nroLote_filtro').val();

			var params = { "cuil":cuil,  "inte":inte , "reintegro":false ,"nroLote_filtro":nroLote_filtro };
			
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_prestaciones_reclamos_reintegros" /></portlet:renderURL>';
																																				  
						
			jQuery('#<portlet:namespace />div_reclamos_prestaciones_por_lotes').load(url,params, function(){
												jQuery('#<portlet:namespace />buscando').hide();            															
																  });
			jQuery("#<portlet:namespace />div_reclamos_prestaciones_por_lotes").show();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
		    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		    jQuery("#div_boton_cancelar_reclamos_prestaciones").hide();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_por_nro_lote").show();
	
		 	
		  	validarLoteVigente();  		
				
			
		}
		    	
		    
		

		function validarLoteVigente(){

			var nroLote=jQuery('#<portlet:namespace/>nroLote_filtro').val();
			var nroLoteVigente =0;
					
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/propone_lote_reclamo_prestacional';		 

			jQuery.ajax({   
					url: url,
					async:false,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						nroLoteVigente = obj.lote;
					}
				}); 
								
			if(parseInt (nroLote) > parseInt(nroLoteVigente)) {
			     alert("Lote no está cerrado.");
	    	}

		}
		
		
		function soloNumeros(e) { 
			var key = window.Event ? e.which : e.keyCode 
			return ((key >= 48 && key <= 57) || (key==8)) 
		}
		
		jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_label_prestacion_reclamo").hide();
		jQuery("#<portlet:namespace />div_reclamos_prestaciones_por_lotes").hide();
	    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_por_nro_lote").hide();
	    
	    
	    
	    function <portlet:namespace />validarComprobante(){
	    	
	    	
	    	var comprobanteValido=true;
	    	if(<%=liquidacion!=null && liquidacion.getId_liquidacion() !=0%> ){
	    		//var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/validar_comprobante_liquidacion&cuit=';	
	    	   
	    		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/validar_comprobante_liquidacion&cuit=';    		
	    		   url += jQuery("#<portlet:namespace />id_prestador").val();
	    		   url +='&comprobante_sucu=';
	    		   url += jQuery("#<portlet:namespace />sucu").val();
	    		   url +='&comprobante_tipo='
	    		   url += jQuery("#<portlet:namespace />comprobante_tipo").val();
	    		   url +='&comprobante_letra='
		    	   url += jQuery("#<portlet:namespace />comprobante_letra").val();
	    		   url +='&comprobante_nro='
		    	   url += jQuery("#<portlet:namespace />comprobante_nro").val();
	    		   url +='&id_liquidacion='
		    	   url += '<%=liquidacion!=null?liquidacion.getId_liquidacion():0%>';
	           jQuery.ajax({   
	    	   url: url,
	    	   async:false,
	    	   success: function(data){
	    		var obj = jQuery.parseJSON(data);
	    		
	    		
	    		var comprobanteErroneo=(obj.comprobanteErroneo === 'true');
	 		   
	 		    if (!comprobanteErroneo){
                  comprobanteValido=true;	 		    	
	 		    }else{
                  comprobanteValido=false;
	 		    }
	    	   }				                                                                                                                                                                                                                                                            
	           });
	           return comprobanteValido;
	         
	        }else{
	        	return comprobanteValido;
	        }
	    }

	    function PadLeft(value, length) {
	        return (value.toString().length < length) ? PadLeft("0" + value, length) : 
	        value;
	    }
	    
	    function formateaNroComprobante(){
	    	var nro=jQuery("#<portlet:namespace />comprobante_nro").val();
	    	var tipo=jQuery("#<portlet:namespace />comprobante_tipo").val();
	    	if(tipo=='FCP' || tipo=='NCR' || tipo=='RCB'){
	    		nro=PadLeft(nro,8);
	    		jQuery("#<portlet:namespace />comprobante_nro").val(nro);
	    	}
	    	
	    	
	    }
	    
	    
	    var popupConfirmaComprobante;
        function <portlet:namespace />confirmaComprobante(){
        	
	    	var comprobanteValido=true;
	    	if(<%=liquidacion==null || liquidacion.getId_liquidacion() ==0%> ){
	    		
	    	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/confirmar_comprobante_liquidacion&cuit=';
	    		   url += jQuery("#<portlet:namespace />cuit_prestador").val();
	    		   url +='&nombre=';
	    		   url += encodeURI(jQuery("#<portlet:namespace />nombre_prestador").val());
	    		   url +='&comprobante_sucu=';
	    		   url += jQuery("#<portlet:namespace />sucu").val();
	    		   url +='&comprobante_tipo='
	    		   url += jQuery("#<portlet:namespace />comprobante_tipo").val();
	    		   url +='&comprobante_letra='
		    	   url += jQuery("#<portlet:namespace />comprobante_letra").val();
	    		   url +='&comprobante_nro='
		    	   url += jQuery("#<portlet:namespace />comprobante_nro").val();
	    		   url +='&importe='
			       url += jQuery("#<portlet:namespace />importe_total").val();
	    		   url +='&id_prestador='
		    	   url += jQuery("#<portlet:namespace />id_prestador").val();;
		    	
		      if (<portlet:namespace />validarCamposComprobante()) {	   
                 popupConfirmaComprobante = Liferay.Popup({title:"Confirmación de Comprobante",modal:true,width:480});
		         jQuery(popupConfirmaComprobante).load(url, function() {   }
			     );
		         
		      }else{
		    	 return false; 
		      }   
	        }else{
	        	<portlet:namespace />saveLiquidacionEntry();
	        }
	    }

        function <portlet:namespace />comprobanteConfirmado(){
        	Liferay.Popup.close(popupConfirmaComprobante);
        	<portlet:namespace />saveLiquidacionEntry();
        }
        
        function <portlet:namespace />comprobanteCancelado(){
        	Liferay.Popup.close(popupConfirmaComprobante);
        }
        
        
        function <portlet:namespace />validarCamposComprobante() {		

			if (trim(jQuery("#<portlet:namespace />importe_total").val()) == "") {
				jQuery("#<portlet:namespace />importe_total").val('0');
			}			
			
			if (trim(jQuery("#<portlet:namespace />importe_total").val()) == 0) {
				alert("El importe total debe ser mayor a Cero");
				jQuery('#<portlet:namespace />importe_total').focus();
				return false;
			}	
			
			var comprobante=jQuery('#<portlet:namespace />comprobante_nro').val();
			var sucu = jQuery('#<portlet:namespace />sucu').val();
			
			if (trim(sucu).length == 0){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucu').focus();
				return false;
			}
			if(trim(comprobante).length == 0){
				alert("<liferay-ui:message key='comprobante-obligatorio' />");
				jQuery('#<portlet:namespace />comprobante_nro').focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_prestador").val() == ""){
				alert("<liferay-ui:message key='profesional-obligatorio' />");
				jQuery("#<portlet:namespace />id_prestador").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_prestador").val() != "" && jQuery("#<portlet:namespace />prest_seleccionada").val()!="1"){
				alert("<liferay-ui:message key='profesional-invalido' />");
				jQuery("#<portlet:namespace />id_prestador").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />cuit_prestador").val() == ""){
				alert("<liferay-ui:message key='cuit-profesional-obligatorio' />");
				jQuery("#<portlet:namespace />cuit_prestador").focus();
				return false;
			}
			if(!validarCuil(jQuery("#<portlet:namespace />cuit_prestador").val(),"<liferay-ui:message key='cuit-profesional'/>")){
				jQuery('#<portlet:namespace />cuit_prestador').focus();
				return false;
			}
			
			var hoy = new Date();
			
			var diaE = document.getElementById("<portlet:namespace />fechaEDia").value; 
			var mesE = document.getElementById("<portlet:namespace />fechaEMes").value; 
			var anioE = document.getElementById("<portlet:namespace />fechaEAnio").value;
			
			var fechaEmision = new Date(anioE,mesE,diaE);

			if (fechaEmision>hoy){
				alert("La fecha de emisión no puede ser posterior al día de hoy");
				return false;
			}
			
			if (document.getElementById("<portlet:namespace />tercerizado_cab").checked != true){
				var cuil=jQuery('#<portlet:namespace />cuil').val();
				var inte=jQuery('#<portlet:namespace />inte').val();
				var cantidad=jQuery('#<portlet:namespace />cantidad').val();
				var importe=jQuery('#<portlet:namespace />importe').val();
				var id_prestacion=jQuery("#<portlet:namespace />id_prestacion").val();
				
				
			//si cualquiera no es vacía, mando a guardar el encabezado solamente
			<c:if test="<%= Validator.isNotNull(liquidacion) %>">
				if (cuil != '' || inte != '' || id_prestacion != '' || cantidad != '' ||  importe != '') {														
			</c:if>						
					var diaE = document.getElementById("<portlet:namespace />fechaEDia").value; 
					var mesE = document.getElementById("<portlet:namespace />fechaEMes").value; //per.substring(0,per.indexOf("_"));
					var anioE = document.getElementById("<portlet:namespace />fechaEAnio").value;//per.substring(per.indexOf("_")+1);
					
					var diaPer = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
					var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
					var anioPer = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
					
					if ((parseInt(anioPer) > parseInt(anioE))
							|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) > parseInt(mesE,10))
							|| (parseInt(anioPer) == parseInt(anioE) && parseInt(mesPer,10) == parseInt(mesE,10) && parseInt(diaPer,10) > parseInt(diaE,10))){
							alert("La fecha de la prestación no puede ser mayor a la fecha de emisión.");
							return false;
					}
					if (document.getElementById("<portlet:namespace />baja_fecha").value != ""){
						var baja = document.getElementById("<portlet:namespace />baja_fecha").value;
						var diaBaja = baja.substring(0, 2);
						var mesBaja = baja.substring(baja.indexOf("/")+1,baja.indexOf("/",baja.indexOf("/")+1));				
						mesPer++;//el mes sacado del select es 0 based					
						var anioBaja = baja.substring(baja.indexOf("/",baja.indexOf("/")+1)+1);					
						if ((parseInt(anioPer) > parseInt(anioBaja))
							|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) > parseInt(mesBaja,10))
							|| (parseInt(anioPer) == parseInt(anioBaja) && parseInt(mesPer,10) == parseInt(mesBaja,10) && parseInt(diaPer,10) > parseInt(diaBaja,10))){
							alert("La fecha de la prestación corresponde a una fecha posterior a la baja del afiliado.");
							return false;
						}
					}
					var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value;				
					if (document.getElementById("<portlet:namespace />fecha_alta_af").value != ""){
						var alta = document.getElementById("<portlet:namespace />fecha_alta_af").value;
						var diaAlta = alta.substring(0, 2);
						var mesAlta = alta.substring(alta.indexOf("/")+1,alta.indexOf("/",alta.indexOf("/")+1));				
						mesPer++;//el mes sacado del select es 0 based
						var anioAlta = alta.substring(alta.indexOf("/",alta.indexOf("/")+1)+1);
						if ((parseInt(anioPer) < parseInt(anioAlta))
							|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) < parseInt(mesAlta,10))
							|| (parseInt(anioPer) == parseInt(anioAlta) && parseInt(mesPer,10) == parseInt(mesAlta,10) && parseInt(diaPer,10) < parseInt(diaAlta,10))){
							alert("La fecha de prestación corresponde a una fecha anterior al alta del afiliado.");
							return false;
						}
					}
					if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
						alert("El afiliado debe tener un plan vigente en la fecha de la prestación");
						return false;
					}
					
															
					if ((parseFloat(jQuery('#<portlet:namespace />cargo_prestadora').val()) != "0" ||
							parseFloat(jQuery('#<portlet:namespace />cargo_imesa').val()) != "0") 
							&&  jQuery('#<portlet:namespace />id_tercerizadora').val() != "PRS" //Se agrego 08/09/2025 a pedido S.Linska
							&&  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MPS" 
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MEN"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "CEU"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MIM"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MON"
							&&	jQuery('#<portlet:namespace />id_tercerizadora').val() != "MCE"
							&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "OMI") {
						alert("El  afiliado debe tener una tercerizadora MOLINEROS POR ENSALUD u OMINT para ingresarle un monto a la Tercerizadora");
						return false;
					}

					if(jQuery("#<portlet:namespace />id_prestacion").val() == ""){
						alert("<liferay-ui:message key='prestacion-obligatoria' />");
						jQuery("#<portlet:namespace />id_prestacion").focus();
						return false;
					}
					if(jQuery("#<portlet:namespace />id_prestacion").val() != "" && jQuery("#<portlet:namespace />pres_seleccionada").val()!="1"){
						alert("<liferay-ui:message key='prestacion-invalida' />");
						jQuery("#<portlet:namespace />id_seccional").focus();
						return false;
					}				
					if(trim(cantidad).length == 0){
						alert("<liferay-ui:message key='cantidad-obligatoria' />");
						jQuery('#<portlet:namespace />cantidad').focus();
						return false;
					}
					if (!isFloat(trim(cantidad))){
						alert("<liferay-ui:message key='cantidad-invalida' />");
						jQuery('#<portlet:namespace />cantidad').focus();
						return false;
					}								
					if(trim(importe).length == 0){
						alert("<liferay-ui:message key='importe-obligatorio' />");
						jQuery('#<portlet:namespace />importe').focus();
						return false;
					}
					if (!isFloat(trim(importe))){
						alert("<liferay-ui:message key='importe-invalido' />");
						jQuery('#<portlet:namespace />importe').focus();
						return false;
					}
					document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
					
			<c:if test="<%= Validator.isNotNull(liquidacion) %>">																					
				}
			</c:if>
			}
			document.getElementById("<portlet:namespace />tercerizado_cab").disabled = false;
			return true;
		}

</script>

<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%
	String viewStr = (String)request.getAttribute(WebKeysLiquidaciones.VIEW_REINTEGRO);
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}

    boolean esOrtodonciauProtesis=false;	
	boolean showABMAuditorButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);
	 
	Reintegro reintegro = (Reintegro)request.getAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION);
	ReintegroPrestacion reintegroPrestacion  = (ReintegroPrestacion)request.getAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACION_EN_EDICION);	 
	Afiliado afiliado = Validator.isNotNull(reintegro) ? reintegro.getAfiliado() : null;
	Prestacion prestacion = Validator.isNotNull(reintegroPrestacion) && Validator.isNotNull(reintegroPrestacion.getPlan_prestacion()) ? reintegroPrestacion.getPlan_prestacion().getNomenclador() : null; 	
 	
 	String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", null);
 	
 	String con_reclamo_prestacional = String.valueOf(request.getAttribute("con_reclamo_prestacional")!=null?request.getAttribute("con_reclamo_prestacional"):0);
 	
 	if (tipo_reintegro == null) {
 	tipo_reintegro = (String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) != null && ((String)(request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION))).length() > 0 ? 
 			(String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) : WebKeysLiquidaciones.REINTEGRO_PRE;
 	}
 	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		
	if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
		showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);
		esOrtodonciauProtesis=true;
	}
			
	Date fechaReintegro = null;
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	fechaReintegro = Validator.isNotNull(reintegro)? reintegro.getFecha() : null;
	if (fechaReintegro == null) {
		fechaHoy.setTime(new Date());
	}
	else{
		fechaHoy.setTime(reintegro.getFecha());
	}
	
	Calendar periodo = CalendarFactoryUtil.getCalendar();
	Date periodoReintegro = null;
	periodoReintegro = Validator.isNotNull(reintegro)? reintegro.getPeriodo() : null;
	//cambio periodo desde encabezado hacia detalle
	periodoReintegro = null;
			
	if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
		periodo.setTime(new Date());
	}
	else {
		if (periodoReintegro == null) {
			periodo.setTime(new Date());
		}
		else{
			periodo.setTime(reintegro.getPeriodo());
		}
	}
	
	Date prestacionFechaRein = null;
	Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
	prestacionFechaRein = Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getFecha_prestacion() : null; 
	if (prestacionFechaRein == null) {
		prestacionFecha.setTime(new Date());
	}
	else{
		prestacionFecha.setTime(reintegroPrestacion.getFecha_prestacion());
	}

	Date prestacionComproFechaRein = null;
	Calendar prestacionComproFecha = CalendarFactoryUtil.getCalendar();
	//prestacionComproFechaRein = Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getFechaCompro_prestacion() : null; 
	if (prestacionComproFechaRein == null) {
		prestacionComproFecha.setTime(new Date());
	}
	else{
		//prestacionFechaCompro.setTime(reintegroPrestacion.getFechaCompro_prestacion());
	}
	
	String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);
	String error =(String) request.getAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT);
	
	List<Reintegro> reintegrosList = new ArrayList<Reintegro>();
	reintegrosList= (ArrayList<Reintegro>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
	
	int reintegrosListSize = reintegro != null && reintegro.getReintegroPrestacion() != null ? reintegro.getReintegroPrestacion().size() : 0;
	
	int lastIndexList = 0;
	int indexInList = 0;
	if (Validator.isNotNull(reintegro) && Validator.isNotNull(reintegrosList) && reintegrosList.size() > 0) {
		lastIndexList = reintegrosList.size();
		indexInList = ReintegroServiceUtil.getIndexOfReintegroList(reintegro, reintegrosList);
	}
	ArrayList <DetalleCuota> detalleCuotas = reintegro != null ? (ArrayList<DetalleCuota>)reintegro.getDetalleCuota() : null;
	
	boolean esOrtopediaOrtodoncia = false;
	if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
		esOrtopediaOrtodoncia = true; 
	}

%>	    
        
<liferay-ui:error exception="<%=PrestacionYaHechaAAfiliadoExcepcion.class%>"
	message="the-prestacion-en-garantia" />

<%@page import="com.liferay.portal.kernel.util.Validator"%>

<%@page import="javax.portlet.PortletSession"%>
<%@page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia"%>

<form action="" method="post" name="<portlet:namespace />fm">

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<input id="<portlet:namespace />esExcepcion" name="<portlet:namespace />esExcepcion" type="hidden" value=false />

<fieldset class="block-labels">
	<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
		<legend><liferay-ui:message key="encabezado-reintegro" /> </legend>
	</c:if> 
	<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
		<legend>Encabezado tratamiento</legend>
	</c:if> 
<input type="hidden" id="<portlet:namespace />tipo_reintegro"
	name="<portlet:namespace />tipo_reintegro" value="<%=tipo_reintegro%>" />
	
	<input   type="hidden" id="<portlet:namespace />con_reclamo_prestacional"
	name="<portlet:namespace />con_reclamo_prestacional" value="<%=con_reclamo_prestacional%>" />


    <input  type="hidden"    id="<portlet:namespace />id_reclamo_prestacional"
	name="<portlet:namespace />id_reclamo_prestacional" value="" />
    
    <input   type="hidden"    id="<portlet:namespace />id_prestacion_reclamo_prestacional"
	name="<portlet:namespace />id_prestacion_reclamo_prestacional" value="" />


<input type="hidden" id="<portlet:namespace />botonprestacionesreclamo"
	name="<portlet:namespace />botonprestacionesreclamo" value="" />
		
<input type="hidden" id="<portlet:namespace />fprest"
	name="<portlet:namespace />fprest" value="<%=prestacionFechaString%>" />
<input type="hidden" id="<portlet:namespace />motivoAltaDiscapacidad" name="<portlet:namespace />motivoAltaDiscapacidad" value="" />
<input type="hidden" id="<portlet:namespace />importe_anterior" name="<portlet:namespace />importe_anterior" value="0.0" />
<input type="hidden" id="<portlet:namespace />cantidad_anterior" name="<portlet:namespace />cantidad_anterior" value="0.0" />
<input type="hidden" id="<portlet:namespace />importeoriginalreclamo" name="<portlet:namespace />importeoriginalreclamo" value="" />
<input type="hidden" id="<portlet:namespace />importeoriginalnovalidado" name="<portlet:namespace />importeoriginalnovalidado" value="" />
<input type="hidden" id="<portlet:namespace />cuitvalidoprestador" name="<portlet:namespace />cuitvalidoprestador" value="" />
<input type="hidden" id="<portlet:namespace />editProtesis" name="<portlet:namespace />editProtesis" value="0" />
<input type="hidden" id="<portlet:namespace />cbu" name="<portlet:namespace />cbu" value="" />
<input type="hidden" id="<portlet:namespace />cuil_cuenta" name="<portlet:namespace />cuil_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />email_cuenta" name="<portlet:namespace />email_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />apellido_cuenta" name="<portlet:namespace />apellido_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />nombre_cuenta" name="<portlet:namespace />nombre_cuenta" value="" />

<%
    if ( !esOrtodonciauProtesis){ %>	    	    
        <input type="hidden" id="<portlet:namespace />marca_rein_liq" name="<portlet:namespace />marca_rein_liq" value="3" />
<%  } else { %>
	    	 
<%      if (!esOrtopediaOrtodoncia) { %>
            <input type="hidden" id="<portlet:namespace />marca_rein_liq" name="<portlet:namespace />marca_rein_liq" value="4" />
<%      } else { %>
            <input type="hidden" id="<portlet:namespace />marca_rein_liq" name="<portlet:namespace />marca_rein_liq" value="5" />
<%      } %>
<%  } %>

<table class="lfr-table" width="100%">
	<tr>
		<td colspan="2"><label><liferay-ui:message
			key="seccional-reintegro" />:</label></td>
		<td rowspan="3" style="vertical-align: top"><liferay-util:include
			page='/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp'>
			<liferay-util:param name="id_seccional_r"
				value='<%= reintegro != null && reintegro.getSeccional() != null ? String.valueOf(reintegro.getSeccional().getId()) : ""%>' />
			<liferay-util:param name="seccional_r"
				value='<%= reintegro != null && reintegro.getSeccional() != null ? reintegro.getSeccional().getDescripcion() : ""%>' />
		</liferay-util:include></td>
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
			<td colspan="1"><label>Estado:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>estado"
				id="<portlet:namespace/>estado"
				<% if (esView || Validator.isNotNull(reintegro)) { %>
				disabled="disabled" <%} %>>
				<c:if test="<%= !Validator.isNotNull(reintegro) %>">
					<option
						value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>">Autorizado</option>
					<option
						value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>">Pendiente</option>
				</c:if>
				<c:if test="<%= Validator.isNotNull(reintegro) %>">
					<option
						value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>"
						<%=reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO ? "selected" : ""  %>>Autorizado</option>
					<option
						value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>"
						<%=reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE ? "selected" : ""  %>>Pendiente</option>
					<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO%>"
						<%=reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO ? "selected" : ""  %>>Auditado</option>
					<option
						value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO%>"
						<%=reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO ? "selected" : ""  %>>Rechazado</option>
				</c:if>
			</select></td>
		</c:if>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>

	<tr>
		<td><label><liferay-ui:message key="date" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="fechaDia"
			dayValue="<%= fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMes"
			monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
			yearParam="fechaAnio" yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
			disabled="<%= esView %>" /></td>
		<td>&nbsp;</td>
		<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
			<td><label><liferay-ui:message key="numero" />:</label></td>
			<td><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="8" maxlength="8"
				type="text"
				value="<%=Validator.isNotNull(reintegro) ? reintegro.getId_reintegro_userString() : "" %>"
				readonly='readonly' />&nbsp;&nbsp;&nbsp;							
				<c:if test="<%= Validator.isNotNull(reintegro) && reintegro.getIdOP() != 0 %>">		
					<label>OP: <%=reintegro.getIdOP() + " / " + reintegro.getFechaOP().toString()%></label>
				</c:if>
			</td>			
		</c:if>
		<td><input id="<portlet:namespace />id_reintegro"
			name="<portlet:namespace />id_reintegro" size="8" maxlength="8"
			type="hidden"
			value="<%=Validator.isNotNull(reintegro) ? reintegro.getId_reintegro() : "" %>"
			readonly='readonly' /></td>
	</tr>
	<% if (Validator.isNotNull(reintegro) && reintegro.getId_reintegro() != 0 ){%>
	<input type="hidden" name="<portlet:namespace />periodoHidden"
		value="<%=periodo.get(Calendar.MONTH) + "/"  + periodo.get(Calendar.YEAR)%>" />
	<%} %>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	<tr>
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
			<td><label><liferay-ui:message
				key="observaciones-diagnostico" />:</label></td>
		</c:if>
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
			<td><label><liferay-ui:message key="observaciones" />:</label></td>
		</c:if>
		<td colspan="3"><textarea rows="2" cols="70"
			id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <% if (esView) { %>
			<%="readonly='readonly'" %> <%}%>><%= reintegro != null && reintegro.getObservaciones() != null? reintegro.getObservaciones() : "" %></textarea>
		</td>
		<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
			<td colspan="1"><label><liferay-ui:message key="periodo" />:</label></td>
			<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
				dayNullable="<%= true %>" dayValue=""
				monthAndYearParam="periodoMesAnio"
				monthValue="<%= periodo.get(Calendar.MONTH) %>"
				monthAndYearNullable="<%= false %>"
				yearValue="<%= periodo.get(Calendar.YEAR) %>"
				yearRangeStart="<%= periodo.get(Calendar.YEAR) - 100 %>"
				yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 100 %>"
				firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" /></td>
		</c:if>
	</tr>
	<tr>
		<td colspan="9">
		<fieldset class="block-labels"><legend> <liferay-ui:message
			key="datos-afiliado" /></legend>
		<div id="loadAfiliado"><liferay-util:include
			page='/html/portlet/liquidaciones/busqueda_afiliado_filtro_prevencion.jsp'>
			<liferay-util:param
				value="<%= (Validator.isNotNull(reintegro) && reintegro.getId_reintegro() != 0) || esView ? String.valueOf(false) : String.valueOf(true)%>"
				name="edit_mode" />
			<liferay-util:param name="cuil"
				value='<%= reintegro != null && reintegro.getAfiliado() != null ?  reintegro.getAfiliado().getCuil_titular() : ""%>' />
			<liferay-util:param name="inte"
				value='<%= reintegro != null && reintegro.getAfiliado() != null ? String.valueOf(reintegro.getAfiliado().getInte()) : ""%>' />
			<liferay-util:param name="pag_reintegro_reclamo" value='1' />			
			<liferay-util:param name="pag_reintegro" value='1' /></div>
		</liferay-util:include></fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
</table>
</fieldset>

<%-- <liferay-util:param name="pag_reintegro_reclamo" value='<%= esOrtodonciauProtesis  ? "0" : "1"%>' /> --%>


<%-- if ( !esOrtodonciauProtesis)  { --%>

<div id="<portlet:namespace />div_boton_reclamos_prestaciones">


<input type="button" value="Ver Prestaciones del Reclamo Prestacional"
			onClick="<portlet:namespace />ver_prestaciones_reclamos();return false;" />
			
</div>
<div id="<portlet:namespace />div_boton_cancelar_reclamos_prestaciones">			
		<input type="button" value="Cancelar Ingreso de Prestacion del Reclamo" id="<portlet:namespace />botoncancelapresreclamo"
			onClick="<portlet:namespace />cancelar_prestaciones_reclamos();return false;" />	
</div>	
<div id="<portlet:namespace />div_label_prestacion_reclamo">
<span  style="font-size:125%"  "> <b>Editando Prestacion de Reclamo de Afiliado</b></b></span>
</div>


<div align="center" id="<portlet:namespace />div_boton_oculta_reclamos_prestaciones">
		<input type="button" value="Oculta Prestaciones del Reclamo Prestacional" onClick="<portlet:namespace />oculta_prestaciones_reclamos();" />
		<br><br>
		<span  style="font-size:155%"  ">Listado De Prestaciones de Reclamos del Afiliado</b></span>
</div>	

<div id="<portlet:namespace />div_reclamos_prestaciones">

<fieldset class="block-labels"><legend><liferay-ui:message key="Prestaciones de los Reclamos del Afiliado" /></legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
    <liferay-util:include page="/html/portlet/liquidaciones/reintegros/reintegro_prestaciones_reclamos_search_result.jsp">	</liferay-util:include>
	</tr>
<tr>
<td>
</td>
</tr>
</table>
</fieldset>
</div>
<%-- } --%>
		
	
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-prestacion" /></legend>
<table class="lfr-table" width="100%">
	<tr>
		<td><label><liferay-ui:message key="date" />:</label></td>
		<td><liferay-ui:input-date dayParam="prestacionFechaDia"
			dayValue="<%= prestacionFecha.get(Calendar.DATE)%>"
			monthParam="prestacionFechaMes"
			monthValue="<%= prestacionFecha.get(Calendar.MONTH) %>"
			yearParam="prestacionFechaAnio"
			yearValue="<%= prestacionFecha.get(Calendar.YEAR) %>"
			yearRangeStart="<%= prestacionFecha.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= prestacionFecha.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= prestacionFecha.getFirstDayOfWeek() - 1 %>"
			disabled="<%=esView %>" /></td>
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
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
				disabled="<%= false %>" /></td>
		</c:if>
		<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
			<td colspan="2">&nbsp;<input type="hidden"
				id="<portlet:namespace />periodo"
				name="<portlet:namespace />periodo"
				value="<%=periodo.get(Calendar.MONTH) + "/"  + periodo.get(Calendar.YEAR)%>" /></td>
		</c:if>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="prestacion" />:</label></td>
		<td colspan="3"><liferay-util:include
			page="/html/portlet/utils/prestaciones/busqueda_prestacion.jsp">
			<liferay-util:param name="search_url"
				value="/liquidaciones/buscar_prestacion" />
			<liferay-util:param name="id_prestacion"
				value='<%=reintegroPrestacion!=null ? String.valueOf(reintegroPrestacion.getId_prestacion()) : "" %>' />
			<liferay-util:param name="codigo" value='' />
			<liferay-util:param name="prestacion" value='' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( !esView )%>' />
				
				
			<liferay-util:param name="cuil"
				value='<%= reintegro != null && reintegro.getAfiliado() != null ?  reintegro.getAfiliado().getCuil_titular() : ""%>' />
				
				
		</liferay-util:include></td>
		<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
			<td colspan="1"><label>Pieza:</label></td>
			<td><input id="<portlet:namespace />pieza"
				name="<portlet:namespace />pieza" size="5" maxlength="2" type="text"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? ((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getPieza() : "" %>"
				onchange="sumarTodo()" <% if (esView) { %>
				<%="readonly='readonly'" %> <%} else {%>
				onkeydown="bloquearHastaPrestacion(event); allowOnlyDigits(event)"
				<%} %> /></td>
			<td colspan="1"><select name="<portlet:namespace/>cara" id="<portlet:namespace/>cara"
				<% if (esView) { %> disabled="disabled" <%} %>>
				<option value=""></option>
				<option value="SUP">SUP</option>
				<option value="INF">INF</option>
			</select></td>
		</c:if>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
		<tr>
		    
			<td colspan="1"><label><liferay-ui:message key="cant" />:</label></td>
			<td colspan="8"><input id="<portlet:namespace />cantidad"
				name="<portlet:namespace />cantidad" size="5" maxlength="10"
				type="text"
				value="<%= Validator.isNotNull(reintegroPrestacion) ?  (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) ?  ((ReintegroPrestacionOdoProtesis) reintegroPrestacion).getCantidad() : ((ReintegroPrestacionNormal)reintegroPrestacion).getCantidad()) : (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) ? "1" : "") %>"
				onchange="validaMontoOriginalReclamoCantidad();sumarTodo();"
				<% if (esView) { %>
				<%="readonly='readonly'" %> <%} else {%>
				onkeydown="bloquearHastaPrestacion(event); allowOnlyDigits(event);"
				<%} %> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
				
				<label><liferay-ui:message
				key="imp" />:</label> &nbsp;<input id="<portlet:namespace />importe"
				name="<portlet:namespace />importe" size="12" maxlength="12"
				type="text"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImporte() : "" %>"
				onchange="validaMontoOriginalReclamoImporte();sumarTodo();" 
				<% if ((esView) || (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS))) { %>
				<%="readonly='readonly'" %> <%} else {%>
				onkeydown="bloquearHastaPrestacion(event); allowOnlyDigitsAndDecimals(event) ;  limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event)"
				<%} %> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label><liferay-ui:message
				key="total" />:</label> &nbsp;<input id="<portlet:namespace />total"
				name="<portlet:namespace />total" size="12" maxlength="20"
				type="text"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImporteTotal() : "" %>"
				readonly="readonly" />
				<%-- 
				<c:if
				test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				--%>
			</td>
		</tr>
		<tr><td>&nbsp;&nbsp;</td></tr>
		<tr>
		    <table><tr>
		      <td>	
			    <label><liferay-ui:message key="cargo-ospim" />:</label>
				&nbsp;<input id="<portlet:namespace />cargo_ospim"
				name="<portlet:namespace />cargo_ospim" size="12" maxlength="20"
				type="text" readonly="readonly"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImporteOspim() : "0" %>"/>
				
				&nbsp;  &nbsp; &nbsp; &nbsp;<label><liferay-ui:message key="cargo-prestadora" />:</label>
				&nbsp;<input id="<portlet:namespace />cargo_prestadora"
				name="<portlet:namespace />cargo_prestadora" size="12" maxlength="20"
				type="text" readonly="readonly"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImportePrestadora() : "0" %>"/> 
				
			    <label>Cargo Monotributo:</label>
				&nbsp;<input id="<portlet:namespace />cargo_imesa"
				name="<portlet:namespace />cargo_imesa" size="12" maxlength="20"
				type="text" readonly="readonly"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImporteImesa() : "0" %>"/> 
				
				
				&nbsp;  &nbsp; &nbsp; &nbsp;<label><liferay-ui:message key="Tope Plan" />:</label>
				&nbsp;<input id="<portlet:namespace />tope_importe_plan"
				name="<portlet:namespace />tope_importe_plan" size="12" maxlength="20"
				type="text" readonly="readonly"
				value=""/>
			  </td>
		    </tr></table>		
		</tr>
		<tr><td>&nbsp;&nbsp;</td></tr>
	</c:if>
	<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
		<tr>
			<td colspan="1">Importe total</td>
			<td colspan="1"><input id="<portlet:namespace />importe"
				name="<portlet:namespace />importe" size="12" maxlength="12"
				type="text"
				onchange="validaMontoOriginalReclamoImporte();"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getImporte() : "" %>"
				<% if (esView) { %> <%="readonly='readonly'" %> <%} else {%>
				onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event)"
				<%} %> /></td>
			<td>Presupuesto profesional:</td>
			<td><input id="<portlet:namespace />presupuesto"
				name="<portlet:namespace />presupuesto" size="12" maxlength="20"
				type="text"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? ((ReintegroPrestacion)reintegroPrestacion).getHonorarios() : "" %>"
				<% if (esView) { %> <%="readonly='readonly'" %> <%} %> /></td>
			<td><label><liferay-ui:message
				key="descontar-en-capitas" />:</label></td>
			<td><select name="<portlet:namespace/>descontar_capitas"
				id="<portlet:namespace/>descontar_capitas" <% if (esView) { %>
				disabled="disabled" <%} %>>
				<option value="0"
					<%=Validator.isNotNull(reintegroPrestacion) && Validator.isNotNull(reintegroPrestacion.getTercerizado()) && reintegroPrestacion.getTercerizado().equals("0") ? "selected" : ""  %>>No</option>
				<option value="1"
					<%=Validator.isNotNull(reintegroPrestacion) && Validator.isNotNull(reintegroPrestacion.getTercerizado()) && !reintegroPrestacion.getTercerizado().equals("1") ? "selected" : ""  %>>Si</option>
			</select></td>

		</tr>
	</c:if>
	</table>
	
	<table>
	<tr>
		<td><label><liferay-ui:message key="prestador" />:</label></td>
		<td colspan="8">
		<table>
		    <tr>
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
					<!--MOSTRAR O COMPONENTE y CAMPOS SUBSIGUIENTES con los viejos datos-->
					<td colspan="5"><liferay-util:include
						page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						<liferay-util:param name="esEditable" value='<%= String.valueOf( !esView ) %>' />
						<liferay-util:param name="cuit" value='' />
						<liferay-util:param name="sucu" value='' />
						<liferay-util:param name="razon" value='' />
						<liferay-util:param name="id_seccional" value='0' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_razon'/>				
					</liferay-util:include></td>
					<%-- <td colspan="3">
					<c:if test="<%= Validator.isNotNull(reintegro) %>">
						<liferay-ui:message key="cuit" />&nbsp;
						<input id="<portlet:namespace />cuit_prestador"
							name="<portlet:namespace />cuit_prestador" maxlength="11"
							size="13" type="text"
							value="<%=reintegroPrestacion!= null ? reintegroPrestacion.getCuit()  : ""%>"
							readonly='readonly' />&nbsp;
						<liferay-ui:message key="razon-social" />&nbsp;
						<input id="<portlet:namespace />nombre_prestador"
							name="<portlet:namespace />nombre_prestador" size="20"
							type="text"
							value="<%=reintegroPrestacion!= null ? reintegroPrestacion.getDescripcion() : "" %>"
							readonly='readonly' />
					</c:if></td> --%>
				</c:if>
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
					<td>
					<fieldset class="block-labels"><liferay-util:include
						page="/html/portlet/utils/prestadores/busqueda_prestador_externo.jsp">
						<liferay-util:param name="search_url"
							value="/liquidaciones/buscar_prestador_externo" />
						<liferay-util:param name="mat_tipo" value='' />
						<liferay-util:param name="mat_numero" value='' />
						<liferay-util:param name="prest_cuit" value='' />
						<liferay-util:param name="id_prestador" value='' />
						<liferay-util:param name="nombre_prestador" value='' />
						
						<liferay-util:param name="esEditable"
							value='<%=String.valueOf( !esView )%>' />
					</liferay-util:include></fieldset>
					</td>
				</c:if>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>




	<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">

		<tr>
			<td colspan="1">Nro. cuotas:</td>
			<td><select name="<portlet:namespace/>nro_cuotas"
				id="<portlet:namespace/>nro_cutoas" <% if (esView) { %>
				disabled="disabled" <%} %>>
				<option value="3"
					<%=reintegro != null && (reintegro.getReintegroPrestacion() != null && reintegro.getReintegroPrestacion().size() > 0)  && reintegro.getReintegroPrestacion().get(0) != null && ((ReintegroPrestacionOdoOrtopediaOrtodoncia)(reintegro.getReintegroPrestacion().get(0))).getNro_cuotas() == 3 ? "selected" : ""  %>>3</option>
				<option value="1"
					<%=reintegro != null && (reintegro.getReintegroPrestacion() != null && reintegro.getReintegroPrestacion().size() > 0) && reintegro.getReintegroPrestacion().get(0) != null && ((ReintegroPrestacionOdoOrtopediaOrtodoncia)(reintegro.getReintegroPrestacion().get(0))).getNro_cuotas() ==  1 ? "selected" : ""  %>>1</option>
			</select></td>

			<% if (Validator.isNotNull(reintegro) && (reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO)) {			
			DetalleCuota cuota1 = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, 1);
		%>
			<td><input type="button"
				value='Cuota 1<%=cuota1.isPaga() ? "(Pagada)" : ""%>' id="cuotaUno"
				onClick="<portlet:namespace />abmCuota(1, false);return false;" />&nbsp;</td>
			<%}
	if (Validator.isNotNull(reintegro) && (reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO)
			&& (Validator.isNotNull(reintegro.getReintegroPrestacion()) && reintegro.getReintegroPrestacion().get(0) != null && ((ReintegroPrestacionOdoOrtopediaOrtodoncia)(reintegro.getReintegroPrestacion().get(0))).getNro_cuotas() ==  3))		
	{ 
		DetalleCuota cuota2 = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, 2);
	%>
			<td><input type="button"
				value='Cuota 2<%=cuota2.isPaga() ? "(Pagada)" : "" %>' id="cuotadOS"
				onClick="<portlet:namespace />abmCuota(2, false);return false;" />&nbsp;</td>
			<%}
	if (Validator.isNotNull(reintegro) && (reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO)
			&& (Validator.isNotNull(reintegro.getReintegroPrestacion()) && reintegro.getReintegroPrestacion().get(0) != null && ((ReintegroPrestacionOdoOrtopediaOrtodoncia)(reintegro.getReintegroPrestacion().get(0))).getNro_cuotas() ==  3))
	{
		DetalleCuota cuota3 = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, 3);
	%>
			<td><input type="button"
				value='Cuota 3<%=cuota3.isPaga() ? "(Pagada)" : "" %>' id="cuotaUno"
				onClick="<portlet:namespace />abmCuota(3, false);return false;" />&nbsp;</td>
			<%} %>

		</tr>
		<tr>
			<td colspan="9">&nbsp;</td>
		</tr>

	</c:if>

	<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
		<tr>
			<td><label><liferay-ui:message key="comprobante" />:</label></td>
			<td><select name="<portlet:namespace/>comprobante_tipo"
						id="<portlet:namespace/>comprobante_tipo"
				<% if (esView) { %> disabled="disabled" <%} %>>
				<option value="FCP">FCP</option>
				<option value="RCB">RCB</option>
				<option value="OTR">OTRO</option>
			</select> &nbsp;
			<select name="<portlet:namespace />comprobante_letra"
					id="<portlet:namespace />comprobante_letra"
					<% if (esView) { %> disabled="disabled" <%} %>>
						<option value=""></option>
						<option value="A">A</option>
						<option value="B">B</option>
						<option value="C">C</option>	
				</select>&nbsp;
												
			    <input id="<portlet:namespace />comprobante_suc"
				name="<portlet:namespace />comprobante_suc" size="8" maxlength="5"
				type="text" 
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getCompro_a_debitar_sucursal() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> />
			
			
			    <input id="<portlet:namespace />comprobante_nro"
				name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
				type="text" 
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getCompro_a_debitar_numero() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> />
				</td>
			<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				<td><input type="hidden" value="0"
					name="<portlet:namespace/>descontar_capitas"
					id="<portlet:namespace/>descontar_capitas" /></td>
			</c:if>
			<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
				<td><label><liferay-ui:message key="descontar-en-capitas" />:</label></td>
				<td><select name="<portlet:namespace/>descontar_capitas"
					id="<portlet:namespace/>descontar_capitas" <% if (esView) { %>
					disabled="disabled" <%} %>>
					<option value="0"
						<%=Validator.isNotNull(reintegroPrestacion) && Validator.isNotNull(reintegroPrestacion.getTercerizado()) && reintegroPrestacion.getTercerizado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1"
						<%=Validator.isNotNull(reintegroPrestacion) && Validator.isNotNull(reintegroPrestacion.getTercerizado()) && !reintegroPrestacion.getTercerizado().equals("1") ? "selected" : ""  %>>Si</option>
				</select></td>
			</c:if>

			<input type="hidden"
				name="<portlet:namespace />id_prestacion_anterior"
				id="<portlet:namespace />id_prestacion_anterior" value="" />
			<input type="hidden"
				name="<portlet:namespace />prestacion_alta_fecha"
				id="<portlet:namespace />prestacion_alta_fecha" value="" />
			<input type="hidden" name="<portlet:namespace />codigo_anterior"
				id="<portlet:namespace />codigo_anterior" value="" />
			<input type="hidden" name="<portlet:namespace />prestacion_id_plan"
				id="<portlet:namespace />prestacion_id_plan" value="" />
			<input type="hidden" name="<portlet:namespace />editPrestaci"
				id="<portlet:namespace />editPrestaci" value="" />

			<td colspan="5">&nbsp;</td>
		</tr>
	</c:if>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	
	
	
	
	
	<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
	
	
		<tr>
		<td><label>Fecha Comprobante:</label></td>
		<td><liferay-ui:input-date dayParam="prestacionComproFechaDia"
			dayValue="<%= prestacionComproFecha.get(Calendar.DATE)%>"
			monthParam="prestacionComproFechaMes"
			monthValue="<%= prestacionComproFecha.get(Calendar.MONTH) %>"
			yearParam="prestacionComproFechaAnio"
			yearValue="<%= prestacionComproFecha.get(Calendar.YEAR) %>"
			yearRangeStart="<%= prestacionComproFecha.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= prestacionComproFecha.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= prestacionComproFecha.getFirstDayOfWeek() - 1 %>"
			disabled="<%=esView %>" />
		</td>
		<td>
		<label>Importe Comprobante:</label>&nbsp;&nbsp;&nbsp;<input id="<portlet:namespace />importeCompro"
				name="<portlet:namespace />importeCompro" size="12" maxlength="12"
				type="text" 
				onchange="validaMontoOriginalReclamoImporte();"
				value="<%= Validator.isNotNull(reintegroPrestacion) ? reintegroPrestacion.getFecha_comprobanteAsString() :  "" %>" 
				<% if (esView) { %>
				<%="readonly='readonly'" %> <%} else {%>
				onkeydown="allowOnlyDigitsAndDecimals(event) ;  limitDecimals(2,document.getElementById('<portlet:namespace />importeCompro'),event)"
				<%} %> />
		</td>
		</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
		
				
	</c:if>
	
	<tr>
		<% if (!esView && showABMButtons && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) { %>
		<td><input type="submit"
			value="<liferay-ui:message key="save" />" id="saveReintegro"
			onClick="<portlet:namespace />saveReintegroEntry();return false;" />&nbsp;
		</td>
		<%}
	if (!esView && showABMButtons && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) && (!Validator.isNotNull(reintegro) || 
			(Validator.isNotNull(reintegro) && (reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO || reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE))))) { %>
		<td><input type="submit"
			value="<liferay-ui:message key="save" />"
			onClick="<portlet:namespace />saveReintegroEntry();return false;" />&nbsp;
		</td>
		<%}
	if (!esView && showABMButtons && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) && 
			((!Validator.isNotNull(reintegro) || 
			((Validator.isNotNull(reintegro) && (reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO || reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE))))))) {

		
		if (reintegro == null) {
			
		%>

		<td><input type="submit"
			value="<liferay-ui:message key="save" />"
			onClick="<portlet:namespace />saveReintegroEntry();return false;" />&nbsp;
		</td>
		<%					
		}else if (reintegro.getDetalleCuota() != null) {			
			DetalleCuota cuota1 = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, 1);
					
			if (detalleCuotas.size() == 1 || ((detalleCuotas.size() == 3) && 
					(cuota1.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO || 
					 cuota1.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE))) {
			%>

		<td><input type="submit"
			value="<liferay-ui:message key="save" />"
			onClick="<portlet:namespace />saveReintegroEntry();return false;" />&nbsp;
		</td>
		<%
			}
		}
	}

	if (!esView && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) && showABMAuditorButtons
			&& Validator.isNotNull(reintegro) && reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO) { %>
		<td><input type="button" value="Auditar"
			onClick="<portlet:namespace />auditarReintegroEntry();return false;" />&nbsp;
		</td>
		<td><input type="button" value="Rechazar"
			onClick="<portlet:namespace />rechazarReintegroEntry();return false;" />&nbsp;
		</td>
		<%}
	  if (!esView && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) 
			 && showABMAuditorButtons && Validator.isNotNull(reintegro) && reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE && reintegrosListSize > 0) { %>
		<td><input type="button" value="Autorizar"
			onClick="<portlet:namespace />autorizarReintegroEntry();return false;" />
		</td>
		&nbsp;
		<%}
	  if (!esView && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) && showABMAuditorButtons
			 && Validator.isNotNull(reintegro) && reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO) { %>
		<td><input type="button" value="Desauditar"
			onClick="<portlet:namespace />desauditarReintegroEntry();return false;" />
		</td>
		&nbsp;
		<%}
	  if (!esView && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) && showABMAuditorButtons
				 && Validator.isNotNull(reintegro) && reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO) { %>
		<td><input type="button" value="Auditar"
			onClick="<portlet:namespace />auditarReintegroEntry();return false;" />
		</td>
		&nbsp;
		<%}
	  if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
	%>
		<td><input type="button" value="Catastral"
			onClick="<portlet:namespace />catastro();return false;" /></td>
		<td><input type="button" value="Odontograma"
			onClick="<portlet:namespace />odontograma();return false;" /></td>
		<%} if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {%>

		<td><input type="button" value="Histórico"
			onClick="<portlet:namespace />historico();return false;" /></td>
		<%}%>
		
		<% if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {%>		
		<td><div id="<portlet:namespace />div_tratamientos_discapacidad"><input type="button" value="Ver Tratamientos"
			onClick="<portlet:namespace />ver_tratamientos_autorizados();return false;" /></div>
		</td>
		<%}%>
		

        
		
		<td><c:if
			test="<%= (Validator.isNotNull(reintegro) && Validator.isNotNull(reintegrosList) && reintegrosList.size() > 0) && indexInList != 0 %>">
			<portlet:renderURL
				windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="backURL">
				<portlet:param name="struts_action"
					value="/liquidaciones/editar_reintegro_entry" />
				<portlet:param name="id_reintegro"
					value="<%=(reintegrosList.get(indexInList-1)).getId_reintegroString()%>" />
				<portlet:param name="tipo_reintegro" value="<%=tipo_reintegro%>" />
			</portlet:renderURL>
			<liferay-ui:icon image="back" message="Anterior" url="<%= backURL %>" />
		</c:if> &nbsp; <c:if
			test="<%= (Validator.isNotNull(reintegro) && Validator.isNotNull(reintegrosList) && reintegrosList.size() > 0) && indexInList != reintegrosList.size()-1 %>">
			<portlet:renderURL
				windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"
				var="forwardURL">
				<portlet:param name="struts_action"
					value="/liquidaciones/editar_reintegro_entry" />
				<portlet:param name="id_reintegro"
					value="<%=(reintegrosList.get(indexInList+1)).getId_reintegroString()%>" />
				<portlet:param name="tipo_reintegro" value="<%=tipo_reintegro%>" />
			</portlet:renderURL>
			<liferay-ui:icon image="forward" message="Siguiente"
				url="<%= forwardURL %>" />
		</c:if></td>
	</tr>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="9">
		<div align="center" id="<portlet:namespace />cant_prestacion_afiliado">
		</div>
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
		<div align="center" id="<portlet:namespace />reintegro_prestaciones">
		<jsp:include page='reintegro_prestaciones_search_result.jsp' /></div>
		</td>
	</tr>
</table>
</fieldset>
</form>


<form action="" method="post" enctype="multipart/form-data"
	id="<portlet:namespace />borrar_prest" name="<portlet:namespace />borrar_prest">
	<input type="hidden" name="<portlet:namespace />id_reintegro" id="<portlet:namespace />borrar_numero" value="" /> 
	<input type="hidden" name="<portlet:namespace />id_prestacion" id="<portlet:namespace />borrar_id_prestacion" value="" /> 
	<input type="hidden" name="<portlet:namespace />alta_fecha" id="<portlet:namespace />borrar_alta_fecha" value="" /> 
	<input type="hidden" name="<portlet:namespace />id_plan" id="<portlet:namespace />borrar_id_plan" value="" /> 
	<input type="hidden" name="<portlet:namespace />comprobante_tipo" id="<portlet:namespace />borrar_comprobante_tipo" value="" />
	<input type="hidden" name="<portlet:namespace />comprobante_letra" id="<portlet:namespace />borrar_comprobante_letra" value="" /> 
	<input type="hidden" name="<portlet:namespace />comprobante_suc" id="<portlet:namespace />borrar_comprobante_sucu" value="" />  
	<input type="hidden" name="<portlet:namespace />comprobante_nro" id="<portlet:namespace />borrar_comprobante_nro" value="" /> 
	<input type="hidden" name="<portlet:namespace />deletePrestaci" value="1" /> 
	<input	type="hidden" name="<portlet:namespace />tipo_r" id="<portlet:namespace />borrar_tipo_r" value="<%=tipo_reintegro%>" />
	
	<input	type="hidden" name="<portlet:namespace />borrar_id_reclamo_prestacion" 	id="<portlet:namespace />borrar_id_reclamo_prestacion" value="" />
	<input	type="hidden" name="<portlet:namespace />borrar_id_prestacion_reclamo" 	id="<portlet:namespace />borrar_id_prestacion_reclamo" value="" />
	
<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>"
	value="<%= Constants.DELETE %>" /></form>

<form action="" method="post" enctype="multipart/form-data"
	id="<portlet:namespace />cambio_estado_reintegro_"
	name="<portlet:namespace />cambio_estado_reintegro_"><input
	type="hidden" name="<portlet:namespace />cambio_estado_numero"
	id="<portlet:namespace />cambio_estado_numero"
	value="<%=Validator.isNotNull(reintegro) ? reintegro.getId_reintegroString() : "" %>" />
<input type="hidden" name="<portlet:namespace />cambio_estado_reintegro"
	id="<portlet:namespace />cambio_estado_reintegro"
	value="<%=Validator.isNotNull(reintegro) ? reintegro.getId_reintegroString() : "" %>" />
<input type="hidden" name="<portlet:namespace />estado_futuro"
	id="<portlet:namespace />estado_futuro" value="" /> <input
	type="hidden" name="<portlet:namespace />tipo_rein"
	id="<portlet:namespace />tipo_rein" value="<%=tipo_reintegro%>" /></form>

<form action="" method="post" name="<portlet:namespace />emple">
<div align="center" id="<portlet:namespace />popupprestadoresexternos">
</div>
</form>
<script>



<%
if (!esOrtodonciauProtesis) {%>
	reLoadAfiliado(); // busca reclamos prestacionales del afiliado   
<%
}
%>


//jQuery("#<portlet:namespace />importe").blur(function(){ validaMontoOriginalReclamoImporte(); }); 
//jQuery("#<portlet:namespace />cantidad").blur(function(){ validaMontoOriginalReclamoCantidad();});
//jQuery("#<portlet:namespace />importeCompro").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />sucursal_entidad").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />entidad").blur(function(){ validaMontoOriginalReclamo(); });
jQuery("#<portlet:namespace />cuit_entidad").blur(function(){ validarCuitPrestador();validaMontoOriginalReclamo(); });

        jQuery("#<portlet:namespace />con_reclamo_prestacional").val(<%=con_reclamo_prestacional%>);
        
        function <portlet:namespace />ver_reclamos_prestacionales() {
        	jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
        }

		function <portlet:namespace />hideDayFieldOfPeriodFields () {
			jQuery("#<portlet:namespace />periodoDia").hide();
		}

		<portlet:namespace />hideDayFieldOfPeriodFields ();
		jQuery('#<portlet:namespace />buscandoPrestaciones').hide();

		
		function <portlet:namespace />validarCampos() {

				//var per = document.getElementById("<portlet:namespace />prestacionFechaDia").value;
				var diaPer = document.getElementById("<portlet:namespace />prestacionFechaDia").value;
				var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
				var anioPer = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);

				var id_prestacion = jQuery("#<portlet:namespace />id_prestacion").val();			

				var cuit_prestador = jQuery("#<portlet:namespace />cuit_entidad").val();				
				var cantidad=jQuery('#<portlet:namespace />cantidad').val();
				var importe=jQuery('#<portlet:namespace />importe').val();
				var comprobante=jQuery('#<portlet:namespace />comprobante_nro').val();
			    var comprobanteSuc=jQuery('#<portlet:namespace />comprobante_suc').val();  

				/* if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
					alert("El afiliado debe tener un plan vigente en la fecha de la prestación");
					return false;
				} */
				var entidad=jQuery('#<portlet:namespace />entidad').val();
				var cuil=jQuery('#<portlet:namespace />cuil').val();
				var inte=jQuery('#<portlet:namespace />inte').val();
				var seccional=jQuery('#<portlet:namespace />id_seccional_r').val();		
				

				if(jQuery("#<portlet:namespace />entidad").val() == ""){
					alert("<liferay-ui:message key='entidad-obligatoria' />");
					jQuery("#<portlet:namespace />entidad").focus();
					return false;
				}
				if(jQuery("#<portlet:namespace />id_seccional_r").val() == ""){
					alert("<liferay-ui:message key='seccional_obligatoria' />");
					jQuery("#<portlet:namespace />id_seccional_r").focus();
					return false;
				}
				if(jQuery("#<portlet:namespace />id_seccional_r").val() != "" && jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
					alert("<liferay-ui:message key='seccional_invalida' />");
					jQuery("#<portlet:namespace />id_seccional_r").focus();
					return false;
				}
				if (trim(cuil).length == 0) {
					alert("<liferay-ui:message key='cuil-obligatorio' />");
					jQuery('#<portlet:namespace />cuil').focus();
					return false;
				}
				if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
					jQuery('#<portlet:namespace />cuil').focus();
					return false;
				}
				if(trim(inte).length == 0){
					alert("<liferay-ui:message key='inte-obligatorio' />");
					jQuery('#<portlet:namespace />inte').focus();
					return false;
				}
				if(jQuery("#<portlet:namespace />id_seccional").val() == ""){
					alert("<liferay-ui:message key='seccional_invalida' />");
					jQuery("#<portlet:namespace />id_seccional").focus();
					return false;
				}                                                                 
				if (jQuery("#<portlet:namespace />id_seccional").val() != jQuery("#<portlet:namespace />id_seccional_r").val()) {
					if (confirm("¿La seccional del reintegro no coincide con la del afiliado, desea continuar grabando?") == false){
						return false;
					}
					//solo aviso puede continuar
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

				//prestaciones								
				<c:if test="<%= (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) && !Validator.isNotNull(reintegro) %>">
					var estado= jQuery('#<portlet:namespace />estado').val();
				</c:if>
				<c:if test="<%= (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) && Validator.isNotNull(reintegro) %>">
					var estado= <%= reintegro.getEstado() %>;
				</c:if>
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
					var estado= '';
				</c:if>

				if (estado == '' || (estado != '' && estado != <%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>)) {
					var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value;				
					if (document.getElementById("<portlet:namespace />fecha_alta_af").value != ""){
						var alta = document.getElementById("<portlet:namespace />fecha_alta_af").value;
						var diaAlta = alta.substring(0, 2);
						var mesAlta = alta.substring(alta.indexOf("/")+1,alta.indexOf("/",alta.indexOf("/")+1));				
						mesPer++;//el mes sacado del select es 0 based					
						var anioAlta = alta.substring(alta.indexOf("/",alta.indexOf("/")+1)+1);
						if ((parseInt(anioPer) < parseInt(anioAlta))
							|| (parseInt(anioPer,10) == parseInt(anioAlta,10) && parseInt(mesPer,10) < parseInt(mesAlta,10))
							|| (parseInt(anioPer,10) == parseInt(anioAlta,10) && parseInt(mesPer,10) == parseInt(mesAlta,10) && parseInt(diaPer,10) < parseInt(diaAlta,10))){
							alert("La fecha de prestación corresponde a una fecha anterior al alta del afiliado.");
							return false;
						}
					}
					if(jQuery("#<portlet:namespace />id_prestacion").val() == ""){
						alert("<liferay-ui:message key='prestacion-obligatoria' />");
						jQuery("#<portlet:namespace />id_prestacion").focus();
						return false;
					}
					if(jQuery("#<portlet:namespace />id_prestacion").val() != "" && jQuery("#<portlet:namespace />pres_seleccionada").val()!="1"){
						alert("<liferay-ui:message key='prestacion-invalida' />");
						jQuery("#<portlet:namespace />id_prestacion").focus();
						return false;
					}
					<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
						if(jQuery("#<portlet:namespace />cuit_entidad").val() == ""){
							alert("<liferay-ui:message key='profesional-obligatorio' />");
							jQuery("#<portlet:namespace />cuit_entidad").focus();
							return false;
						}
						if(jQuery("#<portlet:namespace />sucursal_entidad").val() == ""){
							alert("<liferay-ui:message key='profesional-obligatorio' />");
							jQuery("#<portlet:namespace />sucursal_entidad").focus();
							return false;
						}
						var importeCompro = jQuery('#<portlet:namespace />importeCompro').val(); 	
						if(trim(importeCompro).length == 0 || trim(importeCompro) == 0){
							alert("Importe del Comprobante es obligatorio");
							jQuery('#<portlet:namespace />importeCompro').focus();
							return false;
						}
						if (parseFloat(trim(importeCompro)) <= 0){
							alert("Debe ingresar un importe para el comprobante");
							jQuery('#<portlet:namespace />importeCompro').focus();
							return false;
						}
						var m = parseFloat(parseFloat(trim(cantidad),10) * parseFloat(trim(importe),10));
						var n = parseFloat(parseFloat(trim(importeCompro)));
						m = Math.round (m * 100) / 100;
						n = Math.round (n * 100) / 100;
						if (m > n){
							alert("El importe comprobante debe ser mayor o igual al importe total");
							jQuery('#<portlet:namespace />importeCompro').focus();
							return false;
						}	
						if(jQuery("#<portlet:namespace />cuit_entidad").val() == ""){
							alert("<liferay-ui:message key='profesional-obligatorio' />");
							jQuery("#<portlet:namespace />cuit_entidad").focus();
							return false;
						}
						
						if (jQuery('#<portlet:namespace />id_tercerizadora').val() != "MEN" &&
								jQuery('#<portlet:namespace />id_tercerizadora').val() != "MPS" 
								&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "OMI"
								&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "CEU"
								&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "MIM"
								&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "MON"
								&& jQuery('#<portlet:namespace />id_tercerizadora').val() != "MCE"
								&& (jQuery('#<portlet:namespace />cargo_prestadora').val() != "0"
									||	jQuery('#<portlet:namespace />cargo_imesa').val() != "0")) {
							alert("El  afiliado debe tener una tercerizadora MOLINEROS POR ENSALUD u OMINT para ingresarle un monto a la Tercerizadora");
							return false;
						}
						
						if(jQuery("#<portlet:namespace />cargo_ospim").val() == ""){
							alert("El importe cargo ospim debe ser cero o mayor");
							jQuery("#<portlet:namespace />cargo_ospim").focus();
							return false;
						}
						
						if(jQuery("#<portlet:namespace />cargo_prestadora").val() == ""){
							alert("El importe cargo prestadora debe ser cero o mayor");
							jQuery("#<portlet:namespace />cargo_prestadora").focus();
							return false;
						}
						
						if(jQuery("#<portlet:namespace />cargo_imesa").val() == ""){
							alert("El importe cargo Monotributo debe ser cero o mayor");
							jQuery("#<portlet:namespace />cargo_imesa").focus();
							return false;
						}
							
						var sumaCargos = 	Number(jQuery("#<portlet:namespace />cargo_ospim").val())  +   Number(jQuery("#<portlet:namespace />cargo_prestadora").val()) +
								   Number(jQuery("#<portlet:namespace />cargo_imesa").val());
						var totalAux = Number(jQuery("#<portlet:namespace />total").val());
						if (totalAux.toFixed(2) < sumaCargos.toFixed(2)){
							alert("La suma del importe cargo ospim , cargo prestadora y cargo Monotributo  no debe ser mayor al importe total");
							jQuery('#<portlet:namespace />total').focus();
							return false;
						}
						
						
						
					</c:if>
					<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">					
						if(jQuery("#<portlet:namespace />id_prestador").val() == ""){
							alert("<liferay-ui:message key='profesional-obligatorio' />");
							jQuery("#<portlet:namespace />nombre_prestador").focus();
							return false;
						}
					</c:if>
					<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
						if(trim(cantidad).length == 0){
							alert("<liferay-ui:message key='cantidad-obligatoria' />");
							jQuery('#<portlet:namespace />cantidad').focus();
							return false;
						}
						
						if(trim(comprobanteSuc).length == 0 || trim(comprobanteSuc) == 0){
							alert("<liferay-ui:message key='comprobante-obligatorio-sucursal' />");
							jQuery('#<portlet:namespace />comprobante_suc').focus();
							return false;
						}

						
						if(trim(comprobante).length == 0){
							alert("<liferay-ui:message key='comprobante-obligatorio' />");
							jQuery('#<portlet:namespace />comprobante_nro').focus();
							return false;
						}
						
						if (estado != '' && 
								(trim(jQuery('#<portlet:namespace />pieza').val()).length == 0) &&
								(trim(jQuery('#<portlet:namespace />cara').val()).length == 0)){
							alert("La prestación se debe aplicar en una pieza o cara");
							jQuery('#<portlet:namespace />pieza').focus();
							return false;
						}
						
						if (estado != '' && 
								(trim(jQuery('#<portlet:namespace />pieza').val()).length != 0) &&
								(trim(jQuery('#<portlet:namespace />cara').val()).length != 0)){
							alert("La prestación no puede estar aplicada a pieza y cara simultáneamente");
							jQuery('#<portlet:namespace />pieza').focus();
							return false;
						}
				
						if (estado != '' && 
								(trim(jQuery('#<portlet:namespace />pieza').val()).length != 0) &&
								(trim(jQuery('#<portlet:namespace />pieza').val()) < 11 || trim(jQuery('#<portlet:namespace />pieza').val()) > 85)){
							alert("Número de pieza debería ser entre 11 y 85");
							jQuery('#<portlet:namespace />pieza').focus();
							return false;
						}					
					</c:if>								
					if(trim(importe).length == 0 || trim(importe) == 0){
						jQuery('#<portlet:namespace />importe').focus();
					}					
					if (!isFloat(trim(importe))){
						alert("<liferay-ui:message key='importe-invalido' />");
						jQuery('#<portlet:namespace />importe').focus();
						return false;
					}
				}
								
				if (id_prestacion == '' && estado == <%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>) {					
					jQuery('#<portlet:namespace />id_prestacion').val(0);
					
					<portlet:namespace />saveReintegro();
				}
				
			return true;	    	
		}
        
		function <portlet:namespace />oculta_prestaciones_reclamos(){
		
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
			jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
			jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
			
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
			
			jQuery('#<portlet:namespace />cargo_prestadora').val("");
			jQuery('#<portlet:namespace />cargo_ospim').val("");
			jQuery('#<portlet:namespace />cargo_imesa').val("");
			jQuery('#<portlet:namespace />tope_importe_plan').val("");
			
			jQuery('#<portlet:namespace />cuit_entidad').val("");
			jQuery('#<portlet:namespace />sucursal_entidad').val("");
			jQuery('#<portlet:namespace />entidad_razon').val("");
			
			
			jQuery('#<portlet:namespace />comprobante_tipo').val("");
			jQuery('#<portlet:namespace />comprobante_letra').val("");
			jQuery('#<portlet:namespace />comprobante_suc').val("");
			jQuery('#<portlet:namespace />comprobante_nro').val("");
			jQuery('#<portlet:namespace />importeCompro').val("");
			
/*			
			jQuery('#<portlet:namespace />prestacionComproFechaDia').val("");
			jQuery('#<portlet:namespace />prestacionComproFechaMes').val("");
			jQuery('#<portlet:namespace />prestacionComproFechaAnio').val("");
*/			
			
			// desahilita el control de busqueda de afiliados
			<portlet:namespace />habilitaControlBusquedaAfiliado(true);
			// habilita  controles de importes de la pretacion 
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);
		}
			
		function <portlet:namespace />ver_prestaciones_reclamos() {
	    			
		    var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var params;		
			var p_marca_rein_liq=jQuery('#<portlet:namespace />marca_rein_liq').val();
			// En la solapa de Reintegros (prestacionales) se liquida todo lo que es 
			// Odontología General, marcado en el nomenclador con marca_rein_liq=3
			// Protesis con marca_rein_liq=4
			// Ortopedia-Odontologica=5
			var plan=jQuery('#<portlet:namespace />nombre_plan_campo').val();
			
			params = { "cuil":cuil,  "inte":inte ,"reintegro":true, "marca_rein_liq":p_marca_rein_liq,  "viene_de_cuotas": false, "plan": plan };
						
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_prestaciones_reclamos_reintegros" /></portlet:renderURL>';
			
			jQuery('#<portlet:namespace />div_reclamos_prestaciones').load(url,params, function(){
												jQuery('#<portlet:namespace />buscando').hide();            															
																  });
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		    jQuery("#div_boton_cancelar_reclamos_prestaciones").hide();
			
		}
			
		function <portlet:namespace />saveReintegroEntry() {
			var resp;
						
			resp=jQuery('#<portlet:namespace />cuitvalidoprestador').val();
			if (resp=="false"){
				alert('Cuit de Prestador no presente en informacion de AFIP.');
				return false;
			}
		    
			if (jQuery('#<portlet:namespace />importeoriginalnovalidado').val()!='') {
			      alert('El total ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestaci\u00f3n en el reclamo.');
				  return false; 	
			    }	
			jQuery('#<portlet:namespace />importeoriginalnovalidado').val('');
			    
			if (<portlet:namespace />validarCampos()) {
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
					if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {
						<portlet:namespace />validarTopesDiscapacidad();
					}
					else {
						<portlet:namespace />validarTopes();
					}
				</c:if>
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				
					if (validarTopesProtesisPorFecha() == false){
						return false;
					};
					<portlet:namespace />validarTopesProtesis();
				</c:if>
				<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
					<portlet:namespace />validarTopesOrto();
				</c:if>			
			}
			return false;
		}

		function <portlet:namespace />saveReintegro() {
					
			<% if ( !(Validator.isNotNull(reintegro) && reintegro.getId_reintegro() != 0)  && !esView 	) { 	%>
						<portlet:namespace />habilitaControlBusquedaAfiliado(true);						
			<%}%>		
			
			<%if ( !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) ){ %>
			
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);
			
			<% } %>
			
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= reintegro == null || (reintegro != null && reintegro.getId_reintegro() == 0) ? Constants.ADD : Constants.UPDATE %>";
								
			url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";			
			submitForm(document.<portlet:namespace />fm, url);			
			
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
				
		
		function <portlet:namespace />saveReintegroProtesis(esExcepcion) {
			
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= reintegro == null || (reintegro != null && reintegro.getId_reintegro() == 0) ? Constants.ADD : Constants.UPDATE %>";
			url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
			url += '&esExcepcion='+esExcepcion;
			submitForm(document.<portlet:namespace />fm, url);
		}
		
		
		function <portlet:namespace />validarTopes() {
			var entra = 0;
			if (jQuery("#<portlet:namespace />incapacidad_af").val() != '1') {
				var id_prestacion = jQuery("#<portlet:namespace />id_prestacion").val();
				var tope_prestacion = jQuery("#<portlet:namespace />tope_cantidad").val();
				<%
					for (int id_p : WebKeysLiquidaciones.PRESTACIONES_CON_TOPES) {
				%>
						if (id_prestacion == <%=id_p%>) {
							entra = 1;
							var cuil=jQuery('#<portlet:namespace />cuil').val();
							var inte=jQuery('#<portlet:namespace />inte').val();
							var cantidad=jQuery('#<portlet:namespace />cantidad').val();
							var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_cantidad_prestacion_afiliado&cuil_titular='+cuil+
							  '&inte='+inte+'&id_prestacion='+id_prestacion+'&tope_prestacion='+tope_prestacion+'&cantidad='+cantidad;						
							jQuery("#<portlet:namespace />cant_prestacion_afiliado").load(url);																
						}
				<%
					}
				%>
			}
			if (entra == 0) {					
				<portlet:namespace />saveReintegro();			
			}
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
			var cuit=jQuery('#<portlet:namespace />cuit_entidad').val();
			var sucu=jQuery('#<portlet:namespace />sucursal_entidad').val();
			var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();
			
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
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_topes_discapacidad_afiliado&cuil_titular='+cuil+
			  '&inte='+inte+'&id_prestacion='+id_prestacion+'&fecha_prestacion='+fecha_prestacion+'&cantidad='+cantidad+'&importe='+importe+'&importe_anterior='+importe_anterior+'&cantidad_anterior='+cantidad_anterior+'&cuit_entidad='+cuit+'&sucursal_entidad='+sucu+'&periodo='+ppn+'&codPrestaci='+codPrestaci;
			jQuery("#<portlet:namespace />cant_prestacion_afiliado").load(url);
		}
		
		function <portlet:namespace />validarTopesProtesis() {
			var tope_prestacion = <%=WebKeysLiquidaciones.TOPES_PROTESIS_FAMILIA%>;
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var importe=jQuery('#<portlet:namespace />importe').val();
			var importeTope=jQuery('#<portlet:namespace />tope_importe_plan').val();
			var cantidad=parseInt(jQuery('#<portlet:namespace />cantidad').val());
			var id_prestacion_anterior = jQuery("#<portlet:namespace />id_prestacion_anterior").val();
			var idPlanAfi=-1;
			
			try{
			    idPlanAfi=jQuery("#<portlet:namespace />id_plan_afi").val();
		    }catch (err){}
		    			    
		    // Duvi - 2022-04-19 Si el importe es superior al importe del Plan, pide confirmacion del usuario.
		    if (parseFloat(importe) > parseFloat(importeTope)) {
		    	if (!confirm("El monto a cargo de Ospim  supera el tope de la prestación según plan. ¿Estás seguro de realizar la excepción?")) {
		    		jQuery("#<portlet:namespace />esExcepcion").val(false);		    		         
		    		return;
		    	} else {
		    		jQuery("#<portlet:namespace />esExcepcion").val(true);
		    	}
		    }

		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_cantidad_prestacion_protesis&cuil_titular='+cuil+
			  '&importe='+importe+'&tope_prestacion='+tope_prestacion+'&cantidad='+cantidad+'&id_prestacion_anterior='+id_prestacion_anterior
			  +'&id_plan='+idPlanAfi;	

			jQuery("#<portlet:namespace />cant_prestacion_afiliado").load(url);
		}
	
		function validarTopesProtesisPorFecha() {
		    var params="";
		  	var tope_prestacion = <%=WebKeysLiquidaciones.TOPES_PROTESIS_FAMILIA%>;
			var cuilTitular=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var pieza=jQuery('#<portlet:namespace />pieza').val();
			var cara=jQuery('#<portlet:namespace />cara').val();
			var diaPer = document.getElementById("<portlet:namespace />prestacionFechaDia").value;
			var mesPer = document.getElementById("<portlet:namespace />prestacionFechaMes").value; 
			var anioPer = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;
			var codigo = jQuery("#<portlet:namespace />codigo").val();
			var editProtesis = jQuery("#<portlet:namespace />editProtesis").val();

		    var respuesta=true;
		    var rtaExisteTope=false;
		    var rtaExisteTopeSinCodigo=false;
		    params += "&cuil_titular="+cuilTitular;
		    params += "&inte="+inte;
		    params += "&pieza="+pieza;
		    params += "&cara="+cara;
		    params += "&diaPer="+diaPer;
		    params += "&mesPer="+mesPer;
		    params += "&anioPer="+anioPer;
		    params += "&codigo="+codigo;
		    params += "&editProtesis="+editProtesis;
		    
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/validarTopesProtesisPorFecha';
			   url = url + params;
			   jQuery.ajax({   
				   url: url,
				   async: false,
				   success: function(data) {
						var obj = jQuery.parseJSON(data);
						var respExisteTope = obj.existeTope;
						var respExisteTopeSinCodigo = obj.existeTopeSinCodigo;
						rtaExisteTope=(respExisteTope  === 'true');
						rtaExisteTopeSinCodigo=(respExisteTopeSinCodigo  === 'true');
			   		}
			   }); 
			   
			   if(rtaExisteTope){
				   alert ('Prestación ' + codigo + ' en pieza ' +   pieza + ' está en garantía (1 año). Consulte el odontograma e histórico.');
				   respuesta=false;
			   }
			   
			   if(rtaExisteTopeSinCodigo){
				   alert ('Prestación ' + codigo + ' en pieza ' + pieza + '  está en garantía (5 años). Consulte el odontograma e histórico.');
				   respuesta=false;
			   }
			   
		       return  respuesta;    
			 
		}

		function <portlet:namespace />validarTopesOrto() {
			//POR AHORA NO NECESITAN MANEJO DE TOPES
			<portlet:namespace />saveReintegro();
		}
		
		function borraReintegroPrestacion(id_reintegro, id_prestacion, alta_fecha, id_reclamo, id_prestacion_reclamo, 
				id_plan, tipocompro, letraCompro, sucuCompro, nrocompro){
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
				return false;
			}else{
				jQuery("#<portlet:namespace />borrar_numero").val(id_reintegro);
				jQuery("#<portlet:namespace />borrar_id_prestacion").val(id_prestacion);
				jQuery("#<portlet:namespace />borrar_alta_fecha").val(alta_fecha);
				jQuery("#<portlet:namespace />borrar_id_plan").val(id_plan);
				jQuery("#<portlet:namespace />borrar_comprobante_tipo").val(tipocompro);
				jQuery("#<portlet:namespace />borrar_comprobante_letra").val(letraCompro);
				jQuery("#<portlet:namespace />borrar_comprobante_sucu").val(sucuCompro);
				jQuery("#<portlet:namespace />borrar_comprobante_nro").val(nrocompro);
				jQuery("#<portlet:namespace />borrar_tipo_r").val('<%= tipo_reintegro %>');
				
				jQuery("#<portlet:namespace />borrar_id_reclamo_prestacion").val(id_reclamo);
				jQuery("#<portlet:namespace />borrar_id_prestacion_reclamo").val(id_prestacion_reclamo);
				
				var url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
				submitForm(document.<portlet:namespace />borrar_prest, url);
				return true;
			}
			return false;
		}

		function editarReintegroPrestacion(id_reintegro, id_prestacion, alta_fecha, id_plan, fecha_prestacion, 
				codigo, des_prestacion, pieza, cara, cantidad, id_prestador, honorarios, tercerizadora, periodo, 
				importe, cuit,id_reclamo_prestacional,id_prestacion_reclamo, descripcion, tipocompro, letraCompro, sucucompro, nrocompro, 
				cuit_entidad, sucu_entidad, fecha_comprobante, importe_comprobante,cargo_ospim, cargo_prestadora,cargo_imesa
				)
		{
			
			jQuery("#<portlet:namespace />id_prestacion_anterior").val(id_prestacion);
			jQuery("#<portlet:namespace />id_prestacion").val(id_prestacion);
			jQuery("#<portlet:namespace />prestacion").val(des_prestacion);			
			jQuery("#<portlet:namespace />pres_seleccionada").val("1");
			jQuery("#<portlet:namespace />prestacion_alta_fecha").val(alta_fecha);			
			jQuery("#<portlet:namespace />prestacion_id_plan").val(id_plan);
			jQuery("#<portlet:namespace />tope_cantidad").val(999999999);			
			var diaP = parseInt(fecha_prestacion.substring(0, 2),10);
			var mesP = parseInt(fecha_prestacion.substring(fecha_prestacion.indexOf("/")+1,fecha_prestacion.indexOf("/",fecha_prestacion.indexOf("/")+1)),10);				
			mesP--;//el mes sacado del select es 0 based
			var anioP = fecha_prestacion.substring(6,10);
						
			jQuery("#<portlet:namespace />prestacionFechaDia").val(diaP);
			jQuery("#<portlet:namespace />prestacionFechaMes").val(mesP);
			jQuery("#<portlet:namespace />prestacionFechaAnio").val(anioP);
			
			jQuery("#<portlet:namespace />codigo").val(codigo);		
			jQuery("#<portlet:namespace />codigo_anterior").val(codigo);
			
			jQuery("#<portlet:namespace />pres_seleccionada").val("1");
			jQuery("#<portlet:namespace />importe").val(importe);
			jQuery("#<portlet:namespace />importe_anterior").val(importe);
			jQuery("#<portlet:namespace />cantidad_anterior").val(cantidad);
			<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
				if (cuit_entidad != 'null') {
					jQuery("#<portlet:namespace />cuit_entidad").val(cuit_entidad);
					jQuery("#<portlet:namespace />sucursal_entidad").val(sucu_entidad);		
					jQuery("#<portlet:namespace />cargo_ospim").val(cargo_ospim);
					jQuery("#<portlet:namespace />cargo_prestadora").val(cargo_prestadora);
					jQuery("#<portlet:namespace />cargo_imesa").val(cargo_imesa);
					
					<portlet:namespace />buscarEntidad();
				}

				var diaC = parseInt(fecha_comprobante.substring(0, 2),10);
				var mesC = parseInt(fecha_comprobante.substring(fecha_comprobante.indexOf("/")+1,fecha_comprobante.indexOf("/",fecha_comprobante.indexOf("/")+1)),10);				
				mesC--;//el mes sacado del select es 0 based
				var anioC = fecha_comprobante.substring(6,10);

				jQuery("#<portlet:namespace />prestacionComproFechaDia").val(diaC);
				jQuery("#<portlet:namespace />prestacionComproFechaMes").val(mesC);
				jQuery("#<portlet:namespace />prestacionComproFechaAnio").val(anioC);

				jQuery("#<portlet:namespace />importeCompro").val(importe_comprobante);
				
				jQuery("#<portlet:namespace />cuit_prestador").val(cuit);
				jQuery("#<portlet:namespace />nombre_prestador").val(descripcion);
				
				jQuery("#<portlet:namespace />descontar_capitas").val(tercerizadora);
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
				jQuery("#<portlet:namespace />periodoMesAnio").val(perio);
				var ppn =  diaPeriodo + "/" + mesPeriodo + "/" + anioPeriodo;
				jQuery("#<portlet:namespace />periodoHidden").val(ppn);
			</c:if>
			jQuery("#<portlet:namespace />comprobante_tipo").val(tipocompro);
			jQuery("#<portlet:namespace />comprobante_letra").val(letraCompro);
			jQuery("#<portlet:namespace />comprobante_nro").val(nrocompro);
			jQuery("#<portlet:namespace />comprobante_suc").val(sucucompro);
			jQuery("#<portlet:namespace />editPrestaci").val("1");
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value;
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;
			jQuery("#<portlet:namespace />fprest").val(fpn);
			<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				jQuery("#<portlet:namespace />pieza").val(pieza);
				jQuery("#<portlet:namespace />cara").val(cara);
				jQuery("#<portlet:namespace />id_prestador").val(id_prestador);
				jQuery("#<portlet:namespace />editProtesis").val("1");
				jQuery("#<portlet:namespace />prest_seleccionada").val("1");
				
				jQuery("#<portlet:namespace />cargo_ospim").val(cargo_ospim);
				jQuery("#<portlet:namespace />cargo_prestadora").val(cargo_prestadora);
				jQuery("#<portlet:namespace />cargo_imesa").val(cargo_imesa);
				<portlet:namespace />buscarPrestador();
			</c:if>
			<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
				jQuery("#<portlet:namespace />id_prestador").val(id_prestador);
				jQuery("#<portlet:namespace />presupuesto").val(honorarios);
				jQuery("#<portlet:namespace />descontar_capitas").val(tercerizadora);
				<portlet:namespace />buscarPrestador();
			</c:if>
			reLoadAfiliado();
			jQuery("#<portlet:namespace />importe").val(importe);
			<c:if test="<%= !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
				jQuery("#<portlet:namespace />cantidad").val(cantidad);
				sumarTodo();
			</c:if>
			
			jQuery('#<portlet:namespace />id_reclamo_prestacional').val(id_reclamo_prestacional);
			jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(id_prestacion_reclamo);
			
			if(id_reclamo_prestacional!=0 && id_prestacion_reclamo!=0){
				 <portlet:namespace />desactivaControlesPrestacionDesdeReclamo(true);
				 jQuery("#<portlet:namespace />div_label_prestacion_reclamo").show();
			}			
			
		}
			
		function copiarReintegroPrestacion(fecha_prestacion, cantidad, id_prestador_ext, tipocompro, letraCompro, sucucompro, nrocompro) {
			var diaP = parseInt(fecha_prestacion.substring(0, 2),10);
			var mesP = parseInt(fecha_prestacion.substring(fecha_prestacion.indexOf("/")+1,fecha_prestacion.indexOf("/",fecha_prestacion.indexOf("/")+1)),10);				
			mesP--;//el mes sacado del select es 0 based
			var anioP = fecha_prestacion.substring(6,10);						
			jQuery("#<portlet:namespace />prestacionFechaDia").val(diaP);
			jQuery("#<portlet:namespace />prestacionFechaMes").val(mesP);
			jQuery("#<portlet:namespace />prestacionFechaAnio").val(anioP);
			jQuery("#<portlet:namespace />cantidad").val(cantidad);
			jQuery("#<portlet:namespace />comprobante_tipo").val(tipocompro);
			jQuery("#<portlet:namespace />comprobante_suc").val(sucucompro);
			jQuery("#<portlet:namespace />comprobante_letra").val(letraCompro);
			jQuery("#<portlet:namespace />comprobante_nro").val(nrocompro);
			var diaPrest = document.getElementById("<portlet:namespace />prestacionFechaDia").value; 
			var mesPrest = document.getElementById("<portlet:namespace />prestacionFechaMes").value; //per.substring(0,per.indexOf("_"));
			var anioPrest = document.getElementById("<portlet:namespace />prestacionFechaAnio").value;//per.substring(per.indexOf("_")+1);
			mesPrest++;			
			var fpn =  diaPrest + "/" + mesPrest + "/" + anioPrest;
			jQuery("#<portlet:namespace />fprest").val(fpn);
			jQuery("#<portlet:namespace />id_prestador").val(id_prestador_ext);
			jQuery("#<portlet:namespace />prest_seleccionada").val("1");
			<portlet:namespace />buscarPrestador();
			reLoadAfiliado();
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
			} catch(err){}
		}
		
		function bloquearHastaPrestacion(e){
			if (trim(document.getElementById("<portlet:namespace />tope_cantidad").value) == ""){
				if (window.event) {
					window.event.returnValue=false;
				} else {
					e.preventDefault();
				}
				alert("Debe seleccionar una prestación válida");
				return false;
			}
			return true;
		}

		try {
			sumarTodo();
		} catch (err) {}

		var errorJS = "<%=error != null ? error : ""%>";		
		if (errorJS != ""){
			alert(errorJS);
		} else {
			jQuery("#<portlet:namespace />id_prestacion").val("");
			jQuery("#<portlet:namespace />codigo").val("");
		    jQuery("#<portlet:namespace />prestacion").val("");
		    jQuery("#<portlet:namespace />pres_seleccionada").val("");
			jQuery("#<portlet:namespace />tope_cantidad").val("");
			jQuery("#<portlet:namespace />tope_importe").val("");
			jQuery("#<portlet:namespace />tope_individ_cantidad").val("");
			jQuery("#<portlet:namespace />tope_individ_importe").val("");			
			jQuery("#<portlet:namespace />cuit_prestador").val("");			
			jQuery("#<portlet:namespace />nombre_prestador").val("");			
			jQuery("#<portlet:namespace />comprobante_nro").val("");
			jQuery("#<portlet:namespace />cuit_entidad").val("");			
			jQuery("#<portlet:namespace />sucursal_entidad").val("");
		}

		function reLoadAfiliado() {
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			if (cuil != "" && inte != ""){
				<portlet:namespace />buscarAfiliados(jQuery("#<portlet:namespace />fprest").val());
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

		jQuery('#<portlet:namespace />periodoMesAnio').change(function(){
			var periodo = jQuery('#<portlet:namespace />periodoMesAnio').val();
			var diaPeriodo = '01';
			var mesPeriodo = parseInt(periodo.substring(0, 2));
			var anioPeriodo = periodo.substring(periodo.indexOf("/")+1,periodo.length);				
			mesPeriodo++;//el mes sacado del select es 0 based
			var perio = mesPeriodo + "_" + anioPeriodo;
			var ppn =  diaPeriodo + "/" + mesPeriodo + "/" + anioPeriodo;				
			jQuery("#<portlet:namespace />periodoHidden").val(ppn);								
		});
		
		function <portlet:namespace />auditarReintegroEntry() {
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-auditar-this-reintegro'/>")){
				return false;
			}else{
				var url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
				jQuery("#<portlet:namespace />cambio_estado_reintegro").val('<%=reintegro!=null ? reintegro.getId_reintegroString() : ""%>');
				jQuery("#<portlet:namespace />cambio_estado_numero").val('<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO%>');
				submitForm(document.<portlet:namespace />cambio_estado_reintegro_, url);
				return true;
			}
			return false;
		}

		function <portlet:namespace />rechazarReintegroEntry() {
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-rechazar-this-reintegro'/>")){
				return false;
			}else{
				var url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
				jQuery("#<portlet:namespace />cambio_estado_reintegro").val('<%=reintegro!=null ? reintegro.getId_reintegroString() : ""%>');
				jQuery("#<portlet:namespace />cambio_estado_numero").val('<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO%>');
				submitForm(document.<portlet:namespace />cambio_estado_reintegro_, url);
				return true;
			}
			return false;
		}
		
		function <portlet:namespace />autorizarReintegroEntry() {
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-autorizar-this-reintegro'/>")){
				return false;
			}else{
				var url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
				jQuery("#<portlet:namespace />cambio_estado_reintegro").val('<%=reintegro!=null ? reintegro.getId_reintegroString() : ""%>');
				jQuery("#<portlet:namespace />cambio_estado_numero").val('<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>');
				submitForm(document.<portlet:namespace />cambio_estado_reintegro_, url);
				return true;
			}
			return false;
		}

		function <portlet:namespace />desauditarReintegroEntry() {
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-desauditar-this-reintegro'/>")){
				return false;
			}else{				
				var url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_reintegro_entry' /></portlet:actionURL>";
				jQuery("#<portlet:namespace />cambio_estado_reintegro").val('<%=reintegro!=null ? reintegro.getId_reintegroString() : ""%>');
				jQuery("#<portlet:namespace />cambio_estado_numero").val('<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>');
				submitForm(document.<portlet:namespace />cambio_estado_reintegro_, url);
				return true;
			}
			return false;
		}
		
		var popupCatastro;
		function <portlet:namespace />catastro() {
			var view = 'false';
			<c:if test="<%= viewStr != null %>">
				view = 'true';
			</c:if>
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			if (trim(cuil).length == 0 || trim(inte).length == 0) {
				alert ("Debe seleccionar el afiliado primero");
				return false;
			}			
			popupCatastro = Liferay.Popup({title:"<liferay-ui:message key="Catastral" />",modal:true,width:1000});
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/odo_catastro&cuil='+escape(cuil)+'&inte='+escape(inte)+'&view='+view;
			jQuery(popupCatastro).load(url);
		}

		var popupCuotas;
		function <portlet:namespace />abmCuota(nro_cuota, is_reload) {
			var view = 'false';
			<c:if test="<%= viewStr != null %>">
				view = 'true';
			</c:if>
			var id_reintegro=jQuery('#<portlet:namespace />id_reintegro').val(); //id del tratamiento
			var marca_rein_liq = jQuery('#<portlet:namespace />marca_rein_liq').val();;
			
		    var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			
			//popupCuotas = Liferay.Popup({title:"Cuota "+nro_cuota,modal:true,width:800,position:[220,40]});
			popupCuotas = Liferay.Popup({title:"Cuota "+nro_cuota,modal:true,width:800,position:[20,10]});
		    // &struts_action=/liquidaciones/odo_cuotas&marca_rein_liq='+marca_rein_liq+'&cuota='+nro_cuota+'&id_reitnegro='+id_reintegro+'&view='+view;
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/odo_cuotas&inte='+inte+'&cuil='+cuil+'&marca_rein_liq='+marca_rein_liq+'&cuota='+nro_cuota+'&id_reitnegro='+id_reintegro+'&view='+view+'&is_reload='+is_reload;
			jQuery(popupCuotas).load(url);
		}

		function <portlet:namespace />reloadPopupCuotas() {
			var cuota = jQuery('#<portlet:namespace />nro_cuota').val();			
			Liferay.Popup.close(popupCuotas);
			<portlet:namespace />abmCuota(cuota, true);
		}
		
		var popupHistorico;
		function <portlet:namespace />historico() {
			var view = 'false';
			<c:if test="<%= viewStr != null %>">
				view = 'true';
			</c:if>
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var tipo_reintegro='<%=tipo_reintegro%>';
			if (trim(cuil).length == 0 || trim(inte).length == 0) {
				alert ("Debe seleccionar el afiliado primero");
				return false;
			}
			popupCatastro = Liferay.Popup({title:"<liferay-ui:message key="Histórico" />",modal:true,width:1000});
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/odo_historico&cuil='+escape(cuil)+'&inte='+escape(inte)+'&view='+view+'&tipo_reintegro='+tipo_reintegro;
			jQuery(popupCatastro).load(url);
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
			var cuit=jQuery('#<portlet:namespace />cuit_entidad').val();
			
			var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();			
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
			
			var tipo_reintegro='<%=tipo_reintegro%>';
			if (trim(cuil).length == 0 || trim(inte).length == 0) {
				alert ("Debe seleccionar el afiliado primero");
				return false;
			}
			popupTratamientos = Liferay.Popup({title:"<liferay-ui:message key="Tratamientos autorizados" />",modal:true,width:1000});
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/ver_tratamientos_discapacidad_afiliado&cuil='+escape(cuil)+'&inte='+escape(inte)+'&view='+view+'&tipo_reintegro='+tipo_reintegro+
		    '&id_prestacion='+id_prestacion+'&cuit_entidad='+cuit+'&periodo='+ppn+'&codPrestaci='+codPrestaci;
			jQuery(popupTratamientos).load(url);
		}
		
		var popupOdontograma;
		function <portlet:namespace />odontograma() {
			var view = 'false';
			<c:if test="<%= viewStr != null %>">
				view = 'true';
			</c:if>			
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var tipo_reintegro = '<%=tipo_reintegro%>';
			if (trim(cuil).length == 0 || trim(inte).length == 0) {
				alert ("Debe seleccionar el afiliado primero");
				return false;
			}
			popupCatastro = Liferay.Popup({title:"<liferay-ui:message key="Odontograma" />",modal:true,width:800});
		    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/odo_odontograma&cuil='+escape(cuil)+'&inte='+escape(inte)+'&view='+view+'&tipo_reintegro='+tipo_reintegro;
			jQuery(popupCatastro).load(url);
		}

		function mostrarReintegro(id_reintegro) {
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action='+'/liquidaciones/editar_reintegro_entry'+'&id_reintegro='+id_reintegro;						
			submitForm(document.<portlet:namespace />fm, url);
		}

		function cambiaCuit(){
		}		

		var popupTratamientosD;
		function editarTratamiento(id_tratamiento) {
			popupTratamientosD = Liferay.Popup({title:"Ver Tratamiento",modal:true,position:[150,30],xy: ['center', 100],width:1120});
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/tratamiento_discapacidad&id_tratamiento='+id_tratamiento+'&view=true';
			jQuery(popupTratamientosD).load(url);
		}

		
		jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").hide();
	    jQuery("#<portlet:namespace />div_label_prestacion_reclamo").hide();
	    	    
	    <%-- 
	    // En la solapa de Reintegros (prestacionales) se liquida todo lo que es 
		// Odontología General, marcado en el nomenclador con marca_rein_liq=3
		// Protesis con marca_rein_liq=4
		// Ortopedia-Odontologica=5
	    --%>
	    
	   
	    <%	      
	    if (Integer.parseInt(con_reclamo_prestacional) ==1  || (Validator.isNotNull(reintegro) && reintegro.getId_reintegro() != 0) && !esView ) { 	%>
	    	<%
	    	    if ( !esOrtodonciauProtesis){ %>
	    	    
	    	        jQuery('#<portlet:namespace />marca_rein_liq').val('3') ;
	    	        <portlet:namespace />ver_prestaciones_reclamos();
	    	 <% } else { %>
	    	 
	    	     <% if (!esOrtopediaOrtodoncia) { %>
	    	            jQuery('#<portlet:namespace />marca_rein_liq').val('4') ;
				        <portlet:namespace />ver_prestaciones_reclamos();
			    <% } else { %>
			            jQuery('#<portlet:namespace />marca_rein_liq').val('5') ;
		            
			            <portlet:namespace />ver_prestaciones_reclamos();
			    <% } %>
	    	 <% }
        }%>	    
       
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
       
       function <portlet:namespace />desactivaControlesBuscadorPrestacion(valor) {
    	   if (valor) {
    		   document.getElementById("<portlet:namespace />prestacion").disabled = "disabled";
       		   document.getElementById("<portlet:namespace />codigo").disabled = "disabled";
    	   }
    	   else{
    		   document.getElementById("<portlet:namespace />prestacion").disabled = "";
       		   document.getElementById("<portlet:namespace />codigo").disabled = "";
    	   }
    	   
       }
       function validaMontoOriginalReclamo()
       {
    	   
    	   if (jQuery("#<portlet:namespace />importeoriginalreclamo").val()!=''){
	    	   if (parseFloat(jQuery('#<portlet:namespace />importe').val())>parseFloat(jQuery('#<portlet:namespace />importeoriginalreclamo').val()) ){
	    		   alert('El monto ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestaci\u00f3n en el reclamo.');
	    		   jQuery("#<portlet:namespace />importe").val('');    		   
	    		   jQuery("#<portlet:namespace />importe").focus();
	    		   return false; 
	    	   }    	   
	    	   if (jQuery('#<portlet:namespace />importe').val()==""){
	    		   alert('Debe ingresar un  importe superior a : ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val()  )
	    		   jQuery("#<portlet:namespace />importe").focus();    		   
	    	   }
    	   }
       }
       
       
       function validaMontoOriginalReclamoCantidad(){
    		if (!validaMontoOriginalReclamo()){		 
    	    	jQuery("#<portlet:namespace />cantidad").focus();
    	    	return false;
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
	
    			
    	function validaMontoOriginalReclamoImporte(){
    	    if (!validaMontoOriginalReclamo()){    	    	
    	    	jQuery("#<portlet:namespace />importe").focus();
    		}
    	} 
    			
    	function validarCuitPrestador() {
	       var cuit="";
	       var params="";
	       cuit=jQuery("#<portlet:namespace />cuit_entidad").val();
	       params += "&nroCuitEmpresa="+cuit;
	       
	       if(cuit.trim().length == 0){
	    	   alert('Complete la empresa');
	    	   return false;
	       }
	       
	       
	       var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/validar_cuit_prestador';
	  	   url = url + params;
	  	   jQuery.ajax({   
	  	   url: url,
	  		success: function(data) {
				var obj = jQuery.parseJSON(data);
	  			var respuesta = obj.nroCuitExisteEnAfip;
	  			jQuery('#<portlet:namespace />cuitvalidoprestador').val(respuesta);
	  		}
			}); 

           return  true;    
	  	 
		}
	  	  
	  			
</script>
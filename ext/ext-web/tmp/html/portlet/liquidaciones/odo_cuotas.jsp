<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

String cuota = request.getParameter("cuota");
String id_reitnegro = request.getParameter("id_reitnegro");
String view = (String)request.getParameter("view");
String marca_rein_liq = request.getParameter("marca_rein_liq");
String cuil = request.getParameter("cuil");
String inte = request.getParameter("inte");
String is_reload = request.getParameter("is_reload");

String acum = "";

boolean esReload = true;
if (null==is_reload || !is_reload.equals("true")) {
	esReload = false;	
}

boolean esView = true;
if(null==view || !view.equals("true")){
	esView = false;
}

boolean showABMAuditorButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);

Reintegro reintegro = ReintegroServiceUtil.getReintegroEntry(Integer.valueOf(id_reitnegro));
ArrayList <DetalleCuota> detalleCuotas = (ArrayList<DetalleCuota>)reintegro.getDetalleCuota();

BigDecimal totalPrestacion = reintegro.getImportePrestacion();

DetalleCuota detalleCuota = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, Integer.valueOf(cuota));
reintegro = ReintegroServiceUtil.traeResumenOP(reintegro, detalleCuota.getId_reintegro_user(), WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA);

Date fechaReintegro = null;
Calendar fechaHoy = CalendarFactoryUtil.getCalendar();

fechaReintegro = Validator.isNotNull(detalleCuota)? detalleCuota.getFecha() : null;
if (fechaReintegro == null) {
	fechaHoy.setTime(new Date());
}
else{
	fechaHoy.setTime(detalleCuota.getFecha());
}

Date periodoReintegro = null;
Calendar periodo = CalendarFactoryUtil.getCalendar();
periodoReintegro = Validator.isNotNull(detalleCuota)? detalleCuota.getPeriodo() : null;
if (periodoReintegro == null) {
	periodo.setTime(new Date());
}
else{
	periodo.setTime(detalleCuota.getPeriodo());
}

%>
<style>
	.id-reclamo{
	  background-color: #2196F3;
	  color: white;
	  font-family: Arial;
	  font-size:12px;
	}
	.total-prest{
	  background-color: gray;
	  color: #2196F3;
	  font-family: Arial;
	  font-size:10px;
	}
	.colorOK {
	  background-color: #2196F3;
	  color: white;
	  font-family: Arial;
	  font-size:12px;
	  text-align:right;
	  width:100%;
	  word-wrap: break-word;
	}
	.cartelSuccess {
	  color: green;
	  font-family: Arial;
	  font-size:12px;
	  text-align:left;
	  width:100%;
	  word-wrap: break-word;
	}
	.colorWarning{
	  background-color: #ff9800;
	  color: white;
	  font-family: Arial;
	  font-size:12px;
	  text-align:right;
	  width:100%;
	  word-wrap: break-word;
	}
</style>

<portlet:defineObjects />

<div align="center" id="<portlet:namespace />cuota_resultado">
	<jsp:include page='cuota_search_result.jsp'>
		<jsp:param name="view" value="<%=view%>" />
	</jsp:include>
</div>

<c:if test="<%=(esReload)%>">
	<fieldset class="portlet-msg-success">
		<leyend>Se guardó correctamente, reclamo asociado.</leyend>
	</fieldset>
	<liferay-ui:success key="request_processed" message="grabar-exitoso" />
</c:if>

<c:if test="<%=(detalleCuota.getId_Reclamo() == 0)%>">
<div id="<portlet:namespace />div_reclamos_prestaciones_cuota">

	<fieldset class="block-labels"><legend><liferay-ui:message key="Prestaciones de los Reclamos del Afiliado" /></legend>		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
			<tr>
		    	<liferay-util:include page="/html/portlet/liquidaciones/reintegros/reintegro_prestaciones_reclamos_search_result_cuotas.jsp">	</liferay-util:include>
			</tr>
			<tr>
			<td>
			</td>
			</tr>
		</table>
	</fieldset>
</div>
</c:if>

<td colspan="1">&nbsp;</td>
		
<c:if test="<%=(!esView)%>">		
	<c:if test="<%=(detalleCuota.getId_Reclamo() == 0)%>">
		<td colspan="3">
			<div id="<portlet:namespace />div_boton_reclamos_prestaciones_cuota">
				<input type="button" value="Ver Prestaciones del Reclamo Prestacional"
					onClick="<portlet:namespace />ver_reclamo_prest_cuota(<%=marca_rein_liq%>);return false;" />		
			</div>
			<div align="center" id="<portlet:namespace />div_boton_oculta_reclamos_prestaciones_cuota">
				<input type="button" value="Oculta Prestaciones del Reclamo Prestacional" onClick="<portlet:namespace />oculta_reclamo_prest_cuota();" />
			</div>	
		</td>
	</c:if>
</c:if>  

<c:if test="<%=(detalleCuota.getId_Reclamo() > 0)%>">
	<td colspan="1">
		<div align="right">
			<b><label class="id-reclamo">Id Reclamo: <%= detalleCuota.getId_Reclamo() %></label></b>	
		</div>
	</td>		
</c:if> 
	
<td colspan="3"><input id="<portlet:namespace />cuota_id_reclamo"
	name="<portlet:namespace />cuota_id_reclamo" size="11" maxlength="15"
	type="hidden" 
	value="<%= detalleCuota.getId_Reclamo() %>"
	readonly="readonly"
	/>
<input id="<portlet:namespace />cuota_id_reclamo_prestaciones"
	name="<portlet:namespace />cuota_id_reclamo_prestaciones" size="11" maxlength="15"
	type="hidden" 
	value="<%= detalleCuota.getId_ReclamoPrestaciones() %>"
	readonly="readonly"
	/>
</td>
	
<fieldset class="block-labels"><legend>Cuota <%=cuota%></legend> 	
	<%if(true){ %>
		 
<table class="lfr-table">
	<tr>
		<td colspan="1"><label><liferay-ui:message key="date" />:</label></td>
		<td colspan="3"><liferay-ui:input-date dayParam="fechaDiaC"
			dayValue="<%= fechaHoy.get(Calendar.DATE)%>"
			monthParam="fechaMesC"
			monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
			yearParam="fechaAnioC"
			yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
			disabled="<%=esView%>" />
		</td>
		<td><label><liferay-ui:message key="periodo" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="periodoDiaC"
			dayNullable="<%= true %>" 
			dayValue=""
			monthAndYearParam="periodoMesAnioC"
			monthValue="<%= periodo.get(Calendar.MONTH) %>"
			monthAndYearNullable="<%= false %>"
			yearValue="<%= periodo.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodo.get(Calendar.YEAR) - 100 %>"
			yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 100 %>"
			firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
			disabled="<%=esView%>" />
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label>N. Cuota:</label></td>
		<td>
		<input id="<portlet:namespace />nro_cuota"
				name="<portlet:namespace />nro_cuota" size="3" maxlength="8"
				type="text"
				value="<%= cuota %>"
				readonly="readonly" 
				/>
		</td>
						
		<td colspan="1"><label>Porcentaje:</label></td>
		<td>
		<input id="<portlet:namespace />porcentaje"
				name="<portlet:namespace />porcentaje" size="6" maxlength="8"
				type="text"
				value="<%= detalleCuota.getPorcentaje() %>%"
				readonly="readonly" 
				/>
		</td>
		<td colspan="1"><label>Importe Cuota:</label></td>
		<td>
		
		<%-- readonly="readonly"
		     type="text" 
		--%>
		<input id="<portlet:namespace />importe_cuota"
				name="<portlet:namespace />importe_cuota" size="10" maxlength="8"
				type="text"
				onchange="validaImporte();"	
				value="<%= detalleCuota.getImporte() %>"
				readonly="readonly"
				<%-- if (esView) { %> <%="readonly='readonly'" %> <%}--%> 
				/>
		</td>
		
	</tr>
	
	<tr>
		<td colspan="1">&nbsp;</td>
		<td colspan="11">
			<label id="label_acum_cuota"></label>
		</td>
	</tr>
	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
<c:if test="<%= Integer.valueOf(cuota) == 1 %>">
	<tr>
		<td colspan="1"><label>Diagnóstico:</label></td>
		<td colspan="9"><textarea rows="2" cols="80" id="<portlet:namespace />diagnostico"
			name="<portlet:namespace />diagnostico" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getDiagnostico() != null? detalleCuota.getDiagnostico() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>		
		<td colspan="1"><label>P. Tratamiento:</label></td>
		<td colspan="9"><textarea rows="2" cols="80" id="<portlet:namespace />plan_tratamiento"
			name="<portlet:namespace />plan_tratamiento" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getPlan_tratamiento() != null? detalleCuota.getPlan_tratamiento() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="1"><label>T. Estimado:</label></td>
			<td colspan="9"><textarea rows="2" cols="80" id="<portlet:namespace />tiempo_estimado"
			name="<portlet:namespace />tiempo_estimado" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getTiempo_estimado() != null? detalleCuota.getTiempo_estimado() : "" %></textarea>
		</td>
	</tr>	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label>Pronostico:</label></td>
			<td colspan="9"><textarea rows="1" cols="80" id="<portlet:namespace />pronostico"
			name="<portlet:namespace />pronostico" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getPronostico() != null? detalleCuota.getPronostico() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>		
	<tr>
		<td colspan="1"><label>Aparatología:</label></td>
			<td colspan="9"><textarea rows="1" cols="80" id="<portlet:namespace />informe"
			name="<portlet:namespace />informe" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getInforme() != null? detalleCuota.getInforme() : "" %></textarea>
		</td>
	</tr>	
</c:if>
<c:if test="<%= Integer.valueOf(cuota) != 1 %>">	
	<tr>
		<td colspan="1"><label>Informe:</label></td>
			<td colspan="9"><textarea rows="5" cols="80" id="<portlet:namespace />informe"
			name="<portlet:namespace />informe" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleCuota != null && detalleCuota.getInforme() != null? detalleCuota.getInforme() : "" %></textarea>
		</td>
	</tr>
</c:if>	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>		
		<td><label><liferay-ui:message key="comprobante" />:</label></td>
		<td><select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo"
			<% if (esView) { %> 
				disabled="disabled"
			<%} %>>
			<option value="">Seleccione </option>
			<option value="FCP" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getCompro_a_debitar_tipo()) ? (detalleCuota.getCompro_a_debitar_tipo().equals("FCP") ? "selected" : "") : ""%>>FCP</option>
			<option value="RCB" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getCompro_a_debitar_tipo()) ? (detalleCuota.getCompro_a_debitar_tipo().equals("RCB") ? "selected" : "") : ""%>>RCB</option>
			<option value="OTR" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getCompro_a_debitar_tipo()) ? (detalleCuota.getCompro_a_debitar_tipo().equals("OTR") ? "selected" : "") : ""%>>OTRO</option>
			</select> 
			&nbsp; 
			<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra"
			<% if (esView) { %> 
				disabled="disabled"
			<%} %>>
			<option value="">Seleccione </option>
			<option value="A" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getComproaDebitarLetra()) ? (detalleCuota.getComproaDebitarLetra().equals("A") ? "selected" : "") : "" %> >A</option>
			<option value="B" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getComproaDebitarLetra()) ? (detalleCuota.getComproaDebitarLetra().equals("B") ? "selected" : "") : "" %> >B</option>
			<option value="C" <%=Validator.isNotNull(detalleCuota) && Validator.isNotNull(detalleCuota.getComproaDebitarLetra()) ? (detalleCuota.getComproaDebitarLetra().equals("C") ? "selected" : "") : "" %> >C</option>
			</select> 
			&nbsp;  
			<input id="<portlet:namespace />comprobante_sucu"
				name="<portlet:namespace />comprobante_sucu" size="6" maxlength="11"
				type="text"
				value="<%= Validator.isNotNull(detalleCuota) ? detalleCuota.getComproaDebitarSucursal() : "" %>" 
				<% if (esView) { %>
				readonly="readonly"
				<%} %>/>
			&nbsp;
			<input id="<portlet:namespace />comprobante_nro"
				name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
				type="text"
				value="<%= Validator.isNotNull(detalleCuota) ? detalleCuota.getCompro_a_debitar_numero() : "" %>" 
				<% if (esView) { %>
				readonly="readonly"
				<%} %>/>				
		</td>
		<td colspan="1"><label>Estado:</label></td>
		<td colspan="1">
			<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado"
					disabled="disabled">				
				<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>" <%=detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO ? "selected" : ""  %>>Autorizado</option>
				<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>" <%=detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE ? "selected" : ""  %>>Pendiente</option>
				<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO%>" <%=detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO ? "selected" : ""  %>>Auditado</option>
				<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO%>" <%=detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO ? "selected" : ""  %>>Rechazado</option>				
			</select>
		</td>				
	</tr>
	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
	<td>
	<label>Nro Reintegro:</label>
	</td>
	<td colspan="1">
		<input size="6" type="text" name="<portlet:namespace/>id_rtgro" id="<portlet:namespace/>id_rtgro"
				readonly="readonly" value="<%=detalleCuota.getId_reintegro_user() == 0 ? "" : detalleCuota.getId_reintegro_userString() %>"/>
		&nbsp;&nbsp;&nbsp;<c:if test="<%= Validator.isNotNull(reintegro) && reintegro.getIdOP() != 0 %>">		
		<label>OP: <%=reintegro.getIdOP() + " / " + reintegro.getFechaOP().toString()%></label>
			</c:if>						
	</td>	
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="1">
<%	if (!esView && showABMButtons && (detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO || detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE)) { 
%>
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarCuota();" />
<%  }%>
		</td>
	<%}
	
	  if (!esView && showABMAuditorButtons
			&& detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO) {		
		int cuotax = Integer.valueOf(cuota) - 1;
		boolean auditable = false;
		if (cuotax >= 1) {
			DetalleCuota detalleCuotax = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, Integer.valueOf(cuotax));
			auditable = detalleCuotax.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO;
		}
		if (cuotax == 0) {
			auditable = true;	
		}
		if (auditable) {		
		%>
		<td><input type="button"
			value="Auditar"
			onClick="<portlet:namespace />auditarCuotaEntry();return false;" />&nbsp;
		</td>		
			<td><input type="button"
			value="Rechazar"
			onClick="<portlet:namespace />rechazarCuotaEntry();return false;" />&nbsp;
		</td>
	<%
		}
			}
	  if (!esView && showABMAuditorButtons && detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO && !detalleCuota.isPaga()) {
	  		int cuotax2 = Integer.valueOf(cuota) + 1;
			boolean desauditable = false;
			if (cuotax2 <= 3) {
				if (detalleCuotas.size() > 1){
					DetalleCuota detalleCuotax = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, Integer.valueOf(cuotax2));				
					desauditable = detalleCuotax.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO;
				} else {
					desauditable = true;
				}
			}
			if (cuotax2 == 4) {
				desauditable = true;	
			}
			if (desauditable) {
			%>
		<td><input type="button"
			value="Desauditar"
			onClick="<portlet:namespace />desauditarCuotaEntry();return false;" />
		</td>
		&nbsp;
	<%
		}
	}
	  if (!esView && showABMAuditorButtons
				 && detalleCuota.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO) {
				 
			int cuotax = Integer.valueOf(cuota) - 1;
			boolean auditable = false;
			if (cuotax >= 1) {
				if (detalleCuotas.size() > 1){
					DetalleCuota detalleCuotax = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, Integer.valueOf(cuotax));
					auditable = detalleCuotax.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO;
				} else {
					auditable = true;
				}
			}
			if (cuotax == 0) {
				auditable = true;	
			}		
			if (auditable) {
				 %>	
			<td><input type="button"
				value="Auditar"
				onClick="<portlet:namespace />auditarCuotaEntry();return false;" />
			</td>
			&nbsp;
			<%} 
		}	  
		%>
	</tr>
	<tr>
		<input id="label_acum_cuota_rest" type="hidden" size="8" maxlength="8" readonly="readonly" 
		       value=<%= String.valueOf(ReintegroServiceUtil.getImporteRestanteCuota(detalleCuotas, Integer.valueOf(cuota)))%>></input>
		<input class="total-prest" id="<portlet:namespace />importe_cuota_tot_prest"
			   name="<portlet:namespace />importe_cuota_tot_prest" size="8" maxlength="8"
			   type="hidden" value="<%= totalPrestacion %>"
			   readonly="readonly"/>
	
	</tr>	
	
</table>

<div align="center" id="<portlet:namespace />buscandoCuota">
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
	jQuery('#<portlet:namespace />buscandoCuota').hide();
	<portlet:namespace />hideDayFieldOfPeriodFieldscuota ();
	<portlet:namespace />oculta_reclamo_prest_cuota();
	<portlet:namespace />calc_porcentaje();
	
	function <portlet:namespace />hideDayFieldOfPeriodFieldscuota () {
		jQuery("#<portlet:namespace />periodoDiaC").hide();
	}
	
	function <portlet:namespace />calc_porcentaje() {
		var _importe  = Number(jQuery("#<portlet:namespace />importe_cuota").val());
		var total = Number(jQuery("#<portlet:namespace />importe_cuota_tot_prest").val());

		var _porcentaje = Number(0);
		if (Number(total) > 0) {
			_porcentaje = ((_importe / total) * 100).toFixed(2);
		}
		jQuery("#<portlet:namespace />porcentaje").val(_porcentaje + "%");
	}
	
	function validaValorImporte() {
		var _imp  = jQuery("#<portlet:namespace />importe_cuota").val();
		var total = jQuery("#<portlet:namespace />importe_cuota_tot_prest").val();
		var acum = jQuery("#label_acum_cuota_rest").val();
				
		if ((_imp == null) || ( _imp == ''))
	    {
	    	alert('Debe ingresar un valor numerico');
	        return false;
	    }	    
		
		var _importe = Number(_imp);
		
		if (_importe > 0) {
			
			<portlet:namespace />calc_porcentaje();
			
			if (_importe <= total) {
				
				var acumTot = parseFloat(acum) + parseFloat(_importe);
				jQuery("#label_acum_cuota").text("Total acumulado de cuotas: " + Number(acumTot).toFixed(2) + " sobre un máximo de: " + total);
				jQuery("#label_acum_cuota").removeClass("colorWarning").addClass("colorOK")
				
				if (acumTot > total) {
					var msg = ("Atención: Total acumulado de cuotas: " + Number(acumTot).toFixed(2) + " sobre un máximo de: " + total);
					jQuery("#label_acum_cuota").text(msg);
					jQuery("#label_acum_cuota").removeClass("colorOK").addClass("colorWarning")
					alert(msg);
				   	return false;
				}
				
			} else {
				alert('El monto no debe superar al total del tratamiento');
			   	return false;
			}
			
		} else {
		   	alert('Debe ingresar un monto de cuota mayor a cero que no supere al total del tratamiento');
		   	return false;
		}
		return true;
	}
	
	function validaImporte(){
		if (!validaValorImporte()){		 
	    	jQuery("#<portlet:namespace />importe_cuota").focus();	    		  
	    	return false;
		}
		return true;
	} 

	//EDITAR LOS CONTENIDOS DE ESTAS FUNCIONES
	function <portlet:namespace />grabarCuota(){
				
		if(!<portlet:namespace />validarDatosCuota()){
			return false;
		}else{
			
			jQuery('#<portlet:namespace />buscandoCuota').show();

			var id_reitnegro = <%=id_reitnegro%>;
			var id_reitnegro_user = <%=detalleCuota.getId_reintegro_userString()%>;
			var cuota = <%=cuota%>;
			var diaPer = document.getElementById("<portlet:namespace />fechaDiaC").value;
			var mesPer = document.getElementById("<portlet:namespace />fechaMesC").value; 
			var anioPer = document.getElementById("<portlet:namespace />fechaAnioC").value;
			var periodo = document.getElementById("<portlet:namespace />periodoMesAnioC").value;
			
			
			try {
				var id_reclamo = document.getElementById("<portlet:namespace />cuota_id_reclamo").value;
				var id_reclamo_prestaciones = document.getElementById("<portlet:namespace />cuota_id_reclamo_prestaciones").value;
			} catch (err) {}						
			
			var diagnostico = '';
			var plan_tratamiento = '';
			var tiempo_estimado = '';
			var pronostico = '';			
			try {
				diagnostico = document.getElementById("<portlet:namespace />diagnostico").value;
				plan_tratamiento = document.getElementById("<portlet:namespace />plan_tratamiento").value;
				tiempo_estimado = document.getElementById("<portlet:namespace />tiempo_estimado").value;
				pronostico = document.getElementById("<portlet:namespace />pronostico").value;
			} catch (err) {}
			
			
			var informe = document.getElementById("<portlet:namespace />informe").value;
			var comprobante_tipo = document.getElementById("<portlet:namespace />comprobante_tipo").value;
			var comprobante_letra = document.getElementById("<portlet:namespace />comprobante_letra").value;
			var comprobante_sucu = document.getElementById("<portlet:namespace />comprobante_sucu").value;
			var comprobante_nro = document.getElementById("<portlet:namespace />comprobante_nro").value;
			
			var importe_cuota = document.getElementById("<portlet:namespace />importe_cuota").value;
			var porcentaje_cuota = document.getElementById("<portlet:namespace />porcentaje").value;
						
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/grabar_cuota&id_reitnegro='+id_reitnegro+'&cuota='+cuota+
			'&diaPer='+diaPer+'&mesPer='+mesPer+'&anioPer='+anioPer+'&periodo='+periodo+'&diagnostico='+encodeURI(diagnostico)+'&plan_tratamiento='+encodeURI(plan_tratamiento)+
			'&tiempo_estimado='+encodeURI(tiempo_estimado)+'&pronostico='+encodeURI(pronostico)+'&informe='+encodeURI(informe)+
			'&comprobante_tipo='+comprobante_tipo+'&comprobante_letra='+comprobante_letra+'&comprobante_sucu='+comprobante_sucu+'&comprobante_nro='+encodeURI(comprobante_nro)+
			'&id_reitnegro_user='+id_reitnegro_user+'&id_reclamo='+id_reclamo+'&id_reclamo_prestaciones='+id_reclamo_prestaciones+
			'&importe_cuota='+importe_cuota+'&porcentaje_cuota='+porcentaje_cuota;
					
			
			jQuery('#<portlet:namespace />cuota_resultado').load(url, function() {
																			jQuery('#<portlet:namespace />buscandoCuota').hide();            															
																			   });
		}
	}
	
	function <portlet:namespace />validarDatosCuota(){
		var sinError = true;
		//valida comprobante obligatorio
		var comprobante_tipo = document.getElementById("<portlet:namespace />comprobante_tipo").value;
		var comprobante_letra = document.getElementById("<portlet:namespace />comprobante_letra").value;
		var comprobante_sucu = document.getElementById("<portlet:namespace />comprobante_sucu").value;
		var comprobante_nro = document.getElementById("<portlet:namespace />comprobante_nro").value;
		
		if (!validaImporte()){
			sinError = false;
		}
		
		if(trim(comprobante_tipo).length == 0){
			alert("<liferay-ui:message key='comprobante-obligatorio' />");
			jQuery('#<portlet:namespace />comprobante_tipo').focus();
			sinError = false;
		}
		if(trim(comprobante_letra).length == 0){
			alert("<liferay-ui:message key='comprobante-obligatorio' />");
			jQuery('#<portlet:namespace />comprobante_letra').focus();
			sinError = false;
		}
		if(trim(comprobante_sucu).length == 0){
			alert("<liferay-ui:message key='comprobante-obligatorio' />");
			jQuery('#<portlet:namespace />comprobante_sucu').focus();
			sinError = false;
		}
		if(trim(comprobante_nro).length == 0){
			alert("<liferay-ui:message key='comprobante-obligatorio' />");
			jQuery('#<portlet:namespace />comprobante_nro').focus();
			sinError = false;
		}
		return sinError;
	}

	function <portlet:namespace />auditarCuotaEntry() {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-auditar-this-reintegro'/>")){
			return false;
		}else{
			var id_reitnegro = <%=id_reitnegro%>;			
			var cuota = <%=cuota%>;
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/cambiar_estado_cuota&id_reitnegro='+id_reitnegro+'&cuota='+cuota+
			'&estado='+<%= WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO  %>			
			jQuery('#<portlet:namespace />cuota_resultado').load(url, function() {
																					jQuery('#<portlet:namespace />buscandoCuota').hide();            															
																			   }
															   );			
		}		
	}

	function <portlet:namespace />rechazarCuotaEntry() {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-rechazar-this-reintegro'/>")){
			return false;
		}else{
			var id_reitnegro = <%=id_reitnegro%>;			
			var cuota = <%=cuota%>;			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/cambiar_estado_cuota&id_reitnegro='+id_reitnegro+'&cuota='+cuota+
			'&estado='+<%= WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO  %>			
			jQuery('#<portlet:namespace />cuota_resultado').load(url, function() {
																					jQuery('#<portlet:namespace />buscandoCuota').hide();            															
																			   }
															   );			
		}
	}

	function <portlet:namespace />desauditarCuotaEntry() {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-desauditar-this-reintegro'/>")){
			return false;
		}else{
			var id_reitnegro = <%=id_reitnegro%>;			
			var cuota = <%=cuota%>;
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/cambiar_estado_cuota&id_reitnegro='+id_reitnegro+'&cuota='+cuota+
			'&estado='+<%= WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO  %>

			jQuery('#<portlet:namespace />cuota_resultado').load(url, function() {
																					jQuery('#<portlet:namespace />buscandoCuota').hide();            															
																			   }
															   );			
		}
	}
	
	function <portlet:namespace />ver_reclamo_prest_cuota(pMarcaReinLiq) {
				
		var params;		
		// En la solapa de Reintegros (prestacionales) se liquida todo lo que es 
		// Odontología General, marcado en el nomenclador con marca_rein_liq=3
		// Protesis con marca_rein_liq=4
		// Ortopedia-Odontologica=5
				
		params = { "cuil":<%=cuil%>,  "inte":<%=inte%> ,"reintegro":true, "marca_rein_liq": <%=marca_rein_liq%>, "viene_de_cuotas": true };
		
		// alert("Marca Rein Liq " + params.marca_rein_liq + " cuil: " + params.cuil + " inte " + params.inte);
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_prestaciones_reclamos_reintegros" /></portlet:renderURL>';
		
		jQuery('#<portlet:namespace />div_reclamos_prestaciones_cuota').load(url,params, function(){
			jQuery('#<portlet:namespace />buscando').hide();            															
		});
		jQuery("#<portlet:namespace />div_reclamos_prestaciones_cuota").show();
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones_cuota").hide();
		jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_cuota").show();
		
	}
	
	function <portlet:namespace />oculta_reclamo_prest_cuota(){
		
		jQuery("#<portlet:namespace />div_reclamos_prestaciones_cuota").hide();
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones_cuota").show();
		jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones_cuota").hide();
    }


</script>
<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

String id_liquidacion = request.getParameter("id_liquidacion") != null ? request.getParameter("id_liquidacion") : "" ;
String view = (String)request.getParameter("view");
String periodoReq = (String)request.getParameter("periodo") != null ? request.getParameter("periodo") : " "; 

boolean esView = true;
if(null==view || !view.equals("true")){
	esView = false;
}

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
LiquidacionDebitoTercero liquidacionDebitos = null;

if (id_liquidacion.length() > 0) {
	liquidacionDebitos = LiquidacionDebitosActionUtil.getLiquidacionDebitosEntry(Integer.valueOf(id_liquidacion));	
} else {
	id_liquidacion = "0";
}

Date periodoLiq = null;
Calendar periodo = CalendarFactoryUtil.getCalendar();
periodoLiq = Validator.isNotNull(liquidacionDebitos)? liquidacionDebitos.getPeriodoHasta() : null;
if (periodoLiq == null) {	
	SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.PERIODO);
	periodo.setTime(dateFormat.parse(periodoReq));
}
else{
	periodo.setTime(liquidacionDebitos.getPeriodoHasta());
}

%>
<portlet:defineObjects />

<fieldset class="block-labels"><legend>Observaciones:</legend>
<table class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="periodo" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
			dayNullable="<%= true %>" 
			dayValue=""
			monthAndYearParam="periodoMesAnio"
			monthValue="<%= periodo.get(Calendar.MONTH) %>"
			monthAndYearNullable="<%= false %>"
			yearValue="<%= periodo.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodo.get(Calendar.YEAR) - 100 %>"
			yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 100 %>"
			firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
			disabled="<%=true%>" />
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label>Observaciones:</label></td>
		<td colspan="9"><textarea rows="2" cols="80" id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= liquidacionDebitos != null && liquidacionDebitos.getObservaciones() != null? liquidacionDebitos.getObservaciones() : "" %></textarea>
		</td>
		<input type="hidden" id="<portlet:namespace />id_liquidacion"
			name="<portlet:namespace />id_liquidacion" value="<%= liquidacionDebitos != null && liquidacionDebitos.getId_liquidacionString() != null? liquidacionDebitos.getId_liquidacionString() : "0" %>">
		<input type="hidden" id="<portlet:namespace />importe_total"
			name="<portlet:namespace />importe_total" value="<%= liquidacionDebitos != null && liquidacionDebitos.getImporte_total() != null? liquidacionDebitos.getImporte_total().toPlainString() : "0" %>">
		
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="1">		
<%	if (!esView && showABMButtons) {
%>
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabar();" />
<%  }%>
		</td>
		<td>
			<input type="button" value="Ver Detalle en Hoja de Cálculo"
					onClick="<portlet:namespace />imprimirHC();return false;" /></td>
		<td>&nbsp;</td>
		<c:if test="<%=Validator.isNotNull(liquidacionDebitos)%>">
			<td><input type="button" value="Imprimir Nota Débito"
				onClick="<portlet:namespace />imprimirND();return false;" /></td>
		</c:if>
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

<div align="center" id="<portlet:namespace />cuota_resultado">
	<jsp:include
		page='tercerizadoras_nota_debito_search_result.jsp'>
		<jsp:param name="view" value="<%=view%>" />
	</jsp:include></div>

</fieldset>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoCuota').hide();
	<portlet:namespace />hideDayFieldOfPeriodFieldscuota();

	function <portlet:namespace />hideDayFieldOfPeriodFieldscuota () {
		jQuery("#<portlet:namespace />periodoDia").hide();
	}

	function <portlet:namespace />grabar(){
		if(!<portlet:namespace />validarDatos()){
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoCuota').show();
			var id_liquidacion = document.getElementById("<portlet:namespace />id_liquidacion").value;
			document.getElementById("<portlet:namespace />periodoMesAnio").disabled = false;
			var periodo = document.getElementById("<portlet:namespace />periodoMesAnio").value;
			var observaciones = '';
			try {
				observaciones = document.getElementById("<portlet:namespace />observaciones").value;
			} catch (err) {}
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/grabar_liquidacion_ndt&id_liquidacion='+id_liquidacion+'&observaciones='+encodeURI(observaciones)+'&periodo='+periodo;								

			jQuery('#<portlet:namespace />cuota_resultado').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoCuota').hide();            															
																			   }
															   );
		}
	}
	
	function <portlet:namespace />validarDatos(){
		var sinError = true;
		//valida comprobante obligatorio
		var observacion = document.getElementById("<portlet:namespace />observaciones").value;		
		if(trim(observacion).length == 0){
			alert("Observación obligatoria");
			jQuery('#<portlet:namespace />observacion').focus();
			sinError = false;
		}
		return sinError;
	}

	function <portlet:namespace />imprimirND(){
		var importe_total = document.getElementById("<portlet:namespace />importe_total").value;		
		window.location.href ='/pdfservlet/?accion=<%="notaDebitoLiquidacion"%>&id_liquidacion=<%=id_liquidacion%>&terceros=1&importe_terceros='+importe_total;		
	}

	function <portlet:namespace />imprimirHC(){
		document.getElementById("<portlet:namespace />periodoMesAnio").disabled = false;
		var periodo = document.getElementById("<portlet:namespace />periodoMesAnio").value;
		document.getElementById("<portlet:namespace />periodoMesAnio").disabled = true;		
		window.location.href ='/xlsservlet/?reporte=DETALLE_lIQUIDACION_DEBITOS_TERCEROS'+'&periodo='+periodo+'&id_liquidacion=<%=id_liquidacion%>';		
	}	
</script>
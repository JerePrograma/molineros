<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<% 
Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

boolean esEdicion = false;

if (request.getAttribute(WebKeysTesoreria.RECIBOS_ACTION_EDICION) != null || recibo == null) {
	esEdicion = true;
}

if (recibo !=null && recibo.getId() != 0){
	esEdicion = false;
}

Calendar current = CalendarFactoryUtil.getCalendar();
Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
if (recibo!=null && recibo.getFecha() != null){
	fechaInicio.setTime(recibo.getFecha());
} else {
	fechaInicio.setTime(new Date());
}

%>
<liferay-ui:error exception="<%= DuplicateNumeroReciboException.class %>" message="numero-recibo-existente" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="ingreso-menor-fecha-contable" />

<script type="text/javascript">
function sumarConceptos(){
	var idsActas = document.getElementById("ids_actas").value.split(";");
	var idsConvenios = document.getElementById("ids_convenios").value.split(";");
	var totalChequesRechazados = document.getElementById("total_cheques_rechazados").value;	
	var totalChequesASustituir = document.getElementById("total_cheques_no_depositados").value;	
	var totalOtros = document.getElementById("total_otros").value;	
	var capitalAnticiposTmp = document.getElementById("capitalAnticiposTmp").value;	

	var total = 0.0;
	if (idsActas.length >1){
		for (var i = 0; i<idsActas.length-1; i++){
			if (IsNumeric(trim(document.getElementById("total_acta_" + idsActas[i]).value))){
				total =  Math.round((total  + Math.round(parseFloat(document.getElementById("total_acta_" + idsActas[i]).value) * 100) /100)*100)/100;
			}
		}
	}

	if (idsConvenios.length >1){
		for (var i = 0; i<idsConvenios.length-1; i++){
			if (IsNumeric(trim(document.getElementById("total_convenio_" + idsConvenios[i]).value))){
				total =  Math.round((total  + Math.round(parseFloat(document.getElementById("total_convenio_" + idsConvenios[i]).value) * 100) /100)*100)/100;
			}
		}
	}
	
	total =  Math.round(
			(total  + Math.round(parseFloat(totalChequesRechazados) * 100) /100 
			+  Math.round(parseFloat(totalChequesASustituir) * 100) /100 
			+ Math.round(parseFloat(totalOtros) * 100) /100
			+  Math.round(parseFloat(capitalAnticiposTmp) *100)/100)*100 )/100;
	document.getElementById("<portlet:namespace />total_conceptos").value =  total;
	agregarCeros(document.getElementById("<portlet:namespace />total_conceptos"));
}
</script>

<fieldset class="block-labels"><legend><liferay-ui:message	key="datos-ingreso" /></legend>
<table class="lfr-table">
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="entidad" />:</label></td>
			<td>
				<select name="<portlet:namespace/>entidad_bla" id="<portlet:namespace/>entidad_bla">
					<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
						<option value="<%= entidad %>"><%=entidad%></option>						
					<%}%>
				</select>
			</td>
			<td><label><liferay-ui:message key="id-ingreso" />:</label></td>
			<td><input type="text" name="<portlet:namespace />recibo_numero" id="<portlet:namespace />recibo_numero" maxlength="12" 
							value="<%= recibo!=null ? (recibo.getNumero()!=null&&!recibo.getNumero().trim().equals("") ? recibo.getNumero():(recibo.getId()!=0?recibo.getId():"aaa")):""%>"
							<%if (!esEdicion) { %>readonly="readonly"<%} %>/>&nbsp;
							<%if (recibo == null || recibo.getId() == 0) {%><input type="checkbox" id="anulado" name="anulado" value="anulado"/>Crear Anulado<%} %></td>
			<td><label><liferay-ui:message key="fecha-emision" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaInicioDia"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaInicioMes"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion   %>" />
			</td>			
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<tr>
		<td><label><liferay-ui:message key="descripcion" />:</label></td>
		<td colspan="5"><textarea <%if (!esEdicion) { %>readonly="readonly"<%} %> rows="7" id="<portlet:namespace />obs" name="<portlet:namespace />obs" cols="100"><%=recibo != null && recibo.getObservaciones() != null ? recibo.getObservaciones() : "" %></textarea> </td>
		</tr>
</table>
</fieldset>
<table class="lfr-table" width="100%">
	<tr>
		<td width="100%" valign="top">
			<liferay-util:include page="/html/portlet/uoma/recibos/recibo_no_os_conceptos.jsp">
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<input type="hidden" id="capitalAnticiposTmp" value="0.00"/>
			<td width="100%">
				<liferay-util:include page="/html/portlet/uoma/recibos/recibos_no_os_anticipo_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
	</tr>
	<tr>
		<td>	
			<liferay-ui:message	key="total-conceptos" />:&nbsp;<input id="<portlet:namespace />total_conceptos" name="<portlet:namespace />total_conceptos" type="text" readonly="readonly" />
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
</table>
<table class="lfr-table" width="100%">
		<tr>
		<input type="hidden" id="capitalIngresoTmp" value="0.00"/>
			<td width="100%">
				<liferay-util:include page="/html/portlet/uoma/recibos/formas_ingreso_no_os_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message	key="total-ingresos" />:&nbsp;<input id="<portlet:namespace />total_ingresos" type="text" readonly="readonly" />
			</td>
		</tr>
</table>
<% if (esEdicion) { %> 
<br />
<input type="submit" value="<liferay-ui:message key="crear-ingreso" />" onClick="<portlet:namespace />saveRecibo();return false;"/>
<%} else { 
  	if(!renderResponse.getNamespace().equals("_EST_1_")) {%>
		<br />
		<input type="submit" value="<liferay-ui:message key="crear-nuevo-recibo" />" onClick="<portlet:namespace />nuevoRecibo();return false;"/>
	<%}
} %>


				
<script type="text/javascript">
	function <portlet:namespace />saveRecibo() {
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />rec.<portlet:namespace /><%= Constants.CMD %>.value =  "<%=(recibo == null || recibo.getId() == 0 ? Constants.ADD : Constants.UPDATE) %>";
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_recibos_no_os_entry" /></portlet:actionURL>';
			document.<portlet:namespace />rec.method = 'post';
			submitForm(document.<portlet:namespace />rec, url);
		}
	}     
	
	function <portlet:namespace />validarCampos() {
		 if (document.getElementById("anulado").checked){
			 return true;
		 }
		try{
			if (trim(document.getElementById("<portlet:namespace />cuit_entidad").value) == "" || 
					trim(document.getElementById("<portlet:namespace />sucursal_entidad").value) == "" ){
				alert("Debe seleccionar una empresa");
				return false;
			}

			/*if (trim(document.getElementById("<portlet:namespace />recibo_numero").value) == "") {
				alert("Debe ingresar el numero de recibo");
				return false;
			} */

			if (parseFloat(document.getElementById("<portlet:namespace />total_conceptos").value) != parseFloat(document.getElementById("<portlet:namespace />total_ingresos").value)) {
				alert("El Capital Total de Conceptos debe ser igual al Capital Total por Ingresos.");
				return false;
			}
			
		} catch (err) {
			return false;
		}
		return true;
	}

	function setearCapitalIngreso(){
		document.getElementById("<portlet:namespace />total_ingresos").value =document.getElementById("capitalIngresoTmp").value;
	}	
	
	sumarConceptos();
	setearCapitalIngreso();
	
	function <portlet:namespace />nuevoRecibo(){
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/estudio_isidro/editar_recibos_no_os_entry" /></portlet:renderURL>';
		document.<portlet:namespace />rec.method = 'post';
		submitForm(document.<portlet:namespace />rec, url);
	}
	
	
	function actualizarConceptos(){
		var fechaDia  = document.getElementById("<portlet:namespace />fechaInicioDia");
		var fechaMes= document.getElementById("<portlet:namespace />fechaInicioMes");
		var fechaAnio = document.getElementById("<portlet:namespace />fechaInicioAnio");
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/traer_conceptos_ingreso_para_fecha';
		url +='&fechaDia='+fechaDia.value;
		url +='&fechaMes='+fechaMes.value;
		url +='&fechaAnio='+fechaAnio.value;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#<portlet:namespace />otro_concepto').find('option').remove();
				jQuery('#<portlet:namespace />otro_concepto').append('<option value=""></option>');
				for(var i =0;i< obj.conceptos.length; i++){
					jQuery('#<portlet:namespace />otro_concepto').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}	
	
	jQuery(document).ready(function() {
	
		
		jQuery(".ui-datepicker-trigger").hide();
		jQuery('#<portlet:namespace/>fechaInicioMes').change(function(){
			
			actualizarConceptos();
		});
		jQuery('#<portlet:namespace/>fechaInicioAnio').change(function(){
			actualizarConceptos();
		});
	});
</script>


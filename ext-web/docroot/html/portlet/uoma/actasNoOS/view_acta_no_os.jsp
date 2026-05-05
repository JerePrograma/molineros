<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<% 

String portlet_name = null;

if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
}

if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS) || PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO)|| portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);

String cuit= (String)request.getSession().getAttribute("cuit");
String busqueda=(String)request.getAttribute("busqueda");

if (auditorActas){
	showABMButtons = true;
}
List<ActaEstadoSeguimiento> estadosSeguimActa = ActaServiceUtil.getEstadosSeguimientoActas();

Boolean fromDeuda=(Boolean) request.getAttribute("fromBusquedaDeuda");

Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

String entidad_acta=null!=acta?acta.getEntidad():"";
String estado_acta=null!=acta?acta.getEstado():"";

boolean esEdicion = false;

if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
	esEdicion = true;
}


Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Date fechaInicioActa = acta != null ? acta.getFechaInicio() : null; 
if (fechaInicioActa == null) {
	fechaInicio.setTime(new Date());
}
else{
	fechaInicio.setTime(acta.getFechaInicio());
}


Calendar fechaPago = CalendarFactoryUtil.getCalendar();
Date fechaPagoActa = acta != null ? acta.getFechaPago() : null; 
if (fechaPagoActa == null) {
	Calendar pago = Calendar.getInstance();
	pago.add(Calendar.MONTH, 1);
	fechaPago.setTime(pago.getTime());
} else{
	fechaPago.setTime(acta.getFechaPago());
}
Calendar current = CalendarFactoryUtil.getCalendar();

Calendar periodoIniCal= CalendarFactoryUtil.getCalendar();
Date periodoIni= acta!=null ? acta.getPeriodoInicial():null;

if (periodoIni == null) {
	Calendar pago = Calendar.getInstance();
	pago.add(Calendar.MONTH, 1);
	periodoIniCal.setTime(pago.getTime());
} else{
	periodoIniCal.setTime(acta.getPeriodoInicial());
}

Calendar periodoFinCal= CalendarFactoryUtil.getCalendar();
Date periodoFin= acta!=null ? acta.getPeriodoFinal():null;

if (periodoFin == null) {
	Calendar pago = Calendar.getInstance();
	pago.add(Calendar.MONTH, 1);
	periodoFinCal.setTime(pago.getTime());
} else{
	periodoFinCal.setTime(acta.getPeriodoFinal());
}

boolean esAdd = (acta == null || (request.getAttribute("accionOriginal") != null && request.getAttribute("accionOriginal").equals("add")))? true : false;
if (acta != null && acta.isActaCerrada() && !auditorActas){
	esEdicion = false;
}

Calendar cierreFecha = null;
if (acta != null && acta.getCierre_fecha() != null){
	cierreFecha = CalendarFactoryUtil.getCalendar();
	cierreFecha.setTime(acta.getCierre_fecha());
}
String sufi="acta_";

String guardado=null!=acta&&acta.getId()>0?"GUARDADO":"";
%>

<liferay-ui:error exception="<%= DuplicateActaIdException.class %>" message="acta-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.FaltaFechaCierreActaException.class %>" message="falta-fecha-cierre" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-menor-fecha-contable" />
<form action="" method="post" name="<portlet:namespace />actNoOS" >
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos" /></legend>
<table class="lfr-table">
		<tr>
			<td>
				<b><liferay-ui:message	key="empresa" />:</b>
			</td>
			<td colspan="9" width="100%">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
			  		<liferay-util:param name="esEditable" value='<%=String.valueOf(acta== null || acta.getId() == 0) %>'/>
			  		<liferay-util:param name="cuit" value='<%= (acta!=null && acta.getEmpresa() != null ? acta.getEmpresa().getCuit() :	cuit!=null?cuit:"") %>'/>
			  		<liferay-util:param name="sucu" value='<%= acta!=null && acta.getEmpresa() != null ? acta.getEmpresa().getSucursal() :"" %>'/>
			  		<liferay-util:param name="razon" value='<%=acta!=null && acta.getEmpresa() != null ? acta.getEmpresa().getRazon_soc() :"" %>'/>
			  		<liferay-util:param name="portlet_name" value='tesoreria'/>
			  		<liferay-util:param name="suf_entidad" value='<%=sufi%>'/>
					<liferay-util:param name="suf" value='<%=sufi%>'/>
				</liferay-util:include>
			</td>
		</tr>
		<tr><td colspan="10">&nbsp;</td></tr>
		<tr> 
			<td><label><liferay-ui:message key="entidad" />:</label></td>
			<td>				
				<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad" onChange="javascript:cambiarEntidad();">	
					<%if(!portlet_name.equals("farmacia")&&!portlet_name.equals("uoma")){%>
						<option selected value=""></option>
					<%}%>							
						<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
								<% if(portlet_name.equals("uoma") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>									
										<option value="<%= entidad %>" selected><%=entidad%></option>
								<%}else if(portlet_name.equals("farmacia") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)){%>
										<option value="<%= entidad %>" selected><%=entidad%></option>
								<%}else if(!portlet_name.equals("farmacia") && !portlet_name.equals("uoma") && !entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)){%>
									<%
									  if(entidad_acta.equals("")){%>
										   <option value="<%= entidad %>" selected><%=entidad%></option>
									  <%}else if(entidad_acta.equals("U.O.M.A.")){
										if(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>		
											<option value="<%= entidad %>" selected><%=entidad%></option>
										<%}
									  }else{ 
										  if(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) {%> 
											<option value="<%= entidad %>" selected><%=entidad%></option>
										<%}%>
									<%}%>
								<%}%>
						<%}%>									
				</select>
			</td>			
			<td><label><liferay-ui:message key="acta" />&nbsp;Nro:</label></td>
			<td><input id="<portlet:namespace />acta_numero" name="<portlet:namespace />acta_numero" size="13" maxlength="8" type="text" value="<%= acta != null ? acta.getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
			<td><label><liferay-ui:message key="fecha-acta" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaInicioDia"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaInicioMes"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 20%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion   %>" />
			</td>			
			<td><label><liferay-ui:message key="fecha-actualizacion" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaPagoDia"
				dayValue="<%=fechaPago.get(Calendar.DATE)%>" 
				monthParam="fechaPagoMes"
				monthValue="<%=  fechaPago.get(Calendar.MONTH)%>"				
				yearParam="fechaPagoAnio"
				yearValue="<%= fechaPago.get(Calendar.YEAR)%>"
				yearRangeStart="<%= current.get(Calendar.YEAR) -20 %>"	
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 20%>"
				firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion  %>" />
				&nbsp;
			</td>	
			<% if (esEdicion && (acta == null || !acta.isActaCerrada())){ %>
				<td><input type="button" value="<liferay-ui:message key="recalcular-intereses" />" onClick="<portlet:namespace />recalcularIntereses();" /></td>
			<%}else{ %>
				<td>&nbsp;</td>
			<%}%>		
		</tr>
		<tr>
			<td><label><liferay-ui:message key="estado" />:</label></td>
			<td colspan="2">
				<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
					<%for (String estado : WebKeysTesoreria.ESTADO_ACTAS_NO_OS) {	%>
								<option <%=estado_acta!=null&&estado_acta.trim().equals(estado.trim())?"selected":""%> value="<%= estado %>"><%=estado%></option>						
					<%}%>
				</select>
			</td>
			<td colspan="3">
				<label><liferay-ui:message key="estado-seguim" />:</label>
				<select name="<portlet:namespace />estado_seguim"
							id="<portlet:namespace />estado_seguim" ><%-- <% if (!esEdicion) { %> disabled="disabled" <%} %> --%>		
							<option value="0">--Sin estado de seguimiento--</option>
							<% for(ActaEstadoSeguimiento aes : estadosSeguimActa) {%>
								<option value="<%=aes.getId() %>"  
								<%= (acta != null && acta.getEstadoSeguimiento()!=null && acta.getEstadoSeguimiento().getId() == aes.getId())?"selected":""%>
								><%=aes.getDescripcion() %></option> 
							<%} %>
						</select>
			</td>
			<td>
				<img alt="Actualizar Estado Seguimiento" src="<%=themeDisplay.getPathThemeImages()+"/portlet/refresh.png"%>" onclick="javascript:actualizarEstadoSeguimiento();">
				<div id="<portlet:namespace />actuEstadoSegResult" style="display: none;" ><p>Se actualizó correctamente el estado del Acta</p></div>
			</td>
		</tr>	
</table>
</fieldset>
<table class="lfr-table" width="100%">
		<tr>
			<td colspan="3" width="50%" valign="top">
			<fieldset class="block-labels"><legend><liferay-ui:message
				key="inspectores-firmantes" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/inspectores_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</fieldset>
			<input type="hidden" value="" id="deudaActasTmp"/>
			<script type="text/javascript">
				function sumarTodo(){
					var deudaActasTmp = 0;
					var otros = 0;
					var subtotal = 0; 
					var inte = 0;
					if (document.getElementById("deudaActasTmp")!=null){
						var id = document.getElementById("deudaActasTmp").value.split(";");
						var total = 0.0;
						if (id.length >1){
							for (var i = 0; i<id.length-1; i++){
								if (IsNumeric(trim(document.getElementById("saldo_" + id[i]).value))){
									deudaActasTmp =  Math.round((deudaActasTmp  + Math.round(parseFloat(document.getElementById("saldo_" + id[i]).value) * 100) /100)*100)/100;
								}
							}
						}
						if (document.getElementById("<portlet:namespace />dedua") != null){
							document.getElementById("<portlet:namespace />dedua").value = deudaActasTmp;
						}
					}
					if (document.getElementById("<portlet:namespace />otros") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />otros").value)){
						otros = parseFloat(document.getElementById("<portlet:namespace />otros").value);
					}
					if (document.getElementById("<portlet:namespace />subtotal") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal").value)){
						subtotal = parseFloat(document.getElementById("<portlet:namespace />subtotal").value);
					}
					if (document.getElementById("<portlet:namespace />inte") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte").value)){
						inte = parseFloat(document.getElementById("<portlet:namespace />inte").value);
					}
					if (document.getElementById("<portlet:namespace />total")!= null){
						document.getElementById("<portlet:namespace />total").value = Math.round((Math.round(otros *100) / 100 + Math.round(deudaActasTmp *100) / 100 + Math.round(subtotal *100) / 100)*100)/100;
					}
				}
				
				function sumarTodoUOMA(){					
					var deudaActasTmp = 0;
					var otros = 0;
					var subtotal_sindicato = 0; 
					var inte_sindicato = 0;
					var subtotal_solidario = 0; 
					var inte_solidario = 0;
					var subtotal_usufructo = 0; 
					var inte_usufructo = 0;
					var subtotal_art46 = 0; 
					var inte_art46 = 0;
					
					if (document.getElementById("deudaActasTmp")!=null){
						var id = document.getElementById("deudaActasTmp").value.split(";");
						var total = 0.0;
						if (id.length >1){
							for (var i = 0; i<id.length-1; i++){
								if (IsNumeric(trim(document.getElementById("saldo_" + id[i]).value))){
									deudaActasTmp =  Math.round((deudaActasTmp  + Math.round(parseFloat(document.getElementById("saldo_" + id[i]).value) * 100) /100)*100)/100;
								}
							}
						}
						if (document.getElementById("<portlet:namespace />dedua") != null){
							document.getElementById("<portlet:namespace />dedua").value = deudaActasTmp;
						}
					}					
					if (document.getElementById("<portlet:namespace />subtotal_sindicato") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal_sindicato").value)){
						subtotal_sindicato = parseFloat(document.getElementById("<portlet:namespace />subtotal_sindicato").value);
					}
					if (document.getElementById("<portlet:namespace />inte_sindicato") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte_sindicato").value)){
						inte_sindicato = parseFloat(document.getElementById("<portlet:namespace />inte_sindicato").value);
					}
					if (document.getElementById("<portlet:namespace />subtotal_solidario") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal_solidario").value)){
						subtotal_solidario = parseFloat(document.getElementById("<portlet:namespace />subtotal_solidario").value);
					}
					if (document.getElementById("<portlet:namespace />inte_solidario") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte_solidario").value)){
						inte_solidario = parseFloat(document.getElementById("<portlet:namespace />inte_solidario").value);
					}
					if (document.getElementById("<portlet:namespace />subtotal_usufructo") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal_usufructo").value)){
						subtotal_usufructo = parseFloat(document.getElementById("<portlet:namespace />subtotal_usufructo").value);
					}
					if (document.getElementById("<portlet:namespace />inte_usufructo") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte_usufructo").value)){
						inte_usufructo = parseFloat(document.getElementById("<portlet:namespace />inte_usufructo").value);
					}
					if (document.getElementById("<portlet:namespace />subtotal_art46") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal_art46").value)){
						subtotal_art46 = parseFloat(document.getElementById("<portlet:namespace />subtotal_art46").value);
					}
					if (document.getElementById("<portlet:namespace />inte_art46") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte_art46").value)){
						inte_art46 = parseFloat(document.getElementById("<portlet:namespace />inte_art46").value);
					}
					if (document.getElementById("<portlet:namespace />total")!= null){
						
						document.getElementById("<portlet:namespace />total").value = Math.round((Math.round(subtotal_art46 *100) / 100 + Math.round(inte_art46 *100) / 100+
																								 Math.round(subtotal_usufructo *100) / 100 + Math.round(inte_usufructo *100) / 100+
																								 Math.round(subtotal_solidario *100) / 100 + Math.round(inte_solidario *100) / 100 + 
																								 Math.round(subtotal_sindicato *100) / 100 + Math.round(inte_sindicato *100) / 100)*100)/100;
					}
				}
				
				function sumarTodoAMTIMA(){					
					var deudaActasTmp = 0;
					var otros = 0;
					var subtotal = 0; 
					var inte = 0;
										
					if (document.getElementById("deudaActasTmp")!=null){
						var id = document.getElementById("deudaActasTmp").value.split(";");
						var total = 0.0;
						if (id.length >1){
							for (var i = 0; i<id.length-1; i++){
								if (IsNumeric(trim(document.getElementById("saldo_" + id[i]).value))){
									deudaActasTmp =  Math.round((deudaActasTmp  + Math.round(parseFloat(document.getElementById("saldo_" + id[i]).value) * 100) /100)*100)/100;
								}
							}
						}
						if (document.getElementById("<portlet:namespace />dedua") != null){
							document.getElementById("<portlet:namespace />dedua").value = deudaActasTmp;
						}
					}					
					if (document.getElementById("<portlet:namespace />subtotal") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal").value)){
						subtotal = parseFloat(document.getElementById("<portlet:namespace />subtotal").value);
					}
					if (document.getElementById("<portlet:namespace />inte") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte").value)){
						inte = parseFloat(document.getElementById("<portlet:namespace />inte").value);
					}
					
					if (document.getElementById("<portlet:namespace />total")!= null){
						
						document.getElementById("<portlet:namespace />total").value = Math.round((Math.round(subtotal*100) / 100 + Math.round(inte*100) / 100)*100)/100;
					}
				}
			
				function setearTempValueActas(ids){
					document.getElementById("deudaActasTmp").value = ids;
					if(jQuery("#<portlet:namespace/>entidad").val()=="U.O.M.A."){
						sumarTodoUOMA();
					}else if(jQuery("#<portlet:namespace/>entidad").val()=="A.M.T.I.M.A."){
						sumarTodoAMTIMA();
					}else{
						sumarTodo();
					}
				}
					
			</script>
			<!--fieldset class="block-labels"><legend><liferay-ui:message
				key="actas-saldo" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/actas_asociadas_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion && (acta == null || !acta.isActaCerrada()))%>"/>
				</liferay-util:include>
			</fieldset-->
			</td>
			<td colspan="3" valign="top">
			<fieldset class="block-labels"><legend><liferay-ui:message key="detalle" /></legend>
			  <div id="prorrateo">
					<table>
						<tr>					
							<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
							<td>
								<liferay-ui:input-date
								dayParam="fechaPeriodoDesdeDia"
								dayValue="<%= periodoIniCal.get(Calendar.DATE) %>" 
								monthParam="fechaPeriodoDesdeMes"
								monthValue="<%= periodoIniCal.get(Calendar.MONTH) %>"				
								yearParam="fechaPeriodoDesdeAnio"
								yearValue="<%= periodoIniCal.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= periodoIniCal.get(Calendar.YEAR) + 20%>"
								firstDayOfWeek="<%= periodoIniCal.getFirstDayOfWeek() - 1 %>"
								disabled="<%= !esEdicion   %>" />
							</td>
							<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
							<td>
								<liferay-ui:input-date
								dayParam="fechaPeriodoHastaDia"
								dayValue="<%=periodoFinCal.get(Calendar.DATE)%>" 
								monthParam="fechaPeriodoHastaMes"
								monthValue="<%=  periodoFinCal.get(Calendar.MONTH)%>"				
								yearParam="fechaPeriodoHastaAnio"
								yearValue="<%= periodoFinCal.get(Calendar.YEAR)%>"
								yearRangeStart="<%= current.get(Calendar.YEAR) -20 %>"	
								yearRangeEnd="<%= periodoFinCal.get(Calendar.YEAR) + 20%>"
								firstDayOfWeek="<%= periodoFinCal.getFirstDayOfWeek() - 1 %>"
								disabled="<%= !esEdicion  %>" />
								&nbsp;
							</td>
						
						</tr>
					</table>
				</div>
				<div id="detalle_uoma">
				<table>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td  width="120"><liferay-ui:message key="subtotal-sindicato" />:</td>
							<td>
								<input type="text" name="<portlet:namespace />subtotal_sindicato" id="<portlet:namespace />subtotal_sindicato"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapitalSindicato()!=null ?acta.getCapitalSindicato().toString() : ""%>"/>
							</td>
						</tr>					
						<tr>
							<td><liferay-ui:message key="interes-sindicato" />:</td>
							<td>
								<input type="text"	name="<portlet:namespace />inte_sindicato" id="<portlet:namespace />inte_sindicato" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteresSindicato()  != null ? acta.getInteresSindicato().toString() : "" %>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message key="subtotal-solidario" />:</td>
							<td>
								<input type="text" name="<portlet:namespace />subtotal_solidario" id="<portlet:namespace />subtotal_solidario"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapitalSolidario()!=null ?acta.getCapitalSolidario().toString() : ""%>"/>
							</td>
						</tr>					
						<tr>
							<td><liferay-ui:message key="interes-solidario" />:</td>
							<td>
								<input type="text"	name="<portlet:namespace />inte_solidario" id="<portlet:namespace />inte_solidario" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteresSolidario()  != null ? acta.getInteresSolidario().toString() : "" %>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message key="subtotal-usufructo" />:</td>
							<td>
								<input type="text" name="<portlet:namespace />subtotal_usufructo" id="<portlet:namespace />subtotal_usufructo"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapitalUsufructo()!=null ?acta.getCapitalUsufructo().toString() : ""%>"/>
							</td>
						</tr>					
						<tr>
							<td><liferay-ui:message key="interes-usufructo" />:</td>
							<td>
								<input type="text"	name="<portlet:namespace />inte_usufructo" id="<portlet:namespace />inte_usufructo" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteresUsufructo()  != null ? acta.getInteresUsufructo().toString() : "" %>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message key="subtotal-art46" />:</td>
							<td>
								<input type="text" name="<portlet:namespace />subtotal_art46" id="<portlet:namespace />subtotal_art46"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapitalArt46()!=null ?acta.getCapitalArt46().toString() : ""%>"/>
							</td>
						</tr>					
						<tr>
							<td><liferay-ui:message key="interes-art46" />:</td>
							<td>
								<input type="text"	name="<portlet:namespace />inte_art46" id="<portlet:namespace />inte_art46" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoUOMA()"
									<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteresArt46()  != null ? acta.getInteresArt46().toString() : "" %>" />
							</td>
						</tr>
				</table>
				</div>
				<div id="detalle_amtima">
					<table>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td  width="120"><liferay-ui:message key="subtotal-amtima" />:</td>
								<td>
									<input type="text" name="<portlet:namespace />subtotal" id="<portlet:namespace />subtotal"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodo()"
										<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapital()!=null ?acta.getCapital().toString() : ""%>"/>
								</td>
							</tr>					
							<tr>
								<td><liferay-ui:message key="interes-amtima" />:</td>
								<td>
									<input type="text"	name="<portlet:namespace />inte" id="<portlet:namespace />inte" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange="sumarTodoAMTIMA()"
										<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteres()  != null ? acta.getInteres().toString() : "" %>" />
								</td>
							</tr>					
					</table>
				</div>
				<table>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>					
					<tr>
						<td  width="120"><liferay-ui:message key="total"/>:</td>
						<td>
							<input type="text" name="<portlet:namespace />total" id="<portlet:namespace />total"  readonly='readonly' />
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
				</table>
				<div align="center">											
					<input type="submit" id="submitPeriodos" name="submitPeriodos" value="<liferay-ui:message key="periodos" />" onClick="<portlet:namespace />buscarPeriodos();return false;"/>
					<% if (acta != null && acta.getId()>0 && acta.getPeriodos() != null && !acta.getPeriodos().isEmpty()&& acta.getEntidad().equals("U.O.M.A.")){%>
						&nbsp;<input type="button" value="<liferay-ui:message key="ver-detalle-excel" />" onClick="<portlet:namespace />reporteDetalleGral();" /></td>
					<%} %>
				</div>
				</fieldset>				
			</td>
		</tr>
</table>
<div id="div_formas_de_pago"  >
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
			<fieldset class="block-labels">
				<legend><liferay-ui:message	key="formas-de-pago" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/formas_pago_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</fieldset>
			</td>
		</tr>
</table>
</div>


<% if (esEdicion) { %> 
<br />

<% if(showABMButtons && (null!=acta && !acta.isActaCerrada())) { %>
	<input type="submit" value="<liferay-ui:message key="crear-acta" />" onClick="<portlet:namespace />closeActa();return false;"/>
<%} %>
<% if(portlet_name.equals("estudio_isidro")) {%>
	<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveActaNoOSPopup();return false;"/>
<% }  else {%>
	<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveActa();return false;"/>
<%} %>




<input type="hidden" name="fromBusquedaDeuda" id="fromBusquedaDeuda" value="<%=fromDeuda%>"/>
<input type="hidden" id="<portlet:namespace />guardado" name="<portlet:namespace />guardado" value="<%=guardado%>"/>
<input type="hidden" value="" name="cerrarActa" id="cerrarActa" />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa"/>
<input type="hidden" value="" name="tabs1" id="tabs1"/>
<input type="hidden" value="" name="view" id="view"/>
<input type="hidden" value="" name="popupActa" id="popupActa"/>
<input type="hidden" value="" name="popupActaNoOS" id="popupActaNoOS"/>
<input type="hidden" value="" name="busqueda" id="busqueda" value="<%=busqueda%>"/>
<input type="hidden" name="<portlet:namespace />acta_id" id="<portlet:namespace />acta_id" value="<%= acta != null ? acta.getId() : "" %>"/>
<input type="hidden" value="<%= request.getAttribute("accionOriginal") != null && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE)%>" name="accionOriginal" id="accionOriginal"/>
<%} %>

<div style="visibility:hidden" id="<portlet:namespace />actualizarEmpresa">		
	</div>
</form>	
				
				
<script type="text/javascript">
	var popup;
	function <portlet:namespace />reporteDetalleGral() {			 
		 var entidad=document.getElementById("<portlet:namespace/>entidad").value;			 	
			var url = '/xlsservlet/?reporte=ACTA_NO_OS_PERIODOS_GENERAL';								
			url+='&acta_id=<%=null!=acta?acta.getId():0%>';	
			url+='&entidad=' + entidad;			
			url += '&rnd=' + Math.floor(Math.random()*100);				
			window.location.href = url;
	}
	if(jQuery("#<portlet:namespace/>entidad").val()=="U.O.M.A."){		
		jQuery("#detalle_amtima").hide();
		jQuery("#detalle_uoma").show();
	}else{
		jQuery("#detalle_amtima").show();
		jQuery("#detalle_uoma").hide();
	}	
	function <portlet:namespace />saveActa() {		 
		 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
			 document.getElementById("<portlet:namespace />id_banco").focus();
			 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
			 return false;
		 }
		 
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />actNoOS.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry';			
			document.getElementById("busqueda").value="<%=busqueda%>";
			document.getElementById("popupActaNoOS").value="true";						
			document.<portlet:namespace />actNoOS.method = 'post';
			document.getElementById("cambioSolapa").name = "xx";
			document.getElementById("tabs1").name = "xx2";
			submitForm(document.<portlet:namespace />actNoOS, url);
		}
	}
	
	function <portlet:namespace />saveActaNoOSPopup() {
		if (<portlet:namespace />validarCampos()) {			
			document.<portlet:namespace />actNoOS.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
		
		
			if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
					 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
				 document.getElementById("<portlet:namespace />id_banco").focus();
				 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
				 return false;
			}	
/*			
			if (trim(document.getElementById("<portlet:namespace />acta_numero").value)== ""){
				 alert("Por favor, ingrese un numero de acta");
				 document.getElementById("<portlet:namespace />acta_numero").focus();
				 return false;
			 }
*/			
			var form = jQuery(<portlet:namespace />actNoOS);		
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/edit_actas_no_os_entry" /></portlet:actionURL>';
			document.getElementById("popupActaNoOS").value="true";		
			document.getElementById("popupActa").value="true";		
			form.ajaxForm(
				{
					url: url,
			    	//target: tar,//".ui-dialog-content",//poopup
			        type: "POST",
			        beforeSubmit: function() {
			        },
			        success: function(data) {			        	
			        	Liferay.Popup.close(popupActa);			        	
			        	jQuery('#<portlet:namespace />busquedaActaDiv').html(data);			        	
			        	<% 
		        		if (portlet_name.equals("estudio_isidro")) { %>		        			
		        	    <%}%>
			        }
			    }
			);	
						
			form.submit();
		}
		
	}
	
	function <portlet:namespace />saveActaPeriodo(acta_id) {
		 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
			 document.getElementById("<portlet:namespace />id_banco").focus();
			 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
			 return false;
		 }
		if (<portlet:namespace />validarCampos()) {			
			var guardado=jQuery('#<portlet:namespace />guardado').val();
			if(guardado=="GUARDADO"){				
				document.<portlet:namespace />actNoOS.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE%>";
			}else{
				document.<portlet:namespace />actNoOS.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
			}
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry&acta_id='+acta_id;			
			document.getElementById("busqueda").value="<%=busqueda%>";
			document.getElementById("popupActaNoOS").value="true";						
			document.<portlet:namespace />actNoOS.method = 'post';
			document.getElementById("cambioSolapa").name = "xx";
			document.getElementById("tabs1").name = "xx2";
			jQuery('#<portlet:namespace />acta_id').val(acta_id);
			var form = jQuery(document.<portlet:namespace />actNoOS);			
			
			//jQuery("#<portlet:namespace />submitPeriodos").trigger("click");
			form.ajaxForm(
					{
						url: url,				    	
				        type: "POST",
				        beforeSubmit: function() {
				        	jQuery('#<portlet:namespace />guardandoDiv').show();
				        },
				        success: function() {				        	
				        	jQuery('#<portlet:namespace />guardandoDiv').hide();				        	
				        	jQuery('#<portlet:namespace />guardado').val('GUARDADO');
				        	//alert('cierropopup');
				        	//popup.close();				        	
				        	 <portlet:namespace />buscarPeriodosEnActualPop();
				        	//jQuery("#<portlet:namespace />buscar_periodos").trigger("click");				        	
				        }
				    }
			);	
							
			form.submit();
		}		
				 
	}
	
	function <portlet:namespace />validarCampos() {
		try{						
			if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value) == ""){
					alert("Debe seleccionar una empresa");
				return false;
			}

			
		} catch (err) {
			return false;
		}
		return true;
	}
	 
	 function cambioRadioButton(){
		 var esEdicion = <%= esEdicion%>;
		 if (esEdicion){
			 if (document.getElementById("<portlet:namespace />inspectorTrue").checked == true){
				document.getElementById("<portlet:namespace />subtotal").readOnly  = true;
				document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				document.getElementById("<portlet:namespace />otros").readOnly  = false;
				document.getElementById("<portlet:namespace />inte").readOnly  = true;
			 }else{
				 document.getElementById("<portlet:namespace />subtotal").readOnly = false;
				 document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				 document.getElementById("<portlet:namespace />otros").readOnly  = false;
				 document.getElementById("<portlet:namespace />inte").readOnly  = false;			 
			 }	
		 } else {
				document.getElementById("<portlet:namespace />subtotal").readOnly  = true;
				document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				document.getElementById("<portlet:namespace />otros").readOnly  = true;
				document.getElementById("<portlet:namespace />inte").readOnly  = true;
			 
		 }
	 }
	 /* Pagan por transf. bancarias y recibo, no registrados en uoma.uoma_aportes.
	 FOODARG SRL 	30715865943 	UOMA
	 GOLOCAN S.A. 	30711313822 	UOMA
	 GRUPO PILAR S.A. 	30707628312 	UOMA / AMTIMA
	 MOLINOS LAS JUNTURAS S.A. 	30579632735 	UOMA
	 PROVEEDORES SRL 	30646887328 	UOMA
	 PROVIFE SRL 	30710764138
	 MOLINOS CAÑUELAS 30507950848*/
	 var popup;
	 function <portlet:namespace />buscarPeriodos() {
		 var cuit_sel = document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value; 
	 	 if(trim(cuit_sel)==''){
	 	 	alert('<liferay-ui:message key="ingresar-cuit" />');
	 	 	return false;
	 	 }else if (cuit_sel == '30715865943' || cuit_sel == '30707628312'
	 		|| cuit_sel == '30579632735'|| cuit_sel == '30646887328'|| cuit_sel == '30710764138'
 	 		/*|| cuit_sel == '30507950848'*/){
	 		alert('Esta empresa realiza pagos por transferencia bancaria, Consulte con Tesoreria UOMA.');
	 		return false;
	 	 }
	 	 if (trim(document.getElementById("<portlet:namespace />entidad").value)==""){
			alert("Debe seleccionar una entidad");
			return false;
		 }
		 var entidad=document.getElementById("<portlet:namespace/>entidad").value;		 
	     popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-periodos" />",modal:true,position:[50,50],xy: ['center', 100],width:1200});
	     var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos_no_os';	     	     
	     <%if (esEdicion) {%>
	    	 url += '&esEdicion=esEdicion';
	     <%}%>
	     url +='&entidad='+ entidad;
	     url += '&rnd=' + Math.floor(Math.random()*100);	     
	 	jQuery(popup).load(url);    
	 }
	 
	 function <portlet:namespace />buscarPeriodosEnActualPop() {		 
	 	 if(trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)==''){
	 	 	alert('<liferay-ui:message key="ingresar-cuit" />');
	 	 	return false;
	 	 }
	 	 if (trim(document.getElementById("<portlet:namespace />entidad").value)==""){
			alert("Debe seleccionar una entidad");
			return false;
		 }
		 var entidad=document.getElementById("<portlet:namespace/>entidad").value;		 
	     //popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-periodos" />",modal:true,position:[50,50],xy: ['center', 100],width:1200});
	     var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos_no_os';	     	     
	     <%if (esEdicion) {%>
	    	 url += '&esEdicion=esEdicion';
	     <%}%>
	     url +='&entidad='+ entidad;
	     url += '&rnd=' + Math.floor(Math.random()*100);	     
	 	jQuery(popup).load(url);    
	 }
	 
	 function <portlet:namespace />recalcularIntereses(){		 
		 popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-periodos" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		 
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos_no_os&recalcular=recalcular';
		 url=url+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)
					+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
					+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
					+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;
	     <%if (esEdicion) {%>
	    	 url += '&esEdicion=esEdicion';
	     <%}%>
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url, function(){});

	} 
	
	 function <portlet:namespace />closeActa(){	 	 
		 if (document.getElementById("div_formas_de_pago").style.visibility == "collapse"){
			 document.getElementById("div_formas_de_pago").style.visibility = "visible";
			 alert("Por favor, complete una forma de pago antes de crear el acta correspondiente.");
			 return false;
		 } else {
			 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
							 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
				 document.getElementById("<portlet:namespace />id_banco").focus();
				 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
				 return false;
			 }
		 }
		 if (<portlet:namespace />validarCampos()) {		  	 
			 if (trim(document.getElementById("<portlet:namespace />acta_numero").value)== ""){
				 alert("Por favor, ingrese un numero de acta");
				 document.getElementById("<portlet:namespace />acta_numero").focus();
				 return false;
			 }	
			 document.<portlet:namespace />actNoOS.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
             document.<portlet:namespace />actNoOS.method = 'post';
			 document.getElementById("cambioSolapa").name = "xx";
			 document.getElementById("tabs1").name = "xx2";
			 document.getElementById("cerrarActa").value = "cerrarActa";
			 
			 <%if(portlet_name.equals("estudio_isidro")){%>
			 	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry';
			 	document.getElementById("busqueda").value="<%=busqueda%>";
				document.getElementById("popupActaNoOS").value="true";				
				var form = jQuery(document.<portlet:namespace />actNoOS);			
				
				//jQuery("#<portlet:namespace />submitPeriodos").trigger("click");
				form.ajaxForm(
						{
							url: url,				    	
					        type: "POST",
					        beforeSubmit: function() {					        	
					        	jQuery('#<portlet:namespace />guardandoDiv').show();
					        },
					        success: function(data) {				        	
					        	Liferay.Popup.close(popupActa);
					        	jQuery('#<portlet:namespace />tabla_resumen').html(data);					        	
					        	jQuery('#<portlet:namespace />busquedaCalculoDiv').css('display','none');					        	
					        	<% 
				        		if (portlet_name.equals("estudio_isidro")) { %>		        			
				        	    <%}%>		        	
					        }
					    }
				);	
								
				form.submit();
			 <%}else{%>	
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/edit_actas_no_os_entry';
				submitForm(document.<portlet:namespace />actNoOS, url);
			 <%}%>
			}
	 }

	 function <portlet:namespace />reporte() {	 	 
			var url = '/xlsservlet/?reporte=ACTA_NO_OS_PERIODOS_DETALLE&totales=totales&'+
				'&acta_numero='  + trim(document.getElementById("<portlet:namespace />acta_numero").value) +
				'&cuit=' + trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value) + 
				'&desc=' + escape(trim(document.getElementById("<portlet:namespace />entidad<%=sufi%>").value));
			  url += '&rnd=' + Math.floor(Math.random()*100);
			  window.location.href = url;
	 }
	 
	// cambioRadioButton();
	 if(jQuery("#<portlet:namespace/>entidad").val()=="U.O.M.A."){
	 	sumarTodoUOMA();
	 }else if(jQuery("#<portlet:namespace/>entidad").val()=="A.M.T.I.M.A."){
	 	sumarTodoAMTIMA();
	 }else{
	 	sumarTodo();
	 }

	 function cambiaCuit(){
		}
	 function cambiarEntidad(){		
		if(jQuery("#<portlet:namespace/>entidad").val()=="U.O.M.A."){
			jQuery("#detalle_amtima").hide();
			jQuery("#detalle_uoma").show();
		}else{
			jQuery("#detalle_amtima").show();
			jQuery("#detalle_uoma").hide();
		}				
	 }
	 
	 //ESTO ES PARA EVITAR ERROR DE JS DE COMPONENTE DE EMPLEADORES.	 
	 function cambiaCuit<%=sufi%>(){}
	 function filtrarConceptosUOMA(){}
	 
	 <%if (acta==null){%>
		 if(trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value!="")){		 
			 <portlet:namespace />buscarEntidad<%=sufi%>();
		 }
 	<%}%>
	
 	function actualizarEstadoSeguimiento(){
		 
		var id_estado_seg = jQuery('#<portlet:namespace />estado_seguim').val();
		var id_acta = jQuery('#<portlet:namespace />acta_id').val();

		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/estudio_isidro/actaCambiarEstadoSeguimiento&id_estado='+id_estado_seg+'&id_acta='+id_acta;
		
		jQuery.ajax({   
			url: url,
			success: function(data){
										
				var obj = jQuery.parseJSON(data);
				if(obj.result == 'true'){
					jQuery("#<portlet:namespace />actuEstadoSegResult").show();		
				}
			}	
		});
}
		
</script>



<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
Calendar current = CalendarFactoryUtil.getCalendar();
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
if (recibo != null && recibo.getId() != 0){
	esEdicion = false;
}

List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
.getSession().getAttribute(
		WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

%>



<table width="100%">
  <tr>
	<td width="50%" valign="top">
  <fieldset class="block-labels">
	<legend><liferay-ui:message	key="ingresos" /></legend>
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td width="10%">
						<liferay-ui:message key="tipo-ingreso"/>:&nbsp;
					</td>
					<td width="20%">
						<select id="<portlet:namespace />tipo_ingreso" name="<portlet:namespace />tipo_ingreso" onchange="changeTipo();">
							<option value="<%=Cheque.class.getName() %>">Cheque</option>
							<option value="<%=Efectivo.class.getName() %>">Efectivo</option>
							<option value="Deposito_Bancario_1">Deposito Bancario</option>
							<option value="Deposito_Bancario_2">Transferencia Bancaria</option>
							<option value="Redondeo">Redondeo en Ingresos</option>
							<option value="AFIP">Ingresos AFIP</option>
							<option value="Quitas">Descuentos y quitas</option>
							<option value="Manuales">Ingresos x Btas. Manuales</option>
						</select>
					</td>
					<td>
						 <span id="<portlet:namespace />importeSpan">
							<liferay-ui:message key="importe"/>:
						</span>
					</td>
					<td>
						 <span id="<portlet:namespace />importeSpan2">
							<input type="text" value="" name="<portlet:namespace />importe" id="<portlet:namespace />importe" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />fechaSpan">
							<liferay-ui:message key="fecha-pago"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />fechaSpan2">
						<liferay-ui:input-date
						dayParam="fechaPagoDia"
						dayValue="<%= current.get(Calendar.DATE) %>" 
						monthParam="fechaPagoMes"
						monthValue="<%= current.get(Calendar.MONTH) %>"				
						yearParam="fechaPagoAnio"
						yearValue="<%= current.get(Calendar.YEAR) %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
						yearRangeEnd="<%= current.get(Calendar.YEAR) + 50%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="<%= !esEdicion  %>" />
						</span>
					</td>
					<td>
						<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarIngreso();" />
					</td>
				</tr>
				<tr>
					<td>
						<span id="<portlet:namespace />numeroSpan">
						<liferay-ui:message key="numero"/>:
						</span>						
					</td>
					<td>
					<span id="<portlet:namespace />numeroSpan2">
						<input type="text" value="" name="<portlet:namespace />nro" id="<portlet:namespace />nro" />
					</span>
					</td>
					<td>
					<span id="<portlet:namespace />spanbanco">
						<liferay-ui:message key="banco"/>:
					</span>
					<span id="<portlet:namespace />spanctabcria">
						<liferay-ui:message key="cuenta-bancaria-destino"/>:
					</span>
					</td>
					<td>
					<span id="<portlet:namespace />spanbanco2">
					<select id="<portlet:namespace />id_banco" name="<portlet:namespace />id_banco">
						<% for (Banco b : bancos) { %>
						<option value="<%=b.getId_banco() %>"><%=b.getDescripcion_banco() %></option>
						<%} %>
					</select>
					</span>
					
					<span id="<portlet:namespace />spanctabcria2">
					<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
						<% 	for (CuentaBancaria cta : ctas) {  %>
								<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>/<%=String.valueOf(cta.getSucursal())%></option>
							<%	 } %>
					</select>
					</span>
					
					</td>
					<td colspan="3">
						&nbsp;
					</td>
				</tr>
				</table>
			</td>
		</tr>
		<%} %>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />agregandoIngreso">
			<table style="align: center;">
				<tr>
					<td><liferay-ui:message key='buscando' /></td>
					<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />ingresos">
			<jsp:include page='recibo_no_os_ingresos_search_result.jsp' /></div>
			</td>
		</tr>
	</table>
	</fieldset>
	</td>
  </tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">
	function <portlet:namespace />agregarIngreso(){
			jQuery('#<portlet:namespace />agregandoIngreso').show();	
			var nro=jQuery('#<portlet:namespace />nro').val();
			var idBanco=jQuery('#<portlet:namespace />id_banco').val();
			var importe=jQuery('#<portlet:namespace />importe').val();
			var tipo=jQuery('#<portlet:namespace />tipo_ingreso').val();
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();


			if (document.getElementById("<portlet:namespace />tipo_ingreso").value != "Anticipo") {
				if (trim(importe) == "" || !IsNumeric(importe)){
					alert("Debe completar el importe");
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				} 
				if (tipo == "<%=Cheque.class.getName()%>" && (trim(nro) == "" || idBanco == 0)){
					alert("Debe completar el numero y banco");
					jQuery('#<portlet:namespace />nro').focus();
					return false;
				} 
			}

			var fechaPagoDia = document.getElementById("<portlet:namespace />fechaPagoDia");
			var fechaPagoMes = document.getElementById("<portlet:namespace />fechaPagoMes");
			var fechaPagoAnio = document.getElementById("<portlet:namespace />fechaPagoAnio");

			var cuit = document.getElementById("<portlet:namespace />cuit_entidad");
			var sucu = document.getElementById("<portlet:namespace />sucursal_entidad");
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/agregar_recibo_no_os_ingreso&nro=' +nro+   '&id_banco=' + idBanco
			+ '&importe=' + importe
			+ '&esEdicion=' +"<%=esEdicion%>" 	
			+'&fechaPagoDia='+fechaPagoDia.value
			+'&fechaPagoMes='+fechaPagoMes.value
			+'&fechaPagoAnio='+fechaPagoAnio.value
			+'&tipo=' + tipo
			+'&cuit_entidad=' + cuit.value
			+'&sucursal_entidad=' + sucu.value
			+'&id_cta_bcria=' + idCtaBcria;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														 jQuery('#<portlet:namespace />nro').val("");
														 jQuery('#<portlet:namespace />importe').val("");
														 setearCapitalIngreso();
										   }
			 );	
	}

	function borraIngreso(tipo, nro, idBanco, idCtaBcria, importe){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/sacar_recibo_no_os_ingreso'
			+  '&tipo=' +tipo
			+  '&nro=' +nro
			+  '&id_banco=' + idBanco
			+  '&id_cta_bcria=' + idCtaBcria
			+  '&importe=' + importe
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						setearCapitalIngreso();     
																			   }
															   );
	}

	function recargarIngresos(){
		var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/agregar_recibo_no_os_ingreso&reload=reload';
		url1 += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url1 , function(){
																	setearCapitalIngreso();	
																}
														);
	}

	function changeTipo(){
		if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=Efectivo.class.getName()%>"  
				|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "Redondeo"
					|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "AFIP"
						|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "Quitas"
							|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "Manuales"){
			jQuery('#<portlet:namespace />numeroSpan').hide();
			jQuery('#<portlet:namespace />numeroSpan2').hide();
			jQuery('#<portlet:namespace />spanbanco').hide();
			jQuery('#<portlet:namespace />spanbanco2').hide();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();
		} else if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "Deposito_Bancario_1"
			|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "Deposito_Bancario_2"){
			jQuery('#<portlet:namespace />numeroSpan').show();
			jQuery('#<portlet:namespace />numeroSpan2').show();
			jQuery('#<portlet:namespace />spanctabcria').show();
			jQuery('#<portlet:namespace />spanctabcria2').show();
			jQuery('#<portlet:namespace />spanbanco').hide();
			jQuery('#<portlet:namespace />spanbanco2').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();
		} else if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "Anticipo") {
			jQuery('#<portlet:namespace />numeroSpan').hide();
			jQuery('#<portlet:namespace />numeroSpan2').hide();
			jQuery('#<portlet:namespace />spanbanco').hide();
			jQuery('#<portlet:namespace />spanbanco2').hide();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />importeSpan').hide();
			jQuery('#<portlet:namespace />importeSpan2').hide();
			jQuery('#<portlet:namespace />fechaSpan').hide();
			jQuery('#<portlet:namespace />fechaSpan2').hide();
		} else {
			jQuery('#<portlet:namespace />numeroSpan').show();
			jQuery('#<portlet:namespace />numeroSpan2').show();
			jQuery('#<portlet:namespace />spanbanco').show();
			jQuery('#<portlet:namespace />spanbanco2').show();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();
		}
	}
	<%if (esEdicion){%>
	changeTipo();
	<%}%>
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
</script>
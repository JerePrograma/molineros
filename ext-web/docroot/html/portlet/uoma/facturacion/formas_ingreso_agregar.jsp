<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.FinanciacionTurismo" %>
<%@ page import="ar.com.ospim.global.beans.Retencion" %>
<%
Factura factura = (Factura) portletSession.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
Calendar current = CalendarFactoryUtil.getCalendar();
boolean esEdicion = ParamUtil.getBoolean(request, "esEdicion", false);
String ptoVtaAfip = ParamUtil.getString(request, "ptoVtaAfip"); 
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
.getSession().getAttribute(
		WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
		
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("") || renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if(renderResponse.getNamespace().equals("_HOT_1_")){
	portlet_name = "hoteles";
}

List<ClaseBase> emisoresTarjetas = TraeListasServiceUtil.getTarjetasDebitoCreditoEmisores();

/* List<ReciboActa> actas = null;
List<ReciboConvenio> convenios = null;
if (factura != null){
	actas = factura.getActas();
	convenios = factura.getConvenios();
} */
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
					<td>
						<liferay-ui:message key="tipo-ingreso"/>:&nbsp;
					</td>
					<td>
						<select id="<portlet:namespace />tipo_ingreso" name="<portlet:namespace />tipo_ingreso" onchange="changeTipo();">
							<option value="<%=Cheque.class.getName() %>">Cheque</option>
							<option value="<%=Efectivo.class.getName() %>" selected="selected" >Efectivo</option>
							<%-- <%if (portlet_name==null || !portlet_name.equals("uoma")){%>
							<option value="<%=Pagare.class.getName() %>">Pagare</option>
							<%}%>	 --%>						
							<!--   <option value="Deposito_Bancario_1">Deposito Bancario</option> -->
							<option value="Deposito_Bancario_2">Transferencia Bancaria</option>
							<!-- <option value="<%=DepositoBancario.class.getName()%> ">Transferencia Bancaria</option> -->
							<option value="<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_DEBITO %>" >Tarjeta Débito</option>
							<option value="<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_CREDITO %>" >Tarjeta Crédito</option>
							<option value="<%=FinanciacionTurismo.class.getName() %>"  >Financiación Turismo</option>
							<option value="<%=ar.com.ospim.global.beans.CuentaCorriente.class.getName() %>"  >Cuenta Corriente</option>
							<option value="<%=Retencion.class.getName()+Retencion.GRAL %>" >Retención GRAL</option>
							<option value="<%=Retencion.class.getName()+Retencion.IIBB %>" >Retención IIBB</option>
							<option value="<%=Retencion.class.getName()+Retencion.IVA %>" >Retención IVA</option>
							<option value="<%=Retencion.class.getName()+Retencion.SUSS %>" >Retención SUSS</option>
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
						<liferay-ui:message key="numero"/>operación:
						</span>						
					</td>
					
					<td>
					
					<span id="<portlet:namespace />numeroSpan2">
						<input type="text" value="" name="<portlet:namespace />nro" id="<portlet:namespace />nro" <%if(portlet_name.equals("uoma")){%>maxlength="20" size="14"<%}%> onkeydown="allowOnlyDigitsAndDecimals(event)"/>
						<span id="<portlet:namespace />sucursalSpan">
							<%if(portlet_name.equals("uoma")){%>
								<liferay-ui:message key="sucursal"/>:<input type="text" value="" name="<portlet:namespace />sucu_dpto" id="<portlet:namespace />sucu_dpto" maxlength="4" size="5"/>
							<%}%>
						</span>
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
						<select id="<portlet:namespace />id_banco" name="<portlet:namespace />id_banco" onclick="javascript:filtrarCtaBancaria();">
							<% for (Banco b : bancos) { %>
							<option value="<%=b.getId_banco() %>"><%=b.getDescripcion_banco() %></option>
							<%} %>
						</select>
						</span>
						
						<span id="<portlet:namespace />spanctabcria2">
						<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
							
							<%for(CuentaBancaria cta : ctas) {  
								if(portlet_name.equals("farmacia")&& cta.getEntidad().equals("A")) {%>
									<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>/<%=String.valueOf(cta.getNro_cuenta())%></option>
								<%}else if(portlet_name.equals("uoma")&& cta.getEntidad().equals("U")) {%>
									<%if((cta.getNro_cuenta()==948373 || cta.getNro_cuenta()==333134 || cta.getNro_cuenta()==800351) 
											&& ((ptoVtaAfip.equals("00020") || (ptoVtaAfip.equals("00030")) 
													|| ptoVtaAfip.equals("9999") ))){ %>
									<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>/<%=String.valueOf(cta.getNro_cuenta())%></option>
									<%}%>
									<%if(cta.getNro_cuenta()==19725 && (ptoVtaAfip.equals("00010") || ptoVtaAfip.equals("9999") )){ %>
									<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>/<%=String.valueOf(cta.getNro_cuenta())%></option>
									<%}%>
								<%}else if(portlet_name.equals("tesoreria")&& cta.getEntidad().equals("O")) {%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 2) {%> selected="selected" <%} %>><%=cta.getDescripcion()%> <%=String.valueOf(cta.getNro_cuenta())%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%}
							}%>	
								
						</select>
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />spanctabcria4">
							<liferay-ui:message key="cta-bcria"/>:
						</span>
					</td>
					<td>
						<div class="selector-cuentaBancaria" id="<portlet:namespace />spanctabcria3">	
							<span id="<portlet:namespace />spanctabcria2">
								<select id="<portlet:namespace />id_cta_bcria_ch" name="<portlet:namespace />id_cta_bcria_ch">
									<option value="0">Seleccione el Banco primero</option>
								</select>
							</span>
						</div>
					</td>
					<td>
						<span  id="<portlet:namespace />ayudaSpan">							
							<a href="javascript:void(0)" onclick="help(event, 'helpCtaBcria')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
						</span>
					</td> 
					<%-- <%if(null!=actas || null!=convenios){%>
						<td>							
							<liferay-ui:message key="Aplicar a"/>:													
						</td>
						<td>
							<select id="<portlet:namespace />aplicar_a" name="<portlet:namespace />aplicar_a">
							<%if(null!=actas) {for(ReciboActa ra:actas){%>
								<option value="<%=ra.getActa().getId()%>_a">Acta <%=ra.getActa().getNumero()%></option>
							<%}}%>
							<%if(null!=convenios) {for(ReciboConvenio rc:convenios){%>
								<option value="<%=rc.getConvenio().getId()%>_c">Convenio <%=rc.getConvenio().getNumero()%></option>
							<%}}%>
							</select>
						</td>
					<%}%>
					<td colspan="<%=null!=actas?2:3%>">
						&nbsp;
					</td> --%>
				</tr>
				<tr>
					<td>
						<span id="<portlet:namespace />che3Span">
							<liferay-ui:message key="cheq-3ros"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />che3Span2">
						<input type="checkbox" id="<portlet:namespace />cheque_3ros" name="<portlet:namespace />cheque_3ros" alt="Cheque de 3ros" />
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />cuit3Span">
							<liferay-ui:message key="cuit-3ros"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />cuit3Span2">
							<input type="text" id="<portlet:namespace />cuit_3ros" name="<portlet:namespace />cuit_3ros" alt="Cuit de Terceros" maxlength="11" />
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />cta3Span">
							<liferay-ui:message key="nueva-cta-bcria"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />cta3Span2">	
							<input type="text" id="<portlet:namespace />id_cta_bcria_nueva" name="<portlet:namespace />id_cta_bcria_nueva" alt="Nueva Cta. Bancaria" maxlength="15" />
						</span>	
					</td>
				</tr>
				
				<tr>
				   
	                 <td>
	                 <span id="<portlet:namespace />tarjetaDatosEmisor1">
	                  <label>Tipo: </label>
	                 </span> 
	                 </td>			
				
				     <td>
				      <span id="<portlet:namespace />tarjetaDatosEmisor">
				     	<select id="<portlet:namespace />id_emisor_tarjeta" name="<portlet:namespace />id_emisor_tarjeta">
							<%for(ClaseBase cta : emisoresTarjetas) {%> 
								<option value="<%=cta.getId()%>"><%=cta.getDescripcion()%></option>
							<%}%>	
						</select>
		              </span>				
				     </td>
				    
				    
				      <td>
				       <span id="<portlet:namespace />tarjetaCuotas1">
				        <label>Cuotas: </label>
				       </span> 
				      </td> 
				      <td>
				         <span id="<portlet:namespace />tarjetaCuotas">
				           <input type="text" id="<portlet:namespace />cuotas_tarjeta" name="<portlet:namespace />cuotas_tarjeta" 
				           maxlength="5" value="1" onkeydown="allowOnlyDigitsAndDecimals(event)"/>
				         </span>
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
				<jsp:include page='factura_ingresos_search_result.jsp' />
			</div>
			</td>
		</tr>
	</table>
	</fieldset>
	</td>
  </tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<div id="helpCtaBcria" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Si la cuenta bancaria para el banco seleccionado no se encuentra, 
sólo debe escribir el número de cta. bcria. en la caja de texto al lado. 
Si el cheque es de terceros, tildar la marca y completar el CUIT terceros y la cta. bcria nueva.<br/> 
</div>
<script type="text/javascript">	
	function <portlet:namespace />agregarIngreso(){
		
			jQuery('#<portlet:namespace />agregandoIngreso').show();	
			var nro=jQuery('#<portlet:namespace />nro').val();
			var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidad').val();
			var idBanco=jQuery('#<portlet:namespace />id_banco').val();
			var idCtaBcriaCh=jQuery('#<portlet:namespace />id_cta_bcria_ch').val();
			var ctaBcria=jQuery('#<portlet:namespace />id_cta_bcria_ch option:selected').text();
			var ctaBcriaNueva=jQuery('#<portlet:namespace />id_cta_bcria_nueva').val();
			var importe=jQuery('#<portlet:namespace />importe').val();
			var tipo=jQuery('#<portlet:namespace />tipo_ingreso').val();
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();
			var chkCheque3ros= document.getElementById('<portlet:namespace />cheque_3ros');
			var esChequeTerceros = chkCheque3ros.checked ? 'true' : 'false'; 
			var cuitTerceros = jQuery('#<portlet:namespace />cuit_3ros').val();
			var idEmisorTarjeta=jQuery('#<portlet:namespace />id_emisor_tarjeta').val();
			var cuotasTarjeta=jQuery('#<portlet:namespace />cuotas_tarjeta').val();
			var descEmisorTarjeta=jQuery('#<portlet:namespace />id_emisor_tarjeta option:selected	').html();

			//var aplicar_a= jQuery('#<portlet:namespace />aplicar_a').val();

	<%-- 		if (document.getElementById("<portlet:namespace />tipo_ingreso").value != "Anticipo") {
				if (trim(importe) == "" || !IsNumeric(importe)){
					alert("Debe completar el importe");
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				} 
				if (tipo == "<%=Cheque.class.getName()%>" && (trim(nro) == "" || idBanco == 0)){
					alert("Debe completar el número y banco");
					jQuery('#<portlet:namespace />nro').focus();
					return false;
				}  
			} --%>
			if (trim(importe) == "" || !IsNumeric(importe) ){
				alert("Debe completar el importe");
				jQuery('#<portlet:namespace />importe_cheque').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			}
			if (tipo == "<%=Cheque.class.getName()%>" && (trim(nro) == "" || idBanco == 0 )){
				alert("Debe completar todos los datos del cheque");
				jQuery('#<portlet:namespace />importe_cheque').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			}
			if (tipo == "<%=Cheque.class.getName()%>" && trim(ctaBcriaNueva) == "" && idCtaBcriaCh == 0 ){
				alert("Debe seleccionar una Cta. Bcria o crear una nueva...");
				jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			}
			
			if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'true' 
					&& (trim(ctaBcriaNueva) == "" || trim(cuitTerceros) == "") ){
				alert("Si es cheque de terceros completar CUIT terceros y nueva Cta. Bancaria");
				jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();
				return false;
			}
			
			if ((tipo == "<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_CREDITO %>" ||
				 tipo == "<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_DEBITO %>" ) && (trim(nro) == "" || idBanco == 0 )){
				alert("Debe completar todos los datos de la Tarjeta");
				jQuery('#<portlet:namespace />importe_cheque').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			}
			
			
			
			/* los idCtaBcria me sirven para validar el cheque aunque todavia no este cargada la nueva cuenta bancaria. */		
			if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'true'){
				idCtaBcria = -1;
			}else if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'false' && trim(ctaBcriaNueva) != ""){
				idCtaBcria = 0 ;
				ctaBcria = ctaBcriaNueva;
			}

			var fechaPagoDia = document.getElementById("<portlet:namespace />fechaPagoDia");
			var fechaPagoMes = document.getElementById("<portlet:namespace />fechaPagoMes");
			var fechaPagoAnio = document.getElementById("<portlet:namespace />fechaPagoAnio");

			var cuit = document.getElementById("<portlet:namespace />cuit_entidad");
			var sucu = document.getElementById("<portlet:namespace />sucursal_entidad");
			
			var sucu_dpto=document.getElementById("<portlet:namespace />sucu_dpto");
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_factura_ingreso';
			url = url + '&nro=' +nro
			+'&cuitEmisor='+cuitEmisor
			+'&id_banco=' + idBanco
			+'&id_cta_bcria=' + idCtaBcria 
			+'&idCtaBcriaCh='+idCtaBcriaCh
			+'&ctaBcriaCh='+encodeURI(ctaBcria)
			+'&ctaBcriaNuevaCh='+encodeURI(ctaBcriaNueva)
			+'&esChequeTerceros='+esChequeTerceros
			+'&cuitTerceros='+encodeURI(cuitTerceros)
			+'&importe=' + importe
			+'&esEdicion=' +"<%=esEdicion%>" 	
			+'&fechaPagoDia='+fechaPagoDia.value
			+'&fechaPagoMes='+fechaPagoMes.value
			+'&fechaPagoAnio='+fechaPagoAnio.value
			+'&tipo=' + tipo
			+'&cuit_entidad=' + cuit.value
			
			<%if(portlet_name.equals("uoma")){%>
			+'&sucursal_dpto=' + sucu_dpto.value
			<%}%>
			+'&sucursal_entidad=' + sucu.value;
			//+'&aplicar_a=' + aplicar_a;
			
			url +='&id_emisor_tarjeta='+ idEmisorTarjeta
			    +'&des_emisor_tarjeta='+ descEmisorTarjeta
			    +'&cuotas_tarjeta='+cuotasTarjeta;
			
			url += '&rnd=' + Math.floor(Math.random()*100);
		
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														 jQuery('#<portlet:namespace />nro').val("");
														 jQuery('#<portlet:namespace />importe').val("");
														 setearCapitalIngreso();
														 //PRUEBA
														 var total_round=jQuery('#<portlet:namespace />total_conceptos').val();
														 if(convenio_id>0){
															var total=jQuery('#total_convenio_'+convenio_id).val()-importe;																																													
															jQuery('#total_convenio_'+convenio_id).val(Math.round(parseFloat(total)*100)/100);
															total_round=total_round-importe;																							
														 }
														 if(acta_id>0){
															var total=jQuery('#total_acta_'+acta_id).val()-importe;																																										
															jQuery('#total_acta_'+acta_id).val(Math.round(parseFloat(total)*100)/100);
															total_round=total_round-importe;
														 }
														 jQuery('#<portlet:namespace />total_conceptos').val(Math.round(parseFloat(total_round)*100)/100);
										   }
			 );	
	}

	function borraIngreso(tipo, nro, idBanco, idCtaBcria, importe, convenio_id, acta_id){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_factura_ingreso'
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
																						var total_round=jQuery('#<portlet:namespace />total_conceptos').val();
																						if(convenio_id>0){
																							var total=jQuery('#total_convenio_'+convenio_id).val()-importe;																																													
																							//jQuery('#total_convenio_'+convenio_id).val(Math.round(parseFloat(total)*100)/100);
																							total_round=total_round-importe;																							
																						}
																						if(acta_id>0){
																							var total=jQuery('#total_acta_'+acta_id).val()-importe;																																										
																							//jQuery('#total_acta_'+acta_id).val(Math.round(parseFloat(total)*100)/100);
																							total_round=total_round-importe;
																						}
																						//jQuery('#<portlet:namespace />total_conceptos').val(Math.round(parseFloat(total_round)*100)/100);
																			   }
															   );
	}

	function recargarIngresos(){
		var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_factura_ingreso&reload=reload';
		url1 += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url1 , function(){
																	setearCapitalIngreso();	
																}
														);
	}

	function changeTipo(){
		if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=Efectivo.class.getName()%>"
			 || document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=FinanciacionTurismo.class.getName()%>"
				 || document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=CuentaCorriente.class.getName()%>"
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
			jQuery('#<portlet:namespace />che3Span').hide();
			jQuery('#<portlet:namespace />che3Span2').hide();
			jQuery('#<portlet:namespace />cuit3Span').hide();
			jQuery('#<portlet:namespace />cuit3Span2').hide();
			jQuery('#<portlet:namespace />cta3Span').hide();
			jQuery('#<portlet:namespace />cta3Span2').hide();
			jQuery('#<portlet:namespace />spanctabcria3').hide();
			jQuery('#<portlet:namespace />spanctabcria4').hide();
			jQuery('#<portlet:namespace />ayudaSpan').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();
			jQuery('#<portlet:namespace />tarjetaCuotas').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas1').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').hide();
		} else if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "Deposito_Bancario_1"
			|| document.getElementById("<portlet:namespace />tipo_ingreso").value == "Deposito_Bancario_2"){
			jQuery('#<portlet:namespace />che3Span').hide();
			jQuery('#<portlet:namespace />che3Span2').hide();
			jQuery('#<portlet:namespace />cuit3Span').hide();
			jQuery('#<portlet:namespace />cuit3Span2').hide();
			jQuery('#<portlet:namespace />cta3Span').hide();
			jQuery('#<portlet:namespace />cta3Span2').hide();
			jQuery('#<portlet:namespace />spanctabcria3').hide();
			jQuery('#<portlet:namespace />spanctabcria4').hide();
			jQuery('#<portlet:namespace />ayudaSpan').hide();
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
			if(document.getElementById("<portlet:namespace />tipo_ingreso").value == "Deposito_Bancario_1"){
				jQuery('#<portlet:namespace />sucursalSpan').show();
			}else{
				jQuery('#<portlet:namespace />sucursalSpan').hide();
			}
			jQuery('#<portlet:namespace />sucu_dpto').attr( "maxlength", "4" );
			jQuery('#<portlet:namespace />tarjetaCuotas').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas1').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').hide();
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
			jQuery('#<portlet:namespace />che3Span').hide();
			jQuery('#<portlet:namespace />che3Span2').hide();
			jQuery('#<portlet:namespace />cuit3Span').hide();
			jQuery('#<portlet:namespace />cuit3Span2').hide();
			jQuery('#<portlet:namespace />cta3Span').hide();
			jQuery('#<portlet:namespace />cta3Span2').hide();
			jQuery('#<portlet:namespace />spanctabcria3').hide();
			jQuery('#<portlet:namespace />spanctabcria4').hide();
			jQuery('#<portlet:namespace />ayudaSpan').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas1').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').hide();
		} else if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=Pagare.class.getName()%>"){
			jQuery('#<portlet:namespace />numeroSpan').hide();
			jQuery('#<portlet:namespace />numeroSpan2').hide();
			jQuery('#<portlet:namespace />spanbanco').hide();
			jQuery('#<portlet:namespace />spanbanco2').hide();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />che3Span').hide();
			jQuery('#<portlet:namespace />che3Span2').hide();
			jQuery('#<portlet:namespace />cuit3Span').hide();
			jQuery('#<portlet:namespace />cuit3Span2').hide();
			jQuery('#<portlet:namespace />cta3Span').hide();
			jQuery('#<portlet:namespace />cta3Span2').hide();
			jQuery('#<portlet:namespace />spanctabcria3').hide();
			jQuery('#<portlet:namespace />spanctabcria4').hide();
			jQuery('#<portlet:namespace />ayudaSpan').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();
			jQuery('#<portlet:namespace />tarjetaCuotas').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas1').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').hide();
		
		} else if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_CREDITO %>" ||
				   document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_DEBITO %>"){
			
			jQuery('#<portlet:namespace />numeroSpan').show();
			jQuery('#<portlet:namespace />numeroSpan2').show();
			jQuery('#<portlet:namespace />spanbanco').show();
			jQuery('#<portlet:namespace />spanbanco2').show();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />che3Span').hide();
			jQuery('#<portlet:namespace />che3Span2').hide();
			jQuery('#<portlet:namespace />cuit3Span').hide();
			jQuery('#<portlet:namespace />cuit3Span2').hide();
			jQuery('#<portlet:namespace />cta3Span').hide();
			jQuery('#<portlet:namespace />cta3Span2').hide();
			jQuery('#<portlet:namespace />spanctabcria3').hide();
			jQuery('#<portlet:namespace />spanctabcria4').hide();
			jQuery('#<portlet:namespace />ayudaSpan').hide();
			jQuery('#<portlet:namespace />importeSpan').show();
			jQuery('#<portlet:namespace />importeSpan2').show();
			jQuery('#<portlet:namespace />fechaSpan').show();
			jQuery('#<portlet:namespace />fechaSpan2').show();	
			
			jQuery('#<portlet:namespace />sucursalSpan').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas').show();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').show();
			jQuery('#<portlet:namespace />tarjetaCuotas1').show();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').show();

			
			jQuery("#<portlet:namespace />id_emisor_tarjeta").val("1");
			if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=TarjetaDebitoCredito.class.getName()+TarjetaDebitoCredito.ID_TIPO_CREDITO %>"){
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=2]").hide();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=3]").show();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=4]").show();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=5]").show();
			}else{
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=2]").show();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=3]").hide();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=4]").hide();
				jQuery("#<portlet:namespace />id_emisor_tarjeta option[value=5]").hide();
			}
		 
		}else {
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
			jQuery('#<portlet:namespace />sucursalSpan').hide();
			jQuery('#<portlet:namespace />che3Span').show();
			jQuery('#<portlet:namespace />che3Span2').show();
			jQuery('#<portlet:namespace />cuit3Span').show();
			jQuery('#<portlet:namespace />cuit3Span2').show();
			jQuery('#<portlet:namespace />cta3Span').show();
			jQuery('#<portlet:namespace />cta3Span2').show();
			jQuery('#<portlet:namespace />spanctabcria3').show();
			jQuery('#<portlet:namespace />spanctabcria4').show();
			jQuery('#<portlet:namespace />ayudaSpan').show();
			//jQuery('#<portlet:namespace />sucu_dpto').attr( "maxlength", "4" );
			jQuery('#<portlet:namespace />tarjetaCuotas').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor').hide();
			jQuery('#<portlet:namespace />tarjetaCuotas1').hide();
			jQuery('#<portlet:namespace />tarjetaDatosEmisor1').hide();
		}
	}
	<%if (esEdicion){%>
	changeTipo();
	<%}%>
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
	
	
	function filtrarCtaBancaria() {

		var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidad').val();
		var sucuEmisor = jQuery('#<portlet:namespace />sucursal_entidad').val();
		var idBanco=jQuery('#<portlet:namespace />id_banco').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscarCtasBancariasPorBanco&idBanco='+idBanco+'&cuit='+cuitEmisor+'&sucur='+sucuEmisor;
		jQuery("#<portlet:namespace />id_cta_bcria_ch").attr('disabled', 'disabled');
		
		jQuery.ajax({   
				url: url,
				async:false,
				success: function(data){
					document.getElementById("<portlet:namespace />id_cta_bcria_ch").length = 0;
					jQuery("#<portlet:namespace />id_cta_bcria_ch").removeAttr('disabled');
					jQuery('.selector-cuentaBancaria select').html(data).fadeIn(); 
				}
			});
	}
</script>
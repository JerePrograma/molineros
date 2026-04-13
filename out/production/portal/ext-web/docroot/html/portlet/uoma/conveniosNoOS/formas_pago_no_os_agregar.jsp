<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%

String portlet_name=null;
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "estudio_isidro";
}

Convenio convenio= (Convenio)request.getSession().getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
Calendar current = CalendarFactoryUtil.getCalendar();
String act = (String)request.getAttribute("accionOriginal");
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

%>
<input type="hidden" id="max_cuota" value="0"/>
<liferay-ui:error exception="<%= ConvenioSinPagosException.class %>" message="convenio-sin-pagos" />
<liferay-ui:error exception="<%= FaltanCuotasConvenioException.class %>" message="convenio-faltan-cuotas" />

<table width="100%">
  <tr>
	<td width="50%" valign="top">
  <fieldset class="block-labels">
	<legend>Pagos</legend>
	<table class="lfr-table">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
				<tr>
					<td colspan="7">Cantidad de Cuotas:&nbsp;<input type="text" id="cant_cuotas" name="cant_cuotas" size="4" onchange="calcularCapitalInteres(); actualizarDetalleCuotas();"/>
					&nbsp;&nbsp;Tasa aplicada:&nbsp;<input type="text" id="tasa" name="tasa" size="4" value="3" onchange="calcularCapitalInteres(); actualizarDetalleCuotas();"/>&nbsp;
					<input type="button" value="Ver detalle propuesto " onclick="helpCuotas(event, 'helpCuotas')"/>
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="cuota-nro"/>
					</td>
					<td>
						<input type="text" name="<portlet:namespace />cuota_nro_cheque" id="<portlet:namespace />cuota_nro_cheque" size="5" maxlength="4" value="1" onchange="calcularCapitalInteres()"/>
					</td>
					<td colspan="3">Cheque&nbsp;<input onClick="javascript:cambiarEtiquetas();" type="radio" name="tipo_pago" id="radio_cheque" value="Cheque"/>&nbsp;&nbsp;Depósito&nbsp;<input onClick="javascript:cambiarEtiquetas();" type="radio" name="tipo_pago" id="radio_deposito" value="Deposito"/>&nbsp;&nbsp;Pagaré&nbsp;<input onClick="javascript:cambiarEtiquetas();" type="radio" name="tipo_pago" id="radio_pagare" value="Pagare"/></td>
					<td colspan="2">&nbsp;</td>
					
				</tr>
				<tr>
					<td>
						<div id="divPagare">
							<liferay-ui:message key="pagare-nro"/>:
						</div>
						<div id="divCheque">
							<liferay-ui:message key="cheque-nro"/>:
						</div>
					</td>
					<td>
						<input type="text" value="" name="<portlet:namespace />nro_cheque" id="<portlet:namespace />nro_cheque" />
					</td>
					<td>
						<liferay-ui:message key="banco"/>:
					</td>
					<td>
					<select id="<portlet:namespace />id_banco" name="<portlet:namespace />id_banco" onclick="javascript:filtrarCtaBancaria();">
						<% for (Banco b : bancos) { %>
						<option value="<%=b.getId_banco() %>"><%=b.getDescripcion_banco() %></option>
						<%} %>
					</select>
					</td>
					<td>
					<span id="<portlet:namespace />spanctabcria">
						<liferay-ui:message key="cta-bcria"/>:
					</span>
					</td>
					<td>
						<div class="selector-cuentaBancaria" id="<portlet:namespace />spanctabcria3" >	
							<span id="<portlet:namespace />spanctabcria2">
								<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria" style="width: 200px;">
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
						<liferay-ui:message key="capital"/>:
					</td>
					<td>
						<input type="text" value="" name="<portlet:namespace />capital_cheque" id="<portlet:namespace />capital_cheque" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
					</td>
					<td>
						<liferay-ui:message key="interes"/>:
					</td>
					<td>
						<input type="text" value="0" name="<portlet:namespace />interes_cheque" id="<portlet:namespace />interes_cheque" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
					</td>
					<td colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="fecha-pago"/>:
					</td>
					<td colspan="2">
						<liferay-ui:input-date
						dayParam="fechaPagoDiaCheque"
						dayValue="<%= current.get(Calendar.DATE) %>" 
						monthParam="fechaPagoMesCheque"
						monthValue="<%= current.get(Calendar.MONTH) %>"				
						yearParam="fechaPagoAnioCheque"
						yearValue="<%= current.get(Calendar.YEAR) %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
						yearRangeEnd="<%= current.get(Calendar.YEAR) + 50%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="<%= !esEdicion  %>" />
					</td>
					<td colspan="4">
						<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarPago();" />
					</td>
				</tr>
				</table>
			</td>
		</tr>
		<%} %>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />agregandoCheque">
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
			<div align="center" id="<portlet:namespace />cheques">
			<liferay-util:include page='/html/portlet/uoma/conveniosNoOS/convenio_no_os_pagos_search_result.jsp'>
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
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

<div id="helpCuotas" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Detalle cuotas propuestas',closed:'true'}" style="top: 200px; left: 300px">
	<div id="detalle_cuotas"></div>
</div>
<div id="helpCtaBcria" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Si después de seleccionar el banco no se obtiene automáticamente la cta. bcria, 
para el alta de nueva cta. bcria. sólo debe escribir el número de cta. bcria. en la caja de texto. 
Si el cheque es de terceros, tildar la marca y completar el CUIT terceros y la cta. bcria nueva.<br/> 
</div>
<script type="text/javascript">	
	jQuery('#divPagare').hide();
	function cambiarEtiquetas(){
		var tipoCheque = jQuery("#radio_cheque");
		var tipoDeposito =jQuery("#radio_deposito");
		var tipoPagare =jQuery("#radio_pagare");
		
		if(tipoCheque.attr("checked")){
			jQuery('#divPagare').hide();
			jQuery('#divCheque').show();
		}
		if(tipoPagare.attr("checked")){
			jQuery('#divPagare').show();
			jQuery('#divCheque').hide();
		}
	}
	function <portlet:namespace />agregarPago(){
		
			var tipoCheque = jQuery("#radio_cheque");
			var tipoDeposito =jQuery("#radio_deposito");
			var tipoPagare =jQuery("#radio_pagare");		
			
			if (!tipoCheque.attr("checked") && !tipoDeposito.attr("checked") && !tipoPagare.attr("checked")) {
				alert("Debe elegir un tipo de pago");	
				return;
			} 
		
			
			
			jQuery('#<portlet:namespace />agregandoCheque').show();	
			var chequeNro=jQuery('#<portlet:namespace />nro_cheque').val();
			var idBanco=jQuery('#<portlet:namespace />id_banco').val();
			var capital=jQuery('#<portlet:namespace />capital_cheque').val();
			var interes=jQuery('#<portlet:namespace />interes_cheque').val();
			var cuotaNro =  jQuery('#<portlet:namespace />cuota_nro_cheque').val();

			var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidad').val();
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();
			var ctaBcria=jQuery('#<portlet:namespace />id_cta_bcria option:selected').text();
			var ctaBcriaNueva=jQuery('#<portlet:namespace />id_cta_bcria_nueva').val();
			var chkCheque3ros= document.getElementById('<portlet:namespace />cheque_3ros');
			var esChequeTerceros = chkCheque3ros.checked ? 'true' : 'false'; 
			var cuitTerceros = jQuery('#<portlet:namespace />cuit_3ros').val();
			
			if (cuotaNro <= 0){
				alert("El numero de cuota debe ser mayor a 0");
				jQuery('#<portlet:namespace />cuota_nro_cheque').focus();
				return false;
			}
			
			if (tipoCheque.attr("checked")){ 
				/* if (trim(chequeNro) == "" || idBanco == 0 || trim(capital) == "" || !IsNumeric(capital)
						|| trim(interes) == "" || !IsNumeric(interes)){
					alert("Debe completar todos los datos del cheque");
					jQuery('#<portlet:namespace />capital_cheque').focus();
					return false;
				}  */
				
				if (trim(chequeNro) == "" || idBanco == 0 || trim(capital) == "" || !IsNumeric(capital)
						|| trim(interes) == "" || !IsNumeric(interes)){
					alert("Debe completar todos los datos del cheque");
					jQuery('#<portlet:namespace />agregandoCheque').hide();	
					jQuery('#<portlet:namespace />capital_cheque').focus();
					return false;
				}
				if (trim(ctaBcriaNueva) == "" && idCtaBcria == 0 ){
					alert("Debe seleccionar una Cta. Bcria o crear una nueva...");
					jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
					jQuery('#<portlet:namespace />agregandoCheque').hide();	
					return false;
				}
				
				if(esChequeTerceros == 'true' 
						&& (trim(ctaBcriaNueva) == "" || trim(cuitTerceros) == "") ){
					alert("Si es cheque de terceros completar CUIT terceros y nueva Cta. Bancaria");
					jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
					jQuery('#<portlet:namespace />agregandoCheque').hide();	
					return false;
				}
				/* los idCtaBcria me sirven para validar el cheque aunque todavia no este cargada la nueva cuenta bancaria. */		
				if(esChequeTerceros == 'true'){
					idCtaBcria = -1;
					ctaBcria=ctaBcriaNueva;
				}else if(esChequeTerceros == 'false' && trim(ctaBcriaNueva) != ""){
					idCtaBcria = 0 ;
					ctaBcria=ctaBcriaNueva;
				}
			}
			
			if (tipoPagare.attr("checked")){ 
				if (trim(chequeNro) == ""){
					alert("Debe completar todos los datos del pagaré");
					jQuery('#<portlet:namespace />nro_cheque').focus();
					return false;
				} 
			}

			var fechaPagoDiaCheque  = document.getElementById("<portlet:namespace />fechaPagoDiaCheque");
			var fechaPagoMesCheque= document.getElementById("<portlet:namespace />fechaPagoMesCheque");
			var fechaPagoAnioCheque = document.getElementById("<portlet:namespace />fechaPagoAnioCheque");
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_convenio_no_os_pago&cheque_nro=' +chequeNro 
			+ '&id_banco=' + idBanco
			+'&cuitEmisor='+cuitEmisor
			+'&idCtaBcria='+idCtaBcria
			+'&ctaBcria='+encodeURI(ctaBcria)
			+'&ctaBcriaNueva='+encodeURI(ctaBcriaNueva)
			+'&esChequeTerceros='+esChequeTerceros
			+'&cuitTerceros='+encodeURI(cuitTerceros)
			+ '&capital_cheque=' + capital
			+ '&interes_cheque=' + interes
			+ '&esEdicion=' +"<%=esEdicion%>" 	
			+'&accionOriginal='+ "<%=act%>"
			+'&fechaPagoDiaCheque='+fechaPagoDiaCheque.value
			+'&fechaPagoMesCheque='+fechaPagoMesCheque.value
			+'&fechaPagoAnioCheque='+fechaPagoAnioCheque.value
			+'&cuota_nro_cheque=' + cuotaNro;
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			if (tipoCheque.attr("checked")) {
				url+= "&tipo_pago=cheque"; 
			} else if (tipoPagare.attr("checked")) {
				url+= "&tipo_pago=pagare"; 
			} else{
				url+= "&tipo_pago=depostio";
			}			
			
			jQuery('#<portlet:namespace />cheques').load(url, function() {
														jQuery('#<portlet:namespace />agregandoCheque').hide();	
														 jQuery('#<portlet:namespace />nro_cheque').val("");
														 jQuery('#<portlet:namespace />capital_cheque').val("");
														 jQuery('#<portlet:namespace />interes_cheque').val("0");		
														 jQuery('#<portlet:namespace />cuota_nro_cheque').val("");
														 document.getElementById("<portlet:namespace />cuota_nro_cheque").value = document.getElementById("max_cuota").value;
														 sumarInteresYCapital();
														 calcularCapitalInteres();
										   }
			 );	
	}

	function borraDepositoBancario(cuotaNro){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_convenio_no_os_pago&tipo_pago=deposito&cuota_nro_cta_bcria=' + cuotaNro + '&esEdicion=' + "<%=esEdicion%>" 	+'&accionOriginal='+ "<%=act%>";
			jQuery('#<portlet:namespace />cheques').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoCtaBcria').hide();  
																						sumarInteresYCapital();          															
																			   }
															   );
		}	
	}

	function borraCheque(chequeNro, idBanco, idCtaBcria){
		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidad').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_convenio_no_os_pago&tipo_pago=cheque&cheque_nro=' +chequeNro 
			+  '&id_banco=' + idBanco+ '&idCtaBcria=' + idCtaBcria+'&cuitEmisor=' + cuitEmisor +'&esEdicion=' + "<%=esEdicion%>" 	+'&accionOriginal='+ "<%=act%>";
			jQuery('#<portlet:namespace />cheques').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoCheque').hide();     
																						sumarInteresYCapital();       															
																			   }
															   );
		}	
	}
	
	function sumarInteresYCapital(){
		 var inteC = parseFloat(document.getElementById("interesCheque").value);
		 document.getElementById("<portlet:namespace />inte").value =  redondear(inteC);

		 var capC = parseFloat(document.getElementById("capitalCheque").value);
		 document.getElementById("<portlet:namespace />capital").value =    redondear(capC);
		 sumarTodo();          															
	}
	
	jQuery('#<portlet:namespace />agregandoCheque').hide();
	jQuery('#<portlet:namespace />agregandoCtaBcria').hide();
	
	function calcularCapitalInteres(){
		if (jQuery('#cant_cuotas').val() == "" || jQuery('#<portlet:namespace />cuota_nro_cheque') == ""){
			return;
		}
		var cuotaNro =  parseInt(jQuery('#<portlet:namespace />cuota_nro_cheque').val(),10);
		var cant_cuotas =  parseInt(jQuery('#cant_cuotas').val(),10);
		var tasa =  parseFloat(jQuery('#tasa').val());
		var deuda =  parseFloat(jQuery('#<portlet:namespace />deuda').val());
		var redito = cant_cuotas * tasa / 100 / cant_cuotas;
			
		var capitalCuota = redondear(redondear(deuda) / redondear(cant_cuotas));
		jQuery('#<portlet:namespace />capital_cheque').val(capitalCuota);
		if (cuotaNro == 1){
			jQuery('#<portlet:namespace />interes_cheque').val(0);
		} else {
			var interesCuota = redondear(redondear(deuda- (capitalCuota * (cuotaNro -1))) * redondear(redito)); 
			jQuery('#<portlet:namespace />interes_cheque').val(interesCuota);
		}
	}
	
	function redondear(nro){
		return Math.round(nro *100) / 100; 
	}
	
	function actualizarDetalleCuotas(){
		var cant_cuotas =  parseInt(jQuery('#cant_cuotas').val(),10);
		var deuda =  parseFloat(jQuery('#<portlet:namespace />deuda').val());
		var capitalCuota = redondear(redondear(deuda) / redondear(cant_cuotas));
		var tasa =  parseFloat(jQuery('#tasa').val());
		var redito = cant_cuotas * tasa / 100 / cant_cuotas;
		
		jQuery("#detalle_cuotas").html("");
		for (var i = 1; i<= cant_cuotas; i++) {
			var interesCuota = 0;
			if (i>1) {
				interesCuota = redondear(redondear(deuda- (capitalCuota * (i -1))) * redondear(redito));
			}
			jQuery("#detalle_cuotas").append("Cuota: " + i + " Capital: $"+capitalCuota+" Interes: $" +interesCuota+ "<br/>");
		}
	}
	

	function helpCuotas(event, id){
		closeHelps();
		jQuery("#" + id).mb_open();
		jQuery("#" + id).css("top",event.clientY + jQuery(document).scrollTop());
		jQuery("#" + id).css("left", 300);
		
		var cant_cuotas =  parseInt(jQuery('#cant_cuotas').val(),10);
		jQuery("#" + id).mb_resizeTo(cant_cuotas * 23, jQuery("#" + id).width());
	}
	
	function filtrarCtaBancaria() {

		var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidad').val();
		var sucuEmisor = jQuery('#<portlet:namespace />sucursal_entidad').val();
		var idBanco=jQuery('#<portlet:namespace />id_banco').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscarCtasBancariasPorBanco&idBanco='+idBanco+'&cuit='+cuitEmisor+'&sucur='+sucuEmisor;
		jQuery("#<portlet:namespace />id_cta_bcria").attr('disabled', 'disabled');
		
		jQuery.ajax({   
				url: url,
				async:false,
				success: function(data){
					document.getElementById("<portlet:namespace />id_cta_bcria").length = 0;
					jQuery("#<portlet:namespace />id_cta_bcria").removeAttr('disabled');
					jQuery('.selector-cuentaBancaria select').html(data).fadeIn(); 
				}
			});
	}

</script>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
	
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}
if(renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
}


boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);
Calendar current = CalendarFactoryUtil.getCalendar();

Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

String act = (String)request.getAttribute("accionOriginal");
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
BigDecimal importeEfectivo = null;
int idCuota = 0;
if (acta != null && acta.getPagos()!= null && acta.getPagos().size()>0){
	for (ActaPago ap : acta.getPagos()){
		if (ap.getTipo().equals(ActaPago.Tipo.CUOTA)){
			idCuota = ap.getId();
		}
	}
}
if (acta != null && acta.isActaCerrada() && !auditorActas){
	esEdicion = false;
}
%>

<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />	
<liferay-ui:error exception="<%= ActaSinPagosException.class %>" message="acta-sin-pagos" />
<%if (idCuota!=0){ %>
	<input type="hidden" name="idCuota" value="<%=idCuota %>"/>
<%} %>
<table class="lfr-table" width="100%" style="border-collapse: collapse; border-spacing: 0 px; ">
	<%if (esEdicion){ %>
	<tr>
		<td>
			<liferay-ui:message key="tipo-ingreso"/>:&nbsp;
		</td>
		<td>
			<select id="<portlet:namespace />tipo_ingreso" name="<portlet:namespace />tipo_ingreso" onchange="changeTipo();">
				<option value="<%=Cheque.class.getName() %>">Cheque</option>
				<option value="<%=Efectivo.class.getName() %>">Efectivo</option>
				<option value="<%=DepositoBancario.class.getName() %>">Depósito Bancario</option>
				<option value="<%=Pagare.class.getName() %>">Pagaré</option>
			</select>
		</td>
		<td>
			<liferay-ui:message key="importe"/>:
		</td>
		<td>
			<input type="text" value="" name="<portlet:namespace />importe_cheque" id="<portlet:namespace />importe_cheque" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
		</td>
		<td>
				<liferay-ui:message key="fecha-pago"/>:&nbsp;
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
				&nbsp;
		</td>
		<td>
			<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarCheque();" />
		</td>
	</tr>
	<tr>
		<td colspan="7">
		<table style="border-collapse: collapse; border-spacing: 0 px; ">
			<tr>	
				<td>
					<span id="<portlet:namespace />numeroSpan">
						<liferay-ui:message key="cheque-nro"/>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</span>
				</td>
				<td>
					<span id="<portlet:namespace />numeroSpan2">
						<input type="text" value="" name="<portlet:namespace />nro_cheque" id="<portlet:namespace />nro_cheque" />
					</span>
				</td>
				<td align="right">
					<span id="<portlet:namespace />spanbanco">
						<liferay-ui:message key="banco"/>:
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
				</td>
				<td>
					<span id="<portlet:namespace />spanctabcria">
						<liferay-ui:message key="cta-bcria"/>:
					</span>
				</td>
				<td>
					<div class="selector-cuentaBancaria" id="<portlet:namespace />spanctabcria3">	
						<span id="<portlet:namespace />spanctabcria2">
							<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
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
				<%-- <td>
					<input type="text" list="cuentasBcrias" id="<portlet:namespace />id_cta_bcria" 
						name="<portlet:namespace />id_cta_bcria" placeholder="Select Type" />
					<div class="selector-cuentaBancaria" id="<portlet:namespace />spanctabcria2">
					<datalist id="cuentasBcrias" >
					  <option value="0">Seleccione el Banco primero</option>
					</datalist>
					</div>
					<a href="javascript:void(0)" onclick="help(event, 'helpCtaBcria')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
					
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
		</table>
		</td>	
	</tr>
	<%} %>
	<tr>
		<td colspan="7">
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
		<td colspan="7">
		<div align="center" id="<portlet:namespace />cheques">
		<jsp:include page='acta_cheques_search_result.jsp' /></div>
		</td>
	</tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<div id="helpCtaBcria" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Si después de seleccionar el banco no se obtiene automáticamente la cta. bcria, 
para el alta de nueva cta. bcria. sólo debe escribir el número de cta. bcria. en la caja de texto. 
Si el cheque es de terceros, tildar la marca y completar el CUIT terceros y la cta. bcria nueva.<br/> 
</div>
<script type="text/javascript">
	function <portlet:namespace />agregarCheque(){
			jQuery('#<portlet:namespace />agregandoCheque').show();	
			var chequeNro=jQuery('#<portlet:namespace />nro_cheque').val();
			var idBanco=jQuery('#<portlet:namespace />id_banco').val();
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();
			var ctaBcria=jQuery('#<portlet:namespace />id_cta_bcria option:selected').text();
			var ctaBcriaNueva=jQuery('#<portlet:namespace />id_cta_bcria_nueva').val();
			var importe=jQuery('#<portlet:namespace />importe_cheque').val();
			var tipo=jQuery('#<portlet:namespace />tipo_ingreso').val();
			/* var liqFC = document.getElementById("<portlet:namespace />cargaFC");
			var validaFC = liqFC.checked ? 'true' : 'false';	 */
			var chkCheque3ros= document.getElementById('<portlet:namespace />cheque_3ros');
			var esChequeTerceros = chkCheque3ros.checked ? 'true' : 'false'; 
			var cuitTerceros = jQuery('#<portlet:namespace />cuit_3ros').val();

			if (trim(importe) == "" || !IsNumeric(importe) ){
				alert("Debe completar el importe");
				jQuery('#<portlet:namespace />importe_cheque').focus();
			}
			if (tipo == "<%=Cheque.class.getName()%>" && (trim(chequeNro) == "" || idBanco == 0 )){
				alert("Debe completar todos los datos del cheque");
				jQuery('#<portlet:namespace />importe_cheque').focus();
				jQuery('#<portlet:namespace />agregandoCheque').hide();	
				return false;
			}
			if (tipo == "<%=Cheque.class.getName()%>" && trim(ctaBcriaNueva) == "" && idCtaBcria == 0 ){
				alert("Debe seleccionar una Cta. Bcria o crear una nueva...");
				jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
				jQuery('#<portlet:namespace />agregandoCheque').hide();	
				return false;
			}
			
			if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'true' 
					&& (trim(ctaBcriaNueva) == "" || trim(cuitTerceros) == "") ){
				alert("Si es cheque de terceros completar CUIT terceros y nueva Cta. Bancaria");
				jQuery('#<portlet:namespace />id_cta_bcria_nueva').focus();
				jQuery('#<portlet:namespace />agregandoCheque').hide();	
				return false;
			}
			/* los idCtaBcria me sirven para validar el cheque aunque todavia no este cargada la nueva cuenta bancaria. */		
			if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'true'){
				idCtaBcria = -1;
			}else if(tipo == "<%=Cheque.class.getName()%>" && esChequeTerceros == 'false' && trim(ctaBcriaNueva) != ""){
				idCtaBcria = 0 ;
				ctaBcria = ctaBcriaNueva;
			}
					
			var fechaPagoDiaCheque  = document.getElementById("<portlet:namespace />fechaPagoDiaCheque");
			var fechaPagoMesCheque= document.getElementById("<portlet:namespace />fechaPagoMesCheque");
			var fechaPagoAnioCheque = document.getElementById("<portlet:namespace />fechaPagoAnioCheque");
			var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidadacta_').val();
			var sucuEmisor = jQuery('#<portlet:namespace />sucursal_entidadacta_').val();
						
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_acta_ingreso';

			url=url+'&cheque_nro=' +chequeNro 
						+'&id_banco=' + idBanco
						+'&importe=' + importe 
						+'&esEdicion=' +"<%=esEdicion%>" 	
						+'&accionOriginal='+ "<%=act%>"
						+'&fechaPagoDiaCheque='+fechaPagoDiaCheque.value
						+'&fechaPagoMesCheque='+fechaPagoMesCheque.value
						+'&fechaPagoAnioCheque='+fechaPagoAnioCheque.value
						+'&cuitEmisor='+cuitEmisor
						+'&idCtaBcria='+idCtaBcria
						+'&ctaBcria='+encodeURI(ctaBcria)
						+'&ctaBcriaNueva='+encodeURI(ctaBcriaNueva)
						+'&esChequeTerceros='+esChequeTerceros
						+'&cuitTerceros='+encodeURI(cuitTerceros)
						+'&forma=' + tipo;
			url += '&rnd=' + Math.floor(Math.random()*100);			
			jQuery('#<portlet:namespace />cheques').load(url, function() {
														jQuery('#<portlet:namespace />agregandoCheque').hide();	
														 jQuery('#<portlet:namespace />nro_cheque').val("");
														 jQuery('#<portlet:namespace />importe_cheque').val("");						
										   }
			 );	
	}

	function borraIngreso(tipo, importe, chequeNro, idBanco, fecha, idActaPago, idCtaBcria){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidadacta_').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_acta_ingreso';
		
			url=url+'&cheque_nro=' +chequeNro 
				+'&importe=' + importe
				+'&forma=' + tipo
				+'&id_banco=' + idBanco
				+'&esEdicion=' + "<%=esEdicion%>" 	
				+'&accionOriginal='+ "<%=act%>"
				+'&fecha=' + fecha
				+'&id_acta_pago=' + idActaPago
				+'&cuitEmisor='+cuitEmisor
				+'&idCtaBcria='+idCtaBcria;
				
			jQuery('#<portlet:namespace />cheques').load(url, function() {
								jQuery('#<portlet:namespace />agregandoCheque').hide();            															
							});
		}	
	}
	
	function filtrarCtaBancaria() {

		var cuitEmisor = jQuery('#<portlet:namespace />cuit_entidadacta_').val();
		var sucuEmisor = jQuery('#<portlet:namespace />sucursal_entidadacta_').val();
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

	function changeTipo(){
		if (document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=Efectivo.class.getName()%>" || 
				document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=DepositoBancario.class.getName()%>" || 
				document.getElementById("<portlet:namespace />tipo_ingreso").value == "<%=Pagare.class.getName()%>"){
			document.getElementById("<portlet:namespace />numeroSpan").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />numeroSpan2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />spanbanco").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />spanbanco2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />spanctabcria").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />spanctabcria2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />che3Span").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />cuit3Span").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />cta3Span").style.visibility = "hidden";	
			document.getElementById("<portlet:namespace />che3Span2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />cuit3Span2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />cta3Span2").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />ayudaSpan").style.visibility = "hidden";
		} else {
			document.getElementById("<portlet:namespace />numeroSpan").style.visibility = "visible";
			document.getElementById("<portlet:namespace />numeroSpan2").style.visibility = "visible";
			document.getElementById("<portlet:namespace />spanbanco").style.visibility = "visible";
			document.getElementById("<portlet:namespace />spanbanco2").style.visibility = "visible";
			document.getElementById("<portlet:namespace />spanctabcria").style.visibility = "visible";
			document.getElementById("<portlet:namespace />spanctabcria2").style.visibility = "visible";
			document.getElementById("<portlet:namespace />che3Span").style.visibility = "visible";
			document.getElementById("<portlet:namespace />cuit3Span").style.visibility = "visible";
			document.getElementById("<portlet:namespace />cta3Span").style.visibility = "visible";
			document.getElementById("<portlet:namespace />che3Span2").style.visibility = "visible";
			document.getElementById("<portlet:namespace />cuit3Span2").style.visibility = "visible";
			document.getElementById("<portlet:namespace />cta3Span2").style.visibility = "visible";	
			document.getElementById("<portlet:namespace />ayudaSpan").style.visibility = "visible";
		}
	}
	<%if (esEdicion){ %>
		changeTipo();
	<%}%>
	jQuery('#<portlet:namespace />agregandoCheque').hide();
	
</script>
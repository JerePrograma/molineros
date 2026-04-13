<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Banco" %>
<%@ page import="ar.com.ospim.global.beans.RetencionIIBB" %>
<%@ page import="ar.com.ospim.global.beans.RetencionIVA" %>
<%@ page import="ar.com.ospim.global.beans.PagoSinSalidaDeFondos" %>
<%
	List<Banco> bancos = TraeListasServiceUtil.getBancos();

	boolean esAmtima=false;
	boolean esUoma=false;
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		esAmtima=true;
		portlet_name = "farmacia";
	} 
	
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		esUoma=true;
		portlet_name = "uoma";
	} 
	
	if(renderResponse.getNamespace().equals("_TES_1_")){
		portlet_name = "tesoreria";
	} 
	
	OrdenPago ordenPago = (OrdenPago) request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

	String esEdicionStr = (String) request
			.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
	String modificaFormaPago=ParamUtil.getString(request, "modificaFormaPago");
	boolean esEdicion = false;
	if (ordenPago == null || ordenPago.getId() == null
			|| ordenPago.getId().equals(0) || esEdicionStr != null ||
			"true".equalsIgnoreCase(modificaFormaPago)) {
		esEdicion = true;
	}

	
	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
	
	String existenChequesReutilizables = (String)request.getAttribute(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);	
	boolean mostrarCartelChequesReutilizables = existenChequesReutilizables != null && existenChequesReutilizables.equals(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
	String cuentaTarjeta = TraeListasServiceUtil.getSystemConfig("UOMA_CTA_BCRIA_TARJETA_RECARGABLE");
	
	
	List<ReintegroList> reintegrosAll  =  (List<ReintegroList>) session.getAttribute(WebKeysLiquidaciones.LISTA_ORDEN_PAGO_EDICION);

	
%>


<table width="100%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table width="100%">
				<% if (mostrarCartelChequesReutilizables){ %>
				<tr>
					<td colspan="5">
						<span style="color: red;"><liferay-ui:message key="cheques-reutilizables-disponibles"/></span>
					</td>
				</tr>
				<%} %>
				<tr>
					<td>
						<liferay-ui:message key="tipo-pago"/>:&nbsp;
					</td>
					<td>
						<select id="<portlet:namespace />tipo_pago" name="<portlet:namespace />tipo_pago" onchange="changeTipo();">
							<%if (portlet_name!=null && portlet_name.equals("farmacia")){%>
								<option value="<%=Caja.class.getName()+Caja.ID_PAGO_CAJA %>">Caja</option>
							<%}%>
							<option value="<%=Cheque.class.getName() %>">Cheque</option>
							<option value="<%=Cheque.class.getName()+Cheque.Estado.RECIBIDO%>">Cheques en Cartera</option>
							<option value="<%=RetencionGanancias.class.getName() %>">Retencion Ganancias</option>
							<option value="<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_DEBITO_BANCARIO %>">Debito Bancario</option>
							<%if (portlet_name!=null && portlet_name.equals("liquidaciones")){%>
								<option value="<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_DEBITO_POR_AUTOGESTION%>">Debito por autogestion</option>
								<option value="<%=PagoSinSalidaDeFondos.class.getName()+PagoSinSalidaDeFondos.ID_PAGO_A_UOMA %>">Sin Salida de Fondos</option>
							<%}%>
							<option value="<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA%>">Transferencia Bancaria</option>
							<%if (portlet_name!=null && portlet_name.equals("uoma")){%>
							   <option value="<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TARJETA_RECARGA%>">Tarjeta Recarga</option>
							<%}%>
							<%if (portlet_name!=null && portlet_name.equals("uoma")){%>
								<option value="<%=Caja.class.getName()+Caja.ID_PAGO_CAJA_LOS_DIQUES %>">Caja Colonia Los Diques</option>
								<option value="<%=RetencionIIBB.class.getName()+RetencionIIBB.BSAS %>">Retencion IIBB - BS.AS.</option>
								<option value="<%=RetencionIVA.class.getName()%>">Retencion IVA</option>
							<%}%>   
						</select>
						<span id="<portlet:namespace />spanBuscarCheques">
							<input type="button" value="<liferay-ui:message key="buscar-cheques-liberados" />" onClick="<portlet:namespace />buscarChequesLiberados();" />
						</span>
						<span id="<portlet:namespace />spanBuscarChequesCartera">
							<liferay-ui:message key="cuit" />:<input type="text" name="cuit_cartera" id="cuit_cartera" size="11"/>&nbsp;
							<liferay-ui:message key="numero-cheque" />:<input type="text" name="nro_cheque_cartera" id="nro_cheque_cartera" size="6"/>&nbsp;							
							<liferay-ui:message key="importe" />:<input type="text" name="importe_cheque_cartera" id="importe_cheque_cartera" size="6"/>&nbsp;
							<input type="button" value="<liferay-ui:message key="buscar-cheques-cartera" />" onClick="<portlet:namespace />buscarChequesEnCartera();" />
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />spanNro">
							<liferay-ui:message key="numero"/>:
						</span>
						<span id="<portlet:namespace />spanCbuNro">
							<liferay-ui:message key="cbu-alias"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />spanNro2">
							<input type="text" value="" name="<portlet:namespace />nro_pago" id="<portlet:namespace />nro_pago"/>
						</span>
					</td>
					<td>
							<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarPago();" />
					</td>
				</tr>
				<tr>
					<td id="<portlet:namespace />spanImporte">
						<span>
							<liferay-ui:message key="importe"/>:
						</span>
						&nbsp;
					</td>
					<td>
						<span id="<portlet:namespace />spanImporte2">
							<input type="text" value="" name="<portlet:namespace />importe_pago" id="<portlet:namespace />importe_pago" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);" size="10"/>
							<input type="hidden" value="" name="<portlet:namespace />cbu_sugerido" id="<portlet:namespace />cbu_sugerido"/>							 
						</span>
						&nbsp;							
					</td>
					<td>
						<span id="<portlet:namespace />spanctabcria">
							<liferay-ui:message key="cuenta-bancaria"/>:
						</span>
						<span id="<portlet:namespace />spanbancos">
							<liferay-ui:message key="banco"/>:
						</span>
					</td>
					<td>
						<span id="<portlet:namespace />spanctabcria2">
						<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria" onchange="sugerirNroCheque()">
							<% 	for (CuentaBancaria cta : ctas) { 
									if (portlet_name.equals("farmacia") && cta.getEntidad().equals("A")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>"> <%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>									
								<%	}else if(portlet_name.equals("liquidaciones") && cta.getEntidad().equals("O")){%>
									<%-- <option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 2) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option> --%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 10) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%	}else if(portlet_name.equals("tesoreria") && cta.getEntidad().equals("O")){%>
									<%-- <option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 2) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option> --%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 10) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%	}else if(portlet_name.equals("uoma") && cta.getEntidad().equals("U")){%>	
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 12) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
									<%}} %>
						</select>
						</span>
						<span id="<portlet:namespace />spanbancos2">
						<select id="<portlet:namespace />id_banco" name="<portlet:namespace />id_banco">
							<% 	for (Banco bco : bancos) {%>
									<option value="<%=bco.getId_banco()%>"><%=bco.getDescripcion_banco()%></option>							
							<%}%>
						</select>
						</span>
					
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
					<tr>
					<td>
						<span id="<portlet:namespace />spanAFavorDe">
							<liferay-ui:message key="a-nombre-de"/>:
						</span>
					</td>
					<td colspan="2">
						<span id="<portlet:namespace />spanAFavorDe2">
							<input size="100" type="text" value="<%=ordenPago!=null&&(null!=ordenPago.getCBU()&&ordenPago.getCBU().trim().equals(""))&&ordenPago.getAFavorDe()!=null?ordenPago.getAFavorDe():""%>" name="<portlet:namespace />a_favor_de" id="<portlet:namespace />a_favor_de"/>
						</span>
					</td>
					<td colspan="2">
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
				<jsp:include page='orden_pago_ospim_pagos_search_result.jsp' /></div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">	



	function <portlet:namespace />agregarPago(){
			jQuery('#<portlet:namespace />agregandoIngreso').show();	

			var aFavorDe = jQuery('#<portlet:namespace />a_favor_de').val();
			var nro=jQuery('#<portlet:namespace />nro_pago').val();
			var importe=jQuery('#<portlet:namespace />importe_pago').val();
			var tipo=jQuery('#<portlet:namespace />tipo_pago').val();
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();

			if (tipo != "<%=Anticipo.class.getName()%>"  && (trim(importe) == "" || !IsNumeric(importe))){
				alert("Debe completar el importe");
				jQuery('#<portlet:namespace />importe_pago').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();
				return false;
			} 
			if ((tipo == "<%=Cheque.class.getName()%>" || tipo == "<%=Anticipo.class.getName()%>") 
					&& (trim(nro) == "" || idCtaBcria == 0)){
				alert("Debe completar el numero.");
				jQuery('#<portlet:namespace />nro_pago').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			}
			
			if ((tipo == "<%=Cheque.class.getName()+Cheque.Estado.RECIBIDO%>") 
					&& (trim(nro) == "" || idCtaBcria == 0)){
				alert("Debe completar el numero.");
				jQuery('#<portlet:namespace />nro_pago').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			} 
			
			if(tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA %>"){
				if(nro.trim().length==22 && !validarCBU(trim(nro), "<liferay-ui:message key='valida-cbu'/>")){
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					return false;	
				}else if (nro.trim().length < 6){
					alert("El Alias debe tener entre 6 y 20 posiciones o 22 posiciones si es CBU.");
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					return false;
				}else if (nro.trim().length < 21 && !validarAlias(nro) ){
					alert("El Alias debe tener entre 6 y 20 posiciones, se aceptan números, caracteres, el . y - ");
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					return false;
				}else if (nro.trim().length == 21 ){
					alert("El CBU debe ser de 22 caraceteres. ");
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					return false;
				}else if (nro.trim().length > 22 ){
					alert("El CBU debe ser de 22 caraceteres. ");
					jQuery('#<portlet:namespace />agregandoIngreso').hide();
					return false;
				}
				
				
			}


			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago&nro=' +nro 
			+ '&importe_pago=' + importe
			+ '&esEdicion=' +"<%=esEdicion%>" 	
			+ '&modificaFormaPago=' +"<%=modificaFormaPago%>"
			+'&tipo=' + tipo
			+'&id_cta_bcria=' + idCtaBcria
			+'&aFavorDe=' + encodeURI(aFavorDe)
			+'&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														 jQuery('#<portlet:namespace />nro_pago').val("");
														 jQuery('#<portlet:namespace />importe_pago').val("");
														 changeTipo();
														 recalcularTotales();
										   }
			 );	
	}

	
	function agregarPagosCBU(){
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysLiquidaciones.ADD_FORMA_PAGO %>";
		
		jQuery('#<portlet:namespace />agregandoIngreso').show();	

		var aFavorDe = jQuery('#<portlet:namespace />a_favor_de').val();
		var nro=jQuery('#<portlet:namespace />nro_pago').val();
		var importe=jQuery('#<portlet:namespace />importe_pago').val();
		var tipo=jQuery('#<portlet:namespace />tipo_pago').val();
		var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();


		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago&nro=' +nro 
		+ '&importe_pago=' + importe
		+ '&esEdicion=' +"<%=esEdicion%>" 	
		+ '&modificaFormaPago=' +"<%=modificaFormaPago%>"
		+'&tipo=' + tipo
		+'&id_cta_bcria=' + idCtaBcria
		+'&aFavorDe=' + encodeURI(aFavorDe)
		+'&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
		url = url + params;	
		
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
													jQuery('#<portlet:namespace />agregandoIngreso').hide();	
													 jQuery('#<portlet:namespace />nro_pago').val("");
													 jQuery('#<portlet:namespace />importe_pago').val("");
													  changeTipo();
													 recalcularTotales();
									 			  }
		 );
	
		valor = "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA%>";
		jQuery("#<portlet:namespace />tipo_pago option[value="+valor+"]").attr("selected",true);

	}
	
	
	function borraPago(tipo, nro,  idCtaBcria, importe,cuit){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_op_pago'
			+  '&tipo=' +tipo
			+  '&nro=' +nro
			+  '&id_cta_bcria=' + idCtaBcria
			+  '&importe_pago=' + importe
			+  '&esEdicion=' + "<%=esEdicion%>" 
			+  '&modificaFormaPago=' +"<%=modificaFormaPago%>"
			+  '&cuit=' +cuit;
			url += '&rnd=' + Math.floor(Math.random()*100);			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						changeTipo();
																						recalcularTotales();
																			   }
															   );
	}
	
	function borraPago(tipo, nro,  idCtaBcria, importe,cuit,jurisdiccion){			
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_op_pago'
		+  '&tipo=' +tipo
		+  '&nro=' +nro
		+  '&id_cta_bcria=' + idCtaBcria
		+  '&importe_pago=' + importe
		+  '&esEdicion=' + "<%=esEdicion%>" 
		+  '&modificaFormaPago=' +"<%=modificaFormaPago%>"
		+  '&cuit=' +cuit
		+  '&jurisdiccion=' +jurisdiccion;
		url += '&rnd=' + Math.floor(Math.random()*100);	
		
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoIngreso').hide();
																					changeTipo();
																					recalcularTotales();
																		   }
														   );
  }
	
	jQuery('#<portlet:namespace />spanCbuNro').hide();
	function changeTipo(){
	 try{	
		 var tipo = document.getElementById("<portlet:namespace />tipo_pago").value;	 
		if (tipo == "<%=Cheque.class.getName()%>" ) {
			jQuery('#<portlet:namespace />spanBuscarCheques').show();
		} else {
			jQuery('#<portlet:namespace />spanBuscarCheques').hide();
		}
		if(tipo == "<%=Caja.class.getName() + Caja.ID_PAGO_CAJA %>" 
				|| tipo == "<%=Caja.class.getName() + Caja.ID_PAGO_CAJA_LOS_DIQUES%>"){			
			jQuery('#<portlet:namespace />spanImporte').show();
			jQuery('#<portlet:namespace />spanImporte2').show();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />spanNro2').hide();
			jQuery('#<portlet:namespace />spanNro').hide();
			jQuery('#<portlet:namespace />spanCbuNro').hide();
 			jQuery('#<portlet:namespace />spanbancos').hide();
 			jQuery('#<portlet:namespace />spanbancos2').hide();
		}
		if(tipo == "<%=Cheque.class.getName()+Cheque.Estado.RECIBIDO%>"){
			jQuery('#<portlet:namespace />spanctabcria').hide();			
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />spanbancos').show();
			jQuery('#<portlet:namespace />spanbancos2').show();
			jQuery('#<portlet:namespace />spanNro2').show();
			jQuery('#<portlet:namespace />spanNro').show();
			jQuery('#<portlet:namespace />spanCbuNro').hide();			
			jQuery('#<portlet:namespace />nro_pago').val("");
			jQuery('#<portlet:namespace />spanAFavorDe2').show();
			jQuery('#<portlet:namespace />spanAFavorDe').show();		
			jQuery('#<portlet:namespace />spanBuscarChequesCartera').show();
			jQuery('#<portlet:namespace />nro_pago').val("");			
		}else{
			jQuery('#<portlet:namespace />spanBuscarChequesCartera').hide();
		}
		if (tipo == "<%=Cheque.class.getName()%>" 
			|| tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_DEBITO_BANCARIO %>"
			|| tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_DEBITO_POR_AUTOGESTION %>"
			|| tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA %>" 
			|| tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TARJETA_RECARGA %>" 
			|| tipo == "<%=RetencionGanancias.class.getName()%>"
			|| tipo == "<%=RetencionIIBB.class.getName()+RetencionIIBB.BSAS %>"
			|| tipo == "<%=RetencionIVA.class.getName()%>"){
			jQuery('#<portlet:namespace />spanImporte').show();
			jQuery('#<portlet:namespace />spanImporte2').show();
			jQuery('#<portlet:namespace />spanctabcria').show();
			jQuery('#<portlet:namespace />spanctabcria2').show();
 			jQuery('#<portlet:namespace />spanbancos').hide();
 			jQuery('#<portlet:namespace />spanbancos2').hide();
			if ( tipo == "<%=RetencionGanancias.class.getName()%>" 
				|| tipo == "<%=RetencionIIBB.class.getName()+RetencionIIBB.BSAS %>"
					|| tipo == "<%=RetencionIVA.class.getName()%>"){
				jQuery('#<portlet:namespace />spanNro2').hide();
				jQuery('#<portlet:namespace />spanNro').hide();
				jQuery('#<portlet:namespace />spanCbuNro').hide();
				jQuery('#<portlet:namespace />spanctabcria').show();
				jQuery('#<portlet:namespace />spanctabcria2').show();				
			}
			if(tipo == "<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA %>"){
				jQuery('#<portlet:namespace />spanNro').hide();
				jQuery('#<portlet:namespace />spanNro2').show();
				jQuery('#<portlet:namespace />spanCbuNro').show();
				cbu_sugerido=jQuery('#<portlet:namespace />cbu_sugerido').val();
				jQuery('#<portlet:namespace />nro_pago').val(cbu_sugerido);	
			}else if(tipo != "<%=Cheque.class.getName()%>+Cheque.Estado.RECIBIDO"){
				jQuery('#<portlet:namespace />spanNro2').show();
				jQuery('#<portlet:namespace />spanNro').show();
				jQuery('#<portlet:namespace />spanCbuNro').hide();
				jQuery('#<portlet:namespace />spanctabcria').show();
				jQuery('#<portlet:namespace />spanctabcria2').show();
				jQuery('#<portlet:namespace />nro_pago').val("");
			}
			
			if(tipo == "<%=PagoBancario.class.getName() +PagoBancario.ID_PAGO_TARJETA_RECARGA%>"){
			    jQuery('#<portlet:namespace />spanNro').show();
			    jQuery('#<portlet:namespace />spanCbuNro').hide();
			    jQuery("#<portlet:namespace />id_cta_bcria").val(<%=cuentaTarjeta%>); 
			    sugerirTarjetaRecargable();
		    }
		} else if( tipo != "<%=Caja.class.getName()+ Caja.ID_PAGO_CAJA %>"
			    && tipo != "<%=Caja.class.getName() + Caja.ID_PAGO_CAJA_LOS_DIQUES %>"
			    && tipo != "<%=Cheque.class.getName()+Cheque.Estado.RECIBIDO%>" ){
			jQuery('#<portlet:namespace />spanNro2').show();
			jQuery('#<portlet:namespace />spanNro').show();
			jQuery('#<portlet:namespace />spanImporte').hide();
			jQuery('#<portlet:namespace />spanImporte2').hide();
			jQuery('#<portlet:namespace />spanctabcria').hide();
			jQuery('#<portlet:namespace />spanctabcria2').hide();
			jQuery('#<portlet:namespace />nro_pago').val("");
		}		

		if (tipo == "<%=Cheque.class.getName()%>"){
			jQuery('#<portlet:namespace />spanAFavorDe2').show();
			jQuery('#<portlet:namespace />spanAFavorDe').show();
			jQuery('#<portlet:namespace />spanCbuNro').hide();			
			sugerirNroCheque();
		} else {
			jQuery('#<portlet:namespace />spanAFavorDe2').hide();
			jQuery('#<portlet:namespace />spanAFavorDe').hide();			
		}
		
		if(tipo == "<%=PagoSinSalidaDeFondos.class.getName() + PagoSinSalidaDeFondos.ID_PAGO_A_UOMA %>"){			
		   jQuery('#<portlet:namespace />spanImporte').show();
		   jQuery('#<portlet:namespace />spanImporte2').show();
		   jQuery('#<portlet:namespace />spanctabcria').hide();
		   jQuery('#<portlet:namespace />spanctabcria2').hide();
		   jQuery('#<portlet:namespace />spanNro2').hide();
		   jQuery('#<portlet:namespace />spanNro').hide();
		   jQuery('#<portlet:namespace />spanCbuNro').hide();
		   jQuery('#<portlet:namespace />spanbancos').hide();
		   jQuery('#<portlet:namespace />spanbancos2').hide();
	    }
	 }catch(err){ }
	}

	function sugerirNroCheque(){
			var tipo = document.getElementById("<portlet:namespace />tipo_pago").value;
			if (tipo == "<%=Cheque.class.getName()%>"){
				var id = document.getElementById("<portlet:namespace />id_cta_bcria").value;
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_ultimo_nro_cheque&id_cta_bcria='+id;
				url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
				url += '&rnd=' + Math.floor(Math.random()*100);
		
				jQuery.ajax({   
					url: url,
					success: function(data){	
						var obj = jQuery.parseJSON(data);
						jQuery("#<portlet:namespace />nro_pago").val(parseInt(obj.numero) +1); 
					}
				});
			}
	}
	
	<%if (esEdicion){%>
	changeTipo();
	<%}%>

	function sugerirAFavorDe(){
			jQuery('#<portlet:namespace />a_favor_de').val(trim(jQuery('#<portlet:namespace />afiliado').val()));
	}
	
	jQuery('#<portlet:namespace />agregandoIngreso').hide();

	var popup;
	function <portlet:namespace />buscarChequesLiberados() {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="reutilizar-cheques" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 	<portlet:namespace />recargarPagos();
			 	}});        
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/reutilizar_cheques_orden_pago_ospim';
	    url += '&rnd=' + Math.floor(Math.random()*100);
	    url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
	    jQuery(popup).load(url);    
	}
	
	function <portlet:namespace />buscarChequesEnCartera() {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="reutilizar-cheques" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 <portlet:namespace />recargarPagos();
			 	}});        
	    var cuit=jQuery("#cuit_cartera").val();
	    var nro_cheque=jQuery("#nro_cheque_cartera").val();
	    var importe=jQuery("#importe_cheque_cartera").val();
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_cheques_recibidos';
	    url += '&cuit='+cuit+'&nro_cheque='+nro_cheque+'&importe='+importe;
	    url += '&rnd=' + Math.floor(Math.random()*100);
	    url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
	    jQuery(popup).load(url);
	}
	
	function <portlet:namespace />recargarPagos(){
		
		jQuery('#<portlet:namespace />agregandoIngreso').show();
		var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/recargar_orden_pago_ospim_pagos';
		url1 += '&rnd=' + Math.floor(Math.random()*100);
		url1 += '&modificaFormaPago=' +"<%=modificaFormaPago%>";
		jQuery('#<portlet:namespace />ingresos').load(url1 , function(){	
																	jQuery('#<portlet:namespace />agregandoIngreso').hide();
																	recalcularTotales();
																}
													);
	}
	
	function <portlet:namespace />agregarPagoVacio(){		
		jQuery('#<portlet:namespace />agregandoIngreso').show();

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago&vacio=si'		
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
													jQuery('#<portlet:namespace />agregandoIngreso').hide();	
													 jQuery('#<portlet:namespace />nro_pago').val("");
													 jQuery('#<portlet:namespace />importe_pago').val("");
													 changeTipo();
													 recalcularTotales();
									   }
		 );	
	}
	
/*	
	function validarCBU(input, message){				
		if(input.trim().length==22){
			a=input.substring(0,1);
			b=input.substring(1,2);
			c=input.substring(2,3);
			d=input.substring(3,4);
			
			q=input.substring(4,5);
			r=input.substring(5,6);
			s=input.substring(6,7);
			
			valida1=input.substring(7,8);
			//alert(a+' '+b+' '+c+' '+d+' '+q+' '+r+' '+s);
			
			suma1=a*7+b*1+c*3+d*9+q*7+r*1+s*3;
			cadenaVal=suma1.toString().substring(suma1.toString().length-1,suma1.toString().length);
			diferencia1= 10-parseInt(cadenaVal);
			
			if(diferencia1==10){
                diferencia1=0;
        	}
			
			if(valida1!=diferencia1){
				alert('ERROR AL VALIDAR CBU, VERIFIQUE NUMEROS');
				return false;				
			}
			
			a=input.substring(8,9);
			b=input.substring(9,10);
			c=input.substring(10,11);
			d=input.substring(11,12);
			e=input.substring(12,13);
			f=input.substring(13,14);
			g=input.substring(14,15);
			h=input.substring(15,16);
			i=input.substring(16,17);
			j=input.substring(17,18);
			k=input.substring(18,19);
			l=input.substring(19,20);
			m=input.substring(20,21);
			
			//alert(a+' '+b+' '+c+' '+d+' '+e+' '+f+' '+g+' '+h+' '+i+' '+j+' '+k+' '+l+' '+m);
			valida2=input.substring(21,22);
			
			suma2=a*3+b*9+c*7+d*1+e*3+f*9+g*7+h*1+i*3+j*9+k*7+l*1+m*3;
			
			cadenaVal2=suma2.toString().substring(suma2.toString().length-1,suma2.toString().length);
			diferencia2= 10-parseInt(cadenaVal2);			
			
			if(diferencia2==10){
                diferencia2=0;
        	}
			
			if(valida2!=diferencia2){
				alert('Ha ingresado un CBU inválido, por favor, verifique dígitos ingresados');
				return false;				
			}
			
			
			if(isPositiveInteger(input)){
				return true
			}		
		}
		alert(message);
		return false;		
	}
*/	
	function validarAlias(input){

		var patt = new RegExp("^[a-zA-Z0-9.-]+$");
		var res = patt.test(input);
		
		return res;
	}
	
	function sugerirTarjetaRecargable(){			
					
		var idSeccional = jQuery("#<portlet:namespace />sucursal_entidad").val();	
		if(idSeccional!=null && idSeccional!=""  && idSeccional!="0")
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_tarjeta_recargable_op';
            url += '&id_seccional=' + idSeccional;
        	url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				data=data.replace(/\n/g,"\\n");					
				var obj = jQuery.parseJSON(data);					
				jQuery("#<portlet:namespace />cbu_sugerido").val(obj.tarjeta);
				jQuery('#<portlet:namespace />nro_pago').val(obj.tarjeta);		
			}
		});
		
	}
	
	
	<% if (ordenPago  != null
			&& reintegrosAll !=null && !reintegrosAll.isEmpty() && esEdicion) {%>
			agregarPagosCBU();

	<%}%>
	
	
	//Aqui
	
</script>
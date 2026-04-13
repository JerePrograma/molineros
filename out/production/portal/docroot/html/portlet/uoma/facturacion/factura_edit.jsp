<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@page import="ar.com.ospim.util.StringUtils"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects/>
<%
 Factura factura = (Factura)portletSession.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION,PortletSession.APPLICATION_SCOPE); 

boolean showFacturaManualUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_FACTURACION_MANUAL);


String portlet_name=null;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_HOT_1_")){
	portlet_name = "hoteles";
}

/* String esEdicionStr=ParamUtil.getString(request,"esEdicion"); */
 String esEdicionStr=(String) request.getAttribute("esEdicion");
boolean esEdicion = true;

if (esEdicionStr != null && esEdicionStr.equalsIgnoreCase("esEdicion")){
	esEdicion = true;
}

Afiliado afiliado = null;

Calendar emisionFecha = CalendarFactoryUtil.getCalendar();
Calendar vtoCAEfecha = CalendarFactoryUtil.getCalendar();

if(factura.getFecha()!=null){
	emisionFecha.setTime(factura.getFecha());
}else{
	emisionFecha.setTime(new Date());
}
if(factura.getFechaCae()!=null){
	vtoCAEfecha.setTime(factura.getFechaCae());
}else{
	vtoCAEfecha.setTime(new Date());
}

List<Producto> productos = (ArrayList<Producto>) request.getSession().getAttribute(WebKeysUOMA.PRODUCTOS_EN_SESSION);

String ptoVtaAfip="00030";

try{
	ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
}catch(Exception e){
	ptoVtaAfip="00030";
}


%>
<form action="" id="<portlet:namespace />fm_fc" name="<portlet:namespace />fm_fc">	

<liferay-ui:success key="insertFacturaOk"  message="<%=(String)request.getAttribute(\"msgFacturaOk\")  %>"  />

<liferay-ui:error exception="<%= ImposibleObtenerTokenAFIPLoginException.class %>" message="error-al-buscar-token" />
<liferay-ui:error exception="<%= ImposibleObtenerCAEAFIPException.class %>" message="error-al-buscar-cae-afip" />
<liferay-ui:error key="error-factura-sin-detalle" message="error-fc-sin-detalle" />	
<liferay-ui:error key="error-cliente-sin-factura" message="error-cl-sin-factura" />	
<liferay-ui:error key="error-factura-sin-fpago" message="error-fc-sin-fpago" />	
<liferay-ui:error key="error-factura-total-fpago" message="error-fc-total-fpago" />
<div id="<portlet:namespace />msg_error" style="background-color:#FACFD4;border-style: solid;border-color: red;" ></div>
<div id="<portlet:namespace />msg_ok" style="background-color:#CFFAD9;border-style: solid;border-color: green;" ></div>
<input type="hidden" id="id_cliente"/>	
<input type="hidden" id="id_reserva"/>	

<input type="hidden" id="id_factura" name="id_factura" value="<%=factura.getId() %>"/>	

<fieldset class="block-labels">
	<% if(ptoVtaAfip == null || ptoVtaAfip.equalsIgnoreCase("00000")){ %>
		<p>No tiene configurado un punto de venta</p>
	<% } %>
	<legend><liferay-ui:message key="datos-factura" /></legend>
	
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		
		<tr>
			<td>
				<fieldset class="block-labels">
				<legend><liferay-ui:message key="fc-cliente" /></legend>		
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td>
							<input type="radio" name="<portlet:namespace/>tipo_cliente" value="0" checked="checked"
							onchange="javascript:<portlet:namespace />controlaTipoCliente(); <portlet:namespace/>cambiarPosicionIVA();" <% if (!esEdicion) { %>readonly="readonly"<%} %> >Consumidor Final &nbsp;
							<input type="radio" name="<portlet:namespace/>tipo_cliente" value="1"
						    onchange="javascript:<portlet:namespace />controlaTipoCliente();  <portlet:namespace/>cambiarPosicionIVA();" <% if (!esEdicion) { %>readonly="readonly"<%} %> >Comercial &nbsp;	 
						</td>
						<%if(portlet_name.equalsIgnoreCase("UOMA")) {%>
						<td align="right">
							<input type="button" value="Buscar Reserva" onclick="<portlet:namespace/>buscarReservasVigentesHotel(' <%=ptoVtaAfip %> ');" > &nbsp;
						</td>
						<%} else { %>
						
						<td>&nbsp;</td>
						<%} %>
					</tr>
					<tr>
						<td style="width: 750px;"> 
							<div id="<portlet:namespace/>tipo_cliente_p_fisica">
								<liferay-util:include page="/html/portlet/uoma/facturacion/busqueda_pers_fisica.jsp">
									<liferay-util:param name="cliente_nro_doc"
										value="<%= factura.getCliente() != null ? String.valueOf(factura.getCliente().getDocumentoNro()) : new String()  %>" />
									<liferay-util:param name="cliente_apellido"
										value="<%= factura.getCliente() != null ? String.valueOf(factura.getCliente().getApellido()) : new String()  %>" />
									<liferay-util:param name="cliente_nombre"
										value="<%= factura.getCliente() != null ? String.valueOf(factura.getCliente().getNombre()) : new String()  %>" />	
									<liferay-util:param name="esEditable" value='<%=String.valueOf("false")%>'/>
								</liferay-util:include>
							</div>
							<div id="<portlet:namespace/>tipo_cliente_empresa">
								<liferay-util:include page="/html/portlet/uoma/facturacion/busqueda_padron_entidades.jsp">
							  		<liferay-util:param name="esEditable" value='<%=String.valueOf("true")%>'/>
							  		<liferay-util:param name="cuit" value='<%= factura.getCliente() != null && factura.getCliente().getCuit() != null ? factura.getCliente().getCuit() : new String("")%>'/>
							  		<liferay-util:param name="sucu" value='<%=factura.getCliente() != null && factura.getCliente().getSucursal() != null ? factura.getCliente().getSucursal() : new String("") %>'/>
							  		<liferay-util:param name="razon" value='<%=factura.getCliente() != null && factura.getCliente().getRazonSocial() != null ? factura.getCliente().getRazonSocial() : new String("") %>'/>
							  		<liferay-util:param name="esEmpresaPrestador" value='true' />
								</liferay-util:include>
							</div>
						</td>
					</tr>
					<tr>
							
					<td><label><liferay-ui:message key="posicion-iva"/>:</label>
									
							
			
						<select <% if (!esEdicion) { %>
									<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>iva"
									id="<portlet:namespace/>iva"  onchange="<portlet:namespace/>cambiarPosicionIVA(); "   >				  							
						</select>
							
					</td>	
					    
					    <% if (esEdicion && showFacturaManualUOMA) { %>
					    <td><label><liferay-ui:message key="fc-manual" />:</label></td>
						<td><input type="checkbox" name="<portlet:namespace/>fc_manual"  id="<portlet:namespace/>fc_manual"
							onchange="<portlet:namespace/>controlFCmanual();" checked="checked" ></td>
						<% } %>	
					    <td><label><liferay-ui:message key="fc-form8001" />:</label></td>
						<td><input type="checkbox" name="<portlet:namespace/>fc_form8001"  id="<portlet:namespace/>fc_form8001" 
							<%if(factura!=null&& factura.isPresentaForm8001()){%> checked="checked" <%} %>></td>
				</table>
				</fieldset>
				<!-- <div>
					<liferay-util:include page="/html/portlet/uoma/facturacion/factura_detalle_search_result.jsp">
						</liferay-util:include>
					
					</fieldset>
				</div>	 -->
			</td>	

		<tr>
			<td>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><liferay-ui:message key="fc-tipo" /> </td> 
						<td><select name="<portlet:namespace />fc_tipo" id="<portlet:namespace />fc_tipo"   onchange="<portlet:namespace/>cambiar_fc_tipo(); " readonly="readonly"  <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%> >
								<option value="<%=WebKeysGlobal.COMPROBANTE_FACTURA%>" <%if(factura.getTipo()!=null && factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)){ %> selected="selected" <%} %> ><%=WebKeysGlobal.COMPROBANTE_FACTURA %></option>
								<option value="<%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO%>" <%if(factura.getTipo()!=null && factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO)){ %> selected="selected" <%} %>><%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO %></option>
								<option value="<%=WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO%>" <%if(factura.getTipo()!=null && factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)){ %> selected="selected" <%} %> ><%=WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO %></option>
								<option value="<%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA%>" <%if(factura.getTipo()!=null && factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)){ %> selected="selected" <%} %>><%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA %></option>
							</select>  
						</td>
						<td><liferay-ui:message key="fc-letra" /> </td> 
						<td><select name="<portlet:namespace />fc_letra" id="<portlet:namespace />fc_letra"  disabled='true' <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>  onchange="<portlet:namespace/>cambiarLetraFC();" >
								<option value="B" <%if(factura.getLetra()!=null && factura.getLetra().equalsIgnoreCase("B")){ %> selected="selected" <%} %> >B</option>
								<option value="A" <%if(factura.getLetra()!=null && factura.getLetra().equalsIgnoreCase("A")){ %> selected="selected" <%} %>>A</option>
								<option value="T" <%if(factura.getLetra()!=null && factura.getLetra().equalsIgnoreCase("T")){ %> selected="selected" <%} %>>T</option>
							</select>  
						</td>
						<td><liferay-ui:message key="fc-suc" /> </td> 
						<td><select name="<portlet:namespace />fc_sucursal" id="<portlet:namespace />fc_sucursal" <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>>
								<!-- 
								<%if(factura.getSucursal()!=null && factura.getSucursal().equalsIgnoreCase("00010")){ %> selected="selected" <%} %>>EVA PERON</option>
								<%if(factura.getSucursal()!=null && factura.getSucursal().equalsIgnoreCase("00020")){ %> selected="selected" <%} %>>LOS DIQUES</option>
								<%if(factura.getSucursal()!=null && factura.getSucursal().equalsIgnoreCase("00030")){ %> selected="selected" <%} %>>MAR DEL PLATA</option>
								 -->
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(factura.getSucursal()!=null && factura.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(factura.getSucursal()!=null && factura.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
							</select></td>
						<td><liferay-ui:message key="fc-nro" /> </td>
						<td><input type="text" name="<portlet:namespace />fc_numero" 
											id="<portlet:namespace />fc_numero" value="<%=factura.getNumero()!=null?factura.getNumero():"" %>" 
										onkeydown="allowOnlyDigits(event)"	>  </td> <!-- readonly="readonly"  --> 
						<td>
							<label><liferay-ui:message key="fecha-emision" />:</label>
									<liferay-ui:input-date
										dayParam="fechaEmisionDia"
										dayValue="<%= emisionFecha.get(Calendar.DATE)%>"
										monthParam="fechaEmisionMes"
										monthValue="<%= emisionFecha.get(Calendar.MONTH) %>"
										yearParam="fechaEmisionAnio"
										yearValue="<%= emisionFecha.get(Calendar.YEAR) %>"
										yearRangeStart="<%= emisionFecha.get(Calendar.YEAR)-1 %>"
										yearRangeEnd="<%= emisionFecha.get(Calendar.YEAR)  %>"
										firstDayOfWeek="<%= emisionFecha.getFirstDayOfWeek()%>"
										disabled = "<%=true %>"
										/> 
						</td>				
					</tr>
				</table>
			</td>
		</tr>	
		<tr>
			<td>
			   <% if (esEdicion && showFacturaManualUOMA) { %>
				<fieldset class="block-labels">
				<legend><liferay-ui:message key="fc-det" /></legend>	
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">

					<tr>
						<td>
							<select name="<portlet:namespace />fc_det_codigo" 
									id="<portlet:namespace />fc_det_codigo" >
								<option value=""></option>	
								<%for(Producto pr : productos)  {%>
								<option value="<%=pr.getId()%>"><%=pr.getDescripcion() %></option>
								<%} %>
							</select>
						</td>
						<td><liferay-ui:message key="precio-uni" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_precio_unit" 
						id="<portlet:namespace />fc_precio_unit" 
						onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);">  </td>
						<% if (esEdicion) { %>
							<td><input type="button" value="Agregar Detalle" onClick="<portlet:namespace />agregarDetalleFC();">  </td>
						<%}else{ %>
							<td>&nbsp;</td>
						<%} %>
					</tr>
					
					<tr>
					 <td colspan="20">
					  
				      </td>
				    </tr>  
				</table>
				</fieldset>
				
			  <%}%>	
			</td>			
		</tr>
		
		
		<tr>
		  <td colspan="15">
		   <div id="<portlet:namespace />lista_productos">
				<liferay-util:include page="/html/portlet/uoma/facturacion/factura_detalle_search_result.jsp">
				   <liferay-util:param name="esEdicion" value="esEdicion"/>
				</liferay-util:include>
						
		   </div>
		
		  </td>
		</tr>
		
		
		<tr>
			<td>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><liferay-ui:message key="cae" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_cae_nro" id="<portlet:namespace />fc_cae_nro"  
								value="<%=factura.getCae()!=null?factura.getCae():""%>" 
								onkeydown="allowOnlyDigits(event)" ></td> <!-- readonly="readonly" -->
						<td><liferay-ui:message key="fc-cae-vto" /></td>
						 
						<td>
							<%if(!esEdicion){ %>
							<liferay-ui:input-date
								dayParam="fechaVtoCAEDia"
								dayValue="<%= vtoCAEfecha.get(Calendar.DATE)%>"
								monthParam="fechaVtoCAEMes"
								monthValue="<%= vtoCAEfecha.get(Calendar.MONTH) %>"
								yearParam="fechaVtoCAEAnio"
								yearValue="<%= vtoCAEfecha.get(Calendar.YEAR) %>"
								yearRangeStart="<%= vtoCAEfecha.get(Calendar.YEAR) %>"
								yearRangeEnd="<%= vtoCAEfecha.get(Calendar.YEAR)+1  %>"
								firstDayOfWeek="<%= vtoCAEfecha.getFirstDayOfWeek()%>"
								disabled = "<%=true %>"
								/>
							<%}else{ %>

								<input type="text" readonly="readonly" value="" id="<portlet:namespace />mostrarVtoCAEtxt">
								
								<div id="<portlet:namespace />mostrarVtoCAEmanual">
									<liferay-ui:input-date
											dayParam="fechaVtoCAEDia"
											dayValue="<%= vtoCAEfecha.get(Calendar.DATE)%>"
											monthParam="fechaVtoCAEMes"
											monthValue="<%= vtoCAEfecha.get(Calendar.MONTH) %>"
											yearParam="fechaVtoCAEAnio"
											yearValue="<%= vtoCAEfecha.get(Calendar.YEAR) %>"
											yearRangeStart="<%= vtoCAEfecha.get(Calendar.YEAR)-1 %>"
											yearRangeEnd="<%= vtoCAEfecha.get(Calendar.YEAR)+1  %>"
											firstDayOfWeek="<%= vtoCAEfecha.getFirstDayOfWeek()%>"
											disabled = "<%=false %>"
										/>
								
								</div>
							<%} %>	 
						</td>
					</tr>
				</table>		
			</td>
		</tr>
		<input type="hidden" id="capitalIngresoTmp" value="0.00"/>
		<input type="hidden" id="<portlet:namespace />idFactura" value=""/>
		
		<tr>
			<td>
			 <fieldset class="block-labels">
	           <legend>Anticipos</legend>
			   <div id="<portlet:namespace />mostrar_adelantos">	
				<liferay-util:include page="/html/portlet/uoma/facturacion/adelantos_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="ptoVtaAfip" value="<%=String.valueOf(ptoVtaAfip) %>"/>
				</liferay-util:include>			
			   </div>
			 </fieldset> 
			</td>
			
		</tr>
		
		
		<tr>
			<td>
			
			  <div id="<portlet:namespace />mostrar_ingresos">	
				<liferay-util:include page="/html/portlet/uoma/facturacion/formas_ingreso_agregar.jsp">
				    <liferay-util:param name="esEdicion" value="true"/>
					<!--  <liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>-->
					<liferay-util:param name="ptoVtaAfip" value="<%=String.valueOf(ptoVtaAfip) %>"/>
				</liferay-util:include>			
			  </div>
			
			</td>
			
		</tr>
	</table>
	
	<div id="<portlet:namespace/>agregar_observaciones">	

		 <tr>
		 	
			<td>
			<label><liferay-ui:message key="observaciones" />:</label>
			</td>
			<td colspan="7"><textarea cols="100"
					<% if (!esEdicion) { %> <%="disabled='disabled'" %> <%}%>
					name="<portlet:namespace/>obs" 
					id="<portlet:namespace/>obs"><%= factura!=null && factura.getObservaciones() != null  ? factura.getObservaciones() : new String("") %></textarea>
			</td>
		</tr>
	</div>
	
	
</fieldset>	
<br/>


 <%if (esEdicion){ %> 
    	
	<%if(!"hoteles".equalsIgnoreCase(portlet_name)) {%>
	
	  <input id="<portlet:namespace />modificaFC"
		value="Guardar"
		title="Guardar modificación de Factura"
		onClick="javascript: <portlet:namespace />salvarModificacion();"
		type="button"  />
<!--  		
	  <input id="<portlet:namespace />obtenerCae"
		value="<liferay-ui:message key="Obtener Cae"/>"
		title="<liferay-ui:message key="Obtener Cae" />"
		<%-- onClick="javascript: <portlet:namespace />solicitarCAE();" --%>
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button"  />
-->
   <%}else{%>
      <input id="<portlet:namespace />obtenerCaeHotel"
		value="<liferay-ui:message key="Obtener Cae"/>"
		title="<liferay-ui:message key="Obtener Cae" />"
		<%-- onClick="javascript: <portlet:namespace />solicitarCAE();" --%>
		onClick="javascript: <portlet:namespace />obtenerCaeHoteles();"
		type="button"  />
		
		
	  <input id="<portlet:namespace />imprimirTicketFC"
		value="Imprimir Ticket"
		title="<liferay-ui:message key="Imprimir" />"
		onClick="javascript: <portlet:namespace />imprimirTicketFactura();"
		type="button"  />
   <%}%>
  			
 <%}%>
	
<% if(user.getScreenName().equalsIgnoreCase("uoma")){ %>
	<input id="<portlet:namespace />imprimirTicketFC1"
		value="Imprimir Ticket"
		title="<liferay-ui:message key="Imprimir" />"
		onClick="javascript: <portlet:namespace />imprimirTicketFacturaBorrar();"
		type="button"  />
 <%}%>
 	
 <%if(!esEdicion && StringUtils.checkNotEmpty(factura.getCae())){ %>		
	
	<input id="<portlet:namespace />imprimirFC"
		value="<liferay-ui:message key="Imprimir"/>"
		title="<liferay-ui:message key="Imprimir" />"
		onClick="javascript: <portlet:namespace />imprimirFactura();"
		type="button"  />
	
	
 <%} %>	
</form>	
<script type="text/javascript">



jQuery("#<portlet:namespace />fc_iva_21_div").hide();
jQuery("#<portlet:namespace />msg_error").hide();
jQuery("#<portlet:namespace />msg_ok").hide();

jQuery("#<portlet:namespace />imprimirTicketFC").hide();

<%
if(factura!=null && factura.getCliente()!=null){
	if(StringUtils.checkEmpty(factura.getCliente().getCuit())){
%>
		jQuery('input:radio[name=<portlet:namespace/>tipo_cliente]')[0].checked = true;
<%	}else{ %>
		jQuery('input:radio[name=<portlet:namespace/>tipo_cliente]')[1].checked = true;
		
<%	}
}%>
<portlet:namespace />controlaTipoCliente();



function <portlet:namespace />saveFactura() {
		var cFcTipo = jQuery("#<portlet:namespace />fc_tipo").val();

		if (<portlet:namespace />validarCampos()) {	
			
/* 			if (cFcTipo == "NCR" ){
				agregarIngreso();		
			} */
			
			
			var params = "&<%= Constants.CMD %>=" + "<%= Constants.SAVE %>";

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';
			url += '&id=' + id
			url = url + params;
		
			document.<portlet:namespace />fm_fc.method = 'post';
			submitForm(document.<portlet:namespace />fm_fc, url);		
		} 
}
	
function <portlet:namespace />validarCampos() {		
		
		var tipo_cli=jQuery("input[name='<portlet:namespace/>tipo_cliente']:checked").val();
		var cli_doc=jQuery('#<portlet:namespace />cliente_nro_doc').val();
		var cli_ape=jQuery('#<portlet:namespace />cliente_apellido').val();
		var cli_nom=jQuery('#<portlet:namespace />cliente_nombre').val();
		var cuit_ent=jQuery('#<portlet:namespace />cuit_entidad').val();
		var chForm8001 = jQuery("#<portlet:namespace/>fc_form8001").is(':checked');
		var manualCh=jQuery("#<portlet:namespace/>fc_manual").is(":checked");
		var nroFactura = jQuery("#<portlet:namespace />fc_numero").val();
		var caeFactura = jQuery("#<portlet:namespace/>fc_cae_nro").val();
		
		if (chForm8001 && tipo_cli == 0){
			alert("Debe seleccionar un cliente como empresa (por cuit) para facturar");
			return false;
		}
		
		if(tipo_cli == 0 && (trim(cli_doc).length == 0 || trim(cli_ape).length == 0 || trim(cli_nom).length == 0)){
			alert("Debe seleccionar un cliente para facturar");
			return false;
		}
		
		if(tipo_cli == 1 && (trim(cuit_ent).length == 0 || trim(cuit_ent).length < 11) ){
			alert("Debe seleccionar una empresa para facturar");
			return false;
		}
		
		if(manualCh == true && (trim(nroFactura).length == 0 || nroFactura == 0) ){
			alert("Debe completar el número de factura si está cargando factura manual");
			return false;
		}
		
		if(manualCh == true && (trim(caeFactura).length == 0 || caeFactura == 0) ){
			alert("Debe completar el CAE de factura si está cargando factura manual");
			return false;
		}
		
		if (chForm8001){
			alert("Atención no presentó el formulario 8001");
		}
		
		return true;
}


function filtrarCategoriaIVA() {
	var tipo_cliente=jQuery("input[name='<portlet:namespace/>tipo_cliente']:checked").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/filtrarCategoriaIVA&tipo_cliente='+tipo_cliente;
	jQuery("#<portlet:namespace/>iva").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>iva").length = 0;
			jQuery("#<portlet:namespace/>iva").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			
			jQuery('#<portlet:namespace />iva').html(data).fadeIn();

		}
	});
}



function filtrarLocalidad() {		
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;	
				jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
				jQuery('.selector-localidad select').html(data).fadeIn();
			}
		});		
}
	
function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_localidad_codpostal&idLocalidad='+idLocalidad;		
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
}

function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
}	
	
function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
}



function <portlet:namespace />controlaTipoCliente(){
	var sel = jQuery("input[name='<portlet:namespace/>tipo_cliente']:checked").val();
	var condIVA = jQuery('#<portlet:namespace />imp_iva').val();

	var condIVA_Aux = '<%= factura!=null && factura.getCliente()!=null && factura.getCliente().getCategoriaIVA()!=null? factura.getCliente().getCategoriaIVA():""%>';

	if(condIVA=='' && condIVA_Aux!=''){
		condIVA=condIVA_Aux;
		jQuery('#<portlet:namespace />imp_iva').val(condIVA)
	}
	
	filtrarCategoriaIVA();  
	if (sel == 1){
		if(condIVA == "RI" || condIVA == "AC" || condIVA == ""){
			jQuery("#<portlet:namespace />iva option[value='RI']").attr("selected",true);
			if(condIVA_Aux==''){
			  jQuery("#<portlet:namespace />fc_letra option[value='A']").attr("selected",true);
			}  
			jQuery("#<portlet:namespace />fc_iva_21_div").show();
		}
		if(condIVA == "EX"){
			jQuery("#<portlet:namespace />iva option[value='EX']").attr("selected",true);
			if(condIVA_Aux==''){
			  jQuery("#<portlet:namespace />fc_letra option[value='B']").attr("selected",true);
			}  
			jQuery("#<portlet:namespace />fc_iva_21_div").hide();
		}
    	jQuery('#<portlet:namespace/>tipo_cliente_p_fisica').hide();
    	jQuery('#<portlet:namespace/>tipo_cliente_empresa').show();
		jQuery('#<portlet:namespace/>fc_form8001').removeAttr('disabled');

      }else{
       	jQuery('#<portlet:namespace/>tipo_cliente_p_fisica').show();
    	jQuery('#<portlet:namespace/>tipo_cliente_empresa').hide();
    	if(condIVA_Aux==''){
		  jQuery("#<portlet:namespace />iva option[value='CS']").attr("selected",true);
		  jQuery("#<portlet:namespace />fc_letra option[value='B']").attr("selected",true);
    	}  
		jQuery("#<portlet:namespace />fc_iva_21_div").hide();
		jQuery('#<portlet:namespace/>fc_form8001').attr("disabled","disabled");
/* 		<portlet:namespace/>cambiarPosicionIVA();
	 	filtrarCategoriaIVA(); */
    }
	/* <portlet:namespace/>cambiarPosicionIVA(); */
 	/* filtrarCategoriaIVA(); */
} 

/* function <portlet:namespace/>cambiarLetraFC(){
	var le = jQuery("#<portlet:namespace />fc_letra").val();
	
	if(le=="A"){	
		jQuery("#<portlet:namespace />iva option[value='RI']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").show();
	}else{
		jQuery("#<portlet:namespace />iva option[value='CS']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").hide();
	}
}  */

function <portlet:namespace/>cambiar_fc_tipo(){
	var cFcTipo = jQuery("#<portlet:namespace />fc_tipo").val();

	if (cFcTipo == "NCR"  || cFcTipo == "NCE"){
		jQuery("#<portlet:namespace />mostrar_ingresos").show();
//		jQuery("#<portlet:namespace />mostrar_ingresos").hide();
	}else{
		jQuery("#<portlet:namespace />mostrar_ingresos").show();

	}
	
} 


function <portlet:namespace/>cambiarPosicionIVA(){
	var cIva = jQuery("#<portlet:namespace />iva").val();

	if(cIva=="RI"){	
		jQuery("#<portlet:namespace />fc_letra option[value='A']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").show();
	}
	if(cIva=="CS"){
		jQuery("#<portlet:namespace />fc_letra option[value='B']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").hide();
		
	}
	if(cIva=="EX"){
		jQuery("#<portlet:namespace />fc_letra option[value='B']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").hide();
		
	}
	if(cIva=="null"){
		jQuery("#<portlet:namespace />fc_letra option[value='B']").attr("selected",true);
		jQuery("#<portlet:namespace />fc_iva_21_div").hide();
	}
	<portlet:namespace />recalcularDetalleFC();
} 

/* function filtrarConceptosUOMA(){		
} */

function <portlet:namespace />agregarDetalleFC(){
	
	var codigo_det = jQuery('#<portlet:namespace />fc_det_codigo').val();
	var descri_det = jQuery('#<portlet:namespace />fc_det_codigo option:selected').text();
	var precio_det = jQuery('#<portlet:namespace />fc_precio_unit').val();
	var cond_iva = jQuery('#<portlet:namespace/>iva').val();
	var cliente_tipo = jQuery('#<portlet:namespace />persfisica_tipo').val();
		
	if(codigo_det == ''){
		alert("Debe seleccionar un detalle");
		return false;
	}
	if(precio_det== '' || precio_det == 0){
		alert("Debe ingresar el importe del producto");
		return false;
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/lista_detalles_fc';
	var params = {"codigo" : codigo_det,
				  "descripcion" : descri_det,
				  "precio" : precio_det,
				  "condIva" : cond_iva,
				  "clienteTipo" : cliente_tipo,
				  "cmd" : "add"} ;
	
	jQuery("#<portlet:namespace />lista_productos").load(url,params, function() { 
		jQuery("#aaa").hide() ;
		jQuery('#<portlet:namespace />fc_det_codigo option[value=""]').attr("selected", true);
		jQuery('#<portlet:namespace />fc_precio_unit').val("");
		jQuery('#<portlet:namespace />importe').val(precio_det);
		});
}

function eliminarDetalleFC(idDet){

	var codigo_det = jQuery('#<portlet:namespace />fc_det_codigo').val();
	var cond_iva = jQuery('#<portlet:namespace/>iva').val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/lista_detalles_fc';

	var params = {"idDetalle" : idDet,
				  "condIva" : cond_iva,
				  "cmd" : "delete"} ;
	
	jQuery("#<portlet:namespace />lista_productos").load(url,params, function() {
		jQuery('#<portlet:namespace />importe').val("");
	});
}


function eliminarAllDetalleFC(){

	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/lista_detalles_fc';
	<%--var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/lista_detalles_fc" /></portlet:renderURL>';--%>

	<%-- var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/lista_detalles_fc'; --%>
	var params = {"cmd" : "cancel"} ;
	
	jQuery("#<portlet:namespace />lista_productos").load(url,params);
}


function <portlet:namespace />salvarEdicion(){
	
	window.onbeforeunload = null;
	if (<portlet:namespace />validarCampos()) {	
	//	var cFcTipo = jQuery("#<portlet:namespace />fc_tipo").val();

	//	if (cFcTipo == "NCR" ){
	//		<portlet:namespace />agregarIngreso();
	//	}
		
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.SAVE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';				
		url += '&fc_letra=' + jQuery("#<portlet:namespace />fc_letra").val();
		url += '&iva=' + jQuery("#<portlet:namespace />iva").val();
		url = url + params;
		document.<portlet:namespace />fm_fc.method = 'post';
		submitForm(document.<portlet:namespace />fm_fc, url);
		
	}	
	
	
}

function <portlet:namespace />imprimirFactura() {
	
	window.location.href ='/pdfservlet/?accion=generarFacturaUOMA'
		+'&id_factura='+<%=factura.getId() %>
		+'&id_ejemplar=1'
		+'&mostrarRazonSoc=true'
	
}

function <portlet:namespace />buscarReservasVigentesHotel(puntoVentaHotel){
	var ptoVta='<%=ptoVtaAfip%>';
	jQuery("#<portlet:namespace />cliente_nro_doc").val("");
	jQuery("#<portlet:namespace />cliente_apellido").val("");
	jQuery("#<portlet:namespace />cliente_nombre").val("");
	eliminarAllDetalleFC();

	var fcSucursal = jQuery("#<portlet:namespace />fc_sucursal").val();
	
	if (puntoVentaHotel == 9999) {
		ptoVta = fcSucursal;
	}	
	
	popupHOT = Liferay.Popup({title:"BUSCAR UNA RESERVA DEL HOTEL",modal:true,width:700,position:[150,100],xy: ['center', 100],
		 onClose: function() {
			var id_cliente = jQuery("#id_cliente").val();	
			var id_reserva = jQuery("#id_reserva").val();				
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_reservas_vigentes_hotel';
			    url +='&ptovta='+ptoVta;
				url +='&id_cliente='+id_cliente;
				url +='&id_reserva='+id_reserva;
			    jQuery.ajax({   
					url: url,
					async:false,
					success: function(data){
						var obj = jQuery.parseJSON(data);
					    if(id_cliente != ''){
							jQuery("#<portlet:namespace />cliente_nro_doc").val("");
							
							if (obj.consumos != '0') {
								jQuery('#<portlet:namespace />fc_det_codigo').val('3');
								jQuery('#<portlet:namespace />fc_precio_unit').val(obj.consumos);
								<portlet:namespace />agregarDetalleFC();
							}	
							if (obj.reserva != '0') {	
								jQuery('#<portlet:namespace />fc_det_codigo').val('1');
								jQuery('#<portlet:namespace />fc_precio_unit').val(obj.reserva);
								<portlet:namespace />agregarDetalleFC();
								<portlet:namespace />agregarAnticiposFC();
								
							}
							
							
							jQuery("#id_cliente").val('0');
							jQuery("#id_reserva").val('0');
						}
					}
				});
	 	}});
	
    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/facturacion_editar';
    url +='&ptovta='+ptoVta;
    url +='&cmd='+'reservas';
    url += '&rnd=' + Math.floor(Math.random()*100);
	

   
    jQuery(popupHOT).load(url);

	
}


function <portlet:namespace/>controlFCmanual() {
	var manualCh=jQuery("#<portlet:namespace/>fc_manual").is(":checked");

	if(manualCh == false){

		jQuery("#<portlet:namespace />fc_numero").attr('readonly', true);
		
		jQuery("#<portlet:namespace/>fc_cae_nro").attr("readonly", true);
		
		jQuery("#<portlet:namespace/>mostrarVtoCAEmanual").hide();
		
		jQuery("#<portlet:namespace />mostrarVtoCAEtxt").show();
		
		jQuery("#<portlet:namespace />fechaEmisionDia").attr('disabled', true);
		jQuery("#<portlet:namespace />fechaEmisionMes").attr('disabled', true);
		jQuery("#<portlet:namespace />fechaEmisionAnio").attr('disabled', true);
		
	}else{
		
		jQuery("#<portlet:namespace />fc_numero").attr('readonly', false);
		
		jQuery("#<portlet:namespace/>fc_cae_nro").attr("readonly", false);
		
		jQuery("#<portlet:namespace/>mostrarVtoCAEmanual").show();
		
		jQuery("#<portlet:namespace />mostrarVtoCAEtxt").hide();
		
		jQuery("#<portlet:namespace />fechaEmisionDia").attr('disabled', false);
		jQuery("#<portlet:namespace />fechaEmisionMes").attr('disabled', false);
		jQuery("#<portlet:namespace />fechaEmisionAnio").attr('disabled', false);
		
	}

	
}

<portlet:namespace/>controlFCmanual();
<portlet:namespace/>cambiar_fc_tipo();

function <portlet:namespace />obtenerCaeHoteles(){
	
	var clienteNroDoc= jQuery('#<portlet:namespace />cliente_nro_doc').val();
	var clienteApe= jQuery('#<portlet:namespace />cliente_apellido').val();
	var clienteNom= jQuery('#<portlet:namespace />cliente_nombre').val();
	var clienteTipo= jQuery("input[name='<portlet:namespace/>tipo_cliente']:checked").val();
	var cuit= jQuery('#<portlet:namespace />cuit_entidad').val();
	var presForm8001 = jQuery("#<portlet:namespace/>fc_form8001").is(':checked');
	
	
	var clienteEstado= jQuery("#<portlet:namespace />persfisica_estado").val();
	var sucursal= jQuery("#<portlet:namespace />sucursal_entidad").val();
	var razonSocial = jQuery("#<portlet:namespace />entidad").val();
	var iva = jQuery("#<portlet:namespace />iva").val();
	var fcTipo = jQuery("#<portlet:namespace />fc_tipo").val();
	var fcLetra = jQuery("#<portlet:namespace />fc_letra").val();
	var fcSucursal = jQuery("#<portlet:namespace />fc_sucursal").val();
	
	
	var fechaEmisionDia = jQuery("#<portlet:namespace />fechaEmisionDia").val();
	var fechaEmisionMes = jQuery("#<portlet:namespace />fechaEmisionMes").val();
	var fechaEmisionAnio = jQuery("#<portlet:namespace />fechaEmisionAnio").val();
	
	if(clienteTipo==1){
		clienteTipo="EMPRESA";
	}else{
		clienteTipo="AFILIADO";
	}
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/hoteles/facturar_confiteria';
	url += '&cliente_nro_doc='+clienteNroDoc;
	url += '&cliente_apellido='+encodeURI(clienteApe);
	url += '&cliente_nombre='+encodeURI(clienteNom);
	url += '&persfisica_tipo='+clienteTipo;
	url += '&persfisica_estado='+clienteEstado;
	url += '&cuit_entidad='+cuit;
	url += '&sucursal_entidad='+sucursal;
	url += '&entidad='+encodeURI(razonSocial);
	url += '&iva='+iva;
	url += '&fc_form8001='+presForm8001;
	url += '&fc_tipo='+fcTipo;
	url += '&fc_letra='+fcLetra;
	url += '&fc_sucursal='+fcSucursal;
	url += '&fechaEmisionDia='+fechaEmisionDia;
	url += '&fechaEmisionMes='+fechaEmisionMes;
	url += '&fechaEmisionAnio='+fechaEmisionAnio;
	
	url += '&rnd=' + Math.floor(Math.random()*100);

    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			if(""!= obj.error){
				jQuery("#<portlet:namespace />msg_ok").hide();
				jQuery("#<portlet:namespace />msg_error").html("&nbsp;&nbsp;"+obj.error);
				jQuery("#<portlet:namespace />msg_error").show(); 
				
			}else{
				jQuery("#<portlet:namespace />msg_error").html("");
				jQuery("#<portlet:namespace />msg_error").hide();
				
				if(obj.numero!=""){
					
				   jQuery("#<portlet:namespace />msg_ok").html("Se ha generado la Factura nro "+obj.numero) 	
				   jQuery("#<portlet:namespace />msg_ok").show();
				   jQuery("#<portlet:namespace />fc_numero").val(obj.numero);
				   jQuery("#<portlet:namespace />fc_cae_nro").val(obj.cae);
				   if(obj.caefecha!=null){
					   jQuery("#<portlet:namespace />mostrarVtoCAEtxt").val(obj.caefecha);
				   }
				   jQuery("#<portlet:namespace />idFactura").val(obj.idfactura);
				   jQuery("#<portlet:namespace />obtenerCaeHotel").hide();
				   jQuery("#<portlet:namespace />imprimirTicketFC").show();
				   
				}
				
			}
		}
	});
}	

	
function <portlet:namespace />imprimirTicketFactura() {
	
	var idFactura=jQuery("#<portlet:namespace />idFactura").val();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/facturar_comandera';
	url += '&id_factura='+idFactura;
	url += '&id_ejemplar=1';
	url += '&copias=2';
	url += '&mostrarRazonSoc='+<%=(factura!=null && factura.getImporteTotal().compareTo(new BigDecimal(5000)) > 0  
		    || (factura!=null && factura.getCliente() !=null &&  factura.getCliente().getCategoriaIVA()!=null && factura.getCliente().getCategoriaIVA().equalsIgnoreCase("RI"))
		    || (factura!=null && factura.getCliente() !=null && factura.getCliente().getTipo()!=null && factura.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.AFILIADO))
		)?true:false %>;
	
	jQuery.ajax({   
		url: url,
		async: false,
		success: function(data){
	
		}
	});	
}


function <portlet:namespace />recalcularDetalleFC(){
	
	var cond_iva = jQuery('#<portlet:namespace/>iva').val();
	
	var cliente_tipo= jQuery("input[name='<portlet:namespace/>tipo_cliente']:checked").val();
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/lista_detalles_fc';
	var params = {"condIva" : cond_iva,
				  "clienteTipo" : cliente_tipo,
				  "cmd" : "recalcular"} ;
	
	jQuery("#<portlet:namespace />lista_productos").load(url,params);
		
}

function <portlet:namespace />imprimirTicketFacturaBorrar() {
	
	var idFactura=jQuery("#<portlet:namespace />idFactura").val();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/facturar_comandera';
	url += '&id_factura=21';
	url += '&id_ejemplar=1';
	url += '&copias=2';
	url += '&mostrarRazonSoc='+<%=(factura!=null && factura.getImporteTotal().compareTo(new BigDecimal(5000)) > 0  
		    || (factura!=null && factura.getCliente() !=null &&  factura.getCliente().getCategoriaIVA()!=null && factura.getCliente().getCategoriaIVA().equalsIgnoreCase("RI"))
		    || (factura!=null && factura.getCliente() !=null && factura.getCliente().getTipo()!=null && factura.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.AFILIADO))
		)?true:false %>;
	
	jQuery.ajax({   
		url: url,
		async: false,
		success: function(data){
	
		}
	});	
}

function <portlet:namespace />agregarAnticiposFC(){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/lista_adelantos_fc';
	
	jQuery("#<portlet:namespace />mostrar_adelantos").load(url,function(){
		var importe=jQuery("#<portlet:namespace />importe").val();
		var anticipo=jQuery("#total_anticipos").val();
		jQuery('#<portlet:namespace />importe').val(importe-anticipo);
	});
}


function <portlet:namespace />salvarModificacion(){
	
	window.onbeforeunload = null;
	if (<portlet:namespace />validarCampos()) {	
		
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		
		var idFactura=jQuery("#idFactura").val();
		if(idFactura==0){
			params = "&<%= Constants.CMD %>=" + "<%= Constants.SAVE %>";	
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';				
		url += '&fc_letra=' + jQuery("#<portlet:namespace />fc_letra").val();
		url += '&iva=' + jQuery("#<portlet:namespace />iva").val();
		url += '&id_factura=' + jQuery("#id_factura").val();
		url = url + params;
		document.<portlet:namespace />fm_fc.method = 'post';
		submitForm(document.<portlet:namespace />fm_fc, url);
		
	}	
	
	
}

</script>
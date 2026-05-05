<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.ComprobanteSinImporteException"%>
<%@ page import="ar.com.ospim.liquidaciones.ComprobanteSinConceptosException"%>
<%@ page import="ar.com.ospim.liquidaciones.ComprobanteImoprteConceptoInvalidoException"%>
<%@ page import="ar.com.ospim.liquidaciones.ComprobanteConceptoInvalidoException"%>
<%@ page import="ar.com.ospim.liquidaciones.comprobantes.ConceptoConsolidarException"%>
<%@ page import="ar.com.ospim.global.ComprobanteExistenteException"%>

<%

	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}

	String view =(String) request.getAttribute("VIEW");
	boolean esEdicion = true;
	if (view != null && view.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	Comprobante comp = (Comprobante) request
			.getAttribute(WebKeysLiquidaciones.COMPROBANTE_EN_EDICION);
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	if (comp != null && comp.getFechaEmision() != null){
		fecha.setTime(comp.getFechaEmision());
	} else {
		fecha.setTime(new Date());
	}
	
	Calendar fechaFin = CalendarFactoryUtil.getCalendar();
	if (comp != null && comp.getFechaRecepcion() != null){
		fechaFin.setTime(comp.getFechaRecepcion());
	} else {
		fecha.setTime(new Date());
	}
	
	Calendar fechaVencimiento = CalendarFactoryUtil.getCalendar();
	if (comp != null && comp.getFechaVencimiento() != null){
		fechaVencimiento.setTime(comp.getFechaVencimiento());
	} else {
		fechaVencimiento.setTime(new Date());
	}

	Calendar periodo = CalendarFactoryUtil.getCalendar();
	if (comp != null && comp.getPeriodoPrestacion() != null) {
		periodo.setTime(comp.getPeriodoPrestacion());
	} else{
		periodo.setTime(new Date());
		periodo.add(Calendar.MONTH, -1);
	}
	
	
	Boolean nuevo = (Boolean) request.getAttribute(WebKeysLiquidaciones.COMPROBANTE_NUEVO);
	if (nuevo == null){
		nuevo = Boolean.FALSE;
	}
	
	
String cuit = comp != null && comp.getAcreedorEmpresa() != null ?  comp.getAcreedorEmpresa().getCuit() : "";
String sucu = comp != null && comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getSucursal() : "";
String razon = comp != null && comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getRazon_soc() : "";

boolean opcionIngreso = false;
if (comp != null && comp.getAfiliado() !=null ){
	opcionIngreso = true;
}
	
%>
<form action="" method="post" name="<portlet:namespace />act" >
<input type="hidden" id="<portlet:namespace />sucursal" name="<portlet:namespace />sucursal" value="0" maxlenght="6" size="5" />
<liferay-ui:error exception="<%= ComprobanteConceptoInvalidoException.class %>" message="exception-comp-concepto-anticipo-invalido" />
<liferay-ui:error exception="<%= ComprobanteExistenteException.class %>" message="exception-comp-existente" />
<liferay-ui:error exception="<%= ComprobanteSinImporteException.class %>" message="exception-comp-sin-importe" />
<liferay-ui:error exception="<%= ComprobanteSinConceptosException.class %>" message="exception-comp-sin-conceptos" />
<liferay-ui:error exception="<%= ComprobanteImoprteConceptoInvalidoException.class %>" message="exception-comp-conceptos-invalidos" />
<liferay-ui:error exception="<%= ConceptoConsolidarException.class %>" message="comprobante-consolidar" />
<fieldset class="block-labels">
<legend>
	<liferay-ui:message key="alta-comprobante" />
</legend>
<table width="100%">
	<%if (nuevo) { %>
		<input type="hidden" name="<%=WebKeysLiquidaciones.COMPROBANTE_NUEVO %>" value="true"/>
	<% } %>
	<tr>
		<td><label><liferay-ui:message key="acreedor" />:</label>
			<select  onchange="cambiarAEntidad()" name="<portlet:namespace />entidadIngreso" id="<portlet:namespace />entidadIngreso"> 
		    	<option value="Entidad" <% if(null==comp || null==comp.getAfiliado()){ %> selected <%} %> >Empresa</option>
		    	<option value="Afiliado" <% if(null!=comp && null!=comp.getAfiliado()&&null!=comp.getAfiliado().getCuil_titular()){ %> selected <%} %> >Afiliado</option>				
			</select>
		</td>
		<td colspan="8">			
			<div id="<portlet:namespace />divBusqEntidad">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
			  		<liferay-util:param name="esEditable" value='<%=String.valueOf(nuevo) %>'/>
			  		<liferay-util:param name="cuit" value='<%= comp != null && comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : new String("")%>'/>
			  		<liferay-util:param name="sucu" value='<%=comp != null && comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getSucursal() : new String("") %>'/>
			  		<liferay-util:param name="razon" value='<%=comp != null && comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getRazon_soc() : new String("") %>'/>
			  		<liferay-util:param name="id_seccional" value='<%=comp != null && comp.getSeccional() != null ? String.valueOf(comp.getSeccional().getId()) : new String("") %>'/>
			  		<liferay-util:param name="portlet_name" value='farmacia'/>
			  	</liferay-util:include>
			</div>
			<div id="<portlet:namespace />divBusqAfiliado" <% if(opcionIngreso){%> style="display: inline;" <% } else { %> style="display: none;" <% } %> >
				<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>				
					<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
						<liferay-util:param name="cuil" value='<%= comp!=null && comp.getAfiliado() != null ? comp.getAfiliado().getCuil_titular() :	"" %>'/>
						<liferay-util:param name="inte" value='<%= comp!=null && comp.getAfiliado() != null ? String.valueOf(comp.getAfiliado().getInte()) :	new String("") %>'/>
						<liferay-util:param name="apellido" value='<%= comp!=null && comp.getAfiliado() != null ? comp.getAfiliado().getApellido() :	"" %>'/>
						<liferay-util:param name="nombre" value='<%= comp!=null && comp.getAfiliado() != null ? comp.getAfiliado().getNombre() :	"" %>'/>
						<liferay-util:param name="entiAfi" value='<%= String.valueOf(WebKeysGlobal.ENTIDAD_AMTIMA)  %>'/>
						<liferay-util:param name="nroAfi"  value='<%= comp!=null && comp.getAfiliado() != null ? String.valueOf(comp.getAfiliado().getId_amtima()) :	"" %>'/>					
						<liferay-util:param value="<%=String.valueOf(esEdicion)%>" name="edit_mode" />						
						<liferay-util:param name="origen" value="_compro"/>
					</liferay-util:include>
				</fieldset>
			</div>	
		</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="tipo" />:</label></td>
		<td> 
			<% if (!nuevo) { %><input type="hidden" value="<%=comp != null && comp.getTipoComprobante() != null ? comp.getTipoComprobante() : new String()%>" name="<portlet:namespace />tipo_comprobante"/> <%} %>
			<select id="<portlet:namespace />tipo_comprobante" onchange="actualizarCuit();actualizarLetra();"
				<% if (!nuevo) { %> disabled="disabled" <%} else { %>  name="<portlet:namespace />tipo_comprobante" <%} %>>
				<option value="FCP">FCP</option>
				<option value="NCR">NCR</option>
				<option value="NDB">NDB</option>
				<option value="RCB">RCB</option>
				<option value="ANT">ANT</option>
				<option value="VAR">VAR</option>
				<option value="TCK">TCK</option>
			</select>
		</td>
		<td>	<% if (!nuevo) { %><input type="hidden" value="<%=comp != null && comp.getLetraComprobante() != null ? comp.getLetraComprobante() : new String()%>" name="<portlet:namespace />letra"/> <%} %>
		<label><liferay-ui:message key="letra" />:</label>
		</td>
		<td>
			<select id="<portlet:namespace />letra"  <% if (!nuevo) { %> disabled="disabled" <%} else {%>name="<portlet:namespace />letra" <%} %>>
			<option value=" ">&nbsp;</option>
			<option value="A">A</option>
			<option value="B" selected="selected">B</option>
			<option value="C">C</option>
			<option value="X">X</option>
		</select></td>
		<td><label><liferay-ui:message key="pto-venta" />:</label></td>
		<td><input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" <% if (!nuevo) { %> readonly="readonly" <%} %>
			onkeydown="allowOnlyDigits(event)" value ="<%= comp != null && comp.getPtoVenta() != 0 ? String.valueOf(comp.getPtoVenta()) :  new String("") %>" />
		</td>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td>
		 	<input type="text" id="<portlet:namespace />nro_comprobante" <% if (!nuevo) { %> readonly="readonly" <%} %>
			name="<portlet:namespace />nro_comprobante"  maxlength="15"  value ="<%= comp != null && comp.getNroComprobante() != null ? comp.getNroComprobante() :  new String("") %>" />
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="emisor" />:</label></td>
		<td colspan="7"><liferay-ui:message key="cuit" />&nbsp;<input type="text" id="<portlet:namespace />cuit_compr_emisor" name="<portlet:namespace />cuit_compr_emisor" <% if (!nuevo) { %> readonly="readonly" <%} %>
			onkeydown="allowOnlyDigits(event)" size="13" maxlength="11" value ="<%= comp != null && comp.getCuitEmisor() != null ? comp.getCuitEmisor() :  new String("") %>"/>
			<%if (nuevo){ %>
			<br/><span style="font-size: 7pt"><a href="#" onclick="javascript:sugerirCuit('OSPIM','<portlet:namespace />cuit_compr_emisor')">OSPIM</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuit('UOMA','<portlet:namespace />cuit_compr_emisor')">UOMA</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuit('AMTIMA','<portlet:namespace />cuit_compr_emisor')">AMTIMA</a>&nbsp;</span></td>
           <%} %>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="fecha-emision" />:</label>
		</td>
		<td colspan="2"><liferay-ui:input-date dayParam="fechaEmisionComprobanteDia"
			dayValue="<%= fecha.get(Calendar.DATE)%>"
			monthParam="fechaEmisionComprobanteMes"
			monthValue="<%= fecha.get(Calendar.MONTH) %>"
			yearParam="fechaEmisionComprobanteAnio"
			yearValue="<%= fecha.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fecha.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 5 %>"
			firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<td><label><liferay-ui:message key="fecha-recibido" />:</label>
		</td>
		<td colspan="2"><liferay-ui:input-date
			dayParam="fechaRecepcionComprobanteDia"
			dayValue="<%= fechaFin.get(Calendar.DATE)%>"
			monthParam="fechaRecepcionComprobanteMes"
			monthValue="<%= fechaFin.get(Calendar.MONTH) %>"
			yearParam="fechaRecepcionComprobanteAnio"
			yearValue="<%= fechaFin.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaFin.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= fechaFin.get(Calendar.YEAR) + 5 %>"
			firstDayOfWeek="<%= fechaFin.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<td><label><liferay-ui:message key="fecha-vencimiento" />:</label>
		</td>
		<%if (comp != null && comp.getFechaVencimiento() != null){  %>
		<td colspan="2"><liferay-ui:input-date
			monthNullable="true" 
			dayNullable="true"
			yearNullable="true"
			dayParam="fechaVencimientoComprobanteDia"
			dayValue="<%= fechaVencimiento.get(Calendar.DATE)%>"
			monthParam="fechaVencimientoComprobanteMes"
			monthValue="<%= fechaVencimiento.get(Calendar.MONTH) %>"
			yearParam="fechaVencimientoComprobanteAnio"
			yearValue="<%= fechaVencimiento.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaVencimiento.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= fechaVencimiento.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaVencimiento.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion %>" /></td>
		<%} else { %>
		<td colspan="2"><liferay-ui:input-date
			monthNullable="true" 
			dayNullable="true"
			yearNullable="true"
			dayParam="fechaVencimientoComprobanteDia"
			monthParam="fechaVencimientoComprobanteMes"
			yearParam="fechaVencimientoComprobanteAnio"
			yearRangeStart="<%= fechaVencimiento.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= fechaVencimiento.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaVencimiento.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<%} %>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="periodo-prestacion" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
			dayNullable="<%= true %>" 
			dayValue=""
			monthAndYearParam="periodoMesAnio"
			monthValue="<%= periodo.get(Calendar.MONTH) %>"
			monthAndYearNullable="<%= false %>"
			yearValue="<%= periodo.get(Calendar.YEAR) %>"
			yearRangeStart="<%= periodo.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 5 %>"
			firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !esEdicion%>" /></td>
		<td colspan="7">&nbsp;</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr><td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="8"><textarea <% if (!esEdicion) {%>readonly="readonly"<%} %>  maxlength="250" cols="100" rows="5" onkeyup="return ismaxlength(this);" name="<portlet:namespace />obs"><%=comp!=null && comp.getObservaciones() != null ? comp.getObservaciones() : new String() %> </textarea></td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="importe-comprobante" />:</label></td>
		<td><input type="text" id="<portlet:namespace />importe_comprobante" name="<portlet:namespace />importe_comprobante" onkeydown="allowOnlyDigitsAndDecimals(event)"
			onchange="agregarCeros(this); sugerirEnConcepto(this);" value ="<%=comp != null &&comp.getImporteComprobante() != null ? comp.getImporteComprobante().toString() :  new String("") %>" <% if (!esEdicion) {%>readonly="readonly"<%} %>/></td>
		<td colspan="7">&nbsp;</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td colspan="9">
			<liferay-util:include page="/html/portlet/farmacia/comprobantes/conceptos_amtima_agregar.jsp">
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="7">
		<% if (esEdicion){ %>
			<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />agregarComprobante();" />
		<%} %>
		<% if (comp != null && comp.getAlta_fecha() != null){  %>
			&nbsp;<input type="button" value="Crear Nuevo Comprobante" onClick="<portlet:namespace />crearNuevoComprobante();" />
		<%} %>
		<%if (comp != null && comp.getTipoComprobante() != null && comp.getTipoComprobante().equalsIgnoreCase("NDB")){ %>
			&nbsp;<input type="button" value="Imprimir Nota Débito"
				onClick="<portlet:namespace />imprimirND();return false;" />
		<%} %>
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td colspan="9">
		<div align="center" id="<portlet:namespace />buscandoComprobantes">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center"><img alt="<liferay-ui:message key='buscando'/>" src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
</fieldset>
<input type="hidden" name="redirigirNuevoComprobante" id="redirigirNuevoComprobante" value=""/>
<input type="hidden" name="esAmtima" id="esAmtima" value="esAmtima"/>
</form>
<script type="text/javascript">

	function cambiaCuit(){
		var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad").value;
		if (document.getElementById("<portlet:namespace />cuit_compr_emisor") != null){
			document.getElementById("<portlet:namespace />cuit_compr_emisor").value = cuit_entidad;
		}
	}

	function <portlet:namespace />crearNuevoComprobante(){
		jQuery('#redirigirNuevoComprobante').val("redirigirNuevoComprobante");
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/farmacia/editar_comprobante_amtima" /></portlet:renderURL>';
		document.<portlet:namespace />act.method = 'post';
		submitForm(document.<portlet:namespace />act, url);
	}

	function <portlet:namespace />agregarComprobante(){
			var cuit=jQuery('#<portlet:namespace />cuit_compr_emisor').val();
			var cuitAcreedor=jQuery('#<portlet:namespace />cuit_entidad').val();
			var pto_venta=jQuery('#<portlet:namespace />pto_venta').val();
			var tipo_comprobante=jQuery('#<portlet:namespace />tipo_comprobante').val();
			var nro_comprobante=jQuery('#<portlet:namespace />nro_comprobante').val();			
			var letra=document.getElementById("<portlet:namespace />letra").value;
			var sucu=document.getElementById("<portlet:namespace />sucursal").value;			

			var fechaEmisionComprobanteDia=jQuery('#<portlet:namespace />fechaEmisionComprobanteDia').val();
			var fechaEmisionComprobanteMes=jQuery('#<portlet:namespace />fechaEmisionComprobanteMes').val();
			var fechaEmisionComprobanteAnio=jQuery('#<portlet:namespace />fechaEmisionComprobanteAnio').val();			

			var fechaRecepcionComprobanteDia=jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').val();
			var fechaRecepcionComprobanteMes=jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').val();
			var fechaRecepcionComprobanteAnio=jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').val();
						
			var importe_comprobante=jQuery('#<portlet:namespace />importe_comprobante').val();

			if (trim(pto_venta).length == 0 || trim(nro_comprobante).length == 0 || trim(importe_comprobante).length == 0) {
				alert ('Todos los campos, excepto observaciones y fecha de vencimiento, son requeridos');
				return false;
			}
			if (parseInt(pto_venta) > 32000){
				alert("Punto de venta invalido");
				return false;
			}


			if(cuit.length!=11){
				alert("<liferay-ui:message key='valida-cuit'/>");
				jQuery('#<portlet:namespace />cuit_compr_emisor').focus();
				return false;
			}	
			
			if(cuit.length>0){
				if(!validarCuil2(cuit,"<liferay-ui:message key='valida-cuit'/>")){
					jQuery('#<portlet:namespace />cuit_compr_emisor').focus();
					return false;
				}
			}		

			if (trim(cuitAcreedor).length > 0 && trim(cuitAcreedor).length != 11){
				alert("El cuit acreedor es invalido.");
				jQuery('#<portlet:namespace />cuit_entidad').focus();
				return false;

				if(!validarCuil2(cuit,"El cuit acreedor es invalido.")){
					jQuery('#<portlet:namespace />cuit_entidad').focus();
					return false;
				}
			}

			var emision =new Date(fechaEmisionComprobanteAnio, fechaEmisionComprobanteMes, fechaEmisionComprobanteDia, 0, 0, 0);
			var today = new Date();

			if (emision>today)  {
			  alert("La fecha de emision no puede ser mayor al dia de hoy");
			  return;
			}
			
			var recepcion =new Date(fechaRecepcionComprobanteAnio, fechaRecepcionComprobanteMes, fechaRecepcionComprobanteDia, 0, 0, 0);

			if (recepcion>today)  {
			  alert("La fecha de recepcion no puede ser mayor al dia de hoy");
			  return;
			}
			
			jQuery('#<portlet:namespace />buscandoComprobantes').show();
			document.getElementById("<portlet:namespace />sucursal").value = pto_venta;
			var url = '<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/farmacia/editar_comprobante_amtima" /></portlet:actionURL>';
			document.<portlet:namespace />act.method = 'post';
			submitForm(document.<portlet:namespace />act, url);
	}

	function validarCuil2(input, message){	

		if (input.trim() == "00000000000"){
			return true;
		}
		if(input.trim().length>0){		
			if(isPositiveInteger(input)){			
				if(input.trim().length==11){				
					return true;
				}else{				
					alert(message);
					return false;
				}
			}else{
				alert(message);
				return false;
			}
		}else{
				
			return false;
		}
	}

	function <portlet:namespace />imprimirND(){
		window.location.href ='/pdfservlet/?accion=<%="notaDebitoLiquidacion"%>&id_liquidacion=<%= (comp != null && comp.getNroComprobante() != null) ? comp.getNroComprobante() : new String("") %>&terceros=2';		
	}
	
	function actualizarCuit(){
		var sel = document.getElementById("<portlet:namespace />tipo_comprobante");
		if (sel.value == "REI"){
			jQuery('#<portlet:namespace />cuit_compr_emisor').val("00000000000")
		}
	}
	
	function actualizarLetra(){
		var sel = document.getElementById("<portlet:namespace />tipo_comprobante");
		if (sel.value == "FCP"){
			document.getElementById('<portlet:namespace />letra').selectedIndex  = 2;
		} else {
			document.getElementById('<portlet:namespace />letra').selectedIndex  = 0;
		}

		if (sel.value == "ANT" || sel.value == "VAR" || sel.value == "NDB"){
			document.getElementById('<portlet:namespace />letra').selectedIndex = 0;
			document.getElementById('<portlet:namespace />letra').disabled = true;
			document.getElementById('<portlet:namespace />nro_comprobante').value = '';
			document.getElementById('<portlet:namespace />nro_comprobante').readOnly = true;
			document.getElementById('<portlet:namespace />pto_venta').value = '';
			document.getElementById('<portlet:namespace />pto_venta').readOnly = true;
		} else {
			document.getElementById('<portlet:namespace />letra').disabled = false;
			document.getElementById('<portlet:namespace />nro_comprobante').readOnly = false;
			document.getElementById('<portlet:namespace />pto_venta').readOnly = false;
		}
		sugerirNumero();
	}
	
	jQuery('#<portlet:namespace />buscandoComprobantes').hide();

	function sugerirNumero(){
		var tipo_acreedor = jQuery('#<portlet:namespace />tipo_acreedor').val();
		var id_acreedor = jQuery('#<portlet:namespace />id_acreedor').val();
		var sucu = jQuery('#<portlet:namespace />sucursal_entidad').val();
		
		var cuit = jQuery('#<portlet:namespace />cuit_entidad').val(); 
//		var sel = document.getElementById("<portlet:namespace />tipo_comprobante");
		
		var sel = jQuery('#<portlet:namespace />tipo_comprobante').val();  
		
        if(jQuery("#<portlet:namespace />entidadIngreso").val()!='Entidad'){
	       cuit=jQuery('#<portlet:namespace />cuil_compro').val();
	       sucu='000';
        }
		if ((sel == "ANT" || sel == "VAR" || sel == "NDB") && trim(cuit)!= ""){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/buscar_ultimo_nro_comprobante_amtima&tipo='+sel+'&cuit='+cuit;
			 url += '&tipo_acreedor=' + tipo_acreedor;
			 url += '&id_acreedor=' + id_acreedor;
			 url += '&sucursal=' + sucu;
			 url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery.ajax({   
				url: url,
				success: function(data){
					var cuit_acr  = jQuery("#<portlet:namespace />cuit_entidad").val();	
					var sucu = jQuery('#<portlet:namespace />sucursal_entidad').val();
					if(jQuery("#<portlet:namespace />entidadIngreso").val()!='Entidad'){
						cuit_acr=jQuery('#<portlet:namespace />cuil_compro').val();
						sucu='000';
					}
					var obj = jQuery.parseJSON(data);
					if ( sel.value == "NDB") {
						jQuery("#<portlet:namespace />nro_comprobante").val((parseInt(obj.numero) +1));
						jQuery("#<portlet:namespace />pto_venta").val(2);
					}else{ 
					jQuery("#<portlet:namespace />nro_comprobante").val(cuit_acr + "-" + sucu +  "/"+ (parseInt(obj.numero) +1));
					jQuery("#<portlet:namespace />pto_venta").val(1);
					}
				}
			});
			
		}
	}

	
	function sugerirCuit(entidad,  inp){
		if (entidad == 'OSPIM'){
			jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_OSPIM%>');	
		}
		if (entidad == 'UOMA'){
			jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_UOMA%>');
		}
		if (entidad == 'AMTIMA'){
			jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_AMTIMA%>');
		}

		if (inp == '<portlet:namespace />cuit_entidad'){
			sugerirNumero();
		}
	}
	
	<% if (comp != null && comp.getLetraComprobante() != null) {%>
	 	seleccionarSelect("<portlet:namespace />letra", "<%=comp.getLetraComprobante().trim().equals("") ? new String (" ") : comp.getLetraComprobante()%>");
	 <%}%>

	 <% if (comp != null && comp.getTipoComprobante() != null) {%>
	 	seleccionarSelect("<portlet:namespace />tipo_comprobante", "<%=comp.getTipoComprobante()%>");
	 <%}%>

	 <% if (nuevo){%>
	 actualizarLetra();
	 <%}else{%>
	 
	 <%}%>

	 jQuery("#<portlet:namespace />periodoDia").hide();

	 function sugerirEnConcepto(){
		 var comp = jQuery("#<portlet:namespace />importe_comprobante").val();
		 jQuery("#<portlet:namespace />importe_concepto").val(parseFloat(trim(comp))-parseFloat(jQuery("#total_conceptos").val()));
		 if (isNaN(jQuery("#<portlet:namespace />importe_concepto").val())){
			 jQuery("#<portlet:namespace />importe_concepto").val('');
		 }
	 }	 


		jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').change(function(){
			if (jQuery('#<portlet:namespace />fechaVencimientoComprobanteDia').val() == ""){	
				jQuery('#<portlet:namespace />fechaVencimientoComprobanteDia').val(jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').val());
			}
		}); 
		jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').change(function(){
			if (jQuery('#<portlet:namespace />fechaVencimientoComprobanteMes').val() == ""){	
				jQuery('#<portlet:namespace />fechaVencimientoComprobanteMes').val(jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').val());
			}
		}); 
		jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').change(function(){
			if (jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnio').val() == ""){	
				jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnio').val(jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').val());
			}
		});


		
		<% if (cuit != null && !cuit.trim().equals("") && sucu != null && !sucu.trim().equals("") && (razon == null || razon.trim().equals(""))) { %>
				<portlet:namespace />buscarEntidad();
		<%}%>
		
		<% if(null!=comp && null!=comp.getAfiliado()&&null!=comp.getAfiliado().getCuil_titular()){ %> 
	 		cambiarAEntidad(); 
		<%} %> 
	 
		function cambiarAEntidad(){	
			var entidad=jQuery("#<portlet:namespace />entidadIngreso").val();			
			if(entidad=='Entidad'){
				jQuery('#<portlet:namespace />divBusqEntidad').show();
				jQuery('#<portlet:namespace />divBusqAfiliado').hide();	
			}else{
				jQuery('#<portlet:namespace />divBusqEntidad').hide();
				jQuery('#<portlet:namespace />divBusqAfiliado').show();
			}
		
		}
	 
</script>



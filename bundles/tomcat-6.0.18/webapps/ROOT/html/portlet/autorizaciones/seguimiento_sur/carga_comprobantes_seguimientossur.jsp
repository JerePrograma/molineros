<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
<%
 
Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(new Date());

String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
     esEditableStr ="false";
}
boolean showABMButtons = Boolean.parseBoolean(esEditableStr);;
%>
		<% if(showABMButtons) { %>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
		<table width="70%">
		
		       <tr>
					<td colspan="7">
						<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
					  		<liferay-util:param name="search_url" value="/autorizaciones/buscar_prestador" />
					  		<liferay-util:param name="ext" value='_aut'/>
						</liferay-util:include>
					</td>	
				</tr>
				
				<tr><td colspan="8">&nbsp;</td></tr>
		</table>
		<table>
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					<select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante">
						<option value="">Todos</option>						
						<option value="FCP">FCP</option>
						<option value="NCR">NCR</option>
						<option value="NDB">NDB</option>
						<option value="RCB">RCB</option>
						<option value="ANT">ANT</option>
						<option value="REI">REI</option>
						<option value="REM">REM</option>
						<option value="VAR">VAR</option>
						</select>
					</td>
					<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select id="<portlet:namespace />letra" name="<portlet:namespace />letra">
						<option value="">Todos</option>						
						<option value="A">A</option>
						<option value="B">B</option>
						<option value="C">C</option>
						<option value="M">M</option>
						<option value="R">R</option>
						<option value="X">X</option>
						</select>
					</td>
					<td>
						<label><liferay-ui:message key="pto-venta" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" onkeydown="allowOnlyDigits(event)" />
					</td>
					<td>
						<label><liferay-ui:message key="numero" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />nro_comprobante" name="<portlet:namespace />nro_comprobante" value="" maxlength="25"/>
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
			</table>
			<table class="lfr-table">	
				<tr>
					<td>
						<label><liferay-ui:message key="fecha-emision" />:</label>
					</td>
					<td colspan="1">
						<liferay-ui:input-date 
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaEmisionComprobanteDia"
						monthParam="fechaEmisionComprobanteMes"
						yearParam="fechaEmisionComprobanteAnio"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					<td>
						<label><liferay-ui:message key="fecha-recibido" />:</label>
					</td>
					<td colspan="1">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaRecepcionComprobanteDia"
						monthParam="fechaRecepcionComprobanteMes"
						yearParam="fechaRecepcionComprobanteAnio" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-vencimiento" />:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaVencimientoComprobanteDia"
						monthParam="fechaVencimientoComprobanteMes"
						yearParam="fechaVencimientoComprobanteAnio" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>	
					<td colspan="1"><label><liferay-ui:message key="imp" />:</label></td>
					<td colspan="1"><input id="<portlet:namespace />importe"
						name="<portlet:namespace />importe" size="12" maxlength="12"
						type="text"
						value=""
						onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event);"
						/>
					</td>		
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
			</table>
			<table>	
				<tr>
					<td>
						<input id="<portlet:namespace />buscarCpte" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
					</td>
					
					<td>
						<input id="<portlet:namespace />limpiarComprobante" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar" />" type="button"/>							
					</td>
					
					<td>
						<% if(showABMButtons) { %>
							<input type="button" value="<liferay-ui:message key="alta-comprobante" />" onClick="<portlet:namespace />agregaComprobanteLiq();" />
						<%} %>
					</td>	
					
				</tr>
			</table>
			<table class="lfr-table" width="100%">	
				<tr>
				 <td colspan="12">
				       <div align="center" id="<portlet:namespace />busquedaComprobanteDiv">
				            <jsp:include page='/html/portlet/autorizaciones/seguimiento_sur/comprobantes_liquidados_search_result.jsp' /></div>
			           </div>
				 </td>
				</tr>
			</table>				
		</fieldset>
		<%} %>
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscandoComprobante">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
		</fieldset>
		
		
		
		
			
<script type="text/javascript">
    var popupCL;

	jQuery('#<portlet:namespace />buscandoComprobante').hide();	
	
	jQuery('#<portlet:namespace />buscarCpte').click(function(){

		var idPrestador=jQuery('#<portlet:namespace />id_prestador_aut').val();
		var cuit=jQuery('#<portlet:namespace />cuit_prestador_aut').val();
		var razonSocial = jQuery("#<portlet:namespace />nombre_prestador_aut").val(); 
		var pto_venta=jQuery('#<portlet:namespace />pto_venta').val();
		var tipo_comprobante=jQuery('#<portlet:namespace />tipo_comprobante').val();
		var nro_comprobante=jQuery('#<portlet:namespace />nro_comprobante').val();			
		var letra=document.getElementById("<portlet:namespace />letra").value;

		var fechaEmisionComprobanteDia=jQuery('#<portlet:namespace />fechaEmisionComprobanteDia').val();
		var fechaEmisionComprobanteMes=jQuery('#<portlet:namespace />fechaEmisionComprobanteMes').val();
		var fechaEmisionComprobanteAnio=jQuery('#<portlet:namespace />fechaEmisionComprobanteAnio').val();			

		if (fechaEmisionComprobanteDia != "" || fechaEmisionComprobanteMes != "" || fechaEmisionComprobanteAnio != ""){
			if (fechaEmisionComprobanteDia == "" || fechaEmisionComprobanteMes == "" || fechaEmisionComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Emision.");
				return false;
			}
		}

		var fechaRecepcionComprobanteDia=jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').val();
		var fechaRecepcionComprobanteMes=jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').val();
		var fechaRecepcionComprobanteAnio=jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').val();

		if (fechaRecepcionComprobanteDia != "" || fechaRecepcionComprobanteMes != "" || fechaRecepcionComprobanteAnio != ""){
			if (fechaRecepcionComprobanteDia == "" || fechaRecepcionComprobanteMes == "" || fechaRecepcionComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Recepcion.");
				return false;
			}
		}

		var fechaVencimientoComprobanteDia=jQuery('#<portlet:namespace />fechaVencimientoComprobanteDia').val();
		var fechaVencimientoComprobanteMes=jQuery('#<portlet:namespace />fechaVencimientoComprobanteMes').val();
		var fechaVencimientoComprobanteAnio=jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnio').val();

		if (fechaVencimientoComprobanteDia != "" || fechaVencimientoComprobanteMes != "" || fechaVencimientoComprobanteAnio != ""){
			if (fechaVencimientoComprobanteDia == "" || fechaVencimientoComprobanteMes == "" || fechaVencimientoComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Vencimiento.");
				return false;
			}
		}

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_comprobantes_liquidados_seguimiento_sur';
		url += '&pto_venta='+pto_venta+
		       '&tipo_comprobante='+tipo_comprobante+
		       '&nro_comprobante='+nro_comprobante+
		       '&fechaEmisionComprobanteDia='+fechaEmisionComprobanteDia+'&fechaEmisionComprobanteMes='+fechaEmisionComprobanteMes+'&fechaEmisionComprobanteAnio='+fechaEmisionComprobanteAnio+
		       '&fechaRecepcionComprobanteDia='+fechaRecepcionComprobanteDia+'&fechaRecepcionComprobanteMes='+fechaRecepcionComprobanteMes+'&fechaRecepcionComprobanteAnio='+fechaRecepcionComprobanteAnio+
		       '&fechaVencimientoComprobanteDia='+fechaVencimientoComprobanteDia+'&fechaVencimientoComprobanteMes='+fechaVencimientoComprobanteMes+'&fechaVencimientoComprobanteAnio='+fechaVencimientoComprobanteAnio + 
		       '&cuit=' + cuit + 
		       '&letra=' + escape(letra);
		
		url += '&razonsocial='+escape(razonSocial);
		url += '&idprestador='+idPrestador;

		if(popupCL==null)
    		popupCL = Liferay.Popup({title:"Búsqueda Comprobantes",modal:true,width:900,onClose: function() { popupCL = null;}});
		
		jQuery(popupCL).load(url);
		
	});
	
	
	function <portlet:namespace />agregaComprobanteLiq(){

		var idPrestador=jQuery('#<portlet:namespace />id_prestador_aut').val();
		var cuit=jQuery('#<portlet:namespace />cuit_prestador_aut').val();
		var razonSocial = jQuery("#<portlet:namespace />nombre_prestador_aut").val(); 
		var pto_venta=jQuery('#<portlet:namespace />pto_venta').val();
		var tipo_comprobante=jQuery('#<portlet:namespace />tipo_comprobante').val();
		var nro_comprobante=jQuery('#<portlet:namespace />nro_comprobante').val();			
		var letra=document.getElementById("<portlet:namespace />letra").value;

		var fechaEmisionComprobanteDia=jQuery('#<portlet:namespace />fechaEmisionComprobanteDia').val();
		var fechaEmisionComprobanteMes=jQuery('#<portlet:namespace />fechaEmisionComprobanteMes').val();
		var fechaEmisionComprobanteAnio=jQuery('#<portlet:namespace />fechaEmisionComprobanteAnio').val();			

		if (fechaEmisionComprobanteDia != "" || fechaEmisionComprobanteMes != "" || fechaEmisionComprobanteAnio != ""){
			if (fechaEmisionComprobanteDia == "" || fechaEmisionComprobanteMes == "" || fechaEmisionComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Emision.");
				return false;
			}
		}

		var fechaRecepcionComprobanteDia=jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').val();
		var fechaRecepcionComprobanteMes=jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').val();
		var fechaRecepcionComprobanteAnio=jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').val();

		if (fechaRecepcionComprobanteDia != "" || fechaRecepcionComprobanteMes != "" || fechaRecepcionComprobanteAnio != ""){
			if (fechaRecepcionComprobanteDia == "" || fechaRecepcionComprobanteMes == "" || fechaRecepcionComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Recepcion.");
				return false;
			}
		}

		var fechaVencimientoComprobanteDia=jQuery('#<portlet:namespace />fechaVencimientoComprobanteDia').val();
		var fechaVencimientoComprobanteMes=jQuery('#<portlet:namespace />fechaVencimientoComprobanteMes').val();
		var fechaVencimientoComprobanteAnio=jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnio').val();
		var importe=jQuery('#<portlet:namespace />importe').val();
		if (fechaVencimientoComprobanteDia != "" || fechaVencimientoComprobanteMes != "" || fechaVencimientoComprobanteAnio != ""){
			if (fechaVencimientoComprobanteDia == "" || fechaVencimientoComprobanteMes == "" || fechaVencimientoComprobanteAnio == ""){
				alert("Por favor seleccione todos los campos de la fecha de Vencimiento.");
				return false;
			}
		}
		
		
		if(idPrestador==null || ""==idPrestador || cuit==null || cuit=="" || razonSocial== null || razonSocial=="") {
			alert("Debe seleccionar un prestador");
			return false;
		}
		
		
		if(tipo_comprobante==null || tipo_comprobante==""){
			alert("Debe ingresar un tipo de comprobante");
			return false;
		}
		
		if(letra==null || letra==""){
			alert("Debe ingresar la letra del comprobante");
			return false;
		}
		
		if(nro_comprobante==null || ""==nro_comprobante){
			alert("Debe llenar el Nro de comprobante");
			return false;
		}
		
		if((importe==null || ""==importe || importe==0) && tipo_comprobante!="REM"){
			alert("Debe llenar el Importe");
			return false;
		}
		
		if (fechaEmisionComprobanteDia == "" || fechaEmisionComprobanteMes == "" || fechaEmisionComprobanteAnio == ""){
			alert("Por favor seleccione todos los campos de la fecha de Emision.");
			return false;
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/agregacomprobanteliquidado';
        url += "&cuit=" + encodeURI(cuit);
        url += "&tipocomprobante=" + encodeURI(tipo_comprobante);
        url += "&letracomprobante=" + encodeURI(letra);
        url += "&ptoventa="+encodeURI(pto_venta);
        url += "&sucucomprobante="+encodeURI(pto_venta);
        url += "&nrocomprobante="+encodeURI(nro_comprobante);
        url += "&idprestador="+encodeURI(idPrestador);
        url += "&razonsocial=" + encodeURI(razonSocial);
        url += '&fechaEmisionComprobanteDia='+fechaEmisionComprobanteDia+'&fechaEmisionComprobanteMes='+fechaEmisionComprobanteMes+'&fechaEmisionComprobanteAnio='+fechaEmisionComprobanteAnio+
	           '&fechaRecepcionComprobanteDia='+fechaRecepcionComprobanteDia+'&fechaRecepcionComprobanteMes='+fechaRecepcionComprobanteMes+'&fechaRecepcionComprobanteAnio='+fechaRecepcionComprobanteAnio+
	           '&fechaVencimientoComprobanteDia='+fechaVencimientoComprobanteDia+'&fechaVencimientoComprobanteMes='+fechaVencimientoComprobanteMes+'&fechaVencimientoComprobanteAnio='+fechaVencimientoComprobanteAnio ; 
        url += "&importe="+encodeURI(importe);
        
		jQuery('#<portlet:namespace />busquedaComprobanteDiv').load(url);
		
		jQuery('#<portlet:namespace />limpiarComprobante').click();
	}
	
	
	
	jQuery('#<portlet:namespace />limpiarComprobante').click(function(){

		jQuery('#<portlet:namespace />id_prestador_aut').val("");
		jQuery('#<portlet:namespace />cuit_prestador_aut').val("");
		jQuery("#<portlet:namespace />nombre_prestador_aut").val("");
		jQuery("#<portlet:namespace />prest_seleccionada_aut").val("");
		jQuery("#<portlet:namespace />divBtnBuscaPrestador_aut").show();
		
		jQuery('#<portlet:namespace />pto_venta').val("");
		jQuery('#<portlet:namespace />tipo_comprobante').val("");
		jQuery('#<portlet:namespace />nro_comprobante').val("");
		
		document.getElementById("<portlet:namespace />letra").value="";

		jQuery('#<portlet:namespace />fechaEmisionComprobanteDia').val("");
		jQuery('#<portlet:namespace />fechaEmisionComprobanteMes').val("");
		jQuery('#<portlet:namespace />fechaEmisionComprobanteAnio').val("");			

		jQuery('#<portlet:namespace />fechaRecepcionComprobanteDia').val("");
		jQuery('#<portlet:namespace />fechaRecepcionComprobanteMes').val("");
		jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnio').val("");

		
		jQuery('#<portlet:namespace />fechaVencimientoComprobanteDia').val("");
		jQuery('#<portlet:namespace />fechaVencimientoComprobanteMes').val("");
		jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnio').val("");
		jQuery('#<portlet:namespace />importe').val("");
		
				
	});
	
	function <portlet:namespace />seleccionarComprobantesLiq(inputs){	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/seleccioncomprobantesliquidados';
        url += "&comprobantes=" + encodeURI(inputs);
        jQuery('#<portlet:namespace />busquedaComprobanteDiv').load(url,
				function() {
			                	Liferay.Popup.close(popupCL);
						   }
		);		
	}
	
	function borraComprobanteLiq(cuit,tipocomprobante,letracomprobante,ptoventa,sucucomprobante,nrocomprobante,idprestador){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/eliminacomprobanteliquidado';
        url += "&cuit=" + encodeURI(cuit);
        url += "&tipocomprobante=" + encodeURI(tipocomprobante);
        url += "&letracomprobante=" + encodeURI(letracomprobante);
        url += "&ptoventa="+encodeURI(ptoventa);
        url += "&sucucomprobante="+encodeURI(sucucomprobante);
        url += "&nrocomprobante="+encodeURI(nrocomprobante);
        url += "&idprestador="+encodeURI(idprestador);
		jQuery('#<portlet:namespace />busquedaComprobanteDiv').load(url);	
	}
</script>

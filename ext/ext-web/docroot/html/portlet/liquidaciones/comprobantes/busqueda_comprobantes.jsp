<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
<%
 
Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(new Date());


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
%>
		<% if(showABMButtons) { %>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
		<table width="70%">
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
				<tr>
					<td><label><liferay-ui:message key="acreedor" />:</label></td>
					<td colspan="7">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
						</liferay-util:include>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td><label><liferay-ui:message key="emisor" />:</label></td>
					<td colspan="7"><liferay-ui:message key="cuit" />&nbsp;<input type="text" id="<portlet:namespace />cuit_compr_emisor" name="<portlet:namespace />cuit_compr_emisor"
						onkeydown="allowOnlyDigits(event)" size="13" maxlength="11" value =""/>
						<br/><span style="font-size: 7pt"><a href="#" onclick="javascript:sugerirCuit('OSPIM','<portlet:namespace />cuit_compr_emisor')">OSPIM</a>&nbsp;
														  <a href="#" onclick="javascript:sugerirCuit('UOMA','<portlet:namespace />cuit_compr_emisor')">UOMA</a>&nbsp;
														  <a href="#" onclick="javascript:sugerirCuit('AMTIMA','<portlet:namespace />cuit_compr_emisor')">AMTIMA</a>&nbsp;</span></td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label><liferay-ui:message key="fecha-emision" />:</label>
					</td>
					<td colspan="3">
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
					<td colspan="3">
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
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td><label><liferay-ui:message key="periodo-prestacion" />:</label></td>
					<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
						dayNullable="<%= true %>" 
						dayValue=""
						monthAndYearParam="periodoMesAnio"
						monthAndYearNullable="<%= true %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
					<td><liferay-ui:message key="estado" /></td>	
					<td colspan="3">
						<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">								
								<option value="0" selected>Todos</option>
								<option value="1">No Pagados</option>
								<option value="2">Pagados</option>
						</select>
					</td>
				</tr>	
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
					</td>
					<td>
						<% if(showABMButtons) { %>
							<input type="button" value="<liferay-ui:message key="alta-comprobante" />" onClick="<portlet:namespace />altaComprobante();" />
						<%} %>
					</td>	
					<td colspan="6">&nbsp;</td>	
				</tr>
			</table>				
		</fieldset>
		<%} %>
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaChequeDiv">
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){


		var cuit=jQuery('#<portlet:namespace />cuit_compr_emisor').val();
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



		var cuit_acreedor = jQuery("#<portlet:namespace />cuit_entidad").val();    
	    var sucu_acreedor = jQuery("#<portlet:namespace />sucursal_entidad").val();
		var seccional_acreedor = jQuery("#<portlet:namespace />id_seccional").val();
		var peri = jQuery("#<portlet:namespace />periodoMesAnio").val();
		var estado= jQuery("#<portlet:namespace/>estado").val();
		
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_comprobantes';
		url += '&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante+
		 '&fechaEmisionComprobanteDia='+fechaEmisionComprobanteDia+'&fechaEmisionComprobanteMes='+fechaEmisionComprobanteMes+'&fechaEmisionComprobanteAnio='+fechaEmisionComprobanteAnio+
			'&fechaRecepcionComprobanteDia='+fechaRecepcionComprobanteDia+'&fechaRecepcionComprobanteMes='+fechaRecepcionComprobanteMes+'&fechaRecepcionComprobanteAnio='+fechaRecepcionComprobanteAnio
			+ '&cuit_compr_emisor=' + cuit + '&letra=' + escape(letra);
		url += '&cuit_entidad='+cuit_acreedor;
		url += '&sucursal_entidad='+sucu_acreedor;
		url += '&id_seccional='+seccional_acreedor;
		url += '&periodoMesAnio=' + peri;
		url += '&estado='+estado;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery('#<portlet:namespace />busquedaChequeDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();
        															  }
        );	
	});
	

	function <portlet:namespace />altaComprobante() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_comprobante" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
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

    function sugerirNumero(){
    }
	 jQuery("#<portlet:namespace />periodoDia").hide();

		function cambiaCuit(){
		}
		
</script>

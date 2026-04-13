<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
<liferay-ui:error exception="<%=ar.com.ospim.global.services.ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados-baja" />
<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}else if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}else if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_TES_1_")){
		portlet_name = "tesoreria";
	}
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>
		<% if(esEditable) { %>		
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
		<table width="70%">
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					<select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante" onchange="actualizarCuit();actualizarLetra();">
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
					<td><label><liferay-ui:message key="emisor" />:</label></td>
					<td colspan="7"><liferay-ui:message key="cuit" />&nbsp;<input type="text" id="<portlet:namespace />cuit_compr_emisor" name="<portlet:namespace />cuit_compr_emisor"
						onkeydown="allowOnlyDigits(event)" size="13" maxlength="11" value =""/>
						<br/><span style="font-size: 7pt"><a href="javascript:void(0)" onclick="javascript:sugerirCuit('OSPIM','<portlet:namespace />cuit_compr_emisor')">OSPIM</a>&nbsp;
														  <a href="javascript:void(0)" onclick="javascript:sugerirCuit('UOMA','<portlet:namespace />cuit_compr_emisor')">UOMA</a>&nbsp;
														  <a href="javascript:void(0)" onclick="javascript:sugerirCuit('AMTIMA','<portlet:namespace />cuit_compr_emisor')">AMTIMA</a>&nbsp;</span></td>
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
					<td colspan="4">&nbsp;</td>
				</tr>	
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td colspan="6">&nbsp;</td>	
				</tr>
			</table>				
		</fieldset>
		<%} %>
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
		<fieldset class="block-labels">
			<legend>
					<label><liferay-ui:message key="comprobantes" />:</label>
			</legend>
		<%-- 	<div align="center" id="<portlet:namespace />busquedaChequeDiv">
				<jsp:include page='comprobantes_search_result.jsp' /></div>
			</div> --%>
			<div align="center" id="<portlet:namespace />busquedaComprobDiv">
				<liferay-util:include page="/html/portlet/utils/comprobantes/comprobantes_search_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
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
				
				var fechaAltaOPDia=jQuery('#<portlet:namespace />altaFechaDia').val();
				var fechaAltaOPMes=jQuery('#<portlet:namespace />altaFechaMes').val();
				var fechaAltaOPAnio=jQuery('#<portlet:namespace />altaFechaAnio').val();

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

				if (trim(cuit_acreedor) == ""){
					alert("Debe seleccionar un cuit acreedor");
					jQuery("#<portlet:namespace />cuit_entidad").focus();
					return false;
				}
				var peri = jQuery("#<portlet:namespace />periodoMesAnio").val();
				var retIVA=jQuery('#<portlet:namespace />retIvaChk').attr('checked');
				var retGAN=jQuery('#<portlet:namespace />retGanChk').attr('checked');
				
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_comprobante_embebido';
				url += '&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante+
				 '&fechaEmisionComprobanteDia='+fechaEmisionComprobanteDia+'&fechaEmisionComprobanteMes='+fechaEmisionComprobanteMes+'&fechaEmisionComprobanteAnio='+fechaEmisionComprobanteAnio+
					'&fechaRecepcionComprobanteDia='+fechaRecepcionComprobanteDia+'&fechaRecepcionComprobanteMes='+fechaRecepcionComprobanteMes+'&fechaRecepcionComprobanteAnio='+fechaRecepcionComprobanteAnio+
					'&fechaAltaOPDia='+fechaAltaOPDia+'&fechaAltaOPMes='+fechaAltaOPMes+'&fechaAltaOPAnio='+fechaAltaOPAnio
					+ '&cuit_compr_emisor=' + cuit + '&letra=' + escape(letra);
				url += '&cuit_entidad='+cuit_acreedor;
				url += '&sucursal_entidad='+sucu_acreedor;
				url += '&id_seccional='+seccional_acreedor;
				url += '&portlet_name=<%=portlet_name%>';
				url += '&periodoMesAnio=' + peri;
				
				url += '&retivaesp='+retIVA;
				url += '&retganesp='+retGAN;
				
				url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
		        		recalcularTotales();
		        		utilizarObservaciones();        		
		        		<portlet:namespace />agregarPagoVacio();
					}
		        );
			});
			
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
			
			function borraComprobante(pto_venta,tipo_comprobante,nro_comprobante, cuit, letra, sucu){
				 
					var fechaAltaOPDia=jQuery('#<portlet:namespace />altaFechaDia').val();
					var fechaAltaOPMes=jQuery('#<portlet:namespace />altaFechaMes').val();
					var fechaAltaOPAnio=jQuery('#<portlet:namespace />altaFechaAnio').val();
					
			 		jQuery('#<portlet:namespace />buscando').show();
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_comprobante_embebido&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante +'&cuit=' + cuit + '&letra=' + escape(letra) + '&sucursal=' + sucu;
					url += '&fechaAltaOPDia='+fechaAltaOPDia+'&fechaAltaOPMes='+fechaAltaOPMes+'&fechaAltaOPAnio='+fechaAltaOPAnio;
					url += '&portlet_name=<%=portlet_name%>';
					url += '&rnd=' + Math.floor(Math.random()*100);		
					jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
							jQuery('#<portlet:namespace />buscando').hide();     
							recalcularTotales();      
							utilizarObservaciones();
			        		<portlet:namespace />agregarPagoVacio();
						  }
					 );
			}
			
			var popup;
			function editaComprobante(pto_venta,tipo_comprobante,nro_comprobante, cuit, letra, sucu){
				    //MOSTRAR POPUP...
				    popup= Liferay.Popup({title:"<liferay-ui:message key="edit" />",modal:true,width:700});	 		
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_comprobante_embebido&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante +'&cuit=' + cuit + '&letra=' + escape(letra) + '&sucursal=' + sucu;
					url += '&portlet_name=<%=portlet_name%>';
					url += '&rnd=' + Math.floor(Math.random()*100);		
					jQuery(popup).load(url);
					
					//ESTO DEBE HACERLO LUEGO DE CERRAR EL POPUP...
					//jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
					//		jQuery('#<portlet:namespace />buscando').hide();     
					//		recalcularTotales();      
					//		utilizarObservaciones(); 															
					//	  }
					//);
			}
		
			 
			function editaConceptoEmbebido(){
				    //MOSTRAR POPUP...		    	
				    
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_concepto_embebido';
					url += '&portlet_name=<%=portlet_name%>';
					url += '&rnd=' + Math.floor(Math.random()*100);
					console.log(jQuery('#<portlet:namespace />conceptos_embebidos').serializeArray());					
					//ESTO DEBE HACERLO LUEGO DE CERRAR EL POPUP...
					jQuery('#<portlet:namespace />busquedaComprobDiv').load(url,jQuery('#<portlet:namespace />conceptos_embebidos').serializeArray(), function() {					     
							recalcularTotales();      																			
						  }
					);
					Liferay.Popup.close(popup);
			}

			function borrarTodos(){
				 jQuery('#<portlet:namespace />buscando').show();
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_comprobante_embebido&todos=todos';
					url += '&portlet_name=<%=portlet_name%>';
					url += '&rnd=' + Math.floor(Math.random()*100);	
					
					jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
							jQuery('#<portlet:namespace />buscando').hide();   
							recalcularTotales(); 
							utilizarObservaciones();    
						  }
					 );
					// TODO refrescar retencions/pagos si se quitaron los comprobantes
			}
			function cerrarPopupRecalcularImportes(){		
				recalcularTotales();  
				Liferay.Popup.close(popup);	
			}
	//recalcularTotales() y utilizarObservaciones() deben ser funciones declaradas en el jsp q incluya a este jsp
</script>

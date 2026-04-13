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
	boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
	
%>


<table class="lfr-table" style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
				<tr>
					<td>
						<liferay-ui:message key="numero"/>:
					</td>
					<td>
						<input type="text" value="" name="<portlet:namespace />nro_pago" id="<portlet:namespace />nro_pago" />
					</td>
					<td>
						<liferay-ui:message key="importe"/>:
					</td>
					<td>
						<input type="text" value="" name="<portlet:namespace />importe_pago" id="<portlet:namespace />importe_pago" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
					</td>
					<td>
						<liferay-ui:message key="cuenta-bancaria"/>:
					</td>
					<td>
						<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
								<% 	for (CuentaBancaria cta : ctas) {  
										if(portlet_name.equals("farmacia") && cta.getEntidad().equals("A")){%>									
											<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
										<%}else if(portlet_name.equals("tesoreria") && cta.getEntidad().equals("O")){%>
											<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
										<%}else if(portlet_name.equals("uoma") && cta.getEntidad().equals("U")){%>
											<option value="<%=cta.getId_cuenta_bcria()%>"><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>	
										<%}
									} %>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="a-nombre-de"/>:
					</td>
					<td colspan="4">
						<input size="100" type="text" value="" name="<portlet:namespace />a_favor_de" id="<portlet:namespace />a_favor_de"/>
					</td>
					<td colspan="2">
						<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarPago();" />
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
				<jsp:include page='canje_cheques_agregados_search_result.jsp' /></div>
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
			var idCtaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();

			if (nro == "" || idCtaBcria == 0){
				alert("Debe completar el numero.");
				jQuery('#<portlet:namespace />nro_pago').focus();
				jQuery('#<portlet:namespace />agregandoIngreso').hide();	
				return false;
			} 
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/canje_cheque_agregar_cheque&nro=' +nro 
			+ '&importe_pago=' + importe
			+ '&esEdicion=' +"<%=esEdicion%>" 	
			+'&id_cta_bcria=' + idCtaBcria
			+'&aFavorDe=' + encodeURI(aFavorDe);
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														 jQuery('#<portlet:namespace />nro_pago').val("");
														 jQuery('#<portlet:namespace />importe_pago').val("");
														 jQuery('#<portlet:namespace />a_favor_de').val("");
										   }
			 );	

	}

	function borraPago(nro,  idCtaBcria, importe){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/canje_cheque_sacar_cheque'
			+  '&nro=' +nro
			+  '&id_cta_bcria=' + idCtaBcria
			+  '&importe_pago=' + importe
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																			   }
															   );
	}

	
	jQuery('#<portlet:namespace />agregandoIngreso').hide();


</script>
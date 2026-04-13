<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	if(renderResponse.getNamespace().equals("_TES_1_")){
		portlet_name = "tesoreria";
	}
	
	OrdenPago ordenPago = (OrdenPago) request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

	String esEdicionStr = (String) request
			.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
	boolean esEdicion = false;
	if (ordenPago == null || ordenPago.getId() == null
			|| ordenPago.getId().equals(0) || esEdicionStr != null) {
		esEdicion = true;
	}

%>


<table width="100%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td>
						<liferay-ui:message key="numero"/>:
					</td>
					<td>
						<input type="text" value="" name="<portlet:namespace />nro_anticipo" id="<portlet:namespace />nro_anticipo" />
					</td>
					<td>
							<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarAnticipo();" />
					</td>
				</tr>
				</table>
			</td>
		</tr>
		<%} %>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />agregandoAnticipo">
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
			<div align="center" id="<portlet:namespace />anticipos">
			<jsp:include page='orden_pago_ospim_anticipos_search_result.jsp' /></div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">
	
	function <portlet:namespace />agregarAnticipo(){

			var nro=jQuery('#<portlet:namespace />nro_anticipo').val();

			if (trim(nro)==""){
				alert("Debe completar el numero.");
				jQuery('#<portlet:namespace />nro_anticipo').focus();
				return false;
			} 

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago&nro=' +nro
			url += '&id_cta_bcria=0'; 
			url += '&tipo=<%=Anticipo.class.getName() %>';
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />agregandoAnticipo').show();
			jQuery('#<portlet:namespace />anticipos').load(url, function() {
														 jQuery('#<portlet:namespace />nro_anticipo').val("");
														 jQuery('#<portlet:namespace />agregandoAnticipo').hide();
														 recalcularTotales();
										   }
			 );	
	}

	function borraAnticipo(tipo, nro,  idCtaBcria, importe){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_op_pago'
			+  '&tipo=<%=Anticipo.class.getSimpleName() %>'
			+  '&nro=' +encodeURI(nro)
			+  '&id_cta_bcria=' + idCtaBcria
			+  '&importe_pago=' + importe
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />agregandoAnticipo').show();
			
			jQuery('#<portlet:namespace />anticipos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoAnticipo').hide();
																						recalcularTotales();
																			   }
															   );
	}

	jQuery('#<portlet:namespace />agregandoAnticipo').hide();
</script>
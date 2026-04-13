<%@ include file="/html/portlet/farmacia/init.jsp" %>

<%

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);
	List<Concepto> conceptos = (List<Concepto>) request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_EGRESOS_AMTIMA);
%>


<table width="60%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
			<tr>
				<td>
					<liferay-ui:message key="conceptos"/>:&nbsp;
				</td>
				<td>
				<select id="<portlet:namespace />id_concepto" name="<portlet:namespace />id_concepto">
					<% 	for (Concepto concepto : conceptos) {  %>
							<option value="<%=concepto.getId()%>"><%=concepto.getDescripcion()%></option>
						<%	 } %>
				</select>
				</td>
				<td>
					<input type="text" value="" name="<portlet:namespace />importe_concepto" id="<portlet:namespace />importe_concepto" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
				</td>
				<td>
					<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarConcepto();" />
				</td>
			</tr>
		<%} %>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoIngreso">
				<table style="align: center;" width="100%">
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
			<td colspan="4">
				<div align="center" id="<portlet:namespace />ingresos">
					<liferay-util:include page="/html/portlet/farmacia/comprobantes/conceptos_amtima_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">
	function <portlet:namespace />agregarConcepto(){
			jQuery('#<portlet:namespace />agregandoIngreso').show();	

			var id = jQuery('#<portlet:namespace />id_concepto').val();
			var importe = jQuery('#<portlet:namespace />importe_concepto').val();

			if (trim(importe) == ""){
				alert("Por favor ingrese el importe del concepto");
				return false;
			}
			 
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/agregar_concepto_amtima' 
			+ '&id_concepto=' + id
			+ '&importe_concepto=' +importe
			+ '&esAmtima=esAmtima'
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														sugerirEnConcepto();
										   }
			 );	
	}

	function borraConcepto(id){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/sacar_concepto_amtima'
			+  '&id_concepto=' +id
			+ '&esAmtima=esAmtima'
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						sugerirEnConcepto();
																			   }
															   );
	}
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
</script>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
fechaInicio.setTime(new Date());
%>
	<table>
		<tr>
			<td colspan="2">
				<input type="submit" value="<liferay-ui:message key="volver" />" onClick="<portlet:namespace />volver();return false;"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="cuil" />:</label></td>
				<td width="80%"><input type="text" name="<portlet:namespace />cuil_manual" id="<portlet:namespace />cuil_manual" maxlength="11" /></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="remuneracion-declarada" />:</label></td>
				<td><input type="text" name="<portlet:namespace />remuneracion_declarada_manual" id="<portlet:namespace />remuneracion_declarada_manual"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="calculado" />&nbsp;(aportes + contribuciones):</label></td>
				<td><input type="text" name="<portlet:namespace />calculado_manual" id="<portlet:namespace />calculado_manual" onchange="sugerirSubtotal();"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="pagado" />:</label></td>
				<td><input type="text" name="<portlet:namespace />pagado_manual" id="<portlet:namespace />pagado_manual"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="fecha-pago" />:</label></td>
				<td><liferay-ui:input-date dayParam="fechaPagoDia1"
					dayValue="<%= fechaInicio.get(Calendar.DATE) %>"
					dayNullable="true"
					monthParam="fechaPagoMes1"
					monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"
					yearParam="fechaPagoAnio1"
					yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 30 %>"
					yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 10%>"
					firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
					disabled="false" /></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="subtotal" />:</label></td>
				<td><input type="text" name="<portlet:namespace />subtotal_manual" id="<portlet:namespace />subtotal_manual"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="interes" />:</label></td>
				<td><input type="text" name="<portlet:namespace />interes_manual" id="<portlet:namespace />interes_manual"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="apellido" />:</label></td>
				<td><input type="text" name="<portlet:namespace />apellido_manual" id="<portlet:namespace />apellido_manual"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="nombre" />:</label></td>
				<td><input type="text" name="<portlet:namespace />nombre_manual" id="<portlet:namespace />nombre_manual"/></td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarPeri();return false;"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Periodo:&nbsp;<%= (String)request.getAttribute("mostrar_periodo")%></b>
				<input type="hidden" name="<portlet:namespace />periodo" id="<portlet:namespace />periodo" value="<%= (String)request.getAttribute("mostrar_periodo")%>" id="periodo"/>
			</td>
		</tr>
		<tr>
			<td  colspan="2" align="center">		
				
		<portlet:defineObjects/>
			<%
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS) ||  PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");				
					List<ActaPeriodoDeudaEmpresa> peris= (ArrayList<ActaPeriodoDeudaEmpresa>)renderRequest.getAttribute(WebKeysTesoreria.ACTAS_PERIODOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();

			 		headerNames.add("cuil");
			 		headerNames.add("remuneracion-declarada");
			 		headerNames.add("calculado");
			 		headerNames.add("pagado");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("subtotal");
			 		headerNames.add("interes");
			 		headerNames.add("apellido");
			 		headerNames.add("nombre");
					if(showABMButtons ) { 
						headerNames.add("borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-actas-were-found"));
					int cant = 0;
					if(null!=peris){
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < peris.size(); i++) {
					 		ActaPeriodoDeudaEmpresa peri = (ActaPeriodoDeudaEmpresa) peris.get(i);
					 		if (!peri.isBorradoLogico()){
						 		for (int j = 0; j < peri.getDetalle().size(); j++) {
						 				ActaPeriodoDeudaEmpresa.Detalle det = peri.getDetalle().get(j); 
							 			if (!det.isBorradoLogico()){
						 					ResultRow row = new ResultRow(det, det.getId(), i);
							 				row.addText(peri.getCuil());
							 				row.addText(peri.getRemuneracionDeclarada().toString());
							 				row.addText(peri.getCalculado().toString());
							 				row.addText(det.getMontoPagadoAsString());
							 				row.addText(det.getFechaPagadoAsString());
							 				row.addText(det.getCapital().toString());
							 				row.addText(det.getInteres().toString());
							 				row.addText(peri.getApellido());
							 				row.addText(peri.getNombre());
											// Action
											if(showABMButtons) {
												StringBuilder sb= new StringBuilder();
							 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
							 					sb.append(themeDisplay.getPathThemeImages());
							 					sb.append("/common/delete.png\" onClick=\"javascript:borraPeriodoManual('");
							 					sb.append((String)request.getAttribute("mostrar_periodo"));
							 					sb.append("','");
							 					sb.append(String.valueOf(det.getId()));
							 					sb.append("','");
							 					sb.append(peri.getCuil());
							 					sb.append("');\" />");
												row.addText(sb.toString());
											}
								 			resultRows.add(row);
								 			cant++;
							 			}
						 		}
					 		}
					 	}
					 	searchContainer.setTotal(cant);
				 	}
			%>

				<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
			</td>
		</tr>
	</table>
	<script type="text/javascript">
	function <portlet:namespace />agregarPeri() {
		var fechaPagoDia  = document.getElementById("<portlet:namespace />fechaPagoDia1").value;
		var fechaPagoMes= document.getElementById("<portlet:namespace />fechaPagoMes1").value;
		var fechaPagoAnio = document.getElementById("<portlet:namespace />fechaPagoAnio1").value;
		
		var cuil = jQuery('#<portlet:namespace />cuil_manual').val();
		var remuneracion_declarada = jQuery('#<portlet:namespace />remuneracion_declarada_manual').val();
		var calculado = jQuery('#<portlet:namespace />calculado_manual').val();
		var pagado = jQuery('#<portlet:namespace />pagado_manual').val();
		var apellido = jQuery('#<portlet:namespace />apellido_manual').val();
		var nombre = jQuery('#<portlet:namespace />nombre_manual').val();
		var periodo = jQuery('#<portlet:namespace />periodo').val();
		var subtotal = jQuery('#<portlet:namespace />subtotal_manual').val();
		var interes = jQuery('#<portlet:namespace />interes_manual').val();
		
		
		if (trim(remuneracion_declarada) == ""){
			alert("Por favor, ingrese una remuneracion declarada");
			return;	
		}
		
		if (trim(calculado) == ""){
			alert("Por favor, ingrese el calculado(aportes + contribuciones)");
			return;	
		}
		
		if (trim(subtotal) == ""){
			alert("Por favor, ingrese un subtotal");
			return;	
		}
		
		if (trim(interes) == ""){
			alert("Por favor, ingrese un interes");
			return;	
		}
		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/agregar_periodo_manual';
		<%}else{%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/agregar_periodo_manual';
		<%}%>
	   	url += '&fechaPagoDia=' + fechaPagoDia;
	   	url += '&fechaPagoMes=' + fechaPagoMes;
	   	url += '&fechaPagoAnio=' + fechaPagoAnio;
	   	url += '&cuil=' + trim(cuil);
	   	url += '&remuneracion_declarada=' + trim(remuneracion_declarada);
	   	url += '&calculado=' + trim(calculado);
	   	url += '&pagado=' + trim(pagado);
	   	url += '&apellido=' + encodeURI(apellido);
	   	url += '&nombre=' + encodeURI(nombre);
	   	url += '&subtotal=' + trim(subtotal);
	   	url += '&interes=' + trim(interes);
	   	url += '&periodo=' + escape(periodo);
	    url += '&rnd=' + Math.floor(Math.random()*100);
	    
		jQuery(popup).load(url);    
	}
	
	 function <portlet:namespace />volver() {
	 	<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
	 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_periodos';
	 	<%}else{%>
	     	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/buscar_periodos';
	    <%}%>
	     
	    	 url += '&esEdicion=esEdicion';
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url);    
	 }
	 
	 function sugerirSubtotal(){
		 var calculado = jQuery('#<portlet:namespace />calculado_manual').val();
		 var subtotal = jQuery('#<portlet:namespace />subtotal_manual').val();
		 if (trim(subtotal)==""){
			 jQuery('#<portlet:namespace />subtotal_manual').val(calculado);
		 }
	 }
	 
	 function borraPeriodoManual(periodo, id, cuil_titular){
		 <%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
		 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/sacar_periodo_acta_manual';
		 <%}else{%>
		  	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/sacar_periodo_acta_manual';
		 <%}%>
	    	 url += '&esEdicion=esEdicion';
	    	 url += '&periodo=' + escape(periodo);
	    	 url += '&cuil_titular=' +escape(cuil_titular);
	    	 url += '&id=' + id;
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url);    
	 }
	 
	 <% if (request.getAttribute(WebKeysTesoreria.ACTA_PERIODOS_SUBTOTAL) != null) { %>
		document.getElementById("<portlet:namespace />subtotal").value = "<%=((BigDecimal)request.getAttribute(WebKeysTesoreria.ACTA_PERIODOS_SUBTOTAL)).toString()%>";
		document.getElementById("<portlet:namespace />inte").value = "<%=((BigDecimal)request.getAttribute(WebKeysTesoreria.ACTA_PERIODOS_INTERES)).toString()%>";
		sumarTodo();
	<% } %>
		
</script>
	 
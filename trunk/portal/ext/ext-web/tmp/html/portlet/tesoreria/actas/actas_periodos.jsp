<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%

String portlet_name = "tesoreria";


if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_EST_1_")){
		portlet_name = "estudio_isidro";
}


if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
}

if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
}
boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);

Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
/* fechaInicio.set(Calendar.DATE, 1);
fechaInicio.set(Calendar.MONTH, 0);
fechaInicio.set(Calendar.YEAR, 2009); */
fechaInicio.add(Calendar.YEAR,-10);
fechaInicio.set(Calendar.DATE,1);

Calendar fechaFin = CalendarFactoryUtil.getCalendar();
/* fechaFin.set(Calendar.DATE, 31);
fechaFin.set(Calendar.MONTH, 11);
fechaFin.set(Calendar.YEAR, 2016); */

Calendar fechaPago = CalendarFactoryUtil.getCalendar();
fechaPago.setTime(new Date());

Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);


boolean esEdicion = false;

if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
 	esEdicion = true;
}
if (acta != null && acta.isActaCerrada()){
	esEdicion = false;
}
String sufi="acta_";
%>
<%if (esEdicion){ %>
<table>
	<tr>
		<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDia1"
			dayValue="<%= fechaInicio.get(Calendar.DATE) %>"
			monthParam="fechaDesdeMes1"
			monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"
			yearParam="fechaDesdeAnio1"
			yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 30 %>"
			yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 10%>"
			firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
			disabled="false" /></td>
		<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaHastaDia2"
			dayValue="<%= fechaFin.get(Calendar.DATE) %>"
			monthParam="fechaHastaMes2"
			monthValue="<%= fechaFin.get(Calendar.MONTH) %>"
			yearParam="fechaHastaAnio2"
			yearValue="<%= fechaFin.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaFin.get(Calendar.YEAR) - 30 %>"
			yearRangeEnd="<%= fechaFin.get(Calendar.YEAR) + 10%>"
			firstDayOfWeek="<%= fechaFin.getFirstDayOfWeek() - 1 %>"
			disabled="false" /></td>
		<td><input type="button"
			value="<liferay-ui:message key="buscar-de-afip" />"
			onClick="<portlet:namespace />agregarDetalleActa();" /></td>
		<td><input type="button"
			value="<liferay-ui:message key="borrar-todos" />"
			onClick="<portlet:namespace />borrarTodos();" /></td>
		<td><input type="button"
			value="Proponer Períodos"
			onClick="<portlet:namespace />proponerPeriodos();" /></td>		
		<% if (acta != null && acta.getId()>0 && acta.getPeriodos() != null && !acta.getPeriodos().isEmpty()){%>
		<td><input type="button"
			value="<liferay-ui:message key="ver-detalle-excel" />"
			onClick="<portlet:namespace />reporteDetalle();" />&nbsp;
			<label><liferay-ui:message
					key="incluir-fecha-pago" />:</label>&nbsp; <input type="checkbox"
					id="<portlet:namespace />incluir_fecha_pago"
					name="<portlet:namespace />incluir_fecha_pago" value="false"/>
		</td>
		<%} %>
	</tr>
	<tr>
		<td colspan="7">&nbsp;</td>
	</tr>
	<tr>
		<td><liferay-ui:message key="periodo" />:</td>
		<td colspan="6"><liferay-ui:input-date
			dayParam="periodoManualDia"
			dayNullable="true"
			dayValue=""
			monthAndYearParam="periodoManualMesAnio"
			monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"
			monthAndYearNullable="true"
			yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"							
			yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 30 %>"
			yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
			disabled="false" />&nbsp;<input type="button"
			value="<liferay-ui:message key="agregar-periodo-manual" />"
			onClick="<portlet:namespace />agregarPeriodoManual();" /></td>
	</tr>
	<tr>
		<td colspan="7">&nbsp;</td>
	</tr>
</table>
<%} else{ %>
<table>
	<tr>
		<% if (acta != null && acta.getId()>0 && acta.getPeriodos() != null && !acta.getPeriodos().isEmpty()){%>
		<td><input type="button"
			value="<liferay-ui:message key="ver-detalle-excel" />"
			onClick="<portlet:namespace />reporteDetalle();" />&nbsp;
			<label><liferay-ui:message
					key="incluir-fecha-pago" />:</label>&nbsp; <input type="checkbox"
					id="<portlet:namespace />incluir_fecha_pago"
					name="<portlet:namespace />incluir_fecha_pago" value="false"/>
		</td>
		<%} %>
	</tr>
</table>
<%} %>
<div align="center" id="<portlet:namespace />agregandoDiv">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>



<portlet:defineObjects />
<% 
				
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
				List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("periodo");
		 		headerNamesTercerizadora.add("cant-afiliados-declarados");
		 		headerNamesTercerizadora.add("cant-afiliados-pagados");
		 		headerNamesTercerizadora.add("rem-declarada");
		 		headerNamesTercerizadora.add("calculado");
		 		headerNamesTercerizadora.add("pagado");
		 		headerNamesTercerizadora.add("interes-calculado");
		 		headerNamesTercerizadora.add("deuda");
		 		
				
		 		if (esEdicion){
					headerNamesTercerizadora.add("Borrar");
					headerNamesTercerizadora.add("Editar");
		 		}
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-detalle-acta-were-found"));
				BigDecimal totalImporte = new BigDecimal("0");
				BigDecimal intTotalCalculado=new BigDecimal("0");
				if (acta != null){
					List<ActaPeriodoDeudaEmpresa> peris= acta.getPeriodos();
					SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
					SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
					
					if(null!=peris){
						ActaPeriodoDeudaEmpresa periodoAMostrar = null; 
						Date periodo = null;
						BigDecimal totalParcial = new BigDecimal("0");
						int cantAfiliadosDeclarados= 0;
						int cantAfiliadosPagados= 0;
						BigDecimal remDeclarada=new BigDecimal("0");
						BigDecimal pagado=new BigDecimal("0");
						BigDecimal calculado=new BigDecimal("0");
						BigDecimal intCalculado=new BigDecimal("0");
	 			 		
		 				List resultRowsDetalle = searchContainer.getResultRows();
		 				int i = 0;
		 			 	for (i = 0; i < peris.size(); i++) {
		 			 		if (peris.get(i).isBorradoLogico()){
		 			 			continue;
		 			 		}
			 				boolean sumado = false;
		 			 		for (ActaPeriodoDeudaEmpresa.Detalle det : peris.get(i).getDetalle()){
		 			 			if (det.isBorradoLogico()){
		 			 				continue;
		 			 			}
			 			 		if (!peris.get(i).getPeriodo().equals(periodo)){
			 			 			if (periodo != null){
			 			 				ResultRow rowDetalle = new ResultRow(periodoAMostrar.getPeriodo(),periodoAMostrar.getPeriodo().toString(), i-1);
					 					rowDetalle.addText(format.format(periodoAMostrar.getPeriodo()));
					 					rowDetalle.addText(String.valueOf(cantAfiliadosDeclarados));
					 					rowDetalle.addText(String.valueOf(cantAfiliadosPagados));
					 			 		rowDetalle.addText(remDeclarada.toString());
					 			 		rowDetalle.addText(calculado.toString());
					 			 		rowDetalle.addText(pagado.toString());
					 			 		rowDetalle.addText(intCalculado.toString());
					 					rowDetalle.addText(totalParcial.toString());					 					
					 					resultRowsDetalle.add(rowDetalle);
					 					if (esEdicion){
					 						StringBuilder sb= new StringBuilder();
						 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
						 					sb.append(themeDisplay.getPathThemeImages());
						 					sb.append("/common/delete.png\" onClick=\"javascript:borraPeriodo('");
						 					sb.append(format.format(periodoAMostrar.getPeriodo()));
						 					sb.append("');\" />");
						 					rowDetalle.addText(sb.toString());
						 					StringBuilder sb2= new StringBuilder();
						 					sb2.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
						 					sb2.append(themeDisplay.getPathThemeImages());
						 					sb2.append("/common/edit.png\" onClick=\"javascript:editarPeriodo('");
						 					Calendar cal = Calendar.getInstance();
						 					cal.setTime(periodoAMostrar.getPeriodo());
						 					cal.add(Calendar.MONTH, -1);
						 					sb2.append(format.format(cal.getTime()));
						 					sb2.append("');\" />");
						 					rowDetalle.addText(sb2.toString());
					 					}
			 			 			}
			 			 			periodoAMostrar = peris.get(i); 
			 			 			periodo = peris.get(i).getPeriodo();
			 			 			totalParcial = new BigDecimal("0");
			 			 			cantAfiliadosDeclarados= 0;
									cantAfiliadosPagados= 0;
									remDeclarada=new BigDecimal("0");
									pagado=new BigDecimal("0");
									calculado=new BigDecimal("0");
									intCalculado=new BigDecimal("0");
			 			 		}
			 			 		totalParcial = totalParcial.add(det.getCapital() != null ? det.getCapital() : new BigDecimal("0"));
			 			 		totalImporte = totalImporte.add(det.getCapital() != null ? det.getCapital() : new BigDecimal("0"));
			 			 		
			 			 		if (!sumado){
				 			 		if (!peris.get(i).equals("00000000000")){// && peris.get(i).getRemuneracionDeclarada().doubleValue() != 0D){
				 			 			cantAfiliadosDeclarados++;
				 			 		}
				 			 		if (det.getMontoPagado() != null && det.getMontoPagado().doubleValue() != 0D){
										cantAfiliadosPagados++;
				 			 		}
									remDeclarada= remDeclarada.add(peris.get(i).getRemuneracionDeclarada());
									calculado= calculado.add(peris.get(i).getCalculado());
									sumado = true;
			 			 		}
								pagado= pagado.add(det.getMontoPagado() != null ? det.getMontoPagado() : new  BigDecimal("0"));
								intCalculado  = intCalculado.add(det.getInteres());
								intTotalCalculado = intTotalCalculado.add(det.getInteres());
				 			}
		 			 	}
		 			 	if (i>0 && periodoAMostrar != null && !periodoAMostrar.isBorradoLogico()){
		 			 		ResultRow rowDetalle = new ResultRow(periodoAMostrar.getPeriodo(),periodoAMostrar.getPeriodo().toString(), i-1);
		 					rowDetalle.addText(format.format(periodoAMostrar.getPeriodo()));
		 					rowDetalle.addText(String.valueOf(cantAfiliadosDeclarados));
		 					rowDetalle.addText(String.valueOf(cantAfiliadosPagados));
		 			 		rowDetalle.addText(remDeclarada.toString());
		 			 		rowDetalle.addText(calculado.toString());
		 			 		rowDetalle.addText(pagado.toString());		 					
		 					rowDetalle.addText(intCalculado.toString());
		 					rowDetalle.addText(totalParcial.toString());
		 					resultRowsDetalle.add(rowDetalle);
		 					if (esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraPeriodo('");
			 					sb.append(format.format(periodoAMostrar.getPeriodo()));
			 					sb.append("');\" />");
			 					rowDetalle.addText(sb.toString());
			 					StringBuilder sb2= new StringBuilder();
			 					sb2.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
			 					sb2.append(themeDisplay.getPathThemeImages());
			 					sb2.append("/common/edit.png\" onClick=\"javascript:editarPeriodo('");
			 					Calendar cal = Calendar.getInstance();
			 					cal.setTime(periodoAMostrar.getPeriodo());
			 					cal.add(Calendar.MONTH, -1);
			 					sb2.append(format.format(cal.getTime()));
			 					sb2.append("');\" />");
			 					rowDetalle.addText(sb2.toString());
		 					}
		 			 	}
					}
				}
 		%>


<liferay-ui:search-iterator paginate="false"
	searchContainer="<%=searchContainer%>" />




<script type="text/javascript">
		function <portlet:namespace />agregarDetalleActa(){
				if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)==""){
					alert("Primero debe seleccionar una empresa");
					return false;
				}
				jQuery('#<portlet:namespace />agregandoDiv').show();
				var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
				var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
				var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio1");

				var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
				var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
				var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_periodo_acta'
					+'&fechaDesdeDia='+fechaDesdeDia.value
					+'&fechaDesdeMes='+fechaDesdeMes.value
					+'&fechaDesdeAnio='+fechaDesdeAnio.value
					+'&fechaHastaDia='+fechaHastaDia.value
					+'&fechaHastaMes='+fechaHastaMes.value
					+'&fechaHastaAnio='+fechaHastaAnio.value
					+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)
					+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
					+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
					+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;
					url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery(popup).load(url);	
		}
		
		function borraPeriodo(fecha){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta'
				+'&fecha='+fecha;			
				
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		}
		
		function editarPeriodo(fecha){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_periodo_acta'
			+'&periodoEnEdicion='+fecha;
			
			
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		}

		function <portlet:namespace />borrarTodos(){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta'
				+'&todos=todos';
			
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);
		}
		jQuery('#<portlet:namespace />agregandoDiv').hide();

		function <portlet:namespace />utilizarTotal(){
			document.getElementById("<portlet:namespace />subtotal").value = "<%=totalImporte.toString()%>";
			document.getElementById("<portlet:namespace />inte").value = "<%=intTotalCalculado.toString()%>";
			sumarTodo();
		}

		 function <portlet:namespace />reporteDetalle() {
			 <%if (esEdicion){ %>
			 <portlet:namespace />utilizarTotal();
			 <%}%>
			 
			 var otros = document.getElementById("<portlet:namespace />otros").value; 
			 var subtotal = document.getElementById("<portlet:namespace />subtotal").value;
			 var inte = document.getElementById("<portlet:namespace />inte").value;
			 var incluir_fecha_pago = document.getElementById("<portlet:namespace />incluir_fecha_pago").checked;			 
				var url = '/xlsservlet/?reporte=ACTA_PERIODOS_DETALLE&totales=totales&otros=' + otros +
					'&subtotal=' + subtotal +
					'&inte=' + inte +
					'&acta_numero='  + trim(document.getElementById("<portlet:namespace />acta_numero").value) +
					'&cuit=' + trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value) + 
					'&desc=' + escape(trim(document.getElementById("<portlet:namespace />entidad<%=sufi%>").value)) 
					+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
					+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
					+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value
					+'&incluirFechaPago='+incluir_fecha_pago;
				  url += '&rnd=' + Math.floor(Math.random()*100);
				  window.location.href = url;
		 }
		 
		 function <portlet:namespace />agregarPeriodoManual() {
			 var periodoManualMesAnio = document.getElementById("<portlet:namespace />periodoManualMesAnio").value;			 
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_periodo_acta'
					+'&periodoEnEdicion=' + periodoManualMesAnio;
			 
			 
			 url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery(popup).load(url);	
		 }

		 jQuery("#<portlet:namespace />periodoManualDia").hide();
		 
		 <%if (esEdicion){ %>
		 <portlet:namespace />utilizarTotal();
		 <%}%>
		 
		 
		 function <portlet:namespace />proponerPeriodos(){
				if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)==""){
					alert("Primero debe seleccionar una empresa");
					return false;
				}
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/proponer_periodo_acta'
					+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)
					+'&entidad=OSPIM';
					url += '&rnd=' + Math.floor(Math.random()*100);
					
				jQuery.ajax({   
					url: url,
					success: function(data){
						jQuery('#<portlet:namespace />buscando').hide();
						var obj = jQuery.parseJSON(data);
						var dia=obj.dia;
						var mes=obj.mes-1;
						var anio=obj.anio;
						jQuery('#<portlet:namespace />fechaDesdeDia1').val(dia);
						jQuery('#<portlet:namespace />fechaDesdeMes1').val(mes);
						jQuery('#<portlet:namespace />fechaDesdeAnio1').val(anio);
					}
				});		

		}

	</script>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
//LISTA DE TIPOS DE BOLETA....
List<ConvenioNacion> convenioNac = TraeListasServiceUtil.getConvenioNac(renderRequest);

String portlet_name = null;

if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
}

if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
} 
boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);

Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
fechaInicio.setTime(new Date());
fechaInicio.add(Calendar.YEAR,-5);
fechaInicio.set(Calendar.DATE,1);

Calendar fechaFin = CalendarFactoryUtil.getCalendar();

Calendar fechaPago = CalendarFactoryUtil.getCalendar();
fechaPago.setTime(new Date());

Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

SimpleDateFormat format =null;
Date fechaIniPeriodo=null;
Date fechaFinPeriodo=null;

boolean esEdicion = false;

if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
 	esEdicion = true;
}
HashMap<Date, TotalActaNoOS> peris=null;
if (acta != null){
	 peris= acta.generarTotalesAgrupados();
	if(acta.isActaCerrada()){
		esEdicion = false;
	}
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
			value="<liferay-ui:message key="buscar-deuda-portal-molineros" />"
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
			onClick="<portlet:namespace />reporteDetalle();" /></td>
		<td><input type="button"
			value="<liferay-ui:message key="ver-nomina-excel" />"
			onClick="<portlet:namespace />reporteNominaDetalle();" /></td>	
		<%} %>
	</tr>
	<tr>
		<td colspan="7">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="3"><input type="button"
			value="<liferay-ui:message key="agregar-periodo-manual" />"
			onClick="<portlet:namespace />agregarPeriodoManual();" />&nbsp;<liferay-ui:input-date
				dayParam="periodoManualDia" dayNullable="true" dayValue=""
				monthAndYearParam="periodoManualMesAnio"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"
				monthAndYearNullable="true"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 30 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 10 %>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="false" /></td>
		<td colspan="4">
			<input type="button"
				value="Quitar Tipos de Aporte del cálculo"
				onClick="<portlet:namespace />borrarAportedeCalculo();" />&nbsp
				<select  name="<portlet:namespace/>tipoBoletaBorrar" id="<portlet:namespace/>tipoBoletaBorrar">						
	 					<% for (ConvenioNacion convenioNacion : convenioNac) {	%>	 						
	 						<%if(portlet_name.equals("uoma") && convenioNacion.getUoma()&& convenioNacion.getTipo_boleta()<6) { %>
	 							<option	value="<%= convenioNacion.getTipo_boleta() %>"selected><%=convenioNacion.getDescripcion()%></option>
							<% }else if(portlet_name.equals("farmacia") && convenioNacion.getAmtima()&& convenioNacion.getTipo_boleta()<6) { %>
								<option	value="<%= convenioNacion.getTipo_boleta() %>"selected><%=convenioNacion.getDescripcion()%></option>
							<%} else if(portlet_name.equals("estudio_isidro") && convenioNacion.getTipo_boleta()<6){%>
							 	<option	value="<%= convenioNacion.getTipo_boleta() %>"selected><%=convenioNacion.getDescripcion()%></option>
							<%}
						} %>	
				</select>&nbsp;
		</td>		
	</tr>	
	<tr>
		<td colspan="7">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="7">
			<%if(null!=acta && acta.getCamaras().trim().length()>2){%>
				<b>CAMARAS EN CALCULO: <%=acta.getCamaras() %></b>
			<%}else{%>
				&nbsp;
			<%}%>
		</td>
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
<div align="center" id="<portlet:namespace />guardandoDiv">
	<table style="align: center;">
		<tr>
			<td><liferay-ui:message key='saving-calculo' /></td>
			<td align="center"><img
				alt="<liferay-ui:message key='saving-calculo'/>"
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
		 		headerNamesTercerizadora.add("rem-declarada");
		 		if(acta!=null&&acta.getEntidad()!=null?acta.getEntidad().contains("A.M.T.I.M.A"):1==1){
		 			headerNamesTercerizadora.add("amtima");
		 			headerNamesTercerizadora.add("interes-amtima");
		 		}else{
			 		headerNamesTercerizadora.add("art-46");
			 		headerNamesTercerizadora.add("interes-art46");
			 		headerNamesTercerizadora.add("cuota-usufructo");
			 		headerNamesTercerizadora.add("interes-usufructo");
			 		headerNamesTercerizadora.add("cuota-social-uoma");
			 		headerNamesTercerizadora.add("interes-sindicato");
			 		headerNamesTercerizadora.add("aporte-solidario-uoma");
			 		headerNamesTercerizadora.add("interes-solidario");			 		
		 		}
		 		headerNamesTercerizadora.add("deuda");
		 		
				
		 		if (esEdicion){
					headerNamesTercerizadora.add("editar-borrar");					
		 		}
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-detalle-acta-were-found"));				
				
				
				if (acta != null){
					
					format = new SimpleDateFormat("MM-yyyy");
					SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
					
					if(null!=peris){
						TotalActaNoOS periodoAMostrar = null; 
						Date periodo = null;
										 			 		
		 				List resultRowsDetalle = searchContainer.getResultRows();
		 				int i = 0;
		 				if(peris!=null){
		 					ArrayList<Date> periodosList=new ArrayList<Date>();
		 					Set <Date> setPeriodo=(Set)peris.keySet();
		 					periodosList.addAll(setPeriodo);
		 					Collections.sort(periodosList);
		 					
		 					for(Date periodoL: periodosList){		 						
		 						periodoAMostrar=peris.get(periodoL);	
			 			 		
			 			 		//PRIMERA FECHA PERIODO
			 			 		if(fechaIniPeriodo==null || (fechaIniPeriodo!=null && fechaIniPeriodo.after(periodoAMostrar.getPeriodo()))){
			 			 			fechaIniPeriodo=periodoAMostrar.getPeriodo();
			 			 		}
			 			 		if(fechaFinPeriodo==null || (fechaFinPeriodo!=null && fechaFinPeriodo.before(periodoAMostrar.getPeriodo()))){
			 			 			fechaFinPeriodo=periodoAMostrar.getPeriodo();
			 			 		}
			 			 		
				 				
			 			 		if (periodoAMostrar != null){
				 			 		ResultRow rowDetalle = new ResultRow(periodoAMostrar.getPeriodo(),periodoAMostrar.getPeriodo().toString(), i-1);
				 					rowDetalle.addText(format.format(periodoAMostrar.getPeriodo()));
				 					rowDetalle.addText(String.valueOf(periodoAMostrar.getCantTotalAfiliados()));		 					
				 			 		rowDetalle.addText(periodoAMostrar.getRemuneracionTotalDeclarada().toString());
				 			 		if(acta!=null&&acta.getEntidad()!=null?acta.getEntidad().contains("A.M.T.I.M.A"):true){
				 			 			rowDetalle.addText(periodoAMostrar.getCapitalAmtima().toString());
					 			 		rowDetalle.addText(periodoAMostrar.getInteresAmtima().toString());
				 			 		}else{
					 			 		rowDetalle.addText(periodoAMostrar.getCapitalArt46().toString());
					 			 		rowDetalle.addText(periodoAMostrar.getInteresArt46().toString());
					 			 		rowDetalle.addText(periodoAMostrar.getCapitalUsufructo().toString());
					 			 		rowDetalle.addText(periodoAMostrar.getInteresUsufructo().toString());		 			 		
					 			 		rowDetalle.addText(periodoAMostrar.getCapitalSindicato().toString());
					 					rowDetalle.addText(periodoAMostrar.getInteresSindicato().toString());	
					 					rowDetalle.addText(periodoAMostrar.getCapitalSolidario().toString());
					 					rowDetalle.addText(periodoAMostrar.getInteresSolidario().toString());
				 					}
				 					rowDetalle.addText(periodoAMostrar.getTotal().toString());	 					
				 					resultRowsDetalle.add(rowDetalle);
				 					if (esEdicion){		 						
					 					StringBuilder sb2= new StringBuilder();
					 					sb2.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
					 					sb2.append(themeDisplay.getPathThemeImages());
					 					sb2.append("/common/edit.png\" onClick=\"javascript:editarPeriodo('");
					 					Calendar cal = Calendar.getInstance();
					 					cal.setTime(periodoAMostrar.getPeriodo());
					 					cal.add(Calendar.MONTH, -1);
					 					sb2.append(format.format(cal.getTime()));
					 					sb2.append("');\" />/");
					 					sb2.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
					 					sb2.append(themeDisplay.getPathThemeImages());
					 					sb2.append("/common/delete.png\" onClick=\"javascript:borraPeriodo('");
					 					sb2.append(format.format(periodoAMostrar.getPeriodo()));
					 					sb2.append("');\" />");
					 					rowDetalle.addText(sb2.toString());
				 					}
				 			 	}
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
				if (trim(document.getElementById("<portlet:namespace />entidad").value)==""){
					alert("Debe seleccionar una entidad");
					return false;
				}
				jQuery('#<portlet:namespace />agregandoDiv').show();
				var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
				var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
				var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio1");

				var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
				var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
				var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
				var entidad=document.getElementById("<portlet:namespace/>entidad").value;
												
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_periodo_acta_no_os';				
				url+='&fechaDesdeDia='+fechaDesdeDia.value;				
				url+='&fechaDesdeMes='+fechaDesdeMes.value;				
				url+='&fechaDesdeAnio='+fechaDesdeAnio.value;				
				url+='&fechaHastaDia='+fechaHastaDia.value;				
				url+='&fechaHastaMes='+fechaHastaMes.value;				
				url+='&fechaHastaAnio='+fechaHastaAnio.value;				
				url+='&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value);				
				url+='&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value;				
				url+='&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value;				
				url+='&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;				
				url += '&rnd=' + Math.floor(Math.random()*100)
				url += '&entidad='+entidad;
				jQuery(popup).load(url);	
		}
		
		function <portlet:namespace />borrarAportedeCalculo(){
			var tipo_aporte=jQuery('#<portlet:namespace/>tipoBoletaBorrar').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta_no_os'
				+'&tipo_aporte='+tipo_aporte;
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		}
		
		function borraPeriodo(fecha){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta_no_os'
				+'&fecha='+fecha;
			
				
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		}
		
		function editarPeriodo(fecha){
			var entidad=document.getElementById("<portlet:namespace/>entidad").value;			
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_periodo_acta_no_os'
				+'&periodoEnEdicion='+fecha+'&entidad='+entidad;
			
			
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		}

		function <portlet:namespace />borrarTodos(){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta_no_os'
					+'&todos=todos';
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);
		}
		jQuery('#<portlet:namespace />agregandoDiv').hide();
		jQuery('#<portlet:namespace />guardandoDiv').hide();

		function <portlet:namespace />utilizarTotalUOMA(){
			document.getElementById("<portlet:namespace />subtotal_sindicato").value = "<%=acta.getCapitalSindicato().toString()%>";
			document.getElementById("<portlet:namespace />inte_sindicato").value = "<%=acta.getInteresSindicato().toString()%>";		
			document.getElementById("<portlet:namespace />subtotal_usufructo").value = "<%=acta.getCapitalUsufructo().toString()%>";
			document.getElementById("<portlet:namespace />inte_usufructo").value = "<%=acta.getInteresUsufructo().toString()%>";        				
        	document.getElementById("<portlet:namespace />subtotal_art46").value = "<%=acta.getCapitalArt46().toString()%>";
			document.getElementById("<portlet:namespace />inte_art46").value = "<%=acta.getInteresArt46().toString()%>";        
        	document.getElementById("<portlet:namespace />subtotal_solidario").value = "<%=acta.getCapitalSolidario().toString()%>";
			document.getElementById("<portlet:namespace />inte_solidario").value = "<%=acta.getInteresSolidario().toString()%>";
			
			
			//SETEAR FECHA INICIO Y FIN
			<%if(fechaIniPeriodo!=null && fechaFinPeriodo!=null){
				Calendar fechaAux = CalendarFactoryUtil.getCalendar();
				fechaAux.setTime(fechaIniPeriodo);%>
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeDia").value="<%=fechaAux.get(Calendar.DATE)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeMes").value="<%=fechaAux.get(Calendar.MONTH)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeAnio").value="<%=fechaAux.get(Calendar.YEAR)%>"
			<%	fechaAux.setTime(fechaFinPeriodo);%>
				document.getElementById("<portlet:namespace />fechaPeriodoHastaDia").value="<%=fechaAux.get(Calendar.DATE)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoHastaMes").value="<%=fechaAux.get(Calendar.MONTH)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoHastaAnio").value="<%=fechaAux.get(Calendar.YEAR)%>"				
			<%}%>
			
			
			sumarTodoUOMA();
		}
		
		function <portlet:namespace />utilizarTotalAMTIMA(){			
			document.getElementById("<portlet:namespace />subtotal").value = "<%=acta.getCapitalAmtima().toString()%>";
			document.getElementById("<portlet:namespace />inte").value = "<%=acta.getInteresAmtima().toString()%>";		
			
			//SETEAR FECHA INICIO Y FIN
			<%if(fechaIniPeriodo!=null && fechaFinPeriodo!=null){
				Calendar fechaAux = CalendarFactoryUtil.getCalendar();
				fechaAux.setTime(fechaIniPeriodo);%>
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeDia").value="<%=fechaAux.get(Calendar.DATE)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeMes").value="<%=fechaAux.get(Calendar.MONTH)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoDesdeAnio").value="<%=fechaAux.get(Calendar.YEAR)%>"
			<%	fechaAux.setTime(fechaFinPeriodo);%>
				document.getElementById("<portlet:namespace />fechaPeriodoHastaDia").value="<%=fechaAux.get(Calendar.DATE)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoHastaMes").value="<%=fechaAux.get(Calendar.MONTH)%>"
				document.getElementById("<portlet:namespace />fechaPeriodoHastaAnio").value="<%=fechaAux.get(Calendar.YEAR)%>"				
			<%}%>
			
			
			sumarTodoAMTIMA();
		}

		function <portlet:namespace />reporteDetalle() {			 
		 	var entidad=document.getElementById("<portlet:namespace/>entidad").value;			 	
			var url = '/xlsservlet/?reporte=ACTA_NO_OS_PERIODOS_DETALLE';								
			url+='&acta_id=<%=acta.getId()%>';	
			url+='&entidad=' + entidad;			
			url += '&rnd=' + Math.floor(Math.random()*100);				
			window.location.href = url;
		}
		 
		 function <portlet:namespace />reporteNominaDetalle() {			 
			 var entidad=document.getElementById("<portlet:namespace/>entidad").value;			 	
				var url = '/xlsservlet/?reporte=ACTA_NO_OS_PERIODOS_NOMINA_DETALLE';								
				url+='&acta_id=<%=acta.getId()%>';	
				url+='&entidad=' + entidad;			
				url += '&rnd=' + Math.floor(Math.random()*100);				
				window.location.href = url;
		 }
		 
		 function <portlet:namespace />agregarPeriodoManual() {
		 	 var periodoManualMesAnio = document.getElementById("<portlet:namespace />periodoManualMesAnio").value;
		 	 var entidad=document.getElementById("<portlet:namespace/>entidad").value;	
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_periodo_acta_no_os';
			 url+='&periodoEnEdicion=' + periodoManualMesAnio;
			 url+='&entidad=' + entidad;
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);	
		 }

		 jQuery("#<portlet:namespace />periodoManualDia").hide();
		 
		 function utilizarTotalNoOS(){			 
		 	var ent=trim(document.getElementById("<portlet:namespace />entidad").value);		 	
		 	if(ent=="U.O.M.A."){
		 		 <portlet:namespace />utilizarTotalUOMA();
		 	}else if(ent=="A.M.T.I.M.A."){
		 		 <portlet:namespace />utilizarTotalAMTIMA();
		 	}
		 }
		 function guardarActaVolver(){		   
		   parent.<portlet:namespace />saveActaPeriodo('<%=acta.getId()%>');
		 }
		 
		 <%if(esEdicion){%>
		 	   utilizarTotalNoOS();
		 <%}%>
		 
		 function <portlet:namespace />proponerPeriodos(){
				if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)==""){
					alert("Primero debe seleccionar una empresa");
					return false;
				}
				var entidad=document.getElementById("<portlet:namespace/>entidad").value;
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/proponer_periodo_acta'
					+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)
					+'&entidad='+entidad;
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
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="ar.com.global.beans.TablaEscalaSalarial.Camara" %>

<%
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
String sufi="acta_";
Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Calendar fechaIngreso = CalendarFactoryUtil.getCalendar();
//LISTA DE TIPOS DE BOLETA....
List<ConvenioNacion> convenioNac = (List<ConvenioNacion>) portletSession.getAttribute(WebKeysTesoreria.CONVENIO_EN_SESSION, PortletSession.APPLICATION_SCOPE);

Integer tipoBoletaU=null;
String camaraU="";
List<ActaPeriodoDeudaEmpresa> peris1= (ArrayList<ActaPeriodoDeudaEmpresa>)renderRequest.getAttribute(WebKeysTesoreria.ACTAS_PERIODOS);
if(null!=peris1 && peris1.size()>0){
	ActaPeriodoDeudaEmpresa peri = (ActaPeriodoDeudaEmpresa) peris1.get(peris1.size()-1);
	camaraU=peri.getCamara();
	tipoBoletaU=peri.getTipoAporte();
}
%>
	<table>
		<tr>
			<td colspan="2">
				<input type="submit" value="<liferay-ui:message key="volver" />" onClick="<portlet:namespace />volver();return false;"/>
			</td>
		</tr>		
		<tr>
			<td colspan="2">
				<b>Periodo:&nbsp;<%= (String)request.getAttribute("mostrar_periodo")%></b>				
			</td>
		</tr>
		<tr>
				<td>
					<label><liferay-ui:message key="tipo-de-aporte" />:</label>
				</td>
				<td>
					<select  name="<portlet:namespace/>tipoBoleta" id="<portlet:namespace/>tipoBoleta" onChange="javascript:if(jQuery('#<portlet:namespace />remuneracion_declarada_manual').val()!=''){calcularAporte();}" >
	 					<% for (ConvenioNacion convenioNacion : convenioNac) {	%>
	 						<%if(portlet_name.equals("uoma") && convenioNacion.getUoma()&& convenioNacion.getTipo_boleta()<6) { %>
	 							<option	value="<%= convenioNacion.getTipo_boleta() %>"
	 							<%if(tipoBoletaU!=null && convenioNacion.getTipo_boleta()==tipoBoletaU){%> 
	 							 selected
	 							<%}%>  
	 							>

	 							<%=convenioNacion.getDescripcion()%></option>
							<% }else if(portlet_name.equals("farmacia") && convenioNacion.getAmtima()&& convenioNacion.getTipo_boleta()<6) { %>
								<option	value="<%= convenioNacion.getTipo_boleta() %>"
								<%if(tipoBoletaU!=null && convenioNacion.getTipo_boleta()==tipoBoletaU){%>
								selected
								<%}%> 
								><%=convenioNacion.getDescripcion()%></option>
							<%} else if(portlet_name.equals("estudio_isidro") && convenioNacion.getTipo_boleta()<6){%>
							 	<option	value="<%= convenioNacion.getTipo_boleta() %>"
							 	<%if(tipoBoletaU!=null && convenioNacion.getTipo_boleta()==tipoBoletaU){%>
							 	selected
							 	<%}%> ><%=convenioNacion.getDescripcion()%></option>
							<%}
						} %>	
					</select>
					<input type="radio" name="calculopago" id="calculopago" value="calculo" checked/>Cálculo
					<input type="radio" name="calculopago" id="calculopago" value="pago"/>Ajuste
					
				</td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="cuil" />:</label></td>
				<td width="80%"><input type="text" name="<portlet:namespace />cuil_manual" id="<portlet:namespace />cuil_manual" maxlength="11" /></td>
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
				<td width="20%"><label><liferay-ui:message key="fecha-ingreso" />:</label></td>
				<td>
				<liferay-ui:input-date dayParam="fechaIngresoDia1"
					dayValue="<%= fechaIngreso.get(Calendar.DATE) %>"
					dayNullable="true"
					monthParam="fechaIngresoMes1"
					monthValue="<%= fechaIngreso.get(Calendar.MONTH) %>"
					yearParam="fechaIngresoAnio1"
					yearValue="<%= fechaIngreso.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaIngreso.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
					firstDayOfWeek="<%= fechaIngreso.getFirstDayOfWeek() - 1 %>"
					disabled="false" />
				</td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="camara" />:</label></td>
				<td>
					<select  name="<portlet:namespace/>camara" id="<portlet:namespace/>camara" onChange="if(jQuery('#<portlet:namespace />remuneracion_declarada_manual').val()!=''){calcularAporte();}" >
	 					<% for (Camara camara : Camara.values()) {	%>	 						
	 							<option	value="<%= camara.toString() %>"
	 							<%if(camaraU !=null && camara.toString().equalsIgnoreCase(camaraU)){%> 
	 							selected
	 							<%}%>  
	 							><%=camara.toString() %></option>								
						<% } %>	
					</select>
				</td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="total-cant-afiliados" />:</label></td>
				<td><input type="text" name="<portlet:namespace />cant_afiliados" id="<portlet:namespace />cant_afiliados"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="remuneracion-declarada" />:</label></td>
				<td><input type="text" name="<portlet:namespace />remuneracion_declarada_manual" id="<portlet:namespace />remuneracion_declarada_manual" onBlur="javascript:calcularAporte();"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="calculado" />&nbsp;*:</label></td>
				<td>
					<input type="text" name="<portlet:namespace />calculado_manual" id="<portlet:namespace />calculado_manual" onchange="sugerirSubtotal();"/>
					<span id="span_calculado"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				</td>
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
					disabled="false"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="pagado" />*:</label></td>
				<td><input type="text" name="<portlet:namespace />pagado_manual" id="<portlet:namespace />pagado_manual" onBlur="javascript:calcularInteres();"/></td>
		</tr>		
		<tr>
				<td width="20%"><label><liferay-ui:message key="interes" />*:</label></td>
				<td><input type="text" name="<portlet:namespace />interes_manual" id="<portlet:namespace />interes_manual" onChange="javascript:sugerirSubtotal();"/></td>
		</tr>
		<tr>
				<td width="20%"><label><liferay-ui:message key="subtotal" />:</label></td>
				<td><input type="text" name="<portlet:namespace />subtotal_manual" id="<portlet:namespace />subtotal_manual"/></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>
				<input type="submit" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarPeri();return false;"/>
			</td>

			<td>
				<input type="submit" value="<liferay-ui:message key="agregar-guardar" />" onClick="<portlet:namespace />agregarPeriGuardar();return false;"/>
			</td>
		</tr>	
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
		</tr>		
		<tr>			
			<td colspan="2">
				Quitar Cálculos con criterio: 
				<input type="text" name="<portlet:namespace />cuil_borrar" id="<portlet:namespace />cuil_borrar" size="11" maxsize="11">- 
				<select  name="<portlet:namespace/>tipoBoletaBorrar" id="<portlet:namespace/>tipoBoletaBorrar">
						<option	value="-2" selected>Todos</option>
	 					<% for (ConvenioNacion convenioNacion : convenioNac) {	%>	 						
	 						<%if(portlet_name.equals("uoma") && convenioNacion.getUoma()&& convenioNacion.getTipo_boleta()<6) { %>
	 							<option	value="<%= convenioNacion.getTipo_boleta() %>"><%=convenioNacion.getDescripcion()%></option>
							<% }else if(portlet_name.equals("farmacia") && convenioNacion.getAmtima()&& convenioNacion.getTipo_boleta()<6) { %>
								<option	value="<%= convenioNacion.getTipo_boleta() %>"><%=convenioNacion.getDescripcion()%></option>
							<%} else if(portlet_name.equals("estudio_isidro") && convenioNacion.getTipo_boleta()<6){%>
							 	<option	value="<%= convenioNacion.getTipo_boleta() %>"><%=convenioNacion.getDescripcion()%></option>
							<%}
						} %>	
				</select>&nbsp;
				<input type="submit" value="<liferay-ui:message key="delete" />" onClick="<portlet:namespace />eliminarPeri();return false;"/>				
			</td>

		</tr>
		<tr>
			<td colspan="2">
				&nbsp;<input type="hidden" name="<portlet:namespace />periodo" id="<portlet:namespace />periodo" value="<%= (String)request.getAttribute("mostrar_periodo")%>" id="periodo"/>
					  <input type="hidden" name="<portlet:namespace />interesApago" id="<portlet:namespace />interesApago" value="1"/>
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
			 		headerNames.add("apellido");
			 		headerNames.add("nombre");
			 		headerNames.add("fecha-ingreso");
			 		headerNames.add("camara");
			 		headerNames.add("tipo-de-aporte");
			 		headerNames.add("total-cant-afiliados");
			 		headerNames.add("remuneracion-declarada");
			 		headerNames.add("calculado");
			 		headerNames.add("interes-al-pago");
			 		headerNames.add("pagado");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("interes");
			 		headerNames.add("subtotal");			 		
			 		
					if(showABMButtons ) { 
						headerNames.add("borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,5000, portletURL, headerNames,
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
							 				row.addText(peri.getApellido());
							 				row.addText(peri.getNombre());
							 				row.addText(peri.getFechaIngresoAsString());
							 				row.addText(peri.getCamara());
							 				row.addText(peri.getTipoAporteAsString());
							 				row.addText(String.valueOf(det.getCantidadAfiliados()));
							 				row.addText(peri.getRemuneracionDeclaradaAsString());
							 				row.addText(peri.getCalculado().toString());
							 				row.addText(det.getInteresAFechaPagada().toString());
							 				row.addText(det.getMontoPagadoAsString());
							 				row.addText(det.getFechaPagadoAsString());
							 				row.addText(det.getInteres().toString());
							 				row.addText(peri.getSubtotalNoOS().toString());
							 				
							 				
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
							 					sb.append("','");
							 					sb.append(peri.getTipoAporte());
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
				<liferay-ui:search-iterator  paginate="<%=false%>"  searchContainer="<%= searchContainer %>" />		
			</td>
		</tr>
	</table>
	<script type="text/javascript">

	jQuery("#span_calculado").hide();
	jQuery("#span_interes").hide();
	function <portlet:namespace />agregarPeriGuardar() {		
		<portlet:namespace />agregarPeri(); 
		<portlet:namespace />volverGuardar();		 
	}
	function <portlet:namespace />agregarPeri() {		
		var fechaPagoDia  = document.getElementById("<portlet:namespace />fechaPagoDia1").value;
		var fechaPagoMes= document.getElementById("<portlet:namespace />fechaPagoMes1").value;
		var fechaPagoAnio = document.getElementById("<portlet:namespace />fechaPagoAnio1").value;
		
		var fechaIngresoDia  = document.getElementById("<portlet:namespace />fechaIngresoDia1").value;
		var fechaIngresoMes= document.getElementById("<portlet:namespace />fechaIngresoMes1").value;
		var fechaIngresoAnio = document.getElementById("<portlet:namespace />fechaIngresoAnio1").value;
		
		var cant_afiliados=document.getElementById("<portlet:namespace />cant_afiliados").value;
		var cuil = jQuery('#<portlet:namespace />cuil_manual').val();
		var remuneracion_declarada = jQuery('#<portlet:namespace />remuneracion_declarada_manual').val();
		var calculado = jQuery('#<portlet:namespace />calculado_manual').val();
		var pagado = jQuery('#<portlet:namespace />pagado_manual').val();
		var apellido = jQuery('#<portlet:namespace />apellido_manual').val();
		var nombre = jQuery('#<portlet:namespace />nombre_manual').val();
		var periodo = jQuery('#<portlet:namespace />periodo').val();
		var subtotal = jQuery('#<portlet:namespace />subtotal_manual').val();
		var interes = jQuery('#<portlet:namespace />interes_manual').val();
		var tipo_boleta=jQuery('#<portlet:namespace/>tipoBoleta').val();				
		var camara=jQuery('#<portlet:namespace/>camara').val();
		var cant_afiliados=jQuery('#<portlet:namespace/>cant_afiliados').val();
		var entidad=document.getElementById("<portlet:namespace/>entidad").value;
		var interesAPago=jQuery('#<portlet:namespace/>interesApago').val();
		var calculopago= jQuery("input[name$='calculopago']:checked").val();
		

		/*if(calculopago=="calculo"){
			
			if (trim(remuneracion_declarada) == "" && tipo_boleta!=4){
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
		}else{
			if (trim(pagado) == ""){
				alert("Por favor, ingrese un pago");
				return;	
			}			
		}	*/
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_periodo_manual_no_os';		
	   	url += '&fechaPagoDia=' + fechaPagoDia;
	   	url += '&fechaPagoMes=' + fechaPagoMes;
	   	url += '&fechaPagoAnio=' + fechaPagoAnio;
	   	url += '&fechaIngresoDia=' + fechaIngresoDia;
	   	url += '&fechaIngresoMes=' + fechaIngresoMes;
	   	url += '&fechaIngresoAnio=' + fechaIngresoAnio;
	   	url += '&cuil=' + trim(cuil);
	   	url += '&remuneracion_declarada=' + trim(remuneracion_declarada);
	   	url += '&cant_afi=' + trim(cant_afiliados);
	   	url += '&tipo_boleta=' + tipo_boleta;
	   	url += '&camara=' + camara;
	   	url += '&calculado=' + trim(calculado);
	   	url += '&pagado=' + trim(pagado);
	   	url += '&periodo='+ trim(periodo);
	   	url += '&apellido=' + encodeURI(apellido);
	   	url += '&nombre=' + encodeURI(nombre);
	   	url += '&subtotal=' + trim(subtotal);
	   	url += '&interes=' + trim(interes);
	   	url += '&entidad=' + trim(entidad);	   		   	
	   	url += '&cant_afiliados='+ trim(cant_afiliados);
	   	url += '&interesApago='+ trim(interesAPago);
	    url += '&rnd=' + Math.floor(Math.random()*100);
   
		jQuery(popup).load(url);  
		
	}
	
	function <portlet:namespace />eliminarPeri() {
		var cuil_titular = jQuery('#<portlet:namespace />cuil_borrar').val();
		var periodo = jQuery('#<portlet:namespace />periodo').val();		
		var tipo_aporte=jQuery('#<portlet:namespace/>tipoBoletaBorrar').val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta_manual_no_os';		
		url += '&esEdicion=esEdicion';
		url += '&periodo=' + escape(periodo);
		url += '&cuil_titular=' +escape(cuil_titular);
		url += '&tipo_aporte=' +tipo_aporte;
		url += '&id=-2';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);    
	}
	
	 function <portlet:namespace />volver() {		
		var entidad=document.getElementById("<portlet:namespace/>entidad").value;

		var entidad=document.getElementById("<portlet:namespace/>entidad").value;
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos_no_os';
	 	url += '&esEdicion=esEdicion';
	 	url += '&entidad='+entidad;
	    url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url);    
	 }
	 
	 function sugerirSubtotal(){		 
		 var calculado = parseFloat(jQuery('#<portlet:namespace />calculado_manual').val());
		 
		 var interes = parseFloat(jQuery('#<portlet:namespace />interes_manual').val());		 
		 var pagado = parseFloat(jQuery('#<portlet:namespace />pagado_manual').val());
		 var calculopago= jQuery("input[name$='calculopago']:checked").val();
		 
		 if(calculopago=="calculo"){		 
			 var subtotal=0;
			 if(!isNaN(calculado)){
			 	subtotal=calculado;
			 }		 	
			 
			 if(!isNaN(pagado)){
			 	subtotal=subtotal-pagado;
			 }
			 if(!isNaN(interes)){
			 	subtotal=subtotal+interes;
			 }
			 
			 jQuery('#<portlet:namespace />subtotal_manual').val((parseFloat(subtotal)*100/100).toFixed(2));
		 }else{			 
			 jQuery('#<portlet:namespace />subtotal_manual').val(pagado);

		 }
		 
	 }
	 
	 function borraPeriodoManual(periodo, id, cuil_titular, tipo_aporte){
		 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_periodo_acta_manual_no_os';		
	    	 url += '&esEdicion=esEdicion';
	    	 url += '&periodo=' + escape(periodo);
	    	 url += '&cuil_titular=' +escape(cuil_titular);
	    	 url += '&tipo_aporte=' +tipo_aporte;
	    	 url += '&id=' + id;
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url);    
	 }
	 
	 <%
	 Acta acta=(Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION); 
	 if (acta != null && portlet_name.equals("uoma")) {%>
				document.getElementById("<portlet:namespace />subtotal_sindicato").value = "<%=acta.getCapitalSindicato().toString()%>";
				document.getElementById("<portlet:namespace />inte_sindicato").value = "<%=acta.getInteresSindicato().toString()%>";		
				document.getElementById("<portlet:namespace />subtotal_usufructo").value = "<%=acta.getCapitalUsufructo().toString()%>";
				document.getElementById("<portlet:namespace />inte_usufructo").value = "<%=acta.getInteresUsufructo().toString()%>";        				
        		document.getElementById("<portlet:namespace />subtotal_art46").value = "<%=acta.getCapitalArt46().toString()%>";
				document.getElementById("<portlet:namespace />inte_art46").value = "<%=acta.getInteresArt46().toString()%>";        
        		document.getElementById("<portlet:namespace />subtotal_solidario").value = "<%=acta.getCapitalSolidario().toString()%>";
				document.getElementById("<portlet:namespace />inte_solidario").value = "<%=acta.getInteresSolidario().toString()%>";		
				sumarTodoUOMA();
	<% } else if (acta != null && portlet_name.equals("farmacia")){ %>
				document.getElementById("<portlet:namespace />subtotal").value = "<%=acta.getCapitalAmtima().toString()%>";
				document.getElementById("<portlet:namespace />inte").value = "<%=acta.getInteresAmtima().toString()%>";
	<% }%>
	function calcularAporte(){				
		jQuery("#span_calculado").show();				
		var periodo = jQuery('#<portlet:namespace />periodo').val();
		var tipo_boleta=jQuery('#<portlet:namespace/>tipoBoleta').val();				
		var camara=jQuery('#<portlet:namespace/>camara').val();
		var cant_afiliados=jQuery('#<portlet:namespace/>cant_afiliados').val();
		var remuneracion=jQuery('#<portlet:namespace />remuneracion_declarada_manual').val();
		var cant_afiliados=jQuery('#<portlet:namespace />cant_afiliados').val();		
		var fechaInicioDia=jQuery('#<portlet:namespace />fechaPagoDia').val();
		var fechaInicioMes=jQuery('#<portlet:namespace />fechaPagoMes').val();		
		var fechaInicioAnio=jQuery('#<portlet:namespace />fechaPagoAnio').val();
			

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/calcular_aportes';
		url += '&tipo_boleta=' + escape(tipo_boleta);
		url += '&remuneracion=' + escape(remuneracion);
		url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url += '&periodo=' + periodo;
		url += '&camara=' +camara;		
		url += '&cant_afi=' +cant_afiliados;
		url += '&obligDia=' +fechaInicioDia;
		url += '&obligMes=' +fechaInicioMes;
		url += '&obligAnio=' +fechaInicioAnio;
		url += '&cant_afiliados=' +cant_afiliados;		
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				jQuery("#span_calculado").hide();										
				var obj = jQuery.parseJSON(data);				
				var value = obj.calculado;				
				jQuery("#<portlet:namespace />calculado_manual").val(value);        
				sugerirSubtotal();                                                                                                                                                                                        
			}
		});	
	}
	

	function calcularInteres(){			
		
		jQuery("#span_interes").show();		
		//var calculopago= jQuery('#<portlet:namespace/>calculopago').is('checked');
		var calculopago= jQuery("input[name$='calculopago']:checked").val();
		if(calculopago=="calculo"){
			var periodo = jQuery('#<portlet:namespace />periodo').val();		
			var fechaInicioDia=jQuery('#<portlet:namespace />fechaPagoDia').val();
			var fechaInicioMes=jQuery('#<portlet:namespace />fechaPagoMes').val();		
			var fechaInicioAnio=jQuery('#<portlet:namespace />fechaPagoAnio').val();
			var fechaPagoDia=jQuery('#<portlet:namespace />fechaPagoDia1').val();
			var fechaPagoMes=jQuery('#<portlet:namespace />fechaPagoMes1').val();
			var fechaPagoAnio=jQuery('#<portlet:namespace />fechaPagoAnio1').val();
			var camara=jQuery('#<portlet:namespace/>camara').val();
			var remuneracion=jQuery('#<portlet:namespace />remuneracion_declarada_manual').val();
			var cant_afiliados=jQuery('#<portlet:namespace/>cant_afiliados').val();
			var pagado=jQuery('#<portlet:namespace />pagado_manual').val();		
			var calculado=jQuery('#<portlet:namespace />calculado_manual').val();
			var tipo_boleta=jQuery('#<portlet:namespace/>tipoBoleta').val();
			var cuit=document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value;				
			
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/calcular_interes';		
				url += '&pagado=' + escape(pagado);
				url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
				url += '&periodo=' + periodo;
				url += '&calculado=' +calculado;
				url += '&obligDia=' +fechaInicioDia;
				url += '&obligMes=' +fechaInicioMes;
				url += '&obligAnio=' +fechaInicioAnio;
				url += '&pagoDia=' +fechaPagoDia;
				url += '&pagoMes=' +fechaPagoMes;
				url += '&pagoAnio=' +fechaPagoAnio;
				url += '&remuneracion=' + escape(remuneracion);
				url += '&cant_afiliados=' + cant_afiliados;
				url += '&cuit=' +cuit;
				url += '&tipo_boleta=' +tipo_boleta;
				url += '&camara=' +camara;
				url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery.ajax({   
					url: url,
					success: function(data){										
						var obj = jQuery.parseJSON(data);				
						var interes = obj.interes;				
						var capital = obj.capital;
						var iap=obj.interesApago
						jQuery("#<portlet:namespace />interes_manual").val(Math.round(parseFloat(interes)*100)/100 );
						jQuery("#<portlet:namespace />calculado_manual").val(parseFloat(capital));					
						jQuery("#<portlet:namespace />interesApago").val(parseFloat(iap));
						sugerirSubtotal();
						                                                                                                                                                                                                                                                            
					}
				});	
		
		}else{
			sugerirSubtotal();
		}
	}
	
	function <portlet:namespace />volverGuardar() {		
		var entidad=document.getElementById("<portlet:namespace/>entidad").value;
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos_no_os';
	 	url += '&esEdicion=esEdicion';
	 	url += '&entidad='+entidad;
	    url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url, function(){parent.guardarActaVolver();});    
	 }
		
</script>
	 
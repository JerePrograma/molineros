<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		PortletURL portletURL = renderResponse.createRenderURL();
		
		//String dinámico que define los parámetros de búsqueda y de resultados de la página, segun si es 'pre' de reintegro prestacional
		//u 'odo' de reintegro odontología, si es nulo se setea por default a 'pre' 
		String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", "pre");
		
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		
		if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);
		}
		boolean showOpcionesAuditor = !PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) && PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO); 
			
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);

		//verificar los calendars
 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		
 		List<String> alta_usrs = (ArrayList<String>) portletSession
		.getAttribute(WebKeysLiquidaciones.ALTA_USR_REINTEGROS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

		if (alta_usrs == null) {
			alta_usrs = TraeListasServiceUtil.getUsuariosAltaReintegros();
			portletSession.setAttribute(
					WebKeysLiquidaciones.ALTA_USR_REINTEGROS_EN_SESSION,
					alta_usrs,
					PortletSession.APPLICATION_SCOPE);	
		}
%>
		<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
		<input type="hidden" id="<portlet:namespace />tipo_reintegro" name="<portlet:namespace />tipo_reintegro" value="<%= tipo_reintegro %>"/>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-reintegros" /></legend>
				<table c+lass="lfr-table">					
					<tr>	
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="usuario" />:</label></td>
						<td>							
							<select name="<portlet:namespace/>alta_usr" id="<portlet:namespace/>alta_usr" >
								<option value=""></option>
							<%
								for (String usuario : alta_usrs) {
							%>
									<option value="<%= usuario %>"  <%= showOpcionesAuditor && usuario.equals(WebKeysLiquidaciones.USUARIO_CARGA_ODONTOLOGIA) ? "selected" : "" %>><%=usuario%></option>
							<%
								}
							%>
							</select>
						</td>					
					</tr>				
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
					<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
						<td><label>Estado:</label></td>
						<td>
							<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
								<option value="0">Todos</option>
								<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUTORIZADO%>" <%= showOpcionesAuditor ? "selected" : "" %>>Autorizado</option>
								<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_PENDIENTE%>">Pendiente</option>
								<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO%>">Auditado</option>
								<option value="<%=WebKeysLiquidaciones.REINTEGRO_ESTADO_RECHAZADO%>">Rechazado</option>					
							</select>
						</td>
					</c:if>
					<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
						<td><label><liferay-ui:message key="cod-prest" />:</label></td>													
						<td><input id="<portlet:namespace />codPrest" name="<portlet:namespace />codPrest" size="6" maxlength="6" type="text" value="" /></td>
					</c:if>							
						<td><label><liferay-ui:message key="numero-texto" />:</label></td>
						<td colspan="1"><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="8" maxlength="8" type="text" value="" /></td>							
						<td colspan="2"><label><liferay-ui:message key="seccional-reintegro" />:</label></td>					
						<td rowspan="2" style="vertical-align:top" ><jsp:include page='/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp' /></td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
					<td colspan="1"><label><liferay-ui:message key="prestacional" />:</label></td>
						<td><input type="checkbox" id="<portlet:namespace />prestacional" name="<portlet:namespace />prestacional" value="1" /></td>
						<td colspan="1"><label><liferay-ui:message key="ortopedia" />:</label></td>
						<td><input type="checkbox" id="<portlet:namespace />ortopedia" name="<portlet:namespace />ortopedia" value="1" /></td>
						<td colspan="1"><label><liferay-ui:message key="protesis" />:</label></td>
						<td><input type="checkbox" id="<portlet:namespace />protesis" name="<portlet:namespace />protesis" value="1" /></td>							
						<td><label><liferay-ui:message key="estado" />:</label></td>
						<td colspan="1">
							<select name="<portlet:namespace />pagos" id="<portlet:namespace />pagos">								
								<option value="2">Pagados</option>
								<option value="0">No Pagados</option>
								<option value="1">Todos</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">
						<fieldset class="block-labels"><legend><liferay-ui:message
							key="datos-afiliado" /></legend>
						<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
						<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
						</liferay-util:include>
						</fieldset>
						</td>
					</tr>					
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<c:if test="<%= tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) %>">
						<!--  algún campo que falte de ortodoncias/ortopedias en el futuro, por ahora no hay -->
					</c:if>					
					<tr>						
						<td coslpan="1">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>		
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">					
		</fieldset>
	</form>		
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var periodoDesdeMesAnio='';
		var periodoHastaMesAnio='';
		var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();		
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var codPrestaci=jQuery('#<portlet:namespace />codPrest').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional_r').val();	
		var numero=jQuery('#<portlet:namespace />numero').val();
		var estado=jQuery('#<portlet:namespace />estado').val();		
		var pagos = jQuery('#<portlet:namespace />pagos').val();

		var boolpresta=jQuery('#<portlet:namespace />prestacional').is(':checked');
		var presta = "0";
		if (boolpresta == true) {
			presta = "1";
		}

		var boolortop=jQuery('#<portlet:namespace />ortopedia').is(':checked');
		var ortop = "0";
		if (boolortop == true) {
			ortop = "1";
		}

		var boolprot=jQuery('#<portlet:namespace />protesis').is(':checked');
		var protesis = "0";
		if (boolprot == true) {
			protesis = "1";
		}

		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}
				
		window.location.href ='/xlsservlet/?reporte=REPORTE_REINTEGROS'+'&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&alta_usr='+alta_usr+'&codPrest='+codPrestaci+
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&id_seccional_r='+seccional+'&numero='+numero+'&pagos='+pagos+'&tipo_reintegro='+'<%=tipo_reintegro%>'+'&estado='+estado+'&presta='+presta+'&ortop='+ortop+'&protesis='+protesis;
	});

	function <portlet:namespace />imprimirHC(){
		document.getElementById("<portlet:namespace />periodoMesAnio").disabled = false;
		var periodo = document.getElementById("<portlet:namespace />periodoMesAnio").value;
		document.getElementById("<portlet:namespace />periodoMesAnio").disabled = true;		
	}

	
	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeDia').val("");
		jQuery('#<portlet:namespace />fechaDesdeMes').val("");		
		jQuery('#<portlet:namespace />fechaDesdeAnio').val("");
		jQuery('#<portlet:namespace />fechaHastaDia').val("");
		jQuery('#<portlet:namespace />fechaHastaMes').val("");
		jQuery('#<portlet:namespace />fechaHastaAnio').val("");			
	}

	<portlet:namespace />resetValid();

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional_r").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada_r").val("1")
		}
	}
	
	<portlet:namespace />initDateFields();

	function emitirReporte(lista){
		window.location.href ="/xlsservlet/?reporte=OP_REINTEGRO&idLista=" + lista ;
	}

	<% if (request.getAttribute("listaId")!= null){ %>
		emitirReporte("<%=request.getAttribute("listaId")%>");
	<%}%>

	function generarReporte(reporte){		
		window.location.href ="/xlsservlet/?reporte=REP_REINTEGRO&idReporte=" + reporte;
	}

	<% if (request.getAttribute("reporteId")!= null) {%>			
			generarReporte('<%=request.getAttribute("reporteId")%>');		
	<%}%>	
</script>
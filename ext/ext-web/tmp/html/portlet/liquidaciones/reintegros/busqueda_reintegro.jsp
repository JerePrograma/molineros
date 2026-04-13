<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
		//String dinámico que define los parámetros de búsqueda y de resultados de la página, segun si es 'pre' de reintegro prestacional
		//u 'odo' de reintegro odontología, si es nulo se setea por default a 'pre' 
		String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", "ort");
		
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
		<liferay-ui:error key="error-lista-reintegros-cuenta" message="error-lista-reintegros-cuenta-msg" />
		<input type="hidden" id="<portlet:namespace />tipo_reintegro" name="<portlet:namespace />tipo_reintegro" value="<%= tipo_reintegro %>"/>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-reintegros" /></legend>
				<table class="lfr-table">					
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
						<td><label>Cod. Prestación:</label></td>													
						<td><input id="<portlet:namespace />codPrest" name="<portlet:namespace />codPrest" size="6" maxlength="6" type="text" value="" /></td>
					</c:if>							
						<td><label><liferay-ui:message key="numero-texto" />:</label></td>
						<td colspan="1"><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="8" maxlength="8" type="text" value="" /></td>							
						<td colspan="2"><label><liferay-ui:message key="seccional-reintegro" />:</label></td>					
						<td rowspan="2" style="vertical-align:top" ><jsp:include page='/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp' /></td>
						<td colspan="1"><label><liferay-ui:message key="incluir-pagos" />:</label></td>
						<td><input type="checkbox" id="<portlet:namespace />pagos" name="<portlet:namespace />pagos" value="1" /></td>						 					
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">
						<fieldset class="block-labels"><legend><liferay-ui:message
							key="datos-afiliado" /></legend>  
						<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado_filtro_prevencion.jsp'>
							<liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
							<!-- liferay-util:param value="<%= String.valueOf(true) %>" name="discapacidad" /-->
							<liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />
							<liferay-util:param name="cuil" value='' />
							<liferay-util:param name="inte" value='' />
							<liferay-util:param value="" name="origen" />
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
						<td colspan="1">
							<c:if test="<%=showABMButtons && !tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
								<input type="button" value="<liferay-ui:message key="alta-reintegro" />" onClick="<portlet:namespace />altaReintegro();" />
							</c:if>
							<c:if test="<%=showABMButtons && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
								<input type="button" value="Alta tratamiento" onClick="<portlet:namespace />altaReintegro();" />
							</c:if>							
						</td>
						<c:if test="<%=!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)%>">
						<td colspan="5">						
							<input id="<portlet:namespace />buscar_detalle" value="Buscar Detalle" title="Buscar Detalle" type="button"/>
						</td>
						</c:if>
					</tr>
					<tr>
						<td colspan="12">
							&nbsp;(<liferay-ui:message key="refine-busqueda-200" />)
						</td>
					</tr>										
				</table>	      	  
		</fieldset>	
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
			<div align="center" id="<portlet:namespace />busquedaReintegroDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').show();
	var url = "<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_reintegros_sesion&tipo_reintegro=<%=tipo_reintegro%>";
	jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url);
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
		var boolp=jQuery('#<portlet:namespace />pagos').is(':checked');
		var pagos = "0";
		if (boolp == true) {
			pagos = "1";
		}		
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_reintegros&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&alta_usr='+alta_usr+'&codPrest='+codPrestaci+
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&id_seccional_r='+seccional+'&numero='+numero+'&pagos='+pagos+'&tipo_reintegro='+'<%=tipo_reintegro%>'+'&estado='+estado;		
		
        jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	});

	jQuery('#<portlet:namespace />buscar_detalle').click(function(){
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
		var boolp=jQuery('#<portlet:namespace />pagos').is(':checked');
		var pagos = "0";
		if (boolp == true) {
			pagos = "1";
		}
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_reintegros_detalle&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&alta_usr='+alta_usr+'&codPrest='+codPrestaci+
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&id_seccional_r='+seccional+'&numero='+numero+'&pagos='+pagos+'&tipo_reintegro='+'<%=tipo_reintegro%>'+'&estado='+estado;		
		
        jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	});
	
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
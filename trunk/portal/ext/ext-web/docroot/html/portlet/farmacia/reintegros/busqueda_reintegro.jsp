<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%
		String portlet_name = ParamUtil.getString(request, "portlet_name");
	
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_LIQ_1_")){
			portlet_name = "liquidaciones";
		} 
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 				
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA);
		
		boolean showOpcionesAuditor = !PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA) && PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA); 
			
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ENTIDAD_UOMA);

		//verificar los calendars
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(new Date());
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
 		periodoHasta.setTime(new Date());
 		
 		List<String> alta_usrs = (ArrayList<String>) portletSession
		.getAttribute(WebKeysFarmacia.ALTA_USR_REINTEGROS_FARMACIA_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

		if (alta_usrs == null) {
			alta_usrs = TraeListasServiceUtil.getUsuariosAltaReintegrosFarmacia();
			portletSession.setAttribute(
					WebKeysFarmacia.ALTA_USR_REINTEGROS_FARMACIA_EN_SESSION,
					alta_usrs,
					PortletSession.APPLICATION_SCOPE);	
		}
%>
	   <liferay-ui:error key="error-lista-reintegros-cuenta" message="error-lista-reintegros-cuenta-msg" />
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-reintegros" /></legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="periodo-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoDesdeDia"
								dayNullable="<%= true %>"
								dayValue=""
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"							
								yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 5 %>"
								yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoHastaDia"
								dayNullable="<%= true %>" 
								dayValue=""							
								monthAndYearParam="periodoHastaMesAnio"
								monthValue="<%= periodoHasta.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= periodoHasta.get(Calendar.YEAR) - 5 %>"
								yearRangeEnd="<%= periodoHasta.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= periodoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="usuario" />:</label></td>
						<td>							
							<select name="<portlet:namespace/>alta_usr" id="<portlet:namespace/>alta_usr" >
								<option value=""></option>
							<%
								for (String usuario : alta_usrs) {
							%>
									<option value="<%= usuario %>"><%=usuario%></option>
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
						<td><label><liferay-ui:message key="numero-texto" />:</label></td>
						<td colspan="1"><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="8" maxlength="8" type="text" value="" onkeydown="allowOnlyDigits(event);"/></td>							
						<td colspan="1"><label><liferay-ui:message key="seccional-reintegro" />:</label></td>					
						<td rowspan="2" style="vertical-align:top" >
							<%if(portlet_name.equals("liquidaciones")){%>
								<jsp:include page='/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp' />
							<%}else{%>
								<jsp:include page='/html/portlet/farmacia/busqueda_seccional_reintegro.jsp' />
							<%}%>
						</td>
						<td><label><liferay-ui:message key="estado" />:</label></td>
						<td colspan="1">
						<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
							<option value="0"></option>
							<option value="1">No Pagados</option>
							<option value="2">Pagados</option>
						</select>
						</td>						 					
					</tr>
		
		<tr>
		<td colspan="12">&nbsp;</td>
		</tr>
					
		<tr>
		<td colspan="4">
		<fieldset class="block-labels">
			<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
			<%if(portlet_name.equals("liquidaciones")){%>
				<liferay-util:param name="search_url"	value="/liquidaciones/buscar_medicamento" />
			<%}else{%>
				<liferay-util:param name="search_url"	value="/farmacia/buscar_medicamento" />
			<%}%>
			<liferay-util:param name="troquel"
				value='' />
			<liferay-util:param name="nombre_medicamento"
				value='' />
			<liferay-util:param name="id_medicamento"
				value='' />
			<liferay-util:param name="esEditable"
				value='true' />
			<liferay-util:param name="popup"
				value='true' />
		</liferay-util:include></fieldset>
		</td>
		<td>
			&nbsp;&nbsp;&nbsp;Receta:
		</td>		
		<td>
			<input id="<portlet:namespace />receta" name="<portlet:namespace />receta" size="6" maxlength="8" type="text" value="" 
			onkeydown="allowOnlyDigits(event);"/>
		</td>
		</tr>					
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="12">
						<fieldset class="block-labels"><legend><liferay-ui:message
							key="datos-afiliado" /></legend>
						<%if(portlet_name.equals("liquidaciones")){%>
							<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
							</liferay-util:include>
						<%}else{%>
							<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
							</liferay-util:include>
						<%}%>
							
						</fieldset>
						</td>
					</tr>					
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>						
						<td coslpan="1">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td colspan="6">
								<input type="button" value="<liferay-ui:message key="alta-reintegro" />" onClick="<portlet:namespace />altaReintegroFarmacia();" />
						</td>						
					</tr>
					<tr>
						<td colspan="12">
							&nbsp;(<liferay-ui:message key="El resultado de la búsqueda se acotará a 1000 registros" />)
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
	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){	

		var fechaDesdeDia='';
		var fechaDesdeMes='';		
		var fechaDesdeAnio='';
		var fechaHastaDia='';
		var fechaHastaMes='';
		var fechaHastaAnio='';
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();
		var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var codPrestaci=''; //esto supongo que va a cambiarse por medicamento, ver con usuario
		var seccional=jQuery('#<portlet:namespace />id_seccional_r').val();	
		var numero=jQuery('#<portlet:namespace />numero').val();
		var estado=jQuery('#<portlet:namespace />estado').val();
		var id_medicamento=jQuery('#<portlet:namespace />id_medicamento').val();
		var receta=jQuery('#<portlet:namespace />receta').val();
		
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_reintegros_farmacia&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&alta_usr='+alta_usr+'&codPrest='+codPrestaci+
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&id_seccional_r='+seccional+'&numero='+numero+'&estado='+estado+'&id_medicamento='+id_medicamento+'&receta='+receta;		
        jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	});
	
	<portlet:namespace />resetValid();

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional_r").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada_r").val("1")
		}
	}

	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
		jQuery('#<portlet:namespace />periodoDesdeMesAnio').val("");
	}

	<portlet:namespace />hideDayFieldOfPeriodFields ();		

	function emitirReporte(lista){
		window.location.href ="/xlsservlet/?reporte=OP_REINTEGRO_FARMACIA&idLista=" + lista ;
	}

	<% if (request.getAttribute("listaId")!= null){ %>
		emitirReporte("<%=request.getAttribute("listaId")%>");
	<%}%>

	function generarReporte(reporte){		
		window.location.href ="/xlsservlet/?reporte=REP_REINTEGRO_FARMACIA&idReporte=" + reporte;
	}

	<% if (request.getAttribute("reporteId")!= null) {%>			
			generarReporte('<%=request.getAttribute("reporteId")%>');		
	<%}%>	
</script>
<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

		List<TarjetaAcceso> usuarios = (ArrayList<TarjetaAcceso>) portletSession
			.getAttribute(WebKeysGlobal.LISTA_TARJETAS_ACCESO,
				PortletSession.APPLICATION_SCOPE);

		if (usuarios == null) {
			usuarios = TraeListasServiceUtil.getTarjetasAccesoVigentes();
			portletSession.setAttribute(
					WebKeysGlobal.LISTA_TARJETAS_ACCESO, usuarios,
					PortletSession.APPLICATION_SCOPE);
		}

		ArrayList<String> semanas = TraeListasServiceUtil.getListaSemanasHaceUnAnio();		

		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_CONSULTA_RRHH);
		
		//verificar los calendars
		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(new Date());
%>		
		<fieldset class="block-labels">
				<legend>Información de personas por periodo</legend>
				<table class="lfr-table">
				<tr>
					<td>
						<label>Ver detalle:</label>
					</td>				
					<td>
					<input type="checkbox" name="<portlet:namespace />verDetalle" id="<portlet:namespace />verDetalle"
						value="verDetalle" onclick="javascript:;"/>
					</td>
					<td>
						<label>Ver por semana:</label>
					</td>
					<td>
					<input type="checkbox" name="<portlet:namespace />periodicidad" id="<portlet:namespace />periodicidad"
						value="mes" onclick="javascript:cambiarDivPeriodo();"/>
					</td>
					<td><label>Periodo:</label></td>
					<td>
					<div align="center" id="<portlet:namespace />periodos_mes">
						<liferay-ui:input-date
							dayParam="periodoDesdeDia"
							dayNullable="<%= false %>"
							dayValue=""
							monthAndYearParam="periodoDesdeMesAnio"
							monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
							monthAndYearNullable="<%= false %>"
							yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"							
							yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 1 %>"
							yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />						
					</div>
					<div align="center" id="<portlet:namespace />periodos_semana">
						<select name="<portlet:namespace/>semana" id="<portlet:namespace/>semana" >
							<%
								for (String semana : semanas) {
							%>
									<option <%= (semanas.get(semanas.size() - 1)).equals(semana) ? "selected" : "" %> value="<%= semana.substring(0, semana.indexOf("-")) %>"><%=semana%></option>
							<%
								}
							%>
						</select>	
					</div>								
					</td>
					
					<td>
						<label>Persona:</label>
					</td>											
					<td>						
						<select name="<portlet:namespace/>persona" id="<portlet:namespace/>persona" >
							<option value="">Todos</option>	
							<% 
								for (TarjetaAcceso usuario : usuarios) {
									
							%>																	
									<option value="<%= usuario.getId_tarjeta_acceso() %>"><%=usuario.getApellido() + ", " + usuario.getNombre() %></option>
							<%
								}
							%>
						</select>														
					</td>		
					
				</tr>
					
					<tr>
						<td colspan="10">
							&nbsp;
						</td>
					</tr>
				<tr>					
						<td colspan="1">
						<% if (showABMButtons) { %>			
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						<%} 
						else {
						%> &nbsp; <%} %>  													
						</td>
						<td>&nbsp;</td>
						<td colspan="5">
						<% if (showABMButtons) { %>
							<input id="<portlet:namespace />exportar" value="<liferay-ui:message key="reporte-excel"/>" title="<liferay-ui:message key="reporte-excel" />" type="button"/>
						<%} 
						else {
						%> &nbsp; <%} %>
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
			<div align="center" id="<portlet:namespace />busquedaLecturasAcessoDiv">
			</div>
		</fieldset>
			
<script type="text/javascript">
 
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />periodos_semana').hide();
	jQuery("#<portlet:namespace />periodoDesdeDia").hide();	
			
	jQuery('#<portlet:namespace />buscar').click(function(){
				
	var checkito1 = jQuery('#<portlet:namespace />periodicidad');		
	var checkito2 = jQuery('#<portlet:namespace />verDetalle');
	
	var semanaSeleccionado = checkito1.is(':checked');
	var detalleSeleccionado = checkito2.is(':checked');
	
	var valorPeriodicidad = 'mes';
	var valorDetalle = "false";

	if (semanaSeleccionado == true) {
		valorPeriodicidad = 'semana';
	}

	if (detalleSeleccionado == true){
		valorDetalle = "true";					
	}
		
	var persona = jQuery("#<portlet:namespace />persona").val();
	var periodoSemana = '';
	var periodoDesdeMesAnio = '';
	
	if (valorPeriodicidad == 'semana' ){
		periodoSemana = jQuery("#<portlet:namespace />semana").val();
		periodoDesdeMesAnio = '';			
	} else if (valorPeriodicidad == 'mes' ){
		periodoSemana = '';
		periodoDesdeMesAnio = jQuery("#<portlet:namespace />periodoDesdeMesAnio").val();
	}
	//var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>'+
	'&struts_action=/rrhh/buscar_informacion_personas&periodicidad='+valorPeriodicidad+'&periodoDesdeMesAnio='+periodoDesdeMesAnio+		
	'&periodoSemana='+periodoSemana+'&persona='+persona+'&verDetalle='+valorDetalle;
				
       jQuery('#<portlet:namespace />busquedaLecturasAcessoDiv').load(url, function() {
       																jQuery('#<portlet:namespace />buscando').hide();
       															  });
	});	

	
	function cambiarDivPeriodo(){
		
		var checkito1 = jQuery('#<portlet:namespace />periodicidad');		
		
		var semanaSeleccionado = checkito1.is(':checked');
		var valorPeriodicidad = 'mes';

		if (semanaSeleccionado == true) {
			valorPeriodicidad = 'semana';
		}				
		if (valorPeriodicidad == 'semana' ){
			jQuery('#<portlet:namespace />periodos_mes').hide();
			jQuery('#<portlet:namespace />periodos_semana').show();
		}
		else if (valorPeriodicidad == 'mes' ){
			jQuery('#<portlet:namespace />periodos_semana').hide();
			jQuery('#<portlet:namespace />periodos_mes').show();			
		} else {
			alert ('selección: "' + valorPeriodicidad + '", no válida');
		}
	}

	jQuery('#<portlet:namespace />exportar').click(function(){

		var checkito1 = jQuery('#<portlet:namespace />periodicidad');		
		var checkito2 = jQuery('#<portlet:namespace />verDetalle');
		
		var semanaSeleccionado = checkito1.is(':checked');
		var detalleSeleccionado = checkito2.is(':checked');
		
		var valorPeriodicidad = 'mes';
		var valorDetalle = "false";

		if (semanaSeleccionado == true) {
			valorPeriodicidad = 'semana';
		}

		if (detalleSeleccionado == true){
			valorDetalle = "true";					
		}
		
		var persona = jQuery("#<portlet:namespace />persona").val();
		var periodoSemana = '';
		var periodoDesdeMesAnio = '';
		
		if (valorPeriodicidad == 'semana' ){
			periodoSemana = jQuery("#<portlet:namespace />semana").val();
			periodoDesdeMesAnio = '';
		} else if (valorPeriodicidad == 'mes' ){
			periodoSemana = '';
			periodoDesdeMesAnio = jQuery("#<portlet:namespace />periodoDesdeMesAnio").val();
		}
		//var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();										 
		window.location.href ='/xlsservlet/?reporte=<%="REPORTE_INFORMACION_PERSONAS"%>&periodicidad='+valorPeriodicidad+'&periodoDesdeMesAnio='+periodoDesdeMesAnio+		
		'&periodoSemana='+periodoSemana+'&persona='+persona+'&verDetalle='+valorDetalle;
		         
	});
	
</script>
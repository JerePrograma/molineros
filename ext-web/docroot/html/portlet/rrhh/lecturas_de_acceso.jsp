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
		
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_CONSULTA_RRHH);
					
		//verificar los calendars
 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());

%>		
		<fieldset class="block-labels">
				<legend>Búsqueda de Lecturas de Acceso por Fecha</legend>
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
						<td>&nbsp;</td>
						<td colspan="1">
						<% if (showABMButtons) { %>			
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						<%} 
						else {
						%> &nbsp; <%} %>  													
						</td>
						<td>&nbsp;</td>
						<td colspan="1">
						<% if (showABMButtons) { %>			
							<input id="<portlet:namespace />exportar" value="<liferay-ui:message key="reporte-excel"/>" title="<liferay-ui:message key="reporte-excel" />" type="button"/>
							&nbsp;
							<input id="<portlet:namespace />exportar_agrupado" value="Reporte Control Agrupado" title="Reporte Control Agrupado" type="button"/>
						<%} 
						else {
						%> &nbsp; <%} %>
						</td>
					</tr>
					<tr>
						<td colspan="10">
							&nbsp;
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
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		//var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>'+
		'&struts_action=/rrhh/buscar_lecturas_acceso&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
        jQuery('#<portlet:namespace />busquedaLecturasAcessoDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();
        															  }
        );
	});

	jQuery('#<portlet:namespace />exportar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		//var alta_usr = jQuery('#<portlet:namespace />alta_usr').val();
		
		window.location.href ='/xlsservlet/?reporte=<%="REPORTE_LECTURAS_ACCESO"%>&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;                
	});

	jQuery('#<portlet:namespace />exportar_agrupado').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		
		window.location.href ='/xlsservlet/?reporte=<%="REPORTE_CONTROL_ACCESO"%>&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;                
	});
	
</script>
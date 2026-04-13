<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server 				
		
		boolean showABMButtons =  PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		
		//verificar los calendars
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar();
 		periodoDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar();
 		periodoHasta.setTime(new Date());
 		
 		List<TipoDiscapacidad> tiposDisc=(ArrayList<TipoDiscapacidad>) portletSession.getAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,PortletSession.APPLICATION_SCOPE);
 		if (tiposDisc == null) {
 			tiposDisc=TraeListasServiceUtil.getTiposDiscapacidad();
 			portletSession.setAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,tiposDisc,PortletSession.APPLICATION_SCOPE);	
 		}
%>	

	<form  method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">		
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-tratamiento-discapacidad" /></legend>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
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
						<td><label><liferay-ui:message key="sur"/>:</label></td>
						<td><input type="checkbox" id="<portlet:namespace />sur" name="<portlet:namespace />sur" value="false"/></td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="codigo-prestacion"/>:</label></td>
						<td><input id="<portlet:namespace />codigo_prestacion"name="<portlet:namespace />codigo_prestacion" size="7" maxlength="7"type="text"value="" /></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
					  		<liferay-util:param name="cuit_entidad" value=''/>
					  		<liferay-util:param name="entidad" value=''/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="ciex"/>:</label></td>
						<td><input id="<portlet:namespace />ciex"name="<portlet:namespace />ciex" size="10" maxlength="10" type="text" value="" /></td>
						<td><label><liferay-ui:message key="tipo-discapacidad"/>:</label></td>
								<td><select name="<portlet:namespace/>tipo_discapacidad" id="<portlet:namespace/>tipo_discapacidad" > <!-- multiple="multiple" -->
										<option value=""></option>
										<%for (TipoDiscapacidad td : tiposDisc) { %>				
										<option value="<%=td.getId()%>"><%=td.getDescripcion()%></option>
										<%}%>
									</select>			 
								</td>
					<td colspan="1"><label><liferay-ui:message key="por-rango-etario"/>:</label></td>
						<td colspan="1"><input type="checkbox" id="<portlet:namespace />rango_etario" name="<portlet:namespace />rango-etario" value="false"/></td>							
					</tr>			
					<tr>						
						<td colspan="1">							
						<input id="<portlet:namespace />generate" value="<liferay-ui:message key="generate"/>" title="<liferay-ui:message key="generate" />" type="button"/></td>		
					</tr>
				</table>	      	  
		</fieldset>
	</form>
				
<script type="text/javascript">

	<portlet:namespace />hideDayFieldOfPeriodFields();
	
	jQuery('#<portlet:namespace />generate').click(function(){

		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();
		var ciex=jQuery('#<portlet:namespace />ciex').val();
		var codigo_prestacion=jQuery('#<portlet:namespace />codigo_prestacion').val();
		var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val(); 
		var sur=jQuery('#<portlet:namespace />sur').is(':checked');
		var rango_etario=jQuery('#<portlet:namespace />rango_etario').is(':checked');
		var tipo_discap = jQuery('#<portlet:namespace/>tipo_discapacidad').val();
		
		window.location.href ='/xlsservlet/?reporte=TRATAMIENTO_DISCAPACIDAD'+'&periodoDesdeMesAnio='+periodoDesdeMesAnio+
				'&periodoHastaMesAnio='+periodoHastaMesAnio+'&ciex='+ciex+'&codigo_prestacion='+codigo_prestacion+
  				'&cuit_entidad='+cuit_entidad+'&entidad='+entidad+'&sur='+sur+'&rango_etario='+rango_etario+'&tiposDiscSel='+tipo_discap;
		
	});
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}
	
</script>
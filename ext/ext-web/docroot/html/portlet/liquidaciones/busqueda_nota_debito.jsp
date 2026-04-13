<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde.setTime(new Date());

Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
	periodoHasta.setTime(new Date());

 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
%>
		<fieldset class="block-labels">
				<legend>Búsqueda Liquidación de Débitos a Terceros</legend>
				<table class="lfr-table">
					<tr>
						<td><label>Periodo Desde</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoDesdeDia"
								dayNullable="<%= true %>"
								dayValue=""
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"								
								monthAndYearNullable="<%= true %>"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) - 1 %>"							
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />							
						</td>					
						<td>&nbsp;</td>
						<td><label>Periodo Hasta</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoHastaDia"
								dayNullable="<%= true %>"
								dayValue=""
								monthAndYearParam="periodoHastaMesAnio"
								monthValue="<%= periodoHasta.get(Calendar.MONTH) %>"																
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoHasta.get(Calendar.YEAR) %>"							
								yearRangeStart="<%= periodoHasta.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= periodoHasta.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= periodoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />							
						</td>
						<td coslpan="1">
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="consultar"/>" title="<liferay-ui:message key="Consultar" />" type="button"/>							
						</td>
						
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">
							<c:if test="<%=showABMButtons %>">
								<input id="<portlet:namespace />generar" type="button" value="Generar Pendientes" title="Generar Pendientes"/>
							</c:if>
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
			<div align="center" id="<portlet:namespace />busquedaDebitosDiv">
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	<portlet:namespace />hideDayFieldOfPeriodFields();

	jQuery('#<portlet:namespace />buscar').click(function(){
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_debitos_periodo&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio;
		jQuery('#<portlet:namespace />buscando').show();
		jQuery('#<portlet:namespace />busquedaDebitosDiv').load(url, function() {
       																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );       
});

	jQuery('#<portlet:namespace />generar').click(function(){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_debitos_periodo&pendiente=1';
		jQuery('#<portlet:namespace />buscando').show();
		jQuery('#<portlet:namespace />busquedaDebitosDiv').load(url, function() {
       																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );       
});

	function buscarGenerar() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_debitos_periodo&pendiente=1';
		jQuery('#<portlet:namespace />buscando').show();
		jQuery('#<portlet:namespace />busquedaDebitosDiv').load(url, function() {
       																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	var popupAltaDT;
	function altaLiquidacionDT(periodo) {
		popupAltaDT = Liferay.Popup({title:"Liquidación Débitos a Terceros",modal:true,width:600,position:[220,40],onClose:function(){ buscarGenerar();
			}});	    	
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/generar_nota_debito_periodo&periodo='+periodo;
		jQuery(popupAltaDT).load(url);
	}

	function editarLiquidacionDT(id) {		
		popupAltaDT = Liferay.Popup({title:"Liquidación Débitos a Terceros",modal:true,width:600,position:[220,40]});
		var id_liquidacion = id;		
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/generar_nota_debito_periodo&id_liquidacion='+id_liquidacion;	    
		jQuery(popupAltaDT).load(url);
	}	
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}
</script>
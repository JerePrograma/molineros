<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.OpConChequesCanjeadosException" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<liferay-ui:error exception="<%= OpConChequesCanjeadosException.class %>" message="exception-anular-canje-op-con-cheques-canjeados" />

<portlet:defineObjects/>

<form action="" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />
<%			

		String portlet_name = ParamUtil.getString(request, "portlet_name");
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		
		boolean showABMButtons = true;
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(new Date());
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
 		periodoHasta.setTime(new Date());
 		
 		boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-canje-cheque-propio" /></legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarCanjes();"/>
							<% if (showABMButtons && !soloVer) { %>							
							<input id="<portlet:namespace />nuevoMov" value="<liferay-ui:message key="nuevo-canje"/>" title="<liferay-ui:message key="nuevo-movimiento" />" type="button" onClick="javascript:<portlet:namespace />nuevoCanje();"/>
							<%} %>
						</td>						
					</tr>	
					<tr>	
						<td><liferay-ui:message key="cheque-canjeado" />:</td>
						<td><input type="text" value="" name="<portlet:namespace />cheque_canjeado" id="<portlet:namespace />cheque_canjeado"/></td>
						<td><liferay-ui:message key="cheque-nuevo" />:</td>
						<td><input type="text" value="" name="<portlet:namespace />cheque_nuevo" id="<portlet:namespace />cheque_nuevo" /></td>
						<td><liferay-ui:message key="op-generada" />:</td>
						<td><input type="text" value="" name="<portlet:namespace />op_generada" id="<portlet:namespace />op_generada" /></td>
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
			<div align="center" id="<portlet:namespace />busquedaMovimientoDiv">						
			</div>
		</fieldset>
</form>
<form id="anulacionCanje" name="anulacionCanje" action="">
	<input type="hidden" value="" name="<portlet:namespace />canje_id" id="<portlet:namespace />canje_id"/>
</form>			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	function <portlet:namespace />buscarCanjes(){
		var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
		var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
		var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
		var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
		var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
		var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
		var cheque_canjeado=jQuery("#<portlet:namespace/>cheque_canjeado").val();
		var cheque_nuevo=jQuery("#<portlet:namespace/>cheque_nuevo").val();
		
		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_canje_cheques_propios';
		jQuery('#<portlet:namespace />buscando').show();		
		jQuery("#<portlet:namespace/>busquedaMovimientoDiv").load(url,{desde_dia:desde_dia, desde_mes:desde_mes, desde_anio:desde_anio, hasta_dia:hasta_dia,
			hasta_mes:hasta_mes, hasta_anio:hasta_anio, cheque_nuevo:cheque_nuevo, cheque_canjeado:cheque_canjeado  }, function(){jQuery('#<portlet:namespace />buscando').hide();});
		
	}


	function <portlet:namespace />nuevoCanje(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/canje_cheques_propios';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     

	function anularCanje(id){
		jQuery('#<portlet:namespace />canje_id').val(id);	
		var url2 = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/canje_cheques_propios_anular';
		document.anulacionCanje.method = 'post';
		submitForm(document.anulacionCanje, url2);
	}
	
</script>
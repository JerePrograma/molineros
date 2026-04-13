<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

		String portlet_name = "tesoreria";

		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 
 		
 		Date fIni= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_INICIAL");
 		if(fIni!=null) fechaDesde.setTime(fIni);
 		Date fFin= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_FINAL");
 		if(fFin!=null) fechaHasta.setTime(fFin);
        String cuit=(String)session.getAttribute("CONTROL_DECLARADOS_N1_CUIT");
%>	
		<fieldset class="block-labels">
				<legend>
				<%if(portlet_name.equals("tesoreria")){ %>
					<liferay-ui:message key="cuentas-corrientes-actas-conv-aportes-contrib" />
				<%}else{%>
					<liferay-ui:message key="cuentas-corrientes-actas-conv" />
				<%}%>
				</legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="acreedor" />:</label></td>
						<td colspan="6">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>
						  		<liferay-util:param name="cuit" value='<%=cuit%>'/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 30 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 30 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>							
							Solo con saldo&nbsp;<input type="checkbox" name="soloConSaldo" id="soloConSaldo" value="true"/>
						</td>
						<td>
							<%if(portlet_name.equals("tesoreria")){%>
								Incluir saldo de aportes y contribuciones&nbsp;<input type="checkbox" name="aportesContrib" id="aportesContrib" value="false"/>
							<%}else{%>
								<input type="hidden" name="aportesContrib" id="aportesContrib"/>
							<%}%>
						</td>
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
						</td>
					</tr>						
					<tr>
						<td colspan="7">&nbsp;</td>
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
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />buscarMovimientos(){
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var apo_contrib = document.getElementById("aportesContrib");
	var apo_contrib_string='false';
	if(apo_contrib.checked){
	    apo_contrib_string='true';
	}else{
		apo_contrib_string='false';
	}
	var url = '/xlsservlet/?reporte=CUENTAS_CORRIENTES_ACTAS_Y_CONV' 
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&fechaHastaAnio=' +hasta_anio
		+ '&cuit_entidad=' +cuit_entidad
		+ '&sucursal_entidad=' +sucursal_entidad
		+'&soloConSaldo=' + (soloConSaldo.checked ? soloConSaldo.value : 'false')
		+'&apoContrib=' + apo_contrib_string
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
		+ '&id_seccional=' +id_seccional;		
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}
function cambiaCuit(){
}
	
</script>
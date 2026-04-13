<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%		
	List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();
    List<Date> fechasLiquidacion = TraeListasServiceUtil.getFechasLiquidacionHistoricaTercerizadoras();

	//verificar los calendars
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde.add(Calendar.MONTH, -6);
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
	fechaHasta.setTime(new Date()); 
	Calendar current = CalendarFactoryUtil.getCalendar();
%>
 
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-correr-proceso-desregulados" />
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	
		<legend>
			<liferay-ui:message key="liquidar-desregulados" />
		</legend>
		
		<fieldset class="block-labels">
		<table class="lfr-table">	
			<tr>	
				<td>
					<liferay-ui:message key="tercerizadora-servicio" />
				</td>
				<td>
					<select name="<portlet:namespace/>tercerizadora" id="<portlet:namespace/>tercerizadora">
						<option value="0">Seleccione una tercerizadora</option>
						<%	for (TercerizadoraServicio terce : tercServList) { %>
								<option value="<%= terce.getId_tercerizadora()%>"><%=terce.getDescripcion()%></option>
						<%	} %>
					</select>
				</td>
					
				<td><label><liferay-ui:message key="fecha-liquidacion-desde" />:</label></td>
				<td>
					<liferay-ui:input-date
					dayParam="fechaDesdeDia"
					dayValue="1" 
					monthParam="fechaDesdeMes"
					monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"				
					yearParam="fechaDesdeAnio"
					yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
					yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)+1 %>"
					firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
				</td>
				<td><label><liferay-ui:message key="Hasta" />:</label></td>
				<td>
					<liferay-ui:input-date
					dayParam="fechaHastaDia"
					dayValue="<%= fechaHasta.get(Calendar.DATE) %>" 
					monthParam="fechaHastaMes"
					monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"				
					yearParam="fechaHastaAnio"
					yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
					yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
				</td>				
			<td><input type="button" value="<liferay-ui:message key="search" />" onClick="javascript: filtrar();" /></td>
			</tr>
		</table>
		</fieldset>
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
	</fieldset>	
		
 		<div id="<portlet:namespace />desregula_liquidados">
			<jsp:include page='/html/portlet/tesoreria/liquida_desregulados/desregula_liquidados.jsp' />  
		</div>	
		
		
		<div align="center" id="<portlet:namespace/>desregula_liquidados_bot">	
			<jsp:include page='/html/portlet/tesoreria/liquida_desregulados/div_liquida_desregulados.jsp' />						
		</div>	
	<br>		 	
	<fieldset class="block-labels" >
	    <legend>
			<liferay-ui:message key="reporte-derivacion" />
		</legend>
		<table class="lfr-table">	
			<tr>	
				<td>
					<liferay-ui:message key="fecha-liquidacion" />
				</td>
				<td>
					<select name="<portlet:namespace/>fechaLiquidacion" id="<portlet:namespace/>fechaLiquidacion">
						<option value="0">Seleccione una Liquidación</option>
						<%	for (Date fl : fechasLiquidacion) { %>
								<option value="<%= fl %>"><%=fl %></option>
						<%	} %>
					</select>
				</td>
				<td>
				   <input type="button" value="<liferay-ui:message key='buscar'/>" onClick="javascript:exportarExcelDerivados()" />
				</td>   
		   </tr>
		</table>
	</fieldset>	   					

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();	

		function exportarExcel(fechaliq, id_terc){		
			window.location.href ='/txtservlet/?reporte=REPORTE_DERIVA_DESREGULADOS&fechaLiq='+fechaliq+'&id_terc='+id_terc;						
		}
		
		function exportarTxt(fechaliq, id_terc){
			window.location.href ='/txtservlet/?reporte=REPORTE_AFILIADOS_SIN_APORTE&fechaLiq='+fechaliq+'&id_terc='+id_terc;						
		}
		
		function exportarComisionesTercerizadora(fechaliq, id_terc){
			window.location.href ='/txtservlet/?reporte=REPORTE_COMISIONES_TERCERIZADORAS&fechaLiq='+fechaliq+'&id_terc='+id_terc;						
		}
		
		function exportarExcelDerivados(){
			var fechaliq = jQuery('#<portlet:namespace />fechaLiquidacion').val();
			window.location.href ='/xlsservlet/?reporte=REPORTE_DERIVACIONES&fechaLiq='+fechaliq;						
		}
		
		function liquidarAportesPendientes(){	
			var cuil = jQuery('#<portlet:namespace />cuil').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/liquidar_desregulados';
			url += "&cuil="+cuil;
			
			var d = new Date();
			
			document.getElementById("boton_liquida_desreg").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + d.getDate()+ "/"+(d.getMonth()+1)+"/"+d.getFullYear() + " "+d.getHours()+":"+d.getMinutes();
			jQuery('#<portlet:namespace/>desregula_liquidados_bot').load(url, function(){
			});

	      
		}
		
		function cancelarProceso(procid){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/liquidar_desregulados&cancela='+procid;
			jQuery('#<portlet:namespace/>desregula_liquidados_bot').load(url, function(){});
		  
		}
		
		function filtrar(){
			var fechaDesdeDia = jQuery('#<portlet:namespace />fechaDesdeDia').val();
			var fechaDesdeMes = jQuery('#<portlet:namespace />fechaDesdeMes').val();
			var fechaDesdeAnio = jQuery('#<portlet:namespace />fechaDesdeAnio').val();
			var fechaHastaDia = jQuery('#<portlet:namespace />fechaHastaDia').val();
			var fechaHastaMes = jQuery('#<portlet:namespace />fechaHastaMes').val();
			var fechaHastaAnio = jQuery('#<portlet:namespace />fechaHastaAnio').val();
			var id_terc = jQuery('#<portlet:namespace/>tercerizadora').val();
 			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/filtrar_desregulados'+
 			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+'&fechaHastaDia='+fechaHastaDia+
 			'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+'&id_terc='+id_terc; 
			jQuery('#<portlet:namespace />buscando').show();		
 			jQuery("#<portlet:namespace />desregula_liquidados").load(url, function(){jQuery('#<portlet:namespace />buscando').hide();}); 
 		}
		

</script>

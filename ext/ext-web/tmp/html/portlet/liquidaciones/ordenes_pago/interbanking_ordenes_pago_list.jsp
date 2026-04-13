<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaFin = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		Integer ini = (Integer)request.getAttribute("ordenIniId");
		Integer fin = (Integer)request.getAttribute("ordenFinId");

 		boolean showInterbanking = PermissionUtil.userContainsRole(user, WebKeysGlobal.ROL_INTERBANKING);
 		
 		Calendar fecha = CalendarFactoryUtil.getCalendar();
 		fecha.setTime(new Date());
 		
 		
 		String portlet_name="";
		String entidad="";

		if (renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
			entidad="AMTIMA";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
			entidad="UOMA";
		}
 		
		if(renderResponse.getNamespace().equals("_TES_1_")){
			portlet_name = "tesoreria";
			entidad="OSPIM";
		}
%>
<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
		<fieldset class="block-labels">
				<legend>Transferencias Interbanking - Reenvio de Ordens de Pago</legend>
				<table >
					<tr>
						<td><label>Orden Pago Desde:</label></td>
						<td><input id="<portlet:namespace />numero_dde" name="<portlet:namespace />numero_dde" size="15" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td><label>Hasta:</label></td>
						<td><input id="<portlet:namespace />numero_hta" name="<portlet:namespace />numero_hta" size="15" maxlength="11" type="text" value="" /></td>						
					</tr>
				</table>
				<br>
				<table class="lfr-table" width="100%">	
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label>
						&nbsp;&nbsp;
						<liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaEmisionDesdeDia"
										monthParam="fechaEmisionDesdeMes"
										yearParam="fechaEmisionDesdeAnio"
										yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
							&nbsp;&nbsp;
						<label><liferay-ui:message key="fecha-hasta" />:</label>
						&nbsp;&nbsp;
						<span id="recep"><liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaEmisionHastaDia"
										monthParam="fechaEmisionHastaMes"
										yearParam="fechaEmisionHastaAnio"
										yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" /></span>
										
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
						
					</tr>
					<tr>						
						<td colspan="5" align="center">							
							<% if(showInterbanking) { %>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
							<%} %>&nbsp;&nbsp;
								<input type="button" value="Blanquear todas" onClick="<portlet:namespace />blanquearOPs();" />
						</td>						
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaOPsDiv">
			  <jsp:include page='/html/portlet/liquidaciones/ordenes_pago/interbanking_orden_pago_search_result.jsp' />  
			</div>
		</fieldset>
</form>			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		var numero_dde=jQuery('#<portlet:namespace />numero_dde').val();
		var numero_hta=jQuery('#<portlet:namespace />numero_hta').val();
		
		var fechaEmisionDesdeDia=jQuery('#<portlet:namespace />fechaEmisionDesdeDia').val();
		var fechaEmisionDesdeMes=jQuery('#<portlet:namespace />fechaEmisionDesdeMes').val();
		var fechaEmisionDesdeAnio=jQuery('#<portlet:namespace />fechaEmisionDesdeAnio').val();
		
		var fechaEmisionHastaDia=jQuery('#<portlet:namespace />fechaEmisionHastaDia').val();
		var fechaEmisionHastaMes=jQuery('#<portlet:namespace />fechaEmisionHastaMes').val();
		var fechaEmisionHastaAnio=jQuery('#<portlet:namespace />fechaEmisionHastaAnio').val();
		
		if(!<portlet:namespace />validarBusqueda(numero_dde,numero_hta, fechaEmisionDesdeMes, fechaEmisionHastaMes)){
			return false;
		}
		
		

		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/interbanking_ordenes_pago';
		url=url+'&numerodde='+numero_dde+'&numerohta='+numero_hta;
		url=url+'&desdeDia='+fechaEmisionDesdeDia+'&desdeMes='+fechaEmisionDesdeMes+'&desdeAnio='+fechaEmisionDesdeAnio;
		url=url+'&hastaDia='+fechaEmisionHastaDia+'&hastaMes='+fechaEmisionHastaMes+'&hastaAnio='+fechaEmisionHastaAnio;
		url=url+'&cmd=filter';
		
		jQuery('#<portlet:namespace />busquedaOPsDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	var popup;
	
	function <portlet:namespace />validarBusqueda(numero_dde,numero_hta,fechaEmisionDesdeMes, fechaEmisionHastaMes){
		if(trim(numero_dde.length)==0 && trim(numero_hta.length)==0 && trim(fechaEmisionDesdeMes.length)==0
				&& trim(fechaEmisionHastaMes.length)==0) {	
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />blanquearOPs() {
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/interbanking_ordenes_pago';
		url=url+'&cmd=deleteall';
		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
		
		
	}     
		

		
	
	
</script>

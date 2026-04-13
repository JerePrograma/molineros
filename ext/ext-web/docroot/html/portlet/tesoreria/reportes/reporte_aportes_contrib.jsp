<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,"Reportes_Tesoreria_Contaduria_Declaracion_Jurada");
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();	
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-aportes-contrib-empresas-actas-convenios" /></legend>
				<table class="lfr-table">
					<tr>						
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="1" 
							monthParam="fechaDesdeMes1"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaInicioAnio1"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td colspan="4">
							<liferay-ui:input-date
							dayParam="fechaHastaDia2"
							dayValue="1" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>					
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>						
						<td><label><liferay-ui:message key="fecha-acreditacion-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaAcreDesdeDia1"
							dayNullable="<%= true %>" dayValue=""							
							monthParam="fechaAcreDesdeMes1"							
							monthNullable="<%= true %>" monthValue="-1"				
							yearParam="fechaAcreDesdeAnio1"
							yearNullable="<%= true %>" yearValue=""
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						<td><label><liferay-ui:message key="fecha-acreditacion-hasta" />:</label></td>
						<td colspan="4">
							<liferay-ui:input-date
							dayParam="fechaAcreHastaDia2"
							dayNullable="<%= true %>" dayValue=""
							monthParam="fechaAcreHastaMes2"
							monthNullable="<%= true %>" monthValue="-1"			
							yearParam="fechaAcreHastaAnio2"
							yearNullable="<%= true %>" yearValue=""
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>					
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" /></td>
						<td colspan="3"><label><liferay-ui:message key="obtener-monotributistas" />:</label>&nbsp;<input type="checkbox" name="<portlet:namespace />monotributistas" id="<portlet:namespace />monotributistas" value="false"/></td>
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="ramo-desde" />:</label></td>
						<td>
							<input id="<portlet:namespace />id_ramo" name="<portlet:namespace />id_ramo" size="3" maxlength="3" type="text" value=""  /> <!-- onblur="javascript:soloUnPeriodo();" -->
						</td>
						<td><label><liferay-ui:message key="ramo-hasta" />:</label></td>
						<td colspan="4">
							<input id="<portlet:namespace />id_ramo_hasta" name="<portlet:namespace />id_ramo_hasta" size="3" maxlength="3" type="text" value=""  /> <!-- onblur="javascript:soloUnPeriodo();" -->
						</td>						
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>					
					<tr>
						<%if(showABMButtons){%>				
							<td><label><liferay-ui:message key="obtener-todas-las-empresas" />:</label>&nbsp;<input type="checkbox" name="<portlet:namespace />todas_empresas" id="<portlet:namespace />todas_empresas" value="true" onClick="javascript:soloUnPeriodo();"/></td>
						<%}else{%>
                            <input type="hidden" name="<portlet:namespace />todas_empresas" id=""<portlet:namespace />todas_empresas"/>
                        <%}%>
						<td>							
							<label><liferay-ui:message key="incluir-acta-convenio" />:</label>&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_acta_conv" id="<portlet:namespace />incluir_acta_conv" value="false"/>
						</td>														
						<td colspan="4" align="right">
							<label><liferay-ui:message key="formato-procesar" />:</label>&nbsp;<input type="checkbox" name="<portlet:namespace />formato_procesar" id="<portlet:namespace />formato_procesar" value="true" checked/>
						</td>						
						<td align="right">
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">							
			<div align="center" id="<portlet:namespace />buscando">		
				<liferay-util:include page="/html/portlet/tesoreria/reportes/progress_bar.jsp">
				</liferay-util:include>				
			</div>			
		</fieldset>
			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		
		var fechaAcreDesdeDia  = document.getElementById("<portlet:namespace />fechaAcreDesdeDia1");
		var fechaAcreDesdeMes= document.getElementById("<portlet:namespace />fechaAcreDesdeMes1");
		var fechaAcreDesdeAnio = document.getElementById("<portlet:namespace />fechaAcreDesdeAnio1");

		var fechaAcreHastaDia = document.getElementById("<portlet:namespace />fechaAcreHastaDia2");
		var fechaAcreHastaMes = document.getElementById("<portlet:namespace />fechaAcreHastaMes2");
		var fechaAcreHastaAnio = document.getElementById("<portlet:namespace />fechaAcreHastaAnio2");
		
		var todasEmpresas = document.getElementById("<portlet:namespace />todas_empresas");
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		
		var incluirActConv= document.getElementById("<portlet:namespace />incluir_acta_conv");

		if (trim(cuit).length != 11 && trim(cuil).length != 11){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		alert('fechaAcreDesdeDia'+fechaAcreDesdeDia.value);
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/reporte_aportes_contribuciones_empresa'
		+'&cuit='+cuit
		+'&cuil='+cuil
		+'&fechaDesdeDia='+fechaDesdeDia.value
		+'&fechaDesdeMes='+fechaDesdeMes.value
		+'&fechaDesdeAnio='+fechaDesdeAnio.value
		+'&fechaHastaDia='+fechaHastaDia.value
		+'&fechaHastaMes='+fechaHastaMes.value
		+'&fechaHastaAnio='+fechaHastaAnio.value
		+'&fechaAcreDesdeDia='+fechaAcreDesdeDia.value
		+'&fechaAcreDesdeMes='+fechaAcreDesdeMes.value
		+'&fechaAcreDesdeAnio='+fechaAcreDesdeAnio.value
		+'&fechaAcreHastaDia='+fechaAcreHastaDia.value
		+'&fechaAcreHastaMes='+fechaAcreHastaMes.value
		+'&fechaAcreHastaAnio='+fechaAcreHastaAnio.value
		+'&incluir_acta_conv=' + (incluirActConv.checked ? incluirActConv.value : 'false')
		+'&todas_empresas=' + (todasEmpresas.checked ? todasEmpresas.value : 'false');

		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});

	jQuery('#<portlet:namespace />reporte').click(function(){		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		
		var fechaAcreDesdeDia  = document.getElementById("<portlet:namespace />fechaAcreDesdeDia1");
		var fechaAcreDesdeMes= document.getElementById("<portlet:namespace />fechaAcreDesdeMes1");
		var fechaAcreDesdeAnio = document.getElementById("<portlet:namespace />fechaAcreDesdeAnio1");

		var fechaAcreHastaDia = document.getElementById("<portlet:namespace />fechaAcreHastaDia2");
		var fechaAcreHastaMes = document.getElementById("<portlet:namespace />fechaAcreHastaMes2");
		var fechaAcreHastaAnio = document.getElementById("<portlet:namespace />fechaAcreHastaAnio2");
		
		var id_ramo = document.getElementById("<portlet:namespace />id_ramo");
		var id_ramo_hasta = document.getElementById("<portlet:namespace />id_ramo_hasta");
		<%if(showABMButtons){%>
			var todasEmpresas = document.getElementById("<portlet:namespace />todas_empresas");	
			
			var esTodasEmpresas = todasEmpresas.checked ? 'true' : 'false';
			
			if( esTodasEmpresas == 'true'){
				fechaHastaDia = fechaDesdeDia;
				fechaHastaMes = fechaDesdeMes;
				fechaHastaAnio = fechaDesdeAnio;
			}
		
		<%}%>
		
		var aleatorio=document.getElementById("aleatorio");		
		
		var formatoProcesar = document.getElementById("<portlet:namespace />formato_procesar");
		var incluirActConv= document.getElementById("<portlet:namespace />incluir_acta_conv");
		var monotributistas= document.getElementById("<portlet:namespace />monotributistas");
			
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();

		<%if(showABMButtons){%>
		  if (trim(cuit).length != 11 && trim(cuil).length != 11 && (null==id_ramo.value || trim(id_ramo.value).length==0) && !todasEmpresas.checked && !monotributistas.checked ){
		<%}else{%>
		  if (trim(cuit).length != 11 && trim(cuil).length != 11 && (null==id_ramo.value || trim(id_ramo.value).length==0) && !monotributistas.checked ){
		<%}%>
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}
		
		var incluir_acta_conv='false';
		if(incluirActConv.checked){
			incluir_acta_conv='true';
		}else{
			incluir_acta_conv='false';
		}		
			
		jQuery('#<portlet:namespace />buscando').show();		
		
		window.location.href ='/xlsservlet/?reporte=APORTES_CONTRIBUCIONES_EMP'
			+'&cuit='+cuit
			+'&cuil='+cuil
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&fechaAcreDesdeDia='+fechaAcreDesdeDia.value
			+'&fechaAcreDesdeMes='+fechaAcreDesdeMes.value
			+'&fechaAcreDesdeAnio='+fechaAcreDesdeAnio.value
			+'&fechaAcreHastaDia='+fechaAcreHastaDia.value
			+'&fechaAcreHastaMes='+fechaAcreHastaMes.value			   
			+'&fechaAcreHastaAnio='+fechaAcreHastaAnio.value
			+'&id_ramo='+id_ramo.value
			+'&id_ramo_hasta='+id_ramo_hasta.value
			+'&aleatorio='+aleatorio.value
			+'&formato_procesar=' + (formatoProcesar.checked ? formatoProcesar.value : 'false')
			+'&incluir_acta_conv=' + incluir_acta_conv
			+'&monotributistas=' + monotributistas.checked 
			<%if(showABMButtons){%>				
				+'&todas_empresas=' + (todasEmpresas.checked ? todasEmpresas.value : 'false');
			<%}%>
			
	});
	
	/* function soloUnPeriodo(){
		alert('Sólo se traera un período al seleccionar todo un ramo o todas las empresas');		
		jQuery("#<portlet:namespace />incluir_acta_conv").attr('checked',false);
		jQuery("#<portlet:namespace />fechaHastaDia2").val(jQuery("#<portlet:namespace />fechaDesdeDia1").val());		
		jQuery("#<portlet:namespace />fechaHastaMes2").val(jQuery("#<portlet:namespace />fechaDesdeMes1").val());
		jQuery("#<portlet:namespace />fechaHastaAnio2").val(jQuery("#<portlet:namespace />fechaDesdeAnio1").val());
		
	} */
	
	
</script>

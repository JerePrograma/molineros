<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%		
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
    fechaDesde.set(Calendar.DAY_OF_MONTH, 1);
//	fechaDesde.add(Calendar.MONTH, -6);
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
	fechaHasta.setTime(new Date()); 
	Calendar current = CalendarFactoryUtil.getCalendar();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());
	
	String portlet_name=null;
	portlet_name="tesoreria";
	
	List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();
	
%>
 </form>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-correr-proceso-desregulados" />
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	
		<legend>
			<liferay-ui:message key="liquidar-jubilados" />
		</legend>
<!-- 		
		<fieldset class="block-labels">
		   <legend>
			Pedido de Informe
		   </legend>
		<table class="lfr-table">	
			<tr>	
				
					
				<td><label>Fecha Informe Desde:</label></td>
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
			<td><input type="button" value="Generar Pedido Informe" onClick="javascript: exportarTxt();" /></td>
			</tr>
		</table>
		</fieldset>
		
-->		
		<fieldset>
		 <legend>
			Procesar Respuesta
		 </legend>
		<table class="lfr-table">
		   <td  align="center">
				<input type="file" name="archivo"/>
			</td>
			<td align="center">

<!--  				<a href="javascript:void(0)" onclick="help(event, 'helpUpload')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
-->
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>
			
			<td>&nbsp;&nbsp;</td>
			
			<td>Período de Liquidación:</td>
			
			
			<td colspan="2"><liferay-ui:input-date dayParam="periodoLiquidacionDia"
						    dayNullable="<%= true %>" 
						    dayValue=""
						    monthValue="<%= fecha.get(Calendar.MONTH) %>"
						    monthAndYearParam="periodoLiquidacionMesAnio"
						    monthAndYearNullable="<%= true %>"
						    yearRangeStart="<%= fecha.get(Calendar.YEAR) - 2 %>"
						    yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 1 %>"
						    yearValue="<%= fecha.get(Calendar.YEAR) %>"
						    firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						    disabled="<%= false%>" />
						   </td>	
					
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
		
 	
 	<fieldset>
 	  <legend>
			Períodos Subidos
	  </legend>
 	  <table class="lfr-table">
	    <tr>
		   <td>
			  <div id="<portlet:namespace />jubilados_liquidados">
						<jsp:include page='/html/portlet/tesoreria/liquida_jubilados/liquida_jubilados_result.jsp' />  
			  </div>
			</td>	
			
			<td>
			   <fieldset>
			       <legend>
			          Planillas
	               </legend>
	               <table class="lfr-table">
	                   <tr valign="top">
		                  <td>
                            Período: 	               
	                      </td>
	                      
	                      
	                      <td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
						    dayNullable="<%= true %>" 
						    dayValue=""
						    monthValue="<%= fecha.get(Calendar.MONTH) %>"
						    monthAndYearParam="periodoMesAnio"
						    monthAndYearNullable="<%= true %>"
						    yearRangeStart="<%= fecha.get(Calendar.YEAR) - 15 %>"
						    yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 1 %>"
						    yearValue="<%= fecha.get(Calendar.YEAR) %>"
						    firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						    disabled="<%= false%>" />
						   </td>
						   
						   
						   <td>
						     <label><liferay-ui:message key="cuil" />:</label>
					       </td>
					       <td>
						     <input type="text" id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" value="" maxlength="11"/>
					       </td>
						   
						   <td>
						     <label>DNI:</label>
					       </td>
					       <td>
						     <input type="text" id="<portlet:namespace />dni" name="<portlet:namespace />dni" value="" maxlength="11"/>
					       </td>
					       
					      
	               </tr>
	               <tr><td>&nbsp;</td></tr>
	               <tr>
	                  <table>
	                  <td>
						  <liferay-ui:message key="tercerizadora-servicio" />
					  </td>
					  <td>
						     <select name="<portlet:namespace/>tercerizadora" id="<portlet:namespace/>tercerizadora">
							   <option value="">Seleccione una tercerizadora</option>
							  <%	for (TercerizadoraServicio terce : tercServList) { %>
									<option value="<%= terce.getId_tercerizadora()%>"><%=terce.getDescripcion()%></option>
							  <%	} %>
						     </select>
					   </td> 
	                  <td align="center" colspan="2"><input id="<portlet:namespace />buscar"
						value="Obtener Planilla"
						title="Obtener Planilla" type="button" onclick="javascript:filtrar()" />
					   </td>
					   </table>
	               </tr> 
	               </table>
			   </fieldset>
			
			
			</td>
	    </tr>
	  </table>
 	</fieldset>
 	
		<div align="center" id="<portlet:namespace/>desregula_liquidados_bot">	
<!--  			<jsp:include page='/html/portlet/tesoreria/liquida_desregulados/div_liquida_desregulados.jsp' /> -->						
		</div>	
	<br>		 	
	   					
</form>
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();	
jQuery("#<portlet:namespace />periodoDia").hide();
jQuery("#<portlet:namespace />periodoLiquidacionDia").hide();


		function exportarTxt(){
			var fechaDesdeDia = jQuery('#<portlet:namespace />fechaDesdeDia').val();
			var fechaDesdeMes = jQuery('#<portlet:namespace />fechaDesdeMes').val();
			var fechaDesdeAnio = jQuery('#<portlet:namespace />fechaDesdeAnio').val();
			var fechaHastaDia = jQuery('#<portlet:namespace />fechaHastaDia').val();
			var fechaHastaMes = jQuery('#<portlet:namespace />fechaHastaMes').val();
			var fechaHastaAnio = jQuery('#<portlet:namespace />fechaHastaAnio').val();
					
			window.location.href ='/txtservlet/?reporte=PEDIDO_INFORME_JUBILADOS&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+
					              '&fechaDesdeAnio='+fechaDesdeAnio+'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+
					              '&fechaHastaAnio='+fechaHastaAnio ;						
		}
		
		
		function <portlet:namespace />uploadArchivo() {	
			var periodo = jQuery('#<portlet:namespace />periodoLiquidacionMesAnio').val();
			if(null!=periodo && periodo.indexOf("_") !==-1){
				
				var mes = parseInt(periodo.split('_')[0])+1;
				var anio = periodo.split('_')[1];
				periodo=anio+mes.toString().padStart(2, '0');
						
			}
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivo_afip';
			url =url + "&periodoliquidacion="+periodo;
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url);
		}
		
		function filtrar(){
			var periodo = jQuery('#<portlet:namespace />periodoMesAnio').val();
			var cuil = jQuery('#<portlet:namespace />cuil').val();
			var dni = jQuery('#<portlet:namespace />dni').val();
			var tercerizadora = jQuery('#<portlet:namespace />tercerizadora').val();
			if(null!=periodo && periodo.indexOf("_") !==-1){
			
				var mes = parseInt(periodo.split('_')[0])+1;
				var anio = periodo.split('_')[1];
				periodo=anio+mes.toString().padStart(2, '0');
				//periodo=mes.toString().padStart(2, '0')+anio.substring(2,4);
						
			}
			window.location.href ='/xlsservlet/?reporte=JUBILADOS_SITACI_DETALLE&periodo='+periodo+
			                      '&cuil='+cuil+
			                      '&dni='+dni+
			                      '&tercerizadora='+tercerizadora+
					              '&rnd=' + Math.floor(Math.random()*100);
		}
		
</script>

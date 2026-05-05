	<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

		String portlet_name = ParamUtil.getString(request, "portlet_name");
		int entidad=WebKeysGlobal.OSPIM;
	
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			portlet_name = "farmacia";
		} 
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
			portlet_name = "uoma";
		} 
		
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES)|| portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		List<Concepto> conceptos=TraeListasServiceUtil.getConceptoIngreso(entidad);
		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="listado-recibos" /></legend>
				<table class="lfr-table">
					<tr>							
						<td colspan="4">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>
						  		<liferay-util:param name="soloOP" value='false'/>						  		
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="conceptos" />:</label></td>
						<td colspan="3">
							<select id="<portlet:namespace/>concepto" name="<portlet:namespace/>concepto" multiple="multiple" size="20">
								<optgroup label="Actas y Convenios">
									<option value="990">ACTAS</option>
									<option value="991">CONVENIOS</option>
								</optgroup>
								<optgroup label="Cheques">
									<option value="992">RECHAZADOS</option>
									<option value="993">NO DEPOSITADOS</option>
								</optgroup>
								<optgroup label="Otros Conceptos">
									<% for (Concepto cpt : conceptos) { %>
													<option value="<%= cpt.getId()%>"><%=cpt.getDescripcion()%></option>											
									<% } %>
								</optgroup>								
							</select>
							<input id="<portlet:namespace />borrar" value="<liferay-ui:message key="limpiar-filtro"/>" title="<liferay-ui:message key="limpiar-filtro" />" type="button"/>
							<label><liferay-ui:message key="ninguna-seleccion-todos" /></label>							
						</td>
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>					
					<tr>						
						<td colspan="4">
							<table>
								<tr>
								<%if(portlet_name.equals("uoma")){%>
									<td>Incluir '0001' <input type="checkbox" id="<portlet:namespace/>0001" name="<portlet:namespace/>0001" checked /></td>
									<td>Incluir '0002' <input type="checkbox" id="<portlet:namespace/>0002" name="<portlet:namespace/>0002" checked /></td>
									<td>Incluir '0003' <input type="checkbox" id="<portlet:namespace/>0003" name="<portlet:namespace/>0003" checked /></td>
									<td>Incluir 'Rend' <input type="checkbox" id="<portlet:namespace/>Rend" name="<portlet:namespace/>Rend" checked /></td>
									<td>Incluir 'BcaP' <input type="checkbox" id="<portlet:namespace/>BcaP" name="<portlet:namespace/>BcaP" checked /></td>
									<td>Incluir 'Otro' <input type="checkbox" id="<portlet:namespace/>Otro" name="<portlet:namespace/>Otro" checked /></td>
								<%}%>		
									<td>Formato exportación <input type="checkbox" id="<portlet:namespace/>export" name="<portlet:namespace/>export" /></td>
								</tr>
							</table>
						</td>
					</tr>						
								
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
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
						<td>
							<liferay-ui:input-date
							dayParam="fechaHastaDia2"
							dayValue="<%= fechaPago.get(Calendar.DATE) %>" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						<td>
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
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
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	

	jQuery('#<portlet:namespace />reporte').click(function(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");

		var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
		var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
		var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();

		var concepto=jQuery("#<portlet:namespace/>concepto").val();
		<%if(portlet_name.equals("uoma")){%>
			var opc001=jQuery('#<portlet:namespace />0001').is(':checked');
			var opc002=jQuery('#<portlet:namespace />0002').is(':checked');
			var opc003=jQuery('#<portlet:namespace />0003').is(':checked');
			var rend=jQuery('#<portlet:namespace />Rend').is(':checked');
			var bcap=jQuery('#<portlet:namespace />BcaP').is(':checked');
			var otro=jQuery('#<portlet:namespace />Otro').is(':checked');
		<%}%>		
		var exportacion=jQuery('#<portlet:namespace />export').is(':checked');
		window.location.href ='/xlsservlet/?reporte=REPORTE_RECIBOS'
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&cuit_entidad=' +cuit_entidad
			+'&sucursal_entidad=' +sucursal_entidad
			+'&id_seccional=' +id_seccional
			<%if(portlet_name.equals("uoma")){%>
				+'&opc0001='+opc001
				+'&opc0002='+opc002
				+'&opc0003='+opc003
				+'&rend='+rend
				+'&bcap='+bcap
				+'&otro='+otro
			<%}%>
			+ '&export='+exportacion
			+ '&conceptos='+concepto
			+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
			+'&rnd=' + Math.floor(Math.random()*100);
	});
	
	jQuery('#<portlet:namespace />borrar').click(function(){		
	    var i; 
	    var select = document.getElementById("<portlet:namespace/>concepto"); 
	    for(i=1;i<select.options.length;i++) 
	         { 
	        select.options[i].selected=false; 
	    } 
	});
	
	
</script>

<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server		
		
		String portlet_name = ParamUtil.getString(request, "portlet_name");
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_LIQ_1_")){
			portlet_name = "liquidaciones";
		} 

		boolean showOpcionesAuditor = !PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) && PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO); 
			
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.add(Calendar.MONTH, -1);
		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
		fechaHasta.setTime(new Date()); 
		Calendar current = CalendarFactoryUtil.getCalendar();

%>

	<form  method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">		
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="ficha-de-farmacia" /></legend>
				<table class="lfr-table">
					<tr>
						<td colspan="8">
								<table class="lfr-table">
									<tr>
									<td><label><liferay-ui:message key="periodo-liquidacion-desde" />:</label></td>
										<td>
											<liferay-ui:input-date
											dayParam="periodoDesdeDia"
											dayValue="1" 
											monthParam="periodoDesdeMes"
											monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
											monthNullable="<%= true %>"				
											yearParam="periodoDesdeAnio"
											yearNullable="<%= true %>"
											yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
											yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
											yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) %>"
											firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
											disabled="<%= false %>" />
										</td>
										<td><label><liferay-ui:message key="periodo-liquidacion-hasta" />:</label></td>
										<td>
											<liferay-ui:input-date
											dayParam="periodoHastaDia"
											dayValue="1" 
											monthParam="periodoHastaMes"
											monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
											monthNullable="<%= true %>"
											yearParam="periodoHastaAnio"
											yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
											yearNullable="<%= true %>"
											yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
											yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) %>"
											firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
											disabled="<%= false %>" />
										</td>
									</tr>
								</table>
						</td> 
					</tr>
						<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>		
						</tr>			
					<tr>
						<td colspan="4">
							<fieldset class="block-labels">
							<legend><liferay-ui:message key="datos-de-liquidacion" /></legend>
							<table class="lfr-table">
								<tr>
									<td><label><liferay-ui:message key="opDesde"/>:</label></td>
									<td><input id="<portlet:namespace />opDesde" name="<portlet:namespace />opDesde" size="10" maxlength="20" type="text" value=''/></td>
									<td><label><liferay-ui:message key="opHasta"/>:</label></td>
									<td><input id="<portlet:namespace />opHasta" name="<portlet:namespace />opHasta" size="10" maxlength="20" type="text" value=''/></td>
									<td colspan="1">PMI: </label></td>
									<td><input type="checkbox" id="<portlet:namespace />pmi" name="<portlet:namespace />pmi" value="false"/></td>
									<td><liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
							 				<%if(portlet_name.equals("liquidaciones")){%>
												<liferay-util:param name="search_url" value="/liquidaciones/buscar_medicamento" />
							 				<%}else{%>
							 					<liferay-util:param name="search_url" value="/farmacia/buscar_medicamento" />
							 				<%}%>
												<liferay-util:param name="troquel" value='' />
												<liferay-util:param name="nombre_medicamento" value='' />
												<liferay-util:param name="id_medicamento" value='' />
												<liferay-util:param name="esEditable" value='true' />
												<liferay-util:param name="popup" value='true' />
										</liferay-util:include></td>
								</tr>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
							</table>
								 <div id="<portlet:namespace />divBuscarFarmacia" name="<portlet:namespace />divBuscarFarmacia">
									<fieldset class="block-labels"><legend>Seleccione Farmacia</legend>
									<liferay-util:include page='/html/portlet/uoma/correspondencia/busqueda_farmacia.jsp'>
										<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
										<liferay-util:param value="" name="id_farmacia" />
										<liferay-util:param value="" name="farmacia" />
 										<liferay-util:param name="pag_reintegro" value='1' />
											</liferay-util:include>
									</fieldset>	
								</div> 
							</fieldset>
						</td>
					</tr>
						<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						</tr>
					<tr>
						<td colspan="12">
							<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
							<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
							</liferay-util:include>
							</fieldset>
						</td>
					</tr>
						<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						</tr>
					<tr>						
						<td coslpan="1">							
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>			
					</tr>
				</table>
		</fieldset>
	</form>

<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){		
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();		
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();		
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();		
		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();		
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();		
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();		
		var inte=jQuery('#<portlet:namespace />inte').val();		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();		
		var entidad=jQuery('#<portlet:namespace />entidad').val();		
		var opDesde=jQuery('#<portlet:namespace />opDesde').val();		
		var opHasta=jQuery('#<portlet:namespace />opHasta').val();
		var troquel=jQuery("#<portlet:namespace />troquel").val();	
	    var nombre_medicamento=jQuery("#<portlet:namespace />nombre_medicamento").val();  
	    var id_farmacia=jQuery("#<portlet:namespace />id_farmacia").val();
	    var farmacia=jQuery("#<portlet:namespace />farmacia").val();
		var pmi=jQuery('#<portlet:namespace />pmi').is(':checked');
		
	jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/xlsservlet/?reporte=FICHA_DE_FARMACIA'+'&entidad='+entidad+
 		'&periodoDesdeDia='+periodoDesdeDia+'&periodoDesdeMes='+periodoDesdeMes+'&periodoDesdeAnio='+periodoDesdeAnio+
 		'&periodoHastaDia='+periodoHastaDia+'&periodoHastaMes='+periodoHastaMes+'&periodoHastaAnio='+periodoHastaAnio+
 		'&opDesde='+opDesde+'&opHasta='+opHasta+'&troquel='+troquel+'&cuil='+cuil+'&inte='+inte+
		'&id_farmacia='+id_farmacia +'&farmacia='+farmacia+'&pmi='+pmi;
	});

</script>
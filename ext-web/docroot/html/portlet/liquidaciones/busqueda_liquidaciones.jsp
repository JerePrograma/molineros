<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
		//String dinámico que define los parámetros de búsqueda y de resultados de la página, segun si es 'pre' de liquidacion prestacional
		//u 'odo' de liquidacion odontología, si es nulo se setea por default a 'pre' 
		String tipo_liquidacion = ParamUtil.getString(request, "tipo_liquidacion", "pre");
		
		//boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		boolean showABMButtons =false;
		
		boolean esLiquidadorExterno = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_LIQUIDACIONES_HOSPITALES);
		
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar();
 		periodoHasta.setTime(new Date());
 		
 		List<ClaseBase> sectores = TraeListasServiceUtil.getSectoresLiquidaciones();
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-liquidaciones" /></legend>
				<table class="lfr-table">										
					<tr>	
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 2 %>"
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoDesdeDia"
								dayNullable="<%= true %>"
								dayValue="<%= periodoDesde.get(Calendar.DATE)%>"
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"							
								yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="periodoHastaDia"
								dayNullable="<%= true %>" 
								dayValue="<%= periodoHasta.get(Calendar.DATE)%>"							
								monthAndYearParam="periodoHastaMesAnio"
								monthValue="<%= periodoHasta.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= periodoHasta.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= periodoHasta.get(Calendar.YEAR) + 2 %>"
								firstDayOfWeek="<%= periodoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE) %>">
						<tr>																			
							<input id="<portlet:namespace />codPrest" name="<portlet:namespace />codPrest" size="6" maxlength="6" type="hidden" value="" />							
							<td colspan="4">
								<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
							  		<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador"/>
							  		<liferay-util:param name="cuit_prestador" value=''/>
							  		<liferay-util:param name="nombre_prestador" value=''/>
							  		<liferay-util:param name="esEditable" value='<%= String.valueOf(true) %>'/>
							  		<liferay-util:param name="esLiquidadorHospital" value='<%= String.valueOf(esLiquidadorExterno) %>'/>
								</liferay-util:include>
							</td>
							<td><label><liferay-ui:message key="numero-texto" />:</label></td>
							<td colspan="1"><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="8" maxlength="8" type="text" value="" /></td>
							<td><label><liferay-ui:message key="estado" />:</label></td>
							<td colspan="1">
							<select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">								
								<option value="0"></option>
								<option value="1">No Pagados</option>
								<option value="2">Pagados</option>
							</select>
							</td>
						</tr>
					</c:if>
					<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO) %>">
						<tr>
							<td colspan="12">
							<jsp:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp' />
							</td>
						</tr>
					</c:if>
					<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO) %>">
						<tr>
							<td colspan="12">&nbsp;</td>
						</tr>
						<tr>							
							<td colspan="1"><label><liferay-ui:message key="presupuesto" />:</label></td>
							<td colspan="1"><input id="<portlet:namespace />presupuesto" name="<portlet:namespace />presupuesto" size="8" maxlength="8" type="text" value="" /></td>						
						</tr>
					</c:if>										
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) %>">
						<tr>
							<td><label><liferay-ui:message key="comprobante" />:</label></td>
							<td colspan="3">
							<select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">
								<option value=""></option>
								<option value="FCP">FCP</option>
								<option value="NCR">NCR</option>
								<option value="NDB">NDB</option>
								<option value="RCB">RCB</option>
							</select> &nbsp;
							<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra">
								<option value=""></option>								
								<option value="B">B</option>
								<option value="C">C</option>								
							</select> &nbsp;						
							<input id="<portlet:namespace />sucu"
								name="<portlet:namespace />sucu" size="5" maxlength="6"
								type="text"
								value="" />&nbsp;							
							<input id="<portlet:namespace />comprobante_nro"
								name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
								type="text"
								value="" />
							</td>
							<td><label><liferay-ui:message key="orden-compra-nro" />:</label></td>
							<td><input id="<portlet:namespace />nro_oc"
								name="<portlet:namespace />nro_oc" size="12" maxlength="8" ></td>
						</tr>
						<tr>
							<td colspan="12">&nbsp;</td>
						</tr>					
					</c:if>
					
					<tr>
					
					   <td><label><liferay-ui:message key="sector" />:</label></td>
					   <td><select id="<portlet:namespace />sector_id" name="<portlet:namespace />sector_id">
					            <option value="-1">Todos</option>
								<%for(ClaseBase cta : sectores) {%>
										<option value="<%=cta.getId()%>"><%=cta.getDescripcion()%></option>
								<%}%>
						   </select>
						</td>
					</tr>
					
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>		
					<tr>						
						<td coslpan="1">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td colspan="6">
							<c:if test="<%=showABMButtons %>">
								<input type="button" value="<liferay-ui:message key="alta-liquidacion" />" onClick="<portlet:namespace />altaLiquidacion();" />
							</c:if>
						</td>						
					</tr>
					<tr>
						<td colspan="12">
							&nbsp;(<liferay-ui:message key="refine-busqueda-200" />)
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
			<div align="center" id="<portlet:namespace />busquedaLiquidacionDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').show();
	
	var url = "<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_liquidaciones_sesion&tipo_liquidacion=<%=tipo_liquidacion%>";
	jQuery('#<portlet:namespace />busquedaLiquidacionDiv').load(url);
	jQuery('#<portlet:namespace />buscando').hide();
		
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();
		var estado=jQuery('#<portlet:namespace />estado').val();
		var nroOC=jQuery('#<portlet:namespace />nro_oc').val();
		
		var entidad="O.S.P.I.M.";
		<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO) %>"> 			
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
			var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
			var apellido=jQuery('#<portlet:namespace />apellido').val();
			var nombre=jQuery('#<portlet:namespace />nombre').val();
			var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
			var presupuesto=jQuery('#<portlet:namespace />presupuesto').val();
		</c:if>
		<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE) %>">			
			var codPrestad=jQuery('#<portlet:namespace />codPrest').val();
			var id_prestador=jQuery('#<portlet:namespace />id_prestador').val();
			var cuit=jQuery('#<portlet:namespace />cuit_prestador').val();
			var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();
			var comprobante_tipo=jQuery('#<portlet:namespace />comprobante_tipo').val();
			var comprobante_letra=jQuery('#<portlet:namespace />comprobante_letra').val();
			var sucu=jQuery('#<portlet:namespace />sucu').val();			
			var comprobante_nro=jQuery('#<portlet:namespace />comprobante_nro').val();
		</c:if>
		var numero=jQuery('#<portlet:namespace />numero').val();
		var sector=jQuery('#<portlet:namespace />sector_id').val();
		
		jQuery('#<portlet:namespace />buscando').show();
		
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />prest_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />nombre_prestador").val("");
			jQuery("#<portlet:namespace />id_prestador").val("");
			jQuery("#<portlet:namespace />cuit_prestador").val("");
		}
		<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE) %>">
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_liquidaciones&entidad='+entidad+
			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
			'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&codPrest='+codPrestad+
			'&comprobante_tipo='+comprobante_tipo+'&comprobante_letra='+comprobante_letra+'&sucu='+sucu+'&comprobante_nro='+comprobante_nro+
			'&cuit='+cuit+'&id_prestador='+id_prestador+'&prestador='+encodeURI(prestador)+'&numero='+numero+'&estado='+estado+'&nro_oc='+nroOC+'&tipo_liquidacion='+'<%=tipo_liquidacion%>';
			url += "&sector="+sector;
		</c:if>
		<c:if test="<%= tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO) %>">
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_liquidaciones&fechaDesdeDia='+fechaDesdeDia+
			'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
			'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&numero='+numero+
			'&cuil='+cuil+'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&presupuesto='+presupuesto+'&tipo_liquidacion='+'<%=tipo_liquidacion%>';
			url += "&sector="+sector;
		</c:if>		
        jQuery('#<portlet:namespace />busquedaLiquidacionDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	});
	
	function <portlet:namespace />hideDayFieldOfPeriodFields () {
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		jQuery("#<portlet:namespace />periodoHastaDia").hide();
	}

	<portlet:namespace />hideDayFieldOfPeriodFields ();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeDia').val("");
		jQuery('#<portlet:namespace />fechaDesdeMes').val("");		
		jQuery('#<portlet:namespace />fechaDesdeAnio').val("");
		jQuery('#<portlet:namespace />fechaHastaDia').val("");
		jQuery('#<portlet:namespace />fechaHastaMes').val("");
		jQuery('#<portlet:namespace />fechaHastaAnio').val("");			
	}

	<portlet:namespace />initDateFields();
	
</script>
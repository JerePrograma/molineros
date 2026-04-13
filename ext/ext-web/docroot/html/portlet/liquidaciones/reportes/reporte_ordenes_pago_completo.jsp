<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

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
		
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		List<CuentaBancaria> ctas=(ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,PortletSession.APPLICATION_SCOPE);
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-ordenes-pago-detallado" /></legend>
				<table class="lfr-table">
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
							<%if(portlet_name.equals("tesoreria")){ %>
								Formato Recepción&nbsp;<input type="checkbox" name="<portlet:namespace />formato_recepcion" id="<portlet:namespace />formato_recepcion" value="false"/>
							<%}%>&nbsp;
						</td>
						<td colspan="4">
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
					<tr>						
						<td colspan="9">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<%if(portlet_name.equals("uoma")){%>
						  			<liferay-util:param name="soloOP" value='false'/>
						  		<%}else{%>
						  			<liferay-util:param name="soloOP" value='true'/>
						  		<%}%>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="comprobante" />:</label></td>
						<td colspan="8">
							<select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">
								<option value=""></option>
								<option value="FCP">FCP</option>
								<option value="NCR">NCR</option>
								<option value="NDB">NDB</option>
								<option value="RCB">RCB</option>
								<option value="ANT">ANT</option>
								<option value="REI">REI</option>
								<option value="VAR">VAR</option>
							</select> &nbsp;Letra:&nbsp;
							<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra">
								<option value=""></option>								
								<option value="B">B</option>
								<option value="C">C</option>								
							</select> &nbsp;Sucu:&nbsp;						
							<input id="<portlet:namespace />sucu"
								name="<portlet:namespace />sucu" size="5" maxlength="6"
								type="text"
								value="" />&nbsp;Nro:&nbsp;					
							<input id="<portlet:namespace />comprobante_nro"
								name="<portlet:namespace />comprobante_nro" size="15" maxlength="17"
								type="text"
								value="" />
						</td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
						<td><label><liferay-ui:message key="nro-lote" />:</label></td>
						<td colspan="8"><input type="text" size="6" id="<portlet:namespace />nro_lote" name="<portlet:namespace />nro_lote"/></td>					
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="cta-bcria" />:</label></td>
						<td colspan="2">
							<select name="<portlet:namespace/>cta_bancaria" id="<portlet:namespace/>cta_bancaria">
								<option value="0">Todas</option>									
								<% for (CuentaBancaria ctaBcria : ctas) { 
								 	if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_FAR_1_") && ctaBcria.getId_cuenta_bcria()==5) {%>
										<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>
									<%}else if(renderResponse==null || renderResponse.getNamespace()==null || !renderResponse.getNamespace().equals("_FAR_1_")){ %>
										<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
								<%	} 
								} %>
							</select>
						</td>
						<td>
							Solo OPs de baja&nbsp;<input type="checkbox" name="<portlet:namespace />solo_de_baja" id="<portlet:namespace />solo_de_baja" value="true"/>
						</td>
						<td colspan="5">
						    Incluir totales&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_totales" id="<portlet:namespace />incluir_totales" value="true"/>
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
		
		var cta_bancaria = document.getElementById("<portlet:namespace />cta_bancaria");
		var soloDeBaja = document.getElementById("<portlet:namespace />solo_de_baja");
		var incluirTotales = document.getElementById("<portlet:namespace />incluir_totales");
		
		var id_prestador = document.getElementById("<portlet:namespace />id_prestador");
		var cuit_prestador = document.getElementById("<portlet:namespace />cuit_entidad");
		var sucur_prestador = document.getElementById("<portlet:namespace />sucursal_entidad");
		var nombre_prestador = document.getElementById("<portlet:namespace />entidad");

		//alert('id_prestador: '+id_prestador.value+' cuit: '+cuit_prestador.value+' nombre_prestador: '+nombre_prestador.value);

		var comprobante_tipo=document.getElementById("<portlet:namespace/>comprobante_tipo");
		var comprobante_letra=document.getElementById("<portlet:namespace/>comprobante_letra");
		var sucu=document.getElementById("<portlet:namespace />sucu");
		var compro_nro=document.getElementById("<portlet:namespace />comprobante_nro");
		var nro_lote=document.getElementById("<portlet:namespace />nro_lote");
		<%if(portlet_name.equals("liquidaciones")){%>
			var formatoRecep = document.getElementById("<portlet:namespace />formato_recepcion");
		<%}%>
		
		//alert('compro_tipo: '+comprobante_tipo.value+' comprobante_letra: '+comprobante_letra.value+' sucu: '+sucu.value+' compro_nro: '+compro_nro.value);
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_ORDENES_PAGO_COMPLETO'
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&cta_bancaria='+cta_bancaria.value			
			+'&cuit_prestador='+cuit_prestador.value
			+'&sucur_prestador='+sucur_prestador.value
			+'&nombre_prestador='+nombre_prestador.value
			+'&compro_tipo='+comprobante_tipo.value
			+'&compro_letra='+comprobante_letra.value
			+'&compro_nro='+compro_nro.value
			+'&sucur='+sucu.value
			+'&nro_lote='+nro_lote.value
			+'&solo_de_baja=' + (soloDeBaja.checked ? soloDeBaja.value : 'false')
			+'&incluir_totales=' + (incluirTotales.checked ? incluirTotales.value : 'false')
			<%if(portlet_name.equals("liquidaciones")){%>
				+'&formato_recepcion=' + (formatoRecep.checked ? 'true' : 'false')
			<%}%>
			<% if(portlet_name.equals("farmacia")) {%>
			+'&entidad=<%=WebKeysGlobal.AMTIMA%>'
			<%}else if(portlet_name.equals("uoma")) {%>
			+'&entidad=<%=WebKeysGlobal.UOMA%>'
			<%}else{%>
			+'&entidad=<%=WebKeysGlobal.OSPIM%>'
			<%}%>			
			+'&rnd=' + Math.floor(Math.random()*100);
			
	});
	function cambiaCuit(){}	
	
</script>

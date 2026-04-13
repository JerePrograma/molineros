<%@ include file="/html/portlet/tesoreria/init.jsp" %>
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
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES)|| portlet_name.equals("farmacia");
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		List<CuentaBancaria> ctas=(ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,PortletSession.APPLICATION_SCOPE);
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="subdiario-ingresos" /></legend>
				<table class="lfr-table">
					<tr>							
						<td colspan="4">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>
						  		<liferay-util:param name="soloOP" value='false'/>
						  		<liferay-util:param name="soloIngresos" value='true'/>
							</liferay-util:include>
						</td>
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
						    Incluir totales&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_totales" id="<portlet:namespace />incluir_totales" value="true"/>
						</td>
						<td>
						    Incluir Cuadro Ingresos&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_cuadro" id="<portlet:namespace />incluir_cuadro" value="true" checked="checked"/>
						</td>
						<td>
							&nbsp;
						</td>
					</tr>
					<%if(portlet_name.equals("uoma")){ %>
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-impresion" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaImpreDia"
							dayValue="<%= fechaPago.get(Calendar.DATE) %>" 
							monthParam="fechaImpreMes"
							monthValue="<%= fechaPago.get(Calendar.MONTH) %>"				
							yearParam="fechaImpreAnio"
							yearValue="<%= fechaPago.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
					</tr>
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					<% } else {%>
						<tr>
							<td colspan="3">&nbsp;</td>
							<td>
								<input type="hidden" name="<portlet:namespace />fechaImpreDia" id="<portlet:namespace />fechaImpreDia" value="0">
								<input type="hidden" name="<portlet:namespace />fechaImpreMes" id="<portlet:namespace />fechaImpreMes" value="0">
								<input type="hidden" name="<portlet:namespace />fechaImpreAnio" id="<portlet:namespace />fechaImpreAnio" value="0">
							</td>
						</tr>
					<% } %>
					<tr>
						<td colspan="4">
						<%if(portlet_name.equals("farmacia")){%>
							<!--input type="hidden" name="<portlet:namespace />incluir_afip" id="<portlet:namespace />incluir_afip"/-->
						    Incluir Boletas AMTIMA&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_afip" id="<portlet:namespace />incluir_afip" value="true" checked="checked"/>&nbsp;
						<%}else if(portlet_name.equals("tesoreria")){%>
						    Incluir Mov. Afip&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_afip" id="<portlet:namespace />incluir_afip" value="true" checked="checked"/>&nbsp;
						<%}else if(portlet_name.equals("uoma")){%>
					    	Incluir Boletas UOMA&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_afip" id="<portlet:namespace />incluir_afip" value="true" checked="checked"/>&nbsp;
						<%}%>
						    Incluir Mov. Recibos&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_recibos" id="<portlet:namespace />incluir_recibos" value="true" checked="checked"/>&nbsp;
						    Incluir Mov. Bancarios&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_bcrios" id="<portlet:namespace />incluir_bcrios" value="true" checked="checked"/>&nbsp;
						    Formato para Exportar &nbsp;<input type="checkbox" name="<portlet:namespace />exportar" id="<portlet:namespace />exportar" value="true" checked="checked"/>&nbsp;
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
	jQuery('#<portlet:namespace />incluir_totales').click(function(){
			
		if(document.getElementById("<portlet:namespace />incluir_totales").checked){
			document.getElementById("<portlet:namespace />exportar").checked=false;
		}
	});
	
	jQuery('#<portlet:namespace />exportar').click(function(){			
		if(document.getElementById("<portlet:namespace />exportar").checked){
			document.getElementById("<portlet:namespace />incluir_totales").checked=false;
		}
	});

	jQuery('#<portlet:namespace />reporte').click(function(){
	
		var exportar = document.getElementById("<portlet:namespace />exportar");
		var incluir_bcrios = document.getElementById("<portlet:namespace />incluir_bcrios");
		var incluir_recibos = document.getElementById("<portlet:namespace />incluir_recibos");
		var incluir_afip = document.getElementById("<portlet:namespace />incluir_afip");
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");

		var fechaImpresionDia  = document.getElementById("<portlet:namespace />fechaImpreDia");
		var fechaImpresionMes  = document.getElementById("<portlet:namespace />fechaImpreMes");
		var fechaImpresionAnio = document.getElementById("<portlet:namespace />fechaImpreAnio");
		
		var incluirTotales = document.getElementById("<portlet:namespace />incluir_totales");

		var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
		var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
		var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();

		var incluirCuadro = document.getElementById("<portlet:namespace />incluir_cuadro");		
		
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas';
			url +='&fechaDesdeDia='+fechaDesdeDia.value;
			url +='&fechaDesdeMes='+fechaDesdeMes.value;
			url +='&fechaDesdeAnio='+fechaDesdeAnio.value;
			url +='&fechaHastaDia='+fechaHastaDia.value;
			url +='&fechaHastaMes='+fechaHastaMes.value;
			url +='&fechaHastaAnio='+fechaHastaAnio.value;			
			url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';			
			url += '&rnd=' + Math.floor(Math.random()*100);			
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if (obj.status == "equivalencias_conceptos_incompleto"){
					alert("Las equivalencias conceptos-cuentas se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
					return;
				}
				if (obj.status == "equivalencias_prestaciones_incompleto"){
					alert("Las equivalencias prestaciones-conceptos se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
					return;
				}
				if (obj.status == "falla_inesperada"){
					alert("Falla inesperada. Contacte a sistemas");
					return;
				}
				if (obj.status == "ok"){					
					window.location.href ='/xlsservlet/?reporte=REPORTE_SUBDIARIO_INGRESOS'
						+'&fechaDesdeDia='+fechaDesdeDia.value
						+'&fechaDesdeMes='+fechaDesdeMes.value
						+'&fechaDesdeAnio='+fechaDesdeAnio.value
						+'&fechaHastaDia='+fechaHastaDia.value
						+'&fechaHastaMes='+fechaHastaMes.value
						+'&fechaHastaAnio='+fechaHastaAnio.value
						+'&fechaImpresionDia='+fechaImpresionDia.value
						+'&fechaImpresionMes='+fechaImpresionMes.value
						+'&fechaImpresionAnio='+fechaImpresionAnio.value
						+'&incluir_cuadro='+ (incluirCuadro.checked ? incluirCuadro.value : 'false')
						+'&incluir_totales=' + (incluirTotales.checked ? incluirTotales.value : 'false')
						+ '&cuit_entidad=' +cuit_entidad
						+ '&sucursal_entidad=' +sucursal_entidad
						+ '&id_seccional=' +id_seccional
						+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'						
						+'&rnd=' + Math.floor(Math.random()*100)
						+'&incluir_bcrios=' + (incluir_bcrios.checked ? incluir_bcrios.value : 'false')
						+'&incluir_recibos=' + (incluir_recibos.checked ? incluir_recibos.value : 'false')										
						+'&incluir_afip=' + (incluir_afip.checked ? incluir_afip.value : 'false')						
						+'&exportar=' + (exportar.checked ? exportar.value : 'false');
				}
				
			}
		});
		
	});
	
	
</script>

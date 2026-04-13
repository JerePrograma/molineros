<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.beans.PagoBancario"%>
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
		
		List<CuentaBancaria> ctas1=null;
 		ctas1=TraeListasServiceUtil.getCtasBcrias();
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="subdiario-egresos" /></legend>
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
						<%if(null==portlet_name || (!portlet_name.equals("farmacia") && !portlet_name.equals("uoma"))){%>						   
						    Incluir Liquidaciones&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" value="true" checked="checked"/>&nbsp;
						    Incluir Reintegros&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" value="true" checked="checked"/>&nbsp;
						    Incluir Proveedores&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_proveedores" id="<portlet:namespace />incluir_proveedores" value="true" checked="checked"/>&nbsp;						    
						    Incluir Mov. Bcrios.&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_movimientosbancarios" id="<portlet:namespace />incluir_movimientosbancarios" value="true" checked="checked"/>&nbsp;
						    Incluir totales&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_totales" id="<portlet:namespace />incluir_totales" value="true"/>&nbsp;
							<input type="hidden" name="<portlet:namespace />incluir_obs_comp" id="<portlet:namespace />incluir_obs_comp" />&nbsp;
						<%}else{%>
							<input type="hidden" name="<portlet:namespace />incluir_liquidaciones" id="<portlet:namespace />incluir_liquidaciones" />&nbsp;
						    <input type="hidden" name="<portlet:namespace />incluir_reintegros" id="<portlet:namespace />incluir_reintegros" />&nbsp;
						 	Incluir Proveedores&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_proveedores" id="<portlet:namespace />incluir_proveedores" value="true" checked="checked"/>&nbsp;						    
						    Incluir Mov. Bcrios.&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_movimientosbancarios" id="<portlet:namespace />incluir_movimientosbancarios" value="true" checked="checked"/>&nbsp;
						    Incluir totales&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_totales" id="<portlet:namespace />incluir_totales" value="true"/>&nbsp;
						    Incluir Obs.Comprobantes&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_obs_comp" id="<portlet:namespace />incluir_obs_comp" value="true"/>&nbsp;						    
						<%}%>
							Incluir Cuadro Egresos&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_cuadro" id="<portlet:namespace />incluir_cuadro" value="true" checked="checked"/>&nbsp;&nbsp;
						    Formato para Exportar &nbsp;<input type="checkbox" name="<portlet:namespace />exportar" id="<portlet:namespace />exportar" value="true" checked="checked"/>&nbsp;&nbsp;
							<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</td>
					</tr>
				</table>	      	  
		</fieldset>	
		
		<%if(portlet_name.equals("tesoreria")){ %>
		<fieldset class="block-labels">
				<legend>Interbanking</legend>
				<table class="lfr-table">
					<tr>
						<td><label>Forma de Pago:</label></td>
						<td>
						  <select id="<portlet:namespace />tipo_pago" name="<portlet:namespace />tipo_pago">
							<option value="<%=PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA%>" selected="selected">Transferencia Bancaria</option>
						  </select>
						
						
		                </td>
		                <td><label>Cuenta Bancaria:</label></td>
						<td>
						  <select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
							<% 	for (CuentaBancaria cta : ctas1) { 
									if (portlet_name.equals("farmacia") && cta.getEntidad().equals("A")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>"> <%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>									
								<%	}else if(portlet_name.equals("liquidaciones") && cta.getEntidad().equals("O")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 10) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%	}else if(portlet_name.equals("tesoreria") && cta.getEntidad().equals("O")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 10) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
								<%	}else if(portlet_name.equals("uoma") && cta.getEntidad().equals("U")){%>	
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 6) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
									<%}} %>
						  </select>
		                </td>
		                <td>
		                <input id="<portlet:namespace />exportarInterbanking" value="Exportar para Interbanking" title="Exportar para Interbanking" type="button"/>
		                </td>
		            </tr>    
		        </table>
		</fieldset>                
		<%}%>                
		                
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
		var incluir_reintegros = document.getElementById("<portlet:namespace />incluir_reintegros");
		var incluir_liquidaciones = document.getElementById("<portlet:namespace />incluir_liquidaciones");
		var incluir_proveedores = document.getElementById("<portlet:namespace />incluir_proveedores");
		var incluir_movimientosbancarios = document.getElementById("<portlet:namespace />incluir_movimientosbancarios");
		var incluir_obs_comp = document.getElementById("<portlet:namespace />incluir_obs_comp");
		var incluirCuadro = document.getElementById("<portlet:namespace />incluir_cuadro");
				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas&amtima=true';		
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
					window.location.href ='/xlsservlet/?reporte=REPORTE_SUBDIARIO_EGRESOS'
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
					+'&incluir_reintegros=' + (incluir_reintegros.checked ? incluir_reintegros.value : 'false')										
					+'&incluir_movimientosbancarios=' + (incluir_movimientosbancarios.checked ? incluir_movimientosbancarios.value : 'false')
					+'&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
					+'&incluir_proveedores=' + (incluir_proveedores.checked ? incluir_proveedores.value : 'false')
					+'&incluir_obs_comp=' + (incluir_obs_comp.checked ? incluir_obs_comp.value : 'false')		
					<%if(portlet_name.equals("tesoreria")){%>										
						+'&incluir_liquidaciones=' + (incluir_liquidaciones.checked ? incluir_liquidaciones.value : 'false')											
					<%}%>
					+'&rnd=' + Math.floor(Math.random()*100)
					+'&exportar=' + (exportar.checked ? exportar.value : 'false');
				}
			}
		});
		
	});
	
	jQuery('#<portlet:namespace />exportarInterbanking').click(function(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		
		var tipoPago = jQuery("#<portlet:namespace />tipo_pago").val();
		var ctabcria = jQuery("#<portlet:namespace />id_cta_bcria").val();
		window.location.href ='/xlsservlet/?reporte=REPORTE_SUBDIARIO_EGRESOS_INTERBANKING'
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&tipopago='+tipoPago
			+'&ctabcria='+ctabcria;
	});

	
	
</script>

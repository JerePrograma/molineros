<%@ include file="/html/portlet/tesoreria/init.jsp" %>
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
		List<Banco> bancos = TraeListasServiceUtil.getBancos();

		List<CuentaBancaria> ctas=null;
		ctas = (ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysTesoreria.CUENTAS_BCRIAS,PortletSession.APPLICATION_SCOPE);
		if(null==ctas){
			ctas=TraeListasServiceUtil.getCtasBcrias();
			portletSession.setAttribute(WebKeysTesoreria.CUENTAS_BCRIAS, ctas, PortletSession.APPLICATION_SCOPE);
		}
		
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(new Date());
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
 		periodoHasta.setTime(new Date());
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="libro-caja-cheques" /></legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="fecha-vto-desde" />:</label></td>
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
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-vto-hasta" />:</label></td>
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)+3 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>						
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="depositados" />:</label></td>
						<td>
							<select id="<portlet:namespace/>depositados" name="<portlet:namespace/>depositados" onChange="javascript:cambiaDepositados();">
								<option value="-1">Todos</option>
								<option value="0">No Depositados</option>
								<option value="1">Depositados</option>
							</select>
						</td>
						<td id="tdFechaDepositoDesdeLabel"  style="display:none;"><label><liferay-ui:message key="fecha-deposito-desde" />:</label></td>
						<td id="tdFechaDepositoDesde" style="display:none;">
								<liferay-ui:input-date
									dayParam="fechaDepositoDesdeDia"
									dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
									dayNullable="<%= true %>" 
									monthParam="fechaDepositoDesdeMes"
									monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
									monthNullable="<%= true %>"				
									yearParam="fechaDepositoDesdeAnio"
									yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
									yearNullable="<%= true %>"
									yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
									firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
						</td>
							<td id="tdFechaDepositoHastaLabel" style="display:none;"><label><liferay-ui:message key="fecha-deposito-hasta" />:</label></td>
							<td id="tdFechaDepositoHasta" style="display:none;">
								<liferay-ui:input-date
									dayParam="fechaDepositoHastaDia"
									dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
									dayNullable="<%= true %>" 
									monthParam="fechaDepositoHastaMes"
									monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
									monthNullable="<%= true %>"				
									yearParam="fechaDepositoHastaAnio"
									yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
									yearNullable="<%= true %>"
									yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
									firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
							</td>
						
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="reemplazados" />:</label></td>
						<td>
							<select id="<portlet:namespace/>reemplazados" name="<portlet:namespace/>reemplazados" onChange="javascript:cambiaDepositados();">
								<option value="-1">Todos</option>
								<option value="0">No Reemplazados</option>
								<option value="1">Reemplazados</option>
							</select>
						</td>
						<td id="tdFechaReemplazoDesdeLabel" style="display:none;"><label><liferay-ui:message key="fecha-reemplazo-desde" />:</label></td>
						<td id="tdFechaReemplazoDesde" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaReemplazoDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaReemplazoDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaReemplazoDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td id="tdFechaReemplazoHastaLabel" style="display:none;"><label><liferay-ui:message key="fecha-reemplazo-hasta" />:</label></td>
						<td id="tdFechaReemplazoHasta" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaReemplazoHastaDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaReemplazoHastaMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaReemplazoHastaAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="rechazados" />:</label></td>
						<td>
							<select id="<portlet:namespace/>rechazados" name="<portlet:namespace/>rechazados" onChange="javascript:cambiaDepositados();">
								<option value="-1">Todos</option>
								<option value="0">No Rechazados</option>
								<option value="1">Rechazados</option>
							</select>
						</td>
						<td id="tdFechaRechazoDesdeLabel" style="display:none;"><label><liferay-ui:message key="fecha-rechazo-desde" />:</label></td>
						<td id="tdFechaRechazoDesde" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaRechazoDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaRechazoDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaRechazoDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td id="tdFechaRechazoHastaLabel" style="display:none;"><label><liferay-ui:message key="fecha-rechazo-hasta" />:</label></td>
						<td id="tdFechaRechazoHasta" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaRechazoHastaDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaRechazoHastaMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaRechazoHastaAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					
					<tr>
						<td><label>Judicializados:</label></td>
						<td>
							<select id="<portlet:namespace/>judicializados" name="<portlet:namespace/>judicializados" onChange="javascript:cambiaDepositados();">
								<option value="-1">Todos</option>
								<option value="0">No Judicializados</option>
								<option value="1">Judicializados</option>
							</select>
						</td>
						<td id="tdFechaJudicialDesdeLabel" style="display:none;"><label>Judicializado Desde:</label></td>
						<td id="tdFechaJudicialDesde" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaJudicialDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaJudicialDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaJudicialDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td id="tdFechaJudicialHastaLabel" style="display:none;"><label>Judicializado Hasta:</label></td>
						<td id="tdFechaJudicialHasta" style="display:none;">
							<liferay-ui:input-date
								dayParam="fechaJudicialHastaDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaJudicialHastaMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaJudicialHastaAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					
					<tr>
						<td>
							<label><liferay-ui:message key="fecha-recibo-desde" />:</label>
						</td>
						<td colspan="5">						
						<liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaReciboDesdeDia"
										monthParam="fechaReciboDesdeMes"
										yearParam="fechaReciboDesdeAnio"
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
							&nbsp;&nbsp;
						
						<label><liferay-ui:message key="fecha-recibo-hasta" />:</label>
						&nbsp;&nbsp;
						<span id="recep"><liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaReciboHastaDia"
										monthParam="fechaReciboHastaMes"
										yearParam="fechaReciboHastaAnio"
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" /></span></td>		
						</td>
						
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>	
						<td colspan="6">
							<table width="100%">						
								<td>
									<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
								  	<liferay-util:param name="esEditable" value='true'/>
								  		<liferay-util:param name="portlet_name" value='tesoreria'/>
								  		<liferay-util:param name="soloOP" value='false'/>								  		
									</liferay-util:include>
								</td>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">
							<table>
								<td><label><liferay-ui:message key="bancos" />:</label></td>
								<td>
									<select name="<portlet:namespace/>id_banco" id="<portlet:namespace/>id_banco">
										<option value="-1">Todos</option>									
										<% for (Banco bco : bancos) { %>
												<option value="<%= bco.getId_banco()%>"><%=bco.getDescripcion_banco()%></option>											
										<% } %>
									</select>
								</td>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">
							<table>
							  <tr>
								<td><label><liferay-ui:message key="cuenta-bancaria" />:</label></td>
								<td>
									<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
									    <option value="-1">Todas</option>
										<%
										for (CuentaBancaria ctaBcria : ctas) {
											if(portlet_name.equals("farmacia") && ctaBcria.getEntidad().equals("A")){
										%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
										<%  }else if(portlet_name.equals("tesoreria") && ctaBcria.getEntidad().equals("O")){%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>
										<%  }else if(portlet_name.equals("uoma") && ctaBcria.getEntidad().equals("U")){%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>
										<%  }
										}
										%>
									</select>
								</td>
							   </tr> 	
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="numero-cheque" />:</label></td>
						<td colspan="5">
							<input " type="text" id="<portlet:namespace />nro_cheque" name="<portlet:namespace />nro_cheque" size="20" maxlength="20" value=''/>
						</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
					     <td>
						   <label>Formato para Procesar </label>
						</td>
						<td>
						  <input type="checkbox" name="<portlet:namespace />formato" id="<portlet:namespace />formato"  checked/>
						</td>  
					  
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="6">
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
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
			<div align="center" id="<portlet:namespace />busquedaMovimientoDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
function cambiaDepositados(){
	var depositados=jQuery("#<portlet:namespace/>depositados").val();	
	var reemplazados=jQuery("#<portlet:namespace/>reemplazados").val();
	var rechazados=jQuery("#<portlet:namespace/>rechazados").val();
	var judicializados=jQuery("#<portlet:namespace/>judicializados").val();
		
	if(reemplazados=="-1"){
		document.getElementById("tdFechaReemplazoDesdeLabel").style.display = 'none';
		document.getElementById("tdFechaReemplazoDesde").style.display = 'none';
		document.getElementById("tdFechaReemplazoHastaLabel").style.display = 'none';
		document.getElementById("tdFechaReemplazoHasta").style.display = 'none';	
	}else{
		document.getElementById("tdFechaReemplazoDesdeLabel").style.display = 'inline';
		document.getElementById("tdFechaReemplazoDesde").style.display = 'inline';
		document.getElementById("tdFechaReemplazoHastaLabel").style.display = 'inline';
		document.getElementById("tdFechaReemplazoHasta").style.display = 'inline';
	}

	if(rechazados=="-1"){
		document.getElementById("tdFechaRechazoDesdeLabel").style.display = 'none';
		document.getElementById("tdFechaRechazoDesde").style.display = 'none';
		document.getElementById("tdFechaRechazoHastaLabel").style.display = 'none';
		document.getElementById("tdFechaRechazoHasta").style.display = 'none';
	}else{
		document.getElementById("tdFechaRechazoDesdeLabel").style.display = 'inline';
		document.getElementById("tdFechaRechazoDesde").style.display = 'inline';
		document.getElementById("tdFechaRechazoHastaLabel").style.display = 'inline';
		document.getElementById("tdFechaRechazoHasta").style.display = 'inline';
	}
	
	if(depositados=="-1"){
		document.getElementById("tdFechaDepositoDesdeLabel").style.display = 'none';
		document.getElementById("tdFechaDepositoDesde").style.display = 'none';
		document.getElementById("tdFechaDepositoHastaLabel").style.display = 'none';
		document.getElementById("tdFechaDepositoHasta").style.display = 'none';
	
	}else{		
		document.getElementById("tdFechaDepositoDesdeLabel").style.display = 'inline';
		document.getElementById("tdFechaDepositoDesde").style.display = 'inline';
		document.getElementById("tdFechaDepositoHastaLabel").style.display = 'inline';
		document.getElementById("tdFechaDepositoHasta").style.display = 'inline';
	}
	
	if(judicializados=="-1"){
		document.getElementById("tdFechaJudicialDesdeLabel").style.display = 'none';
		document.getElementById("tdFechaJudicialDesde").style.display = 'none';
		document.getElementById("tdFechaJudicialHastaLabel").style.display = 'none';
		document.getElementById("tdFechaJudicialHasta").style.display = 'none';
	}else{
		document.getElementById("tdFechaJudicialDesdeLabel").style.display = 'inline';
		document.getElementById("tdFechaJudicialDesde").style.display = 'inline';
		document.getElementById("tdFechaJudicialHastaLabel").style.display = 'inline';
		document.getElementById("tdFechaJudicialHasta").style.display = 'inline';
	}
	
	
}
function <portlet:namespace />buscarMovimientos(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaDesdeMes").val())+1;
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var fecha_vto_desde=desde_anio+'/'+desde_mes+'/'+desde_dia;
	
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaHastaMes").val())+1;
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();

	var fecha_vto_hasta=hasta_anio+'/'+hasta_mes+'/'+hasta_dia;

	var deposito_desde_dia=jQuery("#<portlet:namespace/>fechaDepositoDesdeDia").val();	
	var deposito_desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaDepositoDesdeMes").val())+1;
	var deposito_desde_anio=jQuery("#<portlet:namespace/>fechaDepositoDesdeAnio").val();

	var fecha_deposito_desde=deposito_desde_anio+'/'+deposito_desde_mes+'/'+deposito_desde_dia;
	
	var deposito_hasta_dia=jQuery("#<portlet:namespace/>fechaDepositoHastaDia").val();	
	var deposito_hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaDepositoHastaMes").val())+1;
	var deposito_hasta_anio=jQuery("#<portlet:namespace/>fechaDepositoHastaAnio").val();

	var fecha_deposito_hasta=deposito_hasta_anio+'/'+deposito_hasta_mes+'/'+deposito_hasta_dia;

	var reemplazo_desde_dia=jQuery("#<portlet:namespace/>fechaReemplazoDesdeDia").val();	
	var reemplazo_desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaReemplazoDesdeMes").val())+1;
	var reemplazo_desde_anio=jQuery("#<portlet:namespace/>fechaReemplazoDesdeAnio").val();

	var fecha_reemplazo_desde=reemplazo_desde_anio+'/'+reemplazo_desde_mes+'/'+reemplazo_desde_dia;
	
	var reemplazo_hasta_dia=jQuery("#<portlet:namespace/>fechaReemplazoHastaDia").val();	
	var reemplazo_hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaReemplazoHastaMes").val())+1;
	var reemplazo_hasta_anio=jQuery("#<portlet:namespace/>fechaReemplazoHastaAnio").val();

	var fecha_reemplazo_hasta=reemplazo_hasta_anio+'/'+reemplazo_hasta_mes+'/'+reemplazo_hasta_dia;

	var rechazo_desde_dia=jQuery("#<portlet:namespace/>fechaRechazoDesdeDia").val();	
	var rechazo_desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaRechazoDesdeMes").val())+1;
	var rechazo_desde_anio=jQuery("#<portlet:namespace/>fechaRechazoDesdeAnio").val();

	var fecha_rechazo_desde=rechazo_desde_anio+'/'+rechazo_desde_mes+'/'+rechazo_desde_dia;
	
	var rechazo_hasta_dia=jQuery("#<portlet:namespace/>fechaRechazoHastaDia").val();	
	var rechazo_hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaRechazoHastaMes").val())+1;
	var rechazo_hasta_anio=jQuery("#<portlet:namespace/>fechaRechazoHastaAnio").val();

	var fecha_rechazo_hasta=rechazo_hasta_anio+'/'+rechazo_hasta_mes+'/'+rechazo_hasta_dia;
	
	var recibo_desde_dia=jQuery("#<portlet:namespace/>fechaReciboDesdeDia").val();	
	var recibo_desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaReciboDesdeMes").val())+1;
	var recibo_desde_anio=jQuery("#<portlet:namespace/>fechaReciboDesdeAnio").val();

	var fecha_recibo_desde=recibo_desde_anio+'/'+recibo_desde_mes+'/'+recibo_desde_dia;
	
	var recibo_hasta_dia=jQuery("#<portlet:namespace/>fechaReciboHastaDia").val();	
	var recibo_hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaReciboHastaMes").val())+1;
	var recibo_hasta_anio=jQuery("#<portlet:namespace/>fechaReciboHastaAnio").val();

	var fecha_recibo_hasta=recibo_hasta_anio+'/'+recibo_hasta_mes+'/'+recibo_hasta_dia;
	
	
	var judicial_desde_dia=jQuery("#<portlet:namespace/>fechaJudicialDesdeDia").val();	
	var judicial_desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaJudicialDesdeMes").val())+1;
	var judicial_desde_anio=jQuery("#<portlet:namespace/>fechaJudicialDesdeAnio").val();

	var fecha_judicial_desde=judicial_desde_anio+'/'+judicial_desde_mes+'/'+judicial_desde_dia;
	
	var judicial_hasta_dia=jQuery("#<portlet:namespace/>fechaJudicialHastaDia").val();	
	var judicial_hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaJudicialHastaMes").val())+1;
	var judicial_hasta_anio=jQuery("#<portlet:namespace/>fechaJudicialHastaAnio").val();

	var fecha_judicial_hasta=judicial_hasta_anio+'/'+judicial_hasta_mes+'/'+judicial_hasta_dia;
	
	var id_banco=jQuery("#<portlet:namespace/>id_banco").val();

	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();

	var depositados=jQuery("#<portlet:namespace/>depositados").val();	
	var rechazados=jQuery("#<portlet:namespace/>rechazados").val();
	var reemplazados=jQuery("#<portlet:namespace/>reemplazados").val();
	var judicializados=jQuery("#<portlet:namespace/>judicializados").val();
	
	var cta_bcria=jQuery("#<portlet:namespace />id_cta_bcria").val();
	
	var nro_cheque=jQuery("#<portlet:namespace />nro_cheque").val();	
	
	var formato=jQuery("#<portlet:namespace />formato").is(':checked');
	
	var url = '/xlsservlet/?reporte=LISTADO_CHEQUES' 
		+ '&fechaVtoDesde=' +fecha_vto_desde		
		+ '&fechaVtoHasta=' +fecha_vto_hasta
		+ '&fechaDepositoDesde=' +fecha_deposito_desde
		+ '&fechaDepositoHasta=' +fecha_deposito_hasta
		+ '&fechaReemplazoDesde=' +fecha_reemplazo_desde
		+ '&fechaReemplazoHasta=' +fecha_reemplazo_hasta
		+ '&fechaRechazoDesde=' +fecha_rechazo_desde
		+ '&fechaRechazoHasta=' +fecha_rechazo_hasta	
		+ '&fechaReciboDesde=' +fecha_recibo_desde
		+ '&fechaReciboHasta=' +fecha_recibo_hasta		
		+ '&cuit_entidad=' +cuit_entidad		
		+ '&id_seccional=' +id_seccional
		+ '&id_banco=' + id_banco
		+ '&depositados=' +depositados
		+ '&rechazados=' +rechazados
		+ '&reemplazados=' + reemplazados
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
		+ '&nro_cheque=' + nro_cheque		
		+ '&id_cta_bcria=' + cta_bcria
		+ '&formato='+formato
		+ '&fechaJudicialDesde=' +fecha_judicial_desde
		+ '&fechaJudicialHasta=' +fecha_judicial_hasta
		+ '&judicializados=' +judicializados;
		
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}

function cambiaCuit(){
}
	
</script>
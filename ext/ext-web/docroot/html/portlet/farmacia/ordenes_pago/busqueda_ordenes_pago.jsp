<%@ page import="ar.com.ospim.global.services.ChequesReutilizadosException" %>
<%@ page import="ar.com.ospim.global.services.ComprobantesYaPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposUtilizadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.ComprobantesAnuladosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposNoPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposReUtilizadosException" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
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

Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(new Date());

boolean showABMButtons = true; //PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ORDEN_PAGO_AMTIMA);
%>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
	<liferay-ui:error exception="<%= AnticiposUtilizadosException.class %>" message="exception-anticipos_utilizados" />
	<liferay-ui:error exception="<%= ChequesReutilizadosException.class %>" message="exception-cheques-reutilizados" />
	<liferay-ui:error exception="<%= ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados" />
	<liferay-ui:error exception="<%= ComprobantesAnuladosException.class %>" message="exception-comprobantes-anulados" />
	<liferay-ui:error exception="<%= AnticiposNoPagadosException.class %>" message="exception-anticipos-no-pagados" />
	<liferay-ui:error exception="<%= FechaBajaMenorQueAltaException.class %>" message="exception-fecha-baja-menor-que-alta" />
	<liferay-ui:error exception="<%= AnticiposReUtilizadosException.class %>" message="exception-anticipos-reutilizados" />
	<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="baja-menor-fecha-contable" />
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-ordenes-pago" /></legend>
				<table class="lfr-table" width="100%">
					<tr>
						<td colspan="8">
							<label><liferay-ui:message key="numero" />:</label>&nbsp;&nbsp;<input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="15" maxlength="11" type="text" value="" />
							&nbsp;&nbsp;						
							<label><liferay-ui:message key="cheque-nro" />:</label>&nbsp;
							<input id="<portlet:namespace />cheque_numero" name="<portlet:namespace />cheque_numero" size="15" maxlength="11" type="text" value="" />
						</td>						
					</tr>					
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>				
					<tr>
						<td colspan="6">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
					  		<liferay-util:param name="portlet_name" value='farmacia'/>
						</liferay-util:include>
						<td colspan="2">&nbsp;</td>				
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
		<td colspan="8"><label><liferay-ui:message key="fecha-desde" />:</label>
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
						disabled="<%= false %>" /></span></td>		
		</td>		
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>	
						<td colspan="8" align="center">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
							&nbsp;&nbsp;
							<% if(showABMButtons) { %>
								<input type="button" value="<liferay-ui:message key="alta-orden-pago" />" onClick="<portlet:namespace />altaOP();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>					
				</table>	      	  
		</fieldset>
		
</form>	
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
			<div align="center" id="<portlet:namespace />busquedaChequeDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var numero=jQuery('#<portlet:namespace />numero').val();
		var chequeNumero=jQuery('#<portlet:namespace />cheque_numero').val();
		
		var fechaEmisionDesdeDia=jQuery('#<portlet:namespace />fechaEmisionDesdeDia').val();
		var fechaEmisionDesdeMes=jQuery('#<portlet:namespace />fechaEmisionDesdeMes').val();
		var fechaEmisionDesdeAnio=jQuery('#<portlet:namespace />fechaEmisionDesdeAnio').val();
		
		var fechaEmisionHastaDia=jQuery('#<portlet:namespace />fechaEmisionHastaDia').val();
		var fechaEmisionHastaMes=jQuery('#<portlet:namespace />fechaEmisionHastaMes').val();
		var fechaEmisionHastaAnio=jQuery('#<portlet:namespace />fechaEmisionHastaAnio').val();
		
		var cuit=jQuery("#<portlet:namespace />cuit_entidad").val();
		var sucu=jQuery("#<portlet:namespace />sucursal_entidad").val();
		
		if(!<portlet:namespace />validarBusqueda(numero,chequeNumero, cuit, fechaEmisionDesdeMes, fechaEmisionHastaMes )){
			return false;
		}
		alert(fechaEmisionDesdeDia+'-'+fechaEmisionDesdeMes+'-'+fechaEmisionDesdeAnio);
		alert(fechaEmisionHastaDia+'-'+fechaEmisionHastaMes+'-'+fechaEmisionHastaAnio);
		alert('CUIT= '+cuit);
		alert('sucu= '+sucu);
		
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/buscar_ordenes_pago&numero='+numero+'&cheque_numero='+chequeNumero;
		url=url+'&desdeDia='+fechaEmisionDesdeDia+'&desdeMes='+fechaEmisionDesdeMes+'&desdeAnio='+fechaEmisionDesdeAnio;
		url=url+'&hastaDia='+fechaEmisionHastaDia+'&hastaMes='+fechaEmisionHastaMes+'&hastaAnio='+fechaEmisionHastaAnio;
		url=url+'&cuit='+cuit+'&sucu='+sucu;
		jQuery('#<portlet:namespace />busquedaChequeDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(numero,chequeNumero, cuit, fechaDesde, fechaHasta){	
		if(trim(numero.length)==0 && trim(chequeNumero.length)==0 && trim(cuit.length)==0 && trim(fechaDesde.length)==0 && trim(fechaHasta.length)==0){	
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaOP() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmacia/editar_orden_pago_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
	function <portlet:namespace />altaOPFromLista(){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmacia/editar_orden_pago_amtima_from_reintegros" /></portlet:actionURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />uploadOrdenPago() {			
		url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/farmacia/upload_ordenes_pago' /></portlet:actionURL>";			
		submitForm(document.<portlet:namespace />fmUpload, url);				
		return true;
	}

<%if (ini != null && fin!=null){%>
		alert("Se generaron las OP del:<%= ini.toString()%> al <%= fin.toString()%>");
		window.location.href ="/pdfservlet/?accion=ordenPagoFarmacia&id_orden_pago_ini=<%= ini.toString()%>&id_orden_pago_fin=<%= fin.toString()%>";
<%}%>
</script>

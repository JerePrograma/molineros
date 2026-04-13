<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		PortletURL portletURL = renderResponse.createRenderURL();
		
		boolean showOpcionesAuditor = !PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) && PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO); 
			
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);

		//verificar los calendars
 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		fechaHasta.setTime(new Date());
 		
 		List<String> alta_usrs = (ArrayList<String>) portletSession
		.getAttribute(WebKeysLiquidaciones.ALTA_USR_REINTEGROS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

		if (alta_usrs == null) {
			alta_usrs = TraeListasServiceUtil.getUsuariosAltaReintegros();
			portletSession.setAttribute(
					WebKeysLiquidaciones.ALTA_USR_REINTEGROS_EN_SESSION,
					alta_usrs,
					PortletSession.APPLICATION_SCOPE);	
		}
%>
		<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">		
		<fieldset class="block-labels">
				<legend>Ficha de Consumo</legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td colspan="2">
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
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 20 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td colspan="2">
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
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 20 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="8">
							<fieldset class="block-labels"><legend><liferay-ui:message
								key="datos-afiliado" /></legend>
							<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
							<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
							</liferay-util:include>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>
						<td><label>Prestador:</label></td>
						<td colspan="5">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="cuit" value=''/>
						  		<liferay-util:param name="sucu" value=''/>
						  		<liferay-util:param name="razon" value=''/>
						  		<liferay-util:param name="id_seccional" value=''/>
								<liferay-util:param name="esEmpresaPrestador" value='true'/>
							</liferay-util:include>
						</td>
						<td><label>Cod. Prestación:</label></td>													
						<td><input id="<portlet:namespace />codPrest" name="<portlet:namespace />codPrest" size="7" maxlength="7" type="text" value="" /></td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>			
					<tr>
						<td colspan="8">
							<table class="lfr-table">
								<tr>				
									<td style="background-color:#AEB6BF">
									  <label>Liquidación Prestaciones:</label>
									   <input type="checkbox" id="<portlet:namespace />liquidaciones" name="<portlet:namespace />liquidaciones" value="1" checked="checked" />
									</td>
									<td></td>
									
									
									<td style="background-color:#AEB6BF">
									<label>Reint. Prestacional:</label>
									  <input type="checkbox" id="<portlet:namespace />prestacional" name="<portlet:namespace />prestacional" value="1" checked="checked"/>
									</td>
									<td></td>
									
									<td style="background-color:#AEB6BF">
									  <label>Reint. Odontología General:</label>
									  <input type="checkbox" id="<portlet:namespace />odonto_gral" name="<portlet:namespace />odonto_gral" value="1" checked="checked"/>
									</td>
									<td></td>				
									
									<td style="background-color:#AEB6BF">
									  <label>Reint. Prótesis Odontológica:</label>
									  <input type="checkbox" id="<portlet:namespace />protesis" name="<portlet:namespace />protesis" value="1" checked="checked"/>
								    </td>
									<td></td>
									
									<td style="background-color:#AEB6BF">
									  <label>Reint. Ortopedia/Ortodoncia Odo.:</label>
									  <input type="checkbox" id="<portlet:namespace />ortopedia" name="<portlet:namespace />ortopedia" value="1" checked="checked"/>
									</td>
									<td></td>							
									<td style="background-color:#AEB6BF">
									   <label>Reint. Farmacia.:</label>
									   <input type="checkbox" id="<portlet:namespace />farmacia" name="<portlet:namespace />farmacia" value="1" checked="checked"/>
									</td>
									<td></td>
									
									<td style="background-color:#AEB6BF">
									  <label>Liq. Farmacia.:</label>
									  <input type="checkbox" id="<portlet:namespace />farmacia_liq" name="<portlet:namespace />farmacia_liq" value="1" checked="checked"/>
									</td>
									<td></td>
									<td style="background-color:#AEB6BF">
									  <label>Pre-autorizaciones:</label>
									  <input type="checkbox" id="<portlet:namespace />pre_autoriz" name="<portlet:namespace />pre_autoriz" value="1" checked="checked"/>
									</td>
									<td></td>
									<td>
									 <!--   <label>Resp. Prevención: </label> -->
									  <input type="checkbox" id="<portlet:namespace />rta_prevencion" name="<portlet:namespace />rta_prevencion" value="1" checked="checked"/>
									</td>
									<td></td>
								</tr>
							</table>
						</td>		
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>
					<tr>						
						<td colspan="8">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>		
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">					
		</fieldset>
	</form>		
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />rta_prevencion').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var periodoDesdeMesAnio='';
		var periodoHastaMesAnio='';
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();		
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var codPrestaci=jQuery('#<portlet:namespace />codPrest').val();	
		var pagos = '1';
		var boolpresta=jQuery('#<portlet:namespace />prestacional').is(':checked');
		var presta = "0";
		if (boolpresta == true) {
			presta = "1";
		}
		var boolortop=jQuery('#<portlet:namespace />ortopedia').is(':checked');
		var ortop = "0";
		if (boolortop == true) {
			ortop = "1";
		}
		var boolprot=jQuery('#<portlet:namespace />protesis').is(':checked');
		var protesis = "0";
		if (boolprot == true) {
			protesis = "1";
		}
		var odonto_gral=jQuery('#<portlet:namespace />odonto_gral').is(':checked');
		var odontogral = "0";
		if (odonto_gral == true) {
			odontogral = "1";
		}
		
		var farmacia_check=jQuery('#<portlet:namespace />farmacia').is(':checked');
		var farmacia = "0";
		if (farmacia_check == true) {
			farmacia = "1";
		}
		var farmacia_check=jQuery('#<portlet:namespace />farmacia_liq').is(':checked');
		var farmacia_liq="0";
		if (farmacia_check == true) {
			farmacia_liq = "1";
		}
		var liquida=jQuery('#<portlet:namespace />liquidaciones').is(':checked');
		var liquidac = "0";
		if (liquida == true) {
			liquidac = "1";
		}	
		var pre_auto_check=jQuery('#<portlet:namespace />pre_autoriz').is(':checked');
		var pre_autoriz="0";
		if (pre_auto_check == true) {
			pre_autoriz = "1";
		}
		var rta_prevencion_check=jQuery('#<portlet:namespace />rta_prevencion').is(':checked');
		var rta_prevencion="0";
		if (rta_prevencion_check == true) {
			rta_prevencion = "1";
		}
		
		
		var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad").value;
		var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad").value;
		
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro:
		if(jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
			jQuery("#<portlet:namespace />seccional_r").val("");
			jQuery("#<portlet:namespace />id_seccional_r").val("");
		}
				
		window.location.href ='/xlsservlet/?reporte=FICHA_DE_CONSUMO'+'&entidad='+entidad+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&codPrest='+codPrestaci+'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&cuit='+cuit_entidad+'&sucursal='+sucursal_entidad+
		'&presta='+presta+'&ortop='+ortop+'&protesis='+protesis+'&odontogral='+odontogral+
		'&liquidaciones='+liquidac+'&farmacia='+farmacia+'&liq_farmacia='+farmacia_liq+
		'&pre_autoriz='+pre_autoriz+'&rta_prevencion='+rta_prevencion;
		
	});
	
	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeDia').val("");
		jQuery('#<portlet:namespace />fechaDesdeMes').val("");		
		jQuery('#<portlet:namespace />fechaDesdeAnio').val("");
		jQuery('#<portlet:namespace />fechaHastaDia').val("");
		jQuery('#<portlet:namespace />fechaHastaMes').val("");
		jQuery('#<portlet:namespace />fechaHastaAnio').val("");			
	}
	
	<portlet:namespace />initDateFields();

	function cambiaCuit(){
	}
		
</script>
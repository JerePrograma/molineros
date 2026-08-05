<%@ include file="/html/portlet/farmacia/init.jsp"%>
<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
	}
	 
	String con_reclamo_prestacional = String.valueOf(request.getAttribute("con_reclamo_prestacional")!=null?request.getAttribute("con_reclamo_prestacional"):0);
	
	String viewStr = (String) request
			.getAttribute(WebKeysFarmacia.VIEW_REINTEGRO);
	String edit_mode = "true";
	boolean esView = false;
	if (viewStr != null) {
		esView = true;
		edit_mode = "false";
	}

	ReintegroMedicamento reintegro = (ReintegroMedicamento) request
			.getAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION);
	Afiliado afiliado = Validator.isNotNull(reintegro) ? reintegro
			.getAfiliado() : null;



				
	boolean showABMButtons = PermissionUtil.userContainsRole(user,
			WebKeysFarmacia.ROL_ABM_FARMACIA);

	Date fechaReintegro = null;
	Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
	fechaReintegro = Validator.isNotNull(reintegro) ? reintegro
			.getFecha() : null;
	if (fechaReintegro == null) {
		fechaHoy.setTime(new Date());
	} else {
		fechaHoy.setTime(reintegro.getFecha());
	}

	Date periodoReintegro = null;
	Calendar periodo = CalendarFactoryUtil.getCalendar();
	periodoReintegro = Validator.isNotNull(reintegro) ? reintegro
			.getPeriodo() : null;
	if (periodoReintegro == null) {
		periodo.setTime(new Date());
	} else {
		periodo.setTime(reintegro.getPeriodo());
	}

	String error = (String) request
			.getAttribute(WebKeysFarmacia.ERROR_PARA_ALERT);
%>

<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem"%><form
	action="" method="post" name="<portlet:namespace />fm"><input
	name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />
<input type="hidden" id="<portlet:namespace />fprest"
	name="<portlet:namespace />fprest" value="" />

<input type="hidden" id="<portlet:namespace />con_reclamo_prestacional" name="<portlet:namespace />con_reclamo_prestacional" value="<%=con_reclamo_prestacional%>" />

<input type="hidden" id="<portlet:namespace />id_reclamo_prestacional" 	name="<portlet:namespace />id_reclamo_prestacional" value="" />
    
<input type="hidden" id="<portlet:namespace />id_prestacion_reclamo_prestacional" 	name="<portlet:namespace />id_prestacion_reclamo_prestacional" value="" />
<input type="hidden" id="<portlet:namespace />botonprestacionesreclamo" 	name="<portlet:namespace />botonprestacionesreclamo" value="" />
<input type="hidden" id="<portlet:namespace />importeoriginalreclamo" name="<portlet:namespace />importeoriginalreclamo" value="" />
<input type="hidden" id="<portlet:namespace />importereclamo" name="<portlet:namespace />importereclamo" value="" />		
<input type="hidden" id="<portlet:namespace />monto_cober_prestadora_aux" name="<portlet:namespace />monto_cober_prestadora_aux" value="0.00" />
<input type="hidden" id="<portlet:namespace />cargo_imesa" name="<portlet:namespace />cargo_imesa" value="0.00" />	

<input type="hidden" id="<portlet:namespace />cbu" name="<portlet:namespace />cbu" value="" />
<input type="hidden" id="<portlet:namespace />cuil_cuenta" name="<portlet:namespace />cuil_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />email_cuenta" name="<portlet:namespace />email_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />apellido_cuenta" name="<portlet:namespace />apellido_cuenta" value="" />
<input type="hidden" id="<portlet:namespace />nombre_cuenta" name="<portlet:namespace />nombre_cuenta" value="" />

<liferay-ui:error exception="<%= ValidaExistePrestacion.class %>" 	message="existe-prestacion-reintegro" />	

	
<fieldset class="block-labels"><legend><liferay-ui:message
	key="carga-reintegro-farmacia" /></legend>
<table class="lfr-table" width="100%">
	<tr>
		<td colspan="10">
		<table>
			<tr>
				<td><label><liferay-ui:message
					key="seccional-reintegro" />:</label></td>
				<td colspan="4" style="vertical-align: top">
					<%if(portlet_name.equals("liquidaciones")){%>
						<liferay-util:include page='/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp'>
						<liferay-util:param name="id_seccional_r"
							value='<%= reintegro != null && reintegro.getSeccional() != null ? String.valueOf(reintegro.getSeccional().getId()) : ""%>' />
						<liferay-util:param name="seccional_r"
							value='<%= reintegro != null && reintegro.getSeccional() != null ? reintegro.getSeccional().getDescripcion() : ""%>' />
						</liferay-util:include>
					<%}else{%>
						<liferay-util:include page='/html/portlet/farmacia/busqueda_seccional_reintegro.jsp'>
						<liferay-util:param name="id_seccional_r"
							value='<%= reintegro != null && reintegro.getSeccional() != null ? String.valueOf(reintegro.getSeccional().getId()) : ""%>' />
						<liferay-util:param name="seccional_r"
							value='<%= reintegro != null && reintegro.getSeccional() != null ? reintegro.getSeccional().getDescripcion() : ""%>' />
						</liferay-util:include>
					<%}%>
				</td>
				<td><label><liferay-ui:message key="numero-reintegro" />:</label></td>
				<td><input id="<portlet:namespace />numero"
					name="<portlet:namespace />numero" size="8" maxlength="8"
					type="text"
					value="<%=Validator.isNotNull(reintegro) ? reintegro
					.getId_reintegroString() : ""%>"
					readonly='readonly' /> <input
					id="<portlet:namespace />id_reintegro"
					name="<portlet:namespace />id_reintegro" size="8" maxlength="8"
					type="hidden"
					value="<%=Validator.isNotNull(reintegro) ? reintegro
					.getId_reintegro() : ""%>"
					readonly='readonly' /></td>
			</tr>
			<tr>
				<td colspan="10">&nbsp;</td>
			</tr>
			<tr>
				<td><label><liferay-ui:message key="date" />:</label></td>
				<td colspan="4"><liferay-ui:input-date dayParam="fechaDia"
					dayValue="<%= fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMes"
					monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
					yearParam="fechaAnio"
					yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
					yearRangeStart="<%= (fechaHoy.get(Calendar.MONTH) >= 3) ? fechaHoy.get(Calendar.YEAR) : fechaHoy.get(Calendar.YEAR) - 1 %>"
					yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) %>"
					firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
					disabled="<%=esView %>" /></td>
				<td><label><liferay-ui:message key="periodo" />:</label></td>
				<td colspan="4"><liferay-ui:input-date dayParam="periodoDia"
					dayNullable="<%= true %>" dayValue=""
					monthAndYearParam="periodoMesAnio"
					monthValue="<%= periodo.get(Calendar.MONTH) %>"
					monthAndYearNullable="<%= true %>"
					yearValue="<%= periodo.get(Calendar.YEAR) %>"
					yearRangeStart="<%= (fechaHoy.get(Calendar.MONTH) >= 3) ? fechaHoy.get(Calendar.YEAR) - 5: fechaHoy.get(Calendar.YEAR) - 5 %>"
					yearRangeEnd="<%= periodo.get(Calendar.YEAR) %>"
					firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" /> <%
 	if (Validator.isNotNull(reintegro)
 			&& reintegro.getId_reintegro() != 0) {
 %>
				<input type="hidden" name="<portlet:namespace />periodoHidden"
					value="<%=periodo.get(Calendar.MONTH) + "/"
						+ periodo.get(Calendar.YEAR)%>" />
				<%
					}
				%>
				</td>
			</tr>
		</table>
		</td>
	</tr>

	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="10">
		<fieldset class="block-labels"><legend> <liferay-ui:message
			key="datos-afiliado" /></legend>
		<div id="loadAfiliado">			
				<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%= edit_mode %>" name="edit_mode" />
				<liferay-util:param name="cuil"
					value='<%= reintegro != null && reintegro.getAfiliado() != null ?  reintegro.getAfiliado().getCuil_titular() : ""%>' />
				<liferay-util:param name="inte"
					value='<%= reintegro != null && reintegro.getAfiliado() != null ? String.valueOf(reintegro.getAfiliado().getInte()) : ""%>' />	
				
				<liferay-util:param name="pag_reintegro_farmacia_reclamo" value='1' />
				<liferay-util:param name="pag_reintegro_farmacia" value='1' />
				</liferay-util:include>			
		</div>		
		</fieldset>
		</td>
	</tr>
	</table>
</fieldset>

<div id="<portlet:namespace />div_boton_reclamos_prestaciones">
		<input type="button" value="Ver Prestaciones del Reclamo Prestacional"
			onClick="<portlet:namespace />ver_prestaciones_reclamos();return false;" />
</div>
<div id="<portlet:namespace />div_boton_cancelar_reclamos_prestaciones">			
		<input type="button" value="Cancelar Ingreso de Prestacion del Reclamo"
			onClick="<portlet:namespace />limpiarCamposMedicamento();<portlet:namespace />cancelar_prestaciones_reclamos();return false;" />	
</div>	
<div id="<portlet:namespace />div_label_prestacion_reclamo">
<span  style="font-size:125%"  "> <b>Editando Prestacion de Reclamo de Afiliado</b></b></span>
</div>

<div align="center" id="<portlet:namespace />div_boton_oculta_reclamos_prestaciones">
		<input type="button" value="Oculta Prestaciones del Reclamo Prestacional" onClick="<portlet:namespace />oculta_prestaciones_reclamos();" />
		<br><br>
		<span style="font-size:155%" ><b>Listado de Prestaciones de Farmacia de Reclamos del Afiliado</b></span>
</div>	

<div id="<portlet:namespace />div_reclamos_prestaciones">
<fieldset class="block-labels"><legend><liferay-ui:message
	key="Prestaciones de los Reclamos del Afiliado" /></legend>


<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
    <liferay-util:include page="/html/portlet/farmacia/reintegros/reintegro_prestaciones_reclamos_search_result.jsp">	</liferay-util:include>
	</tr>
<tr>
<td>

</td>
</tr>
</table>

</fieldset>
</div>


<table>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="10">
		<fieldset class="block-labels"><legend> <liferay-ui:message
			key="datos-medicamento" /></legend>
		<div id="loadMedicamento"><liferay-util:include
			page='/html/portlet/farmacia/busqueda_medicamento.jsp'>
			<liferay-util:param
				value="<%= esView ? String.valueOf(false) : String.valueOf(true)%>"
				name="edit_mode" />
		</liferay-util:include></div>
		</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
</table>


<table>
	<tr>
		<%
			if (!esView && showABMButtons) {
		%>
		<td><input type="submit"
			value="<liferay-ui:message key="save" />"
			onClick="<portlet:namespace />saveReintegro();return false;" /></td>
										
		<td colspan="1">&nbsp;</td>							
		<td colspan="1">
			<input type="button" value="<liferay-ui:message key="alta-reintegro" />" onClick="<portlet:namespace />altaReintegro();" />
		</td>			
		<%
			}
		%>
		<td colspan="9">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="10">
		<div align="center" id="<portlet:namespace />cant_prestacion_afiliado">
		</div>
		<div align="center" id="<portlet:namespace />buscandoPrestaciones">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center"><img
					alt="<liferay-ui:message key='buscando'/>"
					src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
		</div>
	</tr>
</table>

</form>
<script><!--
 

		function <portlet:namespace />hideDayFieldOfPeriodFields () {
			jQuery("#<portlet:namespace />periodoDia").hide();
			<%if (!Validator.isNotNull(reintegro)) {%>
			jQuery('#<portlet:namespace />periodoMesAnio').val("");
			<%}%>
		}

		<portlet:namespace />hideDayFieldOfPeriodFields ();
		jQuery('#<portlet:namespace />buscandoPrestaciones').hide();

		function <portlet:namespace />validarCampos() {
			var cantidadMedicamentos=jQuery('#medicamentosSize').val();
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();
			
			if(jQuery("#<portlet:namespace />id_seccional_r").val() == ""){
				alert("<liferay-ui:message key='seccional_obligatoria' />");
				jQuery("#<portlet:namespace />id_seccional_r").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_seccional_r").val() != "" && jQuery("#<portlet:namespace />secc_seleccionada_r").val()!="1"){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional_r").focus();
				return false;
			}
			if (periodo == '') {
				alert('Debe elegir un periodo');
				jQuery("#<portlet:namespace />periodoMesAnio").focus();
				return false;
			}
			if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
				alert("El afiliado debe tener un plan vigente en el periodo");
				return false;
			}
			if(cuil!=null && cuil.length==0){
				alert('<liferay-ui:message key='debe-seleccionar-un-afiliado-antes-guardar-reintegro' />');
				return false;
			}
			if(trim(inte).length == 0){
				alert("<liferay-ui:message key='inte-obligatorio' />");
				jQuery('#<portlet:namespace />inte').focus();
				return false;
			}
			if (jQuery("#<portlet:namespace />id_seccional").val() != jQuery("#<portlet:namespace />id_seccional_r").val()) {
				if (confirm("¿La seccional del reintegro no coincide con la del afiliado, desea continuar grabando?") == false){
					return false;
				}
				//solo aviso puede continuar
			}
			if(null==cantidadMedicamentos || cantidadMedicamentos<=0){
				alert('<liferay-ui:message key='debe-cargar-medicamento' />');
				return false;
			}
			
			return true;
		}

		function <portlet:namespace />saveReintegro() {
			if (!<portlet:namespace />validarCampos()) {
				return false;				
			}
			document.<portlet:namespace />fm.<portlet:namespace /><%=Constants.CMD%>.value = "<%=reintegro == null
					|| (reintegro != null && reintegro.getId_reintegro() == 0)
					? Constants.ADD
					: Constants.UPDATE%>";
			url = '<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>"/>&struts_action=/<%=portlet_name%>/grabar_reintegro_farmacia_entry';					
			submitForm(document.<portlet:namespace />fm, url);
		}

		function <portlet:namespace />altaReintegro() {
			<portlet:namespace />limpiarCamposAfiliado();
			jQuery("#<portlet:namespace />id_reintegro").val("0");			
			var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_reintegro_farmacia_entry';
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}
		
		// FUNCIONES DE RECLAMOS PRESTACIONALES 
		
        function <portlet:namespace />oculta_prestaciones_reclamos(){
			
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
			jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
			jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
			
        }
        
		function <portlet:namespace />ver_prestaciones_reclamos() {
		    			
		    var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
			var farmacia=1;
			var params = { "cuil":cuil,  "inte":inte , "farmacia":farmacia  };
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_prestaciones_reclamos_reintegros" /></portlet:renderURL>';
			
						
			jQuery('#<portlet:namespace />div_reclamos_prestaciones').load(url,params, function(){
												jQuery('#<portlet:namespace />buscando').hide();            															
																  });
			jQuery("#<portlet:namespace />div_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").show();
		    jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		    jQuery("#div_boton_cancelar_reclamos_prestaciones").hide();
		    
		    
			
		}
		
		
		function <portlet:namespace />habilitaControlBusquedaAfiliado(accion){
			if (accion){
				document.getElementById("<portlet:namespace />numero_afi").disabled = "";
				document.getElementById("<portlet:namespace />cuil").disabled = "";
				document.getElementById("<portlet:namespace />inte").disabled = "";
				document.getElementById("<portlet:namespace/>tipoDoc").disabled = "";
				document.getElementById("<portlet:namespace />nroDoc").disabled = "";
				
				
			}else{
				document.getElementById("<portlet:namespace />numero_afi").disabled = "disabled";
				document.getElementById("<portlet:namespace />cuil").disabled = "disabled";
				document.getElementById("<portlet:namespace />inte").disabled = "disabled";
				document.getElementById("<portlet:namespace/>tipoDoc").disabled = "disabled";
				document.getElementById("<portlet:namespace />nroDoc").disabled = "disabled";
				
				
			}
			
		}
				
		function <portlet:namespace />cancelar_prestaciones_reclamos() {
			
			jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").hide();
			jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
		    jQuery('#<portlet:namespace />prestacion').val("");	
		    jQuery('#<portlet:namespace />codigo').val("");
			jQuery('#<portlet:namespace />importe').val("");
			jQuery('#<portlet:namespace />cantidad').val("");
			jQuery('#<portlet:namespace />total').val("");
			
			// datos de la validacion del importe recuperado para una prestacion desde de un reclamo
			jQuery('#<portlet:namespace />importeoriginalreclamo').val("");
			jQuery('#<portlet:namespace />importeoriginalnovalidado').val("");

			
			jQuery('#<portlet:namespace />id_reclamo_prestacional').val("");
			jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val("");			
			// desahilita el control de busqueda de afiliados
			<portlet:namespace />habilitaControlBusquedaAfiliado(true);
			// habilita  controles de importes de la pretacion 
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);
			
		}
					
		function <portlet:namespace />validaMontoOriginalReclamo(){
	    	   if (jQuery("#<portlet:namespace />importeoriginalreclamo").val()!=''){ // prestacion de reclamo prestacional 
	    		   	    	
	    		   var total;
	    		   var totalDeReclamo;
	    		   var valor;
	    		   valor=true;
	    		   
	    		   
	    		   total = jQuery('#<portlet:namespace />total_cob').val();  
	    		   totalDeReclamo  =jQuery('#<portlet:namespace />importeoriginalreclamo').val() ;
	    		   /*
	    		   
	    		   jQuery('#<portlet:namespace />importeoriginalnovalidado').val('') ;
		    	   if ( Math.round(total) >Math.round(totalDeReclamo)  ){
		    		   //alert('El monto ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestaci\u00f3n en el reclamo.');		    		   
		    		   valor=false;
		    		   //jQuery('#<portlet:namespace />importeoriginalnovalidado').val('bad') ;
		    	   }    	   
		    	  */
	    	   }
	    	   return valor;	
	    }
		

		
		
		jQuery("#<portlet:namespace />div_reclamos_prestaciones").hide();
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").show();
		jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
		jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").hide();
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		jQuery("#<portlet:namespace />div_label_prestacion_reclamo").hide();

					
			
			
		
--></script>
<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server

	boolean showABMButtons = PermissionUtil.userContainsRole(user,
			"ABM_Discapacidad");
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/liquidaciones/view");
	
	//verificar los calendars
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
	fechaHasta.setTime(DateUtils.getLastDateOfYear(new Date(), true));	
%>

<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;"><liferay-portlet:renderURLParams
	varImpl="portletURL" /> <%
 	boolean showOspim = PermissionUtil.userContainsRole(user,
 			WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 	boolean showAmtima = PermissionUtil.userContainsRole(user,
 			WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
 	boolean showUoma = PermissionUtil.userContainsRole(user,
 			WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);

 	//Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
 	//fechaDesde.setTime(new Date());
 	//Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 	//fechaHasta.setTime(new Date());
 %>
<fieldset class="block-labels"><legend><liferay-ui:message
	key="Tratamientos de Discapacidad" /></legend>
<table class="lfr-table">
	<tr>
		<td colspan="12">
		<fieldset class="block-labels"><legend><liferay-ui:message
			key="datos-afiliado" /></legend> <liferay-util:include
			page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="edit_mode" />
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="discapacidad" />
			<liferay-util:param name="pag_reintegro" value='1' />				
		</liferay-util:include></fieldset>
		</td>
	</tr>
	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>

	<tr>
		<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
		</td>		
		<td><label><liferay-ui:message key="estado" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace/>estado"
			id="<portlet:namespace/>estado">
			<option value="0"></option>
			<option value="1">En Curso</option>
			<option value="2">Documentación Faltante</option>
			<option value="3">Cambio Prestador</option>
			<option value="4">Finalizado</option>
			<option value="5">Abandonado</option>
		</select></td>		
	</tr>
	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	
	<tr>
		<td>
			<liferay-ui:message key="cuit" />
		</td>
		<td>
			<input  id="<portlet:namespace />cuit_prestador" name="<portlet:namespace />cuit_prestador" maxlength="11" size="13" type="text" value=""/>
		</td>
		<td>
			<liferay-ui:message key="razon-social" />
		</td>
		<td>
			<input id="<portlet:namespace />nombre_prestador" name="<portlet:namespace />nombre_prestador" size="50" type="text" value=""/>&nbsp;
		</td>		
		<td><label>Código Prestación:</label></td>													
		<td><input id="<portlet:namespace />codPrestaci" name="<portlet:namespace />codPrestaci" size="6" maxlength="6" type="text" value="" /></td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>

    <tr>
		<td colspan="2">Incluye Tratamientos Antiguos <input type="checkbox"  name="<portlet:namespace />antiguosfiltro" 
							 id="<portlet:namespace />antiguosfiltro"></td>
	</tr>
	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	

	<tr>
		<td coslpan="1"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button" /></td>
		<td colspan="2"><c:if test="<%=showABMButtons%>">
			<input type="button" value="Alta tratamiento"
				onClick="<portlet:namespace />altaTratamiento();" />
		</c:if></td>
		<td><c:if test="<%=showABMButtons%>"><input type="button" value="Documentación Faltante RTF"
		 	onClick="<portlet:namespace />imprimirDFRTF();return false;" />&nbsp;&nbsp;&nbsp;<input type="button" value="Documentación Faltante ODT"
		 	onClick="<portlet:namespace />imprimirDFODT();return false;"/>
		 	</c:if></td>				
	</tr>
	<tr>
			<td colspan="12">&nbsp;</td>
		</tr>
		
</table>
</fieldset>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
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
<div align="center" id="<portlet:namespace />busquedaReintegroDiv">
</div>
</fieldset>
</form>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_tratamientos_sesion';
	jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url);
	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		<portlet:namespace />busquedaTD();
	});

//TODO AÑADIR CAMPOS NUEVOS
	function <portlet:namespace />busquedaTD(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();

		var codPrestad=jQuery('#<portlet:namespace />cuit_prestador').val();
		var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();

		var estado=jQuery('#<portlet:namespace />estado').val();

		var codPrestaci=jQuery('#<portlet:namespace />codPrestaci').val();
		
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var antiguos=jQuery("#<portlet:namespace/>antiguosfiltro").is(':checked');
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_tratamientos_discapacidad&entidad='+entidad+		
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&codPrest='+codPrestad+'&prestador='+encodeURI(prestador)+'&numero=0'+'&estado='+estado+'&codPrestaci='+codPrestaci+
		'&antiguos='+antiguos;
        jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />initDateFields(){
		//capaz que seleccionar el afiliado del componente de búsqueda de afiliados		
	}
	
	<portlet:namespace />initDateFields();

	var popupTratamientosD;
	function <portlet:namespace />altaTratamiento() {
		if (jQuery("#<portlet:namespace />incapacidad_af").val() != '1') {
			alert ("Debe seleccionar un afiliado discapacitado");
			return false;
		}
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		if (trim(cuil).length == 0) {
			alert("<liferay-ui:message key='cuil-obligatorio' />");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(trim(inte).length == 0){
			alert("<liferay-ui:message key='inte-obligatorio' />");
			jQuery('#<portlet:namespace />inte').focus();
			return false;
		}
		popupTratamientosD = Liferay.Popup({title:"Alta de Tratamiento",modal:true,position:[150,30],xy: ['center', 100],width:1120,onClose: function () {<portlet:namespace />busquedaTD();}});					
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/tratamiento_discapacidad&cuil_titular='+cuil+'&inte='+inte;
		jQuery(popupTratamientosD).load(url);
	}
	
	function editarTratamiento(id_tratamiento) {
		popupTratamientosD = Liferay.Popup({title:"Edición de Tratamiento",modal:true,position:[150,30],xy: ['center', 100],width:1120,onClose: function () {<portlet:namespace />busquedaTD();}});
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/tratamiento_discapacidad&id_tratamiento='+id_tratamiento;
		jQuery(popupTratamientosD).load(url);
	}

	function verTratamiento(id_tratamiento) {
		popupTratamientosD = Liferay.Popup({title:"Ver Detalle",modal:true,position:[150,30],xy: ['center', 100],width:1120,onClose: function () {<portlet:namespace />busquedaTD();}});
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/tratamiento_discapacidad&id_tratamiento='+id_tratamiento+'&view=true';
		jQuery(popupTratamientosD).load(url);
	}
	
	function <portlet:namespace />reloadPopupEditarTratamiento(id) {				
		Liferay.Popup.close(popupTratamientosD);
		editarTratamiento(id);
	}	
	
	function borrarTratamiento(id_tratamiento) {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE
										.toString()%>"/>&struts_action=/liquidaciones/editar_tratamiento_entry&id_tratamiento='+id_tratamiento+'&accionOriginal='+'<%=Constants.DELETE%>';						
			jQuery('#<portlet:namespace />busquedaReintegroDiv').load(url, function() {				
				<portlet:namespace />busquedaTD();
			});
		}
	}

	function <portlet:namespace />imprimirDF(){

		var cuil=jQuery('#<portlet:namespace />cuil_titular').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		if (trim(cuil).length == 0) {
			alert("<liferay-ui:message key='cuil-obligatorio' />");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(trim(inte).length == 0){
			alert("<liferay-ui:message key='inte-obligatorio' />");
			jQuery('#<portlet:namespace />inte').focus();
			return false;
		} 
		if(trim(fechaDesdeDia).length == 0 || trim(fechaHastaDia).length == 0 ){
			alert("Debe ingresar fechas desde y hasta");			
			return false;
		}
		window.location.href ='/pdfservlet/?accion=<%="documentacionFaltante"%>&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+
			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
						
	}

	function <portlet:namespace />imprimirDFRTF(){

		var cuil=jQuery('#<portlet:namespace />cuil_titular').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		if (trim(cuil).length == 0) {
			alert("<liferay-ui:message key='cuil-obligatorio' />");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(trim(inte).length == 0){
			alert("<liferay-ui:message key='inte-obligatorio' />");
			jQuery('#<portlet:namespace />inte').focus();
			return false;
		} 
		if(trim(fechaDesdeDia).length == 0 || trim(fechaHastaDia).length == 0 ){
			alert("Debe ingresar fechas desde y hasta");			
			return false;
		}
		window.location.href ='/odtservlet/?accion=<%="documentacionFaltanteRtf"%>&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+
			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
						
	}

	function <portlet:namespace />imprimirDFODT(){

		var cuil=jQuery('#<portlet:namespace />cuil_titular').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		if (trim(cuil).length == 0) {
			alert("<liferay-ui:message key='cuil-obligatorio' />");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(trim(inte).length == 0){
			alert("<liferay-ui:message key='inte-obligatorio' />");
			jQuery('#<portlet:namespace />inte').focus();
			return false;
		} 
		if(trim(fechaDesdeDia).length == 0 || trim(fechaHastaDia).length == 0 ){
			alert("Debe ingresar fechas desde y hasta");			
			return false;
		}
		window.location.href ='/odtservlet/?accion=<%="documentacionFaltanteOdt"%>&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+
			'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
			'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
						
	}
</script>
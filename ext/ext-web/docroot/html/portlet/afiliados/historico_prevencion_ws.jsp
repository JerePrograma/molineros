<%@ include file="/html/portlet/afiliados/init.jsp"%>
<portlet:defineObjects />

<%
	//verificar los calendars
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde.setTime(new Date());
	fechaDesde.add(Calendar.DATE, -7); 
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
	fechaHasta.setTime(new Date());
	Afiliado afiliado = (Afiliado)request.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	if (afiliado == null) {
		afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
	}
%>

<fieldset class="block-labels"><legend><liferay-ui:message key="grupo-filtro-busqueda-historico-novedades_prevencion" /></legend>

<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td colspan="2"><liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>
		<td colspan="3"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />" type="button"
			onClick="javascript:<portlet:namespace />buscarHistoricosPrevencionWS();" /></td>
		<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUpload')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Glosario" alt="Glosario"/></a>
		</td>	
		<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUploadAfiliaciones')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Afiliaciones" alt="Afiliaciones"/></a>
		</td>
		<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUploadPrevencion')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Prevención" alt="Prevención"/></a>
		</td>
		<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUploadSistemas')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Sistemas Ospim" alt="Sistemas Ospim"/></a>
		</td>
		<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUploadEsperar')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Esperar" alt="Esperar"/></a>
		</td>
			<td>
				<a href="javascript:void(0)" onclick="help(event, 'helpUploadMarcarProcesado')"><img style="height: 19px; width: 19px" src="/html/images/help.png" title="Marcar procesado manualmente" alt="Marcar procesado manualmente"/></a>
		</td>
		
		
	</tr>
	
</table>
<div id="helpUpload" class="containerPlus draggable {buttons:'c', skin:'default', width:'600',title:'Glosario',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b>Glosario de términos de respuestas de novedades a prevención.</b>  
	 <br>
	<br> AnnexMember: algún miembro con datos erroneos.
	<hr>
	<br> FixedPhone: teléfono/celular.
	<hr>  
	<br> BirthDate:  fecha de cumpleaños.
	<hr>
	<br> WorkingRelationshipCuit: El cuit no corresponde con su empleador actual.
	<hr>
	<br> AnnexMember.DocumentNumber: Ya existe ese número de documento en prevención
	<hr>
    <br> FamilyGroupMembers[0].BirthDate: Fecha cumpleaños erronea.
    <hr>
    <br> HolderCuil: El afiliado tiene una baja futura.
    <hr>
    <br> Neighborhood: barrio
    <hr> 
    <br> WorkingRelationship: empresa 	
</div>

<div id="helpUploadAfiliaciones" class="containerPlus draggable {buttons:'c', skin:'default', width:'900',title:'Afiliaciones',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b>Resuelve afiliaciones.</b>
	<br>
	<br> "Message: * Para los parentescos ""Menor bajo guardia"" y ""Hijo menor de 21 años"" la persona debe ser menor de 21 años. 
	<br> Key: AnnexMember.BirthDate ParameterName: request	Message: Formato incorrecto. Key: AnnexMember.FixedPhone[0] ParameterName: request"
	<hr>
	<br>"Message: No se pudo dar de alta la persona en BUP. Validaciones en empleador con cuil <b>99999999999</b>: 
	<br>La Denominación/Razón Social no coincide con la registrada en BUP. 
	<br>La Razón Social/Denominación ingresada es <b>XXX</b> y la registrada en BUP es XXX ANONIMA para el Código Tributario <b>99-99999999-9</b>.
	<br>La denominación no coincide con AFIP Key: BupId ParameterName: request"
	<hr>
	<br>"Message: Debe ser un número de CUIT válido. Key: FamilyGroupMembers[<b>X</b>].WorkingRelationshipCuit ParameterName: request
	<hr>
	<br>"Message: No se pudo dar de alta la persona en BUP. Validaciones en integrante con cuil <b>99999999999</b>: 
	<br>La Fecha de Nacimiento no coincide con la registrada por AFIP. La fecha ingresada es <b>99/99/9999</b> y 
	<br>la fecha de nacimiento según AFIP es <b>99/99/9999</b> Key: BupId ParameterName: request"
    <hr>
    <br>"Message: Formato incorrecto. Key: FamilyGroupMembers[X].FixedPhone[0] ParameterName: request"
    <hr>
	<br>En el contrato <b>99999</b> no existe un afiliado con el número de CUIL ingresado.
	<hr>
	<br>"Message: El valor es obligatorio. Key: FamilyGroupMembers[0].Cuil ParameterName: request"
	<hr>
	<br>"Message: El afiliado no está asociado a la cuenta ingresada. Key: AccountId ParameterName: request"
	<hr>
	<br>"Message: El campo debe tener un máximo de 30 caracteres. Key: AnnexMember.Neighborhood ParameterName: request"
	<hr>
	<br>"Message: No se pudo dar de alta la persona en BUP. Validaciones en integrante con cuil <b>99999999999</b>: 
	<br>El prefijo del Número de Código Tributario, <b>XX</b>, no se corresponde el sexo ingresado para la persona, Masculino. Key: BupId ParameterName: request
	<hr>
	<br>"Message: El afiliado con el siguiente cuil (<b>99999999999</b>) 
	<br>ya se encuentra vigente para la fecha que se quiere dar de alta. Key: FamilyGroupMembers ParameterName: request"
	<hr>
	<br>"Message: Formato incorrecto. Key: FamilyGroupMembers[0].Emails[0] ParameterName: request"
	
</div>

<div id="helpUploadPrevencion" class="containerPlus draggable {buttons:'c', skin:'default', width:'600',title:'Prevención',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b>Resuelve prevención. Usuando la acción Enviar E-Mail Homologación </b>  
	 <br>
	<br>No se encontró la equivalencia para el tipo S. -> PS debe homologar una Nacionalidad
	<hr>
	<br>No se ha encontrado la razón para el motivo de baja <b>999</b>.
	<hr>
	<br>No se encontró la equivalencia para el tipo O. -> PS debe homologar una Localidad
	<hr>
	<br>No se encontró la equivalencia para el tipo N. -> PS debe homologar un Tipo Documento
	
</div>


<div id="helpUploadSistemas" class="containerPlus draggable {buttons:'c', skin:'default', width:'600',title:'Sistemas OSPIM',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b>Resuelve Sistemas Ospim.</b>  
	<br>
	<br>(afi_precarga_solicitudes) The INSERT statement conflicted with the FOREIGN KEY constraint "fk_PRECARGA_solicitudes_3".
	The conflict occurred in database "PrevencionSalud", table "dbo.tarifas".
	<hr>
	<br>The input stream for an incoming message is null.
	<hr>
	<br>Nullable object must have a value.
	<hr>
	<br>Object reference not set to an instance of an object.
	<br>The operation is not valid for the state of the transaction.
	<hr>
	<br>Sequence contains no elements
	<hr>
	<br>integration-pro-ws.gruposancorseguros.com
	<hr>
	<br>could not execute batch command.[SQL: SQL not available]
	<hr>
	<br>Commit failed with SQL exception
	<hr>
	<br>Sequence contains no matching element
	
</div>



<div id="helpUploadEsperar" class="containerPlus draggable {buttons:'c', skin:'default', width:'600',title:'Esperar',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b> Esperar siguiente envío, para que se arregle en forma automática.</b>  
	<br>
	<br>Conexión rehusada (Connection refused)
	<hr>
	<br>An error occurred while receiving the HTTP response to http://bup-pro-ws.sancorseguros.net/PersonBUPService.svc. This could be due to the service endpoint binding not using the HTTP protocol. This could also be due to an HTTP request context being aborted by the server (possibly due to the service shutting down). See server logs for more details.
	<hr>
	<br>The HTTP request to 'http://psaludvalidator-pro-ws.sancorseguros.net/Membership/MembershipService.svc' has exceeded the allotted timeout of 00:01:00. The time allotted to this operation may have been a portion of a longer timeout.
	(precarga_soli_afiliados) 
	<br>The HTTP service located at http://bup-pro-ws.sancorseguros.net/SecurityService.svc is unavailable.  This could be because the service is too busy or because no endpoint was found listening at the specified address. Please ensure that the address is correct and try accessing the service again later.
	<hr>
	<br>The requested service, 'http://psaludvalidator-pro-ws.sancorseguros.net/Membership/MembershipService.svc' could not be activated. See the server's diagnostic trace logs for more information.
	<hr>
	<br>Transaction not connected, or was disconnected
	<hr>
	<br>Read timed out
	<hr>
	<br>"Cannot access a disposed object.Object name: 'Scope was already disposed. This is most likely a bug in the calling code.'."
	
</div>



<div id="helpUploadMarcarProcesado" class="containerPlus draggable {buttons:'c', skin:'default', width:'600',title:'Marcar procesado manualmente',closed:'true'}" 
	style="top: 200px; left: 400px">
	 <b>Resuelve Afiliaciones Marcar procesado manualmente.</b>  
	<br>
	
	<br>"Message: El afiliado con el siguiente cuil (99999999999) ya se encuentra vigente para la fecha que se quiere dar de alta. Key: AnnexMember ParameterName: request"
	<hr>
	<br>"Message: El afiliado posee el mismo plan y plan de farmacia informado. Key: HealthPlan ParameterName: request"

	
</div>

</fieldset>
<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />buscarHistoricosPrevencionWS">
</div>
</fieldset>

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
function <portlet:namespace />buscarHistoricosPrevencionWS(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuilTitular = <%=afiliado.getCuil_titular()%>
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/historico_prevencion_ws&cuil_titular='+cuilTitular+'&cmd='+true;
	jQuery('#<portlet:namespace />buscando').show();		
	jQuery("#<portlet:namespace/>buscarHistoricosPrevencionWS").load(url,{desde_dia:desde_dia, desde_mes:desde_mes, desde_anio:desde_anio, hasta_dia:hasta_dia,
		hasta_mes:hasta_mes, hasta_anio:hasta_anio}, function(){jQuery('#<portlet:namespace />buscando').hide();});	
}	






</script>
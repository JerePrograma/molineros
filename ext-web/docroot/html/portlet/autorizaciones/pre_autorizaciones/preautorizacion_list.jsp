<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}

	//verificar los calendars
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();


	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.add(Calendar.MONTH, -1);


	boolean popup = ParamUtil.getBoolean(request, "popup", false);

	boolean showPreAutorizaciones = PermissionUtil.userContainsRole(user, WebKeysAutorizaciones.ROL_ABM_PREAUTORIZACION);

	List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales();
/*
	List<User> users = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), null, Boolean.TRUE, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
*/

	List<ModalidadAtencion> estadosList = TraeListasServiceUtil.getEstadosSeguimientoSur();
	boolean showProcesaArchivo = PermissionUtil.userContainsRole(user, WebKeysAutorizaciones.ROL_PREAUTORIZACION_PROCESA_ARCHIVO_PREVENCION);
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="busq-pre-autorizaciones" /></legend>

		<table class="lfr-table" style="border-collapse: separate; border-spacing:5px;">
			<tr>
				<td>
					<table class="lfr-table" style="border-collapse: separate; border-spacing:5px;">
						<tr>
							<td>
								<div id="<portlet:namespace/>divAfiliadosSeguimientoSurFiltro">
									<fieldset class="block-labels">
										<legend><liferay-ui:message key="datos-afiliado" /></legend>

										<liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado_filtro_prevencion.jsp'>
											<liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
											<liferay-util:param value="<%= null %>" name="discapacidad" />
											<liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />
											<liferay-util:param name="cuil" value='' />
											<liferay-util:param name="inte" value='' />
											<liferay-util:param value="_filtro" name="origen" />
										</liferay-util:include>
									</fieldset>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td>
					<fieldset class="block-labels">
						<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
							<tr>
								<td><label>Desde:</label></td>
								<td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaDesdeDiaFiltro"
                                            dayValue="-1"
                                            dayNullable="<%= true %>"
                                            monthParam="fechaDesdeMesFiltro"
                                            monthValue="-1"
                                            monthNullable="<%= true %>"
                                            yearParam="fechaDesdeAnioFiltro"
                                            yearValue="-1"
                                            yearNullable="<%= true %>"
                                            yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
								</td>

								<td><label>Email Desde:</label></td>
								<td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaDesdeEmailDiaFiltro"
                                            dayValue="-1"
                                            dayNullable="<%= true %>"
                                            monthParam="fechaDesdeEmailMesFiltro"
                                            monthValue="-1"
                                            monthNullable="<%= true %>"
                                            yearParam="fechaDesdeEmailAnioFiltro"
                                            yearValue="-1"
                                            yearNullable="<%= true %>"
                                            yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
								</td>

								<td colspan="3">
									<label>Incluir bajas:</label>
									<input type="checkbox"
										   id="<portlet:namespace />baja_filtro"
										   name="<portlet:namespace />baja_filtro"
										   value="false">
								</td>
							</tr>

							<tr>
								<td><label>Hasta:</label></td>
								<td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaHastaDiaFiltro"
                                            dayValue="-1"
                                            dayNullable="<%= true %>"
                                            monthParam="fechaHastaMesFiltro"
                                            monthValue="-1"
                                            monthNullable="<%= true %>"
                                            yearParam="fechaHastaAnioFiltro"
                                            yearValue="-1"
                                            yearNullable="<%= true %>"
                                            yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
								</td>

								<td><label>Email Hasta:</label></td>
								<td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaHastaEmailDiaFiltro"
                                            dayValue="-1"
                                            dayNullable="<%= true %>"
                                            monthParam="fechaHastaEmailMesFiltro"
                                            monthValue="-1"
                                            monthNullable="<%= true %>"
                                            yearParam="fechaHastaEmailAnioFiltro"
                                            yearValue="-1"
                                            yearNullable="<%= true %>"
                                            yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
								</td>
							</tr>

							<tr>
								<td><label><liferay-ui:message key="id-preaut"/>:</label></td>
								<td colspan="2">
									<input id="<portlet:namespace />idPreautorizacion_filtro"
										   name="<portlet:namespace />idPreautorizacion_filtro"
										   size="20"
										   maxlength="20"
										   type="text"
										   value=''
										   onkeydown="allowOnlyDigits(event);" />
								</td>

								<option value="">Seleccione estado</option>
								<td>Estado:</td>
								<td>
									<select name="<portlet:namespace />estadoPreautorizacion_filtro"
											id="<portlet:namespace />estadoPreautorizacion_filtro"
											onchange="">
										<option value="">Seleccione estado</option>


										<% for(int i = 0; i < WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES.length; i++ ) { %>
											<option value="<%=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[i][0] %>">
												<%=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[i][1] %>
											</option>
										<% } %>
									</select>
								</td>
								<td><label><liferay-ui:message key="solic-terc"/>:</label></td>
								<td colspan="2">
									<input id="<portlet:namespace />idAutorizacion_filtro"
										   name="<portlet:namespace />idAutorizacion_filtro"
										   size="20"
										   maxlength="20"
										   type="text"
										   value=''
										   onkeydown="allowOnlyDigits(event);" />
								</td>
							</tr>

							<tr>

								<td><label><liferay-ui:message key="seccional" />:</label></td>
								<td colspan="3" style="vertical-align:top;">
									<liferay-util:include page='/html/portlet/autorizaciones/busqueda_seccional.jsp'>
										<liferay-util:param value="_sec_filtro" name="prefijo" />
									</liferay-util:include>
								</td>

							</tr>
						</table>

						<table>
							<tr>
								<td style="background-color:#AEB6BF">
									<label>Alerta Roja:</label>
									<input type="checkbox"
										   id="<portlet:namespace />alerta_roja_filtro"
										   name="<portlet:namespace />alerta_roja_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Discapacidad:</label>
									<input type="checkbox"
										   id="<portlet:namespace />discapacidad_filtro"
										   name="<portlet:namespace />discapacidad_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Supra:</label>
									<input type="checkbox"
										   name="<portlet:namespace />supra_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Cirugía:</label>
									<input type="checkbox"
										   name="<portlet:namespace />cirugia_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Medicamento:</label>
									<input type="checkbox"
										   name="<portlet:namespace />medicamento_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Sin Reintento:</label>
									<input type="checkbox"
										   name="<portlet:namespace />sin_reintento_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Alojamiento:</label>
									<input type="checkbox"
										   name="<portlet:namespace />alojamiento_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Prótesis/Órtesis:</label>
									<input type="checkbox"
										   name="<portlet:namespace />protesisOrtesis_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Posible ART:</label>
									<input type="checkbox"
										   name="<portlet:namespace />ART_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>

								<td style="background-color:#AEB6BF">
									<label>Diabetes:</label>
									<input type="checkbox"
										   name="<portlet:namespace />diabetes_filtro"
										   value="false">
								</td>

								<td>&nbsp;</td>
							</tr>
						</table>


					</fieldset>
				</td>
			</tr>
		</table>


		<table>
			<tr align="left">
				<td>&nbsp;</td>
				<td align="left" width="100%">
					<input id="<portlet:namespace />buscar"
						   value="<liferay-ui:message key="buscar"/>"
						   title="<liferay-ui:message key="buscar" />"
						   onClick="javascript: <portlet:namespace />buscarPreautorizacion();"
						   type="button" />

					<c:if test="<%= showPreAutorizaciones %>">
						<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaPreAutorizacion();"/>&nbsp;
						<input type="button" value="Reporte" onClick="<portlet:namespace />reportePreautorizacion();"/>&nbsp;
					</c:if>
					<%-- <c:if test="<%= showProcesaArchivo %>">
						<input type="button" value="Procesa Archivo Prevención" onClick="<portlet:namespace />procesaArchivoPreAutorizacion();"/>&nbsp;
					</c:if> --%>
				</td>

				<td>&nbsp;</td>
			</tr>
		</table>

		<div id='divSeguimientoSur' style="float:left;"></div>
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
		<div id="<portlet:namespace />listado_preautorizaciones"
			 style="
				width: 100%;
				max-height: 400px;
				overflow-x: auto;
				overflow-y: auto;
				border: 1px solid #ccc;
				border-radius: 6px;
				background: #fff;">
			<jsp:include page='/html/portlet/autorizaciones/pre_autorizaciones/preautorizaciones_result.jsp' />
		</div>


	</fieldset>

	<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>

</form>

<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();
	var popupMD;

	<portlet:namespace />initDateFields();

	function <portlet:namespace />initDateFields(){
	}

	function <portlet:namespace />nuevaPreAutorizacion() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}


	function <portlet:namespace />buscarPreautorizacion(){

		var cuil = jQuery('#<portlet:namespace />cuil_filtro').val();
		var inte = jQuery('#<portlet:namespace />inte_filtro').val();
		var estado = jQuery('#<portlet:namespace />estadoPreautorizacion_filtro').val();
		var id = jQuery('#<portlet:namespace />idPreautorizacion_filtro').val();
		var idAutorizacion = jQuery('#<portlet:namespace />idAutorizacion_filtro').val();
		var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");

		var fechaDesdeEmailDia = document.getElementById("<portlet:namespace />fechaDesdeEmailDiaFiltro");
		var fechaDesdeEmailMes = document.getElementById("<portlet:namespace />fechaDesdeEmailMesFiltro");
		var fechaDesdeEmailAnio = document.getElementById("<portlet:namespace />fechaDesdeEmailAnioFiltro");

		var fechaHastaEmailDia = document.getElementById("<portlet:namespace />fechaHastaEmailDiaFiltro");
		var fechaHastaEmailMes = document.getElementById("<portlet:namespace />fechaHastaEmailMesFiltro");
		var fechaHastaEmailAnio = document.getElementById("<portlet:namespace />fechaHastaEmailAnioFiltro");

		var seccional = jQuery('#<portlet:namespace />id_seccional_sec_filtro').val();
		var alertaRoja = jQuery("#<portlet:namespace/>alerta_roja_filtro").is(':checked');
		var discapacidad = jQuery("#<portlet:namespace/>discapacidad_filtro").is(':checked');
		var supra = jQuery("#<portlet:namespace/>supra_filtro").is(':checked');
		var cirugia = jQuery("#<portlet:namespace/>cirugia_filtro").is(':checked');
		var medicamento = jQuery("#<portlet:namespace/>medicamento_filtro").is(':checked');
		var sinReintento = jQuery("#<portlet:namespace/>sin_reintento_filtro").is(':checked');
		var alojamiento = jQuery("#<portlet:namespace/>alojamiento_filtro").is(':checked');
		var protesisOrtesis = jQuery("#<portlet:namespace/>protesisOrtesis_filtro").is(':checked');
		var art = jQuery("#<portlet:namespace/>ART_filtro").is(':checked');
		var diabetes = jQuery("#<portlet:namespace/>diabetes_filtro").is(':checked');
		var baja = jQuery("#<portlet:namespace/>baja_filtro").is(':checked');

		if(cuil == '' && estado == '' && id == '' && idAutorizacion == ''
				&& fechaDesdeDia.value == '' && fechaDesdeMes.value == '' && fechaDesdeAnio.value == ''
				&& seccional == '' && fechaDesdeEmailDia.value == '' && fechaDesdeEmailMes.value == '' && fechaDesdeEmailAnio.value == ''){

			alert("Debe ingresar algún criterio de búsqueda");
			return false;
		}

		jQuery('#<portlet:namespace />buscando').show();

		var busquedaNom = {
			"cuil": cuil,
			"inte": inte,
			"fechadesdedia": fechaDesdeDia.value,
			"fechadesdemes": fechaDesdeMes.value,
			"fechadesdeanio": fechaDesdeAnio.value,
			"estado": estado,
			"id": id,
			"idAutorizacion": idAutorizacion,
			"cmd": "filterPrestacion",
			"fechahastadia": fechaHastaDia.value,
			"fechahastames": fechaHastaMes.value,
			"fechahastaanio": fechaHastaAnio.value,
			"fechadesdeemaildia": fechaDesdeEmailDia.value,
			"fechadesdeemailmes": fechaDesdeEmailMes.value,
			"fechadesdeemailanio": fechaDesdeEmailAnio.value,
			"fechahastaemaildia": fechaHastaEmailDia.value,
			"fechahastaemailmes": fechaHastaEmailMes.value,
			"fechahastaemailanio": fechaHastaEmailAnio.value,
			"seccional": seccional,
			"alertaroja": alertaRoja,
			"supra": supra,
			"cirugia": cirugia,
			"medicamento": medicamento,
			"sin_reintento": sinReintento,
			"discapacidad": discapacidad,
			"alojamiento": alojamiento,
			"protesisOrtesis": protesisOrtesis,
			"posibleart": art,
			"diabetes": diabetes,
			"baja": baja
		};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />listado_preautorizaciones').load(url, busquedaNom, function(){
			jQuery('#<portlet:namespace />buscando').hide();
		});

	}

	function <portlet:namespace />reportePreautorizacion(){

		var cuil = jQuery('#<portlet:namespace />cuil_filtro').val();
		var inte = jQuery('#<portlet:namespace />inte_filtro').val();
		var estado = jQuery('#<portlet:namespace />estadoPreautorizacion_filtro').val();
		var id = jQuery('#<portlet:namespace />idPreautorizacion_filtro').val();
		var idAutorizacion = jQuery('#<portlet:namespace />idAutorizacion_filtro').val();
		var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");

		var fechaDesdeEmailDia = document.getElementById("<portlet:namespace />fechaDesdeEmailDiaFiltro");
		var fechaDesdeEmailMes = document.getElementById("<portlet:namespace />fechaDesdeEmailMesFiltro");
		var fechaDesdeEmailAnio = document.getElementById("<portlet:namespace />fechaDesdeEmailAnioFiltro");

		var fechaHastaEmailDia = document.getElementById("<portlet:namespace />fechaHastaEmailDiaFiltro");
		var fechaHastaEmailMes = document.getElementById("<portlet:namespace />fechaHastaEmailMesFiltro");
		var fechaHastaEmailAnio = document.getElementById("<portlet:namespace />fechaHastaEmailAnioFiltro");

		var seccional = jQuery('#<portlet:namespace />id_seccional_sec_filtro').val();
		var alertaRoja = jQuery("#<portlet:namespace/>alerta_roja_filtro").is(':checked');
		var discapacidad = jQuery("#<portlet:namespace/>discapacidad_filtro").is(':checked');
		var supra = jQuery("#<portlet:namespace/>supra_filtro").is(':checked');
		var cirugia = jQuery("#<portlet:namespace/>cirugia_filtro").is(':checked');
		var medicamento = jQuery("#<portlet:namespace/>medicamento_filtro").is(':checked');
		var sinReintento = jQuery("#<portlet:namespace/>sin_reintento_filtro").is(':checked');
		var alojamiento = jQuery("#<portlet:namespace/>alojamiento_filtro").is(':checked');
		var protesisOrtesis = jQuery("#<portlet:namespace/>protesisOrtesis_filtro").is(':checked');
		var art = jQuery("#<portlet:namespace/>ART_filtro").is(':checked');
		var diabetes = jQuery("#<portlet:namespace/>diabetes_filtro").is(':checked');
		var baja = jQuery("#<portlet:namespace/>baja_filtro").is(':checked');

		if(cuil == '' && estado == '' && id == '' && idAutorizacion == ''
				&& fechaDesdeDia.value == '' && fechaDesdeMes.value == '' && fechaDesdeAnio.value == ''
				&& seccional == '' && fechaDesdeEmailDia.value == '' && fechaDesdeEmailMes.value == '' && fechaDesdeEmailAnio.value == ''){

			alert("Debe ingresar algún criterio de búsqueda");
			return false;
		}


		var url = '/xlsservlet/?reporte=REPORTE_PREAUTORIZACION';

		url += '&cuil=' + encodeURIComponent(cuil);
		url += '&inte=' + encodeURIComponent(inte);
		url += '&fechadesdedia=' + encodeURIComponent(fechaDesdeDia.value);
		url += '&fechadesdemes=' + encodeURIComponent(fechaDesdeMes.value);
		url += '&fechadesdeanio=' + encodeURIComponent(fechaDesdeAnio.value);
		url += '&estado=' + encodeURIComponent(estado);
		url += '&id=' + encodeURIComponent(id);
		url += '&idAutorizacion=' + encodeURIComponent(idAutorizacion);
		url += '&fechahastadia=' + encodeURIComponent(fechaHastaDia.value);
		url += '&fechahastames=' + encodeURIComponent(fechaHastaMes.value);
		url += '&fechahastaanio=' + encodeURIComponent(fechaHastaAnio.value);
		url += '&fechadesdeemaildia=' + encodeURIComponent(fechaDesdeEmailDia.value);
		url += '&fechadesdeemailmes=' + encodeURIComponent(fechaDesdeEmailMes.value);
		url += '&fechadesdeemailanio=' + encodeURIComponent(fechaDesdeEmailAnio.value);
		url += '&fechahastaemaildia=' + encodeURIComponent(fechaHastaEmailDia.value);
		url += '&fechahastaemailmes=' + encodeURIComponent(fechaHastaEmailMes.value);
		url += '&fechahastaemailanio=' + encodeURIComponent(fechaHastaEmailAnio.value);
		url += '&seccional=' + encodeURIComponent(seccional);
		url += '&alertaroja=' + alertaRoja;
		url += '&supra=' + supra;
		url += '&cirugia=' + cirugia;
		url += '&medicamento=' + medicamento;
		url += '&sin_reintento=' + sinReintento;
		url += '&discapacidad=' + discapacidad;
		url += '&alojamiento=' + alojamiento;
		url += '&protesisOrtesis=' + protesisOrtesis;
		url += '&posibleart=' + art;
		url += '&diabetes=' + diabetes;
		url += '&baja=' + baja;
		window.location.href = url;
	}

	function <portlet:namespace />procesaArchivoPreAutorizacion() {
		var params = "&<%= Constants.CMD %>=" + "procesaArchivo";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>

<style>
	#<portlet:namespace/>divAfiliadosSeguimientoSurFiltro fieldset,
	#<portlet:namespace/>divFiltrosPreautorizaciones fieldset {
		width: 100%;
		box-sizing: border-box;
		padding: 10px;
		margin: 0 auto 10px auto;
	}

#<portlet:namespace/>divAfiliadosSeguimientoSurFiltro table,
	#<portlet:namespace/>divFiltrosPreautorizaciones table {
		width: 100%;
		border-collapse: separate;
		border-spacing: 5px;
	}
</style>

<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<liferay-ui:success key="saldo_diario_agregado" message="grabar-exitoso" />

<div id="<portlet:namespace />borradoOk" style="display:none;"></div>

<%
	String mensajeError = ParamUtil.getString(request, "mensaje_error");

	if (mensajeError != null && !mensajeError.trim().equals("")) {
%>
	<div class="portlet-msg-error">
		<%= mensajeError %>
	</div>
<%
	}
%>

<form action="" method="post" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">

<%
	String portlet_name = "tesoreria";

	List<CuentaBancaria> ctas = null;

	ctas = (ArrayList<CuentaBancaria>) portletSession.getAttribute(
		WebKeysTesoreria.CUENTAS_BCRIAS,
		PortletSession.APPLICATION_SCOPE
	);

	if (ctas == null) {
		ctas = TraeListasServiceUtil.getCtasBcrias();

		portletSession.setAttribute(
			WebKeysTesoreria.CUENTAS_BCRIAS,
			ctas,
			PortletSession.APPLICATION_SCOPE
		);
	}

	Calendar fechaBase = CalendarFactoryUtil.getCalendar();
	fechaBase.setTime(new Date());

	Calendar fechaAgregar = CalendarFactoryUtil.getCalendar();
	fechaAgregar.setTime(new Date());

	boolean conservarFiltros = ParamUtil.getBoolean(request, "buscar_luego");

	int fechaDesdeDiaValue = fechaBase.get(Calendar.DATE);
	int fechaDesdeMesValue = fechaBase.get(Calendar.MONTH);
	int fechaDesdeAnioValue = fechaBase.get(Calendar.YEAR);

	int fechaHastaDiaValue = fechaBase.get(Calendar.DATE);
	int fechaHastaMesValue = fechaBase.get(Calendar.MONTH);
	int fechaHastaAnioValue = fechaBase.get(Calendar.YEAR);

	if (conservarFiltros) {
		fechaDesdeDiaValue = ParamUtil.getInteger(request, "fechaDesdeDia");
		fechaDesdeMesValue = ParamUtil.getInteger(request, "fechaDesdeMes", -1);
		fechaDesdeAnioValue = ParamUtil.getInteger(request, "fechaDesdeAnio");

		fechaHastaDiaValue = ParamUtil.getInteger(request, "fechaHastaDia");
		fechaHastaMesValue = ParamUtil.getInteger(request, "fechaHastaMes", -1);
		fechaHastaAnioValue = ParamUtil.getInteger(request, "fechaHastaAnio");
	}

	List<String> errores = (List<String>) request.getAttribute("errores");

	if (errores != null && !errores.isEmpty()) {
%>
	<table class="lfr-table">
<%
		for (String error : errores) {
%>
		<tr>
			<td><%= error %></td>
		</tr>
<%
		}
%>
	</table>
<%
	}

	int ctaBancariaBuscarSeleccionada = ParamUtil.getInteger(request, "cta_bancaria_buscar");
%>

	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td width="50%" valign="top" style="padding-right:10px;">

				<fieldset class="block-labels">
					<legend>Buscar saldo</legend>

					<table class="lfr-table">
						<tr>
							<td style="padding-bottom: 10px;">
								<label>Cuenta bancaria:</label>
							</td>
							<td>
								<select name="<portlet:namespace/>cta_bancaria_buscar" id="<portlet:namespace/>cta_bancaria_buscar">
									<option value="">TODAS</option>

									<%
										for (CuentaBancaria ctaBcria : ctas) {
											if (ctaBcria.getEntidad().equals("O")) {
									%>
												<option 
													value="<%= ctaBcria.getId_cuenta_bcria() %>"
													<%= ctaBancariaBuscarSeleccionada == ctaBcria.getId_cuenta_bcria() ? "selected" : "" %>>
													<%= ctaBcria.getCtaBcriaAsString() %>
												</option>
									<%
											}
										}
									%>
								</select>
							</td>
						</tr>

						<tr>
							<td style="padding-bottom: 10px;">
								<label>Fecha desde:</label>
							</td>
							<td>
								<liferay-ui:input-date
									dayParam="fechaDesdeDia"
									dayValue="<%= fechaDesdeDiaValue %>"
									dayNullable="<%= true %>"
									monthParam="fechaDesdeMes"
									monthValue="<%= fechaDesdeMesValue %>"
									monthNullable="<%= true %>"
									yearParam="fechaDesdeAnio"
									yearValue="<%= fechaDesdeAnioValue %>"
									yearNullable="<%= true %>"
									yearRangeStart="<%= fechaBase.get(Calendar.YEAR) - 120 %>"
									yearRangeEnd="<%= fechaBase.get(Calendar.YEAR) + 120 %>"
									firstDayOfWeek="<%= fechaBase.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
							</td>
						</tr>

						<tr>
							<td style="padding-bottom: 10px;">
								<label>Fecha hasta:</label>
							</td>
							<td>
								<liferay-ui:input-date
									dayParam="fechaHastaDia"
									dayValue="<%= fechaHastaDiaValue %>"
									dayNullable="<%= true %>"
									monthParam="fechaHastaMes"
									monthValue="<%= fechaHastaMesValue %>"
									monthNullable="<%= true %>"
									yearParam="fechaHastaAnio"
									yearValue="<%= fechaHastaAnioValue %>"
									yearNullable="<%= true %>"
									yearRangeStart="<%= fechaBase.get(Calendar.YEAR) - 120 %>"
									yearRangeEnd="<%= fechaBase.get(Calendar.YEAR) + 120 %>"
									firstDayOfWeek="<%= fechaBase.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
							</td>
						</tr>

						<tr>
							<td>&nbsp;</td>
							<td>
								<input
									id="<portlet:namespace />buscar"
									value="Buscar"
									title="Buscar"
									type="button"
									onClick="javascript:<portlet:namespace />buscarSaldos();" />
							</td>
						</tr>
					</table>
				</fieldset>

			</td>

			<td width="50%" valign="top" style="padding-left:10px;">

				<fieldset class="block-labels">
					<legend>Agregar saldo</legend>

					<table class="lfr-table">
						<tr>
							<td style="padding-bottom: 10px;">
								<label>Cuenta bancaria:</label>
							</td>
							<td>
								<select name="<portlet:namespace/>cta_bancaria_agregar" id="<portlet:namespace/>cta_bancaria_agregar">
									<option value="">Seleccione</option>

									<%
										for (CuentaBancaria ctaBcria : ctas) {
											if (ctaBcria.getEntidad().equals("O")) {
									%>
												<option value="<%= ctaBcria.getId_cuenta_bcria() %>">
													<%= ctaBcria.getCtaBcriaAsString() %>
												</option>
									<%
											}
										}
									%>
								</select>
							</td>
						</tr>

						<tr>
							<td style="padding-bottom: 10px;">
								<label>Saldo:</label>
							</td>
							<td>
								<input
									type="text"
									name="<portlet:namespace/>saldo"
									id="<portlet:namespace/>saldo"
									size="20"
									maxlength="20"
									value="" />
							</td>
						</tr>

						<tr>
							<td style="padding-bottom: 10px;">
								<label>Fecha:</label>
							</td>
							<td>
								<liferay-ui:input-date
									dayParam="fechaAgregarDia"
									dayValue="<%= fechaAgregar.get(Calendar.DATE) %>"
									dayNullable="<%= false %>"
									monthParam="fechaAgregarMes"
									monthValue="<%= fechaAgregar.get(Calendar.MONTH) %>"
									monthNullable="<%= false %>"
									yearParam="fechaAgregarAnio"
									yearValue="<%= fechaAgregar.get(Calendar.YEAR) %>"
									yearNullable="<%= false %>"
									yearRangeStart="<%= fechaAgregar.get(Calendar.YEAR) - 120 %>"
									yearRangeEnd="<%= fechaAgregar.get(Calendar.YEAR) + 120 %>"
									firstDayOfWeek="<%= fechaAgregar.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />
							</td>
						</tr>

						<tr>
							<td>&nbsp;</td>
							<td style="padding-bottom: 10px;">
								<input
									id="<portlet:namespace />agregar"
									value="Agregar"
									title="Agregar"
									type="button"
									onClick="javascript:<portlet:namespace />agregarSaldo();" />
							</td>
						</tr>
					</table>
				</fieldset>

			</td>
		</tr>
	</table>

	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
			<table style="align:center;">
				<tr>
					<td>Buscando</td>
					<td align="center">
						<img
							alt="Buscando"
							src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
		</div>

		<div align="center" id="<portlet:namespace />resultadoSaldosDiv">
		</div>
	</fieldset>

</form>

<%
	boolean buscarLuego = ParamUtil.getBoolean(request, "buscar_luego");
%>

<script type="text/javascript">

jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />buscarSaldos() {
	var desde_dia = jQuery("#<portlet:namespace/>fechaDesdeDia").val();
	var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var hasta_dia = jQuery("#<portlet:namespace/>fechaHastaDia").val();
	var hasta_mes = jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio = jQuery("#<portlet:namespace/>fechaHastaAnio").val();

	var cta_bcria = jQuery("#<portlet:namespace/>cta_bancaria_buscar").val();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%= portlet_name %>/buscar_saldo_diario_cuentas_bancarias';

	jQuery('#<portlet:namespace />buscando').show();

	jQuery("#<portlet:namespace/>resultadoSaldosDiv").load(
		url,
		{
			cmd: 'buscar',
			desde_dia: desde_dia,
			desde_mes: desde_mes,
			desde_anio: desde_anio,
			hasta_dia: hasta_dia,
			hasta_mes: hasta_mes,
			hasta_anio: hasta_anio,
			cta_bcria: cta_bcria
		},
		function() {
			jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}

function <portlet:namespace />agregarSaldo() {
	var cta_bcria = jQuery("#<portlet:namespace/>cta_bancaria_agregar").val();
	var saldo = jQuery("#<portlet:namespace/>saldo").val();

	if (cta_bcria == null || cta_bcria == '') {
		alert('Debe seleccionar una cuenta bancaria.');
		return false;
	}

	if (saldo == null || saldo == '') {
		alert('Debe ingresar un saldo.');
		return false;
	}

	var saldoNormalizado = saldo.replace(',', '.');

	if (isNaN(saldoNormalizado)) {
		alert('Importe o saldo inválido.');
		return false;
	}

	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%= portlet_name %>/buscar_saldo_diario_cuentas_bancarias&cmd=agregar';

	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

function borrarSaldoCuentaBancaria(idCuentaBcria, fechaInicioEjercicio) {
	var desde_dia = jQuery("#<portlet:namespace/>fechaDesdeDia").val();
	var desde_mes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();

	var hasta_dia = jQuery("#<portlet:namespace/>fechaHastaDia").val();
	var hasta_mes = jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio = jQuery("#<portlet:namespace/>fechaHastaAnio").val();

	var cta_bcria = jQuery("#<portlet:namespace/>cta_bancaria_buscar").val();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%= portlet_name %>/buscar_saldo_diario_cuentas_bancarias';

	if (confirm('¿Está seguro que desea borrar este registro?')) {
		jQuery('#<portlet:namespace />buscando').show();

		jQuery("#<portlet:namespace/>resultadoSaldosDiv").load(
			url,
			{
				cmd: 'borrar',
				id_cuenta_bcria: idCuentaBcria,
				fecha_inicio_ejercicio: fechaInicioEjercicio,
				desde_dia: desde_dia,
				desde_mes: desde_mes,
				desde_anio: desde_anio,
				hasta_dia: hasta_dia,
				hasta_mes: hasta_mes,
				hasta_anio: hasta_anio,
				cta_bcria: cta_bcria
			},
			function() {
				jQuery('#<portlet:namespace />buscando').hide();

				jQuery('#<portlet:namespace />borradoOk')
					.removeClass('portlet-msg-error')
					.addClass('portlet-msg-success')
					.html('Registro eliminado correctamente.')
					.show();

				setTimeout(function() {
					jQuery('#<portlet:namespace />borradoOk').fadeOut('slow');
				}, 4000);
			}
		);
	}
}

<%
	if (buscarLuego) {
%>
	jQuery(document).ready(function() {
		<portlet:namespace />buscarSaldos();
	});
<%
	}
%>

jQuery(document).ready(function() {
	setTimeout(function() {
		jQuery('.portlet-msg-success').fadeOut('slow');
		jQuery('.portlet-msg-error').fadeOut('slow');
	}, 4000);
});

</script>
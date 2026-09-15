<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AportesContribuciones" %>

<portlet:defineObjects/>

<%
	String modo = (String)renderRequest.getAttribute("modo");

	if (modo == null || modo.trim().equals("")) {
		modo = ParamUtil.getString(renderRequest, "modo", "nuevo");
	}

	AportesContribuciones tope = (AportesContribuciones)renderRequest.getAttribute("AportesContribuciones");

	SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");

	Calendar calDesde = CalendarFactoryUtil.getCalendar();
	calDesde.set(Calendar.DAY_OF_MONTH, 1);

	Calendar calHasta = CalendarFactoryUtil.getCalendar();
	calHasta.set(Calendar.DAY_OF_MONTH, calHasta.getActualMaximum(Calendar.DAY_OF_MONTH));

	String remuneracion = "";
	String fechaDesdeOriginal = "";
	String remuneracionMinima = "";

	if (tope != null) {

		if (tope.getFechaDesde() != null) {
			calDesde.setTime(tope.getFechaDesde());
		}

		if (tope.getFechaHasta() != null) {
			calHasta.setTime(tope.getFechaHasta());
		}

		if (tope.getTopeRemuneracion() != null) {
			remuneracion = tope.getTopeRemuneracion().toPlainString();
		}

		if (tope.getFechaDesde() != null) {
			fechaDesdeOriginal = sdfFecha.format(tope.getFechaDesde());
		}

		if (tope.getTopeAporte() != null) {
			remuneracionMinima = tope.getTopeAporte().toPlainString();
		}
	}
	
	String remuneracionMinimaRequest = ParamUtil.getString(renderRequest, "topeAporte");

	if (remuneracionMinimaRequest != null && !remuneracionMinimaRequest.trim().equals("")) {
		remuneracionMinima = remuneracionMinimaRequest;
	}

	int fechaDesdeDia = ParamUtil.getInteger(renderRequest, "fechaDesdeDia", calDesde.get(Calendar.DAY_OF_MONTH));
	int fechaDesdeMes = ParamUtil.getInteger(renderRequest, "fechaDesdeMes", calDesde.get(Calendar.MONTH));
	int fechaDesdeAnio = ParamUtil.getInteger(renderRequest, "fechaDesdeAnio", calDesde.get(Calendar.YEAR));
	int fechaHastaDia = ParamUtil.getInteger(renderRequest, "fechaHastaDia", calHasta.get(Calendar.DAY_OF_MONTH));
	int fechaHastaMes = ParamUtil.getInteger(renderRequest, "fechaHastaMes", calHasta.get(Calendar.MONTH));
	int fechaHastaAnio = ParamUtil.getInteger(renderRequest, "fechaHastaAnio", calHasta.get(Calendar.YEAR));

	String remuneracionRequest = ParamUtil.getString(renderRequest, "topeRemuneracion");

	if (remuneracionRequest != null && !remuneracionRequest.trim().equals("")) {
		remuneracion = remuneracionRequest;
	}

	boolean editando = "editar".equals(modo);
%>

<portlet:actionURL var="guardarURL">
	<portlet:param name="struts_action" value="/tesoreria/aportes_contribuciones"/>
</portlet:actionURL>

<portlet:renderURL var="volverListadoURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
	<portlet:param name="struts_action" value="/tesoreria/view"/>
	<portlet:param name="tabs1" value="tabla-aportes-contrib"/>
</portlet:renderURL>

<liferay-ui:error key="error-guardar-tope-aportes" message="No se pudo guardar el registro. Verifique los datos ingresados."/>
<liferay-ui:error key="periodo-ya-cargado" message="El período ya fue cargado."/>
<liferay-ui:error key="remuneracion-maxima-requerida" message="Debe ingresar la remuneración máxima."/>
<liferay-ui:error key="remuneracion-maxima-mayor-cero" message="La remuneración máxima debe ser mayor a 0."/>
<liferay-ui:error key="remuneracion-minima-requerida" message="Debe ingresar la remuneración mínima."/>
<liferay-ui:error key="remuneracion-minima-mayor-cero" message="La remuneración mínima debe ser mayor a 0."/>
<liferay-ui:error key="fecha-periodo-invalida" message="La Fecha Desde no puede ser mayor que la Fecha Hasta."/>
<liferay-ui:error key="remuneracion-minima-mayor-maxima" message="La remuneración mínima no puede ser mayor que la remuneración máxima."/>

<form action="<%= guardarURL %>" method="post" name="<portlet:namespace />fm">
	<input
		type="hidden"
		name="<portlet:namespace />redirect"
		value="<%= volverListadoURL %>"
	/>
	
	<input
		type="hidden"
		name="<portlet:namespace />modo"
		value="<%= modo %>"
	/>
	
	<input
		type="hidden"
		name="<portlet:namespace />fechaDesdeOriginal"
		value="<%= fechaDesdeOriginal %>"
	/>

	<fieldset class="block-labels">

		<legend>
			<%
			if (editando) {
			%>
				Editar Aportes y Contribuciones
			<%
			} else {
			%>
				Nuevo Aporte y Contribución
			<%
			}
			%>
		</legend>

		<table class="lfr-table">
			<tr>
				<td>
					<label>Fecha Desde:</label>
				</td>

				<td>

					<liferay-ui:input-date
						dayParam="fechaDesdeDia"
						monthParam="fechaDesdeMes"
						yearParam="fechaDesdeAnio"
						dayValue="<%= fechaDesdeDia %>"
						monthValue="<%= fechaDesdeMes %>"
						yearValue="<%= fechaDesdeAnio %>"
						yearRangeStart="<%= CalendarFactoryUtil.getCalendar().get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= CalendarFactoryUtil.getCalendar().get(Calendar.YEAR) + 20 %>"
						dayNullable="false"
						monthNullable="false"
						yearNullable="false"
						disabled="<%= editando %>"
					/>
				</td>

				<td style="padding-left:30px;">
					<label>Fecha Hasta:</label>
				</td>

				<td>
					<liferay-ui:input-date
						dayParam="fechaHastaDia"
						monthParam="fechaHastaMes"
						yearParam="fechaHastaAnio"
						dayValue="<%= fechaHastaDia %>"
						monthValue="<%= fechaHastaMes %>"
						yearValue="<%= fechaHastaAnio %>"
						yearRangeStart="<%= CalendarFactoryUtil.getCalendar().get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= CalendarFactoryUtil.getCalendar().get(Calendar.YEAR) + 20 %>"
						dayNullable="false"
						monthNullable="false"
						yearNullable="false"
						disabled="<%= editando %>"
					/>
				</td>
			</tr>

			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>

			<tr>
				<td>
					<label>Remuneración máxima:</label>
				</td>

				<td colspan="3">
					<input
						type="text"
						id="<portlet:namespace />topeRemuneracion"
						name="<portlet:namespace />topeRemuneracion"
						value="<%= remuneracion %>"
						size="25"
						maxlength="20"
						onkeyup="<portlet:namespace />validarImporte(this); <portlet:namespace />calcular();"
						onchange="<portlet:namespace />calcular();"
					/>
				</td>
			</tr>

			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>

			<tr>
				<td>
					<label>Remuneración mínima:</label>
				</td>

				<td colspan="3">
					<input
						type="text"
						id="<portlet:namespace />topeAporte"
						name="<portlet:namespace />topeAporte"
						value="<%= remuneracionMinima %>"
						size="25"
						maxlength="20"
						onkeyup="<portlet:namespace />validarImporte(this);"
					/>
				</td>
			</tr>

			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>

			<tr>
				<td>
					<label>Aporte real (2,55%):</label>
				</td>

				<td colspan="3">
					<input
						type="text"
						id="<portlet:namespace />aporteReal"
						size="25"
						readonly="readonly"
					/>
				</td>
			</tr>
		</table>
		
		<br/>

		<table>
			<tr>
				<td>
					<input type="submit" value="Guardar"/>
				</td>

				<td>
					&nbsp;&nbsp;
				</td>

				<td>
					<input
						type="button"
						value="Cancelar"
						onclick="window.location.href='<%= volverListadoURL %>';"
					/>
				</td>
			</tr>
		</table>
	</fieldset>
</form>


<script type="text/javascript">

function <portlet:namespace />parseNumero(valor) {

	if (!valor) {
		return 0;
	}

	valor = valor.replace(/\$/g, '').replace(/\s/g, '');

	if (valor.indexOf('.') >= 0 && valor.indexOf(',') >= 0) {
		valor = valor.replace(/\./g, '');
		valor = valor.replace(',', '.');
	} else if (valor.indexOf(',') >= 0) {
		valor = valor.replace(',', '.');
	}

	var numero = parseFloat(valor);

	if (isNaN(numero)) {
		return 0;
	}

	return numero;
}

function <portlet:namespace />formatear(numero) {

	return numero.toLocaleString(
		'es-AR',
		{
			minimumFractionDigits: 2,
			maximumFractionDigits: 2
		}
	);
}

function <portlet:namespace />validarImporte(input) {
	input.value = input.value.replace(
		/[^0-9.,]/g,
		''
	);
}

function <portlet:namespace />calcular() {

	var remuneracion = document.getElementById('<portlet:namespace />topeRemuneracion').value;
	var numero = <portlet:namespace />parseNumero(remuneracion);
	var aporteReal = numero * 0.0255;

	document.getElementById('<portlet:namespace />aporteReal').value = <portlet:namespace />formatear(aporteReal);
}

<portlet:namespace />calcular();

</script>
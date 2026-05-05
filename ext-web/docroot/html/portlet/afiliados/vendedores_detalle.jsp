<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %>

<portlet:defineObjects />

<%
Map<String,Object> vendedor = (Map<String,Object>) request.getAttribute("vendedor");
if (vendedor == null) vendedor = new HashMap<String,Object>();

List<Map<String,Object>> historico = (List<Map<String,Object>>) request.getAttribute("historico");
if (historico == null) historico = new ArrayList<Map<String,Object>>();

SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

String id = vendedor.get("id") != null ? String.valueOf(vendedor.get("id")) : "";
String nombre = vendedor.get("nombre") != null ? String.valueOf(vendedor.get("nombre")) : "";
String apellido = vendedor.get("apellido") != null ? String.valueOf(vendedor.get("apellido")) : "";
String dni = vendedor.get("dni") != null ? String.valueOf(vendedor.get("dni")) : "";
String email = vendedor.get("email") != null ? String.valueOf(vendedor.get("email")) : "";

String bajaFecha = "";
if (vendedor.get("baja_fecha") instanceof Timestamp) {
    bajaFecha = sdfFechaHora.format((Timestamp) vendedor.get("baja_fecha"));
}

Calendar cal = CalendarFactoryUtil.getCalendar();

int histDesdeDiaVal = cal.get(Calendar.DAY_OF_MONTH);
int histDesdeMesVal = cal.get(Calendar.MONTH);
int histDesdeAnioVal = cal.get(Calendar.YEAR);

int histHastaDiaVal = cal.get(Calendar.DAY_OF_MONTH);
int histHastaMesVal = cal.get(Calendar.MONTH);
int histHastaAnioVal = cal.get(Calendar.YEAR);

String editIcon = themeDisplay.getPathThemeImages() + "/common/edit.png";
String deleteIcon = themeDisplay.getPathThemeImages() + "/common/delete.png";
String volverIcon = themeDisplay.getPathThemeImages() + "/arrows/01_left.png";

String horaDesde = vendedor.get("hora_desde") != null ? String.valueOf(vendedor.get("hora_desde")) : "";
if (horaDesde.length() >= 5) horaDesde = horaDesde.substring(0,5);

String horaHasta = vendedor.get("hora_hasta") != null ? String.valueOf(vendedor.get("hora_hasta")) : "";
if (horaHasta.length() >= 5) horaHasta = horaHasta.substring(0,5);
%>

<liferay-ui:success key="vendedor_guardado_ok" message="Vendedor guardado correctamente." />
<liferay-ui:error key="vendedor_guardado_error" message="No se pudo guardar el vendedor." />
<liferay-ui:success key="historico_guardado_ok" message="Histórico guardado correctamente." />
<liferay-ui:error key="historico_guardado_error" message="No se pudo guardar el histórico." />
<liferay-ui:error key="historico_fechas_obligatorias" message="Debe seleccionar fecha desde y fecha hasta." />
<liferay-ui:error key="vendedor_dni_duplicado" message="Ya existe un vendedor con ese DNI." />
<liferay-ui:error key="vendedor_datos_obligatorios" message="Debe completar nombre, apellido y DNI." />
<liferay-ui:error key="vendedor_dni_invalido" message="El DNI debe tener entre 5 y 8 dígitos numéricos." />
<liferay-ui:error key="vendedor_nombre_invalido" message="El nombre solo puede contener letras." />
<liferay-ui:error key="vendedor_apellido_invalido" message="El apellido solo puede contener letras." />
<liferay-ui:error key="historico_fechas_obligatorias" message="Debe seleccionar fecha desde y fecha hasta." />
<liferay-ui:error key="historico_rango_fechas_invalido" message="La fecha desde no puede ser posterior a la fecha hasta." />
<liferay-ui:error key="vendedor_email_obligatorio" message="Debe completar el email." />
<liferay-ui:error key="vendedor_email_invalido" message="El email no es válido." />
<liferay-ui:error key="vendedor_horario_obligatorio" message="Debe completar el horario desde y hasta." />
<liferay-ui:error key="vendedor_horario_invalido" message="El horario debe tener formato HH:mm." />
<liferay-ui:error key="vendedor_horario_rango_invalido" message="La hora desde debe ser menor que la hora hasta." />

<portlet:actionURL var="guardarVendedorURL">
    <portlet:param name="struts_action" value="/afiliados/vendedor" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="cmd" value="guardar" />
</portlet:actionURL>

<portlet:actionURL var="guardarHistoricoURL">
    <portlet:param name="struts_action" value="/afiliados/vendedor" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="cmd" value="guardarHistorico" />
</portlet:actionURL>

<%
String venNombre = ParamUtil.getString(request, "nombre", "");
String venApellido = ParamUtil.getString(request, "apellido", "");
String venDni = ParamUtil.getString(request, "dni", "");
%>

<portlet:renderURL var="volverURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/afiliados/view" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="autoBuscar" value="1" />
    <portlet:param name="nombre" value="<%= venNombre %>" />
    <portlet:param name="apellido" value="<%= venApellido %>" />
    <portlet:param name="dni" value="<%= venDni %>" />
</portlet:renderURL>

<portlet:renderURL var="editarVendedorURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
    <portlet:param name="struts_action" value="/afiliados/vendedor" />
    <portlet:param name="tabs1" value="vendedores" />
    <portlet:param name="cmd" value="editar" />
    <portlet:param name="id" value="<%= id %>" />
    <portlet:param name="nombre" value="<%= venNombre %>" />
    <portlet:param name="apellido" value="<%= venApellido %>" />
    <portlet:param name="dni" value="<%= venDni %>" />
</portlet:renderURL>

<div style="width:100%; text-align:right; margin:0 5px 10px 0;">
    <a href="<%= volverURL %>" style="color:#418a4e; font-weight:bold; text-decoration:underline;">
        <img src="<%= volverIcon %>" alt="Volver" style="vertical-align:middle; margin-right:4px; border:0;" />
        Volver
    </a>
</div>

<fieldset class="block-labels">
    <legend><%= Validator.isNotNull(id) ? "Editar vendedor" : "Nuevo vendedor" %></legend>
    <form action="<%= guardarVendedorURL %>" method="post" name="<portlet:namespace />fmVendedor" onsubmit="return <portlet:namespace />validarVendedor();">
        <input type="hidden" name="<portlet:namespace />id" value="<%= id %>" />
		<input type="hidden" name="<portlet:namespace />redirect" value="<%= editarVendedorURL %>" />
		
        <table class="lfr-table ven-form-table">
            <tr>
                <td><label>Nombre</label></td>
                <td><input type="text" name="<portlet:namespace />nombre" value="<%= nombre %>" /></td>
            </tr>

            <tr>
                <td><label>Apellido</label></td>
                <td><input type="text" name="<portlet:namespace />apellido" value="<%= apellido %>" /></td>
            </tr>

            <tr>
                <td><label>DNI</label></td>
                <td><input type="text" name="<portlet:namespace />dni" value="<%= dni %>" /></td>
            </tr>

			<tr>
			    <td><label>Email</label></td>
			    <td><input type="text" name="<portlet:namespace />email" value="<%= email %>" /></td>
			</tr>
			
			<tr>
    <td><label>Horario desde</label></td>
    <td>
        <input type="text"
               name="<portlet:namespace />horaDesde"
               value="<%= horaDesde %>" />
    </td>
</tr>

<tr>
    <td><label>Horario hasta</label></td>
    <td>
        <input type="text"
               name="<portlet:namespace />horaHasta"
               value="<%= horaHasta %>" />
    </td>
</tr>

            <% if (Validator.isNotNull(bajaFecha)) { %>
            <tr>
                <td><b>Baja fecha</b></td>
                <td><%= bajaFecha %></td>
            </tr>
            <% } %>

            <tr>
                <td colspan="2" align="left" class="ven-actions">
                    <input type="submit" value="Guardar" />
                </td>
            </tr>
        </table>
    </form>
</fieldset>

<% if (Validator.isNotNull(id)) { %>
<br/>

<fieldset class="block-labels">
    <legend>Agregar ausencia</legend>

    <form action="<%= guardarHistoricoURL %>" method="post" name="<portlet:namespace />fmHistorico" onsubmit="return <portlet:namespace />validarHistorico();">
        <input type="hidden" name="<portlet:namespace />idVendedor" value="<%= id %>" />
        <input type="hidden" name="<portlet:namespace />idHistorico" id="<portlet:namespace />idHistorico" value="" />
		<input type="hidden" name="<portlet:namespace />redirect" value="<%= editarVendedorURL %>" />
        
        <table class="lfr-table ven-form-table">
            <tr>
                <td><label>Fecha Desde:</label></td>
                <td>
                    <liferay-ui:input-date
                        dayParam="histDesdeDia" monthParam="histDesdeMes" yearParam="histDesdeAnio"
                        dayValue="<%= histDesdeDiaVal %>" monthValue="<%= histDesdeMesVal %>" yearValue="<%= histDesdeAnioVal %>"
                        yearRangeStart="<%= cal.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= cal.get(Calendar.YEAR) + 20 %>"
                        dayNullable="false" monthNullable="false" yearNullable="false" disabled="<%= false %>"
                    />
                </td>

                <td style="padding-left:20px;"><label>Fecha Hasta:</label></td>
                <td>
                    <liferay-ui:input-date
                        dayParam="histHastaDia" monthParam="histHastaMes" yearParam="histHastaAnio"
                        dayValue="<%= histHastaDiaVal %>" monthValue="<%= histHastaMesVal %>" yearValue="<%= histHastaAnioVal %>"
                        yearRangeStart="<%= cal.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= cal.get(Calendar.YEAR) + 20 %>"
                        dayNullable="false" monthNullable="false" yearNullable="false" disabled="<%= false %>"
                    />
                </td>
            </tr>

            <tr>
                <td><label>Motivo</label></td>
                <td>
                    <select name="<portlet:namespace />motivo" id="<portlet:namespace />motivo">
                        <option value="VACACIONES">Vacaciones</option>
                        <option value="LICENCIA">Licencia</option>
                        <option value="OTRO">Otro</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td><label>Observación</label></td>
                <td colspan="3">
                    <input type="text" name="<portlet:namespace />observacion" id="<portlet:namespace />observacion" size="60" />
                </td>
            </tr>

            <tr>
                <td colspan="4" align="left" class="ven-actions">
                    <input type="submit" id="<portlet:namespace />btnGuardarHistorico" value="Guardar" />
					<input type="button" id="<portlet:namespace />btnCancelarHistorico" value="Cancelar" style="display:none;" onclick="<portlet:namespace />cancelarEdicionHistorico();" />
                </td>
            </tr>
        </table>
    </form>
</fieldset>

<br/>

<fieldset class="block-labels">
    <legend>Histórico</legend>

    <% if (historico.isEmpty()) { %>
        <div class="portlet-msg-info">No hay histórico cargado.</div>
    <% } else { %>
        <table class="lfr-table ven-historico-table" width="100%">
            <tr>               
                <th>Fecha desde</th>
                <th>Fecha hasta</th>
                <th>Motivo</th>
                <th>Observación</th>
                <th>Alta fecha</th>
                <th>Alta usr</th>
                <th>Baja fecha</th>
                <th>Baja usr</th>
                <th>Editar</th>
            </tr>

            <%
            for (Map<String,Object> h : historico) {
                String hId = String.valueOf(h.get("id"));

                String hAltaFecha = "";
                if (h.get("alta_fecha") instanceof Timestamp) {
                    hAltaFecha = sdfFechaHora.format((Timestamp) h.get("alta_fecha"));
                }

                String hBajaFecha = "";
                if (h.get("baja_fecha") instanceof Timestamp) {
                    hBajaFecha = sdfFechaHora.format((Timestamp) h.get("baja_fecha"));
                }

                
                String hFechaDesdeJs = "";
                if (h.get("fecha_desde") != null) {
                    hFechaDesdeJs = String.valueOf(h.get("fecha_desde"));
                }

                String hFechaHastaJs = "";
                if (h.get("fecha_hasta") != null) {
                    hFechaHastaJs = String.valueOf(h.get("fecha_hasta"));
                }

                String hFechaDesde = "";
                if (h.get("fecha_desde") instanceof java.util.Date) {
                    hFechaDesde = sdfFecha.format((java.util.Date) h.get("fecha_desde"));
                } else if (h.get("fecha_desde") != null) {
                    hFechaDesde = String.valueOf(h.get("fecha_desde"));
                }

                String hFechaHasta = "";
                if (h.get("fecha_hasta") instanceof java.util.Date) {
                    hFechaHasta = sdfFecha.format((java.util.Date) h.get("fecha_hasta"));
                } else if (h.get("fecha_hasta") != null) {
                    hFechaHasta = String.valueOf(h.get("fecha_hasta"));
                }

                String hMotivo = h.get("motivo") != null ? String.valueOf(h.get("motivo")) : "";
                String hObservacion = h.get("observacion") != null ? String.valueOf(h.get("observacion")) : "";
                Boolean editable = h.get("editable") != null ? (Boolean) h.get("editable") : Boolean.FALSE;

                String hAltaUsr = h.get("alta_usr") != null ? String.valueOf(h.get("alta_usr")) : "";
                String hBajaUsr = h.get("baja_usr") != null ? String.valueOf(h.get("baja_usr")) : "";
                
                boolean mostrarEditar = editable.booleanValue() && !Validator.isNotNull(hBajaFecha);

            %>
            <tr>                
                <td><%= hFechaDesde %></td>
                <td><%= hFechaHasta %></td>
                <td><%= hMotivo %></td>
                <td><%= hObservacion %></td>
                <td><%= hAltaFecha %></td>
                <td><%= hAltaUsr %></td>
                <td><%= hBajaFecha %></td>
                <td><%= hBajaUsr %></td>
                <td>
				    <% if (mostrarEditar) { %>
				        <a class="ven-icon-action" href="javascript:void(0);" onclick="<portlet:namespace />editarHistorico('<%= hId %>', '<%= hFechaDesdeJs %>', '<%= hFechaHastaJs %>', '<%= hMotivo %>', '<%= hObservacion.replace("'", "\\'") %>');" title="Editar">
				            <img src="<%= editIcon %>" alt="Editar" />
				        </a>
				    <% } else { %>
				        -
				    <% } %>
				</td>
            </tr>
            <% } %>
        </table>
    <% } %>
</fieldset>
<% } %>

<script type="text/javascript">
function <portlet:namespace />validarHistorico() {
    var desdeDia = parseInt(jQuery('#<portlet:namespace />histDesdeDia').val(), 10);
    var desdeMes = parseInt(jQuery('#<portlet:namespace />histDesdeMes').val(), 10);
    var desdeAnio = parseInt(jQuery('#<portlet:namespace />histDesdeAnio').val(), 10);

    var hastaDia = parseInt(jQuery('#<portlet:namespace />histHastaDia').val(), 10);
    var hastaMes = parseInt(jQuery('#<portlet:namespace />histHastaMes').val(), 10);
    var hastaAnio = parseInt(jQuery('#<portlet:namespace />histHastaAnio').val(), 10);

    if (!desdeDia || isNaN(desdeMes) || !desdeAnio || !hastaDia || isNaN(hastaMes) || !hastaAnio) {
        alert('Debe seleccionar fecha desde y fecha hasta.');
        return false;
    }

    var fechaDesde = new Date(desdeAnio, desdeMes, desdeDia);
    var fechaHasta = new Date(hastaAnio, hastaMes, hastaDia);

    if (fechaDesde > fechaHasta) {
        alert('La fecha desde no puede ser posterior a la fecha hasta.');
        return false;
    }

    return true;
}

function <portlet:namespace />editarHistorico(idHistorico, fechaDesde, fechaHasta, motivo, observacion) {
    jQuery('#<portlet:namespace />idHistorico').val(idHistorico);
    jQuery('#<portlet:namespace />motivo').val(motivo);
    jQuery('#<portlet:namespace />observacion').val(observacion);
    jQuery('#<portlet:namespace />btnGuardarHistorico').val('Guardar');
    jQuery('#<portlet:namespace />btnCancelarHistorico').show();

    if (fechaDesde) {
        var partesDesde = fechaDesde.split('-');
        if (partesDesde.length === 3) {
            jQuery('#<portlet:namespace />histDesdeAnio').val(partesDesde[0]);
            jQuery('#<portlet:namespace />histDesdeMes').val(parseInt(partesDesde[1], 10) - 1);
            jQuery('#<portlet:namespace />histDesdeDia').val(parseInt(partesDesde[2], 10));
        }
    }

    if (fechaHasta) {
        var partesHasta = fechaHasta.split('-');
        if (partesHasta.length === 3) {
            jQuery('#<portlet:namespace />histHastaAnio').val(partesHasta[0]);
            jQuery('#<portlet:namespace />histHastaMes').val(parseInt(partesHasta[1], 10) - 1);
            jQuery('#<portlet:namespace />histHastaDia').val(parseInt(partesHasta[2], 10));
        }
    }

    jQuery('#<portlet:namespace />motivo').focus();
}

function <portlet:namespace />cancelarEdicionHistorico() {
    jQuery('#<portlet:namespace />idHistorico').val('');
    jQuery('#<portlet:namespace />motivo').val('VACACIONES');
    jQuery('#<portlet:namespace />observacion').val('');
    jQuery('#<portlet:namespace />btnGuardarHistorico').val('Guardar');
    jQuery('#<portlet:namespace />btnCancelarHistorico').hide();

    jQuery('#<portlet:namespace />histDesdeDia').val('<%= histDesdeDiaVal %>');
    jQuery('#<portlet:namespace />histDesdeMes').val('<%= histDesdeMesVal %>');
    jQuery('#<portlet:namespace />histDesdeAnio').val('<%= histDesdeAnioVal %>');

    jQuery('#<portlet:namespace />histHastaDia').val('<%= histHastaDiaVal %>');
    jQuery('#<portlet:namespace />histHastaMes').val('<%= histHastaMesVal %>');
    jQuery('#<portlet:namespace />histHastaAnio').val('<%= histHastaAnioVal %>');
}

function <portlet:namespace />validarVendedor() {
    var nombre = jQuery.trim(jQuery('input[name="<portlet:namespace />nombre"]').val());
    var apellido = jQuery.trim(jQuery('input[name="<portlet:namespace />apellido"]').val());
    var dni = jQuery.trim(jQuery('input[name="<portlet:namespace />dni"]').val());

    var regexNombreApellido = /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' -]+$/;
    var regexDni = /^\d{5,8}$/;
    var email = jQuery.trim(jQuery('input[name="<portlet:namespace />email"]').val());
    var regexEmail = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
    
    if (!nombre) {
        alert('Debe ingresar el nombre.');
        return false;
    }

    if (!regexNombreApellido.test(nombre)) {
        alert('El nombre solo puede contener letras.');
        return false;
    }

    if (!apellido) {
        alert('Debe ingresar el apellido.');
        return false;
    }

    if (!regexNombreApellido.test(apellido)) {
        alert('El apellido solo puede contener letras.');
        return false;
    }

    if (!dni) {
        alert('Debe ingresar el DNI.');
        return false;
    }

    if (!regexDni.test(dni)) {
        alert('El DNI debe tener entre 5 y 8 dígitos numéricos.');
        return false;
    }

    if (!email) {
        alert('Debe ingresar el email.');
        return false;
    }

    if (!regexEmail.test(email)) {
        alert('El email no es válido.');
        return false;
    }
    
    var horaDesde = jQuery.trim(jQuery('input[name="<portlet:namespace />horaDesde"]').val());
    var horaHasta = jQuery.trim(jQuery('input[name="<portlet:namespace />horaHasta"]').val());
    var regexHora = /^([01]\d|2[0-3]):([0-5]\d)$/;

    if (!horaDesde || !horaHasta) {
        alert('Debe ingresar el horario desde y hasta.');
        return false;
    }

    if (!regexHora.test(horaDesde) || !regexHora.test(horaHasta)) {
        alert('El horario debe tener formato HH:mm.');
        return false;
    }

    if (horaDesde >= horaHasta) {
        alert('La hora desde debe ser menor que la hora hasta.');
        return false;
    }
    return true;
}
</script>

<style type="text/css">
    .ven-form-table td {
        padding: 12px 18px 12px 0;
        vertical-align: middle;
    }

    .ven-form-table input[type="text"] {
        width: 180px;
        margin: 0;
    }

    .ven-form-table tr {
        height: 34px;
    }

    .ven-actions {
        padding-top: 18px !important;
    }

    .ven-historico-table td,
    .ven-historico-table th {
        padding: 8px 12px;
        vertical-align: middle;
    }

    .ven-icon-action {
        margin-right: 8px;
    }
</style>
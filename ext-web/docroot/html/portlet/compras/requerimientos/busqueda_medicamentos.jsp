<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/utils/medicamentos/init.jsp" %>

<%
String searchURL = ParamUtil.getString(request, "search_url");
boolean esEditable = ParamUtil.getBoolean(request, "esEditable", false);
String troquel = ParamUtil.getString(request, "troquel", "");
String medicamento = ParamUtil.getString(
        request,
        "nombre_medicamento",
        ""
);
boolean popup = ParamUtil.getBoolean(request, "popup", false);
boolean mostrarConPresentacion = ParamUtil.getBoolean(
        request,
        "mostrar_con_presentacion",
        false
);
%>

<table>
    <tr>
        <td>
            <input id="<portlet:namespace />id_medicamento"
                   name="<portlet:namespace />id_medicamento"
                   type="hidden"
                   value=""
                <% if (!esEditable) { %>
                   readonly="readonly"
                <% } else { %>
                   onBlur="javascript:<portlet:namespace />pierdeFocoMd();"
                   onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event);"
                   onkeydown="allowOnlyDigits(event);"
                <% } %> />
            Medicamento:&nbsp;&nbsp;
        </td>
        <td>
            <input id="<portlet:namespace />nombre_medicamento"
                   name="<portlet:namespace />nombre_medicamento"
                   size="50"
                   type="text"
                   value=""
                <% if (!esEditable) { %>
                   readonly="readonly"
                <% } else { %>
                   onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event)"
                   onBlur="javascript:<portlet:namespace />pierdeFocoMd();"
                <% } %> />&nbsp;
        </td>
        <td>Troquel:&nbsp;&nbsp;</td>
        <td>
            <input id="<portlet:namespace />troquel"
                   name="<portlet:namespace />troquel"
                   maxlength="7"
                   size="7"
                   type="text"
                   value=""
                <% if (!esEditable) { %>
                   readonly="readonly"
                <% } else { %>
                   onBlur="javascript:<portlet:namespace />pierdeFocoMd();"
                   onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event);"
                <% } %> />
        </td>
        <td>
            <% if (esEditable) { %>
                <div id="<portlet:namespace />divBtnBuscaMedicamento">
                    <a href="javascript: void(0);"
                       onclick="javascript:<portlet:namespace />buscarMedicamento();"
                       tabindex="-1">Buscar</a>
                </div>
            <% } %>
        </td>
    </tr>
</table>

<div id="<portlet:namespace />divMedicamento" style="float:left;"></div>

<input id="<portlet:namespace />med_seleccionado"
       name="<portlet:namespace />med_seleccionado"
       type="hidden"
       value="" />

<script type="text/javascript">
var <portlet:namespace />popupMD;

function <portlet:namespace />buscarMedicamento() {
    var troquel =
            jQuery('#<portlet:namespace />troquel').val();
    var nombreMedicamento =
            jQuery('#<portlet:namespace />nombre_medicamento').val();

    if (troquel == null || troquel == '') {
        troquel = '0';
    }

    if (nombreMedicamento == null) {
        nombreMedicamento = '';
    }

    if (troquel == '0' && nombreMedicamento.length == 0) {
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    } else {
        <portlet:namespace />popupMD = Liferay.Popup({
            title: 'Búsqueda Medicamentos',
            modal: true,
            width: 700
        });

        var url =
                '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>'
                        + '&struts_action=<%= searchURL %>'
                        + '&troquel=' + troquel
                        + '&nombre_medicamento='
                        + encodeURI(nombreMedicamento)
                        + '&popup=<%= popup %>';

        jQuery(<portlet:namespace />popupMD).load(url);
    }
}

function <portlet:namespace />buscarMedicamentoOnDiv(e) {
    var evtobj = window.event ? event : e;
    var keyPressed =
            evtobj.keyCode ? evtobj.keyCode : evtobj.charCode;

    if (jQuery('#<portlet:namespace />med_seleccionado').val() == '1'
            && (keyPressed == 8 || keyPressed == 46)) {

        jQuery('#<portlet:namespace />id_medicamento').val('');
        jQuery('#<portlet:namespace />troquel').val('');
        jQuery('#<portlet:namespace />nombre_medicamento').val('');
        jQuery('#<portlet:namespace />med_seleccionado').val('');
        jQuery('#<portlet:namespace />divBtnBuscaMedicamento').show();
        <portlet:namespace />invalidarMedicamentoDetalle();

        return false;
    }

    var troquel =
            jQuery('#<portlet:namespace />troquel').val();
    var nombreMedicamento =
            jQuery('#<portlet:namespace />nombre_medicamento').val();

    if (troquel == null || troquel == '') {
        troquel = '0';
    }

    if (nombreMedicamento == null) {
        nombreMedicamento = '';
    }

    if (jQuery('#<portlet:namespace />med_seleccionado').val() != '1'
            && (nombreMedicamento.length >= 6 || troquel.length > 3)) {

        if (troquel.length > 3) {
            jQuery('#<portlet:namespace />id_medicamento').val('');
            jQuery('#<portlet:namespace />nombre_medicamento').val('');
        } else if (nombreMedicamento.length >= 6) {
            jQuery('#<portlet:namespace />troquel').val('');
            jQuery('#<portlet:namespace />id_medicamento').val('');
        }

        var url =
                '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>'
                        + '&struts_action=<%= searchURL %>'
                        + '&troquel=' + troquel
                        + '&nombre_medicamento='
                        + encodeURI(nombreMedicamento);

        jQuery('#<portlet:namespace />divMedicamento').load(url);
        jQuery('#<portlet:namespace />divMedicamento').show();
    } else {
        jQuery('#<portlet:namespace />divMedicamento').hide('slow');
    }
}

function <portlet:namespace />cerrarDivMd() {
    jQuery('#<portlet:namespace />divMedicamento').hide('slow');
}

function <portlet:namespace />cerrarMd() {
    <portlet:namespace />cerrarDivMd();

    if (<portlet:namespace />popupMD) {
        Liferay.Popup.close(<portlet:namespace />popupMD);
    }
}

function <portlet:namespace />pierdeFocoMd() {
    var seleccionada =
            jQuery('#<portlet:namespace />med_seleccionada').val();

    if (seleccionada == '1') {
        <portlet:namespace />cerrarDivPd();
        return false;
    }

    return false;
}

var <portlet:namespace />troquelJs = '<%= troquel %>';

if ('<%= String.valueOf(esEditable) %>' == 'true'
        && <portlet:namespace />troquelJs != '') {

    <portlet:namespace />buscarMedicamento();
}

function <portlet:namespace />pasarParametrosAParentMd(
        troquel,
        medicamento,
        id,
        presentacion) {

    <portlet:namespace />seleccionaCamposMd(
            id,
            troquel,
            medicamento,
            presentacion
    );

    <portlet:namespace />seleccionarMedicamentoDetalle(
            id,
            troquel,
            jQuery('#<portlet:namespace />nombre_medicamento').val(),
            presentacion
    );

    <portlet:namespace />cerrarMd();
}

function <portlet:namespace />seleccionaCamposMd(
        id,
        codigo,
        nombre,
        presentacion) {

    <% if (mostrarConPresentacion) { %>
        jQuery('#<portlet:namespace />nombre_medicamento').val(
                nombre + ' ' + presentacion
        );
    <% } else { %>
        jQuery('#<portlet:namespace />nombre_medicamento').val(nombre);
    <% } %>

    jQuery('#<portlet:namespace />id_medicamento').val(id);
    jQuery('#<portlet:namespace />troquel').val(codigo);
    jQuery('#<portlet:namespace />med_seleccionado').val('1');
    jQuery('#<portlet:namespace />divBtnBuscaMedicamento').hide();
}

var <portlet:namespace />medicamentoKeyDown = 0;

jQuery(function() {
    var camposMedicamento = jQuery(
            '#<portlet:namespace />troquel, '
                    + '#<portlet:namespace />nombre_medicamento'
    );

    camposMedicamento.bind('keydown', function(event) {
        <portlet:namespace />medicamentoKeyDown =
                event.which || event.keyCode || 0;
    });

    camposMedicamento.bind(
            'input change paste cut drop',
            function(event) {

        if (event.type == 'input'
                && (<portlet:namespace />medicamentoKeyDown == 8
                || <portlet:namespace />medicamentoKeyDown == 46)) {

            return;
        }

        if (jQuery('#<portlet:namespace />med_seleccionado').val() == '1') {
            jQuery('#<portlet:namespace />id_medicamento').val('');
            jQuery('#<portlet:namespace />med_seleccionado').val('');
            jQuery('#<portlet:namespace />divBtnBuscaMedicamento').show();
            <portlet:namespace />invalidarMedicamentoDetalle();
        }
    });

    camposMedicamento.bind('keyup', function() {
        window.setTimeout(function() {
            <portlet:namespace />medicamentoKeyDown = 0;
        }, 0);
    });
});
</script>

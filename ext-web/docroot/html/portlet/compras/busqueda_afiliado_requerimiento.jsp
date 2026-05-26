<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>

<%
RequerimientoCompra reqAfi = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqAfi == null) {
    reqAfi = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqAfi == null) {
    reqAfi = new RequerimientoCompra();
}

String afiliadoCuilTitular = reqAfi.getAfiliadoCuilTitularVisible();
String afiliadoInte = reqAfi.getAfiliadoInteString();
%>

<fieldset class="block-labels" id="<portlet:namespace />afiliado_requerimiento_fieldset">
    <legend>Afiliado</legend>

    <input type="hidden"
           name="<portlet:namespace />afiliado_cuil_titular"
           id="<portlet:namespace />afiliado_cuil_titular"
           value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

    <input type="hidden"
           name="<portlet:namespace />afiliado_inte"
           id="<portlet:namespace />afiliado_inte"
           value="<%= HtmlUtil.escape(afiliadoInte) %>" />

    <table class="lfr-table">
        <tr>
            <td><label><liferay-ui:message key="entidad" />:</label></td>
            <td>
                <select name="<portlet:namespace />entidad" id="<portlet:namespace />entidad">
                    <option value="">Todas</option>
                    <%
                    for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {
                    %>
                        <option value="<%= entidad %>" <%= WebKeysGlobal.ID_DEFAULT_ENTIDAD.equals(entidad) ? "selected=\"selected\"" : "" %>>
                            <%= entidad %>
                        </option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label><liferay-ui:message key="numero-afi" />:</label></td>
            <td>
                <input id="<portlet:namespace />numero_afi"
                       name="<portlet:namespace />numero_afi"
                       size="6"
                       maxlength="10"
                       type="text"
                       value="" />
            </td>

            <td><label><liferay-ui:message key="cuil" />:</label></td>
            <td>
                <input id="<portlet:namespace />cuil"
                       name="<portlet:namespace />cuil"
                       size="13"
                       maxlength="11"
                       type="text"
                       value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />
            </td>

            <td><label><liferay-ui:message key="integrante" />:</label></td>
            <td>
                <input id="<portlet:namespace />inte"
                       name="<portlet:namespace />inte"
                       size="2"
                       maxlength="2"
                       type="text"
                       value="<%= HtmlUtil.escape(afiliadoInte) %>" />
            </td>
        </tr>

        <tr>
            <td colspan="8">&nbsp;</td>
        </tr>

        <tr>
            <td><label><liferay-ui:message key="tipo-documento" />:</label></td>
            <td>
                <select name="<portlet:namespace />tipoDoc" id="<portlet:namespace />tipoDoc">
                    <option value=""></option>
                    <%
                    for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
                    %>
                        <option value="<%= tipoDoc %>"><%= tipoDoc %></option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label><liferay-ui:message key="nro-documento" />:</label></td>
            <td>
                <input id="<portlet:namespace />nroDoc"
                       name="<portlet:namespace />nroDoc"
                       size="9"
                       maxlength="8"
                       type="text"
                       value="" />
            </td>

            <td><label><liferay-ui:message key="apellido" />:</label></td>
            <td>
                <input id="<portlet:namespace />apellido"
                       name="<portlet:namespace />apellido"
                       size="20"
                       maxlength="100"
                       type="text"
                       value="" />
            </td>

            <td><label><liferay-ui:message key="nombre" />:</label></td>
            <td>
                <input id="<portlet:namespace />nombre"
                       name="<portlet:namespace />nombre"
                       size="20"
                       maxlength="100"
                       type="text"
                       value="" />
            </td>
        </tr>

        <tr>
            <td colspan="8">&nbsp;</td>
        </tr>

        <tr>
            <td><label><liferay-ui:message key="baja-fecha" />:</label></td>
            <td>
                <input id="<portlet:namespace />baja_fecha"
                       name="<portlet:namespace />baja_fecha"
                       size="10"
                       type="text"
                       readonly="readonly"
                       value="" />
            </td>

            <td><label><liferay-ui:message key="plan" />:</label></td>
            <td>
                <input id="<portlet:namespace />nombre_plan"
                       name="<portlet:namespace />nombre_plan"
                       size="20"
                       type="text"
                       readonly="readonly"
                       value="" />
            </td>

            <td colspan="4" align="right">
                <input id="<portlet:namespace />buscarAfiliado"
                       value="<liferay-ui:message key="buscar-afiliado" />"
                       title="<liferay-ui:message key="buscar-afiliado" />"
                       type="button"
                       onClick="javascript:<portlet:namespace />buscarAfiliados();" />

                &nbsp;

                <input id="<portlet:namespace />limpiarCampos"
                       value="<liferay-ui:message key="limpiar-campos" />"
                       title="<liferay-ui:message key="limpiar-campos" />"
                       type="button"
                       onClick="javascript:<portlet:namespace />limpiarCamposAfiliado();" />
            </td>
        </tr>
    </table>
</fieldset>

<script type="text/javascript">
    var popupAfill;

    function <portlet:namespace />buscarAfiliados() {
        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numero_afi = jQuery('#<portlet:namespace />numero_afi').val();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, apellido, nombre, entidad, numero_afi)) {
            return false;
        }

        if (cuil.length > 0 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "<liferay-ui:message key='valida-cuil'/>")) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/compras/buscar_afiliados_requerimiento&cuil=' + cuil +
                '&inte=' + inte + '&tipoDoc=' + tipoDoc + '&nroDoc=' + nroDoc + '&nombre=' + encodeURI(nombre) +
                '&apellido=' + encodeURI(apellido) + '&entidad=' + entidad + '&numero_afi=' + numero_afi + '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, apellido, nombre, entidad, numero_afi) {
        if (jQuery.trim(cuil).length == 0 && jQuery.trim(inte).length == 0 &&
                jQuery.trim(tipoDoc).length == 0 && jQuery.trim(nroDoc).length == 0 &&
                jQuery.trim(apellido).length == 0 && jQuery.trim(nombre).length == 0 &&
                jQuery.trim(entidad).length == 0 && jQuery.trim(numero_afi).length == 0) {

            alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
            return false;
        }

        return true;
    }

    function seleccionaAfiliado(
            cuil,
            inte,
            docu_tipo,
            docu_nro,
            nombre,
            apellido,
            id_secc,
            desc_secc,
            ospim,
            uoma,
            amtima,
            bajaFecha,
            nombre_plan,
            id_plan,
            fecha_alta_af,
            incapacidad_af,
            id_tercerizadora,
            afi_tercerizadora,
            conreclamo,
            nroSocioPrevencion,
            nroCredencialPrevencion,
            fechaIncidente,
            tieneAntecedentes) {

        seleccionaCamposAfiliado(
                cuil,
                inte,
                docu_tipo,
                docu_nro,
                nombre,
                apellido,
                id_secc,
                desc_secc,
                ospim,
                uoma,
                amtima,
                bajaFecha,
                nombre_plan,
                id_plan,
                fecha_alta_af,
                incapacidad_af,
                id_tercerizadora,
                afi_tercerizadora,
                conreclamo,
                nroSocioPrevencion,
                nroCredencialPrevencion,
                fechaIncidente,
                tieneAntecedentes
        );

        Liferay.Popup.close(popupAfill);

        return false;
    }

    function seleccionaCamposAfiliado(
            cuil,
            inte,
            docu_tipo,
            docu_nro,
            nombre,
            apellido,
            id_secc,
            desc_secc,
            ospim,
            uoma,
            amtima,
            bajaFecha,
            nombre_plan,
            id_plan,
            fecha_alta_af,
            incapacidad_af,
            id_tercerizadora,
            afi_tercerizadora,
            conreclamo,
            nroSocioPrevencion,
            nroCredencialPrevencion,
            fechaIncidente,
            tieneAntecedentes) {

        cuil = <portlet:namespace />normalizarDatoAfiliado(cuil);
        inte = <portlet:namespace />normalizarDatoAfiliado(inte);
        docu_tipo = <portlet:namespace />normalizarDatoAfiliado(docu_tipo);
        docu_nro = <portlet:namespace />normalizarDatoAfiliado(docu_nro);
        nombre = <portlet:namespace />normalizarDatoAfiliado(nombre);
        apellido = <portlet:namespace />normalizarDatoAfiliado(apellido);
        bajaFecha = <portlet:namespace />normalizarDatoAfiliado(bajaFecha);
        nombre_plan = <portlet:namespace />normalizarDatoAfiliado(nombre_plan);
        ospim = <portlet:namespace />normalizarDatoAfiliado(ospim);
        uoma = <portlet:namespace />normalizarDatoAfiliado(uoma);
        amtima = <portlet:namespace />normalizarDatoAfiliado(amtima);

        jQuery('#<portlet:namespace />cuil').val(cuil);
        jQuery('#<portlet:namespace />inte').val(inte);
        jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
        jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
        jQuery('#<portlet:namespace />apellido').val(apellido);
        jQuery('#<portlet:namespace />nombre').val(nombre);
        jQuery('#<portlet:namespace />baja_fecha').val(bajaFecha);
        jQuery('#<portlet:namespace />nombre_plan').val(nombre_plan);

        if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDAD_OSPIM %>') {
            jQuery('#<portlet:namespace />numero_afi').val(ospim);
        }
        if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDAD_UOMA %>') {
            jQuery('#<portlet:namespace />numero_afi').val(uoma);
        }
        if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDAD_AMTIMA %>') {
            jQuery('#<portlet:namespace />numero_afi').val(amtima);
        }

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(cuil);
        jQuery('#<portlet:namespace />afiliado_inte').val(inte);

        return false;
    }

    function <portlet:namespace />normalizarDatoAfiliado(value) {
        return value == null || value == "null" ? "" : value;
    }

    function <portlet:namespace />limpiarCamposAfiliado() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_inte').val('');

        jQuery('#<portlet:namespace />entidad').val('<%= WebKeysGlobal.ENTIDAD_OSPIM %>');
        jQuery('#<portlet:namespace />numero_afi').val('');
        jQuery('#<portlet:namespace />cuil').val('');
        jQuery('#<portlet:namespace />inte').val('');
        jQuery('#<portlet:namespace />tipoDoc').val('');
        jQuery('#<portlet:namespace />nroDoc').val('');
        jQuery('#<portlet:namespace />apellido').val('');
        jQuery('#<portlet:namespace />nombre').val('');
        jQuery('#<portlet:namespace />baja_fecha').val('');
        jQuery('#<portlet:namespace />nombre_plan').val('');

        return false;
    }
</script>

<%@ include file="/html/portlet/compras/init.jsp" %>

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
            <td><label>Entidad:</label></td>
            <td>
                <select name="<portlet:namespace />entidad" id="<portlet:namespace />entidad">
                    <option value="">Todas</option>
                    <option value="O.S.P.I.M." selected="selected">O.S.P.I.M.</option>
                    <option value="A.M.T.I.M.A.">A.M.T.I.M.A.</option>
                    <option value="U.O.M.A.">U.O.M.A.</option>
                </select>
            </td>

            <td><label>Número afiliado:</label></td>
            <td>
                <input id="<portlet:namespace />numero_afi"
                       name="<portlet:namespace />numero_afi"
                       size="6"
                       maxlength="10"
                       type="text"
                       value="" />
            </td>

            <td><label>CUIL:</label></td>
            <td>
                <input id="<portlet:namespace />cuil"
                       name="<portlet:namespace />cuil"
                       size="13"
                       maxlength="11"
                       type="text"
                       value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />
            </td>

            <td><label>Integrante:</label></td>
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
            <td><label>Tipo doc.:</label></td>
            <td>
                <select name="<portlet:namespace />tipoDoc" id="<portlet:namespace />tipoDoc">
                    <option value=""></option>
                    <option value="DNI">DNI</option>
                    <option value="LC">LC</option>
                    <option value="LE">LE</option>
                    <option value="CI">CI</option>
                </select>
            </td>

            <td><label>Nro. doc.:</label></td>
            <td>
                <input id="<portlet:namespace />nroDoc"
                       name="<portlet:namespace />nroDoc"
                       size="9"
                       maxlength="8"
                       type="text"
                       value="" />
            </td>

            <td><label>Apellido:</label></td>
            <td>
                <input id="<portlet:namespace />apellido"
                       name="<portlet:namespace />apellido"
                       size="20"
                       maxlength="100"
                       type="text"
                       value="" />
            </td>

            <td><label>Nombre:</label></td>
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
            <td><label>Baja fecha:</label></td>
            <td>
                <input id="<portlet:namespace />baja_fecha"
                       name="<portlet:namespace />baja_fecha"
                       size="10"
                       type="text"
                       readonly="readonly"
                       value="" />
            </td>

            <td><label>Plan:</label></td>
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
                       value="Buscar afiliado"
                       title="Buscar afiliado"
                       type="button"
                       onclick="javascript:<portlet:namespace />buscarAfiliados();" />

                &nbsp;

                <input id="<portlet:namespace />limpiarCampos"
                       value="Limpiar campos"
                       title="Limpiar campos"
                       type="button"
                       onclick="javascript:<portlet:namespace />limpiarCamposAfiliado();" />
            </td>
        </tr>
    </table>
</fieldset>

<script type="text/javascript">
    var popupAfill;

    function <portlet:namespace />buscarAfiliados() {
        var cuil = jQuery("#<portlet:namespace />cuil").val();
        var inte = jQuery("#<portlet:namespace />inte").val();
        var tipoDoc = jQuery("#<portlet:namespace />tipoDoc").val();
        var nroDoc = jQuery("#<portlet:namespace />nroDoc").val();
        var apellido = jQuery("#<portlet:namespace />apellido").val();
        var nombre = jQuery("#<portlet:namespace />nombre").val();
        var entidad = jQuery("#<portlet:namespace />entidad").val();
        var numero_afi = jQuery("#<portlet:namespace />numero_afi").val();

        if (!<portlet:namespace />validarBusquedaAfiliado(cuil, inte, tipoDoc, nroDoc, apellido, nombre, entidad, numero_afi)) {
            return false;
        }

        if (cuil.length > 0 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL inválido.")) {
                jQuery("#<portlet:namespace />cuil").focus();
                return false;
            }
        }

        popupAfill = Liferay.Popup({
            title: "Búsqueda de afiliado",
            modal: true,
            width: 830
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" />'
            + '&struts_action=/compras/buscar_afiliados_requerimiento'
            + '&cuil=' + encodeURIComponent(cuil)
            + '&inte=' + encodeURIComponent(inte)
            + '&tipoDoc=' + encodeURIComponent(tipoDoc)
            + '&nroDoc=' + encodeURIComponent(nroDoc)
            + '&apellido=' + encodeURIComponent(apellido)
            + '&nombre=' + encodeURIComponent(nombre)
            + '&entidad=' + encodeURIComponent(entidad)
            + '&numero_afi=' + encodeURIComponent(numero_afi)
            + '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />validarBusquedaAfiliado(cuil, inte, tipoDoc, nroDoc, apellido, nombre, entidad, numero_afi) {
        if (jQuery.trim(cuil).length == 0
                && jQuery.trim(inte).length == 0
                && jQuery.trim(tipoDoc).length == 0
                && jQuery.trim(nroDoc).length == 0
                && jQuery.trim(apellido).length == 0
                && jQuery.trim(nombre).length == 0
                && jQuery.trim(entidad).length == 0
                && jQuery.trim(numero_afi).length == 0) {

            alert("Ingrese al menos un parámetro de búsqueda.");
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
            afi_tercerizadora,
            desc_tercerizadora,
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
                afi_tercerizadora,
                desc_tercerizadora,
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
            afi_tercerizadora,
            desc_tercerizadora,
            conreclamo,
            nroSocioPrevencion,
            nroCredencialPrevencion,
            fechaIncidente,
            tieneAntecedentes) {

        cuil = cuil == null ? "" : cuil;
        inte = inte == null ? "" : inte;
        docu_tipo = docu_tipo == null ? "" : docu_tipo;
        docu_nro = docu_nro == null ? "" : docu_nro;
        nombre = nombre == null ? "" : nombre;
        apellido = apellido == null ? "" : apellido;
        bajaFecha = bajaFecha == null ? "" : bajaFecha;
        nombre_plan = nombre_plan == null || nombre_plan == "null" ? "" : nombre_plan;

        jQuery("#<portlet:namespace />cuil").val(cuil);
        jQuery("#<portlet:namespace />inte").val(inte);
        jQuery("#<portlet:namespace />tipoDoc").val(docu_tipo);
        jQuery("#<portlet:namespace />nroDoc").val(docu_nro);
        jQuery("#<portlet:namespace />apellido").val(apellido);
        jQuery("#<portlet:namespace />nombre").val(nombre);
        jQuery("#<portlet:namespace />baja_fecha").val(bajaFecha);
        jQuery("#<portlet:namespace />nombre_plan").val(nombre_plan);

        if (jQuery("#<portlet:namespace />entidad").val() == "O.S.P.I.M.") {
            jQuery("#<portlet:namespace />numero_afi").val(ospim);
        }

        if (jQuery("#<portlet:namespace />entidad").val() == "U.O.M.A.") {
            jQuery("#<portlet:namespace />numero_afi").val(uoma);
        }

        if (jQuery("#<portlet:namespace />entidad").val() == "A.M.T.I.M.A.") {
            jQuery("#<portlet:namespace />numero_afi").val(amtima);
        }

        jQuery("#<portlet:namespace />afiliado_cuil_titular").val(cuil);
        jQuery("#<portlet:namespace />afiliado_inte").val(inte);

        return false;
    }

    function <portlet:namespace />limpiarCamposAfiliado() {
        jQuery("#<portlet:namespace />afiliado_cuil_titular").val("");
        jQuery("#<portlet:namespace />afiliado_inte").val("");

        jQuery("#<portlet:namespace />entidad").val("O.S.P.I.M.");
        jQuery("#<portlet:namespace />numero_afi").val("");
        jQuery("#<portlet:namespace />cuil").val("");
        jQuery("#<portlet:namespace />inte").val("");
        jQuery("#<portlet:namespace />tipoDoc").val("");
        jQuery("#<portlet:namespace />nroDoc").val("");
        jQuery("#<portlet:namespace />apellido").val("");
        jQuery("#<portlet:namespace />nombre").val("");
        jQuery("#<portlet:namespace />baja_fecha").val("");
        jQuery("#<portlet:namespace />nombre_plan").val("");

        return false;
    }
</script>
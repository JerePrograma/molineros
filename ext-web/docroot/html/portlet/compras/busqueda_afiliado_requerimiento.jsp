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

String origenAfiliadoCompras = "ComprasReq";
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

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>Seleccionado:</label></td>
            <td colspan="5">
                <strong id="<portlet:namespace />afiliado_seleccionado_label">
                    <%
                    if (Validator.isNotNull(afiliadoCuilTitular)) {
                    %>
                        CUIL titular: <%= HtmlUtil.escape(afiliadoCuilTitular) %>
                        <%
                        if (Validator.isNotNull(afiliadoInte)) {
                        %>
                            &nbsp;-&nbsp;Integrante: <%= HtmlUtil.escape(afiliadoInte) %>
                        <%
                        }
                        %>
                    <%
                    } else {
                    %>
                        Sin afiliado seleccionado
                    <%
                    }
                    %>
                </strong>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Entidad:</label></td>
            <td>
                <select id="<portlet:namespace />entidad<%= origenAfiliadoCompras %>"
                        name="<portlet:namespace />entidad<%= origenAfiliadoCompras %>">
                    <option value="">Todas</option>
                    <option value="O.S.P.I.M." selected="selected">O.S.P.I.M.</option>
                    <option value="A.M.T.I.M.A.">A.M.T.I.M.A.</option>
                    <option value="U.O.M.A.">U.O.M.A.</option>
                </select>
            </td>

            <td><label>Número afiliado:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />numero_afi<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />numero_afi<%= origenAfiliadoCompras %>"
                       value=""
                       size="8"
                       maxlength="10" />
            </td>

            <td><label>CUIL:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />cuil<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />cuil<%= origenAfiliadoCompras %>"
                       value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>"
                       size="13"
                       maxlength="11" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Integrante:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />inte<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />inte<%= origenAfiliadoCompras %>"
                       value="<%= HtmlUtil.escape(afiliadoInte) %>"
                       size="3"
                       maxlength="2" />
            </td>

            <td><label>Tipo doc.:</label></td>
            <td>
                <select id="<portlet:namespace />tipoDoc<%= origenAfiliadoCompras %>"
                        name="<portlet:namespace />tipoDoc<%= origenAfiliadoCompras %>">
                    <option value=""></option>
                    <option value="DNI">DNI</option>
                    <option value="LC">LC</option>
                    <option value="LE">LE</option>
                    <option value="CI">CI</option>
                </select>
            </td>

            <td><label>Nro. doc.:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />nroDoc<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />nroDoc<%= origenAfiliadoCompras %>"
                       value=""
                       size="10"
                       maxlength="8" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Apellido:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />apellido<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />apellido<%= origenAfiliadoCompras %>"
                       value=""
                       size="24"
                       maxlength="100" />
            </td>

            <td><label>Nombre:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />nombre<%= origenAfiliadoCompras %>"
                       name="<portlet:namespace />nombre<%= origenAfiliadoCompras %>"
                       value=""
                       size="24"
                       maxlength="100" />
            </td>

            <td colspan="2" align="right">
                <input type="button"
                       value="Buscar afiliado"
                       onclick="javascript:<portlet:namespace />buscarAfiliados<%= origenAfiliadoCompras %>();" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Limpiar"
                       onclick="javascript:<portlet:namespace />limpiarCamposAfiliado<%= origenAfiliadoCompras %>();" />
            </td>
        </tr>
    </table>

    <br />

    <div id="<portlet:namespace />afiliado_requerimiento_resumen"
         class="portlet-msg-info"
         style="<%= Validator.isNotNull(afiliadoCuilTitular) ? "" : "display:none;" %>">

        <table class="lfr-table" width="100%">
            <tr>
                <td><label>CUIL titular:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_cuil"><%= HtmlUtil.escape(afiliadoCuilTitular) %></td>

                <td><label>Integrante:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_inte"><%= HtmlUtil.escape(afiliadoInte) %></td>
            </tr>

            <tr>
                <td><label>Apellido y nombre:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_nombre"></td>

                <td><label>DNI:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_dni"></td>
            </tr>

            <tr>
                <td><label>Baja:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_baja"></td>

                <td><label>Plan:</label></td>
                <td id="<portlet:namespace />afiliado_resumen_plan"></td>
            </tr>
        </table>
    </div>
</fieldset>

<script type="text/javascript">
    var <portlet:namespace />popupAfiliadoComprasReq = null;

    function <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>(campo) {
        var selector = "#<portlet:namespace />" + campo + "<%= origenAfiliadoCompras %>";
        var item = jQuery(selector);

        if (item.length == 0) {
            return "";
        }

        return item.val();
    }

    function <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, name, value) {
        return url + "&" + name + "=" + encodeURIComponent(value == null ? "" : value);
    }

    function <portlet:namespace />validarBusqueda<%= origenAfiliadoCompras %>(
            cuil,
            inte,
            tipoDoc,
            nroDoc,
            apellido,
            nombre,
            entidad,
            numeroAfi) {

        cuil = jQuery.trim(cuil);
        nroDoc = jQuery.trim(nroDoc);
        apellido = jQuery.trim(apellido);
        nombre = jQuery.trim(nombre);
        numeroAfi = jQuery.trim(numeroAfi);

        if (cuil.length == 0
                && nroDoc.length == 0
                && apellido.length == 0
                && nombre.length == 0
                && numeroAfi.length == 0) {

            alert("Ingrese CUIL, documento, apellido, nombre o número de afiliado para buscar.");
            return false;
        }

        return true;
    }

    function <portlet:namespace />buscarAfiliados<%= origenAfiliadoCompras %>() {
        var cuil = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("cuil");
        var inte = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("inte");
        var tipoDoc = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("tipoDoc");
        var nroDoc = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("nroDoc");
        var apellido = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("apellido");
        var nombre = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("nombre");
        var entidad = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("entidad");
        var numeroAfi = <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("numero_afi");

        if (!<portlet:namespace />validarBusqueda<%= origenAfiliadoCompras %>(
                cuil,
                inte,
                tipoDoc,
                nroDoc,
                apellido,
                nombre,
                entidad,
                numeroAfi)) {
            return false;
        }

        cuil = jQuery.trim(cuil);

        if (cuil.length > 0 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL inválido.")) {
                jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").focus();
                return false;
            }
        }

        <portlet:namespace />popupAfiliadoComprasReq = Liferay.Popup({
            title: "Búsqueda de afiliado",
            modal: true,
            width: 830
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" />';

        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "struts_action", "/compras/buscar_afiliados_requerimiento");
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "cuil", cuil);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "inte", inte);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "tipoDoc", tipoDoc);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "nroDoc", nroDoc);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "apellido", apellido);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "nombre", nombre);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "entidad", entidad);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "numero_afi", numeroAfi);
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "origen", "<%= origenAfiliadoCompras %>");
        url = <portlet:namespace />appendAfiliadoParam<%= origenAfiliadoCompras %>(url, "popup", "true");

        jQuery(<portlet:namespace />popupAfiliadoComprasReq).load(url);

        return false;
    }

    function seleccionaAfiliado<%= origenAfiliadoCompras %>(
            cuilTitular,
            integrante,
            tipoDoc,
            nroDoc,
            nombre,
            apellido,
            seccionalId,
            seccionalDescripcion,
            idOspim,
            idUoma,
            idAmtima,
            bajaFecha,
            nombrePlan,
            idPlan,
            altaFecha,
            discapacitado,
            idTercerizadora,
            descTercerizadora,
            conReclamoPrestacional,
            nroSocioPrevencion,
            nroCredencialPrevencion,
            fechaIncidente,
            tieneAntecedentes) {

        cuilTitular = cuilTitular == null ? "" : cuilTitular;
        integrante = integrante == null ? "" : integrante;
        tipoDoc = tipoDoc == null ? "" : tipoDoc;
        nroDoc = nroDoc == null ? "" : nroDoc;
        nombre = nombre == null ? "" : nombre;
        apellido = apellido == null ? "" : apellido;
        bajaFecha = bajaFecha == null ? "" : bajaFecha;
        nombrePlan = nombrePlan == null ? "" : nombrePlan;

        cuilTitular = jQuery.trim(cuilTitular);
        integrante = jQuery.trim(integrante);

        var apellidoNombre = jQuery.trim(apellido + " " + nombre);

        if (cuilTitular == "" || integrante == "") {
            alert("No se pudo seleccionar el afiliado. La búsqueda no devolvió CUIL titular o integrante.");
            return false;
        }

        jQuery("#<portlet:namespace />afiliado_cuil_titular").val(cuilTitular);
        jQuery("#<portlet:namespace />afiliado_inte").val(integrante);

        jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").val(cuilTitular);
        jQuery("#<portlet:namespace />inte<%= origenAfiliadoCompras %>").val(integrante);
        jQuery("#<portlet:namespace />tipoDoc<%= origenAfiliadoCompras %>").val(tipoDoc);
        jQuery("#<portlet:namespace />nroDoc<%= origenAfiliadoCompras %>").val(nroDoc);
        jQuery("#<portlet:namespace />apellido<%= origenAfiliadoCompras %>").val(apellido);
        jQuery("#<portlet:namespace />nombre<%= origenAfiliadoCompras %>").val(nombre);

        jQuery("#<portlet:namespace />afiliado_resumen_cuil").text(cuilTitular);
        jQuery("#<portlet:namespace />afiliado_resumen_inte").text(integrante);
        jQuery("#<portlet:namespace />afiliado_resumen_nombre").text(apellidoNombre);
        jQuery("#<portlet:namespace />afiliado_resumen_dni").text(nroDoc);
        jQuery("#<portlet:namespace />afiliado_resumen_baja").text(bajaFecha);
        jQuery("#<portlet:namespace />afiliado_resumen_plan").text(nombrePlan);

        var label = "CUIL titular: " + cuilTitular + " - Integrante: " + integrante;

        if (apellidoNombre != "") {
            label = label + " - " + apellidoNombre;
        }

        jQuery("#<portlet:namespace />afiliado_seleccionado_label").text(label);
        jQuery("#<portlet:namespace />afiliado_requerimiento_resumen").show();

        try {
            if (<portlet:namespace />popupAfiliadoComprasReq != null) {
                jQuery(<portlet:namespace />popupAfiliadoComprasReq).dialog("close");
            }
        } catch (e) {
        }

        return false;
    }

    function <portlet:namespace />limpiarCamposAfiliado<%= origenAfiliadoCompras %>() {
        jQuery("#<portlet:namespace />afiliado_cuil_titular").val("");
        jQuery("#<portlet:namespace />afiliado_inte").val("");

        jQuery("#<portlet:namespace />entidad<%= origenAfiliadoCompras %>").val("O.S.P.I.M.");
        jQuery("#<portlet:namespace />numero_afi<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />inte<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />tipoDoc<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />nroDoc<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />apellido<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />nombre<%= origenAfiliadoCompras %>").val("");

        jQuery("#<portlet:namespace />afiliado_resumen_cuil").text("");
        jQuery("#<portlet:namespace />afiliado_resumen_inte").text("");
        jQuery("#<portlet:namespace />afiliado_resumen_nombre").text("");
        jQuery("#<portlet:namespace />afiliado_resumen_dni").text("");
        jQuery("#<portlet:namespace />afiliado_resumen_baja").text("");
        jQuery("#<portlet:namespace />afiliado_resumen_plan").text("");

        jQuery("#<portlet:namespace />afiliado_seleccionado_label").text("Sin afiliado seleccionado");
        jQuery("#<portlet:namespace />afiliado_requerimiento_resumen").hide();

        return false;
    }
</script>
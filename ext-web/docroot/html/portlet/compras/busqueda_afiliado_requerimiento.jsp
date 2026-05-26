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

<fieldset class="block-labels">
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
                    <c:choose>
                        <c:when test="<%= Validator.isNotNull(afiliadoCuilTitular) %>">
                            CUIL titular: <%= HtmlUtil.escape(afiliadoCuilTitular) %>
                            <c:if test="<%= Validator.isNotNull(afiliadoInte) %>">
                                &nbsp;-&nbsp;Integrante: <%= HtmlUtil.escape(afiliadoInte) %>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            Sin afiliado seleccionado
                        </c:otherwise>
                    </c:choose>
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
                       value="Aplicar CUIL/Integrante"
                       onclick="javascript:<portlet:namespace />aplicarAfiliadoManual<%= origenAfiliadoCompras %>();" />

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
    var popupAfill = null;

    function <portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>(campo) {
        return jQuery("#<portlet:namespace />" + campo + "<%= origenAfiliadoCompras %>").val();
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

        if (jQuery.trim(cuil).length == 0
                && jQuery.trim(inte).length == 0
                && jQuery.trim(tipoDoc).length == 0
                && jQuery.trim(nroDoc).length == 0
                && jQuery.trim(apellido).length == 0
                && jQuery.trim(nombre).length == 0
                && jQuery.trim(numeroAfi).length == 0) {

            alert("Ingrese al menos un parámetro de búsqueda.");
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

        if (jQuery.trim(cuil).length > 0 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL inválido.")) {
                jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").focus();
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
            + '&cuil=' + encodeURI(cuil)
            + '&inte=' + encodeURI(inte)
            + '&tipoDoc=' + encodeURI(tipoDoc)
            + '&nroDoc=' + encodeURI(nroDoc)
            + '&apellido=' + encodeURI(apellido)
            + '&nombre=' + encodeURI(nombre)
            + '&entidad=' + encodeURI(entidad)
            + '&numero_afi=' + encodeURI(numeroAfi)
            + '&origen=<%= origenAfiliadoCompras %>'
            + '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />aplicarAfiliadoManual<%= origenAfiliadoCompras %>() {
        var cuil = jQuery.trim(<portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("cuil"));
        var inte = jQuery.trim(<portlet:namespace />valorAfiliado<%= origenAfiliadoCompras %>("inte"));

        if (cuil == "") {
            alert("Debe informar CUIL titular.");
            jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").focus();
            return false;
        }

        if (inte == "") {
            alert("Debe informar integrante.");
            jQuery("#<portlet:namespace />inte<%= origenAfiliadoCompras %>").focus();
            return false;
        }

        seleccionaAfiliado<%= origenAfiliadoCompras %>(
                cuil,
                inte,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "0"
        );

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

        var apellidoNombre = jQuery.trim(apellido + " " + nombre);

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
            if (popupAfill != null) {
                jQuery(popupAfill).dialog("close");
            }
        } catch (e) {
        }

        return false;
    }

    function <portlet:namespace />limpiarCamposAfiliado<%= origenAfiliadoCompras %>() {
        jQuery("#<portlet:namespace />afiliado_cuil_titular").val("");
        jQuery("#<portlet:namespace />afiliado_inte").val("");

        jQuery("#<portlet:namespace />cuil<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />inte<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />tipoDoc<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />nroDoc<%= origenAfiliadoCompras %>").val("");
        jQuery("#<portlet:namespace />numero_afi<%= origenAfiliadoCompras %>").val("");
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
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
            <td><label>CUIL:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />afi_req_cuil"
                       name="<portlet:namespace />afi_req_cuil"
                       value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>"
                       size="13"
                       maxlength="11" />
            </td>

            <td><label>Integrante:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />afi_req_inte"
                       name="<portlet:namespace />afi_req_inte"
                       value="<%= HtmlUtil.escape(afiliadoInte) %>"
                       size="3"
                       maxlength="2" />
            </td>

            <td><label>Número afiliado:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />afi_req_numero_afi"
                       name="<portlet:namespace />afi_req_numero_afi"
                       value=""
                       size="8"
                       maxlength="10" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Tipo doc.:</label></td>
            <td>
                <select id="<portlet:namespace />afi_req_tipoDoc"
                        name="<portlet:namespace />afi_req_tipoDoc">
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
                       id="<portlet:namespace />afi_req_nroDoc"
                       name="<portlet:namespace />afi_req_nroDoc"
                       value=""
                       size="10"
                       maxlength="8" />
            </td>

            <td><label>Entidad:</label></td>
            <td>
                <select id="<portlet:namespace />afi_req_entidad"
                        name="<portlet:namespace />afi_req_entidad">
                    <option value="">Todas</option>
                    <option value="O.S.P.I.M." selected="selected">O.S.P.I.M.</option>
                    <option value="A.M.T.I.M.A.">A.M.T.I.M.A.</option>
                    <option value="U.O.M.A.">U.O.M.A.</option>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Apellido:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />afi_req_apellido"
                       name="<portlet:namespace />afi_req_apellido"
                       value=""
                       size="24"
                       maxlength="100" />
            </td>

            <td><label>Nombre:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />afi_req_nombre"
                       name="<portlet:namespace />afi_req_nombre"
                       value=""
                       size="24"
                       maxlength="100" />
            </td>

            <td colspan="2">
                <input type="button"
                       value="Buscar afiliado"
                       onclick="<portlet:namespace />buscarAfiliadoRequerimiento();" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Aplicar CUIL/Integrante"
                       onclick="<portlet:namespace />aplicarAfiliadoManual();" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Limpiar"
                       onclick="<portlet:namespace />limpiarAfiliadoRequerimiento();" />
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
        </table>
    </div>

    <br />

    <div align="center" id="<portlet:namespace />buscando_afiliado_requerimiento" style="display:none;">
        <table style="align:center;">
            <tr>
                <td>Buscando afiliado</td>
                <td align="center">
                    <img alt="Buscando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                </td>
            </tr>
        </table>
    </div>

    <div align="center" id="<portlet:namespace />busqueda_afiliado_requerimiento_div"></div>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />afiReqVal(name) {
        return jQuery("#<portlet:namespace />" + name).val();
    }

    function <portlet:namespace />hayFiltroAfiliado() {
        return jQuery.trim(<portlet:namespace />afiReqVal("afi_req_cuil")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_inte")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_tipoDoc")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_nroDoc")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_numero_afi")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_apellido")).length > 0
            || jQuery.trim(<portlet:namespace />afiReqVal("afi_req_nombre")).length > 0;
    }

    function <portlet:namespace />buscarAfiliadoRequerimiento() {
        var cuil = jQuery.trim(<portlet:namespace />afiReqVal("afi_req_cuil"));

        if (!<portlet:namespace />hayFiltroAfiliado()) {
            alert("Ingrese al menos un parámetro de búsqueda.");
            return;
        }

        if (cuil.length > 0 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL inválido.")) {
                jQuery("#<portlet:namespace />afi_req_cuil").focus();
                return;
            }
        }

        jQuery("#<portlet:namespace />buscando_afiliado_requerimiento").show();

        var url = "<portlet:renderURL windowState='<%= LiferayWindowState.EXCLUSIVE.toString() %>' />&struts_action=/compras/buscar_afiliados_requerimiento";

        var paramsAfi = {
            "cuil": cuil,
            "inte": <portlet:namespace />afiReqVal("afi_req_inte"),
            "tipoDoc": <portlet:namespace />afiReqVal("afi_req_tipoDoc"),
            "nroDoc": escape(<portlet:namespace />afiReqVal("afi_req_nroDoc")),
            "apellido": <portlet:namespace />afiReqVal("afi_req_apellido"),
            "nombre": <portlet:namespace />afiReqVal("afi_req_nombre"),
            "entidad": <portlet:namespace />afiReqVal("afi_req_entidad"),
            "numero_afi": <portlet:namespace />afiReqVal("afi_req_numero_afi"),
            "portlet_name": "COMPRAS_REQUERIMIENTO"
        };

        jQuery("#<portlet:namespace />busqueda_afiliado_requerimiento_div").load(url, paramsAfi, function() {
            jQuery("#<portlet:namespace />buscando_afiliado_requerimiento").hide();
        });
    }

    function <portlet:namespace />aplicarAfiliadoManual() {
        var cuil = jQuery.trim(<portlet:namespace />afiReqVal("afi_req_cuil"));
        var inte = jQuery.trim(<portlet:namespace />afiReqVal("afi_req_inte"));

        if (cuil == "") {
            alert("Debe informar CUIL titular.");
            jQuery("#<portlet:namespace />afi_req_cuil").focus();
            return;
        }

        if (inte == "") {
            alert("Debe informar integrante.");
            jQuery("#<portlet:namespace />afi_req_inte").focus();
            return;
        }

        <portlet:namespace />seleccionarAfiliadoRequerimiento(cuil, inte, "", "");
    }

    function <portlet:namespace />seleccionarAfiliadoRequerimiento(cuilTitular, integrante, apellidoNombre, dni) {
        cuilTitular = cuilTitular == null ? "" : cuilTitular;
        integrante = integrante == null ? "" : integrante;
        apellidoNombre = apellidoNombre == null ? "" : apellidoNombre;
        dni = dni == null ? "" : dni;

        jQuery("#<portlet:namespace />afiliado_cuil_titular").val(cuilTitular);
        jQuery("#<portlet:namespace />afiliado_inte").val(integrante);

        jQuery("#<portlet:namespace />afi_req_cuil").val(cuilTitular);
        jQuery("#<portlet:namespace />afi_req_inte").val(integrante);

        jQuery("#<portlet:namespace />afiliado_resumen_cuil").html(cuilTitular);
        jQuery("#<portlet:namespace />afiliado_resumen_inte").html(integrante);
        jQuery("#<portlet:namespace />afiliado_resumen_nombre").html(apellidoNombre);
        jQuery("#<portlet:namespace />afiliado_resumen_dni").html(dni);

        var label = "CUIL titular: " + cuilTitular + " - Integrante: " + integrante;
        if (apellidoNombre != "") {
            label = label + " - " + apellidoNombre;
        }

        jQuery("#<portlet:namespace />afiliado_seleccionado_label").html(label);
        jQuery("#<portlet:namespace />afiliado_requerimiento_resumen").show();
    }

    function <portlet:namespace />limpiarAfiliadoRequerimiento() {
        jQuery("#<portlet:namespace />afiliado_cuil_titular").val("");
        jQuery("#<portlet:namespace />afiliado_inte").val("");

        jQuery("#<portlet:namespace />afi_req_cuil").val("");
        jQuery("#<portlet:namespace />afi_req_inte").val("");
        jQuery("#<portlet:namespace />afi_req_tipoDoc").val("");
        jQuery("#<portlet:namespace />afi_req_nroDoc").val("");
        jQuery("#<portlet:namespace />afi_req_numero_afi").val("");
        jQuery("#<portlet:namespace />afi_req_apellido").val("");
        jQuery("#<portlet:namespace />afi_req_nombre").val("");

        jQuery("#<portlet:namespace />afiliado_seleccionado_label").html("Sin afiliado seleccionado");
        jQuery("#<portlet:namespace />afiliado_requerimiento_resumen").hide();
        jQuery("#<portlet:namespace />busqueda_afiliado_requerimiento_div").html("");
    }
</script>
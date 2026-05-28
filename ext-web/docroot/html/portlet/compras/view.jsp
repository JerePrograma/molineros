<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

boolean showABMButtons = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO);

if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

List<RequerimientoCompraEstado> estados =
        (List<RequerimientoCompraEstado>) renderRequest.getAttribute(WebKeysCompras.ESTADOS_REQUERIMIENTO);

if (estados == null) {
    try {
        estados = BusquedaRequerimientoCompraServiceUtil.listarEstados();
    } catch (Exception e) {
        estados = new ArrayList<RequerimientoCompraEstado>();
    }
}
%>

<fieldset class="block-labels">
    <legend>Filtro de b&uacute;squeda de requerimientos de compras</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Estado:</label></td>
            <td>
                <select id="<portlet:namespace />estado"
                        name="<portlet:namespace />estado">
                    <option value="0">Todos</option>

                    <%
                    for (int i = 0; i < estados.size(); i++) {
                        RequerimientoCompraEstado estado = estados.get(i);
                    %>
                        <option value="<%= estado.getIdEstado() %>"><%= HtmlUtil.escape(estado.getDescripcionVisible()) %></option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label>Sector:</label></td>
            <td>
                <select id="<portlet:namespace />sector_id"
                        name="<portlet:namespace />sector_id">
                    <option value="0">Todos</option>

                    <%
                    for (int i = 0; i < sectores.size(); i++) {
                        RequerimientoCompraSector sector = sectores.get(i);
                    %>
                        <option value="<%= sector.getIdSector() %>"><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label>Recupero:</label></td>
            <td>
                <select id="<portlet:namespace />recupero"
                        name="<portlet:namespace />recupero">
                    <option value="">Todos</option>
                    <option value="true">SI</option>
                    <option value="false">NO</option>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="6">
                <fieldset class="block-labels">
                    <legend>B&uacute;squeda por afiliado</legend>

                    <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
                        <liferay-util:param name="edit_mode" value="true" />
                    </liferay-util:include>

                    <input id="<portlet:namespace />afiliado_cuil_titular"
                           name="<portlet:namespace />afiliado_cuil_titular"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_int"
                           name="<portlet:namespace />afiliado_int"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_tipo_doc"
                           name="<portlet:namespace />afiliado_tipo_doc"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_nro_doc"
                           name="<portlet:namespace />afiliado_nro_doc"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_apellido"
                           name="<portlet:namespace />afiliado_apellido"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_nombre"
                           name="<portlet:namespace />afiliado_nombre"
                           type="hidden"
                           value="" />

                    <input id="<portlet:namespace />afiliado_id_seccional"
                           name="<portlet:namespace />afiliado_id_seccional"
                           type="hidden"
                           value="" />
                </fieldset>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Tercerizadora:</label></td>
            <td>
                <input id="<portlet:namespace />id_tercerizadora"
                       name="<portlet:namespace />id_tercerizadora"
                       size="10"
                       maxlength="10"
                       type="text"
                       value="" />
            </td>

            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="1">
                <input id="<portlet:namespace />buscar"
                       value="<liferay-ui:message key='buscar' />"
                       title="<liferay-ui:message key='buscar' />"
                       type="button" />
            </td>

            <td colspan="5">
                <input type="button"
                       value="Limpiar afiliado"
                       onClick="<portlet:namespace />limpiarAfiliadoFiltro();" />

                <c:if test="<%= showABMButtons %>">
                    &nbsp;&nbsp;

                    <input type="button"
                           value="Nuevo requerimiento"
                           onClick="<portlet:namespace />altaRequerimiento();" />
                </c:if>
            </td>
        </tr>
    </table>
</fieldset>

<fieldset class="block-labels">
    <div align="center"
         id="<portlet:namespace />buscando">
        <table style="align:center;">
            <tr>
                <td><liferay-ui:message key="buscando" /></td>
                <td align="center">
                    <img alt="<liferay-ui:message key='buscando' />"
                         src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                </td>
            </tr>
        </table>
    </div>

    <div align="center" id="<portlet:namespace />busquedaRequerimientosDiv"></div>
</fieldset>

<script type="text/javascript">
    var popupAfill = null;
    var popup = null;

    function <portlet:namespace />trimValue(id) {
        return jQuery.trim(jQuery('#<portlet:namespace />' + id).val());
    }

    function <portlet:namespace />valorCredencialAfiliado() {
        return jQuery('#<portlet:namespace />numero_afi').val();
    }

    function <portlet:namespace />sincronizarAfiliadoFiltro() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(<portlet:namespace />trimValue('cuil'));
        jQuery('#<portlet:namespace />afiliado_int').val(<portlet:namespace />trimValue('inte'));
        jQuery('#<portlet:namespace />afiliado_tipo_doc').val(<portlet:namespace />trimValue('tipoDoc'));
        jQuery('#<portlet:namespace />afiliado_nro_doc').val(<portlet:namespace />trimValue('nroDoc'));
        jQuery('#<portlet:namespace />afiliado_apellido').val(<portlet:namespace />trimValue('apellido'));
        jQuery('#<portlet:namespace />afiliado_nombre').val(<portlet:namespace />trimValue('nombre'));
        jQuery('#<portlet:namespace />afiliado_id_seccional').val(<portlet:namespace />trimValue('id_seccional'));
    }

    function <portlet:namespace />limpiarAfiliadoFiltro() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery('#<portlet:namespace />cuil').val('');
        jQuery('#<portlet:namespace />inte').val('');
        jQuery('#<portlet:namespace />tipoDoc').val('');
        jQuery('#<portlet:namespace />nroDoc').val('');
        jQuery('#<portlet:namespace />id_seccional').val('');
        jQuery('#<portlet:namespace />seccional').val('');
        jQuery('#<portlet:namespace />apellido').val('');
        jQuery('#<portlet:namespace />nombre').val('');
        jQuery('#<portlet:namespace />baja_fecha').val('');
        jQuery('#<portlet:namespace />fecha_alta_af').val('');

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_int').val('');
        jQuery('#<portlet:namespace />afiliado_tipo_doc').val('');
        jQuery('#<portlet:namespace />afiliado_nro_doc').val('');
        jQuery('#<portlet:namespace />afiliado_apellido').val('');
        jQuery('#<portlet:namespace />afiliado_nombre').val('');
        jQuery('#<portlet:namespace />afiliado_id_seccional').val('');
    }

    function <portlet:namespace />validarFiltroBusqueda() {
        <portlet:namespace />sincronizarAfiliadoFiltro();

        var cuil = jQuery.trim(jQuery('#<portlet:namespace />afiliado_cuil_titular').val());

        if (cuil.length > 0 && cuil.length == 11 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL titular invalido.")) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        return true;
    }

    function <portlet:namespace />buscarAfiliados() {
        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numeroAfi = jQuery('#<portlet:namespace />numero_afi').val();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, numeroAfi)) {
            return false;
        }

        if (cuil.length > 0) {
            if (typeof validarCuil == "function" && !validarCuil(cuil, "<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        if (jQuery("#<portlet:namespace />secc_seleccionada").val() != "1") {
            jQuery("#<portlet:namespace />seccional").val("");
            jQuery("#<portlet:namespace />id_seccional").val("");
        }

        popupAfill = Liferay.Popup({
            title: '<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />',
            modal: true,
            width: 830
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_afiliados' +
            '&cuil=' + encodeURIComponent(cuil) +
            '&inte=' + encodeURIComponent(inte) +
            '&tipoDoc=' + encodeURIComponent(tipoDoc) +
            '&nroDoc=' + encodeURIComponent(nroDoc) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&nombre=' + encodeURIComponent(nombre) +
            '&apellido=' + encodeURIComponent(apellido) +
            '&entidad=' + encodeURIComponent(entidad) +
            '&numero_afi=' + encodeURIComponent(numeroAfi) +
            '&fecha_referencia=null' +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />buscarAfiliados_(fechaReferencia) {
        return <portlet:namespace />buscarAfiliados();
    }

    function <portlet:namespace />buscarSeccional() {
        var id_seccional = jQuery("#<portlet:namespace />id_seccional").val();
        var seccional = jQuery("#<portlet:namespace />seccional").val();

        if (!<portlet:namespace />validaFormSecc(id_seccional, seccional)) {
            return false;
        }

        popup = Liferay.Popup({
            title: '<liferay-ui:message key="busqueda-seccionales" />',
            modal: true,
            width: 420
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_seccional' +
            '&id_seccional=' + encodeURIComponent(id_seccional) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&prefijo=';

        jQuery(popup).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccionalOnDiv(e) {
        var evtobj = window.event ? event : e;
        var keyPressed = evtobj.keyCode ? evtobj.keyCode : evtobj.charCode;

        if (jQuery("#<portlet:namespace />secc_seleccionada").val() == "1" && (keyPressed != 9 && keyPressed != 16)) {
            jQuery("#<portlet:namespace />seccional").val("");
            jQuery("#<portlet:namespace />id_seccional").val("");
            jQuery("#<portlet:namespace />secc_seleccionada").val("");
            jQuery("#<portlet:namespace />btnBuscarSeccional").show();

            <portlet:namespace />sincronizarAfiliadoFiltro();

            return false;
        }

        var id_seccional = jQuery("#<portlet:namespace />id_seccional").val();
        var seccional = jQuery("#<portlet:namespace />seccional").val();

        if ((seccional.length >= 3 || id_seccional.length > 2) && (keyPressed != 9 && keyPressed != 16)) {
            if (id_seccional.length > 2) {
                jQuery("#<portlet:namespace />seccional").val("");
            } else {
                jQuery("#<portlet:namespace />id_seccional").val("");
            }

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery("#divSeccional").load(url);
            jQuery("#divSeccional").show();
        } else {
            jQuery("#divSeccional").hide("slow");
        }

        return true;
    }

    function seleccionaCamposAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        jQuery('#<portlet:namespace />cuil').val(cuil);
        jQuery('#<portlet:namespace />inte').val(inte);
        jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
        jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
        jQuery('#<portlet:namespace />id_seccional').val(id_secc);
        jQuery('#<portlet:namespace />seccional').val(desc_secc);
        jQuery('#<portlet:namespace />apellido').val(apellido);
        jQuery('#<portlet:namespace />nombre').val(nombre);
        jQuery('#<portlet:namespace />secc_seleccionada').val('1');

        if (bajaFecha != null && bajaFecha != 'null') {
            jQuery('#<portlet:namespace />baja_fecha').val(bajaFecha);

            if (bajaFecha != '') {
                document.getElementById("<portlet:namespace />baja_fecha").style.background = "red";
                document.getElementById("<portlet:namespace />baja_fecha").style.color = "white";
            } else {
                document.getElementById("<portlet:namespace />baja_fecha").style.background = "white";
                document.getElementById("<portlet:namespace />baja_fecha").style.color = "black";
            }
        }

        jQuery('#<portlet:namespace />fecha_alta_af').val(fecha_alta_af != null && fecha_alta_af != 'null' ? fecha_alta_af : '');

        if (id_tercerizadora != null && id_tercerizadora != 'null' && id_tercerizadora != '') {
            jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora);
        }

        <portlet:namespace />sincronizarAfiliadoFiltro();

        if (popupAfill != null) {
            Liferay.Popup.close(popupAfill);
        }
    }

    function <portlet:namespace />buscarRequerimientos() {
        if (!<portlet:namespace />validarFiltroBusqueda()) {
            return false;
        }

        var estado = jQuery('#<portlet:namespace />estado').val();
        var sector_id = jQuery('#<portlet:namespace />sector_id').val();
        var afiliado_cuil_titular = jQuery('#<portlet:namespace />afiliado_cuil_titular').val();
        var afiliado_int = jQuery('#<portlet:namespace />afiliado_int').val();
        var afiliado_tipo_doc = jQuery('#<portlet:namespace />afiliado_tipo_doc').val();
        var afiliado_nro_doc = jQuery('#<portlet:namespace />afiliado_nro_doc').val();
        var afiliado_apellido = jQuery('#<portlet:namespace />afiliado_apellido').val();
        var afiliado_nombre = jQuery('#<portlet:namespace />afiliado_nombre').val();
        var afiliado_id_seccional = jQuery('#<portlet:namespace />afiliado_id_seccional').val();
        var id_tercerizadora = jQuery('#<portlet:namespace />id_tercerizadora').val();
        var recupero = jQuery('#<portlet:namespace />recupero').val();

        jQuery('#<portlet:namespace />buscando').show();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" />&struts_action=/compras/buscar_requerimientos' +
            '&estado=' + encodeURIComponent(estado) +
            '&sector_id=' + encodeURIComponent(sector_id) +
            '&afiliado_cuil_titular=' + encodeURIComponent(afiliado_cuil_titular) +
            '&afiliado_int=' + encodeURIComponent(afiliado_int) +
            '&afiliado_tipo_doc=' + encodeURIComponent(afiliado_tipo_doc) +
            '&afiliado_nro_doc=' + encodeURIComponent(afiliado_nro_doc) +
            '&afiliado_apellido=' + encodeURIComponent(afiliado_apellido) +
            '&afiliado_nombre=' + encodeURIComponent(afiliado_nombre) +
            '&afiliado_id_seccional=' + encodeURIComponent(afiliado_id_seccional) +
            '&id_tercerizadora=' + encodeURIComponent(id_tercerizadora) +
            '&recupero=' + encodeURIComponent(recupero);

        jQuery('#<portlet:namespace />busquedaRequerimientosDiv').load(url, function() {
            jQuery('#<portlet:namespace />buscando').hide();
        });

        return false;
    }

    function <portlet:namespace />altaRequerimiento() {
        var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/compras/editar_requerimiento" /></portlet:renderURL>';
        window.location.href = url;
    }

    jQuery(function() {
        jQuery('#<portlet:namespace />buscar').click(function() {
            <portlet:namespace />buscarRequerimientos();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />tipoDoc, #<portlet:namespace />nroDoc, #<portlet:namespace />apellido, #<portlet:namespace />nombre, #<portlet:namespace />id_seccional').change(function() {
            <portlet:namespace />sincronizarAfiliadoFiltro();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />tipoDoc, #<portlet:namespace />nroDoc, #<portlet:namespace />apellido, #<portlet:namespace />nombre, #<portlet:namespace />id_seccional').keyup(function() {
            <portlet:namespace />sincronizarAfiliadoFiltro();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />tipoDoc, #<portlet:namespace />nroDoc, #<portlet:namespace />apellido, #<portlet:namespace />nombre, #<portlet:namespace />id_tercerizadora').keypress(function(event) {
            if (event.which == 13) {
                <portlet:namespace />buscarRequerimientos();
                return false;
            }

            return true;
        });

        jQuery('#<portlet:namespace />buscando').show();
        <portlet:namespace />buscarRequerimientos();
    });
</script>
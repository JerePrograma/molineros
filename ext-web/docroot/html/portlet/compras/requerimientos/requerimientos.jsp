<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
String estadoForzado =
        (String) request.getAttribute(
                "COMPRAS_ESTADO_FORZADO"
        );

boolean estadoForzadoActivo =
        !WebKeysCompras.isEmpty(estadoForzado);

boolean showABMButtons =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )
        && !estadoForzadoActivo;

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>)
                renderRequest.getAttribute(
                        WebKeysCompras.SECTORES_REQUERIMIENTO
                );

if (sectores == null) {
    try {
        sectores =
                BusquedaRequerimientoCompraServiceUtil
                        .listarSectores();
    } catch (Exception e) {
        sectores =
                new ArrayList<RequerimientoCompraSector>();
    }
}

List<RequerimientoCompraEstado> estados =
        (List<RequerimientoCompraEstado>)
                renderRequest.getAttribute(
                        WebKeysCompras.ESTADOS_REQUERIMIENTO
                );

if (estados == null) {
    try {
        estados =
                BusquedaRequerimientoCompraServiceUtil
                        .listarEstados();
    } catch (Exception e) {
        estados =
                new ArrayList<RequerimientoCompraEstado>();
    }
}

String estadoFiltro =
        ParamUtil.getString(
                renderRequest,
                "estado",
                "0"
        );

if (WebKeysCompras.isEmpty(estadoFiltro)) {
    estadoFiltro = "0";
}

if (estadoForzadoActivo) {
    estadoFiltro = estadoForzado;
}

String sectorFiltro =
        ParamUtil.getString(
                renderRequest,
                "sector_id",
                "0"
        );

if (WebKeysCompras.isEmpty(sectorFiltro)) {
    sectorFiltro = "0";
}

String recuperoFiltro =
        ParamUtil.getString(
                renderRequest,
                "recupero",
                ""
        );

if (!"true".equals(recuperoFiltro)
        && !"false".equals(recuperoFiltro)) {

    recuperoFiltro = "";
}

String surgeFiltro =
        ParamUtil.getString(
                renderRequest,
                "surge",
                ""
        );

if (!"true".equals(surgeFiltro)
        && !"false".equals(surgeFiltro)) {

    surgeFiltro = "";
}

String idTercerizadoraFiltro =
        ParamUtil.getString(
                renderRequest,
                "id_tercerizadora",
                ""
        );

if (WebKeysCompras.isEmpty(
        idTercerizadoraFiltro
)) {
    idTercerizadoraFiltro =
            ParamUtil.getString(
                    renderRequest,
                    renderResponse.getNamespace()
                            + "id_tercerizadora",
                    ""
            );
}

if (idTercerizadoraFiltro != null) {
    idTercerizadoraFiltro =
            idTercerizadoraFiltro
                    .trim()
                    .toUpperCase();
}

if ("0".equals(idTercerizadoraFiltro)) {
    idTercerizadoraFiltro = "";
}

List<TercerizadoraServicio> tercerizadoras =
        TraeListasServiceUtil.getTercerizadoraServicio(
                renderRequest
        );

if (tercerizadoras == null) {
    tercerizadoras =
            new ArrayList<TercerizadoraServicio>();
}
%>

<fieldset class="block-labels">
    <legend>
        Filtro de búsqueda de requerimientos de compras
    </legend>

    <table class="lfr-table">
        <tr>
            <td>
                <label>Estado:</label>
            </td>

            <td>
                <select id="<portlet:namespace />estado"
                        name="<portlet:namespace />estado"
                        <%= estadoForzadoActivo
                                ? "disabled=\"disabled\""
                                : "" %>>

                    <option value="0"
                            <%= "0".equals(estadoFiltro)
                                    ? "selected"
                                    : "" %>>
                        Todos
                    </option>

                    <%
                    for (int i = 0; i < estados.size(); i++) {
                        RequerimientoCompraEstado estado =
                                estados.get(i);

                        String idEstado =
                                String.valueOf(
                                        estado.getIdEstado()
                                );
                    %>

                        <option value="<%= idEstado %>"
                                <%= idEstado.equals(estadoFiltro)
                                        ? "selected"
                                        : "" %>>

                            <%= HtmlUtil.escape(
                                    estado.getDescripcionVisible()
                            ) %>
                        </option>

                    <%
                    }
                    %>
                </select>
            </td>

            <td>
                <label>Sector:</label>
            </td>

            <td>
                <select id="<portlet:namespace />sector_id"
                        name="<portlet:namespace />sector_id">

                    <option value="0"
                            <%= "0".equals(sectorFiltro)
                                    ? "selected"
                                    : "" %>>
                        Todos
                    </option>

                    <%
                    for (int i = 0; i < sectores.size(); i++) {
                        RequerimientoCompraSector sector =
                                sectores.get(i);

                        String idSector =
                                String.valueOf(
                                        sector.getIdSector()
                                );
                    %>

                        <option value="<%= idSector %>"
                                <%= idSector.equals(sectorFiltro)
                                        ? "selected"
                                        : "" %>>

                            <%= HtmlUtil.escape(
                                    sector.getDescripcionVisible()
                            ) %>
                        </option>

                    <%
                    }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="6">
                <fieldset class="block-labels">
                    <legend>
                        Búsqueda por afiliado
                    </legend>

                    <liferay-util:include
                            page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">

                        <liferay-util:param
                                name="edit_mode"
                                value="true" />
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
            <td>
                <label>Tercerizadora:</label>
            </td>

            <td>
                <select id="<portlet:namespace />id_tercerizadora_combo"
                        name="<portlet:namespace />id_tercerizadora_combo">

                    <option value=""
                            <%= WebKeysCompras.isEmpty(
                                    idTercerizadoraFiltro
                            ) ? "selected" : "" %>>
                        Todas
                    </option>

                    <%
                    for (int i = 0; i < tercerizadoras.size(); i++) {
                        TercerizadoraServicio tercerizadora =
                                tercerizadoras.get(i);

                        String idTercerizadora =
                                tercerizadora != null
                                        ? tercerizadora.getId_tercerizadora()
                                        : "";

                        String descripcionTercerizadora =
                                tercerizadora != null
                                        ? tercerizadora.getDescripcion()
                                        : "";

                        if (WebKeysCompras.isEmpty(idTercerizadora)) {
                            continue;
                        }
                    %>
                        <option value="<%= HtmlUtil.escape(idTercerizadora) %>"
                                <%= idTercerizadora.equalsIgnoreCase(
                                        idTercerizadoraFiltro
                                ) ? "selected" : "" %>>
                            <%= HtmlUtil.escape(descripcionTercerizadora) %>
                        </option>
                    <%
                    }
                    %>
                </select>

                <input id="<portlet:namespace />id_tercerizadora_filtro"
                       name="<portlet:namespace />id_tercerizadora"
                       type="hidden"
                       value="<%= HtmlUtil.escape(
                               idTercerizadoraFiltro
                       ) %>" />
            </td>

            <td>
                <label>Surge:</label>
            </td>

            <td>
                <select id="<portlet:namespace />surge"
                        name="<portlet:namespace />surge">

                    <option value=""
                            <%= "".equals(surgeFiltro)
                                    ? "selected"
                                    : "" %>>
                        Todos
                    </option>

                    <option value="true"
                            <%= "true".equals(surgeFiltro)
                                    ? "selected"
                                    : "" %>>
                        Sí
                    </option>

                    <option value="false"
                            <%= "false".equals(surgeFiltro)
                                    ? "selected"
                                    : "" %>>
                        No
                    </option>
                </select>
            </td>

            <td colspan="2">&nbsp;</td>
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

                &nbsp;&nbsp;

                <input type="button"
                       value="Limpiar"
                       onClick="<portlet:namespace />limpiarTodosCamposFiltro();" />
            </td>

            <td colspan="5">
                <c:if test="<%= showABMButtons %>">

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
                <td>
                    <liferay-ui:message key="buscando" />
                </td>

                <td align="center">
                    <img alt="<liferay-ui:message key='buscando' />"
                         src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                </td>
            </tr>
        </table>
    </div>

    <div align="center"
         id="<portlet:namespace />busquedaRequerimientosDiv">
    </div>
</fieldset>

<script type="text/javascript">
    var popupAfill = null;
    var popup = null;

    function <portlet:namespace />trimValue(id) {
        return jQuery.trim(
                jQuery(
                        '#<portlet:namespace />' + id
                ).val()
        );
    }

    function <portlet:namespace />valorCredencialAfiliado() {
        return jQuery(
                '#<portlet:namespace />numero_afi'
        ).val();
    }

    function <portlet:namespace />normalizarTercerizadora(value) {
        if (value == null || value == 'null') {
            return '';
        }

        return jQuery.trim(value).toUpperCase();
    }

    function <portlet:namespace />hayAfiliadoSeleccionado() {
        var cuil = jQuery.trim(
                jQuery(
                        '#<portlet:namespace />cuil'
                ).val()
        );

        var inte = jQuery.trim(
                jQuery(
                        '#<portlet:namespace />inte'
                ).val()
        );

        return cuil != '' || inte != '';
    }

    function <portlet:namespace />sincronizarTercerizadoraFiltro() {
        var idTercerizadora =
                <portlet:namespace />normalizarTercerizadora(
                        jQuery(
                                '#<portlet:namespace />id_tercerizadora_combo'
                        ).val()
                );

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).val(idTercerizadora);

        jQuery(
                '#<portlet:namespace />id_tercerizadora_filtro'
        ).val(idTercerizadora);

        return idTercerizadora;
    }

    function <portlet:namespace />bloquearTercerizadoraPorAfiliado(
            idTercerizadora) {

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).removeAttr('disabled');

        return <portlet:namespace />sincronizarTercerizadoraFiltro();
    }

    function <portlet:namespace />desbloquearTercerizadoraFiltro(
            limpiar) {

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).removeAttr('disabled');

        if (limpiar) {
            jQuery(
                    '#<portlet:namespace />id_tercerizadora_combo'
            ).val('');

            jQuery(
                    '#<portlet:namespace />id_tercerizadora_filtro'
            ).val('');
        } else {
            <portlet:namespace />sincronizarTercerizadoraFiltro();
        }
    }

    function <portlet:namespace />sincronizarAfiliadoFiltro() {
        jQuery(
                '#<portlet:namespace />afiliado_cuil_titular'
        ).val(
                <portlet:namespace />trimValue('cuil')
        );

        jQuery(
                '#<portlet:namespace />afiliado_int'
        ).val(
                <portlet:namespace />trimValue('inte')
        );

        jQuery(
                '#<portlet:namespace />afiliado_tipo_doc'
        ).val(
                <portlet:namespace />trimValue('tipoDoc')
        );

        jQuery(
                '#<portlet:namespace />afiliado_nro_doc'
        ).val(
                <portlet:namespace />trimValue('nroDoc')
        );

        jQuery(
                '#<portlet:namespace />afiliado_apellido'
        ).val(
                <portlet:namespace />trimValue('apellido')
        );

        jQuery(
                '#<portlet:namespace />afiliado_nombre'
        ).val(
                <portlet:namespace />trimValue('nombre')
        );

        jQuery(
                '#<portlet:namespace />afiliado_id_seccional'
        ).val(
                <portlet:namespace />trimValue('id_seccional')
        );
    }

    function <portlet:namespace />limpiarCampoSiExiste(id) {
        var campo = jQuery(
                '#<portlet:namespace />' + id
        );

        if (campo.length > 0) {
            campo.val('');
        }
    }

    function <portlet:namespace />limpiarTodosCamposFiltro() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado
                == 'function') {

            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery(
                '#<portlet:namespace />estado'
        ).val(
                '<%= estadoForzadoActivo
                        ? estadoFiltro
                        : "0" %>'
        );

        jQuery(
                '#<portlet:namespace />sector_id'
        ).val('0');

        jQuery(
                '#<portlet:namespace />recupero'
        ).val('');

        jQuery(
                '#<portlet:namespace />surge'
        ).val('');

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).removeAttr('disabled');

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).val('');

        jQuery(
                '#<portlet:namespace />id_tercerizadora_filtro'
        ).val('');

        <portlet:namespace />limpiarCampoSiExiste('cuil');
        <portlet:namespace />limpiarCampoSiExiste('inte');
        <portlet:namespace />limpiarCampoSiExiste('tipoDoc');
        <portlet:namespace />limpiarCampoSiExiste('nroDoc');
        <portlet:namespace />limpiarCampoSiExiste('id_seccional');
        <portlet:namespace />limpiarCampoSiExiste('seccional');
        <portlet:namespace />limpiarCampoSiExiste('apellido');
        <portlet:namespace />limpiarCampoSiExiste('nombre');
        <portlet:namespace />limpiarCampoSiExiste('baja_fecha');
        <portlet:namespace />limpiarCampoSiExiste('fecha_alta_af');
        <portlet:namespace />limpiarCampoSiExiste('numero_afi');
        <portlet:namespace />limpiarCampoSiExiste('nombre_plan');
        <portlet:namespace />limpiarCampoSiExiste('id_plan');
        <portlet:namespace />limpiarCampoSiExiste('id_tercerizadora');
        <portlet:namespace />limpiarCampoSiExiste('afi_tercerizadora');
        <portlet:namespace />limpiarCampoSiExiste('incapacidad_af');
        <portlet:namespace />limpiarCampoSiExiste('secc_seleccionada');
        <portlet:namespace />limpiarCampoSiExiste('tieneAntecedentes');
        <portlet:namespace />limpiarCampoSiExiste('nroSocioPrevencion');
        <portlet:namespace />limpiarCampoSiExiste('nroCredencialPrevencion');

        jQuery(
                '#<portlet:namespace />afiliado_cuil_titular'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_int'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_tipo_doc'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_nro_doc'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_apellido'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_nombre'
        ).val('');

        jQuery(
                '#<portlet:namespace />afiliado_id_seccional'
        ).val('');

        var bajaInput =
                document.getElementById(
                        '<portlet:namespace />baja_fecha'
                );

        if (bajaInput) {
            bajaInput.style.background = 'white';
            bajaInput.style.color = 'black';
        }

        jQuery('#divSeccional').hide();

        <portlet:namespace />sincronizarAfiliadoFiltro();
        <portlet:namespace />sincronizarTercerizadoraFiltro();

        return false;
    }

    function <portlet:namespace />limpiarAfiliadoFiltro() {
        return <portlet:namespace />limpiarTodosCamposFiltro();
    }

    function <portlet:namespace />validarFiltroBusqueda() {
        <portlet:namespace />sincronizarAfiliadoFiltro();
        <portlet:namespace />desbloquearTercerizadoraFiltro(false);

        var cuil = jQuery.trim(
                jQuery(
                        '#<portlet:namespace />afiliado_cuil_titular'
                ).val()
        );

        if (cuil.length > 0
                && cuil.length == 11
                && typeof validarCuil == "function") {

            if (!validarCuil(
                    cuil,
                    "CUIL titular inválido."
            )) {
                jQuery(
                        '#<portlet:namespace />cuil'
                ).focus();

                return false;
            }
        }

        return true;
    }

    function <portlet:namespace />buscarAfiliados() {
        var cuil =
                jQuery(
                        '#<portlet:namespace />cuil'
                ).val();

        var inte =
                jQuery(
                        '#<portlet:namespace />inte'
                ).val();

        var tipoDoc =
                jQuery(
                        '#<portlet:namespace />tipoDoc'
                ).val();

        var nroDoc =
                jQuery(
                        '#<portlet:namespace />nroDoc'
                ).val();

        var seccional =
                jQuery(
                        '#<portlet:namespace />id_seccional'
                ).val();

        var apellido =
                jQuery(
                        '#<portlet:namespace />apellido'
                ).val();

        var nombre =
                jQuery(
                        '#<portlet:namespace />nombre'
                ).val();

        var entidad =
                jQuery(
                        '#<portlet:namespace />entidad'
                ).val();

        var numeroAfi =
                jQuery(
                        '#<portlet:namespace />numero_afi'
                ).val();

        if (!<portlet:namespace />validarBusqueda(
                cuil,
                inte,
                tipoDoc,
                nroDoc,
                seccional,
                apellido,
                nombre,
                entidad,
                numeroAfi
        )) {
            return false;
        }

        if (cuil.length > 0) {
            if (typeof validarCuil == "function"
                    && !validarCuil(
                            cuil,
                            "<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>"
                    )) {

                jQuery(
                        '#<portlet:namespace />cuil'
                ).focus();

                return false;
            }
        }

        if (jQuery(
                "#<portlet:namespace />secc_seleccionada"
        ).val() != "1") {

            jQuery(
                    "#<portlet:namespace />seccional"
            ).val("");

            jQuery(
                    "#<portlet:namespace />id_seccional"
            ).val("");
        }

        popupAfill = Liferay.Popup({
            title:
                    '<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />',
            modal: true,
            width: 830
        });

        var url =
                '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
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

    function <portlet:namespace />buscarAfiliados_(
            fechaReferencia) {

        return <portlet:namespace />buscarAfiliados();
    }

    function <portlet:namespace />buscarSeccional() {
        var id_seccional =
                jQuery(
                        "#<portlet:namespace />id_seccional"
                ).val();

        var seccional =
                jQuery(
                        "#<portlet:namespace />seccional"
                ).val();

        if (!<portlet:namespace />validaFormSecc(
                id_seccional,
                seccional
        )) {
            return false;
        }

        popup = Liferay.Popup({
            title:
                    '<liferay-ui:message key="busqueda-seccionales" />',
            modal: true,
            width: 420
        });

        var url =
                '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional='
                    + encodeURIComponent(id_seccional) +
                '&seccional='
                    + encodeURIComponent(seccional) +
                '&prefijo=';

        jQuery(popup).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccionalOnDiv(e) {
        var evtobj = window.event ? event : e;

        var keyPressed =
                evtobj.keyCode
                        ? evtobj.keyCode
                        : evtobj.charCode;

        if (jQuery(
                "#<portlet:namespace />secc_seleccionada"
        ).val() == "1"
                && keyPressed != 9
                && keyPressed != 16) {

            jQuery(
                    "#<portlet:namespace />seccional"
            ).val("");

            jQuery(
                    "#<portlet:namespace />id_seccional"
            ).val("");

            jQuery(
                    "#<portlet:namespace />secc_seleccionada"
            ).val("");

            jQuery(
                    "#<portlet:namespace />btnBuscarSeccional"
            ).show();

            <portlet:namespace />sincronizarAfiliadoFiltro();

            return false;
        }

        var id_seccional =
                jQuery(
                        "#<portlet:namespace />id_seccional"
                ).val();

        var seccional =
                jQuery(
                        "#<portlet:namespace />seccional"
                ).val();

        if ((seccional.length >= 3
                || id_seccional.length > 2)
                && keyPressed != 9
                && keyPressed != 16) {

            if (id_seccional.length > 2) {
                jQuery(
                        "#<portlet:namespace />seccional"
                ).val("");
            } else {
                jQuery(
                        "#<portlet:namespace />id_seccional"
                ).val("");
            }

            var url =
                    '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                    '&struts_action=/compras/buscar_seccional' +
                    '&id_seccional='
                        + encodeURIComponent(id_seccional) +
                    '&seccional='
                        + encodeURIComponent(seccional) +
                    '&prefijo=';

            jQuery(
                    "#divSeccional"
            ).load(url);

            jQuery(
                    "#divSeccional"
            ).show();
        } else {
            jQuery(
                    "#divSeccional"
            ).hide("slow");
        }

        return true;
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
            reclamoPrestacional,
            nroSocioPrev,
            nroCredenPrev,
            fechaRecepcion,
            tieneAntecedentes) {

        jQuery(
                '#<portlet:namespace />cuil'
        ).val(cuil);

        jQuery(
                '#<portlet:namespace />inte'
        ).val(inte);

        jQuery(
                '#<portlet:namespace />tipoDoc'
        ).val(docu_tipo);

        jQuery(
                '#<portlet:namespace />nroDoc'
        ).val(docu_nro);

        jQuery(
                '#<portlet:namespace />id_seccional'
        ).val(id_secc);

        jQuery(
                '#<portlet:namespace />seccional'
        ).val(desc_secc);

        jQuery(
                '#<portlet:namespace />apellido'
        ).val(apellido);

        jQuery(
                '#<portlet:namespace />nombre'
        ).val(nombre);

        jQuery(
                '#<portlet:namespace />secc_seleccionada'
        ).val('1');

        var entidadSeleccionada =
                jQuery(
                        '#<portlet:namespace />entidad'
                ).val();

        var numeroAfiliado = '';

        if (entidadSeleccionada
                == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {

            numeroAfiliado = ospim;
        }

        if (entidadSeleccionada
                == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {

            numeroAfiliado = uoma;
        }

        if (entidadSeleccionada
                == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {

            numeroAfiliado = amtima;
        }

        if (numeroAfiliado == null
                || numeroAfiliado == 'null') {

            numeroAfiliado = '';
        }

        jQuery(
                '#<portlet:namespace />numero_afi'
        ).val(numeroAfiliado);

        if (bajaFecha != null
                && bajaFecha != 'null') {

            jQuery(
                    '#<portlet:namespace />baja_fecha'
            ).val(bajaFecha);

            var bajaFechaInput =
                    document.getElementById(
                            "<portlet:namespace />baja_fecha"
                    );

            if (bajaFechaInput) {
                if (bajaFecha != '') {
                    bajaFechaInput.style.background = "red";
                    bajaFechaInput.style.color = "white";
                } else {
                    bajaFechaInput.style.background = "white";
                    bajaFechaInput.style.color = "black";
                }
            }
        }

        jQuery(
                '#<portlet:namespace />fecha_alta_af'
        ).val(
                fecha_alta_af != null
                && fecha_alta_af != 'null'
                        ? fecha_alta_af
                        : ''
        );

        if (jQuery(
                '#<portlet:namespace />nombre_plan'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />nombre_plan'
            ).val(
                    nombre_plan != null
                    && nombre_plan != 'null'
                            ? nombre_plan
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />id_plan'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />id_plan'
            ).val(
                    id_plan != null
                    && id_plan != 'null'
                            ? id_plan
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />id_tercerizadora'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />id_tercerizadora'
            ).val(
                    id_tercerizadora != null
                    && id_tercerizadora != 'null'
                            ? id_tercerizadora
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />afi_tercerizadora'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />afi_tercerizadora'
            ).val(
                    afi_tercerizadora != null
                    && afi_tercerizadora != 'null'
                            ? afi_tercerizadora
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />incapacidad_af'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />incapacidad_af'
            ).val(
                    incapacidad_af != null
                    && incapacidad_af != 'null'
                            ? incapacidad_af
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />nroSocioPrevencion'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />nroSocioPrevencion'
            ).val(
                    nroSocioPrev != null
                    && nroSocioPrev != 'null'
                            ? nroSocioPrev
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />nroCredencialPrevencion'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />nroCredencialPrevencion'
            ).val(
                    nroCredenPrev != null
                    && nroCredenPrev != 'null'
                            ? nroCredenPrev
                            : ''
            );
        }

        if (jQuery(
                '#<portlet:namespace />tieneAntecedentes'
        ).length > 0) {
            jQuery(
                    '#<portlet:namespace />tieneAntecedentes'
            ).val(
                    tieneAntecedentes == '1'
                            ? '1'
                            : '0'
            );
        }

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).removeAttr('disabled');

        <portlet:namespace />sincronizarTercerizadoraFiltro();
        <portlet:namespace />sincronizarAfiliadoFiltro();

        if (popupAfill != null) {
            Liferay.Popup.close(popupAfill);
        }
    }

    function <portlet:namespace />buscarRequerimientos() {
        if (!<portlet:namespace />validarFiltroBusqueda()) {
            return false;
        }

        var estado =
                '<%= estadoForzadoActivo
                        ? estadoFiltro
                        : "" %>';

        if (estado == '') {
            estado =
                    jQuery(
                            '#<portlet:namespace />estado'
                    ).val();
        }

        var sector_id =
                jQuery(
                        '#<portlet:namespace />sector_id'
                ).val();

        var afiliado_cuil_titular =
                jQuery(
                        '#<portlet:namespace />afiliado_cuil_titular'
                ).val();

        var afiliado_int =
                jQuery(
                        '#<portlet:namespace />afiliado_int'
                ).val();

        var afiliado_tipo_doc =
                jQuery(
                        '#<portlet:namespace />afiliado_tipo_doc'
                ).val();

        var afiliado_nro_doc =
                jQuery(
                        '#<portlet:namespace />afiliado_nro_doc'
                ).val();

        var afiliado_apellido =
                jQuery(
                        '#<portlet:namespace />afiliado_apellido'
                ).val();

        var afiliado_nombre =
                jQuery(
                        '#<portlet:namespace />afiliado_nombre'
                ).val();

        var afiliado_id_seccional =
                jQuery(
                        '#<portlet:namespace />afiliado_id_seccional'
                ).val();

        var id_tercerizadora =
                <portlet:namespace />sincronizarTercerizadoraFiltro();

        var recupero =
                jQuery(
                        '#<portlet:namespace />recupero'
                ).val();

        var surge =
                jQuery(
                        '#<portlet:namespace />surge'
                ).val();

        jQuery(
                '#<portlet:namespace />buscando'
        ).show();

        var url =
                '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" />' +
                '&struts_action=/compras/buscar_requerimientos' +
                '&estado='
                    + encodeURIComponent(estado) +
                '&sector_id='
                    + encodeURIComponent(sector_id) +
                '&afiliado_cuil_titular='
                    + encodeURIComponent(
                            afiliado_cuil_titular
                    ) +
                '&afiliado_int='
                    + encodeURIComponent(
                            afiliado_int
                    ) +
                '&afiliado_tipo_doc='
                    + encodeURIComponent(
                            afiliado_tipo_doc
                    ) +
                '&afiliado_nro_doc='
                    + encodeURIComponent(
                            afiliado_nro_doc
                    ) +
                '&afiliado_apellido='
                    + encodeURIComponent(
                            afiliado_apellido
                    ) +
                '&afiliado_nombre='
                    + encodeURIComponent(
                            afiliado_nombre
                    ) +
                '&afiliado_id_seccional='
                    + encodeURIComponent(
                            afiliado_id_seccional
                    ) +
                '&id_tercerizadora='
                    + encodeURIComponent(
                            id_tercerizadora
                    ) +
                '&recupero='
                    + encodeURIComponent(
                            recupero
                    ) +
                '&surge='
                    + encodeURIComponent(
                            surge
                    ) +
                '&<portlet:namespace />estado='
                    + encodeURIComponent(estado) +
                '&<portlet:namespace />sector_id='
                    + encodeURIComponent(sector_id) +
                '&<portlet:namespace />afiliado_cuil_titular='
                    + encodeURIComponent(
                            afiliado_cuil_titular
                    ) +
                '&<portlet:namespace />afiliado_int='
                    + encodeURIComponent(
                            afiliado_int
                    ) +
                '&<portlet:namespace />afiliado_tipo_doc='
                    + encodeURIComponent(
                            afiliado_tipo_doc
                    ) +
                '&<portlet:namespace />afiliado_nro_doc='
                    + encodeURIComponent(
                            afiliado_nro_doc
                    ) +
                '&<portlet:namespace />afiliado_apellido='
                    + encodeURIComponent(
                            afiliado_apellido
                    ) +
                '&<portlet:namespace />afiliado_nombre='
                    + encodeURIComponent(
                            afiliado_nombre
                    ) +
                '&<portlet:namespace />afiliado_id_seccional='
                    + encodeURIComponent(
                            afiliado_id_seccional
                    ) +
                '&<portlet:namespace />id_tercerizadora='
                    + encodeURIComponent(
                            id_tercerizadora
                    ) +
                '&<portlet:namespace />recupero='
                    + encodeURIComponent(
                            recupero
                    ) +
                '&<portlet:namespace />surge='
                    + encodeURIComponent(
                            surge
                    );

        jQuery(
                '#<portlet:namespace />busquedaRequerimientosDiv'
        ).load(
                url,
                function() {
                    jQuery(
                            '#<portlet:namespace />buscando'
                    ).hide();
                }
        );

        return false;
    }

    function <portlet:namespace />altaRequerimiento() {
        var url =
                '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">' +
                '<portlet:param name="struts_action" value="/compras/nuevo_requerimiento" />' +
                '<portlet:param name="modo" value="alta" />' +
                '</portlet:renderURL>';

        window.location.href = url;
    }

    jQuery(function() {
        jQuery(
                '#<portlet:namespace />buscar'
        ).click(function() {
            <portlet:namespace />buscarRequerimientos();
        });

        jQuery(
                '#<portlet:namespace />estado, '
                + '#<portlet:namespace />sector_id, '
                + '#<portlet:namespace />recupero, '
                + '#<portlet:namespace />surge'
        ).change(function() {
            <portlet:namespace />buscarRequerimientos();
        });

        jQuery(
                '#<portlet:namespace />id_tercerizadora_combo'
        ).change(function() {
            <portlet:namespace />sincronizarTercerizadoraFiltro();
            <portlet:namespace />buscarRequerimientos();
        });

        jQuery(
                '#<portlet:namespace />cuil, '
                + '#<portlet:namespace />inte, '
                + '#<portlet:namespace />tipoDoc, '
                + '#<portlet:namespace />nroDoc, '
                + '#<portlet:namespace />apellido, '
                + '#<portlet:namespace />nombre, '
                + '#<portlet:namespace />id_seccional'
        ).change(function() {
            <portlet:namespace />sincronizarAfiliadoFiltro();
            <portlet:namespace />desbloquearTercerizadoraFiltro(false);
        });

        jQuery(
                '#<portlet:namespace />cuil, '
                + '#<portlet:namespace />inte, '
                + '#<portlet:namespace />tipoDoc, '
                + '#<portlet:namespace />nroDoc, '
                + '#<portlet:namespace />apellido, '
                + '#<portlet:namespace />nombre, '
                + '#<portlet:namespace />id_seccional'
        ).keyup(function() {
            <portlet:namespace />sincronizarAfiliadoFiltro();
            <portlet:namespace />desbloquearTercerizadoraFiltro(false);
        });

        jQuery(
                '#<portlet:namespace />cuil, '
                + '#<portlet:namespace />inte, '
                + '#<portlet:namespace />tipoDoc, '
                + '#<portlet:namespace />nroDoc, '
                + '#<portlet:namespace />apellido, '
                + '#<portlet:namespace />nombre'
        ).keypress(function(event) {
            if (event.which == 13) {
                <portlet:namespace />buscarRequerimientos();
                return false;
            }

            return true;
        });

        <portlet:namespace />desbloquearTercerizadoraFiltro(false);

        jQuery(
                '#<portlet:namespace />buscando'
        ).show();

        <portlet:namespace />buscarRequerimientos();
    });
</script>

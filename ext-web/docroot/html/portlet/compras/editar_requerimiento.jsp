<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean editable = esNuevo || req.isEditable();

List<RequerimientoCompraSector> sectores = (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA);

if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL verURL = renderResponse.createRenderURL();
verURL.setWindowState(WindowState.MAXIMIZED);
verURL.setParameter("struts_action", "/compras/ver_requerimiento");
verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String solicitanteDefault = req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : (user != null ? user.getScreenName() : "");
String solicitanteNombreDefault = req.getSolicitanteNombre() != null ? req.getSolicitanteNombre() : (user != null ? user.getFullName() : "");
String reqSectorId = req.getSectorId() != null ? String.valueOf(req.getSectorId().intValue()) : "";

String afiliadoCuilTitular = req.getAfiliadoCuilTitularVisible();

if (afiliadoCuilTitular == null) {
    afiliadoCuilTitular = "";
}

String afiliadoInte = req.getAfiliadoInteString();

if (afiliadoInte == null) {
    afiliadoInte = "";
}

String afiliadoIdSeccional = "";
String afiliadoSeccional = "";

try {
    Object valorAfiliadoIdSeccional = req.getClass().getMethod("getAfiliadoIdSeccionalString", new Class[0]).invoke(req, new Object[0]);

    if (valorAfiliadoIdSeccional != null) {
        afiliadoIdSeccional = String.valueOf(valorAfiliadoIdSeccional);
    }
} catch (Exception e) {
    afiliadoIdSeccional = "";
}

try {
    Object valorAfiliadoSeccional = req.getClass().getMethod("getAfiliadoSeccionalVisible", new Class[0]).invoke(req, new Object[0]);

    if (valorAfiliadoSeccional != null) {
        afiliadoSeccional = String.valueOf(valorAfiliadoSeccional);
    }
} catch (Exception e) {
    afiliadoSeccional = "";
}
%>

<c:if test="<%= !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= puedeABM && !editable %>">
    <div class="portlet-msg-info">El requerimiento solo puede editarse en estado Borrador.</div>

    <table class="lfr-table">
        <tr>
            <td>
                <input type="button"
                       value="Ver"
                       onClick="window.location.href='<%= verURL.toString() %>';" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Volver"
                       onClick="window.location.href='<%= volverURL.toString() %>';" />
            </td>
        </tr>
    </table>
</c:if>

<c:if test="<%= puedeABM && editable %>">
    <form action="<%= actionURL.toString() %>" method="post" name="<portlet:namespace />fm">
        <input type="hidden"
               name="<portlet:namespace /><%= Constants.CMD %>"
               id="<portlet:namespace /><%= Constants.CMD %>"
               value="<%= esNuevo ? Constants.ADD : Constants.UPDATE %>" />

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               id="<portlet:namespace />id_requerimiento_compra"
               value="<%= req.getIdRequerimientoCompra() %>" />

        <input type="hidden"
               name="<portlet:namespace />id_estado"
               id="<portlet:namespace />id_estado"
               value="<%= req.getIdEstado() %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table">
                <tr>
                    <td><label>Estado:</label></td>
                    <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Fecha solicitud:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />fecha_solicitud"
                               id="<portlet:namespace />fecha_solicitud"
                               value="<%= HtmlUtil.escape(req.getFechaSolicitudAsString()) %>"
                               size="10"
                               maxlength="10" />
                        dd/MM/yyyy
                    </td>

                    <td><label>Sector:</label></td>
                    <td>
                        <select name="<portlet:namespace />sector_id"
                                id="<portlet:namespace />sector_id">
                            <option value="0">Seleccione</option>

                            <%
                            for (int i = 0; i < sectores.size(); i++) {
                                RequerimientoCompraSector sector = sectores.get(i);
                                String sectorId = String.valueOf(sector.getIdSector());
                                String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            %>
                                <option value="<%= sectorId %>"
                                        data-requiere-afiliado="<%= sector.isRequiereAfiliado() ? "true" : "false" %>"
                                        <%= selected %>><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Solicitante usuario:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />solicitante_usr"
                               id="<portlet:namespace />solicitante_usr"
                               value="<%= HtmlUtil.escape(solicitanteDefault) %>"
                               size="30"
                               maxlength="75" />
                    </td>

                    <td><label>Solicitante nombre:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />solicitante_nombre"
                               id="<portlet:namespace />solicitante_nombre"
                               value="<%= HtmlUtil.escape(solicitanteNombreDefault) %>"
                               size="35"
                               maxlength="120" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset id="<portlet:namespace />afiliado_requerimiento_panel" class="block-labels">
            <legend>Afiliado</legend>

            <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
                <liferay-util:param name="edit_mode" value="<%= String.valueOf(true) %>" />
            </liferay-util:include>

            <input type="hidden"
                   name="<portlet:namespace />afiliado_cuil_titular"
                   id="<portlet:namespace />afiliado_cuil_titular"
                   value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

            <input type="hidden"
                   name="<portlet:namespace />afiliado_inte"
                   id="<portlet:namespace />afiliado_inte"
                   value="<%= HtmlUtil.escape(afiliadoInte) %>" />

            <input type="hidden"
                   name="<portlet:namespace />afiliado_id_seccional"
                   id="<portlet:namespace />afiliado_id_seccional"
                   value="<%= HtmlUtil.escape(afiliadoIdSeccional) %>" />

            <input type="hidden"
                   name="<portlet:namespace />afiliado_seccional"
                   id="<portlet:namespace />afiliado_seccional"
                   value="<%= HtmlUtil.escape(afiliadoSeccional) %>" />
        </fieldset>

        <fieldset class="block-labels">
            <legend>Solicitud</legend>

            <table class="lfr-table">
                <tr>
                    <td><label>Descripci&oacute;n:</label></td>
                    <td colspan="3">
                        <input type="text"
                               name="<portlet:namespace />descripcion"
                               id="<portlet:namespace />descripcion"
                               value="<%= HtmlUtil.escape(req.getDescripcionVisible()) %>"
                               size="100"
                               maxlength="500" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />observaciones"
                                  id="<portlet:namespace />observaciones"
                                  cols="100"
                                  rows="4"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <table class="lfr-table">
            <tr>
                <td>
                    <input type="button"
                           value="Guardar"
                           onClick="<portlet:namespace />guardar();" />

                    <c:if test="<%= !esNuevo %>">
                        &nbsp;&nbsp;

                        <input type="button"
                               value="Ver"
                               onClick="window.location.href='<%= verURL.toString() %>';" />
                    </c:if>

                    &nbsp;&nbsp;

                    <input type="button"
                           value="Volver"
                           onClick="window.location.href='<%= volverURL.toString() %>';" />
                </td>
            </tr>
        </table>
    </form>

    <c:if test="<%= req.getIdRequerimientoCompra() > 0 %>">
        <liferay-util:include page="/html/portlet/compras/requerimiento_detalle.jsp" />
    </c:if>
</c:if>

<script type="text/javascript">
    /*
     * Overrides locales para Compras.
     *
     * No se modifica el módulo Autorizaciones.
     * No se crean actions duplicadas en Compras.
     * Se reutilizan:
     * - /autorizaciones/buscar_afiliados
     * - /autorizaciones/buscar_seccional
     */

    function <portlet:namespace />buscarAfiliados() {
        jQuery('#<portlet:namespace />divObservacionesInternas').hide();

        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numero_afi = jQuery('#<portlet:namespace />numero_afi').val();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, numero_afi)) {
            return false;
        }

        if (cuil.length > 0) {
            if (!validarCuil(cuil, "<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")) {
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

        var fecha_prestacion = 'null';

        try {
            fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();
        } catch (err) {
            fecha_prestacion = 'null';
        }

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
            '&numero_afi=' + encodeURIComponent(numero_afi) +
            '&fecha_referencia=' + encodeURIComponent(fecha_prestacion) +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);
    }

    function <portlet:namespace />buscarAfiliados_(fecha_prest) {
        jQuery('#<portlet:namespace />divObservacionesInternas').hide();

        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numero_afi = jQuery('#<portlet:namespace />numero_afi').val();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, numero_afi)) {
            return false;
        }

        if (cuil.length > 0) {
            if (!validarCuil(cuil, "<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")) {
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

        var fecha_prestacion = fecha_prest;

        try {
            fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();
        } catch (err) {
            fecha_prestacion = fecha_prest;
        }

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
            '&numero_afi=' + encodeURIComponent(numero_afi) +
            '&fecha_referencia=' + encodeURIComponent(fecha_prestacion) +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);
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
            '&struts_action=/autorizaciones/buscar_seccional' +
            '&id_seccional=' + encodeURIComponent(id_seccional) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&prefijo=';

        jQuery(popup).load(url);
    }

    function <portlet:namespace />buscarSeccionalOnDiv(e) {
        var evtobj = window.event ? event : e;
        var keyPressed = evtobj.keyCode ? evtobj.keyCode : evtobj.charCode;

        if (jQuery("#<portlet:namespace />secc_seleccionada").val() == "1" && (keyPressed != 9 && keyPressed != 16)) {
            jQuery("#<portlet:namespace />seccional").val("");
            jQuery("#<portlet:namespace />id_seccional").val("");
            jQuery("#<portlet:namespace />secc_seleccionada").val("");
            jQuery("#<portlet:namespace />btnBuscarSeccional").show();

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

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
                '&struts_action=/autorizaciones/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery("#divSeccional").load(url);
            jQuery("#divSeccional").show();
        } else {
            jQuery("#divSeccional").hide("slow");
        }
    }

    /*
     * Override clave:
     * El JSP de Autorizaciones llama a seleccionaAfiliado(),
     * y seleccionaAfiliado() llama a seleccionaCamposAfiliado().
     *
     * Pisamos esta función para que Compras no ejecute lógica extra de Autorizaciones
     * ni AJAX a /autorizaciones/buscar_afiliado_datos.
     */
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

        var entidadSeleccionada = jQuery('#<portlet:namespace />entidad').val();

        if (entidadSeleccionada == 'OSPIM') {
            jQuery('#<portlet:namespace />numero_afi').val(ospim != null && ospim != 'null' ? ospim : '');
        } else if (entidadSeleccionada == 'UOMA') {
            jQuery('#<portlet:namespace />numero_afi').val(uoma != null && uoma != 'null' ? uoma : '');
        } else if (entidadSeleccionada == 'AMTIMA') {
            jQuery('#<portlet:namespace />numero_afi').val(amtima != null && amtima != 'null' ? amtima : '');
        }

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
        jQuery('#<portlet:namespace />incapacidad_af').val(incapacidad_af != null && incapacidad_af != 'null' ? incapacidad_af : '');
        jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora != null && id_tercerizadora != 'null' ? id_tercerizadora : '');
        jQuery('#<portlet:namespace />nroSocioPrevencion').val(nroSocioPrev != null && nroSocioPrev != 'null' ? nroSocioPrev : '');
        jQuery('#<portlet:namespace />nroCredencialPrevencion').val(nroCredenPrev != null && nroCredenPrev != 'null' ? nroCredenPrev : '');
        jQuery('#<portlet:namespace />tieneAntecedentes').val(tieneAntecedentes == '1' ? '1' : '0');

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />trimValue(id) {
        return jQuery.trim(jQuery('#<portlet:namespace />' + id).val());
    }

    function <portlet:namespace />sectorRequiereAfiliado() {
        return jQuery('#<portlet:namespace />sector_id option:selected').attr('data-requiere-afiliado') == 'true';
    }

    function <portlet:namespace />sincronizarAfiliadoRequerimiento() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(<portlet:namespace />trimValue('cuil'));
        jQuery('#<portlet:namespace />afiliado_inte').val(<portlet:namespace />trimValue('inte'));
        jQuery('#<portlet:namespace />afiliado_id_seccional').val(<portlet:namespace />trimValue('id_seccional'));
        jQuery('#<portlet:namespace />afiliado_seccional').val(<portlet:namespace />trimValue('seccional'));
    }

    function <portlet:namespace />cargarAfiliadoInicial() {
        var afiliadoCuilTitular = jQuery('#<portlet:namespace />afiliado_cuil_titular').val();
        var afiliadoInte = jQuery('#<portlet:namespace />afiliado_inte').val();
        var afiliadoIdSeccional = jQuery('#<portlet:namespace />afiliado_id_seccional').val();
        var afiliadoSeccional = jQuery('#<portlet:namespace />afiliado_seccional').val();

        if (afiliadoCuilTitular != '') {
            jQuery('#<portlet:namespace />cuil').val(afiliadoCuilTitular);
        }

        if (afiliadoInte != '') {
            jQuery('#<portlet:namespace />inte').val(afiliadoInte);
        }

        if (afiliadoIdSeccional != '') {
            jQuery('#<portlet:namespace />id_seccional').val(afiliadoIdSeccional);
            jQuery('#<portlet:namespace />secc_seleccionada').val('1');
        }

        if (afiliadoSeccional != '') {
            jQuery('#<portlet:namespace />seccional').val(afiliadoSeccional);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_inte').val('');
        jQuery('#<portlet:namespace />afiliado_id_seccional').val('');
        jQuery('#<portlet:namespace />afiliado_seccional').val('');

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere) {
        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (requiereAfiliado) {
            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').show();
        } else {
            if (limpiarSiNoRequiere) {
                <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            }

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').hide();
        }
    }

    function <portlet:namespace />guardar() {
        if (<portlet:namespace />trimValue('sector_id') == '0') {
            alert('Debe informar sector.');
            jQuery('#<portlet:namespace />sector_id').focus();
            return;
        }

        if (<portlet:namespace />trimValue('solicitante_usr') == '') {
            alert('Debe informar solicitante.');
            jQuery('#<portlet:namespace />solicitante_usr').focus();
            return;
        }

        if (<portlet:namespace />trimValue('descripcion') == '') {
            alert('Debe informar descripci&oacute;n del requerimiento.');
            jQuery('#<portlet:namespace />descripcion').focus();
            return;
        }

        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (requiereAfiliado) {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            var afiliadoCuilTitular = <portlet:namespace />trimValue('afiliado_cuil_titular');
            var afiliadoInte = <portlet:namespace />trimValue('afiliado_inte');
            var afiliadoIdSeccional = <portlet:namespace />trimValue('afiliado_id_seccional');

            if (afiliadoCuilTitular == '' || afiliadoInte == '') {
                alert('Debe seleccionar un afiliado.');
                jQuery('#<portlet:namespace />cuil').focus();
                return;
            }

            if (afiliadoIdSeccional == '') {
                alert('Debe informar seccional del afiliado.');
                jQuery('#<portlet:namespace />seccional').focus();
                return;
            }
        } else {
            <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        }

        submitForm(document.<portlet:namespace />fm);
    }

    jQuery(function() {
        <portlet:namespace />cargarAfiliadoInicial();
        <portlet:namespace />actualizarVisibilidadAfiliado(false);

        jQuery('#<portlet:namespace />sector_id').change(function() {
            <portlet:namespace />actualizarVisibilidadAfiliado(true);
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_seccional, #<portlet:namespace />seccional').change(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_seccional, #<portlet:namespace />seccional').keyup(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });
    });
</script>
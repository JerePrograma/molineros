<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
RequerimientoCompra req =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (req == null) {
    req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

Object soloLecturaAttr = renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String strutsActionActual = ParamUtil.getString(renderRequest, "struts_action", "");
String modo = ParamUtil.getString(renderRequest, "modo", "");

boolean soloLectura =
        Boolean.TRUE.equals(soloLecturaAttr)
        || "/compras/ver_requerimiento".equals(strutsActionActual)
        || "ver".equalsIgnoreCase(modo);

boolean editablePorEstado = esNuevo || req.isEditable();

boolean puedeEditarPantalla =
        puedeABM
        && editablePorEstado
        && !soloLectura;

renderRequest.setAttribute(
        WebKeysCompras.SOLO_LECTURA_ATTR,
        Boolean.valueOf(soloLectura)
);

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO);

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

PortletURL editarURL = renderResponse.createRenderURL();
editarURL.setWindowState(WindowState.MAXIMIZED);
editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String reqSectorId = req.getSectorId() != null ? String.valueOf(req.getSectorId().intValue()) : "";
String sectorDescripcionSoloLectura = req.getSectorDescripcionVisible();

if (sectorDescripcionSoloLectura.length() == 0) {
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());

        if (reqSectorId.equals(sectorId)) {
            sectorDescripcionSoloLectura = sector.getDescripcionVisible();
            break;
        }
    }
}

String afiliadoCuilTitular = req.getAfiliadoCuilTitularVisible();
String afiliadoInt = req.getAfiliadoIntString();
String idTercerizadora = req.getIdTercerizadora();
int cargoOspimActual = req.getCargoOspim() != null
        ? req.getCargoOspim().intValue()
        : 0;

int cargoTercerizadoraActual = req.getCargoTercerizadora() != null
        ? req.getCargoTercerizadora().intValue()
        : 0;

boolean mostrarTercerizadoraPorCargos =
        !(cargoOspimActual == 100 && cargoTercerizadoraActual == 0);
Afiliado afiliadoRequerimiento =
        (Afiliado) renderRequest.getAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);

if (idTercerizadora == null) {
    idTercerizadora = "";
}

String afiliadoCuilVisible = afiliadoCuilTitular;
String afiliadoIntVisible = afiliadoInt;
String afiliadoTipoDocumento = "";
String afiliadoNumeroDocumento = "";
String afiliadoApellido = "";
String afiliadoNombre = "";
String afiliadoSeccional = "";
String afiliadoBajaFecha = "";
String afiliadoFechaAlta = "";
String afiliadoIdTercerizadora = idTercerizadora;
String afiliadoIncapacidad = "";
String afiliadoAntecedentes = "";

if (afiliadoRequerimiento != null) {
    afiliadoCuilVisible = afiliadoRequerimiento.getCuil_titular() != null ? afiliadoRequerimiento.getCuil_titular() : afiliadoCuilTitular;
    afiliadoIntVisible = afiliadoRequerimiento.getInteAsString();
    afiliadoTipoDocumento = afiliadoRequerimiento.getDocumento_tipo() != null ? afiliadoRequerimiento.getDocumento_tipo() : "";
    afiliadoNumeroDocumento = afiliadoRequerimiento.getDocu_numero() != null ? afiliadoRequerimiento.getDocu_numero() : "";
    afiliadoApellido = afiliadoRequerimiento.getApellido() != null ? afiliadoRequerimiento.getApellido() : "";
    afiliadoNombre = afiliadoRequerimiento.getNombre() != null ? afiliadoRequerimiento.getNombre() : "";

    if (afiliadoRequerimiento.getSeccional() != null) {
        afiliadoSeccional = afiliadoRequerimiento.getSeccional().getDescripcion() != null
                ? afiliadoRequerimiento.getSeccional().getDescripcion()
                : "";
    }

    afiliadoBajaFecha = afiliadoRequerimiento.getBaja_fechaAsString();
    afiliadoFechaAlta = afiliadoRequerimiento.getAlta_fechaAsString();
    afiliadoIdTercerizadora = afiliadoRequerimiento.getId_tercerizadora() != null
            ? afiliadoRequerimiento.getId_tercerizadora()
            : idTercerizadora;
    afiliadoIncapacidad = afiliadoRequerimiento.getDiscapacitado() != null ? afiliadoRequerimiento.getDiscapacitado() : "";
    afiliadoAntecedentes = afiliadoRequerimiento.getTieneAntecedentesJudiciales() == 1 ? "SI" : "NO";
}

String recuperoChecked = mostrarTercerizadoraPorCargos ? "checked=\"checked\"" : "";

String errorParaAlert =
        (String) renderRequest.getAttribute(WebKeysCompras.ERROR_PARA_ALERT);

if (errorParaAlert == null) {
    errorParaAlert = "";
}

String errorCampoCompra =
        (String) renderRequest.getAttribute(WebKeysCompras.ERROR_CAMPO_COMPRA);

if (errorCampoCompra == null) {
    errorCampoCompra = "";
}
%>

<c:if test="<%= !WebKeysCompras.isEmpty(errorParaAlert) %>">
    <div class="portlet-msg-error">
        <strong>No se pudo guardar el requerimiento.</strong>
        <br />
        <%= HtmlUtil.escape(errorParaAlert) %>
    </div>
</c:if>

<c:if test="<%= !soloLectura && !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= !soloLectura && puedeABM && !editablePorEstado %>">
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

<c:if test="<%= soloLectura %>">
    <fieldset class="block-labels">
        <legend>Ver requerimiento de compra</legend>

        <table class="lfr-table">
            <tr>
                <td><label>ID:</label></td>
                <td><%= HtmlUtil.escape(req.getIdString()) %></td>

                <td><label>Estado:</label></td>
                <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Sector:</label></td>
                <td><%= HtmlUtil.escape(sectorDescripcionSoloLectura) %></td>

                <td><label>Alta:</label></td>
                <td><%= HtmlUtil.escape(req.getAltaFechaAsString()) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>CUIL titular:</label></td>
                <td><%= HtmlUtil.escape(afiliadoCuilVisible) %></td>

                <td><label>Integrante:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIntVisible) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Tipo documento:</label></td>
                <td><%= HtmlUtil.escape(afiliadoTipoDocumento) %></td>

                <td><label>Nro. documento:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNumeroDocumento) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Apellido:</label></td>
                <td><%= HtmlUtil.escape(afiliadoApellido) %></td>

                <td><label>Nombre:</label></td>
                <td><%= HtmlUtil.escape(afiliadoNombre) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Seccional:</label></td>
                <td><%= HtmlUtil.escape(afiliadoSeccional) %></td>

                <td><label>Baja:</label></td>
                <td><%= HtmlUtil.escape(afiliadoBajaFecha) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Fecha alta afiliado:</label></td>
                <td><%= HtmlUtil.escape(afiliadoFechaAlta) %></td>

                <td><label>Incapacidad:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIncapacidad) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>ID tercerizadora:</label></td>
                <td><%= HtmlUtil.escape(afiliadoIdTercerizadora) %></td>

                <td><label>Antecedentes:</label></td>
                <td><%= HtmlUtil.escape(afiliadoAntecedentes) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Cargo OSPIM %:</label></td>
                <td><%= HtmlUtil.escape(req.getCargoOspimString()) %></td>

                <td><label>Cargo tercerizadora %:</label></td>
                <td><%= HtmlUtil.escape(req.getCargoTercerizadoraString()) %></td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <% if (mostrarTercerizadoraPorCargos) { %>
                <tr>
                    <td><label>Tercerizadora:</label></td>
                    <td><%= HtmlUtil.escape(idTercerizadora) %></td>

                    <td><label>Recupero:</label></td>
                    <td>SI</td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>
            <% } %>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Observaciones:</label></td>
                <td colspan="3"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></td>
            </tr>
        </table>
    </fieldset>

    <table class="lfr-table">
        <tr>
            <td>
                <c:if test="<%= puedeABM && editablePorEstado %>">
                    <input type="button"
                           value="Editar"
                           onClick="window.location.href='<%= editarURL.toString() %>';" />

                    &nbsp;&nbsp;
                </c:if>

                <input type="button"
                       value="Volver"
                       onClick="window.location.href='<%= volverURL.toString() %>';" />
            </td>
        </tr>
    </table>

    <liferay-util:include page="/html/portlet/compras/requerimiento_detalle_embebido.jsp">
        <liferay-util:param name="solo_lectura" value="true" />
    </liferay-util:include>
</c:if>

<c:if test="<%= puedeEditarPantalla %>">

    <form action="<%= actionURL.toString() %>"
          method="post"
          name="<portlet:namespace />fmCompras"
          id="<portlet:namespace />fmCompras">

        <input type="hidden"
               name="<portlet:namespace /><%= Constants.CMD %>"
               id="<portlet:namespace />compras_cmd"
               value="saveAll" />

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               id="<portlet:namespace />id_requerimiento_compra"
               value="<%= req.getIdRequerimientoCompra() %>" />

        <input type="hidden"
               name="<portlet:namespace />afiliado_cuil_titular"
               id="<portlet:namespace />afiliado_cuil_titular"
               value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

        <input type="hidden"
               name="<portlet:namespace />afiliado_int"
               id="<portlet:namespace />afiliado_int"
               value="<%= HtmlUtil.escape(afiliadoInt) %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table">
                <tr>
                    <td><label>ID:</label></td>
                    <td><%= HtmlUtil.escape(req.getIdString()) %></td>

                    <td><label>Estado:</label></td>
                    <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Sector:</label></td>
                    <td colspan="3">
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
                    <td><label>Cargo OSPIM %:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />cargo_ospim"
                               id="<portlet:namespace />cargo_ospim"
                               value="<%= HtmlUtil.escape(req.getCargoOspimString()) %>"
                               size="5"
                               maxlength="3" />
                    </td>

                    <td><label>Cargo tercerizadora %:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />cargo_tercerizadora"
                               id="<portlet:namespace />cargo_tercerizadora"
                               value="<%= HtmlUtil.escape(req.getCargoTercerizadoraString()) %>"
                               size="5"
                               maxlength="3" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr id="<portlet:namespace />tercerizadora_row"
                    style="<%= mostrarTercerizadoraPorCargos ? "" : "display:none;" %>">
                    <td><label>Tercerizadora:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />id_tercerizadora"
                               id="<portlet:namespace />requerimiento_id_tercerizadora"
                               value="<%= HtmlUtil.escape(idTercerizadora) %>"
                               size="10"
                               maxlength="10"
                               readonly="readonly" />
                    </td>

                    <td><label>Recupero:</label></td>
                    <td>
                        <input type="checkbox"
                               name="<portlet:namespace />recupero"
                               id="<portlet:namespace />recupero"
                               value="true"
                               <%= recuperoChecked %> />
                    </td>
                </tr>
            </table>
        </fieldset>

        <div id="<portlet:namespace />afiliado_requerimiento_panel" style="display:none;">
            <fieldset class="block-labels">
                <legend>
                    <liferay-ui:message key="datos-afiliado" />
                </legend>

                <div id="<portlet:namespace />afiliadoInicialMensaje"
                     class="portlet-msg-info"
                     style="display:none;"></div>

                <div id="<portlet:namespace />afiliadoInicialAutoSelect"
                     style="display:none;"></div>

                <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
                    <liferay-util:param value="<%= String.valueOf(true) %>"
                                        name="edit_mode" />
                    <liferay-util:param value="<%= String.valueOf(true) %>"
                                        name="discapacidad" />
                    <liferay-util:param name="pag_reintegro"
                                        value="1" />
                </liferay-util:include>
            </fieldset>
        </div>

        <fieldset class="block-labels">
            <legend>Observaciones</legend>

            <table class="lfr-table">
                <tr>
                    <td>
                        <textarea name="<portlet:namespace />observaciones"
                                  id="<portlet:namespace />observaciones"
                                  cols="100"
                                  rows="4"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <liferay-util:include page="/html/portlet/compras/requerimiento_detalle_embebido.jsp">
            <liferay-util:param name="solo_lectura" value="false" />
        </liferay-util:include>

        <table class="lfr-table">
            <tr>
                <td>
                    <input type="button"
                           value="Guardar todo"
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
</c:if>

<c:if test="<%= puedeEditarPantalla %>">
<script type="text/javascript">
    var popupAfill = null;
    var popup = null;
    var afiliadoInicialBuscado = false;

    function <portlet:namespace />valorAfiliado(id) {
        return jQuery.trim(jQuery('#<portlet:namespace />' + id).val());
    }

    function <portlet:namespace />valorCredencialAfiliado() {
        return jQuery('#<portlet:namespace />' + 'num' + 'ero_afi').val();
    }

    function <portlet:namespace />paramCredencialAfiliado() {
        return 'num' + 'ero_afi';
    }

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
        var credencial = <portlet:namespace />valorCredencialAfiliado();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, credencial)) {
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

        var fechaReferencia = 'null';

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
            '&' + <portlet:namespace />paramCredencialAfiliado() + '=' + encodeURIComponent(credencial) +
            '&fecha_referencia=' + encodeURIComponent(fechaReferencia) +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);
    }

    function <portlet:namespace />buscarAfiliados_(fechaReferencia) {
        <portlet:namespace />buscarAfiliados();
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
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery("#divSeccional").load(url);
            jQuery("#divSeccional").show();
        } else {
            jQuery("#divSeccional").hide("slow");
        }
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

        var entidadSeleccionada = jQuery('#<portlet:namespace />entidad').val();
        var credencialId = '#<portlet:namespace />' + 'num' + 'ero_afi';

        if (entidadSeleccionada == 'OSPIM') {
            jQuery(credencialId).val(ospim != null && ospim != 'null' ? ospim : '');
        } else if (entidadSeleccionada == 'UOMA') {
            jQuery(credencialId).val(uoma != null && uoma != 'null' ? uoma : '');
        } else if (entidadSeleccionada == 'AMTIMA') {
            jQuery(credencialId).val(amtima != null && amtima != 'null' ? amtima : '');
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
        jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora != null && id_tercerizadora != 'null' ? id_tercerizadora : '');
        jQuery('#<portlet:namespace />incapacidad_af').val(incapacidad_af != null && incapacidad_af != 'null' ? incapacidad_af : '');
        jQuery('#<portlet:namespace />nroSocioPrevencion').val(nroSocioPrev != null && nroSocioPrev != 'null' ? nroSocioPrev : '');
        jQuery('#<portlet:namespace />nroCredencialPrevencion').val(nroCredenPrev != null && nroCredenPrev != 'null' ? nroCredenPrev : '');
        jQuery('#<portlet:namespace />tieneAntecedentes').val(tieneAntecedentes == '1' ? '1' : '0');

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();

        if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
            <portlet:namespace />mostrarMensajeAfiliadoInicial('');
        }
    }

    function <portlet:namespace />trimValue(id) {
        return jQuery.trim(jQuery('#<portlet:namespace />' + id).val());
    }

    function <portlet:namespace />usaTercerizadoraPorCargos(cargoOspim, cargoTercerizadora) {
        return !(cargoOspim == 100 && cargoTercerizadora == 0);
    }

    function <portlet:namespace />actualizarVisibilidadTercerizadora() {
        var cargoOspim = <portlet:namespace />parsePorcentaje('cargo_ospim', 'Cargo OSPIM');
        var cargoTercerizadora = <portlet:namespace />parsePorcentaje('cargo_tercerizadora', 'Cargo tercerizadora');

        if (cargoOspim == null || cargoTercerizadora == null) {
            return false;
        }

        var usaTercerizadora =
                <portlet:namespace />usaTercerizadoraPorCargos(
                        cargoOspim,
                        cargoTercerizadora
                );

        if (usaTercerizadora) {
            jQuery('#<portlet:namespace />tercerizadora_row').show();
            jQuery('#<portlet:namespace />recupero').attr('checked', 'checked');
        } else {
            jQuery('#<portlet:namespace />tercerizadora_row').hide();
            jQuery('#<portlet:namespace />recupero').removeAttr('checked');

            jQuery('#<portlet:namespace />id_tercerizadora').val('');
            jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
        }

        return true;
    }

    function <portlet:namespace />sectorRequiereAfiliado() {
        return jQuery('#<portlet:namespace />sector_id option:selected').attr('data-requiere-afiliado') == 'true';
    }

    function <portlet:namespace />sincronizarAfiliadoRequerimiento() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(<portlet:namespace />trimValue('cuil'));
        jQuery('#<portlet:namespace />afiliado_int').val(<portlet:namespace />trimValue('inte'));

        var cargoOspim = <portlet:namespace />parsePorcentaje('cargo_ospim', 'Cargo OSPIM');
        var cargoTercerizadora = <portlet:namespace />parsePorcentaje('cargo_tercerizadora', 'Cargo tercerizadora');

        var usaTercerizadora = true;

        if (cargoOspim != null && cargoTercerizadora != null) {
            usaTercerizadora =
                    <portlet:namespace />usaTercerizadoraPorCargos(
                            cargoOspim,
                            cargoTercerizadora
                    );
        }

        var idTerc = '';

        if (usaTercerizadora) {
            idTerc = jQuery('#<portlet:namespace />id_tercerizadora').val();

            if (idTerc == null) {
                idTerc = '';
            }
        } else {
            jQuery('#<portlet:namespace />id_tercerizadora').val('');
        }

        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(jQuery.trim(idTerc));
    }

    function <portlet:namespace />cargarAfiliadoInicial() {
        var afiliadoCuilTitular = jQuery('#<portlet:namespace />afiliado_cuil_titular').val();
        var afiliadoInt = jQuery('#<portlet:namespace />afiliado_int').val();
        var idTerc = jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val();

        if (afiliadoCuilTitular != '') {
            jQuery('#<portlet:namespace />cuil').val(afiliadoCuilTitular);
        }

        if (afiliadoInt != '') {
            jQuery('#<portlet:namespace />inte').val(afiliadoInt);
        }

        if (idTerc != '') {
            jQuery('#<portlet:namespace />id_tercerizadora').val(idTerc);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />mostrarMensajeAfiliadoInicial(mensaje) {
        var panel = jQuery('#<portlet:namespace />afiliadoInicialMensaje');

        if (mensaje == null || jQuery.trim(mensaje) == '') {
            panel.hide();
            panel.text('');
            return;
        }

        panel.text(mensaje);
        panel.show();
    }

    function <portlet:namespace />cargarDatosAfiliadoInicial() {
        if (afiliadoInicialBuscado) {
            return;
        }

        afiliadoInicialBuscado = true;

        var puedeBuscarAfiliadoInicial = <%= (!esNuevo && puedeEditarPantalla) ? "true" : "false" %>;

        if (!puedeBuscarAfiliadoInicial || !<portlet:namespace />sectorRequiereAfiliado()) {
            return;
        }

        var afiliadoCuilTitular = jQuery.trim(jQuery('#<portlet:namespace />afiliado_cuil_titular').val());
        var afiliadoInt = jQuery.trim(jQuery('#<portlet:namespace />afiliado_int').val());

        if (afiliadoCuilTitular == '' || afiliadoInt == '') {
            return;
        }

        var entidad = jQuery('#<portlet:namespace />entidad').val();

        if (entidad == null) {
            entidad = '';
        }

        <portlet:namespace />mostrarMensajeAfiliadoInicial('');

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_afiliados' +
            '&auto_select=true' +
            '&funcion_seleccion=seleccionaCamposAfiliado' +
            '&cuil=' + encodeURIComponent(afiliadoCuilTitular) +
            '&inte=' + encodeURIComponent(afiliadoInt) +
            '&entidad=' + encodeURIComponent(entidad) +
            '&fecha_referencia=null';

        jQuery('#<portlet:namespace />afiliadoInicialAutoSelect').load(url);
    }

    function <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_int').val('');
        jQuery('#<portlet:namespace />id_tercerizadora').val('');
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
        <portlet:namespace />mostrarMensajeAfiliadoInicial('');
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

    function <portlet:namespace />parsePorcentaje(id, label) {
        var value = <portlet:namespace />trimValue(id);

        if (value == '') {
            value = '0';
            jQuery('#<portlet:namespace />' + id).val('0');
        }

        if (!/^[0-9]+$/.test(value)) {
            alert(label + ': debe ser un numero entero entre 0 y 100. Valor recibido: "' + value + '".');
            jQuery('#<portlet:namespace />' + id).focus();
            return null;
        }

        var parsed = parseInt(value, 10);

        if (parsed < 0 || parsed > 100) {
            alert(label + ': debe estar entre 0 y 100. Valor recibido: ' + parsed + '.');
            jQuery('#<portlet:namespace />' + id).focus();
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />guardar() {
        var form = document.getElementById('<portlet:namespace />fmCompras');

        if (!form) {
            alert('No se pudo encontrar el formulario principal de Compras. No se puede guardar el requerimiento.');
            return;
        }

        var cmdInput = document.getElementById('<portlet:namespace />compras_cmd');

        if (cmdInput) {
            cmdInput.value = 'saveAll';
        }

        var sectorId = <portlet:namespace />trimValue('sector_id');

        if (sectorId == '' || sectorId == '0') {
            alert('Sector: debe seleccionar un sector.');
            jQuery('#<portlet:namespace />sector_id').focus();
            return;
        }

        var cargoOspim = <portlet:namespace />parsePorcentaje('cargo_ospim', 'Cargo OSPIM');

        if (cargoOspim == null) {
            return;
        }

        var cargoTercerizadora = <portlet:namespace />parsePorcentaje('cargo_tercerizadora', 'Cargo tercerizadora');

        if (cargoTercerizadora == null) {
            return;
        }

        if (cargoOspim + cargoTercerizadora > 100) {
            alert(
                'Cargos: la suma de Cargo OSPIM (' + cargoOspim +
                ') y Cargo tercerizadora (' + cargoTercerizadora +
                ') es ' + (cargoOspim + cargoTercerizadora) +
                '. No puede superar 100.'
            );
            jQuery('#<portlet:namespace />cargo_tercerizadora').focus();
            return;
        }

        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (requiereAfiliado) {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            var afiliadoCuilTitular = <portlet:namespace />trimValue('afiliado_cuil_titular');
            var afiliadoInt = <portlet:namespace />trimValue('afiliado_int');

            if (afiliadoCuilTitular == '') {
                alert('Afiliado: debe seleccionar un afiliado. Falta CUIL titular.');
                jQuery('#<portlet:namespace />cuil').focus();
                return;
            }

            if (afiliadoInt == '') {
                alert('Afiliado: debe seleccionar un afiliado. Falta integrante.');
                jQuery('#<portlet:namespace />inte').focus();
                return;
            }
        } else {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        }

        var usaTercerizadora =
                <portlet:namespace />usaTercerizadoraPorCargos(
                        cargoOspim,
                        cargoTercerizadora
                );

        if (usaTercerizadora) {
            jQuery('#<portlet:namespace />recupero').attr('checked', 'checked');

            if (<portlet:namespace />trimValue('requerimiento_id_tercerizadora') == '') {
                alert('Tercerizadora: debe seleccionar un afiliado con tercerizadora porque la distribución de cargos no es OSPIM 100% / Tercerizadora 0%.');
                jQuery('#<portlet:namespace />cuil').focus();
                return;
            }
        } else {
            jQuery('#<portlet:namespace />recupero').removeAttr('checked');
            jQuery('#<portlet:namespace />id_tercerizadora').val('');
            jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
        }

        var serializadorDetalles = null;

        if (typeof <portlet:namespace />serializarDetallesCompras == 'function') {
            serializadorDetalles = <portlet:namespace />serializarDetallesCompras;
        } else if (typeof window['<portlet:namespace />serializarDetallesCompras'] == 'function') {
            serializadorDetalles = window['<portlet:namespace />serializarDetallesCompras'];
        }

        if (serializadorDetalles == null) {
            alert(
                'Detalles: no se encontro la funcion <portlet:namespace />serializarDetallesCompras(). ' +
                'El JSP embebido no se esta renderizando correctamente o Liferay esta usando una version vieja compilada.'
            );
            return;
        }

        if (!serializadorDetalles()) {
            return;
        }

        if (typeof submitForm == 'function') {
            submitForm(form);
        } else {
            form.submit();
        }
    }

    jQuery(function() {
        <portlet:namespace />cargarAfiliadoInicial();
        <portlet:namespace />actualizarVisibilidadAfiliado(false);
        <portlet:namespace />actualizarVisibilidadTercerizadora();

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').change(function() {
            <portlet:namespace />actualizarVisibilidadTercerizadora();
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').keyup(function() {
            <portlet:namespace />actualizarVisibilidadTercerizadora();
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        jQuery('#<portlet:namespace />sector_id').change(function() {
            <portlet:namespace />actualizarVisibilidadAfiliado(true);

            if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
                window['<portlet:namespace />filtrarArticulosPorSector']();
            }
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').change(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').keyup(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });
    });
</script>
</c:if>

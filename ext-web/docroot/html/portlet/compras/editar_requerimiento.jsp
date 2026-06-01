<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

<portlet:defineObjects/>

<%!
private String jsCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
String namespaceCompra = renderResponse.getNamespace();

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

boolean soloLecturaSolicitada =
        Boolean.TRUE.equals(soloLecturaAttr)
        || "/compras/ver_requerimiento".equals(strutsActionActual)
        || "ver".equalsIgnoreCase(modo);

boolean editablePorEstado = esNuevo || req.isEditable();

boolean layoutEdicion =
        puedeABM
        && editablePorEstado;

boolean modoEditable =
        layoutEdicion
        && !soloLecturaSolicitada;

boolean modoVista = !modoEditable;

renderRequest.setAttribute(
        WebKeysCompras.SOLO_LECTURA_ATTR,
        Boolean.valueOf(modoVista)
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

Object articulosAttr = renderRequest.getAttribute("ARTICULOS_COMPRA");

List<CompraArticulo> articulosCompra = null;

if (articulosAttr instanceof List) {
    articulosCompra = (List<CompraArticulo>) articulosAttr;
}

if (articulosCompra == null || articulosCompra.size() == 0) {
    articulosCompra = new ArrayList<CompraArticulo>();

    String[] metodosArticulos = new String[] {
            "listarArticulos",
            "listarArticulosCompra",
            "listarCompraArticulos",
            "listarArticulosRequerimientoCompra",
            "listarRequerimientoCompraArticulos",
            "getArticulosCompra"
    };

    for (int i = 0; i < metodosArticulos.length && articulosCompra.size() == 0; i++) {
        try {
            Method metodo =
                    BusquedaRequerimientoCompraServiceUtil.class.getMethod(
                            metodosArticulos[i],
                            new Class[0]
                    );

            Object resultado = metodo.invoke(null, new Object[0]);

            if (resultado instanceof List) {
                articulosCompra = (List<CompraArticulo>) resultado;
            }
        } catch (NoSuchMethodException nsme) {
            // Intencional: compatibilidad con distintos nombres de service.
        } catch (Exception e) {
            // Intencional: si un nombre existe pero falla, se intenta el siguiente.
        }
    }
}

renderRequest.setAttribute("ARTICULOS_COMPRA", articulosCompra);

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

boolean sectorRequiereAfiliadoActual = req.isRequiereAfiliado();

if (sectorDescripcionSoloLectura.length() == 0 || !sectorRequiereAfiliadoActual) {
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());

        if (reqSectorId.equals(sectorId)) {
            if (sectorDescripcionSoloLectura.length() == 0) {
                sectorDescripcionSoloLectura = sector.getDescripcionVisible();
            }

            sectorRequiereAfiliadoActual = sector.isRequiereAfiliado();
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

boolean tieneAfiliadoVisible =
        !WebKeysCompras.isEmpty(afiliadoCuilVisible)
        || !WebKeysCompras.isEmpty(afiliadoIntVisible)
        || !WebKeysCompras.isEmpty(afiliadoTipoDocumento)
        || !WebKeysCompras.isEmpty(afiliadoNumeroDocumento)
        || !WebKeysCompras.isEmpty(afiliadoApellido)
        || !WebKeysCompras.isEmpty(afiliadoNombre)
        || !WebKeysCompras.isEmpty(afiliadoSeccional)
        || !WebKeysCompras.isEmpty(afiliadoBajaFecha)
        || !WebKeysCompras.isEmpty(afiliadoFechaAlta)
        || !WebKeysCompras.isEmpty(afiliadoIdTercerizadora)
        || !WebKeysCompras.isEmpty(afiliadoIncapacidad)
        || !WebKeysCompras.isEmpty(afiliadoAntecedentes);

boolean mostrarPanelAfiliadoEnVista = sectorRequiereAfiliadoActual || tieneAfiliadoVisible;

String recuperoChecked = mostrarTercerizadoraPorCargos ? "checked=\"checked\"" : "";
String camposVistaReadOnly = modoVista ? "readonly=\"readonly\"" : "";

String bloqueoSinEstiloVista = modoVista
        ? " class=\"compras-bloqueado-sin-estilo\" tabindex=\"-1\" onmousedown=\"return false;\" onkeydown=\"return false;\" onclick=\"return false;\""
        : "";

String bloqueoCheckboxVista = modoVista
        ? " tabindex=\"-1\" onclick=\"return false;\" onkeydown=\"return false;\""
        : "";

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

String tituloPantalla = "";

if (modoVista) {
    tituloPantalla = "Ver requerimiento de compra";
} else if (esNuevo) {
    tituloPantalla = "Nuevo requerimiento de compra";
} else {
    tituloPantalla = "Editar requerimiento de compra";
}
%>

<style type="text/css">
    .compras-bloqueado-sin-estilo {
        pointer-events: none;
    }
</style>

<c:if test="<%= !WebKeysCompras.isEmpty(errorParaAlert) %>">
    <div class="portlet-msg-error">
        <strong>No se pudo guardar el requerimiento.</strong>
        <br />
        <%= HtmlUtil.escape(errorParaAlert) %>
    </div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && puedeABM && !editablePorEstado %>">
    <div class="portlet-msg-info">El requerimiento solo puede editarse en estado Borrador.</div>
</c:if>

<form action="<%= actionURL.toString() %>"
      method="post"
      name="<portlet:namespace />fmCompras"
      id="<portlet:namespace />fmCompras"
      class="<%= modoVista ? "compras-modo-vista" : "" %>">

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
        <legend><%= tituloPantalla %></legend>

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
                            id="<portlet:namespace />sector_id"
                            onChange="<portlet:namespace />cambiarSectorCompra(true);"
                            <%= bloqueoSinEstiloVista %>>
                        <option value="0" data-requiere-afiliado="false">Seleccione</option>

                        <%
                        for (int i = 0; i < sectores.size(); i++) {
                            RequerimientoCompraSector sector = sectores.get(i);
                            String sectorId = String.valueOf(sector.getIdSector());
                            String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
                        %>
                            <option value="<%= sectorId %>"
                                    data-requiere-afiliado="<%= requiereAfiliado %>"
                                    <%= selected %>>
                                <%= HtmlUtil.escape(sector.getDescripcionVisible()) %>
                            </option>
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
                           maxlength="3"
                           <%= camposVistaReadOnly %> />
                </td>

                <td><label>Cargo tercerizadora %:</label></td>
                <td>
                    <input type="text"
                           name="<portlet:namespace />cargo_tercerizadora"
                           id="<portlet:namespace />cargo_tercerizadora"
                           value="<%= HtmlUtil.escape(req.getCargoTercerizadoraString()) %>"
                           size="5"
                           maxlength="3"
                           <%= camposVistaReadOnly %> />
                </td>
            </tr>

            <tr>
                <td colspan="4">&nbsp;</td>
            </tr>

            <tr id="<portlet:namespace />tercerizadora_row"
                style="<%= mostrarTercerizadoraPorCargos ? "" : "display:none;" %>">
                <td><label>ID tercerizadora:</label></td>
                <td>
                    <input type="text"
                           name="<portlet:namespace />requerimiento_id_tercerizadora_visible"
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
                           <%= recuperoChecked %>
                           <%= bloqueoCheckboxVista %> />
                </td>
            </tr>
        </table>
    </fieldset>

    <c:if test="<%= layoutEdicion || (modoVista && mostrarPanelAfiliadoEnVista) %>">
        <div id="<portlet:namespace />afiliado_requerimiento_panel"
             style="<%= modoEditable && !mostrarPanelAfiliadoEnVista ? "display:none;" : "" %>">
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
                    <liferay-util:param name="origen"
                                        value="" />
                </liferay-util:include>
            </fieldset>
        </div>
    </c:if>

    <c:if test="<%= modoVista && mostrarPanelAfiliadoEnVista %>">
        <script type="text/javascript">
            jQuery(function() {
                var ns = '<portlet:namespace />';
                var panel = jQuery('#' + ns + 'afiliado_requerimiento_panel');

                function setAfiliadoValue(id, value) {
                    var input = jQuery('#' + ns + id);

                    if (input.length > 0) {
                        input.val(value == null ? '' : value);
                    }
                }

                setAfiliadoValue('cuil', '<%= jsCompra(afiliadoCuilVisible) %>');
                setAfiliadoValue('inte', '<%= jsCompra(afiliadoIntVisible) %>');
                setAfiliadoValue('tipoDoc', '<%= jsCompra(afiliadoTipoDocumento) %>');
                setAfiliadoValue('nroDoc', '<%= jsCompra(afiliadoNumeroDocumento) %>');
                setAfiliadoValue('apellido', '<%= jsCompra(afiliadoApellido) %>');
                setAfiliadoValue('nombre', '<%= jsCompra(afiliadoNombre) %>');
                setAfiliadoValue('seccional', '<%= jsCompra(afiliadoSeccional) %>');
                setAfiliadoValue('baja_fecha', '<%= jsCompra(afiliadoBajaFecha) %>');
                setAfiliadoValue('fecha_alta_af', '<%= jsCompra(afiliadoFechaAlta) %>');
                setAfiliadoValue('id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
                setAfiliadoValue('requerimiento_id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
                setAfiliadoValue('incapacidad_af', '<%= jsCompra(afiliadoIncapacidad) %>');

                if ('<%= jsCompra(afiliadoSeccional) %>' != '') {
                    setAfiliadoValue('secc_seleccionada', '1');
                }

                if ('<%= jsCompra(afiliadoAntecedentes) %>' == 'SI') {
                    setAfiliadoValue('tieneAntecedentes', '1');
                } else {
                    setAfiliadoValue('tieneAntecedentes', '0');
                }

                var bajaInput = jQuery('#' + ns + 'baja_fecha');

                if (bajaInput.length > 0) {
                    if (jQuery.trim(bajaInput.val()) != '') {
                        bajaInput.css('background', 'red');
                        bajaInput.css('color', 'white');
                    } else {
                        bajaInput.css('background', 'white');
                        bajaInput.css('color', 'black');
                    }
                }

                if (typeof window[ns + 'aplicarAntecedentesAfiliado'] == 'function') {
                    window[ns + 'aplicarAntecedentesAfiliado'](
                            '<%= jsCompra(afiliadoAntecedentes) %>' == 'SI' ? '1' : '0'
                    );
                }

                panel.find('input[type="text"], textarea').attr('readonly', 'readonly');

                panel.find('select')
                        .addClass('compras-bloqueado-sin-estilo')
                        .attr('tabindex', '-1')
                        .bind('mousedown keydown click change', function() {
                            return false;
                        });

                panel.find('input[type="checkbox"], input[type="radio"]')
                        .attr('tabindex', '-1')
                        .bind('click keydown change', function() {
                            return false;
                        });

                panel.find('input[type="button"], button, img[onclick], a[onclick]')
                        .removeAttr('onclick')
                        .bind('click', function() {
                            return false;
                        });
            });
        </script>
    </c:if>

    <fieldset class="block-labels">
        <legend>Observaciones</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <textarea name="<portlet:namespace />observaciones"
                              id="<portlet:namespace />observaciones"
                              cols="100"
                              rows="4"
                              <%= camposVistaReadOnly %>><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                </td>
            </tr>
        </table>
    </fieldset>

    <liferay-util:include page="/html/portlet/compras/requerimiento_detalle_embebido.jsp">
        <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(modoVista) %>" />
    </liferay-util:include>

    <table class="lfr-table">
        <tr>
            <td>
                <c:if test="<%= modoEditable %>">
                    <input type="button"
                           value="Guardar"
                           onClick="<%= namespaceCompra %>guardar();" />

                    &nbsp;&nbsp;
                </c:if>

                <input type="button"
                       id="<portlet:namespace />btnVolverCompras"
                       class="compras-btn-volver"
                       value="Volver"
                       onClick="window.location.href='<%= volverURL.toString() %>';" />
            </td>
        </tr>
    </table>
    <c:if test="<%= modoVista %>">
        <script type="text/javascript">
            jQuery(function() {
                var ns = '<portlet:namespace />';
                var form = jQuery('#' + ns + 'fmCompras');

                function ocultarBotonesModoVista() {
                    form.find('input[type="button"], input[type="submit"], button')
                            .not('#' + ns + 'btnVolverCompras')
                            .hide();

                    form.find('a[onclick], img[onclick]')
                            .hide();

                    jQuery('#' + ns + 'btnVolverCompras').show();
                }

                ocultarBotonesModoVista();

                setTimeout(ocultarBotonesModoVista, 300);
                setTimeout(ocultarBotonesModoVista, 1000);
            });
        </script>
    </c:if>
</form>

<c:if test="<%= modoEditable %>">
<script type="text/javascript">
    var popup = null;
    var popupAfill = null;

    var <portlet:namespace />sectorRequiereAfiliadoMap = {};

    <%
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());
        String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
    %>
        <portlet:namespace />sectorRequiereAfiliadoMap['<%= sectorId %>'] = <%= requiereAfiliado %>;
    <%
    }
    %>

    function <portlet:namespace />valorSeguroAfiliado(value) {
        if (value == null || typeof value == 'undefined' || value == 'null') {
            return '';
        }

        return value;
    }

    function <portlet:namespace />fechaReferenciaAfiliado() {
        var d = new Date();
        var currDate = d.getDate();
        var currMonth = d.getMonth() + 1;
        var currYear = d.getFullYear();

        return currDate + "/" + currMonth + "/" + currYear;
    }

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
        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numeroAfi = jQuery('#<portlet:namespace />numero_afi').val();
        var nroCredencialPrevencion = jQuery('#<portlet:namespace />nroCredencialPrevencion').val();
        var nroSocioPrevencion = jQuery('#<portlet:namespace />nroSocioPrevencion').val();
        var fechaReferencia = <portlet:namespace />fechaReferenciaAfiliado();

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
            '&fecha_referencia=' + encodeURIComponent(fechaReferencia) +
            '&nroCredencialPrevencion=' + encodeURIComponent(nroCredencialPrevencion) +
            '&nroSocioPrevencion=' + encodeURIComponent(nroSocioPrevencion) +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
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

        return false;
    }

    function seleccionaAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
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
            reclamoPrestacional,
            nroSocioPrev,
            nroCredenPrev,
            fechaRecepcion,
            tieneAntecedentes
        );

        if (popupAfill != null) {
            Liferay.Popup.close(popupAfill);
        }
    }

    function seleccionaCamposAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        nombre_plan = <portlet:namespace />valorSeguroAfiliado(nombre_plan);
        id_plan = <portlet:namespace />valorSeguroAfiliado(id_plan);
        id_tercerizadora = <portlet:namespace />valorSeguroAfiliado(id_tercerizadora);
        afi_tercerizadora = <portlet:namespace />valorSeguroAfiliado(afi_tercerizadora);
        fecha_alta_af = <portlet:namespace />valorSeguroAfiliado(fecha_alta_af);
        incapacidad_af = <portlet:namespace />valorSeguroAfiliado(incapacidad_af);
        nroSocioPrev = <portlet:namespace />valorSeguroAfiliado(nroSocioPrev);
        nroCredenPrev = <portlet:namespace />valorSeguroAfiliado(nroCredenPrev);
        bajaFecha = <portlet:namespace />valorSeguroAfiliado(bajaFecha);

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

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(ospim));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(uoma));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(amtima));
        }

        jQuery('#<portlet:namespace />baja_fecha').val(bajaFecha);

        if (bajaFecha != '') {
            document.getElementById("<portlet:namespace />baja_fecha").style.background = "red";
            document.getElementById("<portlet:namespace />baja_fecha").style.color = "white";
        } else {
            document.getElementById("<portlet:namespace />baja_fecha").style.background = "white";
            document.getElementById("<portlet:namespace />baja_fecha").style.color = "black";
        }

        jQuery('#<portlet:namespace />nombre_plan').val(nombre_plan);
        jQuery('#<portlet:namespace />afi_tercerizadora').val(afi_tercerizadora);
        jQuery('#<portlet:namespace />fecha_alta_af').val(fecha_alta_af);
        jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />incapacidad_af').val(incapacidad_af);
        jQuery('#<portlet:namespace />nroSocioPrevencion').val(nroSocioPrev);
        jQuery('#<portlet:namespace />nroCredencialPrevencion').val(nroCredenPrev);
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

    function <portlet:namespace />parsePorcentajeSilencioso(id) {
        var value = <portlet:namespace />trimValue(id);

        if (value == '') {
            return 0;
        }

        if (!/^[0-9]+$/.test(value)) {
            return null;
        }

        var parsed = parseInt(value, 10);

        if (isNaN(parsed) || parsed < 0 || parsed > 100) {
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />usaTercerizadoraPorCargos(cargoOspim, cargoTercerizadora) {
        return !(cargoOspim == 100 && cargoTercerizadora == 0);
    }

    function <portlet:namespace />actualizarVisibilidadTercerizadora() {
        var cargoOspim = <portlet:namespace />parsePorcentajeSilencioso('cargo_ospim');
        var cargoTercerizadora = <portlet:namespace />parsePorcentajeSilencioso('cargo_tercerizadora');

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
        var sectorId = jQuery.trim(jQuery('#<portlet:namespace />sector_id').val());

        if (sectorId != '' && sectorId != '0') {
            if (typeof <portlet:namespace />sectorRequiereAfiliadoMap[sectorId] != 'undefined') {
                return <portlet:namespace />sectorRequiereAfiliadoMap[sectorId] === true;
            }
        }

        var selected = jQuery('#<portlet:namespace />sector_id option:selected');
        var attr = selected.attr('data-requiere-afiliado');

        return attr == 'true' || attr == '1' || attr == 'SI' || attr == 'S';
    }

    function <portlet:namespace />sincronizarAfiliadoRequerimiento() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(<portlet:namespace />trimValue('cuil'));
        jQuery('#<portlet:namespace />afiliado_int').val(<portlet:namespace />trimValue('inte'));

        var cargoOspim = <portlet:namespace />parsePorcentajeSilencioso('cargo_ospim');
        var cargoTercerizadora = <portlet:namespace />parsePorcentajeSilencioso('cargo_tercerizadora');

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

    function <portlet:namespace />setAfiliadoValue(id, value) {
        var input = jQuery('#<portlet:namespace />' + id);

        if (input.length > 0) {
            input.val(value == null ? '' : value);
        }
    }

    function <portlet:namespace />aplicarColorBajaAfiliadoExistente() {
        var bajaInput = jQuery('#<portlet:namespace />baja_fecha');

        if (bajaInput.length > 0) {
            if (jQuery.trim(bajaInput.val()) != '') {
                bajaInput.css('background', 'red');
                bajaInput.css('color', 'white');
            } else {
                bajaInput.css('background', 'white');
                bajaInput.css('color', 'black');
            }
        }
    }

    function <portlet:namespace />cargarAfiliadoExistenteEnEdicion() {
        if (<%= esNuevo ? "true" : "false" %>) {
            return;
        }

        <portlet:namespace />setAfiliadoValue('cuil', '<%= jsCompra(afiliadoCuilVisible) %>');
        <portlet:namespace />setAfiliadoValue('inte', '<%= jsCompra(afiliadoIntVisible) %>');
        <portlet:namespace />setAfiliadoValue('tipoDoc', '<%= jsCompra(afiliadoTipoDocumento) %>');
        <portlet:namespace />setAfiliadoValue('nroDoc', '<%= jsCompra(afiliadoNumeroDocumento) %>');
        <portlet:namespace />setAfiliadoValue('apellido', '<%= jsCompra(afiliadoApellido) %>');
        <portlet:namespace />setAfiliadoValue('nombre', '<%= jsCompra(afiliadoNombre) %>');
        <portlet:namespace />setAfiliadoValue('seccional', '<%= jsCompra(afiliadoSeccional) %>');
        <portlet:namespace />setAfiliadoValue('baja_fecha', '<%= jsCompra(afiliadoBajaFecha) %>');
        <portlet:namespace />setAfiliadoValue('fecha_alta_af', '<%= jsCompra(afiliadoFechaAlta) %>');
        <portlet:namespace />setAfiliadoValue('id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
        <portlet:namespace />setAfiliadoValue('requerimiento_id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
        <portlet:namespace />setAfiliadoValue('incapacidad_af', '<%= jsCompra(afiliadoIncapacidad) %>');

        if ('<%= jsCompra(afiliadoSeccional) %>' != '') {
            <portlet:namespace />setAfiliadoValue('secc_seleccionada', '1');
        }

        if ('<%= jsCompra(afiliadoAntecedentes) %>' == 'SI') {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '1');
        } else {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '0');
        }

        <portlet:namespace />aplicarColorBajaAfiliadoExistente();

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(
                    '<%= jsCompra(afiliadoAntecedentes) %>' == 'SI' ? '1' : '0'
            );
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
        return false;
    }

    function <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_int').val('');
        jQuery('#<portlet:namespace />id_tercerizadora').val('');
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
        jQuery('#<portlet:namespace />nombre_plan').val('');
        jQuery('#<portlet:namespace />afi_tercerizadora').val('');

        <portlet:namespace />mostrarMensajeAfiliadoInicial('');
    }

    function <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere) {
        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        var tieneAfiliadoExistente =
                !<%= esNuevo ? "true" : "false" %>
                && (
                        jQuery.trim(jQuery('#<portlet:namespace />afiliado_cuil_titular').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />afiliado_int').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />cuil').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />inte').val()) != ''
                );

        if (requiereAfiliado || tieneAfiliadoExistente) {
            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').show();
        } else {
            if (limpiarSiNoRequiere) {
                <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            }

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').hide();
        }
    }

    function <portlet:namespace />cambiarSectorCompra(limpiarSiNoRequiere) {
        <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere);

        if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
            window['<portlet:namespace />filtrarArticulosPorSector']();
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

        <c:if test="<%= !esNuevo %>">
            <portlet:namespace />cargarAfiliadoExistenteEnEdicion();
        </c:if>

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

        jQuery('#<portlet:namespace />sector_id, #<portlet:namespace />id_sector').change(function() {
            <portlet:namespace />cambiarSectorCompra(true);
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').change(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').keyup(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        });

        if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
            window['<portlet:namespace />filtrarArticulosPorSector']();
        }

        setTimeout(function() {
            <portlet:namespace />actualizarVisibilidadAfiliado(false);

            if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
                window['<portlet:namespace />filtrarArticulosPorSector']();
            }
        }, 300);
    });
</script>
</c:if>
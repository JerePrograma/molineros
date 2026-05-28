<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%!
private String jsDetalleCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ");
}

private String jsDetalleCompraAttr(String value) {
    return HtmlUtil.escape(jsDetalleCompra(value));
}
%>

<%
RequerimientoCompra reqDetalle =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqDetalle == null) {
    reqDetalle =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqDetalle == null) {
    int idReq = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

    if (idReq > 0) {
        reqDetalle = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReq);
    }
}

if (reqDetalle == null) {
    reqDetalle = new RequerimientoCompra();
}

Object soloLecturaAttrDetalle =
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String strutsActionDetalle = ParamUtil.getString(renderRequest, "struts_action", "");
String modoDetalle = ParamUtil.getString(renderRequest, "modo", "");

boolean soloLecturaDetalle =
        Boolean.TRUE.equals(soloLecturaAttrDetalle)
        || ParamUtil.getBoolean(request, "solo_lectura", false)
        || "/compras/ver_requerimiento".equals(strutsActionDetalle)
        || "ver".equalsIgnoreCase(modoDetalle);

boolean puedeABMDetalle =
        !soloLecturaDetalle
        && user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
        && reqDetalle.isEditable();

List<RequerimientoCompraDetalle> detalles = reqDetalle.getDetalles();

if (detalles == null) {
    detalles = new ArrayList<RequerimientoCompraDetalle>();
}

PortletURL detalleActionURL = renderResponse.createActionURL();
detalleActionURL.setWindowState(WindowState.MAXIMIZED);
detalleActionURL.setParameter("struts_action", "/compras/editar_requerimiento");

int detalleColspan = puedeABMDetalle ? 7 : 6;
String nsDetalle = renderResponse.getNamespace();
%>

<fieldset class="block-labels">
    <legend>Detalle del requerimiento</legend>

    <table class="lfr-table taglib-search-iterator" width="100%">
        <tr class="portlet-section-header results-header">
            <th>ID</th>
            <th>Art&iacute;culo</th>
            <th>Cantidad</th>
            <th>Precio unitario estimado</th>
            <th>Total estimado</th>
            <th>Observaciones</th>

            <% if (puedeABMDetalle) { %>
                <th>Acciones</th>
            <% } %>
        </tr>

        <%
        if (detalles.size() == 0) {
        %>
            <tr class="portlet-section-body results-row">
                <td colspan="<%= detalleColspan %>">
                    No hay detalles cargados.
                </td>
            </tr>
        <%
        }
        else {
            for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle detalle = detalles.get(i);

                String rowClass = (i % 2 == 0)
                        ? "portlet-section-body results-row"
                        : "portlet-section-alternate results-row alt";
        %>
                <tr class="<%= rowClass %>">
                    <td><%= HtmlUtil.escape(detalle.getIdString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getArticuloVisible()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getCantidadString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getPrecioUnitarioEstimadoString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getPrecioTotalEstimadoString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getObservacionesVisible()) %></td>

                    <% if (puedeABMDetalle) { %>
                        <td>
                            <input
                                type="button"
                                value="Editar"
                                onClick="<portlet:namespace />editarDetalle('<%= detalle.getIdString() %>', '<%= jsDetalleCompraAttr(detalle.getArticuloVisible()) %>', '<%= jsDetalleCompraAttr(detalle.getCantidadString()) %>', '<%= jsDetalleCompraAttr(detalle.getPrecioUnitarioEstimadoString()) %>', '<%= jsDetalleCompraAttr(detalle.getPrecioTotalEstimadoString()) %>', '<%= jsDetalleCompraAttr(detalle.getObservacionesVisible()) %>');"
                            />

                            &nbsp;

                            <form action="<%= detalleActionURL.toString() %>"
                                  method="post"
                                  id="<%= nsDetalle %>deleteDetalleFm<%= detalle.getIdString() %>"
                                  style="display:inline;">
                                <input type="hidden"
                                       name="<portlet:namespace /><%= Constants.CMD %>"
                                       value="deleteItem" />

                                <input type="hidden"
                                       name="<portlet:namespace />id_requerimiento_compra"
                                       value="<%= reqDetalle.getIdRequerimientoCompra() %>" />

                                <input type="hidden"
                                       name="<portlet:namespace />id_detalle"
                                       value="<%= detalle.getIdString() %>" />

                                <input
                                    type="button"
                                    value="Borrar"
                                    onClick="if (confirm('Confirma borrar el detalle?')) submitForm(document.getElementById('<%= nsDetalle %>deleteDetalleFm<%= detalle.getIdString() %>'));"
                                />
                            </form>
                        </td>
                    <% } %>
                </tr>
        <%
            }
        }
        %>
    </table>

    <% if (puedeABMDetalle) { %>
        <br />

        <form action="<%= detalleActionURL.toString() %>"
              method="post"
              name="<portlet:namespace />detalleFm"
              id="<portlet:namespace />detalleFm">

            <input type="hidden"
                   name="<portlet:namespace /><%= Constants.CMD %>"
                   id="<portlet:namespace />detalle_cmd"
                   value="addItem" />

            <input type="hidden"
                   name="<portlet:namespace />id_detalle"
                   id="<portlet:namespace />id_detalle"
                   value="0" />

            <input type="hidden"
                   name="<portlet:namespace />id_requerimiento_compra"
                   value="<%= reqDetalle.getIdRequerimientoCompra() %>" />

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label for="<portlet:namespace />articulo">Art&iacute;culo:</label></td>
                    <td colspan="3">
                        <input
                            type="text"
                            name="<portlet:namespace />articulo"
                            id="<portlet:namespace />articulo"
                            size="80"
                            maxlength="255"
                        />

                        &nbsp;

                        <% if (puedeABMDetalle) { %>
                            <img alt="Nuevo artículo"
                                 title="Nuevo artículo"
                                 align="absmiddle"
                                 src="<%= themeDisplay.getPathThemeImages() %>/common/add.png"
                                 style="cursor:pointer;"
                                 onClick="<portlet:namespace />abrirAltaArticuloCompra();" />
                        <% } %>
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label for="<portlet:namespace />cantidad">Cantidad:</label></td>
                    <td>
                        <input
                            type="text"
                            name="<portlet:namespace />cantidad"
                            id="<portlet:namespace />cantidad"
                            size="8"
                            value="1"
                        />
                    </td>

                    <td><label for="<portlet:namespace />precio_unitario_estimado">Precio unitario:</label></td>
                    <td>
                        <input
                            type="text"
                            name="<portlet:namespace />precio_unitario_estimado"
                            id="<portlet:namespace />precio_unitario_estimado"
                            size="12"
                            value=""
                        />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label for="<portlet:namespace />precio_total_estimado">Total estimado:</label></td>
                    <td>
                        <input
                            type="text"
                            name="<portlet:namespace />precio_total_estimado"
                            id="<portlet:namespace />precio_total_estimado"
                            size="12"
                        />
                    </td>

                    <td><label for="<portlet:namespace />observaciones_detalle">Observaciones:</label></td>
                    <td>
                        <input
                            type="text"
                            name="<portlet:namespace />observaciones_detalle"
                            id="<portlet:namespace />observaciones_detalle"
                            size="60"
                            maxlength="500"
                        />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="4" align="center">
                        <input
                            type="button"
                            id="<portlet:namespace />detalle_submit"
                            value="Agregar detalle"
                            onClick="<portlet:namespace />guardarDetalle();"
                        />

                        &nbsp;&nbsp;

                        <input
                            type="button"
                            id="<portlet:namespace />detalle_cancelar"
                            value="Cancelar edici&oacute;n"
                            style="display:none;"
                            onClick="<portlet:namespace />cancelarEdicionDetalle();"
                        />
                    </td>
                </tr>
            </table>
        </form>
    <% } %>
</fieldset>

<% if (puedeABMDetalle) { %>
<script type="text/javascript">

    var <portlet:namespace />popupArticuloCompra = null;

    function <portlet:namespace />abrirAltaArticuloCompra() {
        var articuloActual = jQuery.trim(jQuery('#<portlet:namespace />articulo').val());

        <portlet:namespace />popupArticuloCompra = Liferay.Popup({
            title: 'Alta de artículo',
            modal: true,
            width: 700
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/alta_articulo_popup' +
            '&articulo=' + encodeURIComponent(articuloActual) +
            '&callback=' + encodeURIComponent('<portlet:namespace />seleccionarArticuloCompra');

        jQuery(<portlet:namespace />popupArticuloCompra).load(url);
    }

    function <portlet:namespace />seleccionarArticuloCompra(descripcion) {
        jQuery('#<portlet:namespace />articulo').val(descripcion);
        <portlet:namespace />cerrarAltaArticuloCompra();
        jQuery('#<portlet:namespace />cantidad').focus();
    }

    function <portlet:namespace />seleccionarArticuloCompraCerrar() {
        <portlet:namespace />cerrarAltaArticuloCompra();
    }

    function <portlet:namespace />cerrarAltaArticuloCompra() {
        if (<portlet:namespace />popupArticuloCompra) {
            Liferay.Popup.close(<portlet:namespace />popupArticuloCompra);
        }
    }

    function <portlet:namespace />detalleValue(value) {
        return value == null ? '' : value;
    }

    function <portlet:namespace />normalizarImporte(value) {
        value = jQuery.trim(value);

        if (value == '') {
            return null;
        }

        if (value.indexOf(',') >= 0) {
            value = value.replace(/\./g, '').replace(',', '.');
        }

        var parsed = parseFloat(value);

        if (isNaN(parsed)) {
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />calcularTotalDetalle() {
        var cantidad = parseInt(jQuery.trim(jQuery('#<portlet:namespace />cantidad').val()), 10);
        var precioUnitario = <portlet:namespace />normalizarImporte(jQuery('#<portlet:namespace />precio_unitario_estimado').val());

        if (isNaN(cantidad) || cantidad <= 0 || precioUnitario == null) {
            return;
        }

        jQuery('#<portlet:namespace />precio_total_estimado').val((cantidad * precioUnitario).toFixed(2));
    }

    function <portlet:namespace />editarDetalle(idDetalle, articulo, cantidad, precioUnitario, precioTotal, observaciones) {
        jQuery('#<portlet:namespace />detalle_cmd').val('updateItem');
        jQuery('#<portlet:namespace />id_detalle').val(<portlet:namespace />detalleValue(idDetalle));
        jQuery('#<portlet:namespace />articulo').val(<portlet:namespace />detalleValue(articulo));
        jQuery('#<portlet:namespace />cantidad').val(<portlet:namespace />detalleValue(cantidad));
        jQuery('#<portlet:namespace />precio_unitario_estimado').val(<portlet:namespace />detalleValue(precioUnitario));
        jQuery('#<portlet:namespace />precio_total_estimado').val(<portlet:namespace />detalleValue(precioTotal));
        jQuery('#<portlet:namespace />observaciones_detalle').val(<portlet:namespace />detalleValue(observaciones));

        jQuery('#<portlet:namespace />detalle_submit').val('Guardar detalle');
        jQuery('#<portlet:namespace />detalle_cancelar').show();
        jQuery('#<portlet:namespace />articulo').focus();
    }

    function <portlet:namespace />cancelarEdicionDetalle() {
        jQuery('#<portlet:namespace />detalle_cmd').val('addItem');
        jQuery('#<portlet:namespace />id_detalle').val('0');
        jQuery('#<portlet:namespace />articulo').val('');
        jQuery('#<portlet:namespace />cantidad').val('1');
        jQuery('#<portlet:namespace />precio_unitario_estimado').val('');
        jQuery('#<portlet:namespace />precio_total_estimado').val('');
        jQuery('#<portlet:namespace />observaciones_detalle').val('');

        jQuery('#<portlet:namespace />detalle_submit').val('Agregar detalle');
        jQuery('#<portlet:namespace />detalle_cancelar').hide();
    }

    function <portlet:namespace />guardarDetalle() {
        var articulo = jQuery.trim(jQuery('#<portlet:namespace />articulo').val());
        var cantidad = jQuery.trim(jQuery('#<portlet:namespace />cantidad').val());

        if (articulo == '') {
            alert('Debe informar articulo.');
            jQuery('#<portlet:namespace />articulo').focus();
            return;
        }

        if (cantidad == '' || !/^[0-9]+$/.test(cantidad) || parseInt(cantidad, 10) <= 0) {
            alert('La cantidad debe ser entera y mayor a cero.');
            jQuery('#<portlet:namespace />cantidad').focus();
            return;
        }

        submitForm(document.getElementById('<portlet:namespace />detalleFm'));
    }

    jQuery('#<portlet:namespace />cantidad, #<portlet:namespace />precio_unitario_estimado').change(function() {
        <portlet:namespace />calcularTotalDetalle();
    });
</script>
<% } %>

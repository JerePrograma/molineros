<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

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
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
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

Integer idSectorActual = reqDetalle.getSectorId();

int sectorIdParametro = ParamUtil.getInteger(request, "sector_id", 0);

if ((idSectorActual == null || idSectorActual.intValue() <= 0) && sectorIdParametro > 0) {
    idSectorActual = Integer.valueOf(sectorIdParametro);
}

String idSectorActualString =
        idSectorActual != null && idSectorActual.intValue() > 0
                ? String.valueOf(idSectorActual.intValue())
                : "";

Object articulosAttr = renderRequest.getAttribute("ARTICULOS_COMPRA");

if (articulosAttr == null) {
    articulosAttr = request.getAttribute("ARTICULOS_COMPRA");
}

List<CompraArticulo> articulos = null;

if (articulosAttr instanceof List) {
    articulos = (List<CompraArticulo>) articulosAttr;
}

if (articulos == null) {
    articulos = new ArrayList<CompraArticulo>();
}

/*
 * Primero intenta métodos SIN parámetro: idealmente deberían traer todos los artículos,
 * así el filtro por sector funciona 100% del lado cliente.
 */
if (articulos.size() == 0) {
    String[] metodosArticulosSinParametro = new String[] {
            "listarArticulos",
            "listarArticulosCompra",
            "listarCompraArticulos",
            "listarArticulosRequerimientoCompra",
            "listarRequerimientoCompraArticulos",
            "getArticulosCompra"
    };

    for (int i = 0; i < metodosArticulosSinParametro.length && articulos.size() == 0; i++) {
        try {
            Method metodo =
                    BusquedaRequerimientoCompraServiceUtil.class.getMethod(
                            metodosArticulosSinParametro[i],
                            new Class[0]
                    );

            Object resultado = metodo.invoke(null, new Object[0]);

            if (resultado instanceof List) {
                articulos = (List<CompraArticulo>) resultado;
            }
        } catch (NoSuchMethodException nsme) {
            // No existe este nombre en esta versión del service.
        } catch (Exception e) {
            // Existe pero falló. Se prueba el siguiente.
        }
    }
}

/*
 * Si no hay método global, intenta métodos POR SECTOR.
 * Esto al menos permite cargar los artículos del sector actual.
 */
if (articulos.size() == 0 && idSectorActual != null && idSectorActual.intValue() > 0) {
    String[] metodosArticulosPorSector = new String[] {
            "listarArticulosPorSector",
            "listarArticulosCompraPorSector",
            "listarCompraArticulosPorSector",
            "listarArticulosSector",
            "getArticulosCompraPorSector",
            "getArticulosPorSector"
    };

    for (int i = 0; i < metodosArticulosPorSector.length && articulos.size() == 0; i++) {
        try {
            Method metodo =
                    BusquedaRequerimientoCompraServiceUtil.class.getMethod(
                            metodosArticulosPorSector[i],
                            new Class[] { Integer.class }
                    );

            Object resultado =
                    metodo.invoke(
                            null,
                            new Object[] { Integer.valueOf(idSectorActual.intValue()) }
                    );

            if (resultado instanceof List) {
                articulos = (List<CompraArticulo>) resultado;
            }
        } catch (NoSuchMethodException nsme) {
            try {
                Method metodo =
                        BusquedaRequerimientoCompraServiceUtil.class.getMethod(
                                metodosArticulosPorSector[i],
                                new Class[] { int.class }
                        );

                Object resultado =
                        metodo.invoke(
                                null,
                                new Object[] { Integer.valueOf(idSectorActual.intValue()) }
                        );

                if (resultado instanceof List) {
                    articulos = (List<CompraArticulo>) resultado;
                }
            } catch (NoSuchMethodException nsme2) {
                // No existe este nombre/firma.
            } catch (Exception e2) {
                // Existe pero falló. Se prueba el siguiente.
            }
        } catch (Exception e) {
            // Existe pero falló. Se prueba el siguiente.
        }
    }
}

int detalleColspan = puedeABMDetalle ? 7 : 6;
%>

<fieldset class="block-labels">
    <legend>Detalle del requerimiento</legend>

    <% if (puedeABMDetalle) { %>
        <fieldset class="block-labels">
            <legend>Agregar / editar detalle</legend>

            <input type="hidden"
                   id="<portlet:namespace />detalle_edit_index"
                   value="-1" />

            <table class="lfr-table" width="100%">
                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_id_articulo">
                            Art&iacute;culo:
                        </label>
                    </td>
                    <td colspan="3">
                        <select id="<portlet:namespace />detalle_id_articulo"
                                style="min-width: 420px;">
                            <option value="">Seleccione...</option>
                        </select>

                        &nbsp;

                        <img alt="Nuevo art&iacute;culo"
                             title="Nuevo art&iacute;culo"
                             align="absmiddle"
                             src="<%= themeDisplay.getPathThemeImages() %>/common/add.png"
                             style="cursor:pointer;"
                             onClick="<portlet:namespace />abrirAltaArticuloCompra();" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_cantidad">
                            Cantidad:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_cantidad"
                               size="8"
                               value="1" />
                    </td>

                    <td>
                        <label for="<portlet:namespace />detalle_precio_unitario_estimado">
                            Precio unitario:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_precio_unitario_estimado"
                               size="12"
                               value="" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_precio_total_estimado">
                            Total estimado:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_precio_total_estimado"
                               size="12"
                               value="" />
                    </td>

                    <td>
                        <label for="<portlet:namespace />detalle_observaciones">
                            Observaciones:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_observaciones"
                               size="60"
                               maxlength="500"
                               value="" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="4" align="center">
                        <input type="button"
                               id="<portlet:namespace />detalle_submit"
                               value="Agregar detalle"
                               onClick="<portlet:namespace />agregarOActualizarDetalle();" />

                        &nbsp;&nbsp;

                        <input type="button"
                               id="<portlet:namespace />detalle_cancelar"
                               value="Cancelar edici&oacute;n"
                               style="display:none;"
                               onClick="<portlet:namespace />cancelarEdicionDetalle();" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <br />
    <% } %>

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

        <tbody id="<portlet:namespace />detalle_body">
            <tr class="portlet-section-body results-row">
                <td colspan="<%= detalleColspan %>">
                    Cargando detalles...
                </td>
            </tr>
        </tbody>
    </table>

    <% if (puedeABMDetalle) { %>
        <div id="<portlet:namespace />detalle_payload"></div>

        <input type="hidden"
               name="<portlet:namespace />detalle_count"
               id="<portlet:namespace />detalle_count"
               value="0" />

        <input type="hidden"
               name="<portlet:namespace />detalle_deleted_ids"
               id="<portlet:namespace />detalle_deleted_ids"
               value="" />
    <% } %>
</fieldset>

<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />articulosCompraCache = [];

    <%
    for (int i = 0; i < articulos.size(); i++) {
        CompraArticulo articulo = articulos.get(i);

        String idArticulo = articulo.getId() != null
                ? String.valueOf(articulo.getId().intValue())
                : "";

        String idSectorArticulo = articulo.getIdSector() != null
                ? String.valueOf(articulo.getIdSector().intValue())
                : "";

        String descripcionArticulo = articulo.getDescripcion() != null
                ? articulo.getDescripcion()
                : "";

        if (idArticulo.length() > 0 && descripcionArticulo.length() > 0) {
    %>
            <portlet:namespace />articulosCompraCache.push({
                id: '<%= jsDetalleCompra(idArticulo) %>',
                sector: '<%= jsDetalleCompra(idSectorArticulo) %>',
                descripcion: '<%= jsDetalleCompra(descripcionArticulo) %>'
            });
    <%
        }
    }
    %>

    <%
    for (int i = 0; i < detalles.size(); i++) {
        RequerimientoCompraDetalle detalle = detalles.get(i);

        String idArticuloDetalle = "";

        if (detalle.getIdArticulo() != null) {
            idArticuloDetalle = String.valueOf(detalle.getIdArticulo().intValue());
        }
    %>
        <portlet:namespace />detallesCompra.push({
            id: '<%= jsDetalleCompra(detalle.getIdString()) %>',
            idArticulo: '<%= jsDetalleCompra(idArticuloDetalle) %>',
            articulo: '<%= jsDetalleCompra(detalle.getArticuloVisible()) %>',
            cantidad: '<%= jsDetalleCompra(detalle.getCantidadString()) %>',
            precioUnitario: '<%= jsDetalleCompra(detalle.getPrecioUnitarioEstimadoString()) %>',
            precioTotal: '<%= jsDetalleCompra(detalle.getPrecioTotalEstimadoString()) %>',
            observaciones: '<%= jsDetalleCompra(detalle.getObservacionesVisible()) %>'
        });
    <%
    }
    %>

    function <portlet:namespace />detalleEscapeHtml(value) {
        if (value == null) {
            return '';
        }

        return jQuery('<div/>').text(value).html();
    }

    function <portlet:namespace />detalleValue(value) {
        return value == null ? '' : value;
    }

    function <portlet:namespace />getSectorSeleccionadoCompra() {
        var sector = '';

        var bySectorId = jQuery('#<portlet:namespace />sector_id');

        if (bySectorId.length > 0) {
            sector = jQuery.trim(bySectorId.val());
        }

        if (sector == '' || sector == '0') {
            var byIdSector = jQuery('#<portlet:namespace />id_sector');

            if (byIdSector.length > 0) {
                sector = jQuery.trim(byIdSector.val());
            }
        }

        if (sector == '' || sector == '0') {
            sector = '<%= HtmlUtil.escape(idSectorActualString) %>';
        }

        return sector;
    }

    function <portlet:namespace />agregarOActualizarArticuloCache(idArticulo, descripcion, idSector) {
        idArticulo = idArticulo == null ? '' : String(idArticulo);
        descripcion = descripcion == null ? '' : String(descripcion);
        idSector = idSector == null ? '' : String(idSector);

        if (idArticulo == '') {
            return;
        }

        for (var i = 0; i < <portlet:namespace />articulosCompraCache.length; i++) {
            if (<portlet:namespace />articulosCompraCache[i].id == idArticulo) {
                <portlet:namespace />articulosCompraCache[i].sector = idSector;
                <portlet:namespace />articulosCompraCache[i].descripcion = descripcion;
                return;
            }
        }

        <portlet:namespace />articulosCompraCache.push({
            id: idArticulo,
            sector: idSector,
            descripcion: descripcion
        });
    }

    function <portlet:namespace />filtrarArticulosPorSector() {
        var select = jQuery('#<portlet:namespace />detalle_id_articulo');

        if (select.length == 0) {
            return;
        }

        var sectorSeleccionado = <portlet:namespace />getSectorSeleccionadoCompra();
        var sectorSeleccionadoNum = parseInt(sectorSeleccionado, 10);
        var valorActual = select.val();
        var valorActualPermitido = false;

        select.empty();
        select.append('<option value="">Seleccione...</option>');

        for (var i = 0; i < <portlet:namespace />articulosCompraCache.length; i++) {
            var articulo = <portlet:namespace />articulosCompraCache[i];

            var sectorArticuloNum = parseInt(articulo.sector, 10);

            /*
             * Si el artículo vino sin idSector, se muestra igual.
             * Eso evita combo vacío cuando el mapper no está seteando CompraArticulo.idSector.
             * Idealmente, el mapper debe setear idSector correctamente.
             */
            var mostrar =
                    isNaN(sectorSeleccionadoNum)
                    || sectorSeleccionadoNum <= 0
                    || articulo.sector == ''
                    || (!isNaN(sectorArticuloNum) && sectorArticuloNum == sectorSeleccionadoNum);

            if (mostrar) {
                var option = jQuery('<option></option>');

                option.val(articulo.id);
                option.attr('data-sector', articulo.sector);
                option.text(articulo.descripcion);

                select.append(option);

                if (articulo.id == valorActual) {
                    valorActualPermitido = true;
                }
            }
        }

        if (valorActual != '' && valorActualPermitido) {
            select.val(valorActual);
        } else {
            select.val('');
        }
    }

    window['<portlet:namespace />filtrarArticulosPorSector'] =
            <portlet:namespace />filtrarArticulosPorSector;

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
        var cantidad = parseInt(
                jQuery.trim(jQuery('#<portlet:namespace />detalle_cantidad').val()),
                10
        );

        var precioUnitario =
                <portlet:namespace />normalizarImporte(
                        jQuery('#<portlet:namespace />detalle_precio_unitario_estimado').val()
                );

        if (isNaN(cantidad) || cantidad <= 0 || precioUnitario == null) {
            return;
        }

        jQuery('#<portlet:namespace />detalle_precio_total_estimado').val(
                (cantidad * precioUnitario).toFixed(2)
        );
    }

    function <portlet:namespace />renderDetallesCompra() {
        var tbody = jQuery('#<portlet:namespace />detalle_body');

        tbody.empty();

        if (<portlet:namespace />detallesCompra.length == 0) {
            tbody.append(
                '<tr class="portlet-section-body results-row">' +
                    '<td colspan="<%= detalleColspan %>">No hay detalles cargados.</td>' +
                '</tr>'
            );

            return;
        }

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            var detalle = <portlet:namespace />detallesCompra[i];

            var rowClass = (i % 2 == 0)
                    ? 'portlet-section-body results-row'
                    : 'portlet-section-alternate results-row alt';

            var html = '';

            html += '<tr class="' + rowClass + '">';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.id) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.articulo) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.cantidad) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioUnitario) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioTotal) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.observaciones) + '</td>';

            <% if (puedeABMDetalle) { %>
                html += '<td>';
                html += '<input type="button" value="Editar" onclick="<portlet:namespace />editarDetalleEnPantalla(' + i + ');" />';
                html += '&nbsp;';
                html += '<input type="button" value="Quitar" onclick="<portlet:namespace />quitarDetalleEnPantalla(' + i + ');" />';
                html += '</td>';
            <% } %>

            html += '</tr>';

            tbody.append(html);
        }
    }

    <% if (puedeABMDetalle) { %>
        var <portlet:namespace />popupArticuloCompra = null;

        function <portlet:namespace />abrirAltaArticuloCompra() {
            var idSector = <portlet:namespace />getSectorSeleccionadoCompra();

            if (idSector == '' || !/^[0-9]+$/.test(idSector) || parseInt(idSector, 10) <= 0) {
                alert('Debe seleccionar un sector antes de cargar un artículo.');
                return;
            }

            <portlet:namespace />popupArticuloCompra = Liferay.Popup({
                title: 'Alta de artículo',
                modal: true,
                width: 700
            });

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                    '&struts_action=/compras/alta_articulo_popup' +
                    '&id_sector=' + encodeURIComponent(idSector) +
                    '&callback=' + encodeURIComponent('<portlet:namespace />seleccionarArticuloCompra');

            jQuery(<portlet:namespace />popupArticuloCompra).load(url);
        }

        function <portlet:namespace />seleccionarArticuloCompra(idArticulo, descripcion, idSector) {
            <portlet:namespace />agregarOActualizarArticuloCache(
                    idArticulo,
                    descripcion,
                    idSector
            );

            <portlet:namespace />filtrarArticulosPorSector();

            var select = jQuery('#<portlet:namespace />detalle_id_articulo');

            select.val(idArticulo);

            <portlet:namespace />cerrarAltaArticuloCompra();

            jQuery('#<portlet:namespace />detalle_cantidad').focus();
        }

        function <portlet:namespace />seleccionarArticuloCompraCerrar() {
            <portlet:namespace />cerrarAltaArticuloCompra();
        }

        function <portlet:namespace />cerrarAltaArticuloCompra() {
            if (<portlet:namespace />popupArticuloCompra) {
                Liferay.Popup.close(<portlet:namespace />popupArticuloCompra);
            }
        }

        function <portlet:namespace />limpiarEditorDetalle() {
            jQuery('#<portlet:namespace />detalle_edit_index').val('-1');
            jQuery('#<portlet:namespace />detalle_id_articulo').val('');
            jQuery('#<portlet:namespace />detalle_cantidad').val('1');
            jQuery('#<portlet:namespace />detalle_precio_unitario_estimado').val('');
            jQuery('#<portlet:namespace />detalle_precio_total_estimado').val('');
            jQuery('#<portlet:namespace />detalle_observaciones').val('');
            jQuery('#<portlet:namespace />detalle_submit').val('Agregar detalle');
            jQuery('#<portlet:namespace />detalle_cancelar').hide();

            <portlet:namespace />filtrarArticulosPorSector();
        }

        function <portlet:namespace />editarDetalleEnPantalla(index) {
            var detalle = <portlet:namespace />detallesCompra[index];

            if (!detalle) {
                return;
            }

            jQuery('#<portlet:namespace />detalle_edit_index').val(index);

            <portlet:namespace />filtrarArticulosPorSector();

            jQuery('#<portlet:namespace />detalle_id_articulo').val(<portlet:namespace />detalleValue(detalle.idArticulo));
            jQuery('#<portlet:namespace />detalle_cantidad').val(<portlet:namespace />detalleValue(detalle.cantidad));
            jQuery('#<portlet:namespace />detalle_precio_unitario_estimado').val(<portlet:namespace />detalleValue(detalle.precioUnitario));
            jQuery('#<portlet:namespace />detalle_precio_total_estimado').val(<portlet:namespace />detalleValue(detalle.precioTotal));
            jQuery('#<portlet:namespace />detalle_observaciones').val(<portlet:namespace />detalleValue(detalle.observaciones));

            jQuery('#<portlet:namespace />detalle_submit').val('Guardar detalle');
            jQuery('#<portlet:namespace />detalle_cancelar').show();

            jQuery('#<portlet:namespace />detalle_id_articulo').focus();
        }

        function <portlet:namespace />cancelarEdicionDetalle() {
            <portlet:namespace />limpiarEditorDetalle();
        }

        function <portlet:namespace />agregarOActualizarDetalle() {
            var idArticulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo').val());
            var articulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo option:selected').text());
            var cantidad = jQuery.trim(jQuery('#<portlet:namespace />detalle_cantidad').val());
            var precioUnitario = jQuery.trim(jQuery('#<portlet:namespace />detalle_precio_unitario_estimado').val());
            var precioTotal = jQuery.trim(jQuery('#<portlet:namespace />detalle_precio_total_estimado').val());
            var observaciones = jQuery.trim(jQuery('#<portlet:namespace />detalle_observaciones').val());

            if (idArticulo == '' || !/^[0-9]+$/.test(idArticulo) || parseInt(idArticulo, 10) <= 0) {
                alert('Debe seleccionar un artículo.');
                jQuery('#<portlet:namespace />detalle_id_articulo').focus();
                return;
            }

            if (cantidad == '' || !/^[0-9]+$/.test(cantidad) || parseInt(cantidad, 10) <= 0) {
                alert('La cantidad debe ser entera y mayor a cero.');
                jQuery('#<portlet:namespace />detalle_cantidad').focus();
                return;
            }

            var precioUnitarioNormalizado =
                    <portlet:namespace />normalizarImporte(precioUnitario);

            var precioTotalNormalizado =
                    <portlet:namespace />normalizarImporte(precioTotal);

            if (precioUnitario != '' && precioUnitarioNormalizado == null) {
                alert('El precio unitario estimado no es valido.');
                jQuery('#<portlet:namespace />detalle_precio_unitario_estimado').focus();
                return;
            }

            if (precioTotal != '' && precioTotalNormalizado == null) {
                alert('El precio total estimado no es valido.');
                jQuery('#<portlet:namespace />detalle_precio_total_estimado').focus();
                return;
            }

            if (precioTotal == '' && precioUnitarioNormalizado != null) {
                precioTotal = (parseInt(cantidad, 10) * precioUnitarioNormalizado).toFixed(2);
                jQuery('#<portlet:namespace />detalle_precio_total_estimado').val(precioTotal);
            }

            var detalle = {
                id: '',
                idArticulo: idArticulo,
                articulo: articulo,
                cantidad: cantidad,
                precioUnitario: precioUnitario,
                precioTotal: precioTotal,
                observaciones: observaciones
            };

            var editIndex = parseInt(
                    jQuery('#<portlet:namespace />detalle_edit_index').val(),
                    10
            );

            if (!isNaN(editIndex)
                    && editIndex >= 0
                    && <portlet:namespace />detallesCompra[editIndex]) {

                detalle.id = <portlet:namespace />detallesCompra[editIndex].id;
                <portlet:namespace />detallesCompra[editIndex] = detalle;
            } else {
                <portlet:namespace />detallesCompra.push(detalle);
            }

            <portlet:namespace />limpiarEditorDetalle();
            <portlet:namespace />renderDetallesCompra();
        }

        function <portlet:namespace />quitarDetalleEnPantalla(index) {
            var detalle = <portlet:namespace />detallesCompra[index];

            if (!detalle) {
                return;
            }

            if (!confirm('Confirma quitar el detalle?')) {
                return;
            }

            if (detalle.id != null && detalle.id != '' && parseInt(detalle.id, 10) > 0) {
                <portlet:namespace />detalleDeletedIds.push(detalle.id);
            }

            <portlet:namespace />detallesCompra.splice(index, 1);
            <portlet:namespace />limpiarEditorDetalle();
            <portlet:namespace />renderDetallesCompra();
        }

        function <portlet:namespace />crearHiddenDetalle(name, value) {
            var form = document.getElementById('<portlet:namespace />fmCompras');

            if (!form) {
                alert('No se encontro el formulario principal para serializar detalles.');
                return false;
            }

            var input = document.createElement('input');

            input.type = 'hidden';
            input.name = '<portlet:namespace />' + name;
            input.value = value == null ? '' : value;
            input.className = '<portlet:namespace />detalle_serializado';

            form.appendChild(input);

            return true;
        }

        function <portlet:namespace />serializarDetallesCompras() {
            var form = document.getElementById('<portlet:namespace />fmCompras');

            if (!form) {
                alert('No se encontro el formulario principal de Compras.');
                return false;
            }

            jQuery('.<portlet:namespace />detalle_serializado').remove();

            if (!<portlet:namespace />crearHiddenDetalle('detalle_count', <portlet:namespace />detallesCompra.length)) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle('detalle_deleted_ids', <portlet:namespace />detalleDeletedIds.join(','))) {
                return false;
            }

            for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
                var detalle = <portlet:namespace />detallesCompra[i];
                var prefix = 'detalle_' + i + '_';

                if (jQuery.trim(detalle.idArticulo) == ''
                        || !/^[0-9]+$/.test(jQuery.trim(detalle.idArticulo))
                        || parseInt(detalle.idArticulo, 10) <= 0) {

                    alert('Hay un detalle sin artículo.');
                    return false;
                }

                if (jQuery.trim(detalle.cantidad) == ''
                        || !/^[0-9]+$/.test(jQuery.trim(detalle.cantidad))
                        || parseInt(detalle.cantidad, 10) <= 0) {

                    alert('Hay un detalle con cantidad invalida.');
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'id', detalle.id)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'id_articulo', detalle.idArticulo)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'cantidad', detalle.cantidad)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'precio_unitario_estimado', detalle.precioUnitario)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'precio_total_estimado', detalle.precioTotal)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'observaciones', detalle.observaciones)) {
                    return false;
                }
            }

            return true;
        }

        window['<portlet:namespace />serializarDetallesCompras'] =
                <portlet:namespace />serializarDetallesCompras;

        jQuery(function() {
            jQuery('#<portlet:namespace />detalle_cantidad, #<portlet:namespace />detalle_precio_unitario_estimado').change(function() {
                <portlet:namespace />calcularTotalDetalle();
            });

            jQuery('#<portlet:namespace />id_sector, #<portlet:namespace />sector_id').change(function() {
                <portlet:namespace />filtrarArticulosPorSector();
            });

            <portlet:namespace />filtrarArticulosPorSector();
        });
    <% } %>

    jQuery(function() {
        <portlet:namespace />renderDetallesCompra();

        <% if (puedeABMDetalle) { %>
            <portlet:namespace />filtrarArticulosPorSector();
        <% } %>
    });
</script>
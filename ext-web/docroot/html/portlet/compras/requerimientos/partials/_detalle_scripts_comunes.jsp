<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />articulosCompraCache = [];
    var <portlet:namespace />prestadoresEnviadosDetalleCache = [];
    var <portlet:namespace />idPrestadorAdjudicado =
            '<%= jsDetalleCompra(idPrestadorAdjudicadoDetalle) %>';
    var <portlet:namespace />prestadorAdjudicado =
            '<%= jsDetalleCompra(prestadorAdjudicadoDetalle) %>';

    <%
    if (prestadoresEnviadosDetalle != null) {
        for (int i = 0;
                i < prestadoresEnviadosDetalle.size();
                i++) {

            PrestadorCotizacion prestadorDetalle =
                    prestadoresEnviadosDetalle.get(i);

            if (prestadorDetalle == null
                    || prestadorDetalle.getIdPrestador() <= 0) {

                continue;
            }
    %>
            <portlet:namespace />prestadoresEnviadosDetalleCache.push({
                id: '<%= prestadorDetalle.getIdPrestador() %>',
                label: '<%= jsDetalleCompra(
                        prestadorDetalle.getEtiquetaVisible()
                ) %>'
            });
    <%
        }
    }
    %>

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

        if (detalle == null) {
            continue;
        }

        String idArticuloDetalle = "";

        if (detalle.getIdArticulo() != null) {
            idArticuloDetalle = String.valueOf(detalle.getIdArticulo().intValue());
        }

        String idDetalleCotizacion =
                detalle.getIdString();

        boolean cotizacionRestaurada =
                preciosCotizacionRestaurados.containsKey(
                        idDetalleCotizacion
                )
                || prestadoresCotizacionRestaurados.containsKey(
                        idDetalleCotizacion
                );

        String precioUnitarioDetalle =
                preciosCotizacionRestaurados.containsKey(
                        idDetalleCotizacion
                )
                        ? preciosCotizacionRestaurados.get(
                                idDetalleCotizacion
                        )
                        : detalle.getPrecioUnitarioEstimadoString();

        String idPrestadorDetalle =
                prestadoresCotizacionRestaurados.containsKey(
                        idDetalleCotizacion
                )
                        ? prestadoresCotizacionRestaurados.get(
                                idDetalleCotizacion
                        )
                        : detalle.getIdPrestadorString();

        String prestadorDetalle =
                labelsPrestadorCotizacionRestaurados.containsKey(
                        idDetalleCotizacion
                )
                        ? labelsPrestadorCotizacionRestaurados.get(
                                idDetalleCotizacion
                        )
                        : detalle.getPrestadorSeleccionadoVisible();
    %>
        <portlet:namespace />detallesCompra.push({
            id: '<%= jsDetalleCompra(detalle.getIdString()) %>',
            idArticulo: '<%= jsDetalleCompra(idArticuloDetalle) %>',
            articulo: '<%= jsDetalleCompra(detalle.getArticuloVisible()) %>',
            cantidad: '<%= jsDetalleCompra(detalle.getCantidadString()) %>',
            precioUnitario: '<%= jsDetalleCompra(precioUnitarioDetalle) %>',
            precioTotal: '<%= cotizacionRestaurada
                    ? ""
                    : jsDetalleCompra(detalle.getPrecioTotalEstimadoString()) %>',
            idPrestador: '<%= jsDetalleCompra(idPrestadorDetalle) %>',
            prestador: '<%= jsDetalleCompra(prestadorDetalle) %>',
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

    function <portlet:namespace />parseImporteDetalle(value) {
        if (value == null) {
            return null;
        }

        var clean = jQuery.trim(String(value));

        if (clean == '') {
            return null;
        }

        clean = clean.replace(/\s/g, '');

        if (clean.indexOf(',') >= 0) {
            clean = clean.replace(/\./g, '').replace(',', '.');
        }

        if (!/^[0-9]+(\.[0-9]+)?$/.test(clean)) {
            return NaN;
        }

        return parseFloat(clean);
    }

    function <portlet:namespace />calcularTotalDetalle(index) {
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return '';
        }

        var cantidad = parseInt(detalle.cantidad, 10);
        var precio = <portlet:namespace />parseImporteDetalle(detalle.precioUnitario);

        if (isNaN(cantidad) || cantidad <= 0 || precio == null || isNaN(precio)) {
            return '';
        }

        return (cantidad * precio).toFixed(2);
    }

    function <portlet:namespace />capturarPrestadorAdjudicado() {
        var selector =
                jQuery(
                        '#<portlet:namespace />id_prestador_adjudicado'
                );

        if (selector.length > 0) {
            <portlet:namespace />idPrestadorAdjudicado =
                    jQuery.trim(
                            selector.val()
                    );

            <portlet:namespace />prestadorAdjudicado =
                    <portlet:namespace />idPrestadorAdjudicado == ''
                            ? ''
                            : jQuery.trim(
                                    selector
                                            .find('option:selected')
                                            .text()
                            );
        }

        for (var i = 0;
                i < <portlet:namespace />detallesCompra.length;
                i++) {

            var detalle =
                    <portlet:namespace />detallesCompra[i];

            if (!detalle) {
                continue;
            }

            detalle.idPrestador =
                    <portlet:namespace />idPrestadorAdjudicado;
            detalle.prestador =
                    <portlet:namespace />prestadorAdjudicado;
        }
    }

    function <portlet:namespace />capturarCotizacionDetalle(index) {
        var detalle =
                <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            if (window.console && window.console.error) {
                window.console.error(
                        '[COMPRAS-COTIZACION] No existe el detalle '
                                + 'en el índice '
                                + index
                );
            }

            return false;
        }

        var precioInputId =
                '<portlet:namespace />detalle_precio_unitario_'
                        + index;

        var precioCellId =
                '<portlet:namespace />detalle_precio_cell_'
                        + index;

        /*
         * Búsqueda principal por el ID esperado.
         */
        var precioInput =
                document.getElementById(
                        precioInputId
                );

        /*
         * Compatibilidad con inputs generados por jQuery legacy:
         * el campo puede estar visible dentro de la celda aunque
         * el constructor no haya aplicado correctamente su ID.
         */
        if (!precioInput) {
            var precioCell =
                    document.getElementById(
                            precioCellId
                    );

            if (precioCell) {
                var inputs =
                        precioCell.getElementsByTagName(
                                'input'
                        );

                /*
                 * La celda debe contener un único input de precio.
                 */
                if (inputs != null && inputs.length == 1) {
                    precioInput =
                            inputs[0];

                    /*
                     * Reparar el ID para que las próximas capturas
                     * puedan encontrarlo directamente.
                     */
                    precioInput.id =
                            precioInputId;

                    precioInput.setAttribute(
                            'data-detalle-index',
                            String(index)
                    );

                    precioInput.setAttribute(
                            'data-detalle-id',
                            detalle.id == null
                                    ? ''
                                    : String(detalle.id)
                    );
                }
            }
        }

        if (!precioInput) {
            if (window.console && window.console.error) {
                window.console.error(
                        '[COMPRAS-COTIZACION] No se encontró el input '
                                + precioInputId
                                + ' ni un input recuperable dentro de '
                                + precioCellId
                );
            }

            return false;
        }

        var precioUnitario =
                jQuery.trim(
                        precioInput.value == null
                                ? ''
                                : String(precioInput.value)
                );

        detalle.precioUnitario =
                precioUnitario;

        if (typeof <portlet:namespace />capturarPrestadorAdjudicado
                == 'function') {

            <portlet:namespace />capturarPrestadorAdjudicado();
        }

        detalle.precioTotal =
                <portlet:namespace />calcularTotalDetalle(
                        index
                );

        var totalElement =
                document.getElementById(
                        '<portlet:namespace />detalle_total_estimado_'
                                + index
                );

        if (totalElement) {
            if (typeof totalElement.textContent != 'undefined') {
                totalElement.textContent =
                        detalle.precioTotal;
            } else {
                /*
                 * Compatibilidad con navegadores legacy.
                 */
                totalElement.innerText =
                        detalle.precioTotal;
            }
        }

        return true;
    }

    function <portlet:namespace />actualizarPrecioDetalle(index) {
        <portlet:namespace />capturarCotizacionDetalle(index);
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
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.observaciones) + '</td>';

            <% if (puedeVerCotizacionDetalle) { %>
                <% if (puedeCotizarDetalle) { %>
                    html += '<td id="<portlet:namespace />detalle_precio_cell_' + i + '"></td>';
                    html += '<td><span id="<portlet:namespace />detalle_total_estimado_' + i + '">' +
                            <portlet:namespace />detalleEscapeHtml(detalle.precioTotal) + '</span></td>';
                <% } else { %>
                    html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioUnitario) + '</td>';
                    html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioTotal) + '</td>';
                <% } %>
            <% } %>

            <% if (puedeABMDetalle) { %>
                html += '<td>';
                html += '<a href="#" title="Editar" onclick="<portlet:namespace />editarDetalleEnPantalla(' + i + '); return false;">';
                html += '<img alt="Editar" src="<%= themeDisplay.getPathThemeImages() %>/common/edit.png" />';
                html += '</a>';
                html += '&nbsp;';
                html += '<a href="#" title="Quitar" onclick="<portlet:namespace />quitarDetalleEnPantalla(' + i + '); return false;">';
                html += '<img alt="Quitar" src="<%= themeDisplay.getPathThemeImages() %>/common/delete.png" />';
                html += '</a>';
                html += '</td>';
            <% } %>

            html += '</tr>';

            tbody.append(html);

            <% if (puedeCotizarDetalle) { %>
                var precioInput = jQuery('<input/>', {
                    type: 'text',
                    size: '10',
                    id:
                            '<portlet:namespace />detalle_precio_unitario_'
                                    + i,
                    'data-detalle-index': i,
                    'data-detalle-id':
                            detalle.id == null
                                    ? ''
                                    : detalle.id,
                    autocomplete: 'off'
                });

                precioInput.val(
                        detalle.precioUnitario == null
                                ? ''
                                : detalle.precioUnitario
                );

                (function(index, input) {
                    input.bind('keyup change', function() {
                        <portlet:namespace />actualizarPrecioDetalle(index);
                    });
                })(i, precioInput);

                jQuery(
                        '#<portlet:namespace />detalle_precio_cell_' + i
                ).append(precioInput);
            <% } %>
        }
    }

    jQuery(function() {
        <% if (puedeCotizarDetalle) { %>
            var selectorPrestador =
                    jQuery(
                            '#<portlet:namespace />id_prestador_adjudicado'
                    );

            if (selectorPrestador.length > 0) {
                selectorPrestador.val(
                        <portlet:namespace />idPrestadorAdjudicado
                );

                if (selectorPrestador.val() == null) {
                    selectorPrestador.val('');
                }
            }

            <portlet:namespace />capturarPrestadorAdjudicado();
        <% } %>

        <portlet:namespace />renderDetallesCompra();

        <% if (puedeCotizarDetalle) { %>
            for (var i = 0;
                    i < <portlet:namespace />detallesCompra.length;
                    i++) {

                <portlet:namespace />capturarCotizacionDetalle(i);
            }
        <% } %>
    });
</script>

<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />prestadoresEnviadosDetalleCache = [];
    var <portlet:namespace />tiposPrestacionDetalleCache = [];
    var <portlet:namespace />sectorDescripcionInicialCompra =
            '<%= jsDetalleCompra(sectorDescripcionActualString) %>';
    var <portlet:namespace />idPrestadorAdjudicado =
            '<%= jsDetalleCompra(idPrestadorAdjudicadoDetalle) %>';
    var <portlet:namespace />prestadorAdjudicado =
            '<%= jsDetalleCompra(prestadorAdjudicadoDetalle) %>';

    <%
    for (int i = 0; i < tiposPrestacionDetalle.size(); i++) {
        TipoPrestacionCompra tipoPrestacion =
                tiposPrestacionDetalle.get(i);

        if (tipoPrestacion == null
                || tipoPrestacion.getIdInt() <= 0
                || tipoPrestacion.getIdSectorInt() <= 0) {

            continue;
        }
    %>
        <portlet:namespace />tiposPrestacionDetalleCache.push({
            id: '<%= tipoPrestacion.getIdInt() %>',
            idSector: '<%= tipoPrestacion.getIdSectorInt() %>',
            descripcion: '<%= jsDetalleCompra(
                    tipoPrestacion.getDescripcionVisible()
            ) %>'
        });
    <%
    }
    %>

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
                ) %>',

                emailActual: '<%= jsDetalleCompra(
                        prestadorDetalle.getEmailVisible()
                ) %>',

                emailDestino: '<%= jsDetalleCompra(
                        prestadorDetalle.getEmailDestinoVisible()
                ) %>',

                estadoEnvio: '<%= jsDetalleCompra(
                        prestadorDetalle.getEstadoEnvioVisible()
                ) %>'
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
            tipoItem: '<%= jsDetalleCompra(detalle.getTipoItemNormalizado()) %>',
            idTipoPrestacion: '<%= jsDetalleCompra(detalle.getIdTipoPrestacionString()) %>',
            tipoPrestacion: '<%= jsDetalleCompra(detalle.getTipoPrestacionDescripcionVisible()) %>',
            codigoItem: '<%= jsDetalleCompra(detalle.getCodigoItemVisible()) %>',
            descripcionItem: '<%= jsDetalleCompra(detalle.getDescripcionItemVisible()) %>',
            idPrestacion: '<%= jsDetalleCompra(detalle.getIdPrestacionString()) %>',
            idTipoNomenclador: '<%= jsDetalleCompra(detalle.getIdTipoNomencladorString()) %>',
            codigoNomenclador: '<%= jsDetalleCompra(detalle.getCodigoNomencladorVisible()) %>',
            descripcionNomenclador: '<%= jsDetalleCompra(detalle.getDescripcionNomencladorVisible()) %>',
            idMedicamento: '<%= jsDetalleCompra(detalle.getIdMedicamentoString()) %>',
            troquel: '<%= jsDetalleCompra(detalle.getTroquelString()) %>',
            nombreMedicamento: '<%= jsDetalleCompra(detalle.getNombreMedicamentoVisible()) %>',
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

        var precioInput =
                document.getElementById(
                        precioInputId
                );

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

                if (inputs != null && inputs.length == 1) {
                    precioInput =
                            inputs[0];

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

    function <portlet:namespace />getSectorDescripcionSeleccionadoCompra() {
        var descripcion = '';

        var bySectorId = jQuery('#<portlet:namespace />sector_id');

        if (bySectorId.length > 0) {
            descripcion =
                    jQuery.trim(
                            bySectorId.find('option:selected').text()
                    );
        }

        if (descripcion == '') {
            var byIdSector = jQuery('#<portlet:namespace />id_sector');

            if (byIdSector.length > 0) {
                descripcion =
                        jQuery.trim(
                                byIdSector.find('option:selected').text()
                        );
            }
        }

        if (descripcion == '') {
            descripcion =
                    <portlet:namespace />sectorDescripcionInicialCompra;
        }

        return descripcion;
    }

    function <portlet:namespace />normalizarSectorCompra(value) {
        value = value == null ? '' : String(value);

        if (typeof value.normalize == 'function') {
            value = value.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        } else {
            value = value.replace(/[áàäâ]/gi, 'a')
                    .replace(/[éèëê]/gi, 'e')
                    .replace(/[íìïî]/gi, 'i')
                    .replace(/[óòöô]/gi, 'o')
                    .replace(/[úùüû]/gi, 'u');
        }

        return jQuery.trim(value).toUpperCase();
    }

    function <portlet:namespace />esSectorFarmaciaCompra() {
        var descripcion =
                <portlet:namespace />getSectorDescripcionSeleccionadoCompra();

        return <portlet:namespace />normalizarSectorCompra(descripcion)
                == 'FARMACIA';
    }

    function <portlet:namespace />esSectorNomencladorCompra() {
        var descripcion =
                <portlet:namespace />normalizarSectorCompra(
                        <portlet:namespace />getSectorDescripcionSeleccionadoCompra()
                );

        return descripcion == 'PRESTACIONES MEDICAS'
                || descripcion == 'DISCAPACIDAD'
                || descripcion == 'ODONTOLOGIA';
    }

    function <portlet:namespace />esSectorDetalleConCodigoCompra() {
        return <portlet:namespace />esSectorFarmaciaCompra()
                || <portlet:namespace />esSectorNomencladorCompra();
    }

    function <portlet:namespace />esSectorDetalleObservacionCompra() {
        var descripcion =
                <portlet:namespace />normalizarSectorCompra(
                        <portlet:namespace />getSectorDescripcionSeleccionadoCompra()
                );

        return descripcion == 'RRHH'
                || descripcion == 'LEGALES'
                || descripcion == 'SISTEMAS'
                || descripcion == 'OTROS';
    }

    function <portlet:namespace />actualizarVisibilidadColumnasDetalleCompra() {
        var mostrarCodigo =
                <portlet:namespace />esSectorDetalleConCodigoCompra();
        var mostrarObservacion =
                <portlet:namespace />esSectorDetalleObservacionCompra();

        jQuery('.compras-detalle-columna-codigo').css(
                'display',
                mostrarCodigo ? '' : 'none'
        );
        jQuery('.compras-detalle-columna-observacion').css(
                'display',
                mostrarObservacion ? '' : 'none'
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

            <portlet:namespace />actualizarVisibilidadColumnasDetalleCompra();

            return;
        }

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            var detalle = <portlet:namespace />detallesCompra[i];

            var rowClass = (i % 2 == 0)
                    ? 'portlet-section-body results-row'
                    : 'portlet-section-alternate results-row alt';

            var html = '';

            html += '<tr class="' + rowClass + '">';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.tipoPrestacion) + '</td>';
            html += '<td class="compras-detalle-columna-codigo">' + <portlet:namespace />detalleEscapeHtml(detalle.codigoItem) + '</td>';
            html += '<td class="compras-detalle-columna-codigo">' + <portlet:namespace />detalleEscapeHtml(detalle.descripcionItem) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.cantidad) + '</td>';
            html += '<td class="compras-detalle-columna-observacion">' + <portlet:namespace />detalleEscapeHtml(detalle.observaciones) + '</td>';

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

            <% if (puedeABMDetalle || puedeEliminarDetalle) { %>
                html += '<td>';

                <% if (puedeABMDetalle) { %>
                    html += '<a href="#" title="Editar" '
                            + 'onclick="<portlet:namespace />editarDetalleEnPantalla('
                            + i
                            + '); return false;">';
                    html += '<img alt="Editar" '
                            + 'src="<%= themeDisplay.getPathThemeImages() %>/common/edit.png" />';
                    html += '</a>';
                <% } %>

                <% if (puedeABMDetalle && puedeEliminarDetalle) { %>
                    html += '&nbsp;';
                <% } %>

                <% if (puedeEliminarDetalle) { %>

                    if (<portlet:namespace />detallesCompra.length > 1) {

                        html += '<a href="#" title="Quitar" '
                                + 'onclick="<portlet:namespace />quitarDetalleEnPantalla('
                                + i
                                + '); return false;">';
                        html += '<img alt="Quitar" '
                                + 'src="<%= themeDisplay.getPathThemeImages() %>/common/delete.png" />';
                        html += '</a>';

                    }

                <% } %>

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

        <portlet:namespace />actualizarVisibilidadColumnasDetalleCompra();
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

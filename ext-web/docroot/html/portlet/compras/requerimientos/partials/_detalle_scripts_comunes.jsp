<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />articulosCompraCache = [];
    var <portlet:namespace />prestadoresEnviadosDetalleCache = [];

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

    function <portlet:namespace />capturarCotizacionDetalle(index) {
        var detalle =
                <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return;
        }

        var precioInput =
                jQuery(
                        '#<portlet:namespace />detalle_precio_unitario_'
                                + index
                );

        var prestadorInput =
                jQuery(
                        '#<portlet:namespace />detalle_id_prestador_'
                                + index
                );

        if (precioInput.length > 0) {
            detalle.precioUnitario =
                    jQuery.trim(
                            precioInput.val()
                    );
        }

        if (prestadorInput.length > 0) {
            detalle.idPrestador =
                    jQuery.trim(
                            prestadorInput.val()
                    );

            if (detalle.idPrestador == '') {
                detalle.prestador = '';
            } else {
                detalle.prestador =
                        jQuery.trim(
                                prestadorInput
                                        .find('option:selected')
                                        .text()
                        );
            }
        }

        detalle.precioTotal =
                <portlet:namespace />calcularTotalDetalle(
                        index
                );

        jQuery(
                '#<portlet:namespace />detalle_total_estimado_'
                        + index
        ).text(
                detalle.precioTotal
        );
    }

    function <portlet:namespace />actualizarPrecioDetalle(index) {
        <portlet:namespace />capturarCotizacionDetalle(index);
    }

    function <portlet:namespace />setPrestadorDetalle(
            index,
            idPrestador) {

        var detalle =
                <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return false;
        }

        var select =
                jQuery(
                        '#<portlet:namespace />detalle_id_prestador_'
                                + index
                );

        if (select.length == 0) {
            return false;
        }

        var idNormalizado =
                idPrestador == null
                        ? ''
                        : String(idPrestador);

        select.val(idNormalizado);

        /*
         * Si el ID no forma parte de las opciones válidas,
         * jQuery devuelve null. No se crea una opción artificial.
         */
        if (select.val() == null) {
            select.val('');
        }

        detalle.idPrestador =
                jQuery.trim(
                        select.val()
                );

        if (detalle.idPrestador == '') {
            detalle.prestador = '';
        } else {
            detalle.prestador =
                    jQuery.trim(
                            select
                                    .find('option:selected')
                                    .text()
                    );
        }

        return true;
    }

    function <portlet:namespace />aplicarPrestadorATodos(index) {
        /*
         * Captura expresamente el valor visible del combo antes de
         * utilizar el objeto JavaScript del detalle.
         */
        <portlet:namespace />capturarCotizacionDetalle(index);

        var detalle =
                <portlet:namespace />detallesCompra[index];

        if (!detalle
                || detalle.idPrestador == null
                || detalle.idPrestador == '') {

            alert('Seleccione primero un prestador.');
            return false;
        }

        for (var i = 0;
                i < <portlet:namespace />detallesCompra.length;
                i++) {

            <portlet:namespace />setPrestadorDetalle(
                    i,
                    detalle.idPrestador
            );
        }

        return false;
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

    function <portlet:namespace />getOpcionesPrestadorDetalle(
            idPrestadorSeleccionado) {

        var seleccionado =
                idPrestadorSeleccionado == null
                        ? ''
                        : String(idPrestadorSeleccionado);

        var html =
                '<option value="">Seleccione...</option>';

        for (var i = 0;
                i < <portlet:namespace />prestadoresEnviadosDetalleCache.length;
                i++) {

            var prestador =
                    <portlet:namespace />prestadoresEnviadosDetalleCache[i];

            if (!prestador
                    || prestador.id == null
                    || String(prestador.id) == '') {

                continue;
            }

            var idPrestador =
                    String(prestador.id);

            var selected =
                    idPrestador == seleccionado
                            ? ' selected="selected"'
                            : '';

            html +=
                    '<option value="'
                    + <portlet:namespace />detalleEscapeHtml(
                            idPrestador
                    )
                    + '"'
                    + selected
                    + '>'
                    + <portlet:namespace />detalleEscapeHtml(
                            prestador.label
                    )
                    + '</option>';
        }

        return html;
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
                    html += '<td>';
                    html += '<input type="text" size="10" id="<portlet:namespace />detalle_precio_unitario_' + i + '" ';
                    html += 'value="' + <portlet:namespace />detalleEscapeHtml(detalle.precioUnitario) + '" ';
                    html += 'onkeyup="<portlet:namespace />actualizarPrecioDetalle(' + i + ');" ';
                    html += 'onchange="<portlet:namespace />actualizarPrecioDetalle(' + i + ');" />';
                    html += '</td>';
                    html += '<td><span id="<portlet:namespace />detalle_total_estimado_' + i + '">' +
                            <portlet:namespace />detalleEscapeHtml(detalle.precioTotal) + '</span></td>';
                    html += '<td>';

                    html += '<select ';
                    html += 'id="<portlet:namespace />detalle_id_prestador_' + i + '" ';
                    html += 'style="max-width: 360px; width: 100%;" ';
                    html += 'onchange="<portlet:namespace />capturarCotizacionDetalle(' + i + ');"';

                    if (<portlet:namespace />prestadoresEnviadosDetalleCache.length == 0) {
                        html += ' disabled="disabled"';
                    }

                    html += '>';

                    html +=
                            <portlet:namespace />getOpcionesPrestadorDetalle(
                                    detalle.idPrestador
                            );

                    html += '</select>';

                    html += '<br />';

                    html += '<input type="button" ';
                    html += 'value="Aplicar a todos" ';
                    html += 'onclick="return <portlet:namespace />aplicarPrestadorATodos(' + i + ');"';

                    if (<portlet:namespace />prestadoresEnviadosDetalleCache.length == 0) {
                        html += ' disabled="disabled"';
                    }

                    html += ' />';

                    html += '</td>';
                <% } else { %>
                    html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioUnitario) + '</td>';
                    html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.precioTotal) + '</td>';
                    html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.prestador) + '</td>';
                <% } %>
            <% } %>

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

    jQuery(function() {
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

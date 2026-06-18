<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />articulosCompraCache = [];
    var <portlet:namespace />prestadoresEnviadosURL =
            '<%= prestadoresEnviadosURL.toString() %>';

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
    %>
        <portlet:namespace />detallesCompra.push({
            id: '<%= jsDetalleCompra(detalle.getIdString()) %>',
            idArticulo: '<%= jsDetalleCompra(idArticuloDetalle) %>',
            articulo: '<%= jsDetalleCompra(detalle.getArticuloVisible()) %>',
            cantidad: '<%= jsDetalleCompra(detalle.getCantidadString()) %>',
            precioUnitario: '<%= jsDetalleCompra(detalle.getPrecioUnitarioEstimadoString()) %>',
            precioTotal: '<%= jsDetalleCompra(detalle.getPrecioTotalEstimadoString()) %>',
            idPrestador: '<%= jsDetalleCompra(detalle.getIdPrestadorString()) %>',
            prestador: '<%= jsDetalleCompra(detalle.getPrestadorSeleccionadoVisible()) %>',
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
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return;
        }

        var precioInput = jQuery('#<portlet:namespace />detalle_precio_unitario_' + index);
        var prestadorInput = jQuery('#<portlet:namespace />detalle_id_prestador_' + index);

        if (precioInput.length > 0) {
            detalle.precioUnitario = jQuery.trim(precioInput.val());
        }

        if (prestadorInput.length > 0) {
            detalle.idPrestador = jQuery.trim(prestadorInput.val());
        }

        detalle.precioTotal = <portlet:namespace />calcularTotalDetalle(index);

        jQuery('#<portlet:namespace />detalle_total_estimado_' + index).text(detalle.precioTotal);
    }

    function <portlet:namespace />actualizarPrecioDetalle(index) {
        <portlet:namespace />capturarCotizacionDetalle(index);
    }

    function <portlet:namespace />setPrestadorDetalle(index, idPrestador, label) {
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return;
        }

        detalle.idPrestador = idPrestador == null ? '' : String(idPrestador);
        detalle.prestador = label == null ? '' : String(label);

        jQuery('#<portlet:namespace />detalle_id_prestador_' + index).val(detalle.idPrestador);
        jQuery('#<portlet:namespace />detalle_prestador_label_' + index).text(detalle.prestador);
    }

    function <portlet:namespace />buscarPrestadoresDetalle(index) {
        var texto = jQuery.trim(jQuery('#<portlet:namespace />detalle_prestador_texto_' + index).val());

        if (texto.length < 2) {
            alert('Ingrese al menos dos caracteres para buscar prestadores.');
            return false;
        }

        jQuery.ajax({
            type: 'GET',
            url: <portlet:namespace />prestadoresEnviadosURL
                    + '&texto=' + encodeURIComponent(texto)
                    + '&_ts=' + new Date().getTime(),
            dataType: 'json',
            cache: false,
            success: function(data) {
                var select = jQuery('#<portlet:namespace />detalle_prestador_result_' + index);

                select.empty();
                select.append('<option value="">Seleccione...</option>');

                if (!data || !data.prestadores || data.prestadores.length == 0) {
                    alert('No se encontraron prestadores enviados para este requerimiento.');
                    select.hide();
                    return;
                }

                for (var i = 0; i < data.prestadores.length; i++) {
                    var prestador = data.prestadores[i];
                    var option = jQuery('<option></option>');

                    option.val(prestador.id);
                    option.text(prestador.label);
                    option.attr('data-label', prestador.label);
                    select.append(option);
                }

                select.show();
                select.focus();
            },
            error: function() {
                alert('No se pudo buscar prestadores enviados.');
            }
        });

        return false;
    }

    function <portlet:namespace />seleccionarPrestadorDetalle(index) {
        var select = jQuery('#<portlet:namespace />detalle_prestador_result_' + index);
        var selected = select.find('option:selected');

        if (selected.length == 0 || selected.val() == '') {
            return false;
        }

        <portlet:namespace />setPrestadorDetalle(
                index,
                selected.val(),
                selected.attr('data-label')
        );

        select.hide();

        return false;
    }

    function <portlet:namespace />aplicarPrestadorATodos(index) {
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle || detalle.idPrestador == null || detalle.idPrestador == '') {
            alert('Seleccione primero un prestador.');
            return false;
        }

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            <portlet:namespace />setPrestadorDetalle(
                    i,
                    detalle.idPrestador,
                    detalle.prestador
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
                    html += '<span id="<portlet:namespace />detalle_prestador_label_' + i + '">' +
                            <portlet:namespace />detalleEscapeHtml(detalle.prestador) + '</span>';
                    html += '<input type="hidden" id="<portlet:namespace />detalle_id_prestador_' + i + '" value="' +
                            <portlet:namespace />detalleEscapeHtml(detalle.idPrestador) + '" />';
                    html += '<br />';
                    html += '<input type="text" size="24" id="<portlet:namespace />detalle_prestador_texto_' + i + '" />';
                    html += '&nbsp;<input type="button" value="Buscar" onclick="return <portlet:namespace />buscarPrestadoresDetalle(' + i + ');" />';
                    html += '&nbsp;<input type="button" value="Aplicar a todos" onclick="return <portlet:namespace />aplicarPrestadorATodos(' + i + ');" />';
                    html += '<br /><select id="<portlet:namespace />detalle_prestador_result_' + i + '" ';
                    html += 'style="display:none; max-width: 360px;" ';
                    html += 'onchange="return <portlet:namespace />seleccionarPrestadorDetalle(' + i + ');"></select>';
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
    });
</script>

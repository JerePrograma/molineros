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
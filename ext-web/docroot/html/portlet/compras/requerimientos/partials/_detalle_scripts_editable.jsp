<script type="text/javascript">
    var <portlet:namespace />detalleAccionEnCurso = false;
    var <portlet:namespace />popupArticuloCompraAbriendo = false;
    var <portlet:namespace />popupArticuloCompra = null;

    var <portlet:namespace />detalleActionURL =
            '<%= detalleActionURL.toString() %>';

    var <portlet:namespace />requerimientoPersistidoDetalle =
            <%= requerimientoPersistidoDetalle ? "true" : "false" %>;

    var <portlet:namespace />idRequerimientoCompraDetalle =
            '<%= idRequerimientoCompraDetalle %>';

    var <portlet:namespace />articulosCompraSectorCargado = {};

    <% if (idSectorActualString != null
            && idSectorActualString.length() > 0
            && articulos != null
            && articulos.size() > 0) { %>
        <portlet:namespace />articulosCompraSectorCargado['<%= idSectorActualString %>'] = true;
    <% } %>

    var <portlet:namespace />articulosSectorURL =
            '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/compras/listar_articulos_sector" /></portlet:renderURL>';

    function <portlet:namespace />abrirAltaArticuloCompra() {
        if (<portlet:namespace />popupArticuloCompraAbriendo) {
            return false;
        }

        <portlet:namespace />popupArticuloCompraAbriendo = true;

        var idSector = <portlet:namespace />getSectorSeleccionadoCompra();

        if (idSector == '' || !/^[0-9]+$/.test(idSector) || parseInt(idSector, 10) <= 0) {
            alert('Debe seleccionar un sector antes de cargar un articulo.');
            <portlet:namespace />popupArticuloCompraAbriendo = false;
            return false;
        }

        <portlet:namespace />popupArticuloCompra = Liferay.Popup({
            title: 'Alta de articulo',
            modal: true,
            width: 700
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/alta_articulo_popup' +
                '&id_sector=' + encodeURIComponent(idSector) +
                '&callback=' + encodeURIComponent('<portlet:namespace />seleccionarArticuloCompra');

        jQuery(<portlet:namespace />popupArticuloCompra).load(url, function() {
            <portlet:namespace />popupArticuloCompraAbriendo = false;
        });

        return false;
    }

    function <portlet:namespace />seleccionarArticuloCompra(idArticulo, descripcion, idSector) {
        var sectorKey = idSector == null ? '' : String(idSector);
        var sectorYaCargado =
                sectorKey != ''
                && <portlet:namespace />articulosCompraSectorCargado[sectorKey];

        <portlet:namespace />agregarOActualizarArticuloCache(
                idArticulo,
                descripcion,
                idSector
        );

        <portlet:namespace />cargarArticulosPorSectorSiHaceFalta(
                sectorYaCargado,
                function() {
                    var select = jQuery('#<portlet:namespace />detalle_id_articulo');

                    select.val(idArticulo);

                    <portlet:namespace />cerrarAltaArticuloCompra();

                    jQuery('#<portlet:namespace />detalle_cantidad').focus();
                }
        );
    }

    function <portlet:namespace />seleccionarArticuloCompraCerrar() {
        <portlet:namespace />cerrarAltaArticuloCompra();
    }

    function <portlet:namespace />cerrarAltaArticuloCompra() {
        <portlet:namespace />popupArticuloCompraAbriendo = false;

        if (<portlet:namespace />popupArticuloCompra) {
            Liferay.Popup.close(<portlet:namespace />popupArticuloCompra);
            <portlet:namespace />popupArticuloCompra = null;
        }
    }

    function <portlet:namespace />cargarArticulosPorSectorRemoto(idSector, callback) {
        idSector = idSector == null ? '' : String(idSector);

        if (idSector == '' || !/^[0-9]+$/.test(idSector) || parseInt(idSector, 10) <= 0) {
            if (typeof callback == 'function') {
                callback();
            }

            return;
        }

        if (<portlet:namespace />articulosCompraSectorCargado[idSector]) {
            if (typeof callback == 'function') {
                callback();
            }

            return;
        }

        jQuery.ajax({
            type: 'GET',
            url: <portlet:namespace />articulosSectorURL
                    + '&sector_id=' + encodeURIComponent(idSector)
                    + '&_ts=' + new Date().getTime(),
            dataType: 'json',
            cache: false,
            success: function(data) {
                if (data && data.articulos) {
                    for (var i = 0; i < data.articulos.length; i++) {
                        var articulo = data.articulos[i];

                        <portlet:namespace />agregarOActualizarArticuloCache(
                                articulo.id,
                                articulo.descripcion,
                                articulo.sector
                        );
                    }
                }

                <portlet:namespace />articulosCompraSectorCargado[idSector] = true;

                if (typeof callback == 'function') {
                    callback();
                }
            },
            error: function() {
                alert('No se pudieron cargar los articulos del sector seleccionado.');

                if (typeof callback == 'function') {
                    callback();
                }
            }
        });
    }

    function <portlet:namespace />cargarArticulosPorSectorSiHaceFalta(sinCargaRemota, callback) {
        var select = jQuery('#<portlet:namespace />detalle_id_articulo');

        if (select.length == 0) {
            if (typeof callback == 'function') {
                callback();
            }

            return;
        }

        var sectorSeleccionado = <portlet:namespace />getSectorSeleccionadoCompra();
        var sectorSeleccionadoNum = parseInt(sectorSeleccionado, 10);

        if (!sinCargaRemota
                && sectorSeleccionado != ''
                && /^[0-9]+$/.test(sectorSeleccionado)
                && sectorSeleccionadoNum > 0
                && !<portlet:namespace />articulosCompraSectorCargado[sectorSeleccionado]) {

            select.empty();
            select.append('<option value="">Cargando articulos...</option>');
            select.attr('disabled', 'disabled');

            <portlet:namespace />cargarArticulosPorSectorRemoto(
                    sectorSeleccionado,
                    function() {
                        select.removeAttr('disabled');

                        <portlet:namespace />cargarArticulosPorSectorSiHaceFalta(true, callback);
                    }
            );

            return;
        }

        var valorActual = select.val();
        var valorActualPermitido = false;

        select.empty();
        select.append('<option value="">Seleccione...</option>');

        for (var i = 0; i < <portlet:namespace />articulosCompraCache.length; i++) {
            var articulo = <portlet:namespace />articulosCompraCache[i];
            var sectorArticuloNum = parseInt(articulo.sector, 10);

            var mostrar =
                    !isNaN(sectorSeleccionadoNum)
                    && sectorSeleccionadoNum > 0
                    && !isNaN(sectorArticuloNum)
                    && sectorArticuloNum == sectorSeleccionadoNum;

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

        if (typeof callback == 'function') {
            callback();
        }
    }

    window['<portlet:namespace />cargarArticulosPorSectorSiHaceFalta'] =
            <portlet:namespace />cargarArticulosPorSectorSiHaceFalta;

    window['<portlet:namespace />filtrarArticulosPorSector'] =
            <portlet:namespace />cargarArticulosPorSectorSiHaceFalta;

    function <portlet:namespace />limpiarEditorDetalle() {
        jQuery('#<portlet:namespace />detalle_edit_index').val('-1');
        jQuery('#<portlet:namespace />detalle_id_articulo').val('');
        jQuery('#<portlet:namespace />detalle_cantidad').val('1');
        jQuery('#<portlet:namespace />detalle_observaciones').val('');
        jQuery('#<portlet:namespace />detalle_submit').val('Agregar detalle');
        jQuery('#<portlet:namespace />detalle_cancelar').hide();

        <portlet:namespace />cargarArticulosPorSectorSiHaceFalta();
    }

    function <portlet:namespace />editarDetalleEnPantalla(index) {
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return;
        }

        jQuery('#<portlet:namespace />detalle_edit_index').val(index);

        <portlet:namespace />cargarArticulosPorSectorSiHaceFalta(
                false,
                function() {
                    jQuery('#<portlet:namespace />detalle_id_articulo').val(
                            <portlet:namespace />detalleValue(detalle.idArticulo)
                    );

                    jQuery('#<portlet:namespace />detalle_cantidad').val(
                            <portlet:namespace />detalleValue(detalle.cantidad)
                    );

                    jQuery('#<portlet:namespace />detalle_observaciones').val(
                            <portlet:namespace />detalleValue(detalle.observaciones)
                    );

                    jQuery('#<portlet:namespace />detalle_submit').val('Guardar detalle');
                    jQuery('#<portlet:namespace />detalle_cancelar').show();

                    jQuery('#<portlet:namespace />detalle_id_articulo').focus();
                }
        );
    }

    function <portlet:namespace />cancelarEdicionDetalle() {
        if (<portlet:namespace />detalleAccionEnCurso) {
            return false;
        }

        <portlet:namespace />limpiarEditorDetalle();

        return false;
    }

    function <portlet:namespace />setDetalleAccionEnCurso(activo) {
        <portlet:namespace />detalleAccionEnCurso = activo;

        var botonAgregar = jQuery('#<portlet:namespace />detalle_submit');
        var botonCancelar = jQuery('#<portlet:namespace />detalle_cancelar');

        if (botonAgregar.length > 0) {
            if (activo) {
                if (botonAgregar.attr('data-texto-original') == null
                        || botonAgregar.attr('data-texto-original') == '') {
                    botonAgregar.attr('data-texto-original', botonAgregar.val());
                }

                botonAgregar.attr('disabled', 'disabled');
                botonAgregar.val('Procesando...');
            } else {
                botonAgregar.removeAttr('disabled');

                var textoOriginal = botonAgregar.attr('data-texto-original');

                if (textoOriginal != null && textoOriginal != '') {
                    botonAgregar.val(textoOriginal);
                }

                botonAgregar.removeAttr('data-texto-original');
            }
        }

        if (botonCancelar.length > 0) {
            if (activo) {
                botonCancelar.attr('disabled', 'disabled');
            } else {
                botonCancelar.removeAttr('disabled');
            }
        }
    }

    function <portlet:namespace />liberarDetalleAccion(delay) {
        if (typeof delay == 'undefined' || delay == null) {
            delay = 0;
        }

        window.setTimeout(function() {
            <portlet:namespace />setDetalleAccionEnCurso(false);
        }, delay);

        return false;
    }

    function <portlet:namespace />existeDetalleConArticulo(idArticulo, ignorarIndex) {
        idArticulo = idArticulo == null ? '' : String(idArticulo);

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            if (typeof ignorarIndex != 'undefined'
                    && ignorarIndex != null
                    && i == ignorarIndex) {
                continue;
            }

            var detalle = <portlet:namespace />detallesCompra[i];

            if (detalle != null && String(detalle.idArticulo) == idArticulo) {
                return true;
            }
        }

        return false;
    }

    function <portlet:namespace />postDetalleServidor(cmd, idDetalle, idArticulo, cantidad, observaciones) {
        var idReq = <portlet:namespace />idRequerimientoCompraDetalle;

        if (idReq == null || idReq == '' || !/^[0-9]+$/.test(String(idReq)) || parseInt(idReq, 10) <= 0) {
            alert('Debe guardar primero la cabecera del requerimiento.');
            return <portlet:namespace />liberarDetalleAccion(0);
        }

        var form = document.createElement('form');

        form.method = 'post';
        form.action = <portlet:namespace />detalleActionURL;
        form.style.display = 'none';

        function addHidden(name, value) {
            var input = document.createElement('input');

            input.type = 'hidden';
            input.name = '<portlet:namespace />' + name;
            input.value = value == null ? '' : value;

            form.appendChild(input);
        }

        addHidden('<%= Constants.CMD %>', cmd);
        addHidden('id_requerimiento_compra', idReq);
        addHidden('id_detalle', idDetalle);
        addHidden('id_articulo', idArticulo);
        addHidden('cantidad', cantidad);
        addHidden('observaciones_detalle', observaciones);

        document.body.appendChild(form);

        form.submit();

        return false;
    }

    function <portlet:namespace />agregarOActualizarDetalle() {
        if (<portlet:namespace />detalleAccionEnCurso) {
            return false;
        }

        var idArticulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo').val());
        var articulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo option:selected').text());
        var cantidad = jQuery.trim(jQuery('#<portlet:namespace />detalle_cantidad').val());
        var observaciones = jQuery.trim(jQuery('#<portlet:namespace />detalle_observaciones').val());

        if (idArticulo == '' || !/^[0-9]+$/.test(idArticulo) || parseInt(idArticulo, 10) <= 0) {
            alert('Debe seleccionar un articulo.');
            jQuery('#<portlet:namespace />detalle_id_articulo').focus();
            return false;
        }

        if (cantidad == '' || !/^[0-9]+$/.test(cantidad) || parseInt(cantidad, 10) <= 0) {
            alert('La cantidad debe ser entera y mayor a cero.');
            jQuery('#<portlet:namespace />detalle_cantidad').focus();
            return false;
        }

        var editIndex = parseInt(
                jQuery('#<portlet:namespace />detalle_edit_index').val(),
                10
        );

        var esEdicion =
                !isNaN(editIndex)
                && editIndex >= 0
                && <portlet:namespace />detallesCompra[editIndex];

        if (<portlet:namespace />existeDetalleConArticulo(
                idArticulo,
                esEdicion ? editIndex : -1
        )) {
            alert('Ya existe un detalle cargado para este articulo. Edite la fila existente en lugar de agregar otra.');
            jQuery('#<portlet:namespace />detalle_id_articulo').focus();
            return false;
        }

        <portlet:namespace />setDetalleAccionEnCurso(true);

        var detalle = {
            id: '',
            idArticulo: idArticulo,
            articulo: articulo,
            cantidad: cantidad,
            precioUnitario: '',
            precioTotal: '',
            idPrestador: '',
            prestador: '',
            observaciones: observaciones
        };

        if (esEdicion) {
            detalle.id = <portlet:namespace />detallesCompra[editIndex].id;
        }

        /*
         * Requerimiento ya guardado:
         * el detalle se persiste con la action modular nueva.
         */
        if (<portlet:namespace />requerimientoPersistidoDetalle) {
            return <portlet:namespace />postDetalleServidor(
                    esEdicion ? 'updateItem' : 'addItem',
                    detalle.id,
                    detalle.idArticulo,
                    detalle.cantidad,
                    detalle.observaciones
            );
        }

        /*
         * Requerimiento nuevo:
         * se conserva el comportamiento anterior en memoria.
         * El saveAll guarda cabecera + detalles serializados.
         */
        if (esEdicion) {
            <portlet:namespace />detallesCompra[editIndex] = detalle;
        } else {
            <portlet:namespace />detallesCompra.push(detalle);
        }

        <portlet:namespace />limpiarEditorDetalle();
        <portlet:namespace />renderDetallesCompra();

        return <portlet:namespace />liberarDetalleAccion(300);
    }

    function <portlet:namespace />quitarDetalleEnPantalla(index) {
        if (<portlet:namespace />detalleAccionEnCurso) {
            return false;
        }

        <portlet:namespace />setDetalleAccionEnCurso(true);

        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return <portlet:namespace />liberarDetalleAccion(0);
        }

        if (!confirm('Confirma quitar el detalle?')) {
            return <portlet:namespace />liberarDetalleAccion(0);
        }

        /*
         * Requerimiento ya guardado + detalle persistido:
         * borrar inmediatamente con action modular.
         */
        if (<portlet:namespace />requerimientoPersistidoDetalle
                && detalle.id != null
                && detalle.id != ''
                && parseInt(detalle.id, 10) > 0) {

            return <portlet:namespace />postDetalleServidor(
                    'deleteItem',
                    detalle.id,
                    '',
                    '',
                    ''
            );
        }

        /*
         * Requerimiento nuevo o detalle no persistido:
         * se borra solo de memoria y luego saveAll define el estado final.
         */
        if (detalle.id != null && detalle.id != '' && parseInt(detalle.id, 10) > 0) {
            <portlet:namespace />detalleDeletedIds.push(detalle.id);
        }

        <portlet:namespace />detallesCompra.splice(index, 1);
        <portlet:namespace />limpiarEditorDetalle();
        <portlet:namespace />renderDetallesCompra();

        return <portlet:namespace />liberarDetalleAccion(700);
    }

    function <portlet:namespace />crearHiddenDetalle(name, value) {
        var form = document.getElementById('<portlet:namespace />fmCompras');

        if (!form) {
            alert('No se encontro el formulario principal para serializar detalles.');
            return false;
        }

        var payload = document.getElementById('<portlet:namespace />detalle_payload');

        if (!payload) {
            payload = form;
        }

        var input = document.createElement('input');

        input.type = 'hidden';
        input.name = '<portlet:namespace />' + name;
        input.id = '<portlet:namespace />serializado_' + name;
        input.value = value == null ? '' : value;
        input.className = '<portlet:namespace />detalle_serializado';

        payload.appendChild(input);

        return true;
    }

    function <portlet:namespace />limpiarPayloadDetallesCompra() {
        var form = jQuery('#<portlet:namespace />fmCompras');

        form.find('input[name="<portlet:namespace />detalle_count"]').remove();
        form.find('input[name="<portlet:namespace />detalle_deleted_ids"]').remove();
        form.find('input[name^="<portlet:namespace />detalle_"]').remove();

        jQuery('#<portlet:namespace />detalle_payload').empty();
    }

    function <portlet:namespace />serializarDetallesCompras() {
        var form = document.getElementById('<portlet:namespace />fmCompras');

        if (!form) {
            alert('No se encontro el formulario principal de Compras.');
            return false;
        }

        <portlet:namespace />limpiarPayloadDetallesCompra();

        if (<portlet:namespace />detallesCompra.length <= 0) {
            alert('Debe cargar al menos un detalle antes de guardar el requerimiento.');
            return false;
        }

        var articulosSerializados = {};

        for (var d = 0; d < <portlet:namespace />detallesCompra.length; d++) {
            var detalleValidacion = <portlet:namespace />detallesCompra[d];

            if (detalleValidacion == null) {
                continue;
            }

            var idArticuloValidacion = jQuery.trim(detalleValidacion.idArticulo);

            if (idArticuloValidacion == '') {
                continue;
            }

            if (articulosSerializados[idArticuloValidacion]) {
                alert(
                    'Detalle #' + (d + 1) +
                    ': el articulo ya fue cargado en otro detalle. ' +
                    'Edite la fila existente en lugar de duplicarlo.'
                );

                return false;
            }

            articulosSerializados[idArticuloValidacion] = true;
        }

        if (!<portlet:namespace />crearHiddenDetalle('detalle_count', <portlet:namespace />detallesCompra.length)) {
            return false;
        }

        if (!<portlet:namespace />crearHiddenDetalle('detalle_deleted_ids', <portlet:namespace />detalleDeletedIds.join(','))) {
            return false;
        }

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            if (typeof <portlet:namespace />capturarCotizacionDetalle == 'function') {
                <portlet:namespace />capturarCotizacionDetalle(i);
            }

            var detalle = <portlet:namespace />detallesCompra[i];
            var prefix = 'detalle_' + i + '_';

            var idArticulo = jQuery.trim(detalle.idArticulo);
            var cantidad = jQuery.trim(detalle.cantidad);

            if (idArticulo == ''
                    || !/^[0-9]+$/.test(idArticulo)
                    || parseInt(idArticulo, 10) <= 0) {

                alert('Detalle #' + (i + 1) + ': debe seleccionar un articulo.');
                return false;
            }

            if (cantidad == ''
                    || !/^[0-9]+$/.test(cantidad)
                    || parseInt(cantidad, 10) <= 0) {

                alert('Detalle #' + (i + 1) + ': la cantidad debe ser entera y mayor a cero.');
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

            if (!<portlet:namespace />crearHiddenDetalle(prefix + 'observaciones', detalle.observaciones)) {
                return false;
            }

            var precioUnitario = jQuery.trim(<portlet:namespace />detalleValue(detalle.precioUnitario));

            if (precioUnitario != '') {
                var precioParseado = <portlet:namespace />parseImporteDetalle(precioUnitario);

                if (precioParseado == null || isNaN(precioParseado) || precioParseado < 0) {
                    alert('Detalle #' + (i + 1) + ': el precio unitario debe ser mayor o igual que cero.');
                    return false;
                }
            }

            if (!<portlet:namespace />crearHiddenDetalle(prefix + 'precio_unitario_estimado', precioUnitario)) {
                return false;
            }

            var idPrestador = jQuery.trim(<portlet:namespace />detalleValue(detalle.idPrestador));

            if (idPrestador != ''
                    && (!/^[0-9]+$/.test(idPrestador) || parseInt(idPrestador, 10) <= 0)) {
                alert('Detalle #' + (i + 1) + ': debe seleccionar un prestador valido.');
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(prefix + 'id_prestador', idPrestador)) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'prestador_label',
                    <portlet:namespace />detalleValue(detalle.prestador)
            )) {
                return false;
            }
        }

        return true;
    }

    window['<portlet:namespace />serializarDetallesCompras'] =
            <portlet:namespace />serializarDetallesCompras;
</script>

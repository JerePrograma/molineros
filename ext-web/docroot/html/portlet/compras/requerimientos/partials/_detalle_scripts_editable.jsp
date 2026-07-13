<script type="text/javascript">
    var <portlet:namespace />detalleAccionEnCurso = false;
    var <portlet:namespace />popupNomencladorDetalle = null;

    var <portlet:namespace />detalleActionURL =
            '<%= detalleActionURL.toString() %>';

    var <portlet:namespace />buscarItemTecnicoURL =
            '<%= jsDetalleCompra(buscarItemTecnicoURL.toString()) %>';

    var <portlet:namespace />requerimientoPersistidoDetalle =
            <%= requerimientoPersistidoDetalle ? "true" : "false" %>;

    var <portlet:namespace />idRequerimientoCompraDetalle =
            '<%= idRequerimientoCompraDetalle %>';

    function <portlet:namespace />resolverTipoItemEditor() {
        if (typeof <portlet:namespace />esSectorFarmaciaCompra == 'function'
                && <portlet:namespace />esSectorFarmaciaCompra()) {

            return 'MEDICAMENTO';
        }

        if (typeof <portlet:namespace />esSectorNomencladorCompra == 'function'
                && <portlet:namespace />esSectorNomencladorCompra()) {

            return 'NOMENCLADOR';
        }

        return '';
    }

    function <portlet:namespace />actualizarTipoItemEditor(preservarValores) {
        var tipoItem =
                <portlet:namespace />resolverTipoItemEditor();

        jQuery('#<portlet:namespace />detalle_tipo_item').val(tipoItem);

        var tipoLabel = '-';

        if (tipoItem == 'MEDICAMENTO') {
            tipoLabel = 'Medicamento';
        } else if (tipoItem == 'NOMENCLADOR') {
            tipoLabel = 'Nomenclador';
        }

        jQuery('#<portlet:namespace />detalle_tipo_item_label').text(tipoLabel);
        jQuery('#<portlet:namespace />detalle_sector_no_admitido').toggle(tipoItem == '');

        if (tipoItem == '') {
            jQuery('#<portlet:namespace />detalle_submit').attr('disabled', 'disabled');
        } else {
            jQuery('#<portlet:namespace />detalle_submit').removeAttr('disabled');
        }

        if (tipoItem == 'MEDICAMENTO') {
            jQuery('#<portlet:namespace />detalle_bloque_medicamento').show();
            jQuery('#<portlet:namespace />detalle_bloque_nomenclador').hide();

            if (!preservarValores) {
                jQuery('#<portlet:namespace />detalle_id_prestacion').val('');
                jQuery('#<portlet:namespace />detalle_id_tipo_nomenclador').val('');
                jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val('');
                jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val('');
            }
        } else if (tipoItem == 'NOMENCLADOR') {
            jQuery('#<portlet:namespace />detalle_bloque_nomenclador').show();
            jQuery('#<portlet:namespace />detalle_bloque_medicamento').hide();

            if (!preservarValores) {
                <portlet:namespace />limpiarSeleccionMedicamento();
            }
        } else {
            jQuery('#<portlet:namespace />detalle_bloque_nomenclador').hide();
            jQuery('#<portlet:namespace />detalle_bloque_medicamento').hide();
        }

        return tipoItem;
    }

    function <portlet:namespace />limpiarSeleccionMedicamento(limpiarCriterios) {
        jQuery('#<portlet:namespace />detalle_id_medicamento').val('');
        jQuery('#<portlet:namespace />detalle_nombre_medicamento').val('');
        jQuery('#<portlet:namespace />detalle_presentacion_medicamento').val('');
        jQuery('#<portlet:namespace />id_medicamento').val('');
        jQuery('#<portlet:namespace />med_seleccionado').val('');

        if (limpiarCriterios !== false) {
            jQuery('#<portlet:namespace />troquel').val('');
            jQuery('#<portlet:namespace />nombre_medicamento').val('');
        }

        jQuery('#<portlet:namespace />divBtnBuscaMedicamento').show();
        jQuery('#divMedicamento').hide();

        return false;
    }

    function <portlet:namespace />limpiarSeleccionNomenclador(limpiarCriterios) {
        jQuery('#<portlet:namespace />detalle_id_prestacion').val('');
        jQuery('#<portlet:namespace />detalle_id_tipo_nomenclador').val('');

        if (limpiarCriterios !== false) {
            jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val('');
            jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val('');
        }

        return false;
    }

    function <portlet:namespace />buscarNomencladorDetalle() {
        var codigo = jQuery.trim(
                jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val()
        );
        var descripcion = jQuery.trim(
                jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val()
        );

        if (codigo == '' && descripcion == '') {
            alert('Ingrese codigo o descripcion.');
            return false;
        }

        <portlet:namespace />limpiarSeleccionNomenclador(false);

        if (<portlet:namespace />popupNomencladorDetalle == null) {
            <portlet:namespace />popupNomencladorDetalle = Liferay.Popup({
                title: 'Busqueda Nomenclador',
                modal: true,
                width: 700,
                onClose: function() {
                    <portlet:namespace />popupNomencladorDetalle = null;
                }
            });
        }

        var url = <portlet:namespace />buscarItemTecnicoURL
                + '&<portlet:namespace />id_requerimiento_compra='
                + encodeURIComponent(<portlet:namespace />idRequerimientoCompraDetalle)
                + '&<portlet:namespace />sector_id='
                + encodeURIComponent(<portlet:namespace />getSectorSeleccionadoCompra())
                + '&<portlet:namespace />codigo='
                + encodeURIComponent(codigo)
                + '&<portlet:namespace />descripcion='
                + encodeURIComponent(descripcion);

        jQuery(<portlet:namespace />popupNomencladorDetalle).load(url);

        return false;
    }

    function <portlet:namespace />seleccionarMedicamentoDetalle(
            idMedicamento,
            troquel,
            nombreCompleto,
            presentacion) {

        jQuery('#<portlet:namespace />detalle_id_medicamento').val(idMedicamento);
        jQuery('#<portlet:namespace />detalle_nombre_medicamento').val(nombreCompleto);
        jQuery('#<portlet:namespace />detalle_presentacion_medicamento').val(
                presentacion == null ? '' : presentacion
        );
        jQuery('#<portlet:namespace />id_medicamento').val(idMedicamento);
        jQuery('#<portlet:namespace />troquel').val(troquel);
        jQuery('#<portlet:namespace />nombre_medicamento').val(nombreCompleto);
        jQuery('#<portlet:namespace />med_seleccionado').val('1');
        jQuery('#<portlet:namespace />divBtnBuscaMedicamento').hide();
    }

    function <portlet:namespace />seleccionarNomencladorDetalle(
            idPrestacion,
            idTipoNomenclador,
            codigo,
            descripcion) {

        jQuery('#<portlet:namespace />detalle_id_prestacion').val(idPrestacion);
        jQuery('#<portlet:namespace />detalle_id_tipo_nomenclador').val(idTipoNomenclador);
        jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val(codigo);
        jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val(descripcion);

        if (<portlet:namespace />popupNomencladorDetalle) {
            Liferay.Popup.close(<portlet:namespace />popupNomencladorDetalle);
            <portlet:namespace />popupNomencladorDetalle = null;
        }
    }

    window['<portlet:namespace />seleccionarMedicamentoDetalle'] =
            <portlet:namespace />seleccionarMedicamentoDetalle;
    window['<portlet:namespace />seleccionarNomencladorDetalle'] =
            <portlet:namespace />seleccionarNomencladorDetalle;

    function <portlet:namespace />limpiarEditorDetalle() {
        jQuery('#<portlet:namespace />detalle_edit_index').val('-1');

        jQuery('#<portlet:namespace />detalle_codigo_item').val('');
        jQuery('#<portlet:namespace />detalle_descripcion_item').val('');

        <portlet:namespace />limpiarSeleccionNomenclador();
        <portlet:namespace />limpiarSeleccionMedicamento();

        jQuery('#<portlet:namespace />detalle_cantidad').val('1');
        jQuery('#<portlet:namespace />detalle_observaciones').val('');
        jQuery('#<portlet:namespace />detalle_submit').val('Agregar detalle');
        jQuery('#<portlet:namespace />detalle_cancelar').hide();

        <portlet:namespace />actualizarTipoItemEditor(false);
    }

    function <portlet:namespace />editarDetalleEnPantalla(index) {
        var detalle = <portlet:namespace />detallesCompra[index];

        if (!detalle) {
            return;
        }

        jQuery('#<portlet:namespace />detalle_edit_index').val(index);

        jQuery('#<portlet:namespace />detalle_tipo_item').val(
                <portlet:namespace />detalleValue(detalle.tipoItem)
        );

        jQuery('#<portlet:namespace />detalle_codigo_item').val(
                <portlet:namespace />detalleValue(detalle.codigoItem)
        );

        jQuery('#<portlet:namespace />detalle_descripcion_item').val(
                <portlet:namespace />detalleValue(detalle.descripcionItem)
        );

        jQuery('#<portlet:namespace />detalle_id_prestacion').val(
                <portlet:namespace />detalleValue(detalle.idPrestacion)
        );

        jQuery('#<portlet:namespace />detalle_id_tipo_nomenclador').val(
                <portlet:namespace />detalleValue(detalle.idTipoNomenclador)
        );

        jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val(
                <portlet:namespace />detalleValue(detalle.codigoNomenclador)
        );

        jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val(
                <portlet:namespace />detalleValue(detalle.descripcionNomenclador)
        );

        jQuery('#<portlet:namespace />detalle_id_medicamento').val(
                <portlet:namespace />detalleValue(detalle.idMedicamento)
        );

        jQuery('#<portlet:namespace />id_medicamento').val(
                <portlet:namespace />detalleValue(detalle.idMedicamento)
        );

        jQuery('#<portlet:namespace />troquel').val(
                <portlet:namespace />detalleValue(detalle.troquel)
        );

        jQuery('#<portlet:namespace />detalle_nombre_medicamento').val(
                <portlet:namespace />detalleValue(detalle.nombreMedicamento)
        );

        jQuery('#<portlet:namespace />nombre_medicamento').val(
                <portlet:namespace />detalleValue(detalle.nombreMedicamento)
        );

        jQuery('#<portlet:namespace />detalle_presentacion_medicamento').val('');

        jQuery('#<portlet:namespace />detalle_cantidad').val(
                <portlet:namespace />detalleValue(detalle.cantidad)
        );

        jQuery('#<portlet:namespace />detalle_observaciones').val(
                <portlet:namespace />detalleValue(detalle.observaciones)
        );

        <portlet:namespace />actualizarTipoItemEditor(true);

        jQuery('#<portlet:namespace />detalle_submit').val('Guardar detalle');
        jQuery('#<portlet:namespace />detalle_cancelar').show();

        if (detalle.tipoItem == 'MEDICAMENTO') {
            jQuery('#<portlet:namespace />med_seleccionado').val('1');
            jQuery('#<portlet:namespace />divBtnBuscaMedicamento').hide();
            jQuery('#<portlet:namespace />nombre_medicamento').focus();
        } else {
            jQuery('#<portlet:namespace />detalle_codigo_nomenclador').focus();
        }
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

    function <portlet:namespace />esEnteroPositivo(value) {
        value =
                value == null
                        ? ''
                        : jQuery.trim(String(value));

        return value != ''
                && /^[0-9]+$/.test(value)
                && parseInt(value, 10) > 0;
    }

    function <portlet:namespace />leerDetalleEditor() {
        var tipoItem =
                <portlet:namespace />actualizarTipoItemEditor(true);

        var cantidad =
                jQuery.trim(
                        jQuery('#<portlet:namespace />detalle_cantidad').val()
                );

        var observaciones =
                jQuery.trim(
                        jQuery('#<portlet:namespace />detalle_observaciones').val()
                );

        var detalle = {
            id: '',
            tipoItem: tipoItem,
            codigoItem: '',
            descripcionItem: '',

            idPrestacion: '',
            idTipoNomenclador: '',
            codigoNomenclador: '',
            descripcionNomenclador: '',

            idMedicamento: '',
            troquel: '',
            nombreMedicamento: '',

            cantidad: cantidad,
            precioUnitario: '',
            precioTotal: '',
            idPrestador: '',
            prestador: '',
            observaciones: observaciones
        };

        if (tipoItem == 'MEDICAMENTO') {
            detalle.idMedicamento =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_id_medicamento').val()
                    );

            detalle.troquel =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />troquel').val()
                    );

            detalle.nombreMedicamento =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_nombre_medicamento').val()
                    );

            detalle.codigoItem =
                    detalle.troquel != ''
                            ? detalle.troquel
                            : detalle.idMedicamento;

            detalle.descripcionItem =
                    detalle.nombreMedicamento;
        } else {
            detalle.idPrestacion =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_id_prestacion').val()
                    );

            detalle.idTipoNomenclador =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_id_tipo_nomenclador').val()
                    );

            detalle.codigoNomenclador =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_codigo_nomenclador').val()
                    );

            detalle.descripcionNomenclador =
                    jQuery.trim(
                            jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').val()
                    );

            detalle.codigoItem =
                    detalle.codigoNomenclador;

            detalle.descripcionItem =
                    detalle.descripcionNomenclador;
        }

        return detalle;
    }

    function <portlet:namespace />validarDetalleEditor(detalle) {
        if (!detalle) {
            alert('Debe informar el detalle.');
            return false;
        }

        if (detalle.tipoItem == 'MEDICAMENTO') {
            if (!<portlet:namespace />esEnteroPositivo(detalle.idMedicamento)) {
                alert('Debe seleccionar el medicamento.');
                jQuery('#<portlet:namespace />nombre_medicamento').focus();
                return false;
            }

            if (detalle.nombreMedicamento == '') {
                alert('Debe informar el nombre del medicamento.');
                jQuery('#<portlet:namespace />nombre_medicamento').focus();
                return false;
            }
        } else if (detalle.tipoItem == 'NOMENCLADOR') {
            if (!<portlet:namespace />esEnteroPositivo(detalle.idPrestacion)) {
                alert('Debe seleccionar la prestación del nomenclador.');
                jQuery('#<portlet:namespace />detalle_codigo_nomenclador').focus();
                return false;
            }

            if (!<portlet:namespace />esEnteroPositivo(detalle.idTipoNomenclador)) {
                alert('Debe informar el tipo de nomenclador.');
                jQuery('#<portlet:namespace />detalle_codigo_nomenclador').focus();
                return false;
            }

            if (detalle.codigoNomenclador == '') {
                alert('Debe informar el código de nomenclador.');
                jQuery('#<portlet:namespace />detalle_codigo_nomenclador').focus();
                return false;
            }

            if (detalle.descripcionNomenclador == '') {
                alert('Debe informar la descripción del nomenclador.');
                jQuery('#<portlet:namespace />detalle_descripcion_nomenclador').focus();
                return false;
            }
        } else {
            alert('Tipo de ítem inválido.');
            return false;
        }

        if (!<portlet:namespace />esEnteroPositivo(detalle.cantidad)) {
            alert('La cantidad debe ser entera y mayor a cero.');
            jQuery('#<portlet:namespace />detalle_cantidad').focus();
            return false;
        }

        return true;
    }

    function <portlet:namespace />postDetalleServidor(cmd, detalle) {
        var idReq = <portlet:namespace />idRequerimientoCompraDetalle;

        if (idReq == null
                || idReq == ''
                || !/^[0-9]+$/.test(String(idReq))
                || parseInt(idReq, 10) <= 0) {

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
        addHidden('id_detalle', detalle.id);
        addHidden('tipo_item', detalle.tipoItem);
        addHidden('codigo_item', detalle.codigoItem);
        addHidden('descripcion_item', detalle.descripcionItem);

        addHidden('id_prestacion', detalle.idPrestacion);
        addHidden('id_tipo_nomenclador', detalle.idTipoNomenclador);
        addHidden('codigo_nomenclador', detalle.codigoNomenclador);
        addHidden('descripcion_nomenclador', detalle.descripcionNomenclador);

        addHidden('id_medicamento', detalle.idMedicamento);
        addHidden('troquel', detalle.troquel);
        addHidden('nombre_medicamento', detalle.nombreMedicamento);

        addHidden('cantidad', detalle.cantidad);
        addHidden('observaciones_detalle', detalle.observaciones);

        document.body.appendChild(form);

        form.submit();

        return false;
    }

    function <portlet:namespace />agregarOActualizarDetalle() {
        if (<portlet:namespace />detalleAccionEnCurso) {
            return false;
        }

        var detalle =
                <portlet:namespace />leerDetalleEditor();

        if (!<portlet:namespace />validarDetalleEditor(detalle)) {
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

        <portlet:namespace />setDetalleAccionEnCurso(true);

        if (esEdicion) {
            detalle.id =
                    <portlet:namespace />detallesCompra[editIndex].id;
        }

        if (<portlet:namespace />requerimientoPersistidoDetalle) {
            return <portlet:namespace />postDetalleServidor(
                    esEdicion ? 'updateItem' : 'addItem',
                    detalle
            );
        }

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

        if (!confirm('¿Confirma quitar el detalle?')) {
            return <portlet:namespace />liberarDetalleAccion(0);
        }

        if (<portlet:namespace />requerimientoPersistidoDetalle
                && detalle.id != null
                && detalle.id != ''
                && parseInt(detalle.id, 10) > 0) {

            return <portlet:namespace />postDetalleServidor(
                    'deleteItem',
                    {
                        id: detalle.id,
                        tipoItem: '',
                        codigoItem: '',
                        descripcionItem: '',
                        idPrestacion: '',
                        idTipoNomenclador: '',
                        codigoNomenclador: '',
                        descripcionNomenclador: '',
                        idMedicamento: '',
                        troquel: '',
                        nombreMedicamento: '',
                        cantidad: '',
                        observaciones: ''
                    }
            );
        }

        if (detalle.id != null && detalle.id != '' && parseInt(detalle.id, 10) > 0) {
            <portlet:namespace />detalleDeletedIds.push(detalle.id);
        }

        <portlet:namespace />detallesCompra.splice(index, 1);
        <portlet:namespace />limpiarEditorDetalle();
        <portlet:namespace />renderDetallesCompra();

        return <portlet:namespace />liberarDetalleAccion(700);
    }

    function <portlet:namespace />crearHiddenDetalle(name, value) {
        var form =
                document.getElementById(
                        '<portlet:namespace />fmCompras'
                );

        if (!form) {
            alert(
                    'No se encontró el formulario principal '
                            + 'para serializar detalles.'
            );
            return false;
        }

        var input = document.createElement('input');

        input.type = 'hidden';
        input.name = '<portlet:namespace />' + name;
        input.id =
                '<portlet:namespace />serializado_'
                        + name;
        input.value =
                value == null
                        ? ''
                        : value;
        input.className =
                'detalle-serializado-compra';

        form.appendChild(input);

        return true;
    }

    function <portlet:namespace />limpiarPayloadDetallesCompra() {
        var form =
                jQuery(
                        '#<portlet:namespace />fmCompras'
                );

        form.find(
                'input.detalle-serializado-compra'
        ).remove();

        jQuery(
                '#<portlet:namespace />detalle_payload'
        ).empty();
    }

    function <portlet:namespace />serializarDetallesCompras() {
        var form =
                document.getElementById(
                        '<portlet:namespace />fmCompras'
                );

        if (!form) {
            alert(
                    'No se encontró el formulario principal de Compras.'
            );

            return false;
        }

        <portlet:namespace />limpiarPayloadDetallesCompra();

        if (<portlet:namespace />detallesCompra.length <= 0) {
            alert(
                    'Debe cargar al menos un detalle '
                            + 'antes de guardar el requerimiento.'
            );

            return false;
        }

        if (!<portlet:namespace />crearHiddenDetalle(
                'detalle_count',
                <portlet:namespace />detallesCompra.length
        )) {
            return false;
        }

        if (!<portlet:namespace />crearHiddenDetalle(
                'detalle_deleted_ids',
                <portlet:namespace />detalleDeletedIds.join(',')
        )) {
            return false;
        }

        if (typeof <portlet:namespace />capturarPrestadorAdjudicado
                == 'function') {

            <portlet:namespace />capturarPrestadorAdjudicado();
        }

        var idPrestadorAdjudicado =
                jQuery.trim(
                        <portlet:namespace />detalleValue(
                                <portlet:namespace />idPrestadorAdjudicado
                        )
                );

        if (idPrestadorAdjudicado != ''
                && (
                        !/^[0-9]+$/.test(
                                idPrestadorAdjudicado
                        )
                        || parseInt(
                                idPrestadorAdjudicado,
                                10
                        ) <= 0
                )) {

            alert(
                    'Debe seleccionar un prestador adjudicado válido.'
            );

            return false;
        }

        if (!<portlet:namespace />crearHiddenDetalle(
                '<%= WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO %>',
                idPrestadorAdjudicado
        )) {
            return false;
        }

        for (
            var i = 0;
            i < <portlet:namespace />detallesCompra.length;
            i++
        ) {
            var detalle =
                    <portlet:namespace />detallesCompra[i];

            if (!detalle) {
                alert(
                        'Detalle #' + (i + 1)
                                + ': no se encontró la información '
                                + 'del detalle en memoria.'
                );

                return false;
            }

            <% if (puedeCotizarDetalle) { %>
                if (typeof <portlet:namespace />capturarCotizacionDetalle
                        != 'function') {

                    alert(
                            'No se encontró la función que captura '
                                    + 'los precios de cotización.'
                    );

                    return false;
                }

                if (!<portlet:namespace />capturarCotizacionDetalle(i)) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': no se pudo leer el campo '
                                    + 'de precio unitario.'
                    );

                    return false;
                }
            <% } %>

            var prefix =
                    'detalle_' + i + '_';

            var idDetalle =
                    jQuery.trim(
                            <portlet:namespace />detalleValue(
                                    detalle.id
                            )
                    );

            var tipoItem =
                    jQuery.trim(
                            <portlet:namespace />detalleValue(
                                    detalle.tipoItem
                            )
                    );

            var cantidad =
                    jQuery.trim(
                            <portlet:namespace />detalleValue(
                                    detalle.cantidad
                            )
                    );

            if (tipoItem != 'NOMENCLADOR'
                    && tipoItem != 'MEDICAMENTO') {

                alert(
                        'Detalle #' + (i + 1)
                                + ': tipo de ítem inválido.'
                );

                return false;
            }

            if (tipoItem == 'MEDICAMENTO') {
                if (!<portlet:namespace />esEnteroPositivo(detalle.idMedicamento)) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe seleccionar el medicamento.'
                    );

                    return false;
                }

                if (jQuery.trim(
                        <portlet:namespace />detalleValue(
                                detalle.nombreMedicamento
                        )
                ) == '') {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe informar el nombre del medicamento.'
                    );

                    return false;
                }
            }

            if (tipoItem == 'NOMENCLADOR') {
                if (!<portlet:namespace />esEnteroPositivo(detalle.idPrestacion)) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe seleccionar la prestación del nomenclador.'
                    );

                    return false;
                }

                if (!<portlet:namespace />esEnteroPositivo(detalle.idTipoNomenclador)) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe informar el tipo de nomenclador.'
                    );

                    return false;
                }

                if (jQuery.trim(
                        <portlet:namespace />detalleValue(
                                detalle.codigoNomenclador
                        )
                ) == '') {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe informar el código de nomenclador.'
                    );

                    return false;
                }

                if (jQuery.trim(
                        <portlet:namespace />detalleValue(
                                detalle.descripcionNomenclador
                        )
                ) == '') {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe informar la descripción del nomenclador.'
                    );

                    return false;
                }
            }

            if (cantidad == ''
                    || !/^[0-9]+$/.test(cantidad)
                    || parseInt(cantidad, 10) <= 0) {

                alert(
                        'Detalle #' + (i + 1)
                                + ': la cantidad debe ser entera '
                                + 'y mayor a cero.'
                );

                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'id',
                    idDetalle
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'tipo_item',
                    tipoItem
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'codigo_item',
                    detalle.codigoItem
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'descripcion_item',
                    detalle.descripcionItem
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'id_prestacion',
                    detalle.idPrestacion
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'id_tipo_nomenclador',
                    detalle.idTipoNomenclador
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'codigo_nomenclador',
                    detalle.codigoNomenclador
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'descripcion_nomenclador',
                    detalle.descripcionNomenclador
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'id_medicamento',
                    detalle.idMedicamento
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'troquel',
                    detalle.troquel
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'nombre_medicamento',
                    detalle.nombreMedicamento
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'cantidad',
                    cantidad
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'observaciones',
                    detalle.observaciones
            )) {
                return false;
            }

            var precioUnitario =
                    jQuery.trim(
                            <portlet:namespace />detalleValue(
                                    detalle.precioUnitario
                            )
                    );

            if (precioUnitario != '') {
                var precioParseado =
                        <portlet:namespace />parseImporteDetalle(
                                precioUnitario
                        );

                if (precioParseado == null
                        || isNaN(precioParseado)
                        || precioParseado < 0) {

                    alert(
                            'Detalle #' + (i + 1)
                                    + ': el precio unitario debe ser '
                                    + 'mayor o igual que cero.'
                    );

                    return false;
                }
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'precio_unitario_estimado',
                    precioUnitario
            )) {
                return false;
            }

            var nombrePrecio =
                    '<portlet:namespace />'
                            + prefix
                            + 'precio_unitario_estimado';

            var hiddenPrecio =
                    form.elements[nombrePrecio];

            if (!hiddenPrecio) {
                alert(
                        'Detalle #' + (i + 1)
                                + ': no se pudo serializar '
                                + 'el precio unitario.'
                );

                return false;
            }

            if (String(hiddenPrecio.value)
                    != String(precioUnitario)) {

                alert(
                        'Detalle #' + (i + 1)
                                + ': el precio enviado no coincide '
                                + 'con el precio capturado.'
                );

                return false;
            }

            detalle.idPrestador =
                    idPrestadorAdjudicado;

            detalle.prestador =
                    <portlet:namespace />prestadorAdjudicado;

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'id_prestador',
                    idPrestadorAdjudicado
            )) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle(
                    prefix + 'prestador_label',
                    <portlet:namespace />detalleValue(
                            <portlet:namespace />prestadorAdjudicado
                    )
            )) {
                return false;
            }
        }

        return true;
    }

    window['<portlet:namespace />serializarDetallesCompras'] =
            <portlet:namespace />serializarDetallesCompras;

    jQuery(function() {
        <portlet:namespace />limpiarEditorDetalle();

        jQuery(
                '#<portlet:namespace />troquel, '
                        + '#<portlet:namespace />nombre_medicamento'
        ).bind('input keyup change', function() {
            <portlet:namespace />limpiarSeleccionMedicamento(false);
        });

        jQuery(
                '#<portlet:namespace />detalle_codigo_nomenclador, '
                        + '#<portlet:namespace />detalle_descripcion_nomenclador'
        ).bind('input keyup change', function() {
            <portlet:namespace />limpiarSeleccionNomenclador(false);
        });

        jQuery('#<portlet:namespace />sector_id, #<portlet:namespace />id_sector').bind(
                'change',
                function() {
                    <portlet:namespace />limpiarEditorDetalle();
                }
        );
    });
</script>

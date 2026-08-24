<%--
Responsabilidad:
    Implementa interacción ES5 de alta, edición y bajas diferidas de prestaciones.
Incluido desde:
    requerimiento_compra_detalle_embebido.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    detalleAccionEnCurso, popupNomencladorDetalle, detalleActionURL, buscarItemTecnicoURL, requerimientoPersistidoDetalle, idRequerimientoCompraDetalle, esDetalleMedicamentoHistorico, detalleValue, resolverTipoItemEditor, esSectorDetalleConCodigoCompra, esSectorDetalleObservacionCompra, configurarEditorMedicamentoHistorico
Efectos secundarios:
    Sólo modifica el DOM o el modelo JavaScript; no ejecuta persistencia.
--%>
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

    function <portlet:namespace />esDetalleMedicamentoHistorico(
            detalle) {

        if (!detalle) {
            return false;
        }

        var idDetalle =
                jQuery.trim(
                        <portlet:namespace />detalleValue(
                                detalle.id
                        )
                );

        return detalle.tipoItem == 'MEDICAMENTO'
                && /^[0-9]+$/.test(idDetalle)
                && parseInt(idDetalle, 10) > 0;
    }

    function <portlet:namespace />resolverTipoItemEditor() {
        if (<portlet:namespace />esSectorDetalleConCodigoCompra()) {
            return 'NOMENCLADOR';
        }

        if (<portlet:namespace />esSectorDetalleObservacionCompra()) {
            return 'OBSERVACION';
        }

        return '';
    }

    function <portlet:namespace />configurarEditorMedicamentoHistorico(
            esHistorico) {

        var codigo =
                jQuery(
                        '#<portlet:namespace />detalle_codigo_nomenclador'
                );

        var descripcion =
                jQuery(
                        '#<portlet:namespace />detalle_descripcion_nomenclador'
                );

        var acciones =
                jQuery(
                        '#<portlet:namespace />detalle_div_btn_busca_nomenclador'
                );

        var mensaje =
                jQuery(
                        '#<portlet:namespace />detalle_medicamento_historico_info'
                );

        if (esHistorico) {
            codigo.attr(
                    'readonly',
                    'readonly'
            );

            descripcion.attr(
                    'readonly',
                    'readonly'
            );

            acciones.hide();
            mensaje.show();
        } else {
            codigo.removeAttr(
                    'readonly'
            );

            descripcion.removeAttr(
                    'readonly'
            );

            acciones.show();
            mensaje.hide();
        }
    }

    function <portlet:namespace />actualizarTipoItemEditor(
            preservarValores) {

        var editIndex =
                parseInt(
                        jQuery(
                                '#<portlet:namespace />detalle_edit_index'
                        ).val(),
                        10
                );

        var detalleExistente =
                !isNaN(editIndex)
                && editIndex >= 0
                        ? <portlet:namespace />detallesCompra[editIndex]
                        : null;

        var esHistorico =
                <portlet:namespace />esDetalleMedicamentoHistorico(
                        detalleExistente
                );

        var tipoItem =
                esHistorico
                        ? 'MEDICAMENTO'
                        : <portlet:namespace />resolverTipoItemEditor();

        jQuery(
                '#<portlet:namespace />detalle_tipo_item'
        ).val(
                tipoItem
        );

        jQuery(
                '#<portlet:namespace />detalle_bloque_nomenclador'
        ).css(
                'display',
                esHistorico || tipoItem == 'NOMENCLADOR'
                        ? ''
                        : 'none'
        );

        jQuery(
                '#<portlet:namespace />detalle_fila_observaciones'
        ).css(
                'display',
                tipoItem == 'OBSERVACION'
                        ? ''
                        : 'none'
        );

        var submit =
                jQuery(
                        '#<portlet:namespace />detalle_submit'
                );

        if (tipoItem == '') {
            submit.attr('disabled', 'disabled');
        } else {
            submit.removeAttr('disabled');
        }

        if (!preservarValores
                && !esHistorico) {

            <portlet:namespace />limpiarSeleccionNomenclador();
        }

        <portlet:namespace />configurarEditorMedicamentoHistorico(
                esHistorico
        );

        return tipoItem;
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

    var <portlet:namespace />tiposNomencladorPrestacionesMedicas = [
        {
            id: '2',
                descripcion: 'NOM.NAC.PRÁCTICAS ESPECIALIZADAS'
        },
        {
            id: '3',
            descripcion: 'NOM-PROPIO'
        },
        {
            id: '4',
                descripcion: 'NOM.NAC ANÁLISIS CLÍNICOS'
        },
        {
            id: '6',
                descripcion: 'NOM.NAC QUIRÚRGICO'
        },
        {
            id: '10',
                descripcion: 'PRÓTESIS E INSUMOS'
        }
    ];

    function <portlet:namespace />normalizarSectorTipoNomenclador(
            value) {

        if (value == null
                || typeof value == 'undefined') {

            return '';
        }

        return jQuery.trim(
                String(value)
        )
                .toUpperCase()
                .replace(
                        /[\u00C1\u00C0\u00C4\u00C2]/g,
                        'A'
                )
                .replace(
                        /[\u00C9\u00C8\u00CB\u00CA]/g,
                        'E'
                )
                .replace(
                        /[\u00CD\u00CC\u00CF\u00CE]/g,
                        'I'
                )
                .replace(
                        /[\u00D3\u00D2\u00D6\u00D4]/g,
                        'O'
                )
                .replace(
                        /[\u00DA\u00D9\u00DC\u00DB]/g,
                        'U'
                )
                .replace(
                        /\s+/g,
                        ' '
                );
    }

    function <portlet:namespace />obtenerSectorDescripcionTipoNomenclador() {
        var opcion =
                jQuery(
                        '#<portlet:namespace />sector_id option:selected'
                );

        if (opcion.length == 0) {
            return '';
        }

        return <portlet:namespace />normalizarSectorTipoNomenclador(
                opcion.text()
        );
    }

    function <portlet:namespace />esSectorFarmaciaTipoNomenclador() {
        return <portlet:namespace />obtenerSectorDescripcionTipoNomenclador()
                == 'FARMACIA';
    }

    function <portlet:namespace />esSectorPrestacionesMedicasTipoNomenclador() {
        return <portlet:namespace />obtenerSectorDescripcionTipoNomenclador()
                == 'PRESTACIONES MEDICAS';
    }

    function <portlet:namespace />esTipoNomencladorPrestacionesMedicas(
            idTipoNomenclador) {

        idTipoNomenclador =
                idTipoNomenclador != null
                        ? jQuery.trim(
                                String(
                                        idTipoNomenclador
                                )
                        )
                        : '';

        return idTipoNomenclador == '4'
                || idTipoNomenclador == '2'
                || idTipoNomenclador == '10'
                || idTipoNomenclador == '6'
                || idTipoNomenclador == '3';
    }

    function <portlet:namespace />agregarOpcionTipoNomenclador(
            select,
            value,
            descripcion) {

        select.append(
                jQuery(
                        '<option></option>'
                )
                        .attr(
                                'value',
                                value
                        )
                        .text(
                                descripcion
                        )
        );
    }

    function <portlet:namespace />cargarTiposNomencladorPrestacionesMedicas(
            select) {

        select.empty();

        <portlet:namespace />agregarOpcionTipoNomenclador(
                select,
                '',
                'Seleccione...'
        );

        for (var i = 0;
                i < <portlet:namespace />tiposNomencladorPrestacionesMedicas.length;
                i++) {

            var tipo =
                    <portlet:namespace />tiposNomencladorPrestacionesMedicas[i];

            <portlet:namespace />agregarOpcionTipoNomenclador(
                    select,
                    tipo.id,
                    tipo.descripcion
            );
        }
    }

    function <portlet:namespace />cargarTipoNomencladorFarmacia(
            select) {

        select.empty();

        <portlet:namespace />agregarOpcionTipoNomenclador(
                select,
                '9',
                'MEDICAMENTOS'
        );

        select.val('9');
    }

    function <portlet:namespace />actualizarTipoNomencladorDetallePorSector(
            limpiarSeleccion) {

        var fila =
                jQuery(
                        '#<portlet:namespace />detalle_fila_tipo_nomenclador'
                );

        var select =
                jQuery(
                        '#<portlet:namespace />detalle_tipo_nomenclador_select'
                );

        var hiddenTipoReal =
                jQuery(
                        '#<portlet:namespace />detalle_id_tipo_nomenclador'
                );

        if (fila.length == 0
                || select.length == 0) {

            return;
        }

        var valorAnterior =
                jQuery.trim(
                        select.val() || ''
                );

        var tipoRealAnterior =
                hiddenTipoReal.length > 0
                        ? jQuery.trim(
                                hiddenTipoReal.val() || ''
                        )
                        : '';

        /*
         * Cuando cambia manualmente el sector, una prestación
         * previamente seleccionada deja de ser válida.
         */
        if (limpiarSeleccion) {
            <portlet:namespace />limpiarSeleccionNomenclador(
                    false
            );

            if (hiddenTipoReal.length > 0) {
                hiddenTipoReal.val('');
            }

            valorAnterior = '';
            tipoRealAnterior = '';
        }

        if (<portlet:namespace />esSectorFarmaciaTipoNomenclador()) {

            <portlet:namespace />cargarTipoNomencladorFarmacia(
                    select
            );

            /*
             * Farmacia está forzada al tipo 9.
             *
             * Se deshabilita visualmente porque el usuario
             * no puede elegir otro tipo.
             *
             * La búsqueda NO depende del submit de este select:
             * obtenerTipoNomencladorBusquedaDetalle() devuelve 9.
             */
            select.attr(
                    'disabled',
                    'disabled'
            );

            fila.show();

            return;
        }

        if (<portlet:namespace />esSectorPrestacionesMedicasTipoNomenclador()) {

            <portlet:namespace />cargarTiposNomencladorPrestacionesMedicas(
                    select
            );

            select.removeAttr(
                    'disabled'
            );

            /*
             * En edición se intenta conservar primero el tipo real
             * correspondiente al detalle ya cargado.
             */
            if (!limpiarSeleccion
                    && <portlet:namespace />esTipoNomencladorPrestacionesMedicas(
                            tipoRealAnterior
                    )) {

                select.val(
                        tipoRealAnterior
                );

            } else if (!limpiarSeleccion
                    && <portlet:namespace />esTipoNomencladorPrestacionesMedicas(
                            valorAnterior
                    )) {

                select.val(
                        valorAnterior
                );

            } else {
                select.val('');
            }

            fila.show();

            return;
        }

        /*
         * Otros sectores conservan el comportamiento existente.
         */
        select.empty();

        <portlet:namespace />agregarOpcionTipoNomenclador(
                select,
                '',
                'Seleccione...'
        );

        select.val('');

        select.attr(
                'disabled',
                'disabled'
        );

        fila.hide();
    }

    function <portlet:namespace />obtenerTipoNomencladorBusquedaDetalle() {

        if (<portlet:namespace />esSectorFarmaciaTipoNomenclador()) {
            return '9';
        }

        if (<portlet:namespace />esSectorPrestacionesMedicasTipoNomenclador()) {
            return '';
        }

        /*
         * Para Odontología, Discapacidad y demás sectores
         * no agregamos un filtro nuevo: conservan su contrato actual.
         */
        return '';
    }

    function <portlet:namespace />validarTipoNomencladorResultadoDetalle(
            idTipoNomencladorResultado) {

        if (<portlet:namespace />esSectorPrestacionesMedicasTipoNomenclador()) {
            if (!<portlet:namespace />esTipoNomencladorPrestacionesMedicas(
                    idTipoNomencladorResultado
            )) {
                alert(
                        'La prestación seleccionada no posee una '
                                + 'clasificación técnica válida.'
                );
                return false;
            }

            return true;
        }

        var idTipoEsperado =
                <portlet:namespace />obtenerTipoNomencladorBusquedaDetalle();

        if (idTipoEsperado === null) {
            return false;
        }

        /*
         * Los sectores sin filtro explícito nuevo continúan
         * utilizando la validación histórica.
         */
        if (idTipoEsperado == '') {
            return true;
        }

        var idTipoResultado =
                idTipoNomencladorResultado != null
                        ? jQuery.trim(
                                String(
                                        idTipoNomencladorResultado
                                )
                        )
                        : '';

        if (idTipoResultado != idTipoEsperado) {

            alert(
                    'El nomenclador seleccionado pertenece al Tipo '
                            + idTipoResultado
                            + ', pero la búsqueda fue realizada para el Tipo '
                            + idTipoEsperado
                            + '. Vuelva a realizar la búsqueda.'
            );

            return false;
        }

        return true;
    }

    function <portlet:namespace />validarTipoNomencladorDetalleSeleccionado() {

        var tipoItem =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_tipo_item'
                        ).val() || ''
                );

        /*
         * La validación de clasificación técnica aplica exclusivamente
         * a detalles NOMENCLADOR.
         *
         * MEDICAMENTO histórico y OBSERVACION utilizan sus
         * contratos específicos.
         */
        if (tipoItem != 'NOMENCLADOR') {
            return true;
        }

        if (<portlet:namespace />esSectorPrestacionesMedicasTipoNomenclador()) {
            var tipoTecnico =
                    jQuery.trim(
                            jQuery(
                                    '#<portlet:namespace />detalle_id_tipo_nomenclador'
                            ).val() || ''
                    );

            if (!<portlet:namespace />esTipoNomencladorPrestacionesMedicas(
                    tipoTecnico
            )) {
                alert(
                        'Debe buscar y seleccionar una prestación válida.'
                );
                return false;
            }

            return true;
        }

        var idTipoEsperado =
                <portlet:namespace />obtenerTipoNomencladorBusquedaDetalle();

        if (idTipoEsperado === null) {
            return false;
        }

        if (idTipoEsperado == '') {
            return true;
        }

        var idTipoReal =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_id_tipo_nomenclador'
                        ).val() || ''
                );

        if (idTipoReal == '') {

            alert(
                    'Debe buscar y seleccionar un nomenclador '
                            + 'de la clasificación técnica requerida.'
            );

            return false;
        }

        if (idTipoReal != idTipoEsperado) {

            alert(
                    'El nomenclador seleccionado es de Tipo '
                            + idTipoReal
                            + ' y la clasificación técnica requerida es '
                            + idTipoEsperado
                            + '. Vuelva a seleccionar el nomenclador.'
            );

            return false;
        }

        return true;
    }

    function <portlet:namespace />buscarNomencladorDetalle() {

        if (jQuery(
                '#<portlet:namespace />detalle_codigo_nomenclador'
        ).attr('readonly')) {

            return false;
        }

        var idTipoNomenclador =
                <portlet:namespace />obtenerTipoNomencladorBusquedaDetalle();

        /*
         * null significa que el sector exige una selección
         * explícita y ésta no es válida.
         */
        if (idTipoNomenclador === null) {
            return false;
        }

        var codigo =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_codigo_nomenclador'
                        ).val()
                );

        var descripcion =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_descripcion_nomenclador'
                        ).val()
                );

        if (codigo == ''
                && descripcion == '') {

            alert(
                    '<liferay-ui:message key="ingrese-parametros-busqueda" />'
            );

            return false;
        }

        /*
         * El texto ingresado ya quedó copiado en variables locales.
         *
         * Se invalida cualquier nomenclador seleccionado anteriormente.
         */
        <portlet:namespace />limpiarSeleccionNomenclador(
                false
        );

        if (<portlet:namespace />popupNomencladorDetalle == null) {

            <portlet:namespace />popupNomencladorDetalle =
                    Liferay.Popup({
                        title: 'Búsqueda Nomenclador',
                        modal: true,
                        width: 700,

                        onClose: function() {
                            <portlet:namespace />popupNomencladorDetalle =
                                    null;
                        }
                    });
        }

        var url =
                <portlet:namespace />buscarItemTecnicoURL
                + '&<portlet:namespace />id_requerimiento_compra='
                + encodeURIComponent(
                        <portlet:namespace />idRequerimientoCompraDetalle
                )
                + '&<portlet:namespace />sector_id='
                + encodeURIComponent(
                        <portlet:namespace />getSectorSeleccionadoCompra()
                )
                + '&<portlet:namespace />codigo='
                + encodeURIComponent(
                        codigo
                )
                + '&<portlet:namespace />descripcion='
                + encodeURIComponent(
                        descripcion
                );

        /*
         * Sólo agregamos el parámetro explícito cuando corresponde:
         *
         * Farmacia:
         *     9
         *
         * PRESTACIONES MÉDICAS:
         *     4, 2, 10, 6 o 3.
         *
         * Los demás sectores mantienen la búsqueda legacy.
         */
        if (idTipoNomenclador != '') {

            url +=
                    '&<portlet:namespace />id_tipo_nomenclador='
                    + encodeURIComponent(
                            idTipoNomenclador
                    );
        }

        jQuery(
                <portlet:namespace />popupNomencladorDetalle
        ).load(
                url
        );

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

        if (jQuery(
                '#<portlet:namespace />detalle_codigo_nomenclador'
        ).attr('readonly')) {

            return false;
        }

        jQuery(
                '#<portlet:namespace />detalle_tipo_item'
        ).val(
                'NOMENCLADOR'
        );

        if (!<portlet:namespace />validarTipoNomencladorResultadoDetalle(
                idTipoNomenclador
        )) {

            return false;
        }

        jQuery(
                '#<portlet:namespace />detalle_id_prestacion'
        ).val(
                idPrestacion
        );

        jQuery(
                '#<portlet:namespace />detalle_id_tipo_nomenclador'
        ).val(
                idTipoNomenclador
        );

        jQuery(
                '#<portlet:namespace />detalle_codigo_nomenclador'
        ).val(
                codigo
        );

        jQuery(
                '#<portlet:namespace />detalle_descripcion_nomenclador'
        ).val(
                descripcion
        );

        jQuery(
                '#<portlet:namespace />detalle_codigo_item'
        ).val(
                codigo
        );

        jQuery(
                '#<portlet:namespace />detalle_descripcion_item'
        ).val(
                descripcion
        );

        if (<portlet:namespace />popupNomencladorDetalle) {
            Liferay.Popup.close(
                    <portlet:namespace />popupNomencladorDetalle
            );

            <portlet:namespace />popupNomencladorDetalle =
                    null;
        }

        return false;
    }

    window['<portlet:namespace />seleccionarNomencladorDetalle'] =
            <portlet:namespace />seleccionarNomencladorDetalle;

        function <portlet:namespace />getIdSectorTipoPrestacionDetalle() {
            var idSector = jQuery.trim(
                    String(
                            jQuery('#<portlet:namespace />sector_id').val()
                                    || ''
                    )
            );

            if (idSector == '') {
                idSector = jQuery.trim(
                        String(
                                jQuery(
                                        '#<portlet:namespace />sector_id_hidden'
                                ).val() || ''
                        )
                );
            }

            return idSector;
        }

        function <portlet:namespace />actualizarTiposPrestacionDetalle(
                valorSeleccionado,
                permitirSinClasificar) {

            var select = jQuery(
                    '#<portlet:namespace />detalle_id_tipo_prestacion'
            );
            var ayuda = jQuery(
                    '#<portlet:namespace />detalle_tipo_prestacion_ayuda'
            );

            if (select.length == 0) {
                return;
            }

            var idSector =
                    <portlet:namespace />getIdSectorTipoPrestacionDetalle();
            var cantidad = 0;
            var opcionInicial = jQuery('<option></option>');

            select.empty();
            opcionInicial.attr('value', '');
            opcionInicial.text(
                    permitirSinClasificar
                            ? 'Sin clasificar (histórico)'
                            : 'Seleccione...'
            );
            select.append(opcionInicial);

            for (var i = 0;
                    i < <portlet:namespace />tiposPrestacionDetalleCache.length;
                    i++) {

                var tipo =
                        <portlet:namespace />tiposPrestacionDetalleCache[i];

                if (String(tipo.idSector) != idSector) {
                    continue;
                }

                var opcionTipo = jQuery('<option></option>');
                opcionTipo.attr('value', tipo.id);
                opcionTipo.text(tipo.descripcion);
                select.append(opcionTipo);
                cantidad++;
            }

            if (cantidad == 0) {
                select.attr('disabled', 'disabled');
                ayuda.text(
                        'No hay tipos disponibles para este sector.'
                ).show();
                return;
            }

            select.removeAttr('disabled');
            ayuda.hide().text('');
            select.val(
                    valorSeleccionado == null
                            ? ''
                            : String(valorSeleccionado)
            );
        }

        window['<portlet:namespace />actualizarTiposPrestacionDetalle'] =
                <portlet:namespace />actualizarTiposPrestacionDetalle;

        function <portlet:namespace />esTipoPrestacionDetalleValidoParaSector(
                idTipoPrestacion) {

            var idSector =
                    <portlet:namespace />getIdSectorTipoPrestacionDetalle();
            var idTipo = idTipoPrestacion == null
                    ? ''
                    : String(idTipoPrestacion);

            for (var i = 0;
                    i < <portlet:namespace />tiposPrestacionDetalleCache.length;
                    i++) {

                var tipo =
                        <portlet:namespace />tiposPrestacionDetalleCache[i];

                if (String(tipo.idSector) == idSector
                        && String(tipo.id) == idTipo) {

                    return true;
                }
            }

            return false;
        }

        function <portlet:namespace />limpiarEditorDetalle() {
            jQuery(
                    '#<portlet:namespace />detalle_edit_index'
            ).val(
                    '-1'
            );

            jQuery(
                    '#<portlet:namespace />detalle_tipo_item'
            ).val(
                    ''
            );

            jQuery(
                    '#<portlet:namespace />detalle_codigo_item'
            ).val(
                    ''
            );

            jQuery(
                    '#<portlet:namespace />detalle_descripcion_item'
            ).val(
                    ''
            );

            <portlet:namespace />limpiarSeleccionNomenclador();

            jQuery(
                    '#<portlet:namespace />detalle_cantidad'
            ).val(
                    '1'
            );

            jQuery(
                    '#<portlet:namespace />detalle_observaciones'
            ).val(
                    ''
            );

            jQuery(
                    '#<portlet:namespace />detalle_submit'
            ).val(
                    'Agregar detalle'
            );

            jQuery(
                    '#<portlet:namespace />detalle_cancelar'
            ).hide();

            <portlet:namespace />actualizarTipoItemEditor(false);
            <portlet:namespace />actualizarTiposPrestacionDetalle('', false);
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

        <portlet:namespace />actualizarTiposPrestacionDetalle(
                detalle.idTipoPrestacion,
                <portlet:namespace />detalleValue(
                        detalle.idTipoPrestacion
                ) == ''
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

        <portlet:namespace />actualizarTipoNomencladorDetallePorSector(
                false
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
        var editIndex =
                parseInt(
                        jQuery(
                                '#<portlet:namespace />detalle_edit_index'
                        ).val(),
                        10
                );

        var detalleExistente =
                !isNaN(editIndex)
                && editIndex >= 0
                        ? <portlet:namespace />detallesCompra[editIndex]
                        : null;

        var esHistorico =
                <portlet:namespace />esDetalleMedicamentoHistorico(
                        detalleExistente
                );

        var tipoEsperado =
                <portlet:namespace />resolverTipoItemEditor();

        var cantidad =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_cantidad'
                        ).val()
                );

        var observaciones =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_observaciones'
                        ).val()
                );

        var detalle = {
            id:
                    detalleExistente
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.id
                            )
                            : '',

            tipoItem:
                    esHistorico
                            ? 'MEDICAMENTO'
                            : tipoEsperado,

            idTipoPrestacion:
                    jQuery.trim(
                            jQuery(
                                    '#<portlet:namespace />detalle_id_tipo_prestacion'
                            ).val() || ''
                    ),

            tipoPrestacion:
                    jQuery.trim(
                            jQuery(
                                    '#<portlet:namespace />detalle_id_tipo_prestacion option:selected'
                            ).text() || ''
                    ),

            codigoItem: '',
            descripcionItem: '',

            idPrestacion: '',
            idTipoNomenclador: '',
            codigoNomenclador: '',
            descripcionNomenclador: '',

            /*
             * Se conservan en memoria para mostrar históricos,
             * pero ya no se serializan desde el navegador.
             */
            idMedicamento:
                    esHistorico
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.idMedicamento
                            )
                            : '',

            troquel:
                    esHistorico
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.troquel
                            )
                            : '',

            nombreMedicamento:
                    esHistorico
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.nombreMedicamento
                            )
                            : '',

            cantidad: cantidad,

            precioUnitario:
                    detalleExistente
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.precioUnitario
                            )
                            : '',

            precioTotal:
                    detalleExistente
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.precioTotal
                            )
                            : '',

            idPrestador:
                    detalleExistente
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.idPrestador
                            )
                            : '',

            prestador:
                    detalleExistente
                            ? <portlet:namespace />detalleValue(
                                    detalleExistente.prestador
                            )
                            : '',

            observaciones: observaciones
        };

        if (esHistorico) {
            detalle.codigoItem =
                    <portlet:namespace />detalleValue(
                            detalleExistente.codigoItem
                    );

            detalle.descripcionItem =
                    <portlet:namespace />detalleValue(
                            detalleExistente.descripcionItem
                    );

            return detalle;
        }

        if (tipoEsperado == 'OBSERVACION') {
            return detalle;
        }

        detalle.idPrestacion =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_id_prestacion'
                        ).val()
                );

        detalle.idTipoNomenclador =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_id_tipo_nomenclador'
                        ).val()
                );

        detalle.codigoNomenclador =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_codigo_nomenclador'
                        ).val()
                );

        detalle.descripcionNomenclador =
                jQuery.trim(
                        jQuery(
                                '#<portlet:namespace />detalle_descripcion_nomenclador'
                        ).val()
                );

        detalle.codigoItem =
                detalle.codigoNomenclador;

        detalle.descripcionItem =
                detalle.descripcionNomenclador;

        return detalle;
    }

    function <portlet:namespace />validarDetalleEditor(
            detalle) {

        if (!detalle) {
            alert(
                    'Debe informar el detalle.'
            );

            return false;
        }

        var selectTipoPrestacion = jQuery(
                '#<portlet:namespace />detalle_id_tipo_prestacion'
        );

        if (!selectTipoPrestacion.attr('disabled')
                && !<portlet:namespace />esTipoPrestacionDetalleValidoParaSector(
                        detalle.idTipoPrestacion
                )
                && !<portlet:namespace />esEnteroPositivo(detalle.id)) {

            alert('Debe seleccionar el Tipo.');
            selectTipoPrestacion.focus();
            return false;
        }

        if (detalle.tipoItem == 'MEDICAMENTO') {
            if (!<portlet:namespace />esEnteroPositivo(
                    detalle.id
            )) {
                alert(
                        'No se pueden crear nuevos detalles '
                                + 'de medicamento.'
                );

                return false;
            }

            if (detalle.codigoItem == ''
                    || detalle.descripcionItem == '') {

                alert(
                        'El detalle histórico de medicamento '
                                + 'no conserva Código o Descripción.'
                );

                return false;
            }
        } else if (detalle.tipoItem == 'NOMENCLADOR') {
            if (!<portlet:namespace />esEnteroPositivo(
                    detalle.idPrestacion
            )) {
                alert(
                        'Debe seleccionar el Código Presentado.'
                );

                jQuery(
                        '#<portlet:namespace />detalle_codigo_nomenclador'
                ).focus();

                return false;
            }

            if (!<portlet:namespace />esEnteroPositivo(
                    detalle.idTipoNomenclador
            )) {
                alert(
                        'La selección no contiene un tipo '
                                + 'de nomenclador válido.'
                );

                return false;
            }

            if (detalle.codigoNomenclador == '') {
                alert(
                        'Debe informar el Código Presentado.'
                );

                jQuery(
                        '#<portlet:namespace />detalle_codigo_nomenclador'
                ).focus();

                return false;
            }

            if (detalle.descripcionNomenclador == '') {
                alert(
                        'Debe informar la Descripción.'
                );

                jQuery(
                        '#<portlet:namespace />detalle_descripcion_nomenclador'
                ).focus();

                return false;
            }
        } else if (detalle.tipoItem == 'OBSERVACION') {
            if (detalle.observaciones == '') {
                alert(
                        'Debe informar las Observaciones.'
                );

                jQuery(
                        '#<portlet:namespace />detalle_observaciones'
                ).focus();

                return false;
            }
        } else {
            alert(
                    'Tipo de ítem inválido.'
            );

            return false;
        }

        if (!<portlet:namespace />esEnteroPositivo(
                detalle.cantidad
        )) {
            alert(
                    'La cantidad debe ser entera y mayor a cero.'
            );

            jQuery(
                    '#<portlet:namespace />detalle_cantidad'
            ).focus();

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
        addHidden('id_tipo_prestacion', detalle.idTipoPrestacion);
        addHidden('codigo_item', detalle.codigoItem);
        addHidden('descripcion_item', detalle.descripcionItem);

        addHidden('id_prestacion', detalle.idPrestacion);
        addHidden('id_tipo_nomenclador', detalle.idTipoNomenclador);
        addHidden('codigo_nomenclador', detalle.codigoNomenclador);
        addHidden('descripcion_nomenclador', detalle.descripcionNomenclador);

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

        if (!<portlet:namespace />validarTipoNomencladorDetalleSeleccionado()) {
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

        if (typeof window[
                '<portlet:namespace />actualizarEstadoItemsHistoricosAfiliado'
        ] == 'function') {

            window[
                    '<portlet:namespace />actualizarEstadoItemsHistoricosAfiliado'
            ]();
        }

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

        if (!<%= puedeCotizarDetalle ? "true" : "false" %>
                && <portlet:namespace />detallesCompra.length <= 1) {

            alert(
                    'El requerimiento debe conservar al menos una prestación.'
            );

            return <portlet:namespace />liberarDetalleAccion(0);
        }

        if (!confirm('¿Confirma quitar el detalle?')) {
            return <portlet:namespace />liberarDetalleAccion(0);
        }

        if (!<%= puedeCotizarDetalle ? "true" : "false" %>
                && <portlet:namespace />requerimientoPersistidoDetalle
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

        if (detalle.id != null
                && detalle.id != ''
                && parseInt(detalle.id, 10) > 0
                && jQuery.inArray(
                        String(detalle.id),
                        <portlet:namespace />detalleDeletedIds
                ) < 0) {

            <portlet:namespace />detalleDeletedIds.push(
                    String(detalle.id)
            );
        }

        <portlet:namespace />detallesCompra.splice(index, 1);
        <portlet:namespace />limpiarEditorDetalle();
        <portlet:namespace />renderDetallesCompra();

        if (typeof window[
                '<portlet:namespace />actualizarEstadoItemsHistoricosAfiliado'
        ] == 'function') {

            window[
                    '<portlet:namespace />actualizarEstadoItemsHistoricosAfiliado'
            ]();
        }

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
                    <% if (puedeCotizarDetalle) { %>
                        'Debe conservar al menos una prestación '
                                + 'antes de guardar la cotización.'
                    <% } else { %>
                        'Debe cargar al menos un detalle '
                                + 'antes de guardar el requerimiento.'
                    <% } %>
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

            var esMedicamentoHistorico =
                    tipoItem == 'MEDICAMENTO'
                    && /^[0-9]+$/.test(idDetalle)
                    && parseInt(idDetalle, 10) > 0;

            if (tipoItem != 'NOMENCLADOR'
                    && tipoItem != 'OBSERVACION'
                    && !esMedicamentoHistorico) {

                alert(
                        'Detalle #' + (i + 1)
                                + ': tipo de ítem inválido.'
                );

                return false;
            }

            if (tipoItem == 'NOMENCLADOR') {
                if (!<portlet:namespace />esEnteroPositivo(
                        detalle.idPrestacion
                )) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe seleccionar '
                                    + 'el Código Presentado.'
                    );

                    return false;
                }

                if (!<portlet:namespace />esEnteroPositivo(
                        detalle.idTipoNomenclador
                )) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': la selección no contiene '
                                    + 'un tipo de nomenclador válido.'
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
                                    + ': debe informar '
                                    + 'el Código Presentado.'
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
                                    + ': debe informar '
                                    + 'la Descripción.'
                    );

                    return false;
                }
            }

            if (tipoItem == 'OBSERVACION'
                    && jQuery.trim(
                            <portlet:namespace />detalleValue(
                                    detalle.observaciones
                            )
                    ) == '') {

                alert(
                        'Detalle #' + (i + 1)
                                + ': debe informar las Observaciones.'
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

            if (idDetalle == ''
                    && !<portlet:namespace />esTipoPrestacionDetalleValidoParaSector(
                            detalle.idTipoPrestacion
                    )) {

                var tieneTiposSector = false;

                for (var j = 0;
                        j < <portlet:namespace />tiposPrestacionDetalleCache.length;
                        j++) {

                    if (String(
                            <portlet:namespace />tiposPrestacionDetalleCache[j].idSector
                    ) == <portlet:namespace />getIdSectorTipoPrestacionDetalle()) {

                        tieneTiposSector = true;
                        break;
                    }
                }

                if (tieneTiposSector) {
                    alert(
                            'Detalle #' + (i + 1)
                                    + ': debe seleccionar el Tipo.'
                    );
                    return false;
                }
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
                    prefix + 'id_tipo_prestacion',
                    detalle.idTipoPrestacion
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

    function <portlet:namespace />filtrarArticulosPorSector() {
        <portlet:namespace />limpiarEditorDetalle();
        <portlet:namespace />renderDetallesCompra();
    }

    window['<portlet:namespace />filtrarArticulosPorSector'] =
            <portlet:namespace />filtrarArticulosPorSector;

    jQuery(function() {

        <portlet:namespace />limpiarEditorDetalle();

        /*
         * El sector puede venir seleccionado desde el render inicial.
         * Inicializamos la clasificación técnica interna sin limpiar
         * datos por un supuesto cambio de sector.
         */
        <portlet:namespace />actualizarTipoNomencladorDetallePorSector(
                false
        );

        jQuery(
                '#<portlet:namespace />detalle_tipo_nomenclador_select'
        ).change(function() {

            var teniaNomencladorSeleccionado =
                    jQuery.trim(
                            jQuery(
                                    '#<portlet:namespace />detalle_id_prestacion'
                            ).val() || ''
                    ) != '';

            /*
             * Si existía una prestación seleccionada, también se
             * eliminan Código y Descripción porque pertenecían al
             * catálogo técnico anterior.
             *
             * Si sólo eran criterios todavía no seleccionados,
             * se conservan para permitir repetir la búsqueda
             * bajo el nuevo Tipo.
             */
            <portlet:namespace />limpiarSeleccionNomenclador(
                    teniaNomencladorSeleccionado
            );
        });

        jQuery(
                '#<portlet:namespace />detalle_codigo_nomenclador, '
                        + '#<portlet:namespace />detalle_descripcion_nomenclador'
        ).bind(
                'input keyup change',
                function() {

                    <portlet:namespace />limpiarSeleccionNomenclador(
                            false
                    );
                }
        );
    });
</script>

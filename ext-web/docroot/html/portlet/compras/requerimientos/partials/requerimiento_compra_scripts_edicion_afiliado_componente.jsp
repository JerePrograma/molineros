<%--
Responsabilidad:
    Declara en ES5 la selección del afiliado y sus ítems históricos.
Incluido desde:
    requerimiento_compra_scripts_edicion_runtime_componente.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; lectura de contexto en edición sin alterar la identidad persistida.
Entradas requeridas:
    Atributos compras.requerimiento.* publicados por el Action/Helper.
Atributos de request consumidos:
    compras.requerimiento.sectores y los inicializados por el runtime común.
Parámetros consumidos:
    Ninguno directamente.
IDs o funciones JavaScript expuestos:
    guardandoCompra, itemsHistoricosAfiliado, buscarAfiliados, seleccionaAfiliado.
Efectos secundarios:
    Modifica únicamente el modelo JavaScript y el DOM del formulario.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_javascript_helper_componente.jsp" %>
<%
List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) request.getAttribute(
                "compras.requerimiento.sectores"
        );
%>
<portlet:renderURL
        var="comprasBuscarAfiliadosURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/buscar_afiliados" />

</portlet:renderURL>
<portlet:renderURL
        var="comprasBuscarItemsHistoricosAfiliadoURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/buscar_items_historicos_afiliado" />

</portlet:renderURL>
<script type="text/javascript">
    var popup = null;
    var popupAfill = null;
    var <portlet:namespace />guardandoCompra = false;

    var <portlet:namespace />itemsHistoricosAfiliadoSecuencia =
            0;

    var <portlet:namespace />itemsHistoricosAfiliadoRequest =
            null;

    var <portlet:namespace />itemsHistoricosAfiliado =
            [];

    function <portlet:namespace />setGuardandoCompraActivo(activo) {
        <portlet:namespace />guardandoCompra = activo;

        var botonGuardar = document.getElementById('<portlet:namespace />btnGuardarCompras');

        if (botonGuardar) {
            if (activo) {
                botonGuardar.disabled = true;
                botonGuardar.setAttribute('disabled', 'disabled');
                botonGuardar.value = 'Guardando...';

                jQuery(botonGuardar).addClass('compras-btn-guardando');
            } else {
                botonGuardar.disabled = false;
                botonGuardar.removeAttribute('disabled');
                botonGuardar.value = 'Guardar';

                jQuery(botonGuardar).removeClass('compras-btn-guardando');
            }
        }
    }

    function <portlet:namespace />cancelarGuardadoCompra() {
        <portlet:namespace />setGuardandoCompraActivo(false);
        return false;
    }

    function <portlet:namespace />focusSeguroCompra(selector) {
        setTimeout(function() {
            jQuery(selector).focus();
        }, 200);
    }

    var <portlet:namespace />sectorRequiereAfiliadoMap = {};

    <%
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());
        String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
    %>
        <portlet:namespace />sectorRequiereAfiliadoMap['<%= sectorId %>'] = <%= requiereAfiliado %>;
    <%
    }
    %>

    function <portlet:namespace />valorSeguroAfiliado(value) {
        if (value == null || typeof value == 'undefined' || value == 'null') {
            return '';
        }

        return value;
    }

    function <portlet:namespace />fechaReferenciaAfiliado() {
        var d = new Date();
        var currDate = d.getDate();
        var currMonth = d.getMonth() + 1;
        var currYear = d.getFullYear();

        return currDate + '/' + currMonth + '/' + currYear;
    }

    function <portlet:namespace />trimValue(id) {
        var input = jQuery('#<portlet:namespace />' + id);

        if (input.length == 0) {
            return '';
        }

        return jQuery.trim(input.val());
    }

    function <portlet:namespace />valorAfiliado(id) {
        return <portlet:namespace />trimValue(id);
    }

    function <portlet:namespace />valorCredencialAfiliado() {
        return jQuery('#<portlet:namespace />' + 'num' + 'ero_afi').val();
    }

    function <portlet:namespace />paramCredencialAfiliado() {
        return 'num' + 'ero_afi';
    }

    function <portlet:namespace />invalidarConsultaItemsHistoricosAfiliado() {

        <portlet:namespace />itemsHistoricosAfiliadoSecuencia++;

        if (<portlet:namespace />itemsHistoricosAfiliadoRequest
                && <portlet:namespace />itemsHistoricosAfiliadoRequest
                        .readyState != 4) {

            try {
                <portlet:namespace />itemsHistoricosAfiliadoRequest
                        .abort();

            } catch (e) {
                /*
                 * El aborto es esperado cuando cambia
                 * el afiliado o el sector.
                 */
            }
        }

        <portlet:namespace />itemsHistoricosAfiliadoRequest =
                null;
    }


    function <portlet:namespace />limpiarVisualItemsHistoricosAfiliado() {

        <portlet:namespace />itemsHistoricosAfiliado =
                [];

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_estado'
        )
                .text('')
                .hide();

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_body'
        ).empty();

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_tabla'
        ).hide();

        var seleccionarTodos =
                document.getElementById(
                        '<portlet:namespace />items_historicos_afiliado_seleccionar_todos'
                );

        if (seleccionarTodos) {
            seleccionarTodos.checked =
                    false;

            seleccionarTodos.disabled =
                    true;
        }

        var botonAgregar =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_agregar'
                );

        if (botonAgregar.length > 0) {
            botonAgregar
                    .attr(
                            'disabled',
                            'disabled'
                    )
                    .val(
                            'Agregar'
                    );
        }

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_acciones'
        ).hide();

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_panel'
        ).hide();
    }

    function <portlet:namespace />ocultarItemsHistoricosAfiliado() {

        <portlet:namespace />invalidarConsultaItemsHistoricosAfiliado();

        <portlet:namespace />limpiarVisualItemsHistoricosAfiliado();
    }


    function <portlet:namespace />claveItemHistoricoAfiliado(
            item) {

        if (!item) {
            return '';
        }

        var idPrestacion =
                item.idPrestacion != null
                        ? jQuery.trim(
                                String(
                                        item.idPrestacion
                                )
                        )
                        : '';

        var idTipoNomenclador =
                item.idTipoNomenclador != null
                        ? jQuery.trim(
                                String(
                                        item.idTipoNomenclador
                                )
                        )
                        : '';

        if (!/^[0-9]+$/.test(idPrestacion)
                || parseInt(idPrestacion, 10) <= 0
                || !/^[0-9]+$/.test(idTipoNomenclador)
                || parseInt(idTipoNomenclador, 10) <= 0) {

            return '';
        }

        return idPrestacion
                + ':'
                + idTipoNomenclador;
    }


    function <portlet:namespace />clavesDetallesActuales() {

        var claves =
                {};

        var detalles =
                typeof <portlet:namespace />detallesCompra
                        != 'undefined'
                && <portlet:namespace />detallesCompra
                        ? <portlet:namespace />detallesCompra
                        : [];

        for (var i = 0; i < detalles.length; i++) {
            var detalle =
                    detalles[i];

            if (!detalle
                    || detalle.tipoItem != 'NOMENCLADOR') {

                continue;
            }

            var clave =
                    <portlet:namespace />claveItemHistoricoAfiliado({
                        idPrestacion:
                                detalle.idPrestacion,

                        idTipoNomenclador:
                                detalle.idTipoNomenclador
                    });

            if (clave != '') {
                claves[clave] =
                        true;
            }
        }

        return claves;
    }


    function <portlet:namespace />descripcionTipoItemHistoricoAfiliado(
            idTipoNomenclador) {

        var id =
                idTipoNomenclador != null
                        ? jQuery.trim(
                                String(
                                        idTipoNomenclador
                                )
                        )
                        : '';

        if (id == '9') {
            return 'MEDICAMENTOS';
        }

        if (typeof <portlet:namespace />tiposNomencladorPrestacionesMedicas
                != 'undefined'
                && <portlet:namespace />tiposNomencladorPrestacionesMedicas) {

            for (var i = 0;
                    i < <portlet:namespace />tiposNomencladorPrestacionesMedicas
                            .length;
                    i++) {

                var tipo =
                        <portlet:namespace />tiposNomencladorPrestacionesMedicas[i];

                if (tipo
                        && String(tipo.id) == id) {

                    return tipo.descripcion;
                }
            }
        }

        return id != ''
                ? 'Tipo ' + id
                : '';
    }

    function <portlet:namespace />actualizarSeleccionItemsHistoricosAfiliado() {

        var body =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_body'
                );

        var checks =
                body.find(
                        'input.compras-item-historico-check'
                );

        var cantidadHabilitados =
                0;

        var cantidadSeleccionados =
                0;

        checks.each(function() {

            if (this.disabled) {
                return;
            }

            cantidadHabilitados++;

            if (this.checked) {
                cantidadSeleccionados++;
            }
        });

        var seleccionarTodos =
                document.getElementById(
                        '<portlet:namespace />items_historicos_afiliado_seleccionar_todos'
                );

        if (seleccionarTodos) {
            seleccionarTodos.disabled =
                    cantidadHabilitados <= 0;

            seleccionarTodos.checked =
                    cantidadHabilitados > 0
                    && cantidadSeleccionados
                            == cantidadHabilitados;
        }

        var botonAgregar =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_agregar'
                );

        if (botonAgregar.length > 0) {

            if (cantidadSeleccionados > 0) {
                botonAgregar.removeAttr(
                        'disabled'
                );
            } else {
                botonAgregar.attr(
                        'disabled',
                        'disabled'
                );
            }
        }
    }


    function <portlet:namespace />seleccionarTodosItemsHistoricosAfiliado(
            seleccionar) {

        var body =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_body'
                );

        var checks =
                body.find(
                        'input.compras-item-historico-check'
                );

        checks.each(function() {

            if (!this.disabled) {
                this.checked =
                        seleccionar
                                ? true
                                : false;
            }
        });

        <portlet:namespace />actualizarSeleccionItemsHistoricosAfiliado();
    }


    function <portlet:namespace />crearDetalleDesdeItemHistoricoAfiliado(
            item) {

        if (!item) {
            return null;
        }

        var idPrestacion =
                item.idPrestacion != null
                        ? jQuery.trim(
                                String(
                                        item.idPrestacion
                                )
                        )
                        : '';

        var idTipoNomenclador =
                item.idTipoNomenclador != null
                        ? jQuery.trim(
                                String(
                                        item.idTipoNomenclador
                                )
                        )
                        : '';

        var codigo =
                item.codigo != null
                        ? jQuery.trim(
                                String(
                                        item.codigo
                                )
                        )
                        : '';

        var descripcion =
                item.descripcion != null
                        ? jQuery.trim(
                                String(
                                        item.descripcion
                                )
                        )
                        : '';

        var idTipoPrestacion = jQuery.trim(
                String(
                        jQuery(
                                '#<portlet:namespace />detalle_id_tipo_prestacion'
                        ).val() || ''
                )
        );

        var tipoPrestacion = jQuery.trim(
                String(
                        jQuery(
                                '#<portlet:namespace />detalle_id_tipo_prestacion option:selected'
                        ).text() || ''
                )
        );

        if (!/^[0-9]+$/.test(
                idPrestacion
        )
                || parseInt(
                        idPrestacion,
                        10
                ) <= 0) {

            return null;
        }

        if (!/^[0-9]+$/.test(
                idTipoNomenclador
        )
                || parseInt(
                        idTipoNomenclador,
                        10
                ) <= 0) {

            return null;
        }

        if (codigo == ''
                || descripcion == '') {

            return null;
        }

        return {
            id:
                    '',

            tipoItem:
                    'NOMENCLADOR',

            idTipoPrestacion:
                    idTipoPrestacion,

            tipoPrestacion:
                    tipoPrestacion,

            codigoItem:
                    codigo,

            descripcionItem:
                    descripcion,

            idPrestacion:
                    idPrestacion,

            idTipoNomenclador:
                    idTipoNomenclador,

            codigoNomenclador:
                    codigo,

            descripcionNomenclador:
                    descripcion,

            idMedicamento:
                    '',

            troquel:
                    '',

            nombreMedicamento:
                    '',

            cantidad:
                    '1',

            precioUnitario:
                    '',

            precioTotal:
                    '',

            idPrestador:
                    '',

            prestador:
                    '',

            observaciones:
                    ''
        };
    }


    function <portlet:namespace />obtenerDetallesHistoricosSeleccionados() {

        var detallesSeleccionados =
                [];

        var clavesActuales =
                <portlet:namespace />clavesDetallesActuales();

        var clavesSeleccionadas =
                {};

        var body =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_body'
                );

        var checks =
                body.find(
                        'input.compras-item-historico-check'
                );

        checks.each(function() {

            if (this.disabled
                    || !this.checked) {

                return;
            }

            var index =
                    parseInt(
                            this.value,
                            10
                    );

            if (isNaN(index)
                    || index < 0
                    || index >= <portlet:namespace />itemsHistoricosAfiliado.length) {

                return;
            }

            var item =
                    <portlet:namespace />itemsHistoricosAfiliado[index];

            var clave =
                    <portlet:namespace />claveItemHistoricoAfiliado(
                            item
                    );

            if (clave == ''
                    || clavesActuales[clave] === true
                    || clavesSeleccionadas[clave] === true) {

                return;
            }

            var detalle =
                    <portlet:namespace />crearDetalleDesdeItemHistoricoAfiliado(
                            item
                    );

            if (!detalle) {
                return;
            }

            detallesSeleccionados.push(
                    detalle
            );

            clavesSeleccionadas[clave] =
                    true;
        });

        return detallesSeleccionados;
    }


    function <portlet:namespace />postItemsHistoricosAfiliadoServidor(
            detalles) {

        if (!detalles
                || detalles.length <= 0) {

            return false;
        }

        if (typeof <portlet:namespace />detalleActionURL
                == 'undefined'
                || <portlet:namespace />detalleActionURL == null
                || jQuery.trim(
                        String(
                                <portlet:namespace />detalleActionURL
                        )
                ) == '') {

            alert(
                    'No se encontró la URL para agregar '
                            + 'los detalles seleccionados.'
            );

            return false;
        }

        var idReq =
                typeof <portlet:namespace />idRequerimientoCompraDetalle
                        != 'undefined'
                        && <portlet:namespace />idRequerimientoCompraDetalle != null
                        ? jQuery.trim(
                                String(
                                        <portlet:namespace />idRequerimientoCompraDetalle
                                )
                        )
                        : '';

        if (!/^[0-9]+$/.test(
                idReq
        )
                || parseInt(
                        idReq,
                        10
                ) <= 0) {

            alert(
                    'Debe guardar primero la cabecera '
                            + 'del requerimiento.'
            );

            return false;
        }

        var form =
                document.createElement(
                        'form'
                );

        form.method =
                'post';

        form.action =
                <portlet:namespace />detalleActionURL;

        form.style.display =
                'none';

        function addHidden(
                name,
                value) {

            var input =
                    document.createElement(
                            'input'
                    );

            input.type =
                    'hidden';

            input.name =
                    '<portlet:namespace />'
                            + name;

            input.value =
                    value == null
                            ? ''
                            : value;

            form.appendChild(
                    input
            );
        }

        addHidden(
                'cmd',
                'addItems'
        );

        addHidden(
                'id_requerimiento_compra',
                idReq
        );

        addHidden(
                'detalle_count',
                detalles.length
        );

        addHidden(
                'detalle_deleted_ids',
                ''
        );

        for (var i = 0;
                i < detalles.length;
                i++) {

            var detalle =
                    detalles[i];

            var prefix =
                    'detalle_'
                            + i
                            + '_';

            addHidden(
                    prefix + 'id',
                    ''
            );

            addHidden(
                    prefix + 'tipo_item',
                    detalle.tipoItem
            );

            addHidden(
                    prefix + 'id_tipo_prestacion',
                    detalle.idTipoPrestacion
            );

            addHidden(
                    prefix + 'codigo_item',
                    detalle.codigoItem
            );

            addHidden(
                    prefix + 'descripcion_item',
                    detalle.descripcionItem
            );

            addHidden(
                    prefix + 'id_prestacion',
                    detalle.idPrestacion
            );

            addHidden(
                    prefix + 'id_tipo_nomenclador',
                    detalle.idTipoNomenclador
            );

            addHidden(
                    prefix + 'codigo_nomenclador',
                    detalle.codigoNomenclador
            );

            addHidden(
                    prefix + 'descripcion_nomenclador',
                    detalle.descripcionNomenclador
            );

            addHidden(
                    prefix + 'id_medicamento',
                    ''
            );

            addHidden(
                    prefix + 'troquel',
                    ''
            );

            addHidden(
                    prefix + 'nombre_medicamento',
                    ''
            );

            addHidden(
                    prefix + 'cantidad',
                    detalle.cantidad
            );

            addHidden(
                    prefix + 'observaciones',
                    detalle.observaciones
            );
        }

        var botonAgregar =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_agregar'
                );

        botonAgregar
                .attr(
                        'disabled',
                        'disabled'
                )
                .val(
                        'Agregando...'
                );

        if (typeof <portlet:namespace />setDetalleAccionEnCurso
                == 'function') {

            <portlet:namespace />setDetalleAccionEnCurso(
                    true
            );
        }

        try {
            document.body.appendChild(
                    form
            );

            form.submit();

        } catch (e) {

            if (form.parentNode) {
                form.parentNode.removeChild(
                        form
                );
            }

            botonAgregar
                    .removeAttr(
                            'disabled'
                    )
                    .val(
                            'Agregar'
                    );

            if (typeof <portlet:namespace />setDetalleAccionEnCurso
                    == 'function') {

                <portlet:namespace />setDetalleAccionEnCurso(
                        false
                );
            }

            alert(
                    'No se pudieron agregar los ítems seleccionados.'
            );
        }

        return false;
    }


    function <portlet:namespace />agregarItemsHistoricosSeleccionados() {

        var selectorTipo = jQuery(
                '#<portlet:namespace />detalle_id_tipo_prestacion'
        );

        if (selectorTipo.length > 0
                && !selectorTipo.attr('disabled')
                && !<portlet:namespace />esTipoPrestacionDetalleValidoParaSector(
                        selectorTipo.val()
                )) {

            alert('Debe seleccionar el Tipo para los detalles a agregar.');
            selectorTipo.focus();
            return false;
        }

        if (typeof <portlet:namespace />detalleAccionEnCurso
                != 'undefined'
                && <portlet:namespace />detalleAccionEnCurso) {

            return false;
        }

        if (typeof <portlet:namespace />guardandoCompra
                != 'undefined'
                && <portlet:namespace />guardandoCompra) {

            return false;
        }

        var detalles =
                <portlet:namespace />obtenerDetallesHistoricosSeleccionados();

        if (!detalles
                || detalles.length <= 0) {

            alert(
                    'Seleccione al menos un ítem para agregar.'
            );

            return false;
        }

        if (typeof <portlet:namespace />requerimientoPersistidoDetalle
                != 'undefined'
                && <portlet:namespace />requerimientoPersistidoDetalle) {

            return <portlet:namespace />postItemsHistoricosAfiliadoServidor(
                    detalles
            );
        }

        if (typeof <portlet:namespace />detallesCompra
                == 'undefined'
                || !<portlet:namespace />detallesCompra) {

            alert(
                    'No se encontró la colección de detalles del requerimiento.'
            );

            return false;
        }

        if (typeof <portlet:namespace />renderDetallesCompra
                != 'function') {

            alert(
                    'No se encontró la función que actualiza '
                            + 'el listado de detalles.'
            );

            return false;
        }

        for (var i = 0;
                i < detalles.length;
                i++) {

            <portlet:namespace />detallesCompra.push(
                    detalles[i]
            );
        }

        /*
         * Se actualiza el listado final una sola vez.
         *
         * No se utiliza agregarOActualizarDetalle()
         * y no se modifican los valores del editor manual.
         */
        <portlet:namespace />renderDetallesCompra();

        /*
         * Los ítems recién incorporados quedan visibles
         * en el histórico, pero deshabilitados y marcados
         * como "Ya agregado".
         */
        <portlet:namespace />renderItemsHistoricosAfiliado();

        return false;
    }


    function <portlet:namespace />renderItemsHistoricosAfiliado() {

        var panel =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_panel'
                );

        if (panel.length == 0) {
            return;
        }

        var tabla =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_tabla'
                );

        var estado =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_estado'
                );

        var body =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_body'
                );

        var acciones =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_acciones'
                );

        var seleccionarTodos =
                document.getElementById(
                        '<portlet:namespace />items_historicos_afiliado_seleccionar_todos'
                );

        var botonAgregar =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_agregar'
                );

        body.empty();

        estado
                .text('')
                .hide();

        if (seleccionarTodos) {
            seleccionarTodos.checked =
                    false;

            seleccionarTodos.disabled =
                    true;
        }

        botonAgregar
                .attr(
                        'disabled',
                        'disabled'
                )
                .val(
                        'Agregar'
                );

        var clavesActuales =
                <portlet:namespace />clavesDetallesActuales();

        var cantidadRenderizada =
                0;

        for (var i = 0;
                i < <portlet:namespace />itemsHistoricosAfiliado.length;
                i++) {

            var item =
                    <portlet:namespace />itemsHistoricosAfiliado[i];

            var clave =
                    <portlet:namespace />claveItemHistoricoAfiliado(
                            item
                    );

            if (clave == '') {
                continue;
            }

            var yaAgregado =
                    clavesActuales[clave] === true;

            var rowClass =
                    cantidadRenderizada % 2 == 0
                            ? 'portlet-section-body results-row'
                            : 'portlet-section-alternate results-row alt';

            var fila =
                    jQuery(
                            '<tr></tr>'
                    )
                            .addClass(
                                    rowClass
                            );

            if (yaAgregado) {
                fila.addClass(
                        'compras-historicos-fila-agregada'
                );
            }

            var celdaCheck =
                    jQuery(
                            '<td></td>'
                    )
                            .addClass(
                                    'compras-historicos-col-check'
                            );

            var check =
                    jQuery(
                            '<input type="checkbox" />'
                    )
                            .addClass(
                                    'compras-item-historico-check'
                            )
                            .val(
                                    String(i)
                            );

            if (yaAgregado) {
                check.attr(
                        'disabled',
                        'disabled'
                );

                check.attr(
                        'title',
                        'Este ítem ya fue agregado al detalle.'
                );
            }

            check.click(function() {
                <portlet:namespace />actualizarSeleccionItemsHistoricosAfiliado();
            });

            celdaCheck.append(
                    check
            );

            fila.append(
                    celdaCheck
            );

            jQuery(
                    '<td></td>'
            )
                    .addClass(
                            'compras-historicos-col-tipo'
                    )
                    .text(
                            <portlet:namespace />descripcionTipoItemHistoricoAfiliado(
                                    item.idTipoNomenclador
                            )
                    )
                    .appendTo(
                            fila
                    );

            jQuery(
                    '<td></td>'
            )
                    .addClass(
                            'compras-historicos-col-codigo'
                    )
                    .text(
                            item.codigo != null
                                    ? String(
                                            item.codigo
                                    )
                                    : ''
                    )
                    .appendTo(
                            fila
                    );

            var celdaDescripcion =
                    jQuery(
                            '<td></td>'
                    )
                            .addClass(
                                    'compras-historicos-col-descripcion'
                            )
                            .text(
                                    item.descripcion != null
                                            ? String(
                                                    item.descripcion
                                            )
                                            : ''
                            );

            if (yaAgregado) {
                celdaDescripcion.append(
                        jQuery(
                                '<span></span>'
                        )
                                .addClass(
                                        'compras-historicos-estado-item'
                                )
                                .text(
                                        'Ya agregado'
                                )
                );
            }

            fila.append(
                    celdaDescripcion
            );

            body.append(
                    fila
            );

            cantidadRenderizada++;
        }

        if (cantidadRenderizada == 0) {
            tabla.hide();
            acciones.hide();
            panel.hide();
            return;
        }

        tabla.show();
        acciones.show();
        panel.show();

        <portlet:namespace />actualizarSeleccionItemsHistoricosAfiliado();
    }

    function <portlet:namespace />actualizarEstadoItemsHistoricosAfiliado() {

        <portlet:namespace />renderItemsHistoricosAfiliado();
    }


    window[
            '<portlet:namespace />actualizarEstadoItemsHistoricosAfiliado'
    ] =
            <portlet:namespace />actualizarEstadoItemsHistoricosAfiliado;


    function <portlet:namespace />consultarItemsHistoricosAfiliado(
            cuil,
            inte) {

        <portlet:namespace />invalidarConsultaItemsHistoricosAfiliado();

        <portlet:namespace />limpiarVisualItemsHistoricosAfiliado();

        var panel =
                jQuery(
                        '#<portlet:namespace />items_historicos_afiliado_panel'
                );

        if (panel.length == 0) {
            return;
        }

        cuil =
                cuil != null
                        ? jQuery.trim(
                                String(
                                        cuil
                                )
                        )
                        : '';

        inte =
                inte != null
                        ? jQuery.trim(
                                String(
                                        inte
                                )
                        )
                        : '';

        var idSector =
                <portlet:namespace />trimValue(
                        'sector_id'
                );

        if (!/^[0-9]{11}$/.test(cuil)
                || !/^[0-9]+$/.test(inte)
                || !/^[0-9]+$/.test(idSector)
                || parseInt(idSector, 10) <= 0
                || !<portlet:namespace />sectorUsaCodigoPrestacion()) {

            return;
        }

        var idRequerimientoExcluir =
                '0';

        if (typeof <portlet:namespace />idRequerimientoCompraDetalle
                != 'undefined'
                && <portlet:namespace />idRequerimientoCompraDetalle != null) {

            idRequerimientoExcluir =
                    jQuery.trim(
                            String(
                                    <portlet:namespace />idRequerimientoCompraDetalle
                            )
                    );
        }

        if (!/^[0-9]+$/.test(idRequerimientoExcluir)
                || parseInt(
                        idRequerimientoExcluir,
                        10
                ) <= 0) {

            idRequerimientoExcluir =
                    '0';
        }

        var secuenciaActual =
                <portlet:namespace />itemsHistoricosAfiliadoSecuencia;

        jQuery(
                '#<portlet:namespace />items_historicos_afiliado_estado'
        )
                .text(
                        'Consultando ítems históricos...'
                )
                .show();

        panel.show();

        <portlet:namespace />itemsHistoricosAfiliadoRequest =
                jQuery.ajax({

                    url:
                            '${comprasBuscarItemsHistoricosAfiliadoURL}',

                    data: {
                        cuil_titular:
                                cuil,

                        inte:
                                inte,

                        id_sector:
                                idSector,

                        id_requerimiento_excluir:
                                idRequerimientoExcluir
                    },

                    cache:
                            false,

                    success: function(data) {

                        if (secuenciaActual
                                != <portlet:namespace />itemsHistoricosAfiliadoSecuencia) {

                            return;
                        }

                        var obj =
                                null;

                        try {
                            obj =
                                    typeof data == 'string'
                                            ? jQuery.parseJSON(
                                                    data
                                            )
                                            : data;

                        } catch (e) {
                            <portlet:namespace />limpiarVisualItemsHistoricosAfiliado();

                            return;
                        }

                        if (!obj
                                || typeof obj.items != 'object'
                                || typeof obj.items.length
                                        == 'undefined') {

                            <portlet:namespace />limpiarVisualItemsHistoricosAfiliado();

                            return;
                        }

                        <portlet:namespace />itemsHistoricosAfiliado =
                                obj.items;

                        <portlet:namespace />renderItemsHistoricosAfiliado();
                    },

                    error: function(xhr, estado) {

                        if (secuenciaActual
                                != <portlet:namespace />itemsHistoricosAfiliadoSecuencia) {

                            return;
                        }

                        if (estado == 'abort') {
                            return;
                        }

                        <portlet:namespace />limpiarVisualItemsHistoricosAfiliado();
                    },

                    complete: function() {

                        if (secuenciaActual
                                == <portlet:namespace />itemsHistoricosAfiliadoSecuencia) {

                            <portlet:namespace />itemsHistoricosAfiliadoRequest =
                                    null;
                        }
                    }
                });
    }

    function <portlet:namespace />buscarAfiliados() {
        if (<portlet:namespace />guardandoCompra) {
            return false;
        }

        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numeroAfi = jQuery('#<portlet:namespace />numero_afi').val();
        var nroCredencialPrevencion = jQuery('#<portlet:namespace />nroCredencialPrevencion').val();
        var nroSocioPrevencion = jQuery('#<portlet:namespace />nroSocioPrevencion').val();
        var fechaReferencia = <portlet:namespace />fechaReferenciaAfiliado();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, numeroAfi)) {
            return false;
        }

        if (cuil.length > 0) {
            if (typeof validarCuil == 'function' && !validarCuil(cuil, '<liferay-ui:message key="valida-cuil-mensaje-limpiar"/>')) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        if (jQuery('#<portlet:namespace />secc_seleccionada').val() != '1') {
            jQuery('#<portlet:namespace />seccional').val('');
            jQuery('#<portlet:namespace />id_seccional').val('');
        }

        popupAfill = Liferay.Popup({
            title: '<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />',
            modal: true,
            width: 830
        });

        var url =
                '${comprasBuscarAfiliadosURL}'
                + '&cuil='
                + encodeURIComponent(cuil)
                + '&inte='
                + encodeURIComponent(inte)
                + '&tipoDoc='
                + encodeURIComponent(tipoDoc)
                + '&nroDoc='
                + encodeURIComponent(nroDoc)
                + '&seccional='
                + encodeURIComponent(seccional)
                + '&nombre='
                + encodeURIComponent(nombre)
                + '&apellido='
                + encodeURIComponent(apellido)
                + '&entidad='
                + encodeURIComponent(entidad)
                + '&numero_afi='
                + encodeURIComponent(numeroAfi)
                + '&fecha_referencia='
                + encodeURIComponent(fechaReferencia)
                + '&nroCredencialPrevencion='
                + encodeURIComponent(nroCredencialPrevencion)
                + '&nroSocioPrevencion='
                + encodeURIComponent(nroSocioPrevencion)
                + '&origen='
                + '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccional() {
        var id_seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var seccional = jQuery('#<portlet:namespace />seccional').val();

        if (!<portlet:namespace />validaFormSecc(id_seccional, seccional)) {
            return false;
        }

        popup = Liferay.Popup({
            title: '<liferay-ui:message key="busqueda-seccionales" />',
            modal: true,
            width: 420
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_seccional' +
            '&id_seccional=' + encodeURIComponent(id_seccional) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&prefijo=';

        jQuery(popup).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccionalOnDiv(e) {
        var evtobj = window.event ? event : e;
        var keyPressed = evtobj.keyCode ? evtobj.keyCode : evtobj.charCode;

        if (jQuery('#<portlet:namespace />secc_seleccionada').val() == '1' && (keyPressed != 9 && keyPressed != 16)) {
            jQuery('#<portlet:namespace />seccional').val('');
            jQuery('#<portlet:namespace />id_seccional').val('');
            jQuery('#<portlet:namespace />secc_seleccionada').val('');
            jQuery('#<portlet:namespace />btnBuscarSeccional').show();

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            return false;
        }

        var id_seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var seccional = jQuery('#<portlet:namespace />seccional').val();

        if ((seccional.length >= 3 || id_seccional.length > 2) && (keyPressed != 9 && keyPressed != 16)) {
            if (id_seccional.length > 2) {
                jQuery('#<portlet:namespace />seccional').val('');
            } else {
                jQuery('#<portlet:namespace />id_seccional').val('');
            }

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery('#divSeccional').load(url);
            jQuery('#divSeccional').show();
        } else {
            jQuery('#divSeccional').hide('slow');
        }

        return false;
    }

    function seleccionaAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        seleccionaCamposAfiliado(
            cuil,
            inte,
            docu_tipo,
            docu_nro,
            nombre,
            apellido,
            id_secc,
            desc_secc,
            ospim,
            uoma,
            amtima,
            bajaFecha,
            nombre_plan,
            id_plan,
            fecha_alta_af,
            incapacidad_af,
            id_tercerizadora,
            afi_tercerizadora,
            reclamoPrestacional,
            nroSocioPrev,
            nroCredenPrev,
            fechaRecepcion,
            tieneAntecedentes
        );

        if (popupAfill != null) {
            Liferay.Popup.close(popupAfill);
        }
    }

    function seleccionaCamposAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        nombre_plan = <portlet:namespace />valorSeguroAfiliado(nombre_plan);
        id_plan = <portlet:namespace />valorSeguroAfiliado(id_plan);
        id_tercerizadora = <portlet:namespace />valorSeguroAfiliado(id_tercerizadora);
        afi_tercerizadora = <portlet:namespace />valorSeguroAfiliado(afi_tercerizadora);
        fecha_alta_af = <portlet:namespace />valorSeguroAfiliado(fecha_alta_af);
        incapacidad_af = <portlet:namespace />valorSeguroAfiliado(incapacidad_af);
        nroSocioPrev = <portlet:namespace />valorSeguroAfiliado(nroSocioPrev);
        nroCredenPrev = <portlet:namespace />valorSeguroAfiliado(nroCredenPrev);
        bajaFecha = <portlet:namespace />valorSeguroAfiliado(bajaFecha);

        jQuery('#<portlet:namespace />cuil').val(cuil);
        jQuery('#<portlet:namespace />inte').val(inte);
        jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
        jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
        jQuery('#<portlet:namespace />id_seccional').val(id_secc);
        jQuery('#<portlet:namespace />seccional').val(desc_secc);
        jQuery('#<portlet:namespace />apellido').val(apellido);
        jQuery('#<portlet:namespace />nombre').val(nombre);
        jQuery('#<portlet:namespace />secc_seleccionada').val('1');

        var entidadSeleccionada = jQuery('#<portlet:namespace />entidad').val();
        var credencialId = '#<portlet:namespace />' + 'num' + 'ero_afi';

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(ospim));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(uoma));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(amtima));
        }

        jQuery('#<portlet:namespace />baja_fecha').val(bajaFecha);

        if (bajaFecha != '') {
            document.getElementById('<portlet:namespace />baja_fecha').style.background = 'red';
            document.getElementById('<portlet:namespace />baja_fecha').style.color = 'white';
        } else {
            document.getElementById('<portlet:namespace />baja_fecha').style.background = 'white';
            document.getElementById('<portlet:namespace />baja_fecha').style.color = 'black';
        }

        jQuery('#<portlet:namespace />nombre_plan').val(nombre_plan);
        jQuery('#<portlet:namespace />id_plan').val(id_plan);
        jQuery('#<portlet:namespace />afi_tercerizadora').val(afi_tercerizadora);
        jQuery('#<portlet:namespace />fecha_alta_af').val(fecha_alta_af);
        jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val(id_tercerizadora);
        jQuery('#<portlet:namespace />incapacidad_af').val(incapacidad_af);
        jQuery('#<portlet:namespace />nroSocioPrevencion').val(nroSocioPrev);
        jQuery('#<portlet:namespace />nroCredencialPrevencion').val(nroCredenPrev);
        jQuery('#<portlet:namespace />tieneAntecedentes').val(tieneAntecedentes == '1' ? '1' : '0');

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes);
        }

        <portlet:namespace />actualizarVerificacionContactoAfiliado(
                cuil,
                inte
        );

        <portlet:namespace />sincronizarAfiliadoRequerimiento();

        /*
         * Actualizar el vencimiento CUD inmediatamente después
         * de seleccionar el afiliado.
         */
        <portlet:namespace />actualizarVencimientoCudAfiliado(
                cuil,
                inte,
                incapacidad_af
        );

        /*
         * Regla exclusiva de Compras.
         *
         * Una vez seleccionado el afiliado, se comprueba si posee
         * al menos una situación médica vigente.
         */
        <portlet:namespace />actualizarSituacionMedicaAfiliado(
                cuil,
                inte
        );

        <portlet:namespace />consultarItemsHistoricosAfiliado(
                cuil,
                inte
        );

        if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
            <portlet:namespace />mostrarMensajeAfiliadoInicial('');
        }
    }

</script>

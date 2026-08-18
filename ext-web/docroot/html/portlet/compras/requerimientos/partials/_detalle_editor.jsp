<fieldset class="block-labels compras-detalle-editor">
    <legend>Agregar / editar detalle</legend>

    <style type="text/css">
        .compras-detalle-editor .historicos-panel {
            display: none;
            margin-bottom: 12px;
            padding: 10px 12px;
            background: #eef6fb;
            border: 1px solid #c9dbe6;
        }

        .compras-detalle-editor .historicos-titulo {
            margin-bottom: 8px;
            font-weight: bold;
            color: #3b4b59;
        }

        .compras-detalle-editor .historicos-estado {
            display: none;
            margin-bottom: 8px;
        }

        .compras-detalle-editor .historicos-tabla {
            display: none;
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            background: #ffffff;
        }

        .compras-detalle-editor .historicos-tabla th,
        .compras-detalle-editor .historicos-tabla td {
            padding: 6px 8px;
            border: 1px solid #d6e3eb;
            vertical-align: middle;
            text-align: left;
        }

        .compras-detalle-editor .historicos-tabla th.col-check,
        .compras-detalle-editor .historicos-tabla td.col-check {
            width: 36px;
            text-align: center;
        }

        .compras-detalle-editor .historicos-tabla th.col-tipo,
        .compras-detalle-editor .historicos-tabla td.col-tipo {
            width: 180px;
        }

        .compras-detalle-editor .historicos-tabla th.col-codigo,
        .compras-detalle-editor .historicos-tabla td.col-codigo {
            width: 120px;
        }

        .compras-detalle-editor .historicos-tabla td.col-descripcion {
            word-wrap: break-word;
            white-space: normal;
        }

        .compras-detalle-editor .historicos-acciones {
            display: none;
            margin-top: 10px;
            text-align: right;
        }

        .compras-detalle-editor .historicos-vacio {
            color: #666666;
            font-style: italic;
        }
    </style>

    <input type="hidden"
           id="<portlet:namespace />detalle_edit_index"
           value="-1" />

    <input type="hidden"
           id="<portlet:namespace />detalle_tipo_item"
           value="NOMENCLADOR" />

    <input type="hidden"
           id="<portlet:namespace />detalle_codigo_item"
           value="" />

    <input type="hidden"
           id="<portlet:namespace />detalle_descripcion_item"
           value="" />

    <input type="hidden"
           id="<portlet:namespace />detalle_id_prestacion"
           value="" />

    <input type="hidden"
           id="<portlet:namespace />detalle_id_tipo_nomenclador"
           value="" />

    <div id="<portlet:namespace />detalle_medicamento_historico_info"
         class="portlet-msg-info"
         style="display:none;">
        Este es un detalle histórico de medicamento.
        El Código y la Descripción se conservan sin cambios.
        Sólo puede modificar la Cantidad.
    </div>

    <% if (puedeABMDetalle) { %>
        <div id="<portlet:namespace />items_historicos_afiliado_panel"
             class="historicos-panel">

            <div class="historicos-titulo">
                Ítems utilizados anteriormente para este afiliado
            </div>

            <div id="<portlet:namespace />items_historicos_afiliado_estado"
                 class="historicos-estado">
            </div>

            <table id="<portlet:namespace />items_historicos_afiliado_tabla"
                   class="historicos-tabla">
                <thead>
                    <tr>
                        <th class="col-check">
                            <input type="checkbox"
                                   id="<portlet:namespace />items_historicos_afiliado_seleccionar_todos"
                                   title="Seleccionar todos"
                                   onclick="return <portlet:namespace />seleccionarTodosItemsHistoricosAfiliado(this.checked);" />
                        </th>
                        <th class="col-tipo">Tipo Nomenclador</th>
                        <th class="col-codigo">Código</th>
                        <th>Descripción</th>
                    </tr>
                </thead>
                <tbody id="<portlet:namespace />items_historicos_afiliado_body">
                </tbody>
            </table>

            <div id="<portlet:namespace />items_historicos_afiliado_acciones"
                 class="historicos-acciones">
                <input type="button"
                       id="<portlet:namespace />items_historicos_afiliado_agregar"
                       value="Agregar seleccionados"
                       onclick="return <portlet:namespace />agregarItemsHistoricosSeleccionados();" />
            </div>
        </div>
    <% } %>

    <table class="lfr-table"
           style="border-collapse: separate; border-spacing: 5px;"
           width="100%">

        <tbody id="<portlet:namespace />detalle_bloque_nomenclador">

            <tr id="<portlet:namespace />detalle_fila_tipo_nomenclador"
                style="display:none;">
                <td>
                    <label for="<portlet:namespace />detalle_tipo_nomenclador_select">
                        Tipo Nomenclador:
                    </label>
                </td>

                <td colspan="3">
                    <select id="<portlet:namespace />detalle_tipo_nomenclador_select">
                        <option value="">Seleccione...</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_codigo_nomenclador">
                        <liferay-ui:message key="codigo-presentado" />:
                    </label>
                </td>

                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_codigo_nomenclador"
                           size="10"
                           maxlength="100"
                           value="" />
                </td>

                <td>
                    <label for="<portlet:namespace />detalle_descripcion_nomenclador">
                        Descripción:
                    </label>

                    <input type="text"
                           id="<portlet:namespace />detalle_descripcion_nomenclador"
                           size="60"
                           maxlength="500"
                           value="" />
                </td>

                <td>
                    <div id="<portlet:namespace />detalle_div_btn_busca_nomenclador">
                        <a href="javascript:void(0);"
                           onclick="return <portlet:namespace />buscarNomencladorDetalle();"
                           tabindex="-1">Buscar</a>

                        &nbsp;

                        <a href="javascript:void(0);"
                           onclick="return <portlet:namespace />limpiarSeleccionNomenclador();"
                           tabindex="-1">Limpiar</a>
                    </div>
                </td>
            </tr>

        </tbody>

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
        </tr>

        <tr id="<portlet:namespace />detalle_fila_observaciones">
            <td>
                <label for="<portlet:namespace />detalle_observaciones">
                    Observaciones:
                </label>
            </td>

            <td colspan="3">
                <input type="text"
                       id="<portlet:namespace />detalle_observaciones"
                       size="80"
                       maxlength="500"
                       value="" />
            </td>
        </tr>

        <tr>
            <td colspan="4" align="center">
                <input type="button"
                       id="<portlet:namespace />detalle_submit"
                       value="Agregar detalle"
                       onclick="return <portlet:namespace />agregarOActualizarDetalle();" />

                &nbsp;&nbsp;

                <input type="button"
                       id="<portlet:namespace />detalle_cancelar"
                       value="Cancelar edición"
                       style="display:none;"
                       onclick="return <portlet:namespace />cancelarEdicionDetalle();" />
            </td>
        </tr>
    </table>
</fieldset>

<script type="text/javascript">
    var <portlet:namespace />itemsHistoricosAfiliado = [];

    function <portlet:namespace />escapeHtml(texto) {
        if (texto == null || typeof texto == 'undefined') {
            return '';
        }

        return String(texto)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function <portlet:namespace />obtenerValorItem(item, nombres) {
        var i;
        for (i = 0; i < nombres.length; i++) {
            if (typeof item[nombres[i]] != 'undefined' && item[nombres[i]] != null) {
                return item[nombres[i]];
            }
        }
        return '';
    }

    function <portlet:namespace />mostrarItemsHistoricosAfiliado(items) {
        var panel = document.getElementById('<portlet:namespace />items_historicos_afiliado_panel');
        var tabla = document.getElementById('<portlet:namespace />items_historicos_afiliado_tabla');
        var body = document.getElementById('<portlet:namespace />items_historicos_afiliado_body');
        var acciones = document.getElementById('<portlet:namespace />items_historicos_afiliado_acciones');
        var estado = document.getElementById('<portlet:namespace />items_historicos_afiliado_estado');
        var seleccionarTodos = document.getElementById('<portlet:namespace />items_historicos_afiliado_seleccionar_todos');

        var i;
        var html = '';
        var item;
        var tipo;
        var codigo;
        var descripcion;

        <portlet:namespace />itemsHistoricosAfiliado = items || [];

        body.innerHTML = '';
        seleccionarTodos.checked = false;

        if (!<portlet:namespace />itemsHistoricosAfiliado.length) {
            panel.style.display = '';
            tabla.style.display = 'none';
            acciones.style.display = 'none';
            estado.style.display = '';
            estado.className = 'historicos-estado historicos-vacio';
            estado.innerHTML = 'No se encontraron ítems históricos para este afiliado.';
            return;
        }

        for (i = 0; i < <portlet:namespace />itemsHistoricosAfiliado.length; i++) {
            item = <portlet:namespace />itemsHistoricosAfiliado[i];

            tipo = <portlet:namespace />obtenerValorItem(item, [
                'tipoNomenclador',
                'descTipoNomenclador',
                'descripcionTipoNomenclador',
                'tipo_nomenclador'
            ]);

            codigo = <portlet:namespace />obtenerValorItem(item, [
                'codigo',
                'codigoItem',
                'codigoPrestacion'
            ]);

            descripcion = <portlet:namespace />obtenerValorItem(item, [
                'descripcion',
                'descripcionItem',
                'descPrestacion'
            ]);

            html += '<tr>';
            html += '<td class="col-check">'
                 +  '<input type="checkbox" '
                 +  'class="<portlet:namespace />item_historico_afiliado_check" '
                 +  'value="' + i + '" />'
                 +  '</td>';
            html += '<td class="col-tipo">' + <portlet:namespace />escapeHtml(tipo) + '</td>';
            html += '<td class="col-codigo">' + <portlet:namespace />escapeHtml(codigo) + '</td>';
            html += '<td class="col-descripcion">' + <portlet:namespace />escapeHtml(descripcion) + '</td>';
            html += '</tr>';
        }

        body.innerHTML = html;

        estado.style.display = 'none';
        estado.innerHTML = '';
        tabla.style.display = '';
        acciones.style.display = '';
        panel.style.display = '';
    }

    function <portlet:namespace />seleccionarTodosItemsHistoricosAfiliado(seleccionar) {
        var body = document.getElementById('<portlet:namespace />items_historicos_afiliado_body');
        var inputs;
        var i;

        if (!body) {
            return false;
        }

        inputs = body.getElementsByTagName('input');

        for (i = 0; i < inputs.length; i++) {
            if (inputs[i].type == 'checkbox'
                    && inputs[i].className == '<portlet:namespace />item_historico_afiliado_check') {
                inputs[i].checked = seleccionar;
            }
        }

        return false;
    }

    function <portlet:namespace />agregarItemsHistoricosSeleccionados() {
        var body = document.getElementById('<portlet:namespace />items_historicos_afiliado_body');
        var checks = body.getElementsByTagName('input');
        var seleccionados = [];
        var i;
        var indice;
        var ok;
        var agregarUno;
        var seleccionarTodos = document.getElementById('<portlet:namespace />items_historicos_afiliado_seleccionar_todos');

        for (i = 0; i < checks.length; i++) {
            if (checks[i].type == 'checkbox'
                    && checks[i].className == '<portlet:namespace />item_historico_afiliado_check'
                    && checks[i].checked) {
                indice = parseInt(checks[i].value, 10);
                if (!isNaN(indice)) {
                    seleccionados.push(indice);
                }
            }
        }

        if (!seleccionados.length) {
            alert('Seleccione al menos un ítem.');
            return false;
        }

        agregarUno = function(item) {
            var idPrestacion = <portlet:namespace />obtenerValorItem(item, [
                'idPrestacion',
                'id_prestacion'
            ]);

            var idTipoNomenclador = <portlet:namespace />obtenerValorItem(item, [
                'idTipoNomenclador',
                'id_tipo_nomenclador'
            ]);

            var tipo = <portlet:namespace />obtenerValorItem(item, [
                'tipoNomenclador',
                'descTipoNomenclador',
                'descripcionTipoNomenclador',
                'tipo_nomenclador'
            ]);

            var codigo = <portlet:namespace />obtenerValorItem(item, [
                'codigo',
                'codigoItem',
                'codigoPrestacion'
            ]);

            var descripcion = <portlet:namespace />obtenerValorItem(item, [
                'descripcion',
                'descripcionItem',
                'descPrestacion'
            ]);

            document.getElementById('<portlet:namespace />detalle_edit_index').value = '-1';
            document.getElementById('<portlet:namespace />detalle_tipo_item').value = 'NOMENCLADOR';
            document.getElementById('<portlet:namespace />detalle_codigo_item').value = codigo;
            document.getElementById('<portlet:namespace />detalle_descripcion_item').value = descripcion;
            document.getElementById('<portlet:namespace />detalle_id_prestacion').value = idPrestacion;
            document.getElementById('<portlet:namespace />detalle_id_tipo_nomenclador').value = idTipoNomenclador;

            if (document.getElementById('<portlet:namespace />detalle_tipo_nomenclador_select')) {
                document.getElementById('<portlet:namespace />detalle_tipo_nomenclador_select').value = idTipoNomenclador;
            }

            document.getElementById('<portlet:namespace />detalle_codigo_nomenclador').value = codigo;
            document.getElementById('<portlet:namespace />detalle_descripcion_nomenclador').value = descripcion;
            document.getElementById('<portlet:namespace />detalle_cantidad').value = '1';
            document.getElementById('<portlet:namespace />detalle_observaciones').value = '';

            return <portlet:namespace />agregarOActualizarDetalle();
        };

        for (i = 0; i < seleccionados.length; i++) {
            ok = agregarUno(<portlet:namespace />itemsHistoricosAfiliado[seleccionados[i]]);
            if (ok === false) {
                return false;
            }
        }

        for (i = 0; i < checks.length; i++) {
            if (checks[i].type == 'checkbox'
                    && checks[i].className == '<portlet:namespace />item_historico_afiliado_check') {
                checks[i].checked = false;
            }
        }

        seleccionarTodos.checked = false;

        if (typeof <portlet:namespace />limpiarSeleccionNomenclador == 'function') {
            <portlet:namespace />limpiarSeleccionNomenclador();
        }

        return false;
    }
</script>
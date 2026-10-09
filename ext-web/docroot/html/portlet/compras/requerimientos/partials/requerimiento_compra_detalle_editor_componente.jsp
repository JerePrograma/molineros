<%--
Responsabilidad:
    Renderiza el editor de prestaciones y el tipo de cotización por detalle.
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
    detalle_edit_index, detalle_tipo_item, detalle_codigo_item, detalle_descripcion_item, detalle_id_prestacion, detalle_id_tipo_nomenclador, detalle_medicamento_historico_info, items_historicos_afiliado_panel, items_historicos_afiliado_estado, items_historicos_afiliado_tabla, items_historicos_afiliado_seleccionar_todos, seleccionarTodosItemsHistoricosAfiliado
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<fieldset class="block-labels compras-detalle-editor">
    <legend>Agregar / editar detalle</legend>

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
             class="compras-historicos-panel"
             style="display:none;">

            <div class="compras-historicos-encabezado">
                <strong>
                    Ítems utilizados anteriormente para este afiliado
                </strong>

                <span class="compras-historicos-ayuda">
                    Seleccione uno o más ítems para incorporarlos al detalle.
                </span>
            </div>

            <div id="<portlet:namespace />items_historicos_afiliado_estado"
                 class="compras-historicos-estado"
                 style="display:none;">
            </div>

            <table id="<portlet:namespace />items_historicos_afiliado_tabla"
                   class="lfr-table taglib-search-iterator compras-historicos-tabla"
                   width="100%"
                   cellspacing="0"
                   cellpadding="0"
                   style="display:none;">

                <thead>
                    <tr class="portlet-section-header results-header">

                        <th class="compras-historicos-col-check">
                            <input type="checkbox"
                                   id="<portlet:namespace />items_historicos_afiliado_seleccionar_todos"
                                   title="Seleccionar todos"
                                   disabled="disabled"
                                   onclick="<portlet:namespace />seleccionarTodosItemsHistoricosAfiliado(this.checked);" />
                        </th>

                        <th class="compras-historicos-col-tipo">
                            Catálogo
                        </th>

                        <th class="compras-historicos-col-codigo">
                            Código
                        </th>

                        <th class="compras-historicos-col-descripcion">
                            Descripción
                        </th>

                    </tr>
                </thead>

                <tbody id="<portlet:namespace />items_historicos_afiliado_body">
                </tbody>

            </table>

            <div id="<portlet:namespace />items_historicos_afiliado_acciones"
                 class="compras-historicos-acciones"
                 style="display:none;">

                <input type="button"
                       id="<portlet:namespace />items_historicos_afiliado_agregar"
                       value="Agregar"
                       disabled="disabled"
                       onclick="return <portlet:namespace />agregarItemsHistoricosSeleccionados();" />

            </div>

        </div>

    <% } %>

    <style type="text/css">
        .compras-detalle-editor .compras-detalle-campos {
            table-layout: fixed;
            border-collapse: separate;
            border-spacing: 5px;
        }

        .compras-detalle-editor .compras-detalle-campos label {
            display: inline-block;
            width: 45%;
            margin-right: 2%;
        }

        .compras-detalle-editor .compras-detalle-columna-centro label {
            width: 18%;
        }

        .compras-detalle-editor .compras-detalle-columna-centro input[type="text"] {
            width: 75%;
        }

        .compras-detalle-editor .compras-detalle-columna-izquierda input[type="text"],
        .compras-detalle-editor .compras-detalle-columna-izquierda select,
        .compras-detalle-editor .compras-detalle-columna-derecha input[type="text"] {
            max-width: 50%;
        }

        .compras-detalle-editor .compras-detalle-formato-empresa .compras-detalle-columna-izquierda label {
            width: 12%;
        }

        .compras-detalle-editor .compras-detalle-formato-empresa .compras-detalle-columna-izquierda input[type="text"] {
            width: 80%;
            max-width: 80%;
        }

        .compras-detalle-editor .compras-detalle-formato-medico .compras-detalle-columna-derecha label {
            width: 13%;
            margin-right: 1.5%;
        }

        .compras-detalle-editor .compras-detalle-acciones a,
        .compras-detalle-editor .compras-detalle-acciones input {
            margin-right: 8px;
        }
    </style>

    <table class="lfr-table compras-detalle-campos"
           id="<portlet:namespace />detalle_campos_editor"
           width="100%">

        <colgroup>
            <col style="width: 30%;" />
            <col style="width: 50%;" />
            <col style="width: 20%;" />
        </colgroup>

        <tr id="<portlet:namespace />detalle_fila_campos_superiores">
            <td id="<portlet:namespace />detalle_celda_tipo"
                class="compras-detalle-columna-izquierda">
                <% if (reqDetalle == null
                        || reqDetalle.getIdRequerimientoCompra() <= 0
                        || !reqDetalle.esSectorSinCotizacionPrestador()) { %>
                <div id="<portlet:namespace />detalle_fila_tipo_prestacion">
                    <label for="<portlet:namespace />detalle_id_tipo_prestacion">
                        Tipo de cotización:
                    </label>
                    <select id="<portlet:namespace />detalle_id_tipo_prestacion">
                        <option value="">Seleccione...</option>
                    </select>
                    <span id="<portlet:namespace />detalle_tipo_prestacion_ayuda"
                          class="portlet-msg-info"
                          style="display:none;">
                    </span>
                </div>
                <% } %>
            </td>

            <td id="<portlet:namespace />detalle_celda_droga"
                class="compras-detalle-columna-centro">
                <div class="compras-detalle-campo-nomenclador">
                    <div id="<portlet:namespace />detalle_fila_droga_nomenclador"
                         style="display:none;">
                        <label for="<portlet:namespace />detalle_droga_nomenclador">
                            Droga:
                        </label>
                        <input type="text"
                               id="<portlet:namespace />detalle_droga_nomenclador"
                               size="60"
                               maxlength="500"
                               value="" />
                    </div>
                </div>
            </td>

            <td id="<portlet:namespace />detalle_celda_cantidad"
                class="compras-detalle-columna-derecha">
                <label for="<portlet:namespace />detalle_cantidad">
                    Cantidad:
                </label>
                <input type="text"
                       id="<portlet:namespace />detalle_cantidad"
                       size="8"
                       value="1" />
            </td>
        </tr>

        <tr id="<portlet:namespace />detalle_fila_campos_inferiores">
            <td class="compras-detalle-columna-izquierda">
                <div id="<portlet:namespace />detalle_bloque_nomenclador"
                     class="compras-detalle-campo-nomenclador">
                    <label for="<portlet:namespace />detalle_codigo_nomenclador">
                        <liferay-ui:message key="codigo-presentado" />:
                    </label>
                    <input type="text"
                           id="<portlet:namespace />detalle_codigo_nomenclador"
                           size="10"
                           maxlength="100"
                           value="" />
                </div>
            </td>

            <td id="<portlet:namespace />detalle_celda_descripcion"
                class="compras-detalle-columna-centro">
                <div class="compras-detalle-campo-nomenclador">
                    <label for="<portlet:namespace />detalle_descripcion_nomenclador">
                        Descripción:
                    </label>
                    <input type="text"
                           id="<portlet:namespace />detalle_descripcion_nomenclador"
                           size="60"
                           maxlength="500"
                           value="" />
                </div>

                <div id="<portlet:namespace />detalle_fila_observaciones">
                    <label for="<portlet:namespace />detalle_observaciones">
                        Descripción:
                    </label>
                    <input type="text"
                           id="<portlet:namespace />detalle_observaciones"
                           size="80"
                           maxlength="500"
                           value="" />
                </div>
            </td>

            <td class="compras-detalle-columna-derecha">
                <div class="compras-detalle-campo-nomenclador compras-detalle-acciones">
                    <div id="<portlet:namespace />detalle_div_btn_busca_nomenclador">
                        <a href="javascript:void(0);"
                           onclick="return <portlet:namespace />buscarNomencladorDetalle();"
                           tabindex="-1">Buscar</a>
                        <a href="javascript:void(0);"
                           onclick="return <portlet:namespace />limpiarSeleccionNomenclador();"
                           tabindex="-1">Limpiar</a>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td colspan="3"
                align="center"
                class="compras-detalle-acciones">
                <input type="button"
                       id="<portlet:namespace />detalle_submit"
                       value="Agregar detalle"
                       onclick="return <portlet:namespace />agregarOActualizarDetalle();" />
                <input type="button"
                       id="<portlet:namespace />detalle_cancelar"
                       value="Cancelar edición"
                       style="display:none;"
                       onclick="return <portlet:namespace />cancelarEdicionDetalle();" />
            </td>
        </tr>

    </table>

</fieldset>

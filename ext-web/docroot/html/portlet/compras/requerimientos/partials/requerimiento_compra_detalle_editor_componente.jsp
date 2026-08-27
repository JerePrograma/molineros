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

    <table class="lfr-table"
           style="border-collapse: separate; border-spacing: 5px;"
           width="100%">

        <% if (reqDetalle == null
                || reqDetalle.getIdRequerimientoCompra() <= 0
                || !reqDetalle.esSectorSinCotizacionPrestador()) { %>
        <tr id="<portlet:namespace />detalle_fila_tipo_prestacion">
            <td>
                <label for="<portlet:namespace />detalle_id_tipo_prestacion">
                    Tipo de cotización:
                </label>
            </td>
            <td colspan="3">
                <select id="<portlet:namespace />detalle_id_tipo_prestacion">
                    <option value="">Seleccione...</option>
                </select>
                <span id="<portlet:namespace />detalle_tipo_prestacion_ayuda"
                      class="portlet-msg-info"
                      style="display:none;">
                </span>
            </td>
        </tr>
        <% } %>

        <tbody id="<portlet:namespace />detalle_bloque_nomenclador">

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

            <td colspan="4"
                align="center">

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

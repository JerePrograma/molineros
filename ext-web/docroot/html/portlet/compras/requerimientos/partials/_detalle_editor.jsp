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

    <% if (puedeEditarEstructuraPantalla) { %>

        <div id="<portlet:namespace />items_historicos_afiliado_panel"
             class="portlet-msg-info"
             style="display:none; margin-bottom:10px;">

            <strong>
                Ítems utilizados anteriormente para este afiliado
            </strong>

            <div id="<portlet:namespace />items_historicos_afiliado_estado"
                 style="display:none; margin-top:8px;"></div>

            <table id="<portlet:namespace />items_historicos_afiliado_tabla"
                   class="lfr-table"
                   width="100%"
                   style="display:none; margin-top:8px;">

                <thead>
                    <tr>
                        <th>
                            Tipo Nomenclador
                        </th>

                        <th>
                            Código
                        </th>

                        <th>
                            Descripción
                        </th>

                        <th>
                            &nbsp;
                        </th>
                    </tr>
                </thead>

                <tbody id="<portlet:namespace />items_historicos_afiliado_body">
                </tbody>
            </table>
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
                        <a href="javascript: void(0);"
                           onclick="return <portlet:namespace />buscarNomencladorDetalle();"
                           tabindex="-1">Buscar</a>

                        &nbsp;

                        <a href="javascript: void(0);"
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
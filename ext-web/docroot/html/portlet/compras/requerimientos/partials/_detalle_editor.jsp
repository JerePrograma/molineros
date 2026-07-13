<fieldset class="block-labels">
    <legend>Agregar / editar detalle</legend>

    <input type="hidden" id="<portlet:namespace />detalle_edit_index" value="-1" />
    <input type="hidden" id="<portlet:namespace />detalle_tipo_item" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_codigo_item" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_descripcion_item" value="" />

    <div id="<portlet:namespace />detalle_sector_no_admitido"
         class="portlet-msg-info"
         style="display:none;">
        El sector seleccionado no admite detalles tecnicos de medicamentos o nomenclador.
    </div>

    <table class="lfr-table" width="100%">
        <tr>
            <td>Tipo:</td>
            <td colspan="3">
                <strong id="<portlet:namespace />detalle_tipo_item_label">-</strong>
            </td>
        </tr>

        <tbody id="<portlet:namespace />detalle_bloque_nomenclador">
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_id_tipo_nomenclador_busqueda">
                        Tipo nomenclador:
                    </label>
                </td>
                <td colspan="3">
                    <input type="hidden"
                           id="<portlet:namespace />detalle_id_prestacion"
                           value="" />
                    <input type="hidden"
                           id="<portlet:namespace />detalle_id_tipo_nomenclador"
                           value="" />
                    <select id="<portlet:namespace />detalle_id_tipo_nomenclador_busqueda">
                        <option value="">Todos</option>
                        <% for (int i = 0; i < tiposNomencladorDetalle.size(); i++) {
                            TipoNomenclador tipoNomencladorDetalle =
                                    tiposNomencladorDetalle.get(i);

                            if (tipoNomencladorDetalle == null) {
                                continue;
                            }
                        %>
                            <option value="<%= tipoNomencladorDetalle.getId_tipo_nomenclador() %>">
                                <%= HtmlUtil.escape(tipoNomencladorDetalle.getDescripcion()) %>
                            </option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_codigo_nomenclador">Codigo:</label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_codigo_nomenclador"
                           size="20"
                           maxlength="50"
                           value="" />
                </td>
                <td>
                    <label for="<portlet:namespace />detalle_descripcion_nomenclador">Descripcion:</label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_descripcion_nomenclador"
                           size="55"
                           maxlength="250"
                           value="" />
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <input type="button"
                           value="Buscar"
                           onclick="return <portlet:namespace />buscarNomencladorDetalle();" />
                    &nbsp;
                    <input type="button"
                           value="Limpiar"
                           onclick="return <portlet:namespace />limpiarSeleccionNomenclador();" />
                    <span id="<portlet:namespace />detalle_nomenclador_seleccionado"></span>
                </td>
            </tr>
        </tbody>

        <tbody id="<portlet:namespace />detalle_bloque_medicamento">
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_troquel">Troquel:</label>
                </td>
                <td>
                    <input type="hidden"
                           id="<portlet:namespace />detalle_id_medicamento"
                           value="" />
                    <input type="hidden"
                           id="<portlet:namespace />detalle_nombre_medicamento"
                           value="" />
                    <input type="text"
                           id="<portlet:namespace />detalle_troquel"
                           size="16"
                           maxlength="20"
                           value="" />
                </td>
                <td>
                    <label for="<portlet:namespace />detalle_nombre_medicamento_busqueda">Nombre:</label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_nombre_medicamento_busqueda"
                           size="35"
                           maxlength="150"
                           value="" />
                </td>
            </tr>
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_presentacion_medicamento_busqueda">Presentacion:</label>
                </td>
                <td colspan="3">
                    <input type="text"
                           id="<portlet:namespace />detalle_presentacion_medicamento_busqueda"
                           size="55"
                           maxlength="150"
                           value="" />
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <input type="button"
                           value="Buscar"
                           onclick="return <portlet:namespace />buscarMedicamentoDetalle();" />
                    &nbsp;
                    <input type="button"
                           value="Limpiar"
                           onclick="return <portlet:namespace />limpiarSeleccionMedicamento();" />
                    <span id="<portlet:namespace />detalle_medicamento_seleccionado"></span>
                </td>
            </tr>
        </tbody>

        <tr>
            <td><label for="<portlet:namespace />detalle_cantidad">Cantidad:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />detalle_cantidad"
                       size="8"
                       value="1" />
            </td>
        </tr>
        <tr>
            <td><label for="<portlet:namespace />detalle_observaciones">Observaciones:</label></td>
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
                       onclick="return <%= namespaceDetalleCompra %>agregarOActualizarDetalle();" />
                &nbsp;&nbsp;
                <input type="button"
                       id="<portlet:namespace />detalle_cancelar"
                       value="Cancelar edicion"
                       style="display:none;"
                       onclick="return <%= namespaceDetalleCompra %>cancelarEdicionDetalle();" />
            </td>
        </tr>
    </table>
</fieldset>

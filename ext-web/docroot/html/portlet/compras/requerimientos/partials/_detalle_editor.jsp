<fieldset class="block-labels">
    <legend>Agregar / editar detalle</legend>

    <input type="hidden"
           id="<portlet:namespace />detalle_edit_index"
           value="-1" />

    <input type="hidden"
           id="<portlet:namespace />detalle_tipo_item"
           value="" />

    <input type="hidden"
           id="<portlet:namespace />detalle_codigo_item"
           value="" />

    <input type="hidden"
           id="<portlet:namespace />detalle_descripcion_item"
           value="" />

    <table class="lfr-table" width="100%">
        <tr>
            <td>
                Tipo:
            </td>
            <td colspan="3">
                <strong id="<portlet:namespace />detalle_tipo_item_label">
                    -
                </strong>
            </td>
        </tr>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tbody id="<portlet:namespace />detalle_bloque_nomenclador">
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_id_prestacion">
                        ID Prestación:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_id_prestacion"
                           size="12"
                           maxlength="20"
                           value="" />
                </td>

                <td>
                    <label for="<portlet:namespace />detalle_id_tipo_nomenclador">
                        Tipo nomenclador:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_id_tipo_nomenclador"
                           size="12"
                           maxlength="20"
                           value="" />
                </td>
            </tr>

            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_codigo_nomenclador">
                        Código:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_codigo_nomenclador"
                           size="20"
                           maxlength="50"
                           value="" />
                </td>

                <td>
                    <label for="<portlet:namespace />detalle_descripcion_nomenclador">
                        Descripción:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_descripcion_nomenclador"
                           size="60"
                           maxlength="250"
                           value="" />
                </td>
            </tr>
        </tbody>

        <tbody id="<portlet:namespace />detalle_bloque_medicamento">
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_id_medicamento">
                        ID Medicamento:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_id_medicamento"
                           size="12"
                           maxlength="20"
                           value="" />
                </td>

                <td>
                    <label for="<portlet:namespace />detalle_troquel">
                        Troquel:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_troquel"
                           size="16"
                           maxlength="20"
                           value="" />
                </td>
            </tr>

            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_nombre_medicamento">
                        Medicamento:
                    </label>
                </td>
                <td colspan="3">
                    <input type="text"
                           id="<portlet:namespace />detalle_nombre_medicamento"
                           size="80"
                           maxlength="250"
                           value="" />
                </td>
            </tr>
        </tbody>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

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

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td>
                <label for="<portlet:namespace />detalle_observaciones">
                    Descripción:
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
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="4" align="center">
                <input type="button"
                       id="<portlet:namespace />detalle_submit"
                       value="Agregar detalle"
                       onClick="return <%= namespaceDetalleCompra %>agregarOActualizarDetalle();" />

                &nbsp;&nbsp;

                <input type="button"
                       id="<portlet:namespace />detalle_cancelar"
                       value="Cancelar edición"
                       style="display:none;"
                       onClick="return <%= namespaceDetalleCompra %>cancelarEdicionDetalle();" />
            </td>
        </tr>
    </table>
</fieldset>
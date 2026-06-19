<fieldset class="block-labels">
    <legend>Agregar / editar detalle</legend>

    <input type="hidden"
           id="<portlet:namespace />detalle_edit_index"
           value="-1" />

    <table class="lfr-table" width="100%">
        <tr>
            <td>
                <label for="<portlet:namespace />detalle_id_articulo">
                    Artículo:
                </label>
            </td>
            <td colspan="3">
                <select id="<portlet:namespace />detalle_id_articulo"
                        style="min-width: 420px;">
                    <option value="">Seleccione...</option>
                </select>

                &nbsp;

                <img alt="Nuevo artículo"
                     title="Nuevo artículo"
                     align="absmiddle"
                     src="<%= themeDisplay.getPathThemeImages() %>/common/add.png"
                     style="cursor:pointer;"
                     onClick="<%= namespaceDetalleCompra + "abrirAltaArticuloCompra();" %>" />
            </td>
        </tr>

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
            <td>
                <input type="text"
                       id="<portlet:namespace />detalle_observaciones"
                       size="60"
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
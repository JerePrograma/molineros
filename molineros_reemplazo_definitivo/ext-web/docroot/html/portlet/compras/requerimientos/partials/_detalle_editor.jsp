<fieldset class="block-labels">
    <legend>Agregar / editar detalle</legend>

    <input type="hidden" id="<portlet:namespace />detalle_edit_index" value="-1" />
    <input type="hidden" id="<portlet:namespace />detalle_tipo_item" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_codigo_item" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_descripcion_item" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_id_prestacion" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_id_tipo_nomenclador" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_id_medicamento" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_nombre_medicamento" value="" />
    <input type="hidden" id="<portlet:namespace />detalle_presentacion_medicamento" value="" />

    <div id="<portlet:namespace />detalle_sector_no_admitido"
         class="portlet-msg-info"
         style="display:none;">
        El sector seleccionado no admite detalles tecnicos de medicamentos o nomenclador.
    </div>

    <table class="lfr-table" width="100%">
        <tbody id="<portlet:namespace />detalle_bloque_nomenclador">
            <tr>
                <td>
                    <label for="<portlet:namespace />detalle_codigo_nomenclador"><liferay-ui:message key="codigo-presentado" />:</label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_codigo_nomenclador"
                           size="10"
                           maxlength="20"
                           value="" />
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />detalle_descripcion_nomenclador"
                           aria-label="Descripcion"
                           size="60"
                           maxlength="200"
                           value="" />
                </td>
                <td>
                    <div id="<portlet:namespace />detalle_div_btn_busca_nomenclador">
                        <a href="javascript: void(0);"
                           onclick="return <portlet:namespace />buscarNomencladorDetalle();"
                           tabindex="-1">Buscar</a>
                        <a href="javascript: void(0);"
                           onclick="return <portlet:namespace />limpiarSeleccionNomenclador();"
                           tabindex="-1">Limpiar</a>
                    </div>
                </td>
            </tr>
        </tbody>

        <tbody id="<portlet:namespace />detalle_bloque_medicamento">
            <tr>
                <td colspan="4">
                    <liferay-util:include
                            page="/html/portlet/compras/requerimientos/busqueda_medicamentos.jsp">
                        <liferay-util:param
                                name="search_url"
                                value="/compras/buscar_medicamentos" />
                        <liferay-util:param name="troquel" value="" />
                        <liferay-util:param name="nombre_medicamento" value="" />
                        <liferay-util:param name="id_medicamento" value="" />
                        <liferay-util:param name="esEditable" value="true" />
                        <liferay-util:param
                                name="mostrar_con_presentacion"
                                value="true" />
                    </liferay-util:include>
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
                       onclick="return <portlet:namespace />agregarOActualizarDetalle();" />
                &nbsp;&nbsp;
                <input type="button"
                       id="<portlet:namespace />detalle_cancelar"
                       value="Cancelar edicion"
                       style="display:none;"
                       onclick="return <portlet:namespace />cancelarEdicionDetalle();" />
            </td>
        </tr>
    </table>
</fieldset>

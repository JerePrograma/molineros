<c:if test="<%= esNuevo && modoEditable %>">
    <fieldset class="block-labels compras-seccion compras-seccion-orden-medica">
        <legend>Orden médica</legend>

        <table class="lfr-table compras-resumen-requerimiento">
            <tr>
                <td>
                    <label for="<portlet:namespace />orden_medica">
                        Orden médica:
                    </label>
                </td>
                <td>
                    <input type="file"
                           id="<portlet:namespace />orden_medica"
                           name="orden_medica"
                           accept=".jpg,.jpeg,.png,image/jpeg,image/png" />
                    <div class="compras-ayuda-campo">
                        Formatos permitidos: JPG, JPEG o PNG.
                    </div>
                </td>
                <td>
                    <label for="<portlet:namespace />fecha_orden_medica">
                        Fecha de la orden médica:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />fecha_orden_medica"
                           name="<portlet:namespace />fecha_orden_medica_visible"
                           maxlength="10"
                           size="12"
                           value="<%= HtmlUtil.escape(
                                   ParamUtil.getString(
                                           renderRequest,
                                           "fecha_orden_medica",
                                           ""
                                   )
                           ) %>" />
                    <div class="compras-ayuda-campo">
                        Formato: AAAA-MM-DD.
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
</c:if>

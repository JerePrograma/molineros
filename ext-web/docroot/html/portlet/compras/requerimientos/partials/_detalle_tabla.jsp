<table class="lfr-table taglib-search-iterator"
       width="100%">

    <tr class="portlet-section-header results-header">
        <th>ID</th>

        <th class="compras-detalle-columna-codigo">
            C&oacute;digo presentado
        </th>

        <th class="compras-detalle-columna-codigo">
            Descripci&oacute;n
        </th>

        <th>Cantidad</th>

        <th class="compras-detalle-columna-observacion">
            Observaciones
        </th>

        <% if (puedeVerCotizacionDetalle) { %>
            <th>Precio unitario</th>
            <th>Total</th>
        <% } %>

        <% if (puedeABMDetalle) { %>
            <th>Acciones</th>
        <% } %>
    </tr>

    <tbody id="<portlet:namespace />detalle_body">
        <tr class="portlet-section-body results-row">
            <td colspan="<%= detalleColspan %>">
                Cargando detalles...
            </td>
        </tr>
    </tbody>
</table>

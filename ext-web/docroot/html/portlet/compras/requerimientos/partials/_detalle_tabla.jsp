<table class="lfr-table taglib-search-iterator compras-detalle-tabla"
       width="100%">

    <tr class="portlet-section-header results-header">

        <th>Tipo</th>

        <th class="compras-detalle-columna-codigo">
            Código presentado
        </th>

        <th class="compras-detalle-columna-codigo">
            Descripción
        </th>

        <th>Cantidad</th>

        <th class="compras-detalle-columna-observacion">
            Observaciones
        </th>

        <% if (puedeVerCotizacionDetalle) { %>
            <th>Precio unitario</th>
            <th>Total</th>
        <% } %>

        <% if (puedeABMDetalle || puedeEliminarDetalle) { %>
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

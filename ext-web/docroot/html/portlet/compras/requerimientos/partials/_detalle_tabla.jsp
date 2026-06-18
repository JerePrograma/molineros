<table class="lfr-table taglib-search-iterator" width="100%">
    <tr class="portlet-section-header results-header">
        <th>ID</th>
        <th>Art&iacute;culo</th>
        <th>Cantidad</th>
        <th>Descripcion</th>

        <% if (puedeVerCotizacionDetalle) { %>
            <th>Precio unitario</th>
            <th>Total</th>
            <th>Prestador adjudicado</th>
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

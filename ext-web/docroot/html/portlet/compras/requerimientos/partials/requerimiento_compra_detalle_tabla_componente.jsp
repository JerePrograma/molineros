<%--
Responsabilidad:
    Renderiza la estructura tabular y el cuerpo namespaced de prestaciones.
Incluido desde:
    requerimiento_compra_detalle_embebido.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    detalle_body
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
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

<% if (puedeCotizarDetalle
        && !WebKeysCompras.isEmpty(
                errorPrestadoresEnviadosDetalle
        )) { %>

    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(
                errorPrestadoresEnviadosDetalle
        ) %>
    </div>

<% } else if (puedeCotizarDetalle
        && !hayPrestadoresEnviadosDetalle) { %>

    <div class="portlet-msg-info">
        No hay prestadores notificados correctamente para este requerimiento.
    </div>

<% } %>

<% if (prestadoresAdjudicadosMixtosDetalle) { %>
    <div class="portlet-msg-error">
        El requerimiento contiene prestadores adjudicados diferentes.
        Seleccione un único prestador antes de guardar la cotización.
    </div>
<% } %>

<% if (puedeVerCotizacionDetalle) { %>
    <fieldset class="block-labels">
        <legend>Adjudicación</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace />id_prestador_adjudicado">
                        Prestador adjudicado:
                    </label>
                </td>
                <td>
                    <% if (puedeCotizarDetalle) { %>
                        <select id="<portlet:namespace />id_prestador_adjudicado"
                                style="max-width: 520px; width: 100%;"
                                onchange="<portlet:namespace />capturarPrestadorAdjudicado();"
                                <%= hayPrestadoresEnviadosDetalle
                                        ? ""
                                        : "disabled=\"disabled\"" %>>
                            <option value="">Seleccione...</option>

                            <%
                            for (int i = 0;
                                    i < prestadoresEnviadosDetalle.size();
                                    i++) {

                                PrestadorCotizacion prestador =
                                        prestadoresEnviadosDetalle.get(i);

                                if (prestador == null
                                        || prestador.getIdPrestador() <= 0) {
                                    continue;
                                }

                                String idPrestador =
                                        String.valueOf(
                                                prestador.getIdPrestador()
                                        );
                            %>
                                <option value="<%= idPrestador %>"
                                        <%= idPrestador.equals(
                                                idPrestadorAdjudicadoDetalle
                                        ) ? "selected=\"selected\"" : "" %>>
                                    <%= HtmlUtil.escape(
                                            prestador.getEtiquetaVisible()
                                    ) %>
                                </option>
                            <%
                            }
                            %>
                        </select>
                    <% } else { %>
                        <strong>
                            <%= WebKeysCompras.isEmpty(
                                    prestadorAdjudicadoDetalle
                            )
                                    ? "Sin adjudicar"
                                    : HtmlUtil.escape(
                                            prestadorAdjudicadoDetalle
                                    ) %>
                        </strong>
                    <% } %>
                </td>
            </tr>
        </table>
    </fieldset>
<% } %>

<table class="lfr-table taglib-search-iterator"
       width="100%">

    <tr class="portlet-section-header results-header">
        <th>ID</th>
        <th>Código presentado</th>
        <th>Descripción</th>
        <th>Cantidad</th>
        <th>Observaciones</th>

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

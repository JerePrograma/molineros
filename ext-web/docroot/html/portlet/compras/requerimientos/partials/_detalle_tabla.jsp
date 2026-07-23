<%
java.util.Set<Integer> idsPrestadoresConPresupuestoDetalle =
        new java.util.HashSet<Integer>();
java.util.Set<Integer> idsPrestadoresHabilitadosDetalle =
        new java.util.HashSet<Integer>();
String errorPresupuestosPrestadoresDetalle = "";

if (puedeCotizarDetalle
        && requerimientoPersistidoDetalle
        && hayPrestadoresEnviadosDetalle) {

    try {
        java.util.List<ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto>
                presupuestosPrestadoresDetalle =
                        BusquedaRequerimientoCompraServiceUtil
                                .listarPresupuestos(
                                        idRequerimientoCompraDetalle
                                );

        for (int i = 0;
                presupuestosPrestadoresDetalle != null
                        && i < presupuestosPrestadoresDetalle.size();
                i++) {

            ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto
                    presupuestoDetalle =
                            presupuestosPrestadoresDetalle.get(i);

            if (presupuestoDetalle == null
                    || presupuestoDetalle.getBajaFecha() != null
                    || presupuestoDetalle.getIdPrestador() == null
                    || presupuestoDetalle.getIdPrestador().intValue() <= 0
                    || presupuestoDetalle.getDlFileEntryId() == null
                    || presupuestoDetalle.getDlFileEntryId().longValue()
                            <= 0L) {

                continue;
            }

            idsPrestadoresConPresupuestoDetalle.add(
                    presupuestoDetalle.getIdPrestador()
            );
        }
    } catch (Exception e) {
        errorPresupuestosPrestadoresDetalle =
                "No se pudo verificar qu\u00e9 prestadores tienen un archivo "
                        + "de presupuesto cargado.";
    }
}

for (int i = 0;
        prestadoresEnviadosDetalle != null
                && i < prestadoresEnviadosDetalle.size();
        i++) {

    PrestadorCotizacion prestadorDetalle =
            prestadoresEnviadosDetalle.get(i);

    if (prestadorDetalle != null
            && prestadorDetalle.getIdPrestador() > 0
            && idsPrestadoresConPresupuestoDetalle.contains(
                    Integer.valueOf(
                            prestadorDetalle.getIdPrestador()
                    )
            )) {

        idsPrestadoresHabilitadosDetalle.add(
                Integer.valueOf(
                        prestadorDetalle.getIdPrestador()
                )
        );
    }
}

boolean hayPrestadoresHabilitadosDetalle =
        !idsPrestadoresHabilitadosDetalle.isEmpty();

if (puedeCotizarDetalle
        && !WebKeysCompras.isEmpty(
                idPrestadorAdjudicadoDetalle
        )) {

    try {
        int idPrestadorAdjudicadoActual =
                Integer.parseInt(
                        idPrestadorAdjudicadoDetalle
                );

        if (!idsPrestadoresHabilitadosDetalle.contains(
                Integer.valueOf(
                        idPrestadorAdjudicadoActual
                )
        )) {

            idPrestadorAdjudicadoDetalle = "";
            prestadorAdjudicadoDetalle = "";
        }
    } catch (NumberFormatException e) {
        idPrestadorAdjudicadoDetalle = "";
        prestadorAdjudicadoDetalle = "";
    }
}
%>

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

<% } else if (puedeCotizarDetalle
        && !WebKeysCompras.isEmpty(
                errorPresupuestosPrestadoresDetalle
        )) { %>

    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(
                errorPresupuestosPrestadoresDetalle
        ) %>
    </div>

<% } else if (puedeCotizarDetalle
        && !hayPrestadoresHabilitadosDetalle) { %>

    <div class="portlet-msg-info">
        No hay cotizaciones cargadas para poder seleccionar un prestador adjudicado
    </div>

<% } %>

<% if (prestadoresAdjudicadosMixtosDetalle) { %>
    <div class="portlet-msg-error">
        El requerimiento contiene prestadores adjudicados diferentes.
        Seleccione un &uacute;nico prestador antes de guardar la cotizaci&oacute;n.
    </div>
<% } %>

<% if (puedeVerCotizacionDetalle) { %>
    <fieldset class="block-labels">
        <legend>Adjudicaci&oacute;n</legend>

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
                                <%= hayPrestadoresHabilitadosDetalle
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
                                boolean prestadorHabilitado =
                                        idsPrestadoresHabilitadosDetalle
                                                .contains(
                                                        Integer.valueOf(
                                                                prestador
                                                                        .getIdPrestador()
                                                        )
                                                );
                            %>
                                <option value="<%= idPrestador %>"
                                        <%= prestadorHabilitado
                                                && idPrestador.equals(
                                                        idPrestadorAdjudicadoDetalle
                                                )
                                                        ? "selected=\"selected\""
                                                        : "" %>
                                        <%= prestadorHabilitado
                                                ? ""
                                                : "disabled=\"disabled\"" %>>
                                    <%= HtmlUtil.escape(
                                            prestador.getEtiquetaVisible()
                                    ) %><%= prestadorHabilitado
                                            ? ""
                                            : " (sin presupuesto cargado)" %>
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
        <th class="compras-detalle-columna-codigo">C&oacute;digo presentado</th>
        <th class="compras-detalle-columna-codigo">Descripci&oacute;n</th>
        <th>Cantidad</th>
        <th class="compras-detalle-columna-observacion">Observaciones</th>

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

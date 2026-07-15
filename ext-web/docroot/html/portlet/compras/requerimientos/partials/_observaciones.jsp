<%
String sectorObservaciones =
        WebKeysCompras
                .normalizarSectorCompra(
                        req.getSectorDescripcion()
                );

boolean sectorSeleccionadoObservaciones =
        req.getIdSector() != null
        && req.getIdSector().intValue() > 0;

boolean sectorUsaCodigoPrestacionObservaciones =
        WebKeysCompras
                .getFiltroTipoNomencladorCompras(
                        sectorObservaciones
                ) != null;

/*
 * En un alta sin sector seleccionado se mantiene oculto.
 *
 * En edición y vista:
 * - sector con Código/Prestación: oculto;
 * - sector sin Código/Prestación: visible.
 */
boolean mostrarPanelObservaciones =
        sectorSeleccionadoObservaciones
        && !sectorUsaCodigoPrestacionObservaciones;
%>

<div id="<portlet:namespace />observaciones_panel"
     style="<%= mostrarPanelObservaciones
             ? ""
             : "display:none;" %>">

    <fieldset class="block-labels">
        <legend>Observaciones / Descripción</legend>

        <% if (puedeEditarEstructuraPantalla) { %>
            <table class="lfr-table">
                <tr>
                    <td>
                        <textarea id="<portlet:namespace />observaciones"
                                  cols="100"
                                  rows="4"><%= HtmlUtil.escape(
                                          req.getObservacionesVisible()
                                  ) %></textarea>
                    </td>
                </tr>
            </table>
        <% } else { %>
            <div class="compras-observaciones-vista">
                <%= HtmlUtil.escape(
                        req.getObservacionesVisible()
                ) %>
            </div>
        <% } %>
    </fieldset>
</div>
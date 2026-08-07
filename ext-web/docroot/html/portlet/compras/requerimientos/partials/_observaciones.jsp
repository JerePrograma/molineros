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
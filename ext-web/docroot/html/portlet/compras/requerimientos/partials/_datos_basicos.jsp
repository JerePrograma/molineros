<fieldset class="block-labels">
    <legend><%= tituloPantalla %></legend>

    <table class="lfr-table">
        <tr>
            <td><label>ID:</label></td>
            <td><%= HtmlUtil.escape(req.getIdString()) %></td>

            <td><label>Estado:</label></td>
            <td colspan="3">
                <strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Sector:</label></td>
            <td colspan="5">
                <% if (modoEditable) { %>
                    <select id="<portlet:namespace />sector_id"
                            onChange="<portlet:namespace />cambiarSectorCompra(true);">
                        <option value="0" data-requiere-afiliado="false">Seleccione</option>

                        <%
                        for (int i = 0; i < sectores.size(); i++) {
                            RequerimientoCompraSector sector = sectores.get(i);
                            String sectorId = String.valueOf(sector.getIdSector());
                            String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
                        %>
                            <option value="<%= sectorId %>"
                                    data-requiere-afiliado="<%= requiereAfiliado %>"
                                    <%= selected %>>
                                <%= HtmlUtil.escape(sector.getDescripcionVisible()) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                <% } else { %>
                    <div class="compras-campo-solo-lectura">
                        <%= HtmlUtil.escape(sectorDescripcionSoloLectura) %>
                    </div>
                <% } %>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr id="<portlet:namespace />fila_cargos_compra"
            style="<%= modoEditable && sectorSinAfiliadoForzaCargoOspim ? "display:none;" : "" %>">
            <td><label>Cargo OSPIM %:</label></td>
            <td>
                <% if (modoEditable) { %>
                    <input type="text"
                           id="<portlet:namespace />cargo_ospim"
                           value="<%= HtmlUtil.escape(cargoOspimVisible) %>"
                           size="5"
                           maxlength="3"
                           onkeyup="<portlet:namespace />sincronizarFormularioCompra();"
                           onchange="<portlet:namespace />sincronizarFormularioCompra();"
                           onblur="<portlet:namespace />sincronizarFormularioCompra();" />
                <% } else { %>
                    <div class="compras-campo-solo-lectura"><%= HtmlUtil.escape(cargoOspimVisible) %></div>
                <% } %>
            </td>

            <td><label>Cargo tercerizadora %:</label></td>
            <td>
                <% if (modoEditable) { %>
                    <input type="text"
                           id="<portlet:namespace />cargo_tercerizadora"
                           value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>"
                           size="5"
                           maxlength="3"
                           onchange="<portlet:namespace />sincronizarFormularioCompra();"
                           onblur="<portlet:namespace />sincronizarFormularioCompra();" />
                <% } else { %>
                    <div class="compras-campo-solo-lectura"><%= HtmlUtil.escape(cargoTercerizadoraVisible) %></div>
                <% } %>
            </td>

            <td><label>Recupero:</label></td>
            <td>
                <% if (modoEditable) { %>
                    <input type="checkbox"
                           id="<portlet:namespace />recupero"
                           value="true"
                           <%= recuperoChecked %>
                           onclick="return false;"
                           onkeydown="return false;"
                           tabindex="-1"
                           aria-disabled="true" />
                <% } else { %>
                    <div class="compras-campo-solo-lectura">
                        <%= recuperoPorCargoTercerizadoraActual ? "Sí" : "No" %>
                    </div>
                <% } %>
            </td>
        </tr>
    </table>

    <% if (modoEditable) { %>
        <input type="hidden"
               id="<portlet:namespace />requerimiento_id_tercerizadora"
               value="<%= HtmlUtil.escape(idTercerizadora) %>" />
    <% } %>
</fieldset>

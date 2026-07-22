<fieldset class="block-labels">
    <legend><%= tituloPantalla %></legend>

    <table class="lfr-table compras-resumen-requerimiento">
        <tr>
            <td><label>ID:</label></td>
            <td><%= HtmlUtil.escape(req.getIdString()) %></td>

            <td><label>Estado:</label></td>
            <td>
                <strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong>
            </td>

            <td><label for="<portlet:namespace />sector_id">Sector:</label></td>
            <td>
                <% if (puedeEditarEstructuraPantalla) { %>
                    <select id="<portlet:namespace />sector_id"
                            onchange="<portlet:namespace />cambiarSectorCompra(true);">
                        <option value="0"
                                data-requiere-afiliado="false"
                                data-usa-codigo-prestacion="false">
                            Seleccione
                        </option>

                        <%
                        for (int i = 0; i < sectores.size(); i++) {
                            RequerimientoCompraSector sector =
                                    sectores.get(i);

                            String sectorId =
                                    String.valueOf(
                                            sector.getIdSector()
                                    );

                            String selected =
                                    reqSectorId.equals(sectorId)
                                            ? "selected=\"selected\""
                                            : "";

                            String requiereAfiliado =
                                    sector.isRequiereAfiliado()
                                            ? "true"
                                            : "false";

                            boolean usaCodigoPrestacion =
                                    WebKeysCompras
                                            .getFiltroTipoNomencladorCompras(
                                                    sector.getDescripcion()
                                            ) != null;

                            String usaCodigoPrestacionAttr =
                                    usaCodigoPrestacion
                                            ? "true"
                                            : "false";
                        %>
                            <option value="<%= sectorId %>"
                                    data-requiere-afiliado="<%= requiereAfiliado %>"
                                    data-usa-codigo-prestacion="<%= usaCodigoPrestacionAttr %>"
                                    <%= selected %>>
                                <%= HtmlUtil.escape(
                                        sector.getDescripcionVisible()
                                ) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                <% } else { %>
                    <div class="compras-campo-solo-lectura">
                        <%= HtmlUtil.escape(
                                sectorDescripcionSoloLectura
                        ) %>
                    </div>
                <% } %>
            </td>
        </tr>
    </table>

    <table class="lfr-table compras-cargos-requerimiento">
        <tr>
            <td style="padding: 0; vertical-align: middle;">
                <div id="<portlet:namespace />fila_cargos_compra"
                     style="<%= puedeEditarEstructuraPantalla && sectorSinAfiliadoForzaCargoOspim ? "display:none;" : "" %>">
                    <table class="lfr-table">
                        <tr>
                            <td><label>Cargo OSPIM %:</label></td>
                            <td>
                                <% if (puedeEditarEstructuraPantalla) { %>
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
                                <% if (puedeEditarEstructuraPantalla) { %>
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
                                <% if (puedeEditarEstructuraPantalla) { %>
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
                </div>
            </td>

            <td style="vertical-align: middle;">
                <label for="<portlet:namespace />surge">
                    Surge:
                </label>
            </td>
            <td style="vertical-align: middle;">
                <% if (puedeEditarEstructuraPantalla) { %>
                    <select id="<portlet:namespace />surge"
                            required="required"
                            aria-required="true"
                            onchange="<portlet:namespace />actualizarSurgeCompra();">
                        <option value=""
                                <%= "".equals(surgeSeleccionadoCompra)
                                        ? "selected=\"selected\""
                                        : "" %>>
                            Seleccione
                        </option>

                        <option value="1"
                                <%= "1".equals(surgeSeleccionadoCompra)
                                        ? "selected=\"selected\""
                                        : "" %>>
                            SI
                        </option>

                        <option value="0"
                                <%= "0".equals(surgeSeleccionadoCompra)
                                        ? "selected=\"selected\""
                                        : "" %>>
                            NO
                        </option>
                    </select>
                <% } else { %>
                    <div class="compras-campo-solo-lectura">
                        <%= HtmlUtil.escape(
                                req.getSurgeDescripcion()
                        ) %>
                    </div>
                <% } %>
            </td>
        </tr>
    </table>

    <% if (puedeEditarEstructuraPantalla) { %>
        <input type="hidden"
               id="<portlet:namespace />requerimiento_id_tercerizadora"
               value="<%= HtmlUtil.escape(idTercerizadora) %>" />
    <% } %>
</fieldset>

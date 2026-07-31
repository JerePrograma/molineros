<fieldset class="block-labels">
    <legend><%= tituloPantalla %></legend>

    <table class="lfr-table compras-resumen-requerimiento compras-cargos-requerimiento compras-datos-basicos-requerimiento"
           style="width: 100%;">
        <tr>
            <td>
                <label for="<portlet:namespace />requerimiento_id_visual">
                    ID:
                </label>
            </td>
            <td>
                <input type="text"
                       id="<portlet:namespace />requerimiento_id_visual"
                       value="<%= HtmlUtil.escape(req.getIdString()) %>"
                       size="10"
                       readonly="readonly"
                       class="compras-campo-solo-lectura" />
            </td>

            <td>
                <label for="<portlet:namespace />estado_visual">
                    Estado:
                </label>
            </td>
            <td>
                <input type="text"
                       id="<portlet:namespace />estado_visual"
                       value="<%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %>"
                       size="20"
                       readonly="readonly"
                       class="compras-campo-solo-lectura"
                       style="font-weight: bold;" />
            </td>

            <td>
                <label for="<portlet:namespace />sector_id">
                    Sector:
                </label>
            </td>
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
                    <input type="text"
                           id="<portlet:namespace />sector_id"
                           value="<%= HtmlUtil.escape(sectorDescripcionSoloLectura) %>"
                           size="25"
                           readonly="readonly"
                           class="compras-campo-solo-lectura" />
                <% } %>
            </td>
        </tr>

        <tr>
            <td colspan="4"
                style="padding: 0; vertical-align: middle;">
                <div id="<portlet:namespace />fila_cargos_compra"
                     style="<%= puedeEditarEstructuraPantalla && sectorSinAfiliadoForzaCargoOspim ? "display:none;" : "" %>">
                    <table class="lfr-table"
                           style="width: 100%;">
                        <tr>
                            <td>
                                <label for="<portlet:namespace />cargo_ospim">
                                    Cargo OSPIM %:
                                </label>
                            </td>
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
                                    <input type="text"
                                           id="<portlet:namespace />cargo_ospim"
                                           value="<%= HtmlUtil.escape(cargoOspimVisible) %>"
                                           size="5"
                                           readonly="readonly"
                                           class="compras-campo-solo-lectura" />
                                <% } %>
                            </td>

                            <td>
                                <label for="<portlet:namespace />cargo_tercerizadora">
                                    Cargo tercerizadora %:
                                </label>
                            </td>
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
                                    <input type="text"
                                           id="<portlet:namespace />cargo_tercerizadora"
                                           value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>"
                                           size="5"
                                           readonly="readonly"
                                           class="compras-campo-solo-lectura" />
                                <% } %>
                            </td>

                            <%--
                                CQA-005: Recupero se conserva en edicion y
                                vista, pero no se muestra durante el alta.
                            --%>
                            <% if (!esNuevo) { %>
                                <td>
                                    <label for="<portlet:namespace />recupero">
                                        Recupero:
                                    </label>
                                </td>
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
                                        <input type="text"
                                               id="<portlet:namespace />recupero"
                                               value="<%= recuperoPorCargoTercerizadoraActual ? "S&iacute;" : "No" %>"
                                               size="5"
                                               readonly="readonly"
                                               class="compras-campo-solo-lectura" />
                                    <% } %>
                                </td>
                            <% } %>
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
                    <input type="text"
                           id="<portlet:namespace />surge"
                           value="<%= HtmlUtil.escape(req.getSurgeDescripcion()) %>"
                           size="12"
                           readonly="readonly"
                           class="compras-campo-solo-lectura" />
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

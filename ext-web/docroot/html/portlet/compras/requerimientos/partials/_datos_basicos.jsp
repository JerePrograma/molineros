<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%
Object soloLecturaCargosAttr =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String modoCargosPantalla =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

String strutsActionCargosPantalla =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

boolean cargosSoloLecturaPantalla =
        Boolean.TRUE.equals(
                soloLecturaCargosAttr
        )
        || ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        )
        || "ver".equalsIgnoreCase(
                modoCargosPantalla
        )
        || "/compras/ver_requerimiento".equals(
                strutsActionCargosPantalla
        );

Object relacionReclamoCabeceraAttr =
        renderRequest.getAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_COMPRA
        );

RequerimientoCompraReclamoPrestacional relacionReclamoCabecera =
        relacionReclamoCabeceraAttr
                instanceof RequerimientoCompraReclamoPrestacional
                ? (RequerimientoCompraReclamoPrestacional)
                        relacionReclamoCabeceraAttr
                : null;

String fechaAltaCabecera =
        req != null
                ? req.getAltaFechaAsString()
                : "";

boolean mostrarFechaAltaCabecera =
        cargosSoloLecturaPantalla
        && !WebKeysCompras.isEmpty(
                fechaAltaCabecera
        );

boolean mostrarIdRpCabecera =
        cargosSoloLecturaPantalla
        && relacionReclamoCabecera != null
        && relacionReclamoCabecera.isVinculado()
        && relacionReclamoCabecera
                .getIdReclamoPrestacionalInt() > 0;

int idRpCabecera =
        mostrarIdRpCabecera
                ? relacionReclamoCabecera
                        .getIdReclamoPrestacionalInt()
                : 0;
%>

<style type="text/css">
    /*
     * Estilos limitados exclusivamente a la cabecera de Compras.
     * No modificar inputs, selects ni labels de otras pantallas.
     */
    .compras-cabecera-requerimiento {
        margin-bottom: 8px;
    }

    .compras-cabecera-requerimiento
    .compras-datos-basicos-requerimiento td {
        padding-top: 6px;
        padding-bottom: 6px;
        vertical-align: middle;
    }

    .compras-cabecera-requerimiento
    .compras-celda-label {
        padding-right: 10px;
        white-space: nowrap;
    }

    .compras-cabecera-requerimiento
    .compras-celda-control {
        padding-right: 32px;
    }

    .compras-cabecera-requerimiento
    .compras-celda-control-final {
        padding-right: 0;
    }

    .compras-cabecera-requerimiento
    .compras-celda-label label,
    .compras-cabecera-requerimiento
    .compras-grupo-cargo label {
        display: inline-block;
        margin: 0;
        line-height: 24px;
        vertical-align: middle;
    }

    /*
     * Anchos controlados. No depender del atributo size.
     */
    .compras-cabecera-requerimiento
    .compras-control-id {
        width: 125px;
    }

    .compras-cabecera-requerimiento
    .compras-control-estado {
        width: 180px;
        font-weight: bold;
    }

    .compras-cabecera-requerimiento
    .compras-control-sector {
        width: 220px;
        max-width: 100%;
    }

    .compras-cabecera-requerimiento
    .compras-control-porcentaje {
        width: 70px;
    }

    .compras-cabecera-requerimiento
    .compras-control-surge {
        width: 120px;
    }

    /*
     * Segunda fila: cargos y Surge.
     */
    .compras-cabecera-requerimiento
    .compras-cargos-celda {
        padding-right: 32px;
    }

    .compras-cabecera-requerimiento
    .compras-fila-cargos {
        display: block !important;
    }

    .compras-cabecera-requerimiento
    .compras-grupo-cargo {
        display: inline-block;
        margin-right: 34px;
        white-space: nowrap;
        vertical-align: middle;
    }

    .compras-cabecera-requerimiento
    .compras-grupo-cargo label {
        margin-right: 10px;
    }
</style>

<fieldset class="block-labels compras-cabecera-requerimiento compras-seccion compras-seccion-datos-basicos">
    <legend><%= tituloPantalla %></legend>

    <table class="lfr-table compras-resumen-requerimiento compras-cargos-requerimiento compras-datos-basicos-requerimiento">
        <tr>
            <td class="compras-celda-label">
                <label for="<portlet:namespace />requerimiento_id_visual">
                    ID:
                </label>
            </td>

            <td class="compras-celda-control">
                <input type="text"
                       id="<portlet:namespace />requerimiento_id_visual"
                       value="<%= HtmlUtil.escape(req.getIdString()) %>"
                       readonly="readonly"
                       class="compras-control compras-control-id compras-campo-solo-lectura" />
            </td>

            <td class="compras-celda-label">
                <label for="<portlet:namespace />estado_visual">
                    Estado:
                </label>
            </td>

            <td class="compras-celda-control">
                <input type="text"
                       id="<portlet:namespace />estado_visual"
                       value="<%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %>"
                       readonly="readonly"
                       class="compras-control compras-control-estado compras-campo-solo-lectura" />
            </td>

            <td class="compras-celda-label">
                <label for="<portlet:namespace />sector_id">
                    Sector:
                </label>
            </td>

            <td class="compras-celda-control compras-celda-control-final">
                <% if (puedeEditarEstructuraPantalla) { %>
                    <select id="<portlet:namespace />sector_id"
                            class="compras-control compras-control-sector"
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
                           readonly="readonly"
                           class="compras-control compras-control-sector compras-campo-solo-lectura" />
                <% } %>
            </td>
        </tr>

        <% if (mostrarFechaAltaCabecera || mostrarIdRpCabecera) { %>
            <tr>
                <td class="compras-celda-label">
                    <strong>Fecha Alta:</strong>
                </td>

                <td class="compras-celda-control">
                    <span class="compras-campo-solo-lectura">
                        <%= HtmlUtil.escape(
                                fechaAltaCabecera
                        ) %>
                    </span>
                </td>

                <% if (mostrarIdRpCabecera) { %>

                    <td class="compras-celda-label">
                        <strong>Id RP:</strong>
                    </td>

                    <td class="compras-celda-control">
                        <span class="compras-nro-rp">
                            <%= idRpCabecera %>
                        </span>
                    </td>

                    <td colspan="2"
                        class="compras-celda-control compras-celda-control-final">
                        &nbsp;
                    </td>

                <% } else { %>

                    <td colspan="4"
                        class="compras-celda-control compras-celda-control-final">
                        &nbsp;
                    </td>

                <% } %>
            </tr>
        <% } %>

        <tr>
            <td colspan="4"
                class="compras-cargos-celda">

                <div id="<portlet:namespace />fila_cargos_compra"
                     class="compras-fila-cargos">

                    <span class="compras-grupo-cargo">
                        <label for="<portlet:namespace />cargo_ospim">
                            Cargo OSPIM %:
                        </label>

                        <% if (!cargosSoloLecturaPantalla) { %>
                            <input type="text"
                                   id="<portlet:namespace />cargo_ospim"
                                   value="<%= HtmlUtil.escape(cargoOspimVisible) %>"
                                   maxlength="3"
                                   class="compras-control compras-control-porcentaje"
                                   onkeyup="<portlet:namespace />sincronizarFormularioCompra();"
                                   onchange="<portlet:namespace />sincronizarFormularioCompra();"
                                   onblur="<portlet:namespace />sincronizarFormularioCompra();" />
                        <% } else { %>
                            <input type="text"
                                   id="<portlet:namespace />cargo_ospim"
                                   value="<%= HtmlUtil.escape(cargoOspimVisible) %>"
                                   maxlength="3"
                                   readonly="readonly"
                                   class="compras-control compras-control-porcentaje compras-campo-solo-lectura" />
                        <% } %>
                    </span>

                    <span class="compras-grupo-cargo">
                        <label for="<portlet:namespace />cargo_tercerizadora">
                            Cargo tercerizadora %:
                        </label>

                        <% if (!cargosSoloLecturaPantalla) { %>
                            <input type="text"
                                   id="<portlet:namespace />cargo_tercerizadora"
                                   value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>"
                                   maxlength="3"
                                   class="compras-control compras-control-porcentaje"
                                   onchange="<portlet:namespace />sincronizarFormularioCompra();"
                                   onblur="<portlet:namespace />sincronizarFormularioCompra();" />
                        <% } else { %>
                            <input type="text"
                                   id="<portlet:namespace />cargo_tercerizadora"
                                   value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>"
                                   maxlength="3"
                                   readonly="readonly"
                                   class="compras-control compras-control-porcentaje compras-campo-solo-lectura" />
                        <% } %>
                    </span>

                </div>
            </td>

            <td class="compras-celda-label">
                <label for="<portlet:namespace />surge">
                    Surge:
                </label>
            </td>

            <td class="compras-celda-control compras-celda-control-final">
                <% if (puedeEditarEstructuraPantalla) { %>
                    <select id="<portlet:namespace />surge"
                            class="compras-control compras-control-surge"
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
                           readonly="readonly"
                           class="compras-control compras-control-surge compras-campo-solo-lectura" />
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
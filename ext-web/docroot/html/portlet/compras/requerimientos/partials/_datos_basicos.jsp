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

boolean cargosEditablesPantalla =
        puedeEditarEstructuraPantalla
        && !cargosSoloLecturaPantalla;

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
     * ==========================================================
     * CABECERA DE REQUERIMIENTO DE COMPRA
     * ==========================================================
     *
     * Estilos limitados exclusivamente a esta cabecera.
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

    /*
     * Labels generales.
     */
    .compras-cabecera-requerimiento
    .compras-celda-label {
        padding-right: 10px;
        white-space: nowrap;
    }

    /*
     * Controles generales.
     */
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
     * ==========================================================
     * ANCHOS DE LOS CONTROLES
     * ==========================================================
     *
     * No depender del atributo HTML "size".
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
     * ==========================================================
     * ID RP
     * ==========================================================
     *
     * Se mantiene como columnas reales de la tabla.
     * De esta forma no se rompe la estructura HTML ni cambia
     * arbitrariamente según la resolución.
     */

    .compras-cabecera-requerimiento
    .compras-celda-label-rp {
        padding-left: 16px;
        padding-right: 8px;
        white-space: nowrap;
    }

    .compras-cabecera-requerimiento
    .compras-celda-label-rp strong {
        display: inline-block;
        margin: 0;
        font-size: 16px;
        font-weight: bold;
        line-height: 28px;
        vertical-align: middle;
        white-space: nowrap;
    }

    .compras-cabecera-requerimiento
    .compras-celda-rp {
        white-space: nowrap;
        vertical-align: middle;
    }

    .compras-cabecera-requerimiento
    .compras-nro-rp {
        display: inline-block;
        font-size: 24px;
        font-weight: bold;
        line-height: 28px;
        vertical-align: middle;
        white-space: nowrap;
    }

    /*
     * ==========================================================
     * SEGUNDA FILA: CARGOS Y SURGE
     * ==========================================================
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

    /*
     * Las dos últimas columnas pertenecen al bloque Id RP.
     * En la segunda fila quedan simplemente vacías.
     */
    .compras-cabecera-requerimiento
    .compras-celda-rp-vacia {
        padding: 0;
    }

    /*
     * ==========================================================
     * SITUACION MEDICA
     * ==========================================================
     */

    .compras-cabecera-requerimiento
    .compras-celda-situacion-medica {
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
    }

    .compras-cabecera-requerimiento
    .compras-boton-situacion-medica {
        min-width: 150px;
    }

</style>

<fieldset class="block-labels compras-cabecera-requerimiento compras-seccion compras-seccion-datos-basicos">

    <legend>
        <%= tituloPantalla %>
    </legend>

    <table class="lfr-table compras-resumen-requerimiento compras-cargos-requerimiento compras-datos-basicos-requerimiento">

        <%--
         * ======================================================
         * PRIMERA FILA
         *
         * 8 columnas:
         *
         * 1 - ID label
         * 2 - ID control
         * 3 - Estado label
         * 4 - Estado control
         * 5 - Sector label
         * 6 - Sector control
         * 7 - Id RP label
         * 8 - Id RP valor
         * ======================================================
         --%>

        <tr>

            <%-- ID --%>

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


            <%-- ESTADO --%>

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


            <%-- SECTOR --%>

            <td class="compras-celda-label">
                <label for="<portlet:namespace />sector_id">
                    Sector:
                </label>
            </td>

            <td class="compras-celda-control">

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


            <%-- ID RP --%>

            <% if (mostrarIdRpCabecera) { %>

                <td class="compras-celda-label compras-celda-label-rp">
                    <strong>
                        Id RP:
                    </strong>
                </td>

                <td class="compras-celda-control compras-celda-control-final compras-celda-rp">

                    <span class="compras-nro-rp">
                        <%= idRpCabecera %>
                    </span>

                </td>

            <% } else { %>

                <td colspan="2"
                    class="compras-celda-control compras-celda-control-final compras-celda-rp-vacia">
                    &nbsp;
                </td>

            <% } %>

        </tr>


        <%--
         * ======================================================
         * SEGUNDA FILA
         *
         * Mantiene también exactamente 8 columnas:
         *
         * Cargos            = colspan 4
         * Surge label       = 1
         * Surge control     = 1
         * Espacio RP        = colspan 2
         *
         * De esta manera Surge sigue alineado con Sector.
         * ======================================================
         --%>

        <tr>

            <%-- CARGOS --%>

            <td colspan="4"
                class="compras-cargos-celda">

                <div id="<portlet:namespace />fila_cargos_compra"
                     class="compras-fila-cargos">


                    <%-- CARGO OSPIM --%>

                    <span class="compras-grupo-cargo">

                        <label for="<portlet:namespace />cargo_ospim">
                            Cargo OSPIM %:
                        </label>

                        <% if (cargosEditablesPantalla) { %>

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


                    <%-- CARGO TERCERIZADORA --%>

                    <span class="compras-grupo-cargo">

                        <label for="<portlet:namespace />cargo_tercerizadora">
                            Cargo tercerizadora %:
                        </label>

                        <% if (cargosEditablesPantalla) { %>

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


            <%-- SURGE --%>

            <td class="compras-celda-label">

                <label for="<portlet:namespace />surge">
                    Surge:
                </label>

            </td>

            <td class="compras-celda-control">

                <% if (puedeEditarSurgePantalla) { %>

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


            <%--
             * Espacio correspondiente a las dos columnas
             * utilizadas por Id RP en la primera fila.
             --%>

            <td colspan="2"
                class="compras-celda-control compras-celda-control-final compras-celda-situacion-medica">

                <input type="button"
                       id="<portlet:namespace />btnSituacionMedica"
                       class="compras-boton-situacion-medica"
                       value="Situación Médica"
                       title="Ver situación médica vigente"
                       onclick="return <portlet:namespace />abrirSituacionMedicaAfiliado();"
                       style="display:none;" />

            </td>

        </tr>

    </table>


    <%--
     * ID TERCERIZADORA
     --%>

    <% if (puedeEditarEstructuraPantalla) { %>

        <input type="hidden"
               id="<portlet:namespace />requerimiento_id_tercerizadora"
               value="<%= HtmlUtil.escape(idTercerizadora) %>" />

    <% } %>

</fieldset>
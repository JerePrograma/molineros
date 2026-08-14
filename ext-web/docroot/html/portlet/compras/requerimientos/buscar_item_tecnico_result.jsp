<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Nomenclador" %>
<%@ page import="ar.com.ospim.autorizaciones.services.NomencladorServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>

<%!
private String comprasNomencladorJs(String value) {
    if (value == null) {
        return "";
    }

    return value.replace("\\", "\\\\")
            .replace("\"", "\\x22")
            .replace("'", "\\x27")
            .replace("&", "\\x26")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}

private String comprasNomencladorInvocacion(
        String callback,
        Nomenclador nomenclador) {

    return callback
            + "(" + nomenclador.getId_prestacion()
            + "," + nomenclador.getId_tipo_nomenclador()
            + ",'" + comprasNomencladorJs(
                    nomenclador.getCodigo() != null
                            ? nomenclador.getCodigo().trim()
                            : ""
            )
            + "','" + comprasNomencladorJs(
                    nomenclador.getDescripcion() != null
                            ? nomenclador.getDescripcion().trim()
                            : ""
            )
            + "')";
}
%>

<%
String errorBusqueda =
        (String) request.getAttribute(
                "COMPRAS_ERROR_BUSQUEDA"
        );

String callbackBusqueda =
        (String) request.getAttribute(
                "COMPRAS_CALLBACK_BUSQUEDA"
        );

String codigoBusqueda =
        (String) request.getAttribute(
                "COMPRAS_CODIGO_NOMENCLADOR"
        );

String descripcionBusqueda =
        (String) request.getAttribute(
                "COMPRAS_DESCRIPCION_NOMENCLADOR"
        );

String sectorBusqueda =
        (String) request.getAttribute(
                "COMPRAS_SECTOR_NOMENCLADOR"
        );

String marcaReinLiqBusqueda =
        (String) request.getAttribute(
                "COMPRAS_MARCA_REIN_LIQ"
        );

String tipoNomencladorBusqueda =
        (String) request.getAttribute(
                "COMPRAS_ID_TIPO_NOMENCLADOR"
        );

int idTipoNomencladorBusqueda = -1;

if (tipoNomencladorBusqueda != null
        && tipoNomencladorBusqueda.matches("^[0-9]+$")) {

    idTipoNomencladorBusqueda =
            Integer.parseInt(
                    tipoNomencladorBusqueda
            );
}

/*
 * El Tipo Nomenclador seleccionado viaja también como
 * parámetro de la request.
 *
 * Se conserva el atributo preparado por el Action como
 * fuente principal, pero el parámetro permite compatibilidad
 * con despliegues legacy del Action que todavía dejaban
 * filtro general 0 para PRESTACIONES MEDICAS.
 *
 * El valor nunca se acepta sin validar contra la whitelist.
 */
int idTipoNomencladorSolicitado =
        ParamUtil.getInteger(
                renderRequest,
                "id_tipo_nomenclador",
                0
        );

int marcaReinLiq = 0;

if (marcaReinLiqBusqueda != null
        && marcaReinLiqBusqueda.matches("^[0-9]+$")) {

    marcaReinLiq =
            Integer.parseInt(
                    marcaReinLiqBusqueda
            );
}

sectorBusqueda =
        sectorBusqueda == null
                ? ""
                : WebKeysCompras.normalizarSectorCompra(
                        sectorBusqueda
                );

/*
 * El Action actual ya debe haber dejado el tipo correcto
 * en COMPRAS_ID_TIPO_NOMENCLADOR.
 *
 * Este fallback sólo actúa si ese atributo no contiene
 * un tipo válido de PRESTACIONES MEDICAS y la request
 * sí contiene el valor seleccionado por el usuario.
 *
 * Esto evita depender de un Action legacy desplegado
 * que todavía propagaba FILTRO_NOMENCLADOR_GENERAL = 0.
 */
if ("PRESTACIONES MEDICAS".equals(
        sectorBusqueda
)
        && !WebKeysCompras
                .esTipoNomencladorPrestacionesMedicas(
                        idTipoNomencladorBusqueda
                )
        && WebKeysCompras
                .esTipoNomencladorPrestacionesMedicas(
                        idTipoNomencladorSolicitado
                )) {

    idTipoNomencladorBusqueda =
            idTipoNomencladorSolicitado;
}

if (errorBusqueda == null
        && (
                callbackBusqueda == null
                || !callbackBusqueda.matches(
                        "^[A-Za-z_$][A-Za-z0-9_$]*$"
                )
        )) {

    errorBusqueda =
            "No se pudo identificar el formulario "
                    + "que recibira la seleccion.";
}

if (errorBusqueda == null
        && idTipoNomencladorBusqueda < 0) {

    errorBusqueda =
            "No se pudo determinar el filtro "
                    + "de nomenclador para el sector.";
}

if (errorBusqueda == null
        && sectorBusqueda.length() == 0) {

    errorBusqueda =
            "No se pudo determinar el sector "
                    + "del requerimiento.";
}
if (errorBusqueda == null
        && "PRESTACIONES MEDICAS".equals(
                sectorBusqueda
        )
        && !WebKeysCompras
                .esTipoNomencladorPrestacionesMedicas(
                        idTipoNomencladorBusqueda
                )) {

    errorBusqueda =
            "El Tipo Nomenclador informado no es válido "
                    + "para PRESTACIONES MEDICAS.";
}
%>

<% if (errorBusqueda != null
        && errorBusqueda.length() > 0) { %>

    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(errorBusqueda) %>
    </div>

<% } else {
    codigoBusqueda =
            codigoBusqueda == null
                    ? ""
                    : codigoBusqueda.trim();

    descripcionBusqueda =
            descripcionBusqueda == null
                    ? ""
                    : descripcionBusqueda.trim();

    List<Nomenclador> archivos;

    /*
     * Matriz de búsqueda:
     *
     * FARMACIA:
     *     busca_nomenclador con tipo 9.
     *
     * DISCAPACIDAD:
     *     busca_nomenclador_marca_reinliq con tipo 0
     *     y marca 6. La funcion incorpora tambien el
     *     codigo especial 431003.
     *
     * ODONTOLOGIA:
     *     busca_nomenclador con tipo 1.
     *
     * PRESTACIONES MEDICAS:
     *     busca_nomenclador_prest_med con el Tipo
     *     Nomenclador seleccionado: 2, 3, 4, 6 o 10.
     *
     */
    if ("DISCAPACIDAD".equals(sectorBusqueda)
            && marcaReinLiq
            == WebKeysCompras
                    .MARCA_REIN_LIQ_DISCAPACIDAD) {

        archivos =
                NomencladorServiceUtil
                        .getListaNomencladorMarcaReinLiq(
                                WebKeysCompras
                                        .FILTRO_NOMENCLADOR_GENERAL,
                                descripcionBusqueda,
                                0,
                                codigoBusqueda,
                                false,
                                "",
                                WebKeysCompras
                                        .MARCA_REIN_LIQ_DISCAPACIDAD
                        );

    } else if ("PRESTACIONES MEDICAS".equals(
            sectorBusqueda
    )) {

        archivos =
                NomencladorServiceUtil
                        .getListaNomencladorPrestacionesMedicasCompras(
                                idTipoNomencladorBusqueda,
                                descripcionBusqueda,
                                0,
                                codigoBusqueda,
                                false,
                                ""
                        );

    } else {

        archivos =
                NomencladorServiceUtil
                        .getListaNomenclador(
                                idTipoNomencladorBusqueda,
                                descripcionBusqueda,
                                0,
                                codigoBusqueda,
                                false,
                                ""
                        );
    }

    /*
     * La consulta reproduce el circuito de RP.
     *
     * Luego se aplica la misma matriz como defensa adicional,
     * se eliminan nomencladores dados de baja y se descartan
     * resultados tecnicamente invalidos.
     */
    List<Nomenclador> archivosFiltrados =
            new ArrayList<Nomenclador>();

    if (archivos != null) {
        for (int i = 0;
             i < archivos.size();
             i++) {

            Nomenclador nomenclador =
                    archivos.get(i);

            if (nomenclador == null
                    || nomenclador.getBaja_fecha() != null) {

                continue;
            }

            int idPrestacionReal =
                    nomenclador.getId_prestacion();

            int idTipoReal =
                    nomenclador
                            .getId_tipo_nomenclador();

            if (idPrestacionReal <= 0
                    || idTipoReal <= 0) {

                continue;
            }

            if (!WebKeysCompras
                    .esNomencladorValidoParaSectorCompras(
                            sectorBusqueda,
                            idTipoReal,
                            nomenclador
                                    .getMarcaReintegroLiquidacion(),
                            nomenclador.getCodigo()
                    )) {

                continue;
            }

            /*
             * Para PRESTACIONES MEDICAS no alcanza con que el tipo
             * pertenezca al conjunto permitido.
             *
             * Debe coincidir exactamente con el Tipo Nomenclador
             * seleccionado para esta búsqueda.
             */
            if ("PRESTACIONES MEDICAS".equals(
                    sectorBusqueda
            )
                    && idTipoReal
                    != idTipoNomencladorBusqueda) {

                continue;
            }

            archivosFiltrados.add(
                    nomenclador
            );
        }
    }

    archivos = archivosFiltrados;

    PortletURL portletURL =
            renderResponse.createRenderURL();

    portletURL.setWindowState(
            LiferayWindowState.POP_UP
    );

    portletURL.setParameter(
            Constants.CMD,
            "PopUp"
    );

    List<String> headerNames =
            new ArrayList<String>();

    headerNames.add("Tipo");
    headerNames.add("Código");
    headerNames.add("Descripción");
    headerNames.add("Especialidad");
    headerNames.add("Recupera SUR");
    headerNames.add("Fecha Baja");

    SearchContainer searchContainer =
            new SearchContainer(
                    renderRequest,
                    null,
                    null,
                    SearchContainer.DEFAULT_CUR_PARAM,
                    Integer.MAX_VALUE,
                    portletURL,
                    headerNames,
                    LanguageUtil.get(
                            pageContext,
                            "nomenclador-no-encontrado"
                    )
            );

    if (archivos != null
            && !archivos.isEmpty()) {

        int total =
                archivos.size();

        pageContext.setAttribute(
                "total",
                Integer.valueOf(total)
        );

        if (total == 1) {
            Nomenclador nomenclador =
                    archivos.get(0);

            String invocacion =
                    comprasNomencladorInvocacion(
                            callbackBusqueda,
                            nomenclador
                    );
%>
            <script type="text/javascript">
                <%= invocacion %>;
            </script>
<%
        } else {
            List resultRows =
                    searchContainer.getResultRows();

            for (int i = 0;
                    i < archivos.size();
                    i++) {

                Nomenclador nomenclador =
                        archivos.get(i);

                String invocacion =
                        comprasNomencladorInvocacion(
                                callbackBusqueda,
                                nomenclador
                        );

                String inicioEnlace =
                        "<a href=\"javascript:"
                                + invocacion
                                + "\">";

                ResultRow row =
                        new ResultRow(
                                nomenclador,
                                Integer.valueOf(1 + i),
                                i
                        );

                String descripcionTipo =
                        nomenclador
                                .getDescripcionTipoNomenclador();

                String codigoNomenclador =
                        nomenclador.getCodigo();

                String descripcionNomenclador =
                        nomenclador.getDescripcion();

                String especialidadDescripcion =
                        nomenclador
                                .getEspecialidadDescripcion();

                descripcionTipo =
                        descripcionTipo == null
                                ? ""
                                : descripcionTipo.trim();

                codigoNomenclador =
                        codigoNomenclador == null
                                ? ""
                                : codigoNomenclador.trim();

                descripcionNomenclador =
                        descripcionNomenclador == null
                                ? ""
                                : descripcionNomenclador.trim();

                especialidadDescripcion =
                        especialidadDescripcion == null
                                ? ""
                                : especialidadDescripcion.trim();

                StringBuilder tipo =
                        new StringBuilder(
                                inicioEnlace
                        );

                tipo.append(
                        HtmlUtil.escape(
                                descripcionTipo
                        )
                );

                tipo.append("</a>");
                row.addText(tipo.toString());

                StringBuilder codigo =
                        new StringBuilder(
                                inicioEnlace
                        );

                codigo.append(
                        HtmlUtil.escape(
                                codigoNomenclador
                        )
                );

                codigo.append("</a>");
                row.addText(codigo.toString());

                StringBuilder descripcion =
                        new StringBuilder(
                                inicioEnlace
                        );

                descripcion.append(
                        HtmlUtil.escape(
                                descripcionNomenclador
                        )
                );

                descripcion.append("</a>");
                row.addText(descripcion.toString());

                StringBuilder especialidad =
                        new StringBuilder(
                                inicioEnlace
                        );

                especialidad.append(
                        HtmlUtil.escape(
                                especialidadDescripcion
                        )
                );

                especialidad.append("</a>");
                row.addText(especialidad.toString());

                StringBuilder recupera =
                        new StringBuilder(
                                inicioEnlace
                        );

                recupera.append(
                        nomenclador.getRecuperaSUR()
                                ? "Sí"
                                : "No"
                );

                recupera.append("</a>");
                row.addText(recupera.toString());

                StringBuilder baja =
                        new StringBuilder(
                                inicioEnlace
                        );

                baja.append(
                        HtmlUtil.escape(
                                nomenclador.getBaja_fecha() != null
                                        ? nomenclador
                                                .getBaja_Fecha_string()
                                        : ""
                        )
                );

                baja.append("</a>");
                row.addText(baja.toString());

                resultRows.add(
                        row
                );
            }
        }
    }
%>

    <script type="text/javascript"></script>

    <%= pageContext.getAttribute("total") != null
            ? "Total Filas encontradas "
                    + pageContext.getAttribute("total")
            : "" %>

    <liferay-ui:search-iterator
            paginate="false"
            searchContainer="<%= searchContainer %>" />

<% } %>
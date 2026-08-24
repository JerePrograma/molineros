<%--
Responsabilidad:
    Renderiza resultados canónicos de prestaciones y devuelve su identidad técnica.
Incluido desde:
    Forward, Action o entry point directo en: tiles-defs.xml.
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo modifica el DOM o comunica la selección al callback namespaced.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Nomenclador" %>

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
            Integer.parseInt(tipoNomencladorBusqueda);
}

sectorBusqueda =
        sectorBusqueda == null
                ? ""
                : WebKeysCompras.normalizarSectorCompra(
                        sectorBusqueda
                );

if (errorBusqueda == null
        && (
                callbackBusqueda == null
                || !callbackBusqueda.matches(
                        "^[A-Za-z_$][A-Za-z0-9_$]*$"
                )
        )) {

    errorBusqueda =
            "No se pudo identificar el formulario "
                            + "que recibirá la selección.";
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

    List<Nomenclador> archivos =
            (List<Nomenclador>) request.getAttribute(
                    "COMPRAS_RESULTADOS_NOMENCLADOR"
            );

    if (archivos == null) {
        archivos =
                new ArrayList<Nomenclador>();
    }

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

    if (!archivos.isEmpty()) {
        int total = archivos.size();

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
                        nomenclador.getDescripcionTipoNomenclador();

                String codigoNomenclador =
                        nomenclador.getCodigo();

                String descripcionNomenclador =
                        nomenclador.getDescripcion();

                String especialidadDescripcion =
                        nomenclador.getEspecialidadDescripcion();

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
                        new StringBuilder(inicioEnlace);
                tipo.append(HtmlUtil.escape(descripcionTipo));
                tipo.append("</a>");
                row.addText(tipo.toString());

                StringBuilder codigo =
                        new StringBuilder(inicioEnlace);
                codigo.append(HtmlUtil.escape(codigoNomenclador));
                codigo.append("</a>");
                row.addText(codigo.toString());

                StringBuilder descripcion =
                        new StringBuilder(inicioEnlace);
                descripcion.append(
                        HtmlUtil.escape(descripcionNomenclador)
                );
                descripcion.append("</a>");
                row.addText(descripcion.toString());

                StringBuilder especialidad =
                        new StringBuilder(inicioEnlace);
                especialidad.append(
                        HtmlUtil.escape(especialidadDescripcion)
                );
                especialidad.append("</a>");
                row.addText(especialidad.toString());

                StringBuilder recupera =
                        new StringBuilder(inicioEnlace);
                recupera.append(
                        nomenclador.getRecuperaSUR()
                                ? "Sí"
                                : "No"
                );
                recupera.append("</a>");
                row.addText(recupera.toString());

                StringBuilder baja =
                        new StringBuilder(inicioEnlace);
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

                resultRows.add(row);
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

<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Nomenclador" %>
<%@ page import="ar.com.ospim.autorizaciones.services.NomencladorServiceUtil" %>

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
            + ",'" + comprasNomencladorJs(nomenclador.getCodigo().trim())
            + "','" + comprasNomencladorJs(nomenclador.getDescripcion().trim())
            + "')";
}
%>

<%
String errorBusqueda = (String) request.getAttribute("COMPRAS_ERROR_BUSQUEDA");
String callbackBusqueda = (String) request.getAttribute("COMPRAS_CALLBACK_BUSQUEDA");
String codigoBusqueda = (String) request.getAttribute("COMPRAS_CODIGO_NOMENCLADOR");
String descripcionBusqueda = (String) request.getAttribute("COMPRAS_DESCRIPCION_NOMENCLADOR");
String esPrestMedBusqueda = (String) request.getAttribute("COMPRAS_ES_PREST_MED");

if (errorBusqueda == null
        && (callbackBusqueda == null
        || !callbackBusqueda.matches("^[A-Za-z_$][A-Za-z0-9_$]*$"))) {

    errorBusqueda =
            "No se pudo identificar el formulario que recibira la seleccion.";
}
%>

<% if (errorBusqueda != null && errorBusqueda.length() > 0) { %>
    <div class="portlet-msg-error"><%= HtmlUtil.escape(errorBusqueda) %></div>
<% } else {
    codigoBusqueda = codigoBusqueda == null ? "" : codigoBusqueda;
    descripcionBusqueda = descripcionBusqueda == null ? "" : descripcionBusqueda;

    List<Nomenclador> archivos;

    if ("1".equals(esPrestMedBusqueda)) {
        archivos = NomencladorServiceUtil.getListaNomencladorPrestacionesMedicas(
                0,
                descripcionBusqueda,
                0,
                codigoBusqueda,
                false,
                ""
        );
    } else {
        archivos = NomencladorServiceUtil.getListaNomenclador(
                0,
                descripcionBusqueda,
                0,
                codigoBusqueda,
                false,
                ""
        );
    }

    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setWindowState(LiferayWindowState.POP_UP);
    portletURL.setParameter(Constants.CMD, "PopUp");

    List<String> headerNames = new ArrayList<String>();
    headerNames.add("Tipo");
    headerNames.add("C\u00f3digo");
    headerNames.add("Descripci\u00f3n");
    headerNames.add("Especialidad");
    headerNames.add("Recupera SUR");
    headerNames.add("Fecha Baja");

    SearchContainer searchContainer = new SearchContainer(
            renderRequest,
            null,
            null,
            SearchContainer.DEFAULT_CUR_PARAM,
            Integer.MAX_VALUE,
            portletURL,
            headerNames,
            LanguageUtil.get(pageContext, "nomenclador-no-encontrado")
    );

    if (archivos != null && !archivos.isEmpty()) {
        int total = archivos.size();
        pageContext.setAttribute("total", total);

        if (total == 1) {
            Nomenclador nomenclador = archivos.get(0);

            String invocacion = comprasNomencladorInvocacion(
                    callbackBusqueda,
                    nomenclador
            );
%>
            <script type="text/javascript">
                <%= invocacion %>;
            </script>
<%
        } else {
            List resultRows = searchContainer.getResultRows();

            for (int i = 0; i < archivos.size(); i++) {
                Nomenclador nomenclador = archivos.get(i);

                if (nomenclador.getBaja_fecha() == null) {
                    String invocacion = comprasNomencladorInvocacion(
                            callbackBusqueda,
                            nomenclador
                    );
                    String inicioEnlace =
                            "<a href=\"javascript:" + invocacion + "\">";

                    ResultRow row = new ResultRow(
                            nomenclador,
                            new Integer(1 + i),
                            i
                    );

                    StringBuilder tipo = new StringBuilder(inicioEnlace);
                    tipo.append(HtmlUtil.escape(
                            nomenclador.getDescripcionTipoNomenclador().trim()
                    ));
                    tipo.append("</a>");
                    row.addText(tipo.toString());

                    StringBuilder codigo = new StringBuilder(inicioEnlace);
                    codigo.append(HtmlUtil.escape(nomenclador.getCodigo().trim()));
                    codigo.append("</a>");
                    row.addText(codigo.toString());

                    StringBuilder descripcion = new StringBuilder(inicioEnlace);
                    descripcion.append(HtmlUtil.escape(
                            nomenclador.getDescripcion().trim()
                    ));
                    descripcion.append("</a>");
                    row.addText(descripcion.toString());

                    StringBuilder especialidad = new StringBuilder(inicioEnlace);
                    especialidad.append(HtmlUtil.escape(
                            nomenclador.getEspecialidadDescripcion().trim()
                    ));
                    especialidad.append("</a>");
                    row.addText(especialidad.toString());

                    StringBuilder recupera = new StringBuilder(inicioEnlace);
                    recupera.append(nomenclador.getRecuperaSUR() ? "Si" : "No");
                    recupera.append("</a>");
                    row.addText(recupera.toString());

                    StringBuilder baja = new StringBuilder(inicioEnlace);
                    baja.append(HtmlUtil.escape(
                            nomenclador.getBaja_fecha() != null
                                    ? nomenclador.getBaja_Fecha_string()
                                    : ""
                    ));
                    baja.append("</a>");
                    row.addText(baja.toString());

                    resultRows.add(row);
                }
            }
        }
    }
%>
    <script type="text/javascript"></script>
    <%= pageContext.getAttribute("total") != null
            ? "Total Filas encontradas " + pageContext.getAttribute("total")
            : "" %>

    <liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<% } %>

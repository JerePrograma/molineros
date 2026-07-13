<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.farmacia.beans.Medicamento" %>
<%@ page import="ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil" %>

<%!
private String comprasMedicamentoJs(String value) {
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

private String comprasMedicamentoInvocacion(
        String callback,
        Medicamento medicamento,
        String presentacion) {

    return callback
            + "('" + comprasMedicamentoJs(
                    String.valueOf(medicamento.getTroquel())
            )
            + "','" + comprasMedicamentoJs(medicamento.getNombre().trim())
            + "','" + comprasMedicamentoJs(
                    medicamento.getId_medicamentoAsString()
            )
            + "','" + comprasMedicamentoJs(presentacion)
            + "')";
}

private String comprasMedicamentoEnlace(
        String invocacion,
        String texto) {

    return "<a href=\"javascript:" + invocacion + "\">"
            + HtmlUtil.escape(texto)
            + "</a>";
}
%>

<%
String troquel = renderRequest.getParameter("troquel");
String nombreMedicamento =
        renderRequest.getParameter("nombre_medicamento");
String callbackMedicamento =
        renderResponse.getNamespace() + "pasarParametrosAParentMd";

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.POP_UP);
portletURL.setParameter(Constants.CMD, "PopUp");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id");
headerNames.add("Troquel");
headerNames.add("Nombre");
headerNames.add("Presentacion");
headerNames.add("Cod Barras");
headerNames.add("Precio");

SearchContainer searchContainer = new SearchContainer(
        renderRequest,
        null,
        null,
        SearchContainer.DEFAULT_CUR_PARAM,
        SearchContainer.MAX_DELTA,
        portletURL,
        headerNames,
        LanguageUtil.get(pageContext, "no-medicamentos-were-found")
);

List<Medicamento> medicamentos =
        BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(
                Integer.parseInt(troquel),
                nombreMedicamento
        );

int total = medicamentos.size();

if (total == 1) {
    Medicamento medicamento = medicamentos.get(0);
    String presentacion =
            medicamento.getPresentacion() != null
                    ? medicamento.getPresentacion()
                    : "";
    String invocacion = comprasMedicamentoInvocacion(
            callbackMedicamento,
            medicamento,
            presentacion
    );
%>
    <script type="text/javascript">
        <%= invocacion %>;
    </script>
<%
} else {
    searchContainer.setTotal(total);

    List resultRows = searchContainer.getResultRows();

    for (int i = 0; i < medicamentos.size(); i++) {
        Medicamento medicamento = medicamentos.get(i);
        ResultRow row = new ResultRow(
                medicamento.getTroquel(),
                medicamento.getNombre(),
                i
        );

        String presentacion =
                medicamento.getPresentacion() != null
                        ? medicamento.getPresentacion()
                        : "";
        String presentacionTrim = presentacion.trim();
        String invocacionTrim = comprasMedicamentoInvocacion(
                callbackMedicamento,
                medicamento,
                presentacionTrim
        );
        String invocacion = comprasMedicamentoInvocacion(
                callbackMedicamento,
                medicamento,
                presentacion
        );
        String codigoBarras =
                medicamento.getCod_barra() != null
                        ? medicamento.getCod_barra().trim()
                        : "";

        row.addText(comprasMedicamentoEnlace(
                invocacionTrim,
                medicamento.getId_medicamentoAsString()
        ));
        row.addText(comprasMedicamentoEnlace(
                invocacionTrim,
                String.valueOf(medicamento.getTroquel())
        ));
        row.addText(comprasMedicamentoEnlace(
                invocacionTrim,
                medicamento.getNombre().trim()
        ));
        row.addText(comprasMedicamentoEnlace(
                invocacion,
                codigoBarras
        ));
        row.addText(comprasMedicamentoEnlace(
                invocacion,
                codigoBarras
        ));
        row.addText(comprasMedicamentoEnlace(
                invocacion,
                medicamento.getPrecio() != null
                        ? medicamento.getPrecio().toString()
                        : "0"
        ));

        resultRows.add(row);
    }
%>
    <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
}
%>

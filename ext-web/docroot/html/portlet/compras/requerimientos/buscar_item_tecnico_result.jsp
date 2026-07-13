<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Nomenclador" %>
<%@ page import="ar.com.ospim.farmacia.beans.Medicamento" %>

<%!
private String comprasJs(String value) {
    if (value == null) {
        return "";
    }

    return value.replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
String tipoItemBusqueda = (String) request.getAttribute("COMPRAS_TIPO_ITEM_BUSQUEDA");
String callbackBusqueda = (String) request.getAttribute("COMPRAS_CALLBACK_BUSQUEDA");
String errorBusqueda = (String) request.getAttribute("COMPRAS_ERROR_BUSQUEDA");
Boolean resultadosLimitados = (Boolean) request.getAttribute("COMPRAS_RESULTADOS_LIMITADOS");

List<Medicamento> medicamentosBusqueda =
        (List<Medicamento>) request.getAttribute("COMPRAS_MEDICAMENTOS_BUSQUEDA");
List<Nomenclador> nomencladoresBusqueda =
        (List<Nomenclador>) request.getAttribute("COMPRAS_NOMENCLADORES_BUSQUEDA");

if (medicamentosBusqueda == null) {
    medicamentosBusqueda = new ArrayList<Medicamento>();
}

if (nomencladoresBusqueda == null) {
    nomencladoresBusqueda = new ArrayList<Nomenclador>();
}
%>

<h3>Seleccionar <%= "MEDICAMENTO".equals(tipoItemBusqueda) ? "medicamento" : "prestacion" %></h3>

<% if (errorBusqueda != null && errorBusqueda.length() > 0) { %>
    <div class="portlet-msg-error"><%= HtmlUtil.escape(errorBusqueda) %></div>
<% } %>

<% if (Boolean.TRUE.equals(resultadosLimitados)) { %>
    <div class="portlet-msg-info">Se muestran los primeros 100 resultados activos. Refine la busqueda.</div>
<% } %>

<% if ("MEDICAMENTO".equals(tipoItemBusqueda) && errorBusqueda == null) { %>
    <table class="lfr-table" width="100%">
        <tr><th>Troquel</th><th>Nombre</th><th>Presentacion</th><th></th></tr>
        <% for (int i = 0; i < medicamentosBusqueda.size(); i++) {
            Medicamento medicamento = medicamentosBusqueda.get(i); %>
            <tr>
                <td><%= medicamento.getTroquel() > 0 ? String.valueOf(medicamento.getTroquel()) : "" %></td>
                <td><%= HtmlUtil.escape(medicamento.getNombre()) %></td>
                <td><%= HtmlUtil.escape(medicamento.getPresentacion()) %></td>
                <td>
                    <a href="#" onclick="return seleccionarMedicamentoCompras(<%= i %>);">Seleccionar</a>
                </td>
            </tr>
        <% } %>
        <% if (medicamentosBusqueda.isEmpty()) { %>
            <tr><td colspan="4">No se encontraron medicamentos activos.</td></tr>
        <% } %>
    </table>
<% } %>

<% if ("NOMENCLADOR".equals(tipoItemBusqueda) && errorBusqueda == null) { %>
    <table class="lfr-table" width="100%">
        <tr><th>Tipo</th><th>Codigo</th><th>Descripcion</th><th></th></tr>
        <% for (int i = 0; i < nomencladoresBusqueda.size(); i++) {
            Nomenclador nomenclador = nomencladoresBusqueda.get(i); %>
            <tr>
                <td><%= HtmlUtil.escape(nomenclador.getDescripcionTipoNomenclador()) %></td>
                <td><%= HtmlUtil.escape(nomenclador.getCodigo()) %></td>
                <td><%= HtmlUtil.escape(nomenclador.getDescripcion()) %></td>
                <td>
                    <a href="#" onclick="return seleccionarNomencladorCompras(<%= i %>);">Seleccionar</a>
                </td>
            </tr>
        <% } %>
        <% if (nomencladoresBusqueda.isEmpty()) { %>
            <tr><td colspan="4">No se encontraron prestaciones activas.</td></tr>
        <% } %>
    </table>
<% } %>

<script type="text/javascript">
    var callbackCompras = '<%= comprasJs(callbackBusqueda) %>';

    function enviarSeleccionCompras(argumentos) {
        var openerWindow = window.opener;
        var callback = openerWindow && openerWindow[callbackCompras];

        if (typeof callback != 'function') {
            alert('No se pudo devolver la seleccion al requerimiento.');
            return false;
        }

        callback.apply(openerWindow, argumentos);
        window.close();
        return false;
    }

    function seleccionarMedicamentoCompras(index) {
        var resultados = [];
        <% for (int i = 0; i < medicamentosBusqueda.size(); i++) {
            Medicamento medicamento = medicamentosBusqueda.get(i); %>
            resultados.push([
                '<%= medicamento.getId_medicamento() %>',
                '<%= medicamento.getTroquel() > 0 ? medicamento.getTroquel() : "" %>',
                '<%= comprasJs(medicamento.getNombre()) %>',
                '<%= comprasJs(medicamento.getPresentacion()) %>'
            ]);
        <% } %>
        return enviarSeleccionCompras(resultados[index]);
    }

    function seleccionarNomencladorCompras(index) {
        var resultados = [];
        <% for (int i = 0; i < nomencladoresBusqueda.size(); i++) {
            Nomenclador nomenclador = nomencladoresBusqueda.get(i); %>
            resultados.push([
                '<%= nomenclador.getId_prestacion() %>',
                '<%= nomenclador.getId_tipo_nomenclador() %>',
                '<%= comprasJs(nomenclador.getCodigo()) %>',
                '<%= comprasJs(nomenclador.getDescripcion()) %>'
            ]);
        <% } %>
        return enviarSeleccionCompras(resultados[index]);
    }
</script>

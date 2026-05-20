<%@ include file="/html/portlet/requerimientos_compras/init.jsp" %>

<%
int idReqHistorial = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
RequerimientoCompra reqHistorial = (RequerimientoCompra) renderRequest.getAttribute(WebKeysRequerimientosCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (reqHistorial == null && idReqHistorial > 0) {
    reqHistorial = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReqHistorial);
}
List<RequerimientoCompraHistorial> historial = reqHistorial != null && reqHistorial.getHistorial() != null
        ? reqHistorial.getHistorial()
        : new ArrayList<RequerimientoCompraHistorial>();
%>

<fieldset class="block-labels">
    <legend>Historial</legend>

    <table class="lfr-table">
        <tr class="portlet-section-header results-header">
            <th>Fecha</th>
            <th>Usuario</th>
            <th>Estado anterior</th>
            <th>Estado nuevo</th>
            <th>Comentario</th>
        </tr>

        <% for (int i = 0; i < historial.size(); i++) {
            RequerimientoCompraHistorial h = historial.get(i);
        %>
            <tr class="<%= (i % 2 == 0) ? "portlet-section-body results-row" : "portlet-section-alternate results-row alt" %>">
                <td><%= h.getFechaAsString() %></td>
                <td><%= h.getUsuario() != null ? h.getUsuario() : "" %></td>
                <td><%= h.getEstadoAnteriorDescripcion() %></td>
                <td><%= h.getEstadoNuevoDescripcion() %></td>
                <td><%= h.getComentario() != null ? h.getComentario() : "" %></td>
            </tr>
        <% } %>

        <c:if test="<%= historial.size() == 0 %>">
            <tr><td colspan="5">Sin movimientos.</td></tr>
        </c:if>
    </table>
</fieldset>

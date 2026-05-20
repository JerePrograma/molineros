<%@ include file="/html/portlet/compras/init.jsp" %>

<%
int idReqAdjuntos = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
RequerimientoCompra reqAdjuntos = (RequerimientoCompra) renderRequest.getAttribute(WebKeysRequerimientosCompras.COMPRA_EN_EDICION);
if (reqAdjuntos == null && idReqAdjuntos > 0) {
    reqAdjuntos = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReqAdjuntos);
}
List<RequerimientoCompraAdjunto> adjuntos = reqAdjuntos != null && reqAdjuntos.getAdjuntos() != null
        ? reqAdjuntos.getAdjuntos()
        : new ArrayList<RequerimientoCompraAdjunto>();
%>

<fieldset class="block-labels">
    <legend>Adjuntos</legend>

    <table class="lfr-table">
        <tr class="portlet-section-header results-header">
            <th>Archivo</th>
            <th>Tipo</th>
            <th>Usuario</th>
            <th>Fecha</th>
        </tr>

        <% for (int i = 0; i < adjuntos.size(); i++) {
            RequerimientoCompraAdjunto adjunto = adjuntos.get(i);
        %>
            <tr class="<%= (i % 2 == 0) ? "portlet-section-body results-row" : "portlet-section-alternate results-row alt" %>">
                <td><%= adjunto.getNombreArchivo() != null ? adjunto.getNombreArchivo() : "" %></td>
                <td><%= adjunto.getTipoArchivo() != null ? adjunto.getTipoArchivo() : "" %></td>
                <td><%= adjunto.getAltaUsr() != null ? adjunto.getAltaUsr() : "" %></td>
                <td><%= adjunto.getAltaFechaAsString() %></td>
            </tr>
        <% } %>

        <c:if test="<%= adjuntos.size() == 0 %>">
            <tr><td colspan="4">Sin adjuntos.</td></tr>
        </c:if>
    </table>

    <div class="portlet-msg-info">
        Base preparada para adjuntos. La integración real debería hacerse contra Document Library y luego registrar file_entry_id en requerimiento_compra_adjunto.
    </div>
</fieldset>

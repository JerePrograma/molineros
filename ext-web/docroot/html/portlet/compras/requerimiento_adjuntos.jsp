<%@ include file="/html/portlet/compras/init.jsp" %>

<%
int idReqAdjuntos = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

RequerimientoCompra reqAdjuntos = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (reqAdjuntos == null) {
    reqAdjuntos = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}
if (reqAdjuntos == null && idReqAdjuntos > 0) {
    reqAdjuntos = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReqAdjuntos);
}

List<RequerimientoCompraAdjunto> adjuntos = reqAdjuntos != null && reqAdjuntos.getAdjuntos() != null
        ? reqAdjuntos.getAdjuntos()
        : new ArrayList<RequerimientoCompraAdjunto>();
%>

<fieldset class="block-labels">
    <legend>Adjuntos</legend>

    <table class="lfr-table taglib-search-iterator" width="100%">
        <thead>
            <tr class="portlet-section-header results-header">
                <th>Archivo</th>
                <th>Tipo</th>
                <th>Usuario</th>
                <th>Fecha</th>
            </tr>
        </thead>
        <tbody>
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
                <tr>
                    <td colspan="4">Sin adjuntos.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</fieldset>

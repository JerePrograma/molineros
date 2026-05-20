<%@ include file="/html/portlet/compras/init.jsp" %>

<%
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_ABM_COMPRAS);
boolean puedeAprobar = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_APROBAR_COMPRAS);

List<RequerimientoCompra> requerimientos = (List<RequerimientoCompra>) renderRequest.getAttribute(WebKeysRequerimientosCompras.BUSQUEDA_COMPRAS);
if (requerimientos == null) {
    requerimientos = (List<RequerimientoCompra>) portletSession.getAttribute(WebKeysRequerimientosCompras.BUSQUEDA_COMPRAS, PortletSession.PORTLET_SCOPE);
}
if (requerimientos == null) {
    requerimientos = new ArrayList<RequerimientoCompra>();
}
%>

<c:if test="<%= requerimientos.size() == 0 %>">
    <div class="portlet-msg-info">No se encontraron requerimientos de compras.</div>
</c:if>

<c:if test="<%= requerimientos.size() > 0 %>">
    <table class="lfr-table taglib-search-iterator">
        <thead>
            <tr class="portlet-section-header results-header">
                <th>Número</th>
                <th>Fecha</th>
                <th>Sector</th>
                <th>Solicitante</th>
                <th>Prioridad</th>
                <th>Estado</th>
                <th>Necesidad</th>
                <th>Importe estimado</th>
                <th>OC</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <% for (int i = 0; i < requerimientos.size(); i++) {
                RequerimientoCompra req = requerimientos.get(i);

                PortletURL verURL = renderResponse.createRenderURL();
                verURL.setWindowState(WindowState.MAXIMIZED);
                verURL.setParameter("struts_action", "/compras/ver_requerimiento");
                verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

                PortletURL editarURL = renderResponse.createRenderURL();
                editarURL.setWindowState(WindowState.MAXIMIZED);
                editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
                editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());
            %>
                <tr class="<%= (i % 2 == 0) ? "portlet-section-body results-row" : "portlet-section-alternate results-row alt" %>">
                    <td><a href="<%= verURL.toString() %>"><%= req.getNumeroString() %></a></td>
                    <td><%= req.getFechaAltaAsString() %></td>
                    <td><%= req.getSectorDescripcion() != null ? req.getSectorDescripcion() : "" %></td>
                    <td><%= req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : "" %></td>
                    <td><%= req.getPrioridadDescripcion() %></td>
                    <td><%= req.getEstadoDescripcion() %></td>
                    <td><%= req.getFechaNecesidadAsString() %></td>
                    <td><%= req.getImporteEstimadoTotalString() %></td>
                    <td><%= req.getIdOrdenCompraString() %></td>
                    <td>
                        <a href="<%= verURL.toString() %>">Ver</a>
                        <c:if test="<%= puedeABM && req.isEditable() %>">
                            &nbsp;|&nbsp;<a href="<%= editarURL.toString() %>">Editar</a>
                        </c:if>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</c:if>

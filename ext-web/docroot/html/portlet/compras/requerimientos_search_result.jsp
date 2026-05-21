<%@ include file="/html/portlet/compras/init.jsp" %>

<%
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<RequerimientoCompra> requerimientos = (List<RequerimientoCompra>) renderRequest.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA);
if (requerimientos == null) {
    requerimientos = (List<RequerimientoCompra>) portletSession.getAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA, PortletSession.PORTLET_SCOPE);
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
                <th>Numero</th>
                <th>Fecha solicitud</th>
                <th>Afiliado</th>
                <th>DNI</th>
                <th>Sector</th>
                <th>Detalle</th>
                <th>Estado</th>
                <th>RP</th>
                <th>OC</th>
                <th>Cotizado</th>
                <th>Recupero</th>
                <th>Localidad</th>
                <th>Provincia</th>
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
                    <td><%= req.getFechaSolicitudAsString() %></td>
                    <td><%= req.getAfiliado() != null ? req.getAfiliado() : "" %></td>
                    <td><%= req.getDniString() %></td>
                    <td><%= req.getSectorDescripcion() != null ? req.getSectorDescripcion() : "" %></td>
                    <td><%= req.getDetalleRequerimiento() != null ? req.getDetalleRequerimiento() : req.getMotivoVisible() %></td>
                    <td><%= req.getEstadoDescripcion() %></td>
                    <td><%= req.getRpNumeroString() %></td>
                    <td><%= req.getOrdenCompraNumeroString() %></td>
                    <td><%= req.getCotizadoDescripcion() %></td>
                    <td><%= req.getRecuperoDescripcion() %></td>
                    <td><%= req.getLocalidad() != null ? req.getLocalidad() : "" %></td>
                    <td><%= req.getProvincia() != null ? req.getProvincia() : "" %></td>
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

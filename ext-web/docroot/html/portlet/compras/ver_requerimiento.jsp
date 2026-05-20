<%@ include file="/html/portlet/compras/init.jsp" %>

<%
int idRequerimientoCompra = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
Object idAttr = renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);
if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
    idRequerimientoCompra = ((Integer) idAttr).intValue();
}

RequerimientoCompra req = null;
if (idRequerimientoCompra > 0) {
    req = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);
}
if (req == null) {
    req = new RequerimientoCompra();
}

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean puedeAnular = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS) || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean puedeAprobar = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_APROBAR_COMPRAS);

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL editarURL = renderResponse.createRenderURL();
editarURL.setWindowState(WindowState.MAXIMIZED);
editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL cambiarEstadoURL = renderResponse.createActionURL();
cambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);
cambiarEstadoURL.setParameter("struts_action", "/compras/cambiar_estado_requerimiento");
%>

<fieldset class="block-labels">
    <legend>Requerimiento de compra</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Número:</label></td>
            <td><%= req.getNumeroString() %></td>
            <td><label>Estado:</label></td>
            <td><strong><%= req.getEstadoDescripcion() %></strong></td>
        </tr>
        <tr>
            <td><label>Fecha alta:</label></td>
            <td><%= req.getFechaAltaAsString() %></td>
            <td><label>Alta usuario:</label></td>
            <td><%= req.getAltaUsr() != null ? req.getAltaUsr() : "" %></td>
        </tr>
        <tr>
            <td><label>Sector:</label></td>
            <td><%= req.getSectorDescripcion() != null ? req.getSectorDescripcion() : "" %></td>
            <td><label>Solicitante:</label></td>
            <td><%= req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : "" %></td>
        </tr>
        <tr>
            <td><label>Prioridad:</label></td>
            <td><%= req.getPrioridadDescripcion() %></td>
            <td><label>Fecha necesidad:</label></td>
            <td><%= req.getFechaNecesidadAsString() %></td>
        </tr>
        <tr>
            <td><label>Motivo:</label></td>
            <td colspan="3"><%= req.getMotivo() != null ? req.getMotivo() : "" %></td>
        </tr>
        <tr>
            <td><label>Observaciones:</label></td>
            <td colspan="3"><%= req.getObservaciones() != null ? req.getObservaciones() : "" %></td>
        </tr>
    </table>
</fieldset>

<liferay-util:include page="/html/portlet/compras/requerimiento_items.jsp" />
<liferay-util:include page="/html/portlet/compras/requerimiento_adjuntos.jsp" />
<liferay-util:include page="/html/portlet/compras/requerimiento_historial.jsp" />

<form action="<%= cambiarEstadoURL.toString() %>" method="post" name="<portlet:namespace />cambioEstadoFm">
    <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= req.getIdRequerimientoCompra() %>" />

    <fieldset class="block-labels">
        <legend>Cambio de estado</legend>

        <table class="lfr-table">
            <tr>
                <td><label>Estado nuevo:</label></td>
                <td>
                    <select name="<portlet:namespace />estado_nuevo" id="<portlet:namespace />estado_nuevo">
                        <option value="">Seleccione</option>

                        <c:if test="<%= WebKeysCompras.puedeEnviarAprobacion(req.getEstado()) && puedeABM %>">
                            <option value="<%= WebKeysCompras.ESTADO_PENDIENTE_APROBACION %>">Enviar a aprobacion</option>
                        </c:if>

                        <c:if test="<%= WebKeysCompras.puedeAprobar(req.getEstado()) && puedeAprobar %>">
                            <option value="<%= WebKeysCompras.ESTADO_APROBADO %>">Aprobar</option>
                            <option value="<%= WebKeysCompras.ESTADO_OBSERVADO %>">Observar</option>
                            <option value="<%= WebKeysCompras.ESTADO_RECHAZADO %>">Rechazar</option>
                        </c:if>

                        <c:if test="<%= WebKeysCompras.puedeCerrar(req.getEstado()) && puedeABM %>">
                            <option value="<%= WebKeysCompras.ESTADO_EN_COMPRA %>">Marcar en compra</option>
                            <option value="<%= WebKeysCompras.ESTADO_CERRADO %>">Cerrar</option>
                        </c:if>

                        <c:if test="<%= WebKeysCompras.puedeAnular(req.getEstado()) && puedeAnular %>">
                            <option value="<%= WebKeysCompras.ESTADO_ANULADO %>">Anular</option>
                        </c:if>
                    </select>
                </td>
                <td><label>Comentario:</label></td>
                <td><input type="text" name="<portlet:namespace />comentario" id="<portlet:namespace />comentario" size="60" maxlength="500" /></td>
                <td><input type="button" value="Aplicar" onclick="<portlet:namespace />cambiarEstado();" /></td>
            </tr>
        </table>
    </fieldset>
</form>

<table>
    <tr>
        <td>
            <c:if test="<%= puedeABM && req.isEditable() %>">
                <input type="button" value="Editar" onclick="window.location.href='<%= editarURL.toString() %>';" />
            </c:if>
            <input type="button" value="Volver" onclick="window.location.href='<%= volverURL.toString() %>';" />
        </td>
    </tr>
</table>

<script type="text/javascript">
    function <portlet:namespace />cambiarEstado() {
        if (jQuery("#<portlet:namespace />estado_nuevo").val() == "") {
            alert("Debe seleccionar un estado.");
            return;
        }
        submitForm(document.<portlet:namespace />cambioEstadoFm);
    }
</script>

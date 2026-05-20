<%@ include file="/html/portlet/requerimientos_compras/init.jsp" %>

<%
RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysRequerimientosCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_ABM_REQUERIMIENTOS_COMPRAS);

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/requerimientos_compras/view");

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/requerimientos_compras/editar_requerimiento");
%>

<c:if test="<%= !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= puedeABM %>">
    <form action="<%= actionURL.toString() %>" method="post" name="<portlet:namespace />fm">
        <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" value="<%= esNuevo ? Constants.ADD : Constants.UPDATE %>" />
        <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" id="<portlet:namespace />id_requerimiento_compra" value="<%= req.getIdRequerimientoCompra() %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table">
                <tr>
                    <td><label>Número:</label></td>
                    <td><%= req.getNumeroString() %></td>

                    <td><label>Estado:</label></td>
                    <td><%= req.getEstadoDescripcion() %></td>
                </tr>

                <tr>
                    <td><label>Solicitante:</label></td>
                    <td><input type="text" name="<portlet:namespace />solicitante_usr" id="<portlet:namespace />solicitante_usr" value="<%= req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : user.getScreenName() %>" maxlength="75" /></td>

                    <td><label>Entidad:</label></td>
                    <td><input type="text" name="<portlet:namespace />entidad" id="<portlet:namespace />entidad" value="<%= req.getEntidad() != null ? req.getEntidad() : "O.S.P.I.M." %>" maxlength="75" /></td>
                </tr>

                <tr>
                    <td><label>Sector:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />sector_id" id="<portlet:namespace />sector_id" value="<%= req.getSectorId() != null ? req.getSectorId().toString() : "" %>" size="8" />
                    </td>

                    <td><label>Prioridad:</label></td>
                    <td>
                        <select name="<portlet:namespace />prioridad" id="<portlet:namespace />prioridad">
                            <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_BAJA %>" <%= req.getPrioridad() == WebKeysRequerimientosCompras.PRIORIDAD_BAJA ? "selected" : "" %>>Baja</option>
                            <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_MEDIA %>" <%= req.getPrioridad() == WebKeysRequerimientosCompras.PRIORIDAD_MEDIA ? "selected" : "" %>>Media</option>
                            <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_ALTA %>" <%= req.getPrioridad() == WebKeysRequerimientosCompras.PRIORIDAD_ALTA ? "selected" : "" %>>Alta</option>
                            <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_URGENTE %>" <%= req.getPrioridad() == WebKeysRequerimientosCompras.PRIORIDAD_URGENTE ? "selected" : "" %>>Urgente</option>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td><label>Fecha necesidad:</label></td>
                    <td><input type="text" name="<portlet:namespace />fecha_necesidad" id="<portlet:namespace />fecha_necesidad" value="<%= req.getFechaNecesidadAsString() %>" size="10" maxlength="10" /> dd/MM/yyyy</td>

                    <td><label>Importe estimado total:</label></td>
                    <td><input type="text" name="<portlet:namespace />importe_estimado_total" id="<portlet:namespace />importe_estimado_total" value="<%= req.getImporteEstimadoTotalString() %>" size="12" /></td>
                </tr>

                <tr>
                    <td><label>Orden compra:</label></td>
                    <td><input type="text" name="<portlet:namespace />id_orden_compra" id="<portlet:namespace />id_orden_compra" value="<%= req.getIdOrdenCompraString() %>" size="10" /></td>

                    <td><label>Motivo:</label></td>
                    <td><input type="text" name="<portlet:namespace />motivo" id="<portlet:namespace />motivo" value="<%= req.getMotivo() != null ? req.getMotivo() : "" %>" size="50" maxlength="255" /></td>
                </tr>

                <tr>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />observaciones" id="<portlet:namespace />observaciones" cols="90" rows="4"><%= req.getObservaciones() != null ? req.getObservaciones() : "" %></textarea>
                    </td>
                </tr>

                <tr>
                    <td colspan="4">
                        <input type="button" value="Guardar" onclick="<portlet:namespace />guardar();" />
                        <input type="button" value="Volver" onclick="window.location.href='<%= volverURL.toString() %>';" />
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>

    <c:if test="<%= req.getIdRequerimientoCompra() > 0 %>">
        <liferay-util:include page="/html/portlet/requerimientos_compras/requerimiento_items.jsp" />
        <liferay-util:include page="/html/portlet/requerimientos_compras/requerimiento_adjuntos.jsp" />
        <liferay-util:include page="/html/portlet/requerimientos_compras/requerimiento_historial.jsp" />
    </c:if>
</c:if>

<script type="text/javascript">
    function <portlet:namespace />guardar() {
        if (jQuery("#<portlet:namespace />solicitante_usr").val() == "") {
            alert("Debe informar solicitante.");
            return;
        }

        if (jQuery("#<portlet:namespace />motivo").val() == "") {
            alert("Debe informar motivo.");
            return;
        }

        submitForm(document.<portlet:namespace />fm);
    }
</script>

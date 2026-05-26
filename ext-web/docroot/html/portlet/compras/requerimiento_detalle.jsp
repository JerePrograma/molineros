<%@ include file="/html/portlet/compras/init.jsp" %>

<%!
private String jsDetalleCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ");
}

private String jsDetalleCompraAttr(String value) {
    return HtmlUtil.escape(jsDetalleCompra(value));
}%>

<%
RequerimientoCompra reqDetalle = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (reqDetalle == null) {
    reqDetalle = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}
if (reqDetalle == null) {
    int idReq = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
    if (idReq > 0) {
        reqDetalle = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReq);
    }
}
if (reqDetalle == null) {
    reqDetalle = new RequerimientoCompra();
}

boolean puedeABMDetalle = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS) && reqDetalle.isEditable();

List<RequerimientoCompraDetalle> detalles = reqDetalle.getDetalles();
if (detalles == null) {
    detalles = new ArrayList<RequerimientoCompraDetalle>();
}

PortletURL detalleActionURL = renderResponse.createActionURL();
detalleActionURL.setWindowState(WindowState.MAXIMIZED);
detalleActionURL.setParameter("struts_action", "/compras/editar_requerimiento");
%>

<fieldset class="block-labels">
    <legend>Detalle del requerimiento</legend>

    <table class="lfr-table taglib-search-iterator" width="100%">
        <thead>
            <tr class="portlet-section-header results-header">
                <th>Renglón</th>
                <th>Tipo artículo</th>
                <th>Artículo</th>
                <th>Cantidad</th>
                <th>Unidad</th>
                <th>Precio unitario estimado</th>
                <th>Total estimado</th>
                <th>Observaciones</th>
                <c:if test="<%= puedeABMDetalle %>">
                    <th>Acciones</th>
                </c:if>
            </tr>
        </thead>
        <tbody>
            <% for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle detalle = detalles.get(i);
            %>
                <tr class="<%= (i % 2 == 0) ? "portlet-section-body results-row" : "portlet-section-alternate results-row alt" %>">
                    <td><%= HtmlUtil.escape(detalle.getRenglonString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getTipoArticuloVisible()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getArticuloVisible()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getCantidadString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getUnidadMedidaVisible()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getPrecioUnitarioEstimadoString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getPrecioTotalEstimadoString()) %></td>
                    <td><%= HtmlUtil.escape(detalle.getObservacionesVisible()) %></td>

                    <c:if test="<%= puedeABMDetalle %>">
                        <td>
                            <input type="button"
                                   value="Editar"
                                   onclick="<portlet:namespace />editarDetalle('<%= detalle.getIdRequerimientoDetalle() %>', '<%= jsDetalleCompraAttr(detalle.getRenglonString()) %>', '<%= jsDetalleCompraAttr(detalle.getTipoArticuloVisible()) %>', '<%= jsDetalleCompraAttr(detalle.getArticuloVisible()) %>', '<%= jsDetalleCompraAttr(detalle.getCantidadString()) %>', '<%= jsDetalleCompraAttr(detalle.getUnidadMedidaVisible()) %>', '<%= jsDetalleCompraAttr(detalle.getPrecioUnitarioEstimadoString()) %>', '<%= jsDetalleCompraAttr(detalle.getPrecioTotalEstimadoString()) %>', '<%= jsDetalleCompraAttr(detalle.getObservacionesVisible()) %>');" />
                            &nbsp;
                            <form action="<%= detalleActionURL.toString() %>" method="post" name="<portlet:namespace />deleteDetalleFm<%= detalle.getIdRequerimientoDetalle() %>">
                                <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="deleteItem" />
                                <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= reqDetalle.getIdRequerimientoCompra() %>" />
                                <input type="hidden" name="<portlet:namespace />id_requerimiento_detalle" value="<%= detalle.getIdRequerimientoDetalle() %>" />
                                <input type="button" value="Borrar" onclick="if(confirm('¿Confirma borrar el renglón?')) submitForm(document.<portlet:namespace />deleteDetalleFm<%= detalle.getIdRequerimientoDetalle() %>);" />
                            </form>
                        </td>
                    </c:if>
                </tr>
            <% } %>

            <c:if test="<%= detalles.size() == 0 %>">
                <tr>
                    <td colspan="<%= puedeABMDetalle ? "9" : "8" %>">No hay renglones cargados.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <c:if test="<%= puedeABMDetalle %>">
        <form action="<%= detalleActionURL.toString() %>" method="post" name="<portlet:namespace />detalleFm">
            <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace />detalle_cmd" value="addItem" />
            <input type="hidden" name="<portlet:namespace />id_requerimiento_detalle" id="<portlet:namespace />id_requerimiento_detalle" value="0" />
            <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= reqDetalle.getIdRequerimientoCompra() %>" />

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Renglón:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />renglon" id="<portlet:namespace />renglon" size="5" maxlength="5" />
                    </td>

                    <td><label>Tipo artículo:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />tipo_articulo" id="<portlet:namespace />tipo_articulo" size="25" maxlength="80" />
                    </td>

                    <td><label>Artículo:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />articulo" id="<portlet:namespace />articulo" size="45" maxlength="255" />
                    </td>
                </tr>

                <tr>
                    <td colspan="6">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Cantidad:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />cantidad" id="<portlet:namespace />cantidad" size="8" value="1" />
                    </td>

                    <td><label>Unidad:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />unidad_medida" id="<portlet:namespace />unidad_medida" size="12" maxlength="30" />
                    </td>

                    <td><label>Precio unitario:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />precio_unitario_estimado" id="<portlet:namespace />precio_unitario_estimado" size="12" value="0" />
                    </td>
                </tr>

                <tr>
                    <td colspan="6">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Total estimado:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />precio_total_estimado" id="<portlet:namespace />precio_total_estimado" size="12" />
                    </td>

                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <input type="text" name="<portlet:namespace />observaciones_detalle" id="<portlet:namespace />observaciones_detalle" size="80" maxlength="500" />
                    </td>
                </tr>

                <tr>
                    <td colspan="6">&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="6" align="center">
                        <input type="button" id="<portlet:namespace />detalle_submit" value="Agregar renglon" onclick="<portlet:namespace />agregarDetalle();" />
                        &nbsp;&nbsp;<input type="button" id="<portlet:namespace />detalle_cancelar" value="Cancelar edicion" style="display:none;" onclick="<portlet:namespace />cancelarEdicionDetalle();" />
                    </td>
                </tr>
            </table>
        </form>
    </c:if>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />detalleValue(value) {
        return value == null ? "" : value;
    }

    function <portlet:namespace />editarDetalle(idDetalle, renglon, tipoArticulo, articulo, cantidad, unidadMedida, precioUnitario, precioTotal, observaciones) {
        jQuery("#<portlet:namespace />detalle_cmd").val("updateItem");
        jQuery("#<portlet:namespace />id_requerimiento_detalle").val(<portlet:namespace />detalleValue(idDetalle));
        jQuery("#<portlet:namespace />renglon").val(<portlet:namespace />detalleValue(renglon));
        jQuery("#<portlet:namespace />tipo_articulo").val(<portlet:namespace />detalleValue(tipoArticulo));
        jQuery("#<portlet:namespace />articulo").val(<portlet:namespace />detalleValue(articulo));
        jQuery("#<portlet:namespace />cantidad").val(<portlet:namespace />detalleValue(cantidad));
        jQuery("#<portlet:namespace />unidad_medida").val(<portlet:namespace />detalleValue(unidadMedida));
        jQuery("#<portlet:namespace />precio_unitario_estimado").val(<portlet:namespace />detalleValue(precioUnitario));
        jQuery("#<portlet:namespace />precio_total_estimado").val(<portlet:namespace />detalleValue(precioTotal));
        jQuery("#<portlet:namespace />observaciones_detalle").val(<portlet:namespace />detalleValue(observaciones));
        jQuery("#<portlet:namespace />detalle_submit").val("Guardar renglon");
        jQuery("#<portlet:namespace />detalle_cancelar").show();
        jQuery("#<portlet:namespace />articulo").focus();
    }

    function <portlet:namespace />cancelarEdicionDetalle() {
        jQuery("#<portlet:namespace />detalle_cmd").val("addItem");
        jQuery("#<portlet:namespace />id_requerimiento_detalle").val("0");
        jQuery("#<portlet:namespace />renglon").val("");
        jQuery("#<portlet:namespace />tipo_articulo").val("");
        jQuery("#<portlet:namespace />articulo").val("");
        jQuery("#<portlet:namespace />cantidad").val("1");
        jQuery("#<portlet:namespace />unidad_medida").val("");
        jQuery("#<portlet:namespace />precio_unitario_estimado").val("0");
        jQuery("#<portlet:namespace />precio_total_estimado").val("");
        jQuery("#<portlet:namespace />observaciones_detalle").val("");
        jQuery("#<portlet:namespace />detalle_submit").val("Agregar renglon");
        jQuery("#<portlet:namespace />detalle_cancelar").hide();
    }

    function <portlet:namespace />agregarDetalle() {
        if (jQuery("#<portlet:namespace />articulo").val() == "") {
            alert("Debe informar articulo.");
            return;
        }

        if (jQuery("#<portlet:namespace />cantidad").val() == "") {
            alert("Debe informar cantidad.");
            return;
        }

        submitForm(document.<portlet:namespace />detalleFm);
    }
</script>
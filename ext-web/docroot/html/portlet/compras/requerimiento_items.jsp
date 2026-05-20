<%@ include file="/html/portlet/compras/init.jsp" %>

<%
RequerimientoCompra reqItems = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (reqItems == null) {
    reqItems = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}
if (reqItems == null) {
    int idReq = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
    if (idReq > 0) {
        reqItems = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReq);
    }
}
if (reqItems == null) {
    reqItems = new RequerimientoCompra();
}

boolean puedeABMItems = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS) && reqItems.isEditable();
List<RequerimientoCompraItem> items = reqItems.getItems();
if (items == null) {
    items = new ArrayList<RequerimientoCompraItem>();
}

PortletURL itemActionURL = renderResponse.createActionURL();
itemActionURL.setWindowState(WindowState.MAXIMIZED);
itemActionURL.setParameter("struts_action", "/compras/editar_requerimiento");
%>

<fieldset class="block-labels">
    <legend>Items del requerimiento</legend>

    <table class="lfr-table">
        <tr class="portlet-section-header results-header">
            <th>Descripción</th>
            <th>Cantidad</th>
            <th>Unidad</th>
            <th>Importe estimado</th>
            <th>Subtotal</th>
            <th>Observaciones</th>
            <c:if test="<%= puedeABMItems %>"><th>Acciones</th></c:if>
        </tr>

        <% for (int i = 0; i < items.size(); i++) {
            RequerimientoCompraItem item = items.get(i);
        %>
            <tr class="<%= (i % 2 == 0) ? "portlet-section-body results-row" : "portlet-section-alternate results-row alt" %>">
                <td><%= item.getDescripcion() != null ? item.getDescripcion() : "" %></td>
                <td><%= item.getCantidadString() %></td>
                <td><%= item.getUnidadMedida() != null ? item.getUnidadMedida() : "" %></td>
                <td><%= item.getImporteEstimadoString() %></td>
                <td><%= item.getSubtotalEstimadoString() %></td>
                <td><%= item.getObservaciones() != null ? item.getObservaciones() : "" %></td>
                <c:if test="<%= puedeABMItems %>">
                    <td>
                        <form action="<%= itemActionURL.toString() %>" method="post" name="<portlet:namespace />deleteItemFm<%= item.getIdItem() %>">
                            <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="deleteItem" />
                            <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= reqItems.getIdRequerimientoCompra() %>" />
                            <input type="hidden" name="<portlet:namespace />id_item" value="<%= item.getIdItem() %>" />
                            <input type="button" value="Borrar" onclick="if(confirm('Confirma borrar el item?')) submitForm(document.<portlet:namespace />deleteItemFm<%= item.getIdItem() %>);" />
                        </form>
                    </td>
                </c:if>
            </tr>
        <% } %>

        <c:if test="<%= items.size() == 0 %>">
            <tr>
                <td colspan="7">No hay items cargados.</td>
            </tr>
        </c:if>
    </table>

    <c:if test="<%= puedeABMItems %>">
        <form action="<%= itemActionURL.toString() %>" method="post" name="<portlet:namespace />itemFm">
            <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="addItem" />
            <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= reqItems.getIdRequerimientoCompra() %>" />

            <table class="lfr-table">
                <tr>
                    <td><label>Descripción:</label></td>
                    <td><input type="text" name="<portlet:namespace />item_descripcion" id="<portlet:namespace />item_descripcion" size="50" maxlength="255" /></td>
                    <td><label>Cantidad:</label></td>
                    <td><input type="text" name="<portlet:namespace />item_cantidad" id="<portlet:namespace />item_cantidad" size="8" value="1" /></td>
                    <td><label>Unidad:</label></td>
                    <td><input type="text" name="<portlet:namespace />item_unidad_medida" id="<portlet:namespace />item_unidad_medida" size="8" maxlength="30" /></td>
                </tr>
                <tr>
                    <td><label>Importe estimado:</label></td>
                    <td><input type="text" name="<portlet:namespace />item_importe_estimado" id="<portlet:namespace />item_importe_estimado" size="12" value="0" /></td>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3"><input type="text" name="<portlet:namespace />item_observaciones" id="<portlet:namespace />item_observaciones" size="70" maxlength="500" /></td>
                </tr>
                <tr>
                    <td colspan="6"><input type="button" value="Agregar item" onclick="<portlet:namespace />agregarItem();" /></td>
                </tr>
            </table>
        </form>
    </c:if>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />agregarItem() {
        if (jQuery("#<portlet:namespace />item_descripcion").val() == "") {
            alert("Debe informar descripcion del item.");
            return;
        }
        submitForm(document.<portlet:namespace />itemFm);
    }
</script>

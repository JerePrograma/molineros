<%-- Referencias: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp:83-110;
     Autorizaciones/pre_autorizaciones/preautorizacion_edit.jsp:905-913.
     Datos comunes de creacion, consulta y suministro; no ejecuta persistencia. --%>
<table class="lfr-table" width="100%"><tr>
<td><label>ID Requerimiento:</label> <input type="text" size="12" readonly="readonly" value="<%= ordenCompra.getIdRequerimientoCompra() %>" /></td>
<td><label>ID Reclamo Prestacional:</label> <input type="text" size="12" readonly="readonly" value="<%= ordenCompra.getIdReclamoPrestacional() > 0 ? String.valueOf(ordenCompra.getIdReclamoPrestacional()) : "" %>" /></td>
<td><label>ID Liquidaci&oacute;n:</label> <input type="text" size="12" readonly="readonly" value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_id_liquidacion", ordenSeguimiento.getIdLiquidacion()) %>" /></td>
<td><label>ID Orden de Pago:</label> <input type="text" size="12" readonly="readonly" value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_id_orden_pago", ordenSeguimiento.getIdOrdenPago()) %>" /></td>
</tr></table>
<fieldset class="block-labels">
<legend><%= ordenSuministro ? "Gesti&oacute;n de Suministro" : "Orden de Compra" %></legend>
<table class="lfr-table" width="100%"><tr>
<td><label>N&uacute;mero OC:</label></td><td><input type="text" size="20" readonly="readonly" value="<%= ordenNueva ? "Se asigna al guardar" : String.valueOf(ordenCompra.getIdOrdenCompra()) %>" /></td>
<td><label>Fecha Alta OC:</label></td><td><input type="text" size="12" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getAltaFechaAsString()) %>" /></td>
<td><label>Sector:</label></td><td><input type="text" size="40" readonly="readonly" value="<%= ordenCompraTexto(ordenRequerimiento.getSectorDescripcionVisible()) %>" /></td>
</tr>
<% if (ordenSuministro) { %>
<tr><td><label>Prestador adjudicado:</label></td><td colspan="5"><input type="text" size="80" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedor()) %>" /></td></tr>
<% } %>
</table>
</fieldset>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_afiliado.jsp" %>
<% if (!ordenSuministro) { %>
<fieldset class="block-labels"><legend>Datos del Prestador / Proveedor</legend>
<table class="lfr-table" width="100%">
<tr><td><label>Raz&oacute;n social / Nombre:</label></td><td><input type="text" size="65" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedor()) %>" /></td>
<td><label>CUIT Proveedor:</label></td><td><input type="text" size="18" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedorCuit()) %>" /></td></tr>
<tr><td><label>Contacto:</label></td><td><input type="text" size="65" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedorContacto()) %>" /></td>
<td><label>Tel&eacute;fono:</label></td><td><input type="text" size="25" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedorTelefono()) %>" /></td></tr>
<tr><td><label>Correo electr&oacute;nico:</label></td><td colspan="3"><input type="text" size="95" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedorEmail()) %>" /></td></tr>
</table></fieldset>
<fieldset class="block-labels"><legend>Detalle de la compra</legend>
<table class="lfr-table taglib-search-iterator" width="100%">
<thead><tr class="portlet-section-header results-header"><th width="8%">Cantidad</th><th width="12%">C&oacute;digo Art.</th><th>Art&iacute;culo / prestaci&oacute;n</th><th width="17%">Precio unit. ($)</th><th width="17%">Subtotal ($)</th></tr></thead>
<tbody>
<% int ordenFilaDatos = 0;
   for (RequerimientoCompraDetalle detalleOrden : ordenCompra.getDetalles()) { %>
<tr class="results-row<%= ordenFilaDatos++ % 2 == 0 ? "" : " alt" %>">
<td align="center"><%= detalleOrden.getCantidad() %></td><td><%= ordenCompraTexto(detalleOrden.getCodigoItemVisible()) %></td>
<td><%= ordenCompraTexto(detalleOrden.getDescripcionItemVisible()) %></td>
<td align="right"><% if (ordenEditable && ordenRequerimiento.esSectorSinCotizacionPrestador() && detalleOrden.getPrecioUnitarioEstimado() == null) {
    String campoPrecio = "precio_" + detalleOrden.getIdInt(); %>
<input type="text" name="<portlet:namespace /><%= campoPrecio %>" id="<portlet:namespace /><%= campoPrecio %>" size="17" maxlength="18" title="Precio unitario obligatorio" value="<%= ordenCompraValor(ordenEntrada, campoPrecio, null) %>" />
<% } else { %><%= ordenCompraImporte(detalleOrden.getPrecioUnitarioEstimado()) %><% } %></td>
<td align="right"><%= ordenCompraImporte(detalleOrden.getPrecioTotalEstimado()) %></td>
</tr><% } %>
<tr><td colspan="3" align="right">IVA: <%= ordenCompraImporte(ordenCompra.getIva()) %> &nbsp; IIBB: <%= ordenCompraImporte(ordenCompra.getIibb()) %></td>
<td align="right"><b>TOTAL ORDEN DE COMPRA:</b></td><td align="right"><b><%= ordenCompraImporte(ordenCompra.getTotal()) %></b></td></tr>
</tbody></table>
</fieldset>
<fieldset class="block-labels"><legend>Entrega confirmada por el proveedor</legend>
<table class="lfr-table"><tr><td><label>Fecha estimada de entrega confirmada por el proveedor<%= ordenNueva ? " (*)" : "" %>:</label></td><td>
<% if (ordenEditable && ordenNueva) {
    Calendar fechaEntregaEstimada = Calendar.getInstance();
    boolean entregaInformada = ordenCompra.getFechaEstimadaEntrega() != null;
    if (entregaInformada) fechaEntregaEstimada.setTime(ordenCompra.getFechaEstimadaEntrega());
    int entregaDia = ordenEntrada != null ? com.liferay.portal.kernel.util.GetterUtil.getInteger(ordenEntrada.get("entregaEstimadaDia"), -1) : entregaInformada ? fechaEntregaEstimada.get(Calendar.DATE) : -1;
    int entregaMes = ordenEntrada != null ? com.liferay.portal.kernel.util.GetterUtil.getInteger(ordenEntrada.get("entregaEstimadaMes"), -1) : entregaInformada ? fechaEntregaEstimada.get(Calendar.MONTH) : -1;
    int entregaAnio = ordenEntrada != null ? com.liferay.portal.kernel.util.GetterUtil.getInteger(ordenEntrada.get("entregaEstimadaAnio"), -1) : entregaInformada ? fechaEntregaEstimada.get(Calendar.YEAR) : -1;
%>
<liferay-ui:input-date dayParam="entregaEstimadaDia" dayValue="<%= entregaDia %>" dayNullable="true"
    monthParam="entregaEstimadaMes" monthValue="<%= entregaMes %>" monthNullable="true"
    yearParam="entregaEstimadaAnio" yearValue="<%= entregaAnio %>" yearNullable="true"
    yearRangeStart="2000" yearRangeEnd="<%= fechaEntregaEstimada.get(Calendar.YEAR) + 20 %>" firstDayOfWeek="<%= fechaEntregaEstimada.getFirstDayOfWeek() - 1 %>" />
<% } else { %>
<input type="text" size="15" readonly="readonly" value="<%= ordenCompra.getFechaEstimadaEntrega() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(ordenCompra.getFechaEstimadaEntrega()) %>" />
<% } %>
</td></tr></table></fieldset>
<fieldset class="block-labels"><legend>Condiciones y observaciones</legend>
<table class="lfr-table" width="100%">
<tr><td><label>Forma de pago:</label></td><td><input type="text" name="<portlet:namespace />forma_pago" size="40" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getFormaPago()) %>" /></td>
<td><label>Plazo de pago:</label></td><td><input type="text" name="<portlet:namespace />plazo_pago" size="40" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getPlazoPago()) %>" /></td></tr>
<tr><td><label>Condiciones:</label></td><td colspan="3"><textarea name="<portlet:namespace />condicion" rows="2" cols="90" readonly="readonly"><%= ordenCompraTexto(ordenCompra.getCondicion()) %></textarea></td></tr>
<tr><td><label>Entrega:</label></td><td colspan="3"><textarea name="<portlet:namespace />entrega" rows="2" cols="90" readonly="readonly"><%= ordenCompraTexto(ordenCompra.getEntrega()) %></textarea></td></tr>
<tr><td><label>Observaciones:</label></td><td colspan="3"><textarea name="<portlet:namespace />observaciones" rows="2" cols="90" readonly="readonly"><%= ordenCompraTexto(ordenCompra.getObservaciones()) %></textarea></td></tr>
</table>
<% if (ordenNueva) { %>
<input type="hidden" name="<portlet:namespace />iva" value="<%= ordenCompra.getIva().toPlainString() %>" />
<input type="hidden" name="<portlet:namespace />iibb" value="<%= ordenCompra.getIibb().toPlainString() %>" />
<% } %>
</fieldset>
<% } %>

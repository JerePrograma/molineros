<%-- Referencia: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp: consulta e imprimirOP tras persistir. --%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden" %>
<%!
private String ordenCompraTexto(Object valor) {
    return HtmlUtil.escape(valor == null || valor.toString().trim().length() == 0
            ? "No informado" : valor.toString());
}
%>
<%
RequerimientoCompraOrden ordenCompra = (RequerimientoCompraOrden) renderRequest.getAttribute("ordenCompra");
String ordenError = (String) renderRequest.getAttribute("ordenCompraError");
PortletURL ordenVolverURL = renderResponse.createRenderURL();
ordenVolverURL.setWindowState(WindowState.MAXIMIZED);
ordenVolverURL.setParameter("struts_action", "/compras/ver_requerimiento");
ordenVolverURL.setParameter("id_requerimiento_compra", ParamUtil.getString(renderRequest, "id_requerimiento_compra"));
%>
<h2>Orden de Compra</h2>
<% if (ordenError != null && ordenError.length() > 0) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape(ordenError) %></div>
<% } %>
<% if (ordenCompra != null) {
    RequerimientoCompra ordenRequerimiento = ordenCompra.getRequerimiento();
%>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<div class="portlet-msg-success">La Orden de Compra N° <%= ordenCompra.getIdOrdenCompra() %> fue creada con los datos existentes. Se inició la descarga del PDF.</div>
<% } %>
<fieldset class="block-labels">
<legend>Datos de la orden emitida</legend>
<table class="lfr-table" width="100%">
<tr><td><label>Número:</label></td><td><%= ordenCompra.getIdOrdenCompra() %></td>
    <td><label>Fecha de emisión:</label></td><td><%= ordenCompra.getFechaEmision() == null ? "No informada" : new SimpleDateFormat("dd/MM/yyyy").format(ordenCompra.getFechaEmision()) %></td></tr>
<tr><td><label>Requerimiento:</label></td><td><%= ordenCompra.getIdRequerimientoCompra() %></td>
    <td><label>Área solicitante:</label></td><td><%= ordenCompraTexto(ordenRequerimiento.getSectorDescripcionVisible()) %></td></tr>
<tr><td><label>Proveedor adjudicado:</label></td><td><%= ordenCompraTexto(ordenCompra.getProveedor()) %></td>
    <td><label>CUIT:</label></td><td><%= ordenCompraTexto(ordenCompra.getProveedorCuit()) %></td></tr>
<tr><td><label>Moneda:</label></td><td><%= ordenCompraTexto(ordenCompra.getMoneda()) %></td>
    <td><label>Forma de pago:</label></td><td><%= ordenCompraTexto(ordenCompra.getFormaPago()) %></td></tr>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>Detalle de la compra</legend>
<table class="lfr-table" width="100%">
<thead><tr><th>Cantidad</th><th>Artículo / prestación</th><th>Precio unitario</th><th>Precio total</th></tr></thead>
<tbody>
<% for (RequerimientoCompraDetalle detalleOrden : ordenCompra.getDetalles()) { %>
<tr><td><%= detalleOrden.getCantidad() %></td><td><%= ordenCompraTexto(detalleOrden.getDescripcionItemVisible()) %></td>
    <td><%= ordenCompraTexto(detalleOrden.getPrecioUnitarioEstimado()) %></td>
    <td><%= ordenCompraTexto(detalleOrden.getPrecioTotalEstimado()) %></td></tr>
<% } %>
</tbody>
</table>
<table class="lfr-table">
<tr><td><label>IVA (importe):</label></td><td><%= ordenCompraTexto(ordenCompra.getIva()) %></td></tr>
<tr><td><label>IIBB (importe):</label></td><td><%= ordenCompraTexto(ordenCompra.getIibb()) %></td></tr>
<tr><td><strong>Total:</strong></td><td><strong><%= ordenCompraTexto(ordenCompra.getTotal()) %></strong></td></tr>
</table>
</fieldset>
<input type="button" value="Descargar Orden de Compra PDF"
    onclick="window.location.href='/pdfservlet/?accion=ordenCompra&amp;id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';" />
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<script type="text/javascript">
window.location.href = '/pdfservlet/?accion=ordenCompra&id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';
</script>
<% } %>
<% } %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />

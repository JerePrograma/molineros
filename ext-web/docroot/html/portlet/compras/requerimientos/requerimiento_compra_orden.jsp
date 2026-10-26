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
boolean ordenEditable = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraEditable"));
PortletURL ordenGuardarURL = renderResponse.createActionURL();
ordenGuardarURL.setWindowState(WindowState.MAXIMIZED);
ordenGuardarURL.setParameter("struts_action", "/compras/orden_compra");
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
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraActualizada"))) { %>
<div class="portlet-msg-success">Las condiciones y observaciones de la Orden de Compra fueron actualizadas.</div>
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
<% if (ordenEditable) { %>
<form action="<%= ordenGuardarURL.toString() %>" method="post" name="<portlet:namespace />ordenCompraCondicionesForm"
      onsubmit="document.getElementById('<portlet:namespace />guardarCondicionesOrdenCompra').disabled = true;">
<input type="hidden" name="<portlet:namespace />cmd" value="update" />
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />id_orden_compra" value="<%= ordenCompra.getIdOrdenCompra() %>" />
<input type="hidden" name="<portlet:namespace />version_edicion" value="<%= ordenCompra.getVersionEdicion() %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token"
       value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<% } %>
<fieldset class="block-labels">
<legend>Condiciones y observaciones</legend>
<table class="lfr-table" width="100%">
<tr><td><label for="<portlet:namespace />condicionOrdenCompra">Condiciones:</label></td><td>
<% if (ordenEditable) { %>
<textarea rows="3" cols="80" id="<portlet:namespace />condicionOrdenCompra" name="<portlet:namespace />condicion"><%= HtmlUtil.escape(ordenCompra.getCondicion() == null ? "" : ordenCompra.getCondicion()) %></textarea>
<% } else { %><%= ordenCompraTexto(ordenCompra.getCondicion()) %><% } %>
</td></tr>
<tr><td><label for="<portlet:namespace />formaPagoOrdenCompra">Forma de pago:</label></td><td>
<% if (ordenEditable) { %>
<input type="text" size="80" maxlength="250" id="<portlet:namespace />formaPagoOrdenCompra" name="<portlet:namespace />forma_pago" value="<%= HtmlUtil.escape(ordenCompra.getFormaPago() == null ? "" : ordenCompra.getFormaPago()) %>" />
<% } else { %><%= ordenCompraTexto(ordenCompra.getFormaPago()) %><% } %>
</td></tr>
<tr><td><label for="<portlet:namespace />plazoPagoOrdenCompra">Plazo de pago:</label></td><td>
<% if (ordenEditable) { %>
<input type="text" size="80" maxlength="250" id="<portlet:namespace />plazoPagoOrdenCompra" name="<portlet:namespace />plazo_pago" value="<%= HtmlUtil.escape(ordenCompra.getPlazoPago() == null ? "" : ordenCompra.getPlazoPago()) %>" />
<% } else { %><%= ordenCompraTexto(ordenCompra.getPlazoPago()) %><% } %>
</td></tr>
<tr><td><label for="<portlet:namespace />entregaOrdenCompra">Entrega:</label></td><td>
<% if (ordenEditable) { %>
<textarea rows="3" cols="80" id="<portlet:namespace />entregaOrdenCompra" name="<portlet:namespace />entrega"><%= HtmlUtil.escape(ordenCompra.getEntrega() == null ? "" : ordenCompra.getEntrega()) %></textarea>
<% } else { %><%= ordenCompraTexto(ordenCompra.getEntrega()) %><% } %>
</td></tr>
<tr><td><label for="<portlet:namespace />observacionesOrdenCompra">Observaciones:</label></td><td>
<% if (ordenEditable) { %>
<textarea rows="5" cols="80" id="<portlet:namespace />observacionesOrdenCompra" name="<portlet:namespace />observaciones"><%= HtmlUtil.escape(ordenCompra.getObservaciones() == null ? "" : ordenCompra.getObservaciones()) %></textarea>
<% } else { %><%= ordenCompraTexto(ordenCompra.getObservaciones()) %><% } %>
</td></tr>
</table>
<% if (ordenEditable) { %>
<input type="submit" id="<portlet:namespace />guardarCondicionesOrdenCompra" value="Guardar condiciones y observaciones" />
<% } %>
</fieldset>
<% if (ordenEditable) { %>
</form>
<% } %>
<input type="button" value="Descargar Orden de Compra PDF"
    onclick="window.location.href='/pdfservlet/?accion=ordenCompra&amp;id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';" />
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<script type="text/javascript">
window.location.href = '/pdfservlet/?accion=ordenCompra&id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';
</script>
<% } %>
<% } %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />

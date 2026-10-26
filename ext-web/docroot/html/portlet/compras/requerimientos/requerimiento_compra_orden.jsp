<%-- Referencia: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp: cabecera readonly, fieldsets y acciones Guardar/Imprimir en una fila. --%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden" %>
<%@ page import="java.util.Map" %>
<%!
private String ordenCompraValor(Map<String, String> entrada, String campo, Object original) {
    String valor = entrada != null && entrada.containsKey(campo)
            ? entrada.get(campo) : (original == null ? "" : original.toString());
    return HtmlUtil.escape(valor == null ? "" : valor);
}
private String ordenCompraTexto(Object valor) {
    return HtmlUtil.escape(valor == null || valor.toString().trim().length() == 0
            ? "" : valor.toString());
}
%>
<%
RequerimientoCompraOrden ordenCompra = (RequerimientoCompraOrden) renderRequest.getAttribute("ordenCompra");
String ordenError = (String) renderRequest.getAttribute("ordenCompraError");
boolean ordenEditable = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraEditable"));
boolean ordenNueva = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraPuedeCrear"));
boolean ordenIncompleta = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraRequiereDatos"));
Map<String, String> ordenEntrada = (Map<String, String>) renderRequest.getAttribute("ordenCompraEntrada");
PortletURL ordenGuardarURL = renderResponse.createActionURL();
ordenGuardarURL.setWindowState(WindowState.MAXIMIZED);
ordenGuardarURL.setParameter("struts_action", "/compras/orden_compra");
PortletURL ordenVolverURL = renderResponse.createRenderURL();
ordenVolverURL.setWindowState(WindowState.MAXIMIZED);
ordenVolverURL.setParameter("struts_action", "/compras/ver_requerimiento");
ordenVolverURL.setParameter("id_requerimiento_compra", ParamUtil.getString(renderRequest, "id_requerimiento_compra"));
%>
<% if (ordenError != null && ordenError.length() > 0) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape(ordenError) %></div>
<% } %>
<% if (ordenCompra != null) {
    RequerimientoCompra ordenRequerimiento = ordenCompra.getRequerimiento();
%>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<div class="portlet-msg-success">La Orden de Compra N° <%= ordenCompra.getIdOrdenCompra() %> fue creada. Se inició la descarga del PDF.</div>
<% } %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraActualizada"))) { %>
<div class="portlet-msg-success">Los datos de la Orden de Compra fueron actualizados.</div>
<% } %>
<% if (ordenIncompleta) { %>
<div class="portlet-msg-info"><%= ordenEditable ? "Complete los precios unitarios antes de emitir o descargar la Orden de Compra." : "La Orden de Compra tiene precios pendientes de completar." %> Los dem&aacute;s datos son opcionales.</div>
<% } %>
<% if (ordenEditable) { %>
<form action="<%= ordenGuardarURL.toString() %>" method="post" name="<portlet:namespace />ordenCompraCondicionesForm"
      onsubmit="document.getElementById('<portlet:namespace />guardarCondicionesOrdenCompra').disabled = true;">
<input type="hidden" name="<portlet:namespace />cmd" value="<%= ordenNueva ? "create" : "update" %>" />
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />id_orden_compra" value="<%= ordenCompra.getIdOrdenCompra() %>" />
<input type="hidden" name="<portlet:namespace />version_edicion" value="<%= ordenCompra.getVersionEdicion() %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token"
       value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<% } %>
<fieldset class="block-labels">
<legend><%= ordenNueva ? "Crear Orden de Compra" : "Orden de Compra" %></legend>
<table class="lfr-table" width="100%">
<tr>
    <td><label>N&uacute;mero:</label></td>
    <td><input type="text" size="20" readonly="readonly" value="<%= ordenNueva ? "Se asigna al guardar" : String.valueOf(ordenCompra.getIdOrdenCompra()) %>" /></td>
    <td><label>Fecha de emisi&oacute;n:</label></td>
    <td><input type="text" size="15" readonly="readonly" value="<%= ordenCompra.getFechaEmision() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(ordenCompra.getFechaEmision()) %>" /></td>
    <td><label>Requerimiento:</label></td>
    <td><input type="text" size="15" readonly="readonly" value="<%= ordenCompra.getIdRequerimientoCompra() %>" /></td>
</tr>
<tr>
    <td><label>&Aacute;rea solicitante:</label></td>
    <td colspan="3"><input type="text" size="50" readonly="readonly" value="<%= ordenCompraTexto(ordenRequerimiento.getSectorDescripcionVisible()) %>" /></td>
    <td><label>Importes:</label></td>
    <td><input type="text" size="20" readonly="readonly" value="Pesos argentinos (ARS)" /></td>
</tr>
<tr><td colspan="6">&nbsp;</td></tr>
<tr>
    <td><label>Proveedor adjudicado:</label></td>
    <td colspan="3"><input type="text" size="80" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedor()) %>" /></td>
    <td><label>CUIT:</label></td>
    <td><input type="text" size="15" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getProveedorCuit()) %>" /></td>
</tr>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>Detalle de la compra</legend>
<table class="lfr-table" width="100%">
<thead><tr><th width="10%">Cantidad</th><th>Art&iacute;culo / prestaci&oacute;n</th><th width="20%">Precio unitario<%= ordenIncompleta ? " (*)" : "" %></th><th width="20%">Precio total</th></tr></thead>
<tbody>
<% for (RequerimientoCompraDetalle detalleOrden : ordenCompra.getDetalles()) { %>
<tr><td align="center"><%= detalleOrden.getCantidad() %></td><td><%= ordenCompraTexto(detalleOrden.getDescripcionItemVisible()) %></td>
    <td align="right"><% if (ordenEditable && ordenRequerimiento.esSectorSinCotizacionPrestador()
            && detalleOrden.getPrecioUnitarioEstimado() == null) {
        String campoPrecio = "precio_" + detalleOrden.getIdInt(); %>
        <input type="text" id="<portlet:namespace /><%= campoPrecio %>" name="<portlet:namespace /><%= campoPrecio %>"
            title="Precio unitario (*)" size="17" maxlength="18" value="<%= ordenCompraValor(ordenEntrada, campoPrecio, null) %>" />
        <% } else { %><%= ordenCompraTexto(detalleOrden.getPrecioUnitarioEstimado()) %><% } %></td>
    <td align="right"><%= ordenCompraTexto(detalleOrden.getPrecioTotalEstimado()) %></td></tr>
<% } %>
</tbody>
</table>
<table class="lfr-table" width="100%">
<tr><td colspan="6">&nbsp;</td></tr>
<tr>
<% if (ordenEditable && ordenNueva) { %>
    <td><label for="<portlet:namespace />ivaOrdenCompra">IVA (importe):</label></td>
    <td><input type="text" id="<portlet:namespace />ivaOrdenCompra" name="<portlet:namespace />iva" size="17" maxlength="18"
        value="<%= ordenCompraValor(ordenEntrada, "iva", ordenCompra.getIva()) %>" /></td>
    <td><label for="<portlet:namespace />iibbOrdenCompra">IIBB (importe):</label></td>
    <td><input type="text" id="<portlet:namespace />iibbOrdenCompra" name="<portlet:namespace />iibb" size="17" maxlength="18"
        value="<%= ordenCompraValor(ordenEntrada, "iibb", ordenCompra.getIibb()) %>" /></td>
<% } else { %>
    <td><label>IVA (importe):</label></td><td><input type="text" size="17" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getIva()) %>" /></td>
    <td><label>IIBB (importe):</label></td><td><input type="text" size="17" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getIibb()) %>" /></td>
<% } %>
    <td><label>Total:</label></td><td><input type="text" size="17" readonly="readonly" value="<%= ordenCompraTexto(ordenCompra.getTotal()) %>" /></td>
</tr>
<% if (ordenEditable && ordenNueva) { %>
<tr><td colspan="6">IVA e IIBB sin informar se toman como cero. Use importes sin separador de miles y hasta dos decimales.</td></tr>
<% } %>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>Condiciones y observaciones</legend>
<table class="lfr-table" width="100%">
<tr><td valign="top"><label for="<portlet:namespace />condicionOrdenCompra">Condiciones:</label></td><td colspan="3">
<textarea rows="3" cols="80" id="<portlet:namespace />condicionOrdenCompra" name="<portlet:namespace />condicion" <% if (!ordenEditable) { %>readonly="readonly"<% } %>><%= ordenCompraValor(ordenEditable ? ordenEntrada : null, "condicion", ordenCompra.getCondicion()) %></textarea>
</td></tr>
<tr><td><label for="<portlet:namespace />formaPagoOrdenCompra">Forma de pago:</label></td><td>
<input type="text" size="35" maxlength="250" id="<portlet:namespace />formaPagoOrdenCompra" name="<portlet:namespace />forma_pago" <% if (!ordenEditable) { %>readonly="readonly"<% } %> value="<%= ordenCompraValor(ordenEditable ? ordenEntrada : null, "forma_pago", ordenCompra.getFormaPago()) %>" />
</td><td><label for="<portlet:namespace />plazoPagoOrdenCompra">Plazo de pago:</label></td><td>
<input type="text" size="35" maxlength="250" id="<portlet:namespace />plazoPagoOrdenCompra" name="<portlet:namespace />plazo_pago" <% if (!ordenEditable) { %>readonly="readonly"<% } %> value="<%= ordenCompraValor(ordenEditable ? ordenEntrada : null, "plazo_pago", ordenCompra.getPlazoPago()) %>" />
</td></tr>
<tr><td valign="top"><label for="<portlet:namespace />entregaOrdenCompra">Entrega:</label></td><td colspan="3">
<textarea rows="3" cols="80" id="<portlet:namespace />entregaOrdenCompra" name="<portlet:namespace />entrega" <% if (!ordenEditable) { %>readonly="readonly"<% } %>><%= ordenCompraValor(ordenEditable ? ordenEntrada : null, "entrega", ordenCompra.getEntrega()) %></textarea>
</td></tr>
<tr><td valign="top"><label for="<portlet:namespace />observacionesOrdenCompra">Observaciones:</label></td><td colspan="3">
<textarea rows="5" cols="80" id="<portlet:namespace />observacionesOrdenCompra" name="<portlet:namespace />observaciones" <% if (!ordenEditable) { %>readonly="readonly"<% } %>><%= ordenCompraValor(ordenEditable ? ordenEntrada : null, "observaciones", ordenCompra.getObservaciones()) %></textarea>
</td></tr>
</table>
</fieldset>
<table class="lfr-table">
<tr><td align="left">
<% if (ordenEditable) { %>
<input type="submit" id="<portlet:namespace />guardarCondicionesOrdenCompra" value="<%= ordenNueva ? "Guardar y emitir Orden de Compra" : "Guardar datos de la Orden de Compra" %>" />&nbsp;&nbsp;
<% } %>
<% if (!ordenNueva && !ordenIncompleta) { %>
<input type="button" value="Descargar Orden de Compra PDF"
    onclick="window.location.href='/pdfservlet/?accion=ordenCompra&amp;id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';" />&nbsp;&nbsp;
<% } %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
</td></tr>
</table>
<% if (ordenEditable) { %>
</form>
<% } %>
<% if (!ordenNueva && !ordenIncompleta) { %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<script type="text/javascript">
window.location.href = '/pdfservlet/?accion=ordenCompra&id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';
</script>
<% } %>
<% } %>
<% } else { %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
<% } %>

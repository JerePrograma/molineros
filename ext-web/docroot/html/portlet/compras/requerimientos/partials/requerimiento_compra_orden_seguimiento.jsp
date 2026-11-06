<%-- Seguimiento manual: mismos fieldsets, controles y botonera de editar_orden_pago_ospim.jsp.
     No representa confirmaciones automaticas de otros modulos. --%>
<%
String calificacionOrden = ordenCompraValor(ordenSeguimientoEntrada, "seg_calificacion", ordenSeguimiento.getCalificacionProveedor());
String facturaOrden = ordenCompraValor(ordenSeguimientoEntrada, "seg_factura_ingresada", ordenSeguimiento.getFacturaIngresada());
String liquidacionOrden = ordenCompraValor(ordenSeguimientoEntrada, "seg_estado_liquidacion", ordenSeguimiento.getEstadoLiquidacion());
String pagoOrden = ordenCompraValor(ordenSeguimientoEntrada, "seg_estado_pago", ordenSeguimiento.getEstadoPago());
boolean auditadaOrden = ordenSeguimientoEntrada != null ? "true".equals(ordenSeguimientoEntrada.get("seg_liquidacion_auditada")) : ordenSeguimiento.isLiquidacionAuditada();
String fechaFacturaOrden = ordenSeguimiento.getFechaFactura() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(ordenSeguimiento.getFechaFactura());
%>
<fieldset class="block-labels" id="<portlet:namespace />seguimientoOrdenCompra"><legend>Calificaci&oacute;n del Proveedor y Estado de Facturaci&oacute;n / Pago</legend>
<% if (ordenNueva) { %><div class="portlet-msg-info">El seguimiento se completa despu&eacute;s de guardar la Orden de Compra.</div><% } %>
<table class="lfr-table" width="100%"><tr>
<td width="50%" valign="top"><fieldset class="block-labels"><legend>1. Calificaci&oacute;n del proveedor en esta compra</legend>
<% String[] calificacionesOrden = { "", "APTO", "PENDIENTE_ENTREGA", "NO_RESPONDE" };
String[] textosCalificacionesOrden = { "Sin informar", "APTO - Cumple especificaciones t&eacute;cnicas, precios y plazos previstos.", "PENDIENTE DE ENTREGA - Pedido emitido a la espera de recepci&oacute;n.", "NO RESPONDE - Demora en cotizaci&oacute;n o sin acuse de recepci&oacute;n de la orden." };
for (int indiceCalificacion = 0; indiceCalificacion < calificacionesOrden.length; indiceCalificacion++) { %>
<div><label><input type="radio" name="<portlet:namespace />seg_calificacion" value="<%= calificacionesOrden[indiceCalificacion] %>" <%= calificacionesOrden[indiceCalificacion].equals(calificacionOrden) ? "checked='checked'" : "" %> <%= !seguimientoPuedeEditar ? "disabled='disabled'" : "" %> /> <%= textosCalificacionesOrden[indiceCalificacion] %></label></div>
<% } %>
<label for="<portlet:namespace />segObservaciones">Observaciones del proveedor / entrega:</label>
<textarea id="<portlet:namespace />segObservaciones" name="<portlet:namespace />seg_observaciones" rows="3" cols="55" <%= !seguimientoPuedeEditar ? "readonly='readonly'" : "" %>><%= ordenCompraValor(ordenSeguimientoEntrada, "seg_observaciones", ordenSeguimiento.getObservacionesProveedor()) %></textarea>
</fieldset></td>
<td width="50%" valign="top"><fieldset class="block-labels"><legend>2. Control de facturaci&oacute;n y liquidaci&oacute;n</legend>
<table class="lfr-table" width="100%">
<tr><td><label for="<portlet:namespace />segFacturaIngresada">&iquest;Ingres&oacute; factura?</label></td><td>
<select id="<portlet:namespace />segFacturaIngresada" name="<portlet:namespace />seg_factura_ingresada" <%= !seguimientoPuedeEditar ? "disabled='disabled'" : "" %>>
<option value=""></option><option value="SI" <%= "SI".equals(facturaOrden) ? "selected='selected'" : "" %>>S&iacute; (ingresada)</option><option value="NO" <%= "NO".equals(facturaOrden) ? "selected='selected'" : "" %>>No</option></select></td></tr>
<tr><td><label for="<portlet:namespace />segNumeroFactura">N&deg; Factura:</label></td><td><input type="text" id="<portlet:namespace />segNumeroFactura" name="<portlet:namespace />seg_numero_factura" size="24" maxlength="100" <%= !seguimientoPuedeEditar ? "readonly='readonly'" : "" %> value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_numero_factura", ordenSeguimiento.getNumeroFactura()) %>" /></td></tr>
<tr><td><label for="<portlet:namespace />segFechaFactura">Fecha factura:</label></td><td><input type="text" id="<portlet:namespace />segFechaFactura" name="<portlet:namespace />seg_fecha_factura" size="15" maxlength="10" title="dd/mm/aaaa" <%= !seguimientoPuedeEditar ? "readonly='readonly'" : "" %> value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_fecha_factura", fechaFacturaOrden) %>" /> (dd/mm/aaaa)</td></tr>
<tr><td><label for="<portlet:namespace />segEstadoLiquidacion">Estado liquidaci&oacute;n:</label></td><td><select id="<portlet:namespace />segEstadoLiquidacion" name="<portlet:namespace />seg_estado_liquidacion" <%= !seguimientoPuedeEditar ? "disabled='disabled'" : "" %>>
<option value=""></option><option value="PENDIENTE" <%= "PENDIENTE".equals(liquidacionOrden) ? "selected='selected'" : "" %>>Pendiente</option><option value="LIQUIDADA" <%= "LIQUIDADA".equals(liquidacionOrden) ? "selected='selected'" : "" %>>Liquidada</option><option value="NO_CORRESPONDE" <%= "NO_CORRESPONDE".equals(liquidacionOrden) ? "selected='selected'" : "" %>>No corresponde</option></select>
<label><input type="checkbox" name="<portlet:namespace />seg_liquidacion_auditada" value="true" <%= auditadaOrden ? "checked='checked'" : "" %> <%= !seguimientoPuedeEditar ? "disabled='disabled'" : "" %> /> Auditada</label></td></tr>
<tr><td><label for="<portlet:namespace />segIdLiquidacion">ID Liquidaci&oacute;n:</label></td><td><input type="text" id="<portlet:namespace />segIdLiquidacion" name="<portlet:namespace />seg_id_liquidacion" size="15" maxlength="10" <%= !seguimientoPuedeEditar ? "readonly='readonly'" : "" %> value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_id_liquidacion", ordenSeguimiento.getIdLiquidacion()) %>" /></td></tr>
<tr><td><label for="<portlet:namespace />segEstadoPago">Estado de pago:</label></td><td><select id="<portlet:namespace />segEstadoPago" name="<portlet:namespace />seg_estado_pago" <%= !seguimientoPuedeEditar ? "disabled='disabled'" : "" %>>
<option value=""></option><option value="PENDIENTE_PAGO" <%= "PENDIENTE_PAGO".equals(pagoOrden) ? "selected='selected'" : "" %>>Pendiente de pago</option><option value="PAGADO" <%= "PAGADO".equals(pagoOrden) ? "selected='selected'" : "" %>>Pagado</option><option value="NO_CORRESPONDE" <%= "NO_CORRESPONDE".equals(pagoOrden) ? "selected='selected'" : "" %>>No corresponde</option></select></td></tr>
<tr><td><label for="<portlet:namespace />segIdOrdenPago">ID Orden de Pago:</label></td><td><input type="text" id="<portlet:namespace />segIdOrdenPago" name="<portlet:namespace />seg_id_orden_pago" size="15" maxlength="10" <%= !seguimientoPuedeEditar ? "readonly='readonly'" : "" %> value="<%= ordenCompraValor(ordenSeguimientoEntrada, "seg_id_orden_pago", ordenSeguimiento.getIdOrdenPago()) %>" /></td></tr>
</table></fieldset></td>
</tr></table>
<div>Los estados y referencias de este seguimiento se informan manualmente.</div>
</fieldset>
<% if (!ordenNueva) { %>
<div id="<portlet:namespace />trazabilidadOrdenCompra">
Alta seguimiento: <%= ordenCompraTexto(ordenSeguimiento.getAltaUsr()) %> <%= ordenCompraTexto(ordenSeguimiento.getAltaFecha()) %>
&nbsp; | &nbsp; &Uacute;ltima modificaci&oacute;n: <%= ordenCompraTexto(ordenSeguimiento.getModiUsr()) %> <%= ordenCompraTexto(ordenSeguimiento.getModiFecha()) %>
</div>
<% } %>

<%-- Referencia: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp: fieldset, lfr-table y PDF tras guardar. --%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden" %>
<%@ page import="java.util.Map" %>
<%!
private String ordenCompraValor(Map<String, String> entrada, String campo, Object original) {
    String valor = entrada != null && entrada.containsKey(campo)
            ? entrada.get(campo) : (original == null ? "" : original.toString());
    return HtmlUtil.escape(valor == null ? "" : valor);
}
private String ordenCompraFecha(Date fecha) {
    return fecha == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(fecha);
}
%>
<%
RequerimientoCompraOrden ordenCompra = (RequerimientoCompraOrden) renderRequest.getAttribute("ordenCompra");
Map<String, String> ordenEntrada = (Map<String, String>) renderRequest.getAttribute("ordenCompraEntrada");
boolean ordenEditable = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraPuedeCrear"));
String ordenError = (String) renderRequest.getAttribute("ordenCompraError");
PortletURL ordenVolverURL = renderResponse.createRenderURL();
ordenVolverURL.setWindowState(WindowState.MAXIMIZED);
ordenVolverURL.setParameter("struts_action", "/compras/ver_requerimiento");
ordenVolverURL.setParameter("id_requerimiento_compra", ParamUtil.getString(renderRequest, "id_requerimiento_compra"));
PortletURL guardarOrdenURL = renderResponse.createActionURL();
guardarOrdenURL.setWindowState(WindowState.MAXIMIZED);
guardarOrdenURL.setParameter("struts_action", "/compras/orden_compra");
%>
<h2><%= ordenEditable ? "Crear Orden de Compra" : "Orden de Compra" %></h2>
<% if (ordenError != null && ordenError.length() > 0) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape(ordenError) %></div>
<% } %>
<% if (ordenCompra != null) {
    RequerimientoCompra ordenRequerimiento = ordenCompra.getRequerimiento();
    boolean ordenMedica = !ordenRequerimiento.esSectorSinCotizacionPrestador();
    String ordenSoloLectura = ordenEditable ? "" : "readonly=\"readonly\"";
    String ordenPreciosSoloLectura = !ordenEditable || ordenMedica ? "readonly=\"readonly\"" : "";
%>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada"))) { %>
<div class="portlet-msg-success">La Orden de Compra N° <%= ordenCompra.getIdOrdenCompra() %> fue creada. Ya puede descargar el PDF.</div>
<% } %>
<form action="<%= guardarOrdenURL.toString() %>" method="post"
      id="<portlet:namespace />ordenCompraForm" onsubmit="return <portlet:namespace />guardarOrdenCompra();">
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token"
       value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<fieldset class="block-labels">
<legend>Datos de la orden</legend>
<table class="lfr-table" width="100%">
<tr><td><label>Número:</label></td><td><%= ordenCompra.getIdOrdenCompra() > 0 ? String.valueOf(ordenCompra.getIdOrdenCompra()) : "Se asigna al guardar" %></td>
    <td><label>Fecha de emisión:</label></td><td><%= ordenCompraFecha(ordenCompra.getFechaEmision()) %></td></tr>
<tr><td><label>Requerimiento:</label></td><td><%= ordenCompra.getIdRequerimientoCompra() %></td>
    <td><label>Área solicitante:</label></td><td><%= HtmlUtil.escape(ordenRequerimiento.getSectorDescripcionVisible()) %></td></tr>
<tr><td><label>Proveedor adjudicado:</label></td><td><%= HtmlUtil.escape(ordenCompra.getProveedor()) %></td>
    <td><label>CUIT:</label></td><td><%= HtmlUtil.escape(ordenCompra.getProveedorCuit()) %></td></tr>
<tr><td><label for="<portlet:namespace />ordenMoneda">Moneda (*):</label></td><td>
<select id="<portlet:namespace />ordenMoneda" name="<portlet:namespace />moneda" <%= !ordenEditable ? "disabled=\"disabled\"" : "" %>>
<% String ordenMoneda = ordenEntrada != null ? ordenEntrada.get("moneda") : ordenCompra.getMoneda(); %>
<option value="">Seleccione</option>
<option value="ARS" <%= "ARS".equals(ordenMoneda) ? "selected=\"selected\"" : "" %>>Pesos argentinos (ARS)</option>
<option value="USD" <%= "USD".equals(ordenMoneda) ? "selected=\"selected\"" : "" %>>Dólares estadounidenses (USD)</option>
</select></td><td colspan="2">Los precios se expresan en la moneda seleccionada.</td></tr>
<%
String[][] ordenCampos = {
    {"proveedorDomicilio", "Domicilio del proveedor", ordenCompra.getProveedorDomicilio(), "250"},
    {"proveedorContacto", "Contacto del proveedor", ordenCompra.getProveedorContacto(), "250"},
    {"proveedorEmail", "Email del proveedor", ordenCompra.getProveedorEmail(), "250"},
    {"proveedorTelefono", "Teléfono del proveedor", ordenCompra.getProveedorTelefono(), "100"},
    {"confeccionadoPor", "Confeccionado por (*)", ordenCompra.getConfeccionadoPor(), "250"},
    {"telefonoCompras", "Teléfono de Compras", ordenCompra.getTelefonoCompras(), "100"},
    {"solicitante", "Solicitante", ordenCompra.getSolicitante(), "250"},
    {"formaPago", "Forma de pago (*)", ordenCompra.getFormaPago(), "250"},
    {"plazoPago", "Plazo de pago", ordenCompra.getPlazoPago(), "250"}
};
for (int campoOrden = 0; campoOrden < ordenCampos.length; campoOrden++) {
    String[] campo = ordenCampos[campoOrden];
%>
<tr><td><label for="<portlet:namespace />oc_<%= campo[0] %>"><%= campo[1] %>:</label></td>
<td colspan="3"><input type="text" id="<portlet:namespace />oc_<%= campo[0] %>" name="<portlet:namespace /><%= campo[0] %>"
    value="<%= ordenCompraValor(ordenEntrada, campo[0], campo[2]) %>" maxlength="<%= campo[3] %>" size="65" <%= ordenSoloLectura %> /></td></tr>
<% } %>
</table>
</fieldset>
<% if (ordenMedica) { %>
<fieldset class="block-labels">
<legend>Prestaciones Médicas</legend>
<table class="lfr-table" width="100%">
<tr><td><label>Reclamo Prestacional N°:</label></td><td><%= ordenCompra.getIdReclamoPrestacional() %></td>
    <td><label>Afiliado:</label></td><td><%= HtmlUtil.escape(ordenRequerimiento.getAfiliadoNombreApellidoVisible()) %></td></tr>
<tr><td><label>Número de afiliado:</label></td><td><%= ordenRequerimiento.getAfiliadoIdOspim() != null ? ordenRequerimiento.getAfiliadoIdOspim().toString() : "" %></td>
    <td><label>Documento:</label></td><td><%= HtmlUtil.escape(ordenRequerimiento.getAfiliadoDocumentoVisible()) %></td></tr>
<tr><td><label for="<portlet:namespace />oc_plan">Plan:</label></td>
    <td colspan="3"><input type="text" id="<portlet:namespace />oc_plan" name="<portlet:namespace />plan" maxlength="120" size="45"
        value="<%= ordenCompraValor(ordenEntrada, "plan", ordenCompra.getPlan()) %>" <%= ordenSoloLectura %> /></td></tr>
<tr><td><label for="<portlet:namespace />oc_fechaOrdenMedica">Fecha de orden médica:</label></td><td>
    <input type="text" id="<portlet:namespace />oc_fechaOrdenMedica" name="<portlet:namespace />fechaOrdenMedica" size="12" maxlength="10"
        value="<%= ordenCompraValor(ordenEntrada, "fechaOrdenMedica", ordenCompraFecha(ordenCompra.getFechaOrdenMedica())) %>" <%= ordenSoloLectura %> /> (dd/mm/aaaa)</td>
    <td><label for="<portlet:namespace />oc_fechaRecepcion">Fecha recibido en Compras:</label></td><td>
    <input type="text" id="<portlet:namespace />oc_fechaRecepcion" name="<portlet:namespace />fechaRecepcion" size="12" maxlength="10"
        value="<%= ordenCompraValor(ordenEntrada, "fechaRecepcion", ordenCompraFecha(ordenCompra.getFechaRecepcion())) %>" <%= ordenSoloLectura %> /> (dd/mm/aaaa)</td></tr>
</table>
</fieldset>
<% } %>
<fieldset class="block-labels">
<legend>Detalle de la compra</legend>
<% if (ordenEditable) { %>
<p><%= ordenMedica ? "Los importes y cantidades corresponden al presupuesto adjudicado autorizado." : "Complete los precios unitarios del presupuesto adjudicado. Use coma o punto decimal, sin separadores de miles." %></p>
<% } %>
<table class="lfr-table" width="100%">
<thead><tr><th>Cantidad</th><th>Artículo / prestación</th><th>Precio unitario</th><th>Precio total</th></tr></thead>
<tbody>
<% for (RequerimientoCompraDetalle ordenDetalle : ordenCompra.getDetalles()) {
    String campoPrecio = "precio_" + ordenDetalle.getIdInt();
%>
<tr><td><%= ordenDetalle.getCantidad() %></td>
    <td><%= HtmlUtil.escape(ordenDetalle.getDescripcionItemVisible()) %></td>
    <td><input type="text" name="<portlet:namespace /><%= campoPrecio %>" size="17" maxlength="18"
        class="orden-compra-precio" data-cantidad="<%= ordenDetalle.getCantidad() %>"
        value="<%= ordenCompraValor(ordenMedica ? null : ordenEntrada, campoPrecio, ordenDetalle.getPrecioUnitarioEstimado()) %>"
        <%= ordenPreciosSoloLectura %> onkeyup="<portlet:namespace />calcularOrdenCompra();" /></td>
    <td class="orden-compra-subtotal"><%= ordenDetalle.getPrecioTotalEstimado() != null ? ordenDetalle.getPrecioTotalEstimado().toPlainString() : "" %></td></tr>
<% } %>
</tbody>
</table>
<table class="lfr-table">
<tr><td><label for="<portlet:namespace />oc_iva">IVA (importe):</label></td><td>
<input type="text" id="<portlet:namespace />oc_iva" name="<portlet:namespace />iva" size="17" maxlength="18"
    value="<%= ordenCompraValor(ordenMedica ? null : ordenEntrada, "iva", ordenCompra.getIva()) %>" <%= ordenPreciosSoloLectura %> onkeyup="<portlet:namespace />calcularOrdenCompra();" /></td></tr>
<tr><td><label for="<portlet:namespace />oc_iibb">IIBB (importe):</label></td><td>
<input type="text" id="<portlet:namespace />oc_iibb" name="<portlet:namespace />iibb" size="17" maxlength="18"
    value="<%= ordenCompraValor(ordenMedica ? null : ordenEntrada, "iibb", ordenCompra.getIibb()) %>" <%= ordenPreciosSoloLectura %> onkeyup="<portlet:namespace />calcularOrdenCompra();" /></td></tr>
<tr><td><strong>Total:</strong></td><td><strong id="<portlet:namespace />oc_total"><%= ordenCompra.getTotal().toPlainString() %></strong></td></tr>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>Condiciones y entrega</legend>
<table class="lfr-table" width="100%">
<%
String[][] ordenTextos = {
    {"condicion", "Condiciones comerciales / IVA / tipo de cambio", ordenCompra.getCondicion(), "1000"},
    {"entrega", "Lugar e instrucciones de entrega (*)", ordenCompra.getEntrega(), "1000"},
    {"observaciones", "Observaciones para la orden", ordenCompra.getObservaciones(), "4000"}
};
for (int textoOrden = 0; textoOrden < ordenTextos.length; textoOrden++) {
    String[] campo = ordenTextos[textoOrden];
%>
<tr><td><label for="<portlet:namespace />oc_<%= campo[0] %>"><%= campo[1] %>:</label></td><td>
<textarea id="<portlet:namespace />oc_<%= campo[0] %>" name="<portlet:namespace /><%= campo[0] %>" rows="3" cols="75"
    <%= ordenSoloLectura %>><%= ordenCompraValor(ordenEntrada, campo[0], campo[2]) %></textarea></td></tr>
<% } %>
</table>
</fieldset>
<% if (ordenEditable) { %>
<p>(*) Datos obligatorios. Al guardar se asignará el número y el requerimiento pasará a ORDEN DE COMPRA.</p>
<input type="submit" id="<portlet:namespace />btnGuardarOrdenCompra" value="Crear Orden de Compra" />
<% } else { %>
<input type="button" value="Descargar Orden de Compra PDF"
    onclick="window.location.href='/pdfservlet/?accion=ordenCompra&amp;id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';" />
<% } %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
</form>
<script type="text/javascript">
function <portlet:namespace />guardarOrdenCompra() {
    var boton = document.getElementById('<portlet:namespace />btnGuardarOrdenCompra');
    if (!boton || boton.disabled) { return false; }
    if (!confirm('¿Confirma crear la Orden de Compra con estos datos?')) { return false; }
    boton.disabled = true;
    boton.value = 'Guardando...';
    return true;
}
function <portlet:namespace />importeCentavosOrdenCompra(texto) {
    var partes = texto.replace(',', '.').split('.');
    if (!/^\d+([.,]\d{1,2})?$/.test(texto)) { return null; }
    return (partes[0] + ((partes[1] || '') + '00').substring(0, 2)).replace(/^0+/, '') || '0';
}
function <portlet:namespace />sumarCentavosOrdenCompra(a, b) {
    var resultado = '';
    var acarreo = 0;
    var i = a.length - 1;
    var j = b.length - 1;
    while (i >= 0 || j >= 0 || acarreo) {
        var suma = (i >= 0 ? Number(a.charAt(i--)) : 0) + (j >= 0 ? Number(b.charAt(j--)) : 0) + acarreo;
        resultado = String(suma % 10) + resultado;
        acarreo = Math.floor(suma / 10);
    }
    return resultado || '0';
}
function <portlet:namespace />multiplicarCentavosOrdenCompra(valor, cantidad) {
    var resultado = '';
    var acarreo = 0;
    for (var i = valor.length - 1; i >= 0; i--) {
        var producto = Number(valor.charAt(i)) * cantidad + acarreo;
        resultado = String(producto % 10) + resultado;
        acarreo = Math.floor(producto / 10);
    }
    return (String(acarreo || '') + resultado).replace(/^0+/, '') || '0';
}
function <portlet:namespace />mostrarCentavosOrdenCompra(valor) {
    while (valor.length < 3) { valor = '0' + valor; }
    return valor.substring(0, valor.length - 2) + '.' + valor.substring(valor.length - 2);
}
function <portlet:namespace />calcularOrdenCompra() {
    var form = document.getElementById('<portlet:namespace />ordenCompraForm');
    var precios = jQuery(form).find('.orden-compra-precio');
    var subtotales = jQuery(form).find('.orden-compra-subtotal');
    var total = '0';
    var valido = true;
    precios.each(function(i) {
        var precio = <portlet:namespace />importeCentavosOrdenCompra(this.value);
        var cantidad = Number(jQuery(this).attr('data-cantidad'));
        var subtotal = precio !== null ? <portlet:namespace />multiplicarCentavosOrdenCompra(precio, cantidad) : null;
        if (precio === null || precio === '0' || cantidad <= 0) { valido = false; }
        subtotales.eq(i).text(subtotal !== null ? <portlet:namespace />mostrarCentavosOrdenCompra(subtotal) : '');
        if (subtotal !== null) { total = <portlet:namespace />sumarCentavosOrdenCompra(total, subtotal); }
    });
    var impuestos = ['iva', 'iibb'];
    for (var i = 0; i < impuestos.length; i++) {
        var texto = document.getElementById('<portlet:namespace />oc_' + impuestos[i]).value;
        var impuesto = texto === '' ? '0' : <portlet:namespace />importeCentavosOrdenCompra(texto);
        if (impuesto === null) { valido = false; }
        else { total = <portlet:namespace />sumarCentavosOrdenCompra(total, impuesto); }
    }
    document.getElementById('<portlet:namespace />oc_total').innerHTML = valido
            ? <portlet:namespace />mostrarCentavosOrdenCompra(total) : 'Complete los importes';
}
<% if (ordenEditable && !ordenMedica) { %>
<portlet:namespace />calcularOrdenCompra();
<% } %>
</script>
<% } else { %>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
<% } %>

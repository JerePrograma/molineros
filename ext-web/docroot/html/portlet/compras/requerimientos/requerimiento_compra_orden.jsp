<%-- Referencia: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp: cabecera readonly, fieldsets y acciones Guardar/Imprimir en una fila. --%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden" %>
<%@ page import="java.util.Map" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_javascript_helper_componente.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento" %>
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
private String ordenCompraImporte(java.math.BigDecimal valor) {
    if (valor == null) return "";
    java.text.NumberFormat formato = java.text.NumberFormat.getNumberInstance(new java.util.Locale("es", "AR"));
    formato.setMinimumFractionDigits(2);
    formato.setMaximumFractionDigits(2);
    return HtmlUtil.escape(formato.format(valor));
}
%>
<%
RequerimientoCompraOrden ordenCompra = (RequerimientoCompraOrden) renderRequest.getAttribute("ordenCompra");
String ordenError = (String) renderRequest.getAttribute("ordenCompraError");
boolean ordenEditable = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraEditable"));
boolean ordenNueva = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraPuedeCrear"));
boolean ordenIncompleta = Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraRequiereDatos"));
Map<String, String> ordenEntrada = (Map<String, String>) renderRequest.getAttribute("ordenCompraEntrada");
RequerimientoCompraOrdenSeguimiento ordenSeguimiento =
        (RequerimientoCompraOrdenSeguimiento) renderRequest.getAttribute("ordenSeguimiento");
if (ordenSeguimiento == null) ordenSeguimiento = new RequerimientoCompraOrdenSeguimiento();
Map<String, String> ordenSeguimientoEntrada =
        (Map<String, String>) renderRequest.getAttribute("ordenSeguimientoEntrada");
boolean seguimientoPuedeEditar = !ordenNueva
        && Boolean.TRUE.equals(renderRequest.getAttribute("ordenSeguimientoEditable"));
PortletURL ordenGuardarURL = renderResponse.createActionURL();
ordenGuardarURL.setWindowState(WindowState.MAXIMIZED);
ordenGuardarURL.setParameter("struts_action", "/compras/orden_compra");
PortletURL ordenVolverURL = renderResponse.createRenderURL();
ordenVolverURL.setWindowState(WindowState.MAXIMIZED);
ordenVolverURL.setParameter("struts_action", "/compras/ver_requerimiento");
ordenVolverURL.setParameter("id_requerimiento_compra", ParamUtil.getString(renderRequest, "id_requerimiento_compra"));
if (!ordenNueva) {
    ordenVolverURL.setParameter("struts_action", "/compras/view");
    ordenVolverURL.setParameter("tabs1", "orden-de-compra");
}
%>
<portlet:renderURL var="comprasActualizaDomicilioURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/compras/actualiza_domicilio" />
</portlet:renderURL>
<% if (ordenError != null && ordenError.length() > 0) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape(ordenError) %></div>
<% } %>
<% if (ordenCompra != null) {
    RequerimientoCompra ordenRequerimiento = ordenCompra.getRequerimiento();
    if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraSoloConstancia"))) {
        PortletURL ordenConstanciaVolverURL = renderResponse.createRenderURL();
        ordenConstanciaVolverURL.setWindowState(WindowState.MAXIMIZED);
        ordenConstanciaVolverURL.setParameter("struts_action", "/compras/view");
        ordenConstanciaVolverURL.setParameter("tabs1", "requerimientos");
%>
<div class="portlet-msg-success">La Orden de Compra N&deg; <%= ordenCompra.getIdOrdenCompra() %> fue emitida.</div>
<input type="button" value="Volver a Requerimientos" onclick="window.location.href='<%= ordenConstanciaVolverURL.toString() %>';" />
<% } else if (!ordenNueva) { %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_consulta.jsp" %>
<% } else {
    boolean ordenSuministro = false;
%>
<div class="portlet-msg-info">Complete la fecha estimada de entrega confirmada por el proveedor para guardar la Orden de Compra.</div>
<% if (ordenIncompleta) { %>
<div class="portlet-msg-info">Complete tambi&eacute;n los precios unitarios pendientes del requerimiento.</div>
<% } %>
<form action="<%= ordenGuardarURL.toString() %>" method="post" name="<portlet:namespace />ordenCompraCondicionesForm"
      onsubmit="document.getElementById('<portlet:namespace />guardarCondicionesOrdenCompra').disabled = true;">
<input type="hidden" name="<portlet:namespace />cmd" value="create" />
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token" value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_datos.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_seguimiento.jsp" %>
<table class="lfr-table" width="100%"><tr><td>
<input type="button" value="Volver al requerimiento" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
</td><td align="right">
<input type="submit" id="<portlet:namespace />guardarCondicionesOrdenCompra" value="Guardar y emitir Orden de Compra" />
</td></tr></table>
</form>
<% } %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenCompraCreada")) && !ordenIncompleta) { %>
<script type="text/javascript">
window.location.href = '/pdfservlet/?accion=ordenCompra&id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';
</script>
<% } %>
<% } else { %>
<input type="button" value="Volver" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
<% } %>

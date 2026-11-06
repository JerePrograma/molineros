<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSuministro" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%-- Referencia: Liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp:83-110,163,251.
     Consulta de OC y gestion de suministro con el formulario legacy de guardado. --%>
<%
boolean ordenSuministro = ParamUtil.getBoolean(renderRequest, "gestion_suministro", false);
PortletURL ordenConsultaURL = renderResponse.createRenderURL();
ordenConsultaURL.setWindowState(WindowState.MAXIMIZED);
ordenConsultaURL.setParameter("struts_action", "/compras/orden_compra");
ordenConsultaURL.setParameter("id_requerimiento_compra", String.valueOf(ordenCompra.getIdRequerimientoCompra()));
ordenConsultaURL.setParameter("origen_ordenes", "1");
PortletURL ordenSuministroURL = renderResponse.createRenderURL();
ordenSuministroURL.setWindowState(WindowState.MAXIMIZED);
ordenSuministroURL.setParameter("struts_action", "/compras/orden_compra");
ordenSuministroURL.setParameter("id_requerimiento_compra", String.valueOf(ordenCompra.getIdRequerimientoCompra()));
ordenSuministroURL.setParameter("origen_ordenes", "1");
ordenSuministroURL.setParameter("gestion_suministro", "1");
RequerimientoCompraOrdenSuministro ordenGestion =
        (RequerimientoCompraOrdenSuministro) renderRequest.getAttribute("ordenSuministro");
Map<String, String> ordenGestionEntrada =
        (Map<String, String>) renderRequest.getAttribute("ordenSuministroEntrada");
Calendar ordenContacto = Calendar.getInstance();
if (ordenGestion != null && ordenGestion.getFechaContacto() != null) {
    ordenContacto.setTime(ordenGestion.getFechaContacto());
}
int ordenContactoDia = ordenGestionEntrada != null
        ? GetterUtil.getInteger(ordenGestionEntrada.get("contactoDia"), ordenContacto.get(Calendar.DATE))
        : ordenContacto.get(Calendar.DATE);
int ordenContactoMes = ordenGestionEntrada != null
        ? GetterUtil.getInteger(ordenGestionEntrada.get("contactoMes"), ordenContacto.get(Calendar.MONTH))
        : ordenContacto.get(Calendar.MONTH);
int ordenContactoAnio = ordenGestionEntrada != null
        ? GetterUtil.getInteger(ordenGestionEntrada.get("contactoAnio"), ordenContacto.get(Calendar.YEAR))
        : ordenContacto.get(Calendar.YEAR);
String ordenRecepcion = ordenCompraValor(ordenGestionEntrada,
        "suministro_recepcion", ordenGestion != null ? ordenGestion.getRecepcionSuministro() : "");
String ordenSatisfaccion = ordenCompraValor(ordenGestionEntrada,
        "suministro_satisfaccion", ordenGestion != null ? ordenGestion.getSatisfaccionEntrega() : "");
Calendar ordenAlta = Calendar.getInstance();
boolean ordenTieneAlta = ordenCompra.getAltaFecha() != null;
if (ordenTieneAlta) {
    ordenAlta.setTime(ordenCompra.getAltaFecha());
}
%>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_datos.jsp" %>
<% if (!ordenSuministro) { %>
<% if (renderRequest.getAttribute("ordenSeguimientoError") != null) { %><div class="portlet-msg-error"><%= HtmlUtil.escape((String) renderRequest.getAttribute("ordenSeguimientoError")) %></div><% } %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenSeguimientoGuardado"))) { %><div class="portlet-msg-success">Los datos del seguimiento de la orden fueron guardados.</div><% } %>
<% if (seguimientoPuedeEditar) { %>
<form action="<%= ordenGuardarURL.toString() %>" method="post" name="<portlet:namespace />seguimientoOrdenForm"
      onsubmit="document.getElementById('<portlet:namespace />guardarSeguimientoOrden').disabled = true;">
<input type="hidden" name="<portlet:namespace />cmd" value="guardar_seguimiento" />
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />id_orden_compra" value="<%= ordenCompra.getIdOrdenCompra() %>" />
<input type="hidden" name="<portlet:namespace />version_seguimiento" value="<%= ordenCompraValor(ordenSeguimientoEntrada, "version_seguimiento", ordenSeguimiento.getVersionEdicion()) %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token" value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<% } %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_seguimiento.jsp" %>
<table class="lfr-table" width="100%"><tr><td>
<input type="button" value="Gesti&oacute;n de Suministro" onclick="window.location.href='<%= ordenSuministroURL.toString() %>';" />
<input type="button" value="Volver al listado" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
<input type="button" value="Seguimiento y Trazabilidad" onclick="document.getElementById('<portlet:namespace />trazabilidadOrdenCompra').scrollIntoView();" />
</td><td align="right">
<input type="button" value="Imprimir Orden" onclick="window.location.href='/pdfservlet/?accion=ordenCompra&amp;id_requerimiento=<%= ordenCompra.getIdRequerimientoCompra() %>';" />
<% if (seguimientoPuedeEditar) { %><input type="submit" id="<portlet:namespace />guardarSeguimientoOrden" value="Guardar Orden de Compra" /><% } %>
</td></tr></table>
<% if (seguimientoPuedeEditar) { %></form><% } %>
<% } else { %>
<form name="<portlet:namespace />gestionSuministroBoceto" action="<%= ordenGuardarURL.toString() %>" method="post"
      onsubmit="document.getElementById('<portlet:namespace />guardarGestionSuministro').disabled = true;">
<input type="hidden" name="<portlet:namespace />cmd" value="guardar_suministro" />
<input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= ordenCompra.getIdRequerimientoCompra() %>" />
<input type="hidden" name="<portlet:namespace />id_orden_compra" value="<%= ordenCompra.getIdOrdenCompra() %>" />
<input type="hidden" name="<portlet:namespace />gestion_suministro" value="1" />
<input type="hidden" name="<portlet:namespace />version_suministro"
       value="<%= ordenCompraValor(ordenGestionEntrada, "version_suministro", ordenGestion.getVersionEdicion()) %>" />
<input type="hidden" name="<portlet:namespace />compras_save_token"
       value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
<% if (renderRequest.getAttribute("ordenSuministroError") != null) { %>
<div class="portlet-msg-error"><%= HtmlUtil.escape((String) renderRequest.getAttribute("ordenSuministroError")) %></div>
<% } %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("ordenSuministroGuardado"))) { %>
<div class="portlet-msg-success">Los datos de la gesti&oacute;n de suministro fueron guardados.</div>
<% } %>
<fieldset class="block-labels">
<legend>Datos de la gesti&oacute;n</legend>
<table class="lfr-table" width="100%">
<tr>
    <td><label>Fecha de contacto:</label></td>
    <td><liferay-ui:input-date dayParam="contactoDia" dayValue="<%= ordenContactoDia %>"
        monthParam="contactoMes" monthValue="<%= ordenContactoMes %>" yearParam="contactoAnio" yearValue="<%= ordenContactoAnio %>"
        yearRangeStart="<%= ordenAlta.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= ordenAlta.get(Calendar.YEAR) + 20 %>"
        firstDayOfWeek="<%= ordenAlta.getFirstDayOfWeek() %>" /></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td><label for="<portlet:namespace />suministroPersona">Persona contactada:</label></td>
    <td><input type="text" id="<portlet:namespace />suministroPersona" name="<portlet:namespace />suministro_persona" size="50" maxlength="100" value="<%= ordenCompraValor(ordenGestionEntrada, "suministro_persona", ordenGestion.getPersonaContactada()) %>" /></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td><label for="<portlet:namespace />suministroMedio">Medio de contacto:</label></td>
    <td><input type="text" id="<portlet:namespace />suministroMedio" name="<portlet:namespace />suministro_medio" size="50" maxlength="100" value="<%= ordenCompraValor(ordenGestionEntrada, "suministro_medio", ordenGestion.getMedioContacto()) %>" /></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td valign="top"><label for="<portlet:namespace />suministroDetalle">Detalle de la gesti&oacute;n:</label></td>
    <td><textarea rows="5" cols="80" id="<portlet:namespace />suministroDetalle" name="<portlet:namespace />suministro_detalle"><%= ordenCompraValor(ordenGestionEntrada, "suministro_detalle", ordenGestion.getDetalle()) %></textarea></td></tr>
</table>
</fieldset>
<fieldset class="block-labels">
<legend>Preguntas de seguimiento</legend>
<table class="lfr-table" width="100%">
<tr>
    <td><label for="<portlet:namespace />suministroRecepcion">&iquest;Recibi&oacute; correctamente el suministro adquirido por OSPIM para usted?</label></td>
    <td><select id="<portlet:namespace />suministroRecepcion" name="<portlet:namespace />suministro_recepcion">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRecepcion) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="PARCIALMENTE"<%= "PARCIALMENTE".equals(ordenRecepcion) ? " selected=\"selected\"" : "" %>>Parcialmente</option>
        <option value="NO"<%= "NO".equals(ordenRecepcion) ? " selected=\"selected\"" : "" %>>No</option>
    </select></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td><label for="<portlet:namespace />suministroSatisfaccion">&iquest;Qu&eacute; tan satisfecho/a est&aacute; con la entrega del suministro?</label></td>
    <td><select id="<portlet:namespace />suministroSatisfaccion" name="<portlet:namespace />suministro_satisfaccion">
        <option value=""></option>
        <option value="MUY_SATISFECHO"<%= "MUY_SATISFECHO".equals(ordenSatisfaccion) ? " selected=\"selected\"" : "" %>>Muy satisfecho/a</option>
        <option value="SATISFECHO"<%= "SATISFECHO".equals(ordenSatisfaccion) ? " selected=\"selected\"" : "" %>>Satisfecho/a</option>
        <option value="NEUTRAL"<%= "NEUTRAL".equals(ordenSatisfaccion) ? " selected=\"selected\"" : "" %>>Ni satisfecho/a ni insatisfecho/a</option>
        <option value="INSATISFECHO"<%= "INSATISFECHO".equals(ordenSatisfaccion) ? " selected=\"selected\"" : "" %>>Insatisfecho/a</option>
        <option value="MUY_INSATISFECHO"<%= "MUY_INSATISFECHO".equals(ordenSatisfaccion) ? " selected=\"selected\"" : "" %>>Muy insatisfecho/a</option>
    </select></td>
</tr>
</table>
</fieldset>
<table class="lfr-table"><tr><td align="left">
<input type="submit" id="<portlet:namespace />guardarGestionSuministro" value="Guardar" />&nbsp;&nbsp;
<input type="button" value="Volver a la OC" onclick="window.location.href='<%= ordenConsultaURL.toString() %>';" />
</td></tr></table>
</form>
<% } %>

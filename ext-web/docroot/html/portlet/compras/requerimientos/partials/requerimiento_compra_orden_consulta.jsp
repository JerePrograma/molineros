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
String[] ordenRespuestas = ordenGestion != null
        ? new String[] { ordenGestion.getRespuesta1(), ordenGestion.getRespuesta2(),
            ordenGestion.getRespuesta3(), ordenGestion.getRespuesta4(), ordenGestion.getRespuesta5() }
        : new String[] { "", "", "", "", "" };
for (int preguntaSuministro = 0; preguntaSuministro < ordenRespuestas.length; preguntaSuministro++) {
    ordenRespuestas[preguntaSuministro] = ordenCompraValor(ordenGestionEntrada,
            "suministro_pregunta_" + (preguntaSuministro + 1), ordenRespuestas[preguntaSuministro]);
}
Calendar ordenAlta = Calendar.getInstance();
boolean ordenTieneAlta = ordenCompra.getAltaFecha() != null;
if (ordenTieneAlta) {
    ordenAlta.setTime(ordenCompra.getAltaFecha());
}
%>
<% if (ordenSuministro) { %>
<% } %>
<fieldset class="block-labels">
<legend><%= ordenSuministro ? "Gesti&oacute;n de Suministro" : "Orden de Compra" %></legend>
<table class="lfr-table" width="100%">
<tr>
    <td><label>N&uacute;mero OC:</label></td>
    <td><input type="text" size="15" readonly="readonly" value="<%= ordenCompra.getIdOrdenCompra() %>" /></td>
    <td><label>Fecha Alta OC:</label></td>
    <td>
        <liferay-ui:input-date dayParam="altaOCDia" dayValue="<%= ordenTieneAlta ? ordenAlta.get(Calendar.DATE) : 0 %>"
            monthParam="altaOCMes" monthValue="<%= ordenTieneAlta ? ordenAlta.get(Calendar.MONTH) : -1 %>"
            yearParam="altaOCAnio" yearValue="<%= ordenTieneAlta ? ordenAlta.get(Calendar.YEAR) : 0 %>"
            yearRangeStart="<%= ordenAlta.get(Calendar.YEAR) - 20 %>" yearRangeEnd="<%= ordenAlta.get(Calendar.YEAR) + 20 %>"
            firstDayOfWeek="<%= ordenAlta.getFirstDayOfWeek() %>" disabled="<%= true %>" />
    </td>
</tr>
<% if (!ordenSuministro) { %>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td><label>Sector:</label></td>
    <td colspan="3"><input type="text" size="50" readonly="readonly" value="<%= ordenCompraTexto(ordenRequerimiento.getSectorDescripcionVisible()) %>" /></td>
</tr>
<% } %>
</table>
</fieldset>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_afiliado.jsp" %>
<% if (!ordenSuministro) { %>
<fieldset class="block-labels">
<legend>Detalle de la compra</legend>
<table class="lfr-table taglib-search-iterator" width="100%">
<thead><tr class="portlet-section-header results-header"><th width="10%">Cantidad</th><th>Art&iacute;culo / prestaci&oacute;n</th></tr></thead>
<tbody>
<% int ordenFila = 0;
   for (RequerimientoCompraDetalle detalleConsultaOrden : ordenCompra.getDetalles()) { %>
<tr class="results-row<%= ordenFila++ % 2 == 0 ? "" : " alt" %>">
    <td align="center"><%= detalleConsultaOrden.getCantidad() %></td>
    <td><%= ordenCompraTexto(detalleConsultaOrden.getDescripcionItemVisible()) %></td>
</tr>
<% } %>
</tbody>
</table>
</fieldset>
<table class="lfr-table"><tr><td align="left">
<input type="button" value="Gesti&oacute;n de Suministro" onclick="window.location.href='<%= ordenSuministroURL.toString() %>';" />&nbsp;&nbsp;
<input type="button" value="Volver al listado" onclick="window.location.href='<%= ordenVolverURL.toString() %>';" />
</td></tr></table>
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
<legend>Preguntas de seguimiento (provisorias)</legend>
<table class="lfr-table" width="100%">
<tr>
    <td><label for="<portlet:namespace />suministroPregunta1">&iquest;Lo recibi&oacute; en tiempo y forma?</label></td>
    <td><select id="<portlet:namespace />suministroPregunta1" name="<portlet:namespace />suministro_pregunta_1">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRespuestas[0]) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="NO"<%= "NO".equals(ordenRespuestas[0]) ? " selected=\"selected\"" : "" %>>No</option>
        <option value="NO_CORRESPONDE"<%= "NO_CORRESPONDE".equals(ordenRespuestas[0]) ? " selected=\"selected\"" : "" %>>No corresponde</option>
    </select></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td><label for="<portlet:namespace />suministroPregunta2">&iquest;Recibi&oacute; todos los art&iacute;culos solicitados?</label></td>
    <td><select id="<portlet:namespace />suministroPregunta2" name="<portlet:namespace />suministro_pregunta_2">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRespuestas[1]) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="NO"<%= "NO".equals(ordenRespuestas[1]) ? " selected=\"selected\"" : "" %>>No</option>
        <option value="NO_CORRESPONDE"<%= "NO_CORRESPONDE".equals(ordenRespuestas[1]) ? " selected=\"selected\"" : "" %>>No corresponde</option>
    </select></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td><label for="<portlet:namespace />suministroPregunta3">&iquest;Los art&iacute;culos recibidos coinciden con lo indicado?</label></td>
    <td><select id="<portlet:namespace />suministroPregunta3" name="<portlet:namespace />suministro_pregunta_3">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRespuestas[2]) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="NO"<%= "NO".equals(ordenRespuestas[2]) ? " selected=\"selected\"" : "" %>>No</option>
        <option value="NO_CORRESPONDE"<%= "NO_CORRESPONDE".equals(ordenRespuestas[2]) ? " selected=\"selected\"" : "" %>>No corresponde</option>
    </select></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td><label for="<portlet:namespace />suministroPregunta4">&iquest;Los art&iacute;culos se recibieron en buen estado?</label></td>
    <td><select id="<portlet:namespace />suministroPregunta4" name="<portlet:namespace />suministro_pregunta_4">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRespuestas[3]) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="NO"<%= "NO".equals(ordenRespuestas[3]) ? " selected=\"selected\"" : "" %>>No</option>
        <option value="NO_CORRESPONDE"<%= "NO_CORRESPONDE".equals(ordenRespuestas[3]) ? " selected=\"selected\"" : "" %>>No corresponde</option>
    </select></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td><label for="<portlet:namespace />suministroPregunta5">&iquest;Necesita asistencia o seguimiento adicional?</label></td>
    <td><select id="<portlet:namespace />suministroPregunta5" name="<portlet:namespace />suministro_pregunta_5">
        <option value=""></option>
        <option value="SI"<%= "SI".equals(ordenRespuestas[4]) ? " selected=\"selected\"" : "" %>>S&iacute;</option>
        <option value="NO"<%= "NO".equals(ordenRespuestas[4]) ? " selected=\"selected\"" : "" %>>No</option>
        <option value="NO_CORRESPONDE"<%= "NO_CORRESPONDE".equals(ordenRespuestas[4]) ? " selected=\"selected\"" : "" %>>No corresponde</option>
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

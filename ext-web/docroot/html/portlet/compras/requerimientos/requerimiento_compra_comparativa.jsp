<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle" %>
<%@ page import="ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper" %>
<%!
private String valorComparativa(Map<String, String> entrada, String clave, Object valor) {
    return HtmlUtil.escape(entrada != null && entrada.containsKey(clave)
            ? entrada.get(clave) : RequerimientoCompraComparativaHelper.texto(valor));
}
%>
<%
RequerimientoCompra comparativaReq = (RequerimientoCompra) renderRequest.getAttribute("comparativaRequerimiento");
List<RequerimientoCompraComparativa> comparativas =
        (List<RequerimientoCompraComparativa>) renderRequest.getAttribute("comparativas");
Map<String, String> comparativaEntrada =
        (Map<String, String>) renderRequest.getAttribute("comparativaEntrada");
boolean comparativaEditable = Boolean.TRUE.equals(renderRequest.getAttribute("comparativaEditable"));
boolean comparativaExiste = Boolean.TRUE.equals(renderRequest.getAttribute("comparativaExiste"));
String comparativaError = (String) renderRequest.getAttribute("comparativaError");
PortletURL comparativaGuardarURL = renderResponse.createActionURL();
comparativaGuardarURL.setWindowState(LiferayWindowState.EXCLUSIVE);
comparativaGuardarURL.setParameter("struts_action", "/compras/comparativa");
PortletURL comparativaEditarURL = renderResponse.createRenderURL();
comparativaEditarURL.setWindowState(LiferayWindowState.EXCLUSIVE);
comparativaEditarURL.setParameter("struts_action", "/compras/comparativa");
comparativaEditarURL.setParameter("id_requerimiento_compra",
        comparativaReq == null ? "0" : String.valueOf(comparativaReq.getIdRequerimientoCompra()));
comparativaEditarURL.setParameter("editar", "true");
%>
<div id="<portlet:namespace />comparativaContenido">
<% if (comparativaError != null) { %>
    <div class="portlet-msg-error"><%= HtmlUtil.escape(comparativaError) %></div>
<% } %>
<% if (Boolean.TRUE.equals(renderRequest.getAttribute("comparativaGuardada"))) { %>
    <div class="portlet-msg-success">Comparativa guardada.</div>
<% } %>
<% if (comparativaReq != null && comparativas != null) { %>
    <h3>Comparativa del requerimiento <%= comparativaReq.getIdRequerimientoCompra() %></h3>
    <% if (comparativaEditable) { %>
        <p>Ingrese cantidades e importes netos sin separador de miles. IVA e IIBB son importes adicionales del presupuesto completo.</p>
    <% } %>
    <form id="<portlet:namespace />comparativaForm" method="post"
          action="<%= comparativaGuardarURL.toString() %>"
          onsubmit="return <portlet:namespace />guardarComparativa(this);">
        <input type="hidden" name="<portlet:namespace />id_requerimiento_compra"
               value="<%= comparativaReq.getIdRequerimientoCompra() %>" />
        <input type="hidden" name="<portlet:namespace />cmd" value="guardar" />
        <div style="max-height:550px;overflow:auto;">
        <% for (RequerimientoCompraComparativa comparativa : comparativas) {
            if (!comparativaEditable && comparativa.getIdComparativa() == 0) {
                continue;
            }
            String sufijoComparativa = "_" + comparativa.getIdPrestador();
        %>
        <fieldset class="block-labels">
            <legend><%= HtmlUtil.escape(comparativa.getPrestador()) %>
                <% if (comparativa.getIdPrestador() == comparativaReq.getIdPrestadorAdjudicadoInt()) { %>
                    (Adjudicado)
                <% } %>
            </legend>
            <input type="hidden" name="<portlet:namespace />prestador<%= sufijoComparativa %>"
                   value="<%= comparativa.getIdPrestador() %>" />
            <table class="lfr-table">
                <tr>
                    <td><label for="<portlet:namespace />fecha<%= sufijoComparativa %>">Fecha presupuesto (dd/mm/aaaa)</label>
                        <% if (comparativaEditable) { %>
                        <input type="text" id="<portlet:namespace />fecha<%= sufijoComparativa %>"
                               name="<portlet:namespace />fecha<%= sufijoComparativa %>" size="12" maxlength="10"
                               value="<%= valorComparativa(comparativaEntrada, "fecha" + sufijoComparativa, comparativa.getFechaPresupuesto()) %>" />
                        <% } else { %><%= valorComparativa(null, "", comparativa.getFechaPresupuesto()) %><% } %>
                    </td>
                    <td><label for="<portlet:namespace />pago<%= sufijoComparativa %>">Forma de pago</label>
                        <% if (comparativaEditable) { %>
                        <select id="<portlet:namespace />pago<%= sufijoComparativa %>" name="<portlet:namespace />pago<%= sufijoComparativa %>">
                            <% for (String opcion : new String[] {"30", "45", "60"}) { %>
                            <option value="<%= opcion %>" <%= opcion.equals(valorComparativa(comparativaEntrada, "pago" + sufijoComparativa, comparativa.getFormaPago())) ? "selected=\"selected\"" : "" %>><%= opcion %> días</option>
                            <% } %>
                        </select>
                        <% } else { %><%= valorComparativa(null, "", comparativa.getFormaPago()) %> días<% } %>
                    </td>
                    <td><label for="<portlet:namespace />plazo<%= sufijoComparativa %>">Plazo de entrega</label>
                        <% if (comparativaEditable) { %>
                        <select id="<portlet:namespace />plazo<%= sufijoComparativa %>" name="<portlet:namespace />plazo<%= sufijoComparativa %>">
                            <option value=""></option>
                            <% for (String opcion : new String[] {"24/48", "48/72"}) { %>
                            <option value="<%= opcion %>" <%= opcion.equals(valorComparativa(comparativaEntrada, "plazo" + sufijoComparativa, comparativa.getPlazoEntrega())) ? "selected=\"selected\"" : "" %>><%= opcion %></option>
                            <% } %>
                        </select>
                        <% } else { %><%= valorComparativa(null, "", comparativa.getPlazoEntrega()) %><% } %>
                    </td>
                    <td><label for="<portlet:namespace />validez<%= sufijoComparativa %>">Validez (horas)</label>
                        <% if (comparativaEditable) { %>
                        <select id="<portlet:namespace />validez<%= sufijoComparativa %>" name="<portlet:namespace />validez<%= sufijoComparativa %>">
                            <option value=""></option>
                            <% for (String opcion : new String[] {"24", "48"}) { %>
                            <option value="<%= opcion %>" <%= opcion.equals(valorComparativa(comparativaEntrada, "validez" + sufijoComparativa, comparativa.getValidezPresupuesto())) ? "selected=\"selected\"" : "" %>><%= opcion %></option>
                            <% } %>
                        </select>
                        <% } else { %><%= valorComparativa(null, "", comparativa.getValidezPresupuesto()) %><% } %>
                    </td>
                </tr>
                <tr>
                    <td><label for="<portlet:namespace />envio<%= sufijoComparativa %>">Envío</label>
                        <% if (comparativaEditable) { %>
                        <select id="<portlet:namespace />envio<%= sufijoComparativa %>" name="<portlet:namespace />envio<%= sufijoComparativa %>">
                            <option value=""></option>
                            <% for (String opcion : new String[] {"Obra Social", "Beneficiario", "Delegación"}) { %>
                            <option value="<%= HtmlUtil.escape(opcion) %>" <%= HtmlUtil.escape(opcion).equals(valorComparativa(comparativaEntrada, "envio" + sufijoComparativa, comparativa.getEnvio())) ? "selected=\"selected\"" : "" %>><%= HtmlUtil.escape(opcion) %></option>
                            <% } %>
                        </select>
                        <% } else { %><%= valorComparativa(null, "", comparativa.getEnvio()) %><% } %>
                    </td>
                    <td><label for="<portlet:namespace />iva<%= sufijoComparativa %>">IVA (importe)</label>
                        <% if (comparativaEditable) { %>
                        <input type="text" id="<portlet:namespace />iva<%= sufijoComparativa %>" name="<portlet:namespace />iva<%= sufijoComparativa %>"
                               size="16" maxlength="20" value="<%= valorComparativa(comparativaEntrada, "iva" + sufijoComparativa, comparativa.getIva()) %>" />
                        <% } else { %><%= valorComparativa(null, "", comparativa.getIva()) %><% } %>
                    </td>
                    <td><label for="<portlet:namespace />iibb<%= sufijoComparativa %>">IIBB (importe)</label>
                        <% if (comparativaEditable) { %>
                        <input type="text" id="<portlet:namespace />iibb<%= sufijoComparativa %>" name="<portlet:namespace />iibb<%= sufijoComparativa %>"
                               size="16" maxlength="20" value="<%= valorComparativa(comparativaEntrada, "iibb" + sufijoComparativa, comparativa.getIibb()) %>" />
                        <% } else { %><%= valorComparativa(null, "", comparativa.getIibb()) %><% } %>
                    </td>
                </tr>
            </table>
            <table class="lfr-table" width="100%">
                <thead><tr><th>Código</th><th>Prestación</th><th>Cantidad cotizada</th><th>Importe unitario neto</th>
                    <% if (!comparativaEditable) { %><th>Subtotal</th><% } %>
                </tr></thead>
                <tbody>
                <% for (RequerimientoCompraComparativaDetalle detalleComparativa : comparativa.getDetalles()) {
                    if (!comparativaEditable && detalleComparativa.getIdDetalle() == 0) {
                        continue;
                    }
                    String claveComparativa = sufijoComparativa + "_" + detalleComparativa.getIdPrestacion();
                %>
                    <tr>
                        <td><%= HtmlUtil.escape(detalleComparativa.getCodigo()) %></td>
                        <td><%= HtmlUtil.escape(detalleComparativa.getPrestacion()) %></td>
                        <td>
                            <% if (comparativaEditable) { %>
                            <input type="text" name="<portlet:namespace />cantidad<%= claveComparativa %>" size="9" maxlength="11"
                                   title="Cantidad cotizada para <%= HtmlUtil.escape(detalleComparativa.getCodigo()) %>"
                                   value="<%= valorComparativa(comparativaEntrada, "cantidad" + claveComparativa, detalleComparativa.getCantidad()) %>" />
                            <% } else { %><%= valorComparativa(null, "", detalleComparativa.getCantidad()) %><% } %>
                        </td>
                        <td>
                            <% if (comparativaEditable) { %>
                            <input type="text" name="<portlet:namespace />importe<%= claveComparativa %>" size="16" maxlength="20"
                                   title="Importe unitario para <%= HtmlUtil.escape(detalleComparativa.getCodigo()) %>"
                                   value="<%= valorComparativa(comparativaEntrada, "importe" + claveComparativa, detalleComparativa.getImporteUnitario()) %>" />
                            <% } else { %><%= valorComparativa(null, "", detalleComparativa.getImporteUnitario()) %><% } %>
                        </td>
                        <% if (!comparativaEditable) { %><td><%= valorComparativa(null, "", detalleComparativa.getSubtotal()) %></td><% } %>
                    </tr>
                <% } %>
                </tbody>
            </table>
            <% if (!comparativaEditable) { %>
                <p><strong>Neto:</strong> <%= valorComparativa(null, "", comparativa.getNeto()) %>
                   &nbsp; <strong><%= comparativa.getIncompleto() ? "Total parcial" : "Total" %>:</strong>
                   <%= valorComparativa(null, "", comparativa.getTotal()) %></p>
                <% if (comparativa.getIncompleto()) { %>
                <p>El total incluye únicamente las prestaciones con cantidad e importe cargados.</p>
                <% } %>
            <% } %>
        </fieldset>
        <% } %>
        </div>
        <% if (comparativaEditable) { %>
            <input type="submit" id="<portlet:namespace />guardarComparativaBoton" value="Guardar comparativa" />
        <% } else if (Boolean.TRUE.equals(renderRequest.getAttribute("comparativaPuedeEditar"))) { %>
            <input type="button" value="Modificar comparativa"
                   onclick="<portlet:namespace />cargarComparativa('<%= HtmlUtil.escape(comparativaEditarURL.toString()) %>');" />
        <% } %>
        <% if (comparativaExiste && !comparativaEditable) { %>
            <input type="button" value="Imprimir PDF"
                   onclick="window.open('/pdfservlet/?accion=comparativaCompra&amp;id_requerimiento=<%= comparativaReq.getIdRequerimientoCompra() %>');" />
        <% } %>
    </form>
<% } %>
<script type="text/javascript">
function <portlet:namespace />cargarComparativa(url) {
    jQuery.ajax({
        url: url,
        success: function(html) { jQuery('#<portlet:namespace />comparativaContenido').replaceWith(html); },
        error: function() { alert('No se pudo consultar la comparativa.'); }
    });
}
function <portlet:namespace />guardarComparativa(form) {
    var boton = jQuery('#<portlet:namespace />guardarComparativaBoton');
    if (boton.attr('disabled')) { return false; }
    boton.attr('disabled', 'disabled');
    jQuery.ajax({
        type: 'POST',
        url: form.action,
        data: jQuery(form).serialize(),
        success: function(html) { jQuery('#<portlet:namespace />comparativaContenido').replaceWith(html); },
        error: function() {
            boton.removeAttr('disabled');
            alert('No se pudo confirmar el guardado. Vuelva a consultar la comparativa.');
        }
    });
    return false;
}
</script>
</div>

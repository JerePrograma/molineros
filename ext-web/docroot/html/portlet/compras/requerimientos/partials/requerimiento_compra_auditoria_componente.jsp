<%--
Responsabilidad:
    Muestra al pie la auditoria persistida del requerimiento de compra.
Incluido desde:
    requerimiento_compra_edicion_ensamblado.jsp, requerimiento_compra_consulta_ensamblado.jsp
Referencia legacy:
    /html/portlet/prestadores/view_prestador.jsp, bloque prestador_auditoria.
Pantallas o estados de uso:
    Edicion y consulta de requerimientos existentes; no muestra datos en alta.
Entradas requeridas:
    El bean publicado por requerimiento_compra_contexto_publicacion_componente.jsp.
Atributos de request consumidos:
    compras.requerimiento.req
Parametros consumidos:
    Ninguno.
IDs o funciones JavaScript expuestos:
    compras_auditoria, con namespace del portlet.
Efectos secundarios:
    Solo presenta datos del bean; no consulta ni modifica persistencia o sesion.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>

<%
RequerimientoCompra requerimientoAuditoria =
        (RequerimientoCompra) request.getAttribute("compras.requerimiento.req");

if (requerimientoAuditoria != null
        && requerimientoAuditoria.getIdRequerimientoCompra() > 0) {
%>
<div id="<portlet:namespace />compras_auditoria" class="compras-seccion" align="center">
    <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
        <tr>
            <td><hr /></td>
        </tr>
        <tr>
            <td>
                <table style="font-size: 8pt;">
                    <tr>
                        <td><label>Alta Usuario:</label></td>
                        <td>
                            <%= HtmlUtil.escape(requerimientoAuditoria.getAltaUsr() != null
                                    ? requerimientoAuditoria.getAltaUsr() : "") %>
                        </td>
                        <td><label>Alta Fecha:</label></td>
                        <td>
                            <%= DateUtils.format(requerimientoAuditoria.getAltaFecha(), DateUtils.LONG) %>
                        </td>
                        <td>|</td>
                        <td><label>Modi Usuario:</label></td>
                        <td>
                            <%= HtmlUtil.escape(requerimientoAuditoria.getModiUsr() != null
                                    ? requerimientoAuditoria.getModiUsr() : "") %>
                        </td>
                        <td><label>Modi Fecha:</label></td>
                        <td>
                            <%= DateUtils.format(requerimientoAuditoria.getModiFecha(), DateUtils.LONG) %>
                        </td>
                        <td>|</td>
                        <td><label>Baja Usuario:</label></td>
                        <td>
                            <%= HtmlUtil.escape(requerimientoAuditoria.getBajaUsr() != null
                                    ? requerimientoAuditoria.getBajaUsr() : "") %>
                        </td>
                        <td><label>Baja Fecha:</label></td>
                        <td>
                            <%= DateUtils.format(requerimientoAuditoria.getBajaFecha(), DateUtils.LONG) %>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>
<% } %>

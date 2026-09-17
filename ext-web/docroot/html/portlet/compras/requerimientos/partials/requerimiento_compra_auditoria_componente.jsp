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

SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

if (requerimientoAuditoria != null
        && requerimientoAuditoria.getIdRequerimientoCompra() > 0) {
%>

    <div align="center">
        <table class="lfr-table"
               style="border-collapse: separate; border-spacing: 5px;">

            <tr>
                <td colspan="12"><hr /></td>
            </tr>

            <tr>
                <td colspan="12">
                    <div align="center"
                         id="<portlet:namespace />compras_auditoria">

                        <table style="font-size:8;">
                            <tr>

                                <td>
                                    <label>Alta Usuario:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getAltaUsr() != null
                                        ? requerimientoAuditoria.getAltaUsr()
                                        : "" %>
                                </td>

                                <td>
                                    <label>Alta Fecha:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getAltaFecha() != null
                                        ? sdf2.format(requerimientoAuditoria.getAltaFecha())
                                        : "" %>
                                </td>

                                <td>
                                |
                                </td>

                                <td>
                                    <label>Modi Usuario:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getModiUsr() != null
                                        ? requerimientoAuditoria.getModiUsr()
                                        : "" %>
                                </td>

                                <td>
                                    <label>Modi Fecha:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getModiFecha() != null
                                        ? sdf2.format(requerimientoAuditoria.getModiFecha())
                                        : "" %>
                                </td>

                                <td>
                                |
                                </td>

                                <td>
                                    <label>Baja Usuario:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getBajaUsr() != null
                                        ? requerimientoAuditoria.getBajaUsr()
                                        : "" %>
                                </td>

                                <td>
                                    <label>Baja Fecha:</label>
                                </td>
                                <td>
                                    <%= requerimientoAuditoria.getBajaFecha() != null
                                        ? sdf2.format(requerimientoAuditoria.getBajaFecha())
                                        : "" %>
                                </td>

                            </tr>
                        </table>

                    </div>
                </td>
            </tr>

        </table>
    </div>

<% } %>

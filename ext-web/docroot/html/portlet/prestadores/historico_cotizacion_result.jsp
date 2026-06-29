<%@ include file="/html/portlet/prestadores/init.jsp" %>

<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%@ page import="ar.com.ospim.prestadores.beans.HistoricoPrestadorCotizacion" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%
List<HistoricoPrestadorCotizacion> historico =
        (List<HistoricoPrestadorCotizacion>)
                request.getAttribute(
                        "historicoPrestadorCotizacion"
                );

if (historico == null) {
    historico =
            Collections.emptyList();
}

SimpleDateFormat formatoFecha =
        new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss"
        );
%>

<% if (historico.isEmpty()) { %>

    <div class="portlet-msg-info">
        El prestador no posee movimientos registrados.
    </div>

<% } else { %>

    <table
        class="taglib-search-iterator"
        width="100%"
        cellspacing="0"
        cellpadding="4">

        <thead>
            <tr class="portlet-section-header">
                <th>Fecha</th>
                <th>Usuario</th>
                <th>Acción</th>
            </tr>
        </thead>

        <tbody>

        <% for (
                HistoricoPrestadorCotizacion movimiento :
                historico
        ) { %>

            <tr class="portlet-section-body">

                <td>
                    <%= movimiento.getFecha() != null
                            ? formatoFecha.format(
                                    movimiento.getFecha()
                              )
                            : "" %>
                </td>

                <td>
                    <%= HtmlUtil.escape(
                            movimiento.getUsuario()
                    ) %>
                </td>

                <td>
                    <strong>
                        <%= movimiento.isEstadoACotizar()
                                ? "HABILITÓ"
                                : "DESHABILITÓ" %>
                    </strong>
                </td>

            </tr>

        <% } %>

        </tbody>
    </table>

<% } %>
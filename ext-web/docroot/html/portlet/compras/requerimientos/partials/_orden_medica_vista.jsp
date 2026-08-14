<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.portlet.PortletURL" %>

<%
List<RequerimientoCompraPresupuesto> ordenesMedicasCompraVista =
        new ArrayList<RequerimientoCompraPresupuesto>();

String errorOrdenMedicaCompraVista =
        null;

try {
    List<RequerimientoCompraPresupuesto> ordenesRecuperadas =
            BusquedaRequerimientoCompraServiceUtil
                    .listarOrdenesMedicas(
                            req.getIdRequerimientoCompra()
                    );

    if (ordenesRecuperadas != null) {
        ordenesMedicasCompraVista =
                ordenesRecuperadas;
    }

} catch (Exception errorOrdenMedica) {
    errorOrdenMedicaCompraVista =
            "No se pudieron recuperar las Órdenes médicas "
                    + "del requerimiento.";
}

SimpleDateFormat formatoFechaOrdenMedicaCompraVista =
        new SimpleDateFormat(
                "dd/MM/yyyy"
        );
%>

<div class="compras-seccion compras-seccion-orden-medica compras-seccion-adjuntos">
    <fieldset class="block-labels">
        <legend>Orden médica</legend>

        <% if (errorOrdenMedicaCompraVista != null) { %>

            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(
                        errorOrdenMedicaCompraVista
                ) %>
            </div>

        <% } else if (ordenesMedicasCompraVista.isEmpty()) { %>

            <div class="portlet-msg-info">
                Este requerimiento histórico no posee Orden médica asociada.
            </div>

        <% } else { %>

            <table class="lfr-table compras-resumen-requerimiento">
                <thead>
                    <tr>
                        <th>Documento</th>
                        <th>Fecha de la orden médica</th>
                        <th>Nombre original</th>
                        <th>Acción</th>
                    </tr>
                </thead>

                <tbody>

                    <%
                    for (int i = 0;
                            i < ordenesMedicasCompraVista.size();
                            i++) {

                        RequerimientoCompraPresupuesto ordenMedicaCompraVista =
                                ordenesMedicasCompraVista.get(i);

                        if (ordenMedicaCompraVista == null) {
                            continue;
                        }

                        String fechaOrdenMedicaCompraVista =
                                "";

                        if (ordenMedicaCompraVista
                                .getFechaDocumento() != null) {

                            fechaOrdenMedicaCompraVista =
                                    formatoFechaOrdenMedicaCompraVista
                                            .format(
                                                    ordenMedicaCompraVista
                                                            .getFechaDocumento()
                                            );
                        }

                        String urlOrdenMedicaCompraVista =
                                "";

                        Long dlFileEntryIdOrdenMedica =
                                ordenMedicaCompraVista
                                        .getDlFileEntryId();

                        if (dlFileEntryIdOrdenMedica != null
                                && dlFileEntryIdOrdenMedica.longValue() > 0L) {

                            PortletURL descargarOrdenMedicaCompraURL =
                                    renderResponse.createActionURL();

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "struts_action",
                                    "/compras/descargar_orden_medica"
                            );

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "id_requerimiento_compra",
                                    String.valueOf(
                                            req.getIdRequerimientoCompra()
                                    )
                            );

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "dl_file_entry_id",
                                    String.valueOf(
                                            dlFileEntryIdOrdenMedica
                                                    .longValue()
                                    )
                            );

                            urlOrdenMedicaCompraVista =
                                    descargarOrdenMedicaCompraURL
                                            .toString();
                        }
                    %>

                        <tr>
                            <td>
                                Orden médica
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        fechaOrdenMedicaCompraVista
                                ) %>
                            </td>

                            <td>
                                <% if (WebKeysCompras.isEmpty(
                                        ordenMedicaCompraVista
                                                .getNumeroReceta()
                                )) { %>

                                    No informado

                                <% } else { %>

                                    <%= HtmlUtil.escape(
                                            ordenMedicaCompraVista
                                                    .getNumeroReceta()
                                    ) %>

                                <% } %>
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        ordenMedicaCompraVista
                                                .getNombreOriginal()
                                ) %>
                            </td>

                            <td>
                                <% if (!WebKeysCompras.isEmpty(
                                        urlOrdenMedicaCompraVista
                                )) { %>

                                    <a href="<%= HtmlUtil.escape(
                                            urlOrdenMedicaCompraVista
                                    ) %>">
                                        Ver / descargar
                                    </a>

                                <% } else { %>

                                    No disponible

                                <% } %>
                            </td>
                        </tr>

                    <%
                    }
                    %>

                </tbody>
            </table>

        <% } %>
    </fieldset>
</div>
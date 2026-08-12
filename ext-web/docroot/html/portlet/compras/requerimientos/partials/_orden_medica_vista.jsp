<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="javax.portlet.PortletURL" %>
<%
RequerimientoCompraPresupuesto ordenMedicaCompraVista = null;
String errorOrdenMedicaCompraVista = null;

try {
    ordenMedicaCompraVista =
            BusquedaRequerimientoCompraServiceUtil.getOrdenMedica(
                    req.getIdRequerimientoCompra()
            );
} catch (Exception errorOrdenMedica) {
    errorOrdenMedicaCompraVista =
            "No se pudo recuperar la Orden médica del requerimiento.";
}

String fechaOrdenMedicaCompraVista = "";
String urlOrdenMedicaCompraVista = "";

if (ordenMedicaCompraVista != null) {
    if (ordenMedicaCompraVista.getFechaDocumento() != null) {
        fechaOrdenMedicaCompraVista = new SimpleDateFormat(
                "dd/MM/yyyy"
        ).format(ordenMedicaCompraVista.getFechaDocumento());
    }

    PortletURL descargarOrdenMedicaCompraURL =
            renderResponse.createActionURL();
    descargarOrdenMedicaCompraURL.setParameter(
            "struts_action",
            "/compras/descargar_orden_medica"
    );
    descargarOrdenMedicaCompraURL.setParameter(
            "id_requerimiento_compra",
            String.valueOf(req.getIdRequerimientoCompra())
    );
    urlOrdenMedicaCompraVista =
            descargarOrdenMedicaCompraURL.toString();
}
%>

<div class="compras-seccion compras-seccion-orden-medica compras-seccion-adjuntos">
    <fieldset class="block-labels">
        <legend>Orden médica</legend>

        <% if (errorOrdenMedicaCompraVista != null) { %>
            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(errorOrdenMedicaCompraVista) %>
            </div>
        <% } else if (ordenMedicaCompraVista == null) { %>
            <div class="portlet-msg-info">
                Este requerimiento histórico no posee Orden médica asociada.
            </div>
        <% } else { %>
            <table class="lfr-table compras-resumen-requerimiento">
                <tr>
                    <th>Documento</th>
                    <th>Fecha de la orden médica</th>
                    <th>Nombre original</th>
                    <th>Acción</th>
                </tr>
                <tr>
                    <td>Orden médica</td>
                    <td><%= HtmlUtil.escape(fechaOrdenMedicaCompraVista) %></td>
                    <td><%= HtmlUtil.escape(
                            ordenMedicaCompraVista.getNombreOriginal()
                    ) %></td>
                    <td>
                        <a href="<%= HtmlUtil.escape(
                                urlOrdenMedicaCompraVista
                        ) %>">
                            Ver / descargar
                        </a>
                    </td>
                </tr>
            </table>
        <% } %>
    </fieldset>
</div>

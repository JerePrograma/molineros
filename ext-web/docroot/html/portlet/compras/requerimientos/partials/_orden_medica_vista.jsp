<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%
List<RequerimientoCompraPresupuesto> ordenesMedicasCompraVista =
        (List<RequerimientoCompraPresupuesto>) request.getAttribute(
                "compras.requerimiento.ordenesMedicas"
        );

String errorOrdenMedicaCompraVista =
        (String) request.getAttribute(
                "compras.requerimiento.errorOrdenesMedicas"
        );

if (ordenesMedicasCompraVista == null) {
    ordenesMedicasCompraVista =
            new ArrayList<RequerimientoCompraPresupuesto>();
}

if (errorOrdenMedicaCompraVista != null
        && errorOrdenMedicaCompraVista.trim().length() == 0) {
    errorOrdenMedicaCompraVista = null;
}

SimpleDateFormat formatoFechaOrdenMedicaCompraVista =
        new SimpleDateFormat("dd/MM/yyyy");
%>

<div class="compras-seccion compras-seccion-orden-medica compras-seccion-adjuntos">

    <fieldset class="block-labels">

        <legend>Orden médica</legend>

        <% if (errorOrdenMedicaCompraVista != null) { %>

            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(errorOrdenMedicaCompraVista) %>
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

                        String fechaOrdenMedicaCompraVista = "";

                        if (ordenMedicaCompraVista.getFechaDocumento() != null) {

                            fechaOrdenMedicaCompraVista =
                                    formatoFechaOrdenMedicaCompraVista.format(
                                            ordenMedicaCompraVista
                                                    .getFechaDocumento()
                                    );
                        }

                        String urlOrdenMedicaCompraVista = "";

                        Long dlFileEntryIdOrdenMedica =
                                ordenMedicaCompraVista.getDlFileEntryId();

                        if (dlFileEntryIdOrdenMedica != null
                                && dlFileEntryIdOrdenMedica.longValue() > 0L) {

                            PortletURL descargarOrdenMedicaCompraURL =
                                    renderResponse.createActionURL();

                            descargarOrdenMedicaCompraURL.setWindowState(
                                    LiferayWindowState.EXCLUSIVE
                            );

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "struts_action",
                                    "/compras/descargar_orden_medica"
                            );

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "strip",
                                    "false"
                            );

                            descargarOrdenMedicaCompraURL.setParameter(
                                    "visualizar",
                                    "true"
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
                                            dlFileEntryIdOrdenMedica.longValue()
                                    )
                            );

                            urlOrdenMedicaCompraVista =
                                    descargarOrdenMedicaCompraURL.toString();
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
                                    ) %>"
                                       target="_blank"
                                       title="Ver Orden médica"
                                       onclick="return <portlet:namespace />verOrdenMedicaCompra(this.href);">

                                        <img
                                                src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
                                                alt="Ver Orden médica"
                                                title="Ver Orden médica"
                                                style="border:0;" />

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

<script type="text/javascript">

function <portlet:namespace />verOrdenMedicaCompra(url) {

    if (!url) {
        return false;
    }

    window.open(
            url,
            'mywindow',
            'width=800,height=800,toolbar=no,resizable=yes'
    );

    return false;
}

</script>
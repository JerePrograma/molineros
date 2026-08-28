<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.portlet.PortletURL" %>

<%
RequerimientoCompraReclamoPrestacional relacionDocumentacionCompras =
        null;

List<RequerimientoCompraPresupuesto>
        ordenesMedicasDocumentacionCompras =
        new ArrayList<RequerimientoCompraPresupuesto>();

RequerimientoCompraPedidoCotizacion
        pedidoCotizacionDocumentacionCompras =
        null;

RequerimientoCompraPresupuesto
        presupuestoDocumentacionCompras =
        null;

boolean mostrarDocumentacionCompras =
        false;

String errorDocumentacionCompras =
        null;

if (reclamoprestacional != null
        && reclamoprestacional.getId_reclamo() > 0) {

    try {
        relacionDocumentacionCompras =
                RequerimientoCompraReclamoPrestacionalServiceUtil
                        .getRelacionPorReclamoPrestacional(
                                reclamoprestacional
                                        .getId_reclamo()
                        );

        mostrarDocumentacionCompras =
                relacionDocumentacionCompras != null
                        && relacionDocumentacionCompras
                        .isVinculado()
                        && relacionDocumentacionCompras
                        .getIdReclamoPrestacionalInt()
                        == reclamoprestacional
                        .getId_reclamo()
                        && relacionDocumentacionCompras
                        .getIdRequerimientoCompra() > 0;

        if (mostrarDocumentacionCompras) {

            int idRequerimientoDocumentacionCompras =
                    relacionDocumentacionCompras
                            .getIdRequerimientoCompra();

            List<RequerimientoCompraPresupuesto>
                    ordenesRecuperadas =
                    BusquedaRequerimientoCompraServiceUtil
                            .listarOrdenesMedicas(
                                    idRequerimientoDocumentacionCompras
                            );

            if (ordenesRecuperadas != null) {
                ordenesMedicasDocumentacionCompras
                        .addAll(
                                ordenesRecuperadas
                        );
            }

            pedidoCotizacionDocumentacionCompras =
                    BusquedaRequerimientoCompraServiceUtil
                            .getPedidoCotizacionAdjudicado(
                                    idRequerimientoDocumentacionCompras
                            );

            presupuestoDocumentacionCompras =
                    BusquedaRequerimientoCompraServiceUtil
                            .getPresupuestoAdjudicado(
                                    idRequerimientoDocumentacionCompras
                            );
        }

    } catch (Exception errorDocumentacion) {

        if (mostrarDocumentacionCompras) {
            errorDocumentacionCompras =
                    "No se pudo recuperar la documentación de Compras.";
        }
    }
}

boolean pedidoCotizacionDocumentacionComprasValido =
        mostrarDocumentacionCompras
                && pedidoCotizacionDocumentacionCompras != null
                && pedidoCotizacionDocumentacionCompras
                .getIdRequerimiento() != null
                && pedidoCotizacionDocumentacionCompras
                .getIdRequerimiento()
                .intValue()
                == relacionDocumentacionCompras
                .getIdRequerimientoCompra()
                && pedidoCotizacionDocumentacionCompras
                .getIdPrestador() != null
                && pedidoCotizacionDocumentacionCompras
                .getIdPrestador()
                .intValue() > 0
                && pedidoCotizacionDocumentacionCompras
                .getIntento() != null
                && pedidoCotizacionDocumentacionCompras
                .getIntento()
                .intValue() > 0
                && pedidoCotizacionDocumentacionCompras
                .getDlFileEntryId() != null
                && pedidoCotizacionDocumentacionCompras
                .getDlFileEntryId()
                .longValue() > 0L;

boolean presupuestoDocumentacionComprasValido =
        mostrarDocumentacionCompras
                && presupuestoDocumentacionCompras != null
                && presupuestoDocumentacionCompras.isActivo()
                && presupuestoDocumentacionCompras
                .getIdRequerimiento() != null
                && presupuestoDocumentacionCompras
                .getIdRequerimiento()
                .intValue()
                == relacionDocumentacionCompras
                .getIdRequerimientoCompra()
                && presupuestoDocumentacionCompras
                .getTipoDocumento() != null
                && presupuestoDocumentacionCompras
                .getTipoDocumento()
                .intValue()
                == RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_PRESUPUESTO
                && presupuestoDocumentacionCompras
                .getIdPrestador() != null
                && presupuestoDocumentacionCompras
                .getIdPrestador()
                .intValue() > 0
                && presupuestoDocumentacionCompras
                .getIdRequerimientoPresupuesto() != null;

String urlPedidoCotizacionDocumentacionCompras =
        "";

String urlPresupuestoDocumentacionCompras =
        "";

if (pedidoCotizacionDocumentacionComprasValido) {

    PortletURL urlPedidoCotizacionCompras =
            renderResponse.createActionURL();

    urlPedidoCotizacionCompras.setParameter(
            "struts_action",
            "/autorizaciones/descargar_documento_compra_reclamo"
    );

    urlPedidoCotizacionCompras.setParameter(
            "id_reclamo_prestacional",
            String.valueOf(
                    reclamoprestacional
                            .getId_reclamo()
            )
    );

    urlPedidoCotizacionCompras.setParameter(
            WebKeysCompras
                    .PARAM_TIPO_DOCUMENTO_COMPRA_RECLAMO,
            WebKeysCompras
                    .DOCUMENTO_COMPRA_RECLAMO_PEDIDO_COTIZACION
    );

    urlPedidoCotizacionDocumentacionCompras =
            urlPedidoCotizacionCompras
                    .toString();
}

if (presupuestoDocumentacionComprasValido) {

    PortletURL urlPresupuestoCompras =
            renderResponse.createActionURL();

    urlPresupuestoCompras.setParameter(
            "struts_action",
            "/autorizaciones/descargar_documento_compra_reclamo"
    );

    urlPresupuestoCompras.setParameter(
            "id_reclamo_prestacional",
            String.valueOf(
                    reclamoprestacional
                            .getId_reclamo()
            )
    );

    urlPresupuestoCompras.setParameter(
            "id_requerimiento_presupuesto",
            String.valueOf(
                    presupuestoDocumentacionCompras
                            .getIdRequerimientoPresupuesto()
            )
    );

    urlPresupuestoDocumentacionCompras =
            urlPresupuestoCompras.toString();
}
%>

<% if (mostrarDocumentacionCompras) { %>

    <fieldset class="block-labels documentacion-compras-reclamo">

        <legend>
            Documentaci&oacute;n de Compras
        </legend>

        <% if (errorDocumentacionCompras != null) { %>

            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(
                        errorDocumentacionCompras
                ) %>
            </div>

        <% } else { %>

            <table class="taglib-search-iterator"
                   width="100%"
                   cellpadding="4"
                   cellspacing="0"
                   border="0">

                <thead>
                    <tr class="portlet-section-header results-header">
                        <th>Documento</th>
                        <th>Fecha</th>
                        <th>Archivo</th>
                        <th>Acci&oacute;n</th>
                    </tr>
                </thead>

                <tbody>

                <%
                boolean hayOrdenMedicaValida =
                        false;

                int filaDocumentacionCompras =
                        0;

                for (int i = 0;
                     i < ordenesMedicasDocumentacionCompras.size();
                     i++) {

                    RequerimientoCompraPresupuesto ordenMedica =
                            ordenesMedicasDocumentacionCompras
                                    .get(i);

                    boolean ordenValida =
                            ordenMedica != null
                                    && ordenMedica.isActivo()
                                    && ordenMedica
                                    .getIdRequerimiento() != null
                                    && ordenMedica
                                    .getIdRequerimiento()
                                    .intValue()
                                    == relacionDocumentacionCompras
                                    .getIdRequerimientoCompra()
                                    && ordenMedica
                                    .getTipoDocumento() != null
                                    && ordenMedica
                                    .getTipoDocumento()
                                    .intValue()
                                    == RequerimientoCompraPresupuesto
                                    .TIPO_DOCUMENTO_ORDEN_MEDICA
                                    && ordenMedica
                                    .getIdPrestador() == null
                                    && ordenMedica
                                    .getFechaDocumento() != null
                                    && ordenMedica
                                    .getIdRequerimientoPresupuesto()
                                    != null;

                    if (!ordenValida) {
                        continue;
                    }

                    hayOrdenMedicaValida =
                            true;

                    String fechaOrden =
                            new SimpleDateFormat(
                                    "dd/MM/yyyy"
                            ).format(
                                    ordenMedica
                                            .getFechaDocumento()
                            );

                    PortletURL urlOrden =
                            renderResponse
                                    .createActionURL();

                    urlOrden.setParameter(
                            "struts_action",
                            "/autorizaciones/descargar_documento_compra_reclamo"
                    );

                    urlOrden.setParameter(
                            "id_reclamo_prestacional",
                            String.valueOf(
                                    reclamoprestacional
                                            .getId_reclamo()
                            )
                    );

                    urlOrden.setParameter(
                            "id_requerimiento_presupuesto",
                            String.valueOf(
                                    ordenMedica
                                            .getIdRequerimientoPresupuesto()
                            )
                    );
                %>

                    <tr class="<%=
                            filaDocumentacionCompras++ % 2 == 0
                                    ? "results-row"
                                    : "results-row alt"
                    %>">
                        <td>Orden m&eacute;dica</td>

                        <td>
                            <%= HtmlUtil.escape(
                                    fechaOrden
                            ) %>
                        </td>

                        <td>
                            <%= HtmlUtil.escape(
                                    ordenMedica
                                            .getNombreOriginal()
                            ) %>
                        </td>

                        <td>
                            <a
                                href="<%= HtmlUtil.escape(
                                        urlOrden.toString()
                                ) %>"
                                title="Ver / descargar Orden médica">

                                <img
                                    src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
                                    alt="Ver / descargar Orden médica"
                                    style="border:0;" />

                            </a>
                        </td>
                    </tr>

                <%
                }

                if (!hayOrdenMedicaValida) {
                %>

                    <tr class="<%=
                            filaDocumentacionCompras++ % 2 == 0
                                    ? "results-row"
                                    : "results-row alt"
                    %>">
                        <td>Orden m&eacute;dica</td>
                        <td colspan="3">No disponible</td>
                    </tr>

                <%
                }
                %>

                <tr class="<%=
                        filaDocumentacionCompras++ % 2 == 0
                                ? "results-row"
                                : "results-row alt"
                %>">
                    <td>Pedido de cotizaci&oacute;n</td>
                    <td>-</td>

                    <% if (pedidoCotizacionDocumentacionComprasValido) { %>

                        <td>
                            <%= HtmlUtil.escape(
                                    pedidoCotizacionDocumentacionCompras
                                            .getNombreOriginal()
                            ) %>
                        </td>

                        <td>
                            <a
                                href="<%= HtmlUtil.escape(
                                        urlPedidoCotizacionDocumentacionCompras
                                ) %>"
                                target="_blank"
                                rel="noopener noreferrer"
                                title="Ver / descargar pedido de cotización">

                                <img
                                    src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
                                    alt="Ver / descargar pedido de cotización"
                                    style="border:0;" />

                            </a>
                        </td>

                    <% } else { %>

                        <td colspan="2">No disponible</td>

                    <% } %>
                </tr>

                <tr class="<%=
                        filaDocumentacionCompras++ % 2 == 0
                                ? "results-row"
                                : "results-row alt"
                %>">
                    <td>
                        Cotizaci&oacute;n del prestador adjudicado
                    </td>

                    <td>-</td>

                    <% if (presupuestoDocumentacionComprasValido) { %>

                        <td>
                            <%= HtmlUtil.escape(
                                    presupuestoDocumentacionCompras
                                            .getNombreOriginal()
                            ) %>
                        </td>

                        <td>
                            <a
                                href="<%= HtmlUtil.escape(
                                        urlPresupuestoDocumentacionCompras
                                ) %>"
                                target="_blank"
                                rel="noopener noreferrer"
                                title="Ver / descargar cotización adjudicada">

                                <img
                                    src="<%= themeDisplay.getPathThemeImages() %>/common/view.png"
                                    alt="Ver / descargar cotización adjudicada"
                                    style="border:0;" />

                            </a>
                        </td>

                    <% } else { %>

                        <td colspan="2">No disponible</td>

                    <% } %>
                </tr>

                </tbody>

            </table>

        <% } %>

    </fieldset>

<% } %>
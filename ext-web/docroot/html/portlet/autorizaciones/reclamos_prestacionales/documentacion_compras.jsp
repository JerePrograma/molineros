<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil" %>
<%
RequerimientoCompraReclamoPrestacional relacionDocumentacionCompras = null;
RequerimientoCompraPresupuesto ordenMedicaDocumentacionCompras = null;
RequerimientoCompraPresupuesto presupuestoDocumentacionCompras = null;
boolean mostrarDocumentacionCompras = false;
String errorDocumentacionCompras = null;

if (reclamoprestacional != null
        && reclamoprestacional.getId_reclamo() > 0) {

    try {
        relacionDocumentacionCompras =
                RequerimientoCompraReclamoPrestacionalServiceUtil
                        .getRelacionPorReclamoPrestacional(
                                reclamoprestacional.getId_reclamo()
                        );

        mostrarDocumentacionCompras =
                relacionDocumentacionCompras != null
                && relacionDocumentacionCompras.isVinculado()
                && relacionDocumentacionCompras
                        .getIdReclamoPrestacionalInt()
                        == reclamoprestacional.getId_reclamo()
                && relacionDocumentacionCompras
                        .getIdRequerimientoCompra() > 0;

        if (mostrarDocumentacionCompras) {
            int idRequerimientoDocumentacionCompras =
                    relacionDocumentacionCompras
                            .getIdRequerimientoCompra();

            ordenMedicaDocumentacionCompras =
                    BusquedaRequerimientoCompraServiceUtil
                            .getOrdenMedica(
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
                    "No se pudo recuperar la documentaci\u00f3n de Compras.";
        }
    }
}

boolean ordenMedicaDocumentacionComprasValida =
        mostrarDocumentacionCompras
        && ordenMedicaDocumentacionCompras != null
        && ordenMedicaDocumentacionCompras.isActivo()
        && ordenMedicaDocumentacionCompras.getIdRequerimiento() != null
        && ordenMedicaDocumentacionCompras
                .getIdRequerimiento().intValue()
                == relacionDocumentacionCompras
                        .getIdRequerimientoCompra()
        && ordenMedicaDocumentacionCompras.getTipoDocumento() != null
        && ordenMedicaDocumentacionCompras
                .getTipoDocumento().intValue()
                == RequerimientoCompraPresupuesto
                        .TIPO_DOCUMENTO_ORDEN_MEDICA
        && ordenMedicaDocumentacionCompras.getIdPrestador() == null
        && ordenMedicaDocumentacionCompras.getFechaDocumento() != null
        && ordenMedicaDocumentacionCompras
                .getIdRequerimientoPresupuesto() != null;

boolean presupuestoDocumentacionComprasValido =
        mostrarDocumentacionCompras
        && presupuestoDocumentacionCompras != null
        && presupuestoDocumentacionCompras.isActivo()
        && presupuestoDocumentacionCompras.getIdRequerimiento() != null
        && presupuestoDocumentacionCompras
                .getIdRequerimiento().intValue()
                == relacionDocumentacionCompras
                        .getIdRequerimientoCompra()
        && presupuestoDocumentacionCompras.getTipoDocumento() != null
        && presupuestoDocumentacionCompras
                .getTipoDocumento().intValue()
                == RequerimientoCompraPresupuesto
                        .TIPO_DOCUMENTO_PRESUPUESTO
        && presupuestoDocumentacionCompras.getIdPrestador() != null
        && presupuestoDocumentacionCompras
                .getIdPrestador().intValue() > 0
        && presupuestoDocumentacionCompras
                .getIdRequerimientoPresupuesto() != null;

String fechaOrdenMedicaDocumentacionCompras = "";
String urlOrdenMedicaDocumentacionCompras = "";
String urlPresupuestoDocumentacionCompras = "";

if (ordenMedicaDocumentacionComprasValida) {
    fechaOrdenMedicaDocumentacionCompras = new SimpleDateFormat(
            "dd/MM/yyyy"
    ).format(ordenMedicaDocumentacionCompras.getFechaDocumento());

    PortletURL urlOrdenMedicaCompras =
            renderResponse.createActionURL();
    urlOrdenMedicaCompras.setParameter(
            "struts_action",
            "/autorizaciones/descargar_documento_compra_reclamo"
    );
    urlOrdenMedicaCompras.setParameter(
            "id_reclamo_prestacional",
            String.valueOf(reclamoprestacional.getId_reclamo())
    );
    urlOrdenMedicaCompras.setParameter(
            "id_requerimiento_presupuesto",
            String.valueOf(
                    ordenMedicaDocumentacionCompras
                            .getIdRequerimientoPresupuesto()
            )
    );
    urlOrdenMedicaDocumentacionCompras =
            urlOrdenMedicaCompras.toString();
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
            String.valueOf(reclamoprestacional.getId_reclamo())
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
        <legend>Documentaci&oacute;n de Compras</legend>

        <% if (errorDocumentacionCompras != null) { %>
            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(errorDocumentacionCompras) %>
            </div>
        <% } else { %>
            <table class="lfr-table">
                <tr>
                    <th>Documento</th>
                    <th>Fecha</th>
                    <th>Archivo</th>
                    <th>Acci&oacute;n</th>
                </tr>
                <tr>
                    <td>Orden m&eacute;dica</td>
                    <% if (ordenMedicaDocumentacionComprasValida) { %>
                        <td><%= HtmlUtil.escape(
                                fechaOrdenMedicaDocumentacionCompras
                        ) %></td>
                        <td><%= HtmlUtil.escape(
                                ordenMedicaDocumentacionCompras
                                        .getNombreOriginal()
                        ) %></td>
                        <td>
                            <a href="<%= HtmlUtil.escape(
                                    urlOrdenMedicaDocumentacionCompras
                            ) %>">Ver / descargar</a>
                        </td>
                    <% } else { %>
                        <td colspan="3">No disponible</td>
                    <% } %>
                </tr>
                <tr>
                    <td>Presupuesto adjudicado</td>
                    <td>-</td>
                    <% if (presupuestoDocumentacionComprasValido) { %>
                        <td><%= HtmlUtil.escape(
                                presupuestoDocumentacionCompras
                                        .getNombreOriginal()
                        ) %></td>
                        <td>
                            <a href="<%= HtmlUtil.escape(
                                    urlPresupuestoDocumentacionCompras
                            ) %>">Ver / descargar</a>
                        </td>
                    <% } else { %>
                        <td colspan="2">No disponible</td>
                    <% } %>
                </tr>
            </table>
        <% } %>
    </fieldset>
<% } %>

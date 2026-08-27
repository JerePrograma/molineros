<%--
Responsabilidad:
    Renderiza resultados documentales del requerimiento.
Incluido desde:
    requerimiento_compra_documentos.jsp
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
Log logPresupuestos =
        LogFactoryUtil.getLog(
                "compras.requerimiento_adjuntos_search_documentos"
        );

String namespaceAdjuntos =
        renderResponse.getNamespace();

RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) request.getAttribute(
                "compras.requerimiento.req"
        );

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
            );
}

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

boolean cotizacionEmpresaPresupuestos =
        reqPresupuestos != null
        && reqPresupuestos.esSectorSinCotizacionPrestador();

int idRequerimientoCompraPresupuestos =
        reqPresupuestos != null
                ? reqPresupuestos.getIdRequerimientoCompra()
                : 0;

if (idRequerimientoCompraPresupuestos <= 0) {
    idRequerimientoCompraPresupuestos =
            ParamUtil.getInteger(
                    renderRequest,
                    "id_requerimiento_compra",
                    0
            );
}

Object soloLecturaAttrPresupuestos =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String modoPresupuestos =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

String strutsActionPresupuestos =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(soloLecturaAttrPresupuestos)
        || ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        )
        || "ver".equalsIgnoreCase(modoPresupuestos)
        || "/compras/ver_requerimiento".equals(
                strutsActionPresupuestos
        );

Object puedeCotizarAttr =
        request.getAttribute(
                "compras.requerimiento.puedeCotizar"
        );

boolean puedeCotizarPresupuestos =
        puedeCotizarAttr instanceof Boolean
                ? Boolean.TRUE.equals(puedeCotizarAttr)
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_COTIZAR_COMPRAS
                        );

boolean puedeEliminarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos != null
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

PortletURL portletURL =
        renderResponse.createRenderURL();

portletURL.setWindowState(
        LiferayWindowState.MAXIMIZED
);

List<String> headerNames =
        new ArrayList<String>();
headerNames.add("Archivo");
headerNames.add(
        cotizacionEmpresaPresupuestos
                        ? "Empresa"
                        : "Prestador"
);
headerNames.add("Descargar");
headerNames.add("Eliminar");

String mensajeSinResultados =
        idRequerimientoCompraPresupuestos > 0
                ? cotizacionEmpresaPresupuestos
                                ? "No hay cotizaciones de empresas asociadas al requerimiento."
                                : "No hay presupuestos asociados al requerimiento."
                : "No se informó el requerimiento de compra.";

SearchContainer searchContainer =
        new SearchContainer(
                renderRequest,
                null,
                null,
                SearchContainer.DEFAULT_CUR_PARAM,
                SearchContainer.DEFAULT_DELTA,
                portletURL,
                headerNames,
                mensajeSinResultados
        );

List<RequerimientoCompraPresupuesto> presupuestos =
        (List<RequerimientoCompraPresupuesto>) request.getAttribute(
                "compras.requerimiento.presupuestos"
        );

Map<Integer, Boolean> documentosValidosPreparados =
        (Map<Integer, Boolean>) request.getAttribute(
                "compras.requerimiento.presupuestoDocumentoValido"
        );

Map<Integer, String> downloadUrlsPreparadas =
        (Map<Integer, String>) request.getAttribute(
                "compras.requerimiento.presupuestoDownloadURL"
        );

try {
    if (presupuestos == null) {
        presupuestos =
                new ArrayList<RequerimientoCompraPresupuesto>();
    }

    if (documentosValidosPreparados == null) {
        documentosValidosPreparados =
                new HashMap<Integer, Boolean>();
    }

    if (downloadUrlsPreparadas == null) {
        downloadUrlsPreparadas =
                new HashMap<Integer, String>();
    }

    int total = presupuestos.size();
    searchContainer.setTotal(total);

    int inicio = searchContainer.getStart();
    int fin = searchContainer.getEnd();

    if (inicio < 0) {
        inicio = 0;
    }

    if (fin > total) {
        fin = total;
    }

    List resultRows =
            searchContainer.getResultRows();

    for (int i = inicio; i < fin; i++) {
        RequerimientoCompraPresupuesto presupuesto =
                presupuestos.get(i);

        if (presupuesto == null
                || presupuesto.getIdRequerimientoPresupuesto() == null
                || presupuesto
                        .getIdRequerimientoPresupuesto()
                        .intValue() <= 0) {
            continue;
        }

        if (cotizacionEmpresaPresupuestos
                != presupuesto.isCotizacionEmpresa()) {

            continue;
        }

        int idRequerimientoPresupuesto =
                presupuesto
                        .getIdRequerimientoPresupuesto()
                        .intValue();

        ResultRow row =
                new ResultRow(
                        presupuesto,
                        idRequerimientoPresupuesto,
                        i
                );

        row.setObject(presupuesto);

        String archivoVisible =
                presupuesto.getNombreOriginal();

        if (WebKeysCompras.isEmpty(archivoVisible)) {
            archivoVisible = presupuesto.getTitulo();
        }

        if (WebKeysCompras.isEmpty(archivoVisible)) {
            archivoVisible = presupuesto.getNombrePersistido();
        }

        row.addText(
                HtmlUtil.escape(archivoVisible)
        );

        if (cotizacionEmpresaPresupuestos) {
            StringBuilder empresaVisible =
                    new StringBuilder();

            empresaVisible.append(
                    HtmlUtil.escape(
                            presupuesto.getDescripcionEmpresa()
                    )
            );

            empresaVisible.append("<br />CUIT: ");
            empresaVisible.append(
                    HtmlUtil.escape(
                            presupuesto.getEmpresaCuit()
                    )
            );

            empresaVisible.append(" - Sucursal: ");
            empresaVisible.append(
                    HtmlUtil.escape(
                            presupuesto.getEmpresaSucursal()
                    )
            );

            row.addText(empresaVisible.toString());

        } else {
            row.addText(
                    HtmlUtil.escape(
                            presupuesto.getDescripcionPrestador()
                    )
            );
        }

        boolean documentoValido =
                Boolean.TRUE.equals(
                        documentosValidosPreparados.get(
                                Integer.valueOf(
                                        idRequerimientoPresupuesto
                                )
                        )
                );

        String downloadURL =
                downloadUrlsPreparadas.get(
                        Integer.valueOf(
                                idRequerimientoPresupuesto
                        )
                );

        if (downloadURL == null) {
            downloadURL = "";
        }

        StringBuilder descargar =
                new StringBuilder();

        if (documentoValido
                && !WebKeysCompras.isEmpty(downloadURL)) {

            descargar.append("<a href=\"");
            descargar.append(HtmlUtil.escape(downloadURL));
            descargar.append("\" target=\"_blank\">");
            descargar.append(
                    "<img alt=\"Descargar presupuesto\" src=\""
            );
            descargar.append(
                    themeDisplay.getPathThemeImages()
            );
            descargar.append("/common/view.png\" />");
            descargar.append("</a>");
        } else {
            descargar.append(
                    "<span title=\"El documento asociado no está disponible\">No disponible</span>"
            );
        }

        row.addText(descargar.toString());

        StringBuilder borrar =
                new StringBuilder();

        if (puedeEliminarPresupuestos
                && documentoValido) {

            borrar.append(
                    "<img alt=\"Eliminar presupuesto\" src=\""
            );
            borrar.append(
                    themeDisplay.getPathThemeImages()
            );
            borrar.append(
                    "/common/delete.png\" style=\"cursor: pointer;\" onclick=\"return "
            );
            borrar.append(namespaceAdjuntos);
            borrar.append(
                    "deletePresupuestoRequerimientoCompra("
            );
            borrar.append(idRequerimientoPresupuesto);
            borrar.append(");\" />");
        }

        row.addText(borrar.toString());
        resultRows.add(row);
    }
%>
<liferay-ui:search-iterator
        searchContainer="<%= searchContainer %>" />

<%
} catch (Exception e) {
    logPresupuestos.error(
            "No se pudieron consultar los presupuestos "
                    + "asociados al requerimiento. "
                    + "idRequerimientoCompra="
                    + idRequerimientoCompraPresupuestos,
            e
    );
%>

    <div class="portlet-msg-error">
        No se pudieron consultar los presupuestos asociados
        al requerimiento.
    </div>

<%
}
%>

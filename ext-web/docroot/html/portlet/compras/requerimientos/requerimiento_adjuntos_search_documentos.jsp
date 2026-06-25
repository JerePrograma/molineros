<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HttpUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%
Log logPresupuestos =
        LogFactoryUtil.getLog(
                "compras.requerimiento_adjuntos_search_documentos"
        );

String namespaceAdjuntos =
        renderResponse.getNamespace();

long groupIdPresupuestos =
        themeDisplay.getScopeGroupId();

RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) renderRequest.getAttribute(
                WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
        );

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

int idRequerimientoCompraPresupuestos =
        0;

if (reqPresupuestos != null) {
    idRequerimientoCompraPresupuestos =
            reqPresupuestos
                    .getIdRequerimientoCompra();
}

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

boolean soloLecturaParamPresupuestos =
        ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        );

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(
                soloLecturaAttrPresupuestos
        )
        || soloLecturaParamPresupuestos
        || "ver".equalsIgnoreCase(
                modoPresupuestos
        )
        || "/compras/ver_requerimiento".equals(
                strutsActionPresupuestos
        );

boolean puedeCotizarPresupuestos =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );

boolean puedeEliminarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos != null
        && reqPresupuestos
                .puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

PortletURL portletURL =
        renderResponse.createRenderURL();

portletURL.setWindowState(
        LiferayWindowState.MAXIMIZED
);

List<String> headerNames =
        new ArrayList<String>();

headerNames.add(
        "Archivo"
);

headerNames.add(
        "Prestador"
);

headerNames.add(
        "Descargar"
);

headerNames.add(
        "Eliminar"
);

String mensajeSinResultados =
        idRequerimientoCompraPresupuestos > 0
                ? "No hay presupuestos asociados al requerimiento."
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

try {
    List<RequerimientoCompraPresupuesto> presupuestos =
            new ArrayList<RequerimientoCompraPresupuesto>();

    if (idRequerimientoCompraPresupuestos > 0) {
        presupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(
                                idRequerimientoCompraPresupuestos
                        );
    }

    if (presupuestos == null) {
        presupuestos =
                new ArrayList<RequerimientoCompraPresupuesto>();
    }

    int total =
            presupuestos.size();

    searchContainer.setTotal(
            total
    );

    int inicio =
            searchContainer.getStart();

    int fin =
            searchContainer.getEnd();

    if (inicio < 0) {
        inicio =
                0;
    }

    if (fin > total) {
        fin =
                total;
    }

    List resultRows =
            searchContainer.getResultRows();

    for (int i = inicio;
            i < fin;
            i++) {

        RequerimientoCompraPresupuesto presupuesto =
                presupuestos.get(
                        i
                );

        if (presupuesto == null
                || presupuesto
                        .getIdRequerimientoPresupuesto() == null
                || presupuesto
                        .getIdRequerimientoPresupuesto()
                        .intValue() <= 0) {

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

        row.setObject(
                presupuesto
        );

        String archivoVisible =
                presupuesto.getNombreOriginal();

        if (WebKeysCompras.isEmpty(
                archivoVisible
        )) {
            archivoVisible =
                    presupuesto.getTitulo();
        }

        if (WebKeysCompras.isEmpty(
                archivoVisible
        )) {
            archivoVisible =
                    presupuesto.getNombrePersistido();
        }

        row.addText(
                HtmlUtil.escape(
                        archivoVisible
                )
        );

        row.addText(
                HtmlUtil.escape(
                        presupuesto
                                .getDescripcionPrestador()
                )
        );

        DLFileEntry fileEntry =
                null;

        boolean documentoValido =
                false;

        try {
            if (presupuesto.getDlFileEntryId() != null
                    && presupuesto
                            .getDlFileEntryId()
                            .longValue() > 0L) {

                fileEntry =
                        DLFileEntryLocalServiceUtil
                                .getDLFileEntry(
                                        presupuesto
                                                .getDlFileEntryId()
                                                .longValue()
                                );
            }

            documentoValido =
                    fileEntry != null
                    && presupuesto.getDlGroupId() != null
                    && presupuesto.getDlFolderId() != null
                    && presupuesto.getNombrePersistido() != null
                    && fileEntry.getFileEntryId()
                            == presupuesto
                                    .getDlFileEntryId()
                                    .longValue()
                    && fileEntry.getGroupId()
                            == presupuesto
                                    .getDlGroupId()
                                    .longValue()
                    && fileEntry.getFolderId()
                            == presupuesto
                                    .getDlFolderId()
                                    .longValue()
                    && presupuesto
                            .getNombrePersistido()
                            .equals(
                                    fileEntry.getName()
                            );

            if (documentoValido
                    && !WebKeysCompras.isEmpty(
                            presupuesto.getDlFileUuid()
                    )) {

                documentoValido =
                        presupuesto
                                .getDlFileUuid()
                                .equals(
                                        fileEntry.getUuid()
                                );
            }

            if (documentoValido
                    && groupIdPresupuestos > 0L) {

                documentoValido =
                        fileEntry.getGroupId()
                                == groupIdPresupuestos;
            }
        } catch (Exception documentoError) {
            documentoValido =
                    false;

            if (logPresupuestos.isDebugEnabled()) {
                logPresupuestos.debug(
                        "No se pudo validar el documento asociado "
                                + "al presupuesto. "
                                + "idRequerimientoPresupuesto="
                                + idRequerimientoPresupuesto,
                        documentoError
                );
            }
        }

        StringBuilder descargar =
                new StringBuilder();

        if (documentoValido) {
            String downloadURL =
                    themeDisplay.getPathMain()
                            + "/document_library/get_file?folderId="
                            + fileEntry.getFolderId()
                            + "&name="
                            + HttpUtil.encodeURL(
                                    fileEntry.getName()
                            );

            descargar.append(
                    "<a href=\""
            );

            descargar.append(
                    HtmlUtil.escape(
                            downloadURL
                    )
            );

            descargar.append(
                    "\" target=\"_blank\">"
            );

            descargar.append(
                    "<img alt=\"Descargar presupuesto\" src=\""
            );

            descargar.append(
                    themeDisplay.getPathThemeImages()
            );

            descargar.append(
                    "/common/view.png\" />"
            );

            descargar.append(
                    "</a>"
            );
        } else {
            descargar.append(
                    "<span title=\"El documento asociado no está disponible\">No disponible</span>"
            );
        }

        row.addText(
                descargar.toString()
        );

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

            borrar.append(
                    namespaceAdjuntos
            );

            borrar.append(
                    "deletePresupuestoRequerimientoCompra("
            );

            borrar.append(
                    idRequerimientoPresupuesto
            );

            borrar.append(
                    ");\" />"
            );
        }

        row.addText(
                borrar.toString()
        );

        resultRows.add(
                row
        );
    }
%>

<br /><br />

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

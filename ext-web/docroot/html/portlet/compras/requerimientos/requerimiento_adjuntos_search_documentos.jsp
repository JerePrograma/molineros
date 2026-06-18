<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.Criterion" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.DynamicQuery" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HttpUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.NoSuchFolderException" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolder" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%!
private String jsCompraAdjunto(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
String namespaceAdjuntos = renderResponse.getNamespace();

long groupIdPresupuestos =
        themeDisplay.getScopeGroupId();

RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

int idRequerimientoCompraPresupuestos = 0;

if (reqPresupuestos != null) {
    idRequerimientoCompraPresupuestos = reqPresupuestos.getIdRequerimientoCompra();
}

if (idRequerimientoCompraPresupuestos <= 0) {
    idRequerimientoCompraPresupuestos =
            ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);
}

Object soloLecturaAttrPresupuestos =
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String modoPresupuestos = ParamUtil.getString(renderRequest, "modo", "");
String strutsActionPresupuestos = ParamUtil.getString(renderRequest, "struts_action", "");

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(soloLecturaAttrPresupuestos)
        || "ver".equalsIgnoreCase(modoPresupuestos)
        || "/compras/ver_requerimiento".equals(strutsActionPresupuestos);

boolean puedeCotizarPresupuestos =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);

boolean puedeEliminarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos != null
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

String keywords =
        idRequerimientoCompraPresupuestos > 0
                ? WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(idRequerimientoCompraPresupuestos) + "%"
                : "";

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);

List<String> headerNames = new ArrayList<String>();

headerNames.add("#");
headerNames.add("folder");
headerNames.add("document");
headerNames.add("Descripci�n");
headerNames.add("");
headerNames.add("");

SearchContainer searchContainer =
        new SearchContainer(
                renderRequest,
                null,
                null,
                SearchContainer.DEFAULT_CUR_PARAM,
                SearchContainer.DEFAULT_DELTA,
                portletURL,
                headerNames,
                LanguageUtil.format(
                        pageContext,
                        "no-documents-were-found-that-matched-the-keywords-x",
                        "<b>" + HtmlUtil.escape(keywords) + "</b>"
                )
        );

try {
    if (idRequerimientoCompraPresupuestos > 0) {

        if (groupIdPresupuestos <= 0) {
            throw new Exception(
                    "No se pudo determinar el sitio actual para consultar "
                            + "los presupuestos del requerimiento."
            );
        }

        try {
            DynamicQuery dlf =
                    DynamicQueryFactoryUtil.forClass(
                            DLFileEntry.class,
                            PortletClassLoaderUtil.getClassLoader()
                    );

            DLFolder folder =
                    DLFolderLocalServiceUtil.getFolder(
                            groupIdPresupuestos,
                            WebKeysCompras
                                    .DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                            WebKeysCompras
                                    .DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
                    );

            long folderId =
                    folder.getFolderId();

            Criterion criterion =
                    RestrictionsFactoryUtil.eq(
                            "folderId",
                            Long.valueOf(folderId)
                    );

            criterion =
                    RestrictionsFactoryUtil.and(
                            criterion,
                            RestrictionsFactoryUtil.ilike(
                                    "title",
                                    WebKeysCompras
                                            .getPrefijoDocumentoRequerimientoCompra(
                                                    idRequerimientoCompraPresupuestos
                                            )
                                            + "%"
                            )
                    );

            dlf.add(criterion);

            List results =
                    DLFileEntryLocalServiceUtil.dynamicQuery(
                            dlf
                    );

            int total =
                    results != null
                            ? results.size()
                            : 0;

            searchContainer.setTotal(total);

            List resultRows =
                    searchContainer.getResultRows();

            for (int i = 0;
                    results != null && i < results.size();
                    i++) {

                DLFileEntry fileEntry =
                        (DLFileEntry) results.get(i);

                ResultRow row =
                        new ResultRow(
                                fileEntry,
                                i,
                                i
                        );

                row.setObject(fileEntry);

                row.addText(
                        searchContainer.getStart()
                                + i
                                + 1
                                + StringPool.PERIOD
                );

                row.addText(
                        HtmlUtil.escape(
                                folder.getName()
                        )
                );

                row.addText(
                        HtmlUtil.escape(
                                fileEntry.getTitle()
                        )
                );

                row.addText(
                        HtmlUtil.escape(
                                fileEntry.getDescription()
                        )
                );

                String downloadURL =
                        themeDisplay.getPathMain()
                                + "/document_library/get_file?folderId="
                                + folderId
                                + "&name="
                                + HttpUtil.encodeURL(
                                        fileEntry.getName()
                                );

                StringBuilder ver =
                        new StringBuilder();

                ver.append("<a href=\"");
                ver.append(
                        HtmlUtil.escape(
                                downloadURL
                        )
                );
                ver.append("\" target=\"_blank\">");
                ver.append(
                        "<img alt=\"Ver presupuesto\" src=\""
                );
                ver.append(
                        themeDisplay.getPathThemeImages()
                );
                ver.append(
                        "/common/view.png\" />"
                );
                ver.append("</a>");

                row.addText(
                        ver.toString()
                );

                StringBuilder borrar =
                        new StringBuilder();

                if (puedeEliminarPresupuestos) {
                    borrar.append(
                            "<img alt=\"Eliminar presupuesto\" src=\""
                    );

                    borrar.append(
                            themeDisplay.getPathThemeImages()
                    );

                    borrar.append(
                            "/common/delete.png\" onclick=\"return "
                    );

                    borrar.append(
                            namespaceAdjuntos
                    );

                    borrar.append(
                            "deletePresupuestoRequerimientoCompra('"
                    );

                    borrar.append(
                            String.valueOf(folderId)
                    );

                    borrar.append("', '");

                    borrar.append(
                            jsCompraAdjunto(
                                    fileEntry.getName()
                            )
                    );

                    borrar.append("', '");

                    borrar.append(
                            jsCompraAdjunto(
                                    fileEntry.getTitle()
                            )
                    );

                    borrar.append(
                            "');\" />"
                    );
                }

                row.addText(
                        borrar.toString()
                );

                resultRows.add(row);
            }
        } catch (NoSuchFolderException e) {
            /*
             * Estado normal antes de la primera subida.
             *
             * La carpeta debe crearla el Action al subir el primer
             * presupuesto. Un JSP de render no debe crear datos.
             */
            searchContainer.setTotal(0);

            if (_log.isDebugEnabled()) {
                _log.debug(
                        "La carpeta de presupuestos todavía no existe. "
                                + "groupId="
                                + groupIdPresupuestos
                                + ", parentFolderId="
                                + WebKeysCompras
                                    .DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS
                                + ", name="
                                + WebKeysCompras
                                    .DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
                );
            }
        }
    } else {
        searchContainer.setTotal(0);
    }
%>

<br /><br />

<liferay-ui:search-iterator
        searchContainer="<%= searchContainer %>" />

<%
} catch (Exception e) {
    _log.error(
            "No se pudieron consultar los presupuestos "
                    + "del requerimiento. groupId="
                    + groupIdPresupuestos
                    + ", idRequerimientoCompra="
                    + idRequerimientoCompraPresupuestos,
            e
    );
%>

    <div class="portlet-msg-error">
        No se pudieron consultar los presupuestos del requerimiento
        en la carpeta de Document Library
        <strong>
            <%= HtmlUtil.escape(
                    WebKeysCompras
                            .DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
            ) %>
        </strong>.
    </div>

<%
}
%>
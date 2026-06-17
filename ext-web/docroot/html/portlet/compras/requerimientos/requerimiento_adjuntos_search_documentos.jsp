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

RequerimientoCompra reqImagenes =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqImagenes == null) {
    reqImagenes =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

int idRequerimientoCompraImagenes = 0;

if (reqImagenes != null) {
    idRequerimientoCompraImagenes = reqImagenes.getIdRequerimientoCompra();
}

if (idRequerimientoCompraImagenes <= 0) {
    idRequerimientoCompraImagenes =
            ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);
}

Object soloLecturaAttrImagenes =
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String modoImagenes = ParamUtil.getString(renderRequest, "modo", "");
String strutsActionImagenes = ParamUtil.getString(renderRequest, "struts_action", "");

boolean soloLecturaImagenes =
        Boolean.TRUE.equals(soloLecturaAttrImagenes)
        || "ver".equalsIgnoreCase(modoImagenes)
        || "/compras/ver_requerimiento".equals(strutsActionImagenes);

boolean puedeABMImagenes =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

boolean puedeEliminarImagenes =
        idRequerimientoCompraImagenes > 0
        && puedeABMImagenes
        && reqImagenes != null
        && reqImagenes.isEditable()
        && !soloLecturaImagenes;

String keywords =
        idRequerimientoCompraImagenes > 0
                ? WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(idRequerimientoCompraImagenes) + "%"
                : "";

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);

List<String> headerNames = new ArrayList<String>();

headerNames.add("#");
headerNames.add("folder");
headerNames.add("document");
headerNames.add("Descripción");
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
    if (idRequerimientoCompraImagenes > 0) {
        DynamicQuery dlf =
                DynamicQueryFactoryUtil.forClass(
                        DLFileEntry.class,
                        PortletClassLoaderUtil.getClassLoader()
                );

        DLFolder folder =
                DLFolderLocalServiceUtil.getFolder(
                        WebKeysCompras.DOCUMENT_LIBRARY_GROUP_ID_COMPRAS,
                        WebKeysCompras.DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                        WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_REQUERIMIENTOS_COMPRAS
                );

        long folderId = folder.getFolderId();

        Criterion criterion =
                RestrictionsFactoryUtil.eq("folderId", Long.valueOf(folderId));

        criterion =
                RestrictionsFactoryUtil.and(
                        criterion,
                        RestrictionsFactoryUtil.ilike(
                                "title",
                                WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                                        idRequerimientoCompraImagenes
                                ) + "%"
                        )
                );

        dlf.add(criterion);

        List results = DLFileEntryLocalServiceUtil.dynamicQuery(dlf);

        int total = results != null ? results.size() : 0;

        searchContainer.setTotal(total);

        List resultRows = searchContainer.getResultRows();

        for (int i = 0; results != null && i < results.size(); i++) {
            DLFileEntry fileEntry = (DLFileEntry) results.get(i);

            ResultRow row = new ResultRow(fileEntry, i, i);

            row.setObject(fileEntry);

            row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);
            row.addText(HtmlUtil.escape(fileEntry.getFolder().getName()));
            row.addText(HtmlUtil.escape(fileEntry.getTitle()));
            row.addText(HtmlUtil.escape(fileEntry.getDescription()));

            String downloadURL =
                    themeDisplay.getPathMain()
                            + "/document_library/get_file?folderId="
                            + folderId
                            + "&name="
                            + HttpUtil.encodeURL(fileEntry.getName());

            StringBuilder ver = new StringBuilder();

            ver.append("<a href=\"");
            ver.append(HtmlUtil.escape(downloadURL));
            ver.append("\" target=\"_blank\">");
            ver.append("<img alt=\"Ver archivo\" src=\"");
            ver.append(themeDisplay.getPathThemeImages());
            ver.append("/common/view.png\" />");
            ver.append("</a>");

            row.addText(ver.toString());

            StringBuilder borrar = new StringBuilder();

            if (puedeEliminarImagenes) {
                borrar.append("<img alt=\"Eliminar archivo\" src=\"");
                borrar.append(themeDisplay.getPathThemeImages());
                borrar.append("/common/delete.png\" onclick=\"return ");
                borrar.append(namespaceAdjuntos);
                borrar.append("deleteImagenRequerimientoCompra('");
                borrar.append(String.valueOf(folderId));
                borrar.append("', '");
                borrar.append(jsCompraAdjunto(fileEntry.getName()));
                borrar.append("', '");
                borrar.append(jsCompraAdjunto(fileEntry.getTitle()));
                borrar.append("');\" />");
            } else {
                borrar.append("");
            }

            row.addText(borrar.toString());

            resultRows.add(row);
        }
    }
%>

<br /><br />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<%
} catch (Exception e) {
    _log.error(e.getMessage(), e);
%>
    <div class="portlet-msg-info">
        No se encontraron archivos para el requerimiento o no existe la carpeta de Document Library
        <strong><%= HtmlUtil.escape(WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_REQUERIMIENTOS_COMPRAS) %></strong>.
    </div>
<%
}
%>

<%!
private static Log _log =
        LogFactoryUtil.getLog("portal-web.docroot.html.portlet.compras.requerimientos.requerimiento_adjuntos_search_documentos.jsp");
%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>

<%@page import="com.liferay.portal.kernel.dao.orm.Criterion"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQuery"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>

<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ page import="ar.com.ospim.autorizaciones.beans.SituacionMedica" %>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>

<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%
String idSituacionMedica = ParamUtil.getString(request, "idSituacionMedica", null);

if (idSituacionMedica == null || idSituacionMedica.trim().equals("")) {
    idSituacionMedica = ParamUtil.getString(request, "id_registro_sitmed", null);
}

if ((idSituacionMedica == null || idSituacionMedica.trim().equals("")) && request.getAttribute("idSituacionMedica") != null) {
    idSituacionMedica = String.valueOf(request.getAttribute("idSituacionMedica"));
}

if ((idSituacionMedica == null || idSituacionMedica.trim().equals("")) && request.getAttribute("id_registro_sitmed") != null) {
    idSituacionMedica = String.valueOf(request.getAttribute("id_registro_sitmed"));
}

if (idSituacionMedica == null || idSituacionMedica.trim().equals("") || "0".equals(idSituacionMedica)) {
    SituacionMedica situacionMedicaEnEdicion =
        (SituacionMedica) request.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION);

    if (situacionMedicaEnEdicion != null) {
        idSituacionMedica = String.valueOf(situacionMedicaEnEdicion.getId_Situacion());
    }
}

if (idSituacionMedica == null || idSituacionMedica.trim().equals("")) {
    idSituacionMedica = "0";
}

String keywords = idSituacionMedica + "%";

String modoConsulta = (String) request.getAttribute("ModoConsulta");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);

List<String> headerNames = new ArrayList<String>();

headerNames.add("#");
headerNames.add("Carpeta");
headerNames.add("Documento");
headerNames.add("Descripción");
headerNames.add("");
headerNames.add("");

SearchContainer searchContainer = new SearchContainer(
    renderRequest,
    null,
    null,
    SearchContainer.DEFAULT_CUR_PARAM,
    SearchContainer.DEFAULT_DELTA,
    portletURL,
    headerNames,
    "No se ha encontrado ningún documento con las palabras clave: " + HtmlUtil.escape(keywords)
);

try {

    List<Object> results = new ArrayList<Object>();

    if (!"0".equals(idSituacionMedica)) {

        DLFolder folder = DLFolderLocalServiceUtil.getFolder(
            10136,
            0L,
            "SituacionMedica"
        );

        long folderId = folder.getFolderId();

        DynamicQuery dq = DynamicQueryFactoryUtil.forClass(
            DLFileEntry.class,
            PortletClassLoaderUtil.getClassLoader()
        );

        Criterion criterion = RestrictionsFactoryUtil.eq("folderId", folderId);

        criterion = RestrictionsFactoryUtil.and(
            criterion,
            RestrictionsFactoryUtil.ilike("title", idSituacionMedica + "%")
        );

        dq.add(criterion);

        results = DLFileEntryLocalServiceUtil.dynamicQuery(dq);
    }

    int total = results.size();

    searchContainer.setTotal(total);

    List resultRows = searchContainer.getResultRows();

    int i = 0;

    for (Object obj : results) {

        DLFileEntry fileEntry = (DLFileEntry) obj;

        ResultRow row = new ResultRow(fileEntry, i, i);

        row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);

        i++;

        row.setObject(fileEntry);

        row.addText(fileEntry.getFolder().getName());
        row.addText(fileEntry.getTitle());
        row.addText(fileEntry.getDescription());

        StringBuilder ver = new StringBuilder();

        ver.append("<img alt=\"Ver Imagen\" src=\"");
        ver.append(themeDisplay.getPathThemeImages());
        ver.append("/common/view.png\" onClick=\"javascript:verImagenSituacionMedica('");
        ver.append(String.valueOf(fileEntry.getFolderId()));
        ver.append("','");
        ver.append(fileEntry.getName());
        ver.append("');\" /> ");

        row.addText(ver.toString());

        StringBuilder eliminar = new StringBuilder();

        if ("si".equalsIgnoreCase(modoConsulta)) {
            eliminar.append("");
        } else {
            eliminar.append("<img alt=\"Delete Imagen\" src=\"");
            eliminar.append(themeDisplay.getPathThemeImages());
            eliminar.append("/common/delete.png\" onClick=\"javascript:deleteImagenSituacionMedica('");
            eliminar.append(String.valueOf(fileEntry.getFolderId()));
            eliminar.append("','");
            eliminar.append(fileEntry.getName());
            eliminar.append("','archivos');\" /> ");
        }

        row.addText(eliminar.toString());

        resultRows.add(row);
    }

%>

<br /><br />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<%
} catch (Exception e) {
    _log.error(e.getMessage(), e);
%>

<div class="portlet-msg-error">
    Error al recuperar documentos de Situación Médica: <%= e.getMessage() %>
</div>

<%
}
%>

<%!
private static Log _log = LogFactoryUtil.getLog("sitmedica_imagenes_search_documentos.jsp");
%>
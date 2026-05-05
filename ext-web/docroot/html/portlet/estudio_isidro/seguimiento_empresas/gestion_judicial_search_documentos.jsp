<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%
/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.DemandaJudicial" %>
<%@ page import="ar.com.ospim.estudioisidro.WebKeysEstudioIsidro" %>

<%@page import="com.liferay.portal.kernel.util.PortalClassLoaderUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Property"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Junction"%>
<%@page import="com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Criterion"%>
<%@page import="com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQuery"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil"%>

<%
String redirect = ParamUtil.getString(request, "redirect");

DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);


DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "DEMANDAS_JUDICIALES");
long folderIdNew=f.getFolderId();

String demandaId= demanda!=null && demanda.getId()!=null?demanda.getId().toString():"NULL";
String keywords = ParamUtil.getString(request, "keywords","DEMANDA_"+demandaId+"-");

Criterion criterion1 = null;
criterion1 = RestrictionsFactoryUtil.eq("folderId",folderIdNew);
criterion1=RestrictionsFactoryUtil.and(criterion1,RestrictionsFactoryUtil.ilike("title",keywords+"%" ));
%>




<form action="" method="post" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;" >


<%
PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setWindowState(LiferayWindowState.MAXIMIZED);


List<String> headerNames = new ArrayList<String>();

headerNames.add("#");
headerNames.add("folder");
headerNames.add("document");
headerNames.add("Descripción");
headerNames.add("");
headerNames.add("");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, LanguageUtil.format(pageContext, "no-documents-were-found-that-matched-the-keywords-x", "<b>" + HtmlUtil.escape(keywords) + "</b>"));

try {
    
    long[] folderIdsArray = new long[] {folderIdNew};
    Hits results = DLFolderLocalServiceUtil.search(company.getCompanyId(), scopeGroupId, 0, folderIdsArray, keywords, searchContainer.getStart(), searchContainer.getEnd());
	
    DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
    dlf.add(criterion1);
    
    List<Object> list  =DLFolderLocalServiceUtil.dynamicQuery(dlf);
    
    int total = list.size() ;

	searchContainer.setTotal(total);

	List resultRows = searchContainer.getResultRows();
		
		

	for (int i = 0; i < list.size() ; i++) {

        DLFileEntry fileEntry = (DLFileEntry)list.get(i);

		ResultRow row = new ResultRow(fileEntry, i, i);

		row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);

		row.setObject(fileEntry);
		row.addText(fileEntry.getFolder().getName());
		row.addText(fileEntry.getTitle());
		row.addText(fileEntry.getDescription());
		
		StringBuilder s = new StringBuilder();
		s.append("<img alt=\"Ver Imagen\" src=\"");
		s.append(themeDisplay.getPathThemeImages());
		s.append("/common/view.png\" onClick=\"javascript:verImagenDemanda('");				 					
		s.append(String.valueOf(folderIdNew));
		s.append("','");
		s.append(fileEntry.getName());
		s.append("');\" /> ");
		row.addText(s.toString());
		
		
		StringBuilder s1 = new StringBuilder();
		s1.append("<img alt=\"Delete Imagen\" src=\"");
		s1.append(themeDisplay.getPathThemeImages());
		s1.append("/common/delete.png\" onClick=\"javascript:deleteImagenDemanda('");				 					
		s1.append(String.valueOf(folderIdNew));
		s1.append("','");
		s1.append(fileEntry.getName());
		s1.append("');\" /> ");
		row.addText(s1.toString());
		
		resultRows.add(row);
	}

%>

		<br /><br />

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<%
}
catch (Exception e) {
	_log.error(e.getMessage());
}
%>

</form>

<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />keywords);
	</script>
</c:if>

<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>


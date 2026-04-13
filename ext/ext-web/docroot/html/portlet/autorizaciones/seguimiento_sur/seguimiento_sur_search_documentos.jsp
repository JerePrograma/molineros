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
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSur" %>
<%@ page import="ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);


DLFolder f = DLFolderLocalServiceUtil.getFolder(
        10136, 0L, "ExpedientesSUR");
long folderIdNew=f.getFolderId();


//String keywords = ParamUtil.getString(request, "keywords",seguimiento.getId_tipo_expediente_nro()+"-"+seguimiento.getClaseExpediente()+"_*");
String keywords = ParamUtil.getString(request, "keywords",seguimiento.getId_tipo_expediente_nro()+"-"+seguimiento.getClaseExpediente()+"_");
%>




<form action="" method="post" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;" >


<%
PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setWindowState(WindowState.MAXIMIZED);


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
	
    List<DLFileEntryImpl>list = SeguimientoSurServiceUtil.getImagenesSeguimientoSur(keywords);
    
    int total = list.size() ;
//	int total = results.getLength();

	searchContainer.setTotal(total);

	List resultRows = searchContainer.getResultRows();
//	for (int i = 0; i < results.getDocs().length; i++) {
		
		

	for (int i = 0; i < list.size() ; i++) {

//		 Document doc = results.doc(i);
//       ResultRow row = new ResultRow(doc, i, i);

        DLFileEntry doc = list.get(i);   //--

		ResultRow row = new ResultRow(doc, i, i);

		row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);
		
	
/*		
		long folderId = GetterUtil.getLong(doc.get("repositoryId"));
		String fileName = doc.get("path");

		DLFileEntry fileEntry = null;

		try {
			fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(folderId, fileName);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Document library search index is stale and contains file entry {" + folderId + ", " + fileName + "}");
			}

			continue;
		}
*/


//DS--
        long folderId = list.get(i).getFolderId() ;
        String fileName = list.get(i).getName() ;

        DLFileEntry fileEntry = list.get(i);
//        

//        try {
//	      fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(folderId, fileName);
//        }
//        catch (Exception e) {
//	      if (_log.isWarnEnabled()) {
//		       _log.warn("Document library search index is stale and contains file entry {" + folderId + ", " + fileName + "}");
//	      }

//	      continue;
//        }
//DS--   


		row.setObject(fileEntry);

		DLFolder folder = null;

		try {
			folder = DLFolderLocalServiceUtil.getFolder(folderId);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Document library search index is stale and contains folder " + folderId);
			}

			continue;
		}

		row.addText(fileEntry.getFolder().getName());
		row.addText(fileEntry.getTitle());
		row.addText(fileEntry.getDescription());
		
		StringBuilder s = new StringBuilder();
		s.append("<img alt=\"Ver Imagen\" src=\"");
		s.append(themeDisplay.getPathThemeImages());
		s.append("/common/view.png\" onClick=\"javascript:verImagenSeguimiento('");				 					
		s.append(String.valueOf(folderIdNew));
		s.append("','");
		s.append(fileEntry.getName());
		s.append("');\" /> ");
		row.addText(s.toString());
		
		
		StringBuilder s1 = new StringBuilder();
		s1.append("<img alt=\"Delete Imagen\" src=\"");
		s1.append(themeDisplay.getPathThemeImages());
		s1.append("/common/delete.png\" onClick=\"javascript:deleteImagenSeguimiento('");				 					
		s1.append(String.valueOf(folderIdNew));
		s1.append("','");
		s1.append(fileEntry.getName());
		s1.append("');\" /> ");
		row.addText(s1.toString());
		
		resultRows.add(row);
	}
	
	
	
/*	
	List<DLFileEntry> aux = DLFileEntryLocalServiceUtil.getFileEntries(folderIdNew);
	
	List<DLFileEntry>results=new ArrayList<DLFileEntry>();
	
	for (DLFileEntry fileEntry :aux) {
		if(fileEntry.getTitle().startsWith(seguimiento.getNro_solicitud_sur()+"_")){
			results.add(fileEntry);
		}
	}
	
	int total = results.size();
	searchContainer.setTotal(total);

	List resultRows = searchContainer.getResultRows();

	int i=-1;
	for (DLFileEntry fileEntry :results) {
        i++;
		ResultRow row = new ResultRow(fileEntry, i, i);

		row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);

		row.setObject(fileEntry);
		row.addText(fileEntry.getFolder().getName());
		row.addText(fileEntry.getTitle());
		row.addText(fileEntry.getDescription());
		
		StringBuilder s = new StringBuilder();
		s.append("<img alt=\"Ver Imagen\" src=\"");
		s.append(themeDisplay.getPathThemeImages());
		s.append("/common/view.png\" onClick=\"javascript:verImagenSeguimiento('");				 					
		s.append(String.valueOf(folderIdNew));
		s.append("','");
		s.append(fileEntry.getName());
		s.append("');\" /> ");
		row.addText(s.toString());
		
		
		StringBuilder s1 = new StringBuilder();
		s1.append("<img alt=\"Delete Imagen\" src=\"");
		s1.append(themeDisplay.getPathThemeImages());
		s1.append("/common/delete.png\" onClick=\"javascript:deleteImagenSeguimiento('");				 					
		s1.append(String.valueOf(folderIdNew));
		s1.append("','");
		s1.append(fileEntry.getName());
		s1.append("');\" /> ");
		row.addText(s1.toString());
		
		resultRows.add(row);
	}
*/	
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

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />keywords);
	</script>
</c:if>

<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>


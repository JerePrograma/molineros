<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil" %>


<%
String redirect = ParamUtil.getString(request, "redirect");

Comprobante comprobante=(Comprobante)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW);


DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");
long folderIdNew=f.getFolderId();

String idFacturaImg = comprobante.getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
		comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

String keywords = ParamUtil.getString(request, "keywords",idFacturaImg);

String portlet_name = ParamUtil.getString(request, "portlet_name");
/*if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "comprobantes";
}else*/
if(renderResponse.getNamespace().equals("_COM_1_")){
	portlet_name = "comprobantes";
}else if(renderResponse.getNamespace().equals("_AUT_1_")){
	portlet_name = "autorizaciones";
}

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

SearchContainer searchContainerCpb = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, LanguageUtil.format(pageContext, "no-documents-were-found-that-matched-the-keywords-x", "<b>" + HtmlUtil.escape(keywords) + "</b>"));
SearchContainer searchContainerAdj = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, LanguageUtil.format(pageContext, "no-documents-were-found-that-matched-the-keywords-x", "<b>" + HtmlUtil.escape(keywords) + "</b>"));

try {
    
    long[] folderIdsArray = new long[] {folderIdNew};
    Hits results = DLFolderLocalServiceUtil.search(company.getCompanyId(), scopeGroupId, 0, folderIdsArray, keywords, searchContainerCpb.getStart(), searchContainerCpb.getEnd());
	
    List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(keywords,"CPBTE");
    
    int total = list.size() ;

	searchContainerCpb.setTotal(total);

	List resultRows = searchContainerCpb.getResultRows();
		
    		

	for (int i = 0; i < list.size() ; i++) {


        DLFileEntry doc = list.get(i);   //--

		ResultRow row = new ResultRow(doc, i, i);

		row.addText(searchContainerCpb.getStart() + i + 1 + StringPool.PERIOD);
        long folderId = list.get(i).getFolderId() ;
        String fileName = list.get(i).getName() ;

        DLFileEntry fileEntry = list.get(i);


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
		s.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
		s.append(String.valueOf(folderIdNew));
		s.append("','");
		s.append(fileEntry.getName());
		s.append("');\" /> ");
		row.addText(s.toString());
		
		resultRows.add(row);
	}
	
	
	
	 List<DLFileEntryImpl>listAdj = ComprobanteServiceUtil.getImagenesComprobantes(keywords,"ADJ");
	 
	 int totalAdj = listAdj.size() ;

		searchContainerAdj.setTotal(totalAdj);

		List resultRowsAdj = searchContainerAdj.getResultRows();
			
	    		

		for (int i = 0; i < listAdj.size() ; i++) {


	        DLFileEntry doc = listAdj.get(i);   //--

			ResultRow row = new ResultRow(doc, i, i);

			row.addText(searchContainerAdj.getStart() + i + 1 + StringPool.PERIOD);
	        long folderId = listAdj.get(i).getFolderId() ;
	        String fileName = listAdj.get(i).getName() ;

	        DLFileEntry fileEntry = listAdj.get(i);


			row.setObject(fileEntry);

			DLFolder folderAdj = null;

			try {
				folderAdj = DLFolderLocalServiceUtil.getFolder(folderId);
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
			s.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
			s.append(String.valueOf(folderIdNew));
			s.append("','");
			s.append(fileEntry.getName());
			s.append("');\" /> ");
			row.addText(s.toString());
			
			resultRowsAdj.add(row);
		}

	 
%>

		<br /><br />
     <fielset>
       <legend>Comprobante</legend>
  	   <liferay-ui:search-iterator searchContainer="<%= searchContainerCpb %>" />
	</fielset>
	<br>
	<fielset>
       <legend>Adjuntos</legend>
	   <liferay-ui:search-iterator searchContainer="<%= searchContainerAdj %>" />
	</fielset>   

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
<script type="text/javascript">

function verImagenComprobante(folderId,fileName){
   var pl ='<%=portlet_name%>';	
 /*
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/comprobantes/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';     
 */  
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/__Portlet/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>'; 
   
   url = url.replace("__Name",fileName).replace("__FolderId",folderId).replace("__Portlet",pl);
   
    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}
</script>

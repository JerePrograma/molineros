<%@ include file="/html/portlet/document_library/init.jsp" %>

<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="ar.com.ospim.crm.beans.DocumentoLegalCRM" %>
<%@page import="ar.com.ospim.crm.WebKeysCrm" %>

<%@page import="com.liferay.portal.kernel.util.PortalClassLoaderUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Property"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Junction"%>
<%@page import="com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.Criterion"%>
<%@page import="com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletClassLoaderUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.DynamicQuery"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.OrderFactoryUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.NoSuchFolderException" %>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder" %>

<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>

<%
String redirect = ParamUtil.getString(request, "redirect");

DocumentoLegalCRM docLegal = null;

boolean esView = ParamUtil.getBoolean(request, "es_view");

if(esView){
	docLegal = (DocumentoLegalCRM) request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);
}else{
	docLegal = (DocumentoLegalCRM) request.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
}

String keywords = "";

if(docLegal != null){
	
	/* keywords = ParamUtil.getString(request, "keywords",afiliado.getCuil_titular() +"%"); */
	/* keywords = ParamUtil.getString(request, "keywords", docLegal.getAfiliado().getCuil_titular() +"%"); */
	keywords = ParamUtil.getString(request, "keywords", WebKeysCrm.CRM_DOCUM_LEGAL_SUBFOLDER+docLegal.getId()+"%");
	
	String accion = (String)request.getAttribute(Constants.CMD);
	
	PortletURL portletURL = renderResponse.createRenderURL();
	
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	
	
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("#");
	headerNames.add("folder");
	headerNames.add("document");
	headerNames.add("Descripción");
	headerNames.add("");
	if(!accion.equalsIgnoreCase(Constants.VIEW) && docLegal.getDescripcionSolucion() == null){
		headerNames.add("");
	}
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 
			SearchContainer.DEFAULT_DELTA, portletURL, headerNames, 
			LanguageUtil.format(pageContext, "no-documents-were-found-that-matched-the-keywords-x", "<b>" + HtmlUtil.escape(keywords) + "</b>"));

	try {
	    
		int total = 0;
		long folderId = 0L;
		List<Object> results = new ArrayList<Object>();
		
		DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
				DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
		
		DLFolder carpetaBase = null;
		try{
			
			carpetaBase = DLFolderLocalServiceUtil.getFolder(10136, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, WebKeysCrm.CRM_DOCUM_LEGAL_FOLDER);
		
		}catch (NoSuchFolderException e) {
			carpetaBase = null;
		}
		
		DLFolder carpetaReclamo = null;
		
		if(carpetaBase != null){
			
			try{
				
				carpetaReclamo = DLFolderLocalServiceUtil.getFolder(10136, carpetaBase.getFolderId(), 
					WebKeysCrm.CRM_DOCUM_LEGAL_SUBFOLDER+docLegal.getId());
			
			}catch (NoSuchFolderException e) {
				carpetaReclamo = null;
			}	
		}
		
		if(carpetaReclamo != null){
			folderId = carpetaReclamo.getFolderId();
			
			Criterion criterion1 = null;
			criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
			criterion1=RestrictionsFactoryUtil.and(criterion1,
			RestrictionsFactoryUtil.ilike("title", docLegal.getAfiliado().getCuil_titular()+"%" ));
			dlf.add(criterion1);
			results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
			total  = results.size();
		}
	
		searchContainer.setTotal(total);
	
		List resultRows = searchContainer.getResultRows();
		
		int i = 0;
		for (Object f1 :results){
			   ResultRow row = new ResultRow(f1, i, i);
	           row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);
	           i++;
			    
			   DLFileEntry fileEntry = (DLFileEntry) f1;
			   row.setObject(fileEntry);
			   row.addText(fileEntry.getFolder().getName());
			   row.addText(fileEntry.getTitle());
			   row.addText(fileEntry.getDescription());
				
			   StringBuilder s = new StringBuilder();
			   s.append("<img alt=\"Ver Imagen\" src=\"");
			   s.append(themeDisplay.getPathThemeImages());
			   s.append("/common/view.png\" onClick=\"javascript:verImagenAfiliado('");				 					
			   s.append(String.valueOf(folderId));
			   s.append("','");
			   s.append(fileEntry.getName());
			   s.append("');\" /> ");
			   row.addText(s.toString());
				
			   if(!accion.equalsIgnoreCase(Constants.VIEW) && docLegal.getDescripcionSolucion() == null){	// si no esta solucionado
				   StringBuilder s1 = new StringBuilder();
				   s1.append("<img alt=\"Delete Imagen\" src=\"");
				   s1.append(themeDisplay.getPathThemeImages());
				   s1.append("/common/delete.png\" onClick=\"javascript:deleteImagenAfiliado('");				 					
				   s1.append(String.valueOf(folderId));
				   s1.append("','");
				   s1.append(fileEntry.getName());
				   s1.append("');\" /> ");
				   row.addText(s1.toString());
			   }
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
}	
%>

<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fmUI.<portlet:namespace />keywords);
	</script>
</c:if>



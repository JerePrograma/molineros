
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamoPrestacional" %>
<%@ include file="/html/portlet/document_library/init.jsp" %>

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
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.Conjunction" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ar.com.ospim.util.StringUtils" %>
 


<%
String redirect = ParamUtil.getString(request, "redirect");

ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

String keywords = ParamUtil.getString(request, "keywords",String.valueOf( reclamoprestacional.getId_reclamo()  ) +"%");

String modoConsulta = (String) request.getAttribute("ModoConsulta");

String solapa = (String) request.getAttribute("solapa_cuenta");


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
    
	
	DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
	
	DLFolder f = DLFolderLocalServiceUtil.getFolder(
            10136, 0L, "ReclamosPrestacionales");
    long folderId = f.getFolderId();
	
    
	Criterion criterion1 = null;

	
	
	criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
	

	if (reclamoprestacional.getCuenta() != null 
		 &&	"0".equals(reclamoprestacional.getCuenta().getCmbTitular() ) &&  StringUtils.checkNotEmpty(reclamoprestacional.getCuenta().getImagenCBU())  ){
	
		criterion1=RestrictionsFactoryUtil.and(criterion1,
		RestrictionsFactoryUtil.eq("name",  reclamoprestacional.getCuenta().getImagenCBU()));
		
		if(reclamoprestacional.getCuenta().getImagenNotaAutorizada() != null){
			criterion1=RestrictionsFactoryUtil.or(criterion1,
				RestrictionsFactoryUtil.eq("name",reclamoprestacional.getCuenta().getImagenNotaAutorizada()));
		}
	}else if (reclamoprestacional.getCuenta() != null 
			 &&	"1".equals(reclamoprestacional.getCuenta().getCmbTitular() ) 
			 &&  StringUtils.checkNotEmpty(reclamoprestacional.getCuenta().getImagenCBU())
			 &&  StringUtils.checkNotEmpty(reclamoprestacional.getCuenta().getImagenNotaAutorizada())) {
		
		criterion1=RestrictionsFactoryUtil.and(criterion1,
				RestrictionsFactoryUtil.eq("name",  reclamoprestacional.getCuenta().getImagenCBU()));
				
				if(reclamoprestacional.getCuenta().getImagenNotaAutorizada() != null){
					criterion1=RestrictionsFactoryUtil.or(criterion1,
						RestrictionsFactoryUtil.eq("name",reclamoprestacional.getCuenta().getImagenNotaAutorizada()));
				}
	}else{
		
		List<String> listStrings = new ArrayList<String>();
		listStrings.add("CBU");
		listStrings.add("NOTA AUTORIZACION PAGO");
		
		criterion1=RestrictionsFactoryUtil.and(criterion1,
		RestrictionsFactoryUtil.ilike("title", String.valueOf(reclamoprestacional.getId_reclamo()) +"%" ));
		
		criterion1=RestrictionsFactoryUtil.and(criterion1,
					RestrictionsFactoryUtil.in("description",  listStrings));
	
		
	}
	dlf.add(criterion1);

	
	List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
	
	int total = results.size();
	

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
		   s.append("/common/view.png\" onClick=\"javascript:verImagenReclamoPrestacional('");				 					
		   s.append(String.valueOf(folderId));
		   s.append("','");
		   s.append(fileEntry.getName());
		   s.append("');\" /> ");
		   row.addText(s.toString());
			
		   StringBuilder s1 = new StringBuilder();
		   if (modoConsulta=="si"){			   
			   s1.append("");    
		   }else{
			   s1.append("<img alt=\"Delete Imagen\" src=\"");
			   s1.append(themeDisplay.getPathThemeImages());
			   s1.append("/common/delete.png\" onClick=\"javascript:deleteImagenReclamoPrestacional('");
			   s1.append(String.valueOf(folderId));
			   s1.append("','");
			   s1.append(fileEntry.getName());
			   s1.append("','");
			   s1.append(solapa);
			   s1.append("');\" /> ");
		   }			 					
		   
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
<!--

-->

<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fmAI.<portlet:namespace />keywords);
	</script>
</c:if>

<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>





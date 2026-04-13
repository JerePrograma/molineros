<%@ include file="/html/portlet/portal_empleadores/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

	<%
					
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("reportes");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-reportes-were-found"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

					ResultRow row = new ResultRow(1, 1, 1);
				PortletURL rowURL = renderResponse.createRenderURL();		 				
				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL.setParameter("struts_action","/portal_empleadores/cuentas_corrientes");
				row.addText("Reporte DDJJ y pagos", rowURL);
				resultRows.add(row);
					
			
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

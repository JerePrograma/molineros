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
				rowURL.setParameter("struts_action","/estudio_isidro/reporte_llamados");
				row.addText("Reporte de Llamados Seguimiento", rowURL);
				resultRows.add(row);
				
				ResultRow row2 = new ResultRow(1, 1, 2);
				PortletURL rowURL2 = renderResponse.createRenderURL();		 				
				rowURL2.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL2.setParameter("struts_action","/estudio_isidro/busqueda_reporte_actas");
				row2.addText("Reporte de Actas", rowURL2);
				resultRows.add(row2);
				
				ResultRow row3 = new ResultRow(1, 1, 3);
				PortletURL rowURL3 = renderResponse.createRenderURL();		 				
				rowURL3.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL3.setParameter("struts_action","/estudio_isidro/busqueda_reporte_convenios");
				row3.addText("Reporte Convenios", rowURL3);
				resultRows.add(row3);
					
				ResultRow row4 = new ResultRow(1, 1, 4);
				PortletURL rowURL4 = renderResponse.createRenderURL();		 				
				rowURL4.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL4.setParameter("struts_action","/estudio_isidro/reporte_estadistico_seguimiento");
				row4.addText("Reporte Estadístico Seguimiento", rowURL4);
				resultRows.add(row4);
				
				ResultRow row5 = new ResultRow(1, 1, 5);
				PortletURL rowURL5 = renderResponse.createRenderURL();		 				
				rowURL5.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL5.setParameter("struts_action","/estudio_isidro/busqueda_reporte_actas_convenios_estadistico");
				row5.addText("Reporte de Actas-Convenio", rowURL5);
				resultRows.add(row5);
			
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

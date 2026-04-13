<%@ include file="/html/portlet/hoteles/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%
				
					/* boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES); */
	
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

		 			ResultRow row1 = new ResultRow(1, 1, 10);
					PortletURL rowURL1 = renderResponse.createRenderURL();		 				
					rowURL1.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL1.setParameter("struts_action","/hoteles/habitaciones_gobernanta");
					row1.addText("Reporte de Habitaciones para servicios", rowURL1);
					resultRows.add(row1);
					
					ResultRow row2 = new ResultRow(1, 1, 11);
					PortletURL rowURL2 = renderResponse.createRenderURL();		 				
					rowURL2.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL2.setParameter("struts_action","/hoteles/estadistica_desayunos");
					row2.addText("Estadística Desayunos", rowURL2);
					resultRows.add(row2);
					
					
		 			
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

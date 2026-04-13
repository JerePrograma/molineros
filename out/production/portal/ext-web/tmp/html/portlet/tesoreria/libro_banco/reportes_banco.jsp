<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

				<%
				
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);
					boolean showReportesBancos = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_REPORTES_BANCOS);				
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

	 				if (showReportesBancos){
		 				ResultRow row = new ResultRow(1, 1, 1);
						PortletURL rowURL = renderResponse.createRenderURL();		 				
						rowURL.setWindowState(WindowState.MAXIMIZED);		 				
						rowURL.setParameter("struts_action","/tesoreria/busqueda_reporte_acreditaciones_afip");
						row.addText("Lista Acreditaciones AFIP", rowURL);
			 			resultRows.add(row);
			 			
			 			ResultRow row2 = new ResultRow(1, 1, 2);
						PortletURL rowURL2 = renderResponse.createRenderURL();		 				
						rowURL2.setWindowState(WindowState.MAXIMIZED);		 				
						rowURL2.setParameter("struts_action","/tesoreria/busqueda_reporte_libro_banco");
						row2.addText("Libro Banco", rowURL2);
			 			resultRows.add(row2);
	 				}
		 			
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

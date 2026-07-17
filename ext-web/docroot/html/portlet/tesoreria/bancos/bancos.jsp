<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

	<%
		String portlet_name = ParamUtil.getString(request, "portlet_name");
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		} 
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		} 
					
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("operaciones-bancarias");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "nada"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

				ResultRow row = new ResultRow(1, 1, 1);
				PortletURL rowURL = renderResponse.createRenderURL();		 				
				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL.setParameter("struts_action","/"+portlet_name+"/buscar_movimiento_bcrio_inicial");
				row.addText("Movimientos Bancarios", rowURL);
					resultRows.add(row);
					
				ResultRow row2 = new ResultRow(1, 1, 2);
				PortletURL rowURL2 = renderResponse.createRenderURL();		 				
				rowURL2.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL2.setParameter("struts_action","/"+portlet_name+"/buscar_canje_cheques_propios_inicial");
				row2.addText("Canje de cheques propios", rowURL2);
					resultRows.add(row2);
			
				ResultRow row3 = new ResultRow(1, 1, 3);
				PortletURL rowURL3 = renderResponse.createRenderURL();
				rowURL3.setWindowState(WindowState.MAXIMIZED);
				rowURL3.setParameter("struts_action","/" + portlet_name + "/buscar_saldo_diario_cuentas_bancarias");
				rowURL3.setParameter("cmd", "inicial");
				row3.addText("Saldo diario cuentas bancarias para tablero de control", rowURL3);
				resultRows.add(row3);		
			
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
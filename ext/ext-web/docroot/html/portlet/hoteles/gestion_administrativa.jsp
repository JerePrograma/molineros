<%@ include file="/html/portlet/hoteles/init.jsp" %>


<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%		
				//Si debe mostrarse el btn de agregar afiliado									
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("gestion-adm");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
					
					/*
					boolean empleadores=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_EMPLEADORES);
					boolean ctacteActas=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_CTACTE_ACTAS_CONVENIOS_UOMA);
					boolean reportesGrales=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_GENERALES_UOMA);
					*/
					
				 	int total = 1;
					searchContainer.setTotal(total);
	 				List resultRows = searchContainer.getResultRows();
	 				
	 				
	 				ResultRow row3 = new ResultRow(1, 1, 1);
					PortletURL rowURL3 = renderResponse.createRenderURL();
					rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL3.setParameter("struts_action","/hoteles/recibos_gestion");
					row3.addText("Gestión de Recibos", rowURL3);
			 		resultRows.add(row3);
			 		
	 				
	 				ResultRow row4 = new ResultRow(1, 1, 1);
					PortletURL rowURL4 = renderResponse.createRenderURL();
					rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL4.setParameter("struts_action","/hoteles/resumen_estado_liquidacion_reservas");
					row4.addText("Resumen de Reservas", rowURL4);
			 		resultRows.add(row4);
	 				
	 				
	 				
	 			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

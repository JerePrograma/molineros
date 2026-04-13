<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

				<%
				
					request.getSession().removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO);
					request.getSession().removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS);
					
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);		
					boolean showAumentoNomenclador = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_AUMENTO_NOMENCLADOR);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("administracion-tablas");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
				
				 	int total = 1;
					searchContainer.setTotal(total);
	 				List resultRows = searchContainer.getResultRows();

/*	 				ResultRow row = new ResultRow(1, 1, 1);
					PortletURL rowURL = renderResponse.createRenderURL();
					rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL.setParameter("struts_action","/liquidaciones/prestadores");
					row.addText("Prestadores", rowURL);
		 			resultRows.add(row); */
		 			
/*		 					 					 			
		 			ResultRow row3 = new ResultRow(1, 1, 2);
					PortletURL rowURL3 = renderResponse.createRenderURL();
					rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL3.setParameter("struts_action","/liquidaciones/tratamientos_discapacidad");
					row3.addText("Tratamientos de Discapacidad", rowURL3);
		 			resultRows.add(row3);
*/		 			
		 			ResultRow row4 = new ResultRow(1, 1, 3);
					PortletURL rowURL4 = renderResponse.createRenderURL();
					rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL4.setParameter("struts_action","/liquidaciones/convenios_prestacionales");
					row4.addText("Convenios Prestacionales", rowURL4);
		 			resultRows.add(row4);
		 			
		 			/*ResultRow row5 = new ResultRow(1, 1, 4);
					PortletURL rowURL5 = renderResponse.createRenderURL();
					rowURL5.setWindowState(WindowState.MAXIMIZED);
					rowURL5.setParameter("struts_action","/liquidaciones/importar_contratos");
					row5.addText("Importar Contrato", rowURL5);
		 			resultRows.add(row5);*/
		 			
		 			if(showAumentoNomenclador) { 
		 			ResultRow row5 = new ResultRow(1, 1, 5);
					PortletURL rowURL5 = renderResponse.createRenderURL();
					rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);
					rowURL5.setParameter("struts_action","/liquidaciones/aumento_prestaciones");
					row5.addText("Aumento de Prestaciones", rowURL5);
		 			resultRows.add(row5);
		 			}

			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
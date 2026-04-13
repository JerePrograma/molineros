<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);				
					List<Empresa> empresasList= (ArrayList<Empresa>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_EMPLEADORES);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("cuit");
					 		headerNames.add("sucursal");
					 		headerNames.add("razon-social");
					headerNames.add("baja-fecha");
					if(showABMButtons) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-empresas-were-found"));
				
					if(null!=empresasList){
				 								 	
				 				//Seteo el total de la lista.
					 	int total = empresasList.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < empresasList.size(); i++) {
					 		Empresa empresa = (Empresa) empresasList.get(i);
				 					ResultRow row = new ResultRow(empresa, empresa.getCuit(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();		 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
					 				rowURL.setParameter("struts_action","/afiliados/view_empleadores_entry");
					 				rowURL.setParameter("cuit", empresa.getCuit());
					 				rowURL.setParameter("sucursal", empresa.getSucursal());
					 				row.addText(empresa.getCuit(), rowURL);
					 				row.addText(empresa.getSucursal(), rowURL);
					 				row.addText(empresa.getRazon_soc(), rowURL);
					 				row.addText(empresa.getBaja_fechaAsString(),rowURL);
							// Action
							if(showABMButtons) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/afiliados/empleadores/editar_borrar_empleador.jsp");
							}
				 			resultRows.add(row);
					 	}
				 			}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

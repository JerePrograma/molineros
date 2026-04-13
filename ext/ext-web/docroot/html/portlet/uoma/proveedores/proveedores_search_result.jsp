<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);				
					List<Proveedor> empresasList= (ArrayList<Proveedor>)session.getAttribute("PROVEEDORES_RESULT");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("cuit");
					 		headerNames.add("sucursal");
					 		headerNames.add("razon-social");
					if(showABMButtons) { 
						headerNames.add("edit");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-empresas-were-found"));
				
					if(null!=empresasList){
				 								 	
				 				//Seteo el total de la lista.
					 	int total = empresasList.size();
					 	searchContainer.setTotal(total);
				 				List resultRows = searchContainer.getResultRows();
				 				PortletURL rowURL = renderResponse.createRenderURL();
					 	for (int i = 0; i < empresasList.size(); i++) {
					 		Proveedor empresa = (Proveedor) empresasList.get(i);
					 				
							ResultRow row = new ResultRow(empresa, empresa.getId(), i);
							rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
							
							StringBuilder sb0 = new StringBuilder();
					 		sb0.append("<a href='javascript:editarProveedor(\"");
					 		sb0.append(empresa.getId());
					 		sb0.append("\",\"");
					 		sb0.append("V");
					 		sb0.append("\")'");
					 		sb0.append(">");
					 		sb0.append(empresa.getCuit());
					 		sb0.append("</a>");
					 		row.addText(sb0.toString());
							
					 		
					 		StringBuilder sb1 = new StringBuilder();
					 		sb1.append("<a href='javascript:editarProveedor(\"");
					 		sb1.append(empresa.getId());
					 		sb1.append("\",\"");
					 		sb1.append("V");
					 		sb1.append("\")'");
					 		sb1.append(">");
					 		sb1.append(empresa.getSucursal());
					 		sb1.append("</a>");
					 		row.addText(sb1.toString());
							
					 		
					 		StringBuilder sb2 = new StringBuilder();
					 	    sb2.append("<a href='javascript:editarProveedor(\"");
					 		sb2.append(empresa.getId());
					 		sb2.append("\",\"");
					 		sb2.append("V");
					 		sb2.append("\")'");
					 		sb2.append(">");
					 		sb2.append(empresa.getRazon_soc());
					 		sb2.append("</a>");
					 		row.addText(sb2.toString());
							
							
							
//					 		if(showABMButtons) {
					 			
								StringBuilder sb= new StringBuilder();
			 					sb.append("&nbsp;&nbsp;<img alt=\"Editar Proveedor\" src=\"");
			 			        sb.append(themeDisplay.getPathThemeImages());
			 	 		        sb.append("/common/edit.png\" onClick=\"javascript:editarProveedor('");
			 	 		        sb.append(empresa.getId() );
			 	 		        sb.append("','E'");
			 	 		        sb.append(");\"");
			 	                sb.append(" title=\"Editar\"");
			 	 		        sb.append("/>");
			 	 		        row.addText(sb.toString()); 
			 					
//							}
							
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	
	


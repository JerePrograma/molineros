<%@ include file="/html/portlet/empresas/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
			<%
					SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);				
					List<EntidadPadronUnificado> empresasList= (ArrayList<EntidadPadronUnificado>)renderRequest.getAttribute("PADRON_ENTIDADES");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("cuit");
					 		headerNames.add("sucursal");
					 		headerNames.add("razon-social");
					if(showABMButtons) { 
						headerNames.add("fecha-baja");
						headerNames.add("edit");
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
				 				PortletURL rowURL = renderResponse.createRenderURL();
					 	for (int i = 0; i < empresasList.size(); i++) {
					 		EntidadPadronUnificado empresa = (EntidadPadronUnificado) empresasList.get(i);
					 				
		 					ResultRow row = new ResultRow(empresa, empresa.getCuit(), i);
			 				rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 			
			 				if(empresa instanceof Seccional){
			 					rowURL.setParameter("struts_action","/empresas/editar_seccionales_entry");
				 				rowURL.setParameter("cuit", empresa.getCuit());
				 				rowURL.setParameter("id_seccional", String.valueOf(empresa.getIdSeccional()));
			 				}else{
				 				rowURL.setParameter("struts_action","/empresas/editar_empleadores_entry");
				 				rowURL.setParameter("cuit", empresa.getCuit());
				 				rowURL.setParameter("sucursal", empresa.getSucursal());
			 				}
			 				row.addText(empresa.getCuit(), rowURL);
			 				row.addText(empresa.getSucursal(), rowURL);
			 				row.addText(empresa.getDescripcion(), rowURL);					 				
							// Action
							if(showABMButtons) {
								PortletURL rowURL2 = renderResponse.createRenderURL();		 				
				 				rowURL2.setWindowState(LiferayWindowState.MAXIMIZED);		 	
				 				if(empresa instanceof Seccional){
				 					rowURL2.setParameter("struts_action","/empresas/editar_seccionales_entry");
					 				rowURL2.setParameter("cuit", empresa.getCuit());
					 				rowURL2.setParameter("id_seccional", String.valueOf(empresa.getIdSeccional()));				 					
				 				}else{
					 				rowURL2.setParameter("struts_action","/empresas/editar_empleadores_entry");
					 				rowURL2.setParameter("cuit", empresa.getCuit());
					 				rowURL2.setParameter("sucursal", empresa.getSucursal());
				 				}
								
				 				StringBuilder sb3= new StringBuilder();
				 				
				 				Empresa emp = null;
				 				
				 				try{
					 				emp = (Empresa) empresa;
									
									if (emp != null && emp.getBaja_fecha() != null){
										sb3.append(formatDate.format(emp.getBaja_fecha()) );	
									}
				 				}catch(Exception e){
				 					// nada porque es Seccional y no se dan de baja
				 				}
								
								row.addText(sb3.toString(), rowURL2);
								
								StringBuilder sb2= new StringBuilder();
								if ((emp != null && emp.getBaja_fecha() == null) || emp == null ){
									sb2.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
				 					sb2.append(themeDisplay.getPathThemeImages());
				 					sb2.append("/common/edit.png\" onClick=\"javascript:editarEmpresa('");			 					
				 					sb2.append(empresa.getCuit());
				 					sb2.append("','");
				 					sb2.append(empresa.getSucursal());
				 					sb2.append("');\" />");
				 					//row.addText(sb2.toString(), rowURL2);
								}else if((emp != null && emp.getBaja_fecha() != null)){
									sb2.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
				 					sb2.append(themeDisplay.getPathThemeImages());
				 					sb2.append("/message_boards/ban_user.png\" onClick=\"javascript:editarEmpresa('");			 					
				 					sb2.append(empresa.getCuit());
				 					sb2.append("','");
				 					sb2.append(empresa.getSucursal());
				 					sb2.append("');\" />");
								}
								row.addText(sb2.toString(), rowURL2);
							}
				 			resultRows.add(row);
					 	}
				 			}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />


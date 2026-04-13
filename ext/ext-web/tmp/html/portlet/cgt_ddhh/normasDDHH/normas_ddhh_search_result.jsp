<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
	 									
					//Si debe mostrarse el btn de agregar afiliado
					List<NormaDdHh> normasDH= (ArrayList<NormaDdHh>) portletSession.getAttribute(WebKeysCGT.BUSQUEDA_NORMASDDHH, PortletSession.APPLICATION_SCOPE);
										 								
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("id-norma");
			 		headerNames.add("sistema");
			 		headerNames.add("tipo");			 		
			 		headerNames.add("numero");
			 		headerNames.add("tema");
			 		headerNames.add("link");
			 		headerNames.add("sigla");
			 		headerNames.add("Editar/Baja");				 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-normas-were-found"));
				
					if(null!=normasDH){				 								 	
				 		
					 	searchContainer.setTotal(normasDH.size());
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < normasDH.size(); i++) {
					 		NormaDdHh norma = (NormaDdHh) normasDH.get(i);
				 					ResultRow row = new ResultRow(norma, norma.getId(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				StringBuilder sb = new StringBuilder();
									sb.append("<a href='javascript:verNormaDDHH(\"");
									sb.append(norma.getId());
									sb.append("\")'><p style=\"font-size:12px\">");			
									sb.append(norma.getId());
									sb.append("</p></a>");
									row.addText(sb.toString());
									StringBuilder sb0 = new StringBuilder();
									sb0.append("<a href='javascript:verNormaDDHH(\"");
									sb0.append(norma.getId());
									sb0.append("\")'><p style=\"font-size:12px\">");			
									sb0.append(norma.getSistema());
									sb0.append("</p></a>");
									row.addText(sb0.toString());
									StringBuilder sb1 = new StringBuilder();
									sb1.append("<a href='javascript:verNormaDDHH(\"");
									sb1.append(norma.getId());
									sb1.append("\")'><p style=\"font-size:12px\">");			
									sb1.append(norma.getTipo().getDescripcion());
									sb1.append("</p></a>");
									row.addText(sb1.toString());
									StringBuilder sb4 = new StringBuilder();
									sb4.append("<a href='javascript:verNormaDDHH(\"");
									sb4.append(norma.getId());
									sb4.append("\")'><p style=\"font-size:12px\">");			
									sb4.append(norma.getNumero());
									sb4.append("</p></a>");
									row.addText(sb4.toString());		
					 				StringBuilder sb2 = new StringBuilder();
									sb2.append("<a href='javascript:verNormaDDHH(\"");
									sb2.append(norma.getId());
									sb2.append("\")'><p style=\"font-size:12px\">");			
									sb2.append(norma.getTema().getDescripcion());
									sb2.append("</p></a>");			
									row.addText(sb2.toString());
					 				StringBuilder sb3 = new StringBuilder();
									sb3.append("<a href='javascript:verNormaDDHH(\"");
									sb3.append(norma.getId());
									sb3.append("\")'><p style=\"font-size:12px\">");			
									sb3.append(norma.getLink());
									sb3.append("</p></a>");				
									row.addText(sb3.toString());
									StringBuilder sb6 = new StringBuilder();
									sb6.append("<a href='javascript:verNormaDDHH(\"");
									sb6.append(norma.getId());
									sb6.append("\")'><p style=\"font-size:12px\">");			
									sb6.append(norma.getSigla());
									sb6.append("</p></a>");				
									row.addText(sb6.toString());
										
																	
									if(showABMButtons){
										StringBuilder sb5 = new StringBuilder();
										sb5.append("<center>");	
										sb5.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
										sb5.append(themeDisplay.getPathThemeImages());
										sb5.append("/common/edit.png\" onClick=\"verNormaDDHH('");
										sb5.append(String.valueOf(norma.getId()));									
										sb5.append("')\" />");
										sb5.append("&nbsp;/&nbsp;");
										sb5.append("<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
										sb5.append(themeDisplay.getPathThemeImages());
										sb5.append("/common/delete.png\" onClick=\"borrarNormaDDHH('");
										sb5.append(String.valueOf(norma.getId())) ;									
										sb5.append("')\" />");
										sb5.append("</center>");
										row.addText(sb5.toString());
									}
																						
									resultRows.add(row);
									

				 					
					 	}					 	
				 			
					 }
				 	
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	


	
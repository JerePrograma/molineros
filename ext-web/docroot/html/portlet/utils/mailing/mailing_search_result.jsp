<%@ include file="/html/portlet/utils/mailing/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
	 									
					//Si debe mostrarse el btn de agregar afiliado
					List<ListaDestinatarios> listas= (ArrayList<ListaDestinatarios>) portletSession.getAttribute(WebKeysGlobal.LISTAS_MAILING, PortletSession.APPLICATION_SCOPE);
														 								
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("id-lista");			 		
			 		headerNames.add("nombre");
			 		headerNames.add("usuario");
			 		headerNames.add("observaciones");
			 		if(showABMButtons){		 						 		
			 			headerNames.add("editar");
			 		}				 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-lista-mailing-were-found"));
				
					if(null!=listas){				 								 	
				 		
					 	searchContainer.setTotal(listas.size());
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < listas.size(); i++) {
					 		ListaDestinatarios lista = (ListaDestinatarios) listas.get(i);
				 					ResultRow row = new ResultRow(lista, lista.getIdListaDestinatarios(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				StringBuilder sb = new StringBuilder();
									sb.append("<a href='javascript:verLista(\"");
									sb.append(lista.getIdListaDestinatarios());
									sb.append("\")'><p style=\"font-size:12px\">");			
									sb.append(lista.getIdListaDestinatarios());
									sb.append("</p></a>");
									row.addText(sb.toString());
									StringBuilder sb0 = new StringBuilder();
									sb0.append("<a href='javascript:verLista(\"");
									sb0.append(lista.getIdListaDestinatarios());
									sb0.append("\")'><p style=\"font-size:12px\">");			
									sb0.append(lista.getNombre());
									sb0.append("</p></a>");
									row.addText(sb0.toString());
									StringBuilder sb2 = new StringBuilder();
									sb2.append("<a href='javascript:verLista(\"");
									sb2.append(lista.getIdListaDestinatarios());
									sb2.append("\")'><p style=\"font-size:12px\">");			
									sb2.append(lista.getAlta_user());
									sb2.append("</p></a>");
									row.addText(sb2.toString());		
									StringBuilder sb1 = new StringBuilder();
									sb1.append("<a href='javascript:verLista(\"");
									sb1.append(lista.getIdListaDestinatarios());
									sb1.append("\")'><p style=\"font-size:12px\">");			
									sb1.append(lista.getObservaciones());
									sb1.append("</p></a>");
									row.addText(sb1.toString());								
																										
									if(showABMButtons){
										StringBuilder sb5 = new StringBuilder();
										sb5.append("<center>");	
										sb5.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
										sb5.append(themeDisplay.getPathThemeImages());
										sb5.append("/common/edit.png\" onClick=\"verLista('");
										sb5.append(String.valueOf(lista.getIdListaDestinatarios()));									
										sb5.append("')\" />");
										sb5.append("</center>");
										row.addText(sb5.toString());
									}
									resultRows.add(row);
					 	}					 	
				 			
					 }
				 	
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
</form>
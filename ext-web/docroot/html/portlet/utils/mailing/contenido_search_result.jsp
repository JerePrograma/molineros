<%@ include file="/html/portlet/utils/mailing/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%	
					
				Boletin boletin = (Boletin)portletSession.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);
			
				List<Contenido> contenidos=null;
				if(null!=boletin){
					contenidos=boletin.getListaContenidos();
				}				
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		
		 		headerNames.add("seccion");
		 		headerNames.add("titulo");
		 		headerNames.add("contenido");		 		
		 		headerNames.add("borrar");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-contenidos-were-found"));			
			
				if(null!=contenidos){
	 				//Seteo el total de la lista.
				 	int total = contenidos.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < contenidos.size(); i++) {
				 		Contenido contenido = (Contenido) contenidos.get(i);
				 		ResultRow row = new ResultRow(contenido,contenido.getTitulo(), i);
				 		row.addText(contenido.getSeccion());				 				 				
			 			row.addText(contenido.getTitulo());
			 			row.addText(contenido.getContenido());
				 		StringBuilder sb= new StringBuilder();				 		
				 		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 		sb.append(themeDisplay.getPathThemeImages());
				 		sb.append("/common/delete.png\" onClick=\"javascript:borraContenido('");				 		
				 		sb.append(contenido.getTitulo());						 			
				 		sb.append("');\" />");
				 		row.addText(sb.toString());				 			
				 		resultRows.add(row);
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

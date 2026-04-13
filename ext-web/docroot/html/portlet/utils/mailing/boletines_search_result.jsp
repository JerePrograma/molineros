<%@ include file="/html/portlet/utils/mailing/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%	
				boolean from= Boolean.parseBoolean((String)renderRequest.getAttribute("fromBusqueda"));		
				List<Boletin> boletines = (List<Boletin>)portletSession.getAttribute(WebKeysGlobal.LISTA_BOLETINES);
										
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		
		 		headerNames.add("id");
		 		headerNames.add("nombre");
		 		headerNames.add("subject");
		 		headerNames.add("observaciones");
		 		headerNames.add("editar-borrar");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-contactos-were-found"));			
			
				if(null!=boletines){
	 				//Seteo el total de la lista.
				 	int total = boletines.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < boletines.size(); i++) {
				 		Boletin boletin = (Boletin) boletines.get(i);
				 		ResultRow row = new ResultRow(boletin,boletin.getIdBoletin(), i);
				 		row.addText(String.valueOf(boletin.getIdBoletin()));		 				
			 			row.addText(boletin.getNombre());
			 			row.addText(boletin.getAsunto());			 							 		
			 			row.addText(boletin.getObservaciones());
				 		StringBuilder sb= new StringBuilder();
				 		if(from){
					 		sb.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
					 		sb.append(themeDisplay.getPathThemeImages());
					 		sb.append("/common/edit.png\" onClick=\"javascript:editarBoletin('");
					 		sb.append(boletin.getIdBoletin());				 			
					 		sb.append("');\" /> / ");
						}					 		
				 		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 		sb.append(themeDisplay.getPathThemeImages());
				 		sb.append("/common/delete.png\" onClick=\"javascript:borraBoletin('");				 		
						sb.append(boletin.getIdBoletin());										 							 			
				 		sb.append("');\" />");
				 		row.addText(sb.toString());				 			
				 		resultRows.add(row);
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

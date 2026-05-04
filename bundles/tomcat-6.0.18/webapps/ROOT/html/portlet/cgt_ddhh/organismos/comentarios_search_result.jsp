<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%
				Organismo organismo = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
				Area area = (Area)portletSession.getAttribute(WebKeysCGT.AREA_EN_EDICION);				
				List<Comentario> comentarios=null;
				
				boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));
				if(!esArea){
					esArea= (Boolean)(renderRequest.getAttribute("esArea")!=null?renderRequest.getAttribute("esArea"):false);
				}
			
				if(esArea){
					if(area!=null&&area.getComentario()!=null){
						comentarios = area.getComentario();
					}				
				}else{
					if(organismo!=null&&organismo.getComentario()!=null){
						comentarios = organismo.getComentario();
					}
				}	
				
				
				
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();		 		
		 		headerNames.add("fecha");
		 		headerNames.add("comments");		 				 		
		 		headerNames.add("delete");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-comments-were-found"));			
			
				if(null!=comentarios){
	 				//Seteo el total de la lista.
				 	int total = comentarios.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < comentarios.size(); i++) {
				 		Comentario comentario = (Comentario) comentarios.get(i);
	 					ResultRow row = new ResultRow(comentario,i+1, i);
		 				row.addText(comentario.getFechaAsString());
		 				row.addText(comentario.getDescripcion());
		 				StringBuilder sb= new StringBuilder();
			 			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 			sb.append(themeDisplay.getPathThemeImages());
			 			sb.append("/common/delete.png\" onClick=\"javascript:borraComentario('");			 			
			 			sb.append(comentario.getFechaAsString());
			 			sb.append("','");
			 			sb.append(comentario.getDescripcion());
			 			sb.append("');\" />");
			 			row.addText(sb.toString());
			 			resultRows.add(row);
		 			 	
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

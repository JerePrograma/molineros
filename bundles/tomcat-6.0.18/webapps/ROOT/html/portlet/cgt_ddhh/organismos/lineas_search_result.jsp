<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%
				Organismo organismo = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);				
				Area area = (Area)portletSession.getAttribute(WebKeysCGT.AREA_EN_EDICION);				
				 
				List<LineaTrabajo> lineas=null;
				
				boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));
				if(!esArea){
					esArea= (Boolean)(renderRequest.getAttribute("esArea")!=null?renderRequest.getAttribute("esArea"):false);
				}
			
				if(esArea){
					if(area!=null&&area.getLineasTrabajo()!=null){
						lineas = area.getLineasTrabajo();
					}				
				}else{
					if(organismo!=null&&organismo.getLineasTrabajo()!=null){
						lineas = organismo.getLineasTrabajo();
					}
				}			
				
				
				
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("tipo");				 		
		 		headerNames.add("descripcion");		 				 		
		 		headerNames.add("delete");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-lineas-were-found"));			
			
				if(null!=lineas){
	 				//Seteo el total de la lista.
				 	int total = lineas.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < lineas.size(); i++) {
				 		LineaTrabajo linea = (LineaTrabajo) lineas.get(i);
	 					ResultRow row = new ResultRow(linea,linea.getId_linea(), i);
	 					row.addText(linea.getTipoLinea());
		 				row.addText(linea.getDescripcion());
		 				StringBuilder sb= new StringBuilder();
			 			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 			sb.append(themeDisplay.getPathThemeImages());
			 			sb.append("/common/delete.png\" onClick=\"javascript:borraLinea('");			 			
			 			sb.append(linea.getDescripcion());
			 			sb.append("');\" />");
			 			row.addText(sb.toString());
			 			resultRows.add(row);
		 			 	
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

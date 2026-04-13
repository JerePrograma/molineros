<%@ include file="/html/portlet/seccional/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%

				List<GestionSeccional> registrosList= (ArrayList<GestionSeccional>)renderRequest.getAttribute(WebKeysSeccionales.GESTIONES);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("fecha");
		 		headerNames.add("observacion");

				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA,portletURL, headerNames,
								
				LanguageUtil.get(pageContext, "no-gest-sec-were-found"));
							    
				if(null!=registrosList){
						
	 				//Seteo el total de la lista.
				 	int total = registrosList.size();
				 	searchContainer.setTotal(total);					 	
	 				List resultRows = searchContainer.getResultRows();
	 				
				 	for (int i = 0; i < registrosList.size(); i++) {						 		
				 		GestionSeccional registro = (GestionSeccional) registrosList.get(i);
				 		
	 					ResultRow row = new ResultRow(registro,registro.getId(), i);
	 					row.addText(registro.getAltaUsr().substring(0, 2) + " " + sdf.format(registro.getFecha()));
		 				row.addText(registro.getObservaciones());
	 																																									
			 			resultRows.add(row);
				 	}
	 			}
		%>


	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

	
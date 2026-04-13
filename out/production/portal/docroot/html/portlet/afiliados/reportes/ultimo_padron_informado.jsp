<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%	    			 				
												
					List<PadronInformado> padrones= (ArrayList<PadronInformado>)renderRequest.getAttribute("ultimoPadron");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("periodo");
			 		headerNames.add("tercerizadora-servicio");
			 		headerNames.add("tipo");
			 		headerNames.add("process-fecha");
			 		headerNames.add("print");
			 		headerNames.add("padron-capitas");					 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,100, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-padrones-were-found"));
				
					if(null!=padrones){
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < padrones.size(); i++) {
					 		PadronInformado padron = (PadronInformado) padrones.get(i);
				 					ResultRow row = new ResultRow(padron, padron.getTercerizadora(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 
					 				
					 				StringBuilder sb = new StringBuilder();									
									sb.append(padron.getPeriodoAsString());									
									row.addText(sb.toString());
					 				
									StringBuilder sb2 = new StringBuilder();									
									sb2.append(padron.getTercerizadora());									
									row.addText(sb2.toString());
					 				
									StringBuilder sb3 = new StringBuilder();												
									sb3.append(padron.getTipo());									
									row.addText(sb3.toString());
									resultRows.add(row);
									
									StringBuilder sb1 = new StringBuilder();									
									sb1.append(padron.getFechaAsString());									
									row.addText(sb1.toString());
									
									StringBuilder sb4= new StringBuilder();		 
									sb4.append("<img alt=\"<liferay-ui:message key='rendir'/>\" src=\"");
		 							sb4.append(themeDisplay.getPathThemeImages());
		 							sb4.append("/common/print.png\" onClick=\"javascript:imprimirListadoAnterior('");
				 					sb4.append(padron.getIdTerc());
				 					sb4.append("','");
				 					sb4.append(padron.getTipo());
				 					sb4.append("','");
				 					sb4.append(padron.getFechaAsString());	
				 					sb4.append("','");
				 					sb4.append(padron.getPeriodoAsString());	
				 					sb4.append("');\" /> ");
				 					row.addText(sb4.toString());
				 					
				 					StringBuilder sb5= new StringBuilder();		 
									sb5.append("<img alt=\"<liferay-ui:message key='rendir'/>\" src=\"");
		 							sb5.append(themeDisplay.getPathThemeImages());
		 							sb5.append("/document_library/ods.png\" onClick=\"javascript:imprimirListadoAnterior('");
				 					sb5.append(padron.getIdTerc());
				 					sb5.append("','");
				 					sb5.append(4);
				 					sb5.append("','");
				 					sb5.append(padron.getFechaAsString());				 						 					
				 					sb5.append("');\" /> ");
				 					row.addText(sb5.toString());	 						
					 	}
					 				
				 			
					 }
				 	
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
</form>


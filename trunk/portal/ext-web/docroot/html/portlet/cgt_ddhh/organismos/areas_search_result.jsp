<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = true;
	 									
					//Si debe mostrarse el btn de agregar afiliado
					Organismo organismo  = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			
					List<Area> areas=null;
					if(null!=organismo){
						areas=organismo.getAreas();
					}
																				 								
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("id-area");
			 		headerNames.add("nombre-area");			 		
			 		headerNames.add("telefono");
			 		headerNames.add("web");
			 		headerNames.add("lineas-trabajo");			 						 		
			 		headerNames.add("editar-area");				 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-areas-were-found"));
				
					if(null!=areas){				 								 	
				 		
					 	searchContainer.setTotal(areas.size());
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < areas.size(); i++) {
					 		Area area = (Area) areas.get(i);
				 					ResultRow row = new ResultRow(area, area.getId_area(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				StringBuilder sb3 = new StringBuilder();
									sb3.append("<a href='javascript:verArea(\"");
									sb3.append(area.getId_area());
									sb3.append("\")'>");			
									sb3.append(area.getId_area());
									sb3.append("</a>");
									row.addText(sb3.toString());
					 				StringBuilder sb = new StringBuilder();
									sb.append("<a href='javascript:verArea(\"");
									sb.append(area.getId_area());
									sb.append("\")'>");			
									sb.append(area.getNombre());
									sb.append("</a>");
									row.addText(sb.toString());
									StringBuilder sb0 = new StringBuilder();
									sb0.append("<a href='javascript:verArea(\"");
									sb0.append(area.getId_area());
									sb0.append("\")'>");			
									sb0.append(area.getTelefono());
									sb0.append("</a>");
									row.addText(sb0.toString());
									StringBuilder sb1 = new StringBuilder();
									sb1.append("<a href='javascript:verArea(\"");
									sb1.append(area.getId_area());
									sb1.append("\")'>");			
									sb1.append(area.getWeb());
									sb1.append("</a>");
									row.addText(sb1.toString());
									StringBuilder sb4 = new StringBuilder();
									sb4.append("<a href='javascript:verArea(\"");
									sb4.append(area.getId_area());
									sb4.append("\")'>");			
									sb4.append(area.getLineasString());
									sb4.append("</a>");
									row.addText(sb4.toString());		
					 																						
									StringBuilder sb5 = new StringBuilder();
									sb5.append("<center>");	
									sb5.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
									sb5.append(themeDisplay.getPathThemeImages());
									sb5.append("/common/edit.png\" onClick=\"verArea('");
									sb5.append(String.valueOf(area.getId_area()));									
									sb5.append("')\" />");									
									sb5.append("</center>");
									row.addText(sb5.toString());
									resultRows.add(row);
				 					
					 	}					 	
				 			
					 }
				 	
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
</form>


<script type="text/javascript">
	function verArea(id_area){		
		jQuery('#<portlet:namespace />id_area').val(id_area);
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/cgt_ddhh/buscar_area" />
		</portlet:renderURL>';
		document.<portlet:namespace />org.method = 'post';
		
		submitForm(document.<portlet:namespace />org, url);		
	}
</script>	
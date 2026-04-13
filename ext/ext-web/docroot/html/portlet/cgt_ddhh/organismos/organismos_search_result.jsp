<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
	 									
					//Si debe mostrarse el btn de agregar afiliado
					List<Organismo> organismos= (ArrayList<Organismo>) portletSession.getAttribute(WebKeysCGT.BUSQUEDA_ORGANISMOS, PortletSession.APPLICATION_SCOPE);
										 								
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("id-organismo");
			 		headerNames.add("sigla");
			 		headerNames.add("nombre");			 		
			 		headerNames.add("ambito");
			 		headerNames.add("telefono");
			 		headerNames.add("web");
			 		headerNames.add("lineas-trabajo");	
			 		if(showABMButtons){		 						 		
			 			headerNames.add("editar-agregar-area");
			 		}
			 		headerNames.add("choose");				 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-organismos-were-found"));
				
					if(null!=organismos){				 								 	
				 		
					 	searchContainer.setTotal(organismos.size());
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < organismos.size(); i++) {
					 		Organismo organismo = (Organismo) organismos.get(i);
				 					ResultRow row = new ResultRow(organismo, organismo.getId_organismo(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				StringBuilder sb = new StringBuilder();
									sb.append("<a href='javascript:verOrganismo(\"");
									sb.append(organismo.getId_organismo());
									sb.append("\")'><p style=\"font-size:12px\">");			
									sb.append(organismo.getId_organismo());
									sb.append("</p></a>");
									row.addText(sb.toString());
									StringBuilder sb0 = new StringBuilder();
									sb0.append("<a href='javascript:verOrganismo(\"");
									sb0.append(organismo.getId_organismo());
									sb0.append("\")'><p style=\"font-size:12px\">");			
									sb0.append(organismo.getSigla());
									sb0.append("</p></a>");
									row.addText(sb0.toString());
									StringBuilder sb1 = new StringBuilder();
									sb1.append("<a href='javascript:verOrganismo(\"");
									sb1.append(organismo.getId_organismo());
									sb1.append("\")'><p style=\"font-size:12px\">");			
									sb1.append(organismo.getNombre());
									sb1.append("</p></a>");
									row.addText(sb1.toString());
									StringBuilder sb4 = new StringBuilder();
									sb4.append("<a href='javascript:verOrganismo(\"");
									sb4.append(organismo.getId_organismo());
									sb4.append("\")'><p style=\"font-size:12px\">");			
									sb4.append(organismo.getAmbito());
									sb4.append("</p></a>");
									row.addText(sb4.toString());		
					 				StringBuilder sb2 = new StringBuilder();
									sb2.append("<a href='javascript:verOrganismo(\"");
									sb2.append(organismo.getId_organismo());
									sb2.append("\")'><p style=\"font-size:12px\">");			
									sb2.append(organismo.getTelefono());
									sb2.append("</p></a>");			
									row.addText(sb2.toString());
					 				StringBuilder sb3 = new StringBuilder();
									sb3.append("<a href='javascript:verOrganismo(\"");
									sb3.append(organismo.getId_organismo());
									sb3.append("\")'><p style=\"font-size:12px\">");			
									sb3.append(organismo.getWeb());
									sb3.append("</p></a>");				
									row.addText(sb3.toString());
									StringBuilder sb6 = new StringBuilder();
									sb6.append("<a href='javascript:verOrganismo(\"");
									sb6.append(organismo.getId_organismo());
									sb6.append("\")'><p style=\"font-size:12px\">");			
									sb6.append(organismo.getLineasString());
									sb6.append("</p></a>");				
									row.addText(sb6.toString());
										
									List<Area> areas=organismo.getAreas();
																	
									if(showABMButtons){
										StringBuilder sb5 = new StringBuilder();
										sb5.append("<center>");	
										sb5.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
										sb5.append(themeDisplay.getPathThemeImages());
										sb5.append("/common/edit.png\" onClick=\"verOrganismo('");
										sb5.append(String.valueOf(organismo.getId_organismo()));									
										sb5.append("')\" />");
										sb5.append("&nbsp;/&nbsp;");
										sb5.append("<img alt=\"<liferay-ui:message key='join'/>\" src=\"");
										sb5.append(themeDisplay.getPathThemeImages());
										sb5.append("/common/join.png\" onClick=\"agregarArea('");
										sb5.append(String.valueOf(organismo.getId_organismo()));									
										sb5.append("')\" />");
										
										if(null==areas || areas.size()==0){
											sb5.append("&nbsp;/&nbsp;");
											sb5.append("<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
											sb5.append(themeDisplay.getPathThemeImages());
											sb5.append("/common/delete.png\" onClick=\"borrarOrganismo('");
											sb5.append(String.valueOf(organismo.getId_organismo()));									
											sb5.append("')\" />");
										}
										sb5.append("</center>");
										row.addText(sb5.toString());
									}
															
									
									if(null!=areas && areas.size()>0){
										ResultRow rowTitulo = new ResultRow("TITULO", 1, 0);
										StringBuilder sbt0 = new StringBuilder();
										sbt0.append("&nbsp;");
										rowTitulo.addText(sbt0.toString());										
										rowTitulo.addText(sbt0.toString());										
										StringBuilder sbt = new StringBuilder();
										sbt.append("<p><b>Area</b></p>");
										rowTitulo.addText(sbt.toString());
										StringBuilder sbt1 = new StringBuilder();
										sbt1.append("&nbsp;");
										rowTitulo.addText(sbt1.toString());										
										StringBuilder sbt3 = new StringBuilder();
										sbt3.append("&nbsp;");
										rowTitulo.addText(sbt3.toString());
										StringBuilder sbt4 = new StringBuilder();
										sbt4.append("&nbsp;");
										rowTitulo.addText(sbt4.toString());
										resultRows.add(rowTitulo);
										StringBuilder sbt5 = new StringBuilder();
										sbt5.append("&nbsp;");
										rowTitulo.addText(sbt5.toString());
										rowTitulo.addText(sbt5.toString());											
									}
									for (int j = 0; j < areas.size(); j++) {
					 					Area area = (Area) areas.get(j);
				 						ResultRow rowArea = new ResultRow(area, area.getId_area(), j);
				 						StringBuilder sbi01 = new StringBuilder();
				 						sbi01.append("&nbsp;");
				 						rowArea.addText(sbi01.toString());
				 						rowArea.addText(sbi01.toString());
				 						StringBuilder sbi = new StringBuilder();
										sbi.append("<a href='javascript:verArea(\"");
										sbi.append(area.getId_area());
										sbi.append("\")'><p style=\"color:black;\">");			
										sbi.append(area.getNombre());
										sbi.append("</p></a>");
										rowArea.addText(sbi.toString());
										rowArea.addText(sbi01.toString());
										StringBuilder sbi1 = new StringBuilder();
										sbi1.append("<a href='javascript:verArea(\"");
										sbi1.append(area.getId_area());
										sbi1.append("\")'><p style=\"color:black;\">");			
										sbi1.append(area.getTelefono());
										sbi1.append("</p></a>");
										rowArea.addText(sbi1.toString());		
										rowArea.addText(sbi01.toString());								
										StringBuilder sbi3 = new StringBuilder();
										sbi3.append("<a href='javascript:verArea(\"");
										sbi3.append(area.getId_area());
										sbi3.append("\")'><p style=\"color:black;\">");			
										sbi3.append(area.getLineasString());
										sbi3.append("</p></a>");
										rowArea.addText(sbi3.toString());
										if(showABMButtons){
											StringBuilder sbi4 = new StringBuilder();
											sbi4.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
											sbi4.append(themeDisplay.getPathThemeImages());
											sbi4.append("/common/edit.png\" onClick=\"verArea('");
											sbi4.append(String.valueOf(area.getId_area()));									
											sbi4.append("')\" />/<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
											sbi4.append(themeDisplay.getPathThemeImages());
											sbi4.append("/common/delete.png\" onClick=\"borrarArea('");
											sbi4.append(String.valueOf(area.getId_area()));									
											sbi4.append("')\" />");										
											rowArea.addText(sbi4.toString());	
										}		
										
				 					}	
				 					StringBuilder sbi5 = new StringBuilder();
									sbi5.append("<input type=\"checkbox\"");
									sbi5.append("name=\"items\"");				
									sbi5.append("id=\"");
									sbi5.append("check-"+organismo.getId_organismo());
								    sbi5.append("\" value=\"");
									sbi5.append(organismo.getId_organismo());									
									sbi5.append("\"/>");				
									row.addText(sbi5.toString());								
									resultRows.add(row);
				 					
					 	}					 	
				 			
					 }
				 	
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
<c:if test="<%= organismos.size() > 0 %>">
		<table>
			<tr>
				<td align="left"><input type="button" value="<liferay-ui:message key="agregar-lista-correo" />" onClick="<portlet:namespace />generarLista('correo');" /></td>				
			</tr>
		</table>
</c:if>	

	
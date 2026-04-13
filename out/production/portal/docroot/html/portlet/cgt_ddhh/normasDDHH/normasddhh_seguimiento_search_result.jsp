<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
	 									
					//Si debe mostrarse el btn de agregar afiliado
				/* 	List<Empresa> empresas= (ArrayList<Empresa>) portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESAS_BUSCADAS,PortletSession.APPLICATION_SCOPE);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("cuit");
			 		headerNames.add("razon-social");
			 		headerNames.add("contacto");
			 		headerNames.add("telefono");
			 		headerNames.add("email");
			 		headerNames.add("estado");
			 		headerNames.add("molinera");
			 		headerNames.add("fecha-calculo-deuda");				 		
			 		headerNames.add("nuevo-llamado/edit");				 		
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-llamados-were-found"));
				
					if(null!=empresas){				 								 	
				 		
					 	searchContainer.setTotal(empresas.size());
					 	List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < empresas.size(); i++) {
					 		Empresa empresa = (Empresa) empresas.get(i);
				 					ResultRow row = new ResultRow(empresa, empresa.getCuit(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				StringBuilder sb = new StringBuilder();
									sb.append("<a href='javascript:buscarRestoPagina(\"");
									sb.append(String.valueOf(empresa.getCuit()));										
									sb.append("\",\"");
									sb.append(empresa.getRazon_soc().trim());
									sb.append("\")'>");			
									sb.append(empresa.getCuit());
									sb.append("</a>");
									row.addText(sb.toString());
									StringBuilder sb1 = new StringBuilder();
									sb1.append("<a href='javascript:buscarRestoPagina(\"");
									sb1.append(String.valueOf(empresa.getCuit()));										
									sb1.append("\",\"");
									sb1.append(empresa.getRazon_soc().trim());
									sb1.append("\")'>");				
									sb1.append(empresa.getRazon_soc().trim());
									sb1.append("</a>");
									row.addText(sb1.toString());
									StringBuilder sb4 = new StringBuilder();
									sb4.append("<a href='javascript:buscarRestoPagina(\"");
									sb4.append(String.valueOf(empresa.getCuit()));										
									sb4.append("\",\"");
									sb4.append(empresa.getRazon_soc().trim());
									sb4.append("\")'>");		
									if(	null!=empresa && null!= empresa.getContacto()){
										sb4.append(empresa.getContacto().trim());
									}else{
									    sb4.append("");
									}
									sb4.append("</a>");				
									row.addText(sb4.toString());		
					 				StringBuilder sb2 = new StringBuilder();
									sb2.append("<a href='javascript:buscarRestoPagina(\"");
									sb2.append(String.valueOf(empresa.getCuit()));										
									sb2.append("\",\"");
									sb2.append(empresa.getRazon_soc().trim());
									sb2.append("\")'>");					
									sb2.append(empresa.getTelefonosAsString().trim());
									sb2.append("</a>");			
									row.addText(sb2.toString());
					 				StringBuilder sb3 = new StringBuilder();
									sb3.append("<a href='javascript:buscarRestoPagina(\"");
									sb3.append(String.valueOf(empresa.getCuit()));										
									sb3.append("\",\"");
									sb3.append(empresa.getRazon_soc().trim());
									sb3.append("\")'>");						
									sb3.append(empresa.getEmailAsString().trim());
									sb3.append("</a>");				
									row.addText(sb3.toString());
									StringBuilder sb6 = new StringBuilder();
									sb6.append("<a href='javascript:buscarRestoPagina(\"");									
									sb6.append(String.valueOf(empresa.getCuit()));										
									sb6.append("\",\"");
									sb6.append(empresa.getRazon_soc().trim());
									sb6.append("\")'>");	
									if(empresa!=null && null!= empresa.getEstado()) { 		
										sb6.append(empresa.getEstado().trim());
									}else{
										sb6.append("");
									}
									sb6.append("</a>");				
									row.addText(sb6.toString());
									
									StringBuilder sb8 = new StringBuilder();
									sb8.append("<a href='javascript:buscarRestoPagina(\"");									
									sb8.append(String.valueOf(empresa.getCuit()));										
									sb8.append("\",\"");
									sb8.append(empresa.getRazon_soc().trim());
									sb8.append("\")'>");	
									if(empresa!=null && empresa.isMolinera()) { 		
										sb8.append("SI");
									}else{
										sb8.append("NO");
									}
									sb8.append("</a>");				
									row.addText(sb8.toString());
									
									StringBuilder sb7 = new StringBuilder();
									sb7.append("<a href='javascript:buscarRestoPagina(\"");									
									sb7.append(String.valueOf(empresa.getCuit()));										
									sb7.append("\",\"");
									sb7.append(empresa.getRazon_soc().trim());
									sb7.append("\")'>");
									sb7.append(empresa.getFechaUltimoCalculoDeudaAsString());
									sb7.append("</a>");				
									row.addText(sb7.toString());
																		
									StringBuilder sb5 = new StringBuilder();
									sb5.append("<center><img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
									sb5.append(themeDisplay.getPathThemeImages());
									sb5.append("/common/telephone.png\" onClick=\"buscarRestoPagina('");
									sb5.append(String.valueOf(empresa.getCuit()));
									sb5.append("','");
									sb5.append(empresa.getRazon_soc().trim());
									sb5.append("')\" />");	
									sb5.append("/<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
									sb5.append(themeDisplay.getPathThemeImages());
									sb5.append("/common/edit.png\" onClick=\"javascript:verInfoEmpresa('");
									sb5.append(empresa.getCuit());
									sb5.append("',");
									sb5.append("'000')\" /></center>");
									row.addText(sb5.toString());							
									resultRows.add(row);
					 	}					 	
				 			
					 } */
				 	
			%>
	
</form>
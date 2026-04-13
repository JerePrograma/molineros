<%@ include file="/html/portlet/cai/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
			<%
					boolean showCAI = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_VER_PORTLET_CAI );

					List<EntidadPadronUnificado> empresasList= (ArrayList<EntidadPadronUnificado>)renderRequest.getAttribute("PADRON_ENTIDADES");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cuit");
			 		headerNames.add("sucursal");
			 		headerNames.add("razon-social");
					headerNames.add("contacto");
									
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-empresas-were-found"));
				
					if(null!=empresasList){
				 								 	
				 		//Seteo el total de la lista.
					 	int total = empresasList.size();
					 	searchContainer.setTotal(total);
					 	
				 		List resultRows = searchContainer.getResultRows();
				 		PortletURL rowURL = renderResponse.createRenderURL();
				 		
					 	for (int i = 0; i < empresasList.size(); i++) {
					 		EntidadPadronUnificado empresa = (EntidadPadronUnificado) empresasList.get(i);
					 				
		 					ResultRow row = new ResultRow(empresa, empresa.getCuit(), i);
			 				rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 			
			 				
			 				row.addText(empresa.getCuit(), rowURL);
			 				row.addText(empresa.getSucursal(), rowURL);
			 				row.addText(empresa.getDescripcion(), rowURL);					 				
							
			 			// Action
							if( showCAI) {
								StringBuilder sb = new StringBuilder();
								sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Nuevo Contacto\" src=\"");
							        sb.append(themeDisplay.getPathThemeImages());
					 		        sb.append("/common/telephone.png\" onClick=\"javascript:nuevoCrmContactoEmpresa('");
					 		        sb.append(empresa.getCuit() );
					 		        sb.append("','");
					 		        sb.append(empresa.getSucursal() );
					 		        sb.append("');\"");
				                    sb.append(" title=\"Nuevo\"");
					 		        sb.append("/>");
					 		    row.addText(sb.toString());
								
							}
							
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />


<script type="text/javascript">
function nuevoCrmContactoEmpresa(cuit,sucursal) {
<%-- 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/cai/editar_contacto_entry';  
		url=url+'&idContacto='+idContacto;
		url=url+'&cmd=add&contactoSeccional=true'; --%>
		
		var strutsUrl = '/cai/editar_contacto_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
		'<liferay-portlet:param name="contactoEmpresa" value="true"/>'+
		'<liferay-portlet:param name="cuit" value="__cuit"/>'+
		'<liferay-portlet:param name="sucursal" value="__sucursal"/>'+
	    '</liferay-portlet:renderURL>';
	    
	    url = url.replace("__strutsUrl",strutsUrl);
	    url = url.replace("__cuit",cuit);
	    url = url.replace("__sucursal",sucursal);
	    
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

</script>	

<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%				
				Organismo organismo = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);				
				Area area = (Area)portletSession.getAttribute(WebKeysCGT.AREA_EN_EDICION);
				List<Contacto> contactos=null;
				boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));
				
				if(!esArea){
					esArea= (Boolean)(renderRequest.getAttribute("esArea")!=null?renderRequest.getAttribute("esArea"):false);
				}			
				
				if(esArea){
					if(area!=null&&area.getContactos()!=null){
						contactos = area.getContactos();
					}				
				}else{
					if(organismo!=null&&organismo.getContactos()!=null){
						contactos = organismo.getContactos();
					}
				}
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("cargo");
		 		headerNames.add("tratamiento");
		 		headerNames.add("nombre");
		 		headerNames.add("apellido");
		 		headerNames.add("telefono");
		 		headerNames.add("email");
		 		headerNames.add("delete");		 			 		
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-contactos-were-found"));			
			
				if(null!=contactos){
	 				//Seteo el total de la lista.
				 	int total = contactos.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < contactos.size(); i++) {
				 		Contacto contacto = (Contacto) contactos.get(i);
	 					ResultRow row = new ResultRow(contacto,contacto.getCargo(), i);
		 				row.addText(contacto.getCargo());
		 				row.addText(contacto.getTratamiento());
		 				row.addText(contacto.getNombre());			 							 		
		 				row.addText(contacto.getApellido());
		 				row.addText(contacto.getTelefono());
		 				row.addText(contacto.getEmail());		 				
			 			StringBuilder sb= new StringBuilder();
			 			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 			sb.append(themeDisplay.getPathThemeImages());
			 			sb.append("/common/delete.png\" onClick=\"javascript:borraContacto('");			 			
			 			sb.append(contacto.getCargo());
			 			sb.append("','");
			 			sb.append(contacto.getNombre());
			 			sb.append("','");
			 			sb.append(contacto.getApellido());
			 			sb.append("');\" />");
			 			row.addText(sb.toString());
			 			resultRows.add(row);
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

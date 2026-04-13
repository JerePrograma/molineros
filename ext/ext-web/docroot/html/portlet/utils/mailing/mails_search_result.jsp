<%@ include file="/html/portlet/utils/mailing/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%	
				boolean from= Boolean.parseBoolean((String)renderRequest.getAttribute("fromBusqueda"));		
				ListaDestinatarios listaD = (ListaDestinatarios)portletSession.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);
			
				List<Destinatario> contactos=null;
				if(null!=listaD){
					contactos=listaD.getListaDestinatarios();
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		
		 		headerNames.add("id");
		 		headerNames.add("tratamiento");
		 		headerNames.add("nombre");
		 		headerNames.add("apellido");		 		
		 		headerNames.add("email");
		 		headerNames.add("editar-borrar");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-contactos-were-found"));			
			
				if(null!=contactos){
	 				//Seteo el total de la lista.
				 	int total = contactos.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < contactos.size(); i++) {
				 		Destinatario contacto = (Destinatario) contactos.get(i);
				 		ResultRow row = new ResultRow(contacto,contacto.getTitle(), i);
				 		row.addText(String.valueOf(contacto.getIdDestinatario()));		 				
			 			row.addText(contacto.getTitle()!=null?contacto.getTitle():"");
			 			row.addText(contacto.getFirstname());			 							 		
			 			row.addText(contacto.getLastname()!=null?contacto.getLastname():"");		 				
			 			row.addText(contacto.getEmail());		 				
				 		StringBuilder sb= new StringBuilder();
				 		if(from){
					 		sb.append("<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
					 		sb.append(themeDisplay.getPathThemeImages());
					 		sb.append("/common/edit.png\" onClick=\"javascript:editarDestinatario('");
					 		sb.append(contacto.getIdDestinatario());				 			
					 		sb.append("');\" /> / ");
						}					 		
				 		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 		sb.append(themeDisplay.getPathThemeImages());
				 		sb.append("/common/delete.png\" onClick=\"javascript:borraContacto('");
				 		if(!from){
				 			sb.append(contacto.getEmail());
						}else{
							sb.append(contacto.getIdDestinatario());
						}				 							 			
				 		sb.append("');\" />");
				 		row.addText(sb.toString());				 			
				 		resultRows.add(row);
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

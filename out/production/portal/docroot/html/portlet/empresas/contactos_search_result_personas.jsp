<%@ include file="/html/portlet/empresas/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%				
				
				EntidadPadronUnificado empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
				
			    if(null==empresa){
					LlamadosEstudio llest = null;
					try{
						llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
					}catch(Exception e){}
					
					empresa= llest!=null?llest.getEmpresa():null;
				}
								  
				List<Contacto> contactos=null;
				
				
				if(null!=empresa){
					/* contactos=empresa.getContactos(); */
					  contactos=empresa!=null?empresa.getContactosPorNombreApePersonas():new ArrayList<Contacto>() ;
				}
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("nombre");
		 		headerNames.add("profesion");
		 		headerNames.add("cargo");
		 		headerNames.add("tipo");
		 		headerNames.add("contacto");
		 		headerNames.add("observaciones");		 		
		 		headerNames.add("delete");		 			 		
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-contactos-were-found"));			
				int cuenta_email=0;
				if(null!=contactos){
	 				//Seteo el total de la lista.
				 	int total = contactos.size();
				 	searchContainer.setTotal(total);				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < contactos.size(); i++) {
				 		Contacto contacto = (Contacto) contactos.get(i);
				 		
				 		StringBuilder sb= new StringBuilder();
				 		
				 		ResultRow row = new ResultRow(contacto,contacto.getTipoAsString(), i);
	 					row.addText(null!=contacto.getNombreApe()?contacto.getNombreApe():"");
	 					row.addText(null!=contacto.getProfesion()?contacto.getProfesion():"");
		 				row.addText(null!=contacto.getCargo()?contacto.getCargo():"");
		 				row.addText(contacto.getTipoAsString());
		 				row.addText(contacto.getContactoAsString());
		 				row.addText(null!=contacto.getObservaciones()?contacto.getObservaciones():"");
		 				if(contacto.getTipoAsString().equals("EMAIL")){
		 					cuenta_email++;
		 				}
		 				
				 		if(null==contacto.getBajaFecha() || 
				 				(contacto.getEstado()!=null && !contacto.getEstado().equals(Contacto.ESTADOS.BAJA) ) ){
		 					
			 				if(empresa instanceof Empresa){
					 			
					 			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
					 			sb.append(themeDisplay.getPathThemeImages());
					 			sb.append("/common/delete.png\" onClick=\"javascript:borraContactoP('");
					 			sb.append(contacto.getTipoAsString());
					 			sb.append("','");
					 			sb.append(contacto.getContactoAsString());
					 			sb.append("','");
					 			sb.append(contacto.getIdContacto());
					 			sb.append("','");
					 			sb.append(contacto.getNombreApe());
					 			sb.append("');\" />");
					 			sb.append("/");
					 			sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
					 			sb.append(themeDisplay.getPathThemeImages());
					 			sb.append("/common/edit.png\" onClick=\"javascript:editarContactoP('");
					 			sb.append(contacto.getTipoAsString());
					 			sb.append("','");
					 			sb.append(contacto.getCargo()!=null?contacto.getCargo():"");
					 			sb.append("','");
					 			sb.append(contacto.getProfesion()!=null?contacto.getProfesion():"");
					 			sb.append("','");
					 			sb.append(contacto.getNombreApe());
					 			sb.append("','");
					 			sb.append(contacto.getContactoAsString());
					 			sb.append("','");
					 			sb.append(contacto.getObservaciones());
					 			sb.append("','");
					 			sb.append(contacto.getTelefono()!=null?contacto.getTelefono().getCodigoArea():"");
					 			sb.append("','");
					 			sb.append(contacto.getTelefono()!=null?contacto.getTelefono().getNumero():"");
					 			sb.append("','");
					 			sb.append(contacto.getTelefono()!=null?contacto.getTelefono().getExtension():"");
					 			sb.append("','");
					 			sb.append(contacto.getIdContacto());
					 			sb.append("');\" />");
					 			row.addText(sb.toString());
			 				}else{
			 					row.addText("");
			 				}
				 			
				 		}else{
				  			sb.append("&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
				  			row.addText(sb.toString());
				 		}
				 		resultRows.add(row);
				 		
				 	}
				 	
	 			}
				if(cuenta_email==0){%>
	 				<input type="hidden" name="<portlet:namespace />tiene_email" id="<portlet:namespace />tiene_email" value="false"/>
	 			<%}else{%>
	 				<input type="hidden" name="<portlet:namespace />tiene_email" id="<portlet:namespace />tiene_email" value="true"/>
	 			<%}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	

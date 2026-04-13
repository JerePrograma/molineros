<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<portlet:defineObjects/>
			<% 
			boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);
			Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
				esEdicion = true;
			}

			//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
			esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
			
			if (acta != null && acta.isActaCerrada()){
				esEdicion = false;
			}
			
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
				List<InspectorWrapper> inspectores= (ArrayList<InspectorWrapper>)request.getSession().getAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("Inspector");		 		
				if(showABMButtons && esEdicion) { 
					headerNamesTercerizadora.add("Borrar");
				}				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-inspectores-were-found"));
			
				if(null!=inspectores){
					int total=inspectores.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < inspectores.size(); i++) {
	 			 		if (!inspectores.get(i).isBorradoLogico()){
		 					ResultRow rowInspector = new ResultRow(inspectores.get(i),inspectores.get(i).getNombre(), i);			
		 					rowInspector.addText(inspectores.get(i).getNombre());	 					
		 					resultRowsInspector.add(rowInspector);
		 					if (showABMButtons & esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraInspector('");
			 					sb.append(inspectores.get(i).getId());
			 					sb.append("');\" />");
			 					rowInspector.addText(sb.toString());
			 			 	}
	 			 		} else {
	 			 			total--;
	 			 		}
	 			 	}
	 				searchContainer.setTotal(total);
		 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainer%>" />

		

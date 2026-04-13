<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<% 
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
				String view=request.getParameter("view");
				Afiliado afiliadoEnSession = (Afiliado)request.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
				if (afiliadoEnSession == null) {
					afiliadoEnSession = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
				}
				List<Afiliado> afiliadosList= BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(afiliadoEnSession.getCuil_titular());
				PortletURL portletURL = renderResponse.createRenderURL();				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("apellido");
				headerNames.add("nombre");
				headerNames.add("paren");				
				StringBuilder sbH= new StringBuilder();
 				sbH.append("<img alt=\"<liferay-ui:message key='editar'/>\" align=\"right\" src=\"");
					sbH.append(themeDisplay.getPathThemeImages());
					sbH.append("/common/close.png\"");
					sbH.append (" onClick=\"javascript:hideGrupoFliar();\""); 					
				    sbH.append("/>");
				headerNames.add(sbH.toString());
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
				if(null!=afiliadosList){
				 	
	 				//Seteo el total de la lista.
				 	int total = afiliadosList.size();
				 	searchContainer.setTotal(total);
				 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < afiliadosList.size(); i++) {
				 		Afiliado afiliado = (Afiliado) afiliadosList.get(i);
	 					ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);
		 				PortletURL rowURL = renderResponse.createRenderURL();		 				
		 				rowURL.setWindowState(WindowState.MAXIMIZED);
		 				if (afiliadoEnSession.getInte() == afiliado.getInte()){
		 					row.addText(afiliado.getApellido());
							row.addText(afiliado.getNombre());						
							row.addText(afiliado.getParentesco());
		 				}else{
		 					rowURL.setParameter("struts_action","/afiliados/view_afiliado_entry");
			 				rowURL.setParameter("cuil_titular", afiliado.getCuil_titular());
			 				rowURL.setParameter("inte", afiliado.getInteAsString());
			 				row.addText(afiliado.getApellido(),rowURL);
							row.addText(afiliado.getNombre(),rowURL);						
							row.addText(afiliado.getParentesco(),rowURL);
		 				}
		 				
						if(showABMButtons && (null==view || !view.trim().equals("true"))) {
							row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/afiliados/editar_borrar_afiliado.jsp");
						}else{
							row.addText("");
						}
			 			resultRows.add(row);
				 	}
	 			}
	 	
 		%>
	<div>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	</div>
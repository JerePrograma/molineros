<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);				
					List<Prestador> prestadores= (ArrayList<Prestador>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("cod-prestador");
					 		headerNames.add("cuit");
					 		headerNames.add("descripcion");
					 		headerNames.add("tipo");
					 		headerNames.add("Cod.Hospital");
							headerNames.add("baja-fecha");							
					if(showABMButtons) {
						headerNames.add("editar-borrar");
					}
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-prestadores-were-found"));
				
					if(null!=prestadores){				 								 
				 				//Seteo el total de la lista.
					 	int total = prestadores.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < prestadores.size(); i++) {
					 		Prestador prestador = (Prestador) prestadores.get(i);
				 					ResultRow row = new ResultRow(prestador, prestador.getId_prestador(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();		 				
					 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					 				rowURL.setParameter("struts_action","/prestadores/editar_prestadores_entry");
					 				rowURL.setParameter("prestador_id", String.valueOf(prestador.getId_prestador()));
					 				rowURL.setParameter("cmd","view");
					 				row.addText(prestador.getId_prestadorString(), rowURL);
					 				row.addText(prestador.getCuit(), rowURL);
					 				row.addText(prestador.getDescripcion(),rowURL);
					 				row.addText(prestador.getTipo().getDescripcion(),rowURL);
					 				row.addText(prestador.getCodigoHospital(),rowURL);
					 				row.addText(prestador.getBaja_fechaAsString(),rowURL);
							// Action
							if(showABMButtons) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/administracion/prestadores/editar_borrar_prestador.jsp");
							}
				 			resultRows.add(row);
					 	}
				 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%@ include file="/html/portlet/cai/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
					boolean showCAI = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_VER_PORTLET_CAI );
			
					List<Prestador> prestadores= (ArrayList<Prestador>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES);
			
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("cod-prestador");
					 		headerNames.add("cuit");
					 		headerNames.add("descripcion");
					 		headerNames.add("tipo");
							headerNames.add("baja-fecha");							
					
					if( showCAI) {
			 			headerNames.add("Contacto");
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
					 				/* rowURL.setParameter("struts_action","/liquidaciones/editar_prestadores_entry");
					 				rowURL.setParameter("prestador_id", String.valueOf(prestador.getId_prestador()));
					 				rowURL.setParameter("cmd","view"); */
					 				row.addText(prestador.getId_prestadorString(), rowURL);
					 				row.addText(prestador.getCuit(), rowURL);
					 				row.addText(prestador.getDescripcion(),rowURL);
					 				row.addText(prestador.getTipo().getDescripcion(),rowURL);					 				
					 				row.addText(prestador.getBaja_fechaAsString(),rowURL);
							// Action
							if( showCAI) {
								StringBuilder sb = new StringBuilder();
								sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Nuevo Contacto\" src=\"");
							        sb.append(themeDisplay.getPathThemeImages());
					 		        sb.append("/common/telephone.png\" onClick=\"javascript:nuevoCrmContactoPrestador('");
					 		        sb.append(prestador.getId_prestador() );
					 		        sb.append("');\"");
				                    sb.append(" title=\"Nuevo\"");
					 		        sb.append("/>");
					 		    row.addText(sb.toString());
								
							}
				 			resultRows.add(row);
					 	}
				 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	
	
	
	
<script type="text/javascript">
function nuevoCrmContactoPrestador(idPrestador) {
<%-- 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/cai/editar_contacto_entry';  
		url=url+'&idContacto='+idContacto;
		url=url+'&cmd=add&contactoSeccional=true'; --%>
		
		var strutsUrl = '/cai/editar_contacto_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
		'<liferay-portlet:param name="contactoPrestador" value="true"/>'+
		'<liferay-portlet:param name="idPrestador" value="__idPrestador"/>'+
	    '</liferay-portlet:renderURL>';
	    
	    url = url.replace("__strutsUrl",strutsUrl);
	    url = url.replace("__idPrestador",idPrestador);
	    
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

</script>	
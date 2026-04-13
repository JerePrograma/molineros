<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_DISCAPACIDAD);				
				
				List<AutorizacionPrestacional> tratamientosList = new ArrayList<AutorizacionPrestacional> ();
				tratamientosList= (ArrayList<AutorizacionPrestacional>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
				if (tratamientosList == null || tratamientosList.size() == 0) {
					tratamientosList = (ArrayList<AutorizacionPrestacional>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Nro");
		 		headerNames.add("Emisión");
		 		headerNames.add("afiliado");
		 		headerNames.add("inte");
		 		headerNames.add("Nro. Afiliado");
		 		headerNames.add("codigo");
		 		headerNames.add("prestacion");
		 		headerNames.add("cantidad");
		 		headerNames.add("importe");
		 		headerNames.add("periodicidad");
		 		headerNames.add("periodo-desde");
		 		headerNames.add("periodo-hasta");
		 		headerNames.add("estado");
		 			
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-tratamientos-were-found"));
			    
				//Seteo el total de la lista.	
			 	int total = 0;
			 	if(null!=tratamientosList){
			 	   total = tratamientosList.size();
			 	}   
			 	searchContainer.setTotal(total);
							    
				if(null!=tratamientosList){
		 			
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < tratamientosList.size(); i++) {
					 		AutorizacionPrestacional tratamiento = (AutorizacionPrestacional) tratamientosList.get(i);
		 					ResultRow row = new ResultRow(tratamiento,tratamiento.getId_tratamiento(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);
			 					
			 				row.addText(tratamiento.getNroAutorizacion().toString());
			 				row.addText(tratamiento.getAlta_fechaAsString());
			 				
			 				row.addText(tratamiento.getAfiliado().getApeNombre());
			 				row.addText(tratamiento.getAfiliado().getInteAsString());
			 				row.addText(String.valueOf(tratamiento.getAfiliado().getId_ospim()));			 					 				
			 				row.addText(tratamiento.getPrestacion().getCodigo());
			 				row.addText(tratamiento.getPrestacion().getDescripcion());
			 				row.addText(tratamiento.getCantidad() != null ? tratamiento.getCantidad().toString() : "");
			 				row.addText(tratamiento.getImporte_total() != null ? tratamiento.getImporte_total().toString(): "");
			 				row.addText(tratamiento.getPeriodicidad() != null ? tratamiento.getPeriodicidad() : "");
			 				row.addText(tratamiento.getPeriodo_desde() != null ? tratamiento.getPeriodoDesdeString() : "");
			 				row.addText(tratamiento.getPeriodo_hasta() != null ? tratamiento.getPeriodoHastaString() : "");
			 				
			 				row.addText((tratamiento.getEstado() == 0 ? "Pre Autorización" : tratamiento.getEstado() == 1 ? "En Curso" : tratamiento.getEstado() == 2 ?"Documentación Faltante": tratamiento.getEstado() == 3 ?
			 						"Cambio Prestador" : tratamiento.getEstado() == 4 ? "Finalizado" : tratamiento.getEstado() == 5 ? "Abandonado" :tratamiento.getEstado() == 6 ? "Rechazado" : tratamiento.getEstado()==7?"Monotributo":"") +
			 						(tratamiento.getMotivoEstado()!=null?"<br>" +tratamiento.getMotivoEstado():""));
			 				
							// Action
							StringBuilder sb= new StringBuilder();
							sb.append("<a href='javascript:;' onClick=\"javascript:verTratamiento('");			 								 				
				 			sb.append(tratamiento.getId_String());
				 			sb.append("');\" >");
				 			sb.append("<img alt=\"Ver Detalle\" src=\"");
				 			sb.append(themeDisplay.getPathThemeImages());
				 			sb.append("/common/view.png\"");
				 			sb.append("/>");
				 			sb.append("</a>");				 			
				 			
							if(showABMButtons) {	
			 					if (tratamiento.getBaja_fecha() == null) {
			 					sb.append("&nbsp;|&nbsp;");
			 					sb.append("<a href='javascript:;' onClick=\"javascript:editarTratamiento('");			 								 				
			 					sb.append(tratamiento.getId_String());
			 					sb.append("','");
			 					sb.append(tratamiento.getAfiliado().getCuil_titular());
			 					sb.append("','");
			 					sb.append(tratamiento.getAfiliado().getInteAsString());
			 					sb.append("');\" >");
			 					sb.append("<img alt=\"<liferay-ui:message key='action.EDIT'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/edit.png\"");
			 					sb.append("/>");
			 					sb.append("</a>");
			 					sb.append("&nbsp;|&nbsp;");
			 					sb.append("<a href='javascript:;' onClick=\"javascript:borrarTratamiento('");			 					
			 					sb.append(tratamiento.getId_String());
			 					sb.append("');\" >");
			 					sb.append("<img alt=\"<liferay-ui:message key='elilminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\"");
			 					sb.append("/>");
			 					sb.append("</a>");
			 					}
			 					else {
			 						sb.append("<a href='javascript:;' >");
				 					sb.append("<img alt=\"Baja\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/common/close.png\"");
				 					sb.append("/>");				 						
			 					}			 					
							}else if(!showABMButtons && tratamiento.getBaja_fecha() != null){
								sb.append("<a href='javascript:;' >");
			 					sb.append("<img alt=\"Baja\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/close.png\"");
			 					sb.append("/>");				 
							}
							
							row.addText(sb.toString());
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
 
	<liferay-util:include page="/html/portlet/autorizaciones/autorizaciones_prestacionales/paginador_autorizaciones_prestacionales.jsp">
    </liferay-util:include>	
    
	<script type="text/javascript">
			
	</script>
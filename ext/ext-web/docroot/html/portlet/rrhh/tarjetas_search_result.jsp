<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<liferay-util:include page="/html/portlet/rrhh/paginador_tarjetas.jsp">
</liferay-util:include>

<portlet:defineObjects/>
			<%
				
			    boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ABM_RRHH);
			
				List<TarjetaAcceso> tarjetasLista  = new ArrayList<TarjetaAcceso>();
				
				tarjetasLista= (ArrayList<TarjetaAcceso>)renderRequest.getAttribute(WebKeysRrhh.BUSQUEDA_REGISTROS_TARJETAS);
				
				if (tarjetasLista == null || tarjetasLista.size() == 0) {
					tarjetasLista = (ArrayList<TarjetaAcceso>) portletSession.getAttribute(WebKeysRrhh.BUSQUEDA_REGISTROS_TARJETAS, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Id");		 		
		 		headerNames.add("Nro Tarjeta");
		 		headerNames.add("Apellido");
		 		headerNames.add("Nombre");
		 		headerNames.add("Legajo");
		 		headerNames.add("Entidad");
		 		headerNames.add("Sector");
		 			
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-tarjetas-were-found"));
			    
							    
				if(null!=tarjetasLista){
		 				//Seteo el total de la lista.
					 	int total = tarjetasLista.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < tarjetasLista.size(); i++) {
					 		TarjetaAcceso tarjetaAcceso    = (TarjetaAcceso) tarjetasLista.get(i);
		 					ResultRow row = new ResultRow(tarjetaAcceso,tarjetaAcceso.getId(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);			 				
			 							 				
			 				rowURL.setParameter("struts_action","/rrhh/editar_borrar_tarjetas_entry");
					 		rowURL.setParameter("id_registro_tarjeta", String.valueOf(tarjetaAcceso.getId() ));
					 		rowURL.setParameter("cmd","view");
					 		
					 		row.addText(  tarjetaAcceso!=null && String.valueOf(tarjetaAcceso.getId()) != null ? String.valueOf(tarjetaAcceso.getId())  : "" ,  rowURL);
			 				row.addText(  tarjetaAcceso!=null && String.valueOf(tarjetaAcceso.getId_tarjeta_acceso()) != null ? String.valueOf(tarjetaAcceso.getId_tarjeta_acceso())  : "" ,  rowURL );
			 				row.addText(  tarjetaAcceso!=null && tarjetaAcceso.getApellido() != null ? tarjetaAcceso.getApellido()  : ""  ,  rowURL);			 				 				
			 				row.addText(  tarjetaAcceso!=null && tarjetaAcceso.getNombre() != null ? tarjetaAcceso.getNombre()  : ""  ,  rowURL);
			 				row.addText(  tarjetaAcceso!=null && String.valueOf(tarjetaAcceso.getLegajo() ) != null ?  String.valueOf(tarjetaAcceso.getLegajo())  : ""  ,  rowURL);
			 				row.addText(  tarjetaAcceso!=null && String.valueOf(tarjetaAcceso.getEntidad() ) != null ? tarjetaAcceso.getEntidad()  : ""  ,  rowURL);
			 				row.addText(  tarjetaAcceso!=null && String.valueOf(tarjetaAcceso.getSector() ) != null ? tarjetaAcceso.getSector()  : ""  ,  rowURL);
							// Action						
							 row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/rrhh/editar_borrar_tarjeta.jsp");												
							
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
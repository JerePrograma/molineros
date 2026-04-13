<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<liferay-util:include page="/html/portlet/farmacia_ospim/paginador_farmacia_ospim.jsp">
</liferay-util:include>
    
<portlet:defineObjects/>

			<%				
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_FARMACIA_OSPIM   );
				List<Farmacia> farmaciaLista = new ArrayList<Farmacia>();
				
				farmaciaLista= (ArrayList<Farmacia>)renderRequest.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_FARMACIA_OSPIM );
				if (farmaciaLista == null || farmaciaLista.size() == 0) {
					farmaciaLista = (ArrayList<Farmacia>) portletSession.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_FARMACIA_OSPIM, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Id Farmacia");		 		
		 		headerNames.add("Descripción");
		 		headerNames.add("Cuit");
		 		headerNames.add("codigo");
		 		headerNames.add("camara");
		 		headerNames.add("calle");
		 		headerNames.add("sucursal");
		 		headerNames.add("telefono");
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-farmacia-were-found"));
			    			    
				if(null!=farmaciaLista){
					 	int total = farmaciaLista.size();
					 	searchContainer.setTotal(total);
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < farmaciaLista.size(); i++) {
					 		Farmacia farmacia     = (Farmacia) farmaciaLista.get(i);
		 					ResultRow row = new ResultRow(farmacia,farmacia.getId_farmacia() , i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);			 				
			 							 				
			 				rowURL.setParameter("struts_action","/farmaciaospim/editar_borrar_farmacia_entry");
					 		rowURL.setParameter("id_registro_farmacia", String.valueOf(farmacia.getId_farmacia()) );
					 		rowURL.setParameter("cmd","view");
					 		row.addText(farmacia  != null ? String.valueOf((farmacia.getId_farmacia()))     : "" ,  rowURL);
			 				row.addText(String.valueOf(farmacia.getDescripcion())     != null ? String.valueOf(farmacia.getDescripcion())  : "" ,  rowURL);
			 				row.addText(farmacia.getEmpresa().getCuit()   != null ? farmacia.getEmpresa().getCuit()  : "" ,  rowURL);			 				 				
			 				row.addText(farmacia.getCodigo()  != null ? farmacia.getCodigo() : "" ,  rowURL);
			 				row.addText(farmacia.getCamara()  != null ? farmacia.getCamara().toUpperCase()   : "" ,  rowURL);
			 				row.addText(farmacia.getCalle()   != null ? farmacia.getCalle().toUpperCase()  : "" ,  rowURL);
			 				row.addText( farmacia.getEmpresa().getSucursal()        != null ? farmacia.getEmpresa().getSucursal()  : "" ,  rowURL);
			 				row.addText( farmacia.getTelefono()         != null ? farmacia.getTelefono()  : "" ,  rowURL);			 				
							// Action						
							row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/farmacia_ospim/editar_borrar_farmacia.jsp");
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	
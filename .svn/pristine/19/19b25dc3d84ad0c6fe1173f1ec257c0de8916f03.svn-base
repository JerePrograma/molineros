<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


	 
     	<liferay-util:include page="/html/portlet/farmacia_ospim/medicamentos/paginador_medicamentos.jsp">
	    </liferay-util:include>
	
	
<portlet:defineObjects/>

			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_MEDICAMENTOS_FARMACIA_OSPIM  );
				List<Medicamento> medicamentoList = new ArrayList<Medicamento >();
				
				medicamentoList= (ArrayList<Medicamento>)renderRequest.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_MEDICAMENTOS_OSPIM );
				if (medicamentoList == null || medicamentoList.size() == 0) {
					medicamentoList = (ArrayList<Medicamento>) portletSession.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_MEDICAMENTOS_OSPIM, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Id Medicamento");		 		
		 		headerNames.add("Troquel");
		 		headerNames.add("Nombre");
		 		headerNames.add("Droga");
		 		headerNames.add("Laboratorio");
		 		headerNames.add("presentacion");
		 		headerNames.add("precio");
		 			
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-medicamentos-were-found"));
			    
							    
				if(null!=medicamentoList){
		 				//Seteo el total de la lista.
					 	int total = medicamentoList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < medicamentoList.size(); i++) {
					 		Medicamento medicamento    = (Medicamento) medicamentoList.get(i);
		 					ResultRow row = new ResultRow(medicamento,medicamento.getId_medicamento() , i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);			 				
			 							 				
			 				rowURL.setParameter("struts_action","/farmaciaospim/editar_borrar_medicamentos_entry");
					 		rowURL.setParameter("id_registro_med", String.valueOf(medicamento.getId_medicamento() ));
					 		rowURL.setParameter("cmd","view");
					 		
					 		row.addText(medicamento  != null ? medicamento.getId_medicamentoAsString()   : "" ,  rowURL);
			 				row.addText(String.valueOf(medicamento.getTroquel())     != null ? String.valueOf(medicamento.getTroquel())  : "" ,  rowURL);
			 				row.addText(medicamento.getNombre()  != null ? medicamento.getNombre().toUpperCase()  : "" ,  rowURL);			 				 				
			 				row.addText(medicamento.getDroga()  != null ? medicamento.getDroga().toUpperCase() : "" ,  rowURL);
			 				row.addText(medicamento.getLaboratorio() != null ? medicamento.getLaboratorio().toUpperCase()  : "" ,  rowURL);
			 				row.addText(medicamento.getPresentacion()  != null ? medicamento.getPresentacion().toUpperCase()  : "" ,  rowURL);
			 				row.addText( medicamento.getPrecio()     != null ? String.valueOf(medicamento.getPrecio())  : "" ,  rowURL);
			 				
							// Action						
							row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/farmacia_ospim/medicamentos/editar_borrar_medicamentos.jsp");												
							
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
		
	<script type="text/javascript">
			
	function imprimir(id){		
		window.location.href ="/pdfservlet/?accion=equipointerdisciplinario&idequipo=" + id ;
	}
	
	
	
	
	</script>

<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado				
				String view=ParamUtil.getString(request,"view");
				String checkbox=ParamUtil.getString(request,"checkbox");
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ENTIDAD_AMTIMA);				
				List<Medicamento> medicamentosList= (ArrayList<Medicamento>)renderRequest.getAttribute(WebKeysFarmacia.BUSQUEDA_MEDICAMENTO);
				System.out.println("MEDICAMENTO LIST= "+medicamentosList.size());
				PortletURL portletURL = renderResponse.createRenderURL();				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("troquel");
		 		headerNames.add("registro");
		 		headerNames.add("nombre");
		 		headerNames.add("droga");
		 		headerNames.add("accion");		 		
		 		headerNames.add("presentacion");		 		
		 		
		 		headerNames.add("Porcentaje");		 		
		 		headerNames.add("Precio");
		 		
		 		headerNames.add("choose");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-medicamentos-were-found"));
							
				if(null!=medicamentosList){
	 			
	 				//Seteo el total de la lista.
				 	int total = medicamentosList.size();
	 				if (total == 1){
	 					Medicamento medicamento = (Medicamento) medicamentosList.get(0);
	 					%>
	 					<script type="text/javascript">
	 					seleccionaMedicamento('<%=medicamento.getId_medicamento()%>','<%=medicamento.getTroquel()%>','<%=medicamento.getRegistro()%>','<%=medicamento.getNombre()%>',
	 							'<%=medicamento.getDroga()%>','<%= medicamento.getPresentacion() %>','<%= medicamento.getLaboratorio() %>',
	 							'<%=medicamento.getPrecio()%>','<%=medicamento.getCober_sssalud()!=null?medicamento.getCober_sssalud().toString():"0"%>','<%=medicamento.getCober_ospim()!=null?medicamento.getCober_ospim().toString():"0"%>',
	 							'<%=medicamento.getCober_amtima()!=null?medicamento.getCober_amtima().toString():"0"%>', '<%=medicamento.getPrecio_ospim()%>', '<%=medicamento.getMonto_cober_ospim()!=null?medicamento.getMonto_cober_ospim().toString():"0"%>'
	 							,'<%=medicamento.getMonto_cober_amtima()!=null?medicamento.getMonto_cober_amtima().toString():"0"%>','<%=medicamento.isPmo()%>', '<%=medicamento.getCod_barra() != null ? medicamento.getCod_barra():""%>' , '<%=medicamento.getTotal_cobertura() != null ? medicamento.getTotal_cobertura():""%>', '<%=medicamento.getAccion() != null ? medicamento.getAccion():""%>');	 				
	 					</script>
	 					<%
	 				} else {
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < medicamentosList.size(); i++) {
					 		Medicamento medicamento = (Medicamento) medicamentosList.get(i);
		 					ResultRow row = new ResultRow(medicamento,medicamento.getRegistro(), i);		 				
			 				row.addText(String.valueOf(medicamento.getTroquel()));
			 				row.addText(String.valueOf(medicamento.getRegistro()));
			 				row.addText(medicamento.getNombre());
			 				row.addText(medicamento.getDroga()!=null?medicamento.getDroga():"-");
			 				row.addText(medicamento.getAccion()!=null?medicamento.getAccion():"-");
			 				row.addText(medicamento.getPresentacion());
			 				row.addText(medicamento.getCober_sssalud()!=null?medicamento.getCober_sssalud().toString():"");
			 				row.addText(medicamento.getPrecio_ospim()!=null?medicamento.getPrecio_ospim().toString():"");
							StringBuilder sb= new StringBuilder();
							
								if(null==view || !view.trim().equals("true")){
				 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:seleccionaMedicamento('");
				 					sb.append(medicamento.getId_medicamento());
				 					sb.append("','");
				 					sb.append(medicamento.getTroquel());
				 					sb.append("','");	 					
				 					sb.append(medicamento.getRegistro());	 					
				 					sb.append("','");
				 					sb.append(medicamento.getNombre());
				 					sb.append("','");
				 					sb.append(medicamento.getDroga()!=null?medicamento.getDroga():"");
				 					sb.append("','");
				 					sb.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion():"");
				 					sb.append("','");
				 					sb.append(medicamento.getLaboratorio()!=null?medicamento.getLaboratorio():"");
				 					sb.append("','");
				 					sb.append(medicamento.getPrecio()!=null?medicamento.getPrecio().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getCober_sssalud()!=null?medicamento.getCober_sssalud().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getCober_ospim()!=null?medicamento.getCober_ospim().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getCober_amtima()!=null?medicamento.getCober_amtima().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getPrecio_ospim()!=null?medicamento.getPrecio_ospim().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getMonto_cober_ospim()!=null?medicamento.getMonto_cober_ospim().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.getMonto_cober_amtima()!=null?medicamento.getMonto_cober_amtima().toString():"0");
				 					sb.append("','");
				 					sb.append(medicamento.isPmo());
				 					sb.append("','");
				 					sb.append(medicamento.getCod_barra() != null ? medicamento.getCod_barra():"");
				 					sb.append("','");
				 					sb.append(medicamento.getTotal_cobertura() != null ? medicamento.getTotal_cobertura():"");
				 					sb.append("','");
				 					sb.append(medicamento.getAccion() != null ? medicamento.getAccion():"");
				 					sb.append("');\" />");
				 					row.addText(sb.toString());			 					
			 					}
				 			resultRows.add(row);
					 	}
	 				}
	 			}
	 	
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	


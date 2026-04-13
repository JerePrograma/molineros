<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			BigDecimal totalImporte = new BigDecimal("0");
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
			MovimientoBancario mov= (MovimientoBancario)request.getSession().getAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION);
			
					boolean esEdicion = true;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)|| PermissionUtil.userContainsRole(user,"ABM_Farmacia") || PermissionUtil.userContainsRole(user,"Entidad_Uoma");				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cheque-nro");
			 		headerNames.add("importe");
			 		headerNames.add("banco");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("nuevo-estado");
					if(showABMButtons && esEdicion) {
						headerNames.add("marcar-rechazado");
						headerNames.add("Sacar de la lista");
					}					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
				 	List<MovimientoBancoCheque> cheques = null;
					if (mov != null){
						cheques = mov.getChequesDepositados();
					}
					if(null!=cheques){
				 		//Seteo el total de la lista.
					 	int total = cheques.size();
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < cheques.size(); i++) {
					 		
					 		MovimientoBancoCheque mbchq = cheques.get(i);
					 		if (mbchq.isBorradoLogico()){
					 			total--;
					 		} else {
						 		Cheque chq = mbchq.getCheque();						 	
			 					ResultRow row = new ResultRow(chq, chq.getNumero().toString(), i);
			 					row.addText(chq.getNumero().toString());
			 					row.addText(chq.getImporte().toString());
			 					totalImporte = totalImporte.add(chq.getImporte());
			 					int index = bancos.indexOf(chq.getBanco());
			 					row.addText(bancos.get(index).getDescripcion_banco());
			 					row.addText(chq.getFechaAsString());
			 					row.addText(chq.getEstado().getDescripcion());
								// Action
			 					if (showABMButtons && esEdicion){
			 						StringBuilder sb1= new StringBuilder();
			 						if (chq.getEstado().getId() != Cheque.Estado.RECHAZADO){
					 					sb1.append("&nbsp;<img title=\"Marcar como Rechazado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/checked.png\" onClick=\"javascript:marcarRechazado('");
					 					sb1.append(mbchq.getId());
					 					sb1.append("');\" />");
			 						} else {
			 							sb1.append("&nbsp;<img title=\"Desmarcar Rechazado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/undo.png\" onClick=\"javascript:desmarcarRechazado('");
					 					sb1.append(mbchq.getId());
					 					sb1.append("');\" />");
			 						}
					 					row.addText(sb1.toString());
				 					
			 						StringBuilder sb= new StringBuilder();
				 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/common/delete.png\" onClick=\"javascript:borraChequesDepositados('");
				 					sb.append(mbchq.getId());
				 					sb.append("');\" />");
				 					row.addText(sb.toString());
			 			 		}
	
					 			resultRows.add(row);
					 		}
					 	}
					 	searchContainer.setTotal(total);
				 	}
			%>

<liferay-ui:search-iterator  paginate="false" searchContainer="<%= searchContainer %>" />

	
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				BigDecimal totalImporte = new BigDecimal("0");
				List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
				Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
					boolean esEdicion = recibo == null || recibo.getId() == 0;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA) || PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cheque-nro");
			 		headerNames.add("importe");
			 		headerNames.add("banco");
			 		headerNames.add("fecha-pago");
					if(showABMButtons && esEdicion) { 
						headerNames.add("Borrar");
					}							
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
				 	List<ReciboCheque> cheques = null;
					if (recibo != null){
						cheques = recibo.getChequesRechazados();
					}
					if(null!=cheques){
				 		//Seteo el total de la lista.
					 	int total = cheques.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < cheques.size(); i++) {
					 		ReciboCheque rChq = cheques.get(i);
					 		Cheque chq = rChq.getChequeASustituir();
		 					ResultRow row = new ResultRow(chq, chq.getNumero().toString(), i);
		 					row.addText(chq.getNumero().toString());
		 					row.addText(chq.getImporte().toString());
		 					totalImporte = totalImporte.add(chq.getImporte());
		 					int index = bancos.indexOf(chq.getBanco());
		 					row.addText(bancos.get(index).getDescripcion_banco());
		 					row.addText(chq.getFechaAsString());
							// Action
		 					if (showABMButtons && esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraChequeChequeRechazado('");
			 					sb.append(chq.getNumero());
			 					sb.append("','");
			 					sb.append(chq.getBanco().getId_banco());
			 					sb.append("','");
			 					sb.append(chq.getCuentaBancaria().getId_cuenta_bcria());
			 					sb.append("','");
			 					sb.append(chq.getCuit());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}

				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator  paginate="false" searchContainer="<%= searchContainer %>" />		
<script type="text/javascript">
	document.getElementById("total_cheques_rechazados").value = "<%= totalImporte.toString()%>";
</script>		
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
			<% 
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
				String view=ParamUtil.getString(request,"view");
				String id_liquidacion=request.getParameter("id_liquidacion");				

				List<ComprobanteItem> debitoList = DebitoServiceUtil.buscaDebitos(Integer.valueOf(id_liquidacion));
				BigDecimal sumaDebitos = DebitoServiceUtil.sumaImportesItems(debitoList);				
				
				PortletURL portletURLDebitos = renderResponse.createRenderURL();
		 		List<String> headerNamesDebitos = new ArrayList<String>();
		 		headerNamesDebitos.add("tipo");
		 		headerNamesDebitos.add("letra");
		 		headerNamesDebitos.add("pto-venta");
		 		headerNamesDebitos.add("numero-texto");
		 		headerNamesDebitos.add("motivo");
		 		headerNamesDebitos.add("importe");		 		
		 		headerNamesDebitos.add("observacion");

		 		if(showABMButtons && (null==view || !view.equals("true"))) {
					headerNamesDebitos.add("editar-borrar");
				}
				SearchContainer searchContainerDebitos= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLDebitos, headerNamesDebitos,
				LanguageUtil.get(pageContext, "no-dobitos-were-found"));
							
				if(null!=debitoList){
					int total=debitoList.size();
	 				searchContainerDebitos.setTotal(total);
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsDebitos = searchContainerDebitos.getResultRows();
	 			 	for (int i = 0; i < debitoList.size(); i++) {
	 			 		ComprobanteItem deb = (ComprobanteItem) debitoList.get(i);
	 			 		ResultRow rowDebito=null;
	 			 		
	 			 		rowDebito = new ResultRow(deb,deb.getItem(), i);	 						 
	 					rowDebito.addText(deb.getTipoComprobante());
	 					rowDebito.addText(deb.getLetraComprobante());	 					
	 					rowDebito.addText(String.valueOf(deb.getPtoVenta()));	 					
	 					rowDebito.addText(deb.getNroComprobante());
	 					rowDebito.addText(deb.getDescripcion_motivo());
	 					rowDebito.addText(deb.getSaldo().toString());
	 					rowDebito.addText(deb.getObservaciones().substring(0,deb.getObservaciones().length()>=100?100:deb.getObservaciones().length()-1));
	 					
	 					if(showABMButtons && (null==view || !view.equals("true"))) {
		 					StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaDebito('");		 					
		 					sb.append(deb.getItem());
		 					sb.append("','");
		 					sb.append(deb.getMotivo());
		 					sb.append("','");
		 					sb.append(deb.getObservaciones());
		 					sb.append("','");
		 					sb.append(deb.getSaldo().toString());
		 					sb.append("');\" />");
		 					sb.append(" / ");
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraDebito('");
		 					sb.append(deb.getItem());
		 					sb.append("');\" />");		 							 				
		 					rowDebito.addText(sb.toString());
	 					}
	 					resultRowsDebitos.add(rowDebito);	 					
	 					if (i == debitoList.size() - 1 ) {
	 						ResultRow rowDebitoFinal = new ResultRow(deb,deb.getItem(), i+1);
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText(sumaDebitos.toString());
	 						rowDebitoFinal.addText("");
	 						rowDebitoFinal.addText("Subtotal");
	 						resultRowsDebitos.add(rowDebitoFinal);
	 					}
	 			 	}
	 			}
 		%>
 	<c:choose>		
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerDebitos%>" />

	<script type="text/javascript">
	try {
		<portlet:namespace />limpiarCamposDebito();
	} catch (err) {
	}
	jQuery('#<portlet:namespace />debitado').val("<%=sumaDebitos%>");			
	pierdeFocoImporteLiq();
	</script>
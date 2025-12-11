<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
					String ids  ="";
					Recibo recibo = (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
				
					boolean esEdicion = recibo == null || recibo.getId() == 0;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("acta");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("importe-total");
			 		if (esEdicion){
			 			headerNames.add("importe-adeudado");
			 		}
			 		headerNames.add("importe-cheques-a-ingresar");
			 		headerNames.add("importes-adicional-ingresar");
			 		headerNames.add("importe-a-pagar");
					if(showABMButtons && esEdicion) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-actas-were-found"));
				
				 	List<ReciboActa> actas = null;
					if (recibo != null){
						actas = recibo.getActas();
					}
					if(null!=actas){
					 	int total = actas.size();
				 		//Seteo el total de la lista.
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < actas.size(); i++) {
					 		ReciboActa acta = actas.get(i);
					 		ids += actas.get(i).getActa().getId() + ";";
		 					ResultRow row = new ResultRow(acta, acta.getActa().getId(), acta.getActa().getId());
		 					row.addText(acta.getActa().getNumero());
		 					row.addText(acta.getActa().getFechaPagoAsString());
		 					row.addText(acta.getActa().getTotal().toString());
		 					BigDecimal deuda = null;
		 					if (esEdicion){
			 					deuda = acta.getActa().getTotal().subtract(acta.getActa().getTotalPagadoIngresado()).subtract(
			 							acta.getActa().getTotalPagadoPorConvenioYActas());
			 					row.addText(deuda.toString());
		 					}
		 					row.addText(acta.getActa().getTotalActaPagosChequeNoIngresados().toString());
		 					BigDecimal adic = acta.getImporteAdicional();
		 					if (adic == null){
		 						adic = BigDecimal.ZERO;
		 					}
		 					if (esEdicion){
		 						row.addText("<input type='text' size='8' id='acta_"+acta.getActa().getId()+"' name='acta_"+acta.getActa().getId()+"' value='"+adic+"' onchange=\"agregarCeros(this); "
		 							+" sumarActa('"+acta.getActa().getTotalActaPagosChequeNoIngresados().toString()+"','"+deuda.subtract(acta.getActa().getTotalActaPagosIngresados()).toString()+"','"+acta.getActa().getId()+"','"+acta.getActa().getNumero()+"');\"/>");
		 					} else {
		 						row.addText(adic.toString());
		 					}
		 					row.addText("<input type='text' size='8' id='total_acta_"+acta.getActa().getId()+"' name='total_acta_"+acta.getActa().getId()+"' value='"+acta.getActa().getTotalActaPagosIngresados().add(adic)+"' disabled='disabled'/>");
							// Action
		 					if (showABMButtons && esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraActa('");
			 					sb.append(actas.get(i).getActa().getId());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}
				 			resultRows.add(row);
					 	}
					 searchContainer.setTotal(total);
				 	}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

<script type="text/javascript">
	document.getElementById("ids_actas").value = "<%= ids%>";
</script>		
	
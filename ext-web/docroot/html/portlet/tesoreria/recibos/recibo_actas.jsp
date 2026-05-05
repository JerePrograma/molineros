<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			
					String portlet_name = ParamUtil.getString(request, "portlet_name");

					if (portlet_name == null || portlet_name.trim().equals("")){
						portlet_name = "tesoreria";
					}
					if(renderResponse.getNamespace().equals("_FAR_1_")){
						portlet_name = "farmacia";
					}
					
					if(renderResponse.getNamespace().equals("_UOM_1_")){
						portlet_name = "uoma";
					}
					String ids  ="";
					Recibo recibo = (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA) || portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
					boolean esEdicion = true;
/*
					if ((recibo !=null && recibo.getId() > 0)){
						esEdicion = false;
					}								
*/									
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
			 		headerNames.add("importes-adicional-ingresar");
			 		if(esEdicion){			 			
			 			headerNames.add("importe-a-pagar");
			 		}else{
			 			headerNames.add("importe-pagado");			 			
			 		}
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
					 		ReciboActa racta = actas.get(i);
					 		Acta acta=racta.getActa();
					 		ids += actas.get(i).getActa().getId() + ";";
		 					ResultRow row = new ResultRow(racta, racta.getActa().getId(), racta.getActa().getId());
		 					row.addText(racta.getActa().getNumero());
		 					row.addText(racta.getActa().getFechaPagoAsString());
		 					row.addText(racta.getActa().getTotal().toString());
		 					BigDecimal deuda = null;
		 					deuda = racta.getActa().getTotal().subtract(racta.getActa().getTotalPagadoIngresado()).subtract(
		 							racta.getActa().getTotalPagadoPorConvenioYActas());
		 					if (esEdicion){			 				
			 					row.addText(deuda.toString());
			 					//row.addText(acta.getTotalActaPagosChequeNoIngresados().toString());
		 					}		 					
		 					BigDecimal adic = racta.getImporteAdicional();
		 					if (adic == null){
		 						adic = BigDecimal.ZERO;
		 					}		 					
		 					if (esEdicion){
		 						row.addText("<input type='text' size='8' id='acta_"+racta.getActa().getId()+"' name='acta_"+racta.getActa().getId()+"' value='"+adic+"' onchange=\"agregarCeros(this); "
			 							+" sumarActa('"+deuda.toString()+"','"+racta.getActa().getTotal().subtract(deuda).toString()+"','"+racta.getActa().getId()+"','"+racta.getActa().getNumero()+"');\"/>");		 						
		 					} else {
		 						row.addText(adic.toString());		 					
		 					}
		 					BigDecimal totalActa=BigDecimal.ZERO;		 					
		 					if(racta.getActa().getTotalActaPagosChequeNoIngresados().add(adic).compareTo(BigDecimal.ZERO)==0){
		 						totalActa=deuda;
		 					}else{
		 						totalActa=deuda.add(adic);
		 					}
		 					/*if(esEdicion){
		 						row.addText("<input type='text' size='8' id='total_acta_"+acta.getId()+"' name='total_acta_"+acta.getId()+"' value='"+totalActa+"' onChange='javascript:sumarConceptos();' />");//readonly='true'
		 					}else{*/
		 						row.addText("<input type='text' size='8' id='total_acta_"+acta.getId()+"' name='total_acta_"+acta.getId()+"' value='"+acta.getTotalActaPagosChequeNoIngresados()+"' onChange='javascript:sumarConceptos();'/>");
		 					//}
		 					
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
	
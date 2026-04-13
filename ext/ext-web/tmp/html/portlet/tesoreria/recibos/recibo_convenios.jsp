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
					//boolean esEdicion = recibo != null && recibo.getId() > 0;
					
					boolean esEdicion = true;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA) || portlet_name.equals("farmacia")|| portlet_name.equals("uoma");				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("convenio");
			 		headerNames.add("fecha");
			 		headerNames.add("importe-total");
			 		if (!esEdicion){
			 			headerNames.add("importe-adeudado");
			 		}
			 		headerNames.add("importes-adicional-ingresar");
			 		if(esEdicion){
			 			headerNames.add("importe-pagado");
			 		}else{
			 			headerNames.add("importe-a-pagar");
			 		}
			 		if(showABMButtons || esEdicion) { 
						headerNames.add("editar-borrar");
					}						
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-convenios-were-found"));
				
				 	List<ReciboConvenio> convenios = null;
					if (recibo != null){
						convenios = recibo.getConvenios();
					}
					if(null!=convenios){
				 		//Seteo el total de la lista.
					 	int total = convenios.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < convenios.size(); i++) {
					 		ReciboConvenio rConv = convenios.get(i);
					 		Convenio conv = rConv.getConvenio();
					 		ids += conv.getId() + ";";
		 					ResultRow row = new ResultRow(conv, conv.getId(), conv.getId());
		 					row.addText(conv.getNumero() != null && !conv.getNumero().trim().equals("") ? conv.getNumero() : String.valueOf(conv.getId()));
		 					row.addText(conv.getFechaInicioAsString());
		 					row.addText(conv.getTotal().toString());
		 					BigDecimal deuda = null;
		 					deuda = conv.getTotal().subtract(conv.getTotalPagadoIngresado()).subtract(
		 							conv.getTotalPagadoPorConvenios());
		 					if (!esEdicion){			 					
			 					row.addText(deuda.toString());		 						
					 		}
		 					BigDecimal adic = rConv.getImporteAdicional();
		 					if (adic == null){
		 						adic = BigDecimal.ZERO;
		 					}
		 					if (esEdicion||showABMButtons){
		 						row.addText("<input type='text' size='8' id='convenio_"+conv.getId()+"' name='convenio_"+conv.getId()+"' value='"+adic+"' onchange=\"agregarCeros(this); "
		 							+" sumarConvenio('"+conv.getTotalConvenioPagosChequeNoIngresados().toString()+"','"+deuda.subtract(conv.getTotalConvenioPagosChequeNoIngresados()).toString()+"','"+conv.getId()+"','"+conv.getNumero()+"');\"/>");
		 					} else {
		 						row.addText(adic.toString());
		 					}
		 					/*if(esEdicion){
		 						row.addText("<input type='text' size='8' id='total_convenio_"+conv.getId()+"' name='total_convenio_"+conv.getId()+"' value='"+conv.getTotalConvenioPagosIngresados()+"' readonly='true'/>");
		 					}else{*/
		 						row.addText("<input type='text' size='8' id='total_convenio_"+conv.getId()+"' name='total_convenio_"+conv.getId()+"' value='"+conv.getTotalConvenioPagosIngresados()+"' onChange='javascript:sumarConceptos();' />");
		 					//}
		 					
							// Action
		 					if (showABMButtons || esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraConvenio('");
			 					sb.append(conv.getId());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
<script type="text/javascript">    
	document.getElementById("ids_convenios").value = "<%= ids%>";
</script>		
	

<%@ include file="/html/portlet/uoma/init.jsp" %>

	<%				
		Factura facturaAux = (Factura) portletSession.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
		List<FacturaDetalle> detalles = facturaAux.getDetalles();
		
		/* List<FacturaDetalle> detalles = (ArrayList<FacturaDetalle>)portletSession.getAttribute(WebKeysUOMA.FACTURA_DETALLE_EN_EDICION,PortletSession.APPLICATION_SCOPE); */
	     String esEdicionStr=(String) request.getAttribute("esEdicion");
		boolean esEdicion = false;
		
		if (esEdicionStr != null && esEdicionStr.equalsIgnoreCase("esEdicion")){
			esEdicion = false;
		}				

		PortletURL portletURL = renderResponse.createRenderURL();
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();

 		/* headerNames.add("Código"); */
 		headerNames.add("descripcion");
 		headerNames.add("importe");
 		/* headerNames.add("IVA"); */
 		if(esEdicion){
 			headerNames.add("Borrar");
 		}
 		
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-fc-detalles-were-found"));			

		if(null!=detalles){

		 	searchContainer.setTotal(detalles.size());
		 	
			List resultRows = searchContainer.getResultRows();
			
		 	for (int i = 0; i < detalles.size(); i++) {
		 		FacturaDetalle detalle = (FacturaDetalle) detalles.get(i);
		 		
		 		StringBuilder sb= new StringBuilder();
		 		
		 		ResultRow row = new ResultRow(detalle, detalle.getId(), i);

 
				/* row.addText(String.valueOf(detalle.getDetalle().getId())); */
 				row.addText(detalle.getDetalle().getDescripcion());
 				/* row.addText(detalle.getDetalle().getPrecioUnitario().toString()); */		 										
				row.addText(detalle.getPrecio().toString());
	 			if(esEdicion){
	 				sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/delete.png\" onClick=\"javascript:eliminarDetalleFC('");
		 			sb.append(String.valueOf(detalle.getId()));
		 			sb.append("');\" />");
		 			/* sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/edit.png\" onClick=\"javascript:editarDetProd('");
					sb.append(detalle.getId());
		 			sb.append("');\" />"); */
		 			row.addText(sb.toString());
				}
 
		 			
		 		
		 		resultRows.add(row);
		 		
		 	}
		 	
		}
	%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	
	<%if(detalles!=null){ %>
	   <table>
		<tr>
			<td>
				<fieldset class="block-labels">
					
				<table>
					<tr>
						<td><liferay-ui:message key="fc-neto" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_neto" 
						id="<portlet:namespace />fc_neto" value="<%=facturaAux.getImporteNeto() %>" readonly="readonly">  </td>
						<td><liferay-ui:message key="fc-iva21" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_iva_21" 
						id="<portlet:namespace />fc_iva_21" value="<%=facturaAux.getIva() %>" readonly="readonly">  </td>
						
						<td>Reintegro IVA </td> 
						<td><input type="text" name="<portlet:namespace />fc_iva_21_reint" 
						id="<portlet:namespace />fc_iva_21_reint" value="<%=facturaAux.getIvaReintegro() %>" readonly="readonly">  </td>
						
						<td><liferay-ui:message key="fc-exen" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_exento" 
						id="<portlet:namespace />fc_exento" value="<%=facturaAux.getImporteExento() %>" readonly="readonly">  </td>
						<td><liferay-ui:message key="fc-total" /> </td> 
						
						<td>Percepciones </td> 
						<td><input type="text" name="<portlet:namespace />fc_percepciones" 
						id="<portlet:namespace />fc_neto" value="<%=facturaAux.getPercepcion() %>" readonly="readonly">  </td>
						
						<td><input type="text" name="<portlet:namespace />fc_total" 
						id="<portlet:namespace />fc_total" value="<%=facturaAux.getImporteTotalCalculado() %>" readonly="readonly"></td>
					</tr>
				</table>
			</td>			
		</tr>
	  </table> 	
	<%}else{%>
		<div id="aaa">
		<table>
		<tr>
			<td>
				<fieldset class="block-labels">
				
				<table>
					<tr>
						<td><liferay-ui:message key="fc-neto" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_neto" 
						id="<portlet:namespace />fc_neto" value="<%=new BigDecimal(0) %>">  </td>
						<%-- <td><liferay-ui:message key="fc-iva21" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_iva_21" 
						id="<portlet:namespace />fc_iva_21" value="<%=new BigDecimal(0) %>">  </td> --%>
						<td>
							<div id="<portlet:namespace />fc_iva_21_div">
								<liferay-ui:message key="fc-iva21" />  
								<input type="text" name="<portlet:namespace />fc_iva_21" 
								id="<portlet:namespace />fc_iva_21" value="<%=new BigDecimal(0) %>">  
							</div>	
						</td>
						<td><liferay-ui:message key="fc-exen" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_exento" 
						id="<portlet:namespace />fc_exento" value="<%= new BigDecimal(0) %>">  </td>
						<td><liferay-ui:message key="fc-total" /> </td> 
						<td><input type="text" name="<portlet:namespace />fc_total" 
						id="<portlet:namespace />fc_total" value="<%=new BigDecimal(0) %>"></td>
					</tr>
				</table>
				</fieldset>
			</td>			
		</tr>
		</table>
		</div>
	<%} %>	
	
	
	
	
	

<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
			<%
			
				String viewStr = (String)request.getAttribute(WebKeysLiquidaciones.VIEW_LIQUIDACION);
				boolean esView = false;
				if (viewStr != null){
					esView = true;
					
				}
				
				Liquidacion liquidacion = (Liquidacion)request.getAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION);
				if(liquidacion==null){
					liquidacion=(Liquidacion)request.getSession().getAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION);
				}
				
				ArrayList <LiquidacionPrestacion> liquidacionPrestaciones = (ArrayList<LiquidacionPrestacion>)request.getAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION);
				if(liquidacionPrestaciones==null || liquidacionPrestaciones.isEmpty()){
					liquidacionPrestaciones=	(ArrayList<LiquidacionPrestacion>)request.getSession().getAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION);
				}
								
				PortletURL portletURLLiquidacionPrestacion = renderResponse.createRenderURL();
		 		List<String> headerNamesLiquidacionPrestacion = new ArrayList<String>();
		 		headerNamesLiquidacionPrestacion.add("fecha");
		 		headerNamesLiquidacionPrestacion.add("servicio");
		 		headerNamesLiquidacionPrestacion.add("nombre");
		 		headerNamesLiquidacionPrestacion.add("cuil-titular");
		 		headerNamesLiquidacionPrestacion.add("inte");
		 		headerNamesLiquidacionPrestacion.add("cod-prest");
				headerNamesLiquidacionPrestacion.add("descripcion");								
				headerNamesLiquidacionPrestacion.add("cant");
				headerNamesLiquidacionPrestacion.add("importe");
				headerNamesLiquidacionPrestacion.add("Estadístico");
				headerNamesLiquidacionPrestacion.add("total");	
				headerNamesLiquidacionPrestacion.add("Cargo Terc.");
				headerNamesLiquidacionPrestacion.add("Cargo Monotributo");	
				headerNamesLiquidacionPrestacion.add("periodo");
				if (!esView){
					headerNamesLiquidacionPrestacion.add("action.DELETE");
				}
				
				SearchContainer searchContainerLiquidacionPrestacion= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLLiquidacionPrestacion, headerNamesLiquidacionPrestacion,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));
			
				if(null!=liquidacionPrestaciones){
					
					String reclamodata="";
					
					int total=liquidacionPrestaciones.size();
	 				searchContainerLiquidacionPrestacion.setTotal(total);
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsLiquidacionPrestacion = searchContainerLiquidacionPrestacion.getResultRows();
	 			 	for (int i = 0; i < liquidacionPrestaciones.size(); i++) {
	 			 		LiquidacionPrestacion liquidacionPrestacion = (LiquidacionPrestacion) liquidacionPrestaciones.get(i);
	 			 		
	 					ResultRow rowLiquidacionPrestacion = new ResultRow(liquidacionPrestacion,liquidacionPrestacion.getId_liquidacionString()+liquidacionPrestacion.getOrdenAsString(), i);	 					
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getFecha_prestacionAsString());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getServicio());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getAfiliado().getApeNombre());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getAfiliado().getCuil_titular() != null ? liquidacionPrestacion.getAfiliado().getCuil_titular() : "" );
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getAfiliado().getInteAsString());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getPrestacion().getCodigo());
	 					reclamodata="";
	 					if (liquidacionPrestacion.getPrestacion().getIdReclamopPrestacional()>0){
	 						reclamodata="(Reclamo:" + liquidacionPrestacion.getPrestacion().getIdReclamopPrestacional() + ")";
	 					}
	 					
	 					//rowLiquidacionPrestacion.addText(liquidacionPrestacion.getPrestacion().getDescripcion());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getPrestacion().getDescripcion()+reclamodata);
	 					
	 					rowLiquidacionPrestacion.addText(String.valueOf(liquidacionPrestacion.getCantidad()));
	 					if (liquidacionPrestacion.getTercerizado().equalsIgnoreCase("0") || liquidacionPrestacion.getTercerizado().equalsIgnoreCase("")) {
	 						rowLiquidacionPrestacion.addText(liquidacionPrestacion.getImporte() != null ? liquidacionPrestacion.getImporte().toString(): "");
	 					} else {  
	 						rowLiquidacionPrestacion.addText("-");
	 					}	
	 			 		if (liquidacionPrestacion.getTercerizado().equalsIgnoreCase("1")) {
	 			 			rowLiquidacionPrestacion.addText(liquidacionPrestacion.getImporte().toString());	 			 			
	 			 		} else {
	 			 			rowLiquidacionPrestacion.addText("-");
	 			 		}

	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getMultiplicarImporteCant().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getCargoPrestadora() != null ? liquidacionPrestacion.getCargoPrestadora().toString() : "0.00");
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getCargoImesa() != null ? liquidacionPrestacion.getCargoImesa().toString() : "0.00");
	 					
	 					rowLiquidacionPrestacion.addText(liquidacionPrestacion.getPeriodoAsString());
	 					
	 					//probar que esto lo saqué del if no es view
	 					StringBuilder sb= new StringBuilder();
	 					
	 					if (liquidacionPrestacion.getPrestacion().getIdReclamopPrestacional()<1){
	 					
	 					sb.append("<img alt=\"<liferay-ui:message key='action.EDIT'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/edit.png\" onClick=\"javascript:editarLiquidacionPrestacion('");
	 					sb.append(liquidacionPrestacion.getOrdenAsString());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getFecha_prestacionAsString());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getPrestacion().getId_prestacionString());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getPrestacion().getCodigo());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getServicio());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getAfiliado().getCuil_titular() != null ? liquidacionPrestacion.getAfiliado().getCuil_titular() : "");
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getAfiliado().getInteAsString());
	 					sb.append("','");
	 					sb.append(String.valueOf(liquidacionPrestacion.getCantidad()));
	 					sb.append("','");		
	 					sb.append(liquidacionPrestacion.getPrestacion().getIdReclamopPrestacional() );
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getPrestacion().getIdPrestacionReclamoPrestacional() );
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getTercerizado());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getPeriodoAsString());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getCargoPrestadora());
	 					sb.append("','");
	 					sb.append(liquidacionPrestacion.getCargoImesa());
	 					sb.append("');\" />");
	 					if (!esView){ 
	 						sb.append(" | ");
	 							}
	 					}
	 					
	 					if (!esView){
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraLiquidacionPrestacion('");
		 					sb.append(liquidacionPrestacion.getId_liquidacionString());
		 					sb.append("','");
		 					sb.append(liquidacionPrestacion.getPrestacion().getIdReclamopPrestacional() );
		 					sb.append("','");
		 					sb.append(liquidacionPrestacion.getPrestacion().getIdPrestacionReclamoPrestacional() );
		 					sb.append("','");
		 					sb.append(liquidacionPrestacion.getOrdenAsString());
		 					sb.append("');\" />");
		 						 						
	 					}
	 					rowLiquidacionPrestacion.addText(sb.toString());
	 					
	 					resultRowsLiquidacionPrestacion.add(rowLiquidacionPrestacion);
	 					if (i == liquidacionPrestaciones.size() - 1 ) {
	 						ResultRow rowLiquidacionPrestacionFinal = new ResultRow(liquidacionPrestacion,liquidacion.getId_liquidacion(), i+1);
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText("");
	 						rowLiquidacionPrestacionFinal.addText(liquidacion.getImporteTotal().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	 						rowLiquidacionPrestacionFinal.addText("Subtotal");
	 						resultRowsLiquidacionPrestacion.add(rowLiquidacionPrestacionFinal);
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
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerLiquidacionPrestacion%>" />

		

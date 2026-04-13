<%@ include file="/html/portlet/liquidaciones/init.jsp"%>

<portlet:defineObjects />
<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	ArrayList<ConvenioPrestacionalDetalle> convPrestDetalleList = (ArrayList<ConvenioPrestacionalDetalle>)request.getSession().getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);
	if(convPrestDetalleList == null){
		convPrestDetalleList = (ArrayList<ConvenioPrestacionalDetalle>)request.getSession().getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
	}

	/* String viewStr = (String) request.getAttribute(WebKeysLiquidaciones.VIEW_CONVENIO_PREST); */	
	String viewStr = ParamUtil.getString(request, WebKeysLiquidaciones.VIEW_CONVENIO_PREST);
	
	boolean esView = false;
	if (viewStr != null && viewStr.equalsIgnoreCase("true")) {
		esView = true;
	} 
	
	PortletURL portletURLConvPrestDetalle = renderResponse.createRenderURL();
	List<String> headerNamesConvPrestDetalle = new ArrayList<String>();
	headerNamesConvPrestDetalle.add("Tipo Nomenclador");
	headerNamesConvPrestDetalle.add("Código Desde");
	headerNamesConvPrestDetalle.add("Código Hasta");
	headerNamesConvPrestDetalle.add("Fecha Desde/Hasta");
	headerNamesConvPrestDetalle.add("Plan");	
	headerNamesConvPrestDetalle.add("Servicio");
	//headerNamesConvPrestDetalle.add("Cartilla");
	headerNamesConvPrestDetalle.add("Coseguro");
	headerNamesConvPrestDetalle.add("Tipo Valorización");
/* 	headerNamesConvPrestDetalle.add("Honorarios");
	headerNamesConvPrestDetalle.add("Gastos"); 
	headerNamesConvPrestDetalle.add("Importe");*/
	headerNamesConvPrestDetalle.add("Importe");
	headerNamesConvPrestDetalle.add("Porcentaje");
	
	if (!esView) {
		headerNamesConvPrestDetalle.add("action.DELETE");
	}

	SearchContainer searchContainerConvPrestDetalle = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE, portletURLConvPrestDetalle,
	headerNamesConvPrestDetalle, LanguageUtil.get(pageContext, "no-convenios-prest-detalles-were-found"));
		
	if (null != convPrestDetalleList) {
		
		Collections.sort(convPrestDetalleList, new Comparator<ConvenioPrestacionalDetalle>() {
			public int compare(ConvenioPrestacionalDetalle o1,
			ConvenioPrestacionalDetalle o2) {
				return o1.getCodigoDesde().compareTo(o2.getCodigoDesde());
			}
		});
		
		int total = convPrestDetalleList.size();
		
		searchContainerConvPrestDetalle.setTotal(total);
		List resultRowsConvPrestDetalle = searchContainerConvPrestDetalle.getResultRows();
		for (int i = 0; i < convPrestDetalleList.size(); i++) {
					
			ConvenioPrestacionalDetalle convPrestDetalle = (ConvenioPrestacionalDetalle) convPrestDetalleList.get(i);
			String fechasDesdeHasta = sdf.format(convPrestDetalle.getFechaDesde())+"-"+(convPrestDetalle.getFechaHasta()!=null?sdf.format(convPrestDetalle.getFechaHasta()):"");
			
			if (esView || (!esView && !((convPrestDetalle.getEstado() != null 
					&& convPrestDetalle.getEstado().equals(ConvenioPrestacionalDetalle.ESTADOS.BAJA)) 
					|| convPrestDetalle.getBajaFecha() != null))) {
			
				ResultRow rowConvPrestDetalle = new ResultRow(convPrestDetalle, String.valueOf(convPrestDetalle.getId()),i);
				rowConvPrestDetalle.addText(convPrestDetalle.getTipoNomenclador().getDescripcion());
				rowConvPrestDetalle.addText(convPrestDetalle.getCodigoDesde());
				rowConvPrestDetalle.addText(convPrestDetalle.getCodigoHasta() != null ? convPrestDetalle.getCodigoHasta() : "");
				rowConvPrestDetalle.addText(fechasDesdeHasta);
				rowConvPrestDetalle.addText(convPrestDetalle.getPlanDescripcion() != null ? convPrestDetalle.getPlanDescripcion() : "");
				rowConvPrestDetalle.addText(convPrestDetalle.getServicio() != null ? !convPrestDetalle.getServicio().equals("0") ? convPrestDetalle.getServicio() : "TODOS" : "");
				rowConvPrestDetalle.addText(convPrestDetalle.getCoseguro().toString());
				rowConvPrestDetalle.addText(convPrestDetalle.getTipoValorizacion().toUpperCase());
				/* rowConvPrestDetalle.addText(convPrestDetalle.getHonorarios().toString());
				rowConvPrestDetalle.addText(convPrestDetalle.getGastos().toString());
				rowConvPrestDetalle.addText(convPrestDetalle.getImporteTotal().toString());			 */
				rowConvPrestDetalle.addText(String.valueOf(convPrestDetalle.getImporte()));
				rowConvPrestDetalle.addText(String.valueOf(convPrestDetalle.getPorcentaje()));
				
				//probar que esto lo saqué del if no es view
				StringBuilder sb = new StringBuilder();				
				if ((convPrestDetalle.getEstado() != null 
						&& convPrestDetalle.getEstado().equals(ConvenioPrestacionalDetalle.ESTADOS.BAJA)) 
						|| convPrestDetalle.getBajaFecha() != null){
					sb.append("<img alt=\"Baja\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/close.png\"/>");
					rowConvPrestDetalle.addText(sb.toString());
				} else {					
					if (!esView) {					
						sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/common/delete.png\" onClick=\"javascript:borraConvPrestDetalle('");
						sb.append(convPrestDetalle.getId());								
						sb.append("');\" />");
						rowConvPrestDetalle.addText(sb.toString());
					}
				}			
				resultRowsConvPrestDetalle.add(rowConvPrestDetalle);				
			}	
		}
	}
%>
<%-- <c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
 --%>
<liferay-ui:search-iterator searchContainer="<%=searchContainerConvPrestDetalle%>" />

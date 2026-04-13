<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<div style="font-size:9px">
<portlet:defineObjects/>
			<%
			List<ReporteAporteContribucionesEmpresa> reporte = (List<ReporteAporteContribucionesEmpresa>)renderRequest.getAttribute(WebKeysTesoreria.REPORTE_APORTES_CONTRIBUYENTES);
			
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("razon");
		 		headerNamesTercerizadora.add("cuit-contribuyente");
 				headerNamesTercerizadora.add("cuil-aportante");
		 		headerNamesTercerizadora.add("apellido");
		 		headerNamesTercerizadora.add("nombre");
				headerNamesTercerizadora.add("periodo");
				headerNamesTercerizadora.add("aporte");
				headerNamesTercerizadora.add("contribucion");
				headerNamesTercerizadora.add("cant-afiliados-declarados");
				headerNamesTercerizadora.add("cant-afiliados-pagados");
		 		headerNamesTercerizadora.add("rem-declarada");
				headerNamesTercerizadora.add("rem-pagada");
		 		headerNamesTercerizadora.add("calculado");
		 		headerNamesTercerizadora.add("pagado");
				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-actas-asociadas-were-found"));
				
				
				if(null!=reporte){
					int total=reporte.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < reporte.size(); i++) {
	 					ResultRow rowActaRelacionada = new ResultRow(reporte.get(i), i, i);			
	 					rowActaRelacionada.addText(reporte.get(i).getRazon());
	 					rowActaRelacionada.addText(reporte.get(i).getCuitContribuyente());
	 					rowActaRelacionada.addText(reporte.get(i).getCuilAportante());
	 					rowActaRelacionada.addText(reporte.get(i).getApellido());
	 					rowActaRelacionada.addText(reporte.get(i).getNombre());
	 					rowActaRelacionada.addText(reporte.get(i).getPeriodo().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getAporte().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getContribucion().toString());
	 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getCantidadAfiliadosDeclarados()));
	 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getCantidadAfiliadosPagados()));
	 					rowActaRelacionada.addText(reporte.get(i).getRemuneracionDeclarada().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getRemuneracionPagada().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getCalculado().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getPagado().toString());
	 					resultRowsInspector.add(rowActaRelacionada);
		 			}
	 				searchContainer.setTotal(total);
				}
 		%>
 		
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

</div>		

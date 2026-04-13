<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<div style="font-size:9px">
<portlet:defineObjects/>
			<%				
			    String agrupar_remuneracion = (String) renderRequest.getAttribute("agrupar_remuneracion");
			    List<ReporteDeudaEmpresa> reporte = (List<ReporteDeudaEmpresa>)renderRequest.getAttribute(WebKeysTesoreria.REPORTE_DEUDA_EMPRESA_PERIODO);			    
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("periodo");
		 		headerNamesTercerizadora.add("cuit-contribuyente");
		 		headerNamesTercerizadora.add("razon");
		 		if(null!=agrupar_remuneracion && agrupar_remuneracion.trim().equals("true")){
			 		headerNamesTercerizadora.add("total-rem-81");
			 		headerNamesTercerizadora.add("total-rem-765");
			 		headerNamesTercerizadora.add("cant-afiliados-81");
			 		headerNamesTercerizadora.add("cant-afiliados-765");
		 		}
		 		headerNamesTercerizadora.add("total-remuneracion");
		 		headerNamesTercerizadora.add("total-cant-afiliados");
		 		headerNamesTercerizadora.add("deuda");
		 		headerNamesTercerizadora.add("ramo");
				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-actas-asociadas-were-found"));
				
				
				if(null!=reporte){
					int total=reporte.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < reporte.size(); i++) {
	 					ResultRow rowActaRelacionada = new ResultRow(reporte.get(i), i, i);	 					
	 					rowActaRelacionada.addText(reporte.get(i).getPeriodoAsString());	 					
	 					rowActaRelacionada.addText(reporte.get(i).getCuit());	 					
	 					rowActaRelacionada.addText(reporte.get(i).getRazonSocial()!=null?reporte.get(i).getRazonSocial():"");
	 					if(null!=agrupar_remuneracion && agrupar_remuneracion.trim().equals("true")){
		 					rowActaRelacionada.addText(reporte.get(i).getRemDeclarada_81AsString());
		 					rowActaRelacionada.addText(reporte.get(i).getRemDeclarada_765AsString());
		 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getCantAfiliadosDeclarados_81()));
		 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getCantAfiliadosDeclarados_765()));
	 					}
	 					rowActaRelacionada.addText(reporte.get(i).getRemDeclaradaAsString());
	 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getCantAfiliadosDeclarados()));
	 					rowActaRelacionada.addText(reporte.get(i).getDeudaAsString());	 					
	 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getRamo()));	 					
	 					resultRowsInspector.add(rowActaRelacionada);
		 			}
	 				searchContainer.setTotal(total);
				}
 		%>
 		
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

</div>		

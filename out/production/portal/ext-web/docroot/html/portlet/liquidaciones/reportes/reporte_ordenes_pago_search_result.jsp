<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoAction.ReporteOrdenPagoOspim" %>

<div style="font-size:9px">
<portlet:defineObjects/>
			<%
			List<ReporteOrdenPagoOspim> reporte = (List<ReporteOrdenPagoOspim>)renderRequest.getAttribute("ops");
			
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("OP");
		 		headerNamesTercerizadora.add("Fecha");
		 		headerNamesTercerizadora.add("Acreedor");
		 		headerNamesTercerizadora.add("Acreedor Razon Soc.");
		 		headerNamesTercerizadora.add("Importe OP");
		 		headerNamesTercerizadora.add("Baja OP");
				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-ops-were-found"));
				
				
				if(null!=reporte){
					int total=reporte.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < reporte.size(); i++) {
	 					ResultRow rowActaRelacionada = new ResultRow(reporte.get(i), i, i);			
	 					rowActaRelacionada.addText(String.valueOf(reporte.get(i).getIdOrdenPago()));
	 					rowActaRelacionada.addText(reporte.get(i).getFechaAsString());
	 					String cuit = reporte.get(i).getAcreedor().getCuit();
	 					String sucursal = reporte.get(i).getAcreedor().getSucursal();
	 					if (reporte.get(i).getSeccional() != null && reporte.get(i).getSeccional().getId() != 0) {
	 						sucursal = String.valueOf(reporte.get(i).getSeccional().getId());
	 					}
	 					rowActaRelacionada.addText((cuit != null ? cuit : "" ) + (sucursal != null ? "-" + sucursal : ""));
	 					rowActaRelacionada.addText(reporte.get(i).getAcreedor() != null && reporte.get(i).getAcreedor().getRazon_soc() != null ? reporte.get(i).getAcreedor().getRazon_soc() : "");
	 					rowActaRelacionada.addText(reporte.get(i).getImporteOp().toString());
	 					rowActaRelacionada.addText(reporte.get(i).getFechaBajaOPAsString());
	 					resultRowsInspector.add(rowActaRelacionada);
		 			}
	 				searchContainer.setTotal(total);
				}
 		%>
 		
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

</div>		

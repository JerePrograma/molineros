<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();				

List<ConsolidadoLiquidaciones> liquidaciones=(List<ConsolidadoLiquidaciones>)renderRequest.getAttribute("consolidadoLiquidacionesDesregulados");
if(liquidaciones==null){  
	liquidaciones=LiquidaDesreguladosServiceUtil.getConsolidadoLiquidaciones(null,null,null);
}
List<String> headerNames = new ArrayList<String>();
headerNames.add("fecha");
headerNames.add("tercerizadora");
headerNames.add("cant-registros");
headerNames.add("importe");
headerNames.add("importe-derivar");
headerNames.add("reporte-afiliado-sin-aporte-comi-terc");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-liquidaciones-were-found"));	
if (liquidaciones != null && !liquidaciones.isEmpty()){
	int total = liquidaciones.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	System.out.println("FUERA DEL FOR");
	for (int i = 0; i < liquidaciones.size(); i++) {
		ConsolidadoLiquidaciones liq = (ConsolidadoLiquidaciones) liquidaciones.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	row.addText(liq.getFechaLiqAsString());
	 	row.addText(liq.getTercerizadora());
	 	row.addText(String.valueOf(liq.getCantRegistros()));
		row.addText(liq.getImporteTotalAsString());
		row.addText(liq.getImporteDerivarAsString());				
		StringBuilder sb=new StringBuilder();
		sb.append("<img alt=\"Exportar reporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/print.png\" onClick=\"javascript:exportarExcel('");
		sb.append(liq.getFechaLiqAsString());
		sb.append("','");
		sb.append(liq.getIdTercerizadora());
		sb.append("');\" />");
		
		sb.append("/<img alt=\"Exportar afiliados sin aporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/page.png\" onClick=\"javascript:exportarTxt('");
		sb.append(liq.getFechaLiqAsString());
		sb.append("','");
		sb.append(liq.getIdTercerizadora());
		sb.append("');\" />");

		if(liq.getIdTercerizadora().equalsIgnoreCase("MPS") || liq.getIdTercerizadora().equalsIgnoreCase("MEN")){
			sb.append("/<img alt=\"Exportar comisiones Tercerizadora\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/document_library/ods.png\" onClick=\"javascript:exportarComisionesTercerizadora('");
			sb.append(liq.getFechaLiqAsString());
			sb.append("','");
			sb.append(liq.getIdTercerizadora());
			sb.append("');\" />");
		}
		
		row.addText(sb.toString());


		
		resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
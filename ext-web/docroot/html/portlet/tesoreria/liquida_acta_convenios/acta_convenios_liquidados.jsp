<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Calendar"%>
<%
System.out.println("EN EL INCLUDE");
PortletURL portletURL = renderResponse.createRenderURL();				

List<ConsolidadoLiquidaciones> liquidaciones=(List<ConsolidadoLiquidaciones>)renderRequest.getAttribute("consolidadoLiquidaciones");
if(liquidaciones==null){  
	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.MONTH, -36);
	liquidaciones=LiquidaActaConveniosServiceUtil.getConsolidadoLiquidaciones(calendar.getTime());
	
//	liquidaciones=LiquidaActaConveniosServiceUtil.getConsolidadoLiquidaciones(null);
}
List<String> headerNames = new ArrayList<String>();
headerNames.add("fecha");
headerNames.add("importe");
headerNames.add("reporte");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-liquidaciones-were-found"));	
if (liquidaciones != null && !liquidaciones.isEmpty()){
	int total = liquidaciones.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	System.out.println("FUERA DEL FOR");
	for (int i = 0; i < liquidaciones.size(); i++) {
	    System.out.println("DENTRO DEL FOR");
		ConsolidadoLiquidaciones liq = (ConsolidadoLiquidaciones) liquidaciones.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	row.addText(liq.getFechaLiqAsString());
		row.addText(liq.getImporteTotalAsString());		
		StringBuilder sb=new StringBuilder();
		sb.append("<img alt=\"Exportar reporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/print.png\" onClick=\"javascript:exportarExcel('");
		sb.append(liq.getFechaLiqAsString());
		sb.append("');\" />");
		row.addText(sb.toString());
		resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	


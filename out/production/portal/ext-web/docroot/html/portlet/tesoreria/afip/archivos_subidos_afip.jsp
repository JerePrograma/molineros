<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<ArchivoSubidoAfip> archivos=(List<ArchivoSubidoAfip>)renderRequest.getAttribute("archivoSubidoAfip");
if(archivos==null){
	archivos=AfipServiceUtil.getArchivosSubidosAfip();
}
List<String> headerNames = new ArrayList<String>();
headerNames.add("tipo");
headerNames.add("process-fecha");
headerNames.add("cant-registros");
headerNames.add("importe-total");
//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ArchivoSubidoAfip liq = (ArchivoSubidoAfip) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	row.addText(liq.getTipo());
		row.addText(liq.getFechaProcesoAsString());
		row.addText(String.valueOf(liq.getCantReg()));
		row.addText(liq.getImporteTotalAsString());		
		/*StringBuilder sb=new StringBuilder();
		sb.append("<img alt=\"Exportar reporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/print.png\" onClick=\"javascript:exportarExcel('");
		sb.append(liq.getFechaLiqAsString());
		sb.append("');\" />");
		row.addText(sb.toString());*/
		resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	


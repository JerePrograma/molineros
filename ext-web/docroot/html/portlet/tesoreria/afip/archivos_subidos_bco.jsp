<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

java.util.Date fecha = new Date();
String porletName = renderResponse.getNamespace();

List<ArchivoSubidoBco> archivos=(List<ArchivoSubidoBco>)renderRequest.getAttribute("archivoSubidoBco");

if (porletName.equals("_FAR_1_") ){
	if(archivos == null || archivos.size() == 0){
		archivos=AfipServiceUtil.getArchivosSubidosBcoAMTIMA(fecha);
	}   
}

else {
	if(archivos == null || archivos.size() == 0){
		archivos=AfipServiceUtil.getArchivosSubidosBcoUOMA(fecha);
	}
}

List<String> headerNames = new ArrayList<String>();

headerNames.add("Ente");
headerNames.add("Fecha Rendición");
headerNames.add("Importe");
//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < archivos.size(); i++) {	    
		ArchivoSubidoBco liq = (ArchivoSubidoBco) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
  		row.addText(liq.getDescripcion());
  		row.addText(liq.getFecha_rendicionAsString());
	 	row.addText(liq.getSumAsString()); 
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<%@   include file="/html/portlet/farmacia_ospim/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

java.util.Date fecha = new Date();
String porletName = renderResponse.getNamespace();

List<ArchivoMedEspecial> archivos = FarmaciaServiceUtil.getArchivosSubidosMedEspecial(); 

List<String> headerNames = new ArrayList<String>();

headerNames.add("Período");
headerNames.add("Fecha Importación");
headerNames.add("Usuario");
headerNames.add("Cant/Reg");
headerNames.add("Total IVA");
headerNames.add("Total S/IVA");
headerNames.add("Gasto Promedio");
headerNames.add("Cantidad Beneficiarios");


//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < archivos.size(); i++) {	    
 		ArchivoMedEspecial liq = (ArchivoMedEspecial) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);  		
  		row.addText(liq.getfecha_periodoAsString());
	 	row.addText(liq.getfecha_importacionAsString());
	 	row.addText(liq.getUsuario());
	 	row.addText(liq.getTotalrecordsString());
	 	row.addText(liq.getTotalconivaString());
	 	row.addText(liq.getTotalsinivaString());
	 	row.addText(liq.getPromedioString());
	 	row.addText(liq.getCantpacientesString());	 		 		 	
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

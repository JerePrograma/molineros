<%@   include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

java.util.Date fecha = new Date();
String porletName = renderResponse.getNamespace();

List<Vademecum> preciosMedicamento ;

NumberFormat format2D = new DecimalFormat("#0.00");

SimpleDateFormat sdf=new SimpleDateFormat("MM/yyyy");

preciosMedicamento  =  (ArrayList<Vademecum >) session.getAttribute(WebKeysFarmaciaOspim.LISTADO_PRECIOS_VADEMECUM );
	

List<String> headerNames = new ArrayList<String>();

headerNames.add("Periodo");
headerNames.add("Unidades");
headerNames.add("SSS Cargo O.S.");
headerNames.add("SSS P.V.P.");
headerNames.add("Manual Dat");


//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (preciosMedicamento != null && !preciosMedicamento.isEmpty()){
	int total = preciosMedicamento.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < preciosMedicamento.size(); i++) {	    
 		Vademecum   med = (Vademecum) preciosMedicamento.get(i);
	 	ResultRow row = new ResultRow(med,new Integer(1+i), i);  		
  		row.addText(String.valueOf(sdf.format(med.getPeriodo()) ) );
	 	row.addText(String.valueOf(med.getUnidades())   );
	 	row.addText(format2D.format(med.getPrecioAcargoOsocialSss()));
	 	row.addText(format2D.format(med.getPrecioAlPublicoSss()  ));
	 	row.addText(format2D.format(med.getPrecioManualDat()   ));
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	



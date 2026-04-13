<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
PortletURL portletURL = renderResponse.createRenderURL();		

List<EscalaSueldosBasicos> sueldosBasicos=(List<EscalaSueldosBasicos>)renderRequest.getAttribute(WebKeysUOMA.SUELDOS_BASICOS);


List<String> headerNames = new ArrayList<String>();
headerNames.add("Antiguedad");
headerNames.add("Porcentaje");
headerNames.add("Cat A");
headerNames.add("Cat B");
headerNames.add("Cat C");
headerNames.add("Cat D");
headerNames.add("Cat E");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-paritarias-were-found"));
					
if (sueldosBasicos != null && !sueldosBasicos.isEmpty()){
	int total = sueldosBasicos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < sueldosBasicos.size(); i++) {	    
		EscalaSueldosBasicos sueldosBasico = (EscalaSueldosBasicos) sueldosBasicos.get(i);
	 	ResultRow row = new ResultRow(sueldosBasico,new Integer(1+i), i);
	 	row.addText(sueldosBasico.getAntiguedad());
	 	row.addText(sueldosBasico.getPorcentaje());
		row.addText(String.valueOf(sueldosBasico.getCatA()));				
		row.addText(String.valueOf(sueldosBasico.getCatB()));				
		row.addText(String.valueOf(sueldosBasico.getCatC()));
		row.addText(String.valueOf(sueldosBasico.getCatD()));
		row.addText(String.valueOf(sueldosBasico.getCatE()));
		
		row.addText("");
		
		
		resultRows.add(row);
	}
}

%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
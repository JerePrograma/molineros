<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%
List<CalculoDeudaMasivoCab> results = (ArrayList<CalculoDeudaMasivoCab>) request.getAttribute(WebKeysTesoreria.CALCULOS_DEUDA_MASIVA_RESULTADOS);
if(results==null){
	results = ActaServiceUtil.getProcesosCalculoDeudaMasivo();
}
SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy");

PortletURL portletURL = renderResponse.createRenderURL();

List<String> headerNames = new ArrayList<String>();

headerNames.add("Id Proceso");
headerNames.add("Entidad");
headerNames.add("Fecha Proceso");
headerNames.add("Usuario Proceso");
headerNames.add("Cantidad Empresas");
headerNames.add("Importe Deuda");
headerNames.add("Período Deuda Solicitado");
headerNames.add("Período Deuda Desde");
headerNames.add("Período Deuda Hasta");
/* headerNames.add("Período Deu.Nóm. Desde");
headerNames.add("Período Deu.Nóm. Hasta"); */
headerNames.add("Sin Deuda Nómina");
/* headerNames.add("Extender 30 días molineros"); */
headerNames.add("");


//headerNames.add("obtener-archivo");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-cal-deu-masivo-were-found"));
					
if (results != null && !results.isEmpty()){
	int total = results.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < results.size(); i++) {	    
 		CalculoDeudaMasivoCab cab = (CalculoDeudaMasivoCab) results.get(i);
	 	ResultRow row = new ResultRow(cab,new Integer(1+i), i);
  		row.addText(String.valueOf(cab.getIdProceso()));
  		row.addText(cab.getEntidad());
  		row.addText(sdf1.format(cab.getSolicitaFecha()));
  		row.addText(cab.getSolicitaUsr());
  		row.addText(String.valueOf(cab.getCantidadEmpresas()));
  		row.addText(cab.getImporteDeudaTotal().toString());
  		row.addText(sdf3.format(cab.getFechaImpago()));
  		row.addText(sdf2.format(cab.getDeudaDesde()));
  		row.addText(sdf2.format(cab.getDeudaHasta()));
/*   		row.addText(cab.getDeudaNominaDesde()!=null?sdf2.format(cab.getDeudaNominaDesde()):"");
  		row.addText(cab.getDeudaNominaHasta()!=null?sdf2.format(cab.getDeudaNominaHasta()):""); */
  		row.addText(cab.isSinDeudaNomina()?"SI":"NO");
  		/* row.addText(cab.isExtender30DiasMoli()?"SI":"NO"); */
  		StringBuilder sb=new StringBuilder();
		sb.append("<img alt=\"Exportar reporte\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/print.png\" onClick=\"javascript:exportarExcel('");
		sb.append(cab.getIdProceso());
		sb.append("');\" />");
		
		row.addText(sb.toString());
		
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%=searchContainer%>" />
					
	
				

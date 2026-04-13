<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ReporteRankingDeudaEmpresaBean" %>
<%@ page import="ar.com.ospim.tesoreria.service.ReportesServiceUtil" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

//List<Nomenclador> archivos=(List<Nomenclador>)renderRequest.getAttribute("Nomenclador");

List<ReporteRankingDeudaEmpresaBean> archivos=(List<ReporteRankingDeudaEmpresaBean>) ReportesServiceUtil.getRankingDeudaEmpresas();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Cuit");
headerNames.add("Tercerizadora");
headerNames.add("Deuda");
headerNames.add("Razón Social");
headerNames.add("Ramo");
headerNames.add("Descripción");
headerNames.add("Acta");
headerNames.add("Número");
headerNames.add("Desde");
headerNames.add("Hasta");
headerNames.add("Total Acta");
headerNames.add("Total Pagado");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "deuda-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ReporteRankingDeudaEmpresaBean liq = (ReporteRankingDeudaEmpresaBean) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		
		row.addText(liq.getCuit()!=null?liq.getCuit():"");
		row.addText(liq.getTercerizadora()!=null?liq.getTercerizadora():"");
		row.addText(liq.getSum()!=null?liq.getSum().toString():"");
		row.addText(liq.getRazonSocial()!=null?liq.getRazonSocial():"");
		row.addText(liq.getRamoEmpresaId()!=null?liq.getRamoEmpresaId().toString():"");
		row.addText(liq.getRamoEmpresaDesc()!=null?liq.getRamoEmpresaDesc():"");
		row.addText(liq.getActaId()!=null?liq.getActaId().toString():"");
		row.addText(liq.getNumero()!=null?liq.getNumero():"");
		row.addText(liq.getMaxPeriodo()!=null?liq.getMaxPeriodoAsString():"");
		row.addText(liq.getMinPeriodo()!=null?liq.getMinPeriodoAsString():"");
		row.addText(liq.getTotalActa()!=null?liq.getTotalActa().toString():"");
		row.addText(liq.getTotalPagado()!=null?liq.getTotalPagado().toString():"");
		
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
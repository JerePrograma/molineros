<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ReporteAportesPagoRamoBean" %>
<%@ page import="ar.com.ospim.tesoreria.service.ReportesServiceUtil" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

//List<Nomenclador> archivos=(List<Nomenclador>)renderRequest.getAttribute("Nomenclador");

List<ReporteAportesPagoRamoBean> archivos=(List<ReporteAportesPagoRamoBean>) ReportesServiceUtil.getAportesPagoRamo();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Periodo");
headerNames.add("Calculado 10");
headerNames.add("Pagado 10");
headerNames.add("Calculado 50");
headerNames.add("Pagado 50");
headerNames.add("Calculado 99");
headerNames.add("Pagado 99");
headerNames.add("Mon.Pagado");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "deuda-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ReporteAportesPagoRamoBean liq = (ReporteAportesPagoRamoBean) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		
		row.addText(liq.getPeriodo()!=null?liq.getPeriodoAsString():"");
		row.addText(liq.getCalculado10()!=null?liq.getCalculado10().toString():"");
		row.addText(liq.getPagado10()!=null?liq.getPagado10().toString():"");
		row.addText(liq.getCalculado50()!=null?liq.getCalculado50().toString():"");
		row.addText(liq.getPagado50()!=null?liq.getPagado50().toString():"");
		row.addText(liq.getCalculado99()!=null?liq.getCalculado99().toString():"");
		row.addText(liq.getPagado99()!=null?liq.getPagado99().toString():"");
		row.addText(liq.getMontribPagado() !=null?liq.getMontribPagado().toString():"");
		
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
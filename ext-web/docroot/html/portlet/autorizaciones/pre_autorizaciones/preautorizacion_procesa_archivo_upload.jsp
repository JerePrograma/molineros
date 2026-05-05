<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<PreAutorizacionLoteProcesado> archivos= PreAutorizacionServiceUtil.lotesProcesados();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nro.Lote");
headerNames.add("Fecha Proceso");
headerNames.add("Total Registros");
headerNames.add("Archivo Procesado");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		PreAutorizacionLoteProcesado liq = (PreAutorizacionLoteProcesado) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getNroLote().toString());
	 	row.addText(liq.getFechaProceso_string());
	 	row.addText(liq.getTotalRegistros().toString());
	 	row.addText(liq.getFileName());
//	 	row.addText(liq.getImputados().toString());
	 	
	 		 	
	 	
	 	resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">

</script>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

List<SeguimientoSurLoteProcesado> archivos= SeguimientoSurServiceUtil.lotesProcesadosAdelantos();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Nro.Lote");
headerNames.add("Tipo Archivo");
headerNames.add("Fecha Proceso");
headerNames.add("Total Registros");
headerNames.add("Imputados");
headerNames.add("No Encontrados");
headerNames.add("Existentes");
headerNames.add("Vencidos");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		SeguimientoSurLoteProcesado liq = (SeguimientoSurLoteProcesado) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getNroLote().toString());
	 	row.addText(liq.getTipoArchivo());
	 	row.addText(liq.getFechaProceso_string());
	 	row.addText(liq.getTotalRegistros().toString());
//	 	row.addText(liq.getImputados().toString());
	 	
	 	if(liq.getImputados()>0){
	 	 StringBuilder sb=new StringBuilder(liq.getImputados().toString());
	 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Lote Seguimiento\" src=\"");
	     sb.append(themeDisplay.getPathThemeImages());
	     sb.append("/document_library/xls.png\" onClick=\"javascript:detalleLoteSeguimientoSur(");
	     /* sb.append(liq.getNroLote() +",'IMP" ); */
	     sb.append(liq.getNroLote() +",'NUE" );
	     sb.append("');\"");
         sb.append(" title=\"Imputados\"");
	     sb.append("/>");
	     row.addText(sb.toString());
	 	}else{
	 	 row.addText("");	
	 	}
	 	
//	 	row.addText(liq.getNoEncontrados().toString());
	 	if(liq.getNoEncontrados()>0){
		 	 StringBuilder sb=new StringBuilder(liq.getNoEncontrados().toString());
		 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Lote Seguimiento\" src=\"");
		     sb.append(themeDisplay.getPathThemeImages());
		     sb.append("/document_library/xls.png\" onClick=\"javascript:detalleLoteSeguimientoSur(");
		     sb.append(liq.getNroLote() +",'NOE" );
		     sb.append("');\"");
	         sb.append(" title=\"No Encontrados\"");
		     sb.append("/>");
		     row.addText(sb.toString());
		 }else{
		 	 row.addText("");	
		 }
//	 	row.addText(liq.getExistentes().toString());
	 	if(liq.getExistentes()>0){
		 	 StringBuilder sb=new StringBuilder(liq.getExistentes().toString());
		 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Lote Seguimiento\" src=\"");
		     sb.append(themeDisplay.getPathThemeImages());
		     sb.append("/document_library/xls.png\" onClick=\"javascript:detalleLoteSeguimientoSur(");
		     sb.append(liq.getNroLote() +",'EXI" );
		     sb.append("');\"");
	         sb.append(" title=\"Existentes\"");
		     sb.append("/>");
		     row.addText(sb.toString());
		 }else{
		 	 row.addText("");	
		 }
	 	
	 	
//	 	row.addText(liq.getVencidos().toString());
	 	if(liq.getVencidos()>0){
		 	 StringBuilder sb=new StringBuilder(liq.getVencidos().toString());
		 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Lote Seguimiento\" src=\"");
		     sb.append(themeDisplay.getPathThemeImages());
		     sb.append("/document_library/xls.png\" onClick=\"javascript:detalleLoteSeguimientoSur(");
		     sb.append(liq.getNroLote() +",'VEN" );
		     sb.append("');\"");
	         sb.append(" title=\"Vencidos\"");
		     sb.append("/>");
		     row.addText(sb.toString());
		 }else{
		 	 row.addText("");	
		 }
	 	
	 	
	 	resultRows.add(row);
	}
}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">

function detalleLoteSeguimientoSur(nroLote,tipo){
	window.location.href ='/xlsservlet/?reporte=REPORTE_DETALLE_LOTE_PAGOS_SEGUIMIENTO_SUR&tipo='+tipo+'&nrolote='+nroLote
}
</script>
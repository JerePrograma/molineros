<%@   include file="/html/portlet/farmacia_ospim/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();

boolean conArchivoAbierto=false;
java.util.Date fecha = new Date();
String porletName = renderResponse.getNamespace();
SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

List<ArchivoVademecum> archivos ;

archivos=FarmaciaServiceUtil.getArchivosSubidosVademecum(); 

List<String> headerNames = new ArrayList<String>();

headerNames.add("Periodo");
headerNames.add("Fecha Proceso");
headerNames.add("Usuario");
headerNames.add("Cant Man Dat");
headerNames.add("Cant S.S.S.");
headerNames.add("Cant Altas");
headerNames.add("Cant Bajas");
headerNames.add("Estado");
headerNames.add("Cierre");
headerNames.add("Exportación");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < archivos.size(); i++) {	    
 		ArchivoVademecum  liq = (ArchivoVademecum) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
  		row.addText(sdf.format(liq.getPeriodo()));
	 	row.addText(String.valueOf(liq.getFecha_importacion()));
	 	row.addText(liq.getUsuario() );
	 	row.addText(String.valueOf(liq.getCantRegManualDat()));
	 	row.addText(String.valueOf(liq.getCantRegSSS()));
	 	row.addText(String.valueOf(liq.getCantRegAltas()));
	 	row.addText(String.valueOf(liq.getCantRegBajas()));
	 	row.addText(String.valueOf(liq.getEstadoCierre()==0?"Abierto":"Cerrado"));
	 	if(liq.getEstadoCierre()== 0 ){
	 	 	 StringBuilder sb=new StringBuilder() ; 
		 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Cierre Vademedecum\" src=\"");
		     sb.append(themeDisplay.getPathThemeImages());
		     sb.append("/document_library/ods.png\" onClick=\"javascript:cierreVademecum(");
		     sb.append(liq.getId());
		     sb.append(");\"");
	         sb.append(" title=\"Cierre de Vademecum\"");
		     sb.append("/>");
		     row.addText(sb.toString());
		     conArchivoAbierto=true;
		 }else{
		 	 row.addText("");	
		 } 
	 	
	 	 if(liq.isExportable() && liq.getEstadoCierre()== 1){
		 	 StringBuilder sb=new StringBuilder() ; 
		 	 sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Exportación de Vademecum\" src=\"");
		     sb.append(themeDisplay.getPathThemeImages());
		     sb.append("/document_library/xls.png\" onClick=\"javascript:exportacionVademecum(");
		     sb.append(liq.getId() );
		     sb.append(");\"");
	         sb.append(" title=\"Exportación de Vademecum\"");
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
<%if (conArchivoAbierto) { %>
	jQuery("#<portlet:namespace />conArchivoAbierto").val(1);
<%}%>

function exportacionVademecum (idCierreVademecum){
	var todos_los_tipos=true;
	window.location.href ='/xlsservlet/?reporte=OBTENER_VADEMECUM'+'&todosLosPadrones='+todos_los_tipos;
}

function cierreVademecum(idCierre) {
	if (confirm( '¿Está seguro de que quiere cerrar?') == true) {
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/farmaciaospim/cerrar_vademecum';
		document.<portlet:namespace />fm2.method = 'post';
		url += "&id_cierre="+idCierre;
		submitForm(document.<portlet:namespace />fm2, url);
	}
}

</script>
<%@   include file="/html/portlet/farmacia_ospim/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<%

PortletURL portletURL = renderResponse.createRenderURL();

java.util.Date fecha = new Date();
String porletName = renderResponse.getNamespace();

NumberFormat format2D = new DecimalFormat("#0.00");
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
List<ArchivoDesglose> archivos ;

archivos=FarmaciaServiceUtil.getArchivosSubidosDesgloseFarmacia (); 

List<String> headerNames = new ArrayList<String>();

headerNames.add("Periodo");
headerNames.add("Fecha Importacion");
headerNames.add("Usuario");
headerNames.add("Cant/Reg");
headerNames.add("Total PVP");
headerNames.add("Total Entidad");
headerNames.add("Total Ospim");
headerNames.add("Total Uoma");
headerNames.add("Total Amtima");
headerNames.add("Exportación");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < archivos.size(); i++) {	    
 		ArchivoDesglose liq = (ArchivoDesglose) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);  		
  		row.addText(sdf1.format(liq.getPeriodo())  ); 
	 	row.addText(sdf.format(liq.getFecha_importacion())   );
	 	row.addText(liq.getUsuario() );
	 	row.addText(String.valueOf(liq.getTotalrecords()));
		row.addText(format2D.format(liq.getTotalpvp()));
		row.addText(format2D.format(liq.getTotalentidad()));
		row.addText(format2D.format(liq.getTotalospim()));
		row.addText(format2D.format(liq.getTotaluoma()));
	 	row.addText(format2D.format(liq.getTotalamtima()));
	 	
	 	StringBuilder sbo=new StringBuilder();
		    sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
			sbo.append(themeDisplay.getPathThemeImages());
			sbo.append("/common/download.png\"  title='" + "Exportación a Excel"  +"'");
			sbo.append(" onClick=\"javascript:exportacion('");
 		    sbo.append(sdf.format(liq.getPeriodo()) );
     	 	sbo.append("','Datos del Archivo Importado');\" />");
	        row.addText(sbo.toString());
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
<script type="text/javascript" >
function exportacion(periodo){
	window.location.href ='/xlsservlet/?reporte=REPORTE_ARCHIVO_PREVENCION_FARMACIA_DESGLOSE_PERIODO'
	+'&periodo='+periodo;
}



</script>
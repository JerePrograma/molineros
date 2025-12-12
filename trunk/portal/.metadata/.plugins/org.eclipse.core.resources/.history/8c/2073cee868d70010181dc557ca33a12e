
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

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
List<DebitosaTotal> archivos ;

archivos=BusquedaDebitosTercerizadorasServiceUtil.getArchivosDebitos(); 

List<String> headerNames = new ArrayList<String>();




headerNames.add("Periodo Prueba");
headerNames.add("Alta Fecha");
headerNames.add("Usuario");
headerNames.add("Tercerizadora");
headerNames.add("Hospitales");
headerNames.add("Reintegros");
headerNames.add("Prestadores");
headerNames.add("Liquidaciones Pendientes");
headerNames.add("Total Debito");
headerNames.add("Exportación");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-periodo-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < archivos.size(); i++) {	    
 		DebitosaTotal deb = (DebitosaTotal) archivos.get(i);
	 	ResultRow row = new ResultRow(deb,new Integer(1+i), i);  		
  		row.addText(sdf1.format(deb.getPeriodo())  ); 
	 	row.addText(sdf1.format(deb.getAltaFecha())   );
	 	row.addText(deb.getAltaUsr() );
	 	row.addText(deb.getDescTercerizadora());
		row.addText(format2D.format(deb.getMontoHospitales()));
		row.addText(format2D.format(deb.getMontoReintegros()));
		row.addText(format2D.format(deb.getMontoPrestadores()));
		row.addText(format2D.format(deb.getMontoLiquidacionPendiente()));
	 	row.addText(format2D.format(deb.getTotal()));
	 	
	 	StringBuilder sbo=new StringBuilder();
		    sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
			sbo.append(themeDisplay.getPathThemeImages());
			sbo.append("/common/download.png\"  title='" + "Exportación a Excel"  +"'");
			sbo.append(" onClick=\"javascript:exportacion('");
 		    sbo.append(deb.getPeriodo());
 		    sbo.append("','");	
 		    sbo.append(deb.getIdTercerizadora());  
 		   	sbo.append("');\" />");
			
	        row.addText(sbo.toString());
		resultRows.add(row);
	} 
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
<script type="text/javascript" >
function exportacion(periodo, idTercerizadora){

	var test = periodo.split("-");
	var anio = test[0];
	var mes = test[1];
	
	var periodo_aux = anio + '-' + mes; 
	 
	var url_sub = '/xlsservlet/?reporte=REPORTE_DEBITO_TERCERIZADORAS'
				  + '&fechaDesdeDia=01'
				  + '&fechaDesdeMes=' +(mes-1)
				  + '&fechaDesdeAnio=' +anio
				  +'&grabarDebitos=false'
				  +'&periodo='+periodo_aux 
  				  +'&tipo_debitos_tercerizadoras='+idTercerizadora
  				  +'&rnd=' + Math.floor(Math.random()*100);

				
	window.location.href =url_sub;
}



</script>
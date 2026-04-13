<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%

String ptoVtaAfip="00030";

try{
	ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
}catch(Exception e){
	//ptoVtaAfip="0000";
	ptoVtaAfip="00030";
}

PortletURL portletURL = renderResponse.createRenderURL();
NumberFormat df = new DecimalFormat("#0.00");
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

List<Reserva> archivos=(List<Reserva>)session.getAttribute(WebKeysHoteles.RESERVAS_RESULT);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Reserva");
headerNames.add("Habitacion");
headerNames.add("Apellido y Nombre");
headerNames.add("Desde");
headerNames.add("Hasta");
headerNames.add("Resumen");
   
//   headerNames.add("Editar|Baja|Docum|Cerrar|Recuperar|Cpte");
   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-reservas-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Reserva liq = (Reserva) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(String.valueOf(liq.getIdReserva()));
		row.addText(liq.getIdHabitacion());
		row.addText(liq.getApellido().trim()+" "+liq.getNombre() );
		row.addText(sdf.format(liq.getFechaDesde()));
		row.addText(sdf.format(liq.getFechaHasta()));
		
		StringBuilder sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;<img alt=\"Editar Producto\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/print.png\" onClick=\"javascript:imprimirResumen('");
	   	sb.append(ptoVtaAfip);
	    sb.append("',");
	    sb.append(liq.getAnio());
	    sb.append(",");
	    sb.append(liq.getIdReserva());
	    sb.append("");
	    sb.append(");\"");
        sb.append(" title=\"Editar\"");
	    sb.append("/>");
		
	    row.addText(sb.toString());  
				
		resultRows.add(row);
		
	}

}
%>
	
 		
	<script type="text/javascript">
	function imprimirResumen(hotel,anio,reserva){
		window.location.href ='/pdfservlet/?accion=resumenliquidacionreserva'
			+'&hotel='+hotel	
			+'&anio='+anio
			+'&reserva='+reserva;			
	}	
	
		
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
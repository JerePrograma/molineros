<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();
List<Prestamo> archivos=(List<Prestamo>)session.getAttribute(WebKeysHoteles.PRESTAMOS_RESULT);

String edit=(String)session.getAttribute("esEdicion");

boolean esEdicion = ParamUtil.getBoolean(request, "edit_mode", true);



SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
DecimalFormat df =new DecimalFormat("$#,###,##0.00");;
List<String> headerNames = new ArrayList<String>();
headerNames.add("Número");
headerNames.add("Fecha");
headerNames.add("Afiliado");
headerNames.add("Seccional");
//headerNames.add("CUIL");
//headerNames.add("Inte");
//headerNames.add("Cód.Hotel");
headerNames.add("Hotel");

headerNames.add("Cuotas");
headerNames.add("Total");
headerNames.add("Pagado");
headerNames.add("Saldo");
headerNames.add("Ultimo Recibo");
headerNames.add("Deuda Exigible");

if(edit==null ){
  headerNames.add("Ver Pagos");
  headerNames.add("Editar");
}   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestamos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Prestamo liq = (Prestamo) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(String.valueOf(liq.getId()));
		row.addText(liq.getAcuerdoFecha()!=null? sdf.format(liq.getAcuerdoFecha()):"");
		row.addText(String.format("%-50s",liq.getAfiliado().getApellido()));
		row.addText(String.format("%-50s",liq.getAfiliado().getSeccional()!=null && 
				liq.getAfiliado().getSeccional().getDescripcion()!=null?liq.getAfiliado().getSeccional().getDescripcion():
			""));
//		row.addText(liq.getAfiliado().getCuil_titular());
//		row.addText(String.valueOf(liq.getAfiliado().getInte()));
//		row.addText(liq.getHotel());
		row.addText(liq.getDescripcionHotel());
		
		row.addText(String.valueOf(liq.getCantidadCuotas()));
		row.addText(liq.getTotal()!=null?df.format(liq.getTotal()):"");
		row.addText(liq.getPagado()!=null?df.format(liq.getPagado()):"");
		row.addText(df.format((liq.getTotal()!=null?liq.getTotal():0) -
				 (liq.getPagado()!=null?liq.getPagado():0)));
		
		row.addText(liq.getUltimoRecibo()!=null?liq.getUltimoRecibo():"");
		row.addText(liq.getDeudaExigible()!=null?df.format(liq.getDeudaExigible()):"");
		
		if(edit==null ){
			StringBuilder sb1=new StringBuilder();
			sb1.append("&nbsp;&nbsp;<img alt=\"Ver Pagos Beneficio\" src=\"");
	        sb1.append(themeDisplay.getPathThemeImages());
		    sb1.append("/common/view.png\" onClick=\"javascript:verPagosPrestamo(");
		    sb1.append(liq.getId());
//		    sb.append("'");
		    sb1.append(");\"");
	        sb1.append(" title=\"Pagos\"");
		    sb1.append("/>");
		    row.addText(sb1.toString());	
			
			
		  StringBuilder sb=new StringBuilder();
		  sb.append("&nbsp;&nbsp;<img alt=\"Editar Beneficio\" src=\"");
          sb.append(themeDisplay.getPathThemeImages());
  	      sb.append("/common/edit.png\" onClick=\"javascript:editarPrestamo(");
	      sb.append(liq.getId());
//	    sb.append("'");
	      sb.append(");\"");
          sb.append(" title=\"Editar\"");
	      sb.append("/>");
	      row.addText(sb.toString());
		}
		  
				
		resultRows.add(row);
	}

}
%>


 		
	<script type="text/javascript">
	
	function editarPrestamo(prestamo){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_prestamo=" + prestamo;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}	
	
	
	function verPagosPrestamo(prestamo){
		var params = "&<%= Constants.CMD %>=" + "verPagos";
	 	params+="&id_prestamo=" + prestamo;
//		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
//		url = url + params;
		//document.<portlet:namespace />fm.method = 'post';
		//submitForm(document.<portlet:namespace />fm, url);

		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
		url = url + params;
		var popup = Liferay.Popup({title:"Pagos",modal:true,width:1000});
		jQuery(popup).load(url);
	 	
	}
	
	
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@page import="ar.com.ospim.tesoreria.beans.Recibo"%>
<%@page import="ar.com.ospim.tesoreria.beans.ReciboPrestamo"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>



<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

Prestamo pr=(Prestamo)session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);

List<Recibo> archivos=(List<Recibo>)session.getAttribute(WebKeysHoteles.PRESTAMO_PAGOS);

String edit=(String)session.getAttribute("esEdicion");

boolean esEdicion = ParamUtil.getBoolean(request, "edit_mode", true);



SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
DecimalFormat df =new DecimalFormat("$#,###,##0.00");;
List<String> headerNames = new ArrayList<String>();
headerNames.add("Número");
headerNames.add("Fecha");
headerNames.add("Pago");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				"No se encontraron pagos");
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Recibo liq = (Recibo) archivos.get(i);
		
		for(ReciboPrestamo rp : liq.getReciboPrestamos()){
		  ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		  PortletURL rowURL = renderResponse.createRenderURL();
		  rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		  row.addText(String.valueOf(liq.getNumero()));
		  row.addText(sdf.format(rp.getPrestamo().getAcuerdoFecha()) );
		  row.addText(rp.getPrestamo()!=null?df.format(rp.getPrestamo().getMonto()):"");
		  resultRows.add(row);
		}
	}

}

String portlet_name = ParamUtil.getString(request, "portlet_name");
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}else{
	portlet_name = "hoteles";
}

%>

<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />
</portlet:renderURL> 
<%if(!"farmacia".equalsIgnoreCase(portlet_name)){ %>
<p><a href="<%= volver %>">Volver a Edición</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>
<%}%>
<h1>Beneficio Nro. <%=pr!=null && pr.getId()!=null? pr.getId().toString() + "-" + ( pr.getAfiliado().getApellido()!=null?pr.getAfiliado().getApellido().toUpperCase():"") :""%></h1> 		
<script type="text/javascript">
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
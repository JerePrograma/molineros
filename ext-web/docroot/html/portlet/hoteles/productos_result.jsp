<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();
NumberFormat df = new DecimalFormat("#0.00");
List<ProductoConfiteria> archivos=(List<ProductoConfiteria>)session.getAttribute(WebKeysHoteles.PRODUCTOS_RESULT);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Código");
headerNames.add("Descripción");
headerNames.add("Descripción Corta");
headerNames.add("Categoría");
headerNames.add("Precio");
headerNames.add("Disponible Habitaciones");
headerNames.add("Editar|Eliminar");
   
//   headerNames.add("Editar|Baja|Docum|Cerrar|Recuperar|Cpte");
   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-productos-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ProductoConfiteria liq = (ProductoConfiteria) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(String.valueOf(liq.getCodigo()));
		row.addText(liq.getDescripcion());
		row.addText(liq.getDescripcionCorta());
		row.addText(liq.getCategoria().getDescripcion());
		row.addText(df.format(liq.getPrecio()));
		
		
		StringBuffer sb0 = new StringBuffer();
		sb0.append("<input type=\"checkbox\"");
		sb0.append("name=\"habitaciones\"");
		if(liq.isHabilitadoHabitaciones()){
			sb0.append("\" checked=\"checked");
		}
		sb0.append("id=\"");
		sb0.append("formu-"+   liq.getCodigo() );
	    sb0.append("\" value=\"");
		sb0.append(liq.getCodigo());									
		sb0.append("\"/>");
		row.addText(sb0.toString());
		
		StringBuilder sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;<img alt=\"Editar Producto\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/edit.png\" onClick=\"javascript:editarProducto('");
	    sb.append(liq.getHotel());
	    sb.append("','");
	    sb.append(liq.getCodigo()  );
	    sb.append("'");
	    sb.append(");\"");
        sb.append(" title=\"Editar\"");
	    sb.append("/>");
		
	    sb.append("&nbsp;&nbsp;<img alt=\"Eliminar Producto\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/delete.png\" onClick=\"javascript:eliminarProducto('");
	    sb.append(liq.getHotel());
	    sb.append("','");
	    sb.append(liq.getCodigo()  );
	    sb.append("'");
	    sb.append(");\"");
        sb.append(" title=\"Eliminar\"");
	    sb.append("/>");
	    
		row.addText(sb.toString());  
				
		resultRows.add(row);
	}

}
%>
	
 		
	<script type="text/javascript">
	function editarProducto(hotel,producto){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_hotel=" + hotel;
	 	params+="&id_producto=" + producto;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_productos_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}	
	
	
	function eliminarProducto(hotel,producto){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
           var categoria=jQuery("#<portlet:namespace />categoria_filtro").val();			
		   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_productos_abm" /></portlet:renderURL>';	
	       url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_hotel='+hotel+'&id_producto='+producto+'&categoria='+categoria;
	   	   jQuery('#<portlet:namespace />div_productos').load(url); 

		} 
				  
	}
	
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
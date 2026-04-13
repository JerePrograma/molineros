<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();
List<Mesa> archivos=(List<Mesa>)session.getAttribute(WebKeysHoteles.MESAS_RESULT);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Número");
headerNames.add("Descripción");
headerNames.add("Grupo");
headerNames.add("Editar|Eliminar");
   
//   headerNames.add("Editar|Baja|Docum|Cerrar|Recuperar|Cpte");
   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-mesas-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Mesa liq = (Mesa) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(String.valueOf(liq.getNumero()));
		row.addText(String.format("%-50s",liq.getDescripcion()));
		row.addText(liq.getGrupo());
		
		StringBuilder sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;<img alt=\"Editar Mesa\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/edit.png\" onClick=\"javascript:editarMesa('");
	    sb.append(liq.getHotel());
	    sb.append("','");
	    sb.append(liq.getNumero()  );
	    sb.append("'");
	    sb.append(");\"");
        sb.append(" title=\"Editar\"");
	    sb.append("/>");
		
	    sb.append("&nbsp;&nbsp;<img alt=\"Eliminar Mesa\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/delete.png\" onClick=\"javascript:eliminarMesa('");
	    sb.append(liq.getHotel());
	    sb.append("','");
	    sb.append(liq.getNumero()  );
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
	function editarMesa(hotel,mesa){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_hotel=" + hotel;
	 	params+="&id_mesa=" + mesa;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_mesas_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}	
	
	
	function eliminarMesa(hotel,mesa){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
		   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_mesas_abm" /></portlet:renderURL>';	
	       url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_hotel='+hotel+'&id_mesa='+mesa;
	   	   jQuery('#<portlet:namespace />div_mesas').load(url); 

		} 
				  
	}
	
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
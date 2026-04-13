<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

String portlet_name=null;
portlet_name = "tesoreria";
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

List<CentroCosto> archivos=(List<CentroCosto>)session.getAttribute(WebKeysUOMA.CENTRO_COSTO_FILTRO);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id");
headerNames.add("Descripción");
headerNames.add("Presupuesto");
headerNames.add("Vigencia Desde");
headerNames.add("Vigencia Hasta");
headerNames.add("");
headerNames.add("");


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "centrocosto-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	NumberFormat formatter = new DecimalFormat("#0.00");     
	
	for (int i = 0; i < archivos.size(); i++) {	    
		CentroCosto liq = (CentroCosto) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		row.addText(liq.getId().toString());
		row.addText(liq.getDescripcion());
		row.addText(formatter.format(liq.getPresupuesto()));
		row.addText(sdf.format(liq.getVigenciaDde()));
		row.addText(liq.getVigenciaHta()!=null?sdf.format(liq.getVigenciaHta()):"");
		
		StringBuilder sb = new StringBuilder();
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/edit.png\" onClick=\"javascript:editarCentroCosto('");
	    sb.append(liq.getId() );
	    sb.append("');\"");
	    sb.append(" title=\"Editar\"");
	    sb.append("/>");
	    row.addText(sb.toString());
	    
	    
	    StringBuilder sb1 = new StringBuilder();
		sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Eliminar\" src=\"");
        sb1.append(themeDisplay.getPathThemeImages());
	    sb1.append("/common/delete.png\" onClick=\"javascript:eliminarCentroCosto('");
	    sb1.append(liq.getId() );
	    sb1.append("');\"");
	    sb1.append(" title=\"Eliminar\"");
	    sb1.append("/>");
	    row.addText(sb1.toString());
		
	    resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">
var autorizacionEnEdicion;

function editarCentroCosto(id_Centro){
	
 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
 	params+="&id_centro_costo=" + id_Centro;
 	params+="&usuario_modi=" +"<%=usuario_modi%>";
// 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/centro_costo_edicion" /></portlet:renderURL>';
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';	
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);	
 	
}

function eliminarCentroCosto(id_Centro){
	if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		return false;
	}else{	
//       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/centro_costo_edicion';
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';
   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_centro_costo='+id_Centro+'&usuario_modi='+'<%=usuario_modi%>';
   	   jQuery("#<portlet:namespace />listado_centro").load(url); 
	}   
}

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>


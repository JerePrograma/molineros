<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

String portlet_name=null;
Integer entidad = WebKeysGlobal.OSPIM;
portlet_name = "tesoreria";
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad = WebKeysGlobal.UOMA;
}

List<CentroCosto> archivos=(List<CentroCosto>)session.getAttribute(WebKeysUOMA.CENTRO_COSTO_FILTRO);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id");
headerNames.add("Descripción");
headerNames.add("Presupuesto");
headerNames.add("Ejecutado");
headerNames.add("Saldo");
headerNames.add("Detalle");

NumberFormat formatter = new DecimalFormat("###,###,###,###,##0.00");    

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "centrocosto-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		CentroCosto liq = (CentroCosto) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		row.addText(liq.getId().toString());
		row.addText(liq.getDescripcion());
		row.addText(formatter.format(liq.getPresupuesto()));
		row.addText(formatter.format(liq.getEjecutado()));
		row.addText(formatter.format(liq.getPresupuesto()-liq.getEjecutado()));
		
		StringBuilder sb = new StringBuilder();
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Ver Detalle\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/view.png\" onClick=\"javascript:detalleCentroCosto('");
	    sb.append(liq.getId() );
	    sb.append("');\"");
	    sb.append(" title=\"Ver Detalle\"");
	    sb.append("/>");
	    row.addText(sb.toString());
	    
	    resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">
var autorizacionEnEdicion;

function detalleCentroCosto(id_Centro){
	
 	var params = "&<%= Constants.CMD %>=" + "detalleComprobantes";
 	params+="&id_centro_costo=" + id_Centro;
 	params+="&usuario_modi=" +"<%=usuario_modi%>";
 	params+= "&entidad_centro="+"<%=entidad%>";

 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/centro_costo_edicion" /></portlet:renderURL>';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);	
 	
}


</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>


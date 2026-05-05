<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>

<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>

<%String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}

if (request.getAttribute("esEditable") != null){
	esEditableStr = (String)request.getAttribute("esEditable");
}
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
boolean esEdicion = Boolean.parseBoolean(esEditableStr); 
Integer entidad =0;

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
	entidad=WebKeysGlobal.OSPIM;
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
	entidad=WebKeysGlobal.AMTIMA;
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad=WebKeysGlobal.UOMA;
} 
List<CentroCosto> pCentros = (List<CentroCosto>)request.getAttribute("centrosCosto");
if(pCentros==null) pCentros=new ArrayList<CentroCosto>();


portletSession.setAttribute("centrosCostoEnSession", pCentros,PortletSession.APPLICATION_SCOPE);

%>
		

<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" 
				title="<liferay-ui:message key="exportar-busqueda" />" type="button" />


<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Cto.Costo");
			 		headerNames.add("Cto.Costo Desc");
			 		
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=pCentros ){					
					 	int total = pCentros.size();
					 	searchContainer.setTotal(total);
					 	pageContext.setAttribute("total", total);	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < pCentros.size(); i++) {
					 		CentroCosto comp =  pCentros.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 							 					
		 					Boolean marcar=false;
		 					
		 					row.addText(comp.getId().toString());
	                        row.addText(comp.getDescripcion());
		 				    
		 				    		 					
	 						resultRows.add(row);
						}
					 }
			%>

    <%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />	
	
<script type="text/javascript">
jQuery('#<portlet:namespace />exportar-busqueda').click(function exportarBusqueda(){
		window.location.href ='/xlsservlet/?reporte=REPORTE_EXPORTAR_CENTROS_COSTOS_CONTABLES';       

});
</script>	
    		
	

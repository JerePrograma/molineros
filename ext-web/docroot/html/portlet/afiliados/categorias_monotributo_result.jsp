<%@page import="ar.com.ospim.util.DateUtils"%>
<%@page import="ar.com.ospim.global.beans.AportesMonotributo" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
			<%
			
			String portlet_name = ParamUtil.getString(request, "portlet_name");
			if (portlet_name == null || portlet_name.trim().equals("")){
			    portlet_name = "afiliados";
			}
			
			List<AportesMonotributo> aportesList= TraeListasServiceUtil.getAportesMonotributo(null);
			
			PortletURL portletURL = renderResponse.createRenderURL();				
			String orderByCol = ParamUtil.getString(request, "orderByCol");
			String orderByType = ParamUtil.getString(request, "orderByType");
		 	List<String> headerNames = new ArrayList<String>();
		 	headerNames.add("Categoría");
		 	headerNames.add("Descripción");
		 	headerNames.add("ID");
		 	headerNames.add("Desde");
		 	headerNames.add("Hasta");
			headerNames.add("Aporte");
			headerNames.add("");
			headerNames.add("");
			
			SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
			if(null!=aportesList){
	 								 	
	 			int total = aportesList.size();
				searchContainer.setTotal(total);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				DecimalFormat df =new DecimalFormat("$#,###,##0.00");
				List resultRows = searchContainer.getResultRows();
				for (int i = 0; i < aportesList.size(); i++) {
					AportesMonotributo aporte = (AportesMonotributo) aportesList.get(i);
	 				ResultRow row = new ResultRow(aporte,aporte.getId(), i);
	 				row.addText(String.valueOf(aporte.getCategoria()));
	 				row.addText(aporte.getDescripcion());
	 				row.addText(String.valueOf(aporte.getId()));
	 				row.addText(sdf.format(aporte.getDesde()));
	 				row.addText(sdf.format(aporte.getHasta()));	
	 				row.addText(df.format(aporte.getAporte()));

	 				StringBuilder sb=new StringBuilder();
 				    sb.append("&nbsp;&nbsp;<img alt=\"Editar Aporte\" src=\"");
	 		        sb.append(themeDisplay.getPathThemeImages());
	 		  	    sb.append("/common/edit.png\" onClick=\"javascript:editarAporte(");
	 			    sb.append(aporte.getId());
	 			    sb.append(");\"");
	 		        sb.append(" title=\"Editar\"");
	 			    sb.append("/>");
	 			    row.addText(sb.toString());	
						
	 			    StringBuilder sbD=new StringBuilder();
				    sbD.append("&nbsp;&nbsp;<img alt=\"Eliminar Aporte\" src=\"");
	 		        sbD.append(themeDisplay.getPathThemeImages());
	 		  	    sbD.append("/common/delete.png\" onClick=\"javascript:eliminarAporte(");
	 			    sbD.append(aporte.getId());
	 			    sbD.append(");\"");
	 		        sbD.append(" title=\"Eliminar\"");
	 			    sbD.append("/>");
	 			    row.addText(sbD.toString());		
						
						
			 		resultRows.add(row);
				}
	 	}
 		%>
<form action="" method="post" name="<portlet:namespace />fm"> 		
<fieldset>
  <input type="button" value="Nuevo"
				onClick="<portlet:namespace />nuevoAporte();" />&nbsp;
</fieldset>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
</form>	
	
<script type="text/javascript">

function <portlet:namespace />nuevoAporte() {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo_action" /></portlet:renderURL>';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);		
}
	
function editarAporte(id){
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_aporte=" + id;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo_action" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
}	
	
	
function eliminarAporte(id){
	if(confirm("Esta seguro de Eliminar la Categoría?")){	
 	  var params = "&<%= Constants.CMD %>=" + "<%= Constants.DELETE%>";
 	  params+="&id_aporte=" + id;
	  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo_action" /></portlet:renderURL>';
	  url = url + params;
	  document.<portlet:namespace />fm.method = 'post';
	  submitForm(document.<portlet:namespace />fm, url);
	}  
}		
	
	
	 
</script>	
	

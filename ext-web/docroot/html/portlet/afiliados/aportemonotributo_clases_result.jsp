<%@page import="ar.com.ospim.util.DateUtils"%>
<%@page import="ar.com.ospim.global.beans.AportesMonotributo" %>
<%@page import="ar.com.ospim.global.beans.AportesMonotributoClase" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
			
			String portlet_name = ParamUtil.getString(request, "portlet_name");
			if (portlet_name == null || portlet_name.trim().equals("")){
			    portlet_name = "afiliados";
			}
			
			AportesMonotributo aporte = (AportesMonotributo)session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);
			
			
			PortletURL portletURL = renderResponse.createRenderURL();				
			String orderByCol = ParamUtil.getString(request, "orderByCol");
			String orderByType = ParamUtil.getString(request, "orderByType");
		 	List<String> headerNames = new ArrayList<String>();
		 	headerNames.add("Clase");
		 	headerNames.add("Desde");
		 	headerNames.add("Hasta");
			headerNames.add("Aporte");
			headerNames.add("");
			//headerNames.add("");
			
			SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
			List<AportesMonotributoClase> clases = aporte.getClases();
			
			if(null!=clases){
	 								 	
	 			int total = clases.size();
				searchContainer.setTotal(total);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				DecimalFormat df =new DecimalFormat("$#,###,##0.00");
				List resultRows = searchContainer.getResultRows();
				for (int i = 0; i < clases.size(); i++) {
					AportesMonotributoClase clase = (AportesMonotributoClase) clases.get(i);
	 				ResultRow row = new ResultRow(clase,clase.getClase(), i);
	 				row.addText(clase.getClase());
	 				row.addText(sdf.format(clase.getDesde()));
	 				row.addText(sdf.format(clase.getHasta()));	
	 				row.addText(df.format(clase.getAporte()));

/*	 				
	 				StringBuilder sb=new StringBuilder();
 				    sb.append("&nbsp;&nbsp;<img alt=\"Editar Aporte\" src=\"");
	 		        sb.append(themeDisplay.getPathThemeImages());
	 		  	    sb.append("/common/edit.png\" onClick=\"javascript:editarClase(");
	 			    sb.append(clase.getId());
	 			    sb.append(");\"");
	 		        sb.append(" title=\"Editar\"");
	 			    sb.append("/>");
	 			    row.addText(sb.toString());	
*/						
	 			    StringBuilder sbD=new StringBuilder();
				    sbD.append("&nbsp;&nbsp;<img alt=\"Eliminar Aporte\" src=\"");
	 		        sbD.append(themeDisplay.getPathThemeImages());
	 		  	    sbD.append("/common/delete.png\" onClick=\"javascript:eliminarAporteClase(");
	 			    sbD.append(clase.getId());
	 			    sbD.append(");\"");
	 		        sbD.append(" title=\"Eliminar\"");
	 			    sbD.append("/>");
	 			    row.addText(sbD.toString());		
						
						
			 		resultRows.add(row);
				}
	 	}
 		%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">
	 
</script>	

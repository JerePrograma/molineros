<%@page import="ar.com.ospim.hoteles.services.WebKeysHoteles"%>
<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();
Prestamo prestamo=(Prestamo)request.getSession().getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
List<PrestamoCuota> archivos=prestamo.getCuotas();
SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
DecimalFormat df =new DecimalFormat("$#,###,##0.00");;
List<String> headerNames = new ArrayList<String>();
headerNames.add("Número");
headerNames.add("Vencimiento");
headerNames.add("Importe");
//headerNames.add("Pagado");
headerNames.add("Editar");
headerNames.add("Eliminar");
headerNames.add("");

boolean showPrestamosSeccional=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_PRESTAMOS_TURISMO_SECCIONAL);

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				"Cuotas no encontradas");
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		PrestamoCuota liq = (PrestamoCuota) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(String.valueOf(liq.getNumero()));
		row.addText(liq.getVencimiento()!=null? sdf.format(liq.getVencimiento()):"");
		row.addText(liq.getImporte()!=null?df.format(liq.getImporte()):"");
//		row.addText(liq.getPagado()!=null?df.format(liq.getPagado()):"");
		StringBuilder sb=new StringBuilder();
		
		if(!showPrestamosSeccional){
		   sb.append("&nbsp;&nbsp;<img alt=\"Editar cuota\" src=\"");
           sb.append(themeDisplay.getPathThemeImages());
	       sb.append("/common/edit.png\" onClick=\"javascript:editarPrestamoCuota(");
	       sb.append(liq.getNumero());
	       sb.append(",'");
	       sb.append(sdf.format(liq.getVencimiento()));
	       sb.append("',");
	       sb.append(liq.getImporte());
	       sb.append(");\"");
           sb.append(" title=\"Editar\"");
	       sb.append("/>");
	       row.addText(sb.toString());
		}else{
			  row.addText(""); 
		}
		  
		if(liq.getPagado()==null || liq.getPagado()==0){
		  StringBuilder sb1=new StringBuilder();
		  if(!showPrestamosSeccional){
		     sb1.append("&nbsp;&nbsp;<img alt=\"Eliminar cuota\" src=\"");
             sb1.append(themeDisplay.getPathThemeImages());
	         sb1.append("/common/delete.png\" onClick=\"javascript:eliminarPrestamoCuota(");
	         sb1.append(liq.getNumero());
	         sb1.append(");\"");
             sb1.append(" title=\"Eliminar\"");
	         sb1.append("/>");
		     row.addText(sb1.toString());
		  }else{
			  row.addText(""); 
		  }
		}else{
		  row.addText("");	
		}
		
		row.addText(liq.getModificada()!=null  && liq.getModificada() ?"Modificada":"");
		resultRows.add(row);
	}

}
%>
	
 		
	<script type="text/javascript">
		
	
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
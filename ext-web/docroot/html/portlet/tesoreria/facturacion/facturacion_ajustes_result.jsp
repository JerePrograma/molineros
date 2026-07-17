<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
		%>
		<table  style="color:red" >
		<%
		for (String error : errores){
			%>
			<tr><td>
			<%=error%>
			</td></tr>
			<%
		}
		%>
		</table>
		<%
}

%>
<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();
List<AjustePlanSuperador> archivos=(List<AjustePlanSuperador>)session.getAttribute(WebKeysTesoreria.AJUSTES_RESULT );

List<String> headerNames = new ArrayList<String>();
//headerNames.add("");
headerNames.add("Id");
headerNames.add("Descripción");
headerNames.add("Vigente Dde");
headerNames.add("Vigente Hta");
headerNames.add("Edad Dde");
headerNames.add("Edad Hta");
headerNames.add("Planes");
headerNames.add("Parentescos");
headerNames.add("Provincias");
headerNames.add("Afiliados");
headerNames.add("Porcentaje");
headerNames.add("Importe");
headerNames.add("Solo Uso Personalizado");
headerNames.add("Editar");
headerNames.add("Eliminar");
      

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				"No se han encontrado items");
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	DecimalFormat df = new DecimalFormat("0.00");
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		AjustePlanSuperador paut = (AjustePlanSuperador) archivos.get(i);
	 	ResultRow row = new ResultRow(paut,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		Boolean marcar=false;		
		/*
		StringBuffer sb0 = new StringBuffer();
		sb0.append("<input type=\"checkbox\"");
		sb0.append("name=\"precio\"");
		if(marcar){
				sb0.append("\" checked=\"checked");
		}
		sb0.append("id=\"");
		sb0.append("formu-"+paut.getId());
	    sb0.append("\" value=\"");
		sb0.append(paut.getId());									
		sb0.append("\"/>");
		row.addText(sb0.toString());
		*/
		
		row.addText(String.valueOf(paut.getId()));
		row.addText(paut.getDescripcion());
		row.addText( paut.getFechaDesdeAsString());
		row.addText( paut.getFechaHastaAsString());
		row.addText(String.valueOf(paut.getEdadDesde()));
		row.addText(String.valueOf(paut.getEdadHasta()));
		row.addText(paut.getPlanesString());
		row.addText(paut.getParentescosString());
		row.addText(paut.getProvinciasString());
		row.addText(paut.getAfiliadosString());
		row.addText(df.format(paut.getPorcentaje()));
		row.addText(paut.getImporte().toPlainString());
		row.addText(paut.getSoloUsoPersonalizado()?"SI":"");
		
		StringBuilder sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;<img alt=\"Editar Precio\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/edit.png\" onClick=\"javascript:editarAjuste(");
	    sb.append(paut.getId() );
	    sb.append("");
	    sb.append(");\"");
        sb.append(" title=\"Editar\"");
	    sb.append("/>");
	    row.addText(sb.toString()); 
		
	    StringBuilder sbEL=new StringBuilder();
		sbEL.append("&nbsp;&nbsp;<img alt=\"Eliminar Precio\" src=\"");
        sbEL.append(themeDisplay.getPathThemeImages());
	    sbEL.append("/common/delete.png\" onClick=\"javascript:eliminarAjuste(");
	    sbEL.append(paut.getId() );
	    sbEL.append("");
	    sbEL.append(");\"");
        sbEL.append(" title=\"Eliminar\"");
	    sbEL.append("/>");
	    row.addText(sbEL.toString());
	    
		resultRows.add(row);
	}
}
%>
	
 		
	<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();
	 var popUpCierre;
    var popUpComprobante;
    
	function editarAjuste(id_Precio){
		
	
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_precio=" + id_Precio;
	 	params+="&usuario_modi=" +"<%=usuario_modi%>";
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action"
	 	value="/tesoreria/facturacion_ajustes" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}	
	
	
	function eliminarAjuste(id_Precio){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/tesoreria/facturacion_ajustes';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_precio='+id_Precio;
	   	   
	   	   jQuery('#<portlet:namespace />ajustesDiv').load(url); 

		}   
	}
	
	
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
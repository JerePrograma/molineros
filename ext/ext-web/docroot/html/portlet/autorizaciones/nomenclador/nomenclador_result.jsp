<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

//List<Nomenclador> archivos=(List<Nomenclador>)renderRequest.getAttribute("Nomenclador");

List<Nomenclador> archivos=(List<Nomenclador>)session.getAttribute("Nomenclador");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Tipo");
headerNames.add("Código");
headerNames.add("Descripción");
headerNames.add("Especialidad");
headerNames.add("Recupera SUR");
headerNames.add("Importe");
headerNames.add("Fecha Baja");
headerNames.add("Editar|Baja|Recuperar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "nomenclador-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		Nomenclador liq = (Nomenclador) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);	
        
		row.addText(liq.getDescripcionTipoNomenclador());
		row.addText(liq.getCodigo());
		row.addText(liq.getDescripcion());
		row.addText(liq.getEspecialidadDescripcion());
		row.addText(liq.getRecuperaSUR()?"Si":"No");
		row.addText(String.valueOf(liq.getImporte()));
		row.addText(liq.getBaja_fecha()!=null?liq.getBaja_Fecha_string():"");
		
		StringBuilder sb=new StringBuilder();
	 	if(liq.getBaja_fecha()!=null){
	 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
	 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
	 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Recuperar nomenclador\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/undo.png\" onClick=\"javascript:recuperarNomenclador('");
	 		sb.append(liq.getId_prestacion() );
	 		sb.append("');\" />");
	 		row.addText(sb.toString());  
	 	}else {	
	 		
	 		if(liq.getId_tipo_nomenclador()==1 || liq.getId_tipo_nomenclador()==6 //Agregado para poder cargar el importe por plan 2020-11-25   
	 				|| liq.getId_tipo_nomenclador()==3 || liq.getId_tipo_nomenclador()==9 || liq.getId_tipo_nomenclador()==10 || liq.getId_tipo_nomenclador()==11 || liq.getId_tipo_nomenclador()==12){
			    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar nomenclador\" src=\"");
			    sb.append(themeDisplay.getPathThemeImages());
	 		    sb.append("/common/edit.png\" onClick=\"javascript:editarNomenclador('");
	 		    sb.append(liq.getId_prestacion() );
	 		    sb.append("');\" />");
	 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Baja nomenclador\" src=\"");
			    sb.append(themeDisplay.getPathThemeImages());
	 		    sb.append("/common/delete.png\" onClick=\"javascript:bajaNomenclador('");
	 		    sb.append(liq.getId_prestacion() );
	 		    sb.append("');\" />");
	 		}else{
	 			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Consultar nomenclador\" src=\"");
			    sb.append(themeDisplay.getPathThemeImages());
	 		    sb.append("/common/edit.png\" onClick=\"javascript:consultarNomenclador('");
	 		    sb.append(liq.getId_prestacion() );
	 		    sb.append("');\" />");
	 		}
	 		row.addText(sb.toString());
	 	}
		resultRows.add(row);
	}
}
%>
	
 		
	<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	var autorizacionEnEdicion;

	function editarNomenclador(id_Prestacion){
        var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_nomenclador=" + id_Prestacion;
	 	params+="&usuario_modi=" +"<%=usuario_modi%>";
	 	params+="&accion=edit"
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_nomenclador" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	}	
			
	 
	function bajaNomenclador(id_Prestacion){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_nomenclador';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_nomenclador='+id_Prestacion+'&usuario_modi='+'<%=usuario_modi%>';
	   	   jQuery("#<portlet:namespace />listado_nomenclador").load(url); 
		}   
	}
	
	function recuperarNomenclador(id_Prestacion){
		if(!confirm("Desea recuperar el nomenclador dado de baja?")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_nomenclador';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.RESTORE %>'+'&id_nomenclador='+id_Prestacion+'&usuario_modi='+'<%=usuario_modi%>';
	   	   jQuery("#<portlet:namespace />listado_nomenclador").load(url); 
		}   
	}
	
	function consultarNomenclador(id_Prestacion){
		
		 var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
		 params+="&id_nomenclador=" + id_Prestacion;
		 params+="&usuario_modi=" +"<%=usuario_modi%>";
		 params+="&accion=view"
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_nomenclador" /></portlet:renderURL>';
		 url = url + params;
		 document.<portlet:namespace />fm.method = 'post';
		 submitForm(document.<portlet:namespace />fm, url);	
	}

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
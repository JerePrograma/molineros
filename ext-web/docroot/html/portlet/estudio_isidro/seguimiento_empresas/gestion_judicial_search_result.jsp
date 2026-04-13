<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}

if (request.getAttribute("esEditable") != null){
	esEditableStr = (String)request.getAttribute("esEditable");
}
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
boolean esEdicion = Boolean.parseBoolean(esEditableStr); 

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "estudio_isidro";
}
NumberFormat format2D = new DecimalFormat("#0.00");

String[] tiposStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_DEMANDA").split(";");
	Map<String,String> tiposJud=new HashMap<String,String>();
	for(int i=0;i<=tiposStr.length-1;i++){
		String codigo = tiposStr[i].split("=")[0];
		String descripcion = tiposStr[i].split("=")[1];
		tiposJud.put(codigo,descripcion);
	}
	

String[] estadosStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_ESTADOS").split(";");
	Map<String,String> estadosJud=new HashMap<String,String>();
		for(int i=0;i<=estadosStr.length-1;i++){
			String codigo = estadosStr[i].split("=")[0];
			String descripcion = estadosStr[i].split("=")[1];
			estadosJud.put(codigo,descripcion);
	}

%>
		


<%-- <%if(portlet_name!=null && !portlet_name.equalsIgnoreCase("tesoreria")) {%> --%>
<liferay-ui:error key="regimenError" message="<%=(String)request.getAttribute(\"msgError2\") %>"  />
<liferay-ui:error key="exencionError" message="<%=(String)request.getAttribute(\"msgError3\") %>"  />
<liferay-ui:error key="exencionUrlError" message="<%=(String)request.getAttribute(\"msgError4\") %>"  />
<%-- <%} %> --%>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					List<DemandaJudicial> comprobantes = (ArrayList<DemandaJudicial>)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDAS_RESULT);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Entidad");
			 		headerNames.add("ID");
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("sucursal");
			 		headerNames.add("razon-social");
			 		
			 		headerNames.add("Expediente");
			 		headerNames.add("Tipo");
			 		headerNames.add("Monto Original");
			 		headerNames.add("Fecha");
					headerNames.add("estado");
					headerNames.add("Comandos");
					headerNames.add("");
					headerNames.add("");
					
					if(esEdicion) { 
						headerNames.add("Borrar");
					}			
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-entries-were-found"));
				
					if(null!=comprobantes){					
					 	int total = comprobantes.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		DemandaJudicial comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					row.addText(comp.getEntidad());
		 					row.addText( comp.getId().toString());
		 					row.addText( comp.getCuit());
		 					row.addText( comp.getSucursal());
		 					row.addText( comp.getRazonSocial());
		 					row.addText( comp.getExpediente());
		 					row.addText( tiposJud.get(comp.getTipo()));
		 					row.addText( format2D.format(comp.getMontoOriginal()));
		 					row.addText( comp.getFechaAsString());
		 					row.addText(estadosJud.get(comp.getUltimoEstado()));
		 					
		 					StringBuilder sb= new StringBuilder();
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/edit.png\" onClick=\"javascript:editaDemanda(");
			 				sb.append(comp.getId());
			 				sb.append(");\" />");
			 				row.addText(sb.toString());			
	 						
	 						StringBuilder sbImg= new StringBuilder();
	 						sbImg.append("");
	 						sbImg.append("&nbsp;&nbsp;<img alt=\"Imagenes Comprobantes\" src=\"");
	 						sbImg.append(themeDisplay.getPathThemeImages());
	 						sbImg.append("/common/preview.png\" onClick=\"javascript:imagenesDemandas(");
	 						sbImg.append(comp.getId());
		 					sbImg.append(");\"");
	 						sbImg.append(" title=\"Imagenes\"");
	 						sbImg.append("/>");
	 						row.addText(sbImg.toString());	
	 						
	 						
	 						StringBuilder sbDel= new StringBuilder();
	 						sbDel.append("&nbsp;&nbsp;<img alt=\"Eliminar preautorizacion\" src=\"");
	 						sbDel.append(themeDisplay.getPathThemeImages());
	 				 		sbDel.append("/common/delete.png\" onClick=\"javascript:eliminaDemanda(");
	 				 		sbDel.append(comp.getId() );
	 				 		sbDel.append(");\"");
	 		                sbDel.append(" title=\"Elimina\"");
	 			 		    sbDel.append("/>");
	 			 		    row.addText(sbDel.toString());	
	 						
	 						resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
    <liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/paginador_gestion_judicial.jsp">
    </liferay-util:include>		
	
<script type="text/javascript">
   
	function editaDemanda(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/demandas_editar" /></portlet:renderURL>';
        url += "&accion=edit";
        url += '&id_demanda='+id;
        url += '&cmd=edit';
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function imagenesDemandas(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/demandas_editar" /></portlet:renderURL>';
        url += "&accion=imagenes";
        url += '&id_demanda='+id;
        url += '&cmd=imagenes';
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function eliminaDemanda(id){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/estudio_isidro/demandas_editar';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_demanda='+id+'&usuario='+'<%=user.getScreenName()%>';
	   	   jQuery('#<portlet:namespace />busquedaDemandasDiv').load(url); 

		}   
	}
	
</script>	

<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.WorkflowDefinition" %>
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


boolean rolAdministradorCajaChica = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA);
boolean rolAdministradorCajaChicaSinOP = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA_SIN_OP);
boolean rolUsuarioCajaChica = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_USUARIO_CAJA_CHICA);

List<CajaChica> archivos=(List<CajaChica>)session.getAttribute("ListaCajasChicas");

List<String> headerNames = new ArrayList<String>();
headerNames.add("Descripción");
headerNames.add("Fecha Alta");
headerNames.add("Ultima Reposición");
headerNames.add("Solicitud Reposición");
headerNames.add("Estado");
headerNames.add("Saldo");
headerNames.add("Editar");
headerNames.add("Asig");
headerNames.add("Mov.Rend.");
headerNames.add("Eg/Rend");
headerNames.add("Reporte");
headerNames.add("Recibo");



SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "cajachica-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	NumberFormat formatter = new DecimalFormat("#0.00");     
	
	for (int i = 0; i < archivos.size(); i++) {	    
		CajaChica liq = (CajaChica) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		WorkflowDefinition wd = liq.getEstado();
		String estadoId =TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_SOLICITA_REPOSICION");
		String fechaSolicitud = "";
		
		if(Integer.toString(wd.getId()).equals(estadoId)){
			fechaSolicitud=sdf.format(wd.getFecha()) ;
		}
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		row.addText(liq.getDescripcion() );
		row.addText(liq.getAlta_fecha()!=null?liq.getAlta_Fecha_string():"");
		row.addText(liq.getUltimaReposicion_string());
		row.addText(fechaSolicitud);
		row.addText(liq.getEstado().getDescripcion()==null?"":liq.getEstado().getDescripcion());
		row.addText(formatter.format(liq.getSaldo()));
		
		StringBuilder sb = new StringBuilder();
		if(rolAdministradorCajaChica || rolAdministradorCajaChicaSinOP){
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar\" src=\"");
           sb.append(themeDisplay.getPathThemeImages());
	       sb.append("/common/edit.png\" onClick=\"javascript:editarCajaChica('");
	       sb.append(liq.getId() );
	       sb.append("');\"");
	       sb.append(" title=\"Editar\"");
	       sb.append("/>");
	       row.addText(sb.toString());
		}else{
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		   row.addText(sb.toString());
		}
		
		if(liq.getEstado().getId()== WebKeysCajaChica.SOLICITAREPOSICION && (rolAdministradorCajaChica || rolAdministradorCajaChicaSinOP)){
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Asignar-Aumentar\" src=\"");
           sb.append(themeDisplay.getPathThemeImages());
	       sb.append("/common/add.png\" onClick=\"javascript:asignarCajaChica('");
	       sb.append(liq.getId() );
	       sb.append("');\"");
	       if(liq.getAsignado()==0){
              sb.append(" title=\"Asignar\"");
	       }else{
	    	   sb.append(" title=\"Aumentar\""); 
	       }
	       sb.append("/>");
	       row.addText(sb.toString());
		}else{
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		   row.addText(sb.toString());
		}
		
		
// Preparar para Rendicion
		if(
		   ((liq.getEstado().getId()== WebKeysCajaChica.SOLICITAREPOSICION  && (rolAdministradorCajaChica || rolAdministradorCajaChicaSinOP ))
		    || 
		    (liq.getEstado().getId()== WebKeysCajaChica.REPOSICIONAPROBADASINOP && rolAdministradorCajaChica) ) ||
		    ("uoma".equalsIgnoreCase(portlet_name) && rolAdministradorCajaChica) 
		     &&
		    liq.getComprobantesEnviadosARendicionResumido().size()>0){
			   sb = new StringBuilder();
			   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Movimientos-Rendición\" src=\"");
	           sb.append(themeDisplay.getPathThemeImages());
		       sb.append("/common/recent_changes.png\" onClick=\"javascript:controlarRendicionCajaChica('");
		       sb.append(liq.getId() );
		       sb.append("');\"");
	           sb.append(" title=\"Movimientos Rendición\"");
		       sb.append("/>");
		       
		       row.addText(sb.toString());
		       
		}else{
			   sb = new StringBuilder();
			   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			   row.addText(sb.toString());
		}

// Fin preparar para Rendicion
		
		if(rolUsuarioCajaChica || ("uoma".equalsIgnoreCase(portlet_name)) ){
			   sb = new StringBuilder();
			   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Egreso-Rendición\" src=\"");
	           sb.append(themeDisplay.getPathThemeImages());
		       sb.append("/common/add_article.png\" onClick=\"javascript:ejecucionCajaChica('");
		       sb.append(liq.getId() );
		       sb.append("');\"");
		       sb.append(" title=\"Egreso-Rendición\"");
		       
		       sb.append("/>");
		       

		       if("uoma".equalsIgnoreCase(portlet_name)){
		    	   sb.append("<a href=\"javascript:void(0)\" onclick=\"help(event, 'helpEjecucion')\"><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>&nbsp;");  
		       }
		       
		       
		       row.addText(sb.toString());
	  }else{
			   sb = new StringBuilder();
			   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			   row.addText(sb.toString());
	  }

	  sb = new StringBuilder();
	  sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Reporte\" src=\"");
      sb.append(themeDisplay.getPathThemeImages());
	  sb.append("/common/print.png\" onClick=\"javascript:reporteCajaChica('");
	  sb.append(liq.getId() );
	  sb.append("');\"");
	  sb.append(" title=\"reporte\"");
	  sb.append("/>");
	  
	  if("uoma".equalsIgnoreCase(portlet_name)){
	   	   sb.append("<a href=\"javascript:void(0)\" onclick=\"help(event, 'helpReporte')\"><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>&nbsp;");  
	  }
	  
	  row.addText(sb.toString());
	  
	  // Recibo por comprobantes de seccional
	  
	  if(
   	      (((liq.getEstado().getId()== WebKeysCajaChica.SOLICITAREPOSICION  && (rolAdministradorCajaChica || rolAdministradorCajaChicaSinOP ))
				    || 
					    (liq.getEstado().getId()== WebKeysCajaChica.REPOSICIONAPROBADASINOP && rolAdministradorCajaChica) ) ||
					    ("uoma".equalsIgnoreCase(portlet_name) ) )
					     &&
				    liq.getComprobantesEnviadosARendicionResumido().size()>0  && liq.getPideSeccionalGasto()
			  ){
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Movimientos-Rendición\" src=\"");
		   sb.append(themeDisplay.getPathThemeImages());
		   sb.append("/common/manage_task.png\" onClick=\"javascript:emisionReciboCajaChica('");
		   sb.append(liq.getId() );
		   sb.append("');\"");
		   sb.append(" title=\"Movimientos Rendición\"");
		   sb.append("/>");
			       
		   row.addText(sb.toString());
			       
	  }else{
		   sb = new StringBuilder();
		   sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		   
		   row.addText(sb.toString());
	 }
      
	  resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">
var autorizacionEnEdicion;
function editarCajaChica(id_CajaChica){
    jQuery('#<portlet:namespace />buscandoCC').show();
 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.EDIT%>',"id_caja_chica":id_CajaChica,"usuario_modi":'<%=usuario_modi%>'};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edición de Caja Chica:" />",modal:true,width:1000});
 	jQuery(autorizacionEnEdicion).load(url,editarNom, function(){
														jQuery('#<portlet:namespace />buscandoCC').hide();            															
													  });		 
}


function asignarCajaChica(id_CajaChica){
	var params = "&<%= Constants.CMD %>=" + "asigna";
	params +="&id_caja_chica=" +id_CajaChica;
 	params +="&usuario_modi=" +"<%=usuario_modi%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}	

function ejecucionCajaChica(id_CajaChica){
	var params = "&<%= Constants.CMD %>=" + "ejecuta";
	params +="&id_caja_chica=" +id_CajaChica;
 	params +="&usuario_modi=" +"<%=usuario_modi%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}	

function controlarRendicionCajaChica(id_CajaChica){
	var params = "&<%= Constants.CMD %>=" + "controlarendicion";
	params +="&id_caja_chica=" +id_CajaChica;
 	params +="&usuario_modi=" +"<%=usuario_modi%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

function reporteCajaChica(id_CajaChica){
	var params = "&<%= Constants.CMD %>=" + "reporte";
	params +="&id_caja_chica=" +id_CajaChica;
 	params +="&usuario_modi=" +"<%=usuario_modi%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}	

function emisionReciboCajaChica(id_CajaChica){
	var params = "&<%= Constants.CMD %>=" + "recibo";
	params +="&id_caja_chica=" +id_CajaChica;
 	params +="&usuario_modi=" +"<%=usuario_modi%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>


<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();
List<PreAutorizacion> archivos=(List<PreAutorizacion>)session.getAttribute(WebKeysAutorizaciones.BUSQUEDA_PREAUTORIZACIONES_RESULT );

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id");
headerNames.add("");
headerNames.add("Fecha");
headerNames.add("EMail");
/* headerNames.add("2do EMail"); */
headerNames.add("Cuil Titular");
headerNames.add("Inte");
headerNames.add("Nombre");
headerNames.add("T.Doc.");
headerNames.add("Documento");
headerNames.add("Seccional");
headerNames.add("Plan");
headerNames.add("Estado");
headerNames.add("Prestaciones/Medicamentos");
headerNames.add("Respuesta Terc.");
//headerNames.add("Notificacion");
/* headerNames.add("Entrega");
headerNames.add("T.Entrega"); */
//headerNames.add("Alerta");
headerNames.add("Editar|Eliminar|Imagen");
      

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-preautorizaciones-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		PreAutorizacion paut = (PreAutorizacion) archivos.get(i);
	 	ResultRow row = new ResultRow(paut,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		String estado="";
		for(int xi=0;xi<WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES.length;xi++){
			if(paut.getUltimoEstado()!=null && paut.getUltimoEstado().getId()!=null && paut.getUltimoEstado().getId().equalsIgnoreCase(WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][0])){
				estado=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][1];
				break;
			}
		}
		
		/* String tipoEntrega="";
		if(paut.getTipoEntrega()!=null){
		  for(int xi=0;xi<WebKeysAutorizaciones.TIPOS_ENTREGA.length;xi++){
			if(paut.getTipoEntrega().equalsIgnoreCase(WebKeysAutorizaciones.TIPOS_ENTREGA[xi][0])){
				tipoEntrega=WebKeysAutorizaciones.TIPOS_ENTREGA[xi][1];
				break;
			}
		  }
		} */
				
        boolean preautorizacionDadaDeBaja = Validator.isNotNull(paut.getBaja_Fecha())
                && paut.getBaja_Fecha().getTime() < System.currentTimeMillis();
		StringBuilder sb0 = new StringBuilder();
 		sb0.append("<a href='javascript:editarPreautorizacion(\"");
 		sb0.append(paut.getId());
 		sb0.append("\",\"");
 		sb0.append("V");
 		sb0.append("\")'");
 		if(paut.isAlertaRoja()){
          sb0.append("style='color: rgb(223,17,17)'"); 			
 		}
 		sb0.append(">");
 		sb0.append(paut.getId().toString());
 		sb0.append("</a>");
 		row.addText(sb0.toString());
 		
 		
 		StringBuilder sb20 = new StringBuilder();
 		sb20.append("<a href='javascript:editarPreautorizacion(\"");
 		sb20.append(paut.getId());
 		sb20.append("\",\"");
 		sb20.append("V");
 		sb20.append("\")'");
 		if(paut.isAlertaRoja()){
          sb20.append("style='color: rgb(223,17,17)'"); 			
 		}
 		sb20.append(">");
 		sb20.append(paut.isAlertaRoja()?"AR":"");
 		sb20.append("</a>");
 		row.addText(sb20.toString());
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("<a href='javascript:editarPreautorizacion(\"");
 		sb1.append(paut.getId());
 		sb1.append("\",\"");
 		sb1.append("V");
 		sb1.append("\")'>");			
 		sb1.append(paut.getFecha_string());
 		sb1.append("</a>");
 		row.addText(sb1.toString());
		
 		StringBuilder sb14 = new StringBuilder();
		sb14.append("<a href='javascript:editarPreautorizacion(\"");
 		sb14.append(paut.getId());
 		sb14.append("\",\"");
 		sb14.append("V");
 		sb14.append("\")'>");			
 		sb14.append(paut.getFechaEnvioMail_string() );
 		sb14.append("</a>");
 		row.addText(sb14.toString());
		
 		/* StringBuilder sb15 = new StringBuilder();
		sb15.append("<a href='javascript:editarPreautorizacion(\"");
 		sb15.append(paut.getId());
 		sb15.append("\",\"");
 		sb15.append("V");
 		sb15.append("\")'>");			
 		sb15.append(paut.getFechaEnvioMail2_string() );
 		sb15.append("</a>");
 		row.addText(sb15.toString()); */
 		
		
//		row.addText(paut.getAfiliado().getCuil_titular());
		StringBuilder sb2 = new StringBuilder();
		sb2.append("<a href='javascript:editarPreautorizacion(\"");
 		sb2.append(paut.getId());
 		sb2.append("\",\"");
 		sb2.append("V");
 		sb2.append("\")'>");			
 		sb2.append(paut.getAfiliado().getCuil_titular());
 		sb2.append("</a>");
 		row.addText(sb2.toString());
		
//		row.addText(String.valueOf(paut.getAfiliado().getInte()));
		StringBuilder sb3 = new StringBuilder();
		sb3.append("<a href='javascript:editarPreautorizacion(\"");
 		sb3.append(paut.getId());
 		sb3.append("\",\"");
 		sb3.append("V");
 		sb3.append("\")'>");			
 		sb3.append(String.valueOf(paut.getAfiliado().getInte()));
 		sb3.append("</a>");
 		row.addText(sb3.toString());
		
//		row.addText(paut.getAfiliado().getApeNombre());
		StringBuilder sb4 = new StringBuilder();
		sb4.append("<a href='javascript:editarPreautorizacion(\"");
 		sb4.append(paut.getId());
 		sb4.append("\",\"");
 		sb4.append("V");
 		sb4.append("\")'>");			
 		sb4.append(paut.getAfiliado().getApeNombre());
 		sb4.append("</a>");
 		row.addText(sb4.toString());
		
//		row.addText(paut.getAfiliado().getDocumento_tipo());
		StringBuilder sb5 = new StringBuilder();
		sb5.append("<a href='javascript:editarPreautorizacion(\"");
 		sb5.append(paut.getId());
 		sb5.append("\",\"");
 		sb5.append("V");
 		sb5.append("\")'>");			
 		sb5.append(paut.getAfiliado().getDocumento_tipo());
 		sb5.append("</a>");
 		row.addText(sb5.toString());
 		
//		row.addText(paut.getAfiliado().getDocu_numero());
		StringBuilder sb6 = new StringBuilder();
		sb6.append("<a href='javascript:editarPreautorizacion(\"");
 		sb6.append(paut.getId());
 		sb6.append("\",\"");
 		sb6.append("V");
 		sb6.append("\")'>");			
 		sb6.append(paut.getAfiliado().getDocu_numero());
 		sb6.append("</a>");
 		row.addText(sb6.toString());
		
		
//		row.addText(paut.getAfiliado().getSeccional().getDescripcion());
		StringBuilder sb7 = new StringBuilder();
		sb7.append("<a href='javascript:editarPreautorizacion(\"");
 		sb7.append(paut.getId());
 		sb7.append("\",\"");
 		sb7.append("V");
 		sb7.append("\")'>");			
 		sb7.append(paut.getAfiliado().getSeccional().getDescripcion());
 		sb7.append("</a>");
 		row.addText(sb7.toString());
		
//		row.addText(paut.getAfiliado().getAfiPlan().getPlan().getDescripcion());
		StringBuilder sb8 = new StringBuilder();
		sb8.append("<a href='javascript:editarPreautorizacion(\"");
 		sb8.append(paut.getId());
 		sb8.append("\",\"");
 		sb8.append("V");
 		sb8.append("\")'>");			
 		sb8.append(paut.getAfiliado().getAfiPlan().getPlan().getDescripcion());
 		sb8.append("</a>");
 		row.addText(sb8.toString());
		
// 		row.addText(estado);
 		StringBuilder sb9 = new StringBuilder();
		sb9.append("<a href='javascript:editarPreautorizacion(\"");
 		sb9.append(paut.getId());
 		sb9.append("\",\"");
 		sb9.append("V");
 		sb9.append("\")'>");			
 		sb9.append(estado);
 		sb9.append("</a>");
 		row.addText(sb9.toString());
		
 		StringBuilder sb16 = new StringBuilder();
		sb16.append("<a href='javascript:editarPreautorizacion(\"");
 		sb16.append(paut.getId());
 		sb16.append("\",\"");
 		sb16.append("V");
 		sb16.append("\")'>");			
 		sb16.append(paut.getPrestaciones()!=null?paut.getPrestaciones():"" );
 		sb16.append("</a>");
 		row.addText(sb16.toString());
 		
// 		row.addText(paut.getFechaRespuestaPS_string());
 		StringBuilder sb10 = new StringBuilder();
		sb10.append("<a href='javascript:editarPreautorizacion(\"");
 		sb10.append(paut.getId());
 		sb10.append("\",\"");
 		sb10.append("V");
 		sb10.append("\")'>");			
 		sb10.append(paut.getFechaRespuestaPS_string());
 		sb10.append("</a>");
 		row.addText(sb10.toString());
		
//		row.addText(paut.getFechaNotificacionAfiliado_string());
		/*StringBuilder sb11 = new StringBuilder();
		sb11.append("<a href='javascript:editarPreautorizacion(\"");
 		sb11.append(paut.getId());
 		sb11.append("\",\"");
 		sb11.append("V");
 		sb11.append("\")'>");			
 		sb11.append(paut.getFechaNotificacionAfiliado_string());
 		sb11.append("</a>");
 		row.addText(sb11.toString());*/
		
//		row.addText(paut.getFechaEntregaRespuesta_string());
		/* StringBuilder sb12 = new StringBuilder();
		sb12.append("<a href='javascript:editarPreautorizacion(\"");
 		sb12.append(paut.getId());
 		sb12.append("\",\"");
 		sb12.append("V");
 		sb12.append("\")'>");			
 		sb12.append(paut.getFechaEntregaRespuesta_string());
 		sb12.append("</a>");
 		row.addText(sb12.toString()); */
 		
//        row.addText(tipoEntrega);
       /*  StringBuilder sb13 = new StringBuilder();
		sb13.append("<a href='javascript:editarPreautorizacion(\"");
 		sb13.append(paut.getId());
 		sb13.append("\",\"");
 		sb13.append("V");
 		sb13.append("\")'>");			
 		sb13.append(tipoEntrega);
 		sb13.append("</a>");
 		row.addText(sb13.toString()); */
/*		
 		if(paut.isAlertaRoja()){
 			row.addText("Alerta Roja");
 		}else{
 			row.addText("");
 		}
*/		
		if(paut.getBaja_Fecha()  ==null){
			StringBuilder sb=new StringBuilder();
			if(!"AU".equalsIgnoreCase(paut.getUltimoEstado().getId()) && !"RE".equalsIgnoreCase(paut.getUltimoEstado().getId()) ||
				(("AU".equalsIgnoreCase(paut.getUltimoEstado().getId()) ||  "RE".equalsIgnoreCase(paut.getUltimoEstado().getId())) 
						&& paut.getFechaEntregaRespuesta() == null)
				){  
		 		sb.append("&nbsp;&nbsp;<img alt=\"Editar Preautorizacion\" src=\"");
		        sb.append(themeDisplay.getPathThemeImages());
 		        sb.append("/common/edit.png\" onClick=\"javascript:editarPreautorizacion('");
 		        sb.append(paut.getId() );
 		        sb.append("','E'");
 		        sb.append(");\"");
                sb.append(" title=\"Editar\"");
 		        sb.append("/>");
 		       
			} else {
				sb.append("");
			}
			
			
			if(!"AU".equalsIgnoreCase(paut.getUltimoEstado().getId()) && !"RE".equalsIgnoreCase(paut.getUltimoEstado().getId()) ){	
		 		sb.append("&nbsp;&nbsp;<img alt=\"Eliminar preautorizacion\" src=\"");
				sb.append(themeDisplay.getPathThemeImages());
		 		sb.append("/common/delete.png\" onClick=\"javascript:eliminarPreautorizacion('");
		 		sb.append(paut.getId() );
		 		sb.append("');\"");
                sb.append(" title=\"Elimina\"");
	 		    sb.append("/>");
			}else{
				sb.append("");
			}
	 		     		    	 		    
//		 		sb.append("&nbsp;&nbsp");
            
		 	
		 	sb.append("&nbsp;&nbsp;<img alt=\"Imagenes Preautorizacion\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/preview.png\" onClick=\"javascript:imagenesPreautorizacion('");
	 		sb.append(paut.getId() );
	 		sb.append("');\"");
            sb.append(" title=\"Imagenes\"");
 		    sb.append("/>");
		 	
		 	
		 	row.addText(sb.toString());  
		 	
		}		
		resultRows.add(row);
	}
//	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
}
%>
	
 		
	<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();
	var autorizacionEnEdicion;
    var popUpCierre;
    var popUpComprobante;
    
	function editarPreautorizacion(id_Preautorizacion,tipoEdicion){
		<%-- 
		/*
        jQuery('#<portlet:namespace />buscando').show();
	 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.EDIT%>',"id_preautorizacion":id_Preautorizacion,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
	 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edición de la Preautorización:" />",modal:true,width:1200});
	 	jQuery(autorizacionEnEdicion).load(url,editarNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });
	 	*/ --%>
	
	 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>";
	 	params+="&id_preautorizacion=" + id_Preautorizacion;
	 	params+="&usuario_modi=" +"<%=usuario_modi%>";
	 	params+="&accion=" + tipoEdicion;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	 	
	}	
	
	
	function eliminarPreautorizacion(id_Preautorizacion){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/preautorizacion_editar';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_preautorizacion='+id_Preautorizacion+'&usuario_modi='+'<%=usuario_modi%>';
	   	   
//	   	   autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Baja Preautorizacion:" />",modal:true,width:400});
	   	   jQuery('#<portlet:namespace />listado_preautorizaciones').load(url); 

		}   
	}
	
	
	function imagenesPreautorizacion(id_Preautorizacion){
		var editarNom = {'<%= Constants.CMD %>':'imagenes',"id_preautorizacion":id_Preautorizacion};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';

        url = url+'&<%= Constants.CMD %>'+'='+'imagenes'+'&id_preautorizacion='+id_Preautorizacion;
        url += "&desde_result=SI";
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

boolean rolExpedienteSUR = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ALTA_EXPEDIENTES_SUR);
boolean rolExpedienteSURCierre = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CIERRE_EXPEDIENTES_SUR);
String usuario_modi = user.getScreenName();

//List<Nomenclador> archivos=(List<Nomenclador>)renderRequest.getAttribute("Nomenclador");

List<SeguimientoSur> archivos=(List<SeguimientoSur>)session.getAttribute(WebKeysAutorizaciones.BUSQUEDA_SEGUIMIENTO_SUR_RESULT);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Tipo");
headerNames.add("Fecha Alta");
headerNames.add("Año");
headerNames.add("Bimestre");
headerNames.add("Afiliado");
headerNames.add("Nro Solicitud");
headerNames.add("Nro Expediente");
//headerNames.add("Código Presentado");
//headerNames.add("Descripción");
headerNames.add("Fecha Cierre");
headerNames.add("Estado");
if(rolExpedienteSUR || rolExpedienteSURCierre){
   headerNames.add("Editar|Docum|Cerrar|Cpte");
   
//   headerNames.add("Editar|Baja|Docum|Cerrar|Recuperar|Cpte");
   
}   

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "nomenclador-no-encontrado"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		SeguimientoSur liq = (SeguimientoSur) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		String clase="";
		for(int xi=0;xi<WebKeysAutorizaciones.CLASES_EXPEDIENTES.length;xi++){
			if(liq.getClaseExpediente().equalsIgnoreCase(WebKeysAutorizaciones.CLASES_EXPEDIENTES[xi][0])){
				clase=WebKeysAutorizaciones.CLASES_EXPEDIENTES[xi][0];
				break;
			}
		}
//        row.addText(liq.getId_tipo_expediente_nro().toString() + "-" +clase.toUpperCase() );
        
        StringBuilder sb0 = new StringBuilder();
 		sb0.append("<a href='javascript:editarSeguimientoSur(\"");
 		sb0.append(liq.getId());
 		sb0.append("\")'>");			
 		sb0.append(liq.getId_tipo_expediente_nro().toString() + "-" +clase.toUpperCase());
 		sb0.append("</a>");
 		row.addText(sb0.toString());
        
        
		row.addText(liq.getAlta_fecha()!=null?liq.getAlta_Fecha_string():"");
		row.addText(Integer.toString(liq.getAnio()));
		row.addText(liq.getBimestreDescripcion());
		row.addText(liq.getAfiliadoNombre());

//		row.addText(liq.getNro_solicitud_sur());
// DS
        StringBuilder sb1 = new StringBuilder();
		sb1.append("<a href='javascript:editarSeguimientoSur(\"");
		sb1.append(liq.getId());
		sb1.append("\")'>");			
		sb1.append(liq.getNro_solicitud_sur());
		sb1.append("</a>");
		row.addText(sb1.toString());
// DS
		
		row.addText(liq.getNro_expediente());
//		if("DI".equals(liq.getClaseExpediente())){
//		   row.addText(liq.getCodigoPresentado());
//		   row.addText(liq.getDescripcionPresentado());
//		}else{
//			SeguimientoSur aux = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId(liq.getId());
//			String codigo="";
//			String descrip="";
//			if(aux.getLiquidaciones().size()>0  ){
//				codigo=Integer.toString(aux.getLiquidaciones().get(0).getMedicamento().getTroquel()) ;
//				descrip=aux.getLiquidaciones().get(0).getMedicamento().getNombre();
//			}
//			row.addText(codigo);
//			row.addText(descrip);
//		}
		
		String motivoCierre="";
		for(int xi = 0; xi < WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES.length; xi++ ) {
		   String motivo=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[xi][0];
           if( motivo.equalsIgnoreCase(liq.getCierre_motivo())){ 
                 motivoCierre="<br> (" +WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[xi][1]+")";
                 break;
           }             		   
        } 		
		row.addText(liq.getCierre_fecha()!=null?liq.getCierre_Fecha_string() +motivoCierre:"");
		
		row.addText(liq.getUltimoEstadoDescripcion());
		
//		row.addText(liq.getBaja_fecha()!=null?liq.getBaja_Fecha_string()+"<br> ("+ liq.getMotivoBaja() +")" :"");
		if(rolExpedienteSUR || rolExpedienteSURCierre){
		
			StringBuilder sb=new StringBuilder();
		 	if(liq.getBaja_fecha()!=null && liq.getCierre_fecha()==null){
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
/*		 		
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Recuperar nomenclador\" src=\"");
				sb.append(themeDisplay.getPathThemeImages());
		 		sb.append("/common/undo.png\" onClick=\"javascript:recuperarSeguimientoSur('");
		 		sb.append(liq.getId() );
		 		sb.append("');\"");
                sb.append(" title=\"Recuperar\"");
	 		    sb.append("/>");
*/	 		    
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		row.addText(sb.toString());  
		 	}else if(liq.getCierre_fecha()==null){
		 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar Seguimiento\" src=\"");
			        sb.append(themeDisplay.getPathThemeImages());
	 		        sb.append("/common/edit.png\" onClick=\"javascript:editarSeguimientoSur('");
	 		        sb.append(liq.getId() );
	 		        sb.append("');\"");
                    sb.append(" title=\"Editar\"");
	 		        sb.append("/>");
/*		 		
		 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Baja Seguimiento\" src=\"");
				    sb.append(themeDisplay.getPathThemeImages());
		 		    sb.append("/common/delete.png\" onClick=\"javascript:bajaSeguimientoSur('");
		 		    sb.append(liq.getId() );
	 		        sb.append("');\"");
                    sb.append(" title=\"Baja\"");
	 		        sb.append("/>");
*/		 		    
		 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Imagenes Expediente\" src=\"");
				    sb.append(themeDisplay.getPathThemeImages());
		 		    sb.append("/common/preview.png\" onClick=\"javascript:imagenesSeguimientoSur('");
		 		    sb.append(liq.getId() );
		 		    sb.append("');\"");
                    sb.append(" title=\"Imagenes\"");
	 		        sb.append("/>");
	 		        
		 		    if(rolExpedienteSURCierre && liq.getNro_expediente()!=null && !"".equalsIgnoreCase(liq.getNro_expediente())){
		 		       sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Cerrar Seguimiento\" src=\"");
				       sb.append(themeDisplay.getPathThemeImages());
		 		       sb.append("/common/define_permissions.png\" onClick=\"javascript:cerrarSeguimientoSur('");
		 		       sb.append(liq.getId());
		 		       sb.append("');\"");
	                   sb.append(" title=\"Cerrar\"");
		 		       sb.append("/>");
		 		    }
		 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		row.addText(sb.toString());
		 	}else if(liq.getCierre_fecha()!=null){
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
                
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Imagenes Expediente\" src=\"");
			    sb.append(themeDisplay.getPathThemeImages());
	 		    sb.append("/common/preview.png\" onClick=\"javascript:imagenesSeguimientoSur('");
	 		    sb.append(liq.getId() );
	 		    sb.append("');\"");
                sb.append(" title=\"Imagenes\"");
 		        sb.append("/>");
 		        
 		        
	            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
	            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		
		 		
		 		if("PG".equalsIgnoreCase(liq.getCierre_motivo()) && 
		 				(liq.getComprobanteNumero()==null || "".equalsIgnoreCase(liq.getComprobanteNumero())) ){

                  sb.append("<img alt=\"Carga Comprobante OMINT\" src=\"");
				  sb.append(themeDisplay.getPathThemeImages());
		 		  sb.append("/common/recent_changes.png\" onClick=\"javascript:editarComprobantePrestadoraSeguimientoSur('");
		 		  sb.append(liq.getId() );
		 		  sb.append("');\"");
                  sb.append(" title=\"Comprobante\"");
	 		      sb.append("/>");
		 		}else{
		 		  sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");	
		 		}
		 		
		 		row.addText(sb.toString());
		 	}else{
	 		    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp");
		 		row.addText(sb.toString());
		 	}
		}
		resultRows.add(row);
	}

}
%>
	
 		
	<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();
	var autorizacionEnEdicion;
    var popUpCierre;
    var popUpComprobante;
	function editarSeguimientoSur(id_Seguimiento){
        jQuery('#<portlet:namespace />buscando').show();
	 	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.EDIT%>',"id_seguimiento":id_Seguimiento,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
	 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edición del Expediente:" />",modal:true,width:1300});
	 	jQuery(autorizacionEnEdicion).load(url,editarNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });		 
	}	
			
	function bajaSeguimientoSur(id_Seguimiento){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_seguimientosur';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE%>'+'&id_seguimiento='+id_Seguimiento+'&usuario_modi='+'<%=usuario_modi%>';
	   	   autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Baja Seguimiento:" />",modal:true,width:400});
	   	   jQuery(autorizacionEnEdicion).load(url);
		}   
	}
	
	function recuperarSeguimientoSur(id_Seguimiento){
		if(!confirm("Desea recuperar el seguimiento dado de baja?")){
			return false;
		}else{	
	       var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_seguimientosur';
	   	   url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.RESTORE %>'+'&id_seguimiento='+id_Seguimiento+'&usuario_modi='+'<%=usuario_modi%>';
	   	   jQuery("#<portlet:namespace />listado_seguimientoSur").load(url); 
		}   
	}
	
	
	function cerrarSeguimientoSur(id_Seguimiento){
     	var editarNom = {'<%= Constants.CMD %>':'<%=Constants.LOCK %>',"id_seguimiento":id_Seguimiento,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
	 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Cerrar Seguimiento:" />",modal:true,width:400});
	 	jQuery(autorizacionEnEdicion).load(url,editarNom, function(){});
	}

	function <portlet:namespace />cerrarSeguimientoSur(){
		Liferay.Popup.close(autorizacionEnEdicion);
    } 
	
	function imagenesSeguimientoSur(id_Seguimiento){
		var editarNom = {'<%= Constants.CMD %>':'imagenes',"id_seguimiento":id_Seguimiento};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
//	 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Imagenes Expediente:" />",modal:true,width:800});
//	 	jQuery(autorizacionEnEdicion).load(url,editarNom, function(){});
//      jQuery(autorizacionEnEdicion).load(url);

        url = url+'&<%= Constants.CMD %>'+'='+'imagenes'+'&id_seguimiento='+id_Seguimiento;
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function editarComprobantePrestadoraSeguimientoSur(id_Seguimiento){
    	var editarNom = {'<%= Constants.CMD %>':'comprobante_edit',"id_seguimiento":id_Seguimiento,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
	 	popUpComprobante = Liferay.Popup({title:"<liferay-ui:message key="Edición del Comprobante Expediente:" />",modal:true,width:700});          
	 	jQuery(popUpComprobante).load(url,editarNom, function(){});		 
	}
	
	function <portlet:namespace />cerrarPopComprobanteSeguimientoSur(){
		Liferay.Popup.close(popUpComprobante);
    } 
</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
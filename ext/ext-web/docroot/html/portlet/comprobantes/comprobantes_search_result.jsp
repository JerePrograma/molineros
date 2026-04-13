<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

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
	portlet_name = "comprobantes";
}
NumberFormat format2D = new DecimalFormat("#0.00");

%>
		
<%if (esEdicion && !soloVer){ %>
<liferay-ui:error exception="<%= ImporteMayorException.class %>" message="importe-mayor-concepto" />
<div align="left">
	<input type="button" value="<liferay-ui:message key="borrar-todos" />" onClick="borrarTodos();" />
</div>
<%} %>

<%-- <%if(portlet_name!=null && !portlet_name.equalsIgnoreCase("tesoreria")) {%> --%>
<liferay-ui:error key="regimenError" message="<%=(String)request.getAttribute(\"msgError2\") %>"  />
<liferay-ui:error key="exencionError" message="<%=(String)request.getAttribute(\"msgError3\") %>"  />
<liferay-ui:error key="exencionUrlError" message="<%=(String)request.getAttribute(\"msgError4\") %>"  />
<%-- <%} %> --%>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					List<Comprobante> comprobantes = (ArrayList<Comprobante>)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("pto-venta");
			 		headerNames.add("numero");
			 		headerNames.add("fecha-recibido");
					headerNames.add("importe");
					headerNames.add("estado");
					headerNames.add("sector");
					headerNames.add("Comandos");
					headerNames.add("");
					headerNames.add("");
					if(esEdicion) { 
						headerNames.add("importe-a-pagar");
					}
					
					if(esEdicion) { 
						headerNames.add("Borrar");
					}			
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=comprobantes){					
					 	int total = comprobantes.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		Comprobante comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( comp.getAcreedorEmpresa().getCuit());
		 					row.addText( comp.getTipoComprobante());
		 					row.addText(comp.getLetraComprobante());
			 				row.addText(String.format("%05d",comp.getPtoVenta()));
	 						row.addText( comp.getNroComprobante());
	 						row.addText( comp.getFechaRecepcionAsString());
	 						row.addText( format2D.format(comp.getImporte()));
	 						row.addText(comp.getEstado());
	 						row.addText(comp.getSectorDestino());
	 						
	 						StringBuilder sb= new StringBuilder();		
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/edit.png\" onClick=\"javascript:editaComprobanteProveedor('");
			 				sb.append(comp.getAcreedorEmpresa().getCuit());
			 				sb.append("','");
			 				sb.append(comp.getTipoComprobante());
			 				sb.append("','");
			 				sb.append(comp.getLetraComprobante());
			 				sb.append("','");
			 				sb.append(comp.getPtoVenta());
			 				sb.append("','");
			 				sb.append(comp.getNroComprobante());
			 				sb.append("');\" />");
		 					row.addText(sb.toString());			
	 						
	 						StringBuilder sbImg= new StringBuilder();
	 						sbImg.append("");
	 						if(comp.getImagenes()!=null){
	 						  if(comp.getImagenes().size()>1){
	 						   sbImg.append("&nbsp;&nbsp;<img alt=\"Imagenes Comprobantes\" src=\"");
	 						   sbImg.append(themeDisplay.getPathThemeImages());
	 						   sbImg.append("/common/preview.png\" onClick=\"javascript:imagenesComprobantes('");
	 						   sbImg.append(comp.getAcreedorEmpresa().getCuit());
	 						   sbImg.append("','");
	 						   sbImg.append(comp.getTipoComprobante());
	 						   sbImg.append("','");
	 						   sbImg.append(comp.getLetraComprobante());
	 						   sbImg.append("','");
	 						   sbImg.append(comp.getPtoVenta());
	 						   sbImg.append("','");
	 						   sbImg.append(comp.getNroComprobante());
		 					   sbImg.append("');\"");
	 						   sbImg.append(" title=\"Imagenes\"");
	 						   sbImg.append("/>");
	 						  }else if(comp.getImagenes().size()==1){
	 							sbImg.append("<img alt=\"Ver Imagen\" src=\"");
	 							sbImg.append(themeDisplay.getPathThemeImages());
	 							sbImg.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
	 							sbImg.append(String.valueOf(comp.getImagenes().get(0).getFolderId()));
	 							sbImg.append("','");
	 							sbImg.append(comp.getImagenes().get(0).getName());
	 							//sbImg.append("');\" /> ");
	 							sbImg.append("');\"");
	 							sbImg.append(" title=\"Imagenes\"");
		 						sbImg.append("/>");
	 						  }else{
	 							 sbImg.append("&nbsp;&nbsp;<img alt=\"Recuperar Imagenes Comprobantes\" src=\"");
		 						 sbImg.append(themeDisplay.getPathThemeImages());
		 						 sbImg.append("/portlet/refresh.png\" onClick=\"javascript:recuperarImagenesComprobantes('");
		 						 sbImg.append(comp.getAcreedorEmpresa().getCuit());
		 						 sbImg.append("','");
		 						 sbImg.append(comp.getTipoComprobante());
		 						 sbImg.append("','");
		 						 sbImg.append(comp.getLetraComprobante());
		 						 sbImg.append("','");
		 						 sbImg.append(comp.getPtoVenta());
		 						 sbImg.append("','");
		 						 sbImg.append(comp.getNroComprobante());
		 						 sbImg.append("','");
		 						 sbImg.append(comp.getId());
			 					 sbImg.append("');\"");
		 						 sbImg.append(" title=\"Recuperar Imagenes\"");
		 						 sbImg.append("/>");
 	 						  }
	 						}else{
	 							
	 							 sbImg.append("&nbsp;&nbsp;<img alt=\"Recuperar Imagenes Comprobantes\" src=\"");
		 						   sbImg.append(themeDisplay.getPathThemeImages());
		 						   sbImg.append("/classic/images/portlet/refresh.png\" onClick=\"javascript:recuperarImagenesComprobantes('");
		 						   sbImg.append(comp.getAcreedorEmpresa().getCuit());
		 						   sbImg.append("','");
		 						   sbImg.append(comp.getTipoComprobante());
		 						   sbImg.append("','");
		 						   sbImg.append(comp.getLetraComprobante());
		 						   sbImg.append("','");
		 						   sbImg.append(comp.getPtoVenta());
		 						   sbImg.append("','");
		 						   sbImg.append(comp.getNroComprobante());
		 						   sbImg.append("','");
		 						   sbImg.append(comp.getId());
			 					   sbImg.append("');\"");
		 						   sbImg.append(" title=\"Recuperar Imagenes\"");
		 						   sbImg.append("/>");
		 						   
	 						}

	 			 		    row.addText(sbImg.toString());
	 			 		    
//////////////////////
// RECIBO
//////////////////////
	 			 		  StringBuilder sbImgR= new StringBuilder();
	 						sbImgR.append("");
	 						if(comp.getImagenRecibo()==null){
	 						  	 sbImgR.append("&nbsp;&nbsp;<img alt=\"Recuperar Imágen Recibo Comprobante\" src=\"");
		 						 sbImgR.append(themeDisplay.getPathThemeImages());
		 						 sbImgR.append("/portlet/refresh.png\" onClick=\"javascript:recuperarImagenRecibo('");
		 						 sbImgR.append(comp.getAcreedorEmpresa().getCuit());
		 						 sbImgR.append("','");
		 						 sbImgR.append(comp.getTipoComprobante());
		 						 sbImgR.append("','");
		 						 sbImgR.append(comp.getLetraComprobante());
		 						 sbImgR.append("','");
		 						 sbImgR.append(comp.getPtoVenta());
		 						 sbImgR.append("','");
		 						 sbImgR.append(comp.getNroComprobante());
		 						 sbImgR.append("','");
		 						 sbImgR.append(comp.getId());
			 					 sbImgR.append("');\"");
		 						 sbImgR.append(" title=\"Recuperar Imágen Recibo\"");
		 						 sbImgR.append("/>");
	 						}
	 						
	 			 		    row.addText(sbImgR.toString());	
//////////////////////
//////////////////////
	 			 		    
	 						resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
    <liferay-util:include page="/html/portlet/comprobantes/paginador_comprobantes.jsp">
    </liferay-util:include>		
	
<script type="text/javascript">
   
	function editaComprobanteProveedor(cuit,tipo,letra,ptovta,numero){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores" /></portlet:renderURL>';
        url += "&accion=edit";
        url += '&cuit='+cuit;
        url += "&tipo="+tipo;
        url += "&letraComprobante="+letra;
        url += "&ptovta="+ptovta;
        url += "&nro="+numero;
        url += "&desde_result=SI";
        url += '&cmd=edit';
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function imagenesComprobantes(cuit,tipo,letra,ptovta,numero){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores" /></portlet:renderURL>';
        url += "&accion=imagenes";
        url += '&cuit='+cuit;
        url += "&tipo="+tipo;
        url += "&letraComprobante="+letra;
        url += "&ptovta="+ptovta;
        url += "&nro="+numero;
        url += "&desde_result=SI";
        url += '&cmd=imagenes';
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function verImagenComprobante(folderId,fileName){
		   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		   '<liferay-portlet:param name="struts_action" value="/comprobantes/documentacion_adjunta_recuperar"/>'+
		   '<liferay-portlet:param name="name" value="__Name"/>'+
		   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
		   '</liferay-portlet:actionURL>';      
		   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
		   
		    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
	}
	
	
	function recuperarImagenesComprobantes(cuit,tipo,letra,ptovta,numero,id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores" /></portlet:renderURL>';
        url += "&accion=recuperarimagenes";
        url += '&cuit='+cuit;
        url += "&tipo="+tipo;
        url += "&letraComprobante="+letra;
        url += "&ptovta="+ptovta;
        url += "&nro="+numero;
        url += "&id_comprobante="+id;
        url += "&desde_result=SI";
        url += '&cmd=recuperarimagenes';
        jQuery('#<portlet:namespace />buscando').show();
        jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
    		jQuery('#<portlet:namespace />buscando').hide();
		});
        
        /*
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
		*/
	}
	
	function recuperarImagenRecibo(cuit,tipo,letra,ptovta,numero,id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores" /></portlet:renderURL>';
        url += "&accion=recuperarrecibo";
        url += '&cuit='+cuit;
        url += "&tipo="+tipo;
        url += "&letraComprobante="+letra;
        url += "&ptovta="+ptovta;
        url += "&nro="+numero;
        url += "&id_comprobante="+id;
        url += "&desde_result=SI";
        url += '&cmd=recuperarrecibo';
        jQuery('#<portlet:namespace />buscando').show();
        jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
    		jQuery('#<portlet:namespace />buscando').hide();
		});
	}
	
	
</script>	

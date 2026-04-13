<%@page import="java.math.BigDecimal"%>
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
		
<%if (esEdicion && !soloVer){ %>
<div align="left">
	<input type="button" value="<liferay-ui:message key="borrar-todos" />" onClick="borrarTodos();" />
</div>
<%} %>

<%-- <%if(portlet_name!=null && !portlet_name.equalsIgnoreCase("tesoreria")) {%> --%>
<liferay-ui:success key="insertCabOk" message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

<liferay-ui:error key="regimenError" message="<%=(String)request.getAttribute(\"msgError2\") %>"  />
<liferay-ui:error key="exencionError" message="<%=(String)request.getAttribute(\"msgError3\") %>"  />
<liferay-ui:error key="exencionUrlError" message="<%=(String)request.getAttribute(\"msgError4\") %>"  />
<%-- <%} %> --%>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					List<ComprobanteHospital> comprobantes = (ArrayList<ComprobanteHospital>)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROVEEDORES);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Sel");
			 		headerNames.add("Status");
			 		headerNames.add("Id.Prestador");
			 		headerNames.add("Razón Social");
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("pto-venta");
			 		headerNames.add("numero");
			 		headerNames.add("fecha-recibido");
					headerNames.add("importe");
					headerNames.add("Fecha Prest.");
					headerNames.add("Precarga");
					headerNames.add("OP");
					headerNames.add("Edit");
					headerNames.add("Imagen");
					headerNames.add("Comprobantes");
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
					 	pageContext.setAttribute("total", total);	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		ComprobanteHospital comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					Boolean marcar=false;
		 					
		 					StringBuffer sb0 = new StringBuffer();
		 					sb0.append("<input type=\"checkbox\"");
		 					sb0.append("name=\"comprob\"");
		 					if(marcar){
		 						sb0.append("\" checked=\"checked");
		 					}
		 					sb0.append("id=\"");
		 					sb0.append("formu-"+comp.getId());
		 			        sb0.append("\" value=\"");
		 					sb0.append(comp.getId());									
		 					sb0.append("\"/>");
		 					row.addText(sb0.toString());
		 					
                            String sb000 = "";
                            if(comp.isConProblema()){
                            	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#F1948A; font-weight:bold; font-size:10px'>" + (comp.getError()!=null?" " + comp.getError() +" ":"_")   +"</span>";
                            }else{
                            	if("OK".equals(comp.getError())){
                            	   sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#ABEBC6; font-weight:bold; font-size:10px'>" +  "OK"   +"</span>";
                            	}   
                            }
                            row.addText(sb000);
		 					row.addText( comp.getIdPrestador().toString());
                            row.addText( comp.getAcreedorEmpresa().getRazon_soc());
		 					row.addText( comp.getAcreedorEmpresa().getCuit());
		 					
		 					row.addText( comp.getTipoComprobante());
		 					row.addText(comp.getLetraComprobante());
			 				row.addText(String.format("%05d",comp.getPtoVenta()));
	 						row.addText( comp.getNroComprobante());
	 						row.addText( comp.getFechaRecepcionAsString());
	 						row.addText( format2D.format(comp.getImporte()));
	 						row.addText( (new SimpleDateFormat("dd/MM/yyyy")).format(comp.getPeriodoPrestacion()));
	 						
	 						
	 						StringBuilder sbe= new StringBuilder();	
	 						sbe.append("<p style='color:");
	 						if(comp.getGenerado()!=null && comp.getGenerado()){
	 							sbe.append("green");
	 						    sbe.append( "';>");
	 						    sbe.append("SI");
	 						} else {
	 							sbe.append("red");
	 						    sbe.append( "';>");
	 						    sbe.append("NO");
	 						}
	 						sbe.append( "</p>");
	 						row.addText(sbe.toString());
	 						
	 						row.addText( String.valueOf(comp.getIdOp()));
	 						
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
	 						  }
	 						}  
	 			 		    row.addText(sbImg.toString());	
// Nuevo	 			 		    
	 			 		    
	 			 		  StringBuilder sbLiq= new StringBuilder();
	 					  sbLiq.append("");
	 					  if(comp.getGenerado()!=null && comp.getGenerado() ){
	 						sbLiq.append("<img alt=\"Editar Comprobante\" src=\"");
	 						sbLiq.append(themeDisplay.getPathThemeImages());
	 						sbLiq.append("/common/manage_task.png\" onClick=\"javascript:editarComprobanteOspim('");	
	 						
	 						sbLiq.append(comp.getAcreedorEmpresa().getCuit());
 						    sbLiq.append("','");
	 						sbLiq.append(comp.getTipoComprobante());
	 						sbLiq.append("','");
	 						sbLiq.append(comp.getLetraComprobante());
	 						sbLiq.append("','");
	 						sbLiq.append(comp.getPtoVenta());
	 						sbLiq.append("','");
	 						sbLiq.append(comp.getNroComprobante());
	 						sbLiq.append("');\"");
	 						sbLiq.append(" title=\"Comprobante\"");
	 						sbLiq.append("/>");
	 						 
	 					  }
	 					  row.addText(sbLiq.toString());  
//Fin 	 			 		    
	 						resultRows.add(row);
						}
					 }
			%>

    <%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
    		
	
<script type="text/javascript">
   
	function editaComprobanteProveedor(cuit,tipo,letra,ptovta,numero){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores_comunes" /></portlet:renderURL>';
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
	
	
	var popupdl;
	function editarComprobanteOspim(cuit,tipo,letra,ptovta,numero) {
		   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/comprobantes/editar_comprobante_molinero" /></portlet:renderURL>';
	        url += "&origen=portalproveedores";
            url += '&cuit_compr_emisor='+cuit;
            url += "&tipo_comprobante="+tipo;
            url += "&letra="+letra;
            url += "&pto_venta="+ptovta;
            url += "&sucursal="+ptovta;
            url += "&nro_comprobante="+numero;
            
        window.open(url,'mywindow') ;
	}
	
</script>	

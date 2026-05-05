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
					List<ComprobanteAcompanante> comprobantes = (ArrayList<ComprobanteAcompanante>)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ACOMPANANTES);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Sel");
			 		headerNames.add("Status");
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("pto-venta");
			 		headerNames.add("numero");
			 		headerNames.add("fecha-recibido");
					headerNames.add("importe");
					headerNames.add("cuil");
					headerNames.add("Prestación");
					headerNames.add("Fecha Prest.");
					headerNames.add("Reclamo Prestacional");
					headerNames.add("Comandos");
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
					 	pageContext.setAttribute("total", total);	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		ComprobanteAcompanante comp = comprobantes.get(i);
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
                            
                            
                            /*
                            if(comp.isConProblema()){
                            	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#F1948A; font-weight:bold; font-size:20px'>" + (comp.getError()!=null?" " + comp.getError() +" ":"_____")   +"</span>";
                            }else{
                            	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#ABEBC6; font-weight:bold; font-size:20px'>" +  "_____"   +"</span>";
                            }
                            */
                            row.addText(sb000);
		 					
		 					
		 					row.addText( comp.getAcreedorEmpresa().getCuit());
		 					row.addText( comp.getTipoComprobante());
		 					row.addText(comp.getLetraComprobante());
			 				row.addText(String.format("%05d",comp.getPtoVenta()));
	 						row.addText( comp.getNroComprobante());
	 						row.addText( comp.getFechaRecepcionAsString());
	 						row.addText( format2D.format(comp.getImporte()));
	 						row.addText(comp.getAfiliado().getCuil()!=null?comp.getAfiliado().getCuil():"");
	 						
	 						row.addText((comp.getCodigoPrestacion()!=null? comp.getCodigoPrestacion():"") +"  "+ (comp.getDescripcionPrestacion()!=null?comp.getDescripcionPrestacion():""));
	 						row.addText( (new SimpleDateFormat("dd/MM/yyyy")).format(comp.getPeriodoPrestacion()));
	 						row.addText(String.valueOf(comp.getReclamoId()));
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
	 						resultRows.add(row);
						}
					 }
			%>

    <%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
    		
	
<script type="text/javascript">
   
	function editaComprobanteProveedor(cuit,tipo,letra,ptovta,numero){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores_acompanantes" /></portlet:renderURL>';
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
	
</script>	

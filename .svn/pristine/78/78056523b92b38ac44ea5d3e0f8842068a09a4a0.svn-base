<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>

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
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
} 

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
} 
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
					List<Comprobante> comprobantes = (ArrayList<Comprobante>)request.getSession().getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("pto-venta");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("sucursal");
			 		headerNames.add("numero");
					headerNames.add("cuit-emisor");
					headerNames.add("cuit-acreedor");
					headerNames.add("importe");
					if(esEdicion) { 
						headerNames.add("importe-a-pagar");
					}
					headerNames.add("fecha-recibido");
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
			 				row.addText(String.valueOf(comp.getPtoVenta()));
			 				row.addText( comp.getTipoComprobante());
			 				row.addText(comp.getLetraComprobante());
			 				row.addText( String.valueOf(comp.getSucuComprobante()));
			 				//OFUSCAMOS EL PAGO PARCIAL...
		 					if(comp.getNroComprobante().indexOf("&")>=0){
		 						row.addText(comp.getNroComprobante().substring(0, comp.getNroComprobante().indexOf("&")));
		 					}else{
		 						row.addText( comp.getNroComprobante());
		 					}			 				
			 				row.addText( comp.getCuit());
			 				row.addText( comp.getAcreedorEmpresa().getCuit());
			 				if (comp.isDebitoParaEgreso()){
			 					row.addText( comp.getImporteComprobanteOriginal().negate().toString());
			 				} else {
			 					row.addText( comp.getImporteComprobanteOriginal().toString());
			 				}
			 				if(esEdicion && !soloVer) {
			 					StringBuilder sb= new StringBuilder();		
			 					if (comp.isDebitoParaEgreso()){
				 					sb.append( comp.getImporteComprobante().negate().toString());
				 				} else {
				 					sb.append( comp.getImporteComprobante().toString());
				 				}
			 					
/* Permite el pago parcial - DS 2026/01/15   Comentado para desarrollar cuando se pida			 					
			 					if(!comp.getTipoComprobante().equals("ANT")){
				 					sb.append("/<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/common/edit.png\" onClick=\"javascript:editaComprobante('");
				 					sb.append(comp.getPtoVenta());
				 					sb.append("','");
				 					sb.append(comp.getTipoComprobante());
				 					sb.append("','");
				 					//OFUSCAMOS EL PAGO PARCIAL...
				 					if(comp.getNroComprobante().indexOf("&")>=0){
				 						sb.append(comp.getNroComprobante().substring(0, comp.getNroComprobante().indexOf("&")));
				 					}else{
				 						sb.append(comp.getNroComprobante());
				 					}
				 					
				 					sb.append("','");
				 					sb.append(comp.getCuitEmisor());
				 					sb.append("','");
				 					sb.append(comp.getLetraComprobante());
				 					sb.append("','");
				 					sb.append(comp.getSucuComprobante());
				 					sb.append("');\" />");
			 					}
*/
			 					row.addText(sb.toString());			 					
			 				}
			 				row.addText( comp.getFechaRecepcionAsString());
			 				obs.append(comp.getObservaciones()!=null?comp.getObservaciones().replaceAll("\"", "'"):"");
			 				obs.append(" - ");
			 				if (esEdicion && !soloVer){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraComprobante('");
			 					sb.append(comp.getPtoVenta());
			 					sb.append("','");
			 					sb.append(comp.getTipoComprobante());
			 					sb.append("','");
			 					sb.append(comp.getNroComprobante());
			 					sb.append("','");
			 					sb.append(comp.getCuitEmisor());
			 					sb.append("','");
			 					sb.append(comp.getLetraComprobante());
			 					sb.append("','");
			 					sb.append(comp.getSucuComprobante());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		} 
					 		resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

	
	<table width="100%">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>
				<div align="left" id="<portlet:namespace />conceptos">
					<jsp:include page='conceptos_search_result.jsp' /></div>
			</td>
		</tr>
	</table>
	
	<input type="hidden" id="obs_comprobantes" value="<%=obs%>"/>

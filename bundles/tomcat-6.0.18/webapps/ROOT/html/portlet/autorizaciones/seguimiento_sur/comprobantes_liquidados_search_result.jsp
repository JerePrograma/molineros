<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante" %>
<%String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}

if (request.getAttribute("esEditable") != null){
	esEditableStr = (String)request.getAttribute("esEditable");
}
boolean soloVer = false;
boolean esEdicion = Boolean.parseBoolean(esEditableStr); %>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
//					List<Comprobante> comprobantes = (ArrayList<Comprobante>)request.getSession().getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
					
					SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("pto-venta");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("sucursal");
			 		headerNames.add("numero");
					headerNames.add("cuit");
					headerNames.add("razon-social");
					headerNames.add("importe");
					headerNames.add("fecha-recibido");
					headerNames.add("Borrar");
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=seguimiento.getComprobantes() ){					
					 	int total = seguimiento.getComprobantes().size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < seguimiento.getComprobantes().size(); i++) {
					 		SeguimientoSurComprobante comp = seguimiento.getComprobantes().get(i);
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
			 				row.addText( comp.getAcreedorEmpresa().getRazon_soc());
			 				row.addText( comp.getImporteComprobante().toString());
			 				row.addText( comp.getFechaRecepcionAsString());
			 				
			 				StringBuilder sb= new StringBuilder();
			 				sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:borraComprobanteLiq('");
			 				sb.append(comp.getCuit());
			 				sb.append("','");
			 				sb.append(comp.getTipoComprobante());
			 				sb.append("','");
			 				sb.append(comp.getLetraComprobante());
			 				sb.append("','");
			 				sb.append(comp.getPtoVenta());
			 				sb.append("','");
			 				sb.append(comp.getSucuComprobante());
			 				sb.append("','");
			 				sb.append(comp.getNroComprobante());
			 				sb.append("','");
			 				sb.append(comp.getAcreedorEmpresa().getSucursal());
			 				sb.append("');\" />");
			 				row.addText(sb.toString());
		 			 		resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

	
	
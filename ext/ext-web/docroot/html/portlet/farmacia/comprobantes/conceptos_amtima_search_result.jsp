<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>

<portlet:defineObjects/>
			<% 
			BigDecimal totalConceptos= BigDecimal.ZERO;
			List<ComprobanteConcepto>  conceptos= (List<ComprobanteConcepto>) request.getSession().getAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);


			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("descripcion");
	 		headerNamesTercerizadora.add("importe");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-conceptos-were-found"));
		
			
			if(null!=conceptos){
				int total=conceptos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < conceptos.size(); i++) {
 			 		ComprobanteConcepto pago = conceptos.get(i);
 			 		if (pago.isBorradoLogicamente()){
 			 			total--;
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(pago, pago.getConceptoComprobante().getId(), i);
	 					row.addText(pago.getConceptoComprobante().getDescripcion());
	 					row.addText(pago.getImporte().toString());
	 					totalConceptos = totalConceptos.add(pago.getImporte());
	 					if (showABMButtons && esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraConcepto('");
		 					sb.append(pago.getConceptoComprobante().getId());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else {
	 			 			row.addText("");
	 			 		}
	 					resultRowsInspector.add(row);
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	<table width="100%" align="left">
	<tr>
	<td><label><liferay-ui:message key="importe-conceptos" />:</label>&nbsp;&nbsp;&nbsp;<%=totalConceptos.toString()%></td>
	<input type="hidden" id="total_conceptos" value="<%=totalConceptos.toString()%>"/>
	</tr>
	</table>
		

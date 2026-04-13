<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<table>
				   <tr>    
				    <td width="95%" valign="top">    
 
<portlet:defineObjects/>
			<%
			
			NumberFormat formatter = new DecimalFormat("#0.00");
			
			
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "liquidaciones";
			}
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
			} 
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
	 		headerNamesTercerizadora.add("Gravado");
	 		headerNamesTercerizadora.add("Tasa IVA");
	 		headerNamesTercerizadora.add("IVA");
	 		headerNamesTercerizadora.add("Exento");
	 		headerNamesTercerizadora.add("Percep.IVA");
	 		headerNamesTercerizadora.add("Percep.IIBB");
	 		headerNamesTercerizadora.add("Jurisdicción");
	 		headerNamesTercerizadora.add("O.Tributos");
	 		headerNamesTercerizadora.add("Total");
	 
	 		if("uoma".equalsIgnoreCase(portlet_name)){
	 			headerNamesTercerizadora.add("C.Costo");
	 		}

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
	 					
	 					row.addText(pago.getGravadoIVA()!=null? formatter.format(pago.getGravadoIVA().doubleValue()) :"");
	 					row.addText(pago.getTasaIva()!=null?pago.getTasaIva().toString():"");
	 					row.addText(pago.getIva()!=null?formatter.format(pago.getIva().doubleValue()):"");
	 					row.addText(pago.getExento()!=null?formatter.format(pago.getExento().doubleValue()):"");
	 					row.addText(pago.getPercepcionIVA()!=null?formatter.format(pago.getPercepcionIVA().doubleValue()):"");
	 					row.addText(pago.getPercepcionIIBB()!=null?formatter.format(pago.getPercepcionIIBB().doubleValue()):"");
	 					row.addText(pago.getJurisdiccionIIBB() !=null?pago.getJurisdiccionIIBB().toString():"");
	 					row.addText(pago.getOtrosTributos()!=null?formatter.format(pago.getOtrosTributos().doubleValue()):"");
	 					
	 					
	 					BigDecimal gravadoIVA =pago.getGravadoIVA()!=null?pago.getGravadoIVA():BigDecimal.ZERO;
	 					BigDecimal iva=pago.getIva()!=null?pago.getIva():BigDecimal.ZERO;
	 					BigDecimal exento=pago.getExento()!=null?pago.getExento():BigDecimal.ZERO;
	 					BigDecimal percepcionIva=pago.getPercepcionIVA()!=null?pago.getPercepcionIVA():BigDecimal.ZERO;
	 					BigDecimal percepcionIIBB=pago.getPercepcionIIBB()!=null?pago.getPercepcionIIBB():BigDecimal.ZERO;
	 					BigDecimal otrosTributos=pago.getOtrosTributos()!=null?pago.getOtrosTributos():BigDecimal.ZERO;
	 					
	 					BigDecimal totalCpto = ((((gravadoIVA.add(iva)).add(exento)).add(percepcionIva)).add(percepcionIIBB)).add(otrosTributos);
	 							
	 					pago.setImporte(totalCpto);
	 					row.addText(formatter.format(pago.getImporte().doubleValue()));
	 					
	 					if("uoma".equalsIgnoreCase(portlet_name)){
	 						if(pago.getCentroCosto()!=null && pago.getCentroCosto().getDescripcion()!=null){
	 						   row.addText(pago.getCentroCosto().getDescripcion());
	 						}else{
	 						   row.addText("");
	 						}
	 					}
	 					totalConceptos = totalConceptos.add(pago.getImporte());
	 					if (showABMButtons && esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraConcepto('");
		 					sb.append(pago.getConceptoComprobante().getId());
		 					if(portlet_name.equals("uoma")){
		 						sb.append("','");
		 						sb.append(pago.getConceptoComprobante().getIdSeccional());	
		 						sb.append("','");
		 						sb.append(pago.getCentroCosto().getId());
		 						sb.append("','");
		 						sb.append(pago.getTasaIva());
		 						sb.append("','");
		 						sb.append(pago.getImporte());
		 						sb.append("');\" />");
		 					}else{
		 						sb.append("');\" />");
		 					}
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

</td>
<td width="5%" valign="top">		
 <liferay-util:include page="/html/portlet/liquidaciones/comprobantes/conceptos_total_comprobante_extendido.jsp">
</liferay-util:include>		
  </td>
</tr>	
</table>	
					

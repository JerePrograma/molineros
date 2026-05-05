<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Anticipo" %>
 
<liferay-ui:error exception="<%= ComprobanteInexistenteException.class %>" message="anticipo-inexistente" />

<portlet:defineObjects/>
			<% 
			String portlet_name="liquidaciones";
			
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
			}
			if(renderResponse.getNamespace().equals("_TES_1_")){
				portlet_name = "tesoreria";
			}
			BigDecimal totalPagos = BigDecimal.ZERO;
			String ids  ="";
			OrdenPago ordenPago = (OrdenPago)  request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			
			String esEdicionStr = (String) request.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			boolean esEdicion = false;
			if (ordenPago == null || ordenPago.getId() == null
					|| ordenPago.getId().equals(0) || esEdicionStr != null) {
				esEdicion = true;
			}

			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			
			List<FormaPago> pagos= null;
			if (ordenPago != null ){
				pagos = ordenPago.getFormaPago();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("tipo");
	 		headerNamesTercerizadora.add("numero");
	 		headerNamesTercerizadora.add("importe");
	 		if(portlet_name.equals("uoma")){
	 			headerNamesTercerizadora.add("cant-cuotas");
	 		}	 		
	 		headerNamesTercerizadora.add("emisor");
	 		headerNamesTercerizadora.add("observaciones");
	 		headerNamesTercerizadora.add("OP Origen");
	 		headerNamesTercerizadora.add("Fecha OP");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-anticipos-were-found"));
		
			
			if(null!=pagos){
				int total=pagos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < pagos.size(); i++) {
 			 			FormaPago pago = pagos.get(i);
 			 			if (pago.getTipo().equals("Anticipo")){
		 					ResultRow row = new ResultRow(pago, pago.getNumeroStr(), i);
		 					row.addText(pago.getTipo());
		 					row.addText(pago.getNumeroStr());
		 					totalPagos = totalPagos.add(pago.getImporte());
		 							 							 							 					
		 					Comprobante ant = ((Anticipo)pago.getPago()).getAnticipo();
		 					
		 					if(esEdicion){
		 						row.addText("<input type=\"text\" size=\"10\" id=\""+pago.getNumeroStr()+"_anticipo\" value=\""+pago.getImporte().negate().toString()+"\" onChange=\"javascript:actualizarValorAnticipos(this);\"/>");
		 					}else{
								row.addText(pago.getImporte().negate().toString());		 						
		 					}
		 					if(portlet_name.equals("uoma")){
	 							row.addText(String.valueOf(ant.getCuotasPlanString()));
		 					}
	 						row.addText(((Anticipo)pago.getPago()).getAnticipo().getCuitEmisor());	 	
		 					row.addText(ant.getObservaciones() != null ?  ant.getObservaciones().substring(0, ant.getObservaciones().length() >= 70 ? 70 : ant.getObservaciones().length()) : "");
		 					
		 					row.addText(String.valueOf(((Anticipo)pago.getPago()).getOpOrigen()));
		 					row.addText(String.valueOf(((Anticipo)pago.getPago()).getFechaOPOrigenAsString()));
		 							 					
		 					if (showABMButtons && esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraAnticipo('");
			 					sb.append(pago.getTipo());
			 					sb.append("','");
			 					sb.append(pago.getNumeroStr());
			 					sb.append("','");
			 					if (pago.getCuentaBancaria() != null) {
			 						sb.append(pago.getCuentaBancaria().getId_cuenta_bcria());
			 					} else {
			 						sb.append(0);
			 					}
			 					sb.append("','");
			 					sb.append(pago.getImporte().toString());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());			 					
		 			 		} else {
		 			 			row.addText("");
		 			 		}
		 					resultRowsInspector.add(row);
 			 			}
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	<br/>	

<table width="100%">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>
				<div align="left" id="<portlet:namespace />conceptos">
					<jsp:include page='conceptos_anticipo_search_result.jsp' /></div>
				</td>
			</td>
		</tr>
	</table>


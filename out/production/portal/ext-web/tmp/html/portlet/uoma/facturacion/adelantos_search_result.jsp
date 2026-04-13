<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ page import="ar.com.ospim.hoteles.beans.Recibo" %>
<%@ page import="java.text.DecimalFormat"%>
<portlet:defineObjects/>
			<% 
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			DecimalFormat df= new DecimalFormat("#0.00");
			boolean esEdicion = ParamUtil.getBoolean(request, "esEdicion", false);
			String ptoVtaAfip = ParamUtil.getString(request, "ptoVtaAfip");
			String portlet_name="liquidaciones";
			
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
			}
			if(renderResponse.getNamespace().equals("_TES_1_")){
				portlet_name = "tesoreria";
			}
			BigDecimal totalPagos = BigDecimal.ZERO;
			String ids  ="";
			
			Factura factura = (Factura)portletSession.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION,PortletSession.APPLICATION_SCOPE); 
			
			if (factura == null || factura.getId() ==0) {
				esEdicion = true;
			}

			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			
			List<Recibo> pagos= null;
			if (factura != null ){
				pagos = factura.getRecibosAdelantos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Sucursal");
	 		headerNamesTercerizadora.add("Número");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Fecha");
			if(esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}else{
				headerNamesTercerizadora.add("");
			}
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-anticipos-were-found"));
		
			
			if(null!=pagos){
				int total=pagos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < pagos.size(); i++) {
 			 			Recibo pago = pagos.get(i);
 			 			
		 				ResultRow row = new ResultRow(pago, pago.getNumero(), i);
		 				row.addText(pago.getSucursal());
		 				row.addText(pago.getNumero().toString());
		 				totalPagos = totalPagos.add(new BigDecimal(pago.getTotal()));
		 							 							 							 					
		 				//No se permite cambiar el importe del recibo para que no difiera del original.
		 				row.addText(df.format(pago.getTotal()*-1));	
		 				/*
		 				if(esEdicion){
		 						row.addText("<input type=\"text\" size=\"10\" id=\""+pago.getNumero()+"_anticipo\" value=\""+pago.getTotal()*(-1)+"\" onChange=\"javascript:actualizarValorAnticipos(this);\"/>");
		 				}else{
								row.addText(df.format(pago.getTotal()*-1));		 						
		 				}
		 				*/
		 				row.addText( sdf.format(pago.getFecha()));
		 							 					
		 				if (esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraAnticipo('");
			 					sb.append(pago.getSucursal());
			 					sb.append("','");
			 					sb.append(pago.getNumero());
			 					sb.append("','");
			 					sb.append(pago.getTotal().toString());
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
	<br/>	
    <table width="100%" align="left">
     <tr>
       <td><label><liferay-ui:message key="importe-anticipos" />:</label>&nbsp;&nbsp;&nbsp;<%=totalPagos.negate().toString()%></td>
       <input type="hidden" id="total_anticipos" value="<%=totalPagos.toString()%>"/>
      </tr>
    </table>

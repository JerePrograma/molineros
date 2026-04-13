<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad" %>
<%@page import="java.math.BigDecimal"%>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="ar.com.ospim.global.beans.ComprobanteItem" %>
<portlet:defineObjects/>
			<%
			NumberFormat formatter = new DecimalFormat("#0.00");
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<ComprobanteTratamientoDiscapacidad> tratamientos = null;
			SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			tratamientos= seguimiento.getLiquidaciones();
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			String claseExpediente = (String)request.getSession().getAttribute("clase_expediente");
			if (claseExpediente == null) {
				claseExpediente = (String) portletSession.getAttribute("clase_expediente", PortletSession.PORTLET_SCOPE);
			}
			
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);

			boolean showABMButtons = true;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		if("ME".equalsIgnoreCase(seguimiento.getClaseExpediente()) || "ME".equalsIgnoreCase(claseExpediente)  ){
	 		   headerNamesTercerizadora.add("Droga");	
	 		   headerNamesTercerizadora.add("Troquel");
	 		   headerNamesTercerizadora.add("Medicamento");
	 		}else if("PR".equalsIgnoreCase(seguimiento.getClaseExpediente()) || "OT".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
	 				 "PR".equalsIgnoreCase(claseExpediente) || "OT".equalsIgnoreCase(claseExpediente) ||
	 				 "HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
	 			     "HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
	 			     "DR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
	 			     "DB".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
	 			     "HI".equalsIgnoreCase(claseExpediente) ||
	 			     "HE".equalsIgnoreCase(claseExpediente) ||
	 			     "DR".equalsIgnoreCase(claseExpediente) ||
	 			     "DB".equalsIgnoreCase(claseExpediente)){
	 			headerNamesTercerizadora.add("    ");
	 			headerNamesTercerizadora.add("Código");
		 		headerNamesTercerizadora.add("Prestación");	 
	 		}else{
	 			headerNamesTercerizadora.add("Droga");	
		 		headerNamesTercerizadora.add("Troquel/Código");
		 		headerNamesTercerizadora.add("Medicamento/Prestación");
	 		}
	 		
	 		
	 		headerNamesTercerizadora.add("Prestador");
	 		
	 		headerNamesTercerizadora.add("Período");
	 		headerNamesTercerizadora.add("Comprobante");
	 		
	 		headerNamesTercerizadora.add("Total");
	 		headerNamesTercerizadora.add("Débitos");
	 		headerNamesTercerizadora.add("Importe Débitos");
	 		
	 		headerNamesTercerizadora.add("Comprobantes");
	 		headerNamesTercerizadora.add("Elimina");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=tratamientos){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < tratamientos.size(); i++) {
 			 		ComprobanteTratamientoDiscapacidad tratamiento = tratamientos.get(i);
 			 		
	 					ResultRow row = new ResultRow(tratamiento, tratamiento.getLiquidacionPrestacion().getId_liquidacion() , i);
	                    
	 					row.addText(tratamiento.getMedicamento().getDroga()==null?"":tratamiento.getMedicamento().getDroga());
	 					row.addText( Integer.toString(tratamiento.getMedicamento().getTroquel()));
	 					row.addText(tratamiento.getMedicamento().getNombre());
	 					row.addText(tratamiento.getPrestador().getDescripcion());
	 					
	 					row.addText(tratamiento.getLiquidacionPrestacion().getLiquidacion().getPeriodoString());
	 					row.addText(tratamiento.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_tipo()+ " " +
	 							    tratamiento.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_letra()+ " " +
	 							    tratamiento.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_numero());
	 					
	 					row.addText(tratamiento.getLiquidacionPrestacion().getLiquidacion().getImporte().toString());
	 					
//DS - Inicio	 					
	 					Double importeDebito=0D;
	 					StringBuilder sb0= new StringBuilder();
	 			 		if(tratamiento.getLiquidacionPrestacion().getLiquidacion().getDebitos()!=null){
	 			 			StringBuffer sb1 = new StringBuffer();
	 			 			for(ComprobanteItem c: tratamiento.getLiquidacionPrestacion().getLiquidacion().getDebitos()){
	 			 				sb0.append(c.getTipoComprobante()+ " " + c.getLetraComprobante() +" "+ c.getPtoVenta()+
	 			 						"-"+c.getNroComprobante()+";");
	 			 				importeDebito+= c.getSaldo()==null?0D:c.getSaldo().doubleValue();
	 			 			}
	 			 			row.addText(sb0.toString());
	 			 		}
	 			 		
	 			 		if(importeDebito!=0){
	 			 		   row.addText(formatter.format(importeDebito));
 			 	        } else {
 			 		      row.addText("");
 			 	        }  

// 			 	
//DS - Fin	 					
	 					
	 					
//	 					row.addText(formatter.format(tratamiento.getLiquidacionPrestacion().getCantidad().multiply(tratamiento.getLiquidacionPrestacion().getImporte())));
	 					StringBuilder sb1= new StringBuilder();
	 				 	StringBuilder sb= new StringBuilder();
	 					sb.append("<img alt=\"<liferay-ui:message key='ver'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/view.png\" onClick=\"javascript:verComprobantesME('");
	 					sb.append(tratamiento.getLiquidacionPrestacion().getId_liquidacion());
	 					sb.append("','");
	 					sb.append(tratamiento.getLiquidacionPrestacion().getId_prestacion());
	 					sb.append("','");
	 					sb.append(tratamiento.getLiquidacionPrestacion().getOrden());
	 					sb.append("');\" />");
	 					row.addText(sb.toString());
	 					
//	 					if (seguimiento.getId()==null || seguimiento.getId()==0){
			 				sb1.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 				sb1.append(themeDisplay.getPathThemeImages());
			 				sb1.append("/common/delete.png\" onClick=\"javascript:borraComprobanteME('");
			 				sb1.append(tratamiento.getLiquidacionPrestacion().getId_liquidacion());
			 				sb1.append("','");
			 				sb1.append(tratamiento.getLiquidacionPrestacion().getId_prestacion());
			 				sb1.append("','");
			 				sb1.append(tratamiento.getLiquidacionPrestacion().getOrden());
			 				sb1.append("');\" />");
			 				row.addText(sb1.toString());
			 				
//		 			 	} else {
//		 			 		row.addText("");
//		 			 	}
	 			 		resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	
<script type="text/javascript">
var importeSS=jQuery('#<portlet:namespace />importeSeguimientoSUR').val();	
//if(importeSS==null || Number(importeSS)==0 ){
	jQuery('#<portlet:namespace />importeSeguimientoSUR').val('<%=seguimiento.getImportePresentado()==null?0: (new DecimalFormat("#.00")).format(seguimiento.getImportePresentado())%>'.replace(",","."));
	
//}
</script>		
	
	
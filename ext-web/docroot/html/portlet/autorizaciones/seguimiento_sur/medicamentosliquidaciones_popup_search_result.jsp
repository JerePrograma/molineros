<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="ar.com.ospim.global.beans.ComprobanteItem" %>
<portlet:defineObjects/>
			<%
			String portlet_name=null;
			NumberFormat formatter = new DecimalFormat("#0.00");
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<ComprobanteTratamientoDiscapacidad> comprobantes = new ArrayList<ComprobanteTratamientoDiscapacidad> ();
			comprobantes= (ArrayList<ComprobanteTratamientoDiscapacidad>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD);
			if (comprobantes == null || comprobantes.size() == 0) {
				comprobantes = (ArrayList<ComprobanteTratamientoDiscapacidad>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD, PortletSession.PORTLET_SCOPE);
			}
			
			SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);

			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Cuit Prest");
	 		headerNamesTercerizadora.add("Prestador");
	 		headerNamesTercerizadora.add("Tipo");
	 		headerNamesTercerizadora.add("Letra");
	 		headerNamesTercerizadora.add("Número");
	 		headerNamesTercerizadora.add("Sucursal");
	 		headerNamesTercerizadora.add("Período");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Prestación");
	 		
	 		headerNamesTercerizadora.add("Débitos");
	 		headerNamesTercerizadora.add("Importe Débitos");
	 		
	 		headerNamesTercerizadora.add("");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=comprobantes){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < comprobantes.size(); i++) {
 			 		ComprobanteTratamientoDiscapacidad comprobante = comprobantes.get(i);
 			 		ResultRow row = new ResultRow(comprobante, comprobante.getLiquidacionPrestacion().getId_liquidacion() +
 			 				comprobante.getLiquidacionPrestacion().getId_prestacion(), i);
 			 		row.addText(comprobante.getPrestador().getCuit()==null?"":comprobante.getPrestador().getCuit());
 			 		row.addText(comprobante.getPrestador().getDescripcion());
 			 		row.addText(comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_tipo());
 			 		row.addText(comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_letra());
 			 		row.addText(comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_numero());
 			 		row.addText(String.valueOf(comprobante.getLiquidacionPrestacion().getLiquidacion().getSucu()));
 			 		row.addText(comprobante.getLiquidacionPrestacion().getPeriodo().toString() );
 			 		row.addText(formatter.format(comprobante.getLiquidacionPrestacion().getLiquidacion().getImporte()));
 			 		row.addText(comprobante.getLiquidacionPrestacion().getPrestacion().getDescripcion() );
 			 		
 			 		Double importeDebito=0D;
 			 		if(comprobante.getLiquidacionPrestacion().getLiquidacion().getDebitos()!=null){
 			 			StringBuffer sb1 = new StringBuffer();
 			 			for(ComprobanteItem c: comprobante.getLiquidacionPrestacion().getLiquidacion().getDebitos()){
 			 				sb1.append(c.getTipoComprobante()+ " " + c.getLetraComprobante() +" "+ c.getPtoVenta()+
 			 						"-"+c.getNroComprobante()+";");
 			 				importeDebito+= c.getSaldo()==null?0D:c.getSaldo().doubleValue();
 			 			}
 			 			row.addText(sb1.toString());
 			 		}
 			 		
 			 		row.addText(formatter.format(importeDebito));
 			 		
 			 		Boolean marcar=false;
 			 		

	 				if(seguimiento.getLiquidaciones()!=null &&	seguimiento.getLiquidaciones().size()>0){
	 						for(int xi=0;xi<seguimiento.getLiquidaciones().size();xi++){
	 							if(comprobante.getLiquidacionPrestacion().getId_liquidacion() == seguimiento.getLiquidaciones().get(xi).getLiquidacionPrestacion().getId_liquidacion() &&
	 							  comprobante.getLiquidacionPrestacion().getId_prestacion()  == seguimiento.getLiquidaciones().get(xi).getLiquidacionPrestacion().getId_prestacion() &&
								  comprobante.getLiquidacionPrestacion().getOrden()  == seguimiento.getLiquidaciones().get(xi).getLiquidacionPrestacion().getOrden()){
	 								
	 								marcar=true;
								        break;
	 							}
	 						}
	 				}


	 					StringBuffer sb0 = new StringBuffer();
	 					sb0.append("<input type=\"checkbox\"");
	 					sb0.append("name=\"comprob\"");
	 					if(marcar){
	 						sb0.append("\" checked=\"checked");
	 					}
/*	 					
	 					if(seguimiento.getId()!=null && seguimiento.getId()!=0){
	 						sb0.append("\" disabled=\"disabled");
	 					}
*/
	 					sb0.append("id=\"");
	 					sb0.append("formu-"+comprobante.getLiquidacionPrestacion().getId_liquidacion()+"|"+comprobante.getLiquidacionPrestacion().getId_prestacion()+"|"+comprobante.getTratamientoId()+"|"+comprobante.getPrestador().getId_prestador());
	 			        sb0.append("\" value=\"");
	 					sb0.append(comprobante.getLiquidacionPrestacion().getId_liquidacion()+"|"+comprobante.getLiquidacionPrestacion().getId_prestacion()+"|"+comprobante.getTratamientoId()+"|"+comprobante.getPrestador().getId_prestador()
	 							+"|"+comprobante.getLiquidacionPrestacion().getOrden());									
	 					sb0.append("\"/>");
	 					
	 					row.addText(sb0.toString());
	 					
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<div align="center" id="<portlet:namespace />seleccionmedicamentosliquidacionesdiv">
	</div>
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
   	<table>
		<tr>
			<td align="left"><input type="button" value="<liferay-ui:message key="Seleccionar" />" 
			onClick="<portlet:namespace />seleccionComprobantesME();" /></td>				
		</tr>
	</table>


<script type="text/javascript">
		function <portlet:namespace />seleccionComprobantesME() {
			var trat = document.getElementsByName('comprob');
			var tratValue = "";
			var i = 0;
			for (i = 0; i<trat.length; i++){
				if (trat[i].checked) {					
					tratValue= tratValue+trat[i].value+";"; 
				}
			}
			<portlet:namespace />seleccionarComprobantesME(tratValue);
		}
</script>			
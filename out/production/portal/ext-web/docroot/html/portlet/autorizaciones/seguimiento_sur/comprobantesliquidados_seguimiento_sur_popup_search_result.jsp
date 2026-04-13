<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="ar.com.ospim.global.beans.ComprobanteItem" %>
<portlet:defineObjects/>
			<%
			String portlet_name=null;
			NumberFormat formatter = new DecimalFormat("#0.00");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<SeguimientoSurComprobante> comprobantes = new ArrayList<SeguimientoSurComprobante> ();
			comprobantes= (ArrayList<SeguimientoSurComprobante>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_SEGUIMIENTOSUR);
			if (comprobantes == null || comprobantes.size() == 0) {
				comprobantes = (ArrayList<SeguimientoSurComprobante>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_SEGUIMIENTOSUR, PortletSession.PORTLET_SCOPE);
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
	 		headerNamesTercerizadora.add("Cuit");
	 		headerNamesTercerizadora.add("Razón Social");
	 		headerNamesTercerizadora.add("Pto.Vta.");
	 		headerNamesTercerizadora.add("Tipo");
	 		headerNamesTercerizadora.add("Letra");
	 		headerNamesTercerizadora.add("Número");
	 		headerNamesTercerizadora.add("Sucursal");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Emisión");
	 		headerNamesTercerizadora.add("Recepción");
	 		headerNamesTercerizadora.add("Vencimiento");
	 		
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
 			 		SeguimientoSurComprobante comprobante = comprobantes.get(i);
 			 		ResultRow row = new ResultRow(comprobante,
 			 				comprobante.getCuit() +	comprobante.getLetraComprobante()+
 			 				comprobante.getPtoVenta() + comprobante.getTipoComprobante()+
 			 				comprobante.getSucuComprobante() + comprobante.getNroComprobante(), i);
 			 		
 			 		row.addText(comprobante.getCuit()==null?"":comprobante.getCuit());
 			 		row.addText(comprobante.getAcreedorEmpresa().getRazon_soc());
 			 		row.addText(Integer.toString( comprobante.getPtoVenta()));
 			 		row.addText(comprobante.getTipoComprobante());
 			 		row.addText(comprobante.getLetraComprobante());
 			 		row.addText(comprobante.getNroComprobante());
 			 		row.addText(Integer.toString(comprobante.getSucuComprobante()));
 			 		row.addText(formatter.format(comprobante.getImporteComprobante()));
 			 		row.addText(comprobante.getFechaEmision()==null?"":sdf.format(comprobante.getFechaEmision()));
 			 		row.addText(comprobante.getFechaRecepcion()==null?"":sdf.format(comprobante.getFechaRecepcion()));
 			 		row.addText(comprobante.getFechaVencimiento()==null?"":sdf.format(comprobante.getFechaVencimiento()));
 			 		
 			 		
 			 		Boolean marcar=false;
 			 		

	 				if(seguimiento.getComprobantes() !=null &&	seguimiento.getComprobantes().size()>0){
	 						for(int xi=0;xi<seguimiento.getComprobantes().size();xi++){
	 							
	 							if( comprobante.getCuit().equalsIgnoreCase(seguimiento.getComprobantes().get(xi).getCuit()) &&
	 								comprobante.getTipoComprobante().equalsIgnoreCase(seguimiento.getComprobantes().get(xi).getTipoComprobante()) &&
	 								comprobante.getLetraComprobante().equalsIgnoreCase(seguimiento.getComprobantes().get(xi).getLetraComprobante()) &&
	 								comprobante.getPtoVenta()==seguimiento.getComprobantes().get(xi).getPtoVenta() &&
	 								comprobante.getNroComprobante().equalsIgnoreCase(seguimiento.getComprobantes().get(xi).getNroComprobante()) &&
	 								comprobante.getSucuComprobante()== seguimiento.getComprobantes().get(xi).getSucuComprobante()){
	 								
	 								marcar=true;
								        break;
	 							}
	 							
	 						}
	 				}


	 				StringBuffer sb0 = new StringBuffer();
	 				sb0.append("<input type=\"checkbox\"");
	 				sb0.append("name=\"comprobLiq\"");
	 				if(marcar){
	 						sb0.append("\" checked=\"checked");
	 				}
	 				sb0.append("id=\"");
	 				sb0.append("formu-"+ comprobante.getCuit() +"|"+comprobante.getTipoComprobante()+"|"+
	 						comprobante.getLetraComprobante() +"|"+
	 						comprobante.getPtoVenta() + "|" +
	 						comprobante.getSucuComprobante() + "|"+
	 						comprobante.getNroComprobante() + "|"+
	 						comprobante.getAcreedorEmpresa().getSucursal());
	 			    sb0.append("\" value=\"");
	 				sb0.append(comprobante.getCuit() +"|"+comprobante.getTipoComprobante()+"|"+
	 						   comprobante.getLetraComprobante() +"|"+
	 						   comprobante.getPtoVenta() + "|" +
	 						   comprobante.getSucuComprobante() + "|"+
	 						   comprobante.getNroComprobante()+ "|"+
	 			 			   comprobante.getAcreedorEmpresa().getSucursal());									
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
			onClick="<portlet:namespace />seleccionComprobantesLiq();" /></td>				
		</tr>
	</table>


<script type="text/javascript">
		function <portlet:namespace />seleccionComprobantesLiq() {
			var trat = document.getElementsByName('comprobLiq');
			var tratValue = "";
			var i = 0;
			for (i = 0; i<trat.length; i++){
				if (trat[i].checked) {					
					tratValue= tratValue+trat[i].value+";"; 
				}
			}
			<portlet:namespace />seleccionarComprobantesLiq(tratValue);
		}
</script>			
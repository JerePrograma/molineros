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
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

%>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
 					List<Comprobante> comprobantesProcesados = (ArrayList<Comprobante>)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS);
 					List<Comprobante> comprobantesErroneos = (ArrayList<Comprobante>)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS);
 					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("razon-soc");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("pto-venta");
			 		headerNames.add("numero");
			 		headerNames.add("orden-pago");
			 		headerNames.add("fecha-pago");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=comprobantesProcesados){					
					 	int total = comprobantesProcesados.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantesProcesados.size(); i++) {
					 		Comprobante comp = comprobantesProcesados.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( comp.getAcreedorEmpresa().getCuit());
		 					row.addText( comp.getAcreedorEmpresa().getRazon_soc());
		 					row.addText( comp.getTipoComprobante());
		 					row.addText(comp.getLetraComprobante());
			 				row.addText(String.format("%05d",comp.getPtoVenta()));
	 						row.addText( comp.getNroComprobante());
	 						row.addText(String.valueOf(comp.getIdOp()));
	 						row.addText(sdf.format(comp.getFechaPrimerPago()));
	 						resultRows.add(row);
						}
					 }
					
					 SearchContainer searchContainerErr = new SearchContainer(renderRequest, null, null,
							SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
							LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
						
					if(null!=comprobantesErroneos){					
					 	int total = comprobantesErroneos.size();
					 	searchContainerErr.setTotal(total);
						List resultRows = searchContainerErr.getResultRows();
						for (int i = 0; i < comprobantesErroneos.size(); i++) {
								Comprobante comp = comprobantesErroneos.get(i);
				 				ResultRow row = new ResultRow(comp, comp.hashCode(), i);
				 					
				 				row.addText( comp.getAcreedorEmpresa().getCuit());
				 				row.addText( comp.getAcreedorEmpresa().getRazon_soc());
				 				row.addText( comp.getTipoComprobante());
				 				row.addText(comp.getLetraComprobante());
					 			row.addText(String.format("%05d",comp.getPtoVenta()));
			 					row.addText( comp.getNroComprobante());
		 						row.addText(String.valueOf(comp.getIdOp()));
		 						row.addText(sdf.format(comp.getFechaPrimerPago()));
			 					resultRows.add(row);
						}
					}
		%>
			<fielset>
             <legend>Comprobantes Procesados</legend>
			  <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
			</fielset>  
			
			<fielset>
               <legend>Comprobantes NO Procesados</legend>
			   <liferay-ui:search-iterator searchContainer="<%= searchContainerErr %>" />
			</fielset>   	

<script type="text/javascript">
</script>	

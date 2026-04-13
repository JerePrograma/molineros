<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.liquidaciones.ChequeSinChequeraException" %>
<%@ page import="ar.com.ospim.global.beans.Cheque.Tipo"%>
<%@ page import="ar.com.ospim.global.beans.RetencionIIBB"%>
<%@ page import="ar.com.ospim.global.beans.RetencionIVA"%>
<% 

List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

Cheque cheque = (Cheque)request.getAttribute("CHEQUE_DUPLICADO");
	if (cheque != null){ %>
<span class="portlet-msg-error">Numero de cheque existente: <%=cheque.getNumero().toString()%></span>
<%}%>

<liferay-ui:error
	exception="<%= DuplicateNumeroChequeException.class %>"
	message="numero-cheque-duplicado" />

<liferay-ui:error
	exception="<%= ChequeSinChequeraException.class %>"
	message="cheque-sin-chequera" />	
	
<liferay-ui:error
	exception="<%= ComprobanteInexistenteException.class %>"
	message="anticipo-inexistente" />

<portlet:defineObjects />
<% 
			BigDecimal totalPagos = BigDecimal.ZERO;
			String ids  ="";
			OrdenPago ordenPago = (OrdenPago) request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			
			String esEdicionStr = (String) request.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			String modificaFormaPago=ParamUtil.getString(request, "modificaFormaPago");
			boolean esEdicion = false;
			if (ordenPago == null || ordenPago.getId() == null
					|| ordenPago.getId().equals(0) || esEdicionStr != null ||
							"true".equalsIgnoreCase(modificaFormaPago)) {
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
	 		headerNamesTercerizadora.add("cuenta-bancaria");
	 		headerNamesTercerizadora.add("descripcion");
	 		headerNamesTercerizadora.add("a-nombre-de");
	 		headerNamesTercerizadora.add("");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}else if (showABMButtons && ordenPago!= null && ordenPago.getId() != null && ordenPago.getId().intValue() != 0){
				headerNamesTercerizadora.add("");	
			}else{
				headerNamesTercerizadora.add("");
			}
			headerNamesTercerizadora.add("");
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-cheques-were-found"));
		
			
			if(null!=pagos){
				int total=pagos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < pagos.size(); i++) {
 			 			FormaPago pago = pagos.get(i);
 			 			if (!pago.getTipo().equals("Anticipo")){
		 					ResultRow row = new ResultRow(pago, pago.getNumeroStr(), i);
		 					if(pago.getTipo().equals("Cheque") && ((Cheque)pago.getPago()).getDebitoCredito().equals(Cheque.Tipo.CREDITO)){		 						
		 						row.addText("Cheque de Terceros");		 						
		 					}else{
		 						row.addText(pago.getTipo());
		 					}
		 					row.addText(pago.getNumeroStr());
		 					totalPagos = totalPagos.add(pago.getImporte());
		 					row.addText(pago.getImporte().toString());
		 					if(!pago.getTipo().equals("RetencionGanancias")){
			 					if (pago.getCuentaBancaria()  != null && pago.getCuentaBancaria().getId_cuenta_bcria()!=0) {
				 					int index = ctas.indexOf(pago.getCuentaBancaria());
				 					row.addText(ctas.get(index).getDescripcion() + " " +ctas.get(index).getNro_cuenta()+ "/" + ctas.get(index).getSucursal());
			 					} else {
			 						row.addText("");
			 					}
		 					}else{
		 						row.addText("");
		 					}
		 					
			 				row.addText(pago.getDescripcion());
		 					
	 			 			if (pago.getPago().getPagoBancario() != null && !StringUtils.checkEmpty(pago.getPago().getPagoBancario().getCuilCuenta()) ){
		 						row.addText(pago.getPago().getPagoBancario().getApellidoCuenta() + ", "
		 									+ pago.getPago().getPagoBancario().getNombreCuenta() + " - Cuil: "
		 									+ pago.getPago().getPagoBancario().getCuilCuenta() + " - Email: "
		 									+ pago.getPago().getPagoBancario().getEmailCuenta()
		 								
		 						);
		 					}else{
		 						row.addText(pago.getANombreDe() != null ? pago.getANombreDe() : "");
		 					}
		 					row.addText("");
		 					
		 					if (showABMButtons && esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraPago('");
			 					sb.append(pago.getPago().getClass().getSimpleName() + pago.getPago().getIdTipo());
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
			 					sb.append("','");
			 					if(pago.getTipo().equals("Cheque")){
			 						sb.append( ((Cheque) pago.getPago()).getCuit()  );
			 					}else{
			 						sb.append("");
			 					}
			 					
			 					sb.append("','");
			 					if(pago.getTipo().equals("RetencionIIBB")){
			 						sb.append( ((RetencionIIBB) pago.getPago()).getJurisdiccion() );
			 					}else{
			 						sb.append("");
			 					}
			 					
			 					sb.append("');\" />");
			 					
			 					row.addText(sb.toString());
			 					
		 			 		} else {
		 			 			row.addText("");
		 			 		}
		 					if (showABMButtons && ordenPago!= null && ordenPago.getId() != null && ordenPago.getId().intValue() != 0 
		 							&& (ordenPago instanceof OrdenPagoOspim)){
		 						StringBuilder sb= new StringBuilder();
		 						if (pago.getPago() instanceof Cheque){
			 						sb.append("<a href=\"javascript:void(0)\" onclick=\"imprimirChequePorUnidad('" + pago.getNumeroStr() +"')\">Imprimir</a>");
			 						row.addText(sb.toString());
		 						}
		 					}
		 					resultRowsInspector.add(row);
 			 			}
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>

<liferay-ui:error exception="<%= Exception.class %>"
	message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false"
	searchContainer="<%=searchContainer%>" />
<table width="100%" align="left">
	<tr>
		<td><label><liferay-ui:message key="importe-pagos" />:</label>&nbsp;&nbsp;&nbsp;<%=totalPagos.toString()%></td>
		<input type="hidden" id="total_formas_pago"
			value="<%=totalPagos.toString()%>" />
	</tr>
</table>

<script type="text/javascript">
	function imprimirChequePorUnidad(nro,idbanco){
		var op_nro = jQuery("#orden_pago_id").val();
			window.location.href ="/odtservlet/?accion=cheque&numero=" + nro +"&numero_op="+op_nro;
		}
</script>
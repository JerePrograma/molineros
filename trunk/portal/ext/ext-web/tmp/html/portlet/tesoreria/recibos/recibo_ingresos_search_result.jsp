<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<% 
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

 List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
.getSession().getAttribute(
		WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION); 

Cheque cheque = (Cheque)request.getAttribute("CHEQUE_DUPLICADO");
	if (cheque != null){ %>
		<span class="portlet-msg-error">N&umero;mero de cheque existente: <%=cheque.getNumero().toString()%></span> 
	<%}%>

<liferay-ui:error exception="<%= DuplicateNumeroChequeException.class %>" message="cheque-ya-ingresado" />

<portlet:defineObjects/>
			<% 
			BigDecimal capital = new BigDecimal("0");
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

			
			String ids  ="";
			Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

						
			/*if (recibo != null && recibo.getId() != 0){
				esEdicion = false;
			}*/
			
			//Si debe mostrarse el btn de agregar ingreso			
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);
			List<ReciboIngreso> ingresos= null;
			if (recibo != null ){
				ingresos = recibo.getIngresos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("tipo");
	 		headerNamesTercerizadora.add("numero");
	 		if(portlet_name.equals("uoma")){
	 			headerNamesTercerizadora.add("suc-bco");
	 		}	 		
	 		headerNamesTercerizadora.add("importe");
	 		headerNamesTercerizadora.add("banco");
	 		headerNamesTercerizadora.add("cuenta-bancaria-destino");
	 		headerNamesTercerizadora.add("fecha-pago");
			if(showABMButtons) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-cheques-were-found"));
		
			
			if(null!=ingresos){
				int total=ingresos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < ingresos.size(); i++) {
 			 			Ingreso ingreso = ingresos.get(i).getIngreso();
 			 			if (ingreso.getTipo().equals("Anticipo")){
 			 				total--;
 			 				continue;
 			 			}
	 					ResultRow row = new ResultRow(ingreso,ingreso.getNumeroStr(), i);
	 					row.addText(ingreso.getTipo());
	 					row.addText(ingreso.getNumeroStr());
	 					if(portlet_name.equals("uoma") && ingreso.getTipo().equals("Deposito Bancario")){
	 						if(ingreso.getTipo().equals("Deposito Bancario")){
	 							row.addText(String.valueOf(((DepositoBancario)ingreso).getSucuNacion()));
	 						}
	 					}else if(portlet_name.equals("uoma")){
	 						row.addText("");
	 					}
	 					row.addText(ingreso.getImporte().toString());
	 					capital = capital.add(ingreso.getImporte());
	 					if (ingreso.getBanco() != null) {
		 					int index = bancos.indexOf(ingreso.getBanco());
		 					row.addText(bancos.get(index).getDescripcion_banco());
	 					} else {
	 						row.addText("");
	 					}
	 				/* 	if (ingreso.getCuentaBancaria()  != null && ingreso.getCuentaBancaria().getId_cuenta_bcria()!=0) {
		 					int index = ctas.indexOf(ingreso.getCuentaBancaria());		 					
		 					row.addText(ctas.get(index).getDescripcion()+" "+String.valueOf(ctas.get(index).getNro_cuenta())+"/"+String.valueOf(ctas.get(index).getSucursal()));
	 					} else {
	 						row.addText("");
	 					} */
	 					if (ingreso.getCuentaBancaria() != null /* && ingreso.getCuentaBancaria().getId_cuenta_bcria()!=0 */) {	 					
	 						if(portlet_name.equals("uoma") || portlet_name.equals("farmacia") ){
	 							int index = ctas.indexOf(ingreso.getCuentaBancaria());
	 							if(index >= 0){
			 						row.addText(ctas.get(index).getDescripcion()+" "+String.valueOf(ctas.get(index).getNro_cuenta())+"/"+String.valueOf(ctas.get(index).getSucursal()));
	 							}else{
	 								row.addText( ingreso.getCuentaBancaria().getDescripcion()!=null?ingreso.getCuentaBancaria().getDescripcion():"");
	 							}
	 						}else{
	 							row.addText( ingreso.getCuentaBancaria().getDescripcion()!=null?ingreso.getCuentaBancaria().getDescripcion():"");
	 						}
	 					} else {
	 						row.addText("");
	 					} 
	 					row.addText(ingreso.getFechaAsString());
	 					resultRowsInspector.add(row);
	 					if (showABMButtons && ingresos.get(i).getMovBcrioId()==0){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraIngreso('");
		 					sb.append(ingreso.getClass().getName());
		 					sb.append("','");
		 					sb.append(ingreso.getNumeroStr());
		 					sb.append("','");
		 					if (ingreso.getBanco() != null) {
		 						sb.append(ingreso.getBanco().getId_banco());
		 					} else {
		 						sb.append(0);
		 					}
		 					sb.append("','");
		 					if (ingreso.getCuentaBancaria() != null) {
		 						sb.append(ingreso.getCuentaBancaria().getId_cuenta_bcria());
		 					} else {
		 						sb.append(0);
		 					}
		 					sb.append("','");
		 					sb.append(ingreso.getImporte().toString());
		 					sb.append("','");		 					
		 					sb.append(String.valueOf(ingreso.getConvenioId()));
		 					sb.append("','");		 					
		 					sb.append(String.valueOf(ingreso.getActaId()));
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else if(ingresos.get(i).getMovBcrioId()>0){
	 			 			row.addText("Ingreso con Mov. Bcrio");
	 			 		} else if (!showABMButtons){
	 			 			row.addText("");
	 			 		}
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

	<script type="text/javascript" >		
		document.getElementById("capitalIngresoTmp").value = "<%=capital.toString()%>";					
	</script>

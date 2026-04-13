<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Cheque" %>
<% 

List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
.getSession().getAttribute(
		WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

Cheque cheque = (Cheque)request.getAttribute("CHEQUE_DUPLICADO");
	if (cheque != null){ %>
		<span class="portlet-msg-error">Numero de cheque existente: <%=cheque.getNumero().toString()%></span> 
	<%}%>

<liferay-ui:error exception="<%= DuplicateNumeroChequeException.class %>" message="cheque-ya-ingresado" />

<portlet:defineObjects/>
			<% 
			BigDecimal capital = new BigDecimal("0");
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

			
			String ids  ="";
			Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);


			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.RECIBOS_ACTION_EDICION) != null || recibo == null) {
				esEdicion = true; 
			}
			String esEd = ParamUtil.getString(request, "esEdicion");
			if (esEd == null || esEd.equals("")){
				esEd = (String) request.getAttribute("esEdicion");
			}
			if (esEd != null && !esEd.equals("")){
				esEdicion= Boolean.parseBoolean(esEd);
			}
			if (recibo != null && recibo.getId() != 0){
				esEdicion = false;
			}
			
			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
			List<ReciboIngreso> ingresos= null;
			if (recibo != null ){
				ingresos = recibo.getIngresos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("tipo");
	 		headerNamesTercerizadora.add("numero");
	 		headerNamesTercerizadora.add("importe");
	 		headerNamesTercerizadora.add("banco");
	 		headerNamesTercerizadora.add("cuenta-bancaria-destino");
	 		headerNamesTercerizadora.add("fecha-pago");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,200, portletURLTercerizadora, headerNamesTercerizadora,
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
	 					row.addText(ingreso.getImporte().toString());
	 					capital = capital.add(ingreso.getImporte());
	 					if (ingreso.getBanco() != null) {
		 					int index = bancos.indexOf(ingreso.getBanco());
		 					row.addText(bancos.get(index).getDescripcion_banco());
	 					} else {
	 						row.addText("");
	 					}
	 					if (ingreso.getCuentaBancaria()  != null && ingreso.getCuentaBancaria().getId_cuenta_bcria()!=0) {
		 					int index = ctas.indexOf(ingreso.getCuentaBancaria());
		 					row.addText(ctas.get(index).getDescripcion() + "/" + ctas.get(index).getSucursal());
	 					} else {
	 						row.addText("");
	 					}
	 					row.addText(ingreso.getFechaAsString());
	 					resultRowsInspector.add(row);
	 					if (showABMButtons && esEdicion && ingreso.isNew()){
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
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else if (showABMButtons && esEdicion && !ingreso.isNew()){
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

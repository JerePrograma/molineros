<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<% Cheque cheque = (Cheque)request.getAttribute("CHEQUE_DUPLICADO");
	if (cheque != null){ %>
		<span class="portlet-msg-error">N&uacute;mero de cheque existente: <%=cheque.getNumero().toString()%></span> 
	<%}%>

<portlet:defineObjects/>
			<% 
			int maxCuota = 0;
			BigDecimal interes = new BigDecimal("0");
			BigDecimal capital = new BigDecimal("0");
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

			
			String ids  ="";
			Convenio convenio= (Convenio)request.getSession().getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);


			
			boolean esEdicion =  Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
			
			
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
				List<ConvenioPago> pagos= null;
				if (convenio != null ){
					pagos =convenio.getPagos();
				}
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("Tipo");
		 		headerNamesTercerizadora.add("cuota-nro");
		 		headerNamesTercerizadora.add("cheque-nro");
		 		headerNamesTercerizadora.add("capital");
		 		headerNamesTercerizadora.add("interes");
		 		headerNamesTercerizadora.add("total");
		 		headerNamesTercerizadora.add("banco");
		 		headerNamesTercerizadora.add("cta-bcria");
		 		headerNamesTercerizadora.add("fecha-pago");
				if(showABMButtons && esEdicion) { 
					headerNamesTercerizadora.add("Borrar");
				}				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,10000, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-cheques-were-found"));
			
				
				if(null!=pagos){
					int total=pagos.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < pagos.size(); i++) {
	 			 		if (pagos.get(i).isBorradoLogico()
	 			 				|| pagos.get(i).getTipo().equals(ConvenioPago.Tipo.CUOTA)) {
	 			 			total--;
	 			 		} else {
		 					ResultRow row = new ResultRow(pagos.get(i),pagos.get(i).getId(), i);
		 					if (pagos.get(i).getCheque() != null){
		 						row.addText("Cheque");
		 					} else if (pagos.get(i).getPagare() != null){
		 						row.addText("Pagaré");
		 					} else{
		 						row.addText("Depósito");
		 					}
		 					
		 					row.addText(String.valueOf(pagos.get(i).getNroCuota()));
		 					if (maxCuota < pagos.get(i).getNroCuota()){
		 						maxCuota = pagos.get(i).getNroCuota();
		 					}
		 					if (pagos.get(i).getCheque() != null) {
			 					row.addText(pagos.get(i).getCheque().getNumero().toString());
		 					} else {
		 						row.addText("");
		 					}
		 					row.addText(pagos.get(i).getImporte().toString());
		 					capital = capital.add(pagos.get(i).getImporte());
		 					row.addText(pagos.get(i).getInteres().toString());
		 					interes = interes.add(pagos.get(i).getInteres());
		 					BigDecimal totalCapInt=(null!=pagos?pagos.get(i).getImporte():BigDecimal.ZERO).add(null!=pagos?pagos.get(i).getInteres():BigDecimal.ZERO);
		 					row.addText(totalCapInt.toString());
		 					if (pagos.get(i).getCheque() != null) {
		 					int index = bancos.indexOf(pagos.get(i).getCheque().getBanco());
		 						row.addText(bancos.get(index).getDescripcion_banco());
		 						if(pagos.get(i).getCheque().getCuentaBancaria()!=null &&
		 								pagos.get(i).getCheque().getCuentaBancaria().getDescripcion()!= null){
			 						row.addText(pagos.get(i).getCheque().getCuentaBancaria().getDescripcion());
			 					}else{
			 						row.addText("");
		 			 			}	
		 					} else {
		 						row.addText("");
		 						row.addText("");
		 					}
		 					
		 					row.addText(pagos.get(i).getFechaPagoAsString());
		 					resultRowsInspector.add(row);
		 					if (showABMButtons && esEdicion){
			 					StringBuilder sb= new StringBuilder();
				 				sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 				sb.append(themeDisplay.getPathThemeImages());
		 						if (pagos.get(i).getCheque() != null){
				 					sb.append("/common/delete.png\" onClick=\"javascript:borraCheque('");
				 					sb.append(pagos.get(i).getCheque().getNumero().toString());
				 					sb.append("','" + pagos.get(i).getCheque().getBanco().getId_banco());
				 					if(pagos.get(i).getCheque().getCuentaBancaria()!=null){
				 						sb.append("','" + pagos.get(i).getCheque().getCuentaBancaria().getId_cuenta_bcria() );
				 					}else{
				 						sb.append("','0");
				 					}
		 						} else {
				 					sb.append("/common/delete.png\" onClick=\"javascript:borraDepositoBancario('");
				 					sb.append(pagos.get(i).getNroCuota());
		 						}
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}
	 			 		}
	 				searchContainer.setTotal(total);
		 			}
				}
				maxCuota++;
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	<%if(null!=capital){%><b>Capital= <%=capital %></b><%}%>&nbsp;&nbsp;&nbsp;&nbsp;<%if(null!=interes){%><b>Inter&eacute;s= <%=interes %></b>
		&nbsp;&nbsp;&nbsp;&nbsp;<%}%> 
	<b>Total= <%=(interes!=null?interes:BigDecimal.ZERO).add(capital!=null?capital:BigDecimal.ZERO)%></b>
	

	<script type="text/javascript" >
		jQuery(document).ready(function(){
			document.getElementById("max_cuota").value = "<%=maxCuota%>";
			document.getElementById("interesCheque").value = "<%=interes.toString()%>";
			document.getElementById("capitalCheque").value = "<%=capital.toString()%>";
		});
	</script>

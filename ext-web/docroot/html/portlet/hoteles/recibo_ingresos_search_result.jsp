<%@ include file="/html/portlet/hoteles/init.jsp" %>
<%@ page import="ar.com.uoma.facturacion.*" %>
<%@ page import="ar.com.ospim.global.beans.Cheque" %>
<%@ page import="ar.com.ospim.global.beans.Ingreso" %>
<%@ page import="ar.com.ospim.global.beans.Efectivo" %>
<%@ page import="ar.com.ospim.global.beans.Pagare" %>
<%@ page import="ar.com.ospim.global.beans.DepositoBancario"%>
<%@ page import="ar.com.ospim.global.beans.TarjetaDebitoCredito" %>
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

if(renderResponse.getNamespace().equals("_HOT_1_")){
	portlet_name = "hoteles";
}

boolean esEdicion = ParamUtil.getBoolean(request, "esEdicion", false);

//List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
//.getSession().getAttribute(
//		WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION); 

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
			Recibo recibo = (Recibo) portletSession.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION,PortletSession.APPLICATION_SCOPE); 


						
			/*if (recibo != null && recibo.getId() != 0){
				esEdicion = false;
			}*/
			
			//Si debe mostrarse el btn de agregar ingreso			
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);
			List<FacturaIngreso> ingresos= null;
			if (recibo != null ){
				ingresos = recibo.getIngresos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("tipo");
	 		headerNamesTercerizadora.add("numero");
	 		headerNamesTercerizadora.add("emisor");
	 		if(portlet_name.equals("uoma") || portlet_name.equals("hoteles")){
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
	 					row.addText(ingreso.getEmisorDescripcion()==null?"":ingreso.getEmisorDescripcion());
	 					if((portlet_name.equals("uoma") || portlet_name.equals("hoteles")) && ingreso.getTipo().equals("Deposito Bancario")){
	 						if(ingreso.getTipo().equals("Deposito Bancario")){
	 							row.addText(String.valueOf(((DepositoBancario)ingreso).getSucuNacion()));
	 						}
	 					}else if(portlet_name.equals("uoma") || portlet_name.equals("hoteles")){
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
	 					if (ingreso.getCuentaBancaria() != null /* && ingreso.getCuentaBancaria().getId_cuenta_bcria()!=0 */) {	 					
	 						if(portlet_name.equals("uoma") || portlet_name.equals("hoteles")){
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
	 					if (showABMButtons && ingresos.get(i).getMovBcrioId()==0  && esEdicion){
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
	 			 		} else if(ingresos.get(i).getMovBcrioId()>0 && esEdicion){
	 			 			row.addText("Ingreso con Mov. Bcrio");
	 			 		} else if (!showABMButtons || !esEdicion){
	 			 			row.addText("");
	 			 		}
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>
 	
 	<style>
 	input[type=text].total {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 16px;
	color: black;
	background:#C2F9F4;
	text-align: right;  
	}
	
	label.total{
	  font-family: Arial, Helvetica, sans-serif;
	  font-size: 14px;
	  color: black;
	}
 	
 	</style>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	<br>
	<table>
	    <tr>
			<td>
			    <label class="total">Total Recibo:</label>
			</td>
			<td><input id="<portlet:namespace />total_recibo" class="total"
					    name="<portlet:namespace />total_recibo" size="15"
					    maxlength="15" type="text" readonly="readonly" 
					    value='<%=recibo.getTotal()!=null?(new DecimalFormat( "#,###,###,##0.00" )).format(recibo.getTotal()):"" %>' />
			</td>
		</tr>
	</table>
	

	<script type="text/javascript" >		
	</script>

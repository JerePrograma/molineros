<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.EstadoChequeInvalidoException" %>
<%@ page import="ar.com.ospim.tesoreria.ErrorAlSacarChequeException" %>

<% Cheque cheque = (Cheque)request.getAttribute("CHEQUE_DUPLICADO");
	if (cheque != null){ %>
		<span class="portlet-msg-error">N&uacute;mero de cheque existente: <%=cheque.getNumero().toString()%></span> 
	<%}%>



<liferay-ui:error exception="<%= ErrorAlSacarChequeException.class %>" message="error-desconocido-sacando-cheque" />
<liferay-ui:error exception="<%= EstadoChequeInvalidoException.class %>" message="estado-cheque-invalido-exception" />

<portlet:defineObjects/>
			<% 
			boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);

			
			String ids  ="";
			Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
				esEdicion = true;
			}
			esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

			if (acta != null && acta.isActaCerrada()){
				esEdicion = false;
			}
			
			if (acta != null && auditorActas) {
				esEdicion = true;
			}
			
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
				List<ActaPago> pagos= null;
				if (acta != null ){
					pagos =acta.getPagos();
				}
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("tipo");
		 		headerNamesTercerizadora.add("cheque-nro");
		 		headerNamesTercerizadora.add("importe");
		 		headerNamesTercerizadora.add("banco");
		 		headerNamesTercerizadora.add("cta-bcria");
		 		headerNamesTercerizadora.add("fecha-pago");
				if(showABMButtons && esEdicion) { 
					headerNamesTercerizadora.add("Borrar");
				}				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-cheques-were-found"));
			
				
				if(null!=pagos){
					int total=pagos.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < pagos.size(); i++) {
	 			 		if (pagos.get(i).getIngreso() == null || pagos.get(i).isBorradoLogico() || pagos.get(i).getTipo().equals(ActaPago.Tipo.CUOTA)) {
	 			 			total--;
	 			 		} else {
		 					ResultRow row = new ResultRow(pagos.get(i),pagos.get(i).getId(), i);
		 					row.addText(pagos.get(i).getIngreso().getClass().getSimpleName());
		 					row.addText(pagos.get(i).getIngreso().getNumeroStr());
		 					row.addText(pagos.get(i).getIngreso().getImporte().toString());
		 					if (pagos.get(i).getIngreso().getBanco() != null){ 
		 					int index = bancos.indexOf(pagos.get(i).getIngreso().getBanco());
			 					row.addText(bancos.get(index).getDescripcion_banco());
			 					if(pagos.get(i).getIngreso().getCuentaBancaria()!=null && 
			 							pagos.get(i).getIngreso().getCuentaBancaria().getDescripcion() != null){
			 						row.addText(pagos.get(i).getIngreso().getCuentaBancaria().getDescripcion());
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
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraIngreso('");
			 					sb.append(pagos.get(i).getIngreso().getClass().getName());
			 					sb.append("','");
			 					sb.append(pagos.get(i).getIngreso().getImporte());
			 					sb.append("','");
			 					sb.append(pagos.get(i).getIngreso().getNumeroStr());
			 					sb.append("','");
			 					if (pagos.get(i).getIngreso().getBanco() != null){
			 					  	sb.append(pagos.get(i).getIngreso().getBanco().getId_banco());
			 					} else {
			 						sb.append("0");
			 					}
			 					sb.append("','");
			 					sb.append(pagos.get(i).getIngreso().getFechaAsString());
			 					sb.append("','" + pagos.get(i).getId());
			 					sb.append("','");
			 					if (pagos.get(i).getIngreso().getCuentaBancaria() != null){
			 					  	sb.append(pagos.get(i).getIngreso().getCuentaBancaria().getId_cuenta_bcria());
			 					} else {
			 						sb.append("0");
			 					}
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}
	 			 		}
	 				searchContainer.setTotal(total);
		 			}
				}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainer%>" />
		

<%@ include file="/html/portlet/empresas/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%		
				EntidadPadronUnificado empresa=null;
				
				empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);					
				
				List<CuentaBancaria> cuentas=null;
				if(null!=empresa){
					cuentas=empresa.getCuentasBcrias();
				}
				
				boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
				
							
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("banco");
		 		headerNames.add("sucursal");
		 		headerNames.add("Cta.Bcria.");		 		
		 		headerNames.add("cbu");
		 		headerNames.add("F.U.M");
		 		headerNames.add("user");		 		
		 		headerNames.add("delete");
		 		
		 				 			 		
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-cuentas-were-found"));			
			
				if(null!=cuentas){
	 				//Seteo el total de la lista.
				 	int total = cuentas.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < cuentas.size(); i++) {				 		
				 		CuentaBancaria cuenta = (CuentaBancaria) cuentas.get(i);
				 		if(null==cuenta.getBajaFecha()){
		 					ResultRow row = new ResultRow(cuenta,cuenta.getDescripcion(), i);
			 				row.addText(cuenta.getBanco().getDescripcion_banco(renderRequest));
			 				row.addText(cuenta.getSucursalString());
			 				row.addText(cuenta.getDescripcion());
			 				row.addText(cuenta.getCBU()!=null?cuenta.getCBU():"");
			 				row.addText(cuenta.getModiFechaAsString());
			 				row.addText(cuenta.getModiUsr());			 				
			 				
						 		StringBuilder sb= new StringBuilder();
						 		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
						 		sb.append(themeDisplay.getPathThemeImages());
						 		sb.append("/common/delete.png\" onClick=\"javascript:borraCuenta('");			 			
						 		sb.append(empresa.getCuit());
						 		sb.append("','");
						 		sb.append(empresa.getSucursal());
						 		sb.append("','");
						 		sb.append(cuenta.getId_cuenta_bcria());
						 		sb.append("','");
						 		sb.append(cuenta.getBanco().getId_banco());
						 		sb.append("','");
						 		sb.append(cuenta.getDescripcion());
						 		sb.append("','");
						 		sb.append(cuenta.getSucursalString());
						 		sb.append("','");
						 		sb.append(cuenta.getCBU());
						 		sb.append("');\" />");
						 		sb.append("/");
						 		sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
						 		sb.append(themeDisplay.getPathThemeImages());
						 		sb.append("/common/edit.png\" onClick=\"javascript:editaCuenta('");			 			
						 		sb.append(empresa.getCuit());
						 		sb.append("','");
						 		sb.append(empresa.getSucursal());
						 		sb.append("','");
						 		sb.append(cuenta.getId_cuenta_bcria());
						 		sb.append("','");
						 		sb.append(cuenta.getBanco().getId_banco());
						 		sb.append("','");
						 		sb.append(cuenta.getDescripcion());
						 		sb.append("','");
						 		sb.append(cuenta.getSucursalString());
						 		sb.append("','");
						 		sb.append(cuenta.getCBU());
						 		sb.append("');\" />");
						 		row.addText(sb.toString());
			 				
			 				
				 			resultRows.add(row);
				 		}
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	

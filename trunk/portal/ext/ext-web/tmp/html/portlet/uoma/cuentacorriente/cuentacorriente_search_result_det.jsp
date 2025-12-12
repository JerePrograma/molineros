<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
				
		<table class="lfr-table">
			<tr>	
				<th colspan="6" align="center" >
					<label>Saldo Inicial</label>
				</th>
				<th colspan="6" align="center" >
					<label>Total Declarado</label>
				</th>
				<th colspan="6" align="center" >
					<label>Ingreso por Boletas</label>
				</th>
				<th colspan="6" align="center" >
					<label>Total por Actas</label>
				</th>
				<th colspan="6" align="center" >
					<label>Saldo</label>
				</th>
				<th colspan="6" align="center" >
					<label>Otros Ingresos</label>
				</th>
			</tr>				
			<tr>
				<td colspan="6" align="center" >
					<label>0,00</label>
				</td>
				<td colspan="6" align="center">
					<label>760.548,58</label>
				</td>
				<td colspan="6" align="center">
					<label>999.999,99</label>
				</td>
				<td colspan="6" align="center">
					<label>999.999,99</label>
				</td>
				<td colspan="6" align="center">
					<label>999.999,99</label>
				</td>
				<td colspan="6" align="center">
					<label>999.999,99</label>
				</td>
			</tr>
			<%
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);				
					List<CuentaCorrienteEmpresa> ctacteList= (ArrayList<CuentaCorrienteEmpresa>)session.getAttribute("CTACTE_RESULT");
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		
					List resultRows;
					SearchContainer searchContainer;
					
					List<String> headerNames = new ArrayList<String>();
					headerNames.add("Cuit");
					headerNames.add("Detalle");
					headerNames.add("Periodo");
			 		headerNames.add("Saldo");
			 		headerNames.add("Detalle");

		 			searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-empresas-were-found"));
		 			
		 			//Seteo el total de la lista.
				 	int total = ctacteList.size();
			 		
				 	searchContainer.setTotal(total);
			 		resultRows = searchContainer.getResultRows();
			 		PortletURL rowURL = renderResponse.createRenderURL();
			 		
					if(null!=ctacteList){
				 								 	
					 	for (int i = 0; i < ctacteList.size(); i++) {
					 		
					 		CuentaCorrienteEmpresa ctacte = (CuentaCorrienteEmpresa) ctacteList.get(i);
					 									 	
							ResultRow row = new ResultRow(ctacte, ctacte.getId(), i);
							rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
					 		
							//headerNames.add("Cuit");
					 		StringBuilder sbCuit = new StringBuilder();
					 		sbCuit.append(ctacte.getCuit());
					 		row.addText(sbCuit.toString());

					 		StringBuilder sb_ = new StringBuilder();
					 		sb_.append(ctacte.getCuentaNombre());
					 		row.addText(sb_.toString());
				 							 		
					 		//headerNames.add("Periodo");
					 		StringBuilder sbPeriodo = new StringBuilder();
					 		sbPeriodo.append(ctacte.getPeriodo());
					 		row.addText(sbPeriodo.toString());
					 							 							 									
					 		//headerNames.add("Saldo");
					 		StringBuilder sb3 = new StringBuilder();
					 		sb3.append(ctacte.getMonto());
					 		row.addText(sb3.toString());							
					 							 							 			
							StringBuilder sb= new StringBuilder();
			 				sb.append("&nbsp;&nbsp;<img alt=\"Detalle Cuenta Corriente\" src=\"");
			 			    sb.append(themeDisplay.getPathThemeImages());
			 	 		    sb.append("/common/edit.png\" onClick=\"javascript:buscarDetalleRecibo(");
			 	 		  	sb.append(");\"");
			 	            sb.append(" title=\"Ver\"");
			 	 		    sb.append("/>");
			 	 		    row.addText(sb.toString()); 
			 												
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	
	


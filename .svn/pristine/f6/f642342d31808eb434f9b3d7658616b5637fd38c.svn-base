<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<% 
String total_monto = (String)session.getAttribute("CTACTE_RESULT_TOT");  
String total_monto_ddjj = (String)session.getAttribute("CTACTE_RESULT_TOT_DDJJ");
String total_monto_boletas = (String)session.getAttribute("CTACTE_RESULT_TOT_BOLETAS");
String total_monto_actas = (String)session.getAttribute("CTACTE_RESULT_TOT_ACTAS");
%>
					
<portlet:defineObjects/>
				
		<table class="lfr-table">
			<tr>	
				<th colspan="6" align="center" >
					<label>Saldo Inicial...</label>
				</th>
				<th colspan="6" align="center" >
					<label>Total Declarado</label>
				</th>
				<th colspan="6" align="center" >
					<label>Ingreso por Boletas</label>
				</th>
				<th colspan="6" align="center" >
					<label>Tot.Actas Deudoras</label>
				</th>
				<th colspan="6" align="center" >
					<label>Tot.Actas Canceladas</label>
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
					<label>0.00</label>
				</td>
				<td colspan="6" align="center">
					<label id=hdDDJJ>0.00</label>
				</td>
				<td colspan="6" align="center">
					<label id=hdBoletas>0.00</label>
				</td>
				<td colspan="6" align="center">
					<label>0.00</label>
				</td>
				<td colspan="6" align="center">
					<label id=hdActas>0.00</label>
				</td>
				<td colspan="6" align="center">
				    <label id=hdSaldo>0.00</label>
				</td>
				<td colspan="6" align="center">
					<label>0.00</label>
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
					headerNames.add("Saldo Inicial");
			 		headerNames.add("Total Declarado");
			 		headerNames.add("Ingresos por Boletas");
			 		headerNames.add("Total por Actas");
			 		headerNames.add("Saldo");
			 		headerNames.add("Otros Ingresos");
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
					 						 		
					 		//headerNames.add("saldo inicial");
					 		StringBuilder sb_si = new StringBuilder();
					 		sb_si.append("0.00");
					 		row.addText(sb_si.toString());
					 		
					 		//headerNames.add("Total Declarado");
					 		StringBuilder sbTotDec = new StringBuilder();
					 		sbTotDec.append(ctacte.getMontoDDJJ_BD());
					 		row.addText(sbTotDec.toString());
					 		
					 		//headerNames.add("Ingresos por Boletas");
					 		StringBuilder sbBol = new StringBuilder();
					 		sbBol.append(ctacte.getMontoBoletas_BD());
					 		row.addText(sbBol.toString());							
					 		
					 		//headerNames.add("Total por Actas");
					 		StringBuilder sb2 = new StringBuilder();
					 		sb2.append(ctacte.getMontoActas_BD());
					 		row.addText(sb2.toString());			
							
					 		//headerNames.add("Saldo");
					 		StringBuilder sb3 = new StringBuilder();
					 		sb3.append(ctacte.getMonto_BD());
					 		row.addText(sb3.toString());							
					 		
					 		//headerNames.add("Otros Ingresos");
							StringBuilder sb4 = new StringBuilder();
					 		sb4.append("0.00");
					 		row.addText(sb4.toString());					 		
					 							 			
							StringBuilder sb= new StringBuilder();
			 				sb.append("&nbsp;&nbsp;<img alt=\"Detalle Cuenta Corriente\" src=\"");
			 			    sb.append(themeDisplay.getPathThemeImages());
			 	 		    

			 			    sb.append("/common/view.png\" onClick=\"javascript:buscar_vista_1();\"");
			 	 		  	

			 	            sb.append(" title=\"Ver\"");
			 	 		    sb.append("/>");
			 	 		    row.addText(sb.toString()); 
			 												
				 			resultRows.add(row);
					 	}
				 	}
			%>
			<script type="text/javascript">					
			var aux = parseFloat(<%=total_monto%>);
			//jQuery("#hdSaldo").text(parseFloat(aux));
			jQuery("#hdSaldo").text(aux.toLocaleString());
			aux = parseFloat(<%=total_monto_ddjj%>);
			//jQuery("#hdDDJJ").text(parseFloat(aux));
			jQuery("#hdDDJJ").text(aux.toLocaleString());
			aux = parseFloat(<%=total_monto_boletas%>);
			//jQuery("#hdBoletas").text(parseFloat(aux));
			jQuery("#hdBoletas").text(aux.toLocaleString());
			aux = parseFloat(<%=total_monto_actas%>);
			//jQuery("#hdActas").text(parseFloat(aux));
			jQuery("#hdActas").text(aux.toLocaleString());
			</script>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	<liferay-util:include page="/html/portlet/uoma/cuentacorriente/paginador_cuentacorriente_v0.jsp">
    </liferay-util:include>
	


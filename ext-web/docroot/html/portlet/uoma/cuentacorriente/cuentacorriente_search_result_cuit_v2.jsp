<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<% 
  // String total_monto = (String)session.getAttribute("CTACTE_RESULT_TOT");  
  String total_monto_ddjj = (String)session.getAttribute("CTACTE_RESULT_TOT_DDJJ");
  String total_monto_boletas = (String)session.getAttribute("CTACTE_RESULT_TOT_BOLETAS");
  String total_monto_actas = (String)session.getAttribute("CTACTE_RESULT_TOT_ACTAS");
  String total_saldo_ini = (String)session.getAttribute("CTACTE_RESULT_TOT_SALDO_INI");

  String query_cuit = (String) session.getAttribute("CTACTE_RESULT_TOT_CUIT");
  String query_tipocta = (String) session.getAttribute("CTACTE_RESULT_TOT_TIPOCTA");
  String razsoc = (String) session.getAttribute("CTACTE_RESULT_TOT_RAZSOC");
  Double _aux_saldo = 0.00;
  
%>
					
<portlet:defineObjects/>
				
		<table class="lfr-table">
			<tr>
				<th colspan="6" align="center" >
					<label>CUIT</label>
				</th>
				<th colspan="6" align="center" >
					<label>Razon Social</label>
				</th>
				<th colspan="6" align="center" >
					<label>Tipo Cta</label>
				</th>
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
				<td colspan="6" align="center">
					<label id=hdCuit></label>
				</td>
				<td colspan="6" align="center">
					<label id=hdRazSoc></label>
				</td>
				<td colspan="6" align="center">
					<label id=hdTipoCta></label>
				</td>
				<td colspan="6" style="text-align:right" >
					<label id=hdSaldoIni></label>
				</td>
				<td colspan="6" style="text-align:right">
					<label id=hdDDJJ>0.00</label>
				</td>
				<td colspan="6" style="text-align:right">
					<label id=hdBoletas>0.00</label>
				</td>
				<td colspan="6" style="text-align:right">
					<label>0.00</label>
				</td>
				<td colspan="6" style="text-align:right">
				    <label id=hdActas>0.00</label>
				</td>
				<td colspan="6" style="text-align:right">
				    <label id=hdSaldo></label>
				</td>
				<td colspan="6" style="text-align:right">
					<label>0.00</label>
				</td>
			</tr>
			<%
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_EMPLEADORES);				
					List<CuentaCorrienteEmpresa> ctacteList= (ArrayList<CuentaCorrienteEmpresa>)session.getAttribute("CTACTE_RESULT");				
					
					double _debe = 0.00;
					double _haber = 0.00;
					String _max = "";
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		
					List resultRows;
					SearchContainer searchContainer;
					
					List<String> headerNames = new ArrayList<String>();
					
					headerNames.add("Periodo");
			 		headerNames.add("N° Boleta");
					headerNames.add("N° DJ");
					headerNames.add("Ent.Cobranza");
					headerNames.add("Fec.Cobranza");
					headerNames.add("DDJJ"); // Debe
					headerNames.add("Boletas");
					//headerNames.add("Pagos");
					headerNames.add("Actas");
					
					headerNames.add("Deuda"); // Debe
			 		headerNames.add("Pagos"); // Haber
			 		headerNames.add("Saldo");
					
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
					 		
							StringBuilder sb_per = new StringBuilder();
					 		sb_per.append(ctacte.getPeriodo_yyyymm());
					 		row.addText(sb_per.toString());
					 		
					 		StringBuilder sbIdBoleta = new StringBuilder();
					 		sbIdBoleta.append(ctacte.getNumeroSecuencia());
					 		row.addText(sbIdBoleta.toString());					 		

					 		_max = "";
					 		if ((ctacte.getDDJJ_Es_Max().equals("true")) && ctacte.getDDJJ_Seq() > 0)
					 			_max = " (Rectificativa)";
					 		
					 		StringBuilder sbIdDJ = new StringBuilder();
					 		sbIdDJ.append(ctacte.getDDJJ_Seq() + _max);
					 		row.addText(sbIdDJ.toString());					 		

					 		StringBuilder sbEntCob = new StringBuilder();
					 		sbEntCob.append(ctacte.getEntcob());
					 		row.addText(sbEntCob.toString());
					 		
					 		StringBuilder sbFecCob = new StringBuilder();
					 		if (ctacte.getFechaRecauda() != null) {
					 			sbFecCob.append(ctacte.getFechaRecauda());
					 		} else {
					 			sbFecCob.append("");
					 		}
					 		
					 		row.addText(sbFecCob.toString());

					 		//StringBuilder sbDDJJ = new StringBuilder();
					 		//sbDDJJ.append(ctacte.getMontoDDJJ_BD());
					 		//row.addText(sbDDJJ.toString());			
					 		row.addText(String.format("%,.2f", ctacte.getMontoDDJJ_BD()));
					 		
					 		//StringBuilder sbBol = new StringBuilder();
					 		//sbBol.append(ctacte.getMontoBoletas_BD());
					 		//row.addText(sbBol.toString());					 			
					 		row.addText(String.format("%,.2f", ctacte.getMontoBoletas_BD()));

					 		//StringBuilder sbPag = new StringBuilder();
					 		//sbPag.append(ctacte.getMontoPagos_BD());
					 		//row.addText(sbPag.toString());
					 		//row.addText(String.format("%,.2f", ctacte.getMontoPagos_BD()));

					 		//StringBuilder sbAct = new StringBuilder();
					 		//sbAct.append(ctacte.getMontoActas_BD());
					 		//row.addText(sbAct.toString());					 			
					 		row.addText(String.format("%,.2f", ctacte.getMontoActas_BD()));

					 		_debe = ctacte.getDebe();
					 		_haber = ctacte.getHaber();

					 		//StringBuilder sbDebe = new StringBuilder();
					 		//sbDebe.append(_debe);
					 		//row.addText(sbDebe.toString());
					 		row.addText(String.format("%,.2f", _debe));

					 		//StringBuilder sbHaber = new StringBuilder();
					 		//sbHaber.append(_haber);
					 		//row.addText(sbHaber.toString());
					 		row.addText(String.format("%,.2f", _haber));

					 		//StringBuilder sbSaldo = new StringBuilder();
					 		//sbSaldo.append(ctacte.getSaldo());
					 		//row.addText(sbSaldo.toString());
					 		row.addText(String.format("%,.2f", ctacte.getSaldo()));

					 		_aux_saldo = ctacte.getSaldo();
					 		//total_monto = String.format("%,.2f", ctacte.getSaldo());
					 							 		
					 		/*
							StringBuilder sb= new StringBuilder();
			 				sb.append("&nbsp;&nbsp;<img alt=\"Detalle Cuenta Corriente\" src=\"");
			 			    sb.append(themeDisplay.getPathThemeImages());
			 	 		    
			 			    sb.append("/common/view.png\" onClick=\"javascript:buscar_vista_3(");
			 	 		    sb.append(ctacte.getCuit());
			 	 		    sb.append(", ");
			 	 		  	sb.append(ctacte.getTipoBoleta());
			 	 		  	sb.append(",'");
			 	 		  	sb.append(ctacte.getPeriodo());
			 	 		  	sb.append("');\"");
			 	 		    
			 	            sb.append(" title=\"Ver\"");
			 	 		    sb.append("/>");
			 	 		    row.addText(sb.toString());
			 	 		    */
			 												
				 			resultRows.add(row);
					 	}
				 	}
			%>
			<script type="text/javascript">
			var aux = 0;
			
			aux = parseFloat(<%=_aux_saldo%>);
			jQuery("#hdSaldo").text(aux.toLocaleString());
			
			aux = parseFloat(<%=total_monto_ddjj%>);
			jQuery("#hdDDJJ").text(aux.toLocaleString());
			aux = parseFloat(<%=total_monto_boletas%>);
			jQuery("#hdBoletas").text(aux.toLocaleString());
			aux = parseFloat(<%=total_monto_actas%>);
			jQuery("#hdActas").text(aux.toLocaleString());
			aux = parseFloat(<%=total_saldo_ini%>);
			jQuery("#hdSaldoIni").text(aux.toLocaleString());
			
			jQuery("#hdRazSoc").text('<%=(razsoc.length() > 20) ? razsoc.substring(0, 20) : razsoc.toString()%>');
			jQuery("#hdTipoCta").text('<%=query_tipocta%>');
			jQuery("#hdCuit").text('<%=query_cuit%>');
			
			jQuery("#<portlet:namespace />buscar").show();
			jQuery("#<portlet:namespace />anterior").show();			
			jQuery("#<portlet:namespace />exportar_v0").hide();
			jQuery("#<portlet:namespace />exportar_v1").hide();
			jQuery("#<portlet:namespace />exportar_v2").show();
			
			jQuery("#<portlet:namespace />exportar_actas_uoma").hide();	
			jQuery("#<portlet:namespace />exportar_actas_amtima").hide();	

			jQuery("#<portlet:namespace/>nav").val("3");
			
			</script>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	<liferay-util:include page="/html/portlet/uoma/cuentacorriente/paginador_cuentacorriente_v2.jsp">
    </liferay-util:include>
	
	
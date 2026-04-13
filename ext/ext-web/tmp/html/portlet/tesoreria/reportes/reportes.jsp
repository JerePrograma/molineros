<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

	<%
					
		boolean declaracionJurada= PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_REPORTE_TESORERIA_CONTADURIA_DECLARACION_JURADA);
		boolean contaduria = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_REPORTE_TESORERIA_CONTADURIA);
		boolean liquidaciones = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_REPORTE_TESORERIA_LIQUIDACIONES);
		boolean empleadores=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_EMPLEADORES);
		boolean reporte_egresos=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTES_EGRESOS);
		boolean reporteChequesPendienteCobro=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_CHEQUES_PENDIENTE_COBRO);
		
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("reportes");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-reportes-were-found"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();

		/* if (declaracionJurada && !reporteChequesPendienteCobro){ */
		if (declaracionJurada){	
			ResultRow row = new ResultRow(1, 1, 1);
			PortletURL rowURL = renderResponse.createRenderURL();		 				
			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL.setParameter("struts_action","/tesoreria/busqueda_reporte_aportes_contribuciones_empresa");
			row.addText("Reporte de aportes, contribuciones y saldo de actas y convenios", rowURL);
			resultRows.add(row);
				
			
			ResultRow row21 = new ResultRow(1, 1, 21);
			PortletURL rowURL21 = renderResponse.createRenderURL();		 				
			rowURL21.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL21.setParameter("struts_action","/tesoreria/reporte_desempleo_ss");
			row21.addText("Reporte Desempleo SS", rowURL21);
			resultRows.add(row21);
			
			/* Obsoleto. 
			ResultRow row2 = new ResultRow(1, 1, 2);
			PortletURL rowURL2 = renderResponse.createRenderURL();		 				
			rowURL2.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL2.setParameter("struts_action","/tesoreria/busqueda_reporte_deuda_empresa_por_periodo");
			row2.addText("Reporte de Deuda de empresa por periodo", rowURL2);
			resultRows.add(row2); */
		}
					
		
		if (liquidaciones){
			ResultRow row5 = new ResultRow(1, 1, 7);
			PortletURL rowURL5 = renderResponse.createRenderURL();		 				
			rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL5.setParameter("struts_action","/tesoreria/busqueda_reporte_cuentas_corrientes");
			row5.addText("Listado Cuentas Corrientes Acreedores", rowURL5);
			resultRows.add(row5);
		}
			
		if (contaduria || (declaracionJurada && !reporteChequesPendienteCobro)){
			ResultRow row = new ResultRow(1, 1, 1);
			PortletURL rowURL = renderResponse.createRenderURL();
			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
			rowURL.setParameter("struts_action","/tesoreria/busqueda_reporte_ordenes_pago_completo");
			row.addText("Reporte de Ordenes de Pago", rowURL);
 			resultRows.add(row);
		
			ResultRow row6 = new ResultRow(1, 1, 8);
			PortletURL rowURL6 = renderResponse.createRenderURL();		 				
			rowURL6.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL6.setParameter("struts_action","/tesoreria/busqueda_reporte_cuentas_corrientes_actas_conv");
			row6.addText("Listado Cuentas Corrientes  de actas, convenios, aportes y contribuciones", rowURL6);
				resultRows.add(row6);
		}
		
				
		if (liquidaciones){
			ResultRow row7 = new ResultRow(1, 1, 10);
			PortletURL rowURL7 = renderResponse.createRenderURL();		 				
			rowURL7.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL7.setParameter("struts_action","/tesoreria/busqueda_reporte_estado_comprobantes");
			row7.addText("Listado Estado de comprobantes", rowURL7);
			resultRows.add(row7);
		}	
									
		
		if (contaduria){
		
			ResultRow row3 = new ResultRow(1, 1, 3);
			PortletURL rowURL3 = renderResponse.createRenderURL();		 				
			rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL3.setParameter("struts_action","/tesoreria/busqueda_reporte_libro_banco");
			row3.addText("Libro Banco", rowURL3);
				resultRows.add(row3);
				
			ResultRow row4 = new ResultRow(1, 1, 4);
			PortletURL rowURL4 = renderResponse.createRenderURL();		 				
			rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL4.setParameter("struts_action","/tesoreria/busqueda_reporte_libro_caja");
			row4.addText("Libro Caja", rowURL4);
			resultRows.add(row4);
				
			ResultRow row5 = new ResultRow(1, 1, 5);
			PortletURL rowURL5 = renderResponse.createRenderURL();		 				
			rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL5.setParameter("struts_action","/tesoreria/busqueda_reporte_listado_valores");
			row5.addText("Listado de valores", rowURL5);
			resultRows.add(row5);
					
			ResultRow rowDeuda = new ResultRow(1, 1, 6);
			PortletURL rowDeudaUrl = renderResponse.createRenderURL();		 				
			rowDeudaUrl.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowDeudaUrl.setParameter("struts_action","/tesoreria/busqueda_listado_de_deudas");
			rowDeuda.addText("Listado de Deudas", rowDeudaUrl);
			resultRows.add(rowDeuda);
		
			ResultRow row11 = new ResultRow(1, 1, 9);
			PortletURL rowURL11 = renderResponse.createRenderURL();		 				
			rowURL11.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL11.setParameter("struts_action","/tesoreria/busqueda_reporte_recibos");
			row11.addText("Listado Recibos", rowURL11);
			resultRows.add(row11);
		
			ResultRow row811 = new ResultRow(1, 1, 11);
			PortletURL rowURL811 = renderResponse.createRenderURL();		 				
			rowURL811.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL811.setParameter("struts_action","/tesoreria/busqueda_reporte_acreditaciones_afip");
			row811.addText("Cuadro de acreditaciones AFIP", rowURL811);
			resultRows.add(row811);		
			
		
			ResultRow row8 = new ResultRow(1, 1, 13);
			PortletURL rowURL8 = renderResponse.createRenderURL();		 				
			rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL8.setParameter("struts_action","/tesoreria/busqueda_reporte_subdiario_ingresos");
			row8.addText("Subdiario de Ingresos", rowURL8);
			resultRows.add(row8);
			
			ResultRow row9 = new ResultRow(1, 1, 14);
			PortletURL rowURL9 = renderResponse.createRenderURL();		 				
			rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL9.setParameter("struts_action","/tesoreria/busqueda_reporte_anticipos");
			row9.addText("Reporte Anticipos Actas/Convenios", rowURL9);
			resultRows.add(row9);
			
			ResultRow row16 = new ResultRow(1, 1, 16);
			PortletURL rowURL16 = renderResponse.createRenderURL();		 				
			rowURL16.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL16.setParameter("struts_action","/tesoreria/busqueda_reporte_anticipos_op");
			row16.addText("Reporte Anticipos OP", rowURL16);
			resultRows.add(row16);
			
			ResultRow row17 = new ResultRow(1, 1, 17);
			PortletURL rowURL17 = renderResponse.createRenderURL();		 				
			rowURL17.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL17.setParameter("struts_action","/tesoreria/busqueda_reporte_actas");
			row17.addText("Reporte Actas", rowURL17);
			resultRows.add(row17);
			
			ResultRow row18 = new ResultRow(1, 1, 18);
			PortletURL rowURL18 = renderResponse.createRenderURL();		 				
			rowURL18.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL18.setParameter("struts_action","/tesoreria/busqueda_reporte_convenios");
			row18.addText("Reporte Convenios", rowURL18);
			resultRows.add(row18);
			
			ResultRow row19 = new ResultRow(1, 1, 19);
			PortletURL rowURL19 = renderResponse.createRenderURL();		 				
			rowURL19.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL19.setParameter("struts_action","/tesoreria/busqueda_reporte_aplicacion_cobranzas");
			row19.addText("Reporte Aplicacion Cobranzas", rowURL19);
			resultRows.add(row19);
			
			ResultRow row21 = new ResultRow(1, 1, 21);
			PortletURL rowURL21 = renderResponse.createRenderURL();		 				
			rowURL21.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL21.setParameter("struts_action","/tesoreria/reporte_desempleo_ss");
			row21.addText("Reporte Desempleo SS", rowURL21);
			resultRows.add(row21);
			
			ResultRow row23 = new ResultRow(1, 1, 23);
			PortletURL rowURL23 = renderResponse.createRenderURL();		 				
			rowURL23.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL23.setParameter("struts_action","/tesoreria/reporte_retenciones_ganancias");
			row23.addText("Reporte de Retenciones de Ganancias", rowURL23);
			resultRows.add(row23);
			
		}
		
		if(contaduria || reporte_egresos){
			ResultRow row10 = new ResultRow(1, 1, 15);
			PortletURL rowURL10 = renderResponse.createRenderURL();		 				
			rowURL10.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL10.setParameter("struts_action","/tesoreria/busqueda_reporte_comprobantes_por_conceptos");
			row10.addText("Listado de Egresos por Conceptos", rowURL10);
			resultRows.add(row10);
			
			ResultRow row81 = new ResultRow(1, 1, 12);
			PortletURL rowURL81 = renderResponse.createRenderURL();		 				
			rowURL81.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL81.setParameter("struts_action","/tesoreria/busqueda_reporte_subdiario_egresos");
			row81.addText("Subdiario de Egresos", rowURL81);
			resultRows.add(row81);
			
			/* Se saca el reporte de Raúl
			ResultRow row20 = new ResultRow(1, 1, 20);
			PortletURL rowURL20 = renderResponse.createRenderURL();		 				
			rowURL20.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL20.setParameter("struts_action","/tesoreria/busqueda_reporte_egresos_liquidacion");
			row20.addText("Reporte de egresos OSPIM", rowURL20);
			resultRows.add(row20); */
		}	
		
		if (empleadores){ 
			ResultRow row22 = new ResultRow(1, 1, 22);
			PortletURL rowURL22 = renderResponse.createRenderURL();		 				
			rowURL22.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL22.setParameter("struts_action","/tesoreria/boletas_portal_empleadores");
			row22.addText("Reporte Boletas Portal Empleadores", rowURL22);
			resultRows.add(row22);
		}
		
		if(!reporteChequesPendienteCobro || contaduria){
			ResultRow row23 = new ResultRow(1, 1, 23);
			PortletURL rowURL23 = renderResponse.createRenderURL();		 				
			rowURL23.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL23.setParameter("struts_action","/tesoreria/reporte_hospitales_autogestion");
			row23.addText("Reporte Hospitales Autogestión", rowURL23);
			resultRows.add(row23);
				
			ResultRow row24 = new ResultRow(1, 1, 24);
			PortletURL rowURL24 = renderResponse.createRenderURL();		 				
			rowURL24.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL24.setParameter("struts_action","/tesoreria/ranking_deuda_empresa_periodo");
			row24.addText("Ranking de Deuda Empresa Período", rowURL24);
			resultRows.add(row24);
			
			ResultRow row25 = new ResultRow(1, 1, 25);
			PortletURL rowURL25 = renderResponse.createRenderURL();		 				
			rowURL25.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL25.setParameter("struts_action","/tesoreria/aportes_pago_ramo");
			row25.addText("Aportes pagados por ramo", rowURL25);
			resultRows.add(row25);
			
			ResultRow row26 = new ResultRow(1, 1, 26);
			PortletURL rowURL26 = renderResponse.createRenderURL();		 				
			rowURL26.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL26.setParameter("struts_action","/tesoreria/reporte_empresas_nuevos_afiliados");
			row26.addText("Empresas Nuevos Afiliados por Período", rowURL26);
			resultRows.add(row26);
			
			ResultRow row27 = new ResultRow(1, 1, 27);
			PortletURL rowURL27 = renderResponse.createRenderURL();		 				
			rowURL27.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL27.setParameter("struts_action","/tesoreria/reporte_ingresos_devengados_periodo");
			row27.addText("Ingresos Devengados por Período", rowURL27);
			resultRows.add(row27);
		}
		
		if(reporteChequesPendienteCobro){
			ResultRow row28 = new ResultRow(1, 1, 28);
			PortletURL rowURL28 = renderResponse.createRenderURL();		 				
			rowURL28.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL28.setParameter("struts_action","/tesoreria/reporte_cheques_pendientes_cobro");
			row28.addText("Cheques Pendientes de Cobro", rowURL28);
			resultRows.add(row28);
		}
		
		 if (contaduria){
			ResultRow row29 = new ResultRow(1, 1, 29);
			PortletURL rowURL29 = renderResponse.createRenderURL();		 				
			rowURL29.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL29.setParameter("struts_action","/tesoreria/reporte_eoaf");
			row29.addText("Listado EOAF", rowURL29);
				resultRows.add(row29);
		 		
		 		
			ResultRow row30 = new ResultRow(1, 1, 30);
			PortletURL rowURL30 = renderResponse.createRenderURL();		 				
			rowURL30.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL30.setParameter("struts_action","/tesoreria/reporte_esfc");
			row30.addText("Listado ESFC", rowURL30);
			resultRows.add(row30);
			
		} 
		
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

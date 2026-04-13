<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%		
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
					boolean showReportesBancos = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_REPORTES_OPS);				
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
		 						 			
			 		ResultRow row4 = new ResultRow(1, 1, 1);
					PortletURL rowURL4 = renderResponse.createRenderURL();
					rowURL4.setWindowState(WindowState.MAXIMIZED);
					rowURL4.setParameter("struts_action","/farmacia/busqueda_reporte_ordenes_pago_completo");
					row4.addText("Reporte de Ordenes de Pago", rowURL4);
			 		resultRows.add(row4);
			 		
			 		ResultRow row8 = new ResultRow(1, 1, 2);
					PortletURL rowURL8 = renderResponse.createRenderURL();		 				
					rowURL8.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL8.setParameter("struts_action","/farmacia/busqueda_reporte_subdiario_egresos");
					row8.addText("Subdiario de Egresos", rowURL8);
					resultRows.add(row8);
					
					ResultRow row81 = new ResultRow(1, 1, 3);
					PortletURL rowURL81 = renderResponse.createRenderURL();		 				
					rowURL81.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL81.setParameter("struts_action","/farmacia/busqueda_reporte_subdiario_ingresos");
					row81.addText("Subdiario de Ingresos", rowURL81);
					resultRows.add(row81);
					
					ResultRow row5 = new ResultRow(1, 1, 4);
					PortletURL rowURL5 = renderResponse.createRenderURL();		 				
					rowURL5.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL5.setParameter("struts_action","/farmacia/busqueda_reporte_cuentas_corrientes");
					row5.addText("Listado Cuentas Corrientes Acreedores", rowURL5);
					resultRows.add(row5);
					
					ResultRow row10 = new ResultRow(1, 1, 5);
					PortletURL rowURL10 = renderResponse.createRenderURL();		 				
					rowURL10.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL10.setParameter("struts_action","/farmacia/busqueda_reporte_comprobantes_por_conceptos");					
					row10.addText("Listado de Egresos por Conceptos", rowURL10);
					resultRows.add(row10);
					
					ResultRow row3 = new ResultRow(1, 1, 6);
					PortletURL rowURL3 = renderResponse.createRenderURL();		 				
					rowURL3.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL3.setParameter("struts_action","/farmacia/busqueda_reporte_libro_banco");
					row3.addText("Libro Banco", rowURL3);
					resultRows.add(row3);
					
					ResultRow row41 = new ResultRow(1, 1, 7);
					PortletURL rowURL41 = renderResponse.createRenderURL();		 				
					rowURL41.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL41.setParameter("struts_action","/farmacia/busqueda_reporte_libro_caja");
					row41.addText("Libro Caja", rowURL41);
					resultRows.add(row41);
					
					ResultRow rowDeuda = new ResultRow(1, 1, 8);
					PortletURL rowDeudaUrl = renderResponse.createRenderURL();		 				
					rowDeudaUrl.setWindowState(WindowState.MAXIMIZED);		 				
					rowDeudaUrl.setParameter("struts_action","/farmacia/busqueda_listado_de_deudas");
					rowDeuda.addText("Listado de Deudas", rowDeudaUrl);
					resultRows.add(rowDeuda);
					
					ResultRow row51 = new ResultRow(1, 1, 9);
					PortletURL rowURL51 = renderResponse.createRenderURL();		 				
					rowURL51.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL51.setParameter("struts_action","/farmacia/busqueda_reporte_listado_valores");
					row51.addText("Listado de valores", rowURL51);
					resultRows.add(row51);					
					
					
					ResultRow row11 = new ResultRow(1, 1, 10);
					PortletURL rowURL11 = renderResponse.createRenderURL();		 				
					rowURL11.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL11.setParameter("struts_action","/farmacia/busqueda_reporte_recibos");
					row11.addText("Listado Recibos", rowURL11);
					resultRows.add(row11);
					
					ResultRow row6 = new ResultRow(1, 1, 11);
					PortletURL rowURL6 = renderResponse.createRenderURL();		 				
					rowURL6.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL6.setParameter("struts_action","/farmacia/busqueda_reporte_cuentas_corrientes_actas_conv");
					row6.addText("Listado Cuentas Corrientes  de actas y convenios", rowURL6);
					resultRows.add(row6);
					
					ResultRow row12 = new ResultRow(1, 1, 12);
					PortletURL rowURL12 = renderResponse.createRenderURL();		 				
					rowURL12.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL12.setParameter("struts_action","/farmacia/reporte_retenciones_ganancias");
					row12.addText("Reporte de Retenciones de Ganancias", rowURL12);
					resultRows.add(row12);
					
					ResultRow row62 = new ResultRow(1, 1, 12);
					PortletURL rowURL62 = renderResponse.createRenderURL();		 				
					rowURL62.setWindowState(WindowState.MAXIMIZED);		 				
					rowURL62.setParameter("struts_action","/farmacia/busqueda_reporte_anticipos_op");
					row62.addText("Reporte Anticipos OP", rowURL62);
					resultRows.add(row62);
			 			
			 	%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

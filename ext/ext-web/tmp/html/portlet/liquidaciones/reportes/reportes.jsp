<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%
				
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
					boolean showReportesBancos = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_REPORTES_OPS);				
					boolean showReportesPrestaciones = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.REPORTES_PRESTACIONES);
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
		 			if (showReportesBancos){
			 			
			 			ResultRow row = new ResultRow(1, 1, 1);
						PortletURL rowURL = renderResponse.createRenderURL();
						rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL.setParameter("struts_action","/liquidaciones/busqueda_reporte_ordenes_pago_completo");
						row.addText("Reporte de Ordenes de Pago", rowURL);
			 			resultRows.add(row);
			 			
			 			ResultRow row2 = new ResultRow(1, 1, 2);
						PortletURL rowURL2 = renderResponse.createRenderURL();
						rowURL2.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL2.setParameter("struts_action","/liquidaciones/reportes_reporte_reintegros");
						row2.addText("Reporte de Reintegros", rowURL2);
			 			resultRows.add(row2);
			 			
			 			/* Se saca el reporte de Raúl 
			 			ResultRow row3 = new ResultRow(1, 1, 3);
						PortletURL rowURL3 = renderResponse.createRenderURL();
						rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL3.setParameter("struts_action","/liquidaciones/busqueda_reporte_egresos_liquidacion");
						row3.addText("Reporte de egresos OSPIM", rowURL3);
						resultRows.add(row3); */
			 			
			 			ResultRow row4 = new ResultRow(1, 1, 4);
						PortletURL rowURL4 = renderResponse.createRenderURL();
						rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL4.setParameter("struts_action","/liquidaciones/reporte_listados_tercerizadoras");
						row4.addText("Listado de Padrones para Tercerizadoras", rowURL4);
			 			resultRows.add(row4);			 			
			 			
		 			}
		 			if(showReportesPrestaciones){
		 				ResultRow row5 = new ResultRow(1, 1, 5);
						PortletURL rowURL5 = renderResponse.createRenderURL();
						rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL5.setParameter("struts_action","/liquidaciones/ficha_de_consumo");
						row5.addText("Ficha de Consumo", rowURL5);
			 			resultRows.add(row5);
			 			
			 			ResultRow row6 = new ResultRow(1, 1, 6);
						PortletURL rowURL6 = renderResponse.createRenderURL();
						rowURL6.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL6.setParameter("struts_action","/liquidaciones/reporte_discapacidad");
						row6.addText("Reporte de Discapacidad", rowURL6);
			 			resultRows.add(row6);
			 			
			 			ResultRow row7 = new ResultRow(1,1,7);
			 			PortletURL rowURL7 = renderResponse.createRenderURL();
			 			rowURL7.setWindowState(LiferayWindowState.MAXIMIZED);
			 			rowURL7.setParameter("struts_action","/liquidaciones/reporte_situaciones_medicas");
			 			row7.addText("Reporte Situaciones Médicas", rowURL7);
			 			resultRows.add(row7);
			 			
 			 			ResultRow row8 = new ResultRow(1,1,8);
			 			PortletURL rowURL8 = renderResponse.createRenderURL();
			 			rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);
			 			rowURL8.setParameter("struts_action","/liquidaciones/reporte_tratamiento_discapacidad");
			 			row8.addText("Reporte Tratamiento de Discapacidad", rowURL8);
			 			resultRows.add(row8); 
			 			
			 			ResultRow row9 = new ResultRow(1, 1, 9);
						PortletURL rowURL9 = renderResponse.createRenderURL();
						rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);
						rowURL9.setParameter("struts_action","/liquidaciones/reporte_liq_farmacia");
						row9.addText("Reporte de Liquidaciones de Farmacia", rowURL9);
			 			resultRows.add(row9);
		 			}
		 			
		 			ResultRow row10 = new ResultRow(1, 1, 10);
					PortletURL rowURL10 = renderResponse.createRenderURL();		 				
					rowURL10.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL10.setParameter("struts_action","/liquidaciones/reporte_retenciones_ganancias");
					row10.addText("Reporte de Retenciones de Ganancias", rowURL10);
					resultRows.add(row10);
					
					ResultRow row11 = new ResultRow(1, 1, 11);
					PortletURL rowURL11 = renderResponse.createRenderURL();		 				
					rowURL11.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL11.setParameter("struts_action","/liquidaciones/reporte_debitos_terciarizadoras");
					row11.addText("Débitos a Tercerizadoras ", rowURL11);
					resultRows.add(row11);
					
					ResultRow row12 = new ResultRow(1, 1, 12);
					PortletURL rowURL12 = renderResponse.createRenderURL();		 				
					rowURL12.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL12.setParameter("struts_action","/liquidaciones/reporte_imagenes_liquidaciones");
					row12.addText("Obtener Imágenes Liquidaciones", rowURL12);
					resultRows.add(row12);
					
					ResultRow row13 = new ResultRow(1, 1, 13);
					PortletURL rowURL13 = renderResponse.createRenderURL();		 				
					rowURL13.setWindowState(LiferayWindowState.MAXIMIZED);		 				
					rowURL13.setParameter("struts_action","/liquidaciones/reporte_imagenes_ordenes_pago");
					row13.addText("Obtener Imágenes Ordenes de Pago", rowURL13);
					resultRows.add(row13);
		 			
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

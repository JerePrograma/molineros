<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.uoma.WebKeysUOMA" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%		
				//Si debe mostrarse el btn de agregar afiliado									
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("reportes");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
					boolean empleadores=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_EMPLEADORES);
					boolean ctacteActas=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_CTACTE_ACTAS_CONVENIOS_UOMA);
					boolean reportesGrales=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_GENERALES_UOMA);
					
					
				 	int total = 1;
					searchContainer.setTotal(total);
	 				List resultRows = searchContainer.getResultRows();
	 				if (reportesGrales){			 			
			 		   ResultRow row4 = new ResultRow(1, 1, 1);
					   PortletURL rowURL4 = renderResponse.createRenderURL();
					   rowURL4.setWindowState(WindowState.MAXIMIZED);
					   rowURL4.setParameter("struts_action","/uoma/busqueda_reporte_ordenes_pago_completo");
					   row4.addText("Reporte de Ordenes de Pago", rowURL4);
			 		   resultRows.add(row4);
			 		
			 		   ResultRow row8 = new ResultRow(1, 1, 2);
					   PortletURL rowURL8 = renderResponse.createRenderURL();		 				
					   rowURL8.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL8.setParameter("struts_action","/uoma/busqueda_reporte_subdiario_egresos");
					   row8.addText("Subdiario de Egresos", rowURL8);
					   resultRows.add(row8);
					
					   ResultRow row81 = new ResultRow(1, 1, 3);
					   PortletURL rowURL81 = renderResponse.createRenderURL();		 				
					   rowURL81.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL81.setParameter("struts_action","/uoma/busqueda_reporte_subdiario_ingresos");
					   row81.addText("Subdiario de Ingresos", rowURL81);
					   resultRows.add(row81);
					
					   ResultRow row10 = new ResultRow(1, 1, 4);
					   PortletURL rowURL10 = renderResponse.createRenderURL();		 				
					   rowURL10.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL10.setParameter("struts_action","/uoma/busqueda_reporte_comprobantes_por_conceptos");					
					   row10.addText("Listado de Egresos por Conceptos", rowURL10);
					   resultRows.add(row10);
					
					   ResultRow row5 = new ResultRow(1, 1, 5);
					   PortletURL rowURL5 = renderResponse.createRenderURL();		 				
					   rowURL5.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL5.setParameter("struts_action","/uoma/busqueda_reporte_cuentas_corrientes");
					   row5.addText("Listado Cuentas Corrientes Proveedores/Seccionales/Hoteles", rowURL5);
					   resultRows.add(row5);					
										
					   ResultRow rowDeuda = new ResultRow(1, 1, 6);
					   PortletURL rowDeudaUrl = renderResponse.createRenderURL();		 				
					   rowDeudaUrl.setWindowState(WindowState.MAXIMIZED);		 				
					   rowDeudaUrl.setParameter("struts_action","/uoma/busqueda_listado_de_deudas");
					   rowDeuda.addText("Listado de Deudas", rowDeudaUrl);					
					   resultRows.add(rowDeuda);
					
					   ResultRow row51 = new ResultRow(1, 1, 7);
					   PortletURL rowURL51 = renderResponse.createRenderURL();		 				
					   rowURL51.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL51.setParameter("struts_action","/uoma/busqueda_reporte_listado_valores");
					   row51.addText("Listado de valores", rowURL51);
					   resultRows.add(row51);
					
					   ResultRow row111 = new ResultRow(1, 1, 8);
					   PortletURL rowURL111 = renderResponse.createRenderURL();		 				
					   rowURL111.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL111.setParameter("struts_action","/uoma/busqueda_reporte_recibos");
					   row111.addText("Listado Recibos", rowURL111);
					   resultRows.add(row111);
					
					   ResultRow row3 = new ResultRow(1, 1, 9);
					   PortletURL rowURL3 = renderResponse.createRenderURL();		 				
					   rowURL3.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL3.setParameter("struts_action","/uoma/busqueda_reporte_libro_banco");
					   row3.addText("Libro Banco", rowURL3);
					   resultRows.add(row3);
				
					   ResultRow row41 = new ResultRow(1, 1, 10);
					   PortletURL rowURL41= renderResponse.createRenderURL();		 				
					   rowURL41.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL41.setParameter("struts_action","/uoma/busqueda_reporte_libro_caja");
					   row41.addText("Libro Caja", rowURL41);
					   resultRows.add(row41);
	 				}
					
					
					if (ctacteActas || reportesGrales){ 
					   ResultRow row61 = new ResultRow(1, 1, 11);
					   PortletURL rowURL61 = renderResponse.createRenderURL();		 				
					   rowURL61.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL61.setParameter("struts_action","/uoma/busqueda_reporte_cuentas_corrientes_actas_conv");
					   row61.addText("Listado Cuentas Corrientes  de actas y convenios", rowURL61);
					   resultRows.add(row61);
					}   
					
					if (reportesGrales){
					   ResultRow row62 = new ResultRow(1, 1, 12);
					   PortletURL rowURL62 = renderResponse.createRenderURL();		 				
					   rowURL62.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL62.setParameter("struts_action","/uoma/busqueda_reporte_anticipos_op");
					   row62.addText("Reporte Anticipos OP", rowURL62);
					   resultRows.add(row62);
					
					
					   ResultRow row72 = new ResultRow(1, 1, 13);
					   PortletURL rowURL72 = renderResponse.createRenderURL();		 				
					   rowURL72.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL72.setParameter("struts_action","/uoma/busqueda_reporte_estado_comprobantes");
					   row72.addText("Listado Estado de comprobantes", rowURL72);
					   resultRows.add(row72);
					
					   ResultRow row14 = new ResultRow(1, 1, 14);
					   PortletURL rowURL14 = renderResponse.createRenderURL();		 				
					   rowURL14.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL14.setParameter("struts_action","/uoma/reporte_retenciones_ganancias");
					   row14.addText("Reporte de Retenciones de Ganancias", rowURL14);
					   resultRows.add(row14);
					
					   ResultRow row15 = new ResultRow(1, 1, 15);
					   PortletURL rowAcCoUrl = renderResponse.createRenderURL();		 				
					   rowAcCoUrl.setWindowState(WindowState.MAXIMIZED);		 				
					   rowAcCoUrl.setParameter("struts_action","/uoma/busqueda_listado_de_actas_acuerdos");
					   row15.addText("Listado de Actas y Acuerdos", rowAcCoUrl);					
					   resultRows.add(row15);
					}	
					
					if (empleadores || reportesGrales){ 
						ResultRow row22 = new ResultRow(1, 1, 22);
						PortletURL rowURL22 = renderResponse.createRenderURL();		 				
						rowURL22.setWindowState(WindowState.MAXIMIZED);		 				
						rowURL22.setParameter("struts_action","/uoma/boletas_portal_empleadores");
						row22.addText("Reporte Boletas Portal Empleadores", rowURL22);
						resultRows.add(row22);
					}
					
					if (reportesGrales){
					   ResultRow row23 = new ResultRow(1, 1, 23);
					   PortletURL rowURL23 = renderResponse.createRenderURL();		 				
					   rowURL23.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL23.setParameter("struts_action","/uoma/busqueda_control_ingresos_egresos");
					   row23.addText("Tablero Control INGRESOS - EGRESOS", rowURL23);					
					   resultRows.add(row23);
					   
					   ResultRow row24 = new ResultRow(1, 1, 24);
					   PortletURL rowURL24 = renderResponse.createRenderURL();		 				
					   rowURL24.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL24.setParameter("struts_action","/uoma/busqueda_percepciones_iibb");
					   row24.addText("Percepciones Ingresos Brutos", rowURL24);					
					   resultRows.add(row24);
					   
					   ResultRow row25 = new ResultRow(1, 1, 25);
					   PortletURL rowURL25 = renderResponse.createRenderURL();		 				
					   rowURL25.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL25.setParameter("struts_action","/uoma/busqueda_libros_iva");
					   row25.addText("Libros IVA Compras-Ventas/Percepciones-Retenciones ARBA", rowURL25);					
					   resultRows.add(row25);
					}
			 	%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

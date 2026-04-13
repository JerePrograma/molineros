<%@ include file="/html/portlet/afiliados/init.jsp" %>

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
		boolean reporte_aportes_contrib= PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_APORTES_CONTRIB);
		boolean reporte_amtima= PermissionUtil.userContainsRole(user,"reportes_amtima");
		boolean abm_afiliaciones= PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
		boolean padron_afiliaciones= PermissionUtil.userContainsRole(user,"reporte_padron_afiliados");
		boolean empleadores=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_EMPLEADORES);
				
		
		
		int total = 1;
		searchContainer.setTotal(total);
	 	List resultRows = searchContainer.getResultRows();
		if(abm_afiliaciones || padron_afiliaciones) {
		 	ResultRow row = new ResultRow(1, 1, 1);
			PortletURL rowURL = renderResponse.createRenderURL();		 				
			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL.setParameter("struts_action","/afiliados/reporte_padrones");
			row.addText("Listado de Padrones", rowURL);
			resultRows.add(row);// POR EL MOMENTO INHABILITADO HASTA FIN DE ELECCIONES...
		}
		// consulta de afiliados ANSES   	
		/*
		if(abm_afiliaciones || padron_afiliaciones) {
		 	ResultRow rowanses = new ResultRow(1, 1, 1);
			PortletURL rowURLanses = renderResponse.createRenderURL();		 				
			rowURLanses.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURLanses.setParameter("struts_action","/afiliados/reporte_afiliados_anses");			
			rowanses.addText("Listado de Afiliados Jubilados ANSES ", rowURLanses);
			resultRows.add(rowanses); 			
		}
		*/
		//  
			
		if(reporte_aportes_contrib){			
			ResultRow row3 = new ResultRow(1, 1, 2);
			PortletURL rowURL3 = renderResponse.createRenderURL();		 				
			rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL3.setParameter("struts_action","/afiliados/busqueda_reporte_aportes_contribuciones_empresa");
			row3.addText("Reporte de Aportes y Contribuciones por empresa", rowURL3);
			resultRows.add(row3);
		}
		
		
		if(reporte_amtima) {
			ResultRow row4 = new ResultRow(1, 1, 3);
			PortletURL rowURL4 = renderResponse.createRenderURL();		 				
			rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL4.setParameter("struts_action","/afiliados/reporte_amtima_pmi_busqueda");
			row4.addText("Reporte de AMTIMA PMI", rowURL4);
			resultRows.add(row4);
		}
		
		if(abm_afiliaciones) {
			ResultRow row6 = new ResultRow(1, 1, 5);
			PortletURL rowURL6 = renderResponse.createRenderURL();		 				
			rowURL6.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL6.setParameter("struts_action","/afiliados/reporte_desregulados_sin_aportes");
			/* row6.addText("Listado de Desregulados Morosos", rowURL6); */
			row6.addText("Listado de Morosos", rowURL6);
			resultRows.add(row6);		 
			
			ResultRow row7 = new ResultRow(1, 1, 6);
			PortletURL rowURL7 = renderResponse.createRenderURL();		 				
			rowURL7.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL7.setParameter("struts_action","/afiliados/reporte_listados_ss");
			row7.addText("Listado de Padrones SS", rowURL7);
			resultRows.add(row7);
			
			ResultRow row8 = new ResultRow(1, 1, 7);
			PortletURL rowURL8 = renderResponse.createRenderURL();		 				
			rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL8.setParameter("struts_action","/afiliados/reporte_listados_tercerizadoras");
			row8.addText("Listado de Padrones para Tercerizadoras", rowURL8);
			resultRows.add(row8);
			
			ResultRow row9 = new ResultRow(1, 1, 8);
			PortletURL rowURL9 = renderResponse.createRenderURL();		 				
			rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL9.setParameter("struts_action","/afiliados/panel_control_afiliados");
			row9.addText("Panel de Control Afiliaciones", rowURL9);
			resultRows.add(row9);
			
			ResultRow row10 = new ResultRow(1, 1, 10);
			PortletURL rowURL10 = renderResponse.createRenderURL();		 				
			rowURL10.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL10.setParameter("struts_action","/afiliados/reporte_credenciales");
			row10.addText("Listado de Credenciales emitidas", rowURL10);
			resultRows.add(row10);
			
			ResultRow row11 = new ResultRow(1, 1, 11);
			PortletURL rowURL11 = renderResponse.createRenderURL();		 				
			rowURL11.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL11.setParameter("struts_action","/afiliados/reporte_novedades_sss_procesadas");
			row11.addText("Estadística Procesamiento de Novedades de la SSS", rowURL11);
			resultRows.add(row11);
		}
		
		if (empleadores){ 
			ResultRow row22 = new ResultRow(1, 1, 22);
			PortletURL rowURL22 = renderResponse.createRenderURL();		 				
			rowURL22.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL22.setParameter("struts_action","/afiliados/boletas_portal_empleadores");
			row22.addText("Reporte Boletas Portal Empleadores", rowURL22);
			resultRows.add(row22);
		}
		if(reporte_aportes_contrib){
			ResultRow row28 = new ResultRow(1, 1, 28);
			PortletURL rowURL28 = renderResponse.createRenderURL();		 				
			rowURL28.setWindowState(WindowState.MAXIMIZED);		 				
			rowURL28.setParameter("struts_action","/afiliados/reporte_informe_aportes_monotributo");
			row28.addText("Informe Aportes Monotributistas", rowURL28);
			resultRows.add(row28);
		}	
	%>	
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />


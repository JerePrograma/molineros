<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
	<%											
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
		List<String> headerNames = new ArrayList<String>();
		headerNames.add("reportes");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-reportes-were-found"));
		boolean preautorizacionGerencial=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_PREAUTORIZACION_GERENCIAL);
		boolean estadisticaPrestAut=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ESTADISTICA_PREST_AUTORIZADAS);
				
		int total = 1;
		searchContainer.setTotal(total);
	 	List resultRows = searchContainer.getResultRows();
		
	 	ResultRow row = new ResultRow(1, 1, 1);
		PortletURL rowURL = renderResponse.createRenderURL();		 				
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL.setParameter("struts_action","/autorizaciones/reporte_igs");
		rowURL.setParameter("ir_a_filtro","true");
		row.addText("Consultas IGS", rowURL);
		resultRows.add(row);
		
		
		if(preautorizacionGerencial){
		
		    ResultRow row1 = new ResultRow(1, 1, 1);
		    PortletURL rowURL1 = renderResponse.createRenderURL();		 				
		    rowURL1.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		    rowURL1.setParameter("struts_action","/autorizaciones/reporte_estadistico_preautorizaciones");
		    //rowURL.setParameter("ir_a_filtro","true");
		    row1.addText("Estadística Preautorizaciones por Estado", rowURL1);
		    resultRows.add(row1);

		}
		
		if(preautorizacionGerencial){
			
		    ResultRow row1 = new ResultRow(1, 1, 1);
		    PortletURL rowURL1 = renderResponse.createRenderURL();		 				
		    rowURL1.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		    rowURL1.setParameter("struts_action","/autorizaciones/estadistica_prestaciones_autorizadas");
		    row1.addText("Estadística Prestaciones Autorizadas por PS", rowURL1);
		    resultRows.add(row1);

		}
		
		ResultRow row3 = new ResultRow(1, 1, 1);
		PortletURL rowURL3 = renderResponse.createRenderURL();		 				
		rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL3.setParameter("struts_action","/autorizaciones/reporte_avisos_vencimientos_cud");
	    row3.addText("Aviso Vencimiento CUD vía Email", rowURL3);
	    resultRows.add(row3);
	    
	    ResultRow row4 = new ResultRow(1, 1, 1);
		PortletURL rowURL4 = renderResponse.createRenderURL();		 				
		rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL4.setParameter("struts_action","/autorizaciones/reporte_imagenes_reclamos");
	    row4.addText("Obtener Imágenes Reclamos Prestacionales", rowURL4);
	    resultRows.add(row4);
	    
	    ResultRow row5 = new ResultRow(1, 1, 1);
		PortletURL rowURL5 = renderResponse.createRenderURL();		 				
		rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL5.setParameter("struts_action","/autorizaciones/reporte_imagenes_preautorizaciones");
	    row5.addText("Obtener Imágenes Preautorizaciones", rowURL5);
	    resultRows.add(row5);
	%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%-- <%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
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
	%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" /> --%>

<p>&nbsp;</p>

<a target="_blank" rel="noopener noreferrer" href="http://190.210.223.92/prescripcion/login">Acceso a Formularios de Patologías de ENSALUD</a>

<br/>
<br/>

<a target="_blank" rel="noopener noreferrer" href="http://www.ospim.org.ar/PDF/FORMULARIOS_ENSALUD.pdf">Instructivo</a>




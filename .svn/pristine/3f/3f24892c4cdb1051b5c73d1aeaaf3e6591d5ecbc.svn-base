<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

%>
<portlet:defineObjects/>

	<%
					
	
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Contabilidad");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-reportes-were-found"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

			
		ResultRow row3 = new ResultRow(1, 1, 1);
		PortletURL rowURL3 = renderResponse.createRenderURL();		 				
		rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL3.setParameter("struts_action","/"+portlet_name+"/plan_cuentas");
		row3.addText("Plan cuentas", rowURL3);
		resultRows.add(row3);
		
			
		ResultRow row = new ResultRow(1, 1, 2);
		PortletURL rowURL = renderResponse.createRenderURL();		 				
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL.setParameter("struts_action","/"+portlet_name+"/equivalencias_conceptos_cuentas");
		row.addText("Conceptos - Plan Cuentas", rowURL);
		resultRows.add(row);
		
		if(null==portlet_name || portlet_name.equals("tesoreria")){
			ResultRow row2 = new ResultRow(1, 1, 3);
			PortletURL rowURL2 = renderResponse.createRenderURL();		 				
			rowURL2.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL2.setParameter("struts_action","/"+portlet_name+"/equivalencias_prestaciones_conceptos");
			row2.addText("Prestaciones - Conceptos", rowURL2);
			resultRows.add(row2);
		}
		
		
		ResultRow row4 = new ResultRow(1, 1, 4);
		PortletURL rowURL4 = renderResponse.createRenderURL();		 				
		rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL4.setParameter("struts_action","/"+portlet_name+"/parametros_especiales");
		row4.addText("Parametros Especiales", rowURL4);
		resultRows.add(row4);
		
		ResultRow row5 = new ResultRow(1, 1, 5);
		PortletURL rowURL5 = renderResponse.createRenderURL();		 				
		rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		rowURL5.setParameter("struts_action","/"+portlet_name+"/equivalencias_tipos_mov_bcrios");
		row5.addText("Tipos Movimientos Bancarios - Conceptos", rowURL5);
		resultRows.add(row5);
		
		boolean rolApertura = PermissionUtil.userContainsRole(user,"APERTURA_FECHA_CIERRE_CONTABLE");		
		if (rolApertura) {		
		    ResultRow row7 = new ResultRow(1, 1, 7);
			PortletURL rowURL7 = renderResponse.createRenderURL();		 				
			rowURL7.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL7.setParameter("struts_action","/"+portlet_name+"/fechas_cierre_contable");
			row7.addText("Fechas cierre movimientos/asientos", rowURL7);
			resultRows.add(row7);
		}	
		
		if(null==portlet_name || portlet_name.equals("tesoreria")){
			ResultRow row6 = new ResultRow(1, 1, 6);
			PortletURL rowURL6 = renderResponse.createRenderURL();		 				
			rowURL6.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL6.setParameter("struts_action","/"+portlet_name+"/equivalencias_conceptos_afip");
			row6.addText("Conceptos Afip - Conceptos", rowURL6);
			resultRows.add(row6);			
		}
			
			boolean rolAsientos = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD) || portlet_name.equals("farmacia") 
								  || portlet_name.equals("uoma");
			if (rolAsientos) {
				ResultRow row8 = new ResultRow(1, 1, 8);
				PortletURL rowURL8 = renderResponse.createRenderURL();		 				
				rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL8.setParameter("struts_action","/"+portlet_name+"/asientos");
				row8.addText("Asientos", rowURL8);
				resultRows.add(row8);
				
				ResultRow row9 = new ResultRow(1, 1, 9);
				PortletURL rowURL9 = renderResponse.createRenderURL();		 				
				rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL9.setParameter("struts_action","/"+portlet_name+"/contabilidad_diario");
				row9.addText("Diario", rowURL9);
				resultRows.add(row9);
				
				ResultRow row10 = new ResultRow(1, 1, 10);
				PortletURL rowURL10 = renderResponse.createRenderURL();		 				
				rowURL10.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL10.setParameter("struts_action","/"+portlet_name+"/contabilidad_mayor_general");
				row10.addText("Mayor General", rowURL10);
				resultRows.add(row10);
				
				ResultRow row11 = new ResultRow(1, 1, 11);
				PortletURL rowURL11 = renderResponse.createRenderURL();		 				
				rowURL11.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL11.setParameter("struts_action","/"+portlet_name+"/contabilidad_balance_sumas_saldos");
				row11.addText("Balance de sumas y saldos", rowURL11);
				resultRows.add(row11);
				
				ResultRow row12 = new ResultRow(1, 1, 12);
				PortletURL rowURL12 = renderResponse.createRenderURL();		 				
				rowURL12.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL12.setParameter("struts_action","/"+portlet_name+"/contabilidad_balance_general");
				row12.addText("Balance general", rowURL12);
				resultRows.add(row12);
				
				
				if(null!=portlet_name && portlet_name.equals("tesoreria")){
			  	   ResultRow row13 = new ResultRow(1, 1, 13);
				   PortletURL rowURL13 = renderResponse.createRenderURL();		 				
				   rowURL13.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				   rowURL13.setParameter("struts_action","/"+portlet_name+"/plan_cuentas_sss");
				   row13.addText("Plan de Cuentas SSS", rowURL13);
				   resultRows.add(row13);
				}
				
			}
			
			ResultRow row14 = new ResultRow(1, 1, 14);
			PortletURL rowURL14 = renderResponse.createRenderURL();		 				
			rowURL14.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL14.setParameter("struts_action","/"+portlet_name+"/coeficientes_ajuste_inflacion");
			row14.addText("Coeficientes para Ajuste por Inflación", rowURL14);
			resultRows.add(row14);
		
		
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}


%>


<portlet:defineObjects/>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	<%
	   
	   boolean rolLiquidacion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_LIQUIDACION);				
	
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Proceso de Integración");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-reportes-were-found"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

		
	    boolean rolGeneracion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_GENERACION);	
	    if (rolGeneracion) {	
		   ResultRow row3 = new ResultRow(1, 1, 1);
		   PortletURL rowURL3 = renderResponse.createRenderURL();		 				
		   rowURL3.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		   rowURL3.setParameter("struts_action","/"+portlet_name+"/integracion_lotes");
		   row3.addText("Lotes de Integración", rowURL3);
		   resultRows.add(row3);
		   
		   
		   ResultRow row5 = new ResultRow(1, 1, 2);
		   PortletURL rowURL5 = renderResponse.createRenderURL();		 				
		   rowURL5.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		   rowURL5.setParameter("struts_action","/"+portlet_name+"/integracion_recibos");
		   row5.addText("Ingreso de Recibos", rowURL5);
		   resultRows.add(row5);
	    }
			
	
	 
		if (rolLiquidacion) {
				ResultRow row8 = new ResultRow(1, 1, 1);
				PortletURL rowURL8 = renderResponse.createRenderURL();		 				
				rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL8.setParameter("struts_action","/"+portlet_name+"/integracion_liquidacion");
				row8.addText("Liquidación", rowURL8);
				resultRows.add(row8);
				
				ResultRow row9 = new ResultRow(1, 1, 2);
				PortletURL rowURL9 = renderResponse.createRenderURL();		 				
				rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL9.setParameter("struts_action","/"+portlet_name+"/integracion_transferencias");
				row9.addText("Procesa Transferencias Bancarias", rowURL9);
				resultRows.add(row9);
		}
		
		boolean rolInformes = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_INFORMES);
		if (rolInformes) {
			ResultRow row4 = new ResultRow(1, 1, 1);
			PortletURL rowURL4 = renderResponse.createRenderURL();		 				
			rowURL4.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			rowURL4.setParameter("struts_action","/"+portlet_name+"/integracion_planilla_superintendencia");
			row4.addText("Planilla Superintendencia", rowURL4);
			resultRows.add(row4);
		}
		
		
		
		
		
		if (rolLiquidacion) {
				ResultRow row8 = new ResultRow(1, 1, 3);
				PortletURL rowURL8 = renderResponse.createRenderURL();		 				
				rowURL8.setWindowState(LiferayWindowState.MAXIMIZED);	
				rowURL8.setParameter("struts_action","/"+portlet_name+"/integracion_liquidacion");
				rowURL8.setParameter("flagOcultar", "true");
				row8.addText("Exportar transferencias a Interbanking", rowURL8);
				resultRows.add(row8);
					
		}
		
		boolean rolRendicion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_RENDICION);
		if (rolRendicion) {
			ResultRow row9 = new ResultRow(1, 1, 4);
			PortletURL rowURL9 = renderResponse.createRenderURL();		 				
			rowURL9.setWindowState(LiferayWindowState.MAXIMIZED);	
			rowURL9.setParameter("struts_action","/"+portlet_name+"/integracion_rendicion");
			row9.addText("Rendición", rowURL9);
			resultRows.add(row9);
	    }
		
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

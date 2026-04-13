<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ page import="ar.com.uoma.WebKeysUOMA" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
				<%		
				//Si debe mostrarse el btn de agregar afiliado									
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Centro de Costos");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
					boolean showABMCentroCosto=PermissionUtil.userContainsRole(user,WebKeysUOMA.ROL_ABM_CENTRO_COSTO_UOMA);
					boolean showTABLEROCentroCosto=PermissionUtil.userContainsRole(user,WebKeysUOMA.ROL_TABLERO_CENTRO_COSTO_UOMA);
					
					String portlet_name="";
					String entidad="";

					if (renderResponse.getNamespace().equals("_FAR_1_")){
						portlet_name = "farmacia";
						entidad="AMTIMA";
					}
					if(renderResponse.getNamespace().equals("_UOM_1_")){
						portlet_name = "uoma";
						entidad="UOMA";
					}
					
					if (renderResponse.getNamespace().equals("_TES_1_")){
						portlet_name = "tesoreria";
						entidad="OSPIM";
					}
					
				 	int total = 1;
					searchContainer.setTotal(total);
	 				List resultRows = searchContainer.getResultRows();
	 				
	 				if(showABMCentroCosto){
	 					ResultRow row1 = new ResultRow(1, 1, 1);
						PortletURL rowURL1 = renderResponse.createRenderURL();
						rowURL1.setWindowState(WindowState.MAXIMIZED);
						//rowURL1.setParameter("struts_action","/uoma/abm_centro_costo");
						rowURL1.setParameter("struts_action","/"+ portlet_name+ "/abm_centro_costo");
						row1.addText("ABM - Centro de Costos", rowURL1);
				 		resultRows.add(row1);	
	 					
	 				}
	 				
	 				if(showTABLEROCentroCosto){
	 					ResultRow row1 = new ResultRow(1, 1, 1);
						PortletURL rowURL1 = renderResponse.createRenderURL();
						rowURL1.setWindowState(WindowState.MAXIMIZED);
						rowURL1.setParameter("struts_action","/uoma/tablero_centro_costo");
						row1.addText("Tablero Control - Centro de Costos", rowURL1);
				 		resultRows.add(row1);	
	 					
	 				}
	 				
	 				
/*
					
					if (reportesGrales){
					   ResultRow row23 = new ResultRow(1, 1, 23);
					   PortletURL rowURL23 = renderResponse.createRenderURL();		 				
					   rowURL23.setWindowState(WindowState.MAXIMIZED);		 				
					   rowURL23.setParameter("struts_action","/uoma/busqueda_control_ingresos_egresos");
					   row23.addText("Tablero Control INGRESOS - EGRESOS", rowURL23);					
					   resultRows.add(row23);
					}
*/

			 	%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

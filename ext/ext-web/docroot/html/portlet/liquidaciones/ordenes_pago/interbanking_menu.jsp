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
			 		headerNames.add("Opciones");
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
					
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
					
					/*
					boolean empleadores=PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_EMPLEADORES);
					boolean ctacteActas=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_CTACTE_ACTAS_CONVENIOS_UOMA);
					boolean reportesGrales=PermissionUtil.userContainsRole(user,WebKeysUOMA.VER_REPORTES_GENERALES_UOMA);
					*/
					
				 	int total = 1;
					searchContainer.setTotal(total);
	 				List resultRows = searchContainer.getResultRows();
	 				if(!"tesoreria".equals(portlet_name) ){
	 			      ResultRow row1 = new ResultRow(1, 1, 1);
				      PortletURL rowURL1 = renderResponse.createRenderURL();
				      rowURL1.setWindowState(WindowState.MAXIMIZED);
				      rowURL1.setParameter("struts_action","/"+ portlet_name+ "/upload_archivos_interbanking");
				      row1.addText("Generación de Archivos", rowURL1);
		 		      resultRows.add(row1);
	 				}
	 				
		 		    ResultRow row2 = new ResultRow(1, 1, 2);
				    PortletURL rowURL2 = renderResponse.createRenderURL();
				    rowURL2.setWindowState(WindowState.MAXIMIZED);
				    rowURL2.setParameter("struts_action","/"+ portlet_name+ "/reenviar_ops_interbanking");
				    row2.addText("Habilitar Ordenes de Pago para reenvío", rowURL2);
		 		    resultRows.add(row2);
	 				
			 	%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

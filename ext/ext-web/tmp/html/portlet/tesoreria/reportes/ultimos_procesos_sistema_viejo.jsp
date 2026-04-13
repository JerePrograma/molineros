<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

	List<UltimosProcesosSisOld> archivos=(List<UltimosProcesosSisOld>)renderRequest.getAttribute("ultimosProcesosSisOld");
	PortletURL portletURL = renderResponse.createRenderURL();				
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	
	java.util.Date fecha = new Date();
	String porletName = renderResponse.getNamespace();
	archivos=TraeListasServiceUtil.getUltimosProcesosSisOld(fecha);

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Fecha proceso");	
	headerNames.add("Descargar");					 		
				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM,100, portletURL, headerNames,
	LanguageUtil.get(pageContext, "no-procesos-were-found"));
	
	if(null!=archivos){
	 	List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < archivos.size(); i++) {
	 		
	 		UltimosProcesosSisOld archivo = (UltimosProcesosSisOld) archivos.get(i);
					ResultRow row = new ResultRow(archivo, archivo.getFecha_procesoString(), i);
	 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
	 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
	 				StringBuilder sb = new StringBuilder();	
					sb.append(archivo.getFecha_procesoString());									
					row.addText(sb.toString());
					resultRows.add(row);
					StringBuilder sb2= new StringBuilder();		 
					sb2.append("<img alt=\"<liferay-ui:message key='rendir'/>\" src=\"");
					sb2.append(themeDisplay.getPathThemeImages());
					sb2.append("/document_library/ods.png\" onClick=\"javascript:bajarListadoProcesos('");
					sb2.append(archivo.getFecha_proceso());			 						 					
	 				sb2.append("');\" /> ");
					row.addText(sb2.toString());	 	 				
	 	}
	 }
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

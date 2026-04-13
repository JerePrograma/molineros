<%@page import="javax.rmi.CORBA.UtilDelegate"%>
<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%			   
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysUOMA.BUSCAR_PARITARIAS);				
					List<Paritaria> paritarias= (ArrayList<Paritaria>)renderRequest.getAttribute(WebKeysUOMA.BUSCAR_PARITARIAS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Periodo paritaria:");
					headerNames.add("Camara");
					if(showABMButtons ) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-paritarias-were-found"));
				
					if(null!=paritarias &&  paritarias.size() > 0){
				 								 	
				 		//Seteo el total de la lista.
					 	int total = paritarias.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < paritarias.size(); i++) {
					 		Paritaria paritaria = (Paritaria) paritarias.get(i);
				 			ResultRow row = new ResultRow(paritaria, paritaria.getCamara(), i);
					 		PortletURL rowURL = renderResponse.createRenderURL();		 				
					 		rowURL.setWindowState(WindowState.MAXIMIZED);		 				
					 			StringBuilder sb0 = new StringBuilder();
					 			sb0.append("<a href='javascript:verParitaria(\"");
					 			sb0.append(String.valueOf(paritaria.getCamara()));	
					 			sb0.append( "\" , \"" );
								sb0.append(paritaria.getFechaAltaParitaria());
								sb0.append("\")'>");
								Calendar cal = Calendar.getInstance(); 		
								if(paritaria!=null){
									cal.setTime(paritaria.getFechaAltaParitaria());   
									cal.add(Calendar.MONTH, +1);
								}
								Date date = cal.getTime();             
								SimpleDateFormat format1 = new SimpleDateFormat("MM-yyyy");
								String date1 = format1.format(date); 
								sb0.append(date1);
								sb0.append("</a>");
					 			row.addText(sb0.toString());
					 			StringBuilder sb = new StringBuilder();
								sb.append("<a href='javascript:verParitaria(\"");
								sb.append(String.valueOf(paritaria.getCamara()));										
								sb.append( "\" , \"" );
								sb.append(String.valueOf(paritaria.getFechaAltaParitaria()));
								sb.append("\")'>");	
								sb.append(paritaria.getCamara());
								sb.append("</a>");
								row.addText(sb.toString());	
								resultRows.add(row);
							}
				 			
					}
				 	
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

<script type="text/javascript"> 
	function verParitaria(camara, fecha_paritaria){

		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/alta_ver_paritaria" />
			<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.VIEW%>" /></portlet:renderURL>';
			document.<portlet:namespace />fm.method = 'post';
			url=url+'&camara='+camara
			url=url+'&fecha_paritaria='+fecha_paritaria
			submitForm(document.<portlet:namespace />fm, url);
		
	}	
</script>		

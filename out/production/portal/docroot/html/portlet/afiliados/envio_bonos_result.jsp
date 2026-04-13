<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
    <liferay-ui:error exception="<%= DuplicateEnvioBonosException.class %>" message="the-envio-bono-key-already-exists" />
    <liferay-ui:error exception="<%= EnvioBonosNoExisteEnSeccionalException.class %>" message="the-envio-bono-not-exists-in-seccional" />
    <liferay-ui:error exception="<%= BonoNoCargadoException.class %>" message="the-bono-not-exists-in-bonos" />
			<%
						
				//Si debe mostrarse el btn de agregar afiliado
				List<EnvioBonos> bonosList= (ArrayList<EnvioBonos>) portletSession.getAttribute(WebKeysAfiliados.ENVIO_BONOS,PortletSession.APPLICATION_SCOPE);
				portletSession.setAttribute(WebKeysAfiliados.ENVIO_BONOS, bonosList);
				portletSession.setAttribute(WebKeysAfiliados.ENVIO_BONOS,bonosList,PortletSession.APPLICATION_SCOPE);
				PortletURL portletURL = renderResponse.createRenderURL();				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("tipo-bono");
		 		headerNames.add("seccional");
		 		headerNames.add("fecha-envio");
		 		headerNames.add("bono-desde");
		 		headerNames.add("bono-hasta");
		 		headerNames.add("cantidad");
		 		headerNames.add("fecha-rendicion-fecha-alta");
		 		headerNames.add("fecha-anular");
		 		headerNames.add("anular-reporte");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-envios-bonos-were-found"));
			
				if(null!=bonosList){
	 								 	
	 				//Seteo el total de la lista.
				 	int total = bonosList.size();
				 	searchContainer.setTotal(total);
				 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRows = searchContainer.getResultRows();	 				
				 	for (int i = 0; i < bonosList.size(); i++) {
				 		EnvioBonos envioBonos = (EnvioBonos) bonosList.get(i);
	 					ResultRow row = new ResultRow(envioBonos,envioBonos.getBono_desde(), i);		 				
		 				row.addText(String.valueOf(envioBonos.getTipo_bono_string()));
		 				if(null!=envioBonos.getSeccional_string()){
		 					row.addText(envioBonos.getSeccional_string());	
		 				}else{
		 					row.addText("No Enviado");
		 				}		 				
		 				row.addText(envioBonos.getFecha_envio_string());
		 				row.addText(String.valueOf(envioBonos.getBono_desde()));
		 				row.addText(String.valueOf(envioBonos.getBono_hasta()));
		 				row.addText(String.valueOf(envioBonos.getCant_envio()));		 				
		 				row.addText(envioBonos.getFecha_rendido_string());		 				
		 				row.addText(envioBonos.getFecha_anulacion_string() ); 					
		 				StringBuilder sb= new StringBuilder();	 				
	 					
	 					
	 					if (null==envioBonos.getFecha_rendido() &&  null == envioBonos.getFecha_anulacion() ) 	
	 					{	 							 					
		 					sb.append("<img alt=\"<liferay-ui:message key='anular'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/close.png\" onClick=\"javascript:anularBonos('");
		 					sb.append(envioBonos.getTipo_bono_string());
		 					sb.append("','");
		 					sb.append(String.valueOf(envioBonos.getId_seccional()));
		 					sb.append("','");
		 					sb.append(envioBonos.getSeccional_string());
		 					sb.append("','");
		 					sb.append(envioBonos.getFecha_envio_string());
		 					sb.append("','");		 	
		 					sb.append(envioBonos.getFecha_envio_string());
		 					sb.append("','");		 	
		 					sb.append(String.valueOf(envioBonos.getBono_desde()));
		 					sb.append("','");
		 					sb.append(String.valueOf(envioBonos.getBono_hasta()));	 					
		 					sb.append("');\" /> /");	 			
	 					}else{
	 						sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/");
	 					}
	 					if(null!=envioBonos.getSeccional_string()&& !"No Enviado".equals(envioBonos.getSeccional_string()) &&!"".equals(envioBonos.getSeccional_string())){
		 					sb.append("&nbsp;<img alt=\"<liferay-ui:message key='exportarExcel'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/print.png\" onClick=\"javascript:exportarExcel('");
		 					sb.append(envioBonos.getTipo_bono_string());
		 					sb.append("','");
		 					sb.append(String.valueOf(envioBonos.getId_seccional()));
		 					sb.append("','");
		 					sb.append(envioBonos.getSeccional_string());
		 					sb.append("','");
		 					sb.append(envioBonos.getFecha_envio_string());
		 					sb.append("','");
		 					sb.append(envioBonos.getFecha_envio_string());
		 					sb.append("','");
		 					sb.append(String.valueOf(envioBonos.getBono_desde()));
		 					sb.append("','");
		 					sb.append(String.valueOf(envioBonos.getBono_hasta()));		 					
		 					sb.append("');\" />");
	 					}
	 					
	 					row.addText(sb.toString());	 					
			 			resultRows.add(row);
				 	}
	 			}
 		%>
 		
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

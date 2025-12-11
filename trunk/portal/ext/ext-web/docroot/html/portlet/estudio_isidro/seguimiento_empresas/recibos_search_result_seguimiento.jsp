<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_RECIBOS);				
					List<Recibo> recibos= (ArrayList<Recibo>)portletSession.getAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("entidad");
		 			headerNames.add("recibo");
			 		headerNames.add("fecha");
			 		headerNames.add("empresa");
			 		headerNames.add("importe");
					headerNames.add("baja-fecha");
					/*if(showABMButtons ) { 
						headerNames.add("editar-borrar");
					}*/				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-recibos-were-found"));
				
					if(null!=recibos){					 	
				 		//Seteo el total de la lista.
					 	int total = recibos.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < recibos.size(); i++) {
			 				Recibo rec = recibos.get(i);
			 				String javascriptString=null;
			 				
					 		javascriptString="<a href='javascript:popupRecibo(\"";
					 		
		 					ResultRow row = new ResultRow(rec, rec.getId(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
			 				if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
			 					StringBuilder sb01 = new StringBuilder();
					 			sb01.append(javascriptString);
					 			sb01.append(String.valueOf(String.valueOf(rec.getId())));										
								sb01.append("\",\"").append(rec.getEntidad().trim());
								sb01.append("\")'>");
					 			sb01.append(rec.getEntidad());					 			
					 			sb01.append("</a>");
					 			row.addText(sb01.toString());
					 			StringBuilder sb0 = new StringBuilder();
					 			sb0.append(javascriptString);
					 			sb0.append(String.valueOf(String.valueOf(rec.getId())));										
								sb0.append("\",\"").append(rec.getEntidad().trim());
								sb0.append("\")'>");
					 			sb0.append(String.valueOf(rec.getNumero()));					 			
					 			sb0.append("</a>");
					 			row.addText(sb0.toString());
					 			StringBuilder sb = new StringBuilder();
								sb.append(javascriptString);
								sb.append(String.valueOf(String.valueOf(rec.getId())));										
								sb.append("\",\"").append(rec.getEntidad().trim());
								sb.append("\")'>");			
								sb.append(rec.getFechaAsString());
								sb.append("</a>");
								row.addText(sb.toString());
					 			StringBuilder sb2 = new StringBuilder();
								sb2.append(javascriptString);
					 			sb2.append(String.valueOf(String.valueOf(rec.getId())));
								sb2.append("\",\"").append(rec.getEntidad().trim());
								sb2.append("\")'>");			
								sb2.append(rec.getEmpresa().getRazon_soc());
								sb2.append("</a>");			
								row.addText(sb2.toString());
					 			StringBuilder sb3 = new StringBuilder();
								sb3.append(javascriptString);
								sb3.append(String.valueOf(String.valueOf(rec.getId())));										
								sb3.append("\",\"").append(rec.getEntidad().trim());
								sb3.append("\")'>");			
								sb3.append(rec.getImporte().toString());
								sb3.append("</a>");				
								row.addText(sb3.toString());		
								StringBuilder sb4 = new StringBuilder();
								sb4.append(javascriptString);
								sb4.append(String.valueOf(String.valueOf(rec.getId())));										
								sb4.append("\",\"").append(rec.getEntidad().trim());
								sb4.append("\")'>");
								sb4.append(rec.getBaja_fechaAsString());
								sb4.append("</a>");				
								row.addText(sb4.toString());
								// Action
								/*if(showABMButtons ) {
									row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/estudio_isidro/seguimiento_empresas/editar_borrar_recibo_seguimiento.jsp");
								}*/
							}
				 			resultRows.add(row);
					 	}
				 	}
			%>
<fieldset class="block-labels">			
<legend><liferay-ui:message key="recibos" /></legend>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
</fieldset>			

<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONVENIOS);				
					List<Convenio> convenios= (ArrayList<Convenio>)renderRequest.getAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("convenio");
			 		headerNames.add("empresa");
			 		headerNames.add("fecha");
					headerNames.add("baja-fecha");
					if(showABMButtons ) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-convenios-were-found"));
				
					if(null!=convenios){
				 								 	
				 				//Seteo el total de la lista.
					 	int total = convenios.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < convenios.size(); i++) {
					 		Convenio convenio = (Convenio) convenios.get(i);
				 			ResultRow row = new ResultRow(convenio, convenio.getId(), i);
					 		PortletURL rowURL = renderResponse.createRenderURL();		 				
					 		rowURL.setWindowState(WindowState.MAXIMIZED);		 				
					 		if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
					 			StringBuilder sb0 = new StringBuilder();
					 			sb0.append("<a href='javascript:popupConvenio(\"");
					 			sb0.append(String.valueOf(String.valueOf(convenio.getId())));										
								sb0.append("\")'>");
					 			if (convenio.getNumero() != null && !convenio.getNumero().trim().equals("")){
					 				sb0.append(String.valueOf(convenio.getNumero()));
					 			} else {
					 				sb0.append(String.valueOf(convenio.getId()));
					 			}
					 			sb0.append("</a>");
					 			row.addText(sb0.toString());
					 			StringBuilder sb = new StringBuilder();
								sb.append("<a href='javascript:popupConvenio(\"");
								sb.append(String.valueOf(String.valueOf(convenio.getId())));										
								sb.append("\")'>");			
								sb.append(convenio.getEmpresa().getRazon_soc());
								sb.append("</a>");
								row.addText(sb.toString());
					 			StringBuilder sb2 = new StringBuilder();
								sb2.append("<a href='javascript:popupConvenio(\"");
								sb2.append(String.valueOf(convenio.getId()));										
								sb2.append("\")'>");			
								sb2.append(convenio.getFechaInicioAsString());
								sb2.append("</a>");			
								row.addText(sb2.toString());
					 			StringBuilder sb3 = new StringBuilder();
								sb3.append("<a href='javascript:popupConvenio(\"");
								sb3.append(String.valueOf(convenio.getId()));										
								sb3.append("\")'>");			
								sb3.append(convenio.getBaja_fechaAsString());
								sb3.append("</a>");				
								row.addText(sb3.toString());					 			
					 		}else{
					 			rowURL.setParameter("struts_action","/tesoreria/view_convenios_entry");
					 			rowURL.setParameter("convenio_id", String.valueOf(convenio.getId()));
					 			if (convenio.getNumero() != null && !convenio.getNumero().trim().equals("")){
					 				row.addText(String.valueOf(convenio.getNumero()), rowURL);
					 			} else {
					 				row.addText(String.valueOf(convenio.getId()), rowURL);
					 			}
					 			row.addText(convenio.getEmpresa().getRazon_soc(), rowURL);
					 			row.addText(convenio.getFechaInicioAsString(), rowURL);
					 			row.addText(convenio.getBaja_fechaAsString(), rowURL);
								// Action
								if(showABMButtons ) {
									row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/convenios/editar_borrar_convenio.jsp");
								}
							}
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

<script type="text/javascript">
	var popup;
	function popupConvenio(convenio_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="convenio" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_convenios_entry&convenio_id='+convenio_id;
		jQuery(popup).load(url); 
	}	
</script>		

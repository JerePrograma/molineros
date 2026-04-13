<%@ include file="/html/portlet/cai/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			
				List<Contacto> personalList= (ArrayList<Contacto>)renderRequest.getPortletSession().getAttribute("PERSONAL_SECCIONAL");
			

				boolean showCAI = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_VER_PORTLET_CAI );
				
				PortletURL portletURL = renderResponse.createRenderURL();				
				List<String> headerNames = new ArrayList<String>();
		 		
				headerNames.add("Seccional");
		 		headerNames.add("Cargo");
		 		headerNames.add("Nombre");
		 		headerNames.add("Tipo Tel.");
		 		headerNames.add("Teléfono");
		 		if( showCAI) {
		 			headerNames.add("Contacto");
		 		}	
		 		
				/* if(showABMCrm) { 
					headerNames.add("Contacto");
				}	 */
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-personal-were-found"));
			
				if(null!=personalList){
	 								 	
	 				//Seteo el total de la lista.
				 	int total = personalList.size();
				 	searchContainer.setTotal(total);
				 	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
				 	List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < personalList.size(); i++) {
				 		Contacto contacto = (Contacto) personalList.get(i);
	 					ResultRow row = new ResultRow(contacto,contacto.getIdContacto(), i);
		 				PortletURL rowURL = renderResponse.createRenderURL();		 				
		 				row.addText(null!=contacto.getSeccional() ?contacto.getSeccional().getDescripcion():"");
		 				row.addText(null!=contacto.getCargoDescripcion()?contacto.getCargoDescripcion():"");
		 				row.addText(null!=contacto.getNombreApe()?contacto.getNombreApe():"");
		 				row.addText(null!=contacto.getTelefono() && "F".equalsIgnoreCase(contacto.getTelefono().getTipo()) ?"Fijo" :
		 					null!=contacto.getTelefono() && "M".equalsIgnoreCase(contacto.getTelefono().getTipo())?"Móvil":"");
		 				row.addText(null!=contacto.getTelefono() ?contacto.getTelefono().getNumero() :"");
		 				
						if( showCAI) {
							StringBuilder sb = new StringBuilder();
							sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar Contacto\" src=\"");
						        sb.append(themeDisplay.getPathThemeImages());
				 		        sb.append("/common/telephone.png\" onClick=\"javascript:nuevoCrmContactoSeccional('");
				 		        sb.append(contacto.getIdContacto() );
				 		        sb.append("');\"");
			                    sb.append(" title=\"Editar\"");
				 		        sb.append("/>");
				 		    row.addText(sb.toString());
							
						}else{
							row.addText("");
						}
						resultRows.add(row);
				 	}
	 			}

				%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">
function nuevoCrmContactoSeccional(idContacto) {
<%-- 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/cai/editar_contacto_entry';  
		url=url+'&idContacto='+idContacto;
		url=url+'&cmd=add&contactoSeccional=true'; --%>
		
		var strutsUrl = '/cai/editar_contacto_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="idContacto" value="__idContacto"/>'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
		'<liferay-portlet:param name="contactoSeccional" value="true"/>'+
	    '</liferay-portlet:renderURL>';
	    
	    url = url.replace("__strutsUrl",strutsUrl);
	    url = url.replace("__idContacto",idContacto);
	    
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

</script>

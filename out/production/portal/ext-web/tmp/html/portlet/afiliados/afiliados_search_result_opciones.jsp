<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			
			List<Afiliado> afiliadosList= (ArrayList<Afiliado>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO_OPCIONES);
			if (afiliadosList == null || afiliadosList.size() == 0) {
				afiliadosList = (ArrayList<Afiliado>) portletSession
				.getAttribute(WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);
			}

				//Si debe mostrarse el btn de agregar afiliado								
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);				
				
				PortletURL portletURL = renderResponse.createRenderURL();				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("cuil");
		 		headerNames.add("nro-formulario");
		 		headerNames.add("apellido-y-nombre");
		 		headerNames.add("delegacion");
		 		headerNames.add("fecha-eleccion");
				headerNames.add("fecha-certif");
				headerNames.add("fecha-exportacion");
				headerNames.add("baja-fecha");				
				headerNames.add("dar-alta");
				headerNames.add("editar-opcion");
			/* 	headerNames.add("eliminar-opcion"); */
								
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
				if(null!=afiliadosList){
	 								 	
	 				//Seteo el total de la lista.
				 	int total = afiliadosList.size();
				 	searchContainer.setTotal(total);
				 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < afiliadosList.size(); i++) {
				 		Afiliado afiliado = (Afiliado) afiliadosList.get(i);
	 					ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);
		 				PortletURL rowURL = renderResponse.createRenderURL();		 				
		 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
		 				rowURL.setParameter("struts_action","/afiliados/editar_opcion_entry");
		 				rowURL.setParameter("cuil_titular", afiliado.getCuil_titular());
		 				rowURL.setParameter("opciones", "true");
		 				rowURL.setParameter("editaropcion", "NO");
		 				rowURL.setParameter("nro_formulario", String.valueOf(afiliado.getInte()) ); //chanchada...
		 				row.addText(afiliado.getCuil_titularMasked(),rowURL);
		 				row.addText(afiliado.getInteAsString(),rowURL);
		 				row.addText(afiliado.getApellido()+" "+afiliado.getNombre(),rowURL);		 										
						row.addText(afiliado.getSeccional().getDescripcion()!=null?afiliado.getSeccional().getDescripcion():"Sin Especificar",rowURL);												
						row.addText(afiliado.getIdOspimBajaFechaAsString(),rowURL);
						row.addText(afiliado.getIdUomaBajaFechaAsString(),rowURL);
						row.addText(afiliado.getIdAmtimaBajaFechaAsString(),rowURL);						
						row.addText(afiliado.getBaja_fechaAsString(),rowURL);
						// Action Alta Afiliado a partir de Opcion Sss					
						StringBuilder sb= new StringBuilder();		
						if(afiliado.getDiscapacitado().equalsIgnoreCase("t") && afiliado.getBaja_fecha() == null ){         //casteamos el campo okdesdesss en el campo discapacitado en la busqueda
							sb.append("&nbsp;<img alt=\"<liferay-ui:message key='dar-alta'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/add.png\" onClick=\"javascript:altaOpcion('");
		 					sb.append(afiliado.getCuil_titular());
		 					sb.append("','");
		 					sb.append(afiliado.getInte());   //chanchada dos... 
		 					sb.append("');\" />");
						}else{
							sb.append(" ");
						}
						row.addText(sb.toString());	
						// Action Edicion AfiOpcion Sss					
/* 						StringBuilder sb1= new StringBuilder();		
						if(afiliado.getDiscapacitado().equalsIgnoreCase("f") && StringUtils.checkEmpty(afiliado.getIdAmtimaBajaFechaAsString()) ){
							//casteamos el campo okdesdesss en el campo discapacitado en la busqueda
							//casteamos el campo fecha exportacion en el campo fecha nacimiento en la busqueda 
							sb1.append("&nbsp;<img alt=\"<liferay-ui:message key='editar-opcion'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/edit.png\" onClick=\"javascript:editarOpcion('");
		 					sb1.append(afiliado.getCuil_titular());
		 					sb1.append("');\" />");
						}else{
							sb1.append(" ");
						}
		 				row.addText(sb1.toString()); */
		 				if(showABMButtons) {
							row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/afiliados/editar_borrar_opcion.jsp");
						}
			 			resultRows.add(row);
				 	}
	 			}
 		%>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_CONSULTA_RRHH);				
			    boolean verDetalle = true;				
			
				List<RegistroAcceso> registrosList= new ArrayList<RegistroAcceso> ();
				registrosList= (ArrayList<RegistroAcceso>)renderRequest.getAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS);
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Apellido y Nombre");
		 		headerNames.add("Fecha de Lectura");
	 			headerNames.add("Hora");
		 		headerNames.add("Tipo");		 	
		 		headerNames.add("Horas Permanencia Lectura");
		 		
		 		headerNames.add("Horas Laborales Día");
		 		headerNames.add("Horas Permanencia Día");
		 		headerNames.add("Diferencia por Día");
		 		
		 		headerNames.add("Horas Laborales Periodo");
		 		headerNames.add("Horas Permanencia Periodo");		 		
		 		
		 		headerNames.add("Diferencia por Periodo");
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA,portletURL, headerNames,
								
				LanguageUtil.get(pageContext, "no-contenidos-were-found"));
			    boolean hayReintegrosPagables = false;
							    
				if(null!=registrosList){
						
		 				//Seteo el total de la lista.
					 	int total = registrosList.size();
					 	searchContainer.setTotal(total);
		 				List resultRows = searchContainer.getResultRows();
		 				
			 				//Seteo el total de la lista.						 	
						 	for (int i = 0; i < registrosList.size(); i++) {
						 		RegistroAcceso registro = (RegistroAcceso) registrosList.get(i);
						 		if (!registro.isOcultar()) {
				 					ResultRow row = new ResultRow(registro,registro.getId(), i);
				 					row.addText(registro.getTarjetaAcceso().getApellido() + ", " + registro.getTarjetaAcceso().getNombre());
					 				row.addText(registro.getFecha_registroSinHora());
					 				row.addText(registro.getFecha_registroSoloHora());
					 				row.addText(registro.getTipo_registro());
					 				row.addText(DateUtils.convertMS(registro.getMilisegundosPermanenciaLectura()));
					 				
					 				row.addText(DateUtils.convertMS(registro.getMilisegundosLaboralesDia()));
					 				row.addText(DateUtils.convertMS(registro.getMilisegundosPermanenciaDia()));
					 				row.addText(DateUtils.convertMSInvertido(registro.getDiferenciaMilisegundosDia()));
					 				
					 				row.addText(DateUtils.convertMS(registro.getMilisegundosLaboralesPeriodo()));
					 				row.addText(DateUtils.convertMS(registro.getMilisegundosPermanenciaPeriodo()));
					 				row.addText(DateUtils.convertMSInvertido(registro.getDiferenciaMilisegundosPeriodo()));
					 								 				
						 			resultRows.add(row);
						 		}
						 	}
		 			}
		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
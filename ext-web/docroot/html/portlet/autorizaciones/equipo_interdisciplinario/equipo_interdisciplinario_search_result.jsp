<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_EQUIPO_INTERDISCIPLINARIO  );				
				
				List<EquipoInterdisciplinario> equipoInterdisciplinarioList = new ArrayList<EquipoInterdisciplinario>();
				
				
				equipoInterdisciplinarioList= (ArrayList<EquipoInterdisciplinario>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS );
				if (equipoInterdisciplinarioList == null || equipoInterdisciplinarioList.size() == 0) {
					equipoInterdisciplinarioList = (ArrayList<EquipoInterdisciplinario>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Nro Reg");		 		
		 		headerNames.add("Fecha");
		 		headerNames.add("Estado");
		 		headerNames.add("Seccional");
		 		headerNames.add("Apellido Nombre");
		 		headerNames.add("Cuil");
		 		headerNames.add("Inte");
		 			
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-reclamosprestacionales-were-found"));
			    
							    
				if(null!=equipoInterdisciplinarioList){
		 				//Seteo el total de la lista.
					 	int total = equipoInterdisciplinarioList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < equipoInterdisciplinarioList.size(); i++) {
					 		EquipoInterdisciplinario equipoInter   = (EquipoInterdisciplinario) equipoInterdisciplinarioList.get(i);
		 					ResultRow row = new ResultRow(equipoInter,equipoInter.getId_afiliado(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);			 				
			 							 				
			 				rowURL.setParameter("struts_action","/autorizaciones/editar_borrar_equipointerdisciplinario_entry");
					 		rowURL.setParameter("id_registro_eq", String.valueOf(equipoInter.getId_registroEquipoInter()));
					 		rowURL.setParameter("cmd","view");
					 		
					 		row.addText(equipoInter.getId_registroEquipoInter_String()  != null ? equipoInter.getId_registroEquipoInter_String()  : "" ,  rowURL);
			 				row.addText(equipoInter.getAlta_fecha()    != null ? equipoInter.getAlta_fechaAsString()  : "" );
			 				row.addText(equipoInter.getEstadoRegEquipoInter()   != null ? equipoInter.getEstadoRegEquipoInter()  : "" ,  rowURL);			 				 				
			 				row.addText(equipoInter.getAfiliado().getSeccional().getDescripcion()   != null   ?   equipoInter.getAfiliado().getSeccional().getDescripcion(): "", rowURL);
			 				row.addText(equipoInter.getAfiliado().getApellidoNombre()  != null   ?   equipoInter.getAfiliado().getApellidoNombre() : "", rowURL);
			 				row.addText(equipoInter.getAfiliado().getCuil_titular()   != null   ?   equipoInter.getAfiliado().getCuil_titular() : "", rowURL);
			 				row.addText(equipoInter.getAfiliado().getCuil_titular()   != null   ?   equipoInter.getAfiliado().getInteAsString() : "", rowURL);
			 				
							// Action						
							row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/autorizaciones/equipo_interdisciplinario/editar_borrar_equipo_interdisciplinario.jsp");												
							
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	<script type="text/javascript">
			
	function imprimir(id){		
		window.location.href ="/pdfservlet/?accion=equipointerdisciplinario&idequipo=" + id ;
	}
	
	
	
	
	</script>

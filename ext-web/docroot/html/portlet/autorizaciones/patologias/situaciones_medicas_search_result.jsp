<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

    
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_SITUACIONES_MEDICAS );				
				
			    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    
				List<SituacionMedica> situacionMedicaList = new ArrayList<SituacionMedica>();
				situacionMedicaList= (ArrayList<SituacionMedica>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS );
				if (situacionMedicaList == null || situacionMedicaList.size() == 0) {
					situacionMedicaList = (ArrayList<SituacionMedica>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Nro");		 				 
		 		headerNames.add("Cuil Titular");
		 		headerNames.add("Inte");
		 		headerNames.add("Apellido Nombre");
		 		headerNames.add("Discapacitado");
		 		headerNames.add("Tipo Situ Médica");
		 		headerNames.add("Vig Desde");
		 		headerNames.add("Vig Hasta");	
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-reclamosprestacionales-were-found"));
				if(null!=situacionMedicaList){
		 				//Seteo el total de la lista.
					 	int total = situacionMedicaList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < situacionMedicaList.size(); i++) {
					 		SituacionMedica   situacion   = (SituacionMedica) situacionMedicaList.get(i);
		 					ResultRow row = new ResultRow(situacion,situacion.getId_Situacion(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);			
			 				rowURL.setParameter("struts_action","/autorizaciones/editar_borrar_situacionmedica_entry");
					 		rowURL.setParameter("id_registro_sitmed", String.valueOf(situacion.getId_Situacion() ));
					 		rowURL.setParameter("cmd","view");					 		
					 		row.addText(situacion.getId_String()  != null ? situacion.getId_String() : "" ,  rowURL);
					 		row.addText(situacion.getAfiliado().getCuil_titular()  != null ? situacion.getAfiliado().getCuil_titular() : "" ,  rowURL);
					 		row.addText(situacion.getAfiliado().getInteAsString()   != null ? situacion.getAfiliado().getInteAsString()  : "" ,  rowURL);
					 		row.addText(situacion.getAfiliado().getApellidoNombre()    != null ? situacion.getAfiliado().getApellidoNombre()  : "" ,  rowURL);
					 		row.addText(situacion.isDiscapacitado()  ? "Si" : "No" ,  rowURL);
					 		row.addText(situacion.getTipoSituMedica()  != null ? situacion.getTipoSituMedica() : "" ,  rowURL);			 					 				
					 		row.addText(situacion.getFechaVigen_Desde() != null ? sdf.format(situacion.getFechaVigen_Desde() )  : "" ,  rowURL);										 				
					 		row.addText(situacion.getFechaVigen_Hasta()  != null ? sdf.format(situacion.getFechaVigen_Hasta() ) : "" ,  rowURL);
							// Action		
							showABMButtons=true;
							if(showABMButtons  ) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/autorizaciones/patologias/editar_borrar_situacionmedica.jsp");
							}
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	<liferay-util:include page="/html/portlet/autorizaciones/patologias/paginador_situacion_medica.jsp">
    </liferay-util:include>	
	<script type="text/javascript">
			
	function imprimir(id){		
		window.location.href ="/pdfservlet/?accion=idsituacionmedica&idreclamo=" + id ;
	}
	
	
	</script>
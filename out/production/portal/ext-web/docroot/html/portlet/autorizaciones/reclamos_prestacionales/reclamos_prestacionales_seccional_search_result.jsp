<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				//boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST );				
				
			boolean showABMButtons = true;

				List<ReclamoPrestacional> reclamosPrestacionalesList = new ArrayList<ReclamoPrestacional>();
				reclamosPrestacionalesList= (ArrayList<ReclamoPrestacional>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES );
				if (reclamosPrestacionalesList == null || reclamosPrestacionalesList.size() == 0) {
					reclamosPrestacionalesList = (ArrayList<ReclamoPrestacional>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_RECLAMOS_PRESTACIONALES, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Nro Reclamo");
		 		headerNames.add("Tipo Pedido");
		 		headerNames.add("Sector");
		 		headerNames.add("Estado");
		 		headerNames.add("Fecha Ospim");
		 		headerNames.add("Seccional");		 		
		 		headerNames.add("Apellido, Nombre");
		 		headerNames.add("DNI");
		 		headerNames.add("Plan");		 		
		 		
		 			
		 		String vereditarborrar = "Ver";		 		
				if(showABMButtons) {
					vereditarborrar+="|Editar|Borrar";
				}
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-reclamosprestacionales-were-found"));
			    
			    			     
				if(null!=reclamosPrestacionalesList){
		 				//Seteo el total de la lista.
					 	int total = reclamosPrestacionalesList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < reclamosPrestacionalesList.size(); i++) {
					 		ReclamoPrestacional reclamo  = (ReclamoPrestacional) reclamosPrestacionalesList.get(i);
		 					ResultRow row = new ResultRow(reclamo,reclamo.getId_reclamo(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			 							 				
			 				rowURL.setParameter("struts_action","/autorizaciones/editar_reclamosprestaciones_seccional_entry");
					 		rowURL.setParameter("id_reclamosel", String.valueOf(reclamo.getNroReclamo()));
					 		rowURL.setParameter("cmd","view");
					 		rowURL.setParameter(Constants.ACTION,WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL);
					 		
					 		row.addText(reclamo.getNroReclamo() != null ? reclamo.getNroReclamo().toString()  : "" ,  rowURL);
					 		row.addText(reclamo.getTipoPedido() != null ? reclamo.getTipoPedido() : "" ,  rowURL);
					 		row.addText(reclamo.getSector() != null ? reclamo.getSector() : "" ,  rowURL);
			 				row.addText(reclamo.getEstadoReclamoPrestacion()  != null ? reclamo.getEstadoReclamoPrestacion().toString() : "" );
			 				row.addText(reclamo.getOspim_fechaAsString() != null ? reclamo.getOspim_fechaAsString() : "" , rowURL);	 				
			 				if ( (reclamo.getAfiliado().getSeccional().getIdSeccional()) == 9999){
								row.addText( "DESCONOCIDA"  , rowURL);
			 				}else{
								row.addText(reclamo.getAfiliado().getSeccional().getDescripcion()  , rowURL);
			 				}			
			 				row.addText(reclamo.getAfiliado().getApeNombre()  != null   ?   reclamo.getAfiliado().getApeNombre() : "", rowURL);
			 				row.addText(reclamo.getAfiliado().getInteAsString() != null   ?  reclamo.getAfiliado().getDocu_numero()    : "", rowURL) ;
			 				row.addText(reclamo.getAfiliado() != null && reclamo.getAfiliado().getUltimo_plan().getDescripcion()!=null   ?    String.valueOf(reclamo.getAfiliado().getUltimo_plan().getDescripcion()   ) : "" , rowURL);			 				
							
							if(showABMButtons && reclamo.getEstadoreclamo() !=3 ) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/autorizaciones/reclamos_prestacionales/editar_borrar_reclamo_seccional.jsp");
							}else{
				 				row.addText("<img height='16'  width='20'  src='/html/themes/classic/images/common/preview.png' />", rowURL);
							}
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/paginador_reclamos_prestacionales.jsp">
    </liferay-util:include>	
	<script type="text/javascript">
			
	function imprimir(id){		
		window.location.href ="/pdfservlet/?accion=reclamoprestacional&idreclamo=" + id ;
	}
	
	
	</script>
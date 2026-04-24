<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%							
					List<PrestadorLugarAtencion> lugarAtPrestador = null;

					lugarAtPrestador =  (ArrayList<PrestadorLugarAtencion>) request.getSession().getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);

					if(lugarAtPrestador == null){
						lugarAtPrestador = new ArrayList<PrestadorLugarAtencion>();
					}
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();
					 		headerNames.add("Vigencia Desde");
					 		headerNames.add("Factura");
					 		headerNames.add("Nombre");
					 		headerNames.add("Provincia");
					 		headerNames.add("Localidad");
					 		headerNames.add("Direccion");
					 		headerNames.add("email");
					 		headerNames.add("Telefono");
					 		headerNames.add("Editar/Eliminar");						

					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-lugarat-were-found"));
				
				 		//Seteo el total de la lista.
					 	int total = lugarAtPrestador.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
				 		for (int i = 0; i < lugarAtPrestador.size(); i++) {	    
				 			 		
		 			 		PrestadorLugarAtencion la = (PrestadorLugarAtencion) lugarAtPrestador.get(i);
		 			 		
		 			 		String direccion = "";
							row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/prestadores/editar_borrar_lugar_at_prestador.jsp");
		 			 				    la.getDomicilio().getPiso() + " " + la.getDomicilio().getDepto() + " CP: " + 
		 			 				    la.getDomicilio().getPostal_codi();
		 					ResultRow row = new ResultRow(la,la.getId_domicilio(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			 				rowURL.setParameter("struts_action","/liquidaciones/lista_lugares_atencion_prestador");
			 				rowURL.setParameter("domicilio_id", String.valueOf(la.getId_domicilio()));
			 				rowURL.setParameter("prestador_id", String.valueOf(la.getId_prestador()));
			 				rowURL.setParameter("cmd","edit");
			 				row.addText(DateUtils.format(la.getVigen_desde(), DateUtils.SHORT), rowURL);
			 			 	row.addText(la.getFactura(), rowURL);
			 			 	row.addText(la.getNombre(), rowURL);	 	
			 			 	row.addText(la.getDomicilio().getProvincia().getDescripcion(), rowURL);	 
			 			 	row.addText(la.getDomicilio().getLocalidad().getDescripcion(), rowURL);	 
			 			 	row.addText(direccion , rowURL);
			 			 	row.addText(la.getCorreoElectronico() , rowURL);
			 			 	row.addText(la.getTelefonosConcatenados() , rowURL);
							row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/administracion/prestadores/editar_borrar_lugar_at_prestador.jsp");
						
				 			resultRows.add(row);
					 	}
				 
			%>
<liferay-ui:error exception="<%=LugarAtencionPrestadorException.class %>" message="lugar-at-duplicado" />


<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	



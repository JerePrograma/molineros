<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<portlet:defineObjects/>
			<% 
			List<Domicilio> historico= (List<Domicilio>)request.getAttribute(WebKeysAfiliados.HISTORICO_DOMICILIOS);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			
			if (historico != null){
				
		 		List<String> headerNamesDomi = new ArrayList<String>();
		 		headerNamesDomi.add("tipo");
		 		/* headerNamesAporte.add("vigente-desde"); */
		 		headerNamesDomi.add("calle");
		 		headerNamesDomi.add("numero");
		 		headerNamesDomi.add("piso");
		 		headerNamesDomi.add("Depto");
		 		headerNamesDomi.add("localidad");
		 		headerNamesDomi.add("cod-postal");
		 		headerNamesDomi.add("provincia");
		 		headerNamesDomi.add("usuario-alta");
		 		headerNamesDomi.add("fecha-alta");
		 		headerNamesDomi.add("observaciones");
		 		headerNamesDomi.add("Activo");

				List<String> headerNamesTel = new ArrayList<String>();
				headerNamesTel.add("Cod. area");
		 		headerNamesTel.add("Teléfono");
		 		headerNamesTel.add("Cod. area laboral");
		 		headerNamesTel.add("Teléfono laboral");
		 		headerNamesTel.add("Cod. area celular");
		 		headerNamesTel.add("Celular");
		 		headerNamesTel.add("usuario-alta");
		 		headerNamesTel.add("fecha-alta");
		 		headerNamesTel.add("Activo");
		 		
				PortletURL portletURLDomiTel = renderResponse.createRenderURL();
				
				SearchContainer searchContainerDomicilio= new SearchContainer(renderRequest, null, null,
						SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURLDomiTel, headerNamesDomi,
						LanguageUtil.get(pageContext, "no-domicilios-were-found"));
				
				SearchContainer searchContainerTelefono= new SearchContainer(renderRequest, null, null,
						SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURLDomiTel, headerNamesTel,
						LanguageUtil.get(pageContext, "no-telefonos-were-found"));
				
				int total=historico.size();
 				searchContainerDomicilio.setTotal(total);
				searchContainerTelefono.setTotal(total);

				List resultRowsDomi = searchContainerDomicilio.getResultRows();
				List resultRowsTel = searchContainerTelefono.getResultRows();
				
				for (Domicilio domi : historico){
	 				
	 				
	 				ResultRow rowDomi=null;
	 				
	 				rowDomi = new ResultRow("A", "B", 0);
	 				
	 				if(domi.getDomi_tipo().equalsIgnoreCase("P")){
	 					rowDomi.addText("PORTAL");
	 				}else if(domi.getDomi_tipo().equalsIgnoreCase("V")){
	 					rowDomi.addText("VERAZ");
	 				}else{
	 					rowDomi.addText("EQ.INTERDIS.");
	 				}
	 				/* rowDomi.addText(domi.getDomi_tipo()); */
	 				rowDomi.addText(domi.getCalle());
	 				rowDomi.addText(domi.getNumero());
	 				rowDomi.addText(domi.getPiso());
	 				rowDomi.addText(StringUtils.checkNotEmpty(domi.getDepto())?domi.getDepto():"");
	 				rowDomi.addText(domi.getLocalidadAsString());
	 				rowDomi.addText(domi.getPostal_codi());
	 				rowDomi.addText(domi.getProvinciaAsString());
	 				rowDomi.addText(sdf2.format(domi.getAlta_fecha())) ;
	 				rowDomi.addText(domi.getAlta_usr());
	 				rowDomi.addText(StringUtils.checkNotEmpty(domi.getObservaciones())?domi.getObservaciones():"" );
	 				if(Validator.isNull(domi.getBaja_fecha())){
	 					rowDomi.addText("VIGENTE");	
	 				}else{
	 					rowDomi.addText("");
	 				}
	 						
	 			 	resultRowsDomi.add(rowDomi);

	 			 	if(StringUtils.checkNotEmpty(domi.getTelefono()) || 
	 			 			StringUtils.checkNotEmpty(domi.getCelular()) ||
	 			 					StringUtils.checkNotEmpty(domi.getTel_laboral())){
	 			 	
		 				ResultRow rowTel=null;
		 				
		 				rowTel= new ResultRow("C", "D", 0);
	
		 				rowTel.addText(domi.getCod_area_telefono());
		 				rowTel.addText(domi.getTelefono());
		 				rowTel.addText(domi.getCod_area_tel_laboral() );
		 				rowTel.addText(domi.getTel_laboral());
		 				rowTel.addText(domi.getCod_area_celular());
		 				rowTel.addText(domi.getCelular());
		 				rowTel.addText(sdf2.format(domi.getAlta_fecha())) ;
		 				rowTel.addText(domi.getAlta_usr());
		 				if(Validator.isNull(domi.getBaja_fecha())){
		 					rowTel.addText("VIGENTE");	
		 				}else{
		 					rowTel.addText("");
		 				}
		 						
		 			 	resultRowsTel.add(rowTel);
	 			 	}else{
	 			 		searchContainerTelefono.setTotal(searchContainerTelefono.getTotal()-1);
	 			 	}
		 			 	
	 				%>
	
	 			  <%
				}
				
				%>
				<legend><liferay-ui:message	key="domic-histo" /></legend>
				<liferay-ui:search-iterator searchContainer="<%=searchContainerDomicilio%>" />
				<br/>
				<legend><liferay-ui:message	key="tel-histo" /></legend>
				<liferay-ui:search-iterator searchContainer="<%=searchContainerTelefono%>" />
				<%
			}
				 %>	
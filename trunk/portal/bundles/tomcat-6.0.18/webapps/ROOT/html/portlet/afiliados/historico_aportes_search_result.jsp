<%@ include file="/html/portlet/afiliados/init.jsp" %>
<portlet:defineObjects/>
			<% 
			List<AfiPlan> historico= (List<AfiPlan>)request.getAttribute(WebKeysAfiliados.HISTORICO_APORTES);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			
			if (historico != null){
				
		 		List<String> headerNamesAporte = new ArrayList<String>();
		 		headerNamesAporte.add("plan");		 		
		 		headerNamesAporte.add("vigente-desde");
		 		headerNamesAporte.add("vigente-hasta");
		 		headerNamesAporte.add("motivo-baja");
				headerNamesAporte.add("id-ospim");
				headerNamesAporte.add("id-uoma");
				headerNamesAporte.add("id-amtima");
				headerNamesAporte.add("fecha-alta");
				headerNamesAporte.add("usuario-alta");
				headerNamesAporte.add("fecha_modif");
				headerNamesAporte.add("usuario-modi");
				headerNamesAporte.add("Eliminado");
				
				PortletURL portletURLAporte = renderResponse.createRenderURL();
				
				SearchContainer searchContainerAporte = new SearchContainer(renderRequest, null, null,
						SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURLAporte, headerNamesAporte,
						LanguageUtil.get(pageContext, "no-aporte-were-found"));
				
				int total=historico.size();
 				searchContainerAporte.setTotal(total);
 				
				for (AfiPlan planes : historico){

					List<AfiAportes> aporteList = planes.getAportes();
					
					/* int total=aporteList.size();
	 				searchContainerAporte.setTotal(total); */
	 				
	 				List resultRowsAporte = searchContainerAporte.getResultRows();
	 				ResultRow rowPlanIdSocio=null;
	 				
	 				/* rowPlanIdSocio = new ResultRow("A", "B", 0, true); */
	 				rowPlanIdSocio = new ResultRow("A", "B", 0);
	 				rowPlanIdSocio.addText(planes.getPlan().getDescripcion());
	 				rowPlanIdSocio.addText(sdf2.format(planes.getVigenDesde()));
			 		if(planes.getVigenHasta()!=null){
			 			rowPlanIdSocio.addText(sdf2.format(planes.getVigenHasta()));
			 		}else{
			 			rowPlanIdSocio.addText("");
			 		}
			 		if(planes.getMotivoBaja()!=null && planes.getMotivoBaja().getDescripcion()!=null){
			 			rowPlanIdSocio.addText(planes.getMotivoBaja().getDescripcion());
			 		}else{
			 			rowPlanIdSocio.addText("");
			 		}
			 		
			 		String idOspim = " ", idAmtima = " ", idUoma = " " ;
			 		if(aporteList!=null){
	 			 	  for (int i = 0; i < aporteList.size(); i++) {
	 			 		AfiAportes aporte = (AfiAportes) aporteList.get(i);
	 			 		
	 			 		if(aporte.getTipoIdSocio() != null && aporte.getTipoIdSocio().equalsIgnoreCase("O")){
	 			 			/* rowPlanIdSocio.addText(String.valueOf(aporte.getIdSocio())); */
							idOspim = String.valueOf(aporte.getIdSocio());
	 			 			
	 			 		}else if(aporte.getTipoIdSocio() != null && aporte.getTipoIdSocio().equalsIgnoreCase("A")){
	 			 			/* rowPlanIdSocio.addText(String.valueOf(aporte.getIdSocio())); */
							idAmtima = String.valueOf(aporte.getIdSocio());

	 			 		
	 			 		}else if(aporte.getTipoIdSocio() != null && aporte.getTipoIdSocio().equalsIgnoreCase("U") && aporte.getIdSocio() > 0){
	 			 			/* rowPlanIdSocio.addText(String.valueOf(aporte.getIdSocio())); */
							idUoma = String.valueOf(aporte.getIdSocio());
	 			 		}
	 			 	  }
			 		}
	 			 	rowPlanIdSocio.addText(idOspim);
	 			 	rowPlanIdSocio.addText(idUoma);
	 			 	rowPlanIdSocio.addText(idAmtima);
	 			 	rowPlanIdSocio.addText(sdf.format(planes.getAltaFecha()));
	 			 	rowPlanIdSocio.addText(planes.getAltaUsr());
	 			 	rowPlanIdSocio.addText(sdf.format(planes.getModiFecha()));
	 			 	rowPlanIdSocio.addText(planes.getModiUsr());
	 			 	StringBuilder sb = new StringBuilder("");
					if (Validator.isNotNull(planes.getBajaFecha()) ) {				
							sb.append("<img alt=\"Baja\" src=\"");
							sb.append(themeDisplay.getPathThemeImages());
							sb.append("/common/close.png\"/>");
							
							rowPlanIdSocio.addText(sb.toString());
					}else{
						rowPlanIdSocio.addText("");
					}
	 			 	
	 			 	resultRowsAporte.add(rowPlanIdSocio);
	 				%>
						<%-- <table class="lfr-table">
						   <tr><td colspan="4">&nbsp;</td></tr>
						   <tr>
						    <td><label><liferay-ui:message key="fecha-alta" />:</label></td>
						    <td><%= sdf.format(planes.getAltaFecha())%></td>
						    <td><label><liferay-ui:message key="usuario-alta" />:</label></td>
						    <td><%= planes.getAltaUsr()%></td>
						    <td><label><liferay-ui:message key="fecha_modif" />:</label></td>
						    <td><%= sdf.format(planes.getModiFecha())%></td>
						    <td><label><liferay-ui:message key="usuario-modi" />:</label></td>
						    <td><%= planes.getModiUsr()%></td>						    
						  </tr>
						</table>  --%>

	 				<%-- <liferay-ui:search-iterator searchContainer="<%=searchContainerAporte%>" /> --%>
	 			  <%
				}
				%>
				<liferay-ui:search-iterator searchContainer="<%=searchContainerAporte%>" />
				<%
			}
				 %>	
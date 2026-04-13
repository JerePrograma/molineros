<%@ include file="/html/portlet/afiliados/init.jsp" %>
<portlet:defineObjects/>
			<% 
			List<AfiTercerizadoraServicio> historico= (List<AfiTercerizadoraServicio>)request.getAttribute(WebKeysAfiliados.HISTORICO_TERCERIZADORAS);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

			if (historico != null){
				
		 		List<String> headerNamesTerc = new ArrayList<String>();
		 		headerNamesTerc.add("tercerizadora-servicio");		 		
		 		headerNamesTerc.add("vigente-desde");
		 		headerNamesTerc.add("vigente-hasta");
				headerNamesTerc.add("fecha-alta");
				headerNamesTerc.add("usuario-alta");
				headerNamesTerc.add("fecha_modif");
				headerNamesTerc.add("usuario-modi");
				headerNamesTerc.add("Eliminado");
				
				PortletURL portletURLAporte = renderResponse.createRenderURL();
				
				SearchContainer searchContainerTerceriz = searchContainerTerceriz= new SearchContainer(renderRequest, null, null,
						SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURLAporte, headerNamesTerc,
						LanguageUtil.get(pageContext, "no-aporte-were-found"));
				
				int total=historico.size();
 				searchContainerTerceriz.setTotal(total);
 				
				for (AfiTercerizadoraServicio tercerizadoras : historico){
	 				
	 				List resultRowsTerc = searchContainerTerceriz.getResultRows();
	 				ResultRow rowTercer=null;
	 				
	 				/* rowTercer = new ResultRow("A", "B", 0, true); */
	 				rowTercer = new ResultRow("A", "B", 0);
	 				rowTercer.addText(tercerizadoras.getTercerizadora().getDescripcion());
	 				rowTercer.addText(sdf2.format(tercerizadoras.getFechaInicioPres()));
			 		if(tercerizadoras.getFechaFinPres()!=null){
			 			rowTercer.addText(sdf2.format(tercerizadoras.getFechaFinPres()));
			 		}else{
			 			rowTercer.addText("");
			 		}
	 			 	rowTercer.addText(sdf.format(tercerizadoras.getAltaFecha()));
	 			 	rowTercer.addText(tercerizadoras.getAltaUsr());
	 			 	rowTercer.addText(sdf.format(tercerizadoras.getModiFecha()));
	 			 	rowTercer.addText(tercerizadoras.getModiUsr());
	 			 	StringBuilder sb = new StringBuilder("");
					if (Validator.isNotNull(tercerizadoras.getBajaFecha()) ) {				
							sb.append("<img alt=\"Baja\" src=\"");
							sb.append(themeDisplay.getPathThemeImages());
							sb.append("/common/close.png\"/>");
							
							rowTercer.addText(sb.toString());
					}else{
						rowTercer.addText("");
					}
	 			 	
	 			 	resultRowsTerc.add(rowTercer);
				}
				%>
				<liferay-ui:search-iterator searchContainer="<%=searchContainerTerceriz%>" />
				<%
			}
			 %>	
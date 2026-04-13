<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<portlet:defineObjects/>
			<% 
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);
				Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
	
				boolean esEdicion = false;
	
				if (showABMButtons && (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null)) {
					esEdicion = true;
				}
				
				if (acta != null && acta.isActaCerrada()){
					esEdicion = false;
				}
				
				List<Acta.DetalleActaInspectores> detalles= acta.getDetallesActas();
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("Tipo");
		 		headerNamesTercerizadora.add("Desde");
		 		headerNamesTercerizadora.add("Hasta");
		 		headerNamesTercerizadora.add("Capital");
		 		headerNamesTercerizadora.add("Interes");
		 		headerNamesTercerizadora.add("Total");
				if(esEdicion) { 
					headerNamesTercerizadora.add("Borrar");
				}				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-detalle-acta-were-found"));
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				
				if(null!=detalles){
					int total=detalles.size();	 	
	 				searchContainer.setTotal(total);
	 				List resultRowsDetalle = searchContainer.getResultRows();
	 			 	for (int i = 0; i < detalles.size(); i++) {
					if (detalles.get(i).isBorradoLogico()){
						total--;
						continue;
					}
	 					ResultRow rowDetalle = new ResultRow(detalles.get(i),detalles.get(i).getId(), i);
	 					Acta.DetalleActaInspectores det = detalles.get(i);
	 					rowDetalle.addText("APORTES Y CONTRIBUCIONES");
	 					rowDetalle.addText(det.getDesde() != null ? format.format(det.getDesde()) : "");
	 					rowDetalle.addText(det.getHasta() != null ? format.format(det.getHasta()) : "");
	 					rowDetalle.addText(det.getCapital() != null ? det.getCapital().toString() : "");
	 					rowDetalle.addText(det.getInteres() != null ? det.getInteres().toString() : "");
	 					rowDetalle.addText(det.getTotal() != null ? String.valueOf(det.getTotal()) : "");
	 					resultRowsDetalle.add(rowDetalle);
	 					if (esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraDetalleActa('");
		 					sb.append(detalles.get(i).getId());
		 					sb.append("');\" />");
		 					rowDetalle.addText(sb.toString());
		 			 	}
		 			}
				}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainer%>" />

		

		
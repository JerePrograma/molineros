<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			
			SeguimientoSur seguimiento= (SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			List<SeguimientoSurDetalle>  detalles= new ArrayList<SeguimientoSurDetalle>();
	        if(seguimiento.getDetalles()!=null && seguimiento.getDetalles().size()>0 ){
	        	detalles=seguimiento.getDetalles();
	        }
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
			boolean rolExpedienteSUR = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ALTA_EXPEDIENTES_SUR);
			boolean rolExpedienteSURCierre = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CIERRE_EXPEDIENTES_SUR);


			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = rolExpedienteSUR || rolExpedienteSURCierre;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Fecha de Carga");
	 		headerNamesTercerizadora.add("Fecha");
	 		headerNamesTercerizadora.add("Estado");
	 		headerNamesTercerizadora.add("Observaciones");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Editar/Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=detalles){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < detalles.size(); i++) {
 			 		SeguimientoSurDetalle detalle = detalles.get(i);
 			 		if (detalle.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(detalle, detalle.getId(), i);
	 					row.addText(detalle.getFechaCarga_string());
	 					row.addText(detalle.getFechaNotificacion_string());
	 					row.addText(detalle.getEstadoDescripcion() );
	 					row.addText(detalle.getObservaciones()==null?"":detalle.getObservaciones());
	 					
	 					if (showABMButtons && esEdicion && seguimiento.getCierre_fecha()==null && seguimiento.getBaja_fecha()==null){
	 						
	 						StringBuilder sb= new StringBuilder();
	 						sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/edit.png\" onClick=\"javascript:editaSeguimientoSurDetalle('");
		 					sb.append(detalle.getId());
		 					sb.append("','");
		 					sb.append(detalle.getEstadoId().toString() );
		 					sb.append("','");
		 					sb.append(detalle.getFechaNotificacion_string());
		 					sb.append("','");
		 					sb.append(detalle.getObservaciones());
		 					sb.append("');\" />");
		 					
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraSeguimientoSurDetalle('");
		 					sb.append(detalle.getId());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else {
	 			 			row.addText("");
	 			 		}
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
		
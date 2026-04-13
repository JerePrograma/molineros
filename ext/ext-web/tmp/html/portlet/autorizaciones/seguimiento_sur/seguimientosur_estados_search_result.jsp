<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado" %>
<%@ page import="ar.com.ospim.util.StringUtils" %>
<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			SeguimientoSur seguimiento= (SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			List<SeguimientoSurEstado>  detalles= new ArrayList<SeguimientoSurEstado>();
	        if(seguimiento.getEstados()!=null && seguimiento.getEstados().size()>0 ){
	        	detalles=seguimiento.getEstados();
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
	 		headerNamesTercerizadora.add("Estado ");
	 		headerNamesTercerizadora.add("Descripción");
	 		headerNamesTercerizadora.add("Fecha");
	 		headerNamesTercerizadora.add("Usuario");
	 		headerNamesTercerizadora.add("Observaciones");
	 		headerNamesTercerizadora.add("Motivo");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Editar/Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=detalles){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < detalles.size(); i++) {
 			 		SeguimientoSurEstado detalle = detalles.get(i);
 			 		if (detalle.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(detalle, detalle.getId(), i);
	 					row.addText(detalle.getIdEstado().toString());
	 					row.addText(detalle.getDescripcionEstado());
	 					row.addText(detalle.getFechaEstado_string());
	 					row.addText(detalle.getUsuario());
	 					row.addText(detalle.getObservaciones()==null?"":detalle.getObservaciones());
	 					row.addText(detalle.getDescripcionMotivo() );
	 					
	 					if (showABMButtons && esEdicion && seguimiento.getCierre_fecha()==null && seguimiento.getBaja_fecha()==null){
	 					
	 						StringBuilder sb= new StringBuilder();
	 						sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/edit.png\" onClick=\"javascript:editaSeguimientoSurEstado('");
		 					sb.append(detalle.getId());
		 					sb.append("','");
		 					sb.append(detalle.getIdEstado());
		 					sb.append("','");
		 					sb.append(detalle.getFechaEstado_string());
		 					sb.append("','");
		 					sb.append(StringUtils.encodeURIComponent(detalle.getObservaciones()==null?"":detalle.getObservaciones()));
		 					sb.append("','");
		 					sb.append(detalle.getIdMotivo());
		 					sb.append("');\" />");
		 					
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraSeguimientoSurEstado('");
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
		
		
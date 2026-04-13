<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador" %>
<%@ page import="java.text.DecimalFormat" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			
			SeguimientoSur seguimiento= (SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			List<Nomenclador>  detalles= new ArrayList<Nomenclador>();
	        if(seguimiento.getCodigosPresentados()!=null && seguimiento.getCodigosPresentados().size()>0 ){
	        	detalles=seguimiento.getCodigosPresentados();
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
	 		headerNamesTercerizadora.add("Id ");
	 		headerNamesTercerizadora.add("Código ");
	 		headerNamesTercerizadora.add("Descripción");
	 		headerNamesTercerizadora.add("Nomenclador");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=detalles){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < detalles.size(); i++) {
 			 		Nomenclador detalle = detalles.get(i);
 			 		/*
 			 		if (detalle.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
 			 		*/
	 					ResultRow row = new ResultRow(detalle, detalle.getId_prestacion() , i);
	 					row.addText(detalle.getId_prestacion_string() );
	 					row.addText(detalle.getCodigo() );
	 					row.addText(detalle.getDescripcion() );
	 					row.addText(detalle.getDescripcionTipoNomenclador());
	 					
	 					if (showABMButtons && esEdicion && seguimiento.getCierre_fecha()==null && seguimiento.getBaja_fecha()==null){
	 						
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraSeguimientoSurCodigoNomenclador('");
		 					sb.append(detalle.getId_prestacion());
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
	
<script type="text/javascript">
	jQuery('#<portlet:namespace />topeRecuperoSeguimientoSUR').val('<%=seguimiento.getTopeRecupero()==null?0: (new DecimalFormat("#.00")).format(seguimiento.getTopeRecupero())%>'.replace(",","."));
</script>	
		
		
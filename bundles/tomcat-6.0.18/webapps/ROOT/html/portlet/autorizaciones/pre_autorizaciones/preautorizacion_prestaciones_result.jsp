<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<portlet:defineObjects/>
			<%
			
			PreAutorizacion preautorizacion=(PreAutorizacion)request.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			List<PreAutorizacionPrestacion>  detalles= new ArrayList<PreAutorizacionPrestacion>();
	        if(preautorizacion != null && preautorizacion.getCodigosPresentados()!=null && preautorizacion.getCodigosPresentados().size()>0 ){
	        	detalles=preautorizacion.getCodigosPresentados();
	        }
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
		

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
//	 		headerNamesTercerizadora.add("Id ");
	 		headerNamesTercerizadora.add("Código ");
	 		headerNamesTercerizadora.add("Descripción");
	 		headerNamesTercerizadora.add("Opción");
	 		headerNamesTercerizadora.add("Nomenclador");
	 		headerNamesTercerizadora.add("Req.Autorización");
	 		headerNamesTercerizadora.add("SUPRA");
	 		headerNamesTercerizadora.add("Cirugía");
	 		headerNamesTercerizadora.add("Cantidad");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Total");
	 		
			if( esEdicion) { 
				headerNamesTercerizadora.add("Editar");
				headerNamesTercerizadora.add("Borrar");
			}else{
				headerNamesTercerizadora.add("");
				headerNamesTercerizadora.add("");
			}
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-prestaciones-were-found"));
		
			NumberFormat format2D = new DecimalFormat("#0.00");
			NumberFormat format0D = new DecimalFormat("#0");
			
			if(null!=detalles){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < detalles.size(); i++) {
 			 		PreAutorizacionPrestacion detalle = detalles.get(i);
	 					ResultRow row = new ResultRow(detalle, detalle.getNomenclador().getId_prestacion() , i);
//	 					row.addText(detalle.getNomenclador().getId_prestacion_string() );
	 					row.addText(detalle.getNomenclador().getCodigo() );
	 					row.addText(detalle.getNomenclador().getDescripcion() );
	 				    row.addText(detalle.getOpcionApoyo() == null || detalle.getOpcionApoyo().getDescripcion()==null ||
	 				    		detalle.getOpcionApoyo().getId() == 0?"":detalle.getOpcionApoyo().getDescripcion());
	 					row.addText(detalle.getNomenclador().getDescripcionTipoNomenclador());
	 					
	 					StringBuffer sb0 = new StringBuffer();
	 					sb0.append("<input type=\"checkbox\"");
	 					sb0.append("name=\"presta\"");
	 					if(detalle.getNomenclador().getRequiereAutorizacion()){
	 						sb0.append("\" checked=\"checked");
	 					}
	 					sb0.append("id=\"");
	 					sb0.append("formu-"+   detalle.getNomenclador().getId_prestacion_string() +"|"+ detalle.getIdAux() );
	 			        sb0.append("\" value=\"");
	 					sb0.append(detalle.getNomenclador().getId_prestacion_string() +"|"+  detalle.getIdAux());									
	 					sb0.append("\"/>");
	 					
	 					row.addText(sb0.toString());
	 					
	 					StringBuffer sb10 = new StringBuffer();
	 					sb10.append("<input type=\"checkbox\"");
	 					sb10.append("name=\"presta\"");
	 					if(detalle.getNomenclador().isSupra() ){
	 						sb10.append("\" checked=\"checked");
	 					}
	 					sb10.append("id=\"");
	 					sb10.append("formu-supra-"+   detalle.getNomenclador().getId_prestacion_string() +"|"+ detalle.getIdAux() );
	 			        sb10.append("\" value=\"");
	 					sb10.append(detalle.getNomenclador().getId_prestacion_string() +"|"+  detalle.getIdAux());									
	 					sb10.append("\"/>");
	 					row.addText(sb10.toString());

	 					StringBuffer sb11 = new StringBuffer();
	 					sb11.append("<input type=\"checkbox\"");
	 					sb11.append("name=\"presta2\"");
	 					if(detalle.getNomenclador().isCirugia() ){
	 						sb11.append("\" checked=\"checked");
	 					}
	 					sb11.append("id=\"");
	 					sb11.append("formu-ciru-"+   detalle.getNomenclador().getId_prestacion_string() +"|"+ detalle.getIdAux() );
	 			        sb11.append("\" value=\"");
	 					sb11.append(detalle.getNomenclador().getId_prestacion_string() +"|"+  detalle.getIdAux());									
	 					sb11.append("\"/>");
	 					
	 					row.addText(sb11.toString());
	 					
	 					row.addText(format0D.format(detalle.getCantidad()));
	 					row.addText(format2D.format(detalle.getImporte()));
	 					row.addText(format2D.format(detalle.getImporte() * detalle.getCantidad()));
	 					if ( esEdicion && detalle.getFechaBaja() == null){
	 						
	 						StringBuilder sb1= new StringBuilder();
		 					sb1.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/edit.png\" onClick=\"javascript:editarPreautorizacionCodigoNomenclador('");
		 					sb1.append(detalle.getId());
		 					sb1.append("','");
		 					sb1.append(detalle.getIdAux());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().getId_tipo_nomenclador());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().getId_prestacion());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().getCodigo());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().getDescripcion());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().getRequiereAutorizacion());
		 					sb1.append("','");
		 					sb1.append(detalle.getOpcionApoyo().getId());
		 					sb1.append("','");
		 					sb1.append(detalle.getCantidad());
		 					sb1.append("','");
		 					sb1.append(detalle.getImporte());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().isSupra());
		 					sb1.append("','");
		 					sb1.append(detalle.getNomenclador().isCirugia());
		 					sb1.append("');\" />");
		 					row.addText(sb1.toString());
	 						
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraPreautorizacionCodigoNomenclador('");
		 					sb.append(detalle.getIdAux());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
		 					
	 			 		} else if(detalle.getFechaBaja() != null ){
	 			 			
	 			 			row.addText("");
	 			 			
	 			 			StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/close.png\" />");
		 					row.addText(sb.toString());
		 					
	 			 		} else{
	 			 			row.addText("");
	 			 			row.addText("");
	 			 		}
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	
		
		
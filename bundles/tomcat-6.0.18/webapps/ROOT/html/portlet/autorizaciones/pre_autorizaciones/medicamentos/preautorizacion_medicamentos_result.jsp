<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<portlet:defineObjects/>
			<%
			
			PreAutorizacion preautorizacion=(PreAutorizacion)request.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			List<PreAutorizacionMedicamento>  detalles= preautorizacion.getMedicamentosPresentados();
	        
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
		

			PortletURL portletURLPreAutMed = renderResponse.createRenderURL();
	 		List<String> headerNamesPreAutMed = new ArrayList<String>();
//	 		headerNamesPreAutMed.add("Id ");
	 		headerNamesPreAutMed.add("Troquel");
	 		headerNamesPreAutMed.add("Nombre");
	 		headerNamesPreAutMed.add("Cantidad");
	 		headerNamesPreAutMed.add("Importe");
	 		headerNamesPreAutMed.add("Total");
	 		
			if( esEdicion) { 
				headerNamesPreAutMed.add("Editar");
				headerNamesPreAutMed.add("Borrar");
			}else{
				headerNamesPreAutMed.add("");
				headerNamesPreAutMed.add("");
			}
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLPreAutMed, headerNamesPreAutMed,
			LanguageUtil.get(pageContext, "no-medicamentos-were-found"));
		
			NumberFormat format2D = new DecimalFormat("#0.00");
			NumberFormat format0D = new DecimalFormat("#0");
			
			if(null!=detalles){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < detalles.size(); i++) {
 			 		PreAutorizacionMedicamento detalle = detalles.get(i);
	 					ResultRow row = new ResultRow(detalle, detalle.getMedicamento().getId_medicamento() , i);
//	 					row.addText(detalle.getNomenclador().getId_prestacion_string() );
	 					row.addText(String.valueOf(detalle.getMedicamento().getTroquel()) );
	 					row.addText(detalle.getMedicamento().getNombre() );
	 					row.addText(format0D.format(detalle.getCantidad()));
	 					row.addText(format2D.format(detalle.getImporte()));
	 					row.addText(format2D.format(detalle.getImporte() * detalle.getCantidad()));
	 					if ( esEdicion && detalle.getFechaBaja() == null){
	 						
	 						StringBuilder sb1= new StringBuilder();
		 					sb1.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/edit.png\" onClick=\"javascript:editarPreautorizacionMedicamento('");
		 					sb1.append(detalle.getId());
		 					sb1.append("','");
		 					sb1.append(detalle.getIdAux());
		 					sb1.append("','");
		 					sb1.append(detalle.getMedicamento().getId_medicamentoAsString());
		 					sb1.append("','");
		 					sb1.append(detalle.getMedicamento().getNombre());
		 					sb1.append("','");
		 					sb1.append(detalle.getMedicamento().getTroquel());
		 					sb1.append("','");
		 					sb1.append(detalle.getCantidad());
		 					sb1.append("','");
		 					sb1.append(detalle.getImporte());
		 					sb1.append("');\" />");
		 					row.addText(sb1.toString());
	 						
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraPreautorizacionMedicamento('");
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
	
		
		
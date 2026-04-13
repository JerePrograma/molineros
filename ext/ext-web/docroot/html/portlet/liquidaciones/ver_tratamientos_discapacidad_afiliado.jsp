<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

String cuil_titular = request.getParameter("cuil");
String inte = request.getParameter("inte");
String view = (String)request.getParameter("view");
							
int id_prestacion=ParamUtil.getInteger(request, "id_prestacion");
String cuit=ParamUtil.getString(request, "cuit_entidad", null);
String periodo = ParamUtil.getString(request, "periodo", null);
String codPrestaci = ParamUtil.getString(request, "codPrestaci", null);

				List<TratamientoDiscapacidad> tratamientosList = new ArrayList<TratamientoDiscapacidad> ();				
				tratamientosList = TratamientoDiscapacidadServiceUtil.buscarTratamientosDiscapacidad( null, null, null,
						0, Integer.valueOf(inte), cuil_titular, 0, 0, cuit, null, 0, 0, codPrestaci);									
								
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 		
		 		headerNames.add("afiliado");
		 		headerNames.add("inte");
		 		headerNames.add("Nro. Afiliado");
		 		headerNames.add("codigo");
		 		headerNames.add("prestacion");
		 		headerNames.add("cantidad");
		 		headerNames.add("importe");
		 		headerNames.add("periodicidad");
		 		headerNames.add("periodo-desde");
		 		headerNames.add("periodo-hasta");
		 		headerNames.add("estado");
				headerNames.add("Ver Detalle");
				
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-tratamientos-were-found"));
			    
							    
				if(null!=tratamientosList){
		 				//Seteo el total de la lista.
					 	int total = tratamientosList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < tratamientosList.size(); i++) {
					 		TratamientoDiscapacidad tratamiento = (TratamientoDiscapacidad) tratamientosList.get(i);
		 					ResultRow row = new ResultRow(tratamiento,tratamiento.getId_tratamiento(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);
			 							 							 				
			 				row.addText(tratamiento.getAfiliado().getApeNombre());
			 				row.addText(tratamiento.getAfiliado().getInteAsString());
			 				row.addText(String.valueOf(tratamiento.getAfiliado().getId_ospim()));
			 				row.addText(tratamiento.getPrestacion().getCodigo());
			 				row.addText(tratamiento.getPrestacion().getDescripcion());
			 				row.addText(tratamiento.getCantidad() != null ? tratamiento.getCantidad().toString() : "");
			 				row.addText(tratamiento.getImporte_total() != null ? tratamiento.getImporte_total().toString(): "");
			 				row.addText(tratamiento.getPeriodicidad() != null ? tratamiento.getPeriodicidad() : "");
			 				row.addText(tratamiento.getPeriodo_desde() != null ? tratamiento.getPeriodoDesdeString() : "");
			 				row.addText(tratamiento.getPeriodo_hasta() != null ? tratamiento.getPeriodoHastaString() : "");
			 				
			 				row.addText(tratamiento.getEstado() == 1 ? "En Curso" : tratamiento.getEstado() == 2 ?"Documentación Faltante": tratamiento.getEstado() == 3 ?
			 						"Cambio Prestador" : tratamiento.getEstado() == 4 ? "Finalizado" : tratamiento.getEstado() == 5 ? "Abandonado" : "");
			 				
		 					StringBuilder sb= new StringBuilder();
		 					if (tratamiento.getBaja_fecha() == null)  {
		 						sb.append("<a href='javascript:;' onClick=\"javascript:editarTratamiento('");			 								 				
		 						sb.append(tratamiento.getId_tratamientoString());
		 						sb.append("');\" >");
		 						sb.append("<img alt=\"Ver Detalle\" src=\"");
		 						sb.append(themeDisplay.getPathThemeImages());
		 						sb.append("/common/view.png\"");
		 						sb.append("/>");
		 						sb.append("</a>");		 						
		 					}
		 					else {
		 						sb.append("<a href='javascript:;' >");
			 					sb.append("<img alt=\"Baja\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/close.png\"");
			 					sb.append("/>");	
		 					}
		 					row.addText(sb.toString());
				 			resultRows.add(row);
					 	}
				}
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	<script type="text/javascript">
			
	</script>
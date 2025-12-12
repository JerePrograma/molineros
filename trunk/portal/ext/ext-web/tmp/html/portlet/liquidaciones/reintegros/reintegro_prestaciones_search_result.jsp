<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia"%><portlet:defineObjects/>

			<% 			
			
	    			
			 Logger _log = Logger.getLogger(this.getClass());
			
				String viewStr = (String)request.getAttribute(WebKeysLiquidaciones.VIEW_REINTEGRO);
				boolean esView = false;
				if (viewStr != null){
					esView = true;					
				}

				Reintegro reintegro = (Reintegro)request.getAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION);				
				ArrayList <ReintegroPrestacion> reintegroPrestaciones = (ArrayList<ReintegroPrestacion>)request.getSession().getAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_EN_EDICION);
				String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", "");
				if (tipo_reintegro.length() == 0) {
					tipo_reintegro = (String) request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION);
				}
				
				if (reintegro != null && reintegro.getDetalleCuota() != null) {
					ArrayList <DetalleCuota> detalleCuotas = (ArrayList<DetalleCuota>)reintegro.getDetalleCuota();
					DetalleCuota cuota1 = ReintegroServiceUtil.getDetalleCuota(detalleCuotas, 1);
							
					if ((detalleCuotas.size() == 1 || detalleCuotas.size() == 3) &&  
							(cuota1.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO)) {
						esView = false;
					}
				}
								
				PortletURL portletURLReintegroPrestacion = renderResponse.createRenderURL();
		 		List<String> headerNamesReintegroPrestacion = new ArrayList<String>();
		 		headerNamesReintegroPrestacion.add("cod-prest");
				headerNamesReintegroPrestacion.add("descripcion");
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
					headerNamesReintegroPrestacion.add("cuit");
				}
				headerNamesReintegroPrestacion.add("razon-social");
				headerNamesReintegroPrestacion.add("");
				if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					headerNamesReintegroPrestacion.add("comprobante");
				}
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
					headerNamesReintegroPrestacion.add("Pieza");
				}
				headerNamesReintegroPrestacion.add("price");
				if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					headerNamesReintegroPrestacion.add("cant");				
					headerNamesReintegroPrestacion.add("total");
				}
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					headerNamesReintegroPrestacion.add("Cubre");
				}
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)	) {
					headerNamesReintegroPrestacion.add("Cargo Ospim");
					headerNamesReintegroPrestacion.add("Cargo de Prestadora");
					headerNamesReintegroPrestacion.add("Cargo Monotributo");
					headerNamesReintegroPrestacion.add("periodo");
				}
				
				if ( tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)	) {
						headerNamesReintegroPrestacion.add("Cargo Ospim");
						headerNamesReintegroPrestacion.add("Cargo de Prestadora");
						headerNamesReintegroPrestacion.add("Cargo Monotributo");
				}
				
				//if (!esView){
					headerNamesReintegroPrestacion.add("action.EDIT");
				//}
			
				SearchContainer searchContainerReintegroPrestacion= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE, portletURLReintegroPrestacion, headerNamesReintegroPrestacion,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));
				
_log.error("Paso reintegroPrestaciones");

				if(null!=reintegroPrestaciones){
					int total=reintegroPrestaciones.size();
	 				searchContainerReintegroPrestacion.setTotal(total);
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsReintegroPrestacion = searchContainerReintegroPrestacion.getResultRows();

	 				headerNamesReintegroPrestacion.add("razon-social");
	 				if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
						headerNamesReintegroPrestacion.add("comprobante");
	 				}
					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
						headerNamesReintegroPrestacion.add("Pieza");
					}
					headerNamesReintegroPrestacion.add("price");
					if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
						headerNamesReintegroPrestacion.add("cant");				
						headerNamesReintegroPrestacion.add("total");
						headerNamesReintegroPrestacion.add("Cubre");
					}
					if (!esView){
						headerNamesReintegroPrestacion.add("action.DELETE");
					}
	 				
					String razonsocial;
					String reclamodata="";
					
	 			 	for (int i = 0; i < reintegroPrestaciones.size(); i++) {
//_log.error("Paso Lista Prestaciones");	 			 		
	 			 		ReintegroPrestacion reintegroPrestacion = null;
	 			 		
	 					
	 			 		if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 			 			reintegroPrestacion = (ReintegroPrestacionNormal)reintegroPrestaciones.get(i);
	 			 		} else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
	 			 			reintegroPrestacion = (ReintegroPrestacionOdoProtesis)reintegroPrestaciones.get(i);
	 			 		} else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
	 			 			reintegroPrestacion = (ReintegroPrestacionOdoOrtopediaOrtodoncia)reintegroPrestaciones.get(i);
	 			 		}
	 					ResultRow rowReintegroPrestacion = new ResultRow(reintegroPrestacion,reintegroPrestacion.getId_reintegroString()+reintegroPrestacion.getId_prestacionString()+reintegroPrestacion.getId_planString()+reintegroPrestacion.getAltaFechaAsString(), i);	 					
	 					rowReintegroPrestacion.addText(reintegroPrestacion.getPlan_prestacion().getNomenclador().getCodigo());
	 					reclamodata="";
	 					if (reintegroPrestacion.getId_reclamo_prestacional()>0){
	 						reclamodata=" (Reclamo: " + reintegroPrestacion.getId_reclamo_prestacional()  + ")";
	 					}
	 					
	 					rowReintegroPrestacion.addText(reintegroPrestacion.getPlan_prestacion().getNomenclador().getDescripcion()+reclamodata);
	 					
	 					
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getCuit() != null && reintegroPrestacion.getCuit().length() > 0 ? reintegroPrestacion.getCuit() : reintegroPrestacion.getCuit_entidad());
	 					}
	 					razonsocial="";
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 						//rowReintegroPrestacion.addText(reintegroPrestacion.getCuit() != null && reintegroPrestacion.getCuit().length() > 0 ? reintegroPrestacion.getDescripcion() : reintegroPrestacion.getRazon_social_entidad() );
	 						razonsocial=reintegroPrestacion.getCuit() != null && reintegroPrestacion.getCuit().length() > 0 ? reintegroPrestacion.getDescripcion() : reintegroPrestacion.getRazon_social_entidad() ;
	 					} else {
	 						//rowReintegroPrestacion.addText(reintegroPrestacion.getDescripcion());
	 						razonsocial=reintegroPrestacion.getDescripcion();
	 					}	 					
	 					
	 					if  ((razonsocial!=null && razonsocial!="") &&  (razonsocial.length()>15)){
	 						rowReintegroPrestacion.addText(razonsocial.substring(0,15) + "..." );
	 				 		StringBuilder sbo=new StringBuilder();
			 		    	sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"reintegro\" src=\"");
			 				sbo.append(themeDisplay.getPathThemeImages());
			 				sbo.append("/common/conversation.png\"  title='" + razonsocial +"'");
			 				sbo.append(" onClick=\"javascript:VtnaObs('");
				 			sbo.append(razonsocial);
					 		sbo.append("','Detalle de la Razon social');\" />");
					 		rowReintegroPrestacion.addText(sbo.toString());
	 					}
	 					else{
	 						rowReintegroPrestacion.addText(razonsocial);
	 						rowReintegroPrestacion.addText("");
	 							
	 					}
	 					
	 					if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
	 						rowReintegroPrestacion.addText(
	 								(reintegroPrestacion.getCompro_a_debitar_tipo()!=null?reintegroPrestacion.getCompro_a_debitar_tipo()+" ":"")+
	 								(reintegroPrestacion.getComproaDebitarLetra()!=null?reintegroPrestacion.getComproaDebitarLetra()+" ":"")+
	 								(reintegroPrestacion.getCompro_a_debitar_sucursal()!=null?reintegroPrestacion.getCompro_a_debitar_sucursal()+"-":"")+
	 								reintegroPrestacion.getCompro_a_debitar_numero());
	 					}

	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
	 						rowReintegroPrestacion.addText(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getPieza()) + " " + ((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCara());
	 					}
	 					rowReintegroPrestacion.addText(reintegroPrestacion.getImporte() != null ? reintegroPrestacion.getImporte().toString() : "0");
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 						rowReintegroPrestacion.addText(String.valueOf(((ReintegroPrestacionNormal)reintegroPrestacion).getCantidad()));
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteTotal() != null ? reintegroPrestacion.getImporteTotal().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteOspim() != null ? reintegroPrestacion.getImporteOspim().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImportePrestadora() != null ? reintegroPrestacion.getImportePrestadora().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteImesa() != null ? reintegroPrestacion.getImporteImesa().toString() : "0");
	 					}
	 					
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {	 						
	 						rowReintegroPrestacion.addText(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCantidad()));
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteTotal() != null ? reintegroPrestacion.getImporteTotal().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteOspim() != null ? reintegroPrestacion.getImporteOspim().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImportePrestadora() != null ? reintegroPrestacion.getImportePrestadora().toString() : "0");
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getImporteImesa() != null ? reintegroPrestacion.getImporteImesa().toString() : "0");
	 					}
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
	 						rowReintegroPrestacion.addText(reintegroPrestacion.getTercerizadoString());
	 					}
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 						rowReintegroPrestacion.addText(((ReintegroPrestacionNormal)reintegroPrestacion).getPeriodoAsString());
	 					}
	 					
	 					StringBuilder sb= new StringBuilder();
	 					
	 					sb.append("<img alt=\"<liferay-ui:message key='action.EDIT'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/edit.png\" onClick=\"javascript:editarReintegroPrestacion('");
	 					
	 					sb.append(reintegroPrestacion.getId_reintegroString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getId_prestacionString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getAltaFechaAsString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getId_planString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getFecha_prestacionAsString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getPlan_prestacion().getNomenclador().getCodigo());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getPlan_prestacion().getNomenclador().getDescripcion());
	 					sb.append("','");
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
	 						sb.append("");
	 						sb.append("','");
	 						sb.append("");
	 						sb.append("','");
	 						sb.append(String.valueOf(((ReintegroPrestacionNormal)reintegroPrestacion).getCantidad()));
	 						sb.append("','");
	 						sb.append("0");
	 						sb.append("','");
 							sb.append("");
 							sb.append("','");
 							sb.append(reintegroPrestacion.getTercerizado());
 							sb.append("','");
 		 					sb.append(((ReintegroPrestacionNormal)reintegroPrestacion).getPeriodoAsString());
 		 					sb.append("','");
	 					}
	 					else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
	 						sb.append(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getPieza())); 
	 						sb.append("','");
	 						sb.append(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCara());
	 						sb.append("','");
 							sb.append(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCantidad()));
 							sb.append("','");
 							sb.append(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getId_prestador_externo()));
 							sb.append("','");
 							sb.append("");
 							sb.append("','");
 							sb.append("");
 							sb.append("','");
 							sb.append("");
 							sb.append("','"); 						
	 					}
	 					else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
	 						sb.append("");
	 						sb.append("','");
	 						sb.append("");
	 						sb.append("','");
	 						sb.append("");
	 						sb.append("','");
 							sb.append(String.valueOf(((ReintegroPrestacionOdoOrtopediaOrtodoncia)reintegroPrestacion).getId_prestador_externo()));
 							sb.append("','");
 							sb.append(String.valueOf(((ReintegroPrestacionOdoOrtopediaOrtodoncia)reintegroPrestacion).getHonorarios().toString()));
 							sb.append("','");	 							
 							sb.append(reintegroPrestacion.getTercerizado());
 							sb.append("','");
 							sb.append("");
 							sb.append("','"); 						
	 					}
	 					sb.append(reintegroPrestacion.getImporte().toString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getCuit());
	 					sb.append("','");
	 					
	 					sb.append(reintegroPrestacion.getId_reclamo_prestacional() );
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getId_prestacion_reclamo() );
	 					sb.append("','");
	 					
	 					
	 					sb.append(reintegroPrestacion.getDescripcion());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getCompro_a_debitar_tipo());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getComproaDebitarLetra());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getCompro_a_debitar_sucursal());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getCompro_a_debitar_numero());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getCuit_entidad());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getSucursal_entidad());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getFecha_comprobanteAsString());
	 					sb.append("','");
	 					sb.append(reintegroPrestacion.getImporte_comprobante() != null ? reintegroPrestacion.getImporte_comprobante().toString() : "");	 					
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)
	 						|| tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)	) {
	 						sb.append("','");
	 						sb.append(reintegroPrestacion.getImporteOspim()!= null ? reintegroPrestacion.getImporteOspim().toString() : "");	 					
		 					sb.append("','");
		 					sb.append(reintegroPrestacion.getImportePrestadora() != null ? reintegroPrestacion.getImportePrestadora().toString() : "");
		 					sb.append("','");
		 					sb.append(reintegroPrestacion.getImporteImesa() != null ? reintegroPrestacion.getImporteImesa().toString() : "");
	 					}
	 					
	 					
	 					sb.append("');\" />");	 						 					
	 					if (!esView){
		 					
		 					if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
		 						sb.append(" | ");
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraReintegroPrestacion('");
			 					sb.append(reintegroPrestacion.getId_reintegroString());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getId_prestacionString());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getAltaFechaAsString());
			 					sb.append("','");
			 					
			 					sb.append(reintegroPrestacion.getId_reclamo_prestacional()   );
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getId_prestacion_reclamo()  );
			 					sb.append("','");
			 					
			 					sb.append(reintegroPrestacion.getId_planString());
			 					sb.append("','");		 					
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_tipo());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getComproaDebitarLetra());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_sucursal());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_numero());	 					
			 					sb.append("');\" />");			 					
		 					}
		 					
		 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
		 						sb.append(" | ");
			 					sb.append("<img alt=\"<liferay-ui:message key='action.COPY_EDIT'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/copy.png\" onClick=\"javascript:copiarReintegroPrestacion('");
			 					sb.append(reintegroPrestacion.getFecha_prestacionAsString());
			 					sb.append("','");
		 						sb.append(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCantidad()));
		 						sb.append("','");
	 							sb.append(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getId_prestador_externo()));
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_tipo());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getComproaDebitarLetra());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_sucursal());
			 					sb.append("','");
			 					sb.append(reintegroPrestacion.getCompro_a_debitar_numero());		 					
			 					sb.append("');\" />");
		 					}	 						
	 					}
	 					rowReintegroPrestacion.addText(sb.toString());
	 					
	 					resultRowsReintegroPrestacion.add(rowReintegroPrestacion);
	 			 	}
	 			}	
_log.error("Paso Lista Prestaciones 9999");						
 		%>
 		
 	<c:choose>
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerReintegroPrestacion%>" />

		

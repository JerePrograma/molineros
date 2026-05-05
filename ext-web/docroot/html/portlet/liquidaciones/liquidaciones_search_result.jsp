<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl" %>
<%@ page import="ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>



			<%
				//Si debe mostrarse el btn de agregar afiliado								
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
				String tipo_liquidacion = ParamUtil.getString(request, "tipo_liquidacion", WebKeysLiquidaciones.LIQUIDACION_PRE);
				
			    int liquidacionPrestador = 0;			    
			    	liquidacionPrestador =  ((Integer)(renderRequest.getAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION) != null ?  renderRequest.getAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION) : 0)).intValue();
			    	if (liquidacionPrestador == 0) {
			    		liquidacionPrestador = ((Integer) portletSession.getAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION, PortletSession.PORTLET_SCOPE)  != null ?  (Integer) portletSession.getAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION, PortletSession.PORTLET_SCOPE) : new Integer(0)).intValue();
			    	}			    
				
				List<Liquidacion> liquidacionesList= new ArrayList<Liquidacion> ();
				List<LiquidacionPrestacionOdo> liquidacionesOdoList= new ArrayList<LiquidacionPrestacionOdo> ();
				if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE)) {
					liquidacionesList= (ArrayList<Liquidacion>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);
					if (liquidacionesList == null || liquidacionesList.size() == 0) {
						liquidacionesList = (ArrayList<Liquidacion>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, PortletSession.PORTLET_SCOPE);
					}
				}
				if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO)) {
					liquidacionesOdoList= (ArrayList<LiquidacionPrestacionOdo>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);					
					if (liquidacionesOdoList == null || liquidacionesOdoList.size() == 0) {
						liquidacionesOdoList = (ArrayList<LiquidacionPrestacionOdo>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, PortletSession.PORTLET_SCOPE);
					}					
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("numero");
		 		headerNames.add("fecha");
		 		headerNames.add("periodo");
				if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE)) {
		 			headerNames.add("cod-prest");
		 			headerNames.add("prestador");
		 			headerNames.add("tipo");
		 			headerNames.add("letra");
		 			headerNames.add("pto-venta");
				}
		 		if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO)) {
		 			headerNames.add("numero-afi");
		 			headerNames.add("inte");
		 			headerNames.add("afiliado");
		 			headerNames.add("seccional");
				}			
		 		headerNames.add("numero-texto");
		 		headerNames.add("importe");
		 		headerNames.add("orden-de-pago-num-cheque-fecha");
				if(showABMButtons) {
					headerNames.add("editar-borrar");
				}
				if(liquidacionPrestador != 0) {
					headerNames.add("a-pagar");
				}
				
				headerNames.add("Imagen");
				
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-liquidaciones-were-found"));
				
				if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE)) {
					if(null!=liquidacionesList){
		 				//Seteo el total de la lista.
					 	int total = liquidacionesList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < liquidacionesList.size(); i++) {
					 		Liquidacion liquidacion = (Liquidacion) liquidacionesList.get(i);
					 		
					 		List<DLFileEntryImpl>imagenes = EditarLiquidacionServiceUtil.getImagenes(liquidacion);
					 		liquidacion.setImagenes(imagenes);
					 		
		 					ResultRow row = new ResultRow(liquidacion,liquidacion.getId_liquidacionString(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);
			 				rowURL.setParameter("struts_action","/liquidaciones/view_liquidaciones_entry");
			 				rowURL.setParameter("id_liquidacion", liquidacion.getId_liquidacionString());
			 				row.addText(liquidacion.getId_liquidacionString(),rowURL);
			 				row.addText(liquidacion.getFechaAsString(),rowURL);
			 				row.addText(liquidacion.getPeriodoString(),rowURL);			 							 		
			 				row.addText(String.valueOf(liquidacion.getPrestador_lugar_atencion().getPrestador().getId_prestador()),rowURL);
			 				row.addText(liquidacion.getPrestador_lugar_atencion().getPrestador().getDescripcion(),rowURL);
			 				row.addText(String.valueOf(liquidacion.getCompro_a_debitar_tipo()), rowURL);
							row.addText(String.valueOf(liquidacion.getCompro_a_debitar_letra()), rowURL);
							row.addText(String.valueOf(liquidacion.getSucu()), rowURL);
							row.addText(String.valueOf(liquidacion.getCompro_a_debitar_numero()), rowURL);
							row.addText(liquidacion.getImporte() != null ? liquidacion.getImporte().toString() : "0" ,rowURL);
							row.addText(liquidacion.getOPLiquidacion(),rowURL);
							// Action
							if(showABMButtons && liquidacion.getEstado() < WebKeysLiquidaciones.LIQUIDACION_ESTADO_LIQUIDADO  ) {
								row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/editar_borrar_liquidaciones.jsp");
							} else if (showABMButtons && liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_LIQUIDADO) {
								row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/editar_borrar_liquidaciones_pagas.jsp");
							} else if (showABMButtons && liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CIERRE_PERIODO_CONTABLE && liquidacion.getEstado() <  WebKeysLiquidaciones.LIQUIDACION_ESTADO_CERRADO ) {
								row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/editar_borrar_liquidaciones.jsp");
							} else {
								if (Validator.isNotNull(liquidacion.getBaja_fecha())) {
									row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/editar_borrar_liquidaciones.jsp");
								} else {
									row.addText("",rowURL);
								}
							}
							if (liquidacionPrestador != 0) {
							StringBuffer sb = new StringBuffer();
							// validar si no está liquidado
								if (((liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CERRADO)
									|| (liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CIERRE_PERIODO_CONTABLE && liquidacion.isOp_baja_existente())
									|| (liquidacion.getEstado() == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CIERRE_PERIODO_CONTABLE && !liquidacion.isOp_baja_existente() && liquidacion.getIdOP() == 0)
								)
								 && Validator.isNull(liquidacion.getBaja_fecha())) {
																											
									sb.append("<input type=\"checkbox\"");
									sb.append("name=\"");
									sb.append("pagarLiqui"+liquidacion.getId_liquidacion());
									sb.append("\" id=\"");
									sb.append("pagarLiqui"+liquidacion.getId_liquidacion());
									sb.append("\" value=\"");
									sb.append(liquidacion.getId_liquidacionString());									
									sb.append("\"/>");
									
								} else {
									sb.append ("");
								}
								row.addText(sb.toString());
							}
							
//Imagenes
							StringBuilder sbImg= new StringBuilder();
	 						sbImg.append("");
	 						if(liquidacion.getImagenes()!=null && liquidacion.getImagenes().size()>0){
	 						    sbImg.append("<img alt=\"Ver Imagen\" src=\"");
	 							sbImg.append(themeDisplay.getPathThemeImages());
	 							sbImg.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
	 							sbImg.append(String.valueOf(liquidacion.getImagenes().get(0).getFolderId()));
	 							sbImg.append("','");
	 							sbImg.append(liquidacion.getImagenes().get(0).getName());
	 							sbImg.append("');\"");
	 							sbImg.append(" title=\"Imagenes\"");
		 						sbImg.append("/>");
	 						}  
	 			 		    row.addText(sbImg.toString());
//Fin Imagenes
							
							resultRows.add(row);
					 	}
		 			}
				}			
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	
			
	<c:if test="<%= liquidacionesList != null && liquidacionesList.size() > 0 %>">
		<table>
			<tr>
				<td align="left"><input type="button" value="<liferay-ui:message key="reporte-excel" />" onClick="<portlet:namespace />generarReporte();" /></td>
				<td align="left"><input type="button" value="<liferay-ui:message key="emitir-op" />" onClick="<portlet:namespace />emitirOP();" /></td>				
			</tr>
		</table>
	</c:if>	
	
	<script type="text/javascript">
	
	
		function validarSeleccionLiquidacion() {
				valido=false;
				form1 = document.<portlet:namespace />fm;
				var obj;								
				for(a=0;a<form1.elements.length;a++){
					obj = form1.elements[a];
					if(obj.type=="checkbox" && obj.checked==true){
						valido=true;
						break;
					}
				}
				if(!valido){
					return false; 
				}
			return true;
		}

		function <portlet:namespace />emitirOP() {
			if (validarSeleccionLiquidacion()) {
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_orden_pago_from_liquidacion" /></portlet:actionURL>';
				document.<portlet:namespace />fm.method = 'post';
				submitForm(document.<portlet:namespace />fm, url);
			} else {
				alert ('No es posible Emitir OP');
			}					
		}

		function <portlet:namespace />generarReporte(){
			var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();		
			var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();		
			var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
			var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
			var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
			var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
			var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
			var periodoHastaMesAnio=jQuery('#<portlet:namespace />periodoHastaMesAnio').val();
			var estado=jQuery('#<portlet:namespace />estado').val();			
			var entidad="O.S.P.I.M.";
			var codPrestad=jQuery('#<portlet:namespace />codPrest').val();
			var id_prestador=jQuery('#<portlet:namespace />id_prestador').val();
			var cuit=jQuery('#<portlet:namespace />cuit_prestador').val();
			var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();
			var comprobante_tipo=jQuery('#<portlet:namespace />comprobante_tipo').val();
			var comprobante_letra=jQuery('#<portlet:namespace />comprobante_letra').val();
			var sucu=jQuery('#<portlet:namespace />sucu').val();			
			var comprobante_nro=jQuery('#<portlet:namespace />comprobante_nro').val();
			var numero=jQuery('#<portlet:namespace />numero').val();
			var nroOC=jQuery('#<portlet:namespace />nro_oc').val();
			var sector=jQuery('#<portlet:namespace />sector_id').val();
			//jQuery('#<portlet:namespace />buscando').show();
			//Si la seccional no fue obtenida la borro:
			if(jQuery("#<portlet:namespace />prest_seleccionada").val()!="1"){
				jQuery("#<portlet:namespace />nombre_prestador").val("");
				jQuery("#<portlet:namespace />id_prestador").val("");
				jQuery("#<portlet:namespace />cuit_prestador").val("");
			}
			window.location.href ='/xlsservlet/?reporte=REPORTE_LIQUIDACIONES&entidad='+entidad+
				'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
				'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
				'&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&periodoHastaMesAnio='+periodoHastaMesAnio+'&codPrest='+codPrestad+
				'&comprobante_tipo='+comprobante_tipo+'&comprobante_letra='+comprobante_letra+'&sucu='+sucu+'&comprobante_nro='+comprobante_nro+
				'&cuit='+cuit+'&id_prestador='+id_prestador+'&prestador='+encodeURI(prestador)+'&numero='+numero+'&estado='+estado+'&nro_oc='+encodeURI(nroOC)+
				'&sector='+sector;
			
		}
		
		function verImagenComprobante(folderId,fileName){
			   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			   '<liferay-portlet:param name="struts_action" value="/liquidaciones/documentacion_adjunta_recuperar"/>'+
			   '<liferay-portlet:param name="name" value="__Name"/>'+
			   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
			   '</liferay-portlet:actionURL>';      
			   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
			   var izq = screen.width-800;
			   var conf ='width=800,height=800,toolbar=no,resizable=yes,left=screen.width -1000,top=100'.replace('screen.width',izq);
			   //'width=800,height=800,toolbar=no,resizable=yes,left=screen.width -1000,top=100';
			    window.open(url,fileName,conf); 
		}
	</script>
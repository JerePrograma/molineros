<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacion"%><portlet:defineObjects/>
			<%									
			    int seccionalReintegro = 0;			    
		    	seccionalReintegro = ((Integer)(renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL) != null ?  renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL) : 0)).intValue();
		    	if (seccionalReintegro == 0) {
		    		seccionalReintegro = ((Integer) portletSession.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE) != null ? (Integer) portletSession.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE) : new Integer(0)).intValue();
		    	}			    	

				String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", null);
			 	if (tipo_reintegro == null) {
			 	 	tipo_reintegro = (String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) != null && ((String)(request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION))).length() > 0 ? 
			 	 			(String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) : WebKeysLiquidaciones.REINTEGRO_PRE;
		 	 	}

				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
				boolean showABMAuditoria = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);
			
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
					showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);
				}
				
				List<Reintegro> reintegrosList = new ArrayList<Reintegro> ();
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
					reintegrosList= (ArrayList<Reintegro>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
					if (reintegrosList == null || reintegrosList.size() == 0) {
						reintegrosList = (ArrayList<Reintegro>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
						if (reintegrosList != null && reintegrosList.size() > 0 && !reintegrosList.get(0).getTipo_reintegro().equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
							portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
							reintegrosList = new ArrayList<Reintegro> ();
						}
					}
				}
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
					reintegrosList= (ArrayList<Reintegro>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
					if (reintegrosList == null || reintegrosList.size() == 0) {
						reintegrosList = (ArrayList<Reintegro>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
						if (reintegrosList != null && reintegrosList.size() > 0 && !reintegrosList.get(0).getTipo_reintegro().equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
							portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
							reintegrosList = new ArrayList<Reintegro> ();
						}
					}
				}
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					reintegrosList= (ArrayList<Reintegro>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
					if (reintegrosList == null || reintegrosList.size() == 0) {
						reintegrosList = (ArrayList<Reintegro>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
						if (reintegrosList != null && reintegrosList.size() > 0 && !reintegrosList.get(0).getTipo_reintegro().equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
							portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
							reintegrosList = new ArrayList<Reintegro> ();
						}
					}
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("fecha");
		 		headerNames.add("seccional");
		 		if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
		 			headerNames.add("cuil-titular");	
		 		}else {
		 			headerNames.add("Nro. Afiliado");
		 		}
	 			headerNames.add("inte");
		 		headerNames.add("numero");
		 		headerNames.add("importe");
				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					headerNames.add("N° Cuota");
					headerNames.add("Cod. NN");
				}
				headerNames.add("lista-orden-de-pago-num-cheque-fecha");
				headerNames.add(tipo_reintegro.equals(WebKeysLiquidaciones.REINTEGRO_PRE) ? "Ver Detalle (Periodo/Código/Factura)" : "Ver Detalle (Fecha Prestación/Código/Factura)");
				if(showABMButtons || showABMAuditoria) {
					headerNames.add("editar-borrar");
				}				
				if(seccionalReintegro != 0) {
					headerNames.add("incluir-en-op");
				}
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-reintegros-were-found"));
			    boolean hayReintegrosPagables = false;
							    
				if(null!=reintegrosList){
		 				//Seteo el total de la lista.
					 	int total = reintegrosList.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < reintegrosList.size(); i++) {
					 		Reintegro reintegro = (Reintegro) reintegrosList.get(i);
		 					ResultRow row = new ResultRow(reintegro,reintegro.getId_reintegro(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);
			 				rowURL.setParameter("struts_action","/liquidaciones/view_reintegro_entry");
			 				rowURL.setParameter("id_reintegro", reintegro.getId_reintegroString());
			 				rowURL.setParameter("tipo_reintegro",tipo_reintegro);
			 				row.addText(reintegro.getFechaAsString(),rowURL);
			 				row.addText(reintegro.getAfiliado().getSeccional().getDescripcion(),rowURL);     
			 				if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			 					row.addText(String.valueOf(reintegro.getAfiliado().getId_ospim()),rowURL);			 					
			 				}
			 				else if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			 					row.addText(reintegro.getAfiliado().getCuil_titular(),rowURL);
			 				}
			 				row.addText(reintegro.getAfiliado().getInteAsString(),rowURL);										 				
							if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
								row.addText(String.valueOf(reintegro.getId_reintegro_userString()), rowURL);
								row.addText(reintegro.getImporteTotal() != null ? reintegro.getImporteTotal().toString() : "",rowURL);
							}
							if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
								row.addText(reintegro.getDetalleCuota().size() > 0 ? String.valueOf(reintegro.getDetalleCuota().get(0).getId_reintegro_userString()) : "", rowURL);
								row.addText(reintegro.getDetalleCuota().size() > 0 ? reintegro.getDetalleCuota().get(0).getImporte() != null ? reintegro.getDetalleCuota().get(0).getImporte().toString() : "" : "", rowURL);
								row.addText(reintegro.getDetalleCuota().size() > 0 ? String.valueOf(reintegro.getDetalleCuota().get(0).getNro_cuota()) : "", rowURL);
								row.addText(String.valueOf(reintegro.getReintegroPrestacion().get(0).getCodigo() == null ? "" : reintegro.getReintegroPrestacion().get(0).getCodigo()), rowURL);
							}							
							row.addText(reintegro.getOPReintegro(),rowURL);
							row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/reintegros/ver_detalle_reintegro.jsp");
							// Action
							if(showABMButtons || showABMAuditoria) {
								row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/reintegros/editar_borrar_reintegro.jsp");
							}
							if(seccionalReintegro != 0) {
								StringBuffer sb = new StringBuffer();
								// validar si no está liquidado
								if (!reintegro.estaLiquidado() && Validator.isNull(reintegro.getBaja_fecha())) {
									if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE) || (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) && reintegro.getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO)) {									
										sb.append("<input type=\"checkbox\"");
										sb.append("name=\"");
										sb.append("pagarRein"+reintegro.getId_reintegroString());
										sb.append("\" id=\"");
										sb.append("pagarRein"+reintegro.getId_reintegroString());
										sb.append("\" checked='checked' value=\"");
										sb.append(reintegro.getImporteTotal() != null ? reintegro.getImporteTotal().toString() : "");									
										sb.append("\"/>");
										hayReintegrosPagables = true;
									}
									if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA) && (reintegro.getDetalleCuota().size() > 0 && reintegro.getDetalleCuota().get(0).getEstado() == WebKeysLiquidaciones.REINTEGRO_ESTADO_AUDITADO)) {										
										sb.append("<input type=\"checkbox\"");
										sb.append("name=\"");
										sb.append("pagarRein"+reintegro.getDetalleCuota().get(0).getId_reintegro_userString());
										sb.append("\" id=\"");										
										sb.append("pagarRein"+reintegro.getDetalleCuota().get(0).getId_reintegro_userString());
										sb.append("\" checked='checked' value=\"");
										sb.append(reintegro.getDetalleCuota().get(0).getImporte().toString() != null ? reintegro.getDetalleCuota().get(0).getImporte().toString() : "");									
										sb.append("\"/>");										
										hayReintegrosPagables = true;										
									}
								}
								else {
									sb.append ("");
								}
								row.addText(sb.toString());								
							} else {
								row.addText("");		
							}
				 			resultRows.add(row);
					 	}
		 			}
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

	<table>
		<tr>
	<c:if test="<%= reintegrosList != null && reintegrosList.size() > 0 %>">
			<td align="left"><input type="button" value="<liferay-ui:message key="reporte-reintegros-excel" />" onClick="<portlet:namespace />generarReporteReintegros();" /></td>						
	</c:if>
	<c:if test="<%= seccionalReintegro != 0 && hayReintegrosPagables %>">		
			<td align="left"><input type="button" value="<liferay-ui:message key="crear-lista-op" />" onClick="<portlet:namespace />emitirOP();" /></td>											
	</c:if>
		</tr>
	</table>
	<input type="hidden" id="<portlet:namespace />seccional_op"  name="<portlet:namespace />seccional_op" value="<%= seccionalReintegro %>" />
	<script type="text/javascript">
			
			function validarSeleccionReintegros() {
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
				if (validarSeleccionReintegros()) {
					var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/crear_lista_para_pago_reintegros" /></portlet:actionURL>';
					document.<portlet:namespace />fm.method = 'post';
					<portlet:namespace />limpiarCamposAfiliado();
					submitForm(document.<portlet:namespace />fm, url);
				}
				else {
					alert ('No es posible generar la lista');
				}
			}

			function <portlet:namespace />generarReporteReintegros(){
					var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/generar_reporte_reintegros" /></portlet:actionURL>';
					document.<portlet:namespace />fm.method = 'post';
					<portlet:namespace />limpiarCamposAfiliado();
					submitForm(document.<portlet:namespace />fm, url);
			}
	</script>
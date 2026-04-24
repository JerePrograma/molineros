<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
					boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
			
					List<Comprobante> comprobantes = (ArrayList<Comprobante>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("pto-venta");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("sucursal");
			 		headerNames.add("numero");
					headerNames.add("cuit-emisor");
					headerNames.add("cuit-acreedor");
					headerNames.add("importe");
					headerNames.add("fecha-emision");
					headerNames.add("fecha-recibido");
					headerNames.add("editar-borrar");
					headerNames.add("");
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=comprobantes){
					 	int total = comprobantes.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		Comprobante comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
			 				rowURL.setParameter("struts_action","/farmacia/view_comprobante_amtima_entry");
			 				rowURL.setParameter("pto_venta", String.valueOf(comp.getPtoVenta()));
			 				rowURL.setParameter("tipo_comprobante", comp.getTipoComprobante());
			 				rowURL.setParameter("letra", comp.getLetraComprobante());
			 				rowURL.setParameter("sucursal", String.valueOf(comp.getSucuComprobante()));
			 				rowURL.setParameter("nro_comprobante", comp.getNroComprobante());
			 				rowURL.setParameter("cuit_compr_emisor", comp.getCuit());
			 				rowURL.setParameter("esAmtima", "esAmtima");
			 				rowURL.setParameter("VIEW", "VIEW");
			 				row.addText(String.valueOf(comp.getPtoVenta()), rowURL);
			 				row.addText( comp.getTipoComprobante(), rowURL);
			 				row.addText(comp.getLetraComprobante(), rowURL);
			 				row.addText( String.valueOf(comp.getSucuComprobante()), rowURL);
			 				row.addText( comp.getNroComprobante(), rowURL);
			 				row.addText( comp.getCuit(), rowURL);
			 				row.addText( comp.getAcreedorEmpresa().getCuit(), rowURL);
			 				row.addText( comp.getImporteComprobante().toString(), rowURL);
			 				row.addText( comp.getFechaEmisionAsString(), rowURL);
			 				row.addText( comp.getFechaRecepcionAsString(), rowURL);
			 				if (comp.getAnulacion_fecha() != null){
			 					row.addText("Anulado el " + comp.getAnulacion_fechaAsString());
			 					row.addText("");
			 				} else if (comp.isPagado()){
			 					row.addText("Pagado");
			 					row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/farmacia/comprobantes/editar_borrar_comprobante_amtima.jsp");
			 				} else if(!soloVer) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/farmacia/comprobantes/editar_borrar_comprobante_amtima.jsp");
								row.addText("");
			 				} else if(soloVer) {
			 					row.addText("Impago");
			 					row.addText("");
			 				}
					 		resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

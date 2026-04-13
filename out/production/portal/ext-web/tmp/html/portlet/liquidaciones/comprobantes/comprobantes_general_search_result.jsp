<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<portlet:defineObjects/>
			<%
			        NumberFormat f2D = new DecimalFormat("#0.00");
			        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String portlet_name=null;
					boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
					if (portlet_name == null || portlet_name.trim().equals("")){
						portlet_name = "liquidaciones";
					}
					if(renderResponse.getNamespace().equals("_UOM_1_")){
						portlet_name = "uoma";
					}	 		
					List<Comprobante> comprobantes = (ArrayList<Comprobante>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_GLOBALES);
					renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_GLOBALES, comprobantes);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cuit-emisor");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("pto-venta");
			 		headerNames.add("numero");
			 		headerNames.add("fecha");
			 		headerNames.add("importe");
			 		headerNames.add("Origen");
			 		headerNames.add("Reclamo");
			 		headerNames.add("Reintegro");
			 		headerNames.add("R.Tipo");
			 		headerNames.add("Liquidación");
			 		headerNames.add("Orden de Pago");
			 		headerNames.add("Pagado");
			 		headerNames.add("Transferencia");
					
//					headerNames.add("Editar");
					
					
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

                            row.addText( comp.getAcreedorEmpresa().getCuit()!=null?comp.getAcreedorEmpresa().getCuit():"");
                            row.addText( comp.getTipoComprobante()!=null?comp.getTipoComprobante():"");
                            row.addText( comp.getLetraComprobante()!=null?comp.getLetraComprobante():"");
                            row.addText( String.valueOf(comp.getPtoVenta()));    
                        	row.addText( comp.getNroComprobante()!=null?comp.getNroComprobante():"");   
                        	
                        	row.addText( comp.getFechaEmision()!=null?comp.getFechaEmisionAsString():"");   
                        	
                        	row.addText( f2D.format(comp.getImporteComprobante()));  
                        	row.addText( comp.getOrigen());
                        	row.addText( comp.getReclamoId()!=null?String.valueOf(comp.getReclamoId()):"");
                        	row.addText( comp.getReintegroId()!=null? String.valueOf(comp.getReintegroId()):"");
                        	row.addText( comp.getReintegroTipo()!=null?comp.getReintegroTipo():"");
                        	row.addText( comp.getLiquidacionId()!=null?String.valueOf(comp.getLiquidacionId()):"");
                        	row.addText( comp.getOrdenPagoId()!=null?String.valueOf(comp.getOrdenPagoId()):"");
                        	row.addText( comp.getImportePagado()!=null?f2D.format(comp.getImportePagado()):"");
                        	row.addText( comp.getFechaPrimerPago()!=null?sdf.format(comp.getFechaPrimerPago()):"");   

                        	
/*                        	
                        	StringBuilder sb=new StringBuilder();
                			sb.append("&nbsp;&nbsp;<img alt=\"Editar Comprobante\" src=\"");
                		    sb.append(themeDisplay.getPathThemeImages());
                 		    sb.append("/common/edit.png\" onClick=\"javascript:editarComprobanteGeneral('");
                 		    sb.append(comp.getAcreedorEmpresa().getCuit());
                 		    sb.append("','");
                 		    sb.append(comp.getTipoComprobante());
                		    sb.append("','");
                		    sb.append(comp.getLetraComprobante());
                		    sb.append("','");
                		    sb.append( String.valueOf(comp.getPtoVenta()));
                		    sb.append("','");
                		    sb.append(comp.getNroComprobante());
                		    sb.append("'");
                 		    sb.append(");\"");
                            sb.append(" title=\"Editar\"");
                 		    sb.append("/>");
                 		    row.addText(sb.toString()); 
*/                        	
	
					 		resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />	
	<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/paginador_comprobantes_consulta_global.jsp">
    </liferay-util:include>		

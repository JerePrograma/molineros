<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<portlet:defineObjects/>
			<%
			NumberFormat nf = new DecimalFormat("#0.00");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				List<IntegracionDetalleDR> list = new ArrayList<IntegracionDetalleDR>();
				list= (ArrayList<IntegracionDetalleDR>)renderRequest.getAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO );
				if (list == null || list.size() == 0) {
					list = (ArrayList<IntegracionDetalleDR>) portletSession.getAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO , PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Status");
		 		headerNames.add("Clave");
		 		headerNames.add("Tipo");
		 		headerNames.add("Período Presentación");
		 		headerNames.add("Período Prestación");
		 		headerNames.add("CUIL");		 		
		 		headerNames.add("Código Práctica");
		 		headerNames.add("Importe Subsidiado");
		 		headerNames.add("Importe Solicitado");		 		
		 		headerNames.add("Nro AFI");
		 		headerNames.add("CUIT CBU");
		 		headerNames.add("Cpbte Nro");
//		 		headerNames.add("CBU");
		 		headerNames.add("Orden Pago I");
//		 		headerNames.add("Orden Pago II");
		 		headerNames.add("Transf I");
/*		 		
		 		headerNames.add("Transf II");
		 		headerNames.add("Cheque");
		 		headerNames.add("Importe Transf");
		 		headerNames.add("Ret.Ganancias");
		 		headerNames.add("Ret.IIBB");
		 		headerNames.add("Otras Retenciones");
		 		headerNames.add("Importe Aplicado");
		 		headerNames.add("Fdos.Propios Disc.");
		 		headerNames.add("Fdos.Propios Otros");
		 		headerNames.add("Recibo");
		 		headerNames.add("Importe Trasladado");
		 		headerNames.add("Importe Devuelto");
		 		headerNames.add("Saldo No Aplicado");
		 		headerNames.add("Recupero");
		 		headerNames.add("Observaciones");
*/		 		
		 		String vereditarborrar = "Editar";		 		
				headerNames.add(vereditarborrar);
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-detalle-were-found"));
			    
			    			     
				if(null!=list){
		 				//Seteo el total de la lista.
					 	int total = list.size();
					 	searchContainer.setTotal(total);
					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < list.size(); i++) {
					 		IntegracionDetalleDR detalle  = (IntegracionDetalleDR) list.get(i);
		 					ResultRow row = new ResultRow(detalle,detalle.getId() , i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
			 							 				
                            String sb = "";
                            
                            if(detalle.isConProblema()){
                            	sb = "<span id=lb_'"+ detalle.getId()+"' style='background-color:#F1948A; font-weight:bold; font-size:20px'>" + (detalle.getError()!=null?" " + detalle.getError() +" ":"_____")   +"</span>";
                            }else{
                            	sb = "<span id=lb_'"+ detalle.getId()+"' style='background-color:#ABEBC6; font-weight:bold; font-size:20px'>" +  "_____"   +"</span>";
                            }
                            
                            row.addText(sb);
                            row.addText(detalle.getClave());
                            row.addText(detalle.getTipoArchivo());
                            row.addText(detalle.getPeriodoPresentacion().toString());
                            row.addText(detalle.getPeriodoPrestacion().toString());
                            row.addText(detalle.getCuil());
                            row.addText(detalle.getPrestacionCodigo().toString());
                            row.addText(nf.format(detalle.getImporteLiquidado().doubleValue()));
                            row.addText(nf.format(detalle.getImporteSolicitado().doubleValue()));
                            row.addText(detalle.getNroEnvioAfip().toString());
					 		row.addText(detalle.getCbuCuit() !=null?detalle.getCbuCuit():"");
					 		row.addText(detalle.getComprobanteNro() !=null?detalle.getComprobanteNro().toString():"");
//					 		row.addText(detalle.getCbu()!=null?detalle.getCbu():"");
					 		row.addText(detalle.getOrdenPagoI()!=null?detalle.getOrdenPagoI().toString():"");
//					 		row.addText(detalle.getOrdenPagoII()!=null?detalle.getOrdenPagoII().toString():"");
					 		row.addText(detalle.getFechaTransferenciaI()!=null?sdf.format(detalle.getFechaTransferenciaI()):"");
/*					 		
					 		row.addText(detalle.getFechaTransferenciaII()!=null?sdf.format(detalle.getFechaTransferenciaII()):"");
					 		row.addText(detalle.getCheque()!=null?detalle.getCheque():"");
					 		row.addText(nf.format(detalle.getImporteTransferido().doubleValue()));
					 		row.addText(nf.format(detalle.getRetencionGanancias().doubleValue()));
					 		row.addText(nf.format(detalle.getRetencionIIBB().doubleValue()));
					 		row.addText(nf.format(detalle.getOtrasRetenciones().doubleValue()));
					 		row.addText(nf.format(detalle.getImporteAplicado().doubleValue()));
					 		row.addText(nf.format(detalle.getFondosPropiosDiscapacidad().doubleValue()));
					 		row.addText(nf.format(detalle.getFondosPropiosOtraCuenta().doubleValue()));
					 		row.addText(detalle.getNroRecibo()!=null?detalle.getNroRecibo().toString():"");
					 		row.addText(nf.format(detalle.getImporteTrasladado().doubleValue()));
					 		row.addText(nf.format(detalle.getImporteDevuelto().doubleValue()));
					 		row.addText(nf.format(detalle.getSaldoNoAplicado().doubleValue()));
					 		row.addText(nf.format(detalle.getRecuperoFondosPropios().doubleValue()));
					 		row.addText(detalle.getObservaciones()!=null?detalle.getObservaciones():""); 
*/					 		

					 		StringBuilder sb1=new StringBuilder();
							sb1.append("&nbsp;&nbsp;<img alt=\"Editar Registro\" src=\"");
						    sb1.append(themeDisplay.getPathThemeImages());
				 		    sb1.append("/common/edit.png\" onClick=\"javascript:editarRegistroDR(");
				 		    sb1.append(detalle.getId() );
				 		    sb1.append(");\"");
				            sb1.append(" title=\"Editar\"");
				 		    sb1.append("/>");
				 		    row.addText(sb1.toString());   
							
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"  />
	
	<liferay-util:include page="/html/portlet/autorizaciones/integracion/paginador_integracion_devolucion_gestion.jsp">
    </liferay-util:include>	
	<script type="text/javascript">
	
	
	</script>
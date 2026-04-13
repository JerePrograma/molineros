<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
			boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP);
	 		boolean rolVER = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_VER_OP);
	 		boolean rolABMPagos = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP_PAGOS);
	 		
	 		if (rolABM) {
	 			rolVER = true;
	 		}
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
			
				List<OrdenPagoOspim> ordenes = (ArrayList<OrdenPagoOspim>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_ORDENES_PAGO);
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("nro");
		 		headerNames.add("importe");
		 		headerNames.add("fecha");
		 		headerNames.add("baja-fecha");
		 		if (rolABM  || rolABMPagos){
		 			headerNames.add("");
		 		}
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-ordenes-pago-were-found"));			
			
				if(null!=ordenes){
	 				//Seteo el total de la lista.
				 	int total = ordenes.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < ordenes.size(); i++) {
				 		OrdenPagoOspim op = (OrdenPagoOspim) ordenes.get(i);
				 		PortletURL rowURL = renderResponse.createRenderURL();		 		
				 		rowURL.setWindowState(WindowState.MAXIMIZED);		 				
						rowURL.setParameter("struts_action","/liquidaciones/view_orden_pago_ospim_entry");
						rowURL.setParameter("orden_pago_id", String.valueOf(op.getId()));
						
						if (showABMButtons){
		 					ResultRow row = new ResultRow(op,op.getId(), i);
		 					/*if(op.isFarmacia()){
		 						StringBuilder sb = new StringBuilder();
								sb.append("<a href='javascript:imprimirOPFarmacia(\"");
								sb.append(op.getId().toString());										
								sb.append("\")'>");			
								sb.append(op.getId().toString());
								sb.append("</a>");				
								row.addText(sb.toString());
								StringBuilder sb1 = new StringBuilder();
								sb1.append("<a href='javascript:imprimirOPFarmacia(\"");
								sb1.append(op.getId().toString());										
								sb1.append("\")'>");			
								sb1.append(op.getImporte().toString());
								sb1.append("</a>");				
								row.addText(sb1.toString());
								StringBuilder sb2 = new StringBuilder();
								sb2.append("<a href='javascript:imprimirOPFarmacia(\"");
								sb2.append(op.getId().toString());										
								sb2.append("\")'>");			
								sb2.append(op.getFechaAltaAsString());
								sb2.append("</a>");				
								row.addText(sb2.toString());
								StringBuilder sb3 = new StringBuilder();
								sb3.append("<a href='javascript:imprimirOPFarmacia(\"");
								sb3.append(op.getId().toString());										
								sb3.append("\")'>");			
								sb3.append(op.getFechaAltaAsString());
								sb3.append("</a>");				
								row.addText(sb3.toString());				
		 					}else{*/
			 				row.addText(op.getId().toString(),rowURL);
			 				row.addText(op.getImporte().toString(),rowURL);
			 				row.addText(op.getFechaAltaAsString(),rowURL);
			 				row.addText(op.getBaja_fechaAsString(),rowURL);
			 				//}
			 				if (rolABM || rolABMPagos ){
		 						row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/ordenes_pago/boton_editar_orden_pago_ospim.jsp");
			 				}
			 				resultRows.add(row);
						}
				 	}
	 			}
 		%>
 		
 		
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	
<script type="text/javascript">
	var popup;
	function anularOpOspim(id_op) {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="anular-op" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 jQuery('#<portlet:namespace />buscar').click();
			 	}});        
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/anular_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);    
	}
	
	function reactivarOpOspim(id_op) {
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/reactivar_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		document.location = url;    
	}
	
	function editarFormaPagoOpOspim(id_op) {
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/editar_forma_pago_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		document.location = url;    
	}
	
	function imprimirOPFarmacia(id_ini_p){		 	
		window.location.href ="/pdfservlet/?accion=ordenPagoOspimFarmacia&id_ini="+id_ini_p+"&id_fin="+id_ini_p;			
	}	
	
</script>
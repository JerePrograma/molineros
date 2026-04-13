<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
				String portlet_name=null;
				if (portlet_name == null || portlet_name.trim().equals("")){
					portlet_name = "liquidaciones";
				}
				if(renderResponse.getNamespace().equals("_UOM_1_")){
					portlet_name = "uoma";
				}
				if(renderResponse.getNamespace().equals("_FAR_1_")){
					portlet_name = "farmacia";
				}	 	
				List<OrdenPagoAmtima> ordenes = (ArrayList<OrdenPagoAmtima>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_ORDENES_PAGO);
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("nro");
		 		headerNames.add("importe");
		 		headerNames.add("fecha");
		 		headerNames.add("baja-fecha");
		 		headerNames.add("");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-ordenes-pago-were-found"));			
			
				if(null!=ordenes){
	 				//Seteo el total de la lista.
				 	int total = ordenes.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < ordenes.size(); i++) {
				 		OrdenPagoAmtima op = (OrdenPagoAmtima) ordenes.get(i);
				 		PortletURL rowURL = renderResponse.createRenderURL();		 		
				 		rowURL.setWindowState(WindowState.MAXIMIZED);		 				
						rowURL.setParameter("struts_action","/"+portlet_name+"/view_orden_pago_entry");
						rowURL.setParameter("orden_pago_id", String.valueOf(op.getId()));
						
	 					ResultRow row = new ResultRow(op,op.getId(), i);
		 				row.addText(op.getId().toString(),rowURL);
		 				row.addText(op.getImporte().toString(),rowURL);
		 				row.addText(op.getAlta_fechaAsString(),rowURL);
		 				row.addText(op.getBaja_fechaAsString(),rowURL);
		 				if(!soloVer){
 	 						row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/ordenes_pago/boton_editar_orden_pago.jsp");
		 				}else{
		 					row.addText("");
		 				}
			 			resultRows.add(row);
				 	}
	 			}
 		%>
 		
 		
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		

	
<script type="text/javascript">
	var popup;
	function anularOpAmtima(id_op) {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="anular-op" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 jQuery('#<portlet:namespace />buscar').click();
			 	}});        
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/anular_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&isAmtima=isAmtima';
	    url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);    
	}
	
	function reactivarOpAmtima(id_op) {
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/reactivar_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&isAmtima=isAmtima';
	    url += '&rnd=' + Math.floor(Math.random()*100);
		document.location = url;    
	}
	
	function editarFormaPagoOpAmtima(id_op) {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_forma_pago_orden_pago';
	    url += '&orden_pago_id=' + id_op;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		document.location = url;    
	}
	
</script>

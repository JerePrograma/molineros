<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
			
			String portlet_name="";
			String entidad="";

			if (renderResponse.getNamespace().equals("_FAR_1_")){
				portlet_name = "farmacia";
				entidad="AMTIMA";
			}
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
				entidad="UOMA";
			}
			
			if(renderResponse.getNamespace().equals("_TES_1_")){
				portlet_name = "tesoreria";
				entidad="OSPIM";
			}
			
	 		boolean showInterbanking = PermissionUtil.userContainsRole(user, WebKeysGlobal.ROL_INTERBANKING);
	 		
	 			
				List<OrdenPago> ordenes = (ArrayList<OrdenPago>)renderRequest.getAttribute(WebKeysGlobal.INTERBANKING_OPS);
				
				if(ordenes==null){
					ordenes=(ArrayList<OrdenPago>) session.getAttribute(WebKeysGlobal.INTERBANKING_OPS);
				}
			    
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("nro");
		 		headerNames.add("CUIT");
		 		headerNames.add("Razón Social");
		 		headerNames.add("importe");
		 		headerNames.add("fecha");
		 		headerNames.add("baja-fecha");
		 		headerNames.add("Fecha Proceso");
		 		
		 		if (showInterbanking){
		 			headerNames.add("Limpiar");
		 		}else{
		 			headerNames.add("");
		 		}
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-ordenes-pago-were-found"));			
			
				if(null!=ordenes){
	 				//Seteo el total de la lista.
				 	int total = ordenes.size();
				 	searchContainer.setTotal(total);
				 	pageContext.setAttribute("total", total);	
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < ordenes.size(); i++) {
				 		OrdenPago op = (OrdenPago) ordenes.get(i);
				 		
				 		
						if (showInterbanking){
		 					ResultRow row = new ResultRow(op,op.getId(), i);
		 					
			 				row.addText(op.getId().toString());
			 				row.addText(op.getCuit());
			 				row.addText(op.getRazonSocial());
			 				row.addText(op.getImporte().toString());
			 				row.addText(op.getFechaAltaAsString());
			 				row.addText(op.getBaja_fechaAsString());
			 				row.addText(DateUtils.format(op.getFechaDesde(),DateUtils.SHORT));
			 				
			 				StringBuilder sb=new StringBuilder();
			 				sb.append("&nbsp;&nbsp;<img alt=\"Blanquear Orden de Pago\" src=\"");
							sb.append(themeDisplay.getPathThemeImages());
					 		sb.append("/common/delete.png\" onClick=\"javascript:eliminarOrdenPago('");
					 		sb.append(op.getId() );
					 		sb.append("');\"");
			                sb.append(" title=\"Blanquea\"");
				 		    sb.append("/>");
			 				row.addText(sb.toString());  
			 				
			 				resultRows.add(row);
						}
				 	}
	 			}
 		%>
 		
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%> 		
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
	
	
<script type="text/javascript">
	var popup;
	
	function eliminarOrdenPago(id_op) {
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/interbanking_ordenes_pago';
	    url += '&ordenes=' + id_op;
	    url += '&cmd=delete'
	    url += '&rnd=' + Math.floor(Math.random()*100);
		document.location = url;    
	}
	
		
	
</script>
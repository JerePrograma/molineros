<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				String portlet_name = ParamUtil.getString(request, "portlet_name");
				String origen=ParamUtil.getString(request,"origen");
	
				if (portlet_name == null || portlet_name.trim().equals("")){
					portlet_name = "tesoreria";
				}
				if(renderResponse.getNamespace().equals("_FAR_1_")){
					portlet_name = "farmacia";
				}
				if(renderResponse.getNamespace().equals("_UOM_1_")){
					portlet_name = "uoma";
				}
				//Si debe mostrarse el btn de agregar afiliado							
					boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
					boolean showABMButtons = PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_RECIBOS)||portlet_name.equals("uoma");				
					List<Recibo> recibos= (ArrayList<Recibo>)renderRequest.getAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
		 			headerNames.add("recibo");
			 		headerNames.add("fecha");
			 		if(portlet_name.equals("farmacia")){
			 			headerNames.add("empresa/afiliado");
			 		}else{
			 			headerNames.add("empresa");
			 		}
			 		headerNames.add("importe");
					headerNames.add("baja-fecha");
					if(showABMButtons ) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-recibos-were-found"));
				
					if(null!=recibos){					 	
				 		//Seteo el total de la lista.
					 	int total = recibos.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < recibos.size(); i++) {
			 				Recibo rec = recibos.get(i);
		 					ResultRow row = new ResultRow(rec, rec.getId(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
			 				if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
					 			StringBuilder sb0 = new StringBuilder();
					 			sb0.append("<a href='javascript:popupRecibo(\"");
					 			sb0.append(String.valueOf(String.valueOf(rec.getId())));										
								sb0.append("\")'>");
					 			sb0.append(String.valueOf(rec.getNumero()));					 			
					 			sb0.append("</a>");
					 			row.addText(sb0.toString());
					 			StringBuilder sb = new StringBuilder();
								sb.append("<a href='javascript:popupRecibo(\"");
					 			sb.append(String.valueOf(String.valueOf(rec.getId())));										
								sb.append("\")'>");			
								sb.append(rec.getFechaAsString());
								sb.append("</a>");
								row.addText(sb.toString());
					 			StringBuilder sb2 = new StringBuilder();
								sb2.append("<a href='javascript:popupRecibo(\"");
					 			sb2.append(String.valueOf(String.valueOf(rec.getId())));											
								sb2.append("\")'>");		
								if(rec.getEmpresa()!=null && rec.getEmpresa().getRazon_soc()!=null){
									sb2.append(rec.getEmpresa().getRazon_soc());									
								}else if(rec.getAfiliado()!=null && rec.getAfiliado().getApellido()!=null){
									sb2.append(rec.getAfiliado().getApellido()).append(", ").append(rec.getAfiliado().getNombre());									
								}
								sb2.append("</a>");			
								row.addText(sb2.toString());
					 			StringBuilder sb3 = new StringBuilder();
								sb3.append("<a href='javascript:popupRecibo(\"");
					 			sb3.append(String.valueOf(String.valueOf(rec.getId())));												
								sb3.append("\")'>");			
								sb3.append(rec.getImporte().toString());
								sb3.append("</a>");				
								row.addText(sb3.toString());		
								StringBuilder sb4 = new StringBuilder();
								sb4.append("<a href='javascript:popupRecibo(\"");
					 			sb4.append(String.valueOf(String.valueOf(rec.getId())));												
								sb4.append("\")'>");			
								sb4.append(rec.getBaja_fechaAsString());
								sb4.append("</a>");				
								row.addText(sb4.toString());				 			
					 		}else{
				 				rowURL.setParameter("struts_action","/"+portlet_name+"/editar_recibos_entry");
				 				rowURL.setParameter("recibo_id", String.valueOf(rec.getId()));
				 				rowURL.setParameter("origen", origen);
				 				row.addText(rec.getNumero(), rowURL);
				 				row.addText(rec.getFechaAsString(), rowURL);
				 				if(rec.getEmpresa() != null && rec.getEmpresa().getRazon_soc().length()>0){
				 					row.addText(rec.getEmpresa().getRazon_soc(), rowURL);
				 				}else if(rec.getAfiliado() != null && rec.getAfiliado().getApeNombre().length()>0){
				 					row.addText(rec.getAfiliado().getApeNombre(), rowURL);
				 				}else{
				 					row.addText("", rowURL);
				 				}
				 				
				 				row.addText(rec.getImporte().toString(), rowURL);
				 				row.addText(rec.getBaja_fechaAsString(), rowURL);
								// Action
								if(showABMButtons && !soloVer ) {
									row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/recibos/editar_borrar_recibo.jsp");
								}else if(soloVer){
									row.addText("");
								}
							}
				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
			
<script type="text/javascript">
	var popup;
	function popupRecibo(recibo_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry&recibo_id='+recibo_id;
		jQuery(popup).load(url); 
	}
	function anularRecibo(id_op) {		
	    popup = Liferay.Popup({title:"<liferay-ui:message key="anular-recibo" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
			 onClose: function() {
				 jQuery('#<portlet:namespace />buscar').click();
			 	}});        
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/anular_recibo_fecha';
	    url += '&recibo_id=' + id_op;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);    
	}
</script>		
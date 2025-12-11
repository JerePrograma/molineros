<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			

	 		boolean showABMButtons = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");

		    boolean isActa = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS);
		    if (isActa) {
		    	showABMButtons = true;
		    }
		    
				String fromBusquedaDeuda = (String)renderRequest.getAttribute("fromBusquedaDeuda");
				//Si debe mostrarse el btn de agregar afiliado								
					List<Acta> actas= (ArrayList<Acta>)renderRequest.getAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		if (fromBusquedaDeuda == null || 
			 				(fromBusquedaDeuda != null && !fromBusquedaDeuda.equals("fromBusquedaDeuda"))){ 
			 			headerNames.add("acta");
			 		}
			 		headerNames.add("empresa");
			 		headerNames.add("importe");
			 		headerNames.add("capital");
			 		headerNames.add("interes");
			 		headerNames.add("periodo-inicio-pago");
					headerNames.add("baja-fecha");
					if(showABMButtons ) { 
						headerNames.add("editar-borrar");
					}				
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-actas-were-found"));
				
					if(null!=actas){
				 								 	
				 				//Seteo el total de la lista.
					 	int total = actas.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < actas.size(); i++) {
					 		Acta acta = (Acta) actas.get(i);
				 					ResultRow row = new ResultRow(acta, acta.getId(), i);
					 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
					 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
					 				if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
					 					
						 				if (fromBusquedaDeuda == null || 
								 				(fromBusquedaDeuda != null && !fromBusquedaDeuda.equals("fromBusquedaDeuda"))){
								 			StringBuilder sb0 = new StringBuilder();
											sb0.append("<a href='javascript:popupActa(\"");
											sb0.append(String.valueOf(acta.getId()));										
											sb0.append("\")'>");			
											sb0.append(acta.getNumero());
											sb0.append("</a>");
						 					row.addText(sb0.toString());
						 				}
					 					
					 					StringBuilder sb = new StringBuilder();
										sb.append("<a href='javascript:popupActa(\"");
										sb.append(String.valueOf(acta.getId()));										
										sb.append("\")'>");			
										sb.append(acta.getEmpresa().getRazon_soc());
										sb.append("</a>");
										row.addText(sb.toString());
					 					StringBuilder sb2 = new StringBuilder();
										sb2.append("<a href='javascript:popupActa(\"");
										sb2.append(String.valueOf(acta.getId()));										
										sb2.append("\")'>");			
										sb2.append(acta.getTotal().toString());
										sb2.append("</a>");			
										row.addText(sb2.toString());
					 					StringBuilder sb3 = new StringBuilder();
										sb3.append("<a href='javascript:popupActa(\"");
										sb3.append(String.valueOf(acta.getId()));										
										sb3.append("\")'>");			
										sb3.append(acta.getCapital() != null ? acta.getCapital().toString():"");
										sb3.append("</a>");				
										row.addText(sb3.toString());
					 					StringBuilder sb4 = new StringBuilder();
										sb4.append("<a href='javascript:popupActa(\"");
										sb4.append(String.valueOf(acta.getId()));										
										sb4.append("\")'>");			
										sb4.append(acta.getInteres() != null ? acta.getInteres().toString() : "");
										sb4.append("</a>");
										row.addText(sb4.toString());
					 					StringBuilder sb5 = new StringBuilder();
										sb5.append("<a href='javascript:popupActa(\"");
										sb5.append(String.valueOf(acta.getId()));										
										sb5.append("\")'>");			
										sb5.append(acta.getPeriodoInicialAsString() + " - " + acta.getPeriodoFinalAsString());
										sb5.append("</a>");
										row.addText(sb5.toString());
					 					StringBuilder sb6 = new StringBuilder();
										sb6.append("<a href='javascript:popupActa(\"");
										sb6.append(String.valueOf(acta.getId()));										
										sb6.append("\")'>");			
										sb6.append(acta.getBaja_fechaAsString());
										sb6.append("</a>");
										row.addText(sb6.toString());					 					
					 				
					 				}else{
					 				    rowURL.setParameter("struts_action","/tesoreria/view_actas_entry");
					 				    rowURL.setParameter("acta_id", String.valueOf(acta.getId()));
						 				if (fromBusquedaDeuda == null || 
								 				(fromBusquedaDeuda != null && !fromBusquedaDeuda.equals("fromBusquedaDeuda"))){
						 					row.addText(acta.getNumero(), rowURL);
						 				}
						 				row.addText(acta.getEmpresa().getRazon_soc(), rowURL);
						 				row.addText(acta.getTotal().toString(), rowURL);
						 				row.addText(acta.getCapital() != null ? acta.getCapital().toString():"", rowURL);
						 				row.addText(acta.getInteres() != null ? acta.getInteres().toString() : "", rowURL);
						 				row.addText(acta.getPeriodoInicialAsString() + " - " + acta.getPeriodoFinalAsString(), rowURL);
						 				row.addText(acta.getBaja_fechaAsString(), rowURL);
										
					 				}
					 				// Action
									if(showABMButtons ) {
										if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
											row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/estudio_isidro/seguimiento_empresas/editar_borrar_acta.jsp");
										}else{
											row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/actas/editar_borrar_acta.jsp");
										}
									}
					 				
				 					resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</form>
<form action="" id="form_borrar" name="form_borrar" method="post">
	<input type="hidden" name="id" id="id" value=""/>
	<input type="hidden" name="accion" value="borrar"/>
	<input type="hidden" name="from" value="deuda"/>    
</form>
<script type="text/javascript">
	var popup;
	function popupActa(acta_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="acta" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_actas_entry&acta_id='+acta_id;
		jQuery(popup).load(url); 
	}
	
	function anularActa(id_op, is_acta_cerrada) {		 
		 if (!is_acta_cerrada){
		 		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
		 			var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/borrar_actas_entry" /></portlet:renderURL>';
		 		<%}else{%>
			 		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/borrar_actas_entry" /></portlet:renderURL>';
			 	<%}%>
				 jQuery('#id').val(id_op);
  				 submitForm(document.getElementById("form_borrar"), url);	
		 } else {
		    popup = Liferay.Popup({title:"<liferay-ui:message key="anular-acta" />",modal:true,position:[150,50],xy: ['center', 100],width:1000,
				 onClose: function() {
					 jQuery('#<portlet:namespace />buscar').click();
				 	}});        
			<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/borrar_actas_entry';
			<%}else{%>
		    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/borrar_actas_entry';
		    <%}%>
		    url += '&id=' + id_op;
		    url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery(popup).load(url);
		}
	}
	
</script>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = true;//PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_RECIBOS);				
					List<Recibo> recibos= (ArrayList<Recibo>)renderRequest.getAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("id-ingreso");
		 			headerNames.add("recibo");
		 			headerNames.add("entidad");
			 		headerNames.add("fecha");
			 		headerNames.add("empresa");
			 		headerNames.add("importe");
					headerNames.add("baja-fecha");
					if(showABMButtons ) { 
						headerNames.add("borrar");
					}				
					headerNames.add("incluir-reporte");
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
			 					StringBuilder sb00 = new StringBuilder();
					 			sb00.append("<a href='javascript:popupRecibo(\"");
					 			sb00.append(String.valueOf(String.valueOf(rec.getId())));										
								sb00.append("\")'>");
					 			sb00.append(String.valueOf(rec.getId()));					 			
					 			sb00.append("</a>");			
			 					row.addText(sb00.toString());
			 					
					 			StringBuilder sb0 = new StringBuilder();
					 			sb0.append("<a href='javascript:popupRecibo(\"");
					 			sb0.append(String.valueOf(String.valueOf(rec.getId())));										
								sb0.append("\")'>");
					 			sb0.append(String.valueOf(rec.getNumero()));					 			
					 			sb0.append("</a>");
					 			row.addText(sb0.toString());
					 			StringBuilder sb01 = new StringBuilder();
					 			sb01.append("<a href='javascript:popupRecibo(\"");
					 			sb01.append(String.valueOf(String.valueOf(rec.getId())));										
								sb01.append("\")'>");
					 			sb01.append(rec.getEntidad());					 			
					 			sb01.append("</a>");
					 			row.addText(sb01.toString());
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
								sb2.append(rec.getEmpresa().getRazon_soc());
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
								// Action
								if(showABMButtons ) {
									row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/uoma/recibos/editar_borrar_recibo_no_os.jsp");
								}
								StringBuilder sb5 = new StringBuilder();
								sb5.append("<center><input type='checkbox' name='incluir_recibo_"+rec.getId()+"' id='incluir_recibo_"+rec.getId());
								sb5.append("' onchange=\"javascript:deseleccionarRecibo('"+rec.getId()+"')\" checked /></center>");
								
								row.addText(sb5.toString());
							
				 			resultRows.add(row);
					 	}
				 	}
			%>
	<table>
		<tr>	
			<td>							
				<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="reporte"/>" title="<liferay-ui:message key="reporte" />" type="button" onClick="javascript:verReporte();"/>							
			</td>
		</tr>
	</table>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	
	<table>
		<tr>	
			<td>							
				<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="reporte"/>" title="<liferay-ui:message key="reporte" />" type="button" onClick="javascript:verReporte();"/>							
			</td>
		</tr>
	</table>
			
<script type="text/javascript">	
	var popup;
	function popupRecibo(recibo_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_recibos_no_os_entry&recibo_id='+recibo_id;
		jQuery(popup).load(url); 
	}	
</script>		
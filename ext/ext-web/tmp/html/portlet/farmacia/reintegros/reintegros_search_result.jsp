<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<form name="<portlet:namespace />reinteForm" id="<portlet:namespace />reinteForm">
<portlet:defineObjects/>
			<%				
				String portlet_name = ParamUtil.getString(request, "portlet_name");
	
				if (portlet_name == null || portlet_name.trim().equals("")){
					portlet_name = "farmacia";
				}
				if(renderResponse.getNamespace().equals("_LIQ_1_")){
					portlet_name = "liquidaciones";
				}
			    int seccionalReintegro = 0;
			    if (renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL) != null) {
			    	seccionalReintegro = ((Integer) renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL)).intValue();
			    }

				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA);				
			    
				List<ReintegroMedicamento> reintegrosList= new ArrayList<ReintegroMedicamento> ();
				reintegrosList= (ArrayList<ReintegroMedicamento>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("periodo");
		 		headerNames.add("seccional");
		 		headerNames.add("cuil-titular");
	 			headerNames.add("inte");
	 			headerNames.add("nombre");
		 		headerNames.add("numero");
		 		headerNames.add("importe");
		 		headerNames.add("Pago transferencia");
				headerNames.add("lista-orden-de-pago-num-cheque-fecha");
				if(showABMButtons) {
					headerNames.add("editar-borrar");
				}
				if(seccionalReintegro != 0) {
					headerNames.add("incluir-en-op");
				} else {
					headerNames.add("");
				}
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,1000,portletURL, headerNames,
								
				LanguageUtil.get(pageContext, "no-reintegros-were-found"));
			    boolean hayReintegrosPagables = false;
							    
				if(null!=reintegrosList){
						
		 				//Seteo el total de la lista.
					 	int total = reintegrosList.size();
					 	searchContainer.setTotal(total);					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < reintegrosList.size(); i++) {
					 		ReintegroMedicamento reintegro = (ReintegroMedicamento) reintegrosList.get(i);
		 					ResultRow row = new ResultRow(reintegro,reintegro.getId_reintegro(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(WindowState.MAXIMIZED);
			 				rowURL.setParameter("struts_action","/"+portlet_name+"/view_reintegro_farmacia_entry");
			 				rowURL.setParameter("id_reintegro", reintegro.getId_reintegroString());			 				
			 							 				
			 				row.addText(reintegro.getPeriodoString(),rowURL);
			 				row.addText(reintegro.getSeccional().getDescripcion(),rowURL);
		 					row.addText(reintegro.getAfiliado().getCuil_titular(),rowURL);		 					
			 				row.addText(reintegro.getAfiliado().getInteAsString(),rowURL);
			 				row.addText(reintegro.getAfiliado().getApeNombre(),rowURL);
			 				row.addText(reintegro.getId_reintegroString(),rowURL);
							row.addText(reintegro.getImporteTotal() != null ? reintegro.getImporteTotal().toString() : "",rowURL);							
							row.addText(reintegro.getCbu() != null && reintegro.getCbu().length() > 0 ? "SI" : "NO",rowURL);	
							row.addText(reintegro.getOPReintegro(),rowURL);
							// Action
							if(showABMButtons) {
								row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/farmacia/reintegros/editar_borrar_reintegro.jsp");
							}
							if(seccionalReintegro != 0) {
								StringBuffer sb = new StringBuffer();
								// validar si no está liquidado
								if (reintegro.getIdOP() == 0 && Validator.isNull(reintegro.getBaja_fecha())) {																	
										sb.append("<input type=\"checkbox\"");
										sb.append("name=\"");
										sb.append("pagarRein"+reintegro.getId_reintegroString());
										sb.append("\" id=\"");
										sb.append("pagarRein"+reintegro.getId_reintegroString());
										sb.append("\" checked='checked' value=\"");
										sb.append(reintegro.getImporteTotal() != null ? reintegro.getImporteTotal().toString() : "");									
										sb.append(",");
										sb.append("cbu"+reintegro.getCbu() != null ? reintegro.getCbu() : "");
										sb.append("\"/>");
										hayReintegrosPagables = true;
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

<%
if(null!=reintegrosList){
%>
	<div align="left" ></div> <label> <%=reintegrosList.size()%> reintegros</label></div>
<%	
}
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

	<table>
		<tr>	 
	<c:if test="<%= reintegrosList.size() > 0 %>">
			<td align="left"><input type="button" value="<liferay-ui:message key="reporte-reintegros-excel" />" onClick="<portlet:namespace />generarReporteReintegros();" /></td>						
	</c:if>	
	<c:if test="<%= seccionalReintegro != 0 && hayReintegrosPagables %>">		
			<td align="left"><input type="button" value="<liferay-ui:message key="crear-lista-op" />" onClick="<portlet:namespace />emitirOP();" /></td>											
	</c:if>
	
		</tr>
	</table>
	<input type="hidden" id="<portlet:namespace />seccional_op"  name="<portlet:namespace />seccional_op" value="<%= seccionalReintegro %>" />
</form>	
	<script type="text/javascript">
			
			function validarSeleccionReintegros() {
				valido=false;
				form1 = document.<portlet:namespace />reinteForm;
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
					var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/crear_lista_para_pago_reintegros_farmacia';
					document.<portlet:namespace />reinteForm.method = 'post';
					submitForm(document.<portlet:namespace />reinteForm, url);
				}
				else {
					alert ('No es posible generar la lista');
				}
			}

			function <portlet:namespace />generarReporteReintegros(){					
					var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/generar_reporte_reintegros_farmacia';					
					document.<portlet:namespace />reinteForm.method = 'post';					
					submitForm(document.<portlet:namespace />reinteForm, url);
			}
	</script>
<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "empresas";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	if(renderResponse.getNamespace().equals("_AFI_1_")){
		portlet_name = "afiliados";
	}
	if(renderResponse.getNamespace().equals("_CGT_1_")){
		portlet_name = "cgt";
	}
	if(renderResponse.getNamespace().equals("_EST_1_")){
		portlet_name = "estudio_isidro";
	}
	
	if(renderResponse.getNamespace().equals("_EMP_1_")){
		portlet_name = "empresas";
	}
	Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
    Calendar fechaInicio=Calendar.getInstance();
%>
<fieldset class="block-labels">	
	<legend><liferay-ui:message key="actas-acuerdos" /></legend>
	<div id="actualiza_deuda">
		<liferay-ui:input-date
							dayParam="fechaActualizaDia"
							dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
							monthParam="fechaActualizaMes"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaActualizaAnio1"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 3 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 3%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
	   <input type="button" value="aalala" onClick="javascript:actualizaAportes()"/>
	   <input type="hidden" id="actaActualizar" name="actaActualizar"/>
	</div>
			<%
			

	 		boolean showABMButtons = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");

			NumberFormat formatter = new DecimalFormat("$#0.00");
			Boolean fromBusquedaDeuda = (Boolean) renderRequest.getAttribute("fromBusquedaDeuda");
			//Si debe mostrarse el btn de agregar afiliado
			List<ActaAcuerdoSeguimiento> actas= (ArrayList<ActaAcuerdoSeguimiento>)portletSession.getAttribute(WebKeysEstudioIsidro.ACTAS_ACUERDO_SEGUIMIENTO);
			PortletURL portletURL = renderResponse.createRenderURL();				
			String orderByCol = ParamUtil.getString(request, "orderByCol");
			String orderByType = ParamUtil.getString(request, "orderByType");
			List<String> headerNames = new ArrayList<String>();
			headerNames.add("entidad");
			headerNames.add("tipo");
			headerNames.add("numero");
			headerNames.add("fecha");
			headerNames.add("total");
			headerNames.add("capital");
			headerNames.add("interes");
			headerNames.add("periodos-actas");
			headerNames.add("saldo-acuerdo");			
			headerNames.add("estado-estudio");
			headerNames.add("pagos");
			//headerNames.add("actualiza-deuda");			
			headerNames.add("edit");
			headerNames.add("pasar-deuda");
			 		
			 						
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
					 		String javascriptString=null;
					 		ActaAcuerdoSeguimiento acta = (ActaAcuerdoSeguimiento) actas.get(i);
					 		
				 			ResultRow row = new ResultRow(acta, acta.getId(), i);
					 		row.addText(acta.getEntidad()==WebKeysGlobal.OSPIM?"OSPIM":acta.getEntidad()==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");
					 		row.addText(acta.getTipo());
					 		row.addText(acta.getNumero());
					 		row.addText(acta.getCierreFechaAsString());
					 		row.addText(formatter.format(acta.getTotal()));
					 		row.addText(formatter.format(acta.getCapital()));
					 		row.addText(formatter.format(acta.getInteres()));
					 		row.addText(null!=acta.getPeriodos()?acta.getPeriodos():"");
					 		if(acta.getTipo().equals("ACTA") && acta.getConvenioPago()!=null){
					 			row.addText(acta.getConvenioPago());
					 		}else{ //if(acta.getTipo().equals("ACTA")){					 	
					 			row.addText(null!=acta.getSaldo()?formatter.format(acta.getSaldo()):"");
					 			//row.addText(formatter.format(acta.getSaldo().toString()));
					 		}
					 		row.addText(null!=acta.getEstado()?acta.getEstado():"");
					 		
					 		StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='pagos'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/print.png\" onClick=\"javascript:imprimirPagos('");
		 					sb.append(acta.getId());
		 					sb.append("','");
		 					sb.append(acta.getTipo());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
		 					
		 					//ACTUALIZAR SALDO...
		 					/* if(null!=acta.getEstado() && acta.getEstado().trim().toUpperCase().equals("PENDIENTE")){
			 					sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='actualizar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/portlet/refresh.png\" onClick=\"javascript:actualizarDeuda('");
			 					sb.append(acta.getId());
			 					sb.append("','");
			 					sb.append(acta.getTipo());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 					}else{
		 						row.addText("");
		 					} */
		 					sb= new StringBuilder();
		 					
		 					sb.append("<img alt=\"<liferay-ui:message key='ver'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				if(acta.getEntidad()!=2){
			 					if(acta.getTipo().equals("ACTA")){			 				
			 						sb.append("/common/search.png\" onClick=\"javascript:editarActaNoOSSeguimiento('");
			 					}else{
			 						sb.append("/common/search.png\" onClick=\"javascript:popupConvenioNoOS('");
			 					}
			 				}else{
			 					if(acta.getTipo().equals("ACTA")){			 				
			 						sb.append("/common/search.png\" onClick=\"javascript:editarActaSeguimiento('");
			 					}else{
			 						sb.append("/common/search.png\" onClick=\"javascript:popupConvenio('");
			 					}
			 				}
			 				sb.append(acta.getId());
			 				sb.append("','false');\" />");
			 				row.addText(sb.toString());
			 				
			 				sb= new StringBuilder();
		 					/* if(acta.getTipo()!=null && acta.getTipo().equals("ACTA") && acta.getTotal().compareTo(acta.getSaldo())==0){	 */
		 					if(acta.getTipo()!=null 
		 					    && acta.getTipo().equals("ACTA") 
		 					    && acta.getConvenioPago() == null
		 						&& acta.getTotal().compareTo(acta.getSaldo())==0){		
			 					sb.append("<img alt=\"<liferay-ui:message key='actualizar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					if(acta.getEntidad()!=2){
			 						sb.append("/common/undo.png\" onClick=\"javascript:anularActaNoOS('");
			 					}else{
			 						sb.append("/common/undo.png\" onClick=\"javascript:anularActa('");
			 					}
			 					sb.append(acta.getId());
			 					sb.append("','false');\" />");	
		 					}		 					
			 				
			 				row.addText(sb.toString());
		 					
		 					
				 			resultRows.add(row);
					 	}
					 	
				 	}
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</form>
<form action="" id="form_borrar_d" name="form_borrar_d" method="post">
	<input type="hidden" name="id" id="id" value=""/>
	<input type="hidden" name="accion" value="borrar"/>
	<input type="hidden" name="from" value=""/>
	<input type="hidden" name="acta_cerrada_d"/>	
	<input type="hidden" name="popupActa" id="popupActa" value="true"/>        
</form>
</fieldset>
<script type="text/javascript">
	jQuery("#actualiza_deuda").hide();
	
	var popup;
	function popupActa(acta_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="acta" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_actas_entry&acta_id='+acta_id;
		jQuery(popup).load(url); 
	}
	
	function popupActaNoOS(acta_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="acta" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_actas_no_os_entry&acta_id='+acta_id;
		jQuery(popup).load(url); 
	}
	
	function anularActa(id_op, is_acta_cerrada) {		
		 if(confirm("Confirma el paso del acta a cálculo de deuda? De esta forma, el acta dejará de existir.")){
		 		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
		 			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/borrar_actas_entry&id='+id_op;		 		
			 	<%}%>			 	 
				 jQuery('#id').val(id_op);				 
				 var form = jQuery("#form_borrar_d");
					form.ajaxForm(
					{
						url: url,
				    	//target: tar,//".ui-dialog-content",//poopup
				        type: "POST",
				        beforeSubmit: function() {				        		        
				        },
				        success: function(data) {
				        	jQuery('#<portlet:namespace />busquedaActaDiv').css('display','none');
				        	jQuery('#<portlet:namespace />tabla_resumen').html(data);
						        	
				        }
				    }
				);	
									
				form.submit();
		 }
		  
	}
	var popupActa;
	
	function editarActaSeguimiento(id_op) {
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_actas_entry&acta_id='+id_op+'&cmd=update';
		 popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:1100});
		 jQuery(popupActa).load(url);
	}
	
	function editarActaNoOSSeguimiento(id_op) {
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/edit_actas_no_os_entry&acta_id='+id_op+'&cmd=update';
		 popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:1100});
		 jQuery(popupActa).load(url);
	}
	
		
	function anularActaNoOS(id_op, is_acta_cerrada) {			  
		if(confirm("Confirma el paso del acta a cálculo de deuda? De esta forma, el acta dejará de existir.")){
	
		 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/borrar_actas_no_os_entry" /></portlet:renderURL>';
		 		jQuery('input:hidden#id').val(id_op);		 		
				document.getElementById("popupActa").value="true";
  				var form = jQuery("#form_borrar_d");
				form.ajaxForm(
						{
							url: url,
					    	//target: tar,//".ui-dialog-content",//poopup
					        type: "POST",
					        beforeSubmit: function() {
					        },
					        success: function(data) {					        				        	
					        	jQuery('#<portlet:namespace />busquedaActaDiv').css('display','none');
					        	jQuery('#<portlet:namespace />tabla_resumen').html(data);					        	
					        }
					    }
					);	
								
					form.submit();
		}
	}
	
	function imprimirPagos(id, tipo){				
		var cuit_entidad='<%=empresa.getCuit()%>';	
		var sucursal_entidad='<%=empresa.getSucursal()%>'
		var tipoReporte=jQuery("#<portlet:namespace/>tipo_reporte").val();
		
		var url = '/xlsservlet/?reporte=CUENTAS_CORRIENTES_ACTAS_Y_CONV'
			+ '&fechaDesdeDia=01' 
			+ '&fechaDesdeMes=01' 
			+ '&fechaDesdeAnio=1900'
			+ '&fechaHastaDia=01' 
			+ '&fechaHastaMes=01' 
			+ '&fechaHastaAnio=2999' 
			+ '&cuit_entidad=' +cuit_entidad
			+ '&sucursal_entidad=' +sucursal_entidad					
			+ '&tipoReporte='+tipo
			+ '&id='+id;
		
		url += '&rnd=' + Math.floor(Math.random()*100);		
		window.location.href =url;
	}
	
	function actualizarDeuda(){
		jQuery("#actualiza_deuda").show();
	}
	
	function popupConvenioNoOS(convenio_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="convenio" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_convenios_no_os_entry&convenio_id='+convenio_id;
		jQuery(popup).load(url); 
	}
	function popupConvenio(convenio_id){
		popup= Liferay.Popup({title:"<liferay-ui:message key="convenio" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/view_convenios_entry&convenio_id='+convenio_id;
		jQuery(popup).load(url); 
	}
	
</script>
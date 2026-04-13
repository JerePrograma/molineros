<%@page import="ar.com.ospim.afiliados.services.PlanServiceUtil"%>
<%@ include file="/html/portlet/crm/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>

<portlet:defineObjects/>

<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
if(renderResponse.getNamespace().equals("_JUD_1_")){
	portlet_name = "judicial";
}else{
	portlet_name = "afiliados";
}

		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
 		boolean showABMCrmLegal = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES);

 		session.removeAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
 		session.removeAttribute("cmd");
		
 		boolean estanPreCargadasLasListas = session.getAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO)!=null 
				&& session.getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS)!=null;
		
		if(!estanPreCargadasLasListas){
			try{
				session.setAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO, CrmServiceUtil.buscarTiposReclamo());
				session.setAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS, CrmServiceUtil.buscarMotivosContacto());
				session.setAttribute(WebKeysAfiliados.PLANES_EN_SESSION,TraeListasServiceUtil.getPlanes());
 			}catch (Exception e) {
				System.err.println("Se rompieron las busquedas de las listas");
			}	
		}
		List<MotivoContacto> motivosCrm = (List<MotivoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);
		List<TipoReclamo> tiposReclamoCrm = (List<TipoReclamo>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO);
		
		ArrayList<Plan> planes = (ArrayList<Plan>) request.getSession().getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);

		BusquedaDocumLegalFiltro filtro = (BusquedaDocumLegalFiltro) request.getSession().getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL);
		Calendar fechaDesde = Calendar.getInstance(); 		
		Calendar fechaHasta = Calendar.getInstance();
		int tipoSel = 0;
		int motivoSel = 0;
		String cuilTitularSel = "";
		String inteSel = "";
		int incluirA = 0;
		int nro_doc_legal = 0;
		int planSel = 0;
		int planOmintSel = 0;
		boolean tieneAntec = false;
		boolean concluido = false;
		boolean noconcluido = false;
		
		if(filtro != null){
			if(filtro.getFechaDesde()!=null){
				fechaDesde.setTime(filtro.getFechaDesde());
			}
			if(filtro.getFechaHasta()!=null){
				fechaHasta.setTime(filtro.getFechaHasta());
			}	
			tipoSel=filtro.getTipoReclamo();
			motivoSel=filtro.getMotivo();
			cuilTitularSel=filtro.getCuil_titular();
			inteSel=filtro.getInte();
			incluirA=filtro.getIncluirA();
			nro_doc_legal=filtro.getIdDocumLegal();
			planSel = filtro.getIdPlan();
			planOmintSel = filtro.getIdPlanOmint();
			tieneAntec = filtro.isTieneAntecedente();
			concluido = filtro.isConcluido();
			noconcluido=filtro.isNoConcluido();
		}else{
			fechaDesde.setTime(new Date());
			fechaHasta.setTime(new Date());
		}
		
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-reclamo-crm" /></legend>				
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-nro" /></label></td>
						<td>
							<input id="<portlet:namespace />id_docum_legalBusq"
								name="<portlet:namespace />id_docum_legalBusq" size="20"
								maxlength="100" type="text"  
								value="<%=nro_doc_legal != 0 ? String.valueOf(nro_doc_legal) : ""%>"  />
						</td>
							
						<td><label>Fecha Desde:</label></td>
						<td><liferay-ui:input-date dayParam="fechaDesdeDiaBusq"
							dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaDesdeMesBusq"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaDesdeAnioBusq"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>			
							
						<td><label>Fecha Hasta:</label></td>
						<td><liferay-ui:input-date dayParam="fechaHastaDiaBusq"
							dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaHastaMesBusq"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaHastaAnioBusq"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>			
					</tr>
					<tr>
						<td colspan="6">
							<div id="<portlet:namespace />divBuscarAfiliado" name="<portlet:namespace />divBuscarAfiliado">
								<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
								<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
									<liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
									<liferay-util:param name="cuil" value='' />
									<liferay-util:param name="inte" value='' />
								</liferay-util:include>
								</fieldset>	
							</div>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-tipo" />:</label></td>
						<td><select name="<portlet:namespace />tipo_reclamoBusq"
								id="<portlet:namespace />tipo_reclamoBusq">			
								<option value="0">Todos los tipos de reclamo</option>
								<% for(TipoReclamo tr : tiposReclamoCrm) {%>
									<option value="<%=tr.getId() %>"  
									<%if (tipoSel == tr.getId()) { %> selected="selected" <%}%>><%=tr.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-motivo" />:</label></td>
						<td><select name="<portlet:namespace />motivo_reclamoBusq"
								id="<portlet:namespace />motivo_reclamoBusq">
								<option value="0">Todos los motivos de contacto</option>			
								<% for(MotivoContacto mc : motivosCrm) {%>
									<option value="<%=mc.getId() %>"  
									<%if (motivoSel == mc.getId()) { %> selected="selected" <%}%>><%=mc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td colspan="4">&nbsp;</td>			
					</tr>
					<tr><td><label>Incluir A:</label></td>
					    <td> <select name="<portlet:namespace/>incluirA" id="<portlet:namespace/>incluirA">
							 					<option value="0" <%if(incluirA == 0){ %>  selected="selected"  <%} %> > Todos</option>
							 					<option value="1" <%if(incluirA == 1){ %>  selected="selected"  <%} %> > Solo Afiliados</option>
							 					<option value="2" <%if(incluirA == 2){ %>  selected="selected"  <%} %> > Solo No Afiliados</option>
							 					</select> &nbsp;&nbsp;
						</td>
						<td colspan="4">&nbsp;</td>
				</tr>
				
				<tr>
					<td colspan="1">Plan:</td>
					<td colspan="1">
						<select name="<portlet:namespace/>plan_reclamoBusq" id="<portlet:namespace/>plan_reclamoBusq" style="width: 240px; ">
							<option value='0'>Todos los planes</option>
							<%if(planes!=null){ for (Plan plan : planes) { %>
								<option value="<%= plan.getId()%>"  
									<%if(planSel==plan.getId()){ %> selected="selected" <%} %> ><%=plan.getDescripcion()%></option>
								<% } %>	
							<% } %>
						</select>
					</td>
					<td colspan="1">Plan Omint:</td>
					<td colspan="1">
						<select name="<portlet:namespace/>planOmint_reclamoBusq" id="<portlet:namespace/>planOmint_reclamoBusq" style="width: 150px; ">
							<option value='0'>Todos los planes Omint</option>
							<option value="3" <%if(planOmintSel==3){ %> selected="selected" <%} %> >OSPIM_0</option>
							<option value="1" <%if(planOmintSel==1){ %> selected="selected" <%} %> >OSPIM_1</option>
							<option value="2" <%if(planOmintSel==2){ %> selected="selected" <%} %> >OSPIM_2</option>
							<option value="4" <%if(planOmintSel==4){ %> selected="selected" <%} %> >OSPIM_2A</option>
						</select>
					</td>
					<!-- <td colspan="2">&nbsp;</td> -->
					<td><label><liferay-ui:message key="crm-doc-legal-antec" />:</label>
						<input type="checkbox" name="<portlet:namespace/>antecedente_reclamoBusq"  id="<portlet:namespace/>antecedente_reclamoBusq"
							<%if(tieneAntec){%> checked="checked" <%} %>></td>
					<td><label><liferay-ui:message key="crm-doc-legal-concluido" />:</label>
						<input type="checkbox" name="<portlet:namespace/>concluido_reclamoBusq"  id="<portlet:namespace/>concluido_reclamoBusq" 
						<%if(concluido){%> checked="checked" <%} %>></td>
						
					<td><label>No concluido:</label>
						<input type="checkbox" name="<portlet:namespace/>no_concluido_reclamoBusq"  id="<portlet:namespace/>no_concluido_reclamoBusq" 
						<%if(noconcluido){%> checked="checked" <%} %>></td>				
				</tr>
				
				
				<tr>	
					 <td colspan="6">							
						<input type="button"
							   id="<portlet:namespace />buscar" 
							   value="<liferay-ui:message key="buscar"/>" 
							   title="<liferay-ui:message key="buscar" />" 
							   onclick="<portlet:namespace />buscarDocumentosLegal();" />							
					<%-- &nbsp;&nbsp;						
					<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCampos()'/> --%>
					
					&nbsp;&nbsp;
					<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" 
								onClick="<portlet:namespace />reporteXLS();" />
					</td>	
					
					<td>&nbsp;&nbsp;</td>
					  
					<td>
					     <input type="button"
							   id="<portlet:namespace />nuevo" 
							   value="Nuevo Reclamo Legal" 
							   title="Nuevo" 
							   onclick="<portlet:namespace />nuevoCrmContacto();" />	
					</td>
							 		
				</tr>				
			</table>	      	  
		</fieldset>
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaDocumentosLegalDiv">
			</div>
		</fieldset> 
</form>			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	
	function <portlet:namespace />buscarDocumentosLegal(){
		jQuery('#<portlet:namespace />buscando').show();

		var id_docum_legal=jQuery('#<portlet:namespace />id_docum_legalBusq').val();		
		var cuil_titular=jQuery('#<portlet:namespace />cuil').val();
		var integ=jQuery('#<portlet:namespace />inte').val();		
		var motivo=jQuery('#<portlet:namespace/>motivo_reclamoBusq').val();
		var tipo=jQuery('#<portlet:namespace/>tipo_reclamoBusq').val();
		
		var incluirAquienes = parseInt(jQuery('#<portlet:namespace/>incluirA').val());	 
		
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDiaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMesBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnioBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
		var plan=jQuery('#<portlet:namespace/>plan_reclamoBusq').val();
		var planOmint=jQuery('#<portlet:namespace/>planOmint_reclamoBusq').val();
		var antecedente = document.getElementById("<portlet:namespace />antecedente_reclamoBusq").checked ? 'true' : 'false';
		var concluido = document.getElementById("<portlet:namespace />concluido_reclamoBusq").checked ? 'true' : 'false';
		var noconcluido = document.getElementById("<portlet:namespace />no_concluido_reclamoBusq").checked ? 'true' : 'false';
		
		/* var total_reg = jQuery('#<portlet:namespace />total_registros').val(); */
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		/* var viene_de = jQuery('#<portlet:namespace />viene_de').val(); */
		
		
		var busquedaReclamos = {  "motivo": motivo, "tipoReclamo": tipo, 
								  "fechaDesdeFinal": fechaDesdeFinal, "fechaHastaFinal": fechaHastaFinal, 
								  "incluirA": incluirAquienes, 
								  "cuil_titular": cuil_titular, "inte":integ,
								  "nro_doc_legal":id_docum_legal, 
								  "plan":plan, "planOmint":planOmint,
								  "antecedente" : antecedente,
				  				  "concluido" : concluido,
				  				  "noconcluido" : noconcluido,
								  "pagina" : offset_reg };
		

		
		//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/busqueda_reclamosCRM" /></portlet:renderURL>';
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/busqueda_reclamosCRM';
		
	    jQuery('#<portlet:namespace />busquedaDocumentosLegalDiv').load(url,busquedaReclamos, function() {
	    																jQuery('#<portlet:namespace />buscando').hide();            															
	    															  }
	    );
	}	
	
	/* function <portlet:namespace />limpiarCampos(){
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		jQuery('#<portlet:namespace />id_seccional').val('');		
		jQuery('#<portlet:namespace />seccional').val('');
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />nombre').val('');
		jQuery('#<portlet:namespace />entidad').val('');
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery('#<portlet:namespace />motivo_reclamoBusq').val('0');
		jQuery('#<portlet:namespace />tipo_reclamoBusq').val('0');
		jQuery('#<portlet:namespace />categoria_contactoBusq').val('0');
		jQuery('#<portlet:namespace />estado_contactoBusq').val('');
	} */
	
	function <portlet:namespace />reporteXLS(){

		var id_docum_legal=jQuery('#<portlet:namespace />id_docum_legalBusq').val();		
		var cuil_titular=jQuery('#<portlet:namespace />cuil').val();
		var integ=jQuery('#<portlet:namespace />inte').val();		
		var motivo=jQuery('#<portlet:namespace/>motivo_reclamoBusq').val();
		var tipo=jQuery('#<portlet:namespace/>tipo_reclamoBusq').val();

		var incluirAquienes = parseInt(jQuery('#<portlet:namespace/>incluirA').val());	 

		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDiaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMesBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnioBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
	    var plan=jQuery('#<portlet:namespace/>plan_reclamoBusq').val();
		var planOmint=jQuery('#<portlet:namespace/>planOmint_reclamoBusq').val();
		var antecedente = document.getElementById("<portlet:namespace />antecedente_reclamoBusq").checked ? 'true' : 'false';
		var concluido = document.getElementById("<portlet:namespace />concluido_reclamoBusq").checked ? 'true' : 'false';
		var noconcluido = document.getElementById("<portlet:namespace />no_concluido_reclamoBusq").checked ? 'true' : 'false';
		
		 window.location.href ='/xlsservlet/?reporte=REPORTE_BUSQUEDA_RECLAMOSCRM'
			 				+'&motivo='+motivo
			 				+'&tipoReclamo='+tipo 
			  				+'&fechaDesdeFinal='+fechaDesdeFinal
			  				+'&fechaHastaFinal='+fechaHastaFinal 
			  				+'&incluirA='+incluirAquienes 
			 				+'&cuil_titular='+cuil_titular
			 				+'&inte='+integ  
		 					+'&nro_doc_legal='+id_docum_legal
		  					+'&plan='+plan
		  					+'&planOmint='+planOmint
		  					+'&antecedente='+antecedente
		  					+'&concluido='+concluido
		  					+'&noconcluido='+noconcluido;
	}	
	
	/* function <portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi){
	if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
	   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0){
		alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	}else{
		return true;
	}
}
 	 */
 	var popupCRM; 
 	 
 	function verCrmReclamo(id_serial) {
 		var params = "&<%=Constants.CMD %>=" + "<%= Constants.VIEW%>";
 		params = params + "&id="+id_serial + "&esPopup=si"; 
 		
 		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-doc-legal" />",modal:true, width: 1150, position:['center',10]});

 		/* popupCRM= Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true,width:880,height:655,fixedcenter:true}); */
 		//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_crm_legales_entry';
 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_crm_legales_entry';
 		url = url + params;

 		jQuery(popupCRM).load(url);	
 	}
 	
 	function editaCrmDocumentoLegal(id_serial, cuil_titu, integ) {
 		
<%--  		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_crm_legales_entry';  
 		var params = "&<%=Constants.CMD%>="+"<%= Constants.EDIT%>";
 		params = params + "&id="+id_serial;
 		params = params +"&cuil_titular="+cuil_titu+"&integ="+integ;
 		
 		url = url + params;
 		
 		document.<portlet:namespace />fm.method = 'post';
 		
 		submitForm(document.<portlet:namespace />fm, url); --%>
		//var xportletUrl = '/afiliados/editar_crm_legales_entry';
		
		var xportletUrl = '/<%=portlet_name%>/editar_crm_legales_entry';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="edit"/>'+
		'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
		'<liferay-portlet:param name="integ" value="__inte"/>'+
		'<liferay-portlet:param name="id" value="__id"/>'+
	    '</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
	    url = url.replace("__cuil_titu",cuil_titu);
	    url = url.replace("__inte", integ);
	    url = url.replace("__id",id_serial); 

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
 	}
 	
 	
 	function <portlet:namespace />nuevoCrmContacto() {
 		
 		var cuil_titular=jQuery('#<portlet:namespace />cuil').val();
		var integ=jQuery('#<portlet:namespace />inte').val();	
		if(cuil_titular===null || ""=== cuil_titular || integ===null || integ===""){
			alert("Debe seleccionar un afiliado.");
		}else{	
 		 var xporlet ='<%=portlet_name%>';
 		 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
 		 '<liferay-portlet:param name="struts_action" value="/__porlet/editar_crm_legales_entry" />'+
 		 '<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
 		 '<liferay-portlet:param name="integ" value="__inte"/>'+
 		 '<liferay-portlet:param name="cmd" value="add"/>'+
 		 '<liferay-portlet:param name="noAfiliado" value="false"/>'+
 		 '</liferay-portlet:renderURL>';
 		 url = url.replace("__porlet",xporlet);
 		 url = url.replace("__cuil_titu",cuil_titular);
 		 url = url.replace("__inte",integ);

 		 document.<portlet:namespace />fm.method = 'post';
 		 submitForm(document.<portlet:namespace />fm, url);
 	   }  
 	}

 	 	
</script>

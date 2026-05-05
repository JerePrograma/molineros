<%@page import="ar.com.ospim.afiliados.services.PlanServiceUtil"%>
<%@ include file="/html/portlet/crm/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.TiposDeSituacionesMedicas" %>

<portlet:defineObjects/>

<%

       String portlet_name_space = portletDisplay.getId();
       String portlet_name="afiliados";
       if (portlet_name_space == null || portlet_name_space.trim().equals("")){
	       portlet_name = "afiliados";
       }else if(portlet_name != null && portlet_name_space.trim().equals("CAI_1")){
	       portlet_name = "cai";
       } 
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
 		boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);

 		session.removeAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
 		session.removeAttribute("cmd");
		
 		boolean estanPreCargadasLasListas = session.getAttribute(WebKeysCrm.CRM_LISTA_TIPOS)!=null 
				&& session.getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS)!=null
				&& session.getAttribute(WebKeysCrm.CRM_LISTA_CATEGORIAS)!=null;
		
		if(!estanPreCargadasLasListas){
			try{
				session.setAttribute(WebKeysCrm.CRM_LISTA_TIPOS, CrmServiceUtil.buscarTiposContacto());
				session.setAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS, CrmServiceUtil.buscarMotivosContacto());
				session.setAttribute(WebKeysCrm.CRM_LISTA_CATEGORIAS, CrmServiceUtil.buscarCategoriasContacto());
 				session.setAttribute(WebKeysAfiliados.PLANES_EN_SESSION,TraeListasServiceUtil.getPlanes());
 				session.setAttribute(WebKeysCrm.CRM_LISTA_SITUACIONES_MEDICAS, TraeListasServiceUtil.getTipoSituacionesMedicas());
/* 				session.setAttribute(WebKeysAfiliados.PLANES_EN_SESSION, PlanServiceUtil.getInstance().buscaTodosPlanes());*/
 			}catch (Exception e) {
				System.err.println("Se rompieron las busquedas de las listas");
			}	
		}
		List<MotivoContacto> motivosCrm = (List<MotivoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);
		List<CategoriaContacto> categoriasCrm = (List<CategoriaContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_CATEGORIAS);
		List<TipoContacto> tiposCrm = (List<TipoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_TIPOS);
		List<TiposDeSituacionesMedicas> situacionesMedicas = (List<TiposDeSituacionesMedicas>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_SITUACIONES_MEDICAS);
		
		ArrayList<Plan> planes = (ArrayList<Plan>) request.getSession().getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);

		BusquedaContactoFiltro filtro = (BusquedaContactoFiltro) request.getSession().getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS);
		Calendar fechaDesde = Calendar.getInstance(); 		
		Calendar fechaHasta = Calendar.getInstance();
		String estadoSel = "";
		int tipoSel = 0;
		int categoriaSel = 0;
		int motivoSel = 0;
		String cuilTitularSel = "";
		String inteSel = "";
		int incluirA = 0;
		int nro_contacto = 0;
		String sectorSel = "";
		String usuarioSel = "";
		int planSel = 0;
		int planOmintSel = 0;
		int importancia = 99;
		int incumContrato = 99;
		int eficaciaSel = 99;
		int situacionSel = 0;
		String noAfiliadoDocNumero = "";
		
		if(filtro != null){
			if(filtro.getFechaDesde()!=null){
				fechaDesde.setTime(filtro.getFechaDesde());
			}
			if(filtro.getFechaHasta()!=null){
				fechaHasta.setTime(filtro.getFechaHasta());
			}	
			estadoSel=filtro.getEstado();
			tipoSel=filtro.getTipo();
			categoriaSel=filtro.getCategoria();
			motivoSel=filtro.getMotivo();
			cuilTitularSel=filtro.getCuil_titular();
			inteSel=filtro.getInte();
			incluirA=filtro.getIncluirA();
			nro_contacto=filtro.getNro_contacto();
			planSel = filtro.getIdPlan();
			planOmintSel = filtro.getIdPlanOmint();
			importancia = filtro.getImportancia();
			incumContrato = filtro.getIncumplimientoContacto();
			eficaciaSel = filtro.getEficaciaConformidad();
			noAfiliadoDocNumero = filtro.getNoAfiliadoDocNumero();
			situacionSel=filtro.getSituacionMedica();
			/* sectorSel=filtro.getSector();
			usuarioSel=filtro.getUsuario(); */
		}else{
			fechaDesde.setTime(new Date());
			fechaDesde.add(Calendar.DATE, -60);
			fechaHasta.setTime(new Date());
		}
		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-contacto-crm" /></legend>				
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-nro" /></label></td>
						<td>
							<input id="<portlet:namespace />numero_contactoBusq"
								name="<portlet:namespace />numero_contactoBusq" size="20"
								maxlength="100" type="text"  
								value="<%=nro_contacto != 0 ? String.valueOf(nro_contacto) : ""%>"  />
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
						<td colspan="6">
							<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">
							  <fieldset class="block-labels"><legend><liferay-ui:message key="datos-prestador" /></legend>
							   <liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
							  		<liferay-util:param name="search_url" value="/cai/buscar_prestador"/>
							  		<liferay-util:param name="cuit_prestador" value=''/>
							  		<liferay-util:param name="nombre_prestador" value=''/>
							  		<liferay-util:param name="esEditable" value='<%= String.valueOf(true) %>'/>
							  		<liferay-util:param name="esLiquidadorHospital" value='<%= String.valueOf(false) %>'/>
								</liferay-util:include>
							  </fieldset>	
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="6">
						 <div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">
						     <fieldset class="block-labels"><legend>Empresa</legend>
						      <liferay-util:include page="/html/portlet/crm/busqueda_padron_entidades.jsp">
							  		<liferay-util:param name="esEditable" value='true'/>
							  		<liferay-util:param name="portlet_name" value='liquidaciones'/>
							  		<liferay-util:param name="suf" value='_busqueda'/>
							  		<liferay-util:param name="suf_entidad" value='_busqueda'/>														  		
							  </liferay-util:include>
							 </fieldset>	
						 </div>
						</td>
					</tr>
					
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-tipo" />:</label></td>
						<td><select name="<portlet:namespace />tipo_contactoBusq"
								id="<portlet:namespace />tipo_contactoBusq">			
								<option value="0">Todos los tipos de contacto</option>
								<% for(TipoContacto tc : tiposCrm) {%>
									<option value="<%=tc.getId() %>"  
									<%if (tipoSel == tc.getId()) { %> selected="selected" <%}%>><%=tc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-categoria" />:</label></td>
						<td><select name="<portlet:namespace />categoria_contactoBusq"
								id="<portlet:namespace />categoria_contactoBusq">			
								<option value="0">Todas las categorías de contacto</option>
								<% for(CategoriaContacto cc : categoriasCrm) {%>
									<option value="<%=cc.getId() %>"  
									<%if (categoriaSel == cc.getId()) { %> selected="selected" <%}%>><%=cc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-motivo" />:</label></td>
						<td><select name="<portlet:namespace />motivo_contactoBusq"
								id="<portlet:namespace />motivo_contactoBusq">
								<option value="0">Todos los motivos de contacto</option>			
								<% for(MotivoContacto mc : motivosCrm) {%>
									<option value="<%=mc.getId() %>"  
									<%if (motivoSel == mc.getId()) { %> selected="selected" <%}%>><%=mc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-estado" />:</label></td>
						<td><select name="<portlet:namespace />estado_contactoBusq"
								id="<portlet:namespace />estado_contactoBusq">
								<option value="">Todos los estados de contacto</option>			
								<% for(int i = 0; i < WebKeysCrm.CRM_ESTADOS.length; i++){ %>
									<option value="<%=WebKeysCrm.CRM_ESTADOS[i] %>" 
										<% if (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(estadoSel) ) { %> selected="selected" <%}%> ><%=WebKeysCrm.CRM_ESTADOS[i]%></option>
								<%} %>
							</select>
						</td>
						<%-- <td colspan="2"><liferay-ui:message key="crm-contacto-incumplContrato" />: <input type="checkbox" name="<portlet:namespace/>incumplContrato"  
							 id="<portlet:namespace/>incumplContrato" > &nbsp;&nbsp;</td> --%>
						<td><liferay-ui:message key="crm-contacto-incumplContrato" />:</td>
						<td>	 
							 <select name="<portlet:namespace/>incumplContrato" id="<portlet:namespace/>incumplContrato">
							 					<option value="99" <%if(incumContrato == 99){ %>  selected="selected"  <%} %> > Todos</option>
							 					<option value="1"  <%if(incumContrato == 1) { %>  selected="selected"  <%} %> > Sin Cumplimiento</option>
							 					<option value="0"  <%if(incumContrato == 0) { %>  selected="selected"  <%} %> > Con Cumplimiento</option>
							 				</select>
						</td>	 				
					</tr>
					<tr><td><label>Incluir A:</label></td>
					    <td> <select name="<portlet:namespace/>incluirA" id="<portlet:namespace/>incluirA">
							 					<option value="0" <%if(incluirA == 0){ %>  selected="selected"  <%} %> > Todos</option>
							 					<option value="1" <%if(incluirA == 1){ %>  selected="selected"  <%} %> > Solo Afiliados</option>
							 					<option value="2" <%if(incluirA == 2){ %>  selected="selected"  <%} %> > Solo No Afiliados</option>
							 					</select> &nbsp;&nbsp;
							 
							<%--  <liferay-ui:message key="crm-contacto-importancia" />: <input type="checkbox" name="<portlet:namespace/>importancia"  
							 id="<portlet:namespace/>importancia" > &nbsp;&nbsp; --%>
							 <liferay-ui:message key="crm-contacto-importancia" />:
							 <select name="<portlet:namespace/>importancia" id="<portlet:namespace/>importancia">
							 					<option value="99" <%if(importancia == 99){ %>  selected="selected"  <%} %> > Todos</option>
							 					<option value="1"  <%if(importancia == 1) { %>  selected="selected"  <%} %> > Solo Importantes</option>
							 					<option value="0"  <%if(importancia == 0) { %>  selected="selected"  <%} %> > Solo Normales</option>
							 				</select>
						</td>
						<td colspan="4">
							<liferay-util:include page="/html/portlet/crm/derivacion_liferay.jsp">
									<liferay-util:param name="view" value="<%= new String(\"false\") %>"/>
							</liferay-util:include>
						</td>
				</tr>
				
				<tr>
					<td colspan="1">Plan:</td>
					<td colspan="1">
						<select name="<portlet:namespace/>plan_contactoBusq" id="<portlet:namespace/>plan_contactoBusq" style="width: 240px; ">
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
						<select name="<portlet:namespace/>planOmint_contactoBusq" id="<portlet:namespace/>planOmint_contactoBusq" style="width: 150px; ">
							<option value='0'>Todos los planes Omint</option>
							<option value="3" <%if(planOmintSel==3){ %> selected="selected" <%} %> >OSPIM_0</option>
							<option value="1" <%if(planOmintSel==1){ %> selected="selected" <%} %> >OSPIM_1</option>
							<option value="2" <%if(planOmintSel==2){ %> selected="selected" <%} %> >OSPIM_2</option>
							<option value="4" <%if(planOmintSel==4){ %> selected="selected" <%} %> >OSPIM_2A</option>
						</select>
					</td>
					
					<td><liferay-ui:message key="crm-eficacia-busq-conf" />:	</td>
					<td> 
						 <select name="<portlet:namespace/>eficacia_conf" id="<portlet:namespace/>eficacia_conf">
		 					<option value="99" <%if(eficaciaSel == 99){ %>  selected="selected"  <%} %> > Todos</option>
		 					<option value="1"  <%if(eficaciaSel == 1) { %>  selected="selected"  <%} %> > Con Conformidad</option>
		 					<option value="0"  <%if(eficaciaSel == 0) { %>  selected="selected"  <%} %> > Sin Conformidad</option>
		 				</select>
					</td>
				</tr>
				<tr>
				    <td><label><liferay-ui:message key="seccional" />:</label></td>
				    <td colspan="5">
						<liferay-util:include page="/html/portlet/autorizaciones/busqueda_seccional.jsp">
						   <liferay-util:param name="prefijo" value='_sec'/>
						</liferay-util:include>
				 </td>
				 </tr>
				 <tr>
			    	<td><label><liferay-ui:message key="nro-documento_no_afi" />:</label>	</td>	
					<td>	<input id="<portlet:namespace />noAfiliadoDocNumero"
						name="<portlet:namespace />noAfiliadoDocNumero" size="8"
						maxlength="8" type="text"    onKeyPress="return soloNumeros(event)" 
						value="<%=noAfiliadoDocNumero != null ? noAfiliadoDocNumero : ""%>"  />
				</td>	
				 
				 <td><label><liferay-ui:message key="situacion-medica" />:</label></td>
						<td><select name="<portlet:namespace />situacion_Busq"
								id="<portlet:namespace />situacion_Busq">
								<option value="0">Todos las situaciones médicas</option>			
								<% for(TiposDeSituacionesMedicas mc :situacionesMedicas) {%>
									<option value="<%=mc.getId() %>"  
									<%if (situacionSel == mc.getId()) { %> selected="selected" <%}%>><%=mc.getDescripcion() %></option> 
								<%} %>
							</select>
				</td>
				 
				 
			    </tr>
			    
			   
				<tr>	
					 <td colspan="6">							
						<input type="button"
							   id="<portlet:namespace />buscar" 
							   value="<liferay-ui:message key="buscar"/>" 
							   title="<liferay-ui:message key="buscar" />" 
							   onclick="<portlet:namespace />buscarContactos();" />							
					<%-- &nbsp;&nbsp;						
					<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCampos()'/> --%>
					
					&nbsp;&nbsp;
					<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" 
								onClick="<portlet:namespace />reporteXLS();" />
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
			<div align="center" id="<portlet:namespace />busquedaContactosDiv">
			</div>
		</fieldset> 
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();
	
	function <portlet:namespace />buscarContactos(){
		jQuery('#<portlet:namespace />buscando').show();

		var n_contacto=jQuery('#<portlet:namespace />numero_contactoBusq').val();		
		var cuil_titular=jQuery('#<portlet:namespace />cuil').val();
		var integ=jQuery('#<portlet:namespace />inte').val();		
		var estado=jQuery('#<portlet:namespace/>estado_contactoBusq').val();
		var motivo=jQuery('#<portlet:namespace/>motivo_contactoBusq').val();
		var categoria=jQuery('#<portlet:namespace/>categoria_contactoBusq').val();
		var tipo=jQuery('#<portlet:namespace/>tipo_contactoBusq').val();
		
		var incluirAquienes = parseInt(jQuery('#<portlet:namespace/>incluirA').val());	 
		
/* 		var importancia_chk = document.getElementById("<portlet:namespace />importancia");
		var importancia = importancia_chk.checked ? 1 : 0;	
		var inc_contrato_chk = document.getElementById("<portlet:namespace />incumplContrato");
		var incumpliContrato = inc_contrato_chk.checked ? 1 : 0;	 */

		var importancia=jQuery('#<portlet:namespace />importancia').val();
		var incumpliContrato=jQuery('#<portlet:namespace/>incumplContrato').val();
		var eficaConform=jQuery('#<portlet:namespace/>eficacia_conf').val();
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDiaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMesBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnioBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
	    var sec_dest=jQuery('#<portlet:namespace/>sector_destino').val();
	    var usu_dest=jQuery('#<portlet:namespace/>usuario_destino').val();
		var plan=jQuery('#<portlet:namespace/>plan_contactoBusq').val();
		var planOmint=jQuery('#<portlet:namespace/>planOmint_contactoBusq').val();
	    
		/* var total_reg = jQuery('#<portlet:namespace />total_registros').val(); */
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		
		var idPrestador=jQuery('#<portlet:namespace />id_prestador').val();
		/* var viene_de = jQuery('#<portlet:namespace />viene_de').val(); */
		
		/*
		if(cuil_titular.length>0){
			if(!validarCuil(cuil_titular,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
		} */
		if(trim(sec_dest).length > 0 && trim(usu_dest).length > 0){
			// tambien esta bien porque se selecciono ambos 
		}else{
			//forzamos blanco los 2, uno solo seleccionado no va...
			sec_dest = '';
			usu_dest = '';
		} 
		
		var seccional=jQuery('#<portlet:namespace />id_seccional_sec').val();
		
		var noAfiliadoDocNumero =jQuery('#<portlet:namespace />noAfiliadoDocNumero').val();
		
		var cuit=jQuery("#<portlet:namespace />cuit_entidad_busqueda").val();
		var sucu=jQuery("#<portlet:namespace />sucursal_entidad_busqueda").val();
		var situacion=jQuery('#<portlet:namespace/>situacion_Busq').val();
		
		var busquedaContactos = { "estado": estado, "motivo": motivo, "categoria": categoria, "tipo": tipo, 
								  "fechaDesdeFinal": fechaDesdeFinal, "fechaHastaFinal": fechaHastaFinal, 
								  "incluirA": incluirAquienes, 
								  "cuil_titular": cuil_titular, "inte":integ,
								  "nro_contacto":n_contacto, "sector":sec_dest, 
								  "usuario":usu_dest, 
								  "plan":plan, "planOmint":planOmint,
								  "importancia":importancia,
								  "incumplimientoContrato":incumpliContrato,
								  "eficaciaConform":eficaConform,
								  "pagina" : offset_reg,"seccional":seccional , 
								  "noAfiliadoDocNumero":noAfiliadoDocNumero,
								  "idPrestador":idPrestador,
								  "cuit":cuit,
								  "sucursal":sucu,
								  "situacion_medica":situacion};
		

		//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/busqueda_contactosCRM" /></portlet:renderURL>';
	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/busqueda_contactosCRM';
		
	    jQuery('#<portlet:namespace />busquedaContactosDiv').load(url,busquedaContactos, function() {
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
		jQuery('#<portlet:namespace />motivo_contactoBusq').val('0');
		jQuery('#<portlet:namespace />tipo_contactoBusq').val('0');
		jQuery('#<portlet:namespace />categoria_contactoBusq').val('0');
		jQuery('#<portlet:namespace />estado_contactoBusq').val('');
	} */
	
	function <portlet:namespace />reporteXLS(){
		var n_contacto=jQuery('#<portlet:namespace />numero_contactoBusq').val();		
		var cuil_titular=jQuery('#<portlet:namespace />cuil').val();
		var integ=jQuery('#<portlet:namespace />inte').val();		
		var estado=jQuery('#<portlet:namespace/>estado_contactoBusq').val();
		var motivo=jQuery('#<portlet:namespace/>motivo_contactoBusq').val();
		var categoria=jQuery('#<portlet:namespace/>categoria_contactoBusq').val();
		var tipo=jQuery('#<portlet:namespace/>tipo_contactoBusq').val();

		var incluirAquienes = parseInt(jQuery('#<portlet:namespace/>incluirA').val());	 
		var eficaConform=jQuery('#<portlet:namespace/>eficacia_conf').val();

		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDiaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMesBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnioBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
	    var sec_dest=jQuery('#<portlet:namespace/>sector_destino').val();
	    var usu_dest=jQuery('#<portlet:namespace/>usuario_destino').val();
	    var plan=jQuery('#<portlet:namespace/>plan_contactoBusq').val();
		var planOmint=jQuery('#<portlet:namespace/>planOmint_contactoBusq').val();
		
		var importancia=jQuery('#<portlet:namespace />importancia').val();
		var incumpliContrato=jQuery('#<portlet:namespace/>incumplContrato').val();
		
		var noAfiliadoDocNumero=jQuery('#<portlet:namespace/>noAfiliadoDocNumero').val();


	    if(trim(sec_dest).length > 0 && trim(usu_dest).length > 0){
			// tambien esta bien porque se selecciono ambos 
		}else{
			//forzamos blanco los 2, uno solo seleccionado no va...
			sec_dest = '';
			usu_dest = '';
		}
		
	    var seccional=jQuery('#<portlet:namespace />id_seccional_sec').val();
	    var situacion=jQuery('#<portlet:namespace/>situacion_Busq').val();
	    
		 window.location.href ='/xlsservlet/?reporte=REPORTE_BUSQUEDA_CONTACTOSCRM'
			 				+'&estado='+estado
			 				+'&motivo='+motivo
			 				+'&categoria='+categoria
			 				+'&tipo='+tipo 
			  				+'&fechaDesdeFinal='+fechaDesdeFinal
			  				+'&fechaHastaFinal='+fechaHastaFinal 
			  				+'&incluirA='+incluirAquienes 
			 				+'&cuil_titular='+cuil_titular
			 				+'&inte='+integ  
		 					+'&nro_contacto='+n_contacto
		 					+'&sector='+sec_dest 
		  					+'&usuario='+usu_dest
		  					+'&importancia='+importancia
		  					+'&incumplimientoContrato='+incumpliContrato	
		  					+'&plan='+plan
		  					+'&planOmint='+planOmint
		  					+'&eficaciaConform='+eficaConform
		  					+'&seccional='+seccional
		  					+'&noAfiliadoDocNumero='+noAfiliadoDocNumero
		  					+'&situacion_medica='+situacion;
		 
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
 	 
 	function verCrmContacto(idContSerial) {
 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
 		params = params + '&idContactoSerial='+idContSerial;
 		
 		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});

 		/* popupCRM= Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true,width:880,height:655,fixedcenter:true}); */
 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';
 		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
 		   url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';
 		</c:if>
 		url = url + params;
 		jQuery(popupCRM).load(url);	
 	}

 	function cargaVerificacionEficacia(id_contacto) {

 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.ADD %>";
 		params = params + '&id_contacto='+id_contacto;
 		params = params + '&accion='+ "<%= Constants.ADD %>";
 		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_eficacia_entry" /></portlet:renderURL>';
 		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
 		    url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cai/editar_eficacia_entry" /></portlet:renderURL>';
 		</c:if>
 		url = url + params;
 		document.<portlet:namespace />fm.method = 'post';
 		submitForm(document.<portlet:namespace />fm, url);
 	}
 	
 	function soloNumeros(e) { 
		var key = window.Event ? e.which : e.keyCode 
		return ((key >= 48 && key <= 57) || (key==8)) 
	}
 	 	
 	
 </script>

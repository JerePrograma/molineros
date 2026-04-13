<%@page import="ar.com.ospim.util.StringUtils"%>

<%@ include file="/html/portlet/afiliados/init.jsp"%>
<% 
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	String accion = (String)session.getAttribute(Constants.CMD);
	boolean baja_cascada = Boolean.getBoolean((String) request.getAttribute("baja_cascada1"));
	String preCarga = (String) session.getAttribute("pre_carga");

	Afiliado preAfiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION);

	Calendar fechaVigDesdeNuevo = Calendar.getInstance();
	Calendar fechaVigHastaNuevo = Calendar.getInstance();
	
	AfiPlan afiPlanNuevo = (AfiPlan) request.getSession().getAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION);
	
	ArrayList<MotivoBaja> motivosBaja=(ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,PortletSession.APPLICATION_SCOPE);
	ArrayList<Plan> planes = (ArrayList<Plan>) request.getSession().getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);
	
	Afiliado afiliado=(Afiliado)request.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	AfiPlan afiPlan = afiliado.getAfiPlan();
		
 	boolean esView = false;
/*	if (viewStr != null){
		esView = true;
	} 
	if(accion != null && accion.equalsIgnoreCase("add")){ 
		esView = true;
	}*/

	if(baja_cascada){
		esView = true;
	}
	
	if(afiPlanNuevo == null){
		afiPlanNuevo = new AfiPlan();
		afiPlanNuevo.setVigenDesde(afiliado.getVigen_fecha());
	}
	
	fechaVigDesdeNuevo.setTime(afiPlanNuevo.getVigenDesde());
	
	if(afiPlanNuevo != null && afiPlanNuevo.getVigenHasta() != null){
		fechaVigHastaNuevo.setTime(afiPlanNuevo.getVigenHasta());
	}
	
	/* if(afiPlan != null && afiPlan.getPlan() != null){ // update
		fechaVigDesde.setTime(afiPlan.getVigenDesde());
		if(afiPlan.getVigenHasta() != null){	
			fechaVigHasta.setTime(afiPlan.getVigenHasta());
			fechaVigDesdeNuevo.setTime(afiPlan.getVigenHasta());
			fechaVigDesdeNuevo.add(Calendar.DATE, 1);
		}
		
	}else{ // alta afiliado, ponemos como fecha Vigencia desde, la vigencia del afiliado
		fechaVigDesde.setTime(afiliado.getVigen_fecha());
		fechaVigDesdeNuevo.setTime(afiliado.getVigen_fecha());
	} */
	if(accion.equalsIgnoreCase(Constants.ADD) && preCarga!=null && preCarga.equalsIgnoreCase("true") ){ // desde PreCarga
		if(afiliado.getAfiPlan() != null){
			afiPlanNuevo = afiliado.getAfiPlan();
			fechaVigDesdeNuevo.setTime(afiPlanNuevo.getVigenDesde());
			if(afiliado.getAfiPlan().getVigenHasta() != null){
				fechaVigHastaNuevo.setTime(afiPlanNuevo.getVigenHasta());
			} 
		}	
	}	
	/* }else{ //UPDATE
		afiPlan = new AfiPlan();
		if(preAfiliado.getAfiPlan() != null){
			afiPlan.setPlan(new Plan(preAfiliado.getAfiPlan().getPlan().getId(),preAfiliado.getAfiPlan().getPlan().getDescripcion()));
			afiPlan.setVigenDesde(preAfiliado.getAfiPlan().getVigenDesde());
			fechaVigDesde.setTime(afiPlan.getVigenDesde());
			if(preAfiliado.getAfiPlan().getVigenHasta() != null){
				afiPlan.setVigenHasta(preAfiliado.getAfiPlan().getVigenHasta());
				afiPlan.setMotivoBaja(preAfiliado.getAfiPlan().getMotivoBaja());
				fechaVigHasta.setTime(afiPlan.getVigenHasta());
			}
		}	
		
	} */
	
%>
<!-- 	<table class="lfr-table">	 -->
<table class="lfr-table">
	<tr>
		<!-- <th>&nbsp;</th> -->
		<th><liferay-ui:message	key="descripcion" /></th>
		<!-- <th>&nbsp;Omint</th> -->	
		<th><liferay-ui:message	key="plan-emergencias" />&nbsp;/&nbsp;<liferay-ui:message key="plan-farmacia" /></th>	
		<th><liferay-ui:message	key="desde" /></th>
		<%if(afiPlanNuevo.getVigenHasta() != null){ %>	
   			<th><liferay-ui:message	key="hasta" /></th>	
			<th><liferay-ui:message	key="motivo-baja" /></th>
		<%} %>
		<th>&nbsp;</th>
	</tr>
	<tr>
		<!-- <td>&nbsp;</td> -->
		<td>
			<select <% if ( preCarga==null && ((afiliado.getInte() != 0) || afiPlanNuevo.getPlan() != null)) { %> <%="disabled='disabled'" %> <%}%>  
				name="<portlet:namespace/>nuevoPlan" id="<portlet:namespace/>nuevoPlan" 
				onchange="<portlet:namespace />filtrarPlanOmint();<portlet:namespace />enlazarTercerizadora();"
				onkeydown="javascript:<portlet:namespace />movioLista(event);" 
				style="width: 240px; ">
				<option value='0'><liferay-ui:message key="seleccione-plan" /></option>
				<%if(planes!=null){ for (Plan plan : planes) { %>
					<!-- Evitamos nos cambien el plan por el mismo plan -->
					<%if(afiPlan !=null ) { %>
						<option value="<%= plan.getId()%>"  
							<%if(afiPlanNuevo.getPlan()!=null 
							&& afiPlanNuevo.getPlan().getId()==plan.getId()){ %> selected="selected" <%} %> ><%=plan.getDescripcion()%></option>
					<% } else { %>
						<option value="<%= plan.getId()%>"  
							<%if(afiPlanNuevo.getPlan()!=null 
							&& afiPlanNuevo.getPlan().getId()==plan.getId()){ %> selected="selected" <%} %> ><%=plan.getDescripcion()%></option>
					<% } %>	
				<% }} %>
			</select>
		</td>		
		<td>&nbsp;
			<input id="<portlet:namespace />nuevoPlanOmintId" name="<portlet:namespace />nuevoPlanOmintId" type="hidden" value="<%=afiPlanNuevo.getPlan()!=null?afiPlanNuevo.getId_plan_omint():0 %>" />
			<%-- <input id="<portlet:namespace />nuevoPlanOmint" name="<portlet:namespace />nuevoPlanOmint" type="text" value="" readonly="readonly" style="width: 60px; " /> --%>
			<input id="<portlet:namespace />nuevoPlanOmint" name="<portlet:namespace />nuevoPlanOmint" type="hidden" value="" readonly="readonly" style="width: 60px; " />
			<input id="<portlet:namespace />nuevoPlanPrevencion" name="<portlet:namespace />nuevoPlanPrevencion" type="text" value="" readonly="readonly" style="width: 15px; " />
			<input id="<portlet:namespace />nuevoFarmaciaPrevencion" name="<portlet:namespace />nuevoFarmaciaPrevencion" type="text" value="" readonly="readonly" style="width: 10px; " />
			<input id="<portlet:namespace/>nuevoPlanAutom" name="<portlet:namespace/>nuevoPlanAutom" type="hidden" value="<%=afiPlanNuevo.getPlan()!=null?afiPlanNuevo.getPlan().getId():0 %>" />
		</td>
		<td>
 			<liferay-ui:input-date 
				dayParam="nuevoPlanVigenDesdeDia"
				dayValue="<%= fechaVigDesdeNuevo.get(Calendar.DATE)%>"
				monthParam="nuevoPlanVigenDesdeMes"
				monthValue="<%= fechaVigDesdeNuevo.get(Calendar.MONTH) %>"
				yearParam="nuevoPlanVigenDesdeAnio"
				yearValue="<%= fechaVigDesdeNuevo.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaVigDesdeNuevo.get(Calendar.YEAR) - 40 %>"
				yearRangeEnd="<%= fechaVigDesdeNuevo.get(Calendar.YEAR)+20%>"
				firstDayOfWeek="<%= fechaVigDesdeNuevo.getFirstDayOfWeek() - 1 %>"
				disabled="true"  /> 
			<input type="hidden" name="nuevoPlanVigenDesde" id="nuevoPlanVigenDesde" value="<%= sdf.format(fechaVigDesdeNuevo.getTime()) %>" ></td>	
		 <!-- El nuevo plan no tendra fechaVigenHasta ni motivo de Baja por ser alta cuando es alta, 
		 si viene de baja cascada, el alta ya viene pre-armada y tendra fecha de vig hasta y mot de baja -->
		<%if(afiPlanNuevo.getVigenHasta() != null){ %>
			<td>
			<liferay-ui:input-date 
				dayParam="nuevoPlanVigenHastaDia"
				dayValue="<%= fechaVigHastaNuevo.get(Calendar.DATE)%>"
				monthParam="nuevoPlanVigenHastaMes"
				monthValue="<%=fechaVigHastaNuevo.get(Calendar.MONTH) %>"
				yearParam="nuevoPlanVigenHastaAnio"
				yearValue="<%=fechaVigHastaNuevo.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaVigHastaNuevo.get(Calendar.YEAR) - 40 %>"
				yearRangeEnd="<%= fechaVigHastaNuevo.get(Calendar.YEAR)+20%>"
				firstDayOfWeek="<%= fechaVigHastaNuevo.getFirstDayOfWeek() - 1 %>"
				disabled="true"  />
			<input type="hidden" name="nuevoPlanVigenHasta" id="nuevoPlanVigenHasta" value="<%= sdf.format(fechaVigHastaNuevo.getTime()) %>" >
			</td>
			<td>
				<select disabled='disabled'
					name="<portlet:namespace/>motivoBajaPlan" id="<portlet:namespace/>motivoBajaPlan" style="width: 20em;" >
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivosBaja) { %>
						<option <% if(afiPlanNuevo!=null 
									&& afiPlanNuevo.getMotivoBaja() !=null 
									&& afiPlanNuevo.getMotivoBaja().getId_motivo_baja() == motivoBaja.getId_motivo_baja()){  %>
						 			selected="selected" <%} %>
						value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%} %>
				</select>
				<input type="hidden" name="motivoBajaPlanNuevo" id="motivoBajaPlanNuevo" value="<%= afiPlanNuevo.getMotivoBaja().getId_motivo_baja() %>" ></td>	
				
			</td>
		<%} %>
		<%if(!baja_cascada || accion==null || (accion!=null && !accion.equalsIgnoreCase("add"))){ %>
		<td><span style="font-size: 7pt">
				<a href="#" onclick="javascript:<portlet:namespace />ocultarNuevoPlan();">ocultar</a>
			</span>	 
		</td>
		<%} %>
	</tr>
</table>		

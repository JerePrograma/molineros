<%@page import="ar.com.ospim.util.StringUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>

<%@ include file="/html/portlet/afiliados/init.jsp"%>
<% 
	boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();

	/* ArrayList<MotivoBaja> motivosBaja=(ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,PortletSession.APPLICATION_SCOPE); */
	ArrayList<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) TraeListasServiceUtil.getMotivosBaja();
	
	ArrayList<Plan> planes = (ArrayList<Plan>) portletSession.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION, PortletSession.APPLICATION_SCOPE);
	
	Afiliado afiliado=(Afiliado)request.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	AfiPlan afiPlan = afiliado.getAfiPlan();
		
 	boolean esView = true;
	
	Calendar fechaVigDesde = Calendar.getInstance();
 	Calendar fechaVigHasta = null;
	
	if(afiPlan != null && afiPlan.getPlan() != null){ 
		fechaVigDesde.setTime(afiPlan.getVigenDesde());
		if(afiPlan.getVigenHasta() != null){
			fechaVigHasta = Calendar.getInstance();
			fechaVigHasta.setTime(afiPlan.getVigenHasta());
		}
		
	}
	
%>


<fieldset class="block-labels">
<legend><liferay-ui:message	key="planes-prestadores" /></legend>
<%if(showABMButtons){%>
<a href="javascript:mostrarPlanHisto();" id="<portlet:namespace />mostrarPlanHistoLink" ><liferay-ui:message key="ver-historico" /></a>
<a href="javascript:ocultarPlanHisto();" style="display: none;" id="<portlet:namespace />ocultarPlanHistoLink" ><liferay-ui:message key="ocultar-historico" /></a>
	<div align="left" id="<portlet:namespace />histo_planes">
	   <liferay-util:include page='/html/portlet/afiliados/historico_aportes_search_result.jsp' />
	</div>
<%} %>	
	<table class="lfr-table">	
		
		<%if(afiliado != null && afiPlan != null){ %>
		<tr>
			<th>&nbsp;</th>
			<th><liferay-ui:message	key="descripcion" /></th>
			<!-- <th>Omint</th>	 -->
			<th><liferay-ui:message	key="plan-emergencias" />&nbsp;/&nbsp;<liferay-ui:message key="plan-farmacia" /></th>
			<th><liferay-ui:message	key="desde" /></th>	
			<th><liferay-ui:message	key="hasta" /></th>	
			<th><liferay-ui:message	key="motivo-baja" /></th>	
			<th>&nbsp;</th>
			<th>&nbsp;</th>		 
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input id="<portlet:namespace />plan_vig_desc" name="<portlet:namespace />plan_vig_desc" type="text" value="<%=afiPlan.getPlan().getDescripcion()%>" readonly="readonly" style="width: 240px; " /></td>
			<%-- <td><input id="<portlet:namespace />plan_omint_desc" name="<portlet:namespace />plan_omint_desc" type="text" value="<%=afiPlan.getPlan().getDescripcionOmint()!=null?afiPlan.getPlan().getDescripcionOmint():new String("")%>" readonly="readonly" style="width: 60px; " /></td> --%>
			<td>
				<input id="<portlet:namespace />plan_omint_desc" name="<portlet:namespace />plan_omint_desc" type="text" 
					value="<%=afiPlan.getPlan().getDescripcionEnsalud()!=null?afiPlan.getPlan().getDescripcionEnsalud():new String("")%>" readonly="readonly" style="width: 70px; " />
				&nbsp;/&nbsp;
				<input id="<portlet:namespace />plan_farmacia_desc" name="<portlet:namespace />plan_farmacia_desc" type="text" 
					value="<%=afiPlan.getPlan().getFarmaciaEnsalud()!=null?afiPlan.getPlan().getFarmaciaEnsalud():new String("")%>" readonly="readonly" style="width: 10px; " />
			</td>
			<td><liferay-ui:input-date 
				dayParam="fechaVigenDesdeDia"
				dayValue="<%= fechaVigDesde.get(Calendar.DATE)%>"
				monthParam="fechaVigenDesdeMes"
				monthValue="<%= fechaVigDesde.get(Calendar.MONTH) %>"
				yearParam="fechaVigenDesdeAnio"
				yearValue="<%= fechaVigDesde.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
				yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+20%>"
				firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
				disabled="<%= true %>" /></td>
			<%if(fechaVigHasta == null){%>
				<td><liferay-ui:input-date
					dayNullable="true"
					dayParam="fechaVigenHastaDia"
					monthNullable="true" 
					monthParam="fechaVigenHastaMes"
					yearNullable="true"
					yearParam="fechaVigenHastaAnio"
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+20%>"
					firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" /></td>
			<%}else{%>
				<td><liferay-ui:input-date
					dayNullable="true"
					dayParam="fechaVigenHastaDia"
					dayValue="<%= fechaVigHasta.get(Calendar.DATE)%>"
					monthNullable="true" 
					monthParam="fechaVigenHastaMes"
					monthValue="<%= fechaVigHasta.get(Calendar.MONTH) %>"
					yearNullable="true"
					yearParam="fechaVigenHastaAnio"
					yearValue="<%= fechaVigHasta.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaVigHasta.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigHasta.get(Calendar.YEAR)+20%>"
					firstDayOfWeek="<%= fechaVigHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" /></td>
			<%} %>		
			<td>
				<select disabled='disabled' 
					name="<portlet:namespace/>motivoBajaPlan" id="<portlet:namespace/>motivoBajaPlan" style="width: 20em;" >
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivosBaja) { %>
						<option <%= afiPlan.getMotivoBaja() !=null && afiPlan.getMotivoBaja().getId_motivo_baja() == motivoBaja.getId_motivo_baja() ? "selected" : "" %> 
								value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%} %>
				</select>
			</td>
			<%-- <td colspan="7">
				<input type="button" value="<liferay-ui:message key="ver-historico" />"	onClick="<portlet:namespace />verHistorico();" />
			</td> --%>
		 <!--%if(afiPlan.getPlan().isOspim()){ %-->
		 <%-- 		
				<tr>
					<td colspan="7">
						<liferay-ui:message key="nro-socio-prevencion" />:
						<input id="<portlet:namespace />nro_socio_prev" name="<portlet:namespace />nro_socio_prev" type="text" readonly="readonly" size="5" />
						&nbsp;&nbsp;
						<liferay-ui:message key="nro-credencial-prevencion" />:
						<input id="<portlet:namespace />nro_creden_prev" name="<portlet:namespace />nro_creden_prev" type="text" readonly="readonly" size="8" />
					</td>	
				</tr> --%>
			<!--%} %-->
		<%} %>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>	
		<%if(afiPlan != null){ %>
		<tr>
			<td>&nbsp;</td>
			<td colspan="7">
				<div id="divVerAfiAportes" align="left" >
					 <liferay-util:include page='/html/portlet/afiliados/ver_afi_aportes.jsp' />
				</div>
			</td>
		</tr>
		<%} %>

		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>
	</table>
	
</fieldset>
	
			

<script type="text/javascript">
function <portlet:namespace />mostrarAfiliacionPrevencion() {
	var cuil_titu_ = <%=afiliado.getCuil_titular()%>
	var inte_ = <%=afiliado.getInteAsString()%>
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliacion_prevencion&cuil_titular='+cuil_titu_+'&inte='+inte_;		
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var nroSocioPrev = obj.nroSocioPrev;
			var credencialPrevencion = obj.credencialPrevencion;
			
			if(nroSocioPrev!=0 && credencialPrevencion!=0){						
				jQuery('#<portlet:namespace/>nro_socio_prev').val(nroSocioPrev);
				jQuery('#<portlet:namespace/>nro_creden_prev').val(credencialPrevencion);
			}else{
				jQuery('#<portlet:namespace/>nro_socio_prev').val('');
				jQuery('#<portlet:namespace/>nro_creden_prev').val('');
			}
		}				                                                                                                                                                                                                                                                            
		
	});
}
<%if(afiPlan != null && afiPlan.getPlan().isOspim()){ %>
	<portlet:namespace />mostrarAfiliacionPrevencion();
<%}%>
</script>

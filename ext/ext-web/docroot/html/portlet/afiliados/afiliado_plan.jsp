<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>

<% 
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	String accion = (String)session.getAttribute(Constants.CMD);
	String preCarga = (String) session.getAttribute("pre_carga");

	
	ArrayList<MotivoBaja> motivosBaja=(ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,PortletSession.APPLICATION_SCOPE);
	ArrayList<Plan> planes = (ArrayList<Plan>) portletSession.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION, PortletSession.APPLICATION_SCOPE);
	
	AfiPlan afiPlanNuevo = (AfiPlan) request.getSession().getAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION);
	Afiliado afiliado=(Afiliado)request.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	AfiPlan afiPlan = (preCarga==null||(preCarga!=null&&accion.equalsIgnoreCase(Constants.UPDATE)))?afiliado.getAfiPlan():null;
		
	boolean esView = false;

	if(accion != null && accion.equalsIgnoreCase("add")){ 
		esView = true;
	}
	
	Calendar fechaVigDesde = Calendar.getInstance();
 	Calendar fechaVigHasta = null;
	
	if(afiPlan != null && afiPlan.getPlan() != null){ // update
		fechaVigDesde.setTime(afiPlan.getVigenDesde());
		if(afiPlan.getVigenHasta() != null){
			fechaVigHasta = Calendar.getInstance();
			fechaVigHasta.setTime(afiPlan.getVigenHasta());
		}
		
	}else{ // alta afiliado, ponemos como fecha Vigencia desde, la vigencia del afiliado
		fechaVigDesde.setTime(afiliado.getVigen_fecha());
 	}
	
%>

<fieldset class="block-labels"> 
<legend><liferay-ui:message	key="planes-prestadores" /></legend>
<a href="javascript:mostrarPlanHisto();" id="<portlet:namespace />mostrarPlanHistoLink" ><liferay-ui:message key="ver-historico" /></a>
<a href="javascript:ocultarPlanHisto();" style="display: none;" id="<portlet:namespace />ocultarPlanHistoLink" ><liferay-ui:message key="ocultar-historico" /></a>
	<div align="left" id="<portlet:namespace />histo_planes">
	   <liferay-util:include page='/html/portlet/afiliados/historico_aportes_search_result.jsp' />
	</div>
	
<table style="border-collapse: separate; border-spacing: 5px; width: 100%;">	 <!--  class="lfr-table" --> 
		
		<%if(afiliado != null && afiPlan != null){ %>
		<tr>
			<th><liferay-ui:message	key="descripcion" /></th>
			<!-- <th><liferay-ui:message	key="plan-emergencias" />&nbsp;/&nbsp;<liferay-ui:message key="plan-farmacia" /></th> -->
			<th><liferay-ui:message	key="plan-emergencias" />&nbsp;/&nbsp;<liferay-ui:message key="plan-farmacia" /></th>
			<th><liferay-ui:message	key="desde" /></th>	
			<th><liferay-ui:message	key="hasta" /></th>	
			<th><liferay-ui:message	key="motivo-baja" /></th>	
			<th>&nbsp;</th>
			<th>&nbsp;</th>		 
		</tr>
		<tr>
			<td><input id="<portlet:namespace />plan_estado" name="<portlet:namespace />plan_estado" type="hidden" value="<%=afiPlan.getEstado()%>"/>
			<input id="<portlet:namespace />plan_vig_desc" name="<portlet:namespace />plan_vig_desc" type="text" value="<%=afiPlan.getPlan().getDescripcion()%>" readonly="readonly" style="width: 200px; " /></td>
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
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR)%>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+60%>"
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
					yearRangeStart="<%= fechaVigHasta.get(Calendar.YEAR) -60%>"
					yearRangeEnd="<%= fechaVigHasta.get(Calendar.YEAR)+60%>"
					firstDayOfWeek="<%= fechaVigHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" /></td>
			<%} %>		
			<td>
				<select <% if (afiliado.getInte() != 0) { %> <%="disabled='disabled'" %> <%}%> 
					name="<portlet:namespace/>motivoBajaPlan" id="<portlet:namespace/>motivoBajaPlan" style="width: 200px; vertical-align: top;" 
					onchange="<portlet:namespace />aplicarReglasBajaPlan('',0);" >
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivosBaja) { %>
						<option <%= afiPlan.getMotivoBaja() !=null && afiPlan.getMotivoBaja().getId_motivo_baja() == motivoBaja.getId_motivo_baja() ? "selected" : "" %> 
								value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%} %>
				</select>
			</td>
			<%if(afiPlan != null && afiliado.getInte() == 0){ %>
				<td colspan="1">
					<input type="button" value="<liferay-ui:message key="cambio-plan" />" onClick="<portlet:namespace />verPlanes();" />
				<%-- &nbsp;
					<input type="button" value="<liferay-ui:message key="ver-historico" />"	onClick="<portlet:namespace />verHistorico();" /> --%>
				</td>
			<% } else {%>
				<td colspan="1">&nbsp;</td>
			<% } %>
		</tr>
		<tr>
			<%if(afiPlan!= null && afiPlan.getPlan() != null && afiPlan.getPlan().isOspim()){ %>
					<td colspan="5">
					<!--  >td colspan="5">
						<liferay-ui:message key="nro-socio-prevencion" />:
						<input id="<portlet:namespace />nro_socio_prev" name="<portlet:namespace />nro_socio_prev" type="text" readonly="readonly" size="5" />
						&nbsp;&nbsp;
						<liferay-ui:message key="nro-credencial-prevencion" />:
						<input id="<portlet:namespace />nro_creden_prev" name="<portlet:namespace />nro_creden_prev" type="text" readonly="readonly" size="8" />
					</td-->
					<%if(afiliado != null && afiliado.esTitular()){ %>
					<td>
						<input type="button" value="<liferay-ui:message key="cambios-cobertura" />"	onClick="<portlet:namespace />verCambioHistorico();" />
					</td>
					<%} %>	
			<%}else if(afiliado != null && afiliado.esTitular()){ %>
					<td colspan="6" align="right">
						<input type="button" value="<liferay-ui:message key="cambios-cobertura" />"	onClick="<portlet:namespace />verCambioHistorico();" />
					</td>	
			<%} else{%>
				<td colspan="6" align="right">&nbsp;</td>
			<%}%>
		</tr>	
		<%} %>
		
		<%if(afiPlan != null){ %>
		<tr>
			<!-- <td>&nbsp;</td> -->
			<td colspan="6">
				<table class="lfr-table" style="width: 100%" >
					<tr><td width="100%">
						<div id="divVerAfiAportes" align="left" >
					 		<liferay-util:include page='/html/portlet/afiliados/ver_afi_aportes.jsp' />
						</div>
					</td></tr>
				</table>
			</td>
		</tr>
		<%} %>
		<tr>
			<td colspan="6">
				<!-- Div para mostrar procesando -->
				<div align="center" id="<portlet:namespace />validando_reglas_baja_plan">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='validando-reglas-baja-plan'/></td>
							<td align="center">
								<img alt="<liferay-ui:message key='validando-reglas-baja-plan'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="6">
				<div id="<portlet:namespace />divNuevoPlan" >
					<liferay-util:include page='/html/portlet/afiliados/afiliado_plan_nuevo.jsp' />
				</div>				
			</td>
		</tr>
<!-- 		<tr>
			<td colspan="10">&nbsp;</td>
		</tr> -->
	</table>
	
</fieldset>
			

<script type="text/javascript">

<%if(afiPlan == null || (afiPlanNuevo != null && afiPlanNuevo.getPlan() != null)){ %>
jQuery('#<portlet:namespace />divNuevoPlan').show();
<%} else {%>
jQuery('#<portlet:namespace />divNuevoPlan').hide();

<%}%>

function <portlet:namespace />filtrarPlanOmint() {
	var idPlan = jQuery('#<portlet:namespace/>nuevoPlan').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_plan_omint&idPlan='+idPlan;		
	/* jQuery.ajax({   
		url: url,
		success: function(data) {					
			var obj = jQuery.parseJSON(data);
			for(var i = 0; i< obj.planOmint.length; i++) {
				var aux = obj.planOmint[i];
				var id_omint = aux.split('|')[0];
				var text = aux.split('|')[1];					
				if(id_omint!=0 && id_omint!="" && text!=null){						
					jQuery('#<portlet:namespace/>nuevoPlanOmintId').val(id_omint);
					jQuery('#<portlet:namespace/>nuevoPlanOmint').val(text);
				}else{
					jQuery('#<portlet:namespace/>nuevoPlanOmintId').val('');
					jQuery('#<portlet:namespace/>nuevoPlanOmint').val('');
				}
			}											
		}
	});	 */
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var idOmint = obj.planOmint;
			var descOmint = obj.descripcionOmint;
			var descPreven = obj.descripcionPrevencion;
			var farmPreven = obj.farmaciaPrevencion;
			
			if(idOmint!=0 && idOmint!=""){						
				jQuery('#<portlet:namespace/>nuevoPlanOmintId').val(idOmint);
				jQuery('#<portlet:namespace/>nuevoPlanOmint').val(descOmint);
				jQuery('#<portlet:namespace/>nuevoPlanPrevencion').val(descPreven);
				jQuery('#<portlet:namespace/>nuevoFarmaciaPrevencion').val(farmPreven);
			}else{
				jQuery('#<portlet:namespace/>nuevoPlanOmintId').val('');
				jQuery('#<portlet:namespace/>nuevoPlanOmint').val('');
				jQuery('#<portlet:namespace/>nuevoPlanPrevencion').val('');
				jQuery('#<portlet:namespace/>nuevoFarmaciaPrevencion').val('');
			}
		}				                                                                                                                                                                                                                                                            
		
	});
}
	
function <portlet:namespace />movioLista(event){
	
	
      if (parseInt(event.keyCode) == 40 || parseInt(event.keyCode) == 38) {
    
    	  /*alert('' + parseInt(event.keyCode));*/
    	  <portlet:namespace />filtrarPlanOmint();
    	  
    	  <portlet:namespace />enlazarTercerizadora();
      }
}
function <portlet:namespace />verPlanes() {
	
	if(<portlet:namespace />validarBajaPlanActual()){
		jQuery('#<portlet:namespace />divNuevoPlan').show();
		jQuery('#<portlet:namespace />plan_estado').val('<%=AfiPlan.ESTADOS.MODIFICADO%>');
	}
}

function <portlet:namespace />ocultarNuevoPlan(){
	jQuery("#<portlet:namespace />nuevoPlan option[value=0]").attr("selected",true);
	jQuery('#<portlet:namespace />divNuevoPlan').hide();
	jQuery('#<portlet:namespace />plan_estado').val('');
}

function <portlet:namespace />validarBajaPlanActual(){
	
	var bajaDia = jQuery('#<portlet:namespace />fechaVigenHastaDia').val();
	var bajaMes = jQuery('#<portlet:namespace />fechaVigenHastaMes').val(); 
/* 	var bajaMes=parseInt(jQuery('#<portlet:namespace />fechaVigenHastaMes').val())+1; */
	var bajaAnio = jQuery('#<portlet:namespace />fechaVigenHastaAnio').val();
/* 	var idMotBaja = parseInt(jQuery('#<portlet:namespace />motivoBajaPlan').val()); */
	var idMotBaja = jQuery('#<portlet:namespace />motivoBajaPlan').val();
	
	var desdeDia = jQuery('#<portlet:namespace />fechaVigenDesdeDia').val(); 
  	var desdeMes = jQuery('#<portlet:namespace />fechaVigenDesdeMes').val(); 
	var desdeAnio = jQuery('#<portlet:namespace />fechaVigenDesdeAnio').val();
	
	var mensaje="Debe completar los campos ";
	var sinError = true;
	
	if(bajaDia.length==0){
		mensaje=mensaje+" *<liferay-ui:message key="dia" />";
		sinError=false;
	}
	if(bajaMes.length==0){ 
		mensaje=mensaje+" *<liferay-ui:message key="mes" />";
		sinError=false;
	}
	if(bajaAnio.length==0){
		mensaje=mensaje+" *<liferay-ui:message key="anio" />";
		sinError=false;
	}
	if(!sinError){
		mensaje=mensaje+" de vigencia hasta";
		alert(mensaje);
		return false;
	}

	/* validamos que la fecha hasta no sea anterior o igual a la fecha vigen */
	if(parseInt(desdeAnio)==parseInt(bajaAnio)){
		if(parseInt(desdeMes)>parseInt(bajaMes)){					
			mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
			sinError=false;
		}else if(parseInt(desdeMes)==parseInt(bajaMes) && parseInt(desdeDia)>parseInt(bajaDia)){					
			mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
			sinError=false;
		}
	}else if(parseInt(desdeAnio)>parseInt(bajaAnio)){				
		mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
		sinError=false;				
	}
	
	if(!sinError){
		alert(mensaje);
		return false;
	}
	
	if(idMotBaja.length==0 || idMotBaja == "0"){
		alert('Debe seleccionar un motivo de baja para el plan actual');
		return false;
	}
	
	return true;
}


function <portlet:namespace />sinModificarPlanActual(){
	
	/* verificamos que no se completa el baja fecha ni motivo de baja, asi podemos considerar que el plan no sufrio modificaciones 
	y se presiona Guardar sin problemas...*/
	var sinCompletar = false;
	
	var bajaDia = jQuery('#<portlet:namespace />fechaVigenHastaDia').val();
	var bajaMes = jQuery('#<portlet:namespace />fechaVigenHastaMes').val(); 
	var bajaAnio = jQuery('#<portlet:namespace />fechaVigenHastaAnio').val();
	var idMotBaja = jQuery('#<portlet:namespace />motivoBajaPlan').val();
	
	if(bajaDia.length==0 && bajaMes.length==0 && bajaAnio.length==0 && (idMotBaja.length==0 || idMotBaja == "0") ){
		sinCompletar=true;
	}

	return sinCompletar;
}

function <portlet:namespace />mostrarAfiliacionPrevencion() {
	var cuil_titu_ = <%=afiliado.getCuil_titular()%>
	var inte_ = <%=afiliado.getInteAsString()%>
	var nroSocioPrev = 0;
	var credencialPrevencion = 0;
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliacion_prevencion&cuil_titular='+cuil_titu_+'&inte='+inte_;		
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			
			if(obj != Object){
				nroSocioPrev = obj.nroSocioPrev;
				credencialPrevencion = obj.credencialPrevencion;
			}
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

function <portlet:namespace />enlazarTercerizadora(){
	
	var cuil_titu_ = <%=afiliado.getCuil_titular()%>
	var inte_ = <%=afiliado.getInteAsString()%>
	
	jQuery('#<portlet:namespace />buscandoTercerizadoras').show();
	
	var id_plan = jQuery('#<portlet:namespace/>nuevoPlan').val();
	var planActualHastaDia = jQuery('#<portlet:namespace />fechaVigenHastaDia').val();
	var planActualHastaMes = jQuery('#<portlet:namespace />fechaVigenHastaMes').val();		
	var planActualHastaAnio = jQuery('#<portlet:namespace />fechaVigenHastaAnio').val();
	var planNuevoDesdeDia = jQuery('#<portlet:namespace />nuevoPlanVigenDesdeDia').val();
	var planNuevoDesdeMes = jQuery('#<portlet:namespace />nuevoPlanVigenDesdeMes').val();		
	var planNuevoDesdeAnio = jQuery('#<portlet:namespace />nuevoPlanVigenDesdeAnio').val();
	var planNuevoHastaDia = jQuery('#<portlet:namespace />nuevoPlanVigenHastaDia').val();
	var planNuevoHastaMes = jQuery('#<portlet:namespace />nuevoPlanVigenHastaMes').val();		
	var planNuevoHastaAnio = jQuery('#<portlet:namespace />nuevoPlanVigenHastaAnio').val();
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_tercerizadora';
	url +='&idPlanNuevo='+id_plan;
	url +='&fechaHastaDia='+planActualHastaDia;
	url +='&fechaHastaMes='+planActualHastaMes;
	url +='&fechaHastaAnio='+planActualHastaAnio;
	url +='&fechaDesdeDia='+planNuevoDesdeDia;
	url +='&fechaDesdeMes='+planNuevoDesdeMes;
	url +='&fechaDesdeAnio='+planNuevoDesdeAnio;
	url +='&fechaHastaNuevoDia='+planNuevoHastaDia;
	url +='&fechaHastaNuevoMes='+planNuevoHastaMes;
	url +='&fechaHastaNuevoAnio='+planNuevoHastaAnio;
	url +='&cuil_titular='+cuil_titu_+'&inte='+inte_;
	url +='&accion=ajustaTercAlCambioDePlan';
	url += '&rnd=' + Math.floor(Math.random()*100);
	 
	jQuery('#<portlet:namespace />tercerizadoras').load(url, function() {
			jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();     															
	});	
} 

function <portlet:namespace />verCambioHistorico(){	
	
	var url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/cambia_historico_cobertura_entry' /></portlet:actionURL>";
	
	submitForm(document.<portlet:namespace />fm, url);
}


<%if( afiPlan!=null && afiPlan.getPlan()!=null && afiPlan.getPlan().isOspim() ){ %>
	<portlet:namespace />mostrarAfiliacionPrevencion();
<%}%>
</script>

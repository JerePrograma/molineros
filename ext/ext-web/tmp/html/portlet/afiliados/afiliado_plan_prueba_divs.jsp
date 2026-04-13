<%@page import="ar.com.ospim.util.StringUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>

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
/*	if (viewStr != null){
		esView = true;
	} */
	if(accion != null && accion.equalsIgnoreCase("add")){ 
		esView = true;
	}
	
	Calendar fechaVigDesde = Calendar.getInstance();
/* 	Calendar fechaVigDesdeNuevo = Calendar.getInstance(); */
 	Calendar fechaVigHasta = null;
	
	if(afiPlan != null && afiPlan.getPlan() != null){ // update
		fechaVigDesde.setTime(afiPlan.getVigenDesde());
		if(afiPlan.getVigenHasta() != null){
			fechaVigHasta = Calendar.getInstance();
			fechaVigHasta.setTime(afiPlan.getVigenHasta());
			/* fechaVigDesdeNuevo.setTime(afiPlan.getVigenHasta());
			fechaVigDesdeNuevo.add(Calendar.DATE, 1); */
		}
		
	}else{ // alta afiliado, ponemos como fecha Vigencia desde, la vigencia del afiliado
		fechaVigDesde.setTime(afiliado.getVigen_fecha());
/* 		fechaVigDesdeNuevo.setTime(afiliado.getVigen_fecha()); */
 	}
	
%>

<fieldset class="block-labels"> 
<legend><liferay-ui:message	key="planes-prestadores" /></legend>

<div style="display: table; vertical-align: top;">
		<div id="<portlet:namespace />divPlanHisto" style="display: table-row;">
			<div id="F1_C1" style="display: table-cell;">
				<label><a href="javascript:mostrarPlanHisto();" id="<portlet:namespace />mostrarPlanHistoLink" ><liferay-ui:message key="ver-historico" /></a></label>
				<label><a href="javascript:ocultarPlanHisto();" id="<portlet:namespace />ocultarPlanHistoLink" ><liferay-ui:message key="ocultar-historico" /></a></label>
				<div align="left" id="<portlet:namespace />histo_planes">
	   				<liferay-util:include page='/html/portlet/afiliados/historico_aportes_search_result.jsp' />
				</div>			
			</div>
		</div>
</div>
<%if(afiliado != null && afiPlan != null){ %>
<div style="display: table; vertical-align: middle;">		
		<!-- class="lfr-table" style="border-collapse: separate; border-spacing: 3px;" -->
		<div id="<portlet:namespace />divPlanActualCabecera" style="display: table-row; border-collapse: separate; border-spacing: 3px;" >
			<div id="F2_C1" style="display: table-cell;">
				<label><liferay-ui:message	key="descripcion" /></label>
			</div>
			<div id="F2_C2" style="display: table-cell;">
				<label><liferay-ui:message	key="plan-emergencias" />&nbsp;/&nbsp;<liferay-ui:message key="plan-farmacia" /></label>
			</div>
			<div id="F2_C3" style="display: table-cell;">
				<label><liferay-ui:message	key="desde" /></label>
			</div>
			<div id="F2_C4" style="display: table-cell;">
				<label><liferay-ui:message	key="hasta" /></label>
			</div>
			<div id="F2_C5" style="display: table-cell;">
				<label><liferay-ui:message	key="motivo-baja" /></label>
			</div>
			<div id="F2_C6" style="display: table-cell;">
				<label>&nbsp;</label>
			</div>
		</div>
		<div id="<portlet:namespace />divPlanActualDetalle" style="display: table-row;" class="lfr-table">
			<div id="F3_C1" style="display: table-cell;">
				<input id="<portlet:namespace />plan_vig_desc" name="<portlet:namespace />plan_vig_desc" type="text" value="<%=afiPlan.getPlan().getDescripcion()%>" readonly="readonly" style="width: 200px; " />
			</div>
			<div id="F3_C2" style="display: table-cell;">
				<input id="<portlet:namespace />plan_omint_desc" name="<portlet:namespace />plan_omint_desc" type="text" 
					value="<%=afiPlan.getPlan().getDescripcionPrevencion()!=null?afiPlan.getPlan().getDescripcionPrevencion():new String("")%>" readonly="readonly" style="width: 15px; " />
				&nbsp;/&nbsp;
				<input id="<portlet:namespace />plan_farmacia_desc" name="<portlet:namespace />plan_farmacia_desc" type="text" 
					value="<%=afiPlan.getPlan().getFarmaciaPrevencion()!=null?afiPlan.getPlan().getFarmaciaPrevencion():new String("")%>" readonly="readonly" style="width: 10px; " />
			</div>
			<div id="F3_C3" style="display: table-cell;">
				<liferay-ui:input-date 
					dayParam="fechaVigenDesdeDia"
					dayValue="<%= fechaVigDesde.get(Calendar.DATE)%>"
					monthParam="fechaVigenDesdeMes"
					monthValue="<%= fechaVigDesde.get(Calendar.MONTH) %>"
					yearParam="fechaVigenDesdeAnio"
					yearValue="<%= fechaVigDesde.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+20%>"
					firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= true %>" />
			</div>
			<div id="F3_C4" style="display: table-cell;">
				<%if(fechaVigHasta == null){%>
				<liferay-ui:input-date
					dayNullable="true"
					dayParam="fechaVigenHastaDia"
					monthNullable="true" 
					monthParam="fechaVigenHastaMes"
					yearNullable="true"
					yearParam="fechaVigenHastaAnio"
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+60%>"
					firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" />
			<%}else{%>
				<liferay-ui:input-date
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
					yearRangeEnd="<%= fechaVigHasta.get(Calendar.YEAR)+60%>"
					firstDayOfWeek="<%= fechaVigHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= esView %>" />
			<%} %>
			</div>
			<div id="F3_C5" style="display: table-cell;">
				<select <% if (afiliado.getInte() != 0) { %> <%="disabled='disabled'" %> <%}%> 
					name="<portlet:namespace/>motivoBajaPlan" id="<portlet:namespace/>motivoBajaPlan" style="width: 200px;" 
					onchange="<portlet:namespace />aplicarReglasBajaPlan('',0);" >
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivosBaja) { %>
						<option <%= afiPlan.getMotivoBaja() !=null && afiPlan.getMotivoBaja().getId_motivo_baja() == motivoBaja.getId_motivo_baja() ? "selected" : "" %> 
								value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%} %>
				</select>
			</div>
			<div id="F3_C6" style="display: table-cell;">
				<%if(afiPlan != null && afiliado.getInte() == 0){ %>
					<input type="button" value="<liferay-ui:message key="cambio-plan" />" onClick="<portlet:namespace />verPlanes();" />
				<%-- &nbsp;
					<input type="button" value="<liferay-ui:message key="ver-historico" />"	onClick="<portlet:namespace />verHistorico();" /> --%>
			<% } else {%>
				&nbsp;
			<% } %>
			</div>
		</div>
</div>		
<div style="display: table; vertical-align: middle;">		
	<div id="<portlet:namespace />divPlanActualPrevencion" style="display: table-row;">
		<div id="F4_C1" style="display: table-cell;">
		<%if(afiPlan!= null && afiPlan.getPlan() != null && afiPlan.getPlan().isOspim()){ %>
			<label><liferay-ui:message key="nro-socio-prevencion" />:</label>
				<input id="<portlet:namespace />nro_socio_prev" name="<portlet:namespace />nro_socio_prev" type="text" readonly="readonly" size="5" />
				&nbsp;&nbsp;
			<label>	<liferay-ui:message key="nro-credencial-prevencion" />:</label>
				<input id="<portlet:namespace />nro_creden_prev" name="<portlet:namespace />nro_creden_prev" type="text" readonly="readonly" size="8" />
		<%} %>
		</div>
	</div>		
</div>
	<%} %>
		
 	<%if(afiPlan != null){ %>
	<div style="display: table; vertical-align: bottom;">		
		<div id="<portlet:namespace />divVerAfiAportes" style="display: table-row;">
				<div id="F5_C1" style="display: table-cell;">
					<liferay-util:include page='/html/portlet/afiliados/ver_afi_aportes.jsp' />
				</div>
		</div>
	</div>			
	<%} %>
	<div style="display: table; vertical-align: bottom;" id="<portlet:namespace />validando_reglas_baja_plan">		
		<div id="<portlet:namespace />divValidandoReglasPlan" style="display: table-row;">
				<div id="F6_C1" style="display: table-cell;">
					<label>
						<liferay-ui:message key='validando-reglas-baja-plan'/>
						<img alt="<liferay-ui:message key='validando-reglas-baja-plan'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</label>
				</div>
		</div>
	</div>	
	<div style="display: table; vertical-align: bottom;" id="<portlet:namespace />divNuevoPlan">		
		<div id="<portlet:namespace />divNuevoPlanDetalle" style="display: table-row;">
				<div id="F7_C1" style="display: table-cell;">
					<liferay-util:include page='/html/portlet/afiliados/afiliado_plan_nuevo.jsp' />
				</div>
		</div>
	</div>	
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
      }
}
function <portlet:namespace />verPlanes() {
	
	if(<portlet:namespace />validarBajaPlanActual()){
		jQuery('#<portlet:namespace />divNuevoPlan').show();
	}	
}

function <portlet:namespace />ocultarNuevoPlan(){
	jQuery("#<portlet:namespace />nuevoPlan option[value=0]").attr("selected",true);
	jQuery('#<portlet:namespace />divNuevoPlan').hide();
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
<%if(afiPlan != null && afiPlan.getPlan() != null && afiPlan.getPlan().isOspim()){ %>
	<portlet:namespace />mostrarAfiliacionPrevencion();
<%}%>

</script>

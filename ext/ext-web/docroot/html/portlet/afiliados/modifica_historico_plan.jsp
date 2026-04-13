<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%@page import="ar.com.ospim.util.DateUtils"%>

<%

	List<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) portletSession
			.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);

	ArrayList<Plan> planes = (ArrayList<Plan>) request.getSession().getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);
	
	int i=0;
%>
	
<div id="<portlet:namespace />div1" style="display: table-row;">
<div id="F1_C0" style="display: table-cell;">
<fieldset class="block-labels">
<legend><liferay-ui:message	key="planes-prestadores" /></legend>
		<div style="display: table; vertical-align: top; border-spacing: 2px;">
			<div id="<portlet:namespace />divPlanesCab" style="display: table-row;">
				<div id="FP_C1" style="display: table-cell;"><liferay-ui:message key="descripcion" /></div>
				<div id="FP_C2" style="display: table-cell;"><liferay-ui:message key="desde" /></div>
			   	<div id="FP_C3" style="display: table-cell;"><liferay-ui:message key="hasta" /></div>
			    <div id="FP_C4" style="display: table-cell;"><liferay-ui:message key="motivo-baja" /></div>
				<div id="FP_C5" style="display: table-cell;"><liferay-ui:message key="Eliminar" /></div>
			</div>
			
			<% ArrayList<AfiPlan> afiPlanHistorico = (ArrayList<AfiPlan>) request.getAttribute("planesHistorico"); 
			   
			   Calendar vigenDesde = Calendar.getInstance();	
			   Calendar vigenHasta = Calendar.getInstance();	
			   
			   for(i=0; i < afiPlanHistorico.size(); i++){	
				   
				   AfiPlan ap = afiPlanHistorico.get(i);
				   
				   vigenDesde.setTime(ap.getVigenDesde());
				   
				   if(ap.getVigenHasta()!=null){
					   vigenHasta.setTime(ap.getVigenHasta());
				   }
			%>
			<div id="<portlet:namespace />divPlanNuevo100<%=i%>" style="display: table-row;">
				<div id="divPlanNuevo100<%=i%>_C1" style="display: table-cell;">
					<a href="javascript:mostrarNuevoPlan('<%=i%>');" id="<portlet:namespace />mostrarNuevoPlanLink_<%=i%>" ><liferay-ui:icon image="add" /></a>
					<a href="javascript:ocultarNuevoPlan('<%=i%>');" id="<portlet:namespace />ocultarNuevoPlanLink_<%=i%>" ><liferay-ui:icon image="undo" message="Deshacer"/></a>
				</div>
				<div id="divPlanNuevo100<%=i%>_C2" style="display: table-cell;">&nbsp;</div>
				<div id="divPlanNuevo100<%=i%>_C3" style="display: table-cell;">&nbsp;</div>
				<div id="divPlanNuevo100<%=i%>_C4" style="display: table-cell;">&nbsp;</div>
				<div id="divPlanNuevo100<%=i%>_C5" style="display: table-cell;">&nbsp;</div>
			</div>
			<div id="<portlet:namespace />divPlanNuevoDet_<%=i%>" style="display: table-row;">
				<div id="FP<%=i%>_C1" style="display: table-cell;">
					<input type="hidden" name="nuevo_idSerial_<%=i%>" value="<%=0%>">
					<input type="hidden" name="nuevo_estado_<%=i%>" value="<%=AfiPlan.ESTADOS.ALTA%>">
					<input type="hidden" name="nuevoPlanOmintId_<%=i%>" 
										   id="<portlet:namespace/>nuevoPlanOmintId_<%=i%>" value="">
					<select 
						name="<portlet:namespace/>nuevo_plan_<%=i%>" id="<portlet:namespace/>nuevo_plan_<%=i%>" 
						style="width: 240px;" onchange="<portlet:namespace />filtrarNuevoPlanOmint('<%=i%>');" >
						<option value='0'><liferay-ui:message key="seleccione-plan" /></option>
						<%for (Plan plan : planes) { %>
							<option value="<%= plan.getId()%>"><%=plan.getDescripcion()%></option>
						<% } %>
					</select>
				</div>
				<%	  String nuevaVigDesdeDayParamName = "nuevo_fechaVigenDesdeDia_"+i;
					  String nuevaVigDesdeMonthParamName = "nuevo_fechaVigenDesdeMes_"+i; 
					  String nuevaVigDesdeYearParamName = "nuevo_fechaVigenDesdeAnio_"+i;
					  
					  String nuevaVigHastaDayParamName = "nuevo_fechaVigenHastaDia_"+i;
					  String nuevaVigHastaMonthParamName = "nuevo_fechaVigenHastaMes_"+i; 
					  String nuevaVigHastaYearParamName = "nuevo_fechaVigenHastaAnio_"+i;
					%>		
				<div id="FP<%=i%>_C2" style="display: table-cell;">
					<liferay-ui:input-date 
							dayParam="<%=nuevaVigDesdeDayParamName%>"
							dayNullable="<%= true %>"
							monthParam="<%=nuevaVigDesdeMonthParamName%>"
							monthNullable="<%= true %>"
							yearParam="<%=nuevaVigDesdeYearParamName%>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) - 40 %>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+20%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ap.getBajaFecha()!=null?true:false %>" />
				</div>
				<div id="FP<%=i%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
							dayNullable="true"
							dayParam="<%=nuevaVigHastaDayParamName%>"
							monthNullable="true" 
							monthParam="<%=nuevaVigHastaMonthParamName%>"
							yearNullable="true"
							yearParam="<%=nuevaVigHastaYearParamName%>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) - 20%>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+60%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ap.getBajaFecha()!=null?true:false %>" />
				</div>
				<div id="FP<%=i%>_C4" style="display: table-cell;">
					<select name="<portlet:namespace/>nuevo_motivoBajaPlan_<%=i%>" id="<portlet:namespace/>motivoBajaPlan" style="width: 200px;" >
						<option value="0" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
						<% for (MotivoBaja motivoBaja : motivosBaja) { %>
							<option value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
						<%} %>
					</select>
				</div>
				<div id="FP<%=i%>_C5" style="display: table-cell;">
					&nbsp;
				</div>
			</div>
			
			<div id="<portlet:namespace />divPlanesDet_<%=i%>" style="display: table-row;">
				<div id="FP<%=i%>_C1" style="display: table-cell;">
					<input type="hidden" name="idSerial_<%=i%>" value="<%=ap.getId()%>">
					<input type="hidden" name="estado_<%=i%>" 
										 id="<portlet:namespace />estado_<%=i%>" value="<%=ap.getEstado()!=null?ap.getEstado():""%>">
					<input type="hidden" name="baja_fecha_<%=i%>" value="<%=DateUtils.format(ap.getBajaFecha(), DateUtils.SHORT)%>">
					<input type="hidden" name="vigen_desde_<%=i%>" value="<%=DateUtils.format(ap.getVigenDesde(), DateUtils.SHORT)%>">
					<input type="hidden" name="vigen_hasta_<%=i%>" value="<%=ap.getVigenHasta()!=null?DateUtils.format(ap.getVigenHasta(), DateUtils.SHORT):""%>">
					<input type="hidden" name="planOmintId_<%=i%>" 
										 id="<portlet:namespace/>planOmintId_<%=i%>" value="<%=ap.getId_plan_omint()%>">
					<input type="hidden" name="<portlet:namespace/>aux_plan_<%=i%>" 
										 id="<portlet:namespace/>aux_plan_<%=i%>" value="<%=ap.getPlan().getId()%>">					 
					<select 
						name="<portlet:namespace/>plan_<%=i%>" id="<portlet:namespace/>plan_<%=i%>" 
						style="width: 240px;" onchange="<portlet:namespace />filtrarPlanOmint('<%=i%>');<portlet:namespace />modificarEstado('<%=i%>');"
						<%if(ap.getBajaFecha()!=null){ %> disabled="disabled" <%} %> >
						<option value='0'><liferay-ui:message key="seleccione-plan" /></option>
						<%for (Plan plan : planes) { %>
						
							<option value="<%= plan.getId()%>" 
							<%if(plan.getId()== ap.getPlan().getId()){ %> selected="selected" <%} %> ><%=plan.getDescripcion()%></option>
							
						<% } %>
					</select>
				</div>		
				<%String vigDesdeDayParamName = "fechaVigenDesdeDia_"+i;
					  String vigDesdeMonthParamName = "fechaVigenDesdeMes_"+i; 
					  String vigDesdeYearParamName = "fechaVigenDesdeAnio_"+i;
					  
					  String vigHastaDayParamName = "fechaVigenHastaDia_"+i;
					  String vigHastaMonthParamName = "fechaVigenHastaMes_"+i; 
					  String vigHastaYearParamName = "fechaVigenHastaAnio_"+i;
					%>		
				<div id="FP<%=i%>_C2" style="display: table-cell;">
					<liferay-ui:input-date 
							dayParam="<%=vigDesdeDayParamName%>"
							dayValue="<%= vigenDesde.get(Calendar.DATE)%>"
							monthParam="<%=vigDesdeMonthParamName%>"
							monthValue="<%= vigenDesde.get(Calendar.MONTH) %>"
							yearParam="<%=vigDesdeYearParamName%>"
							yearValue="<%= vigenDesde.get(Calendar.YEAR) %>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) - 40 %>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+20%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ap.getBajaFecha()!=null?true:false %>" />
				</div>
				<%if(ap.getVigenHasta() == null){%>
				<div id="FP<%=i%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
							dayNullable="true"
							dayParam="<%=vigHastaDayParamName%>"
							monthNullable="true" 
							monthParam="<%=vigHastaMonthParamName%>"
							yearNullable="true"
							yearParam="<%=vigHastaYearParamName%>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) -20 %>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+60%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ap.getBajaFecha()!=null?true:false %>" />
				</div>
				<%}else{%>
				<div id="FP<%=i%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
						dayNullable="true"
						dayParam="<%=vigHastaDayParamName%>"
						dayValue="<%= vigenHasta.get(Calendar.DATE)%>"
						monthNullable="true" 
						monthParam="<%=vigHastaMonthParamName%>"
						monthValue="<%= vigenHasta.get(Calendar.MONTH) %>"
						yearNullable="true"
						yearParam="<%=vigHastaYearParamName%>"
						yearValue="<%= vigenHasta.get(Calendar.YEAR) %>"
						yearRangeStart="<%= vigenHasta.get(Calendar.YEAR) -60%>"
						yearRangeEnd="<%= vigenHasta.get(Calendar.YEAR)+60%>"
						firstDayOfWeek="<%= vigenHasta.getFirstDayOfWeek() - 1 %>"
						disabled="<%= ap.getBajaFecha()!=null?true:false %>" />
				</div>
				<%} %>
				<script type="text/javascript">
				<%-- jQuery( "#<portlet:namespace /><%=vigDesdeDayParamName%>" ).change(function(){
					  alert( "Handler for .change() called." );
				}); --%>
				<%-- jQuery( "#<portlet:namespace /><%=vigDesdeDayParamName%>" ).focus(function(){ --%>
				jQuery( "#<portlet:namespace /><%=vigDesdeDayParamName%>").add("#<portlet:namespace /><%=vigDesdeMonthParamName%>") 
						.add("#<portlet:namespace /><%=vigDesdeYearParamName%> " ).focus(function(){
					jQuery('#<portlet:namespace />estado_<%=i%>').val('<%=AfiPlan.ESTADOS.MODIFICADO %>');
				});
				jQuery( "#<portlet:namespace /><%=vigHastaDayParamName%>").add("#<portlet:namespace /><%=vigHastaMonthParamName%>") 
						.add("#<portlet:namespace /><%=vigHastaYearParamName%> " ).focus(function(){
					jQuery('#<portlet:namespace />estado_<%=i%>').val('<%=AfiPlan.ESTADOS.MODIFICADO %>');
				});
				</script>		
				<div id="FP<%=i%>_C4" style="display: table-cell;">
				<select name="<portlet:namespace/>motivoBajaPlan_<%=i%>" id="<portlet:namespace/>motivoBajaPlan" 
				style="width: 200px;" onchange="<portlet:namespace />modificarEstado('<%=i%>');" 
				<%if(ap.getBajaFecha()!=null){ %> disabled="disabled" <%} %> >
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivosBaja) { %>
						<option <%= ap.getMotivoBaja() !=null && ap.getMotivoBaja().getId_motivo_baja() == motivoBaja.getId_motivo_baja() ? "selected" : "" %> 
								value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%} %>
					</select>
				</div>
				<div id="FP<%=i%>_C51" style="display: table-cell;">
				<%if (Validator.isNotNull(ap.getBajaFecha()) ) { %>				
					<img alt="Baja" src="<%=themeDisplay.getPathThemeImages()%>/common/close.png"/>		
				<%}else{ 
					String urlDelete = "javascript:bajaLogicaPlan("+i+");";
					%>
					
					<liferay-ui:icon-delete url="<%=urlDelete %>" />		
				<%} %>
				</div>
				<div id="FP<%=i%>_C52" style="display: table-cell;">
					<img alt="Baja" src="<%=themeDisplay.getPathThemeImages()%>/common/close.png"/>		
				</div>
		    </div>
			<%} %> <!-- fin for plan -->
	</div>
	</fieldset>		
</div>
</div>

<script type="text/javascript">
	
	<% for(int l=0; l < i; l++){ %>
		jQuery('#<portlet:namespace />ocultarNuevoPlanLink_<%=l%>').hide();	
		jQuery('#<portlet:namespace />divPlanNuevoDet_<%=l%>').hide();
		jQuery('#FP<%=l%>_C52').hide();
	<% } %>

	function mostrarNuevoPlan(orden){
		<% for(int l=0; l < i; l++){ %>
			if(orden == <%=l%>){
				jQuery('#<portlet:namespace />mostrarNuevoPlanLink_<%=l%>').hide();	
				jQuery('#<portlet:namespace />ocultarNuevoPlanLink_<%=l%>').show();
				jQuery('#<portlet:namespace />divPlanNuevoDet_<%=l%>').show();
				jQuery('#<portlet:namespace />nuevo_estado_<%=l%>').val('<%=AfiPlan.ESTADOS.ALTA%>');
			}
		<% } %>
	}
	
	function ocultarNuevoPlan(orden){
		<% for(int l=0; l < i; l++){ %>
			if(orden == <%=l%>){
				jQuery('#<portlet:namespace />mostrarNuevoPlanLink_<%=l%>').show();	
				jQuery('#<portlet:namespace />ocultarNuevoPlanLink_<%=l%>').hide();
				jQuery('#<portlet:namespace />divPlanNuevoDet_<%=l%>').hide();
				jQuery('#<portlet:namespace />nuevo_estado_<%=l%>').val('');
			}
		<% } %>
	}
	
	function bajaLogicaPlan(orden){
		
		<% for(int l=0; l < i; l++){ %>
			if(orden == <%=l%>){
				
				<%  AfiPlan apAux = afiPlanHistorico.get(l); 
					apAux.setBajaFecha(new Date());
				%>
				jQuery('#FP<%=l%>_C51').hide();
				jQuery('#FP<%=l%>_C52').show();
				jQuery('#<portlet:namespace />estado_<%=l%>').val('<%=AfiPlan.ESTADOS.BAJA%>');
			}
		<% } %>
	}
	
	function <portlet:namespace />filtrarNuevoPlanOmint(orden) {
		<% for(int l=0; l < i; l++){ %>
		if(orden == <%=l%>){
	
			var idPlan = jQuery('#<portlet:namespace/>nuevo_plan_<%=l%>').val();

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_plan_omint&idPlan='+idPlan;		
			
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var idOmint = obj.planOmint;
				 /* var descOmint = obj.descripcionOmint;
					var descPreven = obj.descripcionPrevencion;
					var farmPreven = obj.farmaciaPrevencion; */
					
					if(idOmint!=0 && idOmint!=""){						
						jQuery('#<portlet:namespace/>nuevoPlanOmintId_<%=l%>').val(idOmint);
					}else{
						jQuery('#<portlet:namespace/>nuevoPlanOmintId_<%=l%>').val('');
					}
				}				                                                                                                                                                                                                                                                            
				
			});
			
		}
		<%}%>
	}
	
	function <portlet:namespace />filtrarPlanOmint(orden) {
		
		<% for(int l=0; l < i; l++){ %>
		if(orden == <%=l%>){
	
			var idPlan = jQuery('#<portlet:namespace/>plan_<%=l%>').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_plan_omint&idPlan='+idPlan;		
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var idOmint = obj.planOmint;
				 /* var descOmint = obj.descripcionOmint;
					var descPreven = obj.descripcionPrevencion;
					var farmPreven = obj.farmaciaPrevencion; */
					if(idOmint!=0 && idOmint!=""){						
						jQuery('#<portlet:namespace/>planOmintId_<%=l%>').val(idOmint);
						jQuery('#<portlet:namespace />estado_<%=l%>').val('<%=AfiPlan.ESTADOS.MODIFICADO%>');
					}else{
						jQuery('#<portlet:namespace/>planOmintId_<%=l%>').val('');
						jQuery('#<portlet:namespace />estado_<%=l%>').val('<%=AfiPlan.ESTADOS.MODIFICADO%>');
					}
				}				                                                                                                                                                                                                                                                            
				
			});
			
		}
		<%}%>
	}
	
	function <portlet:namespace />modificarEstado(orden) {
		
		<% for(int l=0; l < i; l++){ %>
		if(orden == <%=l%>){
			jQuery('#<portlet:namespace />estado_<%=l%>').val('<%=AfiPlan.ESTADOS.MODIFICADO%>');
		}
		<%}%>
	}
	
</script>
<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%@page import="ar.com.ospim.util.DateUtils"%>

<%

List<TercerizadoraServicio> tercServList = (List<TercerizadoraServicio>) request.getSession().getAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION);

List<AfiTercerizadoraServicio> AfiTercServList = (ArrayList<AfiTercerizadoraServicio>) request.getSession().getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);


	
	int s=0;
%>

<portlet:defineObjects />
	
<div id="<portlet:namespace />div1" style="display: table-row;">
<div id="F1_C0" style="display: table-cell;">
<fieldset class="block-labels">
<legend><liferay-ui:message	key="tercerizadora-servicio" /></legend>
	<div style="display: table; vertical-align: top; border-spacing: 2px;">
			<div id="<portlet:namespace />divTercCab" style="display: table-row;">
				<div id="FT_C1" style="display: table-cell;"><liferay-ui:message key="tercerizadora-servicio" /></div>
				<div id="FT_C2" style="display: table-cell;"><liferay-ui:message key="ingre-fecha" /></div>
			   	<div id="FT_C3" style="display: table-cell;"><liferay-ui:message key="egreso-fecha" /></div>
				<div id="FT_C4" style="display: table-cell;"><liferay-ui:message key="Eliminar" /></div>
			</div>
			
			<% ArrayList<AfiTercerizadoraServicio> afiTercHistorico = (ArrayList<AfiTercerizadoraServicio>) request.getAttribute("tercerizadorasHistorico"); 
			   
					   Calendar inicio = Calendar.getInstance();	
					   Calendar fin = Calendar.getInstance();	
					   
					   for(s=0; s < afiTercHistorico.size(); s++){	
						   
						   AfiTercerizadoraServicio ats = afiTercHistorico.get(s);
						   
						   inicio.setTime(ats.getFechaInicioPres());
						   
						   if(ats.getFechaFinPres()!=null){
							   fin.setTime(ats.getFechaFinPres());
						   }
				%>
			<div id="<portlet:namespace />divTercNuevo100<%=s%>" style="display: table-row;">
				<div id="divTercNuevo100<%=s%>_C1" style="display: table-cell;">
					<a href="javascript:mostrarNuevoTerc('<%=s%>');" id="<portlet:namespace />mostrarNuevoTercLink_<%=s%>" ><liferay-ui:icon image="add" /></a>
					<a href="javascript:ocultarNuevoTerc('<%=s%>');" id="<portlet:namespace />ocultarNuevoTercLink_<%=s%>" ><liferay-ui:icon image="undo" message="Deshacer"/></a>
				</div>
				<div id="divTercNuevo100<%=s%>_C2" style="display: table-cell;">&nbsp;</div>
				<div id="divTercNuevo100<%=s%>_C3" style="display: table-cell;">&nbsp;</div>
				<div id="divTercNuevo100<%=s%>_C4" style="display: table-cell;">&nbsp;</div>
			</div>
			<div id="<portlet:namespace />divTercNuevoDet_<%=s%>" style="display: table-row;">
				<div id="FT<%=s%>_C1" style="display: table-cell;">
					<input type="hidden" name="nuevo_idTercSerial_<%=s%>" value="<%=0%>">
					<input type="hidden" name="nuevo_tercEstado_<%=s%>"
					                     id="<portlet:namespace />nuevo_tercEstado_<%=s%>"  value="<%=AfiTercerizadoraServicio.ESTADOS.ALTA%>">
					<select name="<portlet:namespace/>nuevo_tercerizadora_<%=s%>" id="<portlet:namespace/>nuevo_tercerizadora_<%=s%>" 
							style="width: 300px;" 
							<%-- <%if(ats.getBajaFecha()!=null){ %> disabled="disabled" <%} %> --%> >
								<option value="" selected="selected"><liferay-ui:message key="seleccione-tercerizadora-servicio" /></option>
								<% for (TercerizadoraServicio ts : tercServList) { %>
									<option value="<%= ts.getId_tercerizadora()%>"><%=ts.getDescripcion()%></option>		
								<%} %>
						</select>
				</div>
				<%	  String nuevaInicioDayParamName = "nuevo_fechaInicioDia_"+s;
					  String nuevaInicioMonthParamName = "nuevo_fechaInicioMes_"+s; 
					  String nuevaInicioYearParamName = "nuevo_fechaInicioAnio_"+s;
					  
					  String nuevaFinDayParamName = "nuevo_fechaFinDia_"+s;
					  String nuevaFinMonthParamName = "nuevo_fechaFinMes_"+s; 
					  String nuevaFinYearParamName = "nuevo_fechaFinAnio_"+s;
					%>					
				<div id="FT<%=s%>_C2" style="display: table-cell;">
					<liferay-ui:input-date 
							dayParam="<%=nuevaInicioDayParamName%>"
							dayNullable="<%= true %>"
							monthParam="<%=nuevaInicioMonthParamName%>"
							monthNullable="<%= true %>"
							yearParam="<%=nuevaInicioYearParamName%>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= inicio.get(Calendar.YEAR) - 40 %>"
							yearRangeEnd="<%= inicio.get(Calendar.YEAR)+40%>"
							firstDayOfWeek="<%= inicio.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ats.getBajaFecha()!=null?true:false %>" />
				</div>
				<div id="FT<%=s%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
							dayNullable="true"
							dayParam="<%=nuevaFinDayParamName%>"
							monthNullable="true" 
							monthParam="<%=nuevaFinMonthParamName%>"
							yearNullable="true"
							yearParam="<%=nuevaFinYearParamName%>"
							yearRangeStart="<%= inicio.get(Calendar.YEAR)-20%>"
							yearRangeEnd="<%= inicio.get(Calendar.YEAR)+60%>"
							firstDayOfWeek="<%= inicio.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ats.getBajaFecha()!=null?true:false %>" />
				</div>
				<div id="FT<%=s%>_C4" style="display: table-cell;">
					&nbsp;
				</div>
			</div>
			
			<div id="<portlet:namespace />divTercerizadorasDet_<%=s%>" style="display: table-row;">
				<div id="FT<%=s%>_C1" style="display: table-cell;">
					<input type="hidden" name="idTercSerial_<%=s%>" value="<%=ats.getId()%>">
					<input type="hidden" name="tercEstado_<%=s%>" 
									     id="<portlet:namespace />tercEstado_<%=s%>" value="<%=ats.getEstado()!=null?ats.getEstado():""%>">
					<input type="hidden" name="terc_baja_fecha_<%=s%>" value="<%=DateUtils.format(ats.getBajaFecha(), DateUtils.SHORT)%>">
					<%if(ats.getBajaFecha() != null){ %>
					<input type="hidden" name="aux_tercerizadora_<%=s%>" value="<%=ats.getTercerizadora().getId_tercerizadora()%>">
					<input type="hidden" name="aux_ini_fecha_<%=s%>" value="<%=DateUtils.format(ats.getFechaInicioPres(), DateUtils.SHORT)%>">
					<input type="hidden" name="aux_fin_fecha_<%=s%>" value="<%=DateUtils.format(ats.getFechaFinPres(), DateUtils.SHORT)%>">
					<%} %>
					<select name="<portlet:namespace/>tercerizadora_<%=s%>" id="<portlet:namespace/>tercerizadora" 
							style="width: 300px;" onchange="<portlet:namespace />modificarEstadoTerc('<%=s%>');" 
							<%if(ats.getBajaFecha()!=null){ %> disabled="disabled" <%} %> >
								<option value="" selected="selected"><liferay-ui:message key="seleccione-tercerizadora-servicio" /></option>
								<% for (TercerizadoraServicio ts : tercServList) { %>
									<option <%= ats.getTercerizadora().getId_tercerizadora().equalsIgnoreCase(ts.getId_tercerizadora()) ? "selected" : "" %> 
											value="<%= ts.getId_tercerizadora()%>"><%=ts.getDescripcion()%></option>		
								<%} %>
						</select>
				</div>	
				<%	  String inicioDayParamName = "fechaInicioDia_"+s;
					  String inicioMonthParamName = "fechaInicioMes_"+s; 
					  String inicioYearParamName = "fechaInicioAnio_"+s;
					  
					  String finDayParamName = "fechaFinDia_"+s;
					  String finMonthParamName = "fechaFinMes_"+s; 
					  String finYearParamName = "fechaFinAnio_"+s;
					%>	
				
				<div id="FT<%=s%>_C2" style="display: table-cell;">
					<liferay-ui:input-date 
							dayParam="<%=inicioDayParamName%>"
							dayValue="<%= inicio.get(Calendar.DATE)%>"
							monthParam="<%=inicioMonthParamName%>"
							monthValue="<%= inicio.get(Calendar.MONTH) %>"
							yearParam="<%=inicioYearParamName%>"
							yearValue="<%= inicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= inicio.get(Calendar.YEAR) - 40 %>"
							yearRangeEnd="<%= inicio.get(Calendar.YEAR)+40%>"
							firstDayOfWeek="<%= inicio.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ats.getBajaFecha()!=null?true:false %>" />
				</div>
				<%if(ats.getFechaFinPres() == null){%>
				<div id="FT<%=s%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
							dayNullable="true"
							dayParam="<%=finDayParamName%>"
							monthNullable="true" 
							monthParam="<%=finMonthParamName%>"
							yearNullable="true"
							yearParam="<%=finYearParamName%>"
							yearRangeStart="<%= inicio.get(Calendar.YEAR)%>"
							yearRangeEnd="<%= inicio.get(Calendar.YEAR)+60%>"
							firstDayOfWeek="<%= inicio.getFirstDayOfWeek() - 1 %>"
							disabled="<%= ats.getBajaFecha()!=null?true:false %>" />
				</div>
				<%}else{%>
				<div id="FT<%=s%>_C3" style="display: table-cell;">
					<liferay-ui:input-date
						dayNullable="true"
						dayParam="<%=finDayParamName%>"
						dayValue="<%= fin.get(Calendar.DATE)%>"
						monthNullable="true" 
						monthParam="<%=finMonthParamName%>"
						monthValue="<%= fin.get(Calendar.MONTH) %>"
						yearNullable="true"
						yearParam="<%=finYearParamName%>"
						yearValue="<%= fin.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fin.get(Calendar.YEAR) -60%>"
						yearRangeEnd="<%= fin.get(Calendar.YEAR)+60%>"
						firstDayOfWeek="<%= fin.getFirstDayOfWeek() - 1 %>"
						disabled="<%= ats.getBajaFecha()!=null?true:false %>" />
				</div>
				<%} %>	
				<script type="text/javascript">
				jQuery( "#<portlet:namespace /><%=inicioDayParamName%>").add("#<portlet:namespace /><%=inicioMonthParamName%>") 
						.add("#<portlet:namespace /><%=inicioYearParamName%> " ).focus(function(){
					jQuery('#<portlet:namespace />tercEstado_<%=s%>').val('<%=AfiTercerizadoraServicio.ESTADOS.MODIFICADO%>');
				});
				jQuery( "#<portlet:namespace /><%=finDayParamName%>").add("#<portlet:namespace /><%=finMonthParamName%>") 
						.add("#<portlet:namespace /><%=finYearParamName%> " ).focus(function(){
					jQuery('#<portlet:namespace />tercEstado_<%=s%>').val('<%=AfiTercerizadoraServicio.ESTADOS.MODIFICADO%>');
				});
				</script>	
				<div id="FT<%=s%>_C41" style="display: table-cell;">
				<%if (Validator.isNotNull(ats.getBajaFecha()) ) { %>				
					<img alt="Baja" src="<%=themeDisplay.getPathThemeImages()%>/common/close.png"/>		
				<%}else{ 
					String urlDelete = "javascript:bajaLogicaTerc("+s+");";
					%>
					
					<liferay-ui:icon-delete url="<%=urlDelete %>" />		
				<%} %>
				</div>
				<div id="FT<%=s%>_C51" style="display: table-cell;">
					<img alt="Baja" src="<%=themeDisplay.getPathThemeImages()%>/common/close.png"/>		
				</div>
		    </div>
			<%} %> <!-- fin for tercerizadora -->
	</div>
	</fieldset>		
</div>
</div>

<script type="text/javascript">
	
	<% for(int p=0; p < s; p++){ %>
		jQuery('#<portlet:namespace />ocultarNuevoTercLink_<%=p%>').hide();	
		jQuery('#<portlet:namespace />divTercNuevoDet_<%=p%>').hide();
		jQuery('#FT<%=p%>_C51').hide();
	<% } %>

	function mostrarNuevoTerc(orden){
		<% for(int p=0; p < s; p++){ %>
			if(orden == <%=p%>){
				jQuery('#<portlet:namespace />mostrarNuevoTercLink_<%=p%>').hide();	
				jQuery('#<portlet:namespace />ocultarNuevoTercLink_<%=p%>').show();
				jQuery('#<portlet:namespace />divTercNuevoDet_<%=p%>').show();
				jQuery('#<portlet:namespace />nuevo_tercEstado_<%=p%>').val('<%=AfiTercerizadoraServicio.ESTADOS.ALTA%>');
			}
		<% } %>
	}
	
	function ocultarNuevoTerc(orden){
		<% for(int p=0; p < s; p++){ %>
			if(orden == <%=p%>){
				jQuery('#<portlet:namespace />mostrarNuevoTercLink_<%=p%>').show();	
				jQuery('#<portlet:namespace />ocultarNuevoTercLink_<%=p%>').hide();
				jQuery('#<portlet:namespace />divTercNuevoDet_<%=p%>').hide();
				jQuery('#<portlet:namespace />nuevo_tercEstado_<%=p%>').val('');
			}
		<% } %>
	}
	
	function bajaLogicaTerc(orden){
		
		<% for(int l=0; l < s; l++){ %>
			if(orden == <%=l%>){
				
				<%  AfiTercerizadoraServicio ats = afiTercHistorico.get(l);
					ats.setBajaFecha(new Date());
				%>
				jQuery('#FT<%=l%>_C41').hide();
				jQuery('#FT<%=l%>_C51').show();
				jQuery('#<portlet:namespace />tercEstado_<%=l%>').val('<%=AfiTercerizadoraServicio.ESTADOS.BAJA%>');
			}
		<% } %>
	}
	
	function <portlet:namespace />modificarEstadoTerc(orden) {
		
		<% for(int l=0; l < s; l++){ %>
		if(orden == <%=l%>){
			jQuery('#<portlet:namespace />tercEstado_<%=l%>').val('<%=AfiTercerizadoraServicio.ESTADOS.MODIFICADO%>');
		}
		<%}%>
	}
</script>

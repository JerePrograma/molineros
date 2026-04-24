<%@ include file="/html/portlet/crm/init.jsp"%>
<%
    String portlet_name_space = portletDisplay.getId();
    String portlet_name="afiliados";
    if (portlet_name_space == null || portlet_name_space.trim().equals("")){
	   portlet_name = "afiliados";
    }else if(portlet_name != null && portlet_name_space.trim().equals("CAI_1")){
	   portlet_name = "cai";
    }else if(portlet_name != null && portlet_name_space.trim().equals("JUD_1")){
 	   portlet_name = "judicial";
     }

	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	ContactoCRM contacto = null;
	
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}
	
	Afiliado crmafi = (Afiliado) request.getAttribute(WebKeysCrm.CRM_AFILIADO);
	Domicilio afiDomicilio = (Domicilio) request.getAttribute(WebKeysCrm.CRM_AFILIADO_DOMICILIO);

	Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
	Calendar bajaFecha = CalendarFactoryUtil.getCalendar();
	
	if(crmafi != null){
		vigenteFecha.setTime(crmafi.getVigen_fecha());
		if(crmafi.getBaja_fecha() != null){
			bajaFecha.setTime(crmafi.getBaja_fecha() );
		}
	/* }else{
		vigenteFecha.setTime(vigenteFecha.getTime()); */
	}else{
		crmafi = contacto!=null?contacto.getAfiliado():null;
		if(crmafi != null)
		vigenteFecha.setTime(crmafi.getVigen_fecha());
	}
%>

<%if(crmafi!=null) {%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key='afiliado' /></legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
		<tr>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input type="text"
					   id="<portlet:namespace />cuil_titular"
					   name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
					   value='<%=crmafi.getCuil_titular()%>'
					   readonly='readonly'/>
			</td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />integ"
				name="<portlet:namespace />integ" size="2" maxlength="2" type="text"
				value="<%=crmafi.getInteAsString()%>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td>
				<select id="<portlet:namespace/>documento_tipo"
					    name="<portlet:namespace/>documento_tipo" 
					    disabled="disabled">
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	%>
					<option
						<%=crmafi.getDocumento_tipo().equals(tipoDoc) ? "selected" : ""  %>
						value="<%= tipoDoc %>"><%=tipoDoc%>
					</option>
					<%	}	%>
				</select>
			</td>
			<td><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="8"
				value="<%=crmafi.getDocu_numero()%>" 
				readonly="readonly" /></td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text"
				value="<%=crmafi.getApellido()%>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%=crmafi.getNombre()%>" 
				readonly="readonly" />
			</td>			
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="3"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%=String.valueOf(crmafi.getSeccional().getId())%>" />
					<liferay-util:param name="seccional"
						value="<%=crmafi.getSeccional().getDescripcion()%>" />
					<liferay-util:param name="esEdicion" value="false" />
				</liferay-util:include>
			</td>
			<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="3"><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR)  %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek()%>"
					disabled="<%= true %>" />
			</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="plan" />:</label></td>
			<td colspan="2"><input type="text" id="<portlet:namespace />plan_vig_desc" name="<portlet:namespace />plan_vig_desc"  
			value="<%=crmafi.getUltimo_plan().getDescripcion()%>" readonly="readonly" style="width: 240px; " /></td>
			<td>&nbsp;</td>
			<%if(crmafi.getBaja_fecha()!=null) {%>
				<td><label><liferay-ui:message key="fecha-baja" />:</label></td>
				<td colspan="3"><liferay-ui:input-date
					dayParam="bajaFechaDia"
					dayValue="<%= bajaFecha.get(Calendar.DATE)%>"
					monthParam="bajaFechaMes"
					monthValue="<%= bajaFecha.get(Calendar.MONTH) %>"
					yearParam="bajaFechaAnio"
					yearValue="<%= bajaFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= bajaFecha.get(Calendar.YEAR) %>"
					yearRangeEnd="<%= bajaFecha.get(Calendar.YEAR)  %>"
					firstDayOfWeek="<%= bajaFecha.getFirstDayOfWeek()%>"
					disabled="<%= true %>" />
			<%}else{ %>
				<td colspan="3">&nbsp;</td>
			<%} %>
		</tr>
		<tr>
			<td colspan="4">
				<table class="lfr-table">
					<tr>
						<%if(crmafi.getId_ospim_baja_fecha() == null && crmafi.getId_ospim() > 0){ %>
							<td><label><liferay-ui:message key="id-ospim" />:</label></td>
							<td><%=crmafi.getId_ospim() %></td>
						<%} %>
						<%if(crmafi.getId_amtima_baja_fecha() == null && crmafi.getId_amtima() > 0){ %>
							<td><label><liferay-ui:message key="id-amtima" />:</label></td>
							<td><%=crmafi.getId_amtima() %></td>
						<%} %>
						<%if(crmafi.getId_uoma_baja_fecha() == null && crmafi.getId_uoma() > 0){ %>
							<td><label><liferay-ui:message key="id-uoma" />:</label></td>
							<td><%=crmafi.getId_uoma()%></td>
						<%} %>
					</tr>
				</table>
			</td>
			<%if(afiDomicilio != null){ %>		
			<td><label><liferay-ui:message key="crm-contacto-verif-domi" />:</label></td>
			<td>
				<div id="<portlet:namespace />divBotonActualizar">
					<input type="button" value="Actualizar" 
						onclick="javascript:mostrarDomicilioAfiliado('<%=crmafi.getCuil_titular()%>','<%=crmafi.getInte()%>');"/>
				</div>
				<div id="<portlet:namespace />divResultadoActualizarOK">
					<p><b><liferay-ui:message key="crm-actualiza-domicilio"/></b></p>
				</div>
			</td>
			<%} %>
			<td colspan="2" align="right">
				<input type="button" value="<liferay-ui:message key="ver-aportes" />" onClick="<portlet:namespace />verAportes(false);" />
			</td>
		</tr>
		<tr>
			<td colspan="8">
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="observaciones-internas" /></legend>

				<table class="lfr-table">
						<tr>
							<td><label><liferay-ui:message key="observacion-interna" />:</label></td>
							<td colspan="7"><textarea cols="100"
									name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"></textarea></td>
							<td><img alt="<liferay-ui:message key='dar-alta'/>" 
									src="<%= themeDisplay.getPathThemeImages() + "/common/add.png"%>"
		 							onClick="javascript:nuevaObservacion();" />
		 					</td>		
						</tr>
				</table>
				</fieldset>
			</td>
			
		</tr>	
		<tr>
			<td colspan="8">
				<div align="center" id="<portlet:namespace />observaciones_internas" style="height:120px; overflow: scroll; overflow-x: hidden;">
						<liferay-util:include page="/html/portlet/afiliados/afi_observaciones_search_result.jsp">
						</liferay-util:include>
				</div>	
			</td>
		
		</tr>
	</table>
</fieldset>	
<%} %>

<script type="text/javascript">
<% String cuil_titular = crmafi!=null&&!crmafi.getCuil_titular().isEmpty()?crmafi.getCuil_titular():new String("999999999"); %>
/* Extraido de la pagina otros_datos.jsp */
var popupAfill;
function <portlet:namespace />verAportes(cerrarAnterior){  
	var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();

	if(periodoDesdeMesAnio==null){			
		periodoDesdeMesAnio='012011';
	}
			
	if(cerrarAnterior=='true'){
		Liferay.Popup.close(popupAfill);
	}

//	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;		
	popupAfill = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
	jQuery(popupAfill).load(url);
	
}


function nuevaObservacion() {
	var cuil_titu=jQuery('#<portlet:namespace />cuil_titular').val();
	var integ=jQuery('#<portlet:namespace />inte').val();
	var obserInterna = jQuery('#<portlet:namespace/>obs').val();


	if(trim(obserInterna).length == 0){
		
		alert("Complete la observación interna");
		return false;
		
	}
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
				<portlet:param name="struts_action" value="/afiliados/buscar_observaciones_internas" />
				<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.SAVE %>" />
			</portlet:renderURL>';
			
	var params = {
					"cuil_titular":cuil_titu,
					"inte":integ,
					"observacion_interna":obserInterna
				 }
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	
			 url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
							<portlet:param name="struts_action" value="/cai/buscar_observaciones_internas" />
							<portlet:param name="cmd" value="<%=Constants.SAVE %>" />
					</portlet:renderURL>';
	</c:if>
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
	
	 url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
					<portlet:param name="struts_action" value="/judicial/buscar_observaciones_internas" />
					<portlet:param name="cmd" value="<%=Constants.SAVE %>" />
			</portlet:renderURL>';
</c:if>
	
	jQuery("#<portlet:namespace />observaciones_internas").load(url,params);
	jQuery('#<portlet:namespace/>obs').val('');
	
}

jQuery(document).ready(function() {
    var cuil = "<%=crmafi.getCuil_titular()%>";
    var inte = "<%=crmafi.getInte()%>";

    //se agrega llamada para verificar modi_fecha
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
          '&struts_action=/<%=portlet_name%>/buscar_afiliado_datos&cuil_titular=' + cuil +
          '&inte=' + inte;


    jQuery.ajax({
        url: url,
        async: false,
        success: function(data) {
            var obj = jQuery.parseJSON(data);
            var actualizaDomicilio = obj.actualizadomicilio;
            var actualizaTelefono = obj.actualizatelefono;

            if (actualizaDomicilio === true || actualizaDomicilio === "true" ||
                actualizaTelefono === true || actualizaTelefono === "true") {
                jQuery("#<portlet:namespace />divBotonActualizar").show();
                jQuery("#<portlet:namespace />divResultadoActualizarOK").hide();
            } else {
                jQuery("#<portlet:namespace />divBotonActualizar").hide();
                jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
            }
        }
    });
});


</script>

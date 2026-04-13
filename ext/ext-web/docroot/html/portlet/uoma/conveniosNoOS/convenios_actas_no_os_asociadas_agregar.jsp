<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
String portlet_name = null;	

if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "estudio_isidro";
}
Convenio convenio= (Convenio)request.getSession().getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
List<Convenio.ActaRelacionada> actasasociadas= null;

if (convenio!=null){
	actasasociadas = convenio.getActasRelacionadas();
}

String act = (String)request.getAttribute("accionOriginal");
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));


%>

<liferay-ui:error exception="<%= ConvenioSinActasRelacionadasException.class %>" message="convenio-sin-actas" />
<table class="lfr-table" width="100%">
	<tr>
		<td>
		<% if (esEdicion) { %>
			 <input type="text" value="" name="<portlet:namespace />acta_asociada_nro" id="<portlet:namespace />acta_asociada_nro" />&nbsp;
			 <input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarActaAsociada();" /> 
		<%} %>
		</td>
	</tr>
	<tr>
		<td>
		<div align="center" id="<portlet:namespace />buscandoActas">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td>
		<div align="center" id="<portlet:namespace />actasasociadas">
			<jsp:include page='convenios_actas_no_os_asociadas_search_result.jsp' /></div>
		</td>
	</tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">

	function actualizarDetalleCuotas(){		
		var cant_cuotas =  parseInt(jQuery('#cant_cuotas').val(),10);		
		var deuda =  parseFloat(jQuery('#<portlet:namespace />deuda').val());		
		var capitalCuota = redondear(redondear(deuda) / redondear(cant_cuotas));		
		var tasa =  parseFloat(jQuery('#tasa').val());		
		var redito = cant_cuotas * tasa / 100 / cant_cuotas;		
		
		jQuery("#detalle_cuotas").html("");		
		for (var i = 1; i<= cant_cuotas; i++) {
			var interesCuota = 0;
			if (i>1) {
				interesCuota = redondear(redondear(deuda- (capitalCuota * (i -1))) * redondear(redito));
			}			
			jQuery("#detalle_cuotas").append("Cuota: " + i + " Capital: $"+capitalCuota+" Interes: $" +interesCuota+ "<br/>");
		}
	}
	
	function redondear(nro){
		return Math.round(nro *100) / 100; 
	}


	function <portlet:namespace />agregarActaAsociada(){
		if (trim(document.getElementById("<portlet:namespace />cuit_entidad").value)==""){
			alert("Primero debe seleccionar una empresa");
			return false;
		}
		
			jQuery('#<portlet:namespace />buscandoActas').show();	
			var entidad_con=jQuery('#<portlet:namespace />entidad_con').val();			
			var nroActa=jQuery('#<portlet:namespace />acta_asociada_nro').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_convenio_acta_no_os_relacionada&acta_asociada_nro=' +nroActa + '&esEdicion=' + "<%=esEdicion%>" 	
			+'&accionOriginal='+ "<%=act%>"+'&entidad_con='+entidad_con+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad").value);
			
			jQuery('#<portlet:namespace />actasasociadas').load(url, function() {
														jQuery('#<portlet:namespace />buscandoActas').hide();
														 sumarTodo(); 
										   }
			 );	
	}

	function borraActaAsociada(id){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_convenio_acta_no_os_relacionada&acta_asociada_id=' +id+ '&esEdicion=' + "<%=esEdicion%>" 	+'&accionOriginal='+ "<%=act%>";
			jQuery('#<portlet:namespace />actasasociadas').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoActas').hide();            															
																			   }
															   );
		}	
	}

	jQuery('#<portlet:namespace />buscandoActas').hide();
</script>
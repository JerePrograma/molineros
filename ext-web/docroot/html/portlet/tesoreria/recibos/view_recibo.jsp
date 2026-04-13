<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.ospim.tesoreria.service.ReciboServiceUtil"%>

<% 
Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

String origen=ParamUtil.getString(request, "origen");

String reciboNro="";

boolean opcionIngreso = false;
if (recibo != null && recibo.getAfiliado() !=null ){
	opcionIngreso = true;
}


Calendar current = CalendarFactoryUtil.getCalendar();
Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
if (recibo!=null && recibo.getFecha() != null){
	fechaInicio.setTime(recibo.getFecha());
} else {
	fechaInicio.setTime(new Date());
}

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
	
	if(recibo==null || recibo.getNumero()==null){
		reciboNro=ReciboServiceUtil.proximoNumeroDisponible(WebKeysGlobal.AMTIMA);
	}
	
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA) || portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
boolean nueva=false;
if(portlet_name.equals("uoma")){
 	nueva=recibo!=null && recibo.getNumero()!=null && (recibo.getNumero().startsWith("0001") || recibo.getNumero().startsWith("0002") || recibo.getNumero().startsWith("0003") || recibo.getNumero().startsWith("Rend")|| recibo.getNumero().startsWith("BcaP") || recibo.getNumero().startsWith("Otro"));
}else if(portlet_name.equals("tesoreria")){
	nueva=recibo!=null && recibo.getNumero()!=null && (recibo.getNumero().startsWith("0001") || recibo.getNumero().startsWith("9999"));
}
boolean esEdicion = true;
if ((recibo !=null && recibo.getId() > 0) || nueva || showABMButtons){
	esEdicion = true;
}
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>
<liferay-ui:error exception="<%= DuplicateNumeroReciboException.class %>" message="numero-recibo-existente" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="ingreso-menor-fecha-contable" />

<script type="text/javascript">
function sumarConceptos(){
	var idsActas = document.getElementById("ids_actas").value.split(";");
	var idsConvenios = document.getElementById("ids_convenios").value.split(";");
	var totalChequesRechazados = document.getElementById("total_cheques_rechazados").value;
	var totalChequesASustituir = document.getElementById("total_cheques_no_depositados").value;
	var totalOtros = document.getElementById("total_otros").value;
	var capitalAnticiposTmp = document.getElementById("capitalAnticiposTmp").value;
	var totalPrestamos = document.getElementById("total_prestamos").value;

	var total = 0.0;
	if (idsActas.length >1){
		for (var i = 0; i<idsActas.length-1; i++){
			if (IsNumeric(trim(document.getElementById("total_acta_" + idsActas[i]).value))){
				total =  Math.round((total  + Math.round(parseFloat(document.getElementById("total_acta_" + idsActas[i]).value) * 100) /100)*100)/100;
			}
		}
	}

	if (idsConvenios.length >1){
		for (var i = 0; i<idsConvenios.length-1; i++){
			if (IsNumeric(trim(document.getElementById("total_convenio_" + idsConvenios[i]).value))){
				total =  Math.round((total  + Math.round(parseFloat(document.getElementById("total_convenio_" + idsConvenios[i]).value) * 100) /100)*100)/100;
			}
		}
	}
	
	total =  Math.round(
			(total  + Math.round(parseFloat(totalChequesRechazados) * 100) /100 
			+  Math.round(parseFloat(totalChequesASustituir) * 100) /100 
			+ Math.round(parseFloat(totalOtros) * 100) /100
			+  Math.round(parseFloat(capitalAnticiposTmp) *100)/100)*100 )/100
			+ Math.round(parseFloat(totalPrestamos) * 100) /100;
	document.getElementById("<portlet:namespace />total_conceptos").value =  total;
	agregarCeros(document.getElementById("<portlet:namespace />total_conceptos"));
}
</script>

<fieldset class="block-labels"><legend><liferay-ui:message	key="datos-ingreso" /></legend>
<table class="lfr-table">
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr> 
			<td><label><liferay-ui:message key="recibo" />&nbsp;N°:</label></td>
			<td>
			<%if(portlet_name.equals("uoma") || portlet_name.equals("tesoreria")){%>
				<select name="<portlet:namespace />recibo_pre" id="<portlet:namespace />recibo_pre" onChange="javascript:sugerirNroRecibo();">
					<option value="0001" <%if(recibo != null && recibo.getNumero().startsWith("0001")){%> selected="selected"  <% } %> >0001</option>
					<%if(portlet_name.equals("tesoreria")){%>
						<option value="9999" <%if(recibo != null && recibo.getNumero().startsWith("9999")){%> selected="selected"  <% } %> >9999</option>
						<option value="1010" <%if(recibo != null && recibo.getNumero().startsWith("1010")){%> selected="selected"  <% } %> >1010</option>
					<%}else{%>
						<option value="0002" <%if(recibo != null && recibo.getNumero().startsWith("0002")){%> selected="selected"  <% } %> >0002</option>
						<option value="0003" <%if(recibo != null && recibo.getNumero().startsWith("0003")){%> selected="selected"  <% } %> >0003</option>
						<option value="Rend" <%if(recibo != null && recibo.getNumero().startsWith("Rend")){%> selected="selected"  <% } %> >Rend</option>
						<option value="BcaP" <%if(recibo != null && recibo.getNumero().startsWith("BcaP")){%> selected="selected"  <% } %> >BcaP</option>
						<option value="Otro" <%if(recibo != null && recibo.getNumero().startsWith("Otro")){%> selected="selected"  <% } %> >Otro</option>
					<%}%>
					<%if(recibo != null && recibo.getNumero()!=null && !nueva){ %>
						<option value=""></option>
					<% }%>
				</select>
				<input type="text" name="<portlet:namespace />recibo_numero" id="<portlet:namespace />recibo_numero" maxlength="9" 
							size="12" value="<%= recibo != null && recibo.getNumero() != null && nueva ? recibo.getNumero().substring(4) : recibo != null && recibo.getNumero() != null && !nueva ? recibo.getNumero() : "" %>"			
			<%}else{%>
				<input type="text" name="<portlet:namespace />recibo_numero" id="<portlet:namespace />recibo_numero" maxlength="12" 
							value="<%= recibo != null && recibo.getNumero() != null ? recibo.getNumero() : "" %>"
			<%} %>
							<%if (!esEdicion) { %>readonly="readonly"<%} %> onBlur="javascript:verificarNroRecibo();"/>&nbsp;
							<%if (recibo == null || recibo.getId() == 0) {%><input type="checkbox" id="anulado" name="anulado" value="anulado" />Crear Anulado<%} %></td>
			<td><label><liferay-ui:message key="fecha-emision" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaInicioDia"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaInicioMes"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion   %>" />
			</td>
			<td colspan="2">
			  <%if(portlet_name.equals("farmacia")){%>
			    <input type="button" value="Propagar Fecha" onClick="<portlet:namespace />propagarFecha();" />
			  <%}else{%> &nbsp;<%}%>
			</td>
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<tr>
		<td><label><liferay-ui:message key="descripcion" />:</label></td>
		<td colspan="5"><textarea <%if (!esEdicion) { %>readonly="readonly"<%} %> rows="7" id="<portlet:namespace />obs" name="<portlet:namespace />obs" cols="100"><%=recibo != null && recibo.getObservaciones() != null ? recibo.getObservaciones() : "" %></textarea> </td>
		</tr>
</table>
</fieldset>
<table class="lfr-table" width="100%">
	<tr>
		<td width="100%" valign="top">
			<liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_conceptos.jsp">
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<input type="hidden" id="capitalAnticiposTmp" value="0.00"/>
		<input type="hidden" id="origen" name="origen" value="<%=origen%>"/>
			<td width="100%">
				<liferay-util:include page="/html/portlet/tesoreria/recibos/recibos_anticipo_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
	</tr>
	<tr>
		<td>
			<liferay-ui:message	key="total-conceptos" />:&nbsp;<input id="<portlet:namespace />total_conceptos" name="<portlet:namespace />total_conceptos" type="text" readonly="readonly" />
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
</table>
<div id="<portlet:namespace />formas_ingreso" name="<portlet:namespace />formas_ingreso">
<table class="lfr-table" width="100%">
		<tr>
		<input type="hidden" id="capitalIngresoTmp" value="0.00"/>
			<td width="100%">
							
				<liferay-util:include page="/html/portlet/tesoreria/recibos/formas_ingreso_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>				
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message	key="total-ingresos" />:&nbsp;<input name="<portlet:namespace />total_ingresos" id="<portlet:namespace />total_ingresos" type="text"/><!-- readonly="readonly"  -->
			</td>
		</tr>
</table>
</div>

<% if (esEdicion && !soloVer) { %> 
<br />
<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveRecibo();return false;"/>
<input type="hidden" name="<portlet:namespace />rec_<%= Constants.CMD %>" id="<portlet:namespace />rec_<%= Constants.CMD %>"/>
	<%if(!renderResponse.getNamespace().equals("_EST_1_") && !soloVer) {%>			
			&nbsp;<input type="submit" value="<liferay-ui:message key="crear-nuevo-recibo" />" onClick="<portlet:namespace />nuevoRecibo();return false;"/>
	<%}
	
	if(/*portlet_name.equals("tesoreria") &&*/ recibo != null && recibo.getNumero()!=null  ){%>
	    <input type="button" value="Imprimir" onClick="<portlet:namespace />imprimirRecibo();" />
	<%}else{%> &nbsp;<%}
}%>


				
<script type="text/javascript">	

	//VOY A BUSCAR 1er NRO
	<%if(null==recibo && !portlet_name.equals("farmacia")){%>
		sugerirNroRecibo();	
	<%}%>
	
	<%if(null==recibo && portlet_name.equals("farmacia")){%>
	jQuery('#<portlet:namespace />recibo_numero').val('<%=reciboNro%>');
    <%}%>
	function sugerirNroRecibo() {
		var pre=jQuery('#<portlet:namespace />recibo_pre').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sugerir_nro_recibo';
		url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url +='&recibo_pre='+pre;		
		url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#<portlet:namespace />recibo_numero').val(obj.numero);				                                                                                                                                                                                                                                                            
			}
		});		
		
	}	

	function <portlet:namespace />saveRecibo() {
		if (<portlet:namespace />validarCampos()) {
			jQuery("#<portlet:namespace />rec_<%= Constants.CMD %>").val("<%=(recibo == null || recibo.getId() == 0 ? Constants.ADD : Constants.UPDATE) %>");			
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry'			
			document.<portlet:namespace />rec.method = 'post';
			submitForm(document.<portlet:namespace />rec, url);
		}
	}
	
	function <portlet:namespace />validarCampos() {		
		var anuladoCh=jQuery("#anulado").is(":checked");
		try{
			<%if(!portlet_name.equals("tesoreria")){%>
				if(document.getElementsByName('<portlet:namespace />entidadIngreso').item(0).checked){					
					if ((trim(document.getElementById("<portlet:namespace />cuit_entidad").value) == "" || 
							trim(document.getElementById("<portlet:namespace />sucursal_entidad").value) == "") && anuladoCh==false ){
						alert("Debe seleccionar una empresa");
						return false;
					}
				}else{
					if (trim(document.getElementById("<portlet:namespace />cuil<%=origen%>").value) == "" || 
							trim(document.getElementById("<portlet:namespace />apellido<%=origen%>").value) == "" ){
						alert("Debe seleccionar un afiliado");
						return false;
					}
				}	
			<% }else{ %>				
				if ((trim(document.getElementById("<portlet:namespace />cuit_entidad").value) == "" || 
						trim(document.getElementById("<portlet:namespace />sucursal_entidad").value) == "" ) && anuladoCh==false){
					alert("Debe seleccionar una empresa");
					return false;
				}
			<% } %>

			if (trim(document.getElementById("<portlet:namespace />recibo_numero").value) == "") {
				alert("Debe ingresar el numero de recibo");
				return false;
			} 
			if (parseFloat(document.getElementById("<portlet:namespace />total_conceptos").value) != parseFloat(document.getElementById("<portlet:namespace />total_ingresos").value)) {
				alert("El Capital Total de Conceptos debe ser igual al Capital Total por Ingresos.");
				return false;
			}
			
		} catch (err) {			
			alert('error!: '+err);
			return false;
		}		
		return true;
	}	

	function setearCapitalIngreso(){
		document.getElementById("<portlet:namespace />total_ingresos").value =document.getElementById("capitalIngresoTmp").value;
	}	
	
	sumarConceptos();
	setearCapitalIngreso();
	
	function <portlet:namespace />nuevoRecibo(){
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry';
		document.<portlet:namespace />rec.method = 'post';
		submitForm(document.<portlet:namespace />rec, url);
	}
	
	
	function actualizarConceptos(){
		var fechaDia  = document.getElementById("<portlet:namespace />fechaInicioDia");
		var fechaMes= document.getElementById("<portlet:namespace />fechaInicioMes");
		var fechaAnio = document.getElementById("<portlet:namespace />fechaInicioAnio");
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_conceptos_ingreso_para_fecha';
		url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		
		
		url +='&fechaDia='+fechaDia.value;
		url +='&fechaMes='+fechaMes.value;
		url +='&fechaAnio='+fechaAnio.value;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#<portlet:namespace />otro_concepto').find('option').remove();
				jQuery('#<portlet:namespace />otro_concepto').append('<option value=""></option>');
				for(var i =0;i< obj.conceptos.length; i++){
					jQuery('#<portlet:namespace />otro_concepto').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}	
	
	function verificarNroRecibo(){
		var pre=jQuery('#<portlet:namespace />recibo_pre').val();
		var numero=jQuery('#<portlet:namespace />recibo_numero').val();
		if(trim(numero)!=''){
			if(pre=== undefined){
				pre="";	
			}
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_nro_recibo';
			url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			url +='&recibo_pre='+pre+'&recibo_numero='+numero;		
			
			url += '&rnd=' + Math.floor(Math.random()*100);	
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					
					if(obj.existe=='true'){
						alert('El número de recibo ya fue utilizado.');
					}								                                                                                                                                                                                                                                                            
				}
			});		
		}
	}	
	
	jQuery(document).ready(function() {
	
		
		jQuery(".ui-datepicker-trigger").hide();
		jQuery('#<portlet:namespace/>fechaInicioMes').change(function(){
			
			actualizarConceptos();
		});
		jQuery('#<portlet:namespace/>fechaInicioAnio').change(function(){
			actualizarConceptos();
		});
	});
	
	function <portlet:namespace />propagarFecha() {
       var fechaDia  = document.getElementById("<portlet:namespace />fechaInicioDia");
       var fechaMes= document.getElementById("<portlet:namespace />fechaInicioMes");
       var fechaAnio = document.getElementById("<portlet:namespace />fechaInicioAnio");
	   document.getElementById("<portlet:namespace />fechaPrestamoDia").value=fechaDia.value;
	   document.getElementById("<portlet:namespace />fechaPrestamoMes").value=fechaMes.value;
	   document.getElementById("<portlet:namespace />fechaPrestamoAnio").value=fechaAnio.value;
	   document.getElementById("<portlet:namespace />fechaPagoDia").value=fechaDia.value;
	   document.getElementById("<portlet:namespace />fechaPagoMes").value=fechaMes.value;
	   document.getElementById("<portlet:namespace />fechaPagoAnio").value=fechaAnio.value;
	}
	
	function <portlet:namespace />imprimirRecibo(){
	  <%if(portlet_name.equals("tesoreria")){%>
			window.location.href ="/pdfservlet/?accion=reciboIngresoOspim&id=<%=recibo != null && recibo.getId() != 0 ? recibo.getId(): ""%>&cuit=<%=recibo != null && recibo.getEmpresa()!=null && recibo.getEmpresa().getCuit() != "" ? recibo.getEmpresa().getCuit(): ""%>";
	  <%}else if(portlet_name.equals("farmacia")){%>
	        window.location.href ="/pdfservlet/?accion=reciboIngresoAmtima&id=<%=recibo != null && recibo.getId() != 0 ? recibo.getId(): ""%>&cuit=<%=recibo != null && recibo.getEmpresa()!=null && recibo.getEmpresa().getCuit() != "" ? recibo.getEmpresa().getCuit(): ""%>";
	  <%}else if(portlet_name.equals("uoma")){%>
	        window.location.href ="/pdfservlet/?accion=reciboIngresoUoma&id=<%=recibo != null && recibo.getId() != 0 ? recibo.getId(): ""%>&cuit=<%=recibo != null && recibo.getEmpresa()!=null && recibo.getEmpresa().getCuit() != "" ? recibo.getEmpresa().getCuit(): ""%>";
	  <%}%>		
	}
	
	function setearIngresoPrestamos(){ //Pedido  por Carolina Flotts 20/11/2024
	    jQuery('#<portlet:namespace />tipo_ingreso').val('Deposito_Bancario_2');  //Transferencia
	    changeTipo();
	    jQuery('#<portlet:namespace />id_cta_bcria').val(21); //BBVA Turismo
	}	    
			
</script>


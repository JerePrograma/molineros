<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
	
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}
if(renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
} 

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS) || PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO) ;
boolean auditorActas = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_AUDITOR_ACTAS);

String cuit= (String)request.getSession().getAttribute("cuit");

boolean molinera= (Boolean)((request.getSession().getAttribute("molinera"))!=null&&((Boolean)request.getSession().getAttribute("molinera")))?true:false;
if (auditorActas){
	showABMButtons = true;
}
List<ActaEstadoSeguimiento> estadosSeguimActa = ActaServiceUtil.getEstadosSeguimientoActas();

Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

boolean esEdicion = false;

if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
	esEdicion = true;
}


Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Date fechaInicioActa = acta != null ? acta.getFechaInicio() : null; 
if (fechaInicioActa == null) {
	fechaInicio.setTime(new Date());
}
else{
	fechaInicio.setTime(acta.getFechaInicio());
}


Calendar fechaPago = CalendarFactoryUtil.getCalendar();
Date fechaPagoActa = acta != null ? acta.getFechaPago() : null; 
if (fechaPagoActa == null) {
	if(molinera){
		fechaPago=fechaInicio;
	}else{
		Calendar pago = Calendar.getInstance();
		pago.add(Calendar.MONTH, 1);
		fechaPago.setTime(pago.getTime());
	}
}
else{
	fechaPago.setTime(acta.getFechaPago());
}
Calendar current = CalendarFactoryUtil.getCalendar();


boolean esAdd = (acta == null || (request.getAttribute("accionOriginal") != null && request.getAttribute("accionOriginal").equals("add")))? true : false;
if (acta != null && acta.isActaCerrada() && !auditorActas){
	esEdicion = false;
}

Calendar cierreFecha = null;
if (acta != null && acta.getCierre_fecha() != null){
	cierreFecha = CalendarFactoryUtil.getCalendar();
	cierreFecha.setTime(acta.getCierre_fecha());
}
String sufi="acta_";
%>

<liferay-ui:error exception="<%= DuplicateActaIdException.class %>" message="acta-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.FaltaFechaCierreActaException.class %>" message="falta-fecha-cierre" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-menor-fecha-contable" />
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos" /></legend>
<table class="lfr-table">
		<tr>
			<td>
				<b><liferay-ui:message	key="empresa" />:</b>
			</td>
			<td colspan="5">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
			  		<liferay-util:param name="esEditable" value='<%=String.valueOf(acta== null || acta.getId() == 0) %>'/>
			  		<liferay-util:param name="cuit" value='<%= (acta!=null && acta.getEmpresa() != null ? acta.getEmpresa().getCuit() :	cuit!=null?cuit:"") %>'/>
			  		<liferay-util:param name="sucu" value='000'/>
			  		<liferay-util:param name="razon" value='<%=acta!=null && acta.getEmpresa() != null ? acta.getEmpresa().getRazon_soc() :"" %>'/>
			  		<liferay-util:param name="portlet_name" value='tesoreria'/>
			  		<liferay-util:param name="suf_entidad" value='<%=sufi%>'/>
					<liferay-util:param name="suf" value='<%=sufi%>'/>
				</liferay-util:include>
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr> 
			<input type="hidden" id="<portlet:namespace />acta_id" name="<portlet:namespace />acta_id" value="<%= acta != null ? acta.getId() : "" %>"/>
			<td><label><liferay-ui:message key="acta" />&nbsp;N°:</label></td>
			<td><input id="<portlet:namespace />acta_numero"
			name="<portlet:namespace />acta_numero" size="13" maxlength="8"
			type="text"
			value="<%= acta != null ? acta.getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
			<td><label><liferay-ui:message key="fecha-acta" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaInicioDia"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaInicioMes"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 20%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion   %>" />
			</td>
			<td><label><liferay-ui:message key="fecha-actualizacion" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaPagoDia"
				dayValue="<%=fechaPago.get(Calendar.DATE)%>" 
				monthParam="fechaPagoMes"
				monthValue="<%=  fechaPago.get(Calendar.MONTH)%>"				
				yearParam="fechaPagoAnio"
				yearValue="<%= fechaPago.get(Calendar.YEAR)%>"
				yearRangeStart="<%= current.get(Calendar.YEAR) -20 %>"	
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 20%>"
				firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion  %>" />
				&nbsp;
			</td>
			<% if (esEdicion && (acta == null || !acta.isActaCerrada())){ %>
			<td><input type="button" value="<liferay-ui:message key="recalcular-intereses" />" onClick="<portlet:namespace />recalcularIntereses();" /></td>
			<%} %>
		</tr>
		<tr> 
			<td colspan="6">&nbsp;</td>
		</tr>
		<tr> 
			<td><label><liferay-ui:message key="fecha-recepcion" />:</label></td>
			<td colspan="2">
					<liferay-ui:input-date
					dayParam="fechaActaDia"
					monthParam="fechaActaMes"
					yearParam="fechaActaAnio"
					dayValue="<%= cierreFecha != null && acta!=null && acta.isActaCerrada()? cierreFecha.get(Calendar.DATE) : current.get(Calendar.DATE)%>" 
					monthValue="<%= cierreFecha != null && acta!=null && acta.isActaCerrada() ? cierreFecha.get(Calendar.MONTH) : current.get(Calendar.MONTH)%>"				
					yearValue="<%= cierreFecha != null && acta!=null && acta.isActaCerrada()? cierreFecha.get(Calendar.YEAR) : current .get(Calendar.YEAR)%>"
					yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
					yearRangeEnd="<%= current.get(Calendar.YEAR) + 20%>"
					firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion   %>" 
					dayNullable="true"
					monthNullable="true"
					yearNullable="true"/>
				
			</td>
			<td colspan="2">
				<label><liferay-ui:message key="estado-seguim" />:</label>
				<select name="<portlet:namespace />estado_seguim"
							id="<portlet:namespace />estado_seguim" ><%-- <% if (!esEdicion) { %> disabled="disabled" <%} %> --%>		
							<option value="0">--Sin estado de seguimiento--</option>
							<% for(ActaEstadoSeguimiento aes : estadosSeguimActa) {%>
								<option value="<%=aes.getId() %>"  
								<%= (acta != null && acta.getEstadoSeguimiento()!=null && acta.getEstadoSeguimiento().getId() == aes.getId())?"selected":""%>
								><%=aes.getDescripcion() %></option> 
							<%} %>
						</select>
			</td>
			<td>
				<img alt="Actualizar Estado Seguimiento" src="<%=themeDisplay.getPathThemeImages()+"/portlet/refresh.png"%>" onclick="javascript:actualizarEstadoSeguimiento();">
				<div id="<portlet:namespace />actuEstadoSegResult" style="display: none;" ><p>Se actualizó correctamente el estado del Acta</p></div>
			</td>
		</tr>
</table>
</fieldset>
<table class="lfr-table" width="100%">
		<tr>
			<td colspan="3" width="50%" valign="top">
			<fieldset class="block-labels"><legend><liferay-ui:message
				key="inspectores-firmantes" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/inspectores_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</fieldset>
			<input type="hidden" value="" id="deudaActasTmp"/>
			<script type="text/javascript">


				function sumarTodo(){
					var deudaActasTmp = 0;
					var otros = 0;
					var subtotal = 0; 
					var inte = 0;
					if (document.getElementById("deudaActasTmp")!=null){
						var id = document.getElementById("deudaActasTmp").value.split(";");
						var total = 0.0;
						if (id.length >1){
							for (var i = 0; i<id.length-1; i++){
								if (IsNumeric(trim(document.getElementById("saldo_" + id[i]).value))){
									deudaActasTmp =  Math.round((deudaActasTmp  + Math.round(parseFloat(document.getElementById("saldo_" + id[i]).value) * 100) /100)*100)/100;
								}
							}
						}
						if (document.getElementById("<portlet:namespace />dedua") != null){
							document.getElementById("<portlet:namespace />dedua").value = deudaActasTmp;
						}
					}
					if (document.getElementById("<portlet:namespace />otros") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />otros").value)){
						otros = parseFloat(document.getElementById("<portlet:namespace />otros").value);
					}
					if (document.getElementById("<portlet:namespace />subtotal") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />subtotal").value)){
						subtotal = parseFloat(document.getElementById("<portlet:namespace />subtotal").value);
					}
					if (document.getElementById("<portlet:namespace />inte") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte").value)){
						inte = parseFloat(document.getElementById("<portlet:namespace />inte").value);
					}
					if (document.getElementById("<portlet:namespace />total")!= null){
						document.getElementById("<portlet:namespace />total").value = Math.round((Math.round(otros *100) / 100 + Math.round(deudaActasTmp *100) / 100 + Math.round(subtotal *100) / 100 + Math.round(inte *100) / 100)*100)/100;
					}
				}
			
				function setearTempValueActas(ids){
					document.getElementById("deudaActasTmp").value = ids;
					sumarTodo();
				}
					
			</script>
			<fieldset class="block-labels"><legend><liferay-ui:message
				key="actas-saldo" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/actas_asociadas_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion && (acta == null || !acta.isActaCerrada()))%>"/>
				</liferay-util:include>
			</fieldset>			
			</td>
			<td colspan="3" valign="top">
			<fieldset class="block-labels"><legend><liferay-ui:message key="detalle" /></legend>
				<table>
					<!--<tr>
						 <td colspan="2">
						    Central&nbsp;
							<input type="radio" name="<portlet:namespace />inspectorActa" value="false" id="<portlet:namespace />inspectorFalse" onchange="cambioRadioButton();" 
							<if (acta == null || (acta != null && !acta.isInspector())) { %> checked="checked" <}> 
							<if (acta != null && acta.getId() != 0) { %> disabled="disabled" <} >/>
							&nbsp;&nbsp;Inspector
							<input type="radio" name="<portlet:namespace />inspectorActa" value="true" id="<portlet:namespace />inspectorTrue" onchange="cambioRadioButton();"
							<if (acta != null && acta.isInspector()) { %> checked="checked" <}>
							</if (acta != null && acta.getId() != 0) { %> disabled="disabled" <}>/>
						</td>
					</tr> -->
					<tr>
						<td><liferay-ui:message key="subtotal" />:</td>
						<td>
							<input type="text" name="<portlet:namespace />subtotal" id="<portlet:namespace />subtotal"  onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange=" sumarTodo()"
								<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null  && acta.getCapital()!=null ?acta.getCapital().toString() : ""%>"/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="dedua-actas" />:</td>
						<td>
							<input type="text" name="<portlet:namespace />dedua" id="<portlet:namespace />dedua" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange=" sumarTodo()" 
								<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null &&  acta.getDeudaActasRelacionadas() != null ? acta.getDeudaActasRelacionadas().toString() : "" %>"/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="otros" />:</td>
						<td>
							<input type="text" name="<portlet:namespace />otros" id="<portlet:namespace />otros" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange=" sumarTodo()"
								<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getOtros()!=null ? acta.getOtros() : "" %>"/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="inte-fina" />:</td>
						<td>
							<input type="text"	name="<portlet:namespace />inte" id="<portlet:namespace />inte" onkeydown="allowOnlyDigitsAndDecimals(event)"	onChange=" sumarTodo()"
								<% if (acta != null && acta.isActaCerrada()) { %> readonly="readonly" <% }%> value="<%= acta != null && acta.getInteres()  != null ? acta.getInteres().toString() : "" %>" />
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="total"/>:</td>
						<td>
							<input type="text" name="<portlet:namespace />total" id="<portlet:namespace />total"  readonly='readonly' />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="<liferay-ui:message key="periodos" />" onClick="<portlet:namespace />buscarPeriodos();return false;"/>
						</td>
					</tr>
				</table>
				</fieldset>
				<liferay-util:include page="/html/portlet/empresas/cuentas_bancarias.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>	
				</liferay-util:include>
				
			</td>
		</tr>		
</table>
<div id="div_formas_de_pago"  >
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
			<fieldset class="block-labels">
				<legend><liferay-ui:message	key="formas-de-pago" /></legend>
				<liferay-util:include page="/html/portlet/tesoreria/actas/formas_pago_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</fieldset>
			</td>
		</tr>
</table>
</div>


<% if (esEdicion) { %> 
<br />

<% if (!(request.getAttribute("fromActa")!=null && request.getAttribute("fromActa").equals("fromActa")) && !portlet_name.equals("estudio_isidro")){ %>
<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveActa();return false;"/>
<% }else if(portlet_name.equals("estudio_isidro")) {%>
<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveActaPopup();return false;"/>
<% }  else {%>
<input type="hidden" name="fromActa" value="fromActa"/>
<%} %>
<% if(showABMButtons && (acta == null || !acta.isActaCerrada())) { %>
<input type="submit" value="<liferay-ui:message key="crear-acta" />" onClick="<portlet:namespace />closeActa();return false;"/>
<%} %>
<input type="hidden" value="" name="cerrarActa" id="cerrarActa" />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa"/>
<input type="hidden" value="" name="tabs1" id="tabs1"/>
<input type="hidden" value="" name="view" id="view"/>
<input type="hidden" value="" name="popupActa" id="popupActa"/>
<input type="hidden" value="" name="popupActaSeguimiento" id="popupActaSeguimiento"/>
<input type="hidden" value="<%= request.getAttribute("accionOriginal") != null && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE)%>" name="accionOriginal" id="accionOriginal"/>
<%} %>
<input type="button" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />reporte();" />
<div style="visibility:hidden" id="<portlet:namespace />actualizarEmpresa">		
	</div>
				
				
<script type="text/javascript">
		
	var popup;
	function <portlet:namespace />saveActa() {		
		 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
			 document.getElementById("<portlet:namespace />id_banco").focus();
			 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
			 return false;
		 }
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />act.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
			 
			<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>				
				//document.getElementById("_EST_1_act:#popupActa").value="true";
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_actas_entry" /></portlet:actionURL>';
				document.getElementById("popupActa").value="true";
				document.getElementById("popupActaSeguimiento").value="true";							
			<%}else{%>				
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_actas_entry" /></portlet:actionURL>';
			<%}%>
			document.<portlet:namespace />act.method = 'post';
			document.getElementById("cambioSolapa").name = "xx";
			document.getElementById("tabs1").name = "xx2";
			submitForm(document.<portlet:namespace />act, url);			
		}
	}     
	
	function <portlet:namespace />saveActaPopup() {
		document.<portlet:namespace />act.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
		if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
			 document.getElementById("<portlet:namespace />id_banco").focus();
			 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
			 return false;
		}		
			var form = jQuery(document.<portlet:namespace />act);
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_actas_entry" /></portlet:actionURL>';
			document.getElementById("popupActa").value="true";
			document.getElementById("popupActaSeguimiento").value="true";	
			var cuit=jQuery('#<portlet:namespace />cuit').val();
			form.ajaxForm(
				{
					url: url,
			    	//target: tar,//".ui-dialog-content",//poopup
			        type: "POST",
			        beforeSubmit: function() {
			        },
			        success: function(data) {
			        	Liferay.Popup.close(popupActa);	        	
			        	/* jQuery('#<portlet:namespace />busquedaCalculoDiv').html(data); */
			        	<% if (portlet_name.equals("estudio_isidro")) { %>	
						jQuery('#<portlet:namespace />buscandoCalculoDiv').show();
						buscarCalculosDeuda();			
						jQuery('#<portlet:namespace />arrow_calculos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		        	    <%}%>
			        }
			    }
			);	
						
			form.submit();    
		
	}
	
	function <portlet:namespace />validarCampos() {
		try{
			if(document.getElementById("<portlet:namespace />sucursal_entidad<%=sufi%>").value == ""){
						document.getElementById("<portlet:namespace />sucursal_entidad<%=sufi%>").value='000';						
			}			
			if (trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value) == ""){
					alert("Debe seleccionar una empresa");					
				return false;
			}
			

			
		} catch (err) {
			return false;
		}
		return true;
	}

	function submitFormNotSave(){
		//if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />act.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
			document.getElementById("cambioSolapa").value="cambioSolapa";
			document.getElementById("tabs1").value="detalle-acta-inspectores";
			document.getElementById("view").value="true";
			<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_actas_entry" /></portlet:actionURL>';
			<%}else{%>
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_actas_entry" /></portlet:actionURL>';
			<%}%>
			document.<portlet:namespace />act.method = 'post';
			submitForm(document.<portlet:namespace />act, url);
		//}
	}

	 
	 function cambioRadioButton(){
		 var esEdicion = <%= esEdicion%>;
		 if (esEdicion){
			 if (document.getElementById("<portlet:namespace />inspectorTrue").checked == true){
				document.getElementById("<portlet:namespace />subtotal").readOnly  = true;
				document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				document.getElementById("<portlet:namespace />otros").readOnly  = false;
				document.getElementById("<portlet:namespace />inte").readOnly  = true;
			 }else{
				 document.getElementById("<portlet:namespace />subtotal").readOnly = false;
				 document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				 document.getElementById("<portlet:namespace />otros").readOnly  = false;
				 document.getElementById("<portlet:namespace />inte").readOnly  = false;			 
			 }	
		 } else {
				document.getElementById("<portlet:namespace />subtotal").readOnly  = true;
				document.getElementById("<portlet:namespace />dedua").readOnly  = true;
				document.getElementById("<portlet:namespace />otros").readOnly  = true;
				document.getElementById("<portlet:namespace />inte").readOnly  = true;
			 
		 }
	 }

	 var popup;
	 function <portlet:namespace />buscarPeriodos() {	 	 
	     popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-periodos" />",modal:true,position:[100,50],xy: ['center', 100],width:1200});	     
	     var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos';
	     
	     <%if (esEdicion) {%>
	    	 url += '&esEdicion=esEdicion';
	     <%}%>	     
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url);    
	 }
	 
	function <portlet:namespace />recalcularIntereses(){
		 popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-periodos" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		 
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_periodos&recalcular=recalcular';
		 url=url+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value)
					+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
					+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
					+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;
	     <%if (esEdicion) {%>
	    	 url += '&esEdicion=esEdicion';
	     <%}%>
	     url += '&rnd=' + Math.floor(Math.random()*100);
	 	jQuery(popup).load(url, function(){
	 			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/ver_actas_relacionadas&ACTAS_ACTION_EDICION=ACTAS_ACTION_EDICION';
	 						 jQuery('#<portlet:namespace />actasasociadas').load(url);
					 });

	} 
		 
	 function <portlet:namespace />closeActa(){
		 if (document.getElementById("div_formas_de_pago").style.visibility == "collapse"){
			 document.getElementById("div_formas_de_pago").style.visibility = "visible";
			 alert("Por favor, complete una forma de pago antes de crear el acta correspondiente.");
			 return false;
		 } else {
			 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != "" ||
							 trim(jQuery('#<portlet:namespace />importe_cheque').val())!= ""){
				 document.getElementById("<portlet:namespace />id_banco").focus();
				 alert("Para agregar informacion sobre cheques debe presionar el boton 'Agregar'");
				 return false;
			 }
		 }
		 if (<portlet:namespace />validarCampos()) {
			 if (trim(document.getElementById("<portlet:namespace />acta_numero").value)== ""){
				 alert("Por favor, ingrese un numero de acta");
				 document.getElementById("<portlet:namespace />acta_numero").focus();
				 return false;
			 }

			 var fechaActaDia=trim(document.getElementById("<portlet:namespace />fechaActaDia").value);
			 var fechaActaMes=trim(document.getElementById("<portlet:namespace />fechaActaMes").value);
			 var fechaActaAnio=trim(document.getElementById("<portlet:namespace />fechaActaAnio").value);

			 if (fechaActaDia == "" || fechaActaMes == "" || fechaActaAnio == ""){
				 alert("Debe completar la fecha del acta");
				 document.getElementById("<portlet:namespace />fechaActaDia").focus();
				 return false;
			 }
				document.<portlet:namespace />act.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (acta == null ? Constants.ADD : Constants.UPDATE) %>";
				<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
					document.getElementById("popupActa").value="true";
					document.getElementById("popupActaSeguimiento").value="true";
					var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_actas_entry" /></portlet:actionURL>';
				<%}else{%>
					var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_actas_entry';					
				<%}%>
				document.<portlet:namespace />act.method = 'post';
				document.getElementById("cambioSolapa").name = "xx";
				document.getElementById("tabs1").name = "xx2";
				document.getElementById("cerrarActa").value = "cerrarActa";				
				<%if(renderResponse.getNamespace().equals("_EST_1_")){%>				
				var form = jQuery(document.<portlet:namespace />act);
				form.ajaxForm(
						{
							url: url,
					    	//target: tar,//".ui-dialog-content",//poopup
					        type: "POST",
					        beforeSubmit: function() {					        	
					        },
					        success: function(data) {
					        	Liferay.Popup.close(popupActa);
					        	jQuery('#<portlet:namespace />busquedaCalculoDiv').css('display','none');
					        	jQuery('#<portlet:namespace />tabla_resumen').html(data);					    
					        }
					    }
					);	
								
					form.submit();    
				<%}else{%>					
					submitForm(document.<portlet:namespace />act, url);
				<%}%>
			}
	 }

	 function <portlet:namespace />reporte() {
		 var otros = document.getElementById("<portlet:namespace />otros").value; 
		 var subtotal = document.getElementById("<portlet:namespace />subtotal").value;
		 var inte = document.getElementById("<portlet:namespace />inte").value;
			var url = '/xlsservlet/?reporte=ACTA_PERIODOS_DETALLE&totales=totales&otros=' + otros +
				'&subtotal=' + subtotal +
				'&inte=' + inte +
				'&acta_numero='  + trim(document.getElementById("<portlet:namespace />acta_numero").value) +
				'&cuit=' + trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value) + 
				'&desc=' + escape(trim(document.getElementById("<portlet:namespace />entidad<%=sufi%>").value)) 
				+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
				+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
				+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;
			  url += '&rnd=' + Math.floor(Math.random()*100);
			  window.location.href = url;
	 }
	 
	// cambioRadioButton();
	 sumarTodo();

	 function cambiaCuit<%=sufi%>(){
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_cta_bcria_empresa';			
			url+='&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value);				
		 	url+='&sucursal='+trim(document.getElementById("<portlet:namespace />sucursal_entidad<%=sufi%>").value);
			url+='&accion=BUSCAR';
		jQuery('#<portlet:namespace />ctas_bcrias_result').load(url, function() {});
	 }
	 <%if (acta==null){%>
		 if(trim(document.getElementById("<portlet:namespace />cuit_entidad<%=sufi%>").value!="")){		 
			 <portlet:namespace />buscarEntidad<%=sufi%>();
		 }
	 <%}%>
	 
	 function actualizarEstadoSeguimiento(){
		 
			var id_estado_seg = jQuery('#<portlet:namespace />estado_seguim').val();
			var id_acta = jQuery('#<portlet:namespace />acta_id').val();

			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/tesoreria/actaCambiarEstadoSeguimiento&id_estado='+id_estado_seg+'&id_acta='+id_acta;
			
			jQuery.ajax({   
				url: url,
				success: function(data){
											
					var obj = jQuery.parseJSON(data);
					if(obj.result == 'true'){
						jQuery("#<portlet:namespace />actuEstadoSegResult").show();		
					}
				}	
			});
	}
		
</script>



<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.ospim.global.ExisteReciboConvenioException"%>

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

boolean esEdicion = true;
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);


Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Date fechaInicioConvenio = convenio != null ? convenio.getFechaInicio() : null; 
if (fechaInicioConvenio == null) {
	fechaInicio.setTime(new Date());
}
else{
	fechaInicio.setTime(convenio.getFechaInicio());
}

List<ConvenioEstadoSeguimiento> estadosSeguimConvenio = ConvenioServiceUtil.getEstadosSeguimientoConvenios();

Calendar current = CalendarFactoryUtil.getCalendar();

String entidad_convenio=null!=convenio?convenio.getEntidad():"";

boolean esAdd = (convenio == null || (request.getAttribute("accionOriginal") != null && request.getAttribute("accionOriginal").equals("add")))? true : false;

String numeroConvenio = null;
if (convenio != null && convenio.getNumero() != null && !convenio.getNumero().trim().equals("")){
		 numeroConvenio = String.valueOf(convenio.getNumero());
	} else if (convenio != null && convenio.getId() != 0) {
		numeroConvenio = String.valueOf(convenio.getId());
	}
%>
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-menor-fecha-contable" />
<liferay-ui:error exception="<%= DuplicateConvenioIdException.class %>" message="convenio-duplicada" />
<liferay-ui:error exception="<%= ExisteReciboConvenioException.class %>" message="existe-recibo-convenio" />
<fieldset class="block-labels"><legend><liferay-ui:message	key="datos" /></legend>
<table class="lfr-table">
		<tr>
			<td>
				<b><liferay-ui:message	key="empresa" />:</b>
			</td>
			<td colspan="5" width="100%">
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  			<liferay-util:param name="esEditable" value='<%= String.valueOf(convenio== null || convenio.getId() == 0) %>'/>
			  		<liferay-util:param name="cuit" value='<%= convenio!=null && convenio.getEmpresa() != null ? convenio.getEmpresa().getCuit() :	"" %>'/>
			  		<liferay-util:param name="sucu" value='<%=convenio!=null && convenio.getEmpresa() != null ? convenio.getEmpresa().getSucursal() :"" %>'/>
			  		<liferay-util:param name="razon" value='<%=convenio!=null && convenio.getEmpresa() != null ? convenio.getEmpresa().getRazon_soc() :"" %>'/>
			  		<liferay-util:param name="portlet_name" value='tesoreria'/>
				</liferay-util:include>
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr> 
			<td><label><liferay-ui:message key="entidad" />:</label></td>
			<td>
				<select name="<portlet:namespace/>entidad_con" id="<portlet:namespace/>entidad_con">	
								<%if(!portlet_name.equals("farmacia")&&!portlet_name.equals("uoma")){%>
								<option selected value=""></option>
								<%}%>							
								<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
									<% if(portlet_name.equals("uoma") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)) {%>									
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(portlet_name.equals("farmacia") && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)){%>
											<option value="<%= entidad %>" selected><%=entidad%></option>
									<%}else if(!portlet_name.equals("farmacia") && !portlet_name.equals("uoma")){%>
											<option value="<%= entidad %>"><%=entidad%></option>
									<%}%>
								<%}%>								
				</select>
			</td>
			<td><label><liferay-ui:message key="convenio" />&nbsp;N°:</label></td>
			<td><input type="text" readonly="readonly" name="<portlet:namespace />convenio_nro" id="<portlet:namespace />convenio_nro" 
							value="<%= numeroConvenio != null ? numeroConvenio : "" %>"/></td>
			<td><label><liferay-ui:message key="fecha" />:</label></td>
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
				<label><liferay-ui:message key="estado-seguim" />:</label>
				<select name="<portlet:namespace />estado_seguim"
						id="<portlet:namespace />estado_seguim" ><%-- <% if (!esEdicion) { %> disabled="disabled" <%} %> --%>		
						<option value="0">--Sin estado de seguimiento--</option>
						<% for(ConvenioEstadoSeguimiento ces : estadosSeguimConvenio) {%>
							<option value="<%=ces.getId() %>"  
							<%= (convenio != null && convenio.getEstadoSeguimiento()!=null && convenio.getEstadoSeguimiento().getId() == ces.getId())?"selected":""%>
							><%=ces.getDescripcion() %></option> 
						<%} %>
					</select>
			</td>		
		</tr>
</table>
</fieldset>
<input type="hidden" value="" id="deudaActasTmp"/>
<input type="hidden" value="" id="interesCheque"/>
<input type="hidden" value="" id="capitalCheque"/>
<script type="text/javascript">


				function sumarTodo(){
					var deudaActasTmp = 0;
					var inte = 0;
					var capital = 0;
					if (document.getElementById("deudaActasTmp")!=null){
						var id = document.getElementById("deudaActasTmp").value.split(";");
						var total = 0.0;
						if (id.length >1){
							for (var i = 0; i<id.length-1; i++){
								if (!IsNumeric(trim(document.getElementById("ajuste_capital_" + id[i]).value))){
									document.getElementById("ajuste_capital_" + id[i]).value = '0';
								}
								document.getElementById("total_" + id[i]).value = Math.round((Math.round(parseFloat(document.getElementById("importe_" + id[i]).value) * 100) /100
										+ Math.round(parseFloat(document.getElementById("ajuste_capital_" + id[i]).value) * 100) /100 )*100)/100;
								deudaActasTmp =  Math.round((deudaActasTmp  + Math.round(parseFloat(document.getElementById("importe_" + id[i]).value) * 100) /100
									+ Math.round(parseFloat(document.getElementById("ajuste_capital_" + id[i]).value) * 100) /100 )*100)/100;
							}
						}
						if (document.getElementById("<portlet:namespace />deuda") != null){
							document.getElementById("<portlet:namespace />deuda").value = deudaActasTmp;
						}
					}
					if (document.getElementById("<portlet:namespace />capital") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />capital").value)){
						capital = parseFloat(document.getElementById("<portlet:namespace />capital").value);
					}
					
					var ajusteCap = 0;
					var ajusteInt = 0;
					if (document.getElementById("<portlet:namespace />ajuste_capital_forma_pago") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />ajuste_capital_forma_pago").value)){
						ajusteCap = parseFloat(document.getElementById("<portlet:namespace />ajuste_capital_forma_pago").value);
					}
					if (document.getElementById("<portlet:namespace />ajuste_interes_forma_pago") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />ajuste_interes_forma_pago").value)){
						ajusteInt = parseFloat(document.getElementById("<portlet:namespace />ajuste_interes_forma_pago").value);
					}
					if (document.getElementById("<portlet:namespace />inte") != null 
							&& IsNumeric(document.getElementById("<portlet:namespace />inte").value)){
						inte = parseFloat(document.getElementById("<portlet:namespace />inte").value);
					}
					
					if (document.getElementById("<portlet:namespace />totalConvenio")!= null){
						document.getElementById("<portlet:namespace />totalConvenio").value = Math.round((Math.round(deudaActasTmp *100) / 100 + Math.round(ajusteCap *100) / 100 + Math.round(ajusteInt *100) / 100 )*100)/100;
					}
					if (document.getElementById("<portlet:namespace />totalPagos")!= null){
						document.getElementById("<portlet:namespace />totalPagos").value = Math.round((Math.round(capital *100) / 100 + Math.round(inte *100) / 100 + Math.round(ajusteCap *100) / 100 + Math.round(ajusteInt *100) / 100 )*100)/100;
					}
				}
			
				function setearTempValueActas(ids){
					document.getElementById("deudaActasTmp").value = ids;
					sumarTodo();
				}
					
			</script>
<table class="lfr-table" width="100%"> 
	<tr>
		<td valign="top" width="50%">
		<fieldset class="block-labels">
			<legend><liferay-ui:message	key="actas-saldo" /></legend>
					<liferay-util:include page="/html/portlet/uoma/conveniosNoOS/convenios_actas_no_os_asociadas_agregar.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion)%>"/>
					</liferay-util:include>
		</fieldset>
		</td>
		<td valign="top">
			<fieldset class="block-labels"><legend><liferay-ui:message key="detalle-pagos" /></legend>
				<table>
					<tr>
						<td><liferay-ui:message key="capital" />:</td>
						<td>
							<input type="text" name="<portlet:namespace />capital" id="<portlet:namespace />capital" readonly="readonly"	onChange=" sumarTodo()" 
								value="<%= convenio != null &&  convenio.getDeudaActasRelacionadas() != null ? convenio.getDeudaActasRelacionadas().toString() : "" %>"/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="inte-fina" />:</td>
						<td>
							<input type="text"	name="<portlet:namespace />inte" id="<portlet:namespace />inte"  readonly="readonly" onChange=" sumarTodo()"
								value="<%= convenio != null && convenio.getInteres()  != null ? convenio.getInteres().toString() : "" %>" />
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="ajuste-capital" />:</td>
						<td>
							<input type="text" name="<portlet:namespace />ajuste_capital_forma_pago" id="<portlet:namespace />ajuste_capital_forma_pago"  onChange=" sumarTodo()" 
								value="<%= convenio != null &&  convenio.getAjusteCapital() != null ? convenio.getAjusteCapital().toString() : "" %>"
								<% if (!esEdicion) {%>readonly="readonly"<%} %>/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="ajuste-inte" />:</td>
						<td>
							<input type="text"	name="<portlet:namespace />ajuste_interes_forma_pago" id="<portlet:namespace />ajuste_interes_forma_pago"   onChange=" sumarTodo()"
								value="<%= convenio != null && convenio.getAjusteInteres()  != null ? convenio.getAjusteInteres().toString() : "" %>" 
								<% if (!esEdicion) {%>readonly="readonly"<%} %>/>
						</td>
					</tr>
					<tr>
						<td><liferay-ui:message key="total"/>:</td>
						<td>
							<input type="text" name="<portlet:namespace />totalPagos" id="<portlet:namespace />totalPagos"  readonly='readonly' />
						</td>
					</tr>
				</table>
				</fieldset>
			</td>
		</tr>
</table>
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
				<liferay-util:include page="/html/portlet/uoma/conveniosNoOS/formas_pago_no_os_agregar.jsp">
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
				</liferay-util:include>
			</td>
		</tr>
</table>


<% if (esEdicion && !soloVer) { %> 
<br />
<input type="submit" value="<liferay-ui:message key="crear-convenio" />" onClick="<portlet:namespace />saveConvenio();return false;"/>
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa"/>
<input type="hidden" value="" name="tabs1" id="tabs1"/>
<input type="hidden" value="" name="view" id="view"/>
<input type="hidden" value="<%= request.getAttribute("accionOriginal") != null && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (convenio == null ? Constants.ADD : Constants.UPDATE)%>" name="accionOriginal" id="accionOriginal"/>
<%} %>

				
<script type="text/javascript">	
	function <portlet:namespace />saveConvenio() {
		if (parseFloat(document.getElementById("<portlet:namespace />deuda").value) !=
				parseFloat(document.getElementById("<portlet:namespace />capital").value)){
			alert("La deuda de actas debe ser igual al capital pagado");
			return false;
		} 

		if (<portlet:namespace />validarCampos()) {

			
			<%-- document.<portlet:namespace />conv.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (convenio == null ? Constants.ADD : Constants.UPDATE) %>"; --%>			
			var accion = "<%if(convenio !=null && esEdicion == true){%><%=Constants.UPDATE%><%}else{%><%=Constants.ADD%><%}%>"; 
			document.<portlet:namespace />conv.<portlet:namespace /><%= Constants.CMD %>.value = accion;
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_convenios_no_os_entry';			
			document.<portlet:namespace />conv.method = 'post';
			document.getElementById("cambioSolapa").name = "xx";
			document.getElementById("tabs1").name = "xx2";
			submitForm(document.<portlet:namespace />conv, url);
		}
	}     
	
	function <portlet:namespace />validarCampos() {
		try{
			if (trim(document.getElementById("<portlet:namespace />cuit_entidad").value) == "" || 
					trim(document.getElementById("<portlet:namespace />sucursal_entidad").value) == "" ){
				alert("Debe seleccionar una empresa");
				return false;
			}
			 if (trim(jQuery('#<portlet:namespace />nro_cheque').val()) != ""){
				 document.getElementById("<portlet:namespace />id_banco").focus();
				 alert("Para agregar informacion sobre Cheques debe presionar el boton 'Agregar'");
				 return false;
			 }
		 
		} catch (err) {
			return false;
		}
		return true;
	}
	sumarTodo();
	function cambiaCuit(){
	}
	//ESTO ES PARA EVITAR ERROR DE JS DE COMPONENTE DE EMPLEADORES.	 
	function filtrarConceptosUOMA(){}
	
	function actualizarEstadoSeguimiento(){
		 
		var id_estado_seg = jQuery('#<portlet:namespace />estado_seguim').val();
		var id_convenio = jQuery('#<portlet:namespace />convenio_id').val();

		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/estudio_isidro/convenioCambiarEstadoSeguimiento&id_estado='+id_estado_seg+'&id_convenio='+id_convenio;
		
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


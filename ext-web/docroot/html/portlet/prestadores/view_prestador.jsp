<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>

<%
Prestador prestador  = (Prestador)request.getSession().getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);

String cmd = (String) request.getAttribute(Constants.CMD);

boolean esEdicion = false;

if (prestador == null  ||
   (  cmd!=null  && cmd.length() > 0  && !request.getAttribute(Constants.CMD).equals(Constants.VIEW)   ) ) {
	esEdicion = true;
}

Calendar certificacionVto = Calendar.getInstance();
Calendar seguroVto = Calendar.getInstance();
Calendar matriculaFechaVto = Calendar.getInstance();


if(prestador != null && prestador.getFechaVtoCertificacion() != null){
	certificacionVto.setTime(prestador.getFechaVtoCertificacion()); 
}
if(prestador != null && prestador.getFechaVtoSeguro() != null){
	seguroVto.setTime(prestador.getFechaVtoSeguro()); 
}


String tabValue = ParamUtil.getString(request, "tab", null); // "datos"

%>
<portlet:defineObjects />

<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />prestador_fm" id="<portlet:namespace />prestador_fm" >
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
 	
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-prestador" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td width="20" ><label><liferay-ui:message key="codigo" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />id_prestador"
				name="<portlet:namespace />id_prestador" size="7" type="text"
				value="<%= prestador != null ? prestador.getId_prestadorString() : "" %>"
				readonly='readonly' /></td>
			<td><label><liferay-ui:message key="tipo" />:</label></td>
			<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>tipo_prestador" id="tipo_prestador"
				onchange="manejarTipo();">
					<option value=""></option>
					<% for (TipoPrestador tipo : tiposPrestador) { %>
					<option
						<%= prestador != null && prestador.getTipo() != null && prestador.getTipo().getId() == tipo.getId() ? "selected" : ""  %>
						value="<%= tipo.getId() %>"><%=tipo.getDescripcion()%></option>
					<% } %>
			</select></td>
			  <td><label><liferay-ui:message key="cuit" />:</label></td>
			  <td colspan="1"><input id="<portlet:namespace />cuit"
				name="<portlet:namespace />cuit" size="13" maxlength="11"
				type="text"
				value="<%= prestador != null ? prestador.getCuit() : "" %>"
				<% if (prestador != null) { %> <%="readonly='readonly'" %> <%}%>
				onblur="javascript:validarExistencia(event);" /> <input
				type="hidden" name="<portlet:namespace />existencia"
				id="<portlet:namespace />existencia" value="" /></td>
			  <td><label><liferay-ui:message key="descripcion" />:</label></td>
			  <td colspan="1"><input id="<portlet:namespace />desc"
				  name="<portlet:namespace />desc" size="50" type="text" maxlength="250"
				  value="<%= prestador != null ? prestador.getDescripcion() : "" %>"
				  <% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
				
		</tr>
		</table>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
		<tr>
			<td><label><liferay-ui:message key="cod-htal" />:</label></td>
			<td><input id="<portlet:namespace />codigo_hospital"
				name="<portlet:namespace />codigo_hospital" size="7" type="text"
				value="<%= prestador != null ? prestador.getCodigoHospital() : "" %>"
				 <% if (!esEdicion) { %> <%="readonly='readonly'" %> <%} %> /></td>	
			<td colspan="2"><label><liferay-ui:message key="seguro-cobertura" />:</label>
			<input type="checkbox"id="<portlet:namespace />seguro_cobertura" name="<portlet:namespace />seguro_cobertura" 
			<%if(null!=prestador && prestador.getSeguroCobertura()){%>checked<%}%> />
			</td>
		
			<td><label><liferay-ui:message key="compania-seguro" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />compania_seguro"
				name="<portlet:namespace />compania_seguro" size="50" type="text"
				value="<%= prestador != null ? prestador.getCiaSeguro(): "" %>"
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
				
			<td><label><liferay-ui:message key="vto-fecha" />:</label></td>
			<td><liferay-ui:input-date dayParam="seguroFechaVtoDia"
					dayValue="<%= seguroVto.get(Calendar.DATE) %>" 
					dayNullable="<%=true %>"
					monthParam="seguroFechaVtoMes"
					monthValue="<%= seguroVto.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"
					yearParam="seguroFechaVtoAnio"
					yearValue="<%= seguroVto.get(Calendar.YEAR) %>"
					yearRangeStart="<%= seguroVto.get(Calendar.YEAR) - 1 %>"
					yearRangeEnd="<%= seguroVto.get(Calendar.YEAR) + 15 %>"
					yearNullable="<%= true %>"
					firstDayOfWeek="<%= seguroVto.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>" />
			</td>	
		</tr>
		<tr>
			<td><label><liferay-ui:message key="certificacion-profesional" />:</label></td>
			<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
					<%}%> name="<portlet:namespace/>certificacion" id="<portlet:namespace />certificacion" onchange="manejarCertificacion();">
						<option value="true"<%=prestador!=null && prestador.getCertificacionProfesional()==true ? "selected" : "" %>>Si</option>
						<option value="false"<%=prestador!=null && prestador.getCertificacionProfesional()==false ? "selected" : "" %>>No</option>
				</select></td>
			<td><label><liferay-ui:message key="vto-certificacion" />:</label></td>
			<td colspan="1"><liferay-ui:input-date dayParam="certificacionFechaVtoDia"
					dayValue="<%= certificacionVto.get(Calendar.DATE) %>" 
					dayNullable="<%=true %>"
					monthParam="certificacionFechaVtoMes"
					monthValue="<%= certificacionVto.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"
					yearParam="certificacionFechaVtoAnio"
					yearValue="<%= certificacionVto.get(Calendar.YEAR) %>"
					yearRangeStart="<%= certificacionVto.get(Calendar.YEAR) - 1 %>"
					yearRangeEnd="<%= certificacionVto.get(Calendar.YEAR) + 15 %>"
					yearNullable="<%= true %>"
					firstDayOfWeek="<%= certificacionVto.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>" />
			</td>
			<td><label><liferay-ui:message key="otorga-cert" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />otorga_cert"
				name="<portlet:namespace />otorga_cert" size="50" type="text"
				value="<%= prestador != null ? prestador.getOtorgaCertificacion(): "" %>"
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
				
			<td><% if(prestador != null && StringUtils.checkNotEmpty(prestador.getEmpresaCaiCaeNumero()) ){ %><p><%=prestador.getEmpresaCaiCaeNumero() %></p>  <%} else { %> <p>No se ha cargado CAI/CAE</p> <%} %> </td>	
		</tr>
	</table>	
	</fieldset>
	
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	
	<fieldset class="block-labels">
	<legend><liferay-ui:message key="profesion" /></legend>
	<table class="lfr-table"  style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td><label><liferay-ui:message key="profesion" />:</label></td>
			<td colspan="2"><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>profesion"
				id="<portlet:namespace/>profesion" onchange="manejarProfesion();">
					<option selected value="0">Seleccione una profesión</option>
					<% for (ProfesionPrestador prof : profesionPrestador) { %>
					<option
						<%-- <%= prof != null && prof.getIdProfesion() != 0 && prof.getIdProfesion() == prof.getIdProfesion() ? "selected" : ""  %> --%>
						value="<%=prof.getIdProfesion()+"|"+prof.getDescripcion()%>"><%=prof.getDescripcion()%></option>
					<% } %>
			</select></td>
			
			<td><label><liferay-ui:message key="cat-prof-ospim" />:</label></td>
			<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>cat_prof_ospim" id="<portlet:namespace/>cat_prof_ospim">
					<option value=""></option>
					<option value="A">A</option>
					<option value="B">B</option>
					<option value="C">C</option>
					<option value="X">X</option>
			</select></td>
			
			<td><label><liferay-ui:message key="presenta-tit-prof" />:</label>
			  <input type="checkbox"id="<portlet:namespace />titulo_profesional" 
			         name="<portlet:namespace />titulo_profesional" value="false" 
			          <% if (!esEdicion) { %> <%="disabled='disabled'" %> <%} %> /></td>
			
			<td><label><liferay-ui:message key="presenta-tit-espe" />:</label>
			<input type="checkbox"id="<portlet:namespace />titulo_especialista"
			    	name="<portlet:namespace />titulo_especialista" value="false" 
			    	 <% if (!esEdicion) { %> <%="disabled='disabled'" %> <%} %> /></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="Especialidad" />:</label></td>
			<td colspan="2"><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>especialidad"
				id="<portlet:namespace/>especialidad"
				onchange="manejarEspecialidad();" style="width: 200px;" >
					<option selected value="0">Seleccione una especialidad</option>
					<% for (EspecialidadPrestador espe : especialidadPrestador) { %>
					<option
						<%-- <%= espe != null && espe.getIdEspecialidad() != 0 && espe.getIdEspecialidad() == espe.getIdEspecialidad() ? "selected" : ""  %> --%>
						value="<%=espe.getIdEspecialidad()+"|"+espe.getDescripcion()%>"><%=espe.getDescripcion()%></option>
					<% } %>
			</select></td>

			<td><label><liferay-ui:message key="Sub Especialidad" />:</label></td>
			<td colspan="2"><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>sub-especialidad"
				id="<portlet:namespace/>sub-especialidad">
					<option selected value="0">Seleccione una subespecialidad</option>
					<% for (SubEspecialidadPrestador subEspe : subEspecialidadPrestador) { %>
					<option
						<%-- <%= subEspe != null && subEspe.getId() != 0 && subEspe.getId() == subEspe.getId() ? "selected" : ""  %> --%>
						value="<%=subEspe.getId()+"|"+subEspe.getDescripcion()%>"><%=subEspe.getDescripcion()%></option>
					<% } %>
			</select></td>
			<td>&nbsp;</td>
			<td><input type="button" value="<liferay-ui:message key="agregar" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
			      onClick="<portlet:namespace />agregarProfEspecialidad();" /></td>
		</tr>
		<tr>
			<td colspan="8">
				<div id="<portlet:namespace />lista_especialidades">
					<jsp:include page='/html/portlet/prestadores/lista_especialidades_prestador.jsp' />
				</div>
			</td>
		</tr>
	</table>
</fieldset>

<tr>
	<td colspan="6">&nbsp;</td>
</tr>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="matricula-profesional" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td><label><liferay-ui:message key="tipo-matricula" />:</label></td>
			<td><table>
					<select <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>
						name="<portlet:namespace/>mat_tipo"
						id="<portlet:namespace />mat_tipo" onchange="manejarTipoMatricula();">
						<option value="N">NACIONAL</option>
						<option value="P">PROVINCIAL</option>
						<option value="R">R.N.P</option>
					</select>
				</table></td>

			<td><label><liferay-ui:message key="numero" />:</label></td>
			<td><input id="<portlet:namespace />mat_numero"
				name="<portlet:namespace />mat_numero" size="8" maxlength="9" onkeydown="allowOnlyDigits(event);"
				type="text" value="" <% if (!esEdicion) { %>
				<%="disabled='disabled'" %> <%}%> /></td>

			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>mat_provincia"
				id="<portlet:namespace />mat_provincia">
					<% for (Provincia provincia : provincias) { %>
					<option
						<%-- <%= prestador != null && prestador.getProvincia() != null && prestador.getProvinciaMatricula().getId() == provincia.getId() ? "selected" : ""  %> --%>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<% } %>
			</select></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="vto-fecha" />:</label></td>
			<td colspan="3"><liferay-ui:input-date dayParam="matriculaFechaVtoDia"
					dayValue="1"
					dayNullable="<%=true %>"
					monthParam="matriculaFechaVtoMes"
					monthValue="<%= matriculaFechaVto.get(Calendar.MONTH) %>"
					monthNullable="<%=true %>"
					yearParam="matriculaFechaVtoAnio"
					yearValue="<%= matriculaFechaVto.get(Calendar.YEAR) %>"
					yearRangeStart="<%= matriculaFechaVto.get(Calendar.YEAR) - 1 %>"
					yearRangeEnd="<%= matriculaFechaVto.get(Calendar.YEAR) + 15 %>"
					yearNullable="<%=true %>"
					firstDayOfWeek="<%= matriculaFechaVto.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>" />
			</td>
			<td><label><liferay-ui:message key="presento_copia_matricula" />:</label></td>
			<td><input type="checkbox"id="<portlet:namespace />presentoCopiaMatricula"	name="<portlet:namespace />presentoCopiaMatricula" value="false" /></td>
			<td><input type="button" value="<liferay-ui:message key="agregar" />" <% if(!esEdicion){%> disabled="disabled" <% } %>	
			      onClick="<portlet:namespace />agregarMatricula();" /></td>
		</tr>
		<tr>
			<td colspan="7">
				<div id="<portlet:namespace />lista_matriculas">
 					<liferay-util:include page="/html/portlet/prestadores/lista_matriculas_prestador.jsp">
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
</fieldset>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="otros" />
	</legend>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="contacto" />:</label></td>
			<td colspan="5"><textarea rows="5" cols="50"
					id="<portlet:namespace />contacto" maxlength="100"
					name="<portlet:namespace />contacto" <% if (!esEdicion) { %>
					<%="readonly='readonly'" %> <%}%>><%= prestador != null && prestador.getContacto() != null? prestador.getContacto() : "" %></textarea>
			</td>
			<td><label><liferay-ui:message key="observaciones" />:</label></td>
			<td colspan="5"><textarea rows="5" cols="50" maxlength="500"
					id="<portlet:namespace />observaciones"
					name="<portlet:namespace />observaciones" <% if (!esEdicion) { %>
					<%="readonly='readonly'" %> <%}%>><%= prestador != null && prestador.getObservaciones() != null? prestador.getObservaciones() : "" %></textarea>
			</td>
			<td>CBU:</td>
			<td>
			   <input id="<portlet:namespace />cbu"
				name="<portlet:namespace />cbu" size="22" maxlength="22" 
				type="text" value="<%= prestador != null && prestador.getCbu() != null? prestador.getCbu(): "" %>" <% if (!esEdicion) { %><%="disabled='disabled'" %> <%}%> />
			</td>
			
		</tr>
	</table>
</fieldset>
<%if(esEdicion){ %>
<br/>
<div align="left" style="vertical-align: bottom;" >
<input type="button" value="<liferay-ui:message key="next" />"
	onClick="<portlet:namespace />siguienteSolapa();" />
</div>
<%} %>
<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<script type="text/javascript">
<%if(cmd.equals(Constants.ADD)){ %>
jQuery("#<portlet:namespace />cuit").focus();
<%}%>

<% if(prestador != null && prestador.getFechaVtoSeguro() == null){ %>
	jQuery('#<portlet:namespace/>seguroFechaVtoDia').val('');
	jQuery('#<portlet:namespace/>seguroFechaVtoMes').val('');
	jQuery('#<portlet:namespace/>seguroFechaVtoAnio').val('');	
<%} %>
<% if(prestador != null && prestador.getFechaVtoCertificacion() == null){ %>
jQuery('#<portlet:namespace/>certificacionFechaVtoDia').val('');
jQuery('#<portlet:namespace/>certificacionFechaVtoMes').val('');
jQuery('#<portlet:namespace/>certificacionFechaVtoAnio').val('');	
<%} %>

	function <portlet:namespace />siguienteSolapa() {		
		 if (<portlet:namespace />validarCampos()) { 
			<%-- var accionEnCurso = jQuery('#<portlet:namespace /><%= Constants.CMD %>').val(); --%>
			var accionEnCurso = document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />prestador_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/editar_prestadores_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true";
			
			document.<portlet:namespace />prestador_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_fm, url);
	 	}  
	}
	
	function validarExistencia(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode				
		var cuit = jQuery("#<portlet:namespace />cuit").val();
		var tipoSelect  =document.getElementById("tipo_prestador");
		
		if (cuit.length > 0) {	
			if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == ""){
				alert("<liferay-ui:message key='tipo-prestador-obligatorio' />");
				tipoSelect.focus();
				return false;
			}
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/buscar_cuit_existente&cuit='+cuit +'&tipo='+tipoSelect.value;
			jQuery("#validarExistenciaCuit").load(url);		
			jQuery("#validarExistenciaCuit").show();				
		}
	}   
	
	function <portlet:namespace />validarCampos() {
		
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var compania_seguro= jQuery('#<portlet:namespace />compania_seguro').val();
		var seguro_cobertura=jQuery('#<portlet:namespace />seguro_cobertura').is(':checked');
		var seguroFechaVtoDia = jQuery('#<portlet:namespace />seguroFechaVtoDia').val(); 
		var seguroFechaVtoMes = jQuery('#<portlet:namespace />seguroFechaVtoMes').val(); 
		var seguroFechaVtoAnio = jQuery('#<portlet:namespace />seguroFechaVtoAnio').val();		
		var certificacion= jQuery('#<portlet:namespace />certificacion').val();
		var certificacionFechaVtoDia = jQuery('#<portlet:namespace />certificacionFechaVtoDia').val(); 
		var certificacionFechaVtoMes = jQuery('#<portlet:namespace />certificacionFechaVtoMes').val(); 
		var certificacionFechaVtoAnio = jQuery('#<portlet:namespace />certificacionFechaVtoAnio').val();
		var otorga_cert= jQuery('#<portlet:namespace />otorga_cert').val();
		

		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		
		var cbu=jQuery('#<portlet:namespace />cbu').val();
		
		try {
			if (trim(jQuery("#<portlet:namespace />desc").val()).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />desc').focus();
				return false;
			}

			if (trim(cuit).length == 0){
				alert("<liferay-ui:message key='valida-cuit' />");
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
							
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}	

			var tipoSelect  =document.getElementById("tipo_prestador");
			if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == ""){
				alert("<liferay-ui:message key='tipo-prestador-obligatorio' />");
				tipoSelect.focus();
				return false;
			}
			
			if (document.getElementById('<portlet:namespace />seguro_cobertura').checked){
				if(trim(compania_seguro).length == 0){
					alert("Debe completar el Seguro de Cobertura");
					return false;	
				}
			}
			
			if(cbu.trim().length==22 && !validarCBU(trim(cbu), "<liferay-ui:message key='valida-cbu'/>")){
				jQuery('#<portlet:namespace />cbu').focus();
				return false;
			}else if (cbu.trim().length >= 1 && cbu.trim().length <= 21 ){
				alert("El CBU debe ser de 22 caraceteres. ");
				return false;
			}else if (cbu.trim().length > 22 ){
				alert("El CBU debe ser de 22 caraceteres. ");
				return false;
			}else if (cbu.trim().length ==0 ){
				alert("El CBU es obligatorio ");
				return false;
			}
			
		} catch (err) {
			return false;
		}
		return true;
	}

	function manejarTipo(){
		if ("<%=esEdicion%>" == "true"){
			var tipoSelect  =document.getElementById("tipo_prestador");
			if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == "PROFESIONAL"){
 				document.getElementById("<portlet:namespace/>profesion").disabled = "";
 				document.getElementById("<portlet:namespace/>especialidad").disabled = "";
				document.getElementById("<portlet:namespace/>sub-especialidad").disabled = "";

 			} else {
  				document.getElementById("<portlet:namespace/>profesion").disabled = "disabled";
				document.getElementById("<portlet:namespace/>profesion").selectedIndex = 0; 
 				document.getElementById("<portlet:namespace/>especialidad").disabled = "disabled";
				document.getElementById("<portlet:namespace/>especialidad").selectedIndex = 0;
				document.getElementById("<portlet:namespace/>sub-especialidad").disabled = "disabled";
				document.getElementById("<portlet:namespace/>sub-especialidad").selectedIndex = 0;
			};
			if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == "HOSPITAL"){
				document.getElementById("<portlet:namespace />codigo_hospital").disabled = "";
			}else{
				document.getElementById("<portlet:namespace />codigo_hospital").disabled = "disabled";
				document.getElementById("<portlet:namespace />codigo_hospital").value = "";
			}
		}
	}
	
	function manejarCertificacion(){
		if ("<%=esEdicion%>" == "true"){
		var tipoSelect  =document.getElementById("<portlet:namespace />certificacion");
		if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == "Si"){
			document.getElementById("<portlet:namespace/>certificacionFechaVtoDia").disabled = "";
			document.getElementById("<portlet:namespace/>certificacionFechaVtoMes").disabled = "";
 			document.getElementById("<portlet:namespace/>certificacionFechaVtoAnio").disabled = "";
		} else {
			document.getElementById("<portlet:namespace />certificacionFechaVtoDia").disabled = "disabled";
			document.getElementById("<portlet:namespace />certificacionFechaVtoDia").selectedIndex = 0;
			document.getElementById("<portlet:namespace />certificacionFechaVtoMes").disabled = "disabled";
			document.getElementById("<portlet:namespace />certificacionFechaVtoMes").selectedIndex = 0;
			document.getElementById("<portlet:namespace />certificacionFechaVtoAnio").disabled = "disabled";
			document.getElementById("<portlet:namespace />certificacionFechaVtoAnio").selectedIndex = 0;
			};
		}
		
	}

	function manejarProfesion(){		
		var profesionArray = jQuery('#<portlet:namespace/>profesion').val().split("|");
		var idProfesion=profesionArray[0];
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/prestadores/id_profesion_especialidad&idProfesion='+idProfesion;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>especialidad").length = 0;
 				document.getElementById("<portlet:namespace/>sub-especialidad").length = 0;
 				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>especialidad", "Seleccione una especialidad", 0);
				addElementToSelect("<portlet:namespace/>sub-especialidad", "Seleccione una subespecialidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>especialidad", text, value);		
				}				                                                                                                                                                                                                                                                            
			}
		});
	}
	
	function manejarEspecialidad(){
		var especialidadArray = jQuery('#<portlet:namespace/>especialidad').val().split("|");
		var idEspecialidad = especialidadArray[0]; 
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/prestadores/id_especialidad_subEspecialidad&idEspecialidad='+idEspecialidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
 				document.getElementById("<portlet:namespace/>sub-especialidad").length = 0;
 				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>sub-especialidad", "Seleccione una subespecialidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>sub-especialidad", text, value);		
				}				                                                                                                                                                                                                                                                            
			}
		}); 
	}
			
	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor+"|"+texto; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}
	
	function <portlet:namespace />agregarMatricula() {
		var matTipo = jQuery('#<portlet:namespace />mat_tipo').val();
		var matNumero = jQuery('#<portlet:namespace />mat_numero').val();
		var matProvincia = jQuery('#<portlet:namespace />mat_provincia').val(); 
		var provDesc = jQuery('#<portlet:namespace />mat_provincia option:selected').html();
		var presentoCopiaMatricula=jQuery('#<portlet:namespace />presentoCopiaMatricula').is(':checked');
		var matFechaVtoDia = jQuery('#<portlet:namespace />matriculaFechaVtoDia').val(); 
		var matFechaVtoMes = jQuery('#<portlet:namespace />matriculaFechaVtoMes').val(); 
		var matFechaVtoAnio = jQuery('#<portlet:namespace />matriculaFechaVtoAnio').val();
		
		if(!presentoCopiaMatricula){
			alert("Debe marcar Presentó Copia Matrícula");
			jQuery('#<portlet:namespace />presentoCopiaMatricula').focus();
			return false;
		}
		
		var params = {"matTipo":matTipo,
							   "matNumero":matNumero,
							   "matProvincia":matProvincia,
							   "descProvincia":provDesc,
							   "presentoCopiaMatricula":presentoCopiaMatricula,
							   "matFechaVtoDia":matFechaVtoDia,
							   "matFechaVtoMes":matFechaVtoMes,
							   "matFechaVtoAnio":matFechaVtoAnio};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/prestadores/lista_matricula_prestador" /></portlet:renderURL>';
		
	 	jQuery('#<portlet:namespace />lista_matriculas').load(url,params, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });
	 	
	 	jQuery('#<portlet:namespace />presentoCopiaMatricula').attr('checked', false);
	 	jQuery('#<portlet:namespace />mat_numero').val('');
		document.getElementById("<portlet:namespace/>mat_tipo").selectedIndex = 0;
		document.getElementById("<portlet:namespace />mat_provincia").disabled = "disabled";
		document.getElementById("<portlet:namespace />mat_provincia").selectedIndex = 0;
		document.getElementById("<portlet:namespace />matriculaFechaVtoDia").disabled = "disabled";
		document.getElementById("<portlet:namespace />matriculaFechaVtoDia").selectedIndex = 0;
		document.getElementById("<portlet:namespace />matriculaFechaVtoMes").disabled = "disabled";
		document.getElementById("<portlet:namespace />matriculaFechaVtoMes").selectedIndex = 0;
		document.getElementById("<portlet:namespace />matriculaFechaVtoAnio").disabled = "disabled";
		document.getElementById("<portlet:namespace />matriculaFechaVtoAnio").selectedIndex = 0;
	}       		

	function <portlet:namespace />agregarProfEspecialidad(){
		
		var profesionArray = jQuery('#<portlet:namespace/>profesion').val().split("|");
		idProfesion=profesionArray[0];
		profesion=profesionArray[1];
		
		var especialidadArray = jQuery('#<portlet:namespace/>especialidad').val().split("|");
		idEspecialidad=especialidadArray[0];
		especialidad=especialidadArray[1];
		
		var subEspecialidadArray = "0|0";
		try{
			subEspecialidadArray = jQuery('#<portlet:namespace/>sub-especialidad').val().split("|");
		}catch(err){
			subEspecialidadArray = "0|0";
		}	
		idSubEspecialidad=subEspecialidadArray[0];
		subEspecialidad=subEspecialidadArray[1];
 		
		if(idProfesion == 0){
			alert("Debe seleccionar una profesión");
			return false;
		}
		if(idEspecialidad == 0){
			alert("Debe seleccionar una especialidad");
			return false;
		}
		
		var cat_prof_ospim = jQuery('#<portlet:namespace/>cat_prof_ospim').val();
		var tituloProfesional =jQuery('#<portlet:namespace />titulo_profesional').is(':checked');
		var tituloEspecialista =jQuery('#<portlet:namespace />titulo_especialista').is(':checked');
		
		if(!tituloProfesional){
			alert("Debe marcar Presentó título Profesional");
			jQuery('#<portlet:namespace />titulo_profesional').focus();
			return false;
		}
		if(!tituloEspecialista){
			alert("Debe marcar Presentó título Especialista");
			jQuery('#<portlet:namespace />titulo_especialista').focus();
			return false;
		}
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/lista_prof_especialidades_prestador';
		url = url+'&idProfesion='+idProfesion+
		'&idEspecialidad='+idEspecialidad+
		'&idSubEspecialidad='+idSubEspecialidad+
		'&profesion='+escape(profesion)+
		'&especialidad='+escape(especialidad)+
		'&subEspecialidad='+escape(subEspecialidad)+
		'&categoriaProfOspim='+cat_prof_ospim+
		'&tituloProfesional='+tituloProfesional+
		'&tituloEspecialista='+tituloEspecialista; 

		jQuery("#<portlet:namespace />lista_especialidades").load(url);  
			
		document.getElementById("<portlet:namespace/>profesion").selectedIndex = 0; 
		document.getElementById("<portlet:namespace/>especialidad").selectedIndex = 0;
		document.getElementById("<portlet:namespace/>sub-especialidad").selectedIndex = 0;
		document.getElementById("<portlet:namespace/>cat_prof_ospim").selectedIndex = 0;
		jQuery('#<portlet:namespace />titulo_profesional').attr('checked', false);
		jQuery('#<portlet:namespace />titulo_especialista').attr('checked', false);


	}
	 
	function manejarTipoMatricula(){
		if ("<%=esEdicion%>" == "true"){
			var tipoSelect  =document.getElementById("<portlet:namespace />mat_tipo");
			if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == "PROVINCIAL"){
				document.getElementById("<portlet:namespace/>mat_provincia").disabled = "";
			} else {
				document.getElementById("<portlet:namespace />mat_provincia").disabled = "disabled";
				document.getElementById("<portlet:namespace />mat_provincia").selectedIndex = 0;
				};
				
				if (trim(tipoSelect.options[tipoSelect.selectedIndex].innerHTML) == "R.N.P"){
					document.getElementById("<portlet:namespace/>matriculaFechaVtoDia").disabled = "";
					document.getElementById("<portlet:namespace/>matriculaFechaVtoMes").disabled = "";
					document.getElementById("<portlet:namespace/>matriculaFechaVtoAnio").disabled = "";
				} else {
					document.getElementById("<portlet:namespace />matriculaFechaVtoDia").disabled = "disabled";
					document.getElementById("<portlet:namespace />matriculaFechaVtoDia").selectedIndex = 0;
					document.getElementById("<portlet:namespace />matriculaFechaVtoMes").disabled = "disabled";
					document.getElementById("<portlet:namespace />matriculaFechaVtoMes").selectedIndex = 0;
					document.getElementById("<portlet:namespace />matriculaFechaVtoAnio").disabled = "disabled";
					document.getElementById("<portlet:namespace />matriculaFechaVtoAnio").selectedIndex = 0;
					};
			}
	}
	
	manejarTipo();
	manejarCertificacion();
	manejarTipoMatricula();
		
</script>



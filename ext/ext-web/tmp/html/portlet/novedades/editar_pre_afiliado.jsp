<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
String accion = (String)request.getAttribute(Constants.CMD);

PreAfiliadoTotal afiliado = (PreAfiliadoTotal)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
Date vigenteFechaAfil = afiliado != null ? afiliado.getVigen_fecha() : null; 
if (vigenteFechaAfil == null) {
	vigenteFecha.setTime(new Date());
} else {
	vigenteFecha.setTime(afiliado.getVigen_fecha());
}

Calendar fechaNacimiento = CalendarFactoryUtil.getCalendar();
Date fechaNacimientoAfil = afiliado != null ? afiliado.getNaci_fecha() : null; 
if (fechaNacimientoAfil == null) {
	fechaNacimiento.setTime(new Date());
} else {
	fechaNacimiento.setTime(afiliado.getNaci_fecha());
}

String esTituStr = ParamUtil.getString(request, "es_titular_p");
Boolean esTitular = null;

if(esTituStr != null){ 
	esTitular = Boolean.valueOf(esTituStr);
}else{
	esTitular = (afiliado == null ? true : afiliado.getInte() == 0);
}

String tipo_nov = "";
tipo_nov = (String) request.getAttribute("tipo_novedad_pre_afi");
if(tipo_nov == null && afiliado != null){
	tipo_nov = afiliado.getTipo_novedad();
}

if(seccionalFijada == 0 && (afiliado != null && afiliado.getId_seccional() > 0)){
	seccionalFijada = afiliado.getId_seccional();
}

String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}
%> 
<%-- <input name="<portlet:namespace /><%= Constants.CMD %>"
	id="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
	value="<%= accion %>" /> --%>
<liferay-ui:error exception="<%= NoSuchAfiliadoEntryException.class %>"
	message="the-afiliado-could-not-be-found" />
<liferay-ui:error exception="<%= DuplicateAfiliadoIdException.class %>"
	message="the-afiliado-key-already-exists" />
<liferay-ui:error
	exception="<%= AfliadoYaTieneConyugeException.class %>"
	message="the-afiliado-ya-tiene-conyuge" />
<liferay-ui:error exception="<%= SystemException.class %>"
	message="sistema-no-disponible" />
<liferay-ui:error exception="<%= HijoNoPuedeSerCasadoException.class %>"
	message="the-hijo-no-puede-ser-casado" />
<liferay-ui:error
	exception="<%= ConyugeNoPuedeSerSolteroException.class %>"
	message="the-conyuge-no-puede-ser-soltero" />
<liferay-ui:error
	exception="<%= TitularNoPuedeSerSolteroException.class %>"
	message="the-titular-no-puede-ser-soltero" />
		
<liferay-ui:error key="error-afiliado-repetido" message="the-afiliado-key-already-exists" />
		
<form action="" method="post" name="<portlet:namespace />fm">	
	<input type="hidden" name="tabs_a_mostrar" value="<%=tabsAMostrar%>"/>
	<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
			
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="seccional" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional" value="<%=String.valueOf(seccionalFijada)%>" />
					<liferay-util:param name="seccional" value="<%=seccionalString %>" />
					<liferay-util:param name="esEdicion" value="false" />
				</liferay-util:include></td>
		</tr>
	</table>
	</br>
</fieldset>
</br>

<%-- <% if(idSecSugerido > 0) {%>

<script type="text/javascript" >

jQuery('#<portlet:namespace />id_seccional').val('<%=idSecSugerido%>');
jQuery("#<portlet:namespace />seccional").val("abcde");
jQuery("#<portlet:namespace />secc_seleccionada").val("1");
var popupSecc;
<portlet:namespace />buscarSeccional();	
if(popupSecc){		
	Liferay.Popup.close(popupSecc);
}

</script>

<% } %> --%>



<fieldset class="block-labels"> 
	<legend>
		<liferay-ui:message key="datos-afiliado" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><input name="<portlet:namespace />tipo_novedad" id="<portlet:namespace />tipo_novedad" type="hidden" 
							value="<%=tipo_nov  %>" />
			<label><liferay-ui:message key="codigo" /></label></td>
			<td><input id="<portlet:namespace />id"
				name="<portlet:namespace />id" type="text"
				value='<%= afiliado != null ? afiliado.getId() : "" %>'
				readonly='readonly' /></td>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input id="<portlet:namespace />cuil_titular"
				name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
				type="text"
				value='<%= afiliado != null ? afiliado.getCuil_titular() : "" %>'
				<% if (afiliado != null) { %> readonly='readonly'
				onfocus='moveFocus();' <%} else { %>
				onblur='javascript:<portlet:namespace />validarExisteCuil("cuil_titular");<portlet:namespace />proponerDNI();<portlet:namespace />completeCuil();'
				<% } %> /></td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />inte"
				name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
				value="<%= afiliado != null ? String.valueOf(afiliado.getInte()) : "" %>"
				onfocus='moveFocus();' /></td>
			<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="2"><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) - 120 %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR) + 220 %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek()%>"
					disabled="<%= false %>" /></td>
			<td>&nbsp;</td>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text"
				value="<%= afiliado != null ? afiliado.getApellido() : "" %>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%= afiliado != null ? afiliado.getNombre() : "" %>" /></td>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>sexo" id="<portlet:namespace/>sexo" > 
					<option
						<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("M") ? "selected" : ""  %>
						value="m">Masculino</option>
					<option
						<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("F") ? "selected" : ""  %>
						value="f">Femenino</option>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia"
				name="<portlet:namespace/>provincia" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarLocalidad();">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=afiliado != null && afiliado.getId_provincia() == provincia.getId() ? "selected" : ""%>
						<%= afiliado == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1">
			<div class="selector-localidad">
			   <%if(afiliado != null) {%>
			   <select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						<%=afiliado != null && afiliado.getId_localidad() == localidad.getId() ? "selected" : ""%>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			  </select>
			  <%} else{%>
			  	<select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
				 </select>	
			<%} %>		
			 </div>
			 </td>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="1" style="vertical-align: top">
				<input id="<portlet:namespace />calle" name="<portlet:namespace />calle" size="15" type="text" 
				value="<%= afiliado != null ? afiliado.getCalle() : "" %>" /></td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5"
				type="text"
				value="<%= afiliado != null ? afiliado.getNumero() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %>
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" />
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<div id='divCodPostal' style="float: right;"></div>
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="2" type="text"
				value="<%= afiliado != null ? afiliado.getPiso() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				value="<%= afiliado != null ? afiliado.getDepto() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
				
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" maxlength="4"
				type="text" value="<%= afiliado != null ? afiliado.getPostal_codi() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %>></td>
			<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />barrio"
				name="<portlet:namespace />barrio" size="12" maxlength="50"
				type="text" value="<%= afiliado != null ? afiliado.getBarrio() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_telefono"
				name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
				type="text" value="<%= (afiliado != null&&afiliado.getCod_area_telefono()!=null) ? afiliado.getCod_area_telefono() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />telefono" onkeydown="allowOnlyDigitsConSuprimir(event);"
				name="<portlet:namespace />telefono" size="15" maxlength="15"
				type="text" value="<%= (afiliado!=null&&afiliado.getTelefono()!=null) ? afiliado.getTelefono() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_tel_laboral"
				name="<portlet:namespace />cod_area_tel_laboral" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
				type="text" value="<%= (afiliado != null&&afiliado.getCod_area_tel_laboral()!=null) ? afiliado.getCod_area_tel_laboral() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="tel-labo" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />tel_laboral"
				name="<portlet:namespace />tel_laboral" size="15" maxlength="15" onkeydown="allowOnlyDigitsConSuprimir(event);"
				type="text" value="<%= (afiliado!=null&&afiliado.getTel_laboral()!=null) ? afiliado.getTel_laboral(): "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>	
		</tr>	
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>	
		<tr>		
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_celular"
				name="<portlet:namespace />cod_area_celular" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
				type="text" value="<%= (afiliado!=null&&afiliado.getCod_area_celular()!=null) ? afiliado.getCod_area_celular() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>	
			<td colspan="1"><label><liferay-ui:message key="celular" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />celular"
				name="<portlet:namespace />celular" size="15" maxlength="15" onkeydown="allowOnlyDigitsConSuprimir(event);"
				type="text" value="<%= (afiliado != null&&afiliado.getCelular()!=null) ? afiliado.getCelular() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>	
			<td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
			<td colspan="3"><input id="<portlet:namespace />email"
				name="<portlet:namespace />email" size="50" maxlength="50" onblur="javascript:<portlet:namespace />validarEmail();"
				type="text" value="<%= afiliado != null&&afiliado.getEmail()!=null ? afiliado.getEmail() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>	
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>nacionalidad">
					<%	for (Nacionalidad nacion : nacionalidades) { %>
					<option
						<%= afiliado != null && afiliado.getNacionalidad() == nacion.getId() ? "selected" : ""  %>
						<%= afiliado == null && nacion.getId() == WebKeysAfiliados.ID_DEFAULT_NACIONALIDAD ? "selected" : ""  %>
						value="<%= nacion.getId() %>"><%=nacion.getDescripcion()%>
					</option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td colspan="1"><select
				name="<portlet:namespace/>documento_tipo"
				id="<portlet:namespace/>documento_tipo"
				onblur="javascript:<portlet:namespace />buscarAfilExistente(event);">
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	%>
					<option
						<%= afiliado != null && afiliado.getDocumento_tipo().equals(tipoDoc) ? "selected" : ""  %>
						<%= afiliado == null && tipoDoc.equals(WebKeysAfiliados.TIPO_DOCUMENTO_DEFAULT) ? "selected" : ""  %>
						value="<%= tipoDoc %>"><%=tipoDoc%>
					</option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="8"
				type="text"
				value="<%= afiliado != null ? afiliado.getDocumento_numero() : "" %>"
				onblur="javascript:<portlet:namespace />buscarAfilExistente(event);"
				onkeydown="allowOnlyDigits(event);" />
				<div id='<portlet:namespace />nroDocAfilExistente'
					style="float: inherit;"></div></td>
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="11"
				type="text"
				value="<%= afiliado != null ? afiliado.getCuil() : "" %>"
				<% if (afiliado == null || (afiliado != null && afiliado.getId_parentesco_sss()==WebKeysAfiliados.PARENTESCO_DEFAULT)) { %>
				readonly="readonly" onfocus="moveFocusNaci();" <%} else { %>
				onblur="javascript:<portlet:namespace />validarExisteCuil('cuil');"
				<%}%> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
			<td colspan="1">
				<!--<input id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
				<liferay-ui:input-date dayParam="fechaNacimientoDia"
					dayValue="<%= fechaNacimiento.get(Calendar.DATE) %>"
					monthParam="fechaNacimientoMes"
					monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"
					yearParam="fechaNacimientoAnio"
					yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) - 200 %>"
					yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR) + 200 %>"
					firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek()%>"
					disabled="<%= false %>" />
			</td>
			<td><label><liferay-ui:message key="estado-civil" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>estado_civil"
				id="<portlet:namespace/>estado_civil"
				onBlur="javascript:<portlet:namespace />validarOtrosCampos();">
					<%	for (EstadoCivil estadoCivil : estados_civil) {	%>
					<option
						<%= afiliado != null && afiliado.getId_estado_civil_sss()==estadoCivil.getCodigo() ? "selected" : 
							afiliado == null && WebKeysAfiliados.ESTADO_CIVIL_DEFAULT == estadoCivil.getCodigo() ? "selected" : "" %>
						value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="3"><select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco"
				<% if (afiliado == null || (afiliado != null && afiliado.getId_parentesco_sss()==WebKeysAfiliados.PARENTESCO_DEFAULT)) { %>
				disabled="true" <%}%>>
					<%	for (Parentesco parentesco : parentescos) { %>
					
					    <% if(afiliado == null ){ %>
					    	<option
								<%= afiliado == null && parentesco.getCodigo() == WebKeysAfiliados.PARENTESCO_DEFAULT ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
					    <% }else if(afiliado != null && afiliado.getInte() == 0){ %>
							<option
								<%= afiliado != null && afiliado.getId_parentesco_sss() == parentesco.getCodigo() ? "selected" : ""  %>
								<%= afiliado == null && parentesco.getCodigo() == WebKeysAfiliados.PARENTESCO_DEFAULT ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
						<%}else if(afiliado != null && afiliado.getInte() > 0 && parentesco.getCodigo() != WebKeysAfiliados.PARENTESCO_DEFAULT){ %>
							<option
								<%= afiliado != null && afiliado.getId_parentesco_sss() == parentesco.getCodigo() ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
													
						<%	} %>
					<%	} %>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>discapacitado">
					<option value="0"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") ? "selected" : ""  %>>Si</option>
			</select></td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<!-- <tr>
			<td colspan="8">&nbsp;</td>
		</tr> -->
	</table>
</fieldset>
<br />	

<!-- Situacion Laboral -->
<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado_situ_laboral.jsp" >	
				<liferay-util:param name="cuit" value='<%=afiliado!=null?afiliado.getCuit():"" %>'/>
				<liferay-util:param name="razon_soc" value='<%=afiliado!=null?afiliado.getRazonSocial():"" %>'/>
				<liferay-util:param name="sucur" value='<%=afiliado!=null?afiliado.getSucursal():"" %>'/>	
			</liferay-util:include>	
		
<!-- Plan y Tercerizadora -->
<%if(esTitular) { %>
<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado_plan_tercerizadora.jsp" >
 	<liferay-util:param name="esEdicion" value="true" />
</liferay-util:include>
<%} %>
			
<input type="button" value="<liferay-ui:message key="save" />"
	onClick="<portlet:namespace />savePreAfiliadoEntry();" />

&nbsp;&nbsp;
<input type="button" value="<liferay-ui:message key="alta-afiliado" />" 
	onClick="<portlet:namespace />altaPreAfiliado();" />
<%--	
<c:if test="<%= afiliado != null %>">
 	<input type="button"
		value="<liferay-ui:message key="documentacion-adjunta" />"
		onClick="<portlet:namespace />documentacionAdjunta( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" /> 
	<input type="button"
		value="<liferay-ui:message key="cargar-integrante" />"
		onClick="<portlet:namespace />cargarIntegrante();" />
	 <c:if test="<%= afiliado.getTiene_imagen() == 1 %>">
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />"
			onClick="<portlet:namespace />verImagenDirecta();" />
	</c:if>
	<c:if test='<%= null != afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") %>'>
		<input type="button" value="Detalle Discapacidad"
			onClick="<portlet:namespace />detalleDiscapacidad( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
	</c:if> 
</c:if> 
<br /> --%>
<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">		
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />cuil_titular);		
		</script>
</c:if> 

</form>

<script type="text/javascript">

	<%-- function <portlet:namespace />validarExisteCuil(cuil){
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_existe_pre_afiliado&cuil='+cuil_final;
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-invalido'/>");
				}else if(obj.validado=="2"){
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
				}else if(obj.validado=="3"){
					alert("<liferay-ui:message key='cuil-preafi-existente'/>");
				} 	
			}				                                                                                                                                                                                                                                                            
			
		});
	} --%>

	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;
				jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
//				for(var i =0;i< obj.listaFiltrada.length; i++){	
//					jQuery("#<portlet:namespace/>localidad").append(obj.listaFiltrada[i]);
//				}
				
				jQuery('.selector-localidad select').html(data).fadeIn();

			}
		});
	}

	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}

	function filtrarCodAreaTel() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_cod_area_tel&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_area_telefono").length = 0;
				document.getElementById("<portlet:namespace />cod_area_celular").length = 0;
				var obj = jQuery.parseJSON(data);
				if(obj.codAreaTel != null && obj.codAreaTel != 'null' ){
					jQuery('#<portlet:namespace />cod_area_telefono').val(obj.codAreaTel);
					jQuery('#<portlet:namespace />cod_area_celular').val(obj.codAreaTel);
				}else{
					jQuery('#<portlet:namespace />cod_area_telefono').val("");
					jQuery('#<portlet:namespace />cod_area_celular').val("");
				}
				
				
			}
		});	
	}

	function validarEmail() {
		var email = jQuery('#<portlet:namespace/>email').val();
		var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
		
		if(trim(email).length > 0){	
			if( !emailReg.test( email ) ) {
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}	
	}
	
	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}
 
	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}

	function <portlet:namespace />proponerDNI() {
		var tipoDoc = jQuery('#<portlet:namespace/>documento_tipo').val();
		if (tipoDoc=='DU') {
			var dni = jQuery('#<portlet:namespace />cuil_titular').val().substring(2,10);
			jQuery('#<portlet:namespace />nroDoc').val(dni);
		} else {
			jQuery('#<portlet:namespace />nroDoc').val("");
		}
	}

	function <portlet:namespace />completeCuil() {
		var cuil = jQuery('#<portlet:namespace />cuil_titular').val();
		jQuery('#<portlet:namespace />cuil').val(cuil);
		validarPrefijoCuilPorSexo();
	}

	function <portlet:namespace />validarOtrosCampos() {

		var cod_parentesco=parseInt(jQuery("#<portlet:namespace/>parentesco").val());
		var cod_estado_civil=parseInt(jQuery("#<portlet:namespace/>estado_civil").val());
		
		if ((parentesco==1 || parentesco==2) && estado_civil==1) {
			alert("<liferay-ui:message key='the-conyuge-no-puede-ser-soltero' />");	
			return false;	
		} 	if ((parentesco==3||parentesco==4||parentesco==5||parentesco==6||parentesco==7||parentesco==8)&&(estado_civil==2)) {
			alert("<liferay-ui:message key='the-hijo-no-puede-ser-casado' />");
			return false;	
		}

		return true();
	}

	var popup;
	function <portlet:namespace />altaPreAfiliado(){
		popup= Liferay.Popup({title:"<liferay-ui:message key="alta-afiliado" />",modal:true,width:300});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/selecc_alta_pre_afiliado';   		       	
		jQuery(popup).load(url); 
	}

	function <portlet:namespace />tipoAlta(cuil_titu){
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />
				  </portlet:renderURL>';

		url = url + '&cuil_titular='+cuil_titu+ '&<%=Constants.CMD%>='+'<%=Constants.ADD%>';
		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
		
		/* jQuery.post(url,{cuil_titular:cuil_titu}, function() { submitForm(document.<portlet:namespace />fm);
															   Liferay.Popup.close(popup); }); */
	}
</script>
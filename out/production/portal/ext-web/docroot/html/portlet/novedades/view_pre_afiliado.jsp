<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
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

boolean esTitular = afiliado == null ? true : afiliado.getInte() == 0;
boolean esView = true;

%> 
	
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

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-afiliado" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input id="<portlet:namespace />cuil_titular"
				name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
				type="text"
				value='<%= afiliado != null ? afiliado.getCuil_titular() : "" %>'
				<% if (esView) { %> readonly='readonly' <%}%> /></td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />inte"
				name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
				value="<%= afiliado != null ? String.valueOf(afiliado.getInte()) : '0' %>" 
				<% if (esView) { %> readonly='readonly' <%}%>/></td>
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
					disabled="<%= esView %>" /></td>
			<td>&nbsp;</td>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text" <% if (esView) { %> readonly='readonly' <%}%>
				value="<%= afiliado != null ? afiliado.getApellido() : "" %>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%= afiliado != null ? afiliado.getNombre() : "" %>" 
				<% if (esView) { %> readonly='readonly' <%}%> /></td>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>sexo" <% if (esView) { %> disabled="disabled" <%} %>>
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
				name="<portlet:namespace/>provincia" <% if (esView) { %>disabled="disabled" <%} %> >
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=afiliado != null && afiliado.getId_provincia() == provincia.getId() ? "selected" : ""%>
						<%= afiliado == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (esView) { %>disabled="disabled" <%} %> >
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						<%=afiliado != null && afiliado.getId_localidad() == localidad.getId() ? "selected" : ""%>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			</select></td>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="1" style="vertical-align: top">
				<input id="<portlet:namespace />calle" name="<portlet:namespace />calle" size="15" type="text" 
				value="<%= afiliado != null ? afiliado.getCalle() : "" %>" <% if (esView) { %> readonly='readonly' <%}%> /></td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5"
				type="text" <% if (esView) { %> readonly='readonly' <%}%>
				value="<%= afiliado != null ? afiliado.getNumero() : "" %>"/>
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
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				value="<%= afiliado != null ? afiliado.getDepto() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
				
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" maxlength="4"
				type="text" value="<%= afiliado != null ? afiliado.getPostal_codi() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %>></td>
			<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />barrio"
				name="<portlet:namespace />barrio" size="12" maxlength="50"
				type="text" value="<%= afiliado != null ? afiliado.getBarrio() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_telefono"
				name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5"
				type="text" value="<%= afiliado != null&&afiliado.getCod_area_telefono()!=null ? afiliado.getCod_area_telefono() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />telefono"
				name="<portlet:namespace />telefono" size="15" maxlength="15"
				type="text" value="<%= afiliado != null ? afiliado.getTelefono() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_tel_laboral"
				name="<portlet:namespace />cod_area_tel_laboral" size="5" maxlength="5"
				type="text" value="<%= afiliado != null&&afiliado.getCod_area_tel_laboral()!=null ? afiliado.getCod_area_tel_laboral() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="tel-labo" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />tel_laboral"
				name="<portlet:namespace />tel_laboral" size="15" maxlength="15"
				type="text" value="<%= afiliado != null ? afiliado.getTel_laboral(): "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>	
		</tr>	
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>	
		<tr>		
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_celular"
				name="<portlet:namespace />cod_area_celular" size="5" maxlength="5"
				type="text" value="<%= afiliado != null ? afiliado.getCod_area_celular() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>	
			<td colspan="1"><label><liferay-ui:message key="celular" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />celular"
				name="<portlet:namespace />celular" size="15" maxlength="15"
				type="text" value="<%= afiliado != null ? afiliado.getCelular() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>	
			<td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
			<td colspan="3"><input id="<portlet:namespace />email"
				name="<portlet:namespace />email" size="50" maxlength="50" 
				type="text" value="<%= afiliado != null ? afiliado.getEmail() : "" %>"
				<% if (esView) { %> readonly="readonly" <%} %> /></td>	
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>nacionalidad" <% if (esView) { %>disabled="disabled" <%}%>>
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
				id="<portlet:namespace/>documento_tipo" <% if (esView) { %> disabled="disabled" <%}%>>
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
				<% if (esView) { %> readonly='readonly' <%}%> />
				
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="11"
				type="text" <% if (esView) { %> readonly='readonly' <%}%>
				value="<%=afiliado.getCuil()%>" /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
			<td colspan="1">
				<liferay-ui:input-date dayParam="fechaNacimientoDia"
					dayValue="<%= fechaNacimiento.get(Calendar.DATE) %>"
					monthParam="fechaNacimientoMes"
					monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"
					yearParam="fechaNacimientoAnio"
					yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) - 200 %>"
					yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR) + 200 %>"
					firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek()%>"
					disabled="<%= esView %>" />
			</td>
			<td><label><liferay-ui:message key="estado-civil" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>estado_civil"
				id="<portlet:namespace/>estado_civil" <% if (esView) { %> disabled="disabled" <%}%>>
					<%	for (EstadoCivil estadoCivil : estados_civil) {	%>
					<option
						<%= afiliado != null && afiliado.getId_estado_civil_sss()==estadoCivil.getCodigo() ? "selected" : 
							afiliado == null && WebKeysAfiliados.ESTADO_CIVIL_DEFAULT == estadoCivil.getCodigo() ? "selected" : "" %>
						value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="3"><select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco"
				<% if (esView) { %> disabled="true" <%}%>>
					<%	for (Parentesco parentesco : parentescos) { %>
					
					    <option <%= afiliado != null && afiliado.getId_parentesco_sss() == parentesco.getCodigo() ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
						</option>
					<%	} %>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>discapacitado" <% if (esView) { %> disabled="disabled" <%}%>>
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
	</table>
</fieldset>
<br />
<!-- Situacion Laboral -->

<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado_situ_laboral.jsp" >	
				<liferay-util:param name="cuit" value='<%=afiliado!=null?afiliado.getCuit():"" %>'/>
				<liferay-util:param name="razon_soc" value='<%=afiliado!=null?afiliado.getRazonSocial():"" %>'/>
				<liferay-util:param name="sucur" value='<%=afiliado!=null?afiliado.getSucursal():"" %>'/>
				<liferay-util:param name="esEdicion" value="false" />	
</liferay-util:include>	
			
<!-- Plan y Tercerizadora -->

<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado_plan_tercerizadora.jsp" >
	<liferay-util:param name="esEdicion" value="false" />	
</liferay-util:include>


<script type="text/javascript">
<c:if test="<%= afiliado != null %>">
<%-- function <portlet:namespace />uploadImagenAfiliado() {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';
	document.<portlet:namespace />fm.method = 'post';
	url = url+'&imagen'+'='+'<%=Constants.ADD%>'+'&nrsolicitud=<%=afiliado.getCuil() %>';
	submitForm(document.<portlet:namespace />fm, url);
} --%>

function verImagenAfiliado(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

<%-- function deleteImagenAfiliado(folderId,fileName) {	
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){	
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';						
		document.<portlet:namespace />fm.method = 'post';
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fm, url);
	}else{
		return false;
	}	
} --%>
</c:if>
</script>
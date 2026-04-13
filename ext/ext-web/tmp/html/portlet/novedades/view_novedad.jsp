<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

Novedad nove = (Novedad) request.getAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD);

Calendar fechaNacimiento = Calendar.getInstance();
Calendar fechaAltaOS = Calendar.getInstance();
Calendar fechaCierrePres = Calendar.getInstance();

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
fechaNacimiento.setTime(sdf.parse(nove.getFecha_nacimiento_Str()));
fechaAltaOS.setTime(sdf.parse(nove.getFecha_alta_en_ooss_Str()));
fechaCierrePres.setTime(sdf.parse(nove.getFecha_cierre_presentacion_Str()));


//List<SituacionRevista> situacionesRevista=TraeListasServiceUtil.getSituacionRevista();
List<SituacionRevista> situacionesRevista=(ArrayList<SituacionRevista>) portletSession.getAttribute(WebKeysAfiliados.SITUACIONES_REVISTA_EMPRESA_EN_SESSION, PortletSession.APPLICATION_SCOPE);

ArrayList<TipoNovedad> tiposNov = (ArrayList<TipoNovedad>) TraeListasServiceUtil.getTiposNovedadSss();



%>

<fieldset class="block-labels">
<legend><liferay-ui:message key="detalle-novedad" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="tipo-novedad" />:</label></td>
		<td colspan="3">
			<select name="<portlet:namespace/>tipoNov" id="<portlet:namespace/>tipoNov" disabled="disabled">
					<option value=""></option>
					<%
						for (TipoNovedad tipoNov : tiposNov) {
					%>
						<option <%= nove.getCodigo_movimiento().equalsIgnoreCase(tipoNov.getCodigo())? "selected" : "" %> 
							value="<%= tipoNov.getCodigo() %>"><%=tipoNov.getDescripcion()%></option>
					<% } %>
			</select>
		</td>
	</tr>
	<tr>
		<%-- <td><label>Fecha alta en la OOSS:</label></td>
		<td colspan="1">
		<liferay-ui:input-date
			dayParam="fechaAltaOSDia"
			dayValue="<%= fechaAltaOS.get(Calendar.DATE) %>" 
			monthParam="fechaAltaOSMes"
			monthValue="<%= fechaAltaOS.get(Calendar.MONTH) %>"				
			yearParam="fechaAltaOSAnio"
			yearValue="<%= fechaAltaOS.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaAltaOS.get(Calendar.YEAR) %>"
			yearRangeEnd="<%= fechaAltaOS.get(Calendar.YEAR)%>"
			firstDayOfWeek="<%= fechaAltaOS.getFirstDayOfWeek() %>"
			disabled="<%= true %>" />
		</td> --%>
		<td><label>Fecha cierre presentaci&oacute;n:</label></td>
		<td colspan="1">
		<liferay-ui:input-date
			dayParam="fechaCierrePresDia"
			dayValue="<%=fechaCierrePres.get(Calendar.DATE) %>" 
			monthParam="fechaCierrePresMes"
			monthValue="<%=fechaCierrePres.get(Calendar.MONTH) %>"				
			yearParam="fechaCierrePresAnio"
			yearValue="<%=fechaCierrePres.get(Calendar.YEAR) %>"
			yearRangeStart="<%=fechaCierrePres.get(Calendar.YEAR) %>"
			yearRangeEnd="<%=fechaCierrePres.get(Calendar.YEAR)%>"
			firstDayOfWeek="<%=fechaCierrePres.getFirstDayOfWeek() %>"
			disabled="<%= true %>" />
		</td>
		<td><label><b><liferay-ui:message key="detalle-novedad" />:</label></b></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />det_nove"
			name="<portlet:namespace />det_nove" size="13"
			type="text"
			value="<%= nove.getDetalle_novedad()%>" 
			style="font-weight: bold;"  />
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil_titular"
			name="<portlet:namespace />cuil_titular" size="13" type="text"
			value="<%=nove.getCuil_titular() %>" />
		</td>
		<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<td><select disabled="disabled" name="<portlet:namespace/>parentesco">
				<%
					for (Parentesco parentesco : parentescos) {
				%>
				<option
					<%= nove.getCodigo_parentesco()==parentesco.getCodigo() ? "selected" : ""  %>
					value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%></option>
				<% } %>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="cuil" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil"
			name="<portlet:namespace />cuil" size="13"
			type="text"
			value="<%= nove.getCuil()%>" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
		<td>
			<select disabled="disabled" name="<portlet:namespace/>documento_tipo">
			<%
				for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
			%>
			<option <%= nove.getDocumento_tipo().equals(tipoDoc) ? "selected" : "" %>
					value="<%= tipoDoc %>"><%=tipoDoc%></option>
				<% } %>
			</select>
		</td>
		<td><label><liferay-ui:message key="nro-documento" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />nroDoc"
			name="<portlet:namespace />nroDoc" size="9"
			type="text"
			value="<%= nove.getDocumento_numero()%>" />
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>	
	<tr>
		<td><label><liferay-ui:message key="apellido" /> y <liferay-ui:message key="nombre" /> :</label></td>
		<td><input disabled="disabled" id="<portlet:namespace />apellido"
			name="<portlet:namespace />apellido" size="30" maxlength="30"
			type="text"
			value="<%= nove.getApellido_nombre() %>" />
		</td>
		<td><label><liferay-ui:message key="sexo" />:</label></td>
		<td><select disabled="disabled" name="<portlet:namespace/>sexo">
			<option
				<%= nove.getSexo().trim().toUpperCase().equals("M") ? "selected" : ""  %>
				value="m">Masculino</option>
			<option
				<%= nove.getSexo().trim().toUpperCase().equals("F") ? "selected" : ""  %>
				value="f">Femenino</option>
		</select></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
		<td>
		<liferay-ui:input-date
			dayParam="fechaNacimientoDia"
			dayValue="<%= fechaNacimiento.get(Calendar.DATE) %>" 
			monthParam="fechaNacimientoMes"
			monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"				
			yearParam="fechaNacimientoAnio"
			yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) %>"
			yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR)%>"
			firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek() %>"
			disabled="<%= true %>" />
		</td>
		<td><label><liferay-ui:message key="estado-civil" />:</label></td>
		<td><select disabled="disabled" name="<portlet:namespace/>estado_civil">
			<%
				for (EstadoCivil estadoCivil : estados_civil) {
			%>
			<option
				<%=nove.getEstado_civil()== estadoCivil.getCodigo() ? "selected" : ""  %>
				value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
			<%
				}
			%>

		</select></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="nacionalidad" />:</label></td>
		<td><select disabled="disabled" name="<portlet:namespace/>nacionalidad">
			<%
				for (Nacionalidad nacion : nacionalidades) {
			%>
			<option
				<%= nove.getNacionalidad() == nacion.getId_ssuper() ? "selected" : ""  %>
				value="<%= nacion.getId() %>"><%=nacion.getDescripcion()%></option>
			<% } %>
			</select>
		</td>
		<td><label><liferay-ui:message key="discapacitado" />:</label></td>
		<td>
			<select disabled="disabled" name="<portlet:namespace/>discapacitado">
				<option value="0" <%= nove.getIncapacidad().equals("0") ? "selected" : ""  %>>No</option>
				<option value="1" <%= nove.getIncapacidad().equals("1") ? "selected" : ""  %>>Si</option>
			</select>
		</td>			
	</tr>
</table>
</fieldset>
<br/>
<fieldset class="block-labels">
<legend><liferay-ui:message key="home-address-afi" /></legend>
<table class="lfr-table">
	
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td><select disabled="disabled" name="<portlet:namespace/>provincia">
			<%
				for (Provincia provincia : provincias) {
			%>
			<option
				<%= nove.getProvincia() == provincia.getId() ? "selected" : ""  %>
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
			<% } %>
			</select>
		</td>
		<td><label><liferay-ui:message key="localidad" />:</label></td>
<%-- 		<td><select disabled="disabled" name="<portlet:namespace/>localidad">
			<% 
				for (Localidad localidad : localidades) {
			%>
			<option
				<%= nove.getLocalidad() == localidad.getId() ? "selected" : ""  %>
				<%= afiliado == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
			<% } %>
			</select>
		</td> --%>
		<td><input type="text" readonly="readonly" name="<portlet:namespace/>localidad" value="<%=nove.getLocalidad()%>"  >
		</td>
		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td><input  readonly="readonly" id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" maxlength="4"
			type="text"
			value="<%= nove.getCodigo_postal()%>" />
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />calle"
			name="<portlet:namespace />calle" size="50" maxlength="100"
			type="text"
			value="<%=nove.getCalle()%>" />
		</td>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="4" type="text"
			value="<%= nove.getNumero_puerta() %>" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>	
		<td><label><liferay-ui:message key="piso" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="5" type="text"
			value="<%= nove.getPiso() %>" />
		</td>
		<td><label><liferay-ui:message key="departamento" />:</label></td>
		<td colspan="1"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />dpto"
			name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
			value="<%= nove.getDepartamento() %>" />
		</td>
		<td><label><liferay-ui:message key="telefono" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />telefono"
			name="<portlet:namespace />telefono" size="15" maxlength="15"
			type="text"
			value="<%=nove.getTelefono() %>" />
		</td> 
	</tr>

</table>
</fieldset>
<br/>
<legend><liferay-ui:message key="empleador" /></legend>
<fieldset class="block-labels">
<table class="lfr-table">
	<tr>	
		<td><label><liferay-ui:message key="cuit" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuit"
			name="<portlet:namespace />cuit" size="15" maxlength="11" type="text"
			value="<%= nove.getCuit_empleador()%>" />
		</td>
		<td><liferay-ui:message key="situacion-revista" /></td>
		<td><select name="<portlet:namespace/>situRevista"
			id="<portlet:namespace/>situRevista" disabled="disabled" >
			<%
				for (SituacionRevista situRevista: situacionesRevista) {
			%>
			<option <%= nove.getSituacion_revista() == situRevista.getId_situ_revista() ? "selected" : "" %>
				value="<%= situRevista.getId_situ_revista()%>"><%=situRevista.getDescripcion()%></option>
			<% } %>
		</select></td>
	</tr>	
</table>
</fieldset>
<br />


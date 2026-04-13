<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

NovedadTotal nove = (NovedadTotal) request.getAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD);
ArrayList<Afiliado> afiliados = (ArrayList<Afiliado>) request.getAttribute("cambio_cuil_afiliados");

Calendar fechaAltaOS = Calendar.getInstance();
Calendar fechaCierrePres = Calendar.getInstance();
Calendar fechaVigenDesde = Calendar.getInstance();
Calendar fechaBaja = Calendar.getInstance();

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
fechaAltaOS.setTime(sdf.parse(nove.getFecha_alta_en_ooss_Str()));
fechaCierrePres.setTime(sdf.parse(nove.getFecha_cierre_presentacion_Str()));

ArrayList<TipoNovedad> tiposNov = (ArrayList<TipoNovedad>) TraeListasServiceUtil.getTiposNovedadSss();

%>

<fieldset class="block-labels">
<legend><liferay-ui:message key="cuil-nuevo-novedad" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="tipo-novedad" />:</label></td>
		<td colspan="3">
			<input type="hidden" value="<%=nove.getId() %>" name = "<portlet:namespace/>b_idNov" id = "<portlet:namespace/>b_idNov" >
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
		<td><label>Fecha alta en la OOSS:</label></td>
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
		</td>
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
	</tr>
</table>
</fieldset>
<fieldset class="block-labels">
<table class="lfr-table">	
	<tr>
		<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil_titular"
			name="<portlet:namespace />cuil_titular" size="13" type="text"
			value="<%=nove.getCuil_titular() %>" />
		</td>
		<td><label><liferay-ui:message key="apellido" /> y <liferay-ui:message key="nombre" />:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />apellido"
			name="<portlet:namespace />apellido" size="30" maxlength="30"
			type="text"
			value="<%= nove.getApellido_nombre() %>" />
		</td>
 		<td><label><liferay-ui:message key="cuil" />:</label></td> 
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil"
			name="<portlet:namespace />cuil" size="13"
			type="text"
			value="<%= nove.getCuil()%>" />
		</td>
 		<td><label><liferay-ui:message key="tipo-documento" />:</label></td> 
		<td>
			<%-- <select disabled="disabled" name="<portlet:namespace/>documento_tipo">
			<%
				for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
			%>
			<option <%= nove.getDocumento_tipo().equals(tipoDoc) ? "selected" : "" %>
					value="<%= tipoDoc %>"><%=tipoDoc%></option>
				<% } %>
			</select> --%>
			<input disabled="disabled" readonly="readonly" id="<portlet:namespace />documento_tipo"
			name="<portlet:namespace />documento_tipo" size="9"
			type="text"
			value="<%= nove.getDocumento_tipo()%>" />
		</td>
 		<td><label><liferay-ui:message key="nro-documento" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />nroDoc"
			name="<portlet:namespace />nroDoc" size="9"
			type="text"
			value="<%= nove.getDocumento_numero()%>" />
		</td>
	</tr>
	<tr>	
		<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<%-- <td colspan="3"><select disabled="disabled" name="<portlet:namespace/>parentesco">
				<%
					for (Parentesco parentesco : parentescos) {
				%>
				<option
					<%= nove.getCodigo_parentesco()==parentesco.getCodigo() ? "selected" : ""  %>
					value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%></option>
				<% } %>
			</select> --%>
		<td colspan="3">
			<input disabled="disabled" readonly="readonly" id="<portlet:namespace />parentesco"
			name="<portlet:namespace />parentesco" size="13" type="text"
			value="<%=nove.getParentescoDesc() %>" />
		</td>
	</tr>
</table>
</fieldset>
<br/>

<fieldset class="block-labels">
<legend><liferay-ui:message key="afiliados" /></legend>
<table class="lfr-table">
	<%for (Afiliado afi : afiliados){ %>
	<tr>
		<td><label><liferay-ui:message key="cuil-titular" />:</label></td> 
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil_titular"
			name="<portlet:namespace />cuil_titular" size="13" type="text"
			value="<%=afi.getCuil_titular() %>" />
		</td>
		<td><label><liferay-ui:message key="apellido" /> y <liferay-ui:message key="nombre" />:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />apellido"
			name="<portlet:namespace />apellido" size="30" maxlength="30"
			type="text"
			value="<%= afi.getApellido() %>,<%= afi.getNombre() %>" />
		</td>
		<td><label><liferay-ui:message key="cuil" />:</label></td> 
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil"
			name="<portlet:namespace />cuil" size="13"
			type="text"
			value="<%= afi.getCuil()%>" />
		</td>
		<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
		<td>
			<%-- <select disabled="disabled" name="<portlet:namespace/>documento_tipo">
			<%
				for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
			%>
			<option <%= afi.getDocumento_tipo().equals(tipoDoc) ? "selected" : "" %>
					value="<%= tipoDoc %>"><%=tipoDoc%></option>
				<% } %>
			</select> --%>
			<input disabled="disabled" readonly="readonly" id="<portlet:namespace />a_documento_tipo"
			name="<portlet:namespace />a_documento_tipo" size="9"
			type="text"
			value="<%= afi.getDocumento_tipo()%>" />
		</td>
		</td>
		<td><label><liferay-ui:message key="nro-documento" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />a_nroDoc"
			name="<portlet:namespace />a_nroDoc" size="9"
			type="text"
			value="<%= afi.getDocu_numero()%>" />
		</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<td><select disabled="disabled" name="<portlet:namespace/>parentesco" style="width: 100px;" >
				<%
					for (Parentesco parentesco : parentescos) {
				%>
				<option
					<%= afi.getId_parentesco()==parentesco.getCodigo() ? "selected" : ""  %>
					value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%></option>
				<% } %>
			</select>
		</td>
		<%
		fechaVigenDesde.setTime(afi.getVigen_fecha());
		%>
		<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="2"><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= fechaVigenDesde.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= fechaVigenDesde.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearRangeStart="<%= fechaVigenDesde.get(Calendar.YEAR) %>"
					yearRangeEnd="<%= fechaVigenDesde.get(Calendar.YEAR)%>"
					yearValue="<%= fechaVigenDesde.get(Calendar.YEAR) %>"
					disabled="<%= true %>" /></td>
					
		<%if (afi != null && afi.getBaja_fecha() != null){ %>
		<td>
			<label><liferay-ui:message key="baja-fecha" />:</label>
		</td>
		<td>
			<%= afi.getBaja_fechaAsString() %>	
		</td>
		<td>
			<label><liferay-ui:message key="motivo-baja" />:</label>		
		</td>
		<td>
				<%		if (afi.getId_motivo_baja() != -1){
							MotivoBaja m = new MotivoBaja(afi.getId_motivo_baja(), "");
							int indexOf  = motivos.indexOf(m);
							if (indexOf!= -1){
							%>
								<%=motivos.get(indexOf).getDescripcion()%>
						<%	}
						}
				}%>
		</td>			
	</tr>
	<% } %>
	
</table>
</fieldset>
<br/>
<table class="lfr-table">
	<tr>
		<%if(afiliados.size() == 1){ %>
		<td>
			<input type="button" name="aceptarCuil" value="Aceptar" onClick='javascript:confirmaCambioCuil();'  >
		</td>
		<%} %>
		<td>&nbsp;</td>
		<td>
			<input type="button" name="cancelarCuil" value="Cancelar" >
		</td>
	</tr>
</table>

<script type="text/javascript" >

</script>